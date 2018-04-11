package photoselector.domain;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import java.util.List;

import photoselector.controller.AlbumController;
import photoselector.model.AlbumModel;
import photoselector.model.PhotoModel;
import photoselector.ui.PhotoSelectorActivity;

@SuppressLint("HandlerLeak")
public class PhotoSelectorDomain {

	private AlbumController albumController;

	/**
	 *
	 * @param context
     */
	public PhotoSelectorDomain(Context context) {
		albumController = new AlbumController(context);
	}

	/**
	 * 获取最新的图片
	 * @param listener
     */
	public void getReccent(final PhotoSelectorActivity.OnLocalReccentListener listener) {
		final Handler handler = new Handler() {
			@SuppressWarnings("unchecked")
			@Override
			public void handleMessage(Message msg) {
				listener.onPhotoLoaded((List<PhotoModel>) msg.obj);
			}
		};
		new Thread(new Runnable() {
			@Override
			public void run() {
				List<PhotoModel> photos = albumController.getCurrent();
				Message msg = new Message();
				msg.obj = photos;
				handler.sendMessage(msg);
			}
		}).start();
	}

	/** 获取相册列表 */
	public void updateAlbum(final PhotoSelectorActivity.OnLocalAlbumListener listener) {
		final Handler handler = new Handler() {
			@SuppressWarnings("unchecked")
			@Override
			public void handleMessage(Message msg) {
				listener.onAlbumLoaded((List<AlbumModel>) msg.obj);
			}
		};
		new Thread(new Runnable() {
			@Override
			public void run() {
				List<AlbumModel> albums = albumController.getAlbums();
				Message msg = new Message();
				msg.obj = albums;
				handler.sendMessage(msg);
			}
		}).start();
	}

	/** 获取单个相册下的所有照片信息 */
	public void getAlbum(final String name, final PhotoSelectorActivity.OnLocalReccentListener listener) {
		final Handler handler = new Handler() {
			@SuppressWarnings("unchecked")
			@Override
			public void handleMessage(Message msg) {
				listener.onPhotoLoaded((List<PhotoModel>) msg.obj);
			}
		};
		new Thread(new Runnable() {
			@Override
			public void run() {
				List<PhotoModel> photos = albumController.getAlbum(name);
				Message msg = new Message();
				msg.obj = photos;
				handler.sendMessage(msg);
			}
		}).start();
	}

}
