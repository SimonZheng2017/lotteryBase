package photoselector.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;

import com.taihe.photoselect.BuildConfig;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Android大小单位转换工具类
 *
 * @author wader
 */
public class ImageDeal {
    //如果图片文件大小大于250k才压缩，
    private static double sizeBigThan = 250.00d;
    //高比宽大于OPGL_MAX_TEXTURE倍，我们认为就是长图
    private static final int OPGL_MAX_TEXTURE = 4;

    /**
     * 读取图片属性：旋转的角度
     *
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /*
     * 旋转图片
     * @param angle
     * @param bitmap
     * @return Bitmap
     */
    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        if (angle == 0) {
            return bitmap;
        }
        // 旋转图片 动作
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizedBitmap;
    }

    /**
     * 进行相应的计算
     *
     * @param options
     * @param minSideLength
     * @param maxNumOfPixels
     * @return
     */
    private static int computeSampleSize(BitmapFactory.Options options,
                                         int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);

        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }

        return roundedSize;
    }

    /**
     * 进行相应的计算
     *
     * @param options
     * @param minSideLength
     * @param maxNumOfPixels
     * @return
     */
    private static int computeInitialSampleSize(BitmapFactory.Options options,
                                                int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;

        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
                .sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math
                .floor(w / minSideLength), Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }

        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    /**
     * 进行相应的计算
     *
     * @param dst
     * @param width
     * @param height
     * @return
     */
    public static Bitmap getBitmapFromFile(File dst, int width, int height) {
        if (null != dst && dst.exists()) {
            BitmapFactory.Options opts = null;
            int degree = readPictureDegree(dst.getAbsolutePath());
            if (width > 0 && height > 0) {
                opts = new BitmapFactory.Options();
                opts.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(dst.getPath(), opts);
                // 计算图片缩放比例
                final int minSideLength = Math.min(width, height);
                opts.inSampleSize = computeSampleSize(opts, minSideLength, width * height);
                opts.inJustDecodeBounds = false;
                opts.inInputShareable = true;
                opts.inPurgeable = true;
            }
            try {
                return rotaingImageView(degree, BitmapFactory.decodeFile(dst.getPath(), opts));
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    /**
     * @param file
     * @return
     */
    public static BitmapFactory.Options getImageOpt(File file) {
        if (file != null && file.exists()) {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(file.getPath(), opts);
            return opts;
        }

        return null;
    }

    /**
     * @return
     */
    private static String getDebugString(String file) {
        String temp = FileSizeUtil.getAutoFileOrFilesSize(file) + "----";

        BitmapFactory.Options options = getImageOpt(new File(file));
        if (options != null)
            temp += options.outWidth + "*" + options.outHeight;

        return temp;
    }

    /**
     * @param file
     * @return
     */
    public static int[] getFilePicSize(String file) {
        if (file.startsWith("file://"))
            file = file.substring("file://".length());
        BitmapFactory.Options options = getImageOpt(new File(file));

        if (options != null) {
            return new int[]{options.outWidth, options.outHeight};
        }
        return null;
    }

    /**
     * @param file
     * @return
     */
    public static boolean isLargerPicture(String file) {
        if (file.startsWith("file://"))
            file = file.substring("file://".length());
        BitmapFactory.Options options = getImageOpt(new File(file));
        if (options != null)
            return isLargerPicture(options.outWidth, options.outHeight);
        return false;
    }

    /**
     * @param width
     * @param height
     * @return
     */
    public static boolean isLargerPicture(int width, int height) {
        return height > width * OPGL_MAX_TEXTURE;
    }

    /**
     * 上传图片统一压缩
     *
     * @param ctx
     * @param orignalPath 图片原地址
     * @return 压缩后的地址
     */
    private static String zipPictureToXX_XX(Context ctx, String orignalPath) {

        if (orignalPath.endsWith(".gif"))
            return orignalPath;

        String tempFile = orignalPath;
        int degree = readPictureDegree(orignalPath);
        if (FileSizeUtil.getFileOrFilesSize(orignalPath, FileSizeUtil.SIZETYPE_KB) > sizeBigThan || degree > 0) {
            tempFile = FileUtil.getPicUploadTempPath(ctx, orignalPath);
            Bitmap bm = null;
            try {
                bm = getBitmapFromFile(new File(orignalPath), 720, 1280);
                FileOutputStream fos = new FileOutputStream(new File(tempFile));
                bm.compress(Bitmap.CompressFormat.JPEG, 95, fos);
            } catch (Exception e) {
            } finally {
                bm = null;
                logDeug("图片压缩", "图片压缩后大小" + getDebugString(tempFile));
            }
        } else {
            logDeug("图片压缩", "图片不需要压缩，直接上传");
        }

        return tempFile;
    }

    /**
     * 相对来说比较高清的一种压缩方式
     *
     * @param ctx
     * @param orignalPath
     */
    public static String compressPic(Context ctx, String orignalPath) {
        if (orignalPath.endsWith(".gif"))
            return orignalPath;
        logDeug("图片压缩", "图片原大小" + getDebugString(orignalPath));
        String tempFile = orignalPath;
        int degree = readPictureDegree(orignalPath);
        if (FileSizeUtil.getFileOrFilesSize(orignalPath, FileSizeUtil.SIZETYPE_KB) > sizeBigThan || degree > 0) {
            tempFile = FileUtil.getPicUploadTempPath(ctx, orignalPath);
            Bitmap bm = null;
            try {
                bm = getBitmapFromFile(new File(orignalPath), 1080, 1920);
                FileOutputStream fos = new FileOutputStream(new File(tempFile));
                bm.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            } catch (Exception e) {
            } finally {
                bm = null;
                logDeug("图片压缩", "图片压缩后大小" + getDebugString(tempFile));
            }
        } else {
            logDeug("图片压缩", "图片不需要压缩，直接上传");
        }

        return tempFile;
    }

    /**
     * @param ctx
     * @param orignalPath
     * @param listener
     */
    public static void compressPic(final Context ctx, final String orignalPath, final onPicCompressListener listener) {
        logDeug("图片压缩", "图片原大小" + getDebugString(orignalPath));
        if (orignalPath.endsWith(".gif") || FileUtil.isVolumeFile(orignalPath)) {
            if (listener != null)
                listener.onPicCompressResult(orignalPath);
            return;
        }
        int degree = readPictureDegree(orignalPath);
        if (FileSizeUtil.getFileOrFilesSize(orignalPath, FileSizeUtil.SIZETYPE_KB) > sizeBigThan || degree > 0) {
            Luban.get(ctx)
                    .load(new File(checkifNeedRoateImage(orignalPath)))
                    .putGear(Luban.THIRD_GEAR)
                    .setFilename(FileUtil.getPicUploadTempName(orignalPath))
                    .setCompressListener(new OnCompressListener() {
                        @Override
                        public void onStart() {
                        }

                        @Override
                        public void onSuccess(File file) {
                            logDeug("图片压缩", "图片压缩后大小" + getDebugString(file.getAbsolutePath()));
                            if (listener != null)
                                listener.onPicCompressResult(file.getAbsolutePath());
                        }

                        @Override
                        public void onError(Throwable e) {
                            if (listener != null)
                                listener.onPicCompressResult(zipPictureToXX_XX(ctx, orignalPath));
                        }
                    }).launch();
        } else {
            logDeug("图片压缩", "图片不需要压缩，直接上传");
            if (listener != null)
                listener.onPicCompressResult(orignalPath);
        }
    }

    /**
     * @param path
     * @return
     */
    private static String checkifNeedRoateImage(String path) {
        int degree = readPictureDegree(path);
        if (degree == 0)
            return path;

        logDeug("图片压缩", "图片需要旋转" + degree);

        Bitmap bm = null;
        try {
            bm = rotaingImageView(degree, BitmapFactory.decodeFile(path));
            FileOutputStream fos = new FileOutputStream(new File(path));
            bm.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (Exception e) {
        } finally {
            bm = null;
            logDeug("图片压缩", "图片旋转后大小" + getDebugString(path));
        }

        return path;
    }

    /**
     * @param TAG
     * @param log
     */
    private static void logDeug(String TAG, String log) {
        if (BuildConfig.DEBUG) {
            Log.d("文件上传" + TAG, log);
        }
    }

    public interface onPicCompressListener {
        public void onPicCompressResult(String file);
    }
}
