package cc.ibooker.amaplib.listeners;

import com.amap.api.fence.GeoFence;

import java.util.List;

/**
 * 地址围栏回调
 *
 * @author 邹峰立
 */
public interface ZGeoFenceListener {

    void onGeoFenceCreateFinished(List<GeoFence> geoFenceList, int errorCode, String s);
}
