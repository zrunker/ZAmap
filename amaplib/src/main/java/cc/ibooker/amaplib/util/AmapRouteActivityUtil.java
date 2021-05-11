package cc.ibooker.amaplib.util;

import android.content.Context;

import com.amap.api.maps.model.Poi;
import com.amap.api.navi.AmapNaviPage;
import com.amap.api.navi.AmapNaviParams;
import com.amap.api.navi.AmapNaviType;
import com.amap.api.navi.AmapPageType;
import com.amap.api.navi.INaviInfoCallback;

import java.util.List;

public class AmapRouteActivityUtil {
    /**
     * 无起终点启动导航组件
     *
     * @param context 上下文对象
     */
    public AmapRouteActivityUtil startNavi(Context context, INaviInfoCallback callback) {
        return startNavi(context, AmapNaviType.DRIVER, AmapPageType.ROUTE, callback);
    }

    /**
     * 无起终点启动导航组件
     *
     * @param context      上下文对象
     * @param amapNaviType DRIVER, WALK, RIDE
     * @param amapPageType ROUTE-路线规划界面, NAVI-导航界面
     */
    public AmapRouteActivityUtil startNavi(Context context,
                                           AmapNaviType amapNaviType, AmapPageType amapPageType, INaviInfoCallback callback) {
        return startNavi(context, amapNaviType, amapPageType, null, null, null, callback);
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
    public AmapRouteActivityUtil startNavi(Context context,
                                           AmapNaviType amapNaviType, AmapPageType amapPageType,
                                           Poi start, List<Poi> poiList, Poi end, INaviInfoCallback callback) {
        // 构建导航组件配置类，没有传入起点，所以起点默认为 “我的位置”
        AmapNaviParams params = new AmapNaviParams(start, poiList, end, amapNaviType, amapPageType);
        // 启动导航组件
        AmapNaviPage.getInstance().showRouteActivity(context.getApplicationContext(), params, callback);
        return this;
    }

    // 退出导航组件
    public AmapRouteActivityUtil exitNavi() {
        AmapNaviPage.getInstance().exitRouteActivity();
        return this;
    }

    // 获取AmapNaviPage操作类
    public AmapNaviPage getAmapNaviPage() {
        return AmapNaviPage.getInstance();
    }
}
