package com.example.myapplication5;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.PoiOverlay;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.utils.DistanceUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PoiBoundSearch extends AppCompatActivity { // 搜索模块，也可去掉地图模块独立使用
    private static StringBuilder sb = new StringBuilder();
    private GeoCoder mSearch = null;
    //百度地图控件
    private MapView mMapView;
    //百度地图对象
    private BaiduMap mBaiduMap;
    private PoiSearch mPoiSearch;
    private String spacenum;

    private MyLocationListenner myListener1 = new MyLocationListenner();
    public String PoiName;
    private AlertDialog.Builder builder=null;
    private int i = 0;
    private Marker mMarkerA,mMarkerB;
    private LatLng startlatLng;
    private BitmapDescriptor mBitmap = BitmapDescriptorFactory.fromResource(R.drawable.park_markp);
    private BitmapDescriptor mBitmapLoc = BitmapDescriptorFactory.fromResource(R.drawable.icon_marka);
    private  LatLng latLng;
    private LatLng endlatLng;
    private String price;


    private float localZoom;
    private  BaiduMap.OnMarkerClickListener onMarkerClickListener;
    private List<Marker> markers = new ArrayList<>();
    private float x;
    private float suofang;
    private MapStatus mMapStatus;
    private String id,name;



    /**
     * 定位SDK核心类
     */
    private LocationClient locationClient;
    /**
     * 定位监听
     */

    /**
     * 百度地图控件
     */

    /**
     * 百度地图对象
     */
    boolean isFirstLoc = true; // 是否首次定位

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //该方法注意要在setContentView之前实现
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.poi_activity);
        /**
         * 地图初始化
         */
        //获取百度地图控件
        mMapView = (MapView) findViewById(R.id.dituView);
        //获取百度地图对象
        mBaiduMap = mMapView.getMap();
//        ListView lvLocNear = (ListView) findViewById(R.id.lv_location_nearby);
        ArrayList<PoiInfo> nearList = new ArrayList<PoiInfo>();
        //adapter = new LocNearAddressAdapter(context, nearList, isSelected);
        //lvLocNear.setAdapter(adapter);

        Bundle bundle = getIntent().getExtras();
        double n1=  bundle.getDouble("经度");
        double n2 = bundle.getDouble("纬度");
        latLng = new LatLng(n2,n1);



        // 初始化搜索模块，注册事件监听
        //定位
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        //声明定位SDK核心类
        locationClient = new LocationClient(this);
        //注册监听
        locationClient.registerLocationListener(myListener1);
        //定位配置信息
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);//定位请求时间间隔
        locationClient.setLocOption(option);
        //开启定位
        locationClient.start();
        initListener();
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

                try {
                    mBaiduMap.clear();
                    mBaiduMap.removeMarkerClickListener(onMarkerClickListener);
                    OverlayOptions option1 = new MarkerOptions()
                            .position(latLng)
                            .icon(mBitmapLoc).perspective(true);
//在地图上添加Marker，并显示
                    mBaiduMap.addOverlay(option1);
                    System.out.println("执行了clearMap");
                    OkHttpClient client = new OkHttpClient();//创建一个OkHttp实例
                    Request request = new Request.Builder().get().url("https://api.ohaiyo.vip/parkinglot/?bd_latitude_max="+(lat+0.018)+"&bd_latitude_min="+(lat-0.018)+"&bd_longitude_max="+(lon+0.022)+"&bd_longitude_min="+(lon-0.022)+"&page_size=99999999").build();
                    Response response = client.newCall(request).execute();//创建call对象并调用execute获取返回的数据
                    String responseData = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseData);
                    JSONArray results = jsonObject.getJSONArray("results");//得到键为results的JSONArray
                    List<MyItem> items = new ArrayList<MyItem>();
                    int count=0;
                    double[] latNum = new double[results.length()];
                    double[] lonNum = new double[results.length()];
                    int[] counts = new int[results.length()];
                    for (int i = 0; i < results.length(); i++) {
                        JSONObject obj = results.getJSONObject(i);
                        latNum[i]  = obj.getDouble("bd_latitude");
                        lonNum[i]  = obj.getDouble("bd_longitude");
                        //                String name = obj.getString("name");
                        counts[i] = obj.getInt("space_num");
                    }
                    for(int yi=0;yi<counts.length-1;yi++)
                    {
                        for(int er=0;er<counts.length-1-i;er++)
                        {
                            if(counts[er]>counts[er+1])
                            {
                                int temp=counts[er];
                                counts[er]=counts[er+1];
                                counts[er+1]=temp;

                                double temp1=latNum[er];
                                latNum[er]=latNum[er+1];
                                latNum[er+1]=temp1;

                                double temp2=latNum[er];
                                latNum[er]=latNum[er+1];
                                latNum[er+1]=temp2;

                            }
                        }
                    }
                    if(x>1)
                        x=1;
                    else if(x<0.3)
                        x=(float) 0.3;
                    for(int i=0;i<counts.length;i++) {
                        System.out.println("counts=============================>"+counts[i]);
                    }

                    for(int i=results.length()-1;i>results.length()*(1-x);i--){
                        double latnow = latNum[i];
                        double lonnow = lonNum[i];
                        LatLng point = new LatLng(latnow, lonnow);

                        //构建MarkerOption，用于在地图上添加Marker

                        OverlayOptions option = new MarkerOptions()
                                .position(point)
                                .icon(mBitmap).clickable(true).perspective(true).zIndex(4);
                        items.add(new MyItem(point));
                        //在地图上添加Marker，并显示
                        Marker marker = (Marker) mBaiduMap.addOverlay(option);

                        markers.add(marker);

                    }


                    for(Marker marker : markers){
                        marker.setScale(suofang);

                    }



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


                        final Intent intent = new Intent(PoiBoundSearch.this, DrivingRoutSearch1.class);
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



                    }
                });
                builder.setNeutralButton("预定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (Integer.valueOf(spacenum) == 0) {
                            Toast.makeText(PoiBoundSearch.this, "无剩余车位", Toast.LENGTH_LONG).show();
                        }else{
                            Intent intent = new Intent(PoiBoundSearch.this,timeChooseActivity.class);
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




    public void initListener(){

        builder = null;
        builder=new AlertDialog.Builder(this);
        onMarkerClickListener = marker -> {
            endlatLng = marker.getPosition();
            System.out.println("position================>"+endlatLng);
            final Intent intent = new Intent(PoiBoundSearch.this,DrivingRoutSearch1.class);
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

    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null ||  mMapView == null) {
//                Toast.makeText(PoiBoundSearch.this,"未打开定位权限",Toast.LENGTH_SHORT).show();
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
                mMapStatus = new MapStatus.Builder().zoom(14).target(latLng).build();
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
                        System.out.println("zoom======>"+ptCenter);
                        System.out.println("zoom======>"+zoom);
                        if (zoom!=localZoom){
                            mBaiduMap.clear();
                            sendRequestWithOkHttpGetMark(latLng.latitude,latLng.longitude,zoom);
                            localZoom =zoom;

                        }

                    }
                });
            }
        }
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
    }
}
