package photoselector.ui;

import android.content.Context;
import android.util.AttributeSet;

import com.facebook.drawee.view.SimpleDraweeView;

/**
 * Created by Soli on 2016/7/29.
 */
public class PhotoSelectVIew extends SimpleDraweeView {

    public PhotoSelectVIew(Context context) {
        super(context);
    }

    public PhotoSelectVIew(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PhotoSelectVIew(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(width, width);
    }
}
