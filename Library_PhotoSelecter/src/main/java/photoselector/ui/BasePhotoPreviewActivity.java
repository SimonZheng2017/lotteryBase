package photoselector.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.taihe.photoselect.R;

import java.util.List;

import photoselector.model.PhotoModel;
import photoselector.ui.photodrawee.MultiTouchViewPager;
import photoselector.ui.photodrawee.OnViewTapListener;
import photoselector.util.AnimationUtil;

public class BasePhotoPreviewActivity extends FragmentActivity implements OnPageChangeListener, OnClickListener {

    private MultiTouchViewPager mViewPager;
    private RelativeLayout layoutTop;
    private ImageButton btnBack;
    private TextView tvPercent;
    protected List<PhotoModel> photos;
    protected int current;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photopreview);
        initView();
    }

    /**
     *
     */
    protected void initView() {
        layoutTop = (RelativeLayout) findViewById(R.id.layout_top_app);
        btnBack = (ImageButton) findViewById(R.id.btn_back_app);
        tvPercent = (TextView) findViewById(R.id.tv_percent_app);
        mViewPager = (MultiTouchViewPager) findViewById(R.id.vp_base_app);

        btnBack.setOnClickListener(this);
        mViewPager.addOnPageChangeListener(this);

        overridePendingTransition(R.anim.activity_alpha_action_in, 0); // 渐入效果
    }

    /**
     * 绑定数据，更新界面
     */
    protected void bindData() {
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setCurrentItem(current);
    }

    private PagerAdapter mPagerAdapter = new PagerAdapter() {

        @Override
        public int getCount() {
            if (photos == null) {
                return 0;
            } else {
                return photos.size();
            }
        }

        @Override
        public View instantiateItem(final ViewGroup container, final int position) {
            PhotoFullscreenPreview photoPreview = new PhotoFullscreenPreview(getApplicationContext());
            photoPreview.loadImageFromAlum("file://" + photos.get(position).getOriginalPath(), getIntent().getBooleanExtra("isAutoGifPlay", true));
            photoPreview.setOnViewTapListener(new OnViewTapListener() {
                @Override
                public void onViewTap(View view, float x, float y) {
                    if (photoItemClickListener != null)
                        photoItemClickListener.onClick(view);
                }
            });

            try {
                container.addView(photoPreview, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return photoPreview;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    };
    protected boolean isUp;

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_back_app)
            finish();
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    @Override
    public void onPageSelected(int arg0) {
        current = arg0;
        updatePercent();
    }

    protected void updatePercent() {
        tvPercent.setText((current + 1) + "/" + photos.size());
    }

    /**
     * 图片点击事件回调
     */
    private OnClickListener photoItemClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!isUp) {
                new AnimationUtil(getApplicationContext(), R.anim.translate_up)
                        .setInterpolator(new LinearInterpolator()).setFillAfter(true).startAnimation(layoutTop);
                isUp = true;
            } else {
                new AnimationUtil(getApplicationContext(), R.anim.translate_down_current)
                        .setInterpolator(new LinearInterpolator()).setFillAfter(true).startAnimation(layoutTop);
                isUp = false;
            }
        }
    };
}
