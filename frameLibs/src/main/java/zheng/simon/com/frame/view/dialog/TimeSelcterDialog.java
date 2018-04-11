package zheng.simon.com.frame.view.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;


import java.text.SimpleDateFormat;

import zheng.simon.com.frame.R;
import zheng.simon.com.frame.view.wheel.DateWheelPicker;
import zheng.simon.com.frame.widget.DateUtil;


/**
 * Created by Administrator on 2016/5/23.
 */
public class TimeSelcterDialog {

    private Context context;
    /* 选择的日期 */
    private int selectYear, selectMonth, selectDay;
    /* 滚动的日期 */
    private int currentYear, currentMonth, currentDay;

    private View view;
    private PopupWindow popupWindow;
    private DateWheelPicker dpicker;
    private Button btBeDown, btCancel;
    private OnTimeListener listener;

    public TimeSelcterDialog(Context context) {
        this.context = context;
        view = View.inflate(context, R.layout.dialog_datepicker, null);
        popupWindow = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        popupWindow.setOutsideTouchable(true);
        popupWindow.update();
        popupWindow.setTouchable(true);
        popupWindow.setFocusable(true);

        dpicker = (DateWheelPicker) view.findViewById(R.id.datepicker_layout);
        btBeDown = (Button) view.findViewById(R.id.datepicker_btsure);
        btCancel = (Button) view.findViewById(R.id.datepicker_btcancel);
    }


    public void Open(View v) {


        popupWindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);

        dpicker.setOnChangeListener(new DateWheelPicker.OnChangeListener() {
            @Override
            public void onChange(int year, int month, int day, int day_of_week) {
                currentYear = year;
                currentMonth = month;
                currentDay = day;

                if (listener != null) {
                    listener.onChange(year, month, day, day_of_week);
                }

            }
        });

        dpicker.setDefaultDate(selectYear, selectMonth, selectDay);


        btBeDown.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                selectYear = currentYear;
                selectMonth = currentMonth;
                selectDay = currentDay;

                String data;

                String dataTime;

                if (selectMonth < 10 && selectDay < 10) {
//                    data = selectYear + "年0" + selectMonth + "月0" + selectDay+"日";
                    dataTime = selectYear + "-0" + selectMonth + "-0" + selectDay;
                } else if (selectMonth >= 10 && selectDay < 10) {
//                    data = selectYear + "年" + selectMonth + "月0" + selectDay+"日";
                    dataTime = selectYear + "-" + selectMonth + "-0" + selectDay;
                } else if (selectMonth < 10 && selectDay >= 10) {
//                    data = selectYear + "年0" + selectMonth + "月" + selectDay+"日";
                    dataTime = selectYear + "-0" + selectMonth + "-" + selectDay;
                } else {
//                    data = selectYear + "年" + selectMonth + "月" + selectDay+"日";
                    dataTime = selectYear + "-" + selectMonth + "-" + selectDay;
                }

                long selectTime = DateUtil.getInstance().date2TimeStamp(dataTime, DateUtil.FORMAT_DATE);

                if (listener != null) {
                    listener.onClickEnter(v, selectTime, dataTime);
                }

            }
        });

        btCancel.setOnClickListener(view -> popupWindow.dismiss());

        view.findViewById(R.id.ll_pop).setOnClickListener(v1 -> popupWindow.dismiss());
    }


    public void Close() {

        try {
            popupWindow.dismiss();
        } catch (Exception e) {
        }

    }

    /**
     * 设置多少年前的时间
     *
     * @param yearBefor 多少年前，就是多少
     */
    public void setDefaultYearBefor(int yearBefor) {
        int years;
        try {
            years = Integer.parseInt(DateUtil.getInstance().getDateString(new SimpleDateFormat("yyyy"), System.currentTimeMillis()));
        } catch (Exception e) {
            e.printStackTrace();
            years = 2016;
        }
        setDefaultYear(years - yearBefor);
    }

    public void setDefaultYear(int defaultYear) {
        this.selectYear = defaultYear;
    }

    public void setDefaultDate(int selectYear, int selectMonth, int selectDay) {
        this.selectYear = selectYear;
        this.selectMonth = selectMonth;
        this.selectDay = selectDay;
    }

    public void setListener(OnTimeListener listener) {
        this.listener = listener;
    }

    public interface OnTimeListener {

        void onChange(int year, int month, int day, int day_of_week);

        void onClickEnter(View v, long selectTime, String data);

    }


}
