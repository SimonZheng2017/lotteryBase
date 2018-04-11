package photoselector.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.media.ThumbnailUtils;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

/**
 * 图片加载类
 *
 * @author sofia
 */
public class ImageManager {

    private static ImageManager imageManager;
    private LruCache<String, Bitmap> mMemoryCache;

    /**
     * 图片加载队列，后进先出
     */
    private Stack<ImageRef> mImageQueue = new Stack<>();

    /**
     * 图片请求队列，先进先出，用于存放已发送的请求。
     */
    private Queue<ImageRef> mRequestQueue = new LinkedList<>();

    /**
     * 图片加载线程消息处理器
     */
    private Handler mImageLoaderHandler;

    /**
     * 图片加载线程是否就绪
     */
    private boolean mImageLoaderIdle = true;

    /**
     * 请求图片
     */
    private static final int MSG_REQUEST = 1;
    /**
     * 图片加载完成
     */
    private static final int MSG_REPLY = 2;
    /**
     * 中止图片加载线程
     */
    private static final int MSG_STOP = 3;

    /**
     * 如果图片是从网络加载，则应用渐显动画，如果从缓存读出则不应用动画
     */
    private boolean isFromNet = true;

    /**
     * 获取单例，只能在UI线程中使用。
     *
     * @param context
     * @return
     */
    public static ImageManager from(Context context) {

        // 如果不在ui线程中，则抛出异常
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new RuntimeException("Cannot instantiate outside UI thread.");
        }

        if (imageManager == null) {
            imageManager = new ImageManager(context);
        }

        return imageManager;
    }

    /**
     * 私有构造函数，保证单例模式
     *
     * @param context
     */
    private ImageManager(Context context) {
//        int memClass = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
//        memClass = memClass > 32 ? 32 : memClass;
        final int cacheSize = 1024 * 1024 * 40;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getRowBytes() * bitmap.getHeight();
            }
        };
    }

    /**
     * 存放图片信息
     */
    private class ImageRef {

        /*
         * 图片对应ImageView控件
         */
        ImageView imageView;

        /**
         * 图片缓存路径
         */
        String filePath;

        /**
         * 默认图资源ID
         */
        int resId;
        int width = 0;
        int height = 0;

        public ImageRef(ImageView imageView, String filePath, int resId, int width, int height) {
            this.imageView = imageView;
            this.filePath = filePath;
            this.resId = resId;
            this.width = width;
            this.height = height;
        }
    }

    /**
     * 显示图片固定大小图片的缩略图，一般用于显示列表的图片，可以大大减小内存使用
     *
     * @param imageView 加载图片的控件
     * @param filePath  加载地址
     * @param resId     默认图片
     * @param width     指定宽度
     * @param height    指定高度
     */
    public void displayImage(ImageView imageView, String filePath, int resId, int width, int height) {
        if (imageView == null) {
            return;
        }
        if (resId >= 0) {
            imageView.setImageResource(resId);
        }
        if (filePath == null || filePath.equals("")) {
            return;
        }

        // 添加url tag
        imageView.setTag(filePath);
        // 读取map缓存
        Bitmap bitmap = mMemoryCache.get(filePath + width + height);
        if (bitmap != null) {
            setImageBitmap(imageView, bitmap, false);
            return;
        }

        queueImage(new ImageRef(imageView, filePath, resId, width, height));
    }

    /**
     * 入队，后进先出
     *
     * @param imageRef
     */
    public void queueImage(ImageRef imageRef) {

        // 删除已有ImageView
        Iterator<ImageRef> iterator = mImageQueue.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().imageView == imageRef.imageView) {
                iterator.remove();
            }
        }

        // 添加请求
        mImageQueue.push(imageRef);
        sendRequest();
    }

    /**
     * 发送请求
     */
    private void sendRequest() {

        // 开启图片加载线程
        if (mImageLoaderHandler == null) {
            HandlerThread imageLoader = new HandlerThread("image_loader");
            imageLoader.start();
            mImageLoaderHandler = new ImageLoaderHandler(imageLoader.getLooper());
        }

        // 发送请求
        if (mImageLoaderIdle && mImageQueue.size() > 0) {
            ImageRef imageRef = mImageQueue.pop();
            Message message = mImageLoaderHandler.obtainMessage(MSG_REQUEST, imageRef);
            mImageLoaderHandler.sendMessage(message);
            mImageLoaderIdle = false;
            mRequestQueue.add(imageRef);
        }
    }

    /**
     * 图片加载线程
     */
    class ImageLoaderHandler extends Handler {

        public ImageLoaderHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            if (msg == null)
                return;

            switch (msg.what) {

                case MSG_REQUEST: // 收到请求
                    Bitmap bitmap = null;
                    Bitmap tBitmap = null;
                    if (msg.obj != null && msg.obj instanceof ImageRef) {

                        ImageRef imageRef = (ImageRef) msg.obj;
                        String url = imageRef.filePath;
                        if (url == null)
                            return;
                        BitmapFactory.Options opt = new BitmapFactory.Options();
                        opt.inSampleSize = 1;
                        opt.inJustDecodeBounds = true;
                        BitmapFactory.decodeFile(url, opt);
                        int bitmapSize = opt.outHeight * opt.outWidth * 4;
                        opt.inSampleSize = bitmapSize / (1000 * 2000);
                        opt.inJustDecodeBounds = false;
                        tBitmap = BitmapFactory.decodeFile(url, opt);
                        if (imageRef.width != 0 && imageRef.height != 0) {
                            bitmap = ThumbnailUtils.extractThumbnail(tBitmap, imageRef.width, imageRef.height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
                            isFromNet = true;
                        } else {
                            bitmap = tBitmap;
                            tBitmap = null;
                        }

                        if (bitmap != null) {
                            if (imageRef.width != 0 && imageRef.height != 0) {
                                if (mMemoryCache.get(url + imageRef.width + imageRef.height) == null)
                                    mMemoryCache.put(url + imageRef.width + imageRef.height, bitmap);
                            } else {
                                if (mMemoryCache.get(url) == null)
                                    mMemoryCache.put(url, bitmap);
                            }

                            if (mImageManagerHandler != null) {
                                Message message = mImageManagerHandler.obtainMessage(MSG_REPLY, bitmap);
                                mImageManagerHandler.sendMessage(message);
                            }
                        }
                    }
                    break;
                case MSG_STOP: // 收到终止指令
                    Looper.myLooper().quit();
                    break;
            }
        }
    }

    /**
     * UI线程消息处理器
     */
    private Handler mImageManagerHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (msg != null) {
                switch (msg.what) {
                    case MSG_REPLY: // 收到应答
                        ImageRef imageRef = mRequestQueue.remove();
                        if (imageRef == null)
                            break;
                        if (imageRef.imageView == null || imageRef.imageView.getTag() == null || imageRef.filePath == null)
                            break;
                        if (!(msg.obj instanceof Bitmap) || msg.obj == null) {
                            break;
                        }
                        Bitmap bitmap = (Bitmap) msg.obj;
                        // 非同一ImageView
                        if (!(imageRef.filePath).equals(String.valueOf(imageRef.imageView.getTag()))) {
                            break;
                        }
                        setImageBitmap(imageRef.imageView, bitmap, isFromNet);
                        isFromNet = false;
                        break;
                }
            }
            // 设置闲置标志
            mImageLoaderIdle = true;

            // 若服务未关闭，则发送下一个请求。
            if (mImageLoaderHandler != null) {
                sendRequest();
            }
        }
    };

    /**
     * 添加图片显示渐现动画
     */
    private void setImageBitmap(ImageView imageView, Bitmap bitmap,
                                boolean isTran) {
        if (isTran) {
            final TransitionDrawable td = new TransitionDrawable(
                    new Drawable[]{
                            new ColorDrawable(imageView.getContext().getResources().getColor(android.R.color.transparent)),
                            new BitmapDrawable(bitmap)});
            td.setCrossFadeEnabled(true);
            imageView.setImageDrawable(td);
            td.startTransition(100);
        } else {
            imageView.setImageBitmap(bitmap);
        }
    }


    /**
     * Activity#onStop后，ListView不会有残余请求。
     */
    public void stop() {
        // 清空请求队列
        mImageQueue.clear();
    }

}
