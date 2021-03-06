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
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.amap.api.fence.GeoFence;
import com.amap.api.fence.GeoFenceClient;
import com.amap.api.fence.GeoFenceListener;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.DPoint;
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
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.maps.model.animation.Animation;
import com.amap.api.maps.model.animation.ScaleAnimation;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.route.BusPath;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DistanceItem;
import com.amap.api.services.route.DistanceResult;
import com.amap.api.services.route.DistanceSearch;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RidePath;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.TruckPath;
import com.amap.api.services.route.TruckRouteRestult;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkRouteResult;
import com.autonavi.amap.mapcore.interfaces.IMapFragmentDelegate;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cc.ibooker.amaplib.dto.LocationData;
import cc.ibooker.amaplib.listeners.ZDistanceSearchListener;
import cc.ibooker.amaplib.listeners.ZGeoFenceListener;
import cc.ibooker.amaplib.listeners.ZGeocodeSearchListener;
import cc.ibooker.amaplib.listeners.ZLocationListener;
import cc.ibooker.amaplib.listeners.ZMapClickListener;
import cc.ibooker.amaplib.listeners.ZMapLoadedListener;
import cc.ibooker.amaplib.listeners.ZMapLongClickListener;
import cc.ibooker.amaplib.listeners.ZPoiSearchListener;
import cc.ibooker.amaplib.listeners.ZRouteSearchListener;
import cc.ibooker.amaplib.overlays.BusRouteOverlay;
import cc.ibooker.amaplib.overlays.DrivingRouteOverlay;
import cc.ibooker.amaplib.overlays.PoiOverlay;
import cc.ibooker.amaplib.overlays.RideRouteOverlay;
import cc.ibooker.amaplib.overlays.TruckRouteColorfulOverLay;
import cc.ibooker.amaplib.overlays.ViewPoiOverlay;
import cc.ibooker.amaplib.overlays.WalkRouteOverlay;
import cc.ibooker.amaplib.util.AMapUtil;

import static com.amap.api.fence.GeoFenceClient.GEOFENCE_IN;
import static com.amap.api.fence.GeoFenceClient.GEOFENCE_OUT;
import static com.amap.api.fence.GeoFenceClient.GEOFENCE_STAYED;
import static com.amap.api.services.route.RouteSearch.TRUCK_SIZE_MEDIUM;

/**
 * 自定义地图
 * 功能一：显示地图
 * 功能二：路线规划
 * 功能三：POI搜索
 * 功能四：距离搜索-计算
 * 功能五：逆向地址查询
 * 功能六：定位
 * 功能七：地图点击、长按监听
 * 功能八：地址围栏
 * https://lbs.amap.com/dev/demo/ride-route-plan#Android
 *
 * @author 邹峰立
 */
public class ZMapView extends MapView implements
        Thread.UncaughtExceptionHandler,
        RouteSearch.OnRouteSearchListener,
        AMap.OnMapLoadedListener,
        AMap.OnMapClickListener,
        AMap.OnMapLongClickListener,
        PoiSearch.OnPoiSearchListener,
        AMapLocationListener,
        GeocodeSearch.OnGeocodeSearchListener,
        RouteSearch.OnTruckRouteSearchListener,
        GeoFenceListener {
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
    private boolean isOnceLocation = false;// 是否为单次定位
    private long interval = 2000;// 定位间隔,单位毫秒,默认为2000ms，最低1000ms
    private long httpTimeOut = 30000;// 超时时长
    private int defaultZoom = 17;// 默认地址缩放级别 1 - 19

    public static final int ROUTE_TYPE_BUS = 1,
            ROUTE_TYPE_DRIVE = 2,
            ROUTE_TYPE_WALK = 3,
            ROUTE_TYPE_RIDE = 4,
            ROUTE_TYPE_TRUCK = 5;
    private RouteSearch mRouteSearch;
    private String mCurrentCityName = "北京";// 当前城市名称
    private int mRouteType = ROUTE_TYPE_DRIVE;// 交通类型
    private int mTruckSize = TRUCK_SIZE_MEDIUM;// 货车大小-默认中型
    private int mSearchMode = RouteSearch.DRIVING_SINGLE_DEFAULT;// 当前查询模式
    private int mSearchRouteStartIcon = R.drawable.amap_start;
    private int mSearchRouteEndIcon = R.drawable.amap_end;

    // 轨迹
    private Polyline polyline;

    private PoiSearch.Query mPoiQuery;// Poi查询条件类
    private PoiSearch mPoiSearch;// POI搜索
    private int poiSearchCurrentPage = 0;

    private Marker marker;// 地图Marker

    // 地址围栏
    private GeoFenceClient mGeoFenceClient;
    private ZGeoFenceListener zGeoFenceListener;

    public ZMapView setzGeoFenceListener(ZGeoFenceListener zGeoFenceListener) {
        this.zGeoFenceListener = zGeoFenceListener;
        return this;
    }

    // 逆向地址查询
    private GeocodeSearch mGeocodeSearch;

    // 逆向地址查询监听
    private ZGeocodeSearchListener zGeocodeSearchListener;

    public ZMapView setGeocodeSearchListener(ZGeocodeSearchListener geocodeSearchListener) {
        this.zGeocodeSearchListener = geocodeSearchListener;
        return this;
    }

    // 定位
    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;
    private SensorEventHelper mSensorHelper;
    private final int STROKE_COLOR = Color.argb(180, 3, 145, 255);
    private final int FILL_COLOR = Color.argb(10, 0, 0, 180);
    private boolean mFirstLocationFix = false;
    private Marker mLocationMarker;
    private Circle mLocationCircle;
    private boolean isShieldingDefaultLocationOper = false;// 是否屏蔽默认地位操作

    // 计算距离
    private DistanceSearch distanceSearch;

    // 计算距离监听
    private ZDistanceSearchListener zDistanceSearchListener;

    public ZMapView setDistanceSearchListener(ZDistanceSearchListener zDistanceSearchListener) {
        this.zDistanceSearchListener = zDistanceSearchListener;
        return this;
    }

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

    // 地图点击事件监听
    private ZMapClickListener zMapClickListener;

    public ZMapView setzMapClickListener(ZMapClickListener zMapClickListener) {
        this.zMapClickListener = zMapClickListener;
        return this;
    }

    // 地图长按事件监听
    private ZMapLongClickListener zMapLongClickListener;

    public ZMapView setzMapLongClickListener(ZMapLongClickListener zMapLongClickListener) {
        this.zMapLongClickListener = zMapLongClickListener;
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
        aMap.setOnMapClickListener(this);
        aMap.setOnMapLongClickListener(this);
        requestPermissions();
        setHttpTimeOut(httpTimeOut);
    }

    /**
     * 获取地址围栏
     */
    public GeoFenceClient getGeoFenceClient() {
        if (mGeoFenceClient == null) {
            mGeoFenceClient = new GeoFenceClient(getContext());
            mGeoFenceClient.setActivateAction(GEOFENCE_IN | GEOFENCE_OUT | GEOFENCE_STAYED);
            mGeoFenceClient.setGeoFenceListener(this);
        }
        return mGeoFenceClient;
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
        if (mGeocodeSearch != null)
            mGeocodeSearch = null;
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

    // 获取待检测的权限
    public String[] getPermissions() {
        return permissions;
    }

    // 获取权限返回请求码
    public int getAMAP_REQUEST_CODE() {
        return AMAP_REQUEST_CODE;
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
     * 设置POI搜索当前页
     *
     * @param poiSearchCurrentPage 待设置页面
     */
    public ZMapView setPoiSearchCurrentPage(int poiSearchCurrentPage) {
        this.poiSearchCurrentPage = poiSearchCurrentPage;
        return this;
    }

    /**
     * 获取POI搜索当前页面
     */
    public int getPoiSearchCurrentPage() {
        return poiSearchCurrentPage;
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
     * 是否禁止默认定位结果操作
     *
     * @param shieldingDefaultLocationOper 是否禁止
     */
    public ZMapView setShieldingDefaultLocationOper(boolean shieldingDefaultLocationOper) {
        isShieldingDefaultLocationOper = shieldingDefaultLocationOper;
        return this;
    }

    /**
     * 设置默认地址缩放级别
     *
     * @param defaultZoom 待设置值
     */
    public ZMapView setDefaultZoom(int defaultZoom) {
        this.defaultZoom = defaultZoom;
        return this;
    }

    /**
     * 设置是否单次定位
     *
     * @param isOnceLocation 单次定位
     */
    public ZMapView setOnceLocation(boolean isOnceLocation) {
        this.isOnceLocation = isOnceLocation;
        if (getLocationOption() != null)
            getLocationOption().setOnceLocation(isOnceLocation);
        return this;
    }

    /**
     * 设置定位间隔
     *
     * @param interval 定位间隔
     */
    public ZMapView setInterval(long interval) {
        this.interval = interval;
        if (getLocationOption() != null)
            getLocationOption().setInterval(interval);
        return this;
    }

    /**
     * 设置定位超时时长
     *
     * @param httpTimeOut 超时时长
     */
    public ZMapView setHttpTimeOut(long httpTimeOut) {
        this.httpTimeOut = httpTimeOut;
        if (getLocationOption() != null)
            getLocationOption().setHttpTimeOut(httpTimeOut);
        return this;
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
    public ZMapView setCurrentCityName(@NonNull String currentCityName) {
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
    public ZMapView setCurrentCity(@NonNull LatLng latLng, int zoom) {
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
    public ZMapView setCurrentCity(@NonNull LatLonPoint latLonPoint, int zoom) {
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
     * 设置货车大小
     *
     * @param truckSize TRUCK_SIZE_MINI = 1
     *                  TRUCK_SIZE_LIGHT = 2
     *                  TRUCK_SIZE_MEDIUM = 3
     *                  TRUCK_SIZE_HEAVY = 4
     */
    public ZMapView setTruckSize(int truckSize) {
        this.mTruckSize = truckSize;
        return this;
    }

    /**
     * 设置当前交通类型
     *
     * @param mRouteType 带设置值 ROUTE_TYPE_BUS = 1,
     *                   ROUTE_TYPE_DRIVE = 2,
     *                   ROUTE_TYPE_WALK = 3,
     *                   ROUTE_TYPE_RIDE = 4,
     *                   ROUTE_TYPE_TRUCK = 5
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
                mSearchMode = RouteSearch.DRIVING_NORMAL_CAR;
                break;
            case ROUTE_TYPE_TRUCK:// 默认是省钱、不走高速
                mSearchMode = RouteSearch.TRUCK_SAVE_MONEY_NO_HIGHWAY;
                break;
        }
        return this;
    }

    /**
     * 设置默认点
     *
     * @param defaultPoint 默认点
     */
    public ZMapView setDefaultPoint(@NonNull LatLonPoint defaultPoint) {
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
    public ZMapView setCenterPoint(@NonNull LatLng startLatLng, @NonNull LatLng endLatLng) {
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
    public ZMapView setCenterPoint(@NonNull LatLonPoint centerPoint) {
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
    public ZMapView addPoiItems(@NonNull List<PoiItem> poiItems) {
        if (poiItems.size() > 0) {
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
    public ZMapView setPoiOverlay(@NonNull PoiOverlay poiOverlay) {
        this.mPoiOverlay = poiOverlay;
        poiOverlay.setAMap(getAMap());
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
     * @param locationListener 定位监听
     */
    public ZMapView startLocation(@NonNull AMapLocationClientOption.AMapLocationMode aMapLocationMode,
                                  @NonNull ZLocationListener locationListener) {
        this.zLocationListener = locationListener;
        return startLocation(aMapLocationMode);
    }

    /**
     * 启动定位
     *
     * @param aMapLocationMode Battery_Saving为低功耗模式，Device_Sensors是仅设备模式，Hight_Accuracy高精准
     */
    public ZMapView startLocation(@NonNull AMapLocationClientOption.AMapLocationMode aMapLocationMode) {
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
        return this;
    }

    /**
     * 启动定位
     *
     * @param locationListener 定位监听
     */
    public ZMapView startLocation(@NonNull ZLocationListener locationListener) {
        this.zLocationListener = locationListener;
        return startLocation();
    }

    /**
     * 启动定位
     */
    public ZMapView startLocation() {
        return startLocation(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
    }

    /**
     * 分页开始进行poi搜索 - 下一页 - 当前城市
     *
     * @param keywords          关键字
     * @param poiSearchListener POI搜索监听
     */
    public ZMapView poiSearchByPageNext(@NonNull String keywords,
                                        @NonNull ZPoiSearchListener poiSearchListener) {
        this.zPoiSearchListener = poiSearchListener;
        return poiSearchByPageNext(keywords);
    }

    /**
     * 分页开始进行poi搜索 - 下一页 - 当前城市
     *
     * @param keywords 关键字
     */
    public ZMapView poiSearchByPageNext(@NonNull String keywords) {
        poiSearchCurrentPage++;
        return poiSearch(keywords, "", 10);
    }

    /**
     * 分页开始进行poi搜索 - 当前城市
     *
     * @param keywords          关键字
     * @param poiSearchListener POI搜索监听
     */
    public ZMapView poiSearchByPage(@NonNull String keywords, @NonNull ZPoiSearchListener poiSearchListener) {
        this.zPoiSearchListener = poiSearchListener;
        return poiSearchByPage(keywords);
    }

    /**
     * 分页开始进行poi搜索 - 当前城市
     *
     * @param keywords 关键字
     */
    public ZMapView poiSearchByPage(@NonNull String keywords) {
        return poiSearch(keywords, "", 10);
    }

    /**
     * 分页开始进行poi搜索 - 当前城市
     *
     * @param keywords          关键字
     * @param type              poi搜索类型
     * @param poiSearchListener POI搜索监听
     */
    public ZMapView poiSearchByPageNext(@NonNull String keywords, @NonNull String type,
                                        @NonNull ZPoiSearchListener poiSearchListener) {
        this.zPoiSearchListener = poiSearchListener;
        return poiSearchByPageNext(keywords, type);
    }

    /**
     * 分页开始进行poi搜索 - 当前城市
     *
     * @param keywords 关键字
     * @param type     poi搜索类型
     */
    public ZMapView poiSearchByPageNext(@NonNull String keywords, @NonNull String type) {
        poiSearchCurrentPage++;
        return poiSearch(keywords, type, 10);
    }

    /**
     * 分页开始进行poi搜索 - 当前城市
     *
     * @param keywords          关键字
     * @param type              poi搜索类型
     * @param poiSearchListener POI搜索监听
     */
    public ZMapView poiSearchByPage(@NonNull String keywords, @NonNull String type,
                                    @NonNull ZPoiSearchListener poiSearchListener) {
        this.zPoiSearchListener = poiSearchListener;
        return poiSearchByPage(keywords, type);
    }

    /**
     * 分页开始进行poi搜索 - 当前城市
     *
     * @param keywords 关键字
     * @param type     poi搜索类型
     */
    public ZMapView poiSearchByPage(@NonNull String keywords, @NonNull String type) {
        return poiSearch(keywords, type, 10);
    }

    /**
     * 分页开始进行poi搜索 - 当前城市
     *
     * @param keywords          关键字
     * @param pageSize          每页显示条数
     * @param poiSearchListener POI搜索监听
     */
    public ZMapView poiSearchByPageNext(@NonNull String keywords, int pageSize,
                                        @NonNull ZPoiSearchListener poiSearchListener) {
        this.zPoiSearchListener = poiSearchListener;
        return poiSearchByPageNext(keywords, pageSize);
    }

    /**
     * 分页开始进行poi搜索 - 当前城市
     *
     * @param keywords 关键字
     * @param pageSize 每页显示条数
     */
    public ZMapView poiSearchByPageNext(@NonNull String keywords, int pageSize) {
        poiSearchCurrentPage++;
        return poiSearch(keywords, "", pageSize);
    }

    /**
     * 分页开始进行poi搜索 - 当前城市
     *
     * @param keywords          关键字
     * @param pageSize          每页显示条数
     * @param poiSearchListener POI搜索监听
     */
    public ZMapView poiSearchByPage(@NonNull String keywords, int pageSize,
                                    @NonNull ZPoiSearchListener poiSearchListener) {
        this.zPoiSearchListener = poiSearchListener;
        return poiSearchByPage(keywords, pageSize);
    }

    /**
     * 分页开始进行poi搜索 - 当前城市
     *
     * @param keywords 关键字
     * @param pageSize 每页显示条数
     */
    public ZMapView poiSearchByPage(@NonNull String keywords, int pageSize) {
        return poiSearch(keywords, "", pageSize);
    }

    /**
     * 分页开始进行poi搜索 - 当前城市
     *
     * @param keywords          关键字
     * @param type              poi搜索类型
     * @param pageSize          每页显示条数
     * @param poiSearchListener POI搜索监听
     */
    public ZMapView poiSearch(String keywords, @NonNull String type, int pageSize,
                              @NonNull ZPoiSearchListener poiSearchListener) {
        this.zPoiSearchListener = poiSearchListener;
        return poiSearch(keywords, type, pageSize);
    }

    /**
     * 分页开始进行poi搜索 - 当前城市
     *
     * @param keywords 关键字
     * @param type     poi搜索类型
     * @param pageSize 每页显示条数
     */
    public ZMapView poiSearch(String keywords, String type, int pageSize) {
        return poiSearch(keywords, type, mCurrentCityName, pageSize);
    }

    /**
     * 分页开始进行poi搜索
     *
     * @param keywords 关键字
     * @param type     poi搜索类型
     * @param city     城市，名称或code
     * @param pageSize 每页显示条数
     */
    public ZMapView poiSearchNext(String keywords, String type, String city, int pageSize) {
        poiSearchCurrentPage++;
        return poiSearch(keywords, type, city, pageSize);
    }

    /**
     * 分页开始进行poi搜索
     *
     * @param keywords 关键字
     * @param type     poi搜索类型
     * @param city     城市，名称或code
     * @param pageSize 每页显示条数
     */
    public ZMapView poiSearch(String keywords, String type, String city, int pageSize) {
        if (TextUtils.isEmpty(keywords)) {
            if (zPoiSearchListener != null)
                zPoiSearchListener.onPoiSearchFail("关键词未输入！");
            return this;
        }
        if (zPoiSearchListener != null)
            zPoiSearchListener.onPoiSearchStart();
        // 第一个参数表示搜索字符串，
        // 第二个参数表示poi搜索类型，
        // 第三个参数表示poi搜索区域（空字符串代表全国）
        mPoiQuery = new PoiSearch.Query(keywords, type, city);
        // 设置每页最多返回多少条poiItem
        mPoiQuery.setPageSize(pageSize);
        // 设置查第一页
        if (poiSearchCurrentPage <= 0)
            poiSearchCurrentPage = 0;
        mPoiQuery.setPageNum(poiSearchCurrentPage);
        mPoiSearch = new PoiSearch(getContext(), mPoiQuery);
        mPoiSearch.setOnPoiSearchListener(this);
        mPoiSearch.searchPOIAsyn();
        return this;
    }

    /**
     * 周边检索POI
     *
     * @param keywords  关键字
     * @param type      poi搜索类型
     * @param city      城市，名称或code
     * @param pageSize  每页显示条数
     * @param latitude  经度
     * @param longitude 纬度
     * @param range     范围
     */
    public ZMapView poiSearchNext(String keywords, String type, String city, int pageSize,
                                  double latitude, double longitude, int range) {
        poiSearchCurrentPage++;
        return poiSearch(keywords, type, city, pageSize, latitude, longitude, range);
    }

    /**
     * 周边检索POI
     *
     * @param keywords  关键字
     * @param type      poi搜索类型
     * @param city      城市，名称或code
     * @param pageSize  每页显示条数
     * @param latitude  经度
     * @param longitude 纬度
     * @param range     范围
     */
    public ZMapView poiSearch(String keywords, String type, String city, int pageSize,
                              double latitude, double longitude, int range) {
        if (TextUtils.isEmpty(keywords)) {
            if (zPoiSearchListener != null)
                zPoiSearchListener.onPoiSearchFail("关键词未输入！");
            return this;
        }
        if (zPoiSearchListener != null)
            zPoiSearchListener.onPoiSearchStart();
        // 第一个参数表示搜索字符串，
        // 第二个参数表示poi搜索类型，
        // 第三个参数表示poi搜索区域（空字符串代表全国）
        mPoiQuery = new PoiSearch.Query(keywords, type, city);
        // 设置每页最多返回多少条poiItem
        mPoiQuery.setPageSize(pageSize);
        // 设置查第一页
        if (poiSearchCurrentPage <= 0)
            poiSearchCurrentPage = 0;
        mPoiQuery.setPageNum(poiSearchCurrentPage);
        mPoiSearch = new PoiSearch(getContext(), mPoiQuery);
        mPoiSearch.setOnPoiSearchListener(this);
        // 设置周边搜索的中心点以及半径
        mPoiSearch.setBound(new PoiSearch.SearchBound(new LatLonPoint(latitude,
                longitude), range));
        mPoiSearch.searchPOIAsyn();
        return this;
    }

    /**
     * 开始搜索路径规划方案
     *
     * @param startPoint          起点坐标
     * @param endPoint            终点坐标
     * @param startIcon           起点图
     * @param endIcon             终点图
     * @param passedByPoints      途经点
     * @param routeSearchListener 路线规划监听
     */
    public ZMapView searchRouteResult(@NonNull LatLonPoint startPoint, @NonNull LatLonPoint endPoint,
                                      int startIcon, int endIcon, ArrayList<LatLonPoint> passedByPoints,
                                      @NonNull ZRouteSearchListener routeSearchListener) {
        this.zRouteSearchListener = routeSearchListener;
        return searchRouteResult(startPoint, endPoint, startIcon, endIcon, passedByPoints);
    }

    /**
     * 开始搜索路径规划方案
     *
     * @param startPoint     起点坐标
     * @param endPoint       终点坐标
     * @param startIcon      起点图
     * @param endIcon        终点图
     * @param passedByPoints 途经点
     */
    public ZMapView searchRouteResult(@NonNull LatLonPoint startPoint, @NonNull LatLonPoint endPoint,
                                      int startIcon, int endIcon, ArrayList<LatLonPoint> passedByPoints) {
        mSearchRouteStartIcon = startIcon;
        mSearchRouteEndIcon = endIcon;
        return searchRouteResult(startPoint, endPoint, passedByPoints);
    }

    /**
     * 开始搜索路径规划方案
     *
     * @param startPoint          起点坐标
     * @param endPoint            终点坐标
     * @param passedByPoints      途经点
     * @param routeSearchListener 路线规划监听
     */
    public ZMapView searchRouteResult(@NonNull LatLonPoint startPoint, @NonNull LatLonPoint endPoint, ArrayList<LatLonPoint> passedByPoints,
                                      @NonNull ZRouteSearchListener routeSearchListener) {
        this.zRouteSearchListener = routeSearchListener;
        return searchRouteResult(startPoint, endPoint, passedByPoints);
    }

    /**
     * 开始搜索路径规划方案
     *
     * @param startPoint     起点坐标
     * @param endPoint       终点坐标
     * @param passedByPoints 途经点
     */
    public ZMapView searchRouteResult(LatLonPoint startPoint, LatLonPoint endPoint, ArrayList<LatLonPoint> passedByPoints) {
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
        mRouteSearch.setOnTruckRouteSearchListener(this);
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
            RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo, mSearchMode,
                    passedByPoints, null, "");
            mRouteSearch.calculateDriveRouteAsyn(query);// 异步路径规划驾车模式查询
        } else if (mRouteType == ROUTE_TYPE_WALK) {// 步行路径规划
            RouteSearch.WalkRouteQuery query = new RouteSearch.WalkRouteQuery(fromAndTo);
            mRouteSearch.calculateWalkRouteAsyn(query);// 异步路径规划步行模式查询
        } else if (mRouteType == ROUTE_TYPE_RIDE) {// 骑行路径规划
            RouteSearch.RideRouteQuery query = new RouteSearch.RideRouteQuery(fromAndTo);
            mRouteSearch.calculateRideRouteAsyn(query);// 异步路径规划骑行模式查询
        } else if (mRouteType == ROUTE_TYPE_TRUCK) {// 驾车路线规划 - 默认轻型车
            // 第一个参数表示路径规划的起点和终点，
            // 第二个参数表示计算路径的模式，
            // 第三个参数表示途经点，
            // 第四个参数货车大小 必填
            RouteSearch.TruckRouteQuery query = new RouteSearch.TruckRouteQuery(fromAndTo, mSearchMode,
                    passedByPoints, mTruckSize);
            mRouteSearch.calculateTruckRouteAsyn(query);
        }
        return this;
    }

    // 驾车规划结果
    @Override
    public void onDriveRouteSearched(DriveRouteResult result, int errorCode) {
        getAMap().clear();// 清理地图上的所有覆盖物
        if (errorCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null
                    && result.getPaths() != null
                    && result.getPaths().size() > 0) {
                if (zRouteSearchListener != null) {
                    ArrayList<Float> list = new ArrayList<>();
                    List<DrivePath> drivePathList = result.getPaths();
                    for (DrivePath drivePath : drivePathList)
                        list.add(drivePath.getDistance());
                    Collections.sort(list);
                    zRouteSearchListener.onDriveNext(result, list);
                } else {
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
                    drivingRouteOverlay.addToMap(mSearchRouteStartIcon, mSearchRouteEndIcon);
                    drivingRouteOverlay.zoomToSpan();
                }

//                if (zRouteSearchListener != null)
//                    zRouteSearchListener.onRouteSearchSuccess();
            } else {
                if (zRouteSearchListener != null)
                    zRouteSearchListener.onRouteSearchFail("未获取到任何数据！");
            }
        } else {
            if (zRouteSearchListener != null)
                zRouteSearchListener.onRouteSearchFail("路线规划失败：" + errorCode);
        }
        if (zRouteSearchListener != null)
            zRouteSearchListener.onRouteSearchComplete();
    }

    // 步行规划结果
    @Override
    public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int errorCode) {
        getAMap().clear();// 清理地图上的所有覆盖物
        if (errorCode == AMapException.CODE_AMAP_SUCCESS) {
            if (walkRouteResult != null
                    && walkRouteResult.getPaths() != null
                    && walkRouteResult.getPaths().size() > 0) {
                if (zRouteSearchListener != null) {
                    ArrayList<Float> list = new ArrayList<>();
                    List<WalkPath> walkPathList = walkRouteResult.getPaths();
                    for (WalkPath walkPath : walkPathList)
                        list.add(walkPath.getDistance());
                    Collections.sort(list);
                    zRouteSearchListener.onWalkNext(walkRouteResult, list);
                } else {
                    WalkPath walkPath = walkRouteResult.getPaths().get(0);
                    WalkRouteOverlay walkRouteOverlay = new WalkRouteOverlay(
                            getContext(),
                            getAMap(),
                            walkPath,
                            walkRouteResult.getStartPos(),
                            walkRouteResult.getTargetPos()
                    );
                    walkRouteOverlay.addToMap(mSearchRouteStartIcon, mSearchRouteEndIcon);
                    walkRouteOverlay.zoomToSpan();
                }

//                if (zRouteSearchListener != null)
//                    zRouteSearchListener.onRouteSearchSuccess();
            } else {
                if (zRouteSearchListener != null)
                    zRouteSearchListener.onRouteSearchFail("未获取到任何数据！");
            }
        } else {
            if (zRouteSearchListener != null)
                zRouteSearchListener.onRouteSearchFail("路线规划失败：" + errorCode);
        }
        if (zRouteSearchListener != null)
            zRouteSearchListener.onRouteSearchComplete();
    }

    // 骑行规划结果
    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int errorCode) {
        getAMap().clear();// 清理地图上的所有覆盖物
        if (errorCode == AMapException.CODE_AMAP_SUCCESS) {
            if (rideRouteResult != null
                    && rideRouteResult.getPaths() != null
                    && rideRouteResult.getPaths().size() > 0) {
                if (zRouteSearchListener != null) {
                    ArrayList<Float> list = new ArrayList<>();
                    List<RidePath> ridePathList = rideRouteResult.getPaths();
                    for (RidePath ridePath : ridePathList)
                        list.add(ridePath.getDistance());
                    Collections.sort(list);
                    zRouteSearchListener.onRideNext(rideRouteResult, list);
                } else {
                    RidePath ridePath = rideRouteResult.getPaths().get(0);
                    RideRouteOverlay rideRouteOverlay = new RideRouteOverlay(
                            getContext(),
                            getAMap(),
                            ridePath,
                            rideRouteResult.getStartPos(),
                            rideRouteResult.getTargetPos());
                    rideRouteOverlay.removeFromMap();
                    rideRouteOverlay.addToMap(mSearchRouteStartIcon, mSearchRouteEndIcon);
                    rideRouteOverlay.zoomToSpan();
                }

//                if (zRouteSearchListener != null)
//                    zRouteSearchListener.onRouteSearchSuccess();
            } else {
                if (zRouteSearchListener != null)
                    zRouteSearchListener.onRouteSearchFail("未获取到任何数据！");
            }
        } else {
            if (zRouteSearchListener != null)
                zRouteSearchListener.onRouteSearchFail("路线规划失败：" + errorCode);
        }
        if (zRouteSearchListener != null)
            zRouteSearchListener.onRouteSearchComplete();
    }

    // 公交车规划结果
    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int errorCode) {
        getAMap().clear();// 清理地图上的所有覆盖物
        if (errorCode == 1000) {
            if (busRouteResult != null
                    && busRouteResult.getPaths() != null
                    && busRouteResult.getPaths().size() > 0) {
                if (zRouteSearchListener != null) {
                    ArrayList<Float> list = new ArrayList<>();
                    List<BusPath> busPathList = busRouteResult.getPaths();
                    for (BusPath buspath : busPathList)
                        list.add(buspath.getDistance());
                    Collections.sort(list);
                    zRouteSearchListener.onBusNext(busRouteResult, list);
                } else {
                    BusPath buspath = busRouteResult.getPaths().get(0);
                    BusRouteOverlay busrouteOverlay = new BusRouteOverlay(
                            getContext(),
                            getAMap(),
                            buspath,
                            busRouteResult.getStartPos(),
                            busRouteResult.getTargetPos());
                    busrouteOverlay.removeFromMap();
                    busrouteOverlay.addToMap(mSearchRouteStartIcon, mSearchRouteEndIcon);
                    busrouteOverlay.zoomToSpan();
                }

//                if (zRouteSearchListener != null)
//                    zRouteSearchListener.onRouteSearchSuccess();
            } else {
                if (zRouteSearchListener != null)
                    zRouteSearchListener.onRouteSearchFail("未获取到任何数据！");
            }
        } else {
            if (zRouteSearchListener != null)
                zRouteSearchListener.onRouteSearchFail("路线规划失败：" + errorCode);
        }
        if (zRouteSearchListener != null)
            zRouteSearchListener.onRouteSearchComplete();
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
        if (zDistanceSearchListener != null)
            zDistanceSearchListener.onDistanceSearchError(e);
        if (zGeocodeSearchListener != null)
            zGeocodeSearchListener.onGeocodeSearchError(e);
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
        if (errorCode == 1000) {
            if (poiResult != null && poiResult.getQuery() != null) {// 搜索poi的结果
                if (poiResult.getQuery().equals(mPoiQuery)) {// 是否是同一条
                    // 取得搜索到的poiItems有多少页
                    List<PoiItem> poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
                    List<SuggestionCity> suggestionCities = poiResult.getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息
                    if (zPoiSearchListener != null)
                        zPoiSearchListener.onPoiSearchNext(poiItems, suggestionCities);
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
//                    if (zPoiSearchListener != null)
//                        zPoiSearchListener.onPoiSearchSuccess();
                }
            } else {
                if (zPoiSearchListener != null)
                    zPoiSearchListener.onPoiSearchFail("未查询到任何数据");
            }
        } else {
            if (zPoiSearchListener != null)
                zPoiSearchListener.onPoiSearchFail("POI搜索失败：" + errorCode);
        }
        if (zPoiSearchListener != null)
            zPoiSearchListener.onPoiSearchComplete();
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int errorCode) {
        if (zPoiSearchListener != null)
            zPoiSearchListener.onPoiItemSearched(poiItem, errorCode);
    }

    // 定位结果
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                // 定位成功回调信息，设置相关消息
                LocationData locationData = new LocationData();
                locationData.setPointy(amapLocation.getLatitude());// 获取纬度
                locationData.setPointx(amapLocation.getLongitude());// 获取经度
                locationData.setCurrentAddress(amapLocation.getAddress());// 地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                locationData.setCurrentCountry(amapLocation.getCountry());// 国家信息
                locationData.setCurrentProv(amapLocation.getProvince());// 省信息
                mCurrentCityName = amapLocation.getCity();
                locationData.setCurrentCity(mCurrentCityName);// 城市信息
                locationData.setCurrentDistrict(amapLocation.getDistrict());// 城区信息
                locationData.setCurrentStreet(amapLocation.getStreet());// 街道信息
                locationData.setCurrentStreetNum(amapLocation.getStreetNum());// 街道门牌号信息
                locationData.setCurrentCityCode(amapLocation.getCityCode());// 城市编码
                locationData.setCurrentAdCode(amapLocation.getAdCode());// 地区编码
                locationData.setCurrentAoiName(amapLocation.getAoiName());// 获取当前定位点的AOI信息

                if (zLocationListener != null)
                    zLocationListener.onLocationNext(locationData);
                if (!isShieldingDefaultLocationOper) {
                    LatLng location = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());
                    if (!mFirstLocationFix) {
                        mFirstLocationFix = true;
                        addLatLngCircle(location, amapLocation.getAccuracy());// 添加定位精度圆
                        addLatLngMarker(location);// 添加定位图标
                        mSensorHelper.setCurrentMarker(mLocationMarker);// 定位图标旋转
                        getAMap().moveCamera(CameraUpdateFactory.newLatLngZoom(location, defaultZoom));
                    } else {
                        mLocationCircle.setCenter(location);
                        mLocationCircle.setRadius(amapLocation.getAccuracy());
                        mLocationMarker.setPosition(location);
                        getAMap().moveCamera(CameraUpdateFactory.changeLatLng(location));
                    }
                }

//                if (zLocationListener != null)
//                    zLocationListener.onLocationSuccess();
            } else {
                // 显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                if (zLocationListener != null)
                    zLocationListener.onLocationFail("location Error, ErrCode:"
                            + amapLocation.getErrorCode() + ", errInfo:"
                            + amapLocation.getErrorInfo());
            }
            // 单次定位 - 关闭定位
            if (isOnceLocation)
                mLocationClient.stopLocation();
        } else {
            if (zLocationListener != null)
                zLocationListener.onLocationFail("定位失败！");
        }
        if (zLocationListener != null)
            zLocationListener.onLocationComplete();
    }

    /**
     * 添加圆圈
     *
     * @param latlng 位置
     * @param radius 半径
     */
    private ZMapView addLatLngCircle(@NonNull LatLng latlng, double radius) {
        CircleOptions options = new CircleOptions();
        options.strokeWidth(1f)
                .fillColor(FILL_COLOR)
                .strokeColor(STROKE_COLOR)
                .center(latlng)
                .radius(radius);
        mLocationCircle = getAMap().addCircle(options);
        return this;
    }

    /**
     * 添加定位Marker
     *
     * @param latlng 位置信息
     */
    public ZMapView addLatLngMarker(@NonNull LatLng latlng) {
        if (mLocationMarker != null)
            mLocationMarker.remove();
        MarkerOptions options = new MarkerOptions();
        options.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(this.getResources(), R.mipmap.navi_map_gps_locked)))
                .anchor(0.5f, 0.5f)
                .position(latlng);
        mLocationMarker = getAMap().addMarker(options);
        return this;
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

    /**
     * 计算两点距离
     *
     * @param startPoint             起点坐标
     * @param endPoint               终点坐标
     * @param distanceSearchListener 距离搜索监听
     */
    public ZMapView calculateRouteDistance(@NonNull LatLonPoint startPoint, @NonNull LatLonPoint endPoint,
                                           @NonNull ZDistanceSearchListener distanceSearchListener) {
        this.zDistanceSearchListener = distanceSearchListener;
        return calculateRouteDistance(startPoint, endPoint);
    }

    /**
     * 计算两点距离
     *
     * @param startPoint 起点坐标
     * @param endPoint   终点坐标
     */
    public ZMapView calculateRouteDistance(@NonNull LatLonPoint startPoint, @NonNull LatLonPoint endPoint) {
        ArrayList<LatLonPoint> startPoints = new ArrayList<>();
        startPoints.add(startPoint);
        return calculateRouteDistance(startPoints, endPoint);
    }

    /**
     * 计算两点距离
     *
     * @param startPoints            起点坐标集合
     * @param endPoint               终点坐标
     * @param distanceSearchListener 距离搜索监听
     */
    public ZMapView calculateRouteDistance(@NonNull ArrayList<LatLonPoint> startPoints,
                                           @NonNull LatLonPoint endPoint,
                                           @NonNull ZDistanceSearchListener distanceSearchListener) {
        this.zDistanceSearchListener = distanceSearchListener;
        return calculateRouteDistance(startPoints, endPoint);
    }

    /**
     * 计算两点距离
     *
     * @param startPoints 起点坐标集合
     * @param endPoint    终点坐标
     */
    public ZMapView calculateRouteDistance(@NonNull ArrayList<LatLonPoint> startPoints,
                                           @NonNull LatLonPoint endPoint) {
        if (zDistanceSearchListener != null)
            zDistanceSearchListener.onDistanceSearchStart();
        if (distanceSearch == null)
            distanceSearch = new DistanceSearch(this.getContext());
        DistanceSearch.DistanceQuery distanceQuery = new DistanceSearch.DistanceQuery();
        // 设置起点和终点，其中起点支持多个
        distanceQuery.setOrigins(startPoints);
        distanceQuery.setDestination(endPoint);
        // 设置测量方式，支持直线和驾车
        distanceQuery.setType(DistanceSearch.TYPE_DRIVING_DISTANCE);
        distanceSearch.setDistanceSearchListener(new DistanceSearch.OnDistanceSearchListener() {
            @Override
            public void onDistanceSearched(DistanceResult distanceResult, int errorCode) {
                if (errorCode == 1000) {
                    List<DistanceItem> list = distanceResult.getDistanceResults();
                    if (list != null) {
                        ArrayList<Float> distanceList = new ArrayList<>();
                        for (DistanceItem distanceItem : list)
                            distanceList.add(distanceItem.getDistance());
                        if (zDistanceSearchListener != null)
                            zDistanceSearchListener.onDistanceSearched(distanceResult, distanceList);
                    }
//                    if (zDistanceSearchListener != null)
//                        zDistanceSearchListener.onDistanceSearchSuccess();
                } else {
                    if (zDistanceSearchListener != null)
                        zDistanceSearchListener.onDistanceSearchFail("计算失败：" + errorCode);
                }
                if (zDistanceSearchListener != null)
                    zDistanceSearchListener.onDistanceSearchComplete();
            }
        });
        distanceSearch.calculateRouteDistanceAsyn(distanceQuery);
        return this;
    }

    /**
     * 将经纬度转换成坐标点
     *
     * @param pointx 经度
     * @param pointy 纬度
     */
    public LatLonPoint convertToLatLonPoint(double pointx, double pointy) {
        return new LatLonPoint(pointy, pointx);
    }

    /**
     * 根据经纬度获取详情地址信息
     *
     * @param pointx 经度
     * @param pointy 纬度
     */
    public ZMapView getAddressByLatLonPoint(double pointx, double pointy) {
        return getAddressByLatLonPoint(pointx, pointy, zGeocodeSearchListener);
    }

    /**
     * 根据经纬度获取详情地址信息
     *
     * @param pointx                经度
     * @param pointy                纬度
     * @param geocodeSearchListener 查询监听
     */
    public ZMapView getAddressByLatLonPoint(double pointx, double pointy,
                                            @NonNull ZGeocodeSearchListener geocodeSearchListener) {
        return getAddressByLatLonPoint(convertToLatLonPoint(pointx, pointy), geocodeSearchListener);
    }

    /**
     * 根据经纬度获取详情地址信息
     *
     * @param latLonPoint 坐标点
     */
    public ZMapView getAddressByLatLonPoint(@NonNull LatLonPoint latLonPoint) {
        return getAddressByLatLonPoint(latLonPoint, zGeocodeSearchListener);
    }

    /**
     * 根据经纬度获取详情地址信息
     *
     * @param latLonPoint           坐标点
     * @param geocodeSearchListener 查询监听
     */
    public ZMapView getAddressByLatLonPoint(@NonNull LatLonPoint latLonPoint,
                                            @NonNull ZGeocodeSearchListener geocodeSearchListener) {
        return getAddressByLatLonPoint(latLonPoint, 500f, GeocodeSearch.AMAP, geocodeSearchListener);
    }

    /**
     * 根据经纬度获取详情地址信息
     *
     * @param latLonPoint 坐标点
     * @param range       查询范围- m（米）
     * @param type        坐标类型
     */
    public ZMapView getAddressByLatLonPoint(@NonNull LatLonPoint latLonPoint, float range, @NonNull String type) {
        return getAddressByLatLonPoint(latLonPoint, range, type, zGeocodeSearchListener);
    }

    /**
     * 根据经纬度获取详情地址信息
     *
     * @param latLonPoint           坐标点
     * @param range                 查询范围- m（米）
     * @param type                  坐标类型
     * @param geocodeSearchListener 查询监听
     */
    public ZMapView getAddressByLatLonPoint(@NonNull LatLonPoint latLonPoint, float range, @NonNull String type,
                                            @NonNull ZGeocodeSearchListener geocodeSearchListener) {
        this.zGeocodeSearchListener = geocodeSearchListener;
        zGeocodeSearchListener.onGeocodeSearchStart();
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, range, type);
        if (mGeocodeSearch == null) {
            mGeocodeSearch = new GeocodeSearch(getContext());
            mGeocodeSearch.setOnGeocodeSearchListener(this);
        }
        // 异步查询
        mGeocodeSearch.getFromLocationAsyn(query);
        return this;
    }

    // 得到逆向地址异步查询结果
    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
        if (regeocodeResult == null) {
            if (zGeocodeSearchListener != null)
                zGeocodeSearchListener.onGeocodeSearchFail("逆向地址查询失败！" + i);
        } else {
            RegeocodeAddress regeocodeAddress = regeocodeResult.getRegeocodeAddress();
            String address = regeocodeAddress.getFormatAddress();
            if (zGeocodeSearchListener != null) {
                zGeocodeSearchListener.onRegeocodeSearched(regeocodeResult, address);
                zGeocodeSearchListener.onGeocodeSearchComplete();
            }
        }
    }

    // 得到逆向地址查询结果
    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
        if (geocodeResult == null) {
            if (zGeocodeSearchListener != null)
                zGeocodeSearchListener.onGeocodeSearchFail("逆向地址查询失败！" + i);
        } else {
            List<GeocodeAddress> geocodeAddressList = geocodeResult.getGeocodeAddressList();
            ArrayList<String> addressList = new ArrayList<>();
            for (GeocodeAddress geocodeAddress : geocodeAddressList) {
                String address = geocodeAddress.getFormatAddress();
                addressList.add(address);
            }
            if (zGeocodeSearchListener != null) {
                zGeocodeSearchListener.onGeocodeSearched(geocodeResult, geocodeAddressList, addressList);
                zGeocodeSearchListener.onGeocodeSearchComplete();
            }
        }
    }

    // 货车路线规划返回结果
    @Override
    public void onTruckRouteSearched(TruckRouteRestult truckRouteRestult, int errorCode) {
        getAMap().clear();// 清理地图上的所有覆盖物
        if (errorCode == 1000) {
            if (truckRouteRestult != null
                    && truckRouteRestult.getPaths() != null
                    && truckRouteRestult.getPaths().size() > 0) {
                if (zRouteSearchListener != null) {
                    ArrayList<Float> list = new ArrayList<>();
                    List<TruckPath> truckPathList = truckRouteRestult.getPaths();
                    for (TruckPath truckpath : truckPathList)
                        list.add(truckpath.getDistance());
                    Collections.sort(list);
                    zRouteSearchListener.onTruckNext(truckRouteRestult, list);
                } else {
                    TruckPath path = truckRouteRestult.getPaths().get(0);
                    TruckRouteColorfulOverLay drivingRouteOverlay = new TruckRouteColorfulOverLay(
                            getContext(), getAMap(), path, truckRouteRestult.getStartPos(),
                            truckRouteRestult.getTargetPos(), null);
                    drivingRouteOverlay.removeFromMap();
                    drivingRouteOverlay.setIsColorfulline(true);
                    drivingRouteOverlay.addToMap(mSearchRouteStartIcon, mSearchRouteEndIcon);
                    drivingRouteOverlay.zoomToSpan();
                }

//                if (zRouteSearchListener != null)
//                    zRouteSearchListener.onRouteSearchSuccess();
            } else {
                if (zRouteSearchListener != null)
                    zRouteSearchListener.onRouteSearchFail("未获取到任何数据！");
            }
        } else {
            if (zRouteSearchListener != null)
                zRouteSearchListener.onRouteSearchFail("路线规划失败：" + errorCode);
        }
        if (zRouteSearchListener != null)
            zRouteSearchListener.onRouteSearchComplete();
    }

    /**
     * 设置地图自定义样式
     *
     * @param styleId 官网控制台-自定义样式 获取
     */
    public ZMapView setStyleId(String styleId) {
        getMap().setCustomMapStyle(
                new com.amap.api.maps.model.CustomMapStyleOptions()
                        .setEnable(true)
                        .setStyleId(styleId)
        );
        return this;
    }

    // 地图点击事件
    @Override
    public void onMapClick(LatLng latLng) {
        if (zMapClickListener != null)
            zMapClickListener.onMapClick(latLng);
        else
            addMarker(latLng);
    }

    // 地图长按事件
    @Override
    public void onMapLongClick(LatLng latLng) {
        if (zMapLongClickListener != null)
            zMapLongClickListener.onMapLongClick(latLng);
    }

    /**
     * 地图上添加marker
     *
     * @param latLng 待显示位置
     */
    public ZMapView addMarker(LatLng latLng) {
        return addMarker(latLng, R.mipmap.navi_map_gps_locked);
    }

    /**
     * 地图上添加marker
     *
     * @param latLng 待显示位置
     * @param res    图标地址
     */
    public ZMapView addMarker(LatLng latLng, int res) {
        if (aMap != null) {
            if (marker != null)
                marker.remove();
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.icon(BitmapDescriptorFactory.fromResource(res));
            markerOptions.position(latLng);
            marker = aMap.addMarker(markerOptions);
        }
        return this;
    }

    /**
     * 移除当前定位Marker
     */
    public ZMapView removeLocationMarker() {
        if (mLocationMarker != null)
            mLocationMarker.remove();
        return this;
    }

    /**
     * POI关键字搜索并创建高德POI地理围栏
     *
     * @param keyword  POI关键字  例如：首开广场
     * @param poiType  POI类型  例如：写字楼
     * @param city     POI所在的城市名称 例如：北京
     * @param customId 与围栏关联的自有业务Id
     */
    public ZMapView addGeoFence(String keyword, String poiType, String city, int size, String customId) {
        getGeoFenceClient().addGeoFence(keyword, poiType, city, size, customId);
        return this;
    }

    /**
     * POI周边搜索并创建高德POI地理围栏
     *
     * @param keyword      POI关键字 例如：首开广场
     * @param poiType      POI类型  例如：写字楼
     * @param point        周边区域中心点的经纬度，以此中心点建立周边地理围栏 例如：北京
     * @param aroundRadius 周边半径，0-50000米，默认3000米
     * @param customId     与围栏关联的自有业务Id
     */
    public ZMapView addGeoFence(String keyword, String poiType, DPoint point, float aroundRadius, int size, String customId) {
        getGeoFenceClient().addGeoFence(keyword, poiType, point, aroundRadius, size, customId);
        return this;
    }

    /**
     * 行政区划关键字创建行政区划围栏
     *
     * @param keyword  行政区划关键字 例如：朝阳区
     * @param customId 与围栏关联的自有业务Id
     */
    public ZMapView addGeoFence(String keyword, String customId) {
        getGeoFenceClient().addGeoFence(keyword, customId);
        return this;
    }

    /**
     * 创建自定义围栏
     *
     * @param point    围栏中心点
     * @param radius   要创建的围栏半径 ，半径无限制，单位米
     * @param customId 与围栏关联的自有业务Id
     */
    public ZMapView addGeoFence(DPoint point, float radius, String customId) {
        getGeoFenceClient().addGeoFence(point, radius, customId);
        return this;
    }

    /**
     * 多边形围栏
     *
     * @param points   多边形的边界坐标点，最少传3个
     * @param customId 与围栏关联的自有业务Id
     */
    public ZMapView addGeoFence(List<DPoint> points, String customId) {
        getGeoFenceClient().addGeoFence(points, customId);
        return this;
    }

    /**
     * 清空围栏
     */
    public ZMapView removeGeoFence() {
        if (mGeoFenceClient != null)
            mGeoFenceClient.removeGeoFence();
        return this;
    }

    @Override
    public void onGeoFenceCreateFinished(List<GeoFence> list, int i, String s) {
        if (zGeoFenceListener != null)
            zGeoFenceListener.onGeoFenceCreateFinished(list, i, s);
    }

    // 地址坐标转LatLng
    public LatLng toLatLng(double latitude, double longitude) {
        return new LatLng(latitude, longitude);
    }

    /**
     * 绘制直线
     *
     * @param latLngs 轨迹点
     */
    public void addPolyline(List<LatLng> latLngs) {
        addPolyline(latLngs, 10, Color.argb(255, 1, 1, 1));
    }

    /**
     * 绘制直线
     *
     * @param latLngs 轨迹点
     * @param width   关机宽度
     */
    public void addPolyline(List<LatLng> latLngs, int width) {
        addPolyline(latLngs, width, Color.argb(255, 1, 1, 1));
    }

    /**
     * 绘制直线
     *
     * @param latLngs 轨迹点
     * @param width   关机宽度
     * @param color   轨迹颜色
     */
    public void addPolyline(List<LatLng> latLngs, int width, int color) {
        if (polyline != null)
            polyline.remove();
        polyline = getAMap().addPolyline(new PolylineOptions()
                .addAll(latLngs).width(width).color(color));
    }

    /**
     * 绘制直线
     */
    public void addPolyline(PolylineOptions polylineOptions) {
        if (polyline != null)
            polyline.remove();
        polyline = getAMap().addPolyline(polylineOptions);
    }
}
