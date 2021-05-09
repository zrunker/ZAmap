package cc.ibooker.amaplib.util;

import android.content.Context;

import com.amap.api.maps.model.Poi;
import com.amap.api.navi.AmapNaviPage;
import com.amap.api.navi.AmapNaviParams;
import com.amap.api.navi.AmapNaviType;
import com.amap.api.navi.AmapPageType;

import java.util.List;

public class AmapRouteActivityUtil {
    /**
     * 无起终点启动导航组件
     *
     * @param context 上下文对象
     */
    public void startNavi(Context context) {
        startNavi(context, AmapNaviType.DRIVER, AmapPageType.ROUTE);
    }

    /**
     * 无起终点启动导航组件
     *
     * @param context      上下文对象
     * @param amapNaviType DRIVER, WALK, RIDE
     * @param amapPageType ROUTE-路线规划界面, NAVI-导航界面
     */
    public void startNavi(Context context,
                          AmapNaviType amapNaviType, AmapPageType amapPageType) {
        startNavi(context, amapNaviType, amapPageType, null, null, null);
    }

    /**
     * 无起终点启动导航组件
     *
     * @param context      上下文对象
     * @param amapNaviType DRIVER, WALK, RIDE
     * @param amapPageType ROUTE-路线规划界面, NAVI-导航界面
     * @param start        起始点
     * @param poiList      途经点
     * @param end          终止点
     */
    public void startNavi(Context context,
                          AmapNaviType amapNaviType, AmapPageType amapPageType,
                          Poi start, List<Poi> poiList, Poi end) {
        // 构建导航组件配置类，没有传入起点，所以起点默认为 “我的位置”
        AmapNaviParams params = new AmapNaviParams(start, poiList, end, amapNaviType, amapPageType);
        // 启动导航组件
        AmapNaviPage.getInstance().showRouteActivity(context.getApplicationContext(), params, null);
    }
}
