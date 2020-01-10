package cc.ibooker.zamap;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.amap.api.navi.model.NaviLatLng;

import cc.ibooker.amaplib.ZAMapNaviView;

/**
 * 测试导航Activity
 *
 * @author 邹峰立
 */
public class NavActivity extends AppCompatActivity {
    private ZAMapNaviView zaMapNaviView;

    private NaviLatLng mStartPoint = new NaviLatLng(39.955545, 116.20151);//起点，116.335891,39.942295
    private NaviLatLng mEndPoint = new NaviLatLng(39.954545, 116.30155);//终点，116.481288,39.995576

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);

        zaMapNaviView = findViewById(R.id.zAMapNaviView);
        zaMapNaviView.onCreate(savedInstanceState);

        zaMapNaviView.addStartPoint(mStartPoint)
                .addEndPoint(mEndPoint);
    }

    @Override
    protected void onResume() {
        super.onResume();
        zaMapNaviView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        zaMapNaviView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        zaMapNaviView.onZDestroy();
    }
}
