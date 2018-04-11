package zheng.simon.com.frame.widget;

import android.app.Activity;


import java.io.File;

import photoselector.util.FileUtil;
import photoselector.util.FrescoUtil;
import zheng.simon.com.frame.http.HttpUtil;
import zheng.simon.com.frame.http.download.OnFileDownloadListener;

/**
 * Created by zhengyouquan on 07/02/2018.
 */

public class DownloaderUtil {

    /**
     * 保存图片(默认保存在download文件夹下)
     */
    public static void savePictrue(Activity act, String path) {
        File file = FrescoUtil.getFrescoCacheFile(path);
        File downfile = FileUtil.getDownLoadFilePath(act, path);

        if (file != null && file.exists()) {
            FileUtil.copyFile(act, file, downfile, true);
        } else {
            HttpUtil.download(path, downfile, new OnFileDownloadListener() {
                @Override
                public void onResponse(File saveFile) {

                }

                @Override
                public void onFailure() {

                }

                @Override
                public void onProgress(int progress) {

                }
            });

        }

    }

}
