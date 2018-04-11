package zheng.simon.com.frame.widget;

import java.util.Random;

public class ColorUtil {

    /**
     * 随机生成颜色
     *
     * @return
     */
    public static int getRandomColor() {
        Random random = new Random();
        int ranColor = 0xff000000 | random.nextInt(0x00ffffff);
        return ranColor;
    }


}
