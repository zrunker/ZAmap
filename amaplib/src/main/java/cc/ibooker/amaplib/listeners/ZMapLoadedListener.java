package cc.ibooker.amaplib.listeners;

/**
 * 地图加载监听接口
 *
 * @author 邹峰立
 */
public interface ZMapLoadedListener {

    // 地图加载完成
    void onMapLoaded();

    void onMapError(Throwable e);
}
