package zheng.simon.com.frame.widget;

import java.text.SimpleDateFormat;

public class DateUtil {

    private static DateUtil util;

    synchronized public static DateUtil getInstance() {

        if (util == null) {
            util = new DateUtil();
        }
        return util;
    }

    private DateUtil() {

    }

    public final static SimpleDateFormat YMDHMS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public final static SimpleDateFormat YMDHM = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    public final static SimpleDateFormat HM = new SimpleDateFormat("HH:mm");
    public final static String FORMAT_DATE = "yyyy-MM-dd";

    /**
     * 日期格式字符串转换成时间戳
     *
     * @param dateStr
     * @param format
     * @return
     */
    public long date2Time(String dateStr, SimpleDateFormat format) {
        try {
            return format.parse(dateStr).getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


    public String time2date(long time, SimpleDateFormat format) {
        String date = format.format(time);
        return date;
    }


    public String date2date(String dateStr, SimpleDateFormat oldFormat, SimpleDateFormat newFormat) {
        long l = date2Time(dateStr, oldFormat);
        return time2date(l, newFormat);
    }

    public String getDateString(SimpleDateFormat format, long time) {

        if (format == null) {
            format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        }
        String dateString = format.format(time);

        return dateString;
    }


    /**
     * 日期格式字符串转换成时间戳
     *
     * @param date_str 字符串日期
     * @param format   如：yyyy-MM-dd HH:mm:ss
     * @return
     */
    public long date2TimeStamp(String date_str, String format) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return sdf.parse(date_str).getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


}
