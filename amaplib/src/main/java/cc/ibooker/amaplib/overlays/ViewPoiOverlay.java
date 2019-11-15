package cc.ibooker.amaplib.overlays;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.services.core.PoiItem;

import java.util.List;

import cc.ibooker.amaplib.R;

/**
 * 自定义PoiOverlay气泡
 *
 * @author 邹峰立
 */
public class ViewPoiOverlay extends PoiOverlay {
    private Context mContext;

    public ViewPoiOverlay(Context context, AMap aMap, List<PoiItem> list) {
        super(aMap, list);
        this.mContext = context;
    }

    @Override
    protected BitmapDescriptor getBitmapDescriptor(int index) {
        View view;
        view = View.inflate(mContext, R.layout.layout_poioverlay_view, null);
        TextView textView = view.findViewById(R.id.tv_title);
        textView.setText(getTitle(index));
        return BitmapDescriptorFactory.fromView(view);
    }
}