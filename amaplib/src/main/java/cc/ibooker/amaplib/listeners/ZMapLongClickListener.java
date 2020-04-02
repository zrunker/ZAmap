package cc.ibooker.amaplib.listeners;

import com.amap.api.maps.model.LatLng;

/**
 * 地图长按事件监听
 *
 * @author 邹峰立
 */
public interface ZMapLongClickListener {
    void onMapLongClick(LatLng latLng);
}
