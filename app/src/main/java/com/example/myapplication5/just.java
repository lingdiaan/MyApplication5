package com.example.myapplication5;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.PoiSearch;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class just extends AppCompatActivity {
    private BaiduMap mBaiduMap = null;
    private MapView mMapView = null;
    private EditText mEditCity;
    private EditText mEditGeoCodeKey;
    private BitmapDescriptor mbitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_marka);
    private ListView mPoiList;
    private List<PoiInfo> mAllPoi;
    private PoiSearch mPoiSearch = null;
    private RelativeLayout mPoiDetailView;
    private LatLng point;
    private Boolean isFirstLoc = true;
    private MyLocationListenner myLocation = new MyLocationListenner();
    private LocationClient locationClient;
    private LatLng startlatLng;
    private Button find,my;
    private String PoiName;
    private AlertDialog.Builder builder=null;
    private static StringBuilder sb = new StringBuilder();
    private int i = 0;
    private LatLng endlatLng;
//    private ClusterManageer mClusterManager;


    public void onCreate(Bundle savedInstanceState) {
        //初始化点聚合管理类
//        mClusterManager = new ClusterManager<MyItem>(this, mBaiduMap);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.just);
        // 地图初始化
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
//        // 初始化搜索模块，注册事件监听
//        mSearch = GeoCoder.newInstance();
//        mSearch.setOnGetGeoCodeResultListener(this);
        mEditGeoCodeKey = (EditText) findViewById(R.id.geocodekey);
        //地图点击事件
//        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
//            @Override
//            public void onMapClick(LatLng point) {
//                showPoiDetailView(false);
//            }
//
//            @Override
//            public void onMapPoiClick(MapPoi poi) {
//
//            }
//        });

        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        //声明定位SDK核心类
        locationClient = new LocationClient(this);
        //注册监听
        locationClient.registerLocationListener(myLocation);
        //定位配置信息
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);//定位请求时间间隔
        locationClient.setLocOption(option);
        //开启定位
        locationClient.start();
//        initListener();
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                System.out.println("执行了1");
                endlatLng = marker.getPosition();
                //String weizhi = marker.getTitle();
                System.out.println(endlatLng);

//                double dis = DistanceUtil. getDistance(startlatLng, endlatLng);
//                double dis2 = DistanceUtil. getDistance(latLng, endlatLng);
//                sendRequestWithOkHttp(endlatLng.latitude,endlatLng.longitude);
//                sb = new StringBuilder();
//                sb.append( "收费标准："+i+++"元/小时").append("\n");
//                if(dis<1000){
//                    sb.append( "距离："+(int)dis+"米").append("\n");}
//                else{sb.append( "距离："+(int)dis/1000+"Km").append("\n");}
//                sb.append("剩余车位数："+spacenum+"个").append("\n");
//                sb.append("目标位置距停车场"+(int)dis2+"米").append("\n");
                Toast.makeText(just.this,endlatLng.latitude+"+"+endlatLng.longitude,Toast.LENGTH_LONG);
                return true;
            }
        });





    }

    //ClusterItem接口的实现类
//    public class MyItem implements ClusterItem {
//        LatLng mPosition;
//        public MyItem(LatLng position) {
//            mPosition = position;
//        }
//        @Override
//        public LatLng getPosition() {
//            return mPosition;
//        }
//        @Override
//        public BitmapDescriptor getBitmapDescriptor() {
//            return BitmapDescriptorFactory
//                    .fromResource(R.drawable.icon_markb);
//        }
//    }


    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null ||  mMapView == null) {
                return;
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                startlatLng = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(startlatLng).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
//                PoiNearbySearchOption nearbySearchOption = new PoiNearbySearchOption()
//                        .keyword("停车场") // 检索关键字
//                        .location(startlatLng) // 经纬度
//                        .radius(10000);// 检索半径 单位： m
                // 分页编号
//                mPoiSearch.searchNearby(nearbySearchOption);
//                showPoiDetailView(false);
                sendRequestWithOkHttp(location.getLatitude(),location.getAltitude());
            }
        }
    }

    private void sendRequestWithOkHttp(final double lat, final double lon) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();//创建一个OkHttp实例
                    Request request = new Request.Builder().get().url("https://api.ohaiyo.vip/parkinglot/?longitude"+(lon-1)+"longitude"+(lon+1)).build();//创建Request对象发起请求,记得替换成你自己的key



                    //Request request = new Request.Builder().url("https://api.seniverse.com/v3/weather/now.json?key=SrvH71t8JeTOXNLJP&location=beijing&language=zh-Hans&unit=c").build();//创建Request对象发起请求,记得替换成你自己的key
                    Response response = client.newCall(request).execute();//创建call对象并调用execute获取返回的数据
                    String responseData = response.body().string();
                    parseJSONWithJSONObjectGetMark(responseData);//解析SSON数据
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void parseJSONWithJSONObjectGetMark(String jsonData) {//用JSONObect解析JSON数据
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            System.out.println("执行了1");
            JSONArray results = jsonObject.getJSONArray("results");//得到键为results的JSONArray
            System.out.println("执行了2");
//            JSONObject obj = (JSONObject)results.opt(0);
//            spacenum = obj.getString("space_num");  //获得s剩余停车位数量
            for(int i = 0;i<results.length();i++){
                JSONObject obj = results.getJSONObject(i);
                double lat = obj.getDouble("latitude");
                double lon = obj.getDouble("longitude");
//                String name = obj.getString("name");
                //定义Maker坐标点
                LatLng point = new LatLng(lat, lon);
//构建Marker图标
//                BitmapDescriptor bitmap = BitmapDescriptorFactory
//                        .fromResource(R.drawable.icon_marka);
//构建MarkerOption，用于在地图上添加Marker
                OverlayOptions option = new MarkerOptions()
                        .position(point)
                        .icon(mbitmap);
//在地图上添加Marker，并显示
                mBaiduMap.addOverlay(option);
                System.out.println(lat+","+lon);


            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



}

