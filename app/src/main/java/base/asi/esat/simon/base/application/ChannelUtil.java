package base.asi.esat.simon.base.application;


import zheng.simon.com.frame.BuildConfig;

public class ChannelUtil {

    public static final String CHANNEL_DEV = "Develop";

    public static final String CHANNEL_PODUCT = "product";


    /**
     * @return
     */
    public static String getCurrentChanel() {
        return BuildConfig.FLAVOR;
    }


    /**
     * @param channel
     * @return
     */
    public static boolean isEqualToChannel(String channel) {
        return getCurrentChanel().equalsIgnoreCase(channel);
    }


    /**
     * @return
     */
    public static boolean isDEVChannel() {
        return isEqualToChannel(CHANNEL_DEV);
    }


    /**
     * @return
     */
    public static boolean isProductChannel() {
        return isEqualToChannel(CHANNEL_PODUCT);
    }

}
