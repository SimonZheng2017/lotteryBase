package photoselector.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;

import com.facebook.common.util.UriUtil;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.taihe.photoselect.R;

import photoselector.model.PhotoModel;

public class PhotoItem extends FrameLayout implements OnCheckedChangeListener {

    private PhotoSelectVIew ivPhoto;
    private CheckBox cbPhoto;
    private onPhotoItemCheckedListener listener;
    private PhotoModel photo;
    private boolean isCheckAll;


    private PhotoItem(Context context) {
        super(context);
    }

    public PhotoItem(Context context, onPhotoItemCheckedListener listener) {
        this(context);
        LayoutInflater.from(context).inflate(R.layout.layout_photoitem, this, true);
        this.listener = listener;

        ivPhoto = (PhotoSelectVIew) findViewById(R.id.iv_photo_lpsi);
        cbPhoto = (CheckBox) findViewById(R.id.cb_photo_lpsi);

        cbPhoto.setOnCheckedChangeListener(this); // CheckBox选中状态改变监听器

        InitHierarchy();
    }

    /**
     *
     */
    private void InitHierarchy() {
        GenericDraweeHierarchy hierarchy = new GenericDraweeHierarchyBuilder(getContext().getResources())
                .setPlaceholderImage(R.drawable.select_back)
//                .setProgressBarImage(new ProgressBarDrawable())
                .setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP)
                .build();

        ivPhoto.setHierarchy(hierarchy);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        // 让图片变暗或者变亮
        if (isChecked) {
            setDrawingable();
            ivPhoto.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
        } else {
            ivPhoto.clearColorFilter();
        }
        photo.setChecked(isChecked);

        if (!isCheckAll) {
            listener.onCheckedChanged(photo, buttonView, isChecked); // 调用主界面回调函数
        }
    }

    /**
     * 设置路径下的图片对应的缩略图
     *
     * @param photo
     * @param width
     */
    public void setImageDrawable(final PhotoModel photo, int width, boolean isGifAutoPlay) {
        this.photo = photo;

        Uri uri = Uri.parse("file://" + photo.getOriginalPath());
        ImageRequestBuilder imageRequestBuilder = ImageRequestBuilder.newBuilderWithSource(uri);
        if (UriUtil.isNetworkUri(uri)) {
//            imageRequestBuilder.setProgressiveRenderingEnabled(true);
        } else {
            imageRequestBuilder.setResizeOptions(new ResizeOptions(width, width));
        }
        imageRequestBuilder.setAutoRotateEnabled(true);

        DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                .setImageRequest(imageRequestBuilder.build())
                .setOldController(ivPhoto.getController())
                .setAutoPlayAnimations(isGifAutoPlay)
                .build();
        ivPhoto.setController(draweeController);
    }

    private void setDrawingable() {
        ivPhoto.setDrawingCacheEnabled(true);
        ivPhoto.buildDrawingCache();
    }

    @Override
    public void setSelected(boolean selected) {
        if (photo == null) {
            return;
        }
        isCheckAll = true;
        cbPhoto.setChecked(selected);
        isCheckAll = false;
    }

    /**
     *
     */
    public void checkItem() {
        cbPhoto.setChecked(!photo.isChecked());
    }

    /**
     * 图片Item选中事件监听器
     */
    public static interface onPhotoItemCheckedListener {
        public void onCheckedChanged(PhotoModel photoModel, CompoundButton buttonView, boolean isChecked);
    }

    /**
     * 图片点击事件
     */
    public interface onItemClickListener {
        public void onItemClick(int position);
    }

}
