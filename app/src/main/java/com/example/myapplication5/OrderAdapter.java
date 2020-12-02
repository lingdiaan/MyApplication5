package com.example.myapplication5;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;



import java.util.List;

public class OrderAdapter extends ArrayAdapter<Order> {
    private int resourceId;

    public OrderAdapter(@NonNull Context context,int textViewResourceId, @NonNull List<Order> objects) {
        super(context,  textViewResourceId, objects);
        resourceId = textViewResourceId;
    }




    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Order order = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
        TextView orderName = (TextView)view.findViewById(R.id.order_name);
        TextView orderData = (TextView)view.findViewById(R.id.order_time);
//        TextView orderId = (T)
        orderName.setText(order.getParkName());

        orderData.setText(order.getParkTime());
//        order
        return view;
    }
}
