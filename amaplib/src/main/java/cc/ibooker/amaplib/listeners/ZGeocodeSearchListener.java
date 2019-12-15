package cc.ibooker.amaplib.listeners;

import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.RegeocodeResult;

import java.util.ArrayList;
import java.util.List;

/**
 * 逆向地址搜索监听
 *
 * @author 邹峰立
 */
public interface ZGeocodeSearchListener {
    void onGeocodeSearchStart();

    void onGeocodeSearchComplete();

    void onGeocodeSearchFail(String message);

    void onGeocodeSearchError(Throwable e);

    /**
     * 异步逆向地址搜索结果
     *
     * @param regeocodeResult 结果对象
     * @param address         根据经纬度查询到的地址
     */
    void onRegeocodeSearched(RegeocodeResult regeocodeResult, String address);

    /**
     * 逆向地址搜索结果
     *
     * @param geocodeResult      结果集
     * @param geocodeAddressList 地址信息集合
     * @param addressList        地址集合
     */
    void onGeocodeSearched(GeocodeResult geocodeResult, List<GeocodeAddress> geocodeAddressList, ArrayList<String> addressList);
}
