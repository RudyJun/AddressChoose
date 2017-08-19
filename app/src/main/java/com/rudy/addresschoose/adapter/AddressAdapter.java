package com.rudy.addresschoose.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rudy.addresschoose.R;
import com.rudy.addresschoose.bean.Item;

import java.util.ArrayList;

/**
 * Created by liuzaijun on 2017/8/17.
 */

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Item> items;

    public AddressAdapter(Context context, ArrayList<Item> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.rv_item_address, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.tvAddress.setText(items.get(position).itemName);
        if (items.get(position).isChoose) {
            holder.ivChoose.setVisibility(View.VISIBLE);
            holder.tvAddress.setTextColor(Color.parseColor("#ff3e3e"));
        } else {
            holder.ivChoose.setVisibility(View.GONE);
            holder.tvAddress.setTextColor(Color.parseColor("#4a4a4a"));
        }

        holder.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //处理当前的点击事件，即勾选地址
                holder.ivChoose.setVisibility(View.VISIBLE);
                items.get(position).isChoose = true;
                for (Item item : items) {
                    if (!item.itemName.equals(items.get(position).itemName) && item.isChoose) {
                        item.isChoose = false;
                        break;
                    }
                }
                notifyDataSetChanged();
                if (listener != null) {
                    listener.onItemClick(items.get(position).itemName, position);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private OnItemClikListener listener;

    /**
     * 点击item的回调
     * @param listener
     */
    public void setOnItemClikListener(OnItemClikListener listener) {
        this.listener = listener;
    }

    public interface OnItemClikListener {
        void onItemClick(String itemName, int addressPos);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvAddress;
        public ImageView ivChoose;

        public ViewHolder(View itemView) {
            super(itemView);
            tvAddress = (TextView) itemView.findViewById(R.id.tv_address);
            ivChoose = (ImageView) itemView.findViewById(R.id.iv_choose);
        }

        public View getConvertView() {
            return itemView;
        }
    }
}
