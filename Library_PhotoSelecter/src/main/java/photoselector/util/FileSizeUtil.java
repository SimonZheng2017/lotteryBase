package photoselector.util;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;

/**
 * Created by Soli on 2016/7/15.
 */
public class FileSizeUtil {
    public static final int SIZETYPE_B = 1;// 获取文件大小单位为B的double值
    public static final int SIZETYPE_KB = 2;// 获取文件大小单位为KB的double值
    public static final int SIZETYPE_MB = 3;// 获取文件大小单位为MB的double值
    public static final int SIZETYPE_GB = 4;// 获取文件大小单位为GB的double值

    /**
     * 获取文件指定文件的指定单位的大小
     *
     * @param filePath 文件路径
     * @param sizeType 获取大小的类型1为B、2为KB、3为MB、4为GB
     * @return double值的大小
     */
    public static double getFileOrFilesSize(String filePath, int sizeType) {
        File file = new File(filePath);
        long blockSize = 0;
        try {
            if (file.isDirectory()) {
                blockSize = getFileSizes(file);
            } else {
                blockSize = getFileSize(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return FormetFileSize(blockSize, sizeType);
    }

    /**
     * 获取文件大小 kb单位
     *
     * @param filePath
     * @return
     */
    public static String getFileSize(String filePath) {
        double size = getFileOrFilesSize(filePath, SIZETYPE_KB);
        return String.valueOf(new BigDecimal(size).setScale(0, BigDecimal.ROUND_HALF_UP));
    }


    /**
     * 调用此方法自动计算指定文件或指定文件夹的大小
     *
     * @param filePath 文件路径
     * @return 计算好的带B、KB、MB、GB的字符串
     */
    public static String getAutoFileOrFilesSize(String filePath) {
        File file = new File(filePath);
        long blockSize = 0;
        try {
            if (file.isDirectory()) {
                blockSize = getFileSizes(file);
            } else {
                blockSize = getFileSize(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return FormetFileSize(blockSize);
    }

    /**
     * 获取指定文件夹
     *
     * @param f
     * @return
     * @throws Exception
     */
    private static long getFileSizes(File f) throws Exception {
        long size = 0;
        File flist[] = f.listFiles();
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size = size + getFileSizes(flist[i]);
            } else {
                size = size + getFileSize(flist[i]);
            }
        }
        return size;
    }

    /**
     * 获取指定文件大小
     *
     * @param file
     * @return
     * @throws Exception
     */
    private static long getFileSize(File file) throws Exception {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            size = fis.available();
        } else {
            file.createNewFile();
        }
        return size;
    }

    /**
     * 转换文件大小
     *
     * @param fileS
     * @return
     */
    private static String FormetFileSize(long fileS) {
        String fileSizeString = "";
        String wrongSize = "0B";
        if (fileS == 0) {
            return wrongSize;
        }
        if (fileS < 1024) {
            fileSizeString = new BigDecimal((double) fileS).setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "B";
        } else if (fileS < 1048576) {
            fileSizeString = new BigDecimal((double) fileS / 1024).setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = new BigDecimal((double) fileS / 1048576).setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "MB";
        } else {
            fileSizeString = new BigDecimal((double) fileS / 1073741824).setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "GB";
        }
        return fileSizeString;
    }

    /**
     * 转换文件大小,指定转换的类型
     * <p>
     * 不用这个格式化，这个和国家有关，有些国家直接是，
     * DecimalFormat df = new DecimalFormat("#.00");
     *
     * @param fileS
     * @param sizeType
     * @return
     */
    private static double FormetFileSize(long fileS, int sizeType) {
        double size = 0d;
        switch (sizeType) {
            case SIZETYPE_B:
                size = (double) fileS;
                break;
            case SIZETYPE_KB:
                size = (double) fileS / 1024;
                break;
            case SIZETYPE_MB:
                size = (double) fileS / 1048576;
                break;
            case SIZETYPE_GB:
                size = (double) fileS / 1073741824;
                break;
            default:
                break;
        }

        return new BigDecimal(size).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }
}
