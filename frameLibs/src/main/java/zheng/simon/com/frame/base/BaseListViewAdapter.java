package zheng.simon.com.frame.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lizhangfeng on 16/4/21.
 * description: BaseListViewAdapter
 */
public abstract class BaseListViewAdapter<T, VH extends BaseListViewAdapter.ViewHolder> extends BaseAdapter {

    protected Context context;

    protected List<T> mList;

    private VH viewHolder;

    private LayoutInflater inflater;

    protected View itemView;

    public BaseListViewAdapter(Context context) {
        this(context, null);
    }

    public BaseListViewAdapter(Context context, List<T> beans) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.mList = beans;
    }

    public void setDatas(List<T> beans) {
        this.mList = beans;
    }

    public void setList(List<T> list) {
        mList = new ArrayList<T>();
        this.mList = list;
        notifyDataSetChanged();
    }

    public List<T> getList() {
        if (mList == null) {
            mList = new ArrayList<>();
        }
        return this.mList;
    }

    public void add(T t) {
        if (mList == null) {
            mList = new ArrayList<T>();
        }
        mList.add(t);
        notifyDataSetChanged();
    }

    public void set(int location, T t) {
        if (mList == null) {
            mList = new ArrayList<T>();
        }
        mList.set(location, t);
        notifyDataSetChanged();
    }

    public void add(int location, T t) {
        if (mList == null) {
            mList = new ArrayList<T>();
        }
        mList.add(location, t);
        notifyDataSetChanged();
    }

    public void addAll(List<T> list) {
        if (list != null && list.size() > 0)
        {
            if (mList == null) {
                mList = new ArrayList<T>();
            }
            mList.addAll(list);
            notifyDataSetChanged();
        }
    }

    public void addAllToFirst(List<T> list) {
        if (mList == null) {
            mList = new ArrayList<T>();
        }
        mList.addAll(0, list);
        notifyDataSetChanged();
    }

    public void remove(int position) {
        if (mList != null) {
            mList.remove(position);
            notifyDataSetChanged();
        }
    }

    public void removeAll() {
        if (mList != null) {
            mList.removeAll(mList);
            notifyDataSetChanged();
        }
    }


    public void add(T t, int position) {
        this.mList.add(position, t);
        notifyDataSetChanged();
    }

    public List<T> getBeans() {
        return mList;
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public T getItem(int position) {
        return mList != null && mList.size() > 0 && position < getCount() ? mList.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(getResource(), null, false);
            itemView = convertView;
            viewHolder = onCreateViewHolder(convertView, parent);
            viewHolder.view = itemView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (VH) convertView.getTag();
            if (viewHolder != null)
                itemView = viewHolder.view;
        }
        if (mList != null) {
            T t = mList.get(position);
            if (t != null)
                onBindViewHolder(t, viewHolder, position);
        }
        return convertView;
    }


    protected abstract int getResource();

    protected abstract VH onCreateViewHolder(View view, ViewGroup parent);

    protected abstract void onBindViewHolder(T t, VH holder, int position);

    public abstract class ViewHolder {
        public View view;
    }

    /**
     * 不用强转的findviewbyid
     */
    public <T extends View> T findViewByIdNoCast(int id) {
        return (T) itemView.findViewById(id);
    }
}
