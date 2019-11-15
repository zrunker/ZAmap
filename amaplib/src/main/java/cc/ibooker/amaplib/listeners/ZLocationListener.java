package cc.ibooker.amaplib.listeners;

import cc.ibooker.amaplib.dto.LocationData;

/**
 * 定位结果
 *
 * @author 邹峰立
 */
public interface ZLocationListener {

    void onLocationStart();

    void onLocationComplete();

    void onLocationFail(String message);

    void onLocationError(Throwable e);

    void onLocationSuccess();

    void onLocationNext(LocationData locationData);
}
