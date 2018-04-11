package photoselector.ui;

import android.content.Context;
import android.graphics.PointF;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.facebook.common.util.UriUtil;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.taihe.photoselect.R;

import photoselector.ui.photodrawee.MyOnDoubleTapListener;
import photoselector.ui.photodrawee.OnViewTapListener;
import photoselector.ui.photodrawee.PhotoDraweeView;
import photoselector.ui.photodrawee.ScaleImageView;
import photoselector.util.CommonUtils;
import photoselector.util.ImageDeal;

public class PhotoFullscreenPreview extends FrameLayout {

    private OnViewTapListener listener;

    public PhotoFullscreenPreview(Context context) {
        super(context);
    }

    public PhotoFullscreenPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PhotoFullscreenPreview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    /**
     * @param path
     */
    public void loadImageFromAlum(String path, boolean isGifAutoPlay) {
        setPhotoUri(null, path, isGifAutoPlay, 0, 0);
    }

    /**
     * 全屏查看图片，圈子 新闻，但是只有圈子那里有图片宽高
     *
     * @param path
     */
    public void loadImageFromArct(String lowResPaht, String path, int width, int height) {
//        if (!ImageDeal.isLargerPicture(width, height)) {
        //长图不裁剪
        path = getActuralImageLoadPath(path);
//        }
        setPhotoUri(lowResPaht, path, true, width, height);
    }

    /**
     * 获取实际加载图片的地址，加过参数的地址
     *
     * @param path
     * @return
     */
    public String getActuralImageLoadPath(String path) {
        if (UriUtil.isLocalFileUri(Uri.parse(path)))
            return path;
        if (path.endsWith(".gif"))
            return path;
        return path + ((path.contains("@w") || path.contains("@h") || path.contains("@q") || path.contains("@!") || path.contains("@s")) ? "" : "@w_" + CommonUtils.getScreenWidthPixels(getContext()) + ",q_60");
    }

    /**
     * 加载原图
     *
     * @param path
     */
    public void loadImageOrignal(String lowResPaht, String path, int width, int height) {
        setPhotoUri(lowResPaht, path, true, width, height);
    }

    /**
     * @return
     */
    private LayoutParams getContentLayoutParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    /**
     * @return
     */
    private PhotoDraweeView getPhotoDraweeView() {
        GenericDraweeHierarchy hierarchy = new GenericDraweeHierarchyBuilder(getContext().getResources())
//                .setProgressBarImage((Drawable) (roundingParams))
                .setProgressBarImage(new ImageLoadingDrawable(getContext()))
//                .setProgressBarImage(new ProgressBarDrawable())
                .setProgressBarImageScaleType(ScalingUtils.ScaleType.CENTER_INSIDE)
                .setFailureImage(R.mipmap.icon_fail_image)
                .setFailureImageScaleType(ScalingUtils.ScaleType.CENTER_INSIDE)
                .setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER)
                .build();

        PhotoDraweeView draweeView = new PhotoDraweeView(getContext());
        draweeView.setLayoutParams(getContentLayoutParams());
        //
        draweeView.setOnDoubleTapListener(new MyOnDoubleTapListener(draweeView.getAttacher()));

        draweeView.setHierarchy(hierarchy);
        return draweeView;
    }

    /**
     * 用PhotoDraweeView
     *
     * @param lowResPaht
     * @param path
     * @param isShowgig
     */
    private void setContentView_PhotoDraweeView(String lowResPaht, String path, boolean isShowgig) {

        PhotoDraweeView draweeView = getPhotoDraweeView();
        draweeView.setPhotoUri(lowResPaht, path, isShowgig);

        draweeView.setOnViewTapListener(new OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                if (listener != null) {
                    listener.onViewTap(view, x, y);
                }
            }
        });

        addView(draweeView);
    }

    /**
     * ScaleImageView
     *
     * @param lowResPaht
     * @param path
     * @param isShowgig
     */
    private void setContentView_ScaleImageView(String lowResPaht, String path, boolean isShowgig) {
        final ScaleImageView imageView = new ScaleImageView(getContext());
        imageView.setLayoutParams(getContentLayoutParams());
        imageView.setPhotoUri(lowResPaht, path, isShowgig);

        final GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (imageView.isReady()) {
                    if (listener != null) {
                        PointF sCoord = imageView.viewToSourceCoord(e.getX(), e.getY());
                        listener.onViewTap(imageView, sCoord.x, sCoord.y);
                    }
                }
                return super.onSingleTapConfirmed(e);
            }
        });

        imageView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return gestureDetector.onTouchEvent(motionEvent);
            }
        });

        addView(imageView);
    }

    /**
     * 如果path  是网络，就通过width，height来判断
     * 如果是file image, 直接通过获取图片的宽高来判断
     *
     * @param lowResPaht
     * @param path
     * @param isShowgif
     * @param width
     * @param height
     */
    private void setPhotoUri(String lowResPaht, String path, boolean isShowgif, int width, int height) {

        boolean isBigPict = false;

        if (UriUtil.isNetworkUri(Uri.parse(path))) {
            //网络，就通过判断width，height
            isBigPict = ImageDeal.isLargerPicture(width, height);
        } else if (UriUtil.isLocalFileUri(Uri.parse(path))) {
            isBigPict = ImageDeal.isLargerPicture(path);
        }

        removeAllViews();

        if (isBigPict) {
            setContentView_ScaleImageView(lowResPaht, path, isShowgif);
        } else {
            setContentView_PhotoDraweeView(lowResPaht, path, isShowgif);
        }
    }

    /**
     * @param mlistener
     */
    public void setOnViewTapListener(OnViewTapListener mlistener) {
        listener = mlistener;
    }
}
