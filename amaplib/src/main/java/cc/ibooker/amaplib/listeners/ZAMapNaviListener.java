package cc.ibooker.amaplib.listeners;

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
import com.autonavi.tbt.TrafficFacilityInfo;

/**
 * 导航监听（例如路线规划）
 *
 * @author 邹峰立
 */
@Deprecated
public interface ZAMapNaviListener {
    void onInitNaviFailure();

    void onInitNaviSuccess();

    void onStartNavi(int var1);

    void onTrafficStatusUpdate();

    void onLocationChange(AMapNaviLocation var1);

    /**
     * @deprecated
     */
    void onGetNavigationText(int var1, String var2);

    void onGetNavigationText(String var1);

    void onEndEmulatorNavi();

    void onArriveDestination();

    /**
     * @deprecated
     */
    void onCalculateRouteFailure(int var1);

    /**
     * @deprecated
     */
    void onReCalculateRouteForYaw();

    /**
     * @deprecated
     */
    void onReCalculateRouteForTrafficJam();

    void onArrivedWayPoint(int var1);

    void onGpsOpenStatus(boolean var1);

    void onNaviInfoUpdate(NaviInfo var1);

    /**
     * @deprecated
     */
    void onNaviInfoUpdated(AMapNaviInfo var1);

    void updateCameraInfo(AMapNaviCameraInfo[] var1);

    void updateIntervalCameraInfo(AMapNaviCameraInfo var1, AMapNaviCameraInfo var2, int var3);

    void onServiceAreaUpdate(AMapServiceAreaInfo[] var1);

    void showCross(AMapNaviCross var1);

    void hideCross();

    void showModeCross(AMapModelCross var1);

    void hideModeCross();

    /**
     * @deprecated
     */
    void showLaneInfo(AMapLaneInfo[] var1, byte[] var2, byte[] var3);

    void showLaneInfo(AMapLaneInfo var1);

    void hideLaneInfo();

    /**
     * @deprecated
     */
    void onCalculateRouteSuccess(int[] var1);

    /**
     * @deprecated
     */
    void notifyParallelRoad(int var1);

    /**
     * @deprecated
     */
    void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo[] var1);

    /**
     * @deprecated
     */
    void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo var1);

    /**
     * @deprecated
     */
    void OnUpdateTrafficFacility(TrafficFacilityInfo var1);

    /**
     * @deprecated
     */
    void updateAimlessModeStatistics(AimLessModeStat var1);

    /**
     * @deprecated
     */
    void updateAimlessModeCongestionInfo(AimLessModeCongestionInfo var1);

    void onPlayRing(int var1);

    void onCalculateRouteSuccess(AMapCalcRouteResult var1);

    void onCalculateRouteFailure(AMapCalcRouteResult var1);

    void onNaviRouteNotify(AMapNaviRouteNotifyData var1);
}
