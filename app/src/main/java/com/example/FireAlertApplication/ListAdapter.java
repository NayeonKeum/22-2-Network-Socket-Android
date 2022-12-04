package com.example.FireAlertApplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ListAdapter extends BaseAdapter {
    Context context = null;
    LayoutInflater layoutInflater = null;
    ArrayList<chatroomModel> data;

    public ListAdapter(Context context, ArrayList<chatroomModel> data) {
        this.context = context;
        this.data = data;
        this.layoutInflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        if (data!=null) return data.size();
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View converView, ViewGroup viewGroup) {
        View view = layoutInflater.inflate(R.layout.item_list, null);

        TextView tvPort = (TextView)view.findViewById(R.id.tv_port);
//        TextView tvArea = (TextView)view.findViewById(R.id.tv_area);

        tvPort.setText(String.valueOf(data.get(position).getPort()));
//        tvArea.setText(data.get(position).getArea());

        return view;

    }
}
