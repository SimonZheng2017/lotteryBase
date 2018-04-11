package zheng.simon.com.frame.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by zhengyouquan on 17/12/6.
 */

public class RecycleEmptyView extends RecyclerView {

    private View emptyView;


    public RecycleEmptyView(Context context) {
        super(context);
    }

    public RecycleEmptyView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RecycleEmptyView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    @Override
    public void setAdapter(Adapter adapter) {
        Adapter oldAdapter = getAdapter();
        if (oldAdapter != null) {
            oldAdapter.unregisterAdapterDataObserver(observer);
        }

        super.setAdapter(adapter);

        if (adapter != null) {
            adapter.registerAdapterDataObserver(observer);
        }

        checkIfEmpty();

    }

    private void checkIfEmpty() {
        if (emptyView != null && getAdapter() != null) {
            boolean emptyViewVisiAble = (getAdapter().getItemCount() == 0);
            emptyView.setVisibility(emptyViewVisiAble ? VISIBLE : GONE);
        }
    }


    public void setEmptyView(View emptyView) {
        this.emptyView = emptyView;
        checkIfEmpty();
    }


    private AdapterDataObserver observer = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            checkIfEmpty();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            checkIfEmpty();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
            checkIfEmpty();
        }
    };


}
