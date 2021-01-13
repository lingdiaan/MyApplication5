package com.example.myapplication5;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
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
import com.baidu.mapapi.map.InfoWindow;
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
    private BitmapDescriptor mbitmap = BitmapDescriptorFactory.fromResource(R.drawable.park_markp);
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
    private float localZoom;
    private  BaiduMap.OnMarkerClickListener onMarkerClickListener;
    private List<Marker> markers = new ArrayList<>();
    private float x;
    private float suofang;
    private int[] counts;
    private String[] names;
    private List<Park> parkList= new ArrayList<>();
    private String parkName,parkCost,spaceNum;
    private Handler handler;
    private Button showList;
    private double[] latNum;
    private double[] lonNum;
    private Marker markerClick;





    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_geocoder);
        CharSequence titleLable = "PISP";
        setTitle(titleLable);

        geoCoderDemo = this;
        mPoiSearch = PoiSearch.newInstance();
        mEditCity = (EditText) findViewById(R.id.city);
//        mPoiSearch.setOnGetPoiSearchResultListener(this);
        mPoiDetailView = (RelativeLayout) findViewById(R.id.poi_detail);
        mPoiList = (ListView) findViewById(R.id.poi_list);
        showList = (Button)findViewById(R.id.show_list);
        showList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPoiList.setVisibility(view.VISIBLE);


            }
        });

//        find = (Button)findViewById(R.id.find);
        my = (Button)findViewById(R.id.my);
        mEditGeoCodeKey = (AutoCompleteTextView) findViewById(R.id.geocodekey);
        mSugListView = (ListView) findViewById(R.id.sug_list);

        // 初始化建议搜索模块，注册建议搜索事件监听
        mSuggestionSearch = SuggestionSearch.newInstance();
        mSuggestionSearch.setOnGetSuggestionResultListener(this);
        requestPermission();
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
                mPoiList.setVisibility(View.GONE);
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

                Toast.makeText(GeoCoderDemo.this, "有" + cluster.getSize() + "个停车场", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        mClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<MyItem>() {
            @Override
            public boolean onClusterItemClick(MyItem item) {

                return true;
            }
        });
        handler = new Handler();
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
//        Intent intent = new Intent(GeoCoderDemo.this,PoiBoundSearchi.class);
        Intent intent = new Intent(GeoCoderDemo.this,PoiBoundSearch.class);
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

//    public void initListener(){
//
//        builder = null;
//        builder=new AlertDialog.Builder(this);
//        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
//            public boolean onMarkerClick(final Marker marker) {
//                endlatLng = marker.getPosition();
//                final Intent intent = new Intent(GeoCoderDemo.this,DrivingRoutSearch1.class);
//                Bundle budle = new Bundle();
//                budle.putDouble("经度",endlatLng.latitude);
//                budle.putDouble("纬度", endlatLng.longitude);
//                intent.putExtras(budle);
//                double dis = DistanceUtil. getDistance(startlatLng, endlatLng);
//                sendRequestWithOkHttp(endlatLng.latitude,endlatLng.longitude);
//                return true;
//            }
//        });
//    }
public void initListener(){

    builder = null;
    builder=new AlertDialog.Builder(this);
    onMarkerClickListener = marker -> {
        endlatLng = marker.getPosition();
        final Intent intent = new Intent(GeoCoderDemo.this,DrivingRoutSearch1.class);
        Bundle budle = new Bundle();
        budle.putDouble("经度",endlatLng.latitude);
        budle.putDouble("纬度", endlatLng.longitude);
        intent.putExtras(budle);
        double dis = DistanceUtil. getDistance(startlatLng, endlatLng);
        sendRequestWithOkHttp(endlatLng.latitude,endlatLng.longitude);
        return true;
    };
    mBaiduMap.setOnMarkerClickListener(onMarkerClickListener);
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
                localZoom=mBaiduMap.getMapStatus().zoom;
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(mMapStatus));

//                sendRequestWithOkHttpGetMark(location.getLatitude(),location.getLongitude(),localZoom);
                mBaiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
                    @Override
                    public void onMapStatusChangeStart(MapStatus mapStatus) {



                    }

                    @Override
                    public void onMapStatusChangeStart(MapStatus mapStatus, int i) {

                    }

                    @Override
                    public void onMapStatusChange(MapStatus mapStatus) {


                    }

                    @Override
                    public void onMapStatusChangeFinish(MapStatus mapStatus) {
                        float zoom = mBaiduMap.getMapStatus().zoom;
                        //根据获取到的地图中心点(图标地点)坐标获取地址
                        LatLng ptCenter = mapStatus.target;
                        if (zoom!=localZoom){
                            mBaiduMap.clear();
                            sendRequestWithOkHttpGetMark(startlatLng.latitude,startlatLng.longitude,zoom);
                            localZoom =zoom;

                        }

                    }
                });
            }
            }
        }

    private void sendRequestWithOkHttpGetMark(final double lat, final double lon,float localZoom) {
        if(localZoom>17.5)
            localZoom= (float) 17.5;
        else if(localZoom<14.5)
            localZoom= (float) 14.5;
        x= (float) (0.3*(int)localZoom-4.25);
        suofang = (float)(((float)localZoom)*0.16-1.62);
        new Thread(new Runnable() {
            @Override
            public void run() {
                parkList.clear();

                try {
                    mBaiduMap.clear();
                    mBaiduMap.removeMarkerClickListener(onMarkerClickListener);
                        OkHttpClient client = new OkHttpClient();//创建一个OkHttp实例
                        Request request = new Request.Builder().get().url("https://api.ohaiyo.vip/parkinglot/?bd_latitude_max="+(lat+0.018)+"&bd_latitude_min="+(lat-0.018)+"&bd_longitude_max="+(lon+0.022)+"&bd_longitude_min="+(lon-0.022)+"&page_size=99999999").build();
                        Response response = client.newCall(request).execute();//创建call对象并调用execute获取返回的数据
                        String responseData = response.body().string();
                        JSONObject jsonObject = new JSONObject(responseData);
                        JSONArray results = jsonObject.getJSONArray("results");//得到键为results的JSONArray
                        List<MyItem> items = new ArrayList<MyItem>();
                        int count=0;
                        latNum = new double[results.length()];
                        lonNum = new double[results.length()];
                        counts = new int[results.length()];
                        names = new String[results.length()];
                        for (int i = 0; i < results.length(); i++) {
                            JSONObject obj = results.getJSONObject(i);
                            latNum[i]  = obj.getDouble("bd_latitude");
                            lonNum[i]  = obj.getDouble("bd_longitude");
                             //                String name = obj.getString("name");
                            counts[i] = obj.getInt("space_num");
                            names[i]=obj.getString("name");

                        }
                    for(int yi=0;yi<counts.length-1;yi++)
                    {
                        for(int er=0;er<counts.length-1-yi;er++)
                        {
                            if(counts[er]>counts[er+1])
                            {
                                int temp=counts[er];
                                counts[er]=counts[er+1];
                                counts[er+1]=temp;

                                double temp1=latNum[er];
                                latNum[er]=latNum[er+1];
                                latNum[er+1]=temp1;

                                double temp2=lonNum[er];
                                lonNum[er]=lonNum[er+1];
                                lonNum[er+1]=temp2;

                                String nameTem=names[er];
                                names[er]=names[er+1];
                                names[er+1]=nameTem;

                            }
                        }
                    }
                    if(x>1)
                        x=1;
                    else if(x<0.3)
                        x=(float) 0.3;
                    for(int i=results.length()-1;i>results.length()*(1-x);i--){
                        double latnow = latNum[i];
                        double lonnow = lonNum[i];
                        LatLng point = new LatLng(latnow, lonnow);
                        Park park = new Park(names[i],counts[i],latnow,lonnow);
//                        System.out.println(park);
                        parkList.add(park);
                                //构建MarkerOption，用于在地图上添加Marker

                                OverlayOptions option = new MarkerOptions()
                                        .position(point)
                                        .icon(mbitmap).clickable(true).perspective(true).zIndex(4);
                                items.add(new MyItem(point));
                                //在地图上添加Marker，并显示
                        Marker marker = (Marker) mBaiduMap.addOverlay(option);

                        markers.add(marker);

                    }
                        for(Marker marker : markers){
                            marker.setScale(suofang);
                            System.out.println(marker.getPosition());

                        }




//                    mClusterManager.addItems(items);
                    initListener();
                    handler.post(runnableUi1);

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();



    }

    Runnable runnableUi1=new Runnable(){
        @Override
        public void run() {
            ParkAdapter adapter = new ParkAdapter(GeoCoderDemo.this, R.layout.park_item, parkList);
            mPoiList = (ListView)findViewById(R.id.poi_list);
            System.out.println("onclick+++++++++++++++++++++++++++++");
            System.out.println(adapter.toString());
            mPoiList.setAdapter(adapter);
//            System.err.println(parkList);
            mPoiList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if(markerClick!=null){
                        markerClick.setScale(suofang);
                    }
                    Park park = parkList.get(position);
                    System.out.println("onClick Park ===============>"+park);
                    LatLng parkLat = new LatLng(park.getLat(),park.getLon());
                    mMapStatus = new MapStatus.Builder()
                            .target(parkLat)
                            .zoom(18)
                            .build();
                    mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(mMapStatus));

                    for(Marker marker:markers)
                    if(marker.getPosition().latitude==parkLat.latitude&&marker.getPosition().longitude==parkLat.longitude){
                        markerClick=marker;
                        marker.setScale((float) 1.5);
                        Button button = new Button(getApplicationContext());
                        button.setBackgroundResource(R.drawable.popup);
                        button.setText(park.getName());

//构造InfoWindow
//point 描述的位置点
//-100 InfoWindow相对于point在y轴的偏移量
                        InfoWindow mInfoWindow = new InfoWindow(button, parkLat, -200);

//使InfoWindow生效
                        mBaiduMap.showInfoWindow(mInfoWindow);
                        System.out.println(marker.getScaleX()+"++++++++++"+marker.getPosition());
                    }



                }
            });
//更新界面

        }

    };


    private void sendRequestWithOkHttp(final double lat, final double lon) {
        new Thread(new Runnable() {
            @Override

            public void run() {

                try {
                    OkHttpClient client = new OkHttpClient();//创建一个OkHttp实例
                    System.out.println(lat+","+lon);
                    Request request = new Request.Builder().get().url("https://api.ohaiyo.vip/parkinglot/?bd_longitude_max="+(lon+0.00001)+"&bd_longitude_min="+(lon-0.00001)+"&bd_latitude_max="+(lat+0.00001)+"&bd_latitude_min="+(lat-0.00001)).build();//创建Request对象发起请求,记得替换成你自己的key
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
            System.out.println(jsonData);

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
                        budle.putString("位置", name);
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

    private boolean isPermissionRequested;
    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= 23 && !isPermissionRequested) {
            isPermissionRequested = true;
            ArrayList<String> permissionsList = new ArrayList<>();
            String[] permissions = {
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.INTERNET,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_WIFI_STATE,
            };

            for (String perm : permissions) {
                if (PackageManager.PERMISSION_GRANTED != checkSelfPermission(perm)) {
                    permissionsList.add(perm);
                    // 进入到这里代表没有权限.
                }
            }

            if (!permissionsList.isEmpty()) {
                String[] strings = new String[permissionsList.size()];
                requestPermissions(permissionsList.toArray(strings), 0);
            }
        }
    }





}
