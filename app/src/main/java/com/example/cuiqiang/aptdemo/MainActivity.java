package com.example.cuiqiang.aptdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.aptannotation.BindApi;
import com.example.aptapi.AptApiManager;

public class MainActivity extends AppCompatActivity {

    @BindApi(value = R.id.btn_test)
    public View mBtnTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    private void initView() {
        AptApiManager.getInstance().init(this);
    }

    private void initData() {
        mBtnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }


}
