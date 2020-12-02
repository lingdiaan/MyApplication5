package com.example.myapplication5;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class sa extends AppCompatActivity implements View.OnClickListener {
    private TextView responseText;
    private EditText weather;
    private EditText city;
    private EditText temperature;
    private String Weather;
    private String CityName;
    private String Tempeature;
    private AlertDialog.Builder builder=null;
    private String location;
    private double lat,lon;
    private LatLng desLatLng;
    private EditText erc;
    private String newlat,newlon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sa);
        Button sendRequest = (Button) findViewById(R.id.send_request);
        responseText = (TextView) findViewById(R.id.response);
        weather = (EditText) findViewById(R.id.Weather);
        city = (EditText) findViewById(R.id.City);
        temperature = (EditText) findViewById(R.id.Temperature);
        erc = (EditText)findViewById(R.id.rec);
        sendRequest.setOnClickListener(this);
        lat = 43.822568;
        lon = 125.281001;
        desLatLng = change(lat,lon);



    }

    private LatLng change(double lat,double lon){
        LatLng point = new LatLng(lat, lon);
        CoordinateConverter converter  = new CoordinateConverter();
        converter.from(CoordinateConverter.CoordType.COMMON);
// sourceLatLng待转换坐标
        converter.coord(point);
        LatLng desLatLng = converter.convert();
        return desLatLng;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.send_request) {
            sendRequestWithOkHttp(desLatLng.latitude,desLatLng.longitude);
        }
        if(v.getId() == R.id.send){

        }
    }

    private void sendRequestWithOkHttp(final double lat, final double lon) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();//创建一个OkHttp实例
                    int j = 1;
                    while(true) {

                        Request request = new Request.Builder().get().url("https://api.ohaiyo.vip/parkinglot/?p="+j).build();
                        ;//创建Request对象发起请求,记得替换成你自己的key


                        //Request request = new Request.Builder().url("https://api.seniverse.com/v3/weather/now.json?key=SrvH71t8JeTOXNLJP&location=beijing&language=zh-Hans&unit=c").build();//创建Request对象发起请求,记得替换成你自己的key
                        Response response = client.newCall(request).execute();//创建call对象并调用execute获取返回的数据
                        String responseData = response.body().string();
//                        showResPonse(responseData);//显示原始数据和解析后的数据
//                    parseJSONWithJSONObject(responseData);//解析SSON数据
                        JSONObject jsonObject = new JSONObject(responseData);

                        JSONArray results = jsonObject.getJSONArray("results");//得到键为results的JSONArray

//            JSONObject obj = (JSONObject)results.opt(0);
//            spacenum = obj.getString("space_num");  //获得s剩余停车位数量
                        for(int i = 0;i<results.length();i++){
                            JSONObject obj = results.getJSONObject(i);
                            Weather = obj.getString("name");//得到"now"键值的JSONObject下的"text"属性,即天气信息
                            CityName = obj.getString("space_num");  //获得城市名
                            System.out.println(Weather+"------------"+CityName+"---------"+"第"+j+"页");
                        }
                        j++;
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void parseJSONWithJSONObject(String jsonData) {//用JSONObect解析JSON数据
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
//            JSONArray results = jsonObject.getJSONArray("results");//得到键为results的JSONArray
//            JSONObject obj = (JSONObject)results.opt(0);
//            //JSONObject results = jsonObject.getJSONObject("results");
//            //JSONObject now = results.getJSONObject(0).getJSONObject("now");//得到键值为"now"的JSONObject
//            JSONObject name = results.getJSONObject(0).getJSONObject("name");
//            //JSONObject space = results.getJSONObject(0).getJSONObject("space_num");
//            JSONObject address = results.getJSONObject(0).getJSONObject("address");
//            JSONObject location = results.getJSONObject(0).getJSONObject("location");   //得到键值为location的JSONObject
//            //JSONObject location = results.getJSONObject(0).getJSONObject("images");
//            //Weather = results.getString("text");//得到"now"键值的JSONObject下的"text"属性,即天气信息
//            Weather = obj.getString("name");//得到"now"键值的JSONObject下的"text"属性,即天气信息
//            CityName = obj.getString("space_num");  //获得城市名
////            CityName = results.getString("path");  //获得城市名
////            //Tempeature = results.getString("temperature"); //获取温度
////            Tempeature = obj.getString("address"); //获取温度
             location = jsonObject.getString("locations");


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showResPonse(final String response) {
        runOnUiThread(new Runnable() {//切换到主线程,ui界面的更改不能出现在子线程,否则app会崩溃
            @Override
            public void run() {
//                builder=new AlertDialog.Builder(sa.this);
//                builder.setTitle(response).setMessage(location).create().show();
                String[] strings = location.split(",");
                responseText.setText(response);
                if(Integer.valueOf(strings[0].charAt(10))>=5) {
                     newlat = String.valueOf(Double.valueOf(strings[0].substring(0,10))+0.000001);
                }
                if(Integer.valueOf(strings[1].charAt(9))>=5) {
                     newlon = String.valueOf(Double.valueOf(strings[1].substring(0,9))+0.00001);
                }

                temperature.setText(strings[0].substring(0,10)+","+strings[1].substring(0,9));

                weather.setText(String.valueOf(desLatLng.latitude)+","+String.valueOf(desLatLng.longitude));
                city.setText(String.valueOf(lat)+","+String.valueOf(lon));
                erc.setText(newlat+","+newlon);
//                city.setText(CityName);
//                weather.setText(Weather);
//                temperature.setText(Tempeature);
            }
        });
    }
}
