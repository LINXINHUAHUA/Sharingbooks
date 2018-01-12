package com.swun.sharingbooks.zxing.utils;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.swun.sharingbooks.R;

/**
 * Created by BS535 on 2017/5/17.
 */

public class LendHistoryActivity extends AppCompatActivity {
    private TextView authorhistory;
    private TextView pubdatahistory;
    private TextView booktitlehistory;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lendhistory);
        authorhistory=(TextView)findViewById(R.id.historytext_author);
        pubdatahistory=(TextView)findViewById(R.id.historytext_pubdate);
        booktitlehistory=(TextView)findViewById(R.id.historytext_booktitle);
        //保存
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        String stringauthor;
        String stringpubdata;
        String stringbooktitle;
        Intent intent=getIntent();
        String result = intent.getStringExtra("result");
        if ("result".equals(result)) {
            //接受从ResultActivity传来的书籍信息
            stringauthor =intent.getStringExtra("author");
            stringpubdata=intent.getStringExtra("pubdata");
            stringbooktitle=intent.getStringExtra("booktitle");
            editor = preferences.edit();
            editor.putString("author", stringauthor);
            editor.putString("pubdata", stringpubdata);
            editor.putString("booktitle", stringbooktitle);
            editor.apply();
        } else {
            stringauthor = preferences.getString("author", "");
            stringpubdata = preferences.getString("pubdata", "");
            stringbooktitle = preferences.getString("booktitle", "");
        }
        //展示
        authorhistory.setText(stringauthor);
        pubdatahistory.setText(stringpubdata);
        booktitlehistory.setText(stringbooktitle);

        findViewById(R.id.button_returnbook).setOnClickListener(new View.OnClickListener() {
            @Override
            //还书清空历史记录
            public void onClick(View v) {
                authorhistory.setText("");
                pubdatahistory.setText("");
                booktitlehistory.setText("");
                Toast.makeText(LendHistoryActivity.this,"还书成功",Toast.LENGTH_SHORT).show();
                editor = preferences.edit();
                editor.putString("author", "");
                editor.putString("pubdata", "");
                editor.putString("booktitle", "");
                editor.apply();
            }
        });
    }
}
