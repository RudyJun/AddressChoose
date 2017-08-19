package com.rudy.addresschoose;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rudy.addresschoose.adapter.ViewPagerAdapter;
import com.rudy.addresschoose.bean.Province;
import com.rudy.addresschoose.dialog.ChooseAddressDialog;
import com.rudy.addresschoose.utils.FileUtils;
import com.rudy.addresschoose.widget.SlidingTabLayout;

import java.util.ArrayList;

/**
 * Created by liuzaijun on 2017/8/17.
 */
public class MainActivity extends AppCompatActivity {

    private ViewPager vpAddress;
    private SlidingTabLayout stCity;
    private ViewPagerAdapter pagerAdapter;
    private ArrayList<Province> provinces;
    private TextView tvAddress;
    private ChooseAddressDialog chooseAddressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        vpAddress = (ViewPager) findViewById(R.id.vp_address);
        tvAddress = (TextView) findViewById(R.id.tv_address);
        stCity = (SlidingTabLayout) findViewById(R.id.st_city);
        pagerAdapter = new ViewPagerAdapter(this, vpAddress);
        vpAddress.setAdapter(pagerAdapter);
        vpAddress.setOffscreenPageLimit(3);
        stCity.setViewPager(vpAddress);

        chooseAddressDialog = new ChooseAddressDialog(this);
        chooseAddressDialog.setOnAddressChooseListener(new ChooseAddressDialog.OnAddressChooseListener() {
            @Override
            public void onChoose(String province, String city, String region, String street) {
                tvAddress.setText(province + city + region + street + "(可点击)");
            }
        });
        tvAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseAddressDialog.show();
            }
        });

        //解析地址
        getAddressJson();
        pagerAdapter.setOnAddressChooseListener(new ViewPagerAdapter.OnAddressChooseListener() {
            @Override
            public void onChoose(String province, String city, String region, String street) {
                tvAddress.setText(province + city + region + street + "(可点击)");
            }
        });
    }

    public void getAddressJson() {
        //开启子线程处理读取地址并解析
        new Thread(new Runnable() {
            @Override
            public void run() {
                Gson gson = new Gson();
                provinces = gson.fromJson(FileUtils.getFromAssets(MainActivity.this, "addressJson.json"), new TypeToken<ArrayList<Province>>() {
                }.getType());
                initView();
            }
        }).start();
    }

    private void initView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //pagerAdapter、chooseAddressDialog初始化数据
                processData();
            }
        });
    }

    private void processData() {
        pagerAdapter.setData(provinces);
        chooseAddressDialog.setData(provinces);
    }
}
