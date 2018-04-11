package photoselector.controller;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore.Images.ImageColumns;
import android.provider.MediaStore.Images.Media;

import com.taihe.photoselect.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import photoselector.model.AlbumModel;
import photoselector.model.PhotoModel;

public class AlbumController {

    private ContentResolver resolver;
    private Context ctx;
    //图片3k
    private int minSize = 1024 * 2;
    
    public AlbumController(Context context) {
        ctx = context;
        resolver = context.getContentResolver();
    }

    /**
     * 获取最近照片列表
     *
     * @return
     */
    public List<PhotoModel> getCurrent() {
        //查询方式更改
        Cursor cursor = resolver.query(Media.EXTERNAL_CONTENT_URI,
                                    new String[]{ImageColumns.DATA, ImageColumns.DATE_ADDED, ImageColumns.SIZE},
                                    null,
                                    null,
                                    ImageColumns.DATE_ADDED);

        if (cursor == null || !cursor.moveToNext())
            return new ArrayList<>();

        List<PhotoModel> photos = new ArrayList<>();
        cursor.moveToLast();
        do {
            if (cursor.getLong(cursor.getColumnIndex(ImageColumns.SIZE)) > minSize) {
                PhotoModel photoModel = new PhotoModel();
                photoModel.setOriginalPath(cursor.getString(cursor.getColumnIndex(ImageColumns.DATA)));
                photos.add(photoModel);
            }
        } while (cursor.moveToPrevious());

        return photos;
    }

    /**
     * 获取所有相册列表
     * @return
     */
    public List<AlbumModel> getAlbums() {
        List<AlbumModel> albums = new ArrayList<>();
        Map<String, AlbumModel> map = new HashMap<>();
        Cursor cursor = resolver.query(Media.EXTERNAL_CONTENT_URI, new String[]{ImageColumns.DATA,ImageColumns.BUCKET_DISPLAY_NAME, ImageColumns.SIZE}, null, null, null);
        if (cursor == null || !cursor.moveToNext())
            return new ArrayList<>();
        cursor.moveToLast();
        AlbumModel current = new AlbumModel(ctx.getResources().getString(R.string.str_allPicture), 0, cursor.getString(cursor.getColumnIndex(ImageColumns.DATA)), true); // "最近照片"相册
        albums.add(current);
        do {
            if (cursor.getInt(cursor.getColumnIndex(ImageColumns.SIZE)) < minSize)
                continue;

            current.increaseCount();
            String name = cursor.getString(cursor.getColumnIndex(ImageColumns.BUCKET_DISPLAY_NAME));
            if (map.keySet().contains(name))
                map.get(name).increaseCount();
            else {
                AlbumModel album = new AlbumModel(name, 1, cursor.getString(cursor.getColumnIndex(ImageColumns.DATA)));
                map.put(name, album);
                albums.add(album);
            }
        } while (cursor.moveToPrevious());

        return albums;
    }

    /**
     * 获取对应相册下的照片
     */
    public List<PhotoModel> getAlbum(String name) {
        Cursor cursor = resolver.query(Media.EXTERNAL_CONTENT_URI,
                new String[]{ImageColumns.BUCKET_DISPLAY_NAME,ImageColumns.DATA, ImageColumns.DATE_ADDED, ImageColumns.SIZE}, "bucket_display_name = ?",
                new String[]{name}, ImageColumns.DATE_ADDED);
        if (cursor == null || !cursor.moveToNext())
            return new ArrayList<>();

        List<PhotoModel> photos = new ArrayList<>();
        cursor.moveToLast();
        do {
            if (cursor.getLong(cursor.getColumnIndex(ImageColumns.SIZE)) > minSize) {
                PhotoModel photoModel = new PhotoModel();
                photoModel.setOriginalPath(cursor.getString(cursor.getColumnIndex(ImageColumns.DATA)));
                photos.add(photoModel);
            }
        } while (cursor.moveToPrevious());

        return photos;
    }
}
