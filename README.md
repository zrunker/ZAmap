# 地图组件
* 功能一：显示地图
* 功能二：路线规划
* 功能三：POI搜索
* 功能四：距离搜索-计算
* 功能五：逆向地址查询
* 功能六：定位
* 功能七：导航

### 引入方式

一、在build.gradle中引入：
```
api project(':amaplib')
```

二、修改清单文件中高德地图Key：
```
<meta-data
    android:name="com.amap.api.v2.apikey"
    android:value="52e14a8a4538e61eb1883de66371e532" />
```

### API说明：

```
private LatLonPoint mStartPoint = new LatLonPoint(39.955545, 116.20151);//起点，116.335891,39.942295
private LatLonPoint mEndPoint = new LatLonPoint(39.954545, 116.30155);//终点，116.481288,39.995576
```

#### 一、显示地图：
在布局文件中引入ZMapView：
```
<cc.ibooker.amaplib.ZMapView
    android:id="@+id/amapView"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content" />
```
修改相应Activity：
```
public class MainActivity extends AppCompatActivity implements ZRouteSearchListener {
    private ZMapView zAmapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        zAmapView = findViewById(R.id.amapView);
        // 显示地图
        zAmapView.onCreate(savedInstanceState);
    }

    @Override
    protected void onStop() {
        super.onStop();
        zAmapView.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        zAmapView.destroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        zAmapView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        zAmapView.pause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // 在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        zAmapView.onSaveInstanceState(outState);
    }

}

```
#### 定位
```
// 定位
public void onLocation() {
    if (zAmapView == null)
        zAmapView = new ZMapView(this);
    // 判断是否打开GPS
    if (!zAmapView.checkGpsIsOpen())
        zAmapView.openGPSSetting();

    // 开启定位
    zAmapView.setLocationListener(new ZLocationListener() {
                @Override
                public void onLocationStart() {
                    Toast.makeText(MainActivity.this, "定位: onLocationStart", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onLocationComplete() {
                    Toast.makeText(MainActivity.this, "定位: onLocationComplete", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onLocationFail(String message) {
                    Toast.makeText(MainActivity.this, "定位: onLocationFail " + message, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onLocationError(Throwable e) {
                    Toast.makeText(MainActivity.this, "定位: onLocationError " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onLocationNext(LocationData locationData) {
                    Toast.makeText(MainActivity.this, "定位: onLocationNext " + locationData.toString(), Toast.LENGTH_SHORT).show();
                }
            })
            .startLocation();
}
```
#### 路线规划
```
// 驾车规划
public void onDriving() {
    if (zAmapView == null)
        zAmapView = new ZMapView(this);
    zAmapView
//      .setRouteSearchListener(this)
        .setRouteType(ZMapView.ROUTE_TYPE_DRIVE)
        .searchRouteResult(mStartPoint, mEndPoint);
}

// 公交规划
public void onBus() {
    if (zAmapView == null)
        zAmapView = new ZMapView(this);
    zAmapView
//          .setRouteSearchListener(this)
            .setRouteType(ZMapView.ROUTE_TYPE_BUS)
            .searchRouteResult(mStartPoint, mEndPoint);
}

// 步行规划
public void onWalk() {
    if (zAmapView == null)
        zAmapView = new ZMapView(this);
    zAmapView
//          .setRouteSearchListener(this)
            .setRouteType(ZMapView.ROUTE_TYPE_WALK)
            .searchRouteResult(mStartPoint, mEndPoint);
}

// 骑行规划
public void onRide() {
    if (zAmapView == null)
        zAmapView = new ZMapView(this);
    zAmapView
            .setRouteSearchListener(new ZRouteSearchListener() {
                @Override
                public void onRouteSearchStart() {
                        
                }

                @Override
                public void onRouteSearchComplete() {

                }

                @Override
                public void onRouteSearchFail(String message) {

                }

                @Override
                public void onRouteSearchError(Throwable e) {

                }

                @Override
                public void onDriveNext(DriveRouteResult result, ArrayList<Float> distanceList) {

                }

                @Override
                public void onWalkNext(WalkRouteResult result, ArrayList<Float> distanceList) {

                }

                @Override
                public void onRideNext(RideRouteResult result, ArrayList<Float> distanceList) {

                }

                @Override
                public void onBusNext(BusRouteResult result, ArrayList<Float> distanceList) {

                }
            })
            .setRouteType(ZMapView.ROUTE_TYPE_RIDE)
            .searchRouteResult(mStartPoint, mEndPoint);
}
```
#### POI搜索
```
// POI搜索
public void onPoiSearch() {
    if (zAmapView == null)
        zAmapView = new ZMapView(this);
    zAmapView
//                .setPoiSearchListener(new ZPoiSearchListener() {
//                    @Override
//                    public void onPoiSearchStart() {
//
//                    }
//
//                    @Override
//                    public void onPoiSearchComplete() {
//
//                    }
//
//                    @Override
//                    public void onPoiSearchFail(String message) {
//
//                    }
//
//                    @Override
//                    public void onPoiSearchError(Throwable e) {
//
//                    }
//
//                    @Override
//                    public void onPoiSearchNext(List<PoiItem> poiItems, List<SuggestionCity> suggestionCities) {
//
//                    }
//                })
                .poiSearchByPage("海淀区");
}
```
#### 其他方法
```
// 设置中心位置
public void onDefaultPoint() {
    zAmapView.setCenterPoint(new LatLonPoint(31.238068, 121.501654), 10);
}

// 添加Marker
public void onAddMarker() {
    zAmapView.addLatLngMarker(mStartPoint, R.drawable.start);
}

// 添加动态Marker
public void onAddAnimMarker() {
    zAmapView.setOpenMarkerAnim(true)
            .addLatLngMarker(mEndPoint, R.drawable.end);
}

// 添加其实Marker
public void onMarker() {
    zAmapView.setFromandtoMarker(mStartPoint, mEndPoint, R.drawable.start, R.drawable.end);
}

// 缩放按钮不可见
public void onScaleVisible() {
    zAmapView.zoomControlViewVisible(false);
}

// 缩放比例
public void onScale() {
    zAmapView.setZoom(11);
}

// 限制地图范围
public void setMapLimits() {
    zAmapView.setMapLimits(mStartPoint, mEndPoint);
}

// 其他方法...
public void onElse() {
    zAmapView.setCurrentCity(mStartPoint, 11)
            .setShowMapText(true)
            .setShowBuildings(true);
    zAmapView.getAMap();
    zAmapView.getLocationClient();
    zAmapView.getLocationOption();
    // 获取高德地图UI控制类
    zAmapView.getUiSettings();
    /**
     * 将经纬度转换成坐标点
     *
     * @param pointx 经度
     * @param pointy 纬度
     */
     zAmapView.convertToLatLonPoint(39.4156132, 112.4663);
}

...

```

### 计算距离
```
/**
 * 计算两点距离
 */
public void calculateRouteDistance() {
    if (zAmapView == null)
        zAmapView = new ZMapView(this);
    zAmapView.setDistanceSearchListener(new ZDistanceSearchListener() {
        @Override
        public void onDistanceSearchStart() {

        }

        @Override
        public void onDistanceSearchComplete() {

        }

        @Override
        public void onDistanceSearchFail(String message) {

        }

        @Override
        public void onDistanceSearchError(Throwable e) {

        }

        @Override
        public void onDistanceSearched(DistanceResult distanceResult, ArrayList<Float> distanceList) {

        }
    }).calculateRouteDistance(mStartPoint, mEndPoint);
}

```

### 逆向地址查询
```
// 逆向地址查询
public void getAddressByLatLonPoint() {
    if (zAmapView == null)
        zAmapView = new ZMapView(this);
    zAmapView.getAddressByLatLonPoint(mStartPoint, new ZGeocodeSearchListener() {
            @Override
            public void onGeocodeSearchStart() {
                
            }

            @Override
            public void onGeocodeSearchComplete() {

            }

            @Override
            public void onGeocodeSearchFail(String message) {

            }

            @Override
            public void onGeocodeSearchError(Throwable e) {

            }

            @Override
            public void onRegeocodeSearched(RegeocodeResult regeocodeResult, String address) {

            }

            @Override
            public void onGeocodeSearched(GeocodeResult geocodeResult, List<GeocodeAddress> geocodeAddressList, ArrayList<String> addressList) {

            }
        });
}
```

### 导航
高德地图区别于地图控件，导航使用ZAMapNaviView控件。
#### 使用方法
一、首先将ZAMapNaviView添加到布局文件中
```
<?xml version="1.0" encoding="utf-8"?>
<cc.ibooker.amaplib.ZAMapNaviView xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/zAMapNaviView"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
```

二、在Activity等相关界面上显示导航，如给两个点mStartPoint，mEndPoint进行导航，默认是驾车导航
```
/**
 * 测试导航Activity
 *
 * @author 邹峰立
 */
public class NavActivity extends AppCompatActivity {
    private ZAMapNaviView zaMapNaviView;

    private NaviLatLng mStartPoint = new NaviLatLng(39.955545, 116.20151);//起点，116.335891,39.942295
    private NaviLatLng mEndPoint = new NaviLatLng(39.954545, 116.30155);//终点，116.481288,39.995576

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);

        zaMapNaviView = findViewById(R.id.zAMapNaviView);
        zaMapNaviView.onCreate(savedInstanceState);

        zaMapNaviView.addStartPoint(mStartPoint)
                .addEndPoint(mEndPoint);
    }

    @Override
    protected void onResume() {
        super.onResume();
        zaMapNaviView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        zaMapNaviView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        zaMapNaviView.onZDestroy();
    }
}
```
三、其他方法介绍
```
    /**
     * 设置路线规划策略
     *
     * @param strategy 0~20 21种策略
     *                 https://lbs.amap.com/api/android-navi-sdk/guide/route-plan/drive-route-plan
     */
    public ZAMapNaviView setStrategy(int strategy);
    
    /**
     * 设置路线规划类型，如果设置货车模式，要设置车辆信息
     *
     * @param calculateRouteType 0-驾车，1-货车，2-步行，3-骑行
     */
    public ZAMapNaviView setCalculateRouteType(int calculateRouteType);
    
    /**
     * 设置车辆信息
     *
     * @param aMapCarInfo 待设置内容
     */
    public ZAMapNaviView setAMapCarInfo(AMapCarInfo aMapCarInfo);
    
    /**
     * 开启或关闭高德提供的导航图层
     *
     * @param isVisible 开启或关闭
     */
    public ZAMapNaviView setLayoutVisible(boolean isVisible);
    
    
    /**
     * 是否高德提供的导航路线绘制
     *
     * @param bool 自动绘制
     */
    public ZAMapNaviView setAutoDrawRoute(boolean bool);
    
    /**
     * 设置途径点集合
     *
     * @param wayPointList 待设置值
     */
    public ZAMapNaviView setWayPointList(ArrayList<NaviLatLng> wayPointList);
    
    /**
     * 添加途径点
     *
     * @param naviLatLng 途径点
     */
    public ZAMapNaviView addWayPoint(NaviLatLng naviLatLng);
    
    /**
     * 清空途经点
     */
    public ZAMapNaviView clearWayPointList();
    
    /**
     * 设置起始点集合
     *
     * @param sList 待设置值
     */
    public ZAMapNaviView setsList(ArrayList<NaviLatLng> sList);
    
    /**
     * 添加起始点
     *
     * @param latLng 起始点
     */
    public ZAMapNaviView addStartPoint(NaviLatLng latLng);
    
    /**
     * 清空起始点
     */
    public ZAMapNaviView clearSList();
    
    /**
     * 设置终点集合
     *
     * @param eList 待设置值
     */
    public ZAMapNaviView seteList(ArrayList<NaviLatLng> eList);
    
    /**
     * 添加终止点
     *
     * @param latLng 终止点
     */
    public ZAMapNaviView addEndPoint(NaviLatLng latLng);
    
    /**
     * 清空终止点
     */
    public ZAMapNaviView clearEList();
    
    /**
     * 设置当前的导航类型
     *
     * @param currentNaviType 待设置值
     */
    public ZAMapNaviView setCurrentNaviType(int currentNaviType);
    
    ...
    
```
几个常用的监听类
```
// 导航图加载监听
ZAMapNaviViewListener zaMapNaviViewListener;
// 简化导航图加载监听
ZSimpleAMapNaviViewListener zSimpleAMapNaviViewListener;

// 导航监听
ZAMapNaviListener zaMapNaviListener;
// 简化导航监听
ZSimpleAMapNaviListener zSimpleAMapNaviListener;
```
