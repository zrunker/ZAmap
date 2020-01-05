package cc.ibooker.amaplib;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;

import com.amap.api.maps.AMap;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.AMapNaviViewListener;
import com.amap.api.navi.AMapNaviViewOptions;
import com.amap.api.navi.enums.NaviType;
import com.amap.api.navi.model.AMapCalcRouteResult;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapModelCross;
import com.amap.api.navi.model.AMapNaviCameraInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviRouteNotifyData;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.AMapServiceAreaInfo;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.navi.view.RouteOverLay;
import com.autonavi.tbt.TrafficFacilityInfo;

import java.util.ArrayList;

/**
 * 自定义导航
 *
 * @author 邹峰立
 */
public class ZAMapNaviView extends AMapNaviView
        implements AMapNaviViewListener,
        AMapNaviListener {
    private Context context;
    private AMapNavi aMapNavi;// 导航规划计算类
    private ArrayList<NaviLatLng> mWayPointList;// 途经点坐标
    private ArrayList<NaviLatLng> sList;// 起始点集
    private ArrayList<NaviLatLng> eList;// 终点集
    // 当前的导航类型
    private int currentNaviType = NaviType.GPS;

    public ZAMapNaviView(Context context) {
        super(context);
        init(context);
    }

    public ZAMapNaviView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context);
    }

    public ZAMapNaviView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init(context);
    }

    public ZAMapNaviView(Context context, AMapNaviViewOptions aMapNaviViewOptions) {
        super(context, aMapNaviViewOptions);
        init(context);
    }

    // 初始化
    private void init(Context context) {
        this.context = context;
        this.setAMapNaviViewListener(this);
        this.aMapNavi = AMapNavi.getInstance(context.getApplicationContext());
        this.aMapNavi.addAMapNaviListener(this);

//        AMapNaviViewOptions options = getViewOptions();
        // 关闭高德提供的导航图层

//        options.setLayoutVisible(false);

        // 关闭高德提供的导航路线绘制
//        options.setAutoDrawRoute(false);
//        setViewOptions(options);
    }

    @Override
    public AMap getMap() {
        return super.getMap();
    }

    /**
     * 设置途径点集合
     *
     * @param wayPointList 待设置值
     */
    public ZAMapNaviView setWayPointList(ArrayList<NaviLatLng> wayPointList) {
        this.mWayPointList = wayPointList;
        return this;
    }

    /**
     * 设置起始点集合
     *
     * @param sList 待设置值
     */
    public ZAMapNaviView setsList(ArrayList<NaviLatLng> sList) {
        this.sList = sList;
        return this;
    }

    /**
     * 添加起始点
     *
     * @param latLng 起始点
     */
    public ZAMapNaviView addStartPoint(NaviLatLng latLng) {
        if (latLng != null) {
            if (sList == null)
                sList = new ArrayList<>();
            sList.add(latLng);
        }
        return this;
    }

    /**
     * 设置终点集合
     *
     * @param eList 待设置值
     */
    public ZAMapNaviView seteList(ArrayList<NaviLatLng> eList) {
        this.eList = eList;
        return this;
    }

    /**
     * 添加终止点
     *
     * @param latLng 终止点
     */
    public ZAMapNaviView addEndPoint(NaviLatLng latLng) {
        if (latLng != null) {
            if (eList == null)
                eList = new ArrayList<>();
            eList.add(latLng);
        }
        return this;
    }

    /**
     * 设置当前的导航类型
     *
     * @param currentNaviType 待设置值
     */
    public ZAMapNaviView setCurrentNaviType(int currentNaviType) {
        this.currentNaviType = currentNaviType;
        return this;
    }

    /**
     * 开始导航
     */
    public ZAMapNaviView startNavi() {
        aMapNavi.startNavi(currentNaviType);
        return this;
    }

    // 导航
    @Override
    public void onNaviSetting() {

    }

    // 取消导航监听
    @Override
    public void onNaviCancel() {

    }

    // 退出导航监听
    @Override
    public boolean onNaviBackClick() {
        return false;
    }

    // 导航
    @Override
    public void onNaviMapMode(int i) {

    }

    // 导航
    @Override
    public void onNaviTurnClick() {

    }

    // 导航
    @Override
    public void onNextRoadClick() {

    }

    // 导航
    @Override
    public void onScanViewButtonClick() {

    }

    // 导航
    @Override
    public void onLockMap(boolean b) {

    }

    // 导航
    @Override
    public void onNaviViewLoaded() {

    }

    // 导航
    @Override
    public void onMapTypeChanged(int i) {

    }

    // 导航
    @Override
    public void onNaviViewShowMode(int i) {

    }

    // 路线规划对象初始化失败
    @Override
    public void onInitNaviFailure() {

    }

    // 导航初始化成功
    @Override
    public void onInitNaviSuccess() {
        /**
         * 方法:
         *   int strategy=mAMapNavi.strategyConvert(congestion, avoidhightspeed, cost, hightspeed, multipleroute);
         * 参数:
         * @congestion 躲避拥堵
         * @avoidhightspeed 不走高速
         * @cost 避免收费
         * @hightspeed 高速优先
         * @multipleroute 多路径
         *
         * 说明:
         *      以上参数都是boolean类型，其中multipleroute参数表示是否多条路线，如果为true则此策略会算出多条路线。
         * 注意:
         *      不走高速与高速优先不能同时为true
         *      高速优先与避免收费不能同时为true
         */
        int strategy = 0;
        try {
            strategy = aMapNavi.strategyConvert(true, false, false, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        aMapNavi.calculateDriveRoute(sList, eList, mWayPointList, strategy);
    }

    // 开始导航
    @Override
    public void onStartNavi(int i) {

    }

    @Override
    public void onTrafficStatusUpdate() {

    }

    // 位置变化监听
    @Override
    public void onLocationChange(AMapNaviLocation aMapNaviLocation) {

    }

    @Override
    public void onGetNavigationText(int i, String s) {

    }

    @Override
    public void onGetNavigationText(String s) {

    }

    @Override
    public void onEndEmulatorNavi() {

    }

    @Override
    public void onArriveDestination() {

    }

    @Override
    public void onCalculateRouteFailure(int i) {

    }

    @Override
    public void onReCalculateRouteForYaw() {

    }

    @Override
    public void onReCalculateRouteForTrafficJam() {

    }

    @Override
    public void onArrivedWayPoint(int i) {

    }

    @Override
    public void onGpsOpenStatus(boolean b) {

    }

    @Override
    public void onNaviInfoUpdate(NaviInfo naviInfo) {

    }

    @Override
    public void onNaviInfoUpdated(AMapNaviInfo aMapNaviInfo) {

    }

    @Override
    public void updateCameraInfo(AMapNaviCameraInfo[] aMapNaviCameraInfos) {

    }

    @Override
    public void updateIntervalCameraInfo(AMapNaviCameraInfo aMapNaviCameraInfo, AMapNaviCameraInfo aMapNaviCameraInfo1, int i) {

    }

    @Override
    public void onServiceAreaUpdate(AMapServiceAreaInfo[] aMapServiceAreaInfos) {

    }

    @Override
    public void showCross(AMapNaviCross aMapNaviCross) {

    }

    @Override
    public void hideCross() {

    }

    @Override
    public void showModeCross(AMapModelCross aMapModelCross) {

    }

    @Override
    public void hideModeCross() {

    }

    @Override
    public void showLaneInfo(AMapLaneInfo[] aMapLaneInfos, byte[] bytes, byte[] bytes1) {

    }

    @Override
    public void showLaneInfo(AMapLaneInfo aMapLaneInfo) {

    }

    @Override
    public void hideLaneInfo() {

    }

    // 导航路线规划成功 - 多策略
    @Override
    public void onCalculateRouteSuccess(int[] ints) {
        // 显示路径或开启导航
        if (ints != null && ints.length > 0) {
            // 对于多路径需要告诉 AMapNavi 选择的是哪条路，然后才能进行导航
            aMapNavi.selectRouteId(ints[0]);
//
//            //        如果根据获取的导航路线来自定义绘制
//            RouteOverLay routeOverlay = new RouteOverLay(getMap(), aMapNavi.getNaviPath(), context);
//            routeOverlay.setStartPointBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.r1));
//            routeOverlay.setEndPointBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.b1));
//            routeOverlay.setWayPointBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.b2));
//            routeOverlay.setTrafficLine(false);
//            try {
//                routeOverlay.setWidth(30);
//            } catch (Throwable e) {
//                //宽度须>0
//                e.printStackTrace();
//            }
//            int[] color = new int[10];
//            color[0] = Color.BLACK;
//            color[1] = Color.RED;
//            color[2] = Color.BLUE;
//            color[3] = Color.YELLOW;
//            color[4] = Color.GRAY;
//            routeOverlay.addToMap(color, aMapNavi.getNaviPath().getWayPointIndex());
//        routeOverlay.addToMap();

//            aMapNavi.startNavi(AMapNavi.GPSNaviMode);

            startNavi();
        }
    }

    @Override
    public void notifyParallelRoad(int i) {

    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo[] aMapNaviTrafficFacilityInfos) {

    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo aMapNaviTrafficFacilityInfo) {

    }

    @Override
    public void OnUpdateTrafficFacility(TrafficFacilityInfo trafficFacilityInfo) {

    }

    @Override
    public void updateAimlessModeStatistics(AimLessModeStat aimLessModeStat) {

    }

    @Override
    public void updateAimlessModeCongestionInfo(AimLessModeCongestionInfo aimLessModeCongestionInfo) {

    }

    @Override
    public void onPlayRing(int i) {

    }

    // 导航路线规划成功 - 多策略
    @Override
    public void onCalculateRouteSuccess(AMapCalcRouteResult aMapCalcRouteResult) {
        Log.d("ZAMapNaviView:", aMapCalcRouteResult.getRouteid().length + "");
    }

    // 路线规划失败
    @Override
    public void onCalculateRouteFailure(AMapCalcRouteResult aMapCalcRouteResult) {

    }

    @Override
    public void onNaviRouteNotify(AMapNaviRouteNotifyData aMapNaviRouteNotifyData) {

    }
}
