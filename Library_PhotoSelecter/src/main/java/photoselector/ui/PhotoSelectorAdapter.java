package photoselector.ui;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.taihe.photoselect.R;

import java.util.ArrayList;
import java.util.HashMap;

import photoselector.model.PhotoModel;

public class PhotoSelectorAdapter extends MBaseAdapter<PhotoModel> {

    private int itemWidth;
    private int horizentalNum = 3, canSelectNum = 0;
    private PhotoItem.onPhotoItemCheckedListener listener;
    private PhotoItem.onItemClickListener mCallback;
    private boolean isGifAutoPlay = true;
    private HashMap<String, PhotoModel> pathSelect;

    private PhotoSelectorAdapter(Context context, ArrayList<PhotoModel> models) {
        super(context, models);
    }

    public PhotoSelectorAdapter(Context context, ArrayList<PhotoModel> models, int screenWidth,
                                PhotoItem.onPhotoItemCheckedListener listener, PhotoItem.onItemClickListener mCallback) {
        this(context, models);
        this.listener = listener;
        this.mCallback = mCallback;
        setItemWidth(screenWidth);
    }

    public void setIsGifAutoPlay(boolean autoPlay) {
        isGifAutoPlay = autoPlay;
    }

    public void setCanSelectNum(int canSelectNum) {
        this.canSelectNum = canSelectNum;
    }

    public void setPathSelect(HashMap<String, PhotoModel> pathSelect) {
        this.pathSelect = pathSelect;
    }

    /**
     * 设置每一个Item的宽高
     */
    public void setItemWidth(int screenWidth) {
        int horizentalSpace = context.getResources().getDimensionPixelSize(R.dimen.sticky_item_horizontalSpacing);
        this.itemWidth = (screenWidth - dp2px(context, 10) - (horizentalSpace * (horizentalNum - 1))) / horizentalNum;
    }

    /**
     * @param context
     * @param dipValue
     * @return
     */
    private int dp2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final PhotoItem item;
        if (convertView == null) {
            item = new PhotoItem(context, listener);
            convertView = item;
        } else {
            item = (PhotoItem) convertView;
        }
        item.setImageDrawable(models.get(position), itemWidth, isGifAutoPlay);
        if (pathSelect != null && pathSelect.size() > 0)
            models.get(position).setChecked(pathSelect.containsKey(models.get(position).getOriginalPath()));
        item.setSelected(models.get(position).isChecked());
        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (mCallback != null)
//                    mCallback.onItemClick(position);
                if (!models.get(position).isChecked() && pathSelect != null && pathSelect.size() > 0 ? pathSelect.size() == canSelectNum : false) {
                    Toast.makeText(context, "最多可以选择" + canSelectNum + "张图片", Toast.LENGTH_LONG).show();
                } else {
                    item.checkItem();
                }
            }
        });
        return convertView;
    }


}
