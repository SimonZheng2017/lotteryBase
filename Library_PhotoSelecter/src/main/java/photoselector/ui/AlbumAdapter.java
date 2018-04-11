package photoselector.ui;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import photoselector.model.AlbumModel;

public class AlbumAdapter extends MBaseAdapter<AlbumModel> {

    private boolean isGifAutoPlay = true;

    public AlbumAdapter(Context context, ArrayList<AlbumModel> models) {
        super(context, models);
    }

    public void setIsGifAutoPlay(boolean autoPlay) {
        isGifAutoPlay = autoPlay;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AlbumItem albumItem = null;
        if (convertView == null) {
            albumItem = new AlbumItem(context);
            convertView = albumItem;
        } else
            albumItem = (AlbumItem) convertView;
        albumItem.update(models.get(position), isGifAutoPlay);
        return convertView;
    }

}
