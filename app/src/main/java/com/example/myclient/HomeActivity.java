package com.example.myclient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.myclient.adapter.ContentAdapter;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener, ViewPager.OnPageChangeListener{
    // 底部菜单5个Linearlayout
    private LinearLayout ll_home;
    private LinearLayout ll_address;
    private LinearLayout ll_friend;
    private LinearLayout ll_setting;
    private LinearLayout ll_set;
    // 底部菜单5个菜单标题
    private TextView tv_home;
    private TextView tv_address;
    private TextView tv_friend;
    private TextView tv_setting;
    private TextView tv_set;

    // 中间内容区域
    private ViewPager viewPager;

    // ViewPager适配器ContentAdapter
    private ContentAdapter adapter;

    private List<View> views;
    private void initEvent() {
        // 设置按钮监听
        ll_home.setOnClickListener(this);
        ll_address.setOnClickListener(this);
        ll_friend.setOnClickListener(this);
        ll_setting.setOnClickListener(this);
        ll_set.setOnClickListener(this);

        //设置ViewPager滑动监听
        viewPager.setOnPageChangeListener(this);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        // 底部菜单5个Linearlayout
        this.ll_home = findViewById(R.id.ll_home);
        this.ll_address = findViewById(R.id.ll_address);
        this.ll_friend = findViewById(R.id.ll_friend);
        this.ll_setting = findViewById(R.id.ll_setting);
        this.ll_set= findViewById(R.id.ll_set);


        // 底部菜单5个菜单标题
        this.tv_home = findViewById(R.id.tv_home);
        this.tv_address = findViewById(R.id.tv_address);
        this.tv_friend = findViewById(R.id.tv_friend);
        this.tv_setting = findViewById(R.id.tv_setting);
        this.tv_set= findViewById(R.id.tv_set);
        // 中间内容区域ViewPager
        this.viewPager = findViewById(R.id.vp_content);

        // 适配器
        View page_01 = View.inflate(HomeActivity.this, R.layout.page_01, null);
        View page_02 = View.inflate(HomeActivity.this, R.layout.page_02, null);
        View page_03 = View.inflate(HomeActivity.this, R.layout.page_03, null);
        View page_04 = View.inflate(HomeActivity.this, R.layout.page_04, null);
        View page_05 = View.inflate(HomeActivity.this, R.layout.page_05, null);

        views = new ArrayList<>();
        views.add(page_01);
        views.add(page_02);
        views.add(page_03);
        views.add(page_04);
        views.add(page_05);

        this.adapter = new ContentAdapter(views);
        viewPager.setAdapter(adapter);
        initEvent();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_home:
                tv_home.setTextColor(0xff1B940A);
                viewPager.setCurrentItem(0);

                break;
            case R.id.ll_address:
                tv_address.setTextColor(0xff1B940A);
                viewPager.setCurrentItem(1);
                break;
            case R.id.ll_friend:
                tv_friend.setTextColor(0xff1B940A);
                viewPager.setCurrentItem(2);
                break;
            case R.id.ll_setting:
                tv_setting.setTextColor(0xff1B940A);
                viewPager.setCurrentItem(3);
                break;
            case  R.id.ll_set:
                tv_set.setTextColor(0xff1B940A);
                viewPager.setCurrentItem(4);

            default:
                break;
        }
    }
    private void restartBotton() {

        // TextView置为白色
        tv_home.setTextColor(0xffffffff);
        tv_address.setTextColor(0xffffffff);
        tv_friend.setTextColor(0xffffffff);
        tv_setting.setTextColor(0xffffffff);
        tv_set.setTextColor(0xffffffff);
    }
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        restartBotton();
        //当前view被选择的时候,改变底部菜单图片，文字颜色
        switch (position) {
            case 0:
                tv_home.setTextColor(0xff1B940A);
                break;
            case 1:
                tv_address.setTextColor(0xff1B940A);
                break;
            case 2:
                tv_friend.setTextColor(0xff1B940A);

                break;
            case 3:
                tv_setting.setTextColor(0xff1B940A);
                break;
            case 4:
                tv_set.setTextColor(0xff1B940A);

                break;
            default:
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
