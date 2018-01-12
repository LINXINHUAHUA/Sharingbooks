package com.swun.sharingbooks.zxing.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import com.swun.sharingbooks.R;
import com.swun.sharingbooks.zxing.decode.DecodeThread;
import com.swun.sharingbooks.zxing.utils.LendHistoryActivity;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ResultActivity extends Activity {

	private ImageView mResultImage;//拍摄图片
	private ImageView imageView;//书本图片
	private TextView mResultText;//书号
	private TextView title;//书名
	private TextView pubdate;//日期
	private TextView author;//作者
	private Bitmap bitmap1;
    public String sendauthor;
	public String sendpudata;
	public String sendbooktitle;
	private ProgressDialog progressDialog;
	Handler mainHandler=null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_result);
//设置加载页面
		progressDialog = new ProgressDialog(this);
		progressDialog.setTitle("正在查询!");
		progressDialog.setMessage("loading...");
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setCancelable(true);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.show();

		final Bundle extras = getIntent().getExtras();
		mResultImage = (ImageView) findViewById(R.id.result_image);
		mResultText = (TextView) findViewById(R.id.result_text);
		title = (TextView) findViewById(R.id.text_title);
		pubdate = (TextView) findViewById(R.id.text_pubdate);
		author = (TextView) findViewById(R.id.text_author);
		imageView = (ImageView) findViewById(R.id.image_book);
        findViewById(R.id.confirmlend).setOnClickListener(new View.OnClickListener() {
	@Override
	//将书籍信息发送给历史记录
	public void onClick(View v) {
		Intent intent=new Intent(ResultActivity.this,LendHistoryActivity.class);
		intent.putExtra("result", "result");
		intent.putExtra("author",sendauthor);
		intent.putExtra("pubdata",sendpudata);
		intent.putExtra("booktitle",sendbooktitle);
		startActivity(intent);
	}
});
		//接受子线程的数据并赋值
		mainHandler=new Handler() {
			public void handleMessage(Message msg) {
			switch (msg.what){
				case 0:
					sendbooktitle=(String)msg.obj;
				case 1:
					sendauthor = (String) msg.obj;
				case 2:
					sendpudata=(String)msg.obj;
			}
			}
		};
		if (null != extras) {
			//设置图片宽度
			int width = extras.getInt("width");
			int height = extras.getInt("height");
			final LayoutParams lps = new LayoutParams(width, height);
			lps.topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics());
			lps.leftMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics());
			lps.rightMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics());
			//书号
			final String result = extras.getString("result");
			//拍摄图片
			Bitmap barcode = null;
			byte[] compressedBitmap = extras.getByteArray(DecodeThread.BARCODE_BITMAP);
			if (compressedBitmap != null) {
				barcode = BitmapFactory.decodeByteArray(compressedBitmap, 0, compressedBitmap.length, null);
				// Mutable copy:
				barcode = barcode.copy(Bitmap.Config.RGB_565, true);
			}
			final Bitmap bitmap = barcode;
          //获取书籍资料并解析
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						OkHttpClient client = new OkHttpClient.Builder()
                                .connectTimeout(10, TimeUnit.SECONDS)
                                .readTimeout(20, TimeUnit.SECONDS)
                                .build();
						Request request = new Request.Builder()
                                .url("https://api.douban.com/v2/book/isbn/:" + result)
                                .build();
						Response response = client.newCall(request).execute();
						if (response.code() == 200) {
							JSONObject jsonObject = new JSONObject(response.body().string());
							final String subtitlebook = jsonObject.getString("title");
							final String authorbook = jsonObject.getString("author");
							final String pubdatebook = jsonObject.getString("pubdate");
							String imagebook = jsonObject.getString("image");
							if (!imagebook.isEmpty()) {
								Request request1 = new Request.Builder()
										.url(imagebook)
										.build();
								Response response1 = client.newCall(request1).execute();
								bitmap1 = BitmapFactory.decodeStream(response1.body().byteStream());
							} else {

							}
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									progressDialog.dismiss();
									title.setText(subtitlebook);
									author.setText(authorbook);
									pubdate.setText(pubdatebook);
									imageView.setImageBitmap(bitmap1);
									imageView.setImageDrawable(resizeImage(bitmap1, 600, 900));
									//发送给主线程
									mainHandler.obtainMessage(0,subtitlebook).sendToTarget();
									mainHandler.obtainMessage(1,authorbook).sendToTarget();
									mainHandler.obtainMessage(2,pubdatebook).sendToTarget();
									mainHandler.obtainMessage(3,bitmap1).sendToTarget();
								}
							});

						} else {
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									progressDialog.dismiss();
									Toast.makeText(ResultActivity.this,"获取错误",Toast.LENGTH_SHORT).show();
									mResultText.setVisibility(View.VISIBLE);
									mResultImage.setVisibility(View.VISIBLE);
									mResultImage.setLayoutParams(lps);
									mResultText.setText(result);
									mResultImage.setImageBitmap(bitmap);
								}
							});
						}

					} catch (IOException e) {
						e.printStackTrace();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}).start();

		}
		
	}
//设置图片大小函数
	public static Drawable resizeImage(Bitmap bitmap, int w, int h) {
		// load the origial Bitmap
		Bitmap BitmapOrg = bitmap;

		int width = BitmapOrg.getWidth();
		int height = BitmapOrg.getHeight();
		int newWidth = w;
		int newHeight = h;

		String tag=null;
		Log.v(tag,String.valueOf(width));
		Log.v(tag,String.valueOf(height));

		Log.v(tag,String.valueOf(newWidth));
		Log.v(tag,String.valueOf(newHeight));
		// calculate the scale
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// create a matrix for the manipulation
		Matrix matrix = new Matrix();
		// resize the Bitmap
		matrix.postScale(scaleWidth, scaleHeight);
		//  rotate the Bitmap
		matrix.postRotate(0);
		// recreate the new Bitmap
		Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width,
				height, matrix, true);

		return new BitmapDrawable(resizedBitmap);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		progressDialog.dismiss();
	}
}
