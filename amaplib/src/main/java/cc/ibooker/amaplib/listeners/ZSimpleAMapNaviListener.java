package cc.ibooker.amaplib.listeners;

import com.amap.api.navi.model.AMapCalcRouteResult;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapModelCross;
import com.amap.api.navi.model.AMapNaviCameraInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviRouteNotifyData;
import com.amap.api.navi.model.AMapServiceAreaInfo;
import com.amap.api.navi.model.NaviInfo;

/**
 * 导航监听（例如路线规划）
 * 此类是导航事件信息与数据协议类，提供算路导航过程中的事件（如：路径规划成功/失败、TTS字符串、GPS信号弱、到达目的地等）以及实时数据（如：诱导信息NaviInfo、定位信息、电子眼信息等）回调接口。
 *
 * @author 邹峰立
 */
public interface ZSimpleAMapNaviListener {
    void onInitNaviFailure();

    void onStartNavi(int var1);

    void onTrafficStatusUpdate();

    void onLocationChange(AMapNaviLocation var1);

    void onGetNavigationText(String var1);

    void onEndEmulatorNavi();

    void onArriveDestination();

    void onArrivedWayPoint(int var1);

    void onGpsOpenStatus(boolean var1);

    void onNaviInfoUpdate(NaviInfo var1);

    void updateCameraInfo(AMapNaviCameraInfo[] var1);

    void updateIntervalCameraInfo(AMapNaviCameraInfo var1, AMapNaviCameraInfo var2, int var3);

    void onServiceAreaUpdate(AMapServiceAreaInfo[] var1);

    void showCross(AMapNaviCross var1);

    void hideCross();

    void showModeCross(AMapModelCross var1);

    void hideModeCross();

    void showLaneInfo(AMapLaneInfo var1);

    void hideLaneInfo();

    void onPlayRing(int var1);

    void onCalculateRouteSuccess(AMapCalcRouteResult var1);

    void onCalculateRouteFailure(AMapCalcRouteResult var1);

    void onNaviRouteNotify(AMapNaviRouteNotifyData var1);

    // GPS信号弱
    void onGpsSignalWeak(boolean b);

    void onCalculateRouteFailure(int i);
}
