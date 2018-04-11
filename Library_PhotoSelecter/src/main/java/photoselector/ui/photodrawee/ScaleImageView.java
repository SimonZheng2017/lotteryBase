package photoselector.ui.photodrawee;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.facebook.common.references.CloseableReference;
import com.facebook.common.util.UriUtil;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.DraweeHolder;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.image.CloseableStaticBitmap;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.taihe.photoselect.R;

import photoselector.ui.ImageLoadingDrawable;
import photoselector.util.CommonUtils;
import photoselector.util.ImageDeal;


/**
 * Created by Soli on 2016/9/7.
 */
public class ScaleImageView extends SubsamplingScaleImageView {

    private DraweeHolder<GenericDraweeHierarchy> mDraweeHolder;
    private CloseableReference<CloseableImage> imageReference = null;

    private boolean isImageLoadOkay = false;

    public ScaleImageView(Context context) {
        super(context);
        init();
    }

    public ScaleImageView(Context context, AttributeSet attr) {
        super(context, attr);
        init();
    }

    /**
     *
     */
    private void init() {
        if (mDraweeHolder == null) {
//        Object roundingParams = getResources().getDrawable(R.mipmap.fullscreen_picture_load);
//        roundingParams = new AutoRotateDrawable((Drawable) roundingParams, 1000);

            GenericDraweeHierarchy hierarchy = new GenericDraweeHierarchyBuilder(getContext().getResources())
//                .setProgressBarImage((Drawable) (roundingParams))
//                    .setProgressBarImage(new ProgressBarDrawable())
                    .setProgressBarImage(new ImageLoadingDrawable(getContext()))
                    .setProgressBarImageScaleType(ScalingUtils.ScaleType.CENTER_INSIDE)
                    .setFailureImage(R.mipmap.icon_fail_image)
                    .setFailureImageScaleType(ScalingUtils.ScaleType.CENTER_INSIDE)
                    .setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER)
                    .build();
            mDraweeHolder = DraweeHolder.create(hierarchy, getContext());
        }
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mDraweeHolder.onDetach();
        mDraweeHolder.getTopLevelDrawable().setCallback(null);
    }

    @Override
    public void onStartTemporaryDetach() {
        super.onStartTemporaryDetach();
        mDraweeHolder.onDetach();
        mDraweeHolder.getTopLevelDrawable().setCallback(null);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        mDraweeHolder.onAttach();
        mDraweeHolder.getTopLevelDrawable().setCallback(this);
    }

    @Override
    public void onFinishTemporaryDetach() {
        super.onFinishTemporaryDetach();
        mDraweeHolder.onAttach();
        mDraweeHolder.getTopLevelDrawable().setCallback(this);
    }

    @Override
    protected boolean verifyDrawable(Drawable who) {
        if (who == mDraweeHolder.getTopLevelDrawable()) {
            return true;
        }
        return super.verifyDrawable(who);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mDraweeHolder.onTouchEvent(event) || super.onTouchEvent(event);
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

    @Override
    protected void onDraw(Canvas canvas) {
        if (isImageLoadOkay)
            super.onDraw(canvas);
        else {
            Drawable drawable = mDraweeHolder.getTopLevelDrawable();
//            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            drawable.draw(canvas);
        }
    }

    /**
     * @param lowResUri
     * @param uri
     * @param context
     * @param isGifAutoPlay
     */
    public void setPhotoUri(Uri lowResUri, Uri uri, @Nullable Context context, boolean isGifAutoPlay) {
        ImageRequestBuilder imageRequestBuilder = ImageRequestBuilder.newBuilderWithSource(uri);
        if (UriUtil.isNetworkUri(uri)) {
//            imageRequestBuilder.setProgressiveRenderingEnabled(true);
        }
        imageRequestBuilder.setLocalThumbnailPreviewsEnabled(true);
        imageRequestBuilder.setAutoRotateEnabled(true);

        final DataSource<CloseableReference<CloseableImage>> dataSource = Fresco.getImagePipeline().fetchDecodedImage(imageRequestBuilder.build(), this);
        PipelineDraweeControllerBuilder pipelineController = Fresco.newDraweeControllerBuilder()
                .setCallerContext(context)
                .setAutoPlayAnimations(isGifAutoPlay)
                .setImageRequest(imageRequestBuilder.build())
                .setOldController(mDraweeHolder.getController())
                .setControllerListener(new BaseControllerListener<ImageInfo>() {
                                           @Override
                                           public void onFinalImageSet(String s, @Nullable ImageInfo imageInfo, @Nullable Animatable animatable) {
                                               try {
                                                   imageReference = dataSource.getResult();
                                                   if (imageReference != null) {
                                                       CloseableImage image = imageReference.get();
                                                       if (image != null && image instanceof CloseableStaticBitmap) {
                                                           CloseableStaticBitmap closeableStaticBitmap = (CloseableStaticBitmap) image;
                                                           Bitmap bitmap = closeableStaticBitmap.getUnderlyingBitmap();
                                                           if (bitmap != null) {
                                                               isImageLoadOkay = true;
                                                               setImage(ImageSource.bitmap(bitmap));
                                                               setMaxScale(SubsamplingScaleImageView.SCALE_TYPE_CUSTOM);
                                                               setMinimumScaleType(SCALE_TYPE_CUSTOM);
                                                               int width = getWidth();
                                                               int height = getHeight();
                                                               if (width == 0 || height == 0) {
                                                                   width = CommonUtils.getScreenWidthPixels(getContext());
                                                                   height = CommonUtils.getScreenHeightPixels(getContext());
                                                               }
                                                               if ((getSHeight() > height) && getSHeight() / getSWidth() > height / width) {
                                                                   PointF center = new PointF(getSWidth() / 2, 0);
                                                                   float targetScale = Math.max(width / (float) getSWidth(), height / (float) getSHeight());
                                                                   setScaleAndCenter(targetScale, center);
                                                                   setMinScale(targetScale);
                                                               }

                                                               if (ImageDeal.isLargerPicture(getSWidth(), getSHeight())) {
                                                                   ScaleImageView.this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                                                               }
                                                           }
                                                       }
                                                   }
                                               } finally {
                                                   dataSource.close();
                                                   CloseableReference.closeSafely(imageReference);
                                               }
                                           }
                                       }

                );

        if (lowResUri != null)
            pipelineController.setLowResImageRequest(ImageRequest.fromUri(lowResUri));

        mDraweeHolder.setController(pipelineController.build());
    }
}
