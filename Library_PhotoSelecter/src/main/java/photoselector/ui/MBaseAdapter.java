package photoselector.ui;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

public class MBaseAdapter<T> extends BaseAdapter {

	protected Context context;
	protected List<T> models;

	public MBaseAdapter(Context context, ArrayList<T> models) {
		this.context = context;
		if (models == null)
			this.models = new ArrayList<>();
		else
			this.models = models;
	}

	@Override
	public int getCount() {
		if (models != null) {
			return models.size();
		}
		return 0;
	}

	@Override
	public T getItem(int position) {
		return models.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return null;
	}

	public void setList(List<T> list) {
		models = new ArrayList<>();
		this.models = list;
		notifyDataSetChanged();
	}

	public List<T> getItems() {
		return models;
	}

}
