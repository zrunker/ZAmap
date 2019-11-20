package cc.ibooker.amaplib.listeners;

import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.WalkRouteResult;

import java.util.ArrayList;

/**
 * 路线规划搜索结果监听
 *
 * @author 邹峰立
 */
public interface ZRouteSearchListener {

    void onRouteSearchStart();

    void onRouteSearchComplete();

    void onRouteSearchFail(String message);

    void onRouteSearchError(Throwable e);

//    void onRouteSearchSuccess();

    /**
     * 驾车路径规划结果
     *
     * @param result       结果集合
     * @param distanceList 距离集合
     */
    void onDriveNext(DriveRouteResult result, ArrayList<Float> distanceList);

    /**
     * 步行路径规划结果
     *
     * @param result       结果集合
     * @param distanceList 距离集合
     */
    void onWalkNext(WalkRouteResult result, ArrayList<Float> distanceList);

    /**
     * 骑行路径规划结果
     *
     * @param result       结果集合
     * @param distanceList 距离集合
     */
    void onRideNext(RideRouteResult result, ArrayList<Float> distanceList);

    /**
     * 公交车路径规划结果
     *
     * @param result       结果集合
     * @param distanceList 距离集合
     */
    void onBusNext(BusRouteResult result, ArrayList<Float> distanceList);
}
