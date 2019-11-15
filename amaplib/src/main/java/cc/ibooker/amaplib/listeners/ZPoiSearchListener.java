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

    void onPoiSearchSuccess();

    void onPoiSearchNext(List<PoiItem> poiItems, List<SuggestionCity> suggestionCities);
}
