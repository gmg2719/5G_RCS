package com.android.messaging.ui.conversation;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.messaging.R;
import com.android.messaging.ui.MerchantInfo;
import com.android.messaging.util.LogUtil;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;

import java.util.ArrayList;
import java.util.List;

//import static com.baidu.navisdk.adapter.PackageUtil.getSdcardDir;

public class BaiduMapTestActivity extends Activity implements View.OnClickListener, OnGetPoiSearchResultListener {
    private static String APP_NAME = "com.android.messaging";
    private String SNAME = "起点";
    private String DNAME= "终点";
//    private String mDesAddrName = "西湖文化广场";
    private String TEST_DEST_NAME = "江苏大剧院";
    private double TEST_LATITUDE = 32.018426;
    private double TEST_LONGITUDE = 118.720012;


    private MapView mMapView = null;
    private BaiduMap mBaiduMap = null;
    private LocationClient mLocationClient;
    private Button bt1;
    private Button bt2;
    private Button bt3;
    private Button navi_bt;
    private Button dache_bt;
    private boolean isFirstLoc = true;
    private LatLng latLng;
    private boolean mIsMapOpened;
    private double mLatitude;
    private double mLongitude;
    private MerchantInfo mMerchantInfo;
    private PoiSearch mPoiSearch;
    private List mDataList = null;

    private boolean mIsBaiduMapInstalled = OpenLocalMapUtil.isBaiduMapInstalled();
    private boolean mIsGaodeMapInstalled = OpenLocalMapUtil.isGdMapInstalled();

    /*private String mSDCardPath = null;
    private static final String APP_FOLDER_NAME = "Messaging";
    private static final String[] authBaseArr = { Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION };
    private static final String[] authComArr = { Manifest.permission.READ_PHONE_STATE };
    public static List<Activity> activityList = new LinkedList<Activity>();
    private static final int authBaseRequestCode = 1;
    private static final int authComRequestCode = 2;
    private boolean hasInitSuccess = false;
    private boolean hasRequestComAuth = false;
    private double mCurrentLat,mCurrentLon;
    public static final String ROUTE_PLAN_NODE = "routePlanNode";*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.baidumaptest);
        //judgePermission();
        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.map_view);
        bt1 = (Button) findViewById(R.id.bt);
        bt1.setOnClickListener(this);
        bt2 = (Button) findViewById(R.id.button);
        bt2.setOnClickListener(this);
        bt3 = (Button) findViewById(R.id.buttons);
        bt3.setOnClickListener(this);
        navi_bt = (Button)findViewById(R.id.navi_button);
        navi_bt.setOnClickListener(this);
        dache_bt = (Button)findViewById(R.id.dache_button);
        dache_bt.setOnClickListener(this);

        Intent i = getIntent();
        mLatitude = i.getDoubleExtra("latitude", 0.0);
        mLongitude = i.getDoubleExtra("longitude", 0.0);
        initMap();

        mDataList = new ArrayList();

    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mLocationClient.stop();
        mLocationClient.unRegisterLocationListener(myListener);
        mMapView.onDestroy();
    }
    private Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:

                    break;

                default:
                    break;
            }
        };
    };

    /*private boolean initDirs() {
        mSDCardPath = SDCardUtil.getSDCradPath();//getSdcardDir();
        if (mSDCardPath == null) {
            return false;
        }
        File f = new File(mSDCardPath, APP_FOLDER_NAME);
        if (!f.exists()) {
            try {
                f.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }
    String authinfo = null;*/

    /**
     * 内部TTS播报状态回传handler
     */
    /*private Handler ttsHandler = new Handler() {
        public void handleMessage(Message msg) {
            int type = msg.what;
            switch (type) {
                case BaiduNaviManager.TTSPlayMsgType.PLAY_START_MSG: {
                    // showToastMsg("Handler : TTS play start");
                    break;
                }
                case BaiduNaviManager.TTSPlayMsgType.PLAY_END_MSG: {
                    // showToastMsg("Handler : TTS play end");
                    break;
                }
                default:
                    break;
            }
        }
    };*/
    /**
     * 内部TTS播报状态回调接口
     */
    /*private BaiduNaviManager.TTSPlayStateListener ttsPlayStateListener = new BaiduNaviManager.TTSPlayStateListener() {

        @Override
        public void playEnd() {
            // showToastMsg("TTSPlayStateListener : TTS play end");
        }

        @Override
        public void playStart() {
            // showToastMsg("TTSPlayStateListener : TTS play start");
        }
    };



    private void initNavi() {

        BNOuterTTSPlayerCallback ttsCallback = null;

        // 申请权限
        if (android.os.Build.VERSION.SDK_INT >= 23) {

            if (!hasBasePhoneAuth()) {

                this.requestPermissions(authBaseArr, authBaseRequestCode);
                return;

            }
        }

        BaiduNaviManager.getInstance().init(this, mSDCardPath, APP_FOLDER_NAME, new BaiduNaviManager.NaviInitListener() {
            @Override
            public void onAuthResult(int status, String msg) {
                if (0 == status) {
                    authinfo = "key校验成功!";
                } else {
                    authinfo = "key校验失败, " + msg;
                }
//                TodaytaskdetailActivity.this.runOnUiThread(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        Toast.makeText(TodaytaskdetailActivity.this, authinfo, Toast.LENGTH_LONG).show();
//                    }
//                });
            }

            public void initSuccess() {
                //Toast.makeText(TodaytaskdetailActivity.this,"百度导航引擎初始化成功", Toast.LENGTH_SHORT).show();
                hasInitSuccess = true;
                initSetting();
            }

            public void initStart() {
                //Toast.makeText(TodaytaskdetailActivity.this, "百度导航引擎初始化开始", Toast.LENGTH_SHORT).show();
            }

            public void initFailed() {
                //Toast.makeText(TodaytaskdetailActivity.this, "百度导航引擎初始化失败", Toast.LENGTH_SHORT).show();
            }

        }, null, ttsHandler, ttsPlayStateListener);

    }

    private BNRoutePlanNode.CoordinateType mCoordinateType = null;

    //    private void routeplanToNavi(BNRoutePlanNode.CoordinateType coType) {
    private void routeplanToNavi() {
//        mCoordinateType = coType;
        if (!hasInitSuccess) {
            //Toast.makeText(TodaytaskdetailActivity.this, "还未初始化!", Toast.LENGTH_SHORT).show();
        }
        // 权限申请
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            // 保证导航功能完备
            if (!hasCompletePhoneAuth()) {
                if (!hasRequestComAuth) {
                    hasRequestComAuth = true;
                    this.requestPermissions(authComArr, authComRequestCode);
                    return;
                } else {
                    //Toast.makeText(TodaytaskdetailActivity.this, "没有完备的权限!", Toast.LENGTH_SHORT).show();
                }
            }

        }
        BNRoutePlanNode sNode = null;
        BNRoutePlanNode eNode = null;
        sNode = new BNRoutePlanNode(mCurrentLon, mCurrentLat,null, null, CoordType.BD09LL);//起点
        eNode = new BNRoutePlanNode(/*detailBean.getLon()*//*, detailBean.getLat(),null, null, CoordType.WGS84);*///终点
        /*if (sNode != null && eNode != null) {
            List<BNRoutePlanNode> list = new ArrayList<BNRoutePlanNode>();
            list.add(sNode);//添加起点
            list.add(eNode);//添加终点
            BaiduNaviManager.getInstance().launchNavigator(this, list, 1, true, new DemoRoutePlanListener(sNode));
        }
    }
    public class DemoRoutePlanListener implements BaiduNaviManager.RoutePlanListener {

        private BNRoutePlanNode mBNRoutePlanNode = null;

        public DemoRoutePlanListener(BNRoutePlanNode node) {
            mBNRoutePlanNode = node;
        }
        @Override
        public void onJumpToNavigator() {
            /*
             * 设置途径点以及resetEndNode会回调该接口
             */
            /*for (Activity ac : activityList) {
                if (ac.getClass().getName().endsWith("BNDemoGuideActivity")) {
                    return;
                }
            }
            Intent intent = new Intent(BaiduMapTestActivity.this, BNDemoGuideActivity.class);//跳转到导航界面
            Bundle bundle = new Bundle();
            bundle.putSerializable(ROUTE_PLAN_NODE, (BNRoutePlanNode) mBNRoutePlanNode);
            intent.putExtras(bundle);
            startActivity(intent);
        }

        @Override
        public void onRoutePlanFailed() {
            // TODO Auto-generated method stub
            Toast.makeText(BaiduMapTestActivity.this, "算路失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void initSetting() {
        BNaviSettingManager.setDayNightMode(BNaviSettingManager.DayNightMode.DAY_NIGHT_MODE_DAY);
        BNaviSettingManager
                .setShowTotalRoadConditionBar(BNaviSettingManager.PreViewRoadCondition.ROAD_CONDITION_BAR_SHOW_ON);
        BNaviSettingManager.setVoiceMode(BNaviSettingManager.VoiceMode.Veteran);
        // BNaviSettingManager.setPowerSaveMode(BNaviSettingManager.PowerSaveMode.DISABLE_MODE);
        BNaviSettingManager.setRealRoadCondition(BNaviSettingManager.RealRoadCondition.NAVI_ITS_ON);
        Bundle bundle = new Bundle();
        // 必须设置APPID，否则会静音
        bundle.putString(BNCommonSettingParam.TTS_APP_ID, "9480303");//将TTS申请的语音设置好
        BNaviSettingManager.setNaviSdkParam(bundle);
    }


    private boolean hasBasePhoneAuth() {
        // TODO Auto-generated method stub

        PackageManager pm = this.getPackageManager();
        for (String auth : authBaseArr) {
            if (pm.checkPermission(auth, this.getPackageName()) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
    private boolean hasCompletePhoneAuth() {
        // TODO Auto-generated method stub

        PackageManager pm = this.getPackageManager();
        for (String auth : authComArr) {
            if (pm.checkPermission(auth, this.getPackageName()) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }*/


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt:
                //把定位点再次显现出来
                MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLng(latLng);
                mBaiduMap.animateMapStatus(mapStatusUpdate);
                break;
            case R.id.button:
                //卫星地图
                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
//                searchNeayBy("旅馆", 30.28582, 120.172416);
                break;
            case R.id.buttons:
                //普通地图
                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                break;
            case R.id.navi_button:
                /*if(mIsBaiduMapInstalled && mIsGaodeMapInstalled){
                    try {
                        new Dialog().setTitle("请选择").
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else*/ if(mIsBaiduMapInstalled){
//                    openBaiduMap(30.28582/*mLatitude*/,120.172416/*mLongitude*/, DNAME);
                    openBaiduMap(TEST_LATITUDE/*mLatitude*/,TEST_LONGITUDE, TEST_DEST_NAME);
                }else if(mIsGaodeMapInstalled)
                {
                    openGaoDeMap(TEST_LATITUDE/*mLatitude*/,TEST_LONGITUDE/*mLongitude*/, TEST_DEST_NAME);
                }else{
                    openWebMap(/*30.28582*/TEST_LATITUDE,/*120.172416*/TEST_LONGITUDE, DNAME, TEST_DEST_NAME);
                }
                //openBaiduMap(30.28582,120.172416, DNAME);
                //openGaoDeMap(30.28582,120.172416, DNAME);
                //openWebMap(30.28582,120.172416, DNAME, mDesAddrName);
                break;
            case R.id.dache_button:
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://common.diditaxi.com.cn/general/webEntry?wx=true&bizid=257&channel=70365"));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    private void initMap() {
        //获取地图控件引用
        mBaiduMap = mMapView.getMap();
        //普通地图
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mBaiduMap.setMyLocationEnabled(true);

        //默认显示普通地图
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        //开启交通图
        //mBaiduMap.setTrafficEnabled(true);
        //开启热力图
        //mBaiduMap.setBaiduHeatMapEnabled(true);
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL,
                true, BitmapDescriptorFactory.fromResource(R.drawable.icon_geo), 0xAAFFFF88, 0xAA00FF00));
        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        //配置定位SDK参数
        initLocation();
//        setPointLocation(mLatitude, mLongitude);
        mLocationClient.registerLocationListener(myListener);    //注册监听函数
        //开启定位
        mLocationClient.start();
        //图片点击事件，回到定位点
        mLocationClient.requestLocation();
        //BDLocation bdl = new BDLocation();
        //bdl.setAddrStr(DNAME);
    }

    //配置定位SDK参数
    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span = 1000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation
        // .getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);
        option.setOpenGps(true); // 打开gps

        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        mLocationClient.setLocOption(option);
    }


    //实现BDLocationListener接口,BDLocationListener为结果监听接口，异步获取定位结果
    private  /*BDLocationListener*/BDAbstractLocationListener myListener = new /*implements BDLocationListener*/BDAbstractLocationListener() {
        @Override
            public void onReceiveLocation (BDLocation location){
            LogUtil.d("Junwang", "Addr is " + location.getCountry() + location.getProvince()
                    + location.getCity() + location.getDistrict() + location.getStreet());
            LogUtil.d("Junwang", "getAddrStr = " + location.getAddrStr()+ "Latitude ="+ location.getLatitude()
                + "Longitude()=" + location.getLongitude());
//            mLatitude = location.getLatitude();
//            mLongitude = location.getLongitude();
//            setPointLocation(mLatitude, mLongitude);
            //setPointLocation(30.283356,120.130922);
            //setPointLocation(30.279457,120.119997);
            //setPointLocation(31.301773,112.429773);
            //setPointLocation(30.283073,120.143718);
//            setPointLocation(30.28582,120.172416);
            setPointLocation(mLatitude, mLongitude);


            //latLng = new LatLng(location.getLatitude(), location.getLongitude());
            // 构造定位数据
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            // 设置定位数据
            mBaiduMap.setMyLocationData(locData);

            //String addr = location.getAddrStr();    //获取详细地址信息
            //String country = location.getCountry();    //获取国家
            //String province = location.getProvince();    //获取省份
            //String city = location.getCity();    //获取城市
            //String district = location.getDistrict();    //获取区县
            //String street = location.getStreet();    //获取街道信息
            // 当不需要定位图层时关闭定位图层
            //mBaiduMap.setMyLocationEnabled(false);
            if (isFirstLoc){
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                //builder.zoom(18.0f);
                //mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));

                if (location.getLocType() == BDLocation.TypeGpsLocation) {
                    // GPS定位结果
                    Toast.makeText(BaiduMapTestActivity.this, location.getAddrStr(), Toast.LENGTH_SHORT).show();
                } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
                    // 网络定位结果
                    Toast.makeText(BaiduMapTestActivity.this, location.getAddrStr(), Toast.LENGTH_SHORT).show();

                } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {
                    // 离线定位结果
                    Toast.makeText(BaiduMapTestActivity.this, location.getAddrStr(), Toast.LENGTH_SHORT).show();

                } else if (location.getLocType() == BDLocation.TypeServerError) {
                    Toast.makeText(BaiduMapTestActivity.this, "服务器错误，请检查", Toast.LENGTH_SHORT).show();
                } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                    Toast.makeText(BaiduMapTestActivity.this, "网络错误，请检查", Toast.LENGTH_SHORT).show();
                } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                    Toast.makeText(BaiduMapTestActivity.this, "手机模式错误，请检查是否飞行", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(BaiduMapTestActivity.this, "location type = "+location.getLocType(), Toast.LENGTH_LONG);
                }
            }
            // 显示个人位置图标
            MyLocationData.Builder builder = new MyLocationData.Builder();
            builder.latitude(location.getLatitude());
            builder.longitude(location.getLongitude());
            MyLocationData data = builder.build();
            mBaiduMap.setMyLocationData(data);

            //add by junwang for poi test
//            Poi poi = location.getPoiList().get(0);
//            String poiName = poi.getName();    //获取POI名称
//            LogUtil.i("Junwang", "poiName="+poiName);
//            location.getAddrStr();

//            String poiTags = poi.getTag();    //获取POI类型
//            String poiAddr = poi.getAddr();    //获取POI地址 //获取周边POI信息
//
//
//            PoiRegion poiRegion= location.getPoiRegion();
//            String poiDerectionDesc = poiRegion.getDerectionDesc();    //获取PoiRegion位置关系
//            String poiRegionName = poiRegion.getName();    //获取PoiRegion名称
//            String poiTags = poiRegion.getTags();    //获取PoiRegion类型

        }
    };

    private void setPointLocation(double latitude, double longitude){
        //latLng = new LatLng(latitude, longitude);
        // 构造定位数据
        MyLocationData locData = new MyLocationData.Builder()
                // 此处设置开发者获取到的方向信息，顺时针0-360
                .direction(100).latitude(latitude)
                .longitude(longitude).build();
        // 设置定位数据
        mBaiduMap.setMyLocationData(locData);
        if (isFirstLoc) {
            isFirstLoc = false;
            LatLng ll = new LatLng(latitude,
                    longitude);

            MapStatus.Builder builder = new MapStatus.Builder();
            builder.target(ll).zoom(18.0f);
            //builder.zoom(18.0f);
            //mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        }
    }

//    //6.0之后要动态获取权限，重要！！！
//    protected void judgePermission() {
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            // 检查该权限是否已经获取
//            // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
//
//            // sd卡权限
//            String[] SdCardPermission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
//            if (ContextCompat.checkSelfPermission(this, SdCardPermission[0]) != PackageManager.PERMISSION_GRANTED) {
//                // 如果没有授予该权限，就去提示用户请求
//                ActivityCompat.requestPermissions(this, SdCardPermission, 100);
//            }
//
//            //手机状态权限
//            String[] readPhoneStatePermission = {Manifest.permission.READ_PHONE_STATE};
//            if (ContextCompat.checkSelfPermission(this, readPhoneStatePermission[0]) != PackageManager.PERMISSION_GRANTED) {
//                // 如果没有授予该权限，就去提示用户请求
//                ActivityCompat.requestPermissions(this, readPhoneStatePermission, 200);
//            }
//
//            //定位权限
//            String[] locationPermission = {Manifest.permission.ACCESS_FINE_LOCATION};
//            if (ContextCompat.checkSelfPermission(this, locationPermission[0]) != PackageManager.PERMISSION_GRANTED) {
//                // 如果没有授予该权限，就去提示用户请求
//                ActivityCompat.requestPermissions(this, locationPermission, 300);
//            }
//
//            String[] ACCESS_COARSE_LOCATION = {Manifest.permission.ACCESS_COARSE_LOCATION};
//            if (ContextCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION[0]) != PackageManager.PERMISSION_GRANTED) {
//                // 如果没有授予该权限，就去提示用户请求
//                ActivityCompat.requestPermissions(this, ACCESS_COARSE_LOCATION, 400);
//            }
//
//
//            String[] READ_EXTERNAL_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE};
//            if (ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE[0]) != PackageManager.PERMISSION_GRANTED) {
//                // 如果没有授予该权限，就去提示用户请求
//                ActivityCompat.requestPermissions(this, READ_EXTERNAL_STORAGE, 500);
//            }
//
//            String[] WRITE_EXTERNAL_STORAGE = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
//            if (ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE[0]) != PackageManager.PERMISSION_GRANTED) {
//                // 如果没有授予该权限，就去提示用户请求
//                ActivityCompat.requestPermissions(this, WRITE_EXTERNAL_STORAGE, 600);
//            }
//
//            String[] CAMERAPERMISSION = {Manifest.permission.CAMERA};
//            if(ContextCompat.checkSelfPermission(this, CAMERAPERMISSION[0]) != PackageManager.PERMISSION_GRANTED){
//                ActivityCompat.requestPermissions(this, CAMERAPERMISSION, 700);
//            }
//
//            String[] SMSRECEIVERPERMISSION = {Manifest.permission.RECEIVE_SMS};
//            if(ContextCompat.checkSelfPermission(this, SMSRECEIVERPERMISSION[0]) != PackageManager.PERMISSION_GRANTED){
//                ActivityCompat.requestPermissions(this, SMSRECEIVERPERMISSION, 800);
//            }
//
//        }else{
//            //doSdCardResult();
//        }
//        //LocationClient.reStart();
//    }

    /**
     * 打开百度地图
     */
    /**
     *
     * @param slat 纬度
     * @param slon 经度
     * @param content 内容
     */
    private void openBaiduMap(double slat, double slon, String content) {
        if (OpenLocalMapUtil.isBaiduMapInstalled()) {
            try {
                String uri = OpenLocalMapUtil.getBaiduMapUri(String.valueOf(slat), String.valueOf(slon), content);
                Intent intent = new Intent();
                intent.setData(Uri.parse(uri));
                startActivity(intent); //启动调用
                mIsMapOpened = true;
            } catch (Exception e) {
                mIsMapOpened = false;
                e.printStackTrace();
            }
        } else {
            mIsMapOpened = false;
        }
    }

    /**
     * 打开高德地图
     */
    /**
     *
     * @param dlat 纬度
     * @param dlon 纬度
     * @param content 终点
     */
    private void openGaoDeMap(double dlat, double dlon, String content) {
        if (OpenLocalMapUtil.isGdMapInstalled()) {
            try {
                //百度地图定位坐标转换成高德地图可识别坐标
                double[] loca = new double[2];
                //loca = OpenLocalMapUtil.gcj02_To_Bd09(dlat, dlon);
                loca = OpenLocalMapUtil.bd09_To_Gcj02(dlat, dlon);
                String uri = OpenLocalMapUtil.getGdMapUri(APP_NAME,
                        String.valueOf(loca[0]), String.valueOf(loca[1]), content);
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setPackage("com.autonavi.minimap");
                intent.setData(Uri.parse(uri));
                startActivity(intent); //启动调用
                mIsMapOpened = true;

            } catch (Exception e) {
                mIsMapOpened = false;
                e.printStackTrace();
            }
        } else {
            mIsMapOpened = false;
        }
    }

    /**
     * 打开浏览器进行百度地图导航
     */
    /**
     *
     * @param dlat 纬度
     * @param dlon 经度
     * @param dname 终点
     * @param content 地点内容
     */
    private void openWebMap(double dlat, double dlon, String dname, String content) {
        Uri mapUri = Uri.parse(OpenLocalMapUtil.getWebBaiduMapUri(
                String.valueOf(dlat), String.valueOf(dlon),
                dname, content, APP_NAME));
        Intent loction = new Intent(Intent.ACTION_VIEW, mapUri);
        startActivity(loction);
    }

    private void searchNeayBy(String keyword, double latitude, double longitude){
        // POI初始化搜索模块，注册搜索事件监听
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(this);
        PoiNearbySearchOption poiNearbySearchOption = new PoiNearbySearchOption();

        poiNearbySearchOption.keyword(keyword);
        poiNearbySearchOption.location(new LatLng(latitude, longitude));
        poiNearbySearchOption.radius(100);  // 检索半径，单位是米
        poiNearbySearchOption.pageCapacity(20);  // 默认每页10条
        mPoiSearch.searchNearby(poiNearbySearchOption);  // 发起附近检索请求
    }

    @Override
    public void onGetPoiResult(PoiResult result) {
        // 获取POI检索结果
        if (result == null || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {// 没有找到检索结果
            Toast.makeText(BaiduMapTestActivity.this, "未找到结果",Toast.LENGTH_LONG).show();
            return;
        }

        if (result.error == SearchResult.ERRORNO.NO_ERROR) {// 检索结果正常返回
//			mBaiduMap.clear();
            if(result != null){
                if(result.getAllPoi()!= null && result.getAllPoi().size()>0){
                    mDataList.addAll(result.getAllPoi());
//					adapter.notifyDataSetChanged();
                    Message msg = new Message();
                    msg.what = 0;
                    handler.sendMessage(msg);
                }
            }
        }
    }

    @Override
    public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {
        LogUtil.d("Junwang", "onGetPoiDetailResult exec");
    }

    @Override
    public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

    }

    @Override
    public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

    }
}



