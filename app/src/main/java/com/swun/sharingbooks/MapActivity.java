package com.swun.sharingbooks;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.swun.sharingbooks.friend.FriendActivity;
import com.swun.sharingbooks.zxing.ChooseError;
import com.swun.sharingbooks.zxing.activity.CaptureActivity;
import com.swun.sharingbooks.zxing.utils.DynamicStateActivity;
import com.swun.sharingbooks.zxing.utils.LendHistoryActivity;
import com.swun.sharingbooks.zxing.utils.PersonPageActivity;
import com.swun.sharingbooks.zxing.utils.SystemInform;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity implements SensorEventListener,View.OnClickListener {

    //定位
    private LocationClient mLocClient;
    private MyLocationListenner myListener = new MyLocationListenner();
    private MyLocationConfiguration.LocationMode mCurrentMode;//方式 普通 跟随 罗盘
    private BitmapDescriptor mCurrentMarker;//图标
    private SensorManager mSensorManager;//方向传感器
    private Double lastX = 0.0;//判断方向
    private int mCurrentDirection = 0;//方向
    private double mCurrentLat = 0.0;//经度
    private double mCurrentLon = 0.0;//纬度
    private float mCurrentAccracy;
    private MapView mMapView;//地图
    private BaiduMap mBaiduMap;//地图控件
    private MyLocationData locData;
    private Button position;//定位button

    private DrawerLayout mDrawerLayout;//侧滑
    private Button scan;//扫一扫

    private String token1 = "dWCjdw9yC6uArspuo+RRZFiol+/uF+TVBqE8l3rGujek6pRMO4bq+pf1n8Amq4oftUU8zwijXB6OyUbgqEkFr3b9xMHa7tgH";
    private String token2 = "TmixYdsJlacnQmgrT1tz8s3ApVMxc/TUSlsTpIdruR/HT0wWf7E0NbkEFNTb1zNSx8KnYgsHB2e7HxsRi7DOtn9jV9Lmp2Tc";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        //侧滑 toolbar NavigationView
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout_map);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_map);
        TextView title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        View viewcode=findViewById(R.id.map_button_scan);
        viewcode.getBackground().setAlpha(150);
        View viewtucao=findViewById(R.id.tucao);
        viewtucao.getBackground().setAlpha(150);
        View viewdongttai=findViewById(R.id.dongtai);
        viewdongttai.getBackground().setAlpha(150);
        findViewById(R.id.tucao).setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent intent=new Intent(MapActivity.this, ChooseError.class);
        startActivity(intent);
    }
});
        findViewById(R.id.dongtai).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MapActivity.this, DynamicStateActivity.class);
                startActivity(intent);
            }
        });

        toolbar.setTitle("");
        title.setText("共享书");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.xinxi);
        }
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_friend1:
                        Intent intent1 = new Intent(MapActivity.this, FriendActivity.class);
                        intent1.putExtra("token", token1);
                        startActivity(intent1);
                        break;
                    case R.id.nav_friend2:
                        Intent intent2 = new Intent(MapActivity.this, FriendActivity.class);
                        intent2.putExtra("token", token2);
                        startActivity(intent2);
                        break;
                    case R.id.lendhistory:
                        Intent intent = new Intent(MapActivity.this,LendHistoryActivity.class);
                        intent.putExtra("result", "map");
                        startActivity(intent);
                        break;
                    case R.id.personpage:
                        startActivity(new Intent(MapActivity.this,PersonPageActivity.class));
                        break;
                    default:
                        Log.d("错误","哈哈");
                        break;
                }
                return true;
            }
        });

        //扫一扫
        scan = (Button) findViewById(R.id.map_button_scan);
        scan.setOnClickListener(this);
        //定位
        position = (Button) findViewById(R.id.map_button_position);
        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
        position.setText("定位");
        position.setOnClickListener(this);
        //获取传感器事物管理器
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        // 地图初始化
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        // 定位初始化
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        mLocClient.setLocOption(option);
        //添加标记点
        initOverlay();
        //运行时权限
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(MapActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(MapActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.CAMERA);
        }
        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(MapActivity.this, permissions, 1);
        }else {
            mLocClient.start();
        }
        //标记点点击事件
        onclickmarker();
        //地图点击事件
        onclickmap();
    }

    /**
     * 地图点击事件
     */
    private void onclickmap() {
        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mBaiduMap.hideInfoWindow();
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });
    }

    /**
     * 标记点点击事件
     */
    private void onclickmarker() {
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                final TextView textView = new Button(getApplicationContext());
                textView.setBackgroundResource(R.drawable.popup);
                textView.setPadding(30,30,30,30);
                textView.setTextColor(Color.BLACK);
                LatLng latLng = marker.getPosition();
                //显示出地址
                GeoCoder geoCoder =GeoCoder.newInstance();
                geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(latLng));
                geoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
                    @Override
                    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

                    }

                    @Override
                    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
                        if (reverseGeoCodeResult == null || reverseGeoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
                            Toast.makeText(MapActivity.this, "抱歉，未能找到结果", Toast.LENGTH_LONG)
                                    .show();
                            return;
                        }
                        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(reverseGeoCodeResult.getLocation()));
                        textView.setText(reverseGeoCodeResult.getAddress());
                    }
                });
                InfoWindow mInfoWindow = new InfoWindow(textView, latLng, -47);
                mBaiduMap.showInfoWindow(mInfoWindow);
                return true;
            }
        });
    }

    /**
     * 添加标记点
     */
    private void initOverlay() {
        ArrayList<LatLng> latLngs = new ArrayList<>();
        latLngs.add(new LatLng(30.584615,103.965226));
        latLngs.add(new LatLng(30.568385,103.973365));
        latLngs.add(new LatLng(30.573803,103.981926));
        latLngs.add(new LatLng(30.575681,103.974389));
        latLngs.add( new LatLng(30.569655,103.984045));
        latLngs.add( new LatLng(30.589660,103.985040));
        latLngs.add( new LatLng(30.559955,103.984345));
        latLngs.add( new LatLng(30.578655,103.985045));
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory
                .fromResource(R.drawable.booklocation);
        for (int i = 0; i < latLngs.size(); i++) {
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLngs.get(i))
                    .icon(bitmapDescriptor)
                    .zIndex(9);
            markerOptions.animateType(MarkerOptions.MarkerAnimateType.drop);
            mBaiduMap.addOverlay(markerOptions);
        }
    }

    /**
     * 传感器
     * @param event
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        double x = event.values[SensorManager.DATA_X];
        if (Math.abs(x - lastX) > 1.0) {
            mCurrentDirection = (int) x;
            locData = new MyLocationData.Builder()
                    .accuracy(mCurrentAccracy)
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(mCurrentDirection)
                    .latitude(mCurrentLat)
                    .longitude(mCurrentLon)
                    .build();
            mBaiduMap.setMyLocationData(locData);
        }
        lastX = x;
    }

    /**
     * 传感器
     * @param sensor
     * @param accuracy
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /**
     * Button 点击
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.map_button_position:
                switch (mCurrentMode) {
                    case COMPASS:
                        position.setText("跟随");
                        mCurrentMode = MyLocationConfiguration.LocationMode.FOLLOWING;
                        mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(
                                mCurrentMode, true, mCurrentMarker));
                        MapStatus.Builder builder = new MapStatus.Builder();
                        builder.overlook(0);
                        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                        break;
                    case NORMAL:
                        position.setText("跟随");
                        mCurrentMode = MyLocationConfiguration.LocationMode.FOLLOWING;
                        mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(
                                mCurrentMode, true, mCurrentMarker));
                        MapStatus.Builder builder1 = new MapStatus.Builder();
                        builder1.overlook(0);
                        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder1.build()));
                        break;
                    case FOLLOWING:
                        position.setText("罗盘");
                        mCurrentMode = MyLocationConfiguration.LocationMode.COMPASS;
                        mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(
                                mCurrentMode, true, mCurrentMarker));
                        break;
                    default:
                        break;
                }
                break;
            case R.id.map_button_scan:
                startActivity(new Intent(MapActivity.this, CaptureActivity.class));
                break;
        }
    }

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            mCurrentLat = location.getLatitude();
            mCurrentLon = location.getLongitude();
            mCurrentAccracy = location.getRadius();
            locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(mCurrentDirection)
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude())
                    .build();
            mBaiduMap.setMyLocationData(locData);
            LatLng ll = new LatLng(location.getLatitude(),
                    location.getLongitude());
            MapStatus.Builder builder = new MapStatus.Builder();
            builder.target(ll).zoom(18.0f);
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        }
        public void onReceivePoi(BDLocation poiLocation) {
        }
    };

    /**
     * 创建菜单
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.map_menu,menu);
        return true;
    }

    /**
     * 菜单点击事件
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.message_map:
               // Toast.makeText(MapActivity.this,"正在开发",Toast.LENGTH_SHORT).show();
            {
                Intent intent=new Intent(MapActivity.this,SystemInform.class);
                startActivity(intent);
            }
            default:
                break;
        }
        return true;
    }

    /**
     * 权限回调
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this,"必须同意所有的权限",Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    mLocClient.start();
                } else {
                    Toast.makeText(this,"发生未知错误",Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
        //为系统的方向传感器注册监听器
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onStop() {
        //取消注册传感器监听
        mSensorManager.unregisterListener(this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        // 退出时销毁定位
        mLocClient.stop();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
    }
}
