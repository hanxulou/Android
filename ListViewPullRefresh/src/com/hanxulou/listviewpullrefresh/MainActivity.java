package com.hanxulou.listviewpullrefresh;

import java.util.ArrayList;
import java.util.List;

import com.hanxulou.listviewpullrefresh.PullRefreshListView.ILoadListener;
import com.hanxulou.listviewpullrefresh.PullRefreshListView.IRefreshListener;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

public class MainActivity extends Activity implements IRefreshListener, ILoadListener{

	private PullRefreshListView lv;//唯一的listview
	private MyAdapter adapter;//listview的适配器
	private List<MyBean> listdata;//listview的数据
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		lv = (PullRefreshListView) findViewById(R.id.lv);
		lv.setRefreshInterface(this);
		lv.setLoadInterface(this);
		
		listdata = new ArrayList<MyBean>();
		for (int i = 0; i < 10; i++) {
			MyBean bean = new MyBean();
			//原数据和布局文件的一样即可，所以不需要传数据，这里就传了10个空bean
			//			每个bean即为listview中每个item所需要的所有数据
			listdata.add(bean);
		}
		initView();
	}

	private void initView() {
		if (adapter == null) {//adapter为空则创建一个
			adapter = new MyAdapter(listdata, this);
		}else {//已经存在则刷新一下
			adapter.onDateChange(listdata);
		}
		lv.setAdapter(adapter);
	}

	@Override
	public void onRefresh() {
		//这里没有从网络取数据，为了能看到刷新时header的假面效果在此使用handler
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				for (int i = 0; i < 1; i++) {
					MyBean bean = new MyBean();
					bean.setNameTv("刷新的数据");//这里的数据内容和布局文件的不一致了，所以需要设置数据
					listdata.add(0, bean);//添加在最上面
				}
				initView();//刷新界面
				lv.refreshComplete();//调用刷新完成方法
			}
		}, 3000);
	}

	@Override
	public void onLoad() {
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				for (int i = 0; i < 1; i++) {
					MyBean bean = new MyBean();
					bean.setNameTv("加载更多的数据");//这里的数据内容和布局文件的不一致了，所以需要设置数据
					listdata.add(bean);
				}
				initView();//刷新界面
				lv.loadComplete();//调用加载更多数据完成的方法
			}
		}, 3000);
	}


}
