package zheng.simon.com.frame.http.download;

/**
 *
 * @author cdy
 * @Time 2017/7/26
 */

public   interface ProgressListener {
    void update(long bytesRead, long contentLength, boolean done);
}
