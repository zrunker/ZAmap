package cc.ibooker.amaplib.listeners;

/**
 * 导航地图绘制监听
 *
 * @author 邹峰立
 */
public interface ZAMapNaviViewListener {
    void onNaviSetting();

    void onNaviCancel();

    boolean onNaviBackClick();

    void onNaviMapMode(int var1);

    void onNaviTurnClick();

    void onNextRoadClick();

    void onScanViewButtonClick();

    void onLockMap(boolean var1);

    void onNaviViewLoaded();

    void onMapTypeChanged(int var1);

    void onNaviViewShowMode(int var1);
}
