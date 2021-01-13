package com.example.myapplication5;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class ParkAdapter extends ArrayAdapter<Park> {
    private int resourceId;

    public ParkAdapter(@NonNull Context context, int textViewResourceId, @NonNull List<Park> objects) {
        super(context,textViewResourceId,objects);
        resourceId = textViewResourceId;
    }




    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Park park = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
        TextView parkName = (TextView)view.findViewById(R.id.park_name);

        TextView spaceNum=(TextView)view.findViewById(R.id.space_num);
//        TextView orderId = (T)
        parkName.setText(park.getName());


        spaceNum.setText(park.getSpace_num()+"个");
//        order
        return view;
    }
}

