package com.example.myapplication5;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class sa extends AppCompatActivity  {
private ListView mPoiList;
private List<Park> parkList=new ArrayList<>();
private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sa);
        mPoiList=(ListView)findViewById(R.id.list) ;
        parkList.add(new Park("123",5,41.22,123.32));
        handler = new Handler();
 handler.post(runnableUi1);

    }
    Runnable runnableUi1=new Runnable(){
        @Override
        public void run() {
            ParkAdapter adapter = new ParkAdapter(sa.this, R.layout.park_item, parkList);
            System.out.println(adapter.toString());
            mPoiList = (ListView)findViewById(R.id.poi_list);
            System.out.println("onclick+++++++++++++++++++++++++++++");
            mPoiList.setAdapter(adapter);
            System.err.println(parkList);
            mPoiList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Park park = parkList.get(position);
                    Toast.makeText(sa.this,park.getName()+"--------"+park.getSpace_num(),Toast.LENGTH_LONG);


                }
            });
//更新界面

        }

    };



}
