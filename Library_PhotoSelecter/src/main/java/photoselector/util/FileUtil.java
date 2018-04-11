package photoselector.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.text.SimpleDateFormat;

/**
 * simon edit
 */
public class FileUtil {


    /**
     * app图片路径
     */
    public static final String PICTURE = "Pictures";

    /**
     * app拍照路径
     */
    public static final String CAMERA = "Camera";

    /**
     * app拍照或选择的图片缩放后保存的地址
     */
    public static final String UpImageZoom = "upimagezoom";

    /**
     * app下载的图片
     */
    public static final String IMAGE = "Image";

    public static final String ROOT = "frame";


    private static boolean isExternalMemoryAvailable() {
        return Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取目录
     *
     * @param context
     * @return
     */
    public static File getRootDir(Context context, boolean isInAndroidDataFile) {

        File targetDir = null;

        try {
            if (isExternalMemoryAvailable()) {
                if (isInAndroidDataFile)
                    targetDir = new File(context.getExternalCacheDir().getPath() + ROOT);
                else
                    targetDir = new File(Environment.getExternalStorageDirectory(), ROOT);

                if (!targetDir.exists()) {
                    targetDir.mkdirs();
                }
            }

        } catch (Exception e) {
        }


        if (targetDir == null || !targetDir.exists()) {
            targetDir = new File(context.getCacheDir().getAbsolutePath() + "/" + ROOT);
            if (!targetDir.exists()) {
                targetDir.mkdirs();
            }
        }

        return targetDir;
    }


    /**
     * 获取目录
     *
     * @param context
     * @param name
     * @return
     */
    public static File getDir(Context context, String name) {
        File file = new File(getRootDir(context, true), name);
        if (!file.exists()) {
            file.mkdirs();
        }

        return file;
    }


    /**
     * 获取目录
     *
     * @param context
     * @param name
     * @return
     */
    public static File getDir(Context context, String name, boolean isInAndroidDataFile) {
        File file = new File(getRootDir(context, isInAndroidDataFile), name);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }


    /**
     * 获取文件
     *
     * @param context
     * @param dir
     * @param fileName
     * @return
     */
    public static File getFile(Context context, String dir, String fileName) {
        return new File(getDir(context, dir), fileName);
    }

    /**
     * 获取文件
     *
     * @param context
     * @param dir
     * @param fileName
     * @return
     */
    public static File getFile(Context context, String dir, String fileName, boolean isInData) {
        return new File(getDir(context, dir, isInData), fileName);
    }


    /**
     * @param ctx
     * @return
     */
    public static String getPath(Context ctx, String fileName) {
        return getDir(ctx, fileName, false).getAbsolutePath();
    }


    /**
     * @param sourceStr
     * @return
     */
    public static String MD5(String sourceStr) {
        String result = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(sourceStr.getBytes());
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            result = buf.toString();
        } catch (NoSuchAlgorithmException e) {
        }
        return result;
    }

    /**
     * 获取网络下载图片地址保存目录
     *
     * @param context
     * @param imageUrl
     * @return
     */
    public static File getDownLoadFilePath(Context context, String imageUrl) {
        return getDownLoadFilePath(context, imageUrl, false);
    }

    /**
     * @param context
     * @param url
     * @param isIn    保存的位置
     * @return
     */
    public static File getDownLoadFilePath(Context context, String url, boolean isIn) {
        return getFile(context, "download", MD5(url) + getsuffer(url), isIn);
    }

    /**
     * @param ctx
     * @return
     */
    public static File getCrashLogDir(Context ctx) {
        return getDir(ctx, "log", true);
    }

    /**
     * @param url
     * @return
     */
    private static String getsuffer(String url) {

        if (url.indexOf("?") != -1) {
            url = url.substring(0, url.indexOf("?"));
        }

        int index = url.lastIndexOf(".");
        if (index != -1)
            return url.substring(index, url.length());

        return "";
    }


    /**
     * 上传图片临时存放文件的位置
     *
     * @param context
     * @return
     */
    public static String getPicUploadTempPath(Context context, String path) {
        return getFile(context, "upload", "fans_pic_upload" + getsuffer(path), true).getAbsolutePath();
    }

    /**
     * 获取音频文件保存的地址
     *
     * @param ctx
     * @param name 文件名称
     * @return
     */
    public static File getVolumePath(Context ctx, String name) {
        return getFile(ctx, "volume", name, true);
    }

    /**
     * @param path
     * @return
     */
    public static String getPicUploadTempName(String path) {
        return "fans_pic_upload" + getsuffer(path);
    }


    /**
     * 获取拍照保持图片的目录
     *
     * @param ctx
     * @return
     */
    public static String getCarmeraImagePath(Context ctx) {
        return getDir(ctx, CAMERA, false).getAbsolutePath();
    }


    /**
     * 照片文件默认放到XiuDong/Camera下面
     *
     * @param context
     * @param head    文件名，后面加时间参数
     * @return
     */
    public static String preparePicturePath(Context context, String head) {
        return getFile(context, CAMERA, getPictureName(head), false).getAbsolutePath();
    }


    /**
     * @param ctx
     * @return
     */
    public static String getImagePath(Context ctx) {
        return getDir(ctx, PICTURE, false).getAbsolutePath();
    }


    /**
     * 获取拍照保持图片的地址
     *
     * @param ctx
     * @return
     */
    public static String getUpZoomImagePath(Context ctx) {
        return getDir(ctx, UpImageZoom, false).getAbsolutePath();
    }


    /**
     * 根据时间来设置照片的名字
     *
     * @return
     */
    public static String getPictureName(String head) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_hhmmss");
        Date date = new Date(System.currentTimeMillis());

        return head + format.format(date) + ".jpeg";
    }


    /**
     * 把assets里的文件copy到指定文件目录下，同名拷贝
     *
     * @param context
     * @param dir
     * @param assetFileName assets文件的名称
     * @return
     */
    public static File CopyAssetsToXXX(Context context, String dir,
                                       String assetFileName) {
        File file = getFile(context, dir, assetFileName);
        try {
            OutputStream myOutput = new FileOutputStream(file);
            InputStream myInput = context.getAssets().open(assetFileName);
            byte[] buffer = new byte[1024];
            int length = myInput.read(buffer);
            while (length > 0) {
                myOutput.write(buffer, 0, length);
                length = myInput.read(buffer);
            }

            myOutput.flush();
            myInput.close();
            myOutput.close();
        } catch (Exception e) {
            file = null;
        }
        return file;
    }

    /**
     * 读取文本数据
     *
     * @param context  程序上下文
     * @param fileName 文件名
     * @return String, 读取到的文本内容，失败返回null
     */
    public static String readAssets(Context context, String fileName) {
        InputStream is = null;
        String content = null;
        try {
            is = context.getAssets().open(fileName);
            if (is != null) {

                byte[] buffer = new byte[1024];
                ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
                while (true) {
                    int readLength = is.read(buffer);
                    if (readLength == -1)
                        break;
                    arrayOutputStream.write(buffer, 0, readLength);
                }
                is.close();
                arrayOutputStream.close();
                content = new String(arrayOutputStream.toByteArray());

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            content = null;
        } finally {
            try {
                if (is != null)
                    is.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        return content;
    }

    /**
     * @param b
     * @param ret
     * @return
     */
    public static File getFileFromBytes(byte[] b, File ret) {

        BufferedOutputStream stream = null;
        try {
            FileOutputStream fstream = new FileOutputStream(ret);
            stream = new BufferedOutputStream(fstream);
            stream.write(b);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return ret;
    }


    /**
     * 用字节流，读取文件
     *
     * @return 返回读取过后的字符串
     */
    public static String inFile(File file) {
        BufferedInputStream bis = null;

        String str = "";
        try {
            // 选择流
            bis = new BufferedInputStream(new FileInputStream(file));
            // 每次读取文件的长度
            byte[] bt = new byte[2048];
            // 实际接受读取的大小
            int len = 0;
            // 循环读取为文件，直到文件没有返回-1结束循环
            while ((len = bis.read(bt)) != -1) {
                // 将读取的文件拼接成一个字符串
                str += new String(bt, 0, len);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            try {
                if (bis != null)
                    bis.close();
            } catch (IOException e) {
                e.printStackTrace();

            }
        }

        return str;
    }


    /**
     * @param bitmap
     * @param file
     * @return
     * @throws IOException
     * @throws FileNotFoundException
     */
    public static boolean writeBitmapToFile(Bitmap bitmap, File file) throws IOException, FileNotFoundException {

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            return bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    /**
     * copy
     *
     * @param oldfile
     * @param newPath
     * @param showUpdateMei
     */
    public static void copyFile(Context ctx, File oldfile, File newPath, boolean showUpdateMei) {
        try {
            int bytesum = 0;
            int byteread = 0;
            if (oldfile.exists()) {
                InputStream inStream = new FileInputStream(oldfile);
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread;
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        } catch (Exception e) {
        } finally {
            /**更新媒体库*/
            if (showUpdateMei) {
                Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                scanIntent.setData(Uri.fromFile(newPath));
                ctx.sendBroadcast(scanIntent);
            }
        }
    }

    /**
     * 判断文件是否存在
     */
    public static boolean isFileExists(String strFile) {

        if (TextUtils.isEmpty(strFile))
            return false;

        File file = new File(strFile);
        if (file == null)
            return false;

        if (!file.exists())
            return false;

        return true;
    }

    /**
     * 获取视图的bitmap
     *
     * @return
     */
    public static Bitmap getViewBitmap(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    /**
     * 把视图的bitmap储存为图片文件
     *
     * @param ctx
     * @param view
     * @param key  文件名，不需要加后缀，比如保存一张图片
     * @return
     */
    public static File saveViewBitmapToFile(Context ctx, View view, String key) {
        Bitmap bitmap = getViewBitmap(view);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        File file = getDownLoadFilePath(ctx, key + ".jpeg");
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(byteArray, 0, byteArray.length);
            fos.flush();

            Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            scanIntent.setData(Uri.fromFile(file));
            ctx.sendBroadcast(scanIntent);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (!bitmap.isRecycled())
                bitmap.recycle();
            bitmap = null;
        }

        return file;
    }

    public static String[] getSupportedExtensions() {
        return new String[]{"amr", "mp3", "wav", "3gpp", "3gp", "aac", "m4a", "ogg"};
    }

    /**
     * 是否是音频文件
     *
     * @param filename
     * @return
     */
    public static boolean isVolumeFile(String filename) {
        String[] extensions = getSupportedExtensions();
        for (int i = 0; i < extensions.length; i++) {
            if (filename.endsWith("." + extensions[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * 删除文件
     */
    public static boolean delete(String fielPath) {
        if (TextUtils.isEmpty(fielPath)) {
            return false;
        }
        File file = new File(fielPath);
        if (file.isFile() && file.exists()) {
            return file.delete();
        }
        return false;
    }
}
