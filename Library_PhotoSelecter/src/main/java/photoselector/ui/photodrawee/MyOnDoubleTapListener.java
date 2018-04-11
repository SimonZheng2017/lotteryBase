package photoselector.ui.photodrawee;


import android.view.MotionEvent;

/**
 * 双击放大最大or最小
 */
public class MyOnDoubleTapListener extends DefaultOnDoubleTapListener {
    public MyOnDoubleTapListener(Attacher attacher) {
        super(attacher);
    }

    @Override
    public boolean onDoubleTap(MotionEvent event) {
        if (mAttacher == null) {
            return false;
        }

        try {
            float scale = mAttacher.getScale();
            float x = event.getX();
            float y = event.getY();

//            if (scale < mAttacher.getMediumScale()) {
//                mAttacher.setScale(mAttacher.getMaximumScale(), x, y, true);
//            } else if (scale >= mAttacher.getMediumScale() && scale < mAttacher.getMaximumScale()) {
//                mAttacher.setScale(mAttacher.getMaximumScale(), x, y, true);
//            } else {
//                mAttacher.setScale(mAttacher.getMinimumScale(), x, y, true);
//            }

            if (scale <= mAttacher.getMinimumScale())
                mAttacher.setScale(mAttacher.getMaximumScale(), x, y, true);
            else
                mAttacher.setScale(mAttacher.getMinimumScale(), x, y, true);

        } catch (Exception e) {
            // Can sometimes happen when getX() and getY() is called
        }
        return true;
    }
}
