
package zheng.simon.com.frame.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import zheng.simon.com.frame.R;


/**
 * 加载进度条
 */
public class ProgressDialog extends Dialog {

    public ProgressDialog(Context context) {
        super(context, R.style.CustomProgressDialog);
        init();
    }

    private void init() {
        View contentView = View.inflate(getContext(), R.layout.dialog_progress, null);
        setContentView(contentView);

//        getWindow().setBackgroundDrawable(new ColorDrawable(0));
        WindowManager.LayoutParams lp = getWindow().getAttributes();
//        lp.alpha=0.0f;
        lp.dimAmount = 0.0f;
        getWindow().setAttributes(lp);

        ImageView imageView = contentView.findViewById(R.id.image_anim);

//        ViewHelper.setAlpha(iv_bg, 0.75f);

    }




}
