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

//    void onLocationSuccess();

    /**
     * 定位结果相关信息
     *
     * @param locationData 定位相关信息
     */
    void onLocationNext(LocationData locationData);
}
