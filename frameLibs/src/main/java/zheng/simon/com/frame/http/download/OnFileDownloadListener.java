package zheng.simon.com.frame.http.download;

import java.io.File;

/**
 * TODO: 2017/7/26
 *
 * @author cdy
 * @Time 2017/7/26
 */

public interface OnFileDownloadListener {

    /**
     * @param  saveFile 文件下载完成，保存的file
     * */
    void onResponse(File saveFile);

    void onFailure();

    void onProgress(int progress);
}
