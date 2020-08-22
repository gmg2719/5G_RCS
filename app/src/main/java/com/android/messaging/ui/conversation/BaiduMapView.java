package com.android.messaging.ui.conversation;

import android.widget.Toast;

import com.android.messaging.R;
import com.android.messaging.util.LogUtil;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

import static com.android.messaging.BugleApplication.getContext;

public class BaiduMapView {
    private MapView mMapView;
    private BaiduMap mBaiduMap = null;
    private LocationClient mLocationClient;
    private double mLatitude;
    private double mLongitude;
    private boolean isFirstLoc = true;

    public BaiduMapView(MapView mMapView) {
        this.mMapView = mMapView;
    }

    public void initMap() {
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
        mLocationClient = new LocationClient(getContext());     //声明LocationClient类
        //配置定位SDK参数
        initLocation();
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
    private  /*BDLocationListener*/ BDAbstractLocationListener myListener = new /*implements BDLocationListener*/BDAbstractLocationListener() {
        @Override
        public void onReceiveLocation (BDLocation location){
            LogUtil.d("Junwang", "Addr is " + location.getCountry() + location.getProvince()
                    + location.getCity() + location.getDistrict() + location.getStreet());
            LogUtil.d("Junwang", "getAddrStr = " + location.getAddrStr()+ "Latitude ="+ location.getLatitude()
                    + "Longitude()=" + location.getLongitude());
            mLatitude = location.getLatitude();
            mLongitude = location.getLongitude();
//            setPointLocation(mLatitude, mLongitude);
            //setPointLocation(30.283356,120.130922);
            //setPointLocation(30.279457,120.119997);
            //setPointLocation(31.301773,112.429773);
            //setPointLocation(30.283073,120.143718);
//            setPointLocation(30.28582,120.172416);

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
//                    Toast.makeText(getContext(), location.getAddrStr(), Toast.LENGTH_SHORT).show();
                } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
                    // 网络定位结果
//                    Toast.makeText(getContext(), location.getAddrStr(), Toast.LENGTH_SHORT).show();

                } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {
                    // 离线定位结果
//                    Toast.makeText(getContext(), location.getAddrStr(), Toast.LENGTH_SHORT).show();

                } else if (location.getLocType() == BDLocation.TypeServerError) {
                    Toast.makeText(getContext(), "服务器错误，请检查", Toast.LENGTH_SHORT).show();
                } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                    Toast.makeText(getContext(), "网络错误，请检查", Toast.LENGTH_SHORT).show();
                } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                    Toast.makeText(getContext(), "手机模式错误，请检查是否飞行", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getContext(), "location type = "+location.getLocType(), Toast.LENGTH_LONG);
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
}
