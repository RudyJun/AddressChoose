package com.rudy.addresschoose.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.rudy.addresschoose.R;
import com.rudy.addresschoose.bean.Item;
import com.rudy.addresschoose.bean.Province;

import java.util.ArrayList;
/**
 * Created by liuzaijun on 2017/8/17.
 */
public class ViewPagerAdapter extends PagerAdapter {

    private ArrayList<String> tabList;
    private Context context;
    private ArrayList<Province> provinces;
    private ArrayList<ArrayList<Item>> itemList;
    private ViewPager mViewPager;
    private int pPosition = -1;
    private int cPosition = -1;
    private int rPosition = -1;

    public ViewPagerAdapter(Context context, ViewPager viewPager) {
        this.context = context;
        this.tabList = new ArrayList<>();
        this.itemList = new ArrayList<>();
        this.mViewPager = viewPager;
        if (viewPager == null) {
            throw new IllegalArgumentException("viewPager is null , please check");
        }
    }

    /**
     * 省列表，其中每一个省下又有市、区、街道（可能有也可能没有）
     *
     * @param provinces
     */
    public void setData(ArrayList<Province> provinces) {
        if (provinces == null) {
            return;
        }
        this.provinces = provinces;
        ArrayList<Item> provinceItem = new ArrayList<>();
        //遍历provinces，得到省
        for (Province province : provinces) {
            Item item = new Item(province.name);
            provinceItem.add(item);
        }
        //添加省列表
        itemList.add(provinceItem);
        //添加一个tab
        tabList.add("请选择");
        //调用notifyDataSetChanged（）方法，以便刷新tab的内容（最开始tab和viewpager是没有内容的）
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return tabList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public View instantiateItem(ViewGroup container, final int position) {
        View view = View.inflate(context, R.layout.vp_item_address, null);
        view.setTag(position);
        //rvAddress用来填充具体的省、市、区、街道（可能是没有的，得看选中的地区）
        RecyclerView rvAddress = (RecyclerView) view.findViewById(R.id.rv_address);
        //布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvAddress.setLayoutManager(layoutManager);
        //适配器
        AddressAdapter addressAdapter = new AddressAdapter(context, itemList.get(position));
        rvAddress.setAdapter(addressAdapter);
        addressAdapter.setOnItemClikListener(new AddressAdapter.OnItemClikListener() {
            @Override
            public void onItemClick(String itemName, int addressPos) {
                //替换tab的内容，如原来显示的tab是请选择，选中广东省后，替换position为0所对应的string为广东省
                replaceStr(position, itemName);
                //这个list用来存储下一级列表的内容，如广东省下的市
                ArrayList<Item> cityItem = new ArrayList<>();
                //标识此次选择的省、市、区、街道与上次选择的是否一样
                boolean isChange = false;
                switch (position) {
                    case 0:
                        //根据选中的位置判断是否选中的是同一个省或同一个市...其他case也是如此
                        if (addressPos != pPosition) {
                            //此次选择与上次不同，需要截断之后的内容，如上次选择广东省，这次选择北京
                            //那么需要截断广东省后的市、区、街道，即删除省之后的选择界面,从重置省之后的选中position
                            truncateList(position + 1);
                            isChange = true;
                            cPosition = -1;
                            rPosition = -1;
                        }
                        pPosition = addressPos;//记录该次选择省的位置
                        //遍历得到该省下的市级列表
                        for (Province.City city : provinces.get(pPosition).array) {
                            Item item = new Item(city.name);
                            cityItem.add(item);
                        }
                        break;
                    case 1:
                        if (addressPos != cPosition) {
                            truncateList(position + 1);
                            isChange = true;
                            rPosition = -1;
                        }
                        cPosition = addressPos;
                        for (Province.Region region : provinces.get(pPosition).array.get(cPosition).array) {
                            Item item = new Item(region.name);
                            cityItem.add(item);
                        }
                        break;
                    case 2:
                        if (addressPos != rPosition) {
                            truncateList(position + 1);
                            isChange = true;
                        }
                        rPosition = addressPos;
                        //该区是否有街道的列表
                        if (provinces.get(pPosition).array.get(cPosition).array.get(rPosition).array != null
                                && !provinces.get(pPosition).array.get(cPosition).array.get(rPosition).array.isEmpty()) {
                            for (String street : provinces.get(pPosition).array.get(cPosition).array.get(rPosition).array) {
                                Item item = new Item(street);
                                cityItem.add(item);
                            }
                        }
                        break;
                    case 3:
                        break;
                }
                dealDataAndScroll(isChange, cityItem, position + 1);
            }
        });
        container.addView(view);
        return view;
    }

    private void truncateList(int fromIndex) {
        if (tabList.size() > fromIndex) {
            tabList.subList(fromIndex, tabList.size()).clear();
            itemList.subList(fromIndex, itemList.size()).clear();
        }
    }

    /**
     * 替换该postion对应的string
     *
     * @param position
     * @param itemName
     */
    private void replaceStr(int position, String itemName) {
        tabList.remove(position);
        tabList.add(position, itemName);
    }

    /**
     * @param isChange 与上次的选择是否一致
     * @param cityItem 存储下一级地区的list
     * @param position 需要滑动至viewpager的哪个位置
     */
    private void dealDataAndScroll(boolean isChange, ArrayList<Item> cityItem, final int position) {
        //判断cityItem是否为空，不为空说明还有下一级列表，否则说明地区选择完成
        if (!cityItem.isEmpty()) {
            //如果与上次选择不一致，之前已经做了内容的清除，现在新增一个tab
            if (isChange) {
                tabList.add("请选择");
                itemList.add(cityItem);
                //通知adapter，数据源已发生改变，需要刷新界面
                notifyDataSetChanged();
            }
            //延时滑动，以便有简短的事件可以看到选中的项
            mViewPager.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mViewPager.setCurrentItem(position);
                }
            }, 100);
        } else {
            notifyDataSetChanged();
            //地址选择完成
            if (listener != null) {
                String province = "";
                String city = "";
                String region = "";
                String street = "";
                for (int i = 0; i < tabList.size(); i++) {
                    String str = tabList.get(i);
                    switch (i) {
                        case 0:
                            province = str;
                            break;
                        case 1:
                            city = str;

                            break;
                        case 2:
                            region = str;

                            break;
                        case 3:
                            street = str;
                            break;
                    }
                }
                listener.onChoose(province, city, region, street);
            }
        }
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

    @Override
    public int getItemPosition(Object object) {
        View view = (View) object;
        int position = (int) view.getTag();
        //viewpager的局部刷新，想了解原理看请源码或资料
        if (position > mViewPager.getCurrentItem()) {
            //需要重绘
            return POSITION_NONE;
        } else {
            //不需要重绘
            return POSITION_UNCHANGED;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabList.get(position);
    }
}