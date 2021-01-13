package com.example.myapplication5;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
import com.baidu.mapapi.overlayutil.OverlayManager;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;


import java.util.ArrayList;
import java.util.List;

public class DrivingRoutSearch1 extends AppCompatActivity implements OnGetRoutePlanResultListener, BaiduMap.OnMapClickListener {
    private Button mBtnPre = null; // 上一个节点
    private Button mBtnNext = null; // 下一个节点
    private RouteLine mRouteLine = null;
    private OverlayManager mRouteOverlay = null;
    // 地图View
    private MapView mMapView = null;
    private BaiduMap mBaidumap = null;

    // 搜索模块，也可去掉地图模块独立使用
    private RoutePlanSearch mSearch = null;
    private String PoiName;

 //   private BDLocation mLocation = null;
private boolean isFirstLoc;
    // 驾车路线结果
    private DrivingRouteResult mDrivingRouteResult = null;
    private boolean mUseDefaultIcon = false;
    private boolean hasShowDialog = false;
    // 选择路线策略view
    private Spinner mSpinner;
    private double m1,m2;
    //public MyLocationListenner myListener1 = new MyLocationListenner();
    // 驾车路线规划参数
    private DrivingRoutePlanOption mDrivingRoutePlanOption;
    private NodeUtils mNodeUtils;
    private EditText mEditStartCity;
    private EditText mEditEndCity;
    private AutoCompleteTextView mStrartNodeView;
    private  TextView end,start,juli;
    private AutoCompleteTextView mEndNodeView;
    private CheckBox mTrafficPolicyCB;
    private LocationClient locationClient;
    private double jingdustart,weidustart;
    private  double n1,n2;
    private Button drive;
    private DrivingRouteResult resultBeg=null;
    private LatLng oldLatLon=null;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driving_route1);

        Bundle bundle = getIntent().getExtras();
        n1=  bundle.getDouble("经度");
        n2 = bundle.getDouble("纬度");
        PoiName = bundle.getString("位置");
        System.out.println("PoiName====================>"+PoiName);
        drive = (Button)findViewById(R.id.drive);
        drive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchRoute();
            }
        });

        mEditStartCity = (EditText) findViewById(R.id.st_city);
       // mStrartNodeView = (AutoCompleteTextView) findViewById(R.id.st_node);
        start = (TextView)findViewById(R.id.st_node) ;
        mEditEndCity = (EditText) findViewById(R.id.ed_city);
       // mEndNodeView = (AutoCompleteTextView) findViewById(R.id.ed_node);
        end = (TextView) findViewById(R.id.ed_node);
        mTrafficPolicyCB = (CheckBox) findViewById(R.id.traffic);
        juli = (TextView) findViewById(R.id.juli);
        end.setText(PoiName);
        // 初始化UI相关
        mBtnPre = (Button) findViewById(R.id.pre);
        mBtnNext = (Button) findViewById(R.id.next);
        mBtnPre.setVisibility(View.INVISIBLE);
        mBtnNext.setVisibility(View.INVISIBLE);
        mMapView = (MapView) findViewById(R.id.map);
        // 初始化地图
        mBaidumap = mMapView.getMap();
        mNodeUtils = new NodeUtils(this, mBaidumap);
        mBaidumap.setOnMapClickListener(this);
        // 初始化搜索模块，注册事件监听
        mSearch = RoutePlanSearch.newInstance();
        mSearch.setOnGetRoutePlanResultListener(this);
        // 初始化驾车路线相关策略view
        mSpinner = (Spinner) findViewById(R.id.spinner);
        // 开启定位图层
        mBaidumap.setMyLocationEnabled(true);
        //声明定位SDK核心类
        locationClient = new LocationClient(this);
        //注册监听
        locationClient.registerLocationListener(mListener);
        //定位配置信息
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);//定位请求时间间隔
        locationClient.setLocOption(option);
        //开启定位
        locationClient.start();
       //存储终点信息
        LatLng latLng = new LatLng(n1,n2);
        //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.icon_marka);
         //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option1 = new MarkerOptions()
                .position(latLng)
                .icon(bitmap);
        mBaidumap.setMapStatus(MapStatusUpdateFactory.newLatLng(latLng));
        //在地图上添加Marker，并显示
        mBaidumap.addOverlay(option1);
        List<String> list = new ArrayList<>();
        list.add("时间优先");
        list.add("躲避拥堵");
        list.add("最短距离");
        list.add("较少费用");


        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item_vict, list);
        adapter.setDropDownViewResource(R.layout.spinner_item_vict);
        mSpinner.setAdapter(adapter);
        initViewListener();
    }
    private BDAbstractLocationListener mListener = new BDAbstractLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // TODO Auto-generated method stub
            double c1 = location.getLatitude();
            double e1 = location.getLongitude();
            LatLng newLatLon = new LatLng(c1,e1);
            m1 = c1;
            m2 = e1;
            start.setText("当前位置");


        }

    };







    public void initViewListener() {
        // 创建路线规划Option   // 设置参数前创建
        mDrivingRoutePlanOption = new DrivingRoutePlanOption();
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    case 0:
                        // 时间优先策略，  默认时间优先
                        mDrivingRoutePlanOption.policy(DrivingRoutePlanOption.DrivingPolicy.ECAR_TIME_FIRST);
                        break;
                    case 1:
                        // 躲避拥堵策略
                        mDrivingRoutePlanOption.policy(DrivingRoutePlanOption.DrivingPolicy.ECAR_AVOID_JAM);
                        break;
                    case 2:
                        // 最短距离策略
                        mDrivingRoutePlanOption.policy(DrivingRoutePlanOption.DrivingPolicy.ECAR_DIS_FIRST);
                        break;
                    case 3:
                        // 费用较少策略
                        mDrivingRoutePlanOption.policy(DrivingRoutePlanOption.DrivingPolicy.ECAR_FEE_FIRST);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
//    public void searchButtonProcess(View v){
public void searchButtonProcess(View v){
        //重置浏览节点的路线数据
        mRouteLine = null;
        mBtnNext.setVisibility(View.INVISIBLE);
        mBtnPre.setVisibility(View.INVISIBLE);
        //清除之前覆盖物
        mBaidumap.clear();
        //获取Bundle信息
        LatLng latLng = new LatLng(n1,n2);
        LatLng latLng1 = new LatLng(m1,m2);

        //设置节点信息
       // PlanNode startNode = PlanNode.withCityNameAndPlaceName(mEditStartCity.getText().toString().trim(), mStrartNodeView.getText().toString().trim());
        PlanNode startNode = PlanNode.withLocation(latLng1);
        //设置终点参数
       // PlanNode endNode =   PlanNode.withCityNameAndPlaceName(mEditEndCity.getText().toString().trim(), mEndNodeView.getText().toString().trim());
        PlanNode endNode = PlanNode.withLocation(latLng);
        //是否开启路况
        if(mTrafficPolicyCB.isChecked()){
            //开启路况
            mDrivingRoutePlanOption.trafficPolicy(DrivingRoutePlanOption.DrivingTrafficPolicy.ROUTE_PATH_AND_TRAFFIC);
        }else{
            //关闭路况
            mDrivingRoutePlanOption.trafficPolicy(DrivingRoutePlanOption.DrivingTrafficPolicy.ROUTE_PATH);
        }
        //开始路线规划
        mSearch.drivingSearch(mDrivingRoutePlanOption.from(startNode).to(endNode));


    }
public void searchRoute(){
    //重置浏览节点的路线数据
    mRouteLine = null;
    mBtnNext.setVisibility(View.INVISIBLE);
    mBtnPre.setVisibility(View.INVISIBLE);
    //清除之前覆盖物
    mBaidumap.clear();
    //获取Bundle信息
    LatLng latLng = new LatLng(n1,n2);
    LatLng latLng1 = new LatLng(m1,m2);

    //设置节点信息
    // PlanNode startNode = PlanNode.withCityNameAndPlaceName(mEditStartCity.getText().toString().trim(), mStrartNodeView.getText().toString().trim());
    PlanNode startNode = PlanNode.withLocation(latLng1);
    //设置终点参数
    // PlanNode endNode =   PlanNode.withCityNameAndPlaceName(mEditEndCity.getText().toString().trim(), mEndNodeView.getText().toString().trim());
    PlanNode endNode = PlanNode.withLocation(latLng);
    //是否开启路况
    if(mTrafficPolicyCB.isChecked()){
        //开启路况
        mDrivingRoutePlanOption.trafficPolicy(DrivingRoutePlanOption.DrivingTrafficPolicy.ROUTE_PATH_AND_TRAFFIC);
    }else{
        //关闭路况
        mDrivingRoutePlanOption.trafficPolicy(DrivingRoutePlanOption.DrivingTrafficPolicy.ROUTE_PATH);
    }
    //开始路线规划
    mSearch.drivingSearch(mDrivingRoutePlanOption.from(startNode).to(endNode));

}


    @Override
    public void onMapClick(LatLng latLng) {
        //隐藏当前infowindow
        mBaidumap.hideInfoWindow();

    }

    @Override
    public void onMapPoiClick(MapPoi mapPoi) {

    }


    @Override
    public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {

    }

    @Override
    public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {

    }

    @Override
    public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {

    }

    @Override
    public void onGetDrivingRouteResult(DrivingRouteResult result) {
        if(resultBeg==null||result.getRouteLines()!=resultBeg.getRouteLines()){
            System.out.println("result================>"+result);
            System.out.println("resultbeg=============>"+resultBeg);
            resultBeg=result;
        if (result != null && result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            // result.getSuggestAddrInfo()
            Toast.makeText(DrivingRoutSearch1.this, "起终点或途经点地址有岐义,通过 result.getSuggestAddrInfo()接口获取建议查询信息", Toast.LENGTH_SHORT).show();
            return;
        }
        if (result == null || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
            Toast.makeText(DrivingRoutSearch1.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
            if(result == null)
                Toast.makeText(DrivingRoutSearch1.this, "result == null", Toast.LENGTH_SHORT).show();
            if(result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND)
                Toast.makeText(DrivingRoutSearch1.this, "result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND", Toast.LENGTH_SHORT).show();
            return;


        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            int duration = result.getRouteLines().get(0).getDistance();
            String dua = String.valueOf(duration);
            juli.setText(dua+"米");
            Toast.makeText(this, "距离是:" + duration + "米", Toast.LENGTH_SHORT).show();
            mBtnPre.setVisibility(View.VISIBLE);
            mBtnNext.setVisibility(View.VISIBLE);
            if (result.getRouteLines().size() > 1) {
                mDrivingRouteResult = result;
                if (!hasShowDialog) {
                    // 多条路线Dialog
                    SelectRouteDialog selectRouteDialog = new SelectRouteDialog(DrivingRoutSearch1.this,
                            result.getRouteLines(), RouteLineAdapter.Type.DRIVING_ROUTE);
                    selectRouteDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            hasShowDialog = false;
                        }
                    });
                    selectRouteDialog.setOnItemInDlgClickLinster(new SelectRouteDialog.OnItemInDlgClickListener() {
                        public void onItemClick(int position) {
                            // 获取选中的路线
                            mRouteLine = mDrivingRouteResult.getRouteLines().get(position);
                            DrivingRouteOverlay overlay = new MyDrivingRouteOverlay(mBaidumap);
                            mBaidumap.setOnMarkerClickListener(overlay);
                            mRouteOverlay = overlay;
                            overlay.setData(mDrivingRouteResult.getRouteLines().get(position));
                            overlay.addToMap();
                            overlay.zoomToSpan();
                        }

                    });
                    selectRouteDialog.show();
                    hasShowDialog = true;
                }
            } else if (result.getRouteLines().size() == 1) {
                mRouteLine = result.getRouteLines().get(0);
                DrivingRouteOverlay overlay = new MyDrivingRouteOverlay(mBaidumap);
                mRouteOverlay = overlay;
                mBaidumap.setOnMarkerClickListener(overlay);
                overlay.setData(result.getRouteLines().get(0));
                overlay.addToMap();
                overlay.zoomToSpan();
                mBtnPre.setVisibility(View.VISIBLE);
                mBtnNext.setVisibility(View.VISIBLE);
            } else {
                Log.d("route result", "结果数<0");
                return;
            }
        }

    }}

    @Override
    public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

    }

    @Override
    public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

    }

    // 定制RouteOverly
    private class MyDrivingRouteOverlay extends DrivingRouteOverlay {

        private MyDrivingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            if (mUseDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            if (mUseDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
            }
            return null;
        }
    }
        /**
         * 节点浏览
         */
        public void nodeClick(View v) {
            if (null != mRouteLine) {
                mNodeUtils.browseRoutNode(v, mRouteLine);
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
        super.onDestroy();
        // 释放检索对象
        if (mSearch != null) {
            mSearch.destroy();
        }
        mBaidumap.clear();
        mMapView.onDestroy();
    }



}




