package cc.ibooker.amaplib.clazz;

import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.WalkRouteResult;

import java.util.ArrayList;

import cc.ibooker.amaplib.listeners.ZRouteSearchListener;

/**
 * 路线规划监听Class
 *
 * @author 邹峰立
 */
public class RouteSearchListenerClass implements ZRouteSearchListener {
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
    public void onRouteSearchSuccess() {

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
}
