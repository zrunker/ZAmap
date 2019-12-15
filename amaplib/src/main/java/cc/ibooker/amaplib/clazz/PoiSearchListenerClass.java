package cc.ibooker.amaplib.clazz;

import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;

import java.util.List;

import cc.ibooker.amaplib.listeners.ZPoiSearchListener;

/**
 * POI接口实现类
 *
 * @author 邹峰立
 */
@Deprecated
public class PoiSearchListenerClass implements ZPoiSearchListener {
    @Override
    public void onPoiSearchStart() {

    }

    @Override
    public void onPoiSearchComplete() {

    }

    @Override
    public void onPoiSearchFail(String message) {

    }

    @Override
    public void onPoiSearchError(Throwable e) {

    }

    @Override
    public void onPoiSearchNext(List<PoiItem> poiItems, List<SuggestionCity> suggestionCities) {

    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int errorCode) {

    }
}
