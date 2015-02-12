package com.hanxulou.listviewpullrefresh;

import java.util.List;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class MyAdapter extends BaseAdapter {

	private List<MyBean> listdata;//listview的数据
	private Context context;
	public MyAdapter(List<MyBean> listdata, Context context) {
		this.listdata = listdata;
		this.context = context;
	}

	@Override
	public int getCount() {
		//listview数据长度（item个数）
		return listdata.size();
	}

	@Override
	public Object getItem(int arg0) {
		//		每个item中的数据（包括item中的所有数据）
		return listdata.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		//		item序号（第几个item）
		return arg0;
	}

	@Override
	public View getView(int arg0, View convertView, ViewGroup arg2) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.listview_item, null);

			holder.nameTv = (TextView) convertView.findViewById(R.id.nameTv);
			holder.timeTv = (TextView) convertView.findViewById(R.id.timeTv);
			holder.buyBtn = (Button) convertView.findViewById(R.id.buyBtn);

			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}

		if (!TextUtils.isEmpty(listdata.get(arg0).getNameTv())) {
			//如果有传入数据则设置
			holder.nameTv.setText(listdata.get(arg0).getNameTv());
			//因为我们原始数据和布局文件一样就可以了所以不用传来数据，之前传来的为空bean这里必须要加上判断
		}
		return convertView;
	}
	//viewholder一个临时的存储器将getView方法中每次返回的View存起来，
	//以便你下次再用。这样做的好处就是不必每次都到布局文件中去拿到你的View，提高效率。
	class ViewHolder {
		TextView nameTv;
		TextView timeTv;
		Button buyBtn;
	}

	//当listview数据发生改变时
	public void onDateChange(List<MyBean> listdata) {
		this.listdata = listdata;
		this.notifyDataSetChanged();
	}

}
