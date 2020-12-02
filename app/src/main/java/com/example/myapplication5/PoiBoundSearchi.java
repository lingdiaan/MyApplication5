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


public class PoiBoundSearchi extends AppCompatActivity {
    // 搜索模块，也可去掉地图模块独立使用
    private static StringBuilder sb = new StringBuilder();
    private GeoCoder mSearch = null;
    //百度地图控件
    private MapView mapView;
    //百度地图对象
    private BaiduMap  mBaiduMap;
    private PoiSearch mPoiSearch;
    private String spacenum;

    private MyLocationListenner myListener1 = new MyLocationListenner();
    public String PoiName;
    private AlertDialog.Builder builder=null;
    private int i = 0;
    private Marker mMarkerA,mMarkerB;
    private LatLng startlatLng;
    private BitmapDescriptor mBitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_markb);
    private  LatLng latLng;
    private LatLng endlatLng;
    private String price;

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
        mapView = (MapView) findViewById(R.id.dituView);
        //获取百度地图对象
        mBaiduMap = mapView.getMap();
//        ListView lvLocNear = (ListView) findViewById(R.id.lv_location_nearby);
        ArrayList<PoiInfo> nearList = new ArrayList<PoiInfo>();
        //adapter = new LocNearAddressAdapter(context, nearList, isSelected);
        //lvLocNear.setAdapter(adapter);

        Bundle bundle = getIntent().getExtras();
        double n1=  bundle.getDouble("经度");
        double n2 = bundle.getDouble("纬度");
        latLng = new LatLng(n2,n1);

        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(poiListener);
        mPoiSearch.searchNearby(new PoiNearbySearchOption()
                .location(latLng)
                .radius(10000)
                .keyword("停车场")
                .pageNum(9));

        OverlayOptions option1 = new MarkerOptions()
                .position(latLng)
                .icon(mBitmap).perspective(true);
//在地图上添加Marker，并显示
        mBaiduMap.addOverlay(option1);

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




   private class MyPoiOverlay extends PoiOverlay {

        public MyPoiOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }
        @Override
        public boolean onPoiClick(int index) {
            super.onPoiClick(index);
            PoiInfo poi = getPoiResult().getAllPoi().get(index);
            PoiName = poi.name;
            //计算距离
            builder.setIcon(R.drawable.popup);//设置图标
            builder.setTitle("停车场信息:"+PoiName).setMessage(sb.toString());//设置对话框的标题
            builder.setPositiveButton("导航", new DialogInterface.OnClickListener(){  //这个是设置确定按钮
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    final Intent intent = new Intent(PoiBoundSearchi.this,DrivingRoutSearch1.class);
                    Bundle budle = new Bundle();
                    budle.putDouble("经度",endlatLng.latitude);
                    budle.putDouble("纬度", endlatLng.longitude);
                    budle.putString("位置",PoiName);
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
                    if(spacenum.equals(0))
                        Toast.makeText(PoiBoundSearchi.this,"无剩余车位",Toast.LENGTH_LONG);
                    else if(price.equals(null)){
                        Toast.makeText(PoiBoundSearchi.this,"该停车场暂不提供预定",Toast.LENGTH_LONG);

                    }else{
                        Bundle bundle = new Bundle();
                        bundle.putString(price,"price");
                        bundle.putString(PoiName,"name");
                    Intent intent = new Intent(PoiBoundSearchi.this,timeChooseActivity.class);
                    startActivity(intent,bundle);}


                }
            });
            builder.create().show();
            // AlertDialog b=builder.create();
            // b.show();  //必须show一下才能看到对话框，跟Toast一样的道理
            //initListener();
            return true;
        }
    }

    OnGetPoiSearchResultListener poiListener = new OnGetPoiSearchResultListener() {


        public void onGetPoiResult(PoiResult result) {


            //获取POI检索结果
            if (result == null
                    || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {// 没有找到检索结果
                Toast.makeText(PoiBoundSearchi.this, "未找到结果",
                        Toast.LENGTH_LONG).show();
                return;
            } else if (result.error == SearchResult.ERRORNO.NO_ERROR) {
                if (result != null) {

//                    List<PoiInfo> mData = result.getAllPoi();
//                    for (PoiInfo p : mData) {
//                        LatLng point = new LatLng(p.location.latitude, p.location.longitude);
////构建Marker图标
//                        BitmapDescriptor bitmap = BitmapDescriptorFactory
//                                .fromResource(R.mipmap.ic_launcher);
////构建MarkerOption，用于在地图上添加Marker
//                        OverlayOptions option = new MarkerOptions()
//                                .position(point)
//                                .icon(bitmap);
//                        mBaiduMap.addOverlay(option);

//                    mBaiduMap.clear();
                    //创建PoiOverlay
                    PoiOverlay overlay = new MyPoiOverlay( mBaiduMap);
                    //设置overlay可以处理标注点击事件
                    mBaiduMap.setOnMarkerClickListener(overlay);
                    //设置PoiOverlay数据
                    overlay.setData(result);
                    //添加PoiOverlay到地图中\
                    overlay.addToMap();
                    overlay.zoomToSpan( );
                    return;
                }

                mPoiSearch.destroy();

            }
        }

        public void onGetPoiDetailResult(PoiDetailResult result) {
            if (result.error != SearchResult.ERRORNO.NO_ERROR) {
                Toast.makeText(PoiBoundSearchi.this, "抱歉，未找到结果",
                        Toast.LENGTH_SHORT).show();
            } else {// 正常返回结果的时候，此处可以获得很多相关信息

            }
        }
        @Override
        public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {


        }

        @Override
        public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {


        }
    };

   //设置弹窗信息
   public void initListener(){
        builder = null;
        builder=new AlertDialog.Builder(this);
       mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
    @Override
    public boolean onMarkerClick(final Marker marker) {
        Toast.makeText(PoiBoundSearchi.this,"当前所在位置",Toast.LENGTH_SHORT);
        endlatLng = marker.getPosition();
        //String weizhi = marker.getTitle();

        double dis = DistanceUtil. getDistance(startlatLng, endlatLng);
        double dis2 = DistanceUtil. getDistance(latLng, endlatLng);
        sendRequestWithOkHttp(endlatLng.latitude,endlatLng.longitude);
        sb = new StringBuilder();
        sb.append( "收费标准："+i+++"元/小时").append("\n");
        if(dis<1000){
        sb.append( "距离："+(int)dis+"米").append("\n");}
        else{sb.append( "距离："+(int)dis/1000+"Km").append("\n");}
        sb.append("剩余车位数："+spacenum+"个").append("\n");
        sb.append("目标位置距停车场"+(int)dis2+"米").append("\n");
         return true;
    }
});
           }
    private class MyLocationListenner implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mapView == null) {
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
                builder.target(startlatLng).zoom(14.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
        }


    }

    private void sendRequestWithOkHttp(final double lat, final double lon) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();//创建一个OkHttp实例
                    Request request = new Request.Builder().get().url("https://api.ohaiyo.vip/parkinglot/?latitude_min="+lat+"&latitude_max="+lat+"longitude"+lon+"longitude"+lon).build();//创建Request对象发起请求,记得替换成你自己的key



                    //Request request = new Request.Builder().url("https://api.seniverse.com/v3/weather/now.json?key=SrvH71t8JeTOXNLJP&location=beijing&language=zh-Hans&unit=c").build();//创建Request对象发起请求,记得替换成你自己的key
                    Response response = client.newCall(request).execute();//创建call对象并调用execute获取返回的数据
                    String responseData = response.body().string();
                    parseJSONWithJSONObject(responseData);//解析SSON数据
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void parseJSONWithJSONObject(String jsonData) {//用JSONObect解析JSON数据
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray results = jsonObject.getJSONArray("results");//得到键为results的JSONArray
//            JSONObject obj = (JSONObject)results.opt(0);
//            spacenum = obj.getString("space_num");  //获得s剩余停车位数量
            for(int i = 0;i<results.length();i++){
                JSONObject obj = results.getJSONObject(i);
                double lat = obj.getDouble("latitude");
                double lon = obj.getDouble("longitude");
                String name = obj.getString("name");

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        mapView = null;
        super.onDestroy();
    }
}
