package photoselector.ui.photodrawee;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.View;

import com.facebook.common.util.UriUtil;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import photoselector.ui.ImageLoadingDrawable;
import photoselector.util.ImageDeal;

public class PhotoDraweeView extends SimpleDraweeView implements IAttacher {

    private Attacher mAttacher;

    private boolean mEnableDraweeMatrix = true;

    public PhotoDraweeView(Context context, GenericDraweeHierarchy hierarchy) {
        super(context, hierarchy);
        init();
    }

    public PhotoDraweeView(Context context) {
        super(context);
        init();
    }

    public PhotoDraweeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PhotoDraweeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    /**
     *
     */
    protected void init() {
        if (mAttacher == null || mAttacher.getDraweeView() == null) {
            mAttacher = new Attacher(this);
        }
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        int saveCount = canvas.save();
        if (mEnableDraweeMatrix) {
            canvas.concat(mAttacher.getDrawMatrix());
        }
        super.onDraw(canvas);
        canvas.restoreToCount(saveCount);
    }

    @Override
    protected void onAttachedToWindow() {
        init();
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        mAttacher.onDetachedFromWindow();
        super.onDetachedFromWindow();
    }

    @Override
    public float getMinimumScale() {
        return mAttacher.getMinimumScale();
    }

    @Override
    public void setMinimumScale(float minimumScale) {
        mAttacher.setMinimumScale(minimumScale);
    }

    @Override
    public float getMediumScale() {
        return mAttacher.getMediumScale();
    }

    @Override
    public void setMediumScale(float mediumScale) {
        mAttacher.setMediumScale(mediumScale);
    }

    @Override
    public float getMaximumScale() {
        return mAttacher.getMaximumScale();
    }

    @Override
    public void setMaximumScale(float maximumScale) {
        mAttacher.setMaximumScale(maximumScale);
    }

    @Override
    public float getScale() {
        return mAttacher.getScale();
    }

    @Override
    public void setScale(float scale) {
        mAttacher.setScale(scale);
    }

    @Override
    public void setScale(float scale, boolean animate) {
        mAttacher.setScale(scale, animate);
    }

    @Override
    public void setScale(float scale, float focalX, float focalY, boolean animate) {
        mAttacher.setScale(scale, focalX, focalY, animate);
    }

    @Override
    public void setZoomTransitionDuration(long duration) {
        mAttacher.setZoomTransitionDuration(duration);
    }

    @Override
    public void setAllowParentInterceptOnEdge(boolean allow) {
        mAttacher.setAllowParentInterceptOnEdge(allow);
    }

    @Override
    public void setOnDoubleTapListener(GestureDetector.OnDoubleTapListener listener) {
        mAttacher.setOnDoubleTapListener(listener);
    }

    @Override
    public void setOnScaleChangeListener(OnScaleChangeListener listener) {
        mAttacher.setOnScaleChangeListener(listener);
    }

    @Override
    public void setOnLongClickListener(OnLongClickListener listener) {
        mAttacher.setOnLongClickListener(listener);
    }

    @Override
    public OnPhotoTapListener getOnPhotoTapListener() {
        return mAttacher.getOnPhotoTapListener();
    }

    @Override
    public void setOnPhotoTapListener(OnPhotoTapListener listener) {
        mAttacher.setOnPhotoTapListener(listener);
    }

    @Override
    public OnViewTapListener getOnViewTapListener() {
        return mAttacher.getOnViewTapListener();
    }

    @Override
    public void setOnViewTapListener(OnViewTapListener listener) {
        mAttacher.setOnViewTapListener(listener);
    }

    @Override
    public void update(int imageInfoWidth, int imageInfoHeight) {
        mAttacher.update(imageInfoWidth, imageInfoHeight);
    }

    /**
     *
     * @return
     */
    public Attacher getAttacher() {
        return mAttacher;
    }

    public boolean isEnableDraweeMatrix() {
        return mEnableDraweeMatrix;
    }

    public void setEnableDraweeMatrix(boolean enableDraweeMatrix) {
        mEnableDraweeMatrix = enableDraweeMatrix;
    }

    /**
     * @param uri
     */
    public void setPhotoUri(String uri) {
        setPhotoUri(null, Uri.parse(uri), getContext(), false);
    }

    /**
     * @param uri
     * @param isGifAutoPlay
     */
    public void setPhotoUri(String uri, boolean isGifAutoPlay) {
        setPhotoUri(null, Uri.parse(uri), getContext(), isGifAutoPlay);
    }

    /**
     * @param lowResPath
     * @param uri
     * @param isGifAutoPlay
     */
    public void setPhotoUri(String lowResPath, String uri, boolean isGifAutoPlay) {
        setPhotoUri(TextUtils.isEmpty(lowResPath) ? null : Uri.parse(lowResPath), Uri.parse(uri), getContext(), isGifAutoPlay);
    }

    /**
     * @param lowResUri
     * @param uri
     * @param context
     * @param isGifAutoPlay
     */
    public void setPhotoUri(Uri lowResUri, Uri uri, @Nullable Context context, boolean isGifAutoPlay) {
        mEnableDraweeMatrix = false;
        ImageRequestBuilder imageRequestBuilder = ImageRequestBuilder.newBuilderWithSource(uri);
        if (UriUtil.isNetworkUri(uri)) {
//            imageRequestBuilder.setProgressiveRenderingEnabled(true);
        }
        imageRequestBuilder.setLocalThumbnailPreviewsEnabled(true);
        imageRequestBuilder.setAutoRotateEnabled(true);
        imageRequestBuilder.setResizeOptions(new ResizeOptions(dip2px(200), dip2px(200)));

        PipelineDraweeControllerBuilder pipelineController = Fresco.newDraweeControllerBuilder()
                .setCallerContext(context)
                .setAutoPlayAnimations(isGifAutoPlay)
                .setImageRequest(imageRequestBuilder.build())
                .setOldController(getController())
                .setControllerListener(new BaseControllerListener<ImageInfo>() {
                    @Override
                    public void onFailure(String id, Throwable throwable) {
                        super.onFailure(id, throwable);
                        mEnableDraweeMatrix = false;
                    }

                    @Override
                    public void onFinalImageSet(String id, ImageInfo imageInfo,
                                                Animatable animatable) {
                        super.onFinalImageSet(id, imageInfo, animatable);
                        mEnableDraweeMatrix = true;
                        if (imageInfo != null) {
                            update(imageInfo.getWidth(), imageInfo.getHeight());
                            if (ImageDeal.isLargerPicture(imageInfo.getWidth(), imageInfo.getHeight())) {
                                PhotoDraweeView.this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                            }
                        }
                    }

                    @Override
                    public void onIntermediateImageFailed(String id, Throwable throwable) {
                        super.onIntermediateImageFailed(id, throwable);
                        mEnableDraweeMatrix = false;
                    }

                    @Override
                    public void onIntermediateImageSet(String id, ImageInfo imageInfo) {
                        super.onIntermediateImageSet(id, imageInfo);
                        mEnableDraweeMatrix = true;
                        if (imageInfo != null) {
                            update(imageInfo.getWidth(), imageInfo.getHeight());
                        }
                    }
                });

        if (lowResUri != null)
            pipelineController.setLowResImageRequest(ImageRequest.fromUri(lowResUri));

        setController(pipelineController.build());
    }



    /**
     * 展示进度条
     * */
    public void showLoading(boolean isShow){
        if(!isShow){
            return;
        }

        GenericDraweeHierarchy hierarchy = new GenericDraweeHierarchyBuilder(getContext().getResources())
//                .setProgressBarImage((Drawable) (roundingParams))
                .setProgressBarImage(new ImageLoadingDrawable(getContext()))
//                .setProgressBarImage(new ProgressBarDrawable())
                .setProgressBarImageScaleType(ScalingUtils.ScaleType.CENTER_INSIDE)
                .setFailureImage(com.taihe.photoselect.R.mipmap.icon_fail_image)
                .setFailureImageScaleType(ScalingUtils.ScaleType.CENTER_INSIDE)
                .setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER)
                .build();
        setHierarchy(hierarchy);
    }


    private int dip2px(float dipValue) {
        return (int) (dipValue * getContext().getResources().getDisplayMetrics().density + 0.5f);
    }
}
