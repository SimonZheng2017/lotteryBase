package photoselector.ui;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.common.util.UriUtil;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.taihe.photoselect.R;

import photoselector.model.AlbumModel;


public class AlbumItem extends LinearLayout {

    private PhotoSelectVIew ivAlbum;
    private ImageView ivIndex;
    private TextView tvName, tvCount;

    private int width = 0;

    public AlbumItem(Context context) {
        this(context, null);
    }

    public AlbumItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.layout_album, this, true);

        ivAlbum = (PhotoSelectVIew) findViewById(R.id.iv_album_la);
        ivIndex = (ImageView) findViewById(R.id.iv_index_la);
        tvName = (TextView) findViewById(R.id.tv_name_la);
        tvCount = (TextView) findViewById(R.id.tv_count_la);

        width = context.getResources().getDimensionPixelSize(R.dimen.albumitem_content_height);

        InitHierarchy();
    }

    public AlbumItem(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs);
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

        ivAlbum.setHierarchy(hierarchy);
    }

    /**
     * 设置相册封面
     */
    public void setAlbumImage(final String path,boolean isGifAutoPlay) {

        Uri uri = Uri.parse("file://" +path);
        ImageRequestBuilder imageRequestBuilder = ImageRequestBuilder.newBuilderWithSource(uri);
        if (UriUtil.isNetworkUri(uri)) {
//            imageRequestBuilder.setProgressiveRenderingEnabled(true);
        } else {
            imageRequestBuilder.setResizeOptions(new ResizeOptions(width, width));
        }
        imageRequestBuilder.setAutoRotateEnabled(true);
        DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                .setImageRequest(imageRequestBuilder.build())
                .setOldController(ivAlbum.getController())
                .setAutoPlayAnimations(isGifAutoPlay)
                .build();
        ivAlbum.setController(draweeController);
    }

    /**
     * 初始化
     */
    public void update(AlbumModel album,boolean isFromChat) {
        setAlbumImage(album.getRecent(),isFromChat);
        setName(album.getName());
        setCount(album.getCount());
        isCheck(album.isCheck());
    }

    /**
     * @param title
     */
    public void setName(CharSequence title) {
        tvName.setText(title);
    }

    /**
     * @param count
     */
    public void setCount(int count) {
        tvCount.setText(count + "张");
    }

    /**
     * @param isCheck
     */
    public void isCheck(boolean isCheck) {
        if (isCheck)
            ivIndex.setVisibility(View.VISIBLE);
        else
            ivIndex.setVisibility(View.GONE);
    }

}
