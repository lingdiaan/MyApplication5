package com.example.myapplication5;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.baidu.mapapi.utils.DistanceUtil;
import com.example.myapplication5.clusterutil.clustering.Cluster;
import com.example.myapplication5.clusterutil.clustering.ClusterManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * 此demo用来展示如何进行地理编码搜索（用地址检索坐标）
 */
public class GeoCoderDemo extends AppCompatActivity implements OnGetGeoCoderResultListener, AdapterView.OnItemClickListener, PoiListAdapter.OnGetChildrenLocationListener, OnGetSuggestionResultListener {

    // 搜索模块，也可去掉地图模块独立使用
    private GeoCoder mSearch = null;
    private BaiduMap mBaiduMap = null;
    private MapView mMapView = null;
    private EditText mEditCity;
    private AutoCompleteTextView mEditGeoCodeKey;
    private BitmapDescriptor mbitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_marka);
    private ListView mPoiList;
    private List<PoiInfo> mAllPoi;
    private PoiSearch mPoiSearch = null;
    private RelativeLayout mPoiDetailView;
    private LatLng point;
    private Boolean isFirstLoc = true;
    private MyLocationListenner myListener = new MyLocationListenner();
    private LocationClient locationClient;
    private LatLng startlatLng;
    private Button find,my;
    private String PoiName;
    private AlertDialog.Builder builder=null;
    private static StringBuilder sb = new StringBuilder();
    private int i = 0;
    private LatLng endlatLng;
    private String spacenum,name;
    private SuggestionSearch mSuggestionSearch = null;
    private ListView mSugListView;
    private int flag = 0;
    private String location;
    private MapStatus mMapStatus;
    private ClusterManager<MyItem> mClusterManager;
    private String id;
    public static GeoCoderDemo geoCoderDemo = null;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_geocoder);
        CharSequence titleLable = "停车APP";
        setTitle(titleLable);
        geoCoderDemo = this;
        mPoiSearch = PoiSearch.newInstance();
        mEditCity = (EditText) findViewById(R.id.city);
//        mPoiSearch.setOnGetPoiSearchResultListener(this);
        mPoiDetailView = (RelativeLayout) findViewById(R.id.poi_detail);
        mPoiList = (ListView) findViewById(R.id.poi_list);
        mPoiList.setOnItemClickListener(this);
//        find = (Button)findViewById(R.id.find);
        my = (Button)findViewById(R.id.my);
        mEditGeoCodeKey = (AutoCompleteTextView) findViewById(R.id.geocodekey);
        mSugListView = (ListView) findViewById(R.id.sug_list);





        // 初始化建议搜索模块，注册建议搜索事件监听
        mSuggestionSearch = SuggestionSearch.newInstance();
        mSuggestionSearch.setOnGetSuggestionResultListener(this);

        mEditGeoCodeKey.setThreshold(1);
        mEditGeoCodeKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSugListView.setVisibility(View.VISIBLE);
            }
        });

        // 当输入关键字变化时，动态更新建议列表
        mEditGeoCodeKey.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

            }

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                if (cs.length() <= 0) {
                    return;
                }
//                mSugListView.setVisibility(View.VISIBLE);

                // 使用建议搜索服务获取建议列表，结果在onSuggestionResult()中更新
                mSuggestionSearch.requestSuggestion((new SuggestionSearchOption())
                        .keyword(cs.toString()) // 关键字
                        .city(mEditCity.getText().toString())); // 城市
            }
        });


      my.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              Intent intent = new Intent(GeoCoderDemo.this,myActivity.class);
              startActivity(intent);
          }
      });

        // 地图初始化
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        // 初始化搜索模块，注册事件监听
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(this);

        //地图点击事件
        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
//                showPoiDetailView(false);
                mSugListView.setVisibility(View.GONE);
            }

            @Override
            public void onMapPoiClick(MapPoi poi) {

            }
        });

        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        //声明定位SDK核心类
        locationClient = new LocationClient(this);
        //注册监听
        locationClient.registerLocationListener(myListener);
        //定位配置信息
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);//定位请求时间间隔
        locationClient.setLocOption(option);
        //开启定位
        locationClient.start();
//        initListener();
        // 定义点聚合管理类ClusterManager
        mClusterManager = new ClusterManager<MyItem>(this, mBaiduMap);
        // 设置地图监听，当地图状态发生改变时，进行点聚合运算
        mBaiduMap.setOnMapStatusChangeListener(mClusterManager);
        // 设置maker点击时的响应
        mBaiduMap.setOnMarkerClickListener(mClusterManager);

        mClusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<MyItem>() {
            @Override
            public boolean onClusterClick(Cluster<MyItem> cluster) {
                System.out.println("onclick+++++++++++++++++++++++++++++");
                Toast.makeText(GeoCoderDemo.this, "有" + cluster.getSize() + "个停车场", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        mClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<MyItem>() {
            @Override
            public boolean onClusterItemClick(MyItem item) {
                endlatLng = item.getPosition();
                System.out.println(endlatLng+"222222222222222222");
                final Intent intent = new Intent(GeoCoderDemo.this,DrivingRoutSearch1.class);
                sendRequestWithOkHttp(endlatLng.latitude,endlatLng.longitude);
                Bundle budle = new Bundle();
                budle.putDouble("经度",endlatLng.latitude);
                budle.putDouble("纬度", endlatLng.longitude);
                intent.putExtras(budle);
                double dis = DistanceUtil. getDistance(startlatLng, endlatLng);
                return true;
            }
        });



    }



    /**
     * 发起搜索
     */
    public void searchButtonProcess(View v) {
            // 发起Geo搜索
            mSearch.geocode(new GeoCodeOption()
                .city(mEditCity.getText().toString())// 城市
                .address(mEditGeoCodeKey.getText().toString())); // 地址
    }
    /**
     * 地理编码查询结果回调函数
     *
     * @param result  地理编码查询结果
     */
    @Override
    public void onGetGeoCodeResult(GeoCodeResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(GeoCoderDemo.this, "抱歉，未能找到结果", Toast.LENGTH_LONG).show();
            return;
        }
        String strInfo = String.format("纬度：%f 经度：%f", result.getLocation().latitude, result.getLocation().longitude);
       //Toast.makeText(GeoCoderDemo.this, strInfo, Toast.LENGTH_LONG).show();
        Intent intent = new Intent(GeoCoderDemo.this,PoiBoundSearchi.class);
        Bundle bundle = new Bundle();
        bundle.putString("位置",strInfo);
        bundle.putDouble("纬度",result.getLocation().latitude);
        bundle.putDouble("经度",result.getLocation().longitude);
        intent.putExtras(bundle);
        startActivity(intent);
    }
    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        PoiInfo poiInfo = mAllPoi.get(position);
        if (poiInfo.getLocation() == null) {
            return;
        }

        addPoiLoction(poiInfo.getLocation());
    }
    public void getChildrenLocation(LatLng childrenLocation) {
        addPoiLoction(childrenLocation);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onGetSuggestionResult(SuggestionResult suggestionResult) {
        if (suggestionResult == null || suggestionResult.getAllSuggestions() == null) {
            return;
        }

        List<HashMap<String, String>> suggest = new ArrayList<>();
        for (SuggestionResult.SuggestionInfo info : suggestionResult.getAllSuggestions()) {
            if (info.getKey() != null && info.getDistrict() != null && info.getCity() != null) {
                HashMap<String, String> map = new HashMap<>();
                map.put("key",info.getKey());
                map.put("city",info.getCity());
                map.put("dis",info.getDistrict());
                suggest.add(map);
            }
        }

        SimpleAdapter simpleAdapter = new SimpleAdapter(getApplicationContext(),
                suggest,
                R.layout.item_layout,
                new String[]{"key", "city","dis"},
                new int[]{R.id.sug_key, R.id.sug_city, R.id.sug_dis});

        mSugListView.setAdapter(simpleAdapter);
        simpleAdapter.notifyDataSetChanged();
        mSugListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mEditGeoCodeKey.setText(suggest.get(position).get("key"));
                mEditCity.setText(suggest.get(position).get("city"));
                mSugListView.setVisibility(View.GONE);


            }
        });
        System.out.println(LoadActivity.token);
//
    }

    public void initListener(){
        System.out.println("-----------------------------------------+++-----");
        builder = null;
        builder=new AlertDialog.Builder(this);

        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            public boolean onMarkerClick(final Marker marker) {
                endlatLng = marker.getPosition();
                System.out.println(endlatLng+"1111111111111111");
                final Intent intent = new Intent(GeoCoderDemo.this,DrivingRoutSearch1.class);
                Bundle budle = new Bundle();
                budle.putDouble("经度",endlatLng.latitude);
                budle.putDouble("纬度", endlatLng.longitude);
                intent.putExtras(budle);
                double dis = DistanceUtil. getDistance(startlatLng, endlatLng);
                sendRequestWithOkHttp(endlatLng.latitude,endlatLng.longitude);
                return true;
            }
        });
    }

    private void addPoiLoction(LatLng latLng){
        mBaiduMap.clear();
        showPoiDetailView(false);
        OverlayOptions markerOptions = new MarkerOptions().position(latLng).icon(mbitmap);
        mBaiduMap.addOverlay(markerOptions);
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(latLng);
        builder.zoom(18);
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
    }

    /**
     * 是否展示详情 view
     *
     */
    private void showPoiDetailView(boolean whetherShow) {
        if (whetherShow) {
            mPoiDetailView.setVisibility(View.VISIBLE);

        } else {
            mPoiDetailView.setVisibility(View.GONE);

        }
    }

    /**
     * 定位功能
     */




    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null ||  mMapView == null) {
                Toast.makeText(GeoCoderDemo.this,"未打开定位权限",Toast.LENGTH_SHORT).show();
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
//                MapStatus.Builder builder = new MapStatus.Builder();
//                builder.target(startlatLng).zoom(15);
                mMapStatus = new MapStatus.Builder().zoom(15).target(startlatLng).build();
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(mMapStatus));

                sendRequestWithOkHttpGetMark(location.getLatitude(),location.getAltitude());
            }
            }
        }

    private void sendRequestWithOkHttpGetMark(final double lat, final double lon) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int j=1;
                try {


                        OkHttpClient client = new OkHttpClient();//创建一个OkHttp实例
                        Request request = new Request.Builder().get().url("https://api.ohaiyo.vip/parkinglot/?page_size=99999999").build();
                        Response response = client.newCall(request).execute();//创建call对象并调用execute获取返回的数据
                        String responseData = response.body().string();
                        JSONObject jsonObject = new JSONObject(responseData);
                        JSONArray results = jsonObject.getJSONArray("results");//得到键为results的JSONArray
                        List<MyItem> items = new ArrayList<MyItem>();
                        for (int i = 0; i < results.length(); i++) {
                            JSONObject obj = results.getJSONObject(i);
                            double lat = obj.getDouble("bd_latitude");
                            double lon = obj.getDouble("bd_longitude");
                             //                String name = obj.getString("name");
                            //定义Maker坐标点
                            LatLng point = new LatLng(lat, lon);

                            //构建MarkerOption，用于在地图上添加Marker
                            OverlayOptions option = new MarkerOptions()
                                    .position(point)
                                    .icon(mbitmap).clickable(true).perspective(true);
                            items.add(new MyItem(point));
                            //在地图上添加Marker，并显示
                            mBaiduMap.addOverlay(option);
//                            mClusterManager.addItems(items);


                        }
                    initListener();

                        System.out.println(mClusterManager);

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    private void sendRequestWithOkHttp(final double lat, final double lon) {
        new Thread(new Runnable() {
            @Override

            public void run() {
                System.out.println("断点6");
                try {
                    OkHttpClient client = new OkHttpClient();//创建一个OkHttp实例
                    System.out.println(lat+","+lon);
                    Request request = new Request.Builder().get().url("https://api.ohaiyo.vip/parkinglot/?bd_longitude_max="+(lon+0.001)+"&bd_longitude_min="+(lon-0.001)+"&bd_latitude_max="+(lat+0.0001)+"&bd_latitude_min="+(lat-0.0001)).build();//创建Request对象发起请求,记得替换成你自己的key
                    Response response = client.newCall(request).execute();//创建call对象并调用execute获取返回的数据
                    String responseData = response.body().string();
                    parseJSONWithJSONObject(responseData);//解析SSON数据
                    showResPonse(responseData);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void parseJSONWithJSONObject(String jsonData) {//用JSONObect解析JSON数据
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            name = null;
            spacenum=null;

            JSONArray results = jsonObject.getJSONArray("results");//得到键为results的JSONArray
           JSONObject obj = (JSONObject)results.opt(0);
           spacenum = obj.getString("space_num");  //获得s剩余停车位数量
            name = obj.getString("name");
            id = obj.getString("id");
            System.out.println(name+spacenum);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showResPonse(final String response) {
        runOnUiThread(new Runnable() {//切换到主线程,ui界面的更改不能出现在子线程,否则app会崩溃
            @Override
            public void run() {

                sb = new StringBuilder();
                System.out.println(sb.toString() + "断点3");
                sb.append("收费标准：" + null + "元/小时").append("\n");
                sb.append("剩余车位数：" + spacenum + "个").append("\n");
                System.out.println(sb.toString() + "断点4");
                builder.setTitle("停车场信息:" + name).setMessage(sb.toString());//设置对话框的标题
                System.out.println(sb.toString() + "断点7");
                builder.setPositiveButton("导航", new DialogInterface.OnClickListener() {  //这个是设置确定按钮
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {


                        final Intent intent = new Intent(GeoCoderDemo.this, DrivingRoutSearch1.class);
                        Bundle budle = new Bundle();
                        budle.putDouble("经度", endlatLng.latitude);
                        budle.putDouble("纬度", endlatLng.longitude);
                        budle.putString("位置", PoiName);
                        intent.putExtras(budle);
                        startActivity(intent);
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {  //取消按钮

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        flag = 0;


                    }
                });
                builder.setNeutralButton("预定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (Integer.valueOf(spacenum) == 0) {
                            Toast.makeText(GeoCoderDemo.this, "无剩余车位", Toast.LENGTH_LONG).show();
                        }else{
                            Intent intent = new Intent(GeoCoderDemo.this,timeChooseActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("name",id);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            System.out.println(bundle+"bbbbbbbbbbbuuuuuuuuuuuuuuunnnnnnnnnnnnnnnnnnnddddddddddddddddddddddd");
                        }

                    }
                });
                builder.create().show();

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        // 在activity执行onResume时必须调用mMapView. onResume ()
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 在activity执行onPause时必须调用mMapView. onPause ()
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mbitmap.recycle();
        // 释放检索对象
        mSearch.destroy();
        // 清除所有图层
        mBaiduMap.clear();
        // 在activity执行onDestroy时必须调用mMapView. onDestroy ()
        mMapView.onDestroy();
//        mSuggestionSearch.destroy();
    }





}
