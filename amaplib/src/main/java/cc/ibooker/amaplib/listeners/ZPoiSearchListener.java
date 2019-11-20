package cc.ibooker.amaplib.listeners;

import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;

import java.util.List;

/**
 * POI搜索结果监听
 *
 * @author 邹峰立
 */
public interface ZPoiSearchListener {

    void onPoiSearchStart();

    void onPoiSearchComplete();

    void onPoiSearchFail(String message);

    void onPoiSearchError(Throwable e);

//    void onPoiSearchSuccess();

    /**
     * POI搜索结果
     *
     * @param poiItems         单项POI信息
     * @param suggestionCities 建议城市
     */
    void onPoiSearchNext(List<PoiItem> poiItems, List<SuggestionCity> suggestionCities);

    /**
     * 单个Poi搜索结果
     *
     * @param poiItem   单个POI
     * @param errorCode 错误码
     */
    void onPoiItemSearched(PoiItem poiItem, int errorCode);
}
