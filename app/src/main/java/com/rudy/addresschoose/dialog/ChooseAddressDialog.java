package com.rudy.addresschoose.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.rudy.addresschoose.R;
import com.rudy.addresschoose.adapter.ViewPagerAdapter;
import com.rudy.addresschoose.bean.Province;
import com.rudy.addresschoose.widget.SlidingTabLayout;

import java.util.ArrayList;

/**
 * Created by liuzaijun on 2017/8/19.
 */

public class ChooseAddressDialog extends Dialog {
    protected Activity mAttachActivity;
    private ViewPager vpAddress;
    private SlidingTabLayout stCity;
    private ViewPagerAdapter pagerAdapter;
    private ImageView ivClose;

    public ChooseAddressDialog(Activity context) {
        this(context, R.style.customerDialog);
    }

    public ChooseAddressDialog(Activity context, int themeResId) {
        super(context, themeResId);
        mAttachActivity = context;
        init();
    }

    private void init() {
        setContentView(R.layout.dialog_choose_address);
        vpAddress = (ViewPager) findViewById(R.id.vp_address);
        stCity = (SlidingTabLayout) findViewById(R.id.st_city);
        ivClose = (ImageView) findViewById(R.id.iv_close);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        pagerAdapter = new ViewPagerAdapter(mAttachActivity, vpAddress);
        vpAddress.setAdapter(pagerAdapter);
        vpAddress.setOffscreenPageLimit(3);
        stCity.setViewPager(vpAddress);
        //pagerAdapter的回调
        pagerAdapter.setOnAddressChooseListener(new ViewPagerAdapter.OnAddressChooseListener() {
            @Override
            public void onChoose(String province, String city, String region, String street) {
                if (listener != null) {
                    //dialog的回调
                    listener.onChoose(province, city, region, street);
                }
                //延迟消失时为了显示选择完成时可以看到选择了哪项
                vpAddress.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dismiss();
                    }
                }, 100);
            }
        });

        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams attributes = window.getAttributes();
            attributes.height = (int) (getScreenHeight() * (0.56));
            attributes.width = getScreenWidth();
            attributes.gravity = Gravity.BOTTOM;
            window.setAttributes(attributes);
            window.setWindowAnimations(R.style.dialogWindowAnim); //设置窗口弹出动画
        }
        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }

    /**
     * 初始化数据
     *
     * @param provinces
     */
    public void setData(ArrayList<Province> provinces) {
        pagerAdapter.setData(provinces);
    }

    private OnAddressChooseListener listener;

    /**
     * 地址选择完成的回调
     *
     * @param listener
     */
    public void setOnAddressChooseListener(OnAddressChooseListener listener) {
        this.listener = listener;
    }

    public interface OnAddressChooseListener {
        void onChoose(String province, String city, String region, String street);
    }

    /**
     * 获取屏幕宽度
     */
    public int getScreenWidth() {
        return mAttachActivity.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 获取屏幕高度
     */
    public int getScreenHeight() {
        return mAttachActivity.getResources().getDisplayMetrics().heightPixels;
    }

}
