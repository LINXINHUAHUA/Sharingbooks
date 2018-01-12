package com.swun.sharingbooks.friend;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.swun.sharingbooks.R;

import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;

public class FriendActivity extends FragmentActivity {

    private ViewPager mViewPager;
    private FragmentPagerAdapter mFragmentPagerAdapter;//Fragment的数据适配器
    private List<Fragment> mFragmentList;//ViewPager中的数据
    private Fragment mConversationListFragment;//会话列表的fragment 的声明
    private SharedPreferences preferences;//获取保存的token
    private SharedPreferences.Editor editor;
    private String token;//token

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);

        //toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView textView = (TextView) toolbar.findViewById(R.id.toolbar_title);
        toolbar.setTitle("");
        textView.setText("好友");

        //获取token
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        //token = preferences.getString("token","");
        Intent intent = getIntent();
        token = intent.getStringExtra("token");
        editor = preferences.edit();
        editor.putString("token", token);
        editor.apply();

        //加载融云的会话列表
        mConversationListFragment = initConversationListFragment();
        //加载fragment
        mViewPager = (ViewPager) findViewById(R.id.rc_viewpager);
        mFragmentList = new ArrayList<Fragment>();
        mFragmentList.add(mConversationListFragment);
        mFragmentList.add(new FriendFragment());
        mFragmentPagerAdapter = new FragmentPagerAdapter(
                getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                return mFragmentList.get(i);
            }

            @Override
            public int getCount() {
                return mFragmentList.size();
            }
        };
        mViewPager.setAdapter(mFragmentPagerAdapter);
        //连接融云服务器
        linkRongCloud();
    }

    /**
     * 封装的代码加载融云的会话列表的 fragment
     */
    private Fragment initConversationListFragment() {
        ConversationListFragment fragment = new ConversationListFragment();
        Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon()
                .appendPath("conversationlist")
                .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "false") //设置私聊会话是否聚合显示
                .appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "true")
                .appendQueryParameter(Conversation.ConversationType.DISCUSSION.getName(), "false")
                .appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "true")
                .build();
        fragment.setUri(uri);
        return fragment;
    }

    /**
     * 连接融云服务器
     */
    private void linkRongCloud() {
        RongIM.connect(token, new RongIMClient.ConnectCallback() {
            @Override
            public void onTokenIncorrect() {
                Toast.makeText(FriendActivity.this, "身份认证失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(String s) {

            }
            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                Toast.makeText(FriendActivity.this, "连接服务器失败,请检查网络设置", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 创建菜单
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.friend_menu,menu);
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
            case R.id.add_friend_friend:
                startActivity(new Intent(FriendActivity.this, AddfriendActivity.class));
                break;

        }

        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RongIM.getInstance().logout();
        mFragmentList = null;
        mViewPager = null;
        mFragmentPagerAdapter = null;
    }
}
