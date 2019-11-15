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

    void onRouteSearchSuccess();

    void onDriveNext(DriveRouteResult result, ArrayList<Float> distanceList);

    void onWalkNext(WalkRouteResult result, ArrayList<Float> distanceList);

    void onRideNext(RideRouteResult result, ArrayList<Float> distanceList);

    void onBusNext(BusRouteResult result, ArrayList<Float> distanceList);
}
