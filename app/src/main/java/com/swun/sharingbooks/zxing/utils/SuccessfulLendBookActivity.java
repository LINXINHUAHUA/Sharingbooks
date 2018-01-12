package com.swun.sharingbooks.zxing.utils;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.swun.sharingbooks.R;

/**
 * Created by BS535 on 2017/5/17.
 */

public class SuccessfulLendBookActivity extends AppCompatActivity {
    public void onCreate(Bundle savedInstancestate){
        super.onCreate(savedInstancestate);
        setContentView(R.layout.activity_lendsuccessful);
        findViewById(R.id.fromlendtohistory).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SuccessfulLendBookActivity.this,LendHistoryActivity.class);
                startActivity(intent);
            }
        });
    }
}
