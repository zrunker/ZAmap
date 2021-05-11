package cc.ibooker.amaplib.util;

import android.content.Context;

import com.amap.api.maps.model.Poi;
import com.amap.api.navi.AmapNaviPage;
import com.amap.api.navi.AmapNaviParams;
import com.amap.api.navi.AmapNaviType;
import com.amap.api.navi.AmapPageType;
import com.amap.api.navi.INaviInfoCallback;

import java.util.List;

///**
//* 导航播报信息回调函数。
//*
//* @param s 播报文字。
//* @since 5.2.0
//*/
//void onGetNavigationText(String s);
///**
//* 当GPS位置有更新时的回调函数。
//*
//* @param location 当前位置的定位信息。
//* @since 5.2.0
//*/
//void onLocationChange(AMapNaviLocation location);
///**
//* 退出组件或退出组件导航的回调函数
//* @param pageType 参见{@link com.amap.api.navi.enums.PageType}
//* @since 5.6.0
//*/
//void onExitPage(int pageType);
///**
//* 策略选择界面中切换算路偏好回调
//* @param strategy 切换后偏好 参见{@link com.amap.api.navi.enums.PathPlanningStrategy}
//* @since 6.0.0
//*/
//void onStrategyChanged(int strategy);
///**
//* 获取导航地图自定义View，该View在导航整体界面的下面，注意要设置setLayoutParams并且设置高度
//* @return View
//* @since 6.1.0
//*/
//View getCustomNaviBottomView();
///**
//* 获取导航地图自定义View,该View在导航界面的当前路名位置，使用该方法以后，将不会显示当前路名
//* @return View
//* @since 6.1.0
//*/
//View getCustomNaviView();
///**
//* 组件地图白天黑夜模式切换回调
//* @param mapType 枚举值参考AMap类, 3-黑夜，4-白天
//* @since 6.7.0
//*/
//void onMapTypeChanged(int mapType);
//
///**
//* 获取导航地图自定义View,该View在导航界面的垂直居中，水平靠左位置
//* @return View
//* @since 6.9.0
//*/
//View getCustomMiddleView();
///**
//* 导航视角变化回调
//* @since 7.1.0
//* @param naviMode 导航视角, 1-正北朝上模式 2-车头朝上状态
//*/
//void onNaviDirectionChanged(int naviMode);
///**
//* 昼夜模式设置变化回调
//* @since 7.1.0
//* @param mode 0-自动切换 1-白天 2-夜间
//*/
//void onDayAndNightModeChanged(int mode);
///**
//* 播报模式变化回调
//* @since 7.1.0
//* @param mode 1-简洁播报 2-详细播报 3-静音
//*/
//void onBroadcastModeChanged(int mode);
///**
//* 比例尺智能缩放设置变化回调
//* @since 7.1.0
//* @param enable 是否开启
//*/
//void onScaleAutoChanged(boolean enable);
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
