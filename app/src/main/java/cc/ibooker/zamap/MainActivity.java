package cc.ibooker.zamap;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DistanceResult;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.WalkRouteResult;

import java.util.ArrayList;

import cc.ibooker.amaplib.ZMapView;
import cc.ibooker.amaplib.listeners.ZDistanceSearchListener;
import cc.ibooker.amaplib.listeners.ZRouteSearchListener;

public class MainActivity extends AppCompatActivity implements ZRouteSearchListener {
    private ZMapView zAmapView;

    private LatLonPoint mStartPoint = new LatLonPoint(39.955545, 116.20151);//起点，116.335891,39.942295
    private LatLonPoint mEndPoint = new LatLonPoint(39.954545, 116.30155);//终点，116.481288,39.995576

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        zAmapView = findViewById(R.id.amapView);
//        // 默认地址
//        zAmapView.setDefaultPoint(new LatLonPoint(31.238068, 121.501654), 10);
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
        zAmapView.getUiSettings();
        /**
         * 将经纬度转换成坐标点
         *
         * @param pointx 经度
         * @param pointy 纬度
         */
        zAmapView.convertToLatLonPoint(39.4156132, 112.4663);
    }

    // 定位
    public void onLocation(View view) {
        // 判断是否打开GPS
        if (!zAmapView.checkGpsIsOpen())
            zAmapView.openGPSSetting();

        // 开启定位
        zAmapView
//                .setLocationListener(new ZLocationListener() {
//                    @Override
//                    public void onLocationStart() {
//                        Toast.makeText(MainActivity.this, "定位: onLocationStart", Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onLocationComplete() {
//                        Toast.makeText(MainActivity.this, "定位: onLocationComplete", Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onLocationFail(String message) {
//                        Toast.makeText(MainActivity.this, "定位: onLocationFail " + message, Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onLocationError(Throwable e) {
//                        Toast.makeText(MainActivity.this, "定位: onLocationError " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onLocationNext(LocationData locationData) {
//                        Toast.makeText(MainActivity.this, "定位: onLocationNext " + locationData.toString(), Toast.LENGTH_SHORT).show();
//                    }
//                })
                .startLocation();
    }

    // 驾车规划
    public void onDriving(View view) {
        zAmapView
//                .setRouteSearchListener(this)
                .setRouteType(ZMapView.ROUTE_TYPE_DRIVE)
                .searchRouteResult(mStartPoint, mEndPoint);
    }

    // 公交规划
    public void onBus(View view) {
        zAmapView
//                .setRouteSearchListener(this)
                .setRouteType(ZMapView.ROUTE_TYPE_BUS)
                .searchRouteResult(mStartPoint, mEndPoint);
    }

    // 步行规划
    public void onWalk(View view) {
        zAmapView
//                .setRouteSearchListener(this)
                .setRouteType(ZMapView.ROUTE_TYPE_WALK)
                .searchRouteResult(mStartPoint, mEndPoint);
    }

    // 骑行规划
    public void onRide(View view) {
        zAmapView
//                .setRouteSearchListener(this)
                .setRouteType(ZMapView.ROUTE_TYPE_RIDE)
                .searchRouteResult(mStartPoint, mEndPoint);
    }

    // POI搜索
    public void onPoiSearch(View view) {
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

    // 路线规划监听
    @Override
    public void onRouteSearchStart() {
        Toast.makeText(MainActivity.this, "路线规划: onRouteSearchStart", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRouteSearchComplete() {
        Toast.makeText(MainActivity.this, "路线规划: onRouteSearchComplete", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRouteSearchFail(String message) {
        Toast.makeText(MainActivity.this, "路线规划: onRouteSearchFail", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRouteSearchError(Throwable e) {
        Toast.makeText(MainActivity.this, "路线规划: onRouteSearchError", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDriveNext(DriveRouteResult result, ArrayList<Float> distanceList) {
        Toast.makeText(MainActivity.this, "路线规划: onDriveNext", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onWalkNext(WalkRouteResult result, ArrayList<Float> distanceList) {
        Toast.makeText(MainActivity.this, "路线规划: onWalkNext", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRideNext(RideRouteResult result, ArrayList<Float> distanceList) {
        Toast.makeText(MainActivity.this, "路线规划: onRideNext", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBusNext(BusRouteResult result, ArrayList<Float> distanceList) {
        Toast.makeText(MainActivity.this, "路线规划: onBusNext", Toast.LENGTH_SHORT).show();
    }

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

}
