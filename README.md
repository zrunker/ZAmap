# 地图组件

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
public void onLocation(View view) {
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
public void onDriving(View view) {
    if (zAmapView == null)
        zAmapView = new ZMapView(this);
    zAmapView
//      .setRouteSearchListener(this)
        .setRouteType(ZMapView.ROUTE_TYPE_DRIVE)
        .searchRouteResult(mStartPoint, mEndPoint);
}

// 公交规划
public void onBus(View view) {
    if (zAmapView == null)
        zAmapView = new ZMapView(this);
    zAmapView
//          .setRouteSearchListener(this)
            .setRouteType(ZMapView.ROUTE_TYPE_BUS)
            .searchRouteResult(mStartPoint, mEndPoint);
}

// 步行规划
public void onWalk(View view) {
    if (zAmapView == null)
        zAmapView = new ZMapView(this);
    zAmapView
//          .setRouteSearchListener(this)
            .setRouteType(ZMapView.ROUTE_TYPE_WALK)
            .searchRouteResult(mStartPoint, mEndPoint);
}

// 骑行规划
public void onRide(View view) {
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
public void onPoiSearch(View view) {
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
public void onDefaultPoint(View view) {
    zAmapView.setCenterPoint(new LatLonPoint(31.238068, 121.501654), 10);
}

// 添加Marker
public void onAddMarker(View view) {
    zAmapView.addLatLngMarker(mStartPoint, R.drawable.start);
}

// 添加动态Marker
public void onAddAnimMarker(View view) {
    zAmapView.setOpenMarkerAnim(true)
            .addLatLngMarker(mEndPoint, R.drawable.end);
}

// 添加其实Marker
public void onMarker(View view) {
    zAmapView.setFromandtoMarker(mStartPoint, mEndPoint, R.drawable.start, R.drawable.end);
}

// 缩放按钮不可见
public void onScaleVisible(View view) {
    zAmapView.zoomControlViewVisible(false);
}

// 缩放比例
public void onScale(View view) {
    zAmapView.setZoom(11);
}

// 限制地图范围
public void setMapLimits(View view) {
    zAmapView.setMapLimits(mStartPoint, mEndPoint);
}

// 其他方法...
public void onElse(View view) {
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
