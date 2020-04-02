package cc.ibooker.amaplib.listeners;

import com.amap.api.maps.model.LatLng;

/**
 * 地图点击事件监听
 *
 * @author 邹峰立
 */
public interface ZMapClickListener {
    void onMapClick(LatLng latLng);
}
