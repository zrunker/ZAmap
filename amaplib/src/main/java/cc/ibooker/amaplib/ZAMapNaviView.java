package cc.ibooker.amaplib;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.LocationManager;
import android.provider.Settings;
import android.util.AttributeSet;

import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.AMapNaviViewListener;
import com.amap.api.navi.AMapNaviViewOptions;
import com.amap.api.navi.enums.NaviType;
import com.amap.api.navi.enums.TravelStrategy;
import com.amap.api.navi.model.AMapCalcRouteResult;
import com.amap.api.navi.model.AMapCarInfo;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapModelCross;
import com.amap.api.navi.model.AMapNaviCameraInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviRouteNotifyData;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.AMapServiceAreaInfo;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.navi.model.NaviPoi;
import com.amap.api.navi.view.RouteOverLay;

import java.util.ArrayList;

import cc.ibooker.amaplib.listeners.ZAMapNaviListener;
import cc.ibooker.amaplib.listeners.ZAMapNaviViewListener;
import cc.ibooker.amaplib.listeners.ZSimpleAMapNaviListener;
import cc.ibooker.amaplib.listeners.ZSimpleAMapNaviViewListener;

/**
 * 自定义导航、路径规划等
 * 注意：1、优先经纬度算路；2、暂不支持独立路径规划；
 * https://lbs.amap.com/api/android-navi-sdk/guide/route-plan/drive-route-plan
 *
 * @author 邹峰立
 */
public class ZAMapNaviView extends AMapNaviView
        implements AMapNaviViewListener,
        AMapNaviListener {
    private AMapNaviViewOptions options;
    private AMapNavi aMapNavi;// 导航规划计算类
    private ArrayList<NaviLatLng> mWayPointList;// 途经点坐标
    private ArrayList<NaviLatLng> sList;// 起始点集
    private ArrayList<NaviLatLng> eList;// 终点集
    private NaviPoi startNaviPoi, endNaviPoi;// 起始点/终止点POI
    private ArrayList<NaviPoi> mWayNaviPoiList;// 途经点
    // 当前的导航类型
    private int currentNaviType = NaviType.GPS;
    // 路线规划策略
    private int strategy = -1;
    // 骑行/步行路线规划策略
    private TravelStrategy travelStrategy = TravelStrategy.SINGLE;
    // 是否开启躲避拥堵
    private boolean isOpenCongestion = true;
    // 是否走高速
    private boolean isAvoidHightSpeed = false;
    // 是否避免收费
    private boolean isCost = false;
    // 是否高速优先
    private boolean isHightSpeed = false;
    // 是否多路线
    private boolean isMultipleroute = false;
    // 计算路线方式 0-驾车，1-货车，2-步行，3-骑行
    private int calculateRouteType = 0;
    // 车辆信息 - 当计算路线为货车的时候需要设置该值
    private AMapCarInfo aMapCarInfo;

    // 是否开启自定RouteOverLay
    private boolean isOpenRouteOverLay = false;

    // 导航图加载监听
    private ZAMapNaviViewListener zaMapNaviViewListener;

    public ZAMapNaviView setZaMapNaviViewListener(ZAMapNaviViewListener zaMapNaviViewListener) {
        this.zaMapNaviViewListener = zaMapNaviViewListener;
        return this;
    }

    // 简化导航图加载监听
    private ZSimpleAMapNaviViewListener zSimpleAMapNaviViewListener;

    public ZAMapNaviView setzSimpleAMapNaviViewListener(ZSimpleAMapNaviViewListener zSimpleAMapNaviViewListener) {
        this.zSimpleAMapNaviViewListener = zSimpleAMapNaviViewListener;
        return this;
    }

    // 导航监听
    private ZAMapNaviListener zaMapNaviListener;

    public ZAMapNaviView setZaMapNaviListener(ZAMapNaviListener zaMapNaviListener) {
        this.zaMapNaviListener = zaMapNaviListener;
        return this;
    }

    // 简化导航监听
    private ZSimpleAMapNaviListener zSimpleAMapNaviListener;

    public ZAMapNaviView setzSimpleAMapNaviListener(ZSimpleAMapNaviListener zSimpleAMapNaviListener) {
        this.zSimpleAMapNaviListener = zSimpleAMapNaviListener;
        return this;
    }

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
        this.options = getViewOptions();
        this.setAMapNaviViewListener(this);
        this.aMapNavi = AMapNavi.getInstance(context.getApplicationContext());
        this.aMapNavi.addAMapNaviListener(this);
    }

    // 获取焦点
    public void onZResume() {
        super.onResume();
        if (aMapNavi != null)
            aMapNavi.resumeNavi();
    }

    // 暂停
    public void onZPause() {
        super.onPause();
        if (aMapNavi != null)
            aMapNavi.pauseNavi();
    }

    // 销毁
    public void onZDestroy() {
        super.onDestroy();
        if (aMapNavi != null)
            aMapNavi.destroy();
    }

    // 获取导航控件
    public AMapNavi getAMapNavi() {
        return aMapNavi;
    }

    /**
     * 设置路线规划策略
     *
     * @param strategy 0~20 21种策略
     *                 https://lbs.amap.com/api/android-navi-sdk/guide/route-plan/drive-route-plan
     */
    public ZAMapNaviView setStrategy(int strategy) {
        this.strategy = strategy;
        return this;
    }

    // 骑行/步行路线规划策略
    public ZAMapNaviView setTravelStrategy(TravelStrategy travelStrategy) {
        this.travelStrategy = travelStrategy;
        return this;
    }

    /**
     * 设置路线规划类型，如果设置货车模式，要设置车辆信息
     *
     * @param calculateRouteType 0-驾车，1-货车，2-步行，3-骑行
     */
    public ZAMapNaviView setCalculateRouteType(int calculateRouteType) {
        this.calculateRouteType = calculateRouteType;
        return this;
    }

    /**
     * 设置车辆信息
     *
     * @param aMapCarInfo 待设置内容
     */
    public ZAMapNaviView setAMapCarInfo(AMapCarInfo aMapCarInfo) {
//        aMapCarInfo.setCarType("1");//设置车辆类型，0小车，1货车
//        aMapCarInfo.setCarNumber("京DFZ239");//设置车辆的车牌号码. 如:京DFZ239,京ABZ239
//        aMapCarInfo.setVehicleSize("4");// 设置货车的等级
//        aMapCarInfo.setVehicleLoad("100");//设置货车的总重，单位：吨。
//        aMapCarInfo.setVehicleWeight("99");//设置货车的载重，单位：吨。
//        aMapCarInfo.setVehicleLength("25");//  设置货车的最大长度，单位：米。
//        aMapCarInfo.setVehicleWidth("2");//设置货车的最大宽度，单位：米。 如:1.8，1.5等等。
//        aMapCarInfo.setVehicleHeight("4");//设置货车的高度，单位：米。
//        aMapCarInfo.setVehicleAxis("6");//设置货车的轴数
//        aMapCarInfo.setVehicleLoadSwitch(true);//设置车辆的载重是否参与算路
//        aMapCarInfo.setRestriction(true);//设置是否躲避车辆限行。
        this.aMapCarInfo = aMapCarInfo;
        return this;
    }

    /**
     * 开启或关闭高德提供的导航图层
     *
     * @param isVisible 开启或关闭
     */
    public ZAMapNaviView setLayoutVisible(boolean isVisible) {
        if (options != null) {
            options.setLayoutVisible(isVisible);
            setViewOptions(options);
        }
        return this;
    }

    /**
     * 是否高德提供的导航路线绘制
     *
     * @param bool 自动绘制
     */
    public ZAMapNaviView setAutoDrawRoute(boolean bool) {
        if (options != null) {
            options.setAutoDrawRoute(bool);
            setViewOptions(options);
        }
        return this;
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
     * 添加途径点
     *
     * @param naviLatLng 途径点
     */
    public ZAMapNaviView addWayPoint(NaviLatLng naviLatLng) {
        if (naviLatLng != null) {
            if (mWayPointList == null)
                mWayPointList = new ArrayList<>();
            mWayPointList.add(naviLatLng);
        }
        return this;
    }

    /**
     * 清空途经点
     */
    public ZAMapNaviView clearWayPointList() {
        if (mWayPointList != null)
            mWayPointList.clear();
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
     * 清空起始点
     */
    public ZAMapNaviView clearSList() {
        if (sList != null)
            sList.clear();
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
     * 清空终止点
     */
    public ZAMapNaviView clearEList() {
        if (eList != null)
            eList.clear();
        return this;
    }

    /**
     * 设置起始点POI
     */
    public ZAMapNaviView setStartNaviPoi(NaviPoi start) {
        this.startNaviPoi = start;
        return this;
    }

    /**
     * 设置终止点POI
     */
    public ZAMapNaviView setEndNaviPoi(NaviPoi end) {
        this.endNaviPoi = end;
        return this;
    }

    /**
     * 设置途径点集合
     *
     * @param mWayNaviPoiList 待设置值
     */
    public ZAMapNaviView setWayNaviPoiList(ArrayList<NaviPoi> mWayNaviPoiList) {
        this.mWayNaviPoiList = mWayNaviPoiList;
        return this;
    }

    /**
     * 添加途径点
     *
     * @param naviPoi 途径点
     */
    public ZAMapNaviView addWayNaviPoiList(NaviPoi naviPoi) {
        if (naviPoi != null) {
            if (mWayNaviPoiList == null)
                mWayNaviPoiList = new ArrayList<>();
            mWayNaviPoiList.add(naviPoi);
        }
        return this;
    }

    /**
     * 清空途经点
     */
    public ZAMapNaviView clearWayNaviPoiList() {
        if (mWayNaviPoiList != null)
            mWayNaviPoiList.clear();
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
     * 是否开启躲避拥堵
     *
     * @param openCongestion 参数 默认true
     */
    public ZAMapNaviView setOpenCongestion(boolean openCongestion) {
        isOpenCongestion = openCongestion;
        return this;
    }

    /**
     * 是否走高速
     *
     * @param avoidHightSpeed 参数 默认tfalse
     */
    public ZAMapNaviView setAvoidHightSpeed(boolean avoidHightSpeed) {
        isAvoidHightSpeed = avoidHightSpeed;
        return this;
    }

    /**
     * 是否避免收费
     *
     * @param cost 参数 默认tfalse
     */
    public ZAMapNaviView setCost(boolean cost) {
        isCost = cost;
        return this;
    }

    /**
     * 设置是否高速优先
     *
     * @param hightSpeed 参数，默认false
     */
    public ZAMapNaviView setHightSpeed(boolean hightSpeed) {
        isHightSpeed = hightSpeed;
        return this;
    }

    /**
     * 是否多路线
     *
     * @param multipleroute 参数，默认false
     */
    public ZAMapNaviView setMultipleroute(boolean multipleroute) {
        isMultipleroute = multipleroute;
        return this;
    }

    /**
     * 是否开启自定义RouteOverLay
     *
     * @param openRouteOverLay 参数，默认false
     */
    public ZAMapNaviView setOpenRouteOverLay(boolean openRouteOverLay) {
        isOpenRouteOverLay = openRouteOverLay;
        return this;
    }

    /**
     * 开始导航
     */
    public ZAMapNaviView startNavi() {
        if (currentNaviType == NaviType.GPS)
            if (checkGpsIsOpen()) {
                if (!aMapNavi.isGpsReady())
                    aMapNavi.startNavi(NaviType.GPS);
                else
                    aMapNavi.startNavi(currentNaviType);
            } else
                openGPSSetting();
        else
            aMapNavi.startNavi(currentNaviType);
        return this;
    }

    // 导航设置
    @Override
    public void onNaviSetting() {
        if (zaMapNaviViewListener != null)
            zaMapNaviViewListener.onNaviSetting();
    }

    // 取消导航监听
    @Override
    public void onNaviCancel() {
        if (zaMapNaviViewListener != null)
            zaMapNaviViewListener.onNaviCancel();
        if (zSimpleAMapNaviViewListener != null)
            zSimpleAMapNaviViewListener.onNaviCancel();
    }

    // 退出导航监听
    @Override
    public boolean onNaviBackClick() {
        if (zaMapNaviViewListener != null)
            return zaMapNaviViewListener.onNaviBackClick();
        if (zSimpleAMapNaviViewListener != null)
            return zSimpleAMapNaviViewListener.onNaviBackClick();
        return false;
    }

    // 导航地图模式
    @Override
    public void onNaviMapMode(int i) {
        if (zaMapNaviViewListener != null)
            zaMapNaviViewListener.onNaviMapMode(i);
    }

    // 导航转弯点击
    @Override
    public void onNaviTurnClick() {
        if (zaMapNaviViewListener != null)
            zaMapNaviViewListener.onNaviTurnClick();
    }

    // 导航下个道路点击
    @Override
    public void onNextRoadClick() {
        if (zaMapNaviViewListener != null)
            zaMapNaviViewListener.onNextRoadClick();
    }

    // 导航扫描按钮点击
    @Override
    public void onScanViewButtonClick() {
        if (zaMapNaviViewListener != null)
            zaMapNaviViewListener.onScanViewButtonClick();
    }

    // 导航锁定地图
    @Override
    public void onLockMap(boolean b) {
        if (zaMapNaviViewListener != null)
            zaMapNaviViewListener.onLockMap(b);
    }

    // 导航页面加载完成
    @Override
    public void onNaviViewLoaded() {
        if (zaMapNaviViewListener != null)
            zaMapNaviViewListener.onNaviViewLoaded();
    }

    /**
     * 组件地图白天黑夜模式切换回调
     *
     * @param mapType 枚举值参考AMap类, 3-黑夜，4-白天
     * @since 6.7.0
     */
    @Override
    public void onMapTypeChanged(int mapType) {
        if (zaMapNaviViewListener != null)
            zaMapNaviViewListener.onMapTypeChanged(mapType);
    }

    // 导航视图是否展示模式
    @Override
    public void onNaviViewShowMode(int i) {
        if (zaMapNaviViewListener != null)
            zaMapNaviViewListener.onNaviViewShowMode(i);
    }

    // 路线规划对象初始化失败
    @Override
    public void onInitNaviFailure() {
        if (zaMapNaviListener != null)
            zaMapNaviListener.onInitNaviFailure();
        if (zSimpleAMapNaviListener != null)
            zSimpleAMapNaviListener.onInitNaviFailure();
    }

    // 导航初始化成功
    @Override
    public void onInitNaviSuccess() {
        if (zaMapNaviListener != null)
            zaMapNaviListener.onInitNaviSuccess();
        if (zSimpleAMapNaviListener != null)
            zSimpleAMapNaviListener.onInitNaviFailure();
        if (zSimpleAMapNaviListener == null && zaMapNaviListener == null) {
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
            if (strategy < 0) {
                strategy = 0;
                try {
                    strategy = aMapNavi.strategyConvert(isOpenCongestion, isAvoidHightSpeed, isCost, isHightSpeed, isMultipleroute);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // 经纬度算路
            if (sList != null || eList != null) {
                if (sList == null)
                    // 0-驾车，1-货车，2-步行，3-骑行
                    switch (calculateRouteType) {
                        case 1:
                            aMapNavi.setCarInfo(aMapCarInfo);
                            aMapNavi.calculateDriveRoute(eList, mWayPointList, strategy);
                            break;
                        case 2:
                            if (eList != null && eList.size() > 0)
                                aMapNavi.calculateWalkRoute(eList.get(0));
                            break;
                        case 3:
                            if (eList != null && eList.size() > 0)
                                aMapNavi.calculateRideRoute(eList.get(0));
                            break;
                        default:
                            aMapNavi.calculateDriveRoute(eList, mWayPointList, strategy);
                            break;
                    }
                else
                    switch (calculateRouteType) {
                        case 1:
                            aMapNavi.setCarInfo(aMapCarInfo);
                            aMapNavi.calculateDriveRoute(sList, eList, mWayPointList, strategy);
                            break;
                        case 2:
                            if (eList != null && eList.size() > 0 && sList.size() > 0)
                                aMapNavi.calculateWalkRoute(sList.get(0), eList.get(0));
                            break;
                        case 3:
                            if (eList != null && eList.size() > 0 && sList.size() > 0)
                                aMapNavi.calculateRideRoute(sList.get(0), eList.get(0));
                            break;
                        default:
                            aMapNavi.calculateDriveRoute(sList, eList, mWayPointList, strategy);
                            break;
                    }
            }
            // POI算路
            if (startNaviPoi != null && endNaviPoi != null) {
                // 0-驾车，1-货车，2-步行，3-骑行
                switch (calculateRouteType) {
                    case 1:
                        aMapNavi.setCarInfo(aMapCarInfo);
                        aMapNavi.calculateDriveRoute(startNaviPoi, endNaviPoi, mWayNaviPoiList, strategy);
                        break;
                    case 2:
                        aMapNavi.calculateWalkRoute(startNaviPoi, endNaviPoi, travelStrategy);
                        break;
                    case 3:
                        aMapNavi.calculateRideRoute(startNaviPoi, endNaviPoi, travelStrategy);
                        break;
                    default:
                        aMapNavi.calculateDriveRoute(startNaviPoi, endNaviPoi, mWayNaviPoiList, strategy);
                        break;
                }
            }
        }
    }

    // 开始导航
    @Override
    public void onStartNavi(int i) {
        if (zaMapNaviListener != null)
            zaMapNaviListener.onStartNavi(i);
        if (zSimpleAMapNaviListener != null)
            zSimpleAMapNaviListener.onStartNavi(i);
    }

    @Override
    public void onTrafficStatusUpdate() {
        if (zaMapNaviListener != null)
            zaMapNaviListener.onTrafficStatusUpdate();
        if (zSimpleAMapNaviListener != null)
            zSimpleAMapNaviListener.onTrafficStatusUpdate();
    }

    /**
     * 当GPS位置有更新时的回调函数。
     *
     * @param aMapNaviLocation 当前位置的定位信息。
     * @since 5.2.0
     */
    @Override
    public void onLocationChange(AMapNaviLocation aMapNaviLocation) {
        if (zaMapNaviListener != null)
            zaMapNaviListener.onLocationChange(aMapNaviLocation);
        if (zSimpleAMapNaviListener != null)
            zSimpleAMapNaviListener.onLocationChange(aMapNaviLocation);
    }

    /**
     * 导航播报信息回调函数。
     *
     * @param s 播报文字。
     * @since 5.2.0
     */
    @Override
    public void onGetNavigationText(int i, String s) {
        if (zaMapNaviListener != null)
            zaMapNaviListener.onGetNavigationText(i, s);
    }

    /**
     * 导航播报信息回调函数。
     *
     * @param s 播报文字。
     * @since 5.2.0
     */
    @Override
    public void onGetNavigationText(String s) {
        if (zaMapNaviListener != null)
            zaMapNaviListener.onGetNavigationText(s);
        if (zSimpleAMapNaviListener != null)
            zSimpleAMapNaviListener.onGetNavigationText(s);
    }

    @Override
    public void onEndEmulatorNavi() {
        if (zaMapNaviListener != null)
            zaMapNaviListener.onEndEmulatorNavi();
        if (zSimpleAMapNaviListener != null)
            zSimpleAMapNaviListener.onEndEmulatorNavi();
    }

    @Override
    public void onArriveDestination() {
        if (zaMapNaviListener != null)
            zaMapNaviListener.onArriveDestination();
        if (zSimpleAMapNaviListener != null)
            zSimpleAMapNaviListener.onArriveDestination();
    }

    // 路线规划失败
    @Override
    public void onCalculateRouteFailure(int i) {
        if (zaMapNaviListener != null)
            zaMapNaviListener.onCalculateRouteFailure(i);
        if (zSimpleAMapNaviListener != null)
            zSimpleAMapNaviListener.onCalculateRouteFailure(i);
    }

    @Override
    public void onReCalculateRouteForYaw() {
        if (zaMapNaviListener != null)
            zaMapNaviListener.onReCalculateRouteForYaw();
    }

    @Override
    public void onReCalculateRouteForTrafficJam() {
        if (zaMapNaviListener != null)
            zaMapNaviListener.onReCalculateRouteForTrafficJam();
    }

    @Override
    public void onArrivedWayPoint(int i) {
        if (zaMapNaviListener != null)
            zaMapNaviListener.onArrivedWayPoint(i);
        if (zSimpleAMapNaviListener != null)
            zSimpleAMapNaviListener.onArrivedWayPoint(i);
    }

    @Override
    public void onGpsOpenStatus(boolean b) {
        if (zaMapNaviListener != null)
            zaMapNaviListener.onGpsOpenStatus(b);
        if (zSimpleAMapNaviListener != null)
            zSimpleAMapNaviListener.onGpsOpenStatus(b);
    }

    @Override
    public void onNaviInfoUpdate(NaviInfo naviInfo) {
        if (zaMapNaviListener != null)
            zaMapNaviListener.onNaviInfoUpdate(naviInfo);
        if (zSimpleAMapNaviListener != null)
            zSimpleAMapNaviListener.onNaviInfoUpdate(naviInfo);
    }

    @Override
    public void updateCameraInfo(AMapNaviCameraInfo[] aMapNaviCameraInfos) {
        if (zaMapNaviListener != null)
            zaMapNaviListener.updateCameraInfo(aMapNaviCameraInfos);
        if (zSimpleAMapNaviListener != null)
            zSimpleAMapNaviListener.updateCameraInfo(aMapNaviCameraInfos);
    }

    @Override
    public void updateIntervalCameraInfo(AMapNaviCameraInfo aMapNaviCameraInfo, AMapNaviCameraInfo aMapNaviCameraInfo1, int i) {
        if (zaMapNaviListener != null)
            zaMapNaviListener.updateIntervalCameraInfo(aMapNaviCameraInfo, aMapNaviCameraInfo1, i);
        if (zSimpleAMapNaviListener != null)
            zSimpleAMapNaviListener.updateIntervalCameraInfo(aMapNaviCameraInfo, aMapNaviCameraInfo1, i);
    }

    @Override
    public void onServiceAreaUpdate(AMapServiceAreaInfo[] aMapServiceAreaInfos) {
        if (zaMapNaviListener != null)
            zaMapNaviListener.onServiceAreaUpdate(aMapServiceAreaInfos);
        if (zSimpleAMapNaviListener != null)
            zSimpleAMapNaviListener.onServiceAreaUpdate(aMapServiceAreaInfos);
    }

    @Override
    public void showCross(AMapNaviCross aMapNaviCross) {
        if (zaMapNaviListener != null)
            zaMapNaviListener.showCross(aMapNaviCross);
        if (zSimpleAMapNaviListener != null)
            zSimpleAMapNaviListener.showCross(aMapNaviCross);
    }

    @Override
    public void hideCross() {
        if (zaMapNaviListener != null)
            zaMapNaviListener.hideCross();
        if (zSimpleAMapNaviListener != null)
            zSimpleAMapNaviListener.hideCross();
    }

    @Override
    public void showModeCross(AMapModelCross aMapModelCross) {
        if (zaMapNaviListener != null)
            zaMapNaviListener.showModeCross(aMapModelCross);
        if (zSimpleAMapNaviListener != null)
            zSimpleAMapNaviListener.showModeCross(aMapModelCross);
    }

    @Override
    public void hideModeCross() {
        if (zaMapNaviListener != null)
            zaMapNaviListener.hideModeCross();
        if (zSimpleAMapNaviListener != null)
            zSimpleAMapNaviListener.hideModeCross();
    }

    @Override
    public void showLaneInfo(AMapLaneInfo[] aMapLaneInfos, byte[] bytes, byte[] bytes1) {
        if (zaMapNaviListener != null)
            zaMapNaviListener.showLaneInfo(aMapLaneInfos, bytes, bytes1);
    }

    @Override
    public void showLaneInfo(AMapLaneInfo aMapLaneInfo) {
        if (zaMapNaviListener != null)
            zaMapNaviListener.showLaneInfo(aMapLaneInfo);
        if (zSimpleAMapNaviListener != null)
            zSimpleAMapNaviListener.showLaneInfo(aMapLaneInfo);
    }

    @Override
    public void hideLaneInfo() {
        if (zaMapNaviListener != null)
            zaMapNaviListener.hideLaneInfo();
        if (zSimpleAMapNaviListener != null)
            zSimpleAMapNaviListener.hideLaneInfo();
    }

    // 导航路线规划成功 - 多策略
    @Override
    public void onCalculateRouteSuccess(int[] ints) {
        if (zaMapNaviListener != null)
            zaMapNaviListener.onCalculateRouteSuccess(ints);
    }

    @Override
    public void notifyParallelRoad(int i) {
        if (zaMapNaviListener != null)
            zaMapNaviListener.notifyParallelRoad(i);
    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo[] aMapNaviTrafficFacilityInfos) {
        if (zaMapNaviListener != null)
            zaMapNaviListener.OnUpdateTrafficFacility(aMapNaviTrafficFacilityInfos);
    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo aMapNaviTrafficFacilityInfo) {
        if (zaMapNaviListener != null)
            zaMapNaviListener.OnUpdateTrafficFacility(aMapNaviTrafficFacilityInfo);
    }

    @Override
    public void updateAimlessModeStatistics(AimLessModeStat aimLessModeStat) {
        if (zaMapNaviListener != null)
            zaMapNaviListener.updateAimlessModeStatistics(aimLessModeStat);
    }

    @Override
    public void updateAimlessModeCongestionInfo(AimLessModeCongestionInfo aimLessModeCongestionInfo) {
        if (zaMapNaviListener != null)
            zaMapNaviListener.updateAimlessModeCongestionInfo(aimLessModeCongestionInfo);
    }

    @Override
    public void onPlayRing(int i) {
        if (zaMapNaviListener != null)
            zaMapNaviListener.onPlayRing(i);
        if (zSimpleAMapNaviListener != null)
            zSimpleAMapNaviListener.onPlayRing(i);
    }

    // 导航路线规划成功 - 多策略
    @Override
    public void onCalculateRouteSuccess(AMapCalcRouteResult aMapCalcRouteResult) {
        if (zaMapNaviListener != null)
            zaMapNaviListener.onCalculateRouteSuccess(aMapCalcRouteResult);
        if (zSimpleAMapNaviListener != null)
            zSimpleAMapNaviListener.onCalculateRouteSuccess(aMapCalcRouteResult);
        if (zSimpleAMapNaviListener == null && zaMapNaviListener == null) {
            int[] ints = aMapCalcRouteResult.getRouteid();
            // 显示路径或开启导航
            if (ints != null && ints.length > 0) {
                // 对于多路径需要告诉 AMapNavi 选择的是哪条路，然后才能进行导航
                aMapNavi.selectRouteId(ints[0]);
                if (isOpenRouteOverLay) {
                    // 如果根据获取的导航路线来自定义绘制
                    RouteOverLay routeOverlay = new RouteOverLay(getMap(), aMapNavi.getNaviPath(), getContext());
                    routeOverlay.setStartPointBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.amap_r1));
                    routeOverlay.setEndPointBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.amap_b1));
                    routeOverlay.setWayPointBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.amap_b2));
                    routeOverlay.setTrafficLine(false);
                    int[] color = new int[10];
                    color[0] = Color.BLACK;
                    color[1] = Color.RED;
                    color[2] = Color.BLUE;
                    color[3] = Color.YELLOW;
                    color[4] = Color.GRAY;
                    routeOverlay.addToMap(color, aMapNavi.getNaviPath().getWayPointIndex());
                }
                startNavi();
            }
        }
    }

    // 路线规划失败
    @Override
    public void onCalculateRouteFailure(AMapCalcRouteResult aMapCalcRouteResult) {
        if (zaMapNaviListener != null)
            zaMapNaviListener.onCalculateRouteFailure(aMapCalcRouteResult);
        if (zSimpleAMapNaviListener != null)
            zSimpleAMapNaviListener.onCalculateRouteFailure(aMapCalcRouteResult);
    }

    @Override
    public void onNaviRouteNotify(AMapNaviRouteNotifyData aMapNaviRouteNotifyData) {
        if (zaMapNaviListener != null)
            zaMapNaviListener.onNaviRouteNotify(aMapNaviRouteNotifyData);
        if (zSimpleAMapNaviListener != null)
            zSimpleAMapNaviListener.onNaviRouteNotify(aMapNaviRouteNotifyData);
    }

    // GPS信号弱
    @Override
    public void onGpsSignalWeak(boolean b) {
        if (zaMapNaviListener != null)
            zaMapNaviListener.onGpsSignalWeak(b);
        if (zSimpleAMapNaviListener != null)
            zSimpleAMapNaviListener.onGpsSignalWeak(b);
    }

    /**
     * 检查GPS是否打开
     */
    public boolean checkGpsIsOpen() {
        LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * 打开GPS设置
     */
    public ZAMapNaviView openGPSSetting() {
        if (!checkGpsIsOpen()) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            getContext().startActivity(intent);
        }
        return this;
    }

    /**
     * 设置地图自定义样式
     *
     * @param styleId 官网控制台-自定义样式 获取
     */
    public ZAMapNaviView setStyleId(String styleId) {
        getMap().setCustomMapStyle(
                new com.amap.api.maps.model.CustomMapStyleOptions()
                        .setEnable(true)
                        .setStyleId(styleId)
        );
        return this;
    }

    /**
     * 启动组件进行直接导航时，设置是否进行算路 (只有在直接跳转导航页的情况下才生效)
     *
     * @param needCalculateRouteWhenPresent true : 算路，false : 启动组件以后不会算路直接开启导航。默认为true。
     * @since 5.6.0
     */
    public ZAMapNaviView setNeedCalculateRouteWhenPresent(boolean needCalculateRouteWhenPresent) {
        this.setNeedCalculateRouteWhenPresent(needCalculateRouteWhenPresent);
        return this;
    }

    /**
     * 设置退出导航组件是否销毁导航实例
     *
     * @param destroy true-退出导航页时停止导航，退出组件时销毁导航
     *                false-组件直接导航时，退出导航页不会停止导航，退出组件也不会销毁导航
     * @since 5.6.0
     */
    public ZAMapNaviView setNeedDestroyDriveManagerInstanceWhenNaviExit(boolean destroy) {
        this.setNeedDestroyDriveManagerInstanceWhenNaviExit(destroy);
        return this;
    }

    /**
     * 设置车辆信息，进行尾号限行与货车导航
     *
     * @param carInfo {@link AMapCarInfo}<br>
     * @since 6.0.0
     */
    public ZAMapNaviView setCarInfo(AMapCarInfo carInfo) {
        this.setCarInfo(carInfo);
        return this;
    }

    /**
     * 设置是否使用内部语音播报
     *
     * @param isUseInnerVoice 是否使用内部语音播报
     *                        注意：6.1.0版本开始，默认值改为true
     * @since 6.0.0
     */
    public ZAMapNaviView setUseInnerVoice(boolean isUseInnerVoice) {
        this.setUseInnerVoice(isUseInnerVoice);
        return this;
    }

    /**
     * 设置组件规划路线的策略，默认为{@link com.amap.api.navi.enums.PathPlanningStrategy#DRIVING_MULTIPLE_ROUTES_DEFAULT}，速度优先+躲避拥堵+距离较短，注意仅支持多路线策略
     *
     * @param routeStrategy {@link com.amap.api.navi.enums.PathPlanningStrategy}
     */
    public ZAMapNaviView setRouteStrategy(int routeStrategy) {
        this.setRouteStrategy(routeStrategy);
        return this;
    }

    /**
     * 设置播报模式
     *
     * @param context 上下文对象
     * @param mode    1-简洁播报 2-详细播报 3-静音模式
     * @since 7.1.0
     */
    public ZAMapNaviView setBroadcastMode(Context context, int mode) {
        this.setBroadcastMode(context, mode);
        return this;
    }

    /**
     * 设置导航视角
     *
     * @param context 上下文对象
     * @param mode    1-正北向上 2-车头向上
     * @since 7.1.0
     */
    public ZAMapNaviView setCarDirectionMode(Context context, int mode) {
        this.setCarDirectionMode(context, mode);
        return this;
    }

    /**
     * 设置比例尺智能缩放是否开启
     *
     * @param context 上下文对象
     * @param enable  是否开启
     * @since 7.1.0
     */
    public ZAMapNaviView setScaleAutoChangeEnable(Context context, boolean enable) {
        this.setScaleAutoChangeEnable(context, enable);
        return this;
    }

}
