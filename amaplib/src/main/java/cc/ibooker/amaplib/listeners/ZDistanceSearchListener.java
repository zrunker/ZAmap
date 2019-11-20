package cc.ibooker.amaplib.listeners;

import com.amap.api.services.route.DistanceResult;

import java.util.ArrayList;

/**
 * 测量距离 接口
 *
 * @author 邹峰立
 */
public interface ZDistanceSearchListener {

    void onDistanceSearchStart();

    void onDistanceSearchComplete();

    void onDistanceSearchFail(String message);

    void onDistanceSearchError(Throwable e);

//    void onDistanceSearchSuccess();

    /**
     * 测量距离返回结果
     *
     * @param distanceResult 返回结果集合
     * @param distanceList   返回距离集合 单位米
     */
    void onDistanceSearched(DistanceResult distanceResult, ArrayList<Float> distanceList);
}
