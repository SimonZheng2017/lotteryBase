package photoselector.ui;

import android.Manifest;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.taihe.photoselect.R;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import photoselector.domain.PhotoSelectorDomain;
import photoselector.model.AlbumModel;
import photoselector.model.PhotoModel;
import photoselector.util.CommonUtils;
import photoselector.util.FileUtil;
import photoselector.util.FrescoUtil;

public class PhotoSelectorActivity extends FragmentActivity implements
        PhotoItem.onItemClickListener, PhotoItem.onPhotoItemCheckedListener, OnItemClickListener,
        OnClickListener {
    //相册目录选择后是否需要重新选择
    private boolean isNeedRelectAfterSelect = false;

    //是否允许多选
    public static final String IS_SELECTOR_MORE = "is_selector_more";
    public static final String IS_FROM_Chat = "IS_FROM_Chat";
    public static final String MaxSelctNum = "MaxSelctNum";
    //是否是直接进入拍照
    public static final String IS_CARMA = "is_carma";

    private static final int REQUEST_CAMERA = 120;

    private String imagePath;

    private SlidedownView folderSelect;

    private GridView gvPhotos;
    private ListView lvAblum;
    private TextView btnOk;
    private View btnSelectCarma;
    private TextView tvAlbum, tvPreview, tvTitle;
    private PhotoSelectorDomain photoSelectorDomain;
    private PhotoSelectorAdapter photoAdapter;
    private AlbumAdapter albumAdapter;
    private HashMap<String, PhotoModel> selected = new HashMap<>();
    private ArrayList<PhotoModel> selectArray = new ArrayList<>();

    private boolean isSelectorMore = true, isFromChat = false;
    private boolean isCarma = false;
    private int maxSelectNum = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photoselector);

        dealOnRestoreState(savedInstanceState);

        RxPermissions.getInstance(this)
                .request(Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe(aBoolean -> {
                    if (aBoolean) {
                        initView();
                    } else {
                        finish();
                    }
                });
    }

    /**
     *
     */
    private void InitFolderSelect() {
        folderSelect = new SlidedownView(this, (ViewGroup) findViewById(R.id.behindheadview));
        folderSelect.setScrollFromDownToUp();
        View view = getLayoutInflater().inflate(R.layout.select_folder_layout, null);
        lvAblum = (ListView) view.findViewById(R.id.lv_ablum_ar);
        ViewGroup.LayoutParams params = lvAblum.getLayoutParams();
        params.height = getViewHeight();
        lvAblum.setLayoutParams(params);
        folderSelect.setContentView(view);

        albumAdapter = new AlbumAdapter(getApplicationContext(), new ArrayList<AlbumModel>());
        albumAdapter.setIsGifAutoPlay(!isFromChat);
        lvAblum.setAdapter(albumAdapter);
        lvAblum.setOnItemClickListener(this);

        folderSelect.setCallback(new SlidedownView.onLifecallBack() {
            @Override
            public void isDisplay(boolean isShow) {
                setCustomArrow(isShow ? 1 : 2);
            }

            @Override
            public void start(boolean isShow) {
            }
        });
    }

    /**
     * @param isUp 1 箭头向上
     *             2 箭头向下
     *             3 没有箭头
     */
    public void setCustomArrow(int isUp) {
        Drawable drawable = null;
        if (isUp == 1) {
            drawable = this.getResources().getDrawable(R.mipmap.icon_arrow_down);
        } else if (isUp == 2) {
            drawable = this.getResources().getDrawable(R.mipmap.icon_arrow_up);
        }
        if (drawable != null)
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        tvAlbum.setCompoundDrawables(null, null, drawable, null);
    }

    /**
     * @return
     */
    private int getViewHeight() {
        int height = this.getResources().getDisplayMetrics().heightPixels - (int) (45 * this.getResources().getDisplayMetrics().density + 0.5f);
        return (height * 2) / 3;
    }

    /**
     *
     */
    private void checkIsSeletMoreOkay() {
        if (isSelectorMore && maxSelectNum <= 1) {
            throw new IllegalArgumentException("多选的时候，传入选择的数量（MaxSelctNum）必须大于1");
        }
    }

    /**
     *
     */
    private void FrescoInit() {
        FrescoUtil.Init(this);
    }

    /**
     *
     */
    protected void initView() {
        isSelectorMore = getIntent().getBooleanExtra(IS_SELECTOR_MORE, false);
        isFromChat = getIntent().getBooleanExtra(IS_FROM_Chat, false);
        maxSelectNum = getIntent().getIntExtra(MaxSelctNum, 1);
        isCarma = getIntent().getBooleanExtra(IS_CARMA, false);

        checkIsSeletMoreOkay();

        photoSelectorDomain = new PhotoSelectorDomain(getApplicationContext());

        FrescoInit();

        InitFolderSelect();

        btnSelectCarma = findViewById(R.id.btnSelectCarma);
//        if (isFromChat)
//            btnSelectCarma.setVisibility(View.INVISIBLE);
        tvTitle = (TextView) findViewById(R.id.tv_title_lh);
        gvPhotos = (GridView) findViewById(R.id.gv_photos_ar);

        btnOk = (TextView) findViewById(R.id.btnSure);
        tvAlbum = (TextView) findViewById(R.id.tv_album_ar);
        tvPreview = (TextView) findViewById(R.id.tv_preview_ar);

        btnOk.setOnClickListener(this);
        tvAlbum.setOnClickListener(this);
        tvPreview.setOnClickListener(this);

        photoAdapter = new PhotoSelectorAdapter(getApplicationContext(), new ArrayList<>(), CommonUtils.getScreenWidthPixels(this), this, this);
        photoAdapter.setIsGifAutoPlay(!isFromChat);
        photoAdapter.setPathSelect(selected);
        photoAdapter.setCanSelectNum(maxSelectNum);
        gvPhotos.setAdapter(photoAdapter);

        findViewById(R.id.bv_back_lh).setOnClickListener(this); // 返回
        findViewById(R.id.btnCancle).setOnClickListener(this);
        findViewById(R.id.btnSelectCarma).setOnClickListener(this);

        photoSelectorDomain.getReccent(reccentListener); // 更新最近照片
        photoSelectorDomain.updateAlbum(albumListener); // 跟新相册信息

        dealSureTextDisplay();

        if (isCarma) {
            catchPicture();
        }

    }

    /**
     *
     */
    private void dealSureTextDisplay() {
        if (selected.size() > 0) {
            btnOk.setEnabled(true);
        } else {
            btnOk.setEnabled(false);
        }

//        if (maxSelectNum == Integer.MAX_VALUE) {
        btnOk.setText("完成(" + selected.size() + ")");
//        } else {
//            btnOk.setText("完成(" + selected.size() + "/" + maxSelectNum + ")");
//        }

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnSure)
            ok(); // 选完照片
        else if (v.getId() == R.id.tv_album_ar)
            folderSelect.ToggleView();
        else if (v.getId() == R.id.tv_preview_ar)
            priview();
        else if (v.getId() == R.id.btnSelectCarma)
            catchPicture();
        else if (v.getId() == R.id.bv_back_lh || v.getId() == R.id.btnCancle)
            onBackPressed();

    }

    /**
     * 拍照
     */
    private void catchPicture() {
        RxPermissions.getInstance(this)
                .request(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(aBoolean -> {
                    if (aBoolean) {
                        OpenLocalCameraTakePicture();
                    }
                });
    }

    /**
     * 打开本地照相机照相
     */
    private void OpenLocalCameraTakePicture() {
        imagePath = preparePicturePath("vae_capture_");

        // 打开Camera
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(imagePath)));
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    /**
     * 文件放哪里
     */
    private String preparePicturePath(String head) {
        return FileUtil.preparePicturePath(this, head);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {
            onCameraPicSave(imagePath);
        } else {
            if (isCarma)
                finish();
        }
    }

    /**
     * 拍照返回，重启
     *
     * @param path
     */
    private void onCameraPicSave(String path) {
        if (!TextUtils.isEmpty(path) && new File(path).exists()) {
            PhotoModel photoModel = new PhotoModel(path);
            selected.clear();
            selected.put(photoModel.getOriginalPath(), photoModel);

            selectArray.clear();
            selectArray.add(photoModel);

            /**更新媒体库*/
            Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            scanIntent.setData(Uri.fromFile(new File(path)));
            sendBroadcast(scanIntent);
            ok();
        } else {
            Toast.makeText(this, "获取图片失败", Toast.LENGTH_SHORT).show();
        }
    }

//    @Override
//    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//        dealOnRestoreState(savedInstanceState);
//    }

    /**
     * @param bundle
     */
    private void dealOnRestoreState(Bundle bundle) {
        if (bundle != null) {
            String path = bundle.getString("imagePath");
            if (!TextUtils.isEmpty(path)) {
                onCameraPicSave(path);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (!TextUtils.isEmpty(imagePath)) {
            outState.putString("imagePath", imagePath);
        }
        super.onSaveInstanceState(outState);
    }

    /**
     * @return
     */
    private ArrayList<PhotoModel> getList() {
        //顺序没法保证
//        ArrayList<PhotoModel> list = new ArrayList<>();
//        Iterator iter = selected.entrySet().iterator();
//        while (iter.hasNext()) {
//            Map.Entry<String, PhotoModel> entry = (Map.Entry) iter.next();
//            list.add(entry.getValue());
//        }
//
//        return list;

        return selectArray;
    }

    /**
     * 完成
     */
    private void ok() {
        if (selected.isEmpty()) {
            setResult(RESULT_CANCELED);
        } else {
            Intent data = new Intent();
            Bundle bundle = new Bundle();
            bundle.putSerializable("photos", getList());
            data.putExtras(bundle);
            setResult(RESULT_OK, data);
        }
        finish();
    }

    /**
     * 预览照片
     */
    private void priview() {
        Bundle bundle = new Bundle();
        bundle.putSerializable("photos", getList());
        bundle.putSerializable("isAutoGifPlay", !isFromChat);
        CommonUtils.launchActivity(this, PhotoPreviewActivity.class, bundle);
    }

    /**
     * 清空选中的图片
     */
    private void reset() {
        selected.clear();
        selectArray.clear();

        tvPreview.setText("预览");
        tvPreview.setEnabled(false);

        dealSureTextDisplay();
    }

    @Override
    /** 点击查看照片 */
    public void onItemClick(int position) {
        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        bundle.putString("album", tvAlbum.getText().toString());
        bundle.putSerializable("isAutoGifPlay", !isFromChat);
        CommonUtils.launchActivity(this, PhotoPreviewActivity.class, bundle);
    }

    @Override
    /** 照片选中状态改变之后 */
    public void onCheckedChanged(PhotoModel photoModel,
                                 CompoundButton buttonView, boolean isChecked) {
        if (isSelectorMore) {

            //允许多选
            if (isChecked) {
                selected.put(photoModel.getOriginalPath(), photoModel);
                selectArray.add(photoModel);

                tvPreview.setEnabled(true);
            } else {
                selected.remove(photoModel.getOriginalPath());
                selectArray.remove(photoModel);
            }

//            if (selected.size() > maxSelectNum) {
//                selected.remove(photoModel.getOriginalPath());
//                photoModel.setChecked(false);
//                photoAdapter.notifyDataSetChanged();
//                Toast.makeText(this, "最多可以选择" + maxSelectNum + "张图片", Toast.LENGTH_LONG).show();
//            }

            tvPreview.setText("预览(" + selected.size() + ")"); // 修改预览数量

            if (selected.isEmpty()) {
                tvPreview.setEnabled(false);
                tvPreview.setText("预览");
            }

            dealSureTextDisplay();

        } else {
            //不允许多选

            selected.clear();

            if (isChecked) {
                selected.put(photoModel.getOriginalPath(), photoModel);
                selectArray.add(photoModel);

                tvPreview.setEnabled(true);
            } else {
                selected.remove(photoModel.getOriginalPath());
                selectArray.remove(photoModel);
            }

            tvPreview.setText("预览(" + selected.size() + ")"); // 修改预览数量
            if (selected.isEmpty()) {
                tvPreview.setEnabled(false);
                tvPreview.setText("预览");
            }

            dealSureTextDisplay();

//            ok();//单选后还是选择确定才关闭
        }
    }

    @Override
    public void onBackPressed() {
        if (folderSelect != null && folderSelect.isShowing()) {
            folderSelect.ToggleView();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    /** 相册列表点击事件 */
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        AlbumModel current = (AlbumModel) parent.getItemAtPosition(position);
        for (int i = 0; i < parent.getCount(); i++) {
            AlbumModel album = (AlbumModel) parent.getItemAtPosition(i);
            if (i == position)
                album.setCheck(true);
            else
                album.setCheck(false);
        }
        albumAdapter.notifyDataSetChanged();

        try {
            folderSelect.ToggleView();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

        tvAlbum.setText(current.getName());
        tvTitle.setText(current.getName());

        // 更新照片列表
        if (current.getName().equals(getResources().getString(R.string.str_allPicture)))
            photoSelectorDomain.getReccent(reccentListener);
        else
            photoSelectorDomain.getAlbum(current.getName(), reccentListener); // 获取选中相册的照片
    }

    /**
     * 获取本地图库照片回调
     */
    public interface OnLocalReccentListener {
        void onPhotoLoaded(List<PhotoModel> photos);
    }

    /**
     * 获取本地相册信息回调
     */
    public interface OnLocalAlbumListener {
        void onAlbumLoaded(List<AlbumModel> albums);
    }

    /**
     * 相册目录
     */
    private OnLocalAlbumListener albumListener = new OnLocalAlbumListener() {
        @Override
        public void onAlbumLoaded(List<AlbumModel> albums) {
            albumAdapter.setList(albums);
        }
    };

    /**
     * 获取到最新的相册列表数据
     */
    private OnLocalReccentListener reccentListener = new OnLocalReccentListener() {
        @Override
        public void onPhotoLoaded(List<PhotoModel> photos) {
            photoAdapter.setList(photos);
            gvPhotos.smoothScrollToPosition(0); // 滚动到顶端

            if (isNeedRelectAfterSelect)
                reset();
        }
    };
}
