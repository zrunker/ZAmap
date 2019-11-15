package cc.ibooker.amaplib;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.animation.Animation;
import com.amap.api.maps.model.animation.ScaleAnimation;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.route.BusPath;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RidePath;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkRouteResult;
import com.autonavi.amap.mapcore.interfaces.IMapFragmentDelegate;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import cc.ibooker.amaplib.dto.LocationData;
import cc.ibooker.amaplib.listeners.ZLocationListener;
import cc.ibooker.amaplib.listeners.ZMapLoadedListener;
import cc.ibooker.amaplib.listeners.ZPoiSearchListener;
import cc.ibooker.amaplib.listeners.ZRouteSearchListener;
import cc.ibooker.amaplib.overlays.BusRouteOverlay;
import cc.ibooker.amaplib.overlays.DrivingRouteOverlay;
import cc.ibooker.amaplib.overlays.PoiOverlay;
import cc.ibooker.amaplib.overlays.RideRouteOverlay;
import cc.ibooker.amaplib.overlays.ViewPoiOverlay;
import cc.ibooker.amaplib.overlays.WalkRouteOverlay;
import cc.ibooker.amaplib.util.AMapUtil;

/**
 * 自定义地图
 * 功能一：显示地图
 * 功能二：路线规划
 * https://lbs.amap.com/dev/demo/ride-route-plan#Android
 *
 * @author 邹峰立
 */
public class ZMapView extends MapView implements
        Thread.UncaughtExceptionHandler,
        RouteSearch.OnRouteSearchListener,
        AMap.OnMapLoadedListener,
        PoiSearch.OnPoiSearchListener,
        AMapLocationListener {
    // SDK在Android 6.0下需要进行运行检测的权限如下：
    private String[] permissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE
    };
    private final int AMAP_REQUEST_CODE = 111;
    private AMap aMap;
    private UiSettings uiSettings;
    private AMapOptions aMapOptions;
    private boolean isOpenMarkerAnim = false;// 是否开启Marker动画
    private boolean isShowMapText = true;// 是否展示地图上的文字
    private boolean isShowBuildings = true;// 是否隐藏3D楼块效果
    private Animation mMarkerAnimation;// Marker动画
    private PoiOverlay mPoiOverlay;// 气泡样式

    public static final int ROUTE_TYPE_BUS = 1, ROUTE_TYPE_DRIVE = 2, ROUTE_TYPE_WALK = 3, ROUTE_TYPE_RIDE = 4;
    private RouteSearch mRouteSearch;
    private String mCurrentCityName = "北京";// 当前城市名称
    private int mRouteType = ROUTE_TYPE_DRIVE;// 交通类型
    private int mSearchMode = RouteSearch.DRIVING_SINGLE_DEFAULT;// 当前查询模式

    private PoiSearch.Query mPoiQuery;// Poi查询条件类
    private PoiSearch mPoiSearch;// POI搜索
    private int poiSearchCurrentPage = 0;

    // 定位
    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;
    private SensorEventHelper mSensorHelper;
    private final int STROKE_COLOR = Color.argb(180, 3, 145, 255);
    private final int FILL_COLOR = Color.argb(10, 0, 0, 180);
    private boolean mFirstLocationFix = false;
    private Marker mLocationMarker;
    private Circle mLocationCircle;

    // 定位监听
    private ZLocationListener zLocationListener;

    public ZMapView setLocationListener(ZLocationListener zLocationListener) {
        this.zLocationListener = zLocationListener;
        return this;
    }

    // 地图加载监听
    private ZMapLoadedListener zMapLoadedListener;

    public ZMapView setMapLoadedListener(ZMapLoadedListener zMapLoadedListener) {
        this.zMapLoadedListener = zMapLoadedListener;
        return this;
    }

    // POI搜索监听
    private ZPoiSearchListener zPoiSearchListener;

    public ZMapView setPoiSearchListener(ZPoiSearchListener zPoiSearchListener) {
        this.zPoiSearchListener = zPoiSearchListener;
        return this;
    }

    // 搜索路线结果监听
    private ZRouteSearchListener zRouteSearchListener;

    public ZMapView setRouteSearchListener(ZRouteSearchListener zRouteSearchListener) {
        this.zRouteSearchListener = zRouteSearchListener;
        return this;
    }

    public ZMapView(Context context) {
        super(context);
        init();
    }

    public ZMapView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public ZMapView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init();
    }

    public ZMapView(Context context, AMapOptions aMapOptions) {
        super(context, aMapOptions);
        this.aMapOptions = aMapOptions;
        init();
    }

    // 初始化
    private void init() {
        aMap = getMap();
        aMap.setOnMapLoadedListener(this);
        requestPermissions();
    }

    public void resume() {
        this.onResume();
    }

    public void destroy() {
        this.onDestroy();
        if (null != mLocationClient) {
            /*
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            mLocationClient.onDestroy();
            mLocationClient = null;
        }
        if (mLocationOption != null)
            mLocationOption = null;
        if (mLocationMarker != null)
            mLocationMarker.destroy();
    }

    public void pause() {
        this.onPause();
        if (mSensorHelper != null) {
            mSensorHelper.unRegisterSensorListener();
            mSensorHelper.setCurrentMarker(null);
            mSensorHelper = null;
        }
        mFirstLocationFix = false;
    }

    public void stop() {
        if (mLocationClient != null)
            mLocationClient.stopLocation();// 停止定位
    }

    /**
     * 权限检查方法，false代表没有该权限，ture代表有该权限
     */
    public boolean hasPermission(String... permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(getContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 申请权限
     */
    public ZMapView requestPermissions() {
        if (!hasPermission(permissions))
            ActivityCompat.requestPermissions((Activity) getContext(), permissions, AMAP_REQUEST_CODE);
        return this;
    }

    // 获取AMap
    public AMap getAMap() {
        if (aMap == null)
            aMap = getMap();
        return aMap;
    }

    // 获取UiSettings
    public UiSettings getUiSettings() {
        if (uiSettings == null)
            uiSettings = getAMap().getUiSettings();
        return uiSettings;
    }

    /**
     * 获取mLocationOption
     */
    public AMapLocationClientOption getLocationOption() {
        // 初始化定位参数
        if (mLocationOption == null) {
            mLocationOption = new AMapLocationClientOption();
            //设置是否返回地址信息（默认返回地址信息）
            mLocationOption.setNeedAddress(true);
        }
        return mLocationOption;
    }

    /**
     * 获取mLocationClient
     */
    public AMapLocationClient getLocationClient() {
        // 初始化定位
        if (mLocationClient == null) {
            mLocationClient = new AMapLocationClient(getContext());
            //设置定位回调监听
            mLocationClient.setLocationListener(this);
        }
        return mLocationClient;
    }

    /**
     * 是否开启Marker动画
     *
     * @param isOpenMarkerAnim 默认false
     */
    public ZMapView setOpenMarkerAnim(boolean isOpenMarkerAnim) {
        this.isOpenMarkerAnim = isOpenMarkerAnim;
        if (isOpenMarkerAnim) {
            // 初始化生长效果动画
            mMarkerAnimation = new ScaleAnimation(0, 1, 0, 1);
            // 设置动画时间 单位毫秒
            mMarkerAnimation.setDuration(1000);
        }
        return this;
    }

    /**
     * 设置地图文字是否显示
     *
     * @param isShowMapText 是否显示文字，默认true
     */
    public ZMapView setShowMapText(boolean isShowMapText) {
        this.isShowMapText = isShowMapText;
        getAMap().showMapText(isShowMapText);
        return this;
    }

    /**
     * 是否展示3D楼块效果
     *
     * @param isShowBuildings 是否隐藏3D楼块，默认true
     */
    public ZMapView setShowBuildings(boolean isShowBuildings) {
        this.isShowBuildings = isShowBuildings;
        getAMap().showBuildings(isShowBuildings);
        return this;
    }

    /**
     * 控制缩放视图是否显示
     *
     * @param isShow 是否显示 默认true显示
     */
    public ZMapView zoomControlViewVisible(boolean isShow) {
        getUiSettings().setZoomControlsEnabled(isShow);
        return this;
    }

    /**
     * 设置默认定位按钮是否显示
     *
     * @param isShow 是否显示
     */
    public ZMapView setMyLocationButtonEnabled(boolean isShow) {
        getUiSettings().setMyLocationButtonEnabled(isShow);
        return this;
    }

    /**
     * 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
     *
     * @param isEnable 是否可以定位
     */
    public ZMapView setMyLocationEnabled(boolean isEnable) {
        getAMap().setMyLocationEnabled(isEnable);
        return this;
    }

    /**
     * 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
     *
     * @param locationType 定位模式 AMap.LOCATION_TYPE_LOCATE
     */
    public ZMapView setMyLocationType(int locationType) {
        getAMap().setMyLocationType(locationType);
        return this;
    }

    /**
     * 是否显示实时交通状况
     *
     * @param isShow 是否显示 默认false不显示
     */
    public ZMapView setTrafficEnabled(boolean isShow) {
        getAMap().setTrafficEnabled(isShow);
        return this;
    }

    /**
     * 设置地图模式
     *
     * @param mapType 地图模式可选类型：MAP_TYPE_NORMAL,MAP_TYPE_SATELLITE,MAP_TYPE_NIGHT
     */
    public ZMapView setMapType(int mapType) {
        getAMap().setMapType(mapType);
        return this;
    }

    /**
     * 设置当前城市
     *
     * @param currentCityName 当前城市名称或编号，默认北京
     */
    public ZMapView setCurrentCityName(String currentCityName) {
        this.mCurrentCityName = currentCityName;
        return this;
    }

    /**
     * 在地图初始化时显示指定的城市
     *
     * @param sPointy 维度
     * @param sPointx 进度
     * @param zoom    缩放比例 1-19 默认15
     */
    public ZMapView setCurrentCity(double sPointy, double sPointx, int zoom) {
        if (zoom <= 0)
            zoom = 15;
        getAMap().moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(sPointy, sPointx), zoom));
        return this;
    }

    /**
     * 在地图初始化时显示指定的城市
     *
     * @param latLng 当前城市位置
     * @param zoom   缩放比例 1-19 默认15
     */
    public ZMapView setCurrentCity(LatLng latLng, int zoom) {
        if (zoom <= 0)
            zoom = 15;
        getAMap().moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        return this;
    }

    /**
     * 在地图初始化时显示指定的城市
     *
     * @param latLonPoint 当前城市位置
     * @param zoom        缩放比例 1-19 默认15
     */
    public ZMapView setCurrentCity(LatLonPoint latLonPoint, int zoom) {
        if (zoom <= 0)
            zoom = 15;
        getAMap().moveCamera(CameraUpdateFactory.newLatLngZoom(AMapUtil.convertToLatLng(latLonPoint), zoom));
        return this;
    }

    /**
     * 设置当前查询模式
     *
     * @param searchMode 带设置值
     */
    public ZMapView setSearchMode(int searchMode) {
        this.mSearchMode = searchMode;
        return this;
    }

    /**
     * 设置当前交通类型
     *
     * @param mRouteType 带设置值 ROUTE_TYPE_BUS = 1, ROUTE_TYPE_DRIVE = 2, ROUTE_TYPE_WALK = 3, ROUTE_TYPE_RIDE = 4;
     */
    public ZMapView setRouteType(int mRouteType) {
        this.mRouteType = mRouteType;
        switch (mRouteType) {
            case ROUTE_TYPE_BUS:
                mSearchMode = RouteSearch.BUS_DEFAULT;
                break;
            case ROUTE_TYPE_DRIVE:
                mSearchMode = RouteSearch.DRIVING_SINGLE_DEFAULT;
                break;
            case ROUTE_TYPE_WALK:
                mSearchMode = RouteSearch.WALK_DEFAULT;
                break;
            case ROUTE_TYPE_RIDE:
                mSearchMode = RouteSearch.RIDING_DEFAULT;
                break;
        }
        return this;
    }

    /**
     * 设置默认点
     *
     * @param defaultPoint 默认点
     */
    public ZMapView setDefaultPoint(LatLonPoint defaultPoint) {
        return setDefaultPoint(defaultPoint, 0);
    }

    /**
     * 设置默认点
     *
     * @param defaultPoint 默认点
     * @param zoom         1-19 缩放比
     */
    public ZMapView setDefaultPoint(LatLonPoint defaultPoint, float zoom) {
        if (defaultPoint != null) {
            getAMap().clear();
            LatLng centerLatLng = AMapUtil.convertToLatLng(defaultPoint);
            if (aMapOptions != null)
                aMapOptions.camera(new CameraPosition(centerLatLng, zoom, 0, 0));
            else
                try {
                    Method getMapFragmentDelegate = MapView.class.getDeclaredMethod("getMapFragmentDelegate");
                    getMapFragmentDelegate.setAccessible(true);
                    IMapFragmentDelegate iMapFragmentDelegate = (IMapFragmentDelegate) getMapFragmentDelegate.invoke(this);

                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(AMapUtil.convertToLatLng(defaultPoint))
                            .zoom(zoom)
                            .bearing(0)
                            .tilt(0)
                            .build();

                    AMapOptions aOptions = new AMapOptions();
                    aOptions.camera(cameraPosition);
                    iMapFragmentDelegate.setOptions(aOptions);
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
        return this;
    }

    /**
     * 设置中心点
     *
     * @param startLatLng 起点
     * @param endLatLng   终点
     */
    public ZMapView setCenterPoint(LatLng startLatLng, LatLng endLatLng) {
        double centerX = (startLatLng.latitude + endLatLng.latitude) / 2;
        double centerY = (startLatLng.longitude + endLatLng.longitude) / 2;
        setCenterPoint(new LatLonPoint(centerX, centerY));
        return this;
    }

    /**
     * 设置中心点
     *
     * @param centerPoint 中心点
     */
    public ZMapView setCenterPoint(LatLonPoint centerPoint) {
        return setCenterPoint(centerPoint, 0);
    }

    /**
     * 设置中心点
     *
     * @param centerPoint 中心点
     * @param zoom        1-19 缩放比
     */
    public ZMapView setCenterPoint(LatLonPoint centerPoint, int zoom) {
        if (centerPoint != null) {
            // 参数依次是：视角调整区域的中心点坐标、
            // 希望调整到的缩放级别、
            // 俯仰角0°~45°（垂直与地图时为0）、
            // 偏航角 0~360° (正北方为0)
            CameraUpdate mCameraUpdate = CameraUpdateFactory.newCameraPosition(
                    new CameraPosition(AMapUtil.convertToLatLng(centerPoint), zoom, 0, 0));
            getAMap().animateCamera(mCameraUpdate);
        }
        return this;
    }

    /**
     * 设置地图缩放比例
     *
     * @param zoom 1-19 缩放比
     */
    public ZMapView setZoom(int zoom) {
        //设置希望展示的地图缩放级别
        CameraUpdate mCameraUpdate = CameraUpdateFactory.zoomTo(zoom);
        getAMap().animateCamera(mCameraUpdate);
        return this;
    }

    /**
     * 限制地图范围
     *
     * @param startPoint 起点
     * @param endPoint   终点
     */
    public ZMapView setMapLimits(LatLonPoint startPoint, LatLonPoint endPoint) {
        if (startPoint != null && endPoint != null) {
            LatLng startLatLng = AMapUtil.convertToLatLng(startPoint);
            LatLng endLatLng = AMapUtil.convertToLatLng(endPoint);
            // 限制地图范围
            setMapLimits(startLatLng, endLatLng);
            // 设置中心
            setCenterPoint(startLatLng, endLatLng);
        }
        return this;
    }

    /**
     * 限制地图范围
     *
     * @param startLatLng 起点
     * @param endLatLng   终点
     */
    public ZMapView setMapLimits(LatLng startLatLng, LatLng endLatLng) {
        if (startLatLng != null && endLatLng != null) {
            // 限制地图范围
            LatLngBounds latLngBounds = new LatLngBounds(startLatLng, endLatLng);
            getAMap().setMapStatusLimits(latLngBounds);
            // 设置中心
            setCenterPoint(startLatLng, endLatLng);
        }
        return this;
    }

    /**
     * 自定义Marker
     *
     * @param startPoint 起点
     * @param endPoint   终点
     * @param startIcon  起点图地址
     * @param endIcon    终点图地址
     */
    public ZMapView setFromandtoMarker(LatLonPoint startPoint, LatLonPoint endPoint,
                                       int startIcon, int endIcon) {
        if (startPoint != null
                && endPoint != null) {
            LatLng startLatLng = AMapUtil.convertToLatLng(startPoint);
            addLatLngMarker(startLatLng, startIcon);
            LatLng endLatLng = AMapUtil.convertToLatLng(endPoint);
            addLatLngMarker(endLatLng, endIcon);
            setMapLimits(startLatLng, endLatLng);
        }
        return this;
    }

    /**
     * 添加Marker集合
     *
     * @param latLonPoints 经纬度集合
     * @param icon         图标
     */
    public ZMapView addMarkers(ArrayList<LatLonPoint> latLonPoints, int icon) {
        if (latLonPoints != null) {
            for (LatLonPoint latLonPoint : latLonPoints) {
                LatLng latLng = AMapUtil.convertToLatLng(latLonPoint);
                addLatLngMarker(latLng, icon);
            }
        }
        return this;
    }

    /**
     * 添加Marker集合
     *
     * @param latLonPoints 经纬度集合
     * @param icons        图标集合
     */
    public ZMapView addMarkers(ArrayList<LatLonPoint> latLonPoints, ArrayList<Integer> icons) {
        if (latLonPoints != null
                && icons != null
                && icons.size() == latLonPoints.size()) {
            for (int i = 0; i < latLonPoints.size(); i++) {
                LatLonPoint latLng = latLonPoints.get(i);
                int icon = icons.get(i);
                addLatLngMarker(latLng, icon);
            }
        }
        return this;
    }

    /**
     * 添加Marker
     *
     * @param latLonPoint 经纬度
     * @param icon        图标
     */
    public ZMapView addLatLngMarker(LatLonPoint latLonPoint, int icon) {
        if (latLonPoint != null) {
            LatLng latLng = AMapUtil.convertToLatLng(latLonPoint);
            addLatLngMarker(latLng, icon);
        }
        return this;
    }

    /**
     * 添加Marker
     *
     * @param latLng 经纬度
     * @param icon   图标
     */
    public ZMapView addLatLngMarker(LatLng latLng, int icon) {
        if (latLng != null) {
            Marker marker = getAMap().addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.fromResource(icon)));
            if (marker != null && mMarkerAnimation != null && isOpenMarkerAnim) {
                marker.setAnimation(mMarkerAnimation);
                marker.startAnimation();
            }
        }
        return this;
    }

    /**
     * 设置marker动画
     *
     * @param markerAnimation 动画
     */
    public ZMapView setMarkerAnim(Animation markerAnimation) {
        if (markerAnimation == null)
            isOpenMarkerAnim = false;
        else {
            isOpenMarkerAnim = true;
            this.mMarkerAnimation = markerAnimation;
        }
        return this;
    }

    /**
     * 在地图上添加PoiItem，自定义气泡
     *
     * @param poiItems 待添加数据
     */
    public ZMapView addPoiItems(List<PoiItem> poiItems) {
        if (poiItems != null && poiItems.size() > 0) {
            getAMap().clear();// 清理之前的图标
            if (mPoiOverlay == null)
                mPoiOverlay = new ViewPoiOverlay(getContext(), getAMap(), poiItems);
            mPoiOverlay.removeFromMap();
            mPoiOverlay.addToMap();
            mPoiOverlay.zoomToSpan();
        }
        return this;
    }

    /**
     * 设置poiOverlay样式
     *
     * @param poiOverlay 待设置对象
     */
    public ZMapView setPoiOverlay(PoiOverlay poiOverlay) {
        if (poiOverlay != null) {
            this.mPoiOverlay = poiOverlay;
            poiOverlay.setAMap(getAMap());
        }
        return this;
    }

    /**
     * 在地图上添加PoiItem，自定义气泡
     *
     * @param poiItems 待添加数据
     */
    public ZMapView addPoiItems(List<PoiItem> poiItems, PoiOverlay poiOverlay) {
        if (poiItems != null && poiItems.size() > 0 && poiOverlay != null) {
            getAMap().clear();// 清理之前的图标
            poiOverlay.setAMap(getAMap());
            poiOverlay.setPois(poiItems);
            poiOverlay.removeFromMap();
            poiOverlay.addToMap();
            poiOverlay.zoomToSpan();
        }
        return this;
    }

    /**
     * 启动定位
     *
     * @param aMapLocationMode Battery_Saving为低功耗模式，Device_Sensors是仅设备模式，Hight_Accuracy高精准
     */
    public ZMapView startLocation(AMapLocationClientOption.AMapLocationMode aMapLocationMode) {
        if (aMapLocationMode != null) {
            if (mSensorHelper == null) {
                mSensorHelper = new SensorEventHelper(getContext());
                mSensorHelper.registerSensorListener();
            }
            // 初始化定位
            mLocationClient = getLocationClient();
            // 初始化定位参数
            mLocationOption = getLocationOption();
            // 设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
            mLocationOption.setLocationMode(aMapLocationMode);
            // 给定位客户端对象设置定位参数
            mLocationClient.setLocationOption(mLocationOption);
            // 启动定位
            mLocationClient.startLocation();
            // 定位监听
            if (zLocationListener != null)
                zLocationListener.onLocationStart();
        }
        return this;
    }

    /**
     * 启动定位
     */
    public ZMapView startLocation() {
        return startLocation(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
    }

    /**
     * 分页开始进行poi搜索 - 下一页
     *
     * @param keywords 关键字
     */
    public ZMapView poiSearchByPageNext(String keywords) {
        poiSearchCurrentPage++;
        return poiSearch(keywords, "", 10);
    }

    /**
     * 分页开始进行poi搜索
     *
     * @param keywords 关键字
     */
    public ZMapView poiSearchByPage(String keywords) {
        return poiSearch(keywords, "", 10);
    }

    /**
     * 分页开始进行poi搜索
     *
     * @param keywords 关键字
     * @param type     poi搜索类型
     */
    public ZMapView poiSearchByPageNext(String keywords, String type) {
        poiSearchCurrentPage++;
        return poiSearch(keywords, type, 10);
    }

    /**
     * 分页开始进行poi搜索
     *
     * @param keywords 关键字
     * @param type     poi搜索类型
     */
    public ZMapView poiSearchByPage(String keywords, String type) {
        return poiSearch(keywords, type, 10);
    }

    /**
     * 分页开始进行poi搜索
     *
     * @param keywords 关键字
     * @param pageSize 每页显示条数
     */
    public ZMapView poiSearchByPageNext(String keywords, int pageSize) {
        poiSearchCurrentPage++;
        return poiSearch(keywords, "", pageSize);
    }

    /**
     * 分页开始进行poi搜索
     *
     * @param keywords 关键字
     * @param pageSize 每页显示条数
     */
    public ZMapView poiSearchByPage(String keywords, int pageSize) {
        return poiSearch(keywords, "", pageSize);
    }

    /**
     * 分页开始进行poi搜索
     *
     * @param keywords 关键字
     * @param type     poi搜索类型
     * @param pageSize 每页显示条数
     */
    public ZMapView poiSearch(String keywords, String type, int pageSize) {
        if (TextUtils.isEmpty(keywords)) {
            if (zPoiSearchListener != null)
                zPoiSearchListener.onPoiSearchFail("关键词未输入！");
            return this;
        }
        if (zPoiSearchListener != null)
            zPoiSearchListener.onPoiSearchStart();
        if (mPoiQuery == null)
            // 第一个参数表示搜索字符串，
            // 第二个参数表示poi搜索类型，
            // 第三个参数表示poi搜索区域（空字符串代表全国）
            mPoiQuery = new PoiSearch.Query(keywords, type, mCurrentCityName);
        // 设置每页最多返回多少条poiItem
        mPoiQuery.setPageSize(pageSize);
        // 设置查第一页
        if (poiSearchCurrentPage <= 0)
            poiSearchCurrentPage = 1;
        mPoiQuery.setPageNum(poiSearchCurrentPage);
        if (mPoiSearch == null)
            mPoiSearch = new PoiSearch(getContext(), mPoiQuery);
        mPoiSearch.setOnPoiSearchListener(this);
        mPoiSearch.searchPOIAsyn();
        return this;
    }

    /**
     * 开始搜索路径规划方案
     *
     * @param startPoint 起点坐标
     * @param endPoint   终点坐标
     */
    public ZMapView searchRouteResult(LatLonPoint startPoint, LatLonPoint endPoint) {
        if (startPoint == null) {
            if (zRouteSearchListener != null)
                zRouteSearchListener.onRouteSearchFail("起点未设置");
            return this;
        }
        if (endPoint == null) {
            if (zRouteSearchListener != null)
                zRouteSearchListener.onRouteSearchFail("终点未设置");
            return this;
        }
        if (zRouteSearchListener != null)
            zRouteSearchListener.onRouteSearchStart();
        if (mRouteSearch == null)
            mRouteSearch = new RouteSearch(getContext());
        mRouteSearch.setRouteSearchListener(this);
        RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(startPoint, endPoint);
        if (mRouteType == ROUTE_TYPE_BUS) {// 公交路径规划
            // 第一个参数表示路径规划的起点和终点，
            // 第二个参数表示公交查询模式，
            // 第三个参数表示公交查询城市区号，
            // 第四个参数表示是否计算夜班车，0表示不计算
            RouteSearch.BusRouteQuery query = new RouteSearch.BusRouteQuery(fromAndTo, mSearchMode,
                    mCurrentCityName, 0);
            mRouteSearch.calculateBusRouteAsyn(query);// 异步路径规划公交模式查询
        } else if (mRouteType == ROUTE_TYPE_DRIVE) {// 驾车路径规划
            // 第一个参数表示路径规划的起点和终点，
            // 第二个参数表示驾车模式，
            // 第三个参数表示途经点，
            // 第四个参数表示避让区域，
            // 第五个参数表示避让道路
            RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo, mSearchMode, null,
                    null, "");
            mRouteSearch.calculateDriveRouteAsyn(query);// 异步路径规划驾车模式查询
        } else if (mRouteType == ROUTE_TYPE_WALK) {// 步行路径规划
            RouteSearch.WalkRouteQuery query = new RouteSearch.WalkRouteQuery(fromAndTo);
            mRouteSearch.calculateWalkRouteAsyn(query);// 异步路径规划步行模式查询
        } else if (mRouteType == ROUTE_TYPE_RIDE) {// 骑行路径规划
            RouteSearch.RideRouteQuery query = new RouteSearch.RideRouteQuery(fromAndTo);
            mRouteSearch.calculateRideRouteAsyn(query);// 异步路径规划骑行模式查询
        }
        return this;
    }

    // 驾车规划结果
    @Override
    public void onDriveRouteSearched(DriveRouteResult result, int errorCode) {
        if (zRouteSearchListener != null)
            zRouteSearchListener.onRouteSearchComplete();
        getAMap().clear();// 清理地图上的所有覆盖物
        if (errorCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null
                    && result.getPaths() != null
                    && result.getPaths().size() > 0) {
                if (zRouteSearchListener != null)
                    zRouteSearchListener.onDriveNext(result);
                else {
                    DrivePath drivePath = result.getPaths().get(0);
                    DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(
                            getContext(),
                            getAMap(),
                            drivePath,
                            result.getStartPos(),
                            result.getTargetPos(),
                            null
                    );
                    drivingRouteOverlay.setNodeIconVisibility(false);// 设置节点marker是否显示
                    drivingRouteOverlay.setIsColorfulline(true);// 是否用颜色展示交通拥堵情况，默认true
                    drivingRouteOverlay.removeFromMap();
                    drivingRouteOverlay.addToMap();
                    drivingRouteOverlay.zoomToSpan();
                }

                if (zRouteSearchListener != null)
                    zRouteSearchListener.onRouteSearchSuccess();
            } else {
                if (zRouteSearchListener != null)
                    zRouteSearchListener.onRouteSearchFail("未获取到任何数据！");
            }
        } else {
            if (zRouteSearchListener != null)
                zRouteSearchListener.onRouteSearchFail("路线规划失败：" + errorCode);
        }
    }

    // 步行规划结果
    @Override
    public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int errorCode) {
        if (zRouteSearchListener != null)
            zRouteSearchListener.onRouteSearchComplete();
        getAMap().clear();// 清理地图上的所有覆盖物
        if (errorCode == AMapException.CODE_AMAP_SUCCESS) {
            if (walkRouteResult != null
                    && walkRouteResult.getPaths() != null
                    && walkRouteResult.getPaths().size() > 0) {
                if (zRouteSearchListener != null)
                    zRouteSearchListener.onWalkNext(walkRouteResult);
                else {
                    WalkPath walkPath = walkRouteResult.getPaths().get(0);
                    WalkRouteOverlay walkRouteOverlay = new WalkRouteOverlay(
                            getContext(),
                            getAMap(),
                            walkPath,
                            walkRouteResult.getStartPos(),
                            walkRouteResult.getTargetPos()
                    );
                    walkRouteOverlay.addToMap();
                    walkRouteOverlay.zoomToSpan();
                }

                if (zRouteSearchListener != null)
                    zRouteSearchListener.onRouteSearchSuccess();
            } else {
                if (zRouteSearchListener != null)
                    zRouteSearchListener.onRouteSearchFail("未获取到任何数据！");
            }
        } else {
            if (zRouteSearchListener != null)
                zRouteSearchListener.onRouteSearchFail("路线规划失败：" + errorCode);
        }
    }

    // 骑行规划结果
    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int errorCode) {
        if (zRouteSearchListener != null)
            zRouteSearchListener.onRouteSearchComplete();
        getAMap().clear();// 清理地图上的所有覆盖物
        if (errorCode == AMapException.CODE_AMAP_SUCCESS) {
            if (rideRouteResult != null
                    && rideRouteResult.getPaths() != null
                    && rideRouteResult.getPaths().size() > 0) {
                if (zRouteSearchListener != null)
                    zRouteSearchListener.onRideNext(rideRouteResult);
                else {
                    RidePath ridePath = rideRouteResult.getPaths().get(0);
                    RideRouteOverlay rideRouteOverlay = new RideRouteOverlay(
                            getContext(),
                            getAMap(),
                            ridePath,
                            rideRouteResult.getStartPos(),
                            rideRouteResult.getTargetPos());
                    rideRouteOverlay.removeFromMap();
                    rideRouteOverlay.addToMap();
                    rideRouteOverlay.zoomToSpan();
                }

                if (zRouteSearchListener != null)
                    zRouteSearchListener.onRouteSearchSuccess();
            } else {
                if (zRouteSearchListener != null)
                    zRouteSearchListener.onRouteSearchFail("未获取到任何数据！");
            }
        } else {
            if (zRouteSearchListener != null)
                zRouteSearchListener.onRouteSearchFail("路线规划失败：" + errorCode);
        }
    }

    // 公交车规划结果
    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int errorCode) {
        if (zRouteSearchListener != null)
            zRouteSearchListener.onRouteSearchComplete();
        getAMap().clear();// 清理地图上的所有覆盖物
        if (errorCode == 1000) {
            if (busRouteResult != null
                    && busRouteResult.getPaths() != null
                    && busRouteResult.getPaths().size() > 0) {
                if (zRouteSearchListener != null)
                    zRouteSearchListener.onBusNext(busRouteResult);
                else {
                    BusPath buspath = busRouteResult.getPaths().get(0);
                    BusRouteOverlay busrouteOverlay = new BusRouteOverlay(
                            getContext(),
                            getAMap(),
                            buspath,
                            busRouteResult.getStartPos(),
                            busRouteResult.getTargetPos());
                    busrouteOverlay.removeFromMap();
                    busrouteOverlay.addToMap();
                    busrouteOverlay.zoomToSpan();
                }

                if (zRouteSearchListener != null)
                    zRouteSearchListener.onRouteSearchSuccess();
            } else {
                if (zRouteSearchListener != null)
                    zRouteSearchListener.onRouteSearchFail("未获取到任何数据！");
            }
        } else {
            if (zRouteSearchListener != null)
                zRouteSearchListener.onRouteSearchFail("路线规划失败：" + errorCode);
        }
    }

    /**
     * 检测不明异常
     */
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        if (zRouteSearchListener != null)
            zRouteSearchListener.onRouteSearchError(e);
        if (zPoiSearchListener != null)
            zPoiSearchListener.onPoiSearchError(e);
        if (zLocationListener != null)
            zLocationListener.onLocationError(e);
        if (zMapLoadedListener != null)
            zMapLoadedListener.onMapError(e);
    }

    // Map加载完成
    @Override
    public void onMapLoaded() {
        getAMap().showMapText(isShowMapText);
        getAMap().showBuildings(isShowBuildings);
        if (zMapLoadedListener != null)
            zMapLoadedListener.onMapLoaded();
    }

    // poi搜索结果
    @Override
    public void onPoiSearched(PoiResult poiResult, int errorCode) {
        if (zPoiSearchListener != null)
            zPoiSearchListener.onPoiSearchComplete();
        if (errorCode == 1000) {
            if (poiResult != null && poiResult.getQuery() != null) {// 搜索poi的结果
                if (poiResult.getQuery().equals(mPoiQuery)) {// 是否是同一条
                    // 取得搜索到的poiItems有多少页
                    List<PoiItem> poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
                    List<SuggestionCity> suggestionCities = poiResult.getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息
                    if (zPoiSearchListener != null)
                        zPoiSearchListener.onPoiSearchNext(poiItems, suggestionCities);
                    else {
                        if (poiItems != null && poiItems.size() > 0) {
                            getAMap().clear();// 清理之前的图标
                            PoiOverlay poiOverlay = new PoiOverlay(getAMap(), poiItems);
                            poiOverlay.removeFromMap();
                            poiOverlay.addToMap();
                            poiOverlay.zoomToSpan();
                        } else {
                            if (zPoiSearchListener != null)
                                zPoiSearchListener.onPoiSearchFail("未查询到任何数据");
                        }
                    }
                    if (zPoiSearchListener != null)
                        zPoiSearchListener.onPoiSearchSuccess();
                }
            } else {
                if (zPoiSearchListener != null)
                    zPoiSearchListener.onPoiSearchFail("未查询到任何数据");
            }
        } else {
            if (zPoiSearchListener != null)
                zPoiSearchListener.onPoiSearchFail("POI搜索失败：" + errorCode);
        }
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }

    // 定位结果
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (zLocationListener != null)
            zLocationListener.onLocationComplete();
        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                // 定位成功回调信息，设置相关消息
                LocationData locationData = new LocationData();
                locationData.setPointy(amapLocation.getLatitude());// 获取纬度
                locationData.setPointx(amapLocation.getLongitude());// 获取经度
                locationData.setCurrentAddress(amapLocation.getAddress());// 地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                locationData.setCurrentCountry(amapLocation.getCountry());// 国家信息
                locationData.setCurrentProv(amapLocation.getProvince());// 省信息
                locationData.setCurrentCity(amapLocation.getCity());// 城市信息
                locationData.setCurrentDistrict(amapLocation.getDistrict());// 城区信息
                locationData.setCurrentStreet(amapLocation.getStreet());// 街道信息
                locationData.setCurrentStreetNum(amapLocation.getStreetNum());// 街道门牌号信息
                locationData.setCurrentCityCode(amapLocation.getCityCode());// 城市编码
                locationData.setCurrentAdCode(amapLocation.getAdCode());// 地区编码
                locationData.setCurrentAoiName(amapLocation.getAoiName());// 获取当前定位点的AOI信息

                if (zLocationListener != null)
                    zLocationListener.onLocationNext(locationData);
                else {
                    LatLng location = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());
                    if (!mFirstLocationFix) {
                        mFirstLocationFix = true;
                        addLatLngCircle(location, amapLocation.getAccuracy());// 添加定位精度圆
                        addLatLngMarker(location);// 添加定位图标
                        mSensorHelper.setCurrentMarker(mLocationMarker);// 定位图标旋转
                        getAMap().moveCamera(CameraUpdateFactory.newLatLngZoom(location, 17));
                    } else {
                        mLocationCircle.setCenter(location);
                        mLocationCircle.setRadius(amapLocation.getAccuracy());
                        mLocationMarker.setPosition(location);
                        getAMap().moveCamera(CameraUpdateFactory.changeLatLng(location));
                    }
                }

                if (zLocationListener != null)
                    zLocationListener.onLocationSuccess();
            } else {
                // 显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                if (zLocationListener != null)
                    zLocationListener.onLocationFail("location Error, ErrCode:"
                            + amapLocation.getErrorCode() + ", errInfo:"
                            + amapLocation.getErrorInfo());
            }
            // 关闭定位
            mLocationClient.stopLocation();
        } else {
            if (zLocationListener != null)
                zLocationListener.onLocationFail("定位失败！");
        }
    }

    /**
     * 添加圆圈
     *
     * @param latlng 位置
     * @param radius 半径
     */
    private void addLatLngCircle(LatLng latlng, double radius) {
        CircleOptions options = new CircleOptions();
        options.strokeWidth(1f)
                .fillColor(FILL_COLOR)
                .strokeColor(STROKE_COLOR)
                .center(latlng)
                .radius(radius);
        mLocationCircle = getAMap().addCircle(options);
    }

    /**
     * 添加定位Marker
     *
     * @param latlng 位置信息
     */
    private void addLatLngMarker(LatLng latlng) {
        if (mLocationMarker != null)
            return;
        MarkerOptions options = new MarkerOptions();
        options.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(this.getResources(), R.mipmap.navi_map_gps_locked)))
                .anchor(0.5f, 0.5f)
                .position(latlng);
        mLocationMarker = getAMap().addMarker(options);
    }

    /**
     * 检查GPS是否打开
     */
    public boolean checkGpsIsOpen() {
        LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * 打开GPS设置
     */
    public ZMapView openGPSSetting() {
        if (!checkGpsIsOpen()) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            getContext().startActivity(intent);
        }
        return this;
    }

}
