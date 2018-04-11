package zheng.simon.com.frame.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    protected List<T> mList;
    protected Context ctx;
    protected LayoutInflater mInflater;

    public BaseRecyclerAdapter(Context ctx) {
        this.ctx = ctx;
        mInflater = LayoutInflater.from(ctx);
    }

    public BaseRecyclerAdapter(Context ctx, List<T> list) {
        this.ctx = ctx;
        this.mList = list;
        mInflater = LayoutInflater.from(ctx);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }


    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    /**
     * @param position
     * @return
     */
    public T getItemData(int position) {
        return mList != null && mList.size() > 0 && position < getItemCount() ? mList.get(position) : null;
    }

    /**
     * @param list
     */
    public void setList(List<T> list) {
        if (list != null ) {
            mList = new ArrayList<>();
            this.mList = list;
            notifyDataSetChanged();
        }
    }

    public List<T> getList() {
        return this.mList == null ? new ArrayList<>() : mList;
    }

    public void add(T t) {
        if (mList == null) {
            mList = new ArrayList<T>();
        }
        mList.add(t);
        notifyDataSetChanged();
    }

    public void addAll(List<T> list) {
        if (mList == null) {
            mList = new ArrayList<T>();
        }
        mList.addAll(list);
        notifyDataSetChanged();
    }

    public void addAll(int position, List<T> list) {
        if (mList == null) {
            mList = new ArrayList<T>();
        }
        mList.addAll(position, list);
        notifyDataSetChanged();
    }

    public void insertAtTop(T data) {
        insert(0, data);
    }

    /**
     * @param position
     * @param data
     */
    public void insert(int position, T data) {
        if (mList == null) {
            mList = new ArrayList<T>();
        }

        mList.add(position, data);
        notifyItemInserted(position);
    }


    /**
     * @param position
     * @param data
     */
    public void set(int position, T data) {
        if (mList != null && position < getItemCount()) {
            mList.set(position, data);
        }
    }

    /**
     * @param position
     */
    public void remove(int position) {
        try {
            if (mList != null) {
                if (position >= 0 || position < mList.size()) {
                    mList.remove(position);
                    notifyItemRemoved(position);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeAll() {
        if (mList != null) {
            mList.removeAll(mList);
            notifyDataSetChanged();
        }
    }

}
