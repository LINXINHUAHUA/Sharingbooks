package com.swun.sharingbooks.zxing;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.swun.sharingbooks.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by BS535 on 2017/5/8.
 */

public class ChooseError extends AppCompatActivity {
    private Spinner spinner1;
    private List<String> data_list1;
    private ArrayAdapter<String> arr_adapter1;
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tucao);
        Button buttontijiao=(Button)findViewById(R.id.errortijiao);
        buttontijiao.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Toast.makeText(ChooseError.this,"感谢您的提交",Toast.LENGTH_SHORT).show();
            }
        });
        Toolbar toolbar=(Toolbar)findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        spinner1= (Spinner) findViewById(R.id.chooseleixing);
        data_list1 = new ArrayList<String>();
        data_list1.add("图书损坏");
        data_list1.add("二维码损坏");
        data_list1.add("扫描失败");
        arr_adapter1= new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data_list1);
        //设置样式
        arr_adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        spinner1.setAdapter(arr_adapter1);

    }
}
