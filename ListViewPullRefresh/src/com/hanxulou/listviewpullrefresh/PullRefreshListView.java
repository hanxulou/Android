package com.hanxulou.listviewpullrefresh;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class PullRefreshListView extends ListView implements AbsListView.OnScrollListener {

	private View header, footer;
	private int headerHeight;

	private int scrollState;//listview当前滚动状态
	private int firstVisibleItem;//当前listview可见的第一个item的序号（位置），只有当前listview可见item的顶部（第一个item）显示的是listview数据中的第一个数据才能下拉出提示
	private boolean isRemark;//标记当前listview显示的为最顶端的部分，即是初始的状态
	private int startY;//按下时记录Y坐标的值

	//在listview下拉时状态会发生变换
	private int state;//当前listview的状态（下拉可以刷新或是松开刷新or正在刷新）
	final int NONE = 0;//正常状态，即和普通listview看起来一样
	final int PULL = 1;//下拉状态
	final int RELESE = 2;//松开状态
	final int REFRESHING = 3;//正在刷新状态

	private IRefreshListener iRefreshListener;//刷新数据的接口
	
	private int totalItemCount;//总item数量
	private int lastVisibleItem;//最后一个可见的item
	private boolean isLoading;//正在加载
	
	private ILoadListener iLoadListener;//加载更多数据的接口

	public PullRefreshListView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initView(context);
	}

	public PullRefreshListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public PullRefreshListView(Context context) {
		super(context);
		initView(context);
	}

	private void initView(Context context) {
		header = LayoutInflater.from(context).inflate(R.layout.headerview_listview, null);
		footer = LayoutInflater.from(context).inflate(R.layout.footerview_listview, null);
		footer.findViewById(R.id.loadLinearLayout).setVisibility(View.GONE);

		measureView(header);

		headerHeight = header.getMeasuredHeight();
		/*
		 * getMeasuredHeight()是实际View的大小，与屏幕无关，而getHeight的大小则与屏幕有关。
		 *	当屏幕可以包裹内容的时候，他们的值相等，只有当view超出屏幕后，才能看出他们的区别：
		 *	当超出屏幕后， getMeasuredHeight() 等于 getHeight()加上屏幕之外没有显示的大小
		 *	只有在一个控件的 onMeasure()方法被执行过后，才能使用getMeasuredWidth()取得正确的值
		 */
		topPadding(-headerHeight);
		Log.e("headerHeight: ", String.valueOf(headerHeight));
		System.out.println("---------------------" + headerHeight);
		
		this.addHeaderView(header);
		this.addFooterView(footer);
		this.setOnScrollListener(this);
	}

	//让header隐藏
	private void topPadding(int topPadding) {
		if (topPadding > 300) {//不需要可以一直下拉
			topPadding = 300;
		}
		//header默认隐藏，下拉事随着下拉距离逐渐显现，使用padding让header的位置在屏幕顶部的上方。
		header.setPadding(header.getPaddingLeft(), topPadding, header.getPaddingRight(), header.getPaddingBottom());
		header.invalidate();//刷新header界面
	}

	//通知父布局，子view所占的宽高
	private void measureView(View view) {
		ViewGroup.LayoutParams lp = view.getLayoutParams();
		if (lp == null) {
			lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		}

		int width = ViewGroup.getChildMeasureSpec(0, 0, lp.width);//子布局的宽度
		int height;
		int tempHeight = lp.height;
		if (tempHeight > 0) {
			//给与精确的高度，填充内容，用来确定子view的高度
			height = MeasureSpec.makeMeasureSpec(tempHeight, MeasureSpec.EXACTLY);

			/*
			 *MeasureSpec有3种模式分别是UNSPECIFIED, EXACTLY和AT_MOST
				AT_MOST： specSize 代表的是最大可获得的空间； 
				EXACTLY： specSize 代表的是精确的尺寸； 
				UNSPECIFIED： 对于控件尺寸来说，没有任何参考意义。
				这些模式和我们平时设置的layout参数fill_parent, wrap_content的关系：
				当设置width或height为fill_parent时，容器在布局时调用子 view的measure方法传入的模式是EXACTLY，因为子view会占据剩余容器的空间，所以它大小是确定的。
				而当设置为 wrap_content时，容器传进去的是AT_MOST, 表示子view的大小最多是多少，这样子view会根据这个上限来设置自己的尺寸。当子view的大小设置为精确值时，容器传入的是EXACTLY, 
				而MeasureSpec的UNSPECIFIED模式在还未定义或不清楚是fill（match）或wrap两者的情况下情况下使用。 
				View的onMeasure方法默认行为是当模式为UNSPECIFIED时，设置尺寸为mMinWidth(通常为0)或者背景drawable的最小尺寸，当模式为EXACTLY或者AT_MOST时，尺寸设置为传入的MeasureSpec的大小。 
				有个观念需要纠正的是，fill_parent应该是子view会占据剩下容器的空间，而不会覆盖前面已布局好的其他view空间，当然后面布局子 view就没有空间给分配了，所以fill_parent属性对布局顺序很重要。
			 */
		}else {
			//高度为0--不需要给子view填充内容
			height = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		}
		//将子view的宽高设置
		view.measure(width, height);

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		this.firstVisibleItem = firstVisibleItem;
		this.lastVisibleItem = firstVisibleItem + visibleItemCount;//最后一个可见的item=第一个可见的加上所有可见的item数
		this.totalItemCount = totalItemCount;//总的item数
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		this.scrollState = scrollState;
		//最后一个可见的item就是所有item的最后一个 且当前滚动停止了
		if (lastVisibleItem == totalItemCount && scrollState == SCROLL_STATE_IDLE) {
			if (!isLoading) {
				isLoading = true;
				footer.findViewById(R.id.loadLinearLayout).setVisibility(View.VISIBLE);
				iLoadListener.onLoad();
			}
			
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (firstVisibleItem == 0) {//当前listview为初始状态
				isRemark = true;
				startY = (int) ev.getY();
			}
			break;
		case MotionEvent.ACTION_MOVE:
			onMove(ev);
			break;
		case MotionEvent.ACTION_UP:
			if (state == RELESE) {
				state = REFRESHING;

				//刷新--加载最新数据
				refreshViewByState();
				iRefreshListener.onRefresh();
			}else if (state == PULL) {
				state = NONE;
				isRemark = false;
				refreshViewByState();
			}
			break;

		default:
			break;
		}
		return super.onTouchEvent(ev);
	}

	//移动过程操作：
	private void onMove(MotionEvent ev) {
		if (!isRemark) {//如果listview并不位于最顶部
			return;
		}
		int tempY = (int) ev.getY();
		int distance = tempY - startY;
		int topPadding = distance - headerHeight;//在下拉过程中让隐藏的header随着下拉距离而显示出来
		switch (state) {
		case NONE:
			if (distance > 0) {//下拉操作
				state = PULL;
				refreshViewByState();
			}
			break;
		case PULL:
			topPadding(topPadding);
			if (distance > headerHeight + 50 && scrollState == SCROLL_STATE_TOUCH_SCROLL) {
				//当下拉达到一定的高度，且当前listview的滚动状态为正在滚动
				state =  RELESE;
				refreshViewByState();
			}
			break;
		case RELESE:
			topPadding(topPadding);
			if (distance < headerHeight + 50 ) {
				//当下拉又小于一定的高度
				state = PULL;
				refreshViewByState();
			}else if (distance <= 0) {//下拉高度小于等于0==没有下拉，回到正常状态
				state = NONE;
				isRemark = false;
				refreshViewByState();
			}
			break;
			//		case REFRESHING:
			//			
			//			break;

		default:
			break;
		}
	}

	//根据当前的状态改变header的界面
	private void refreshViewByState() {
		TextView stateTv = (TextView) header.findViewById(R.id.stateTv);
		ImageView arrowIv = (ImageView) header.findViewById(R.id.arrowIv);
		ProgressBar headerProgressbar = (ProgressBar) header.findViewById(R.id.headerPogressbar);

		RotateAnimation rotateTo180 = new RotateAnimation(0, -180, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		rotateTo180.setDuration(500);
		rotateTo180.setFillEnabled(true);//亲测不用这个也有效
		rotateTo180.setFillAfter(true);//该方法用于设置一个动画效果执行完毕后，View对象保留在终止的位置。该方法的执行，需要首先通过setFillEnabled方法使能填充效果，否则设置无效。
		
		RotateAnimation rotateBack = new RotateAnimation(-180, 0, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		rotateBack.setDuration(500);
		rotateBack.setFillEnabled(true);
		rotateBack.setFillAfter(true);
		switch (state) {
		case NONE:
			arrowIv.clearAnimation();
			topPadding(-headerHeight);
			break;
		case PULL:
			arrowIv.setVisibility(View.VISIBLE);
			headerProgressbar.setVisibility(View.GONE);
			stateTv.setText("下拉可以刷新哦~");
			arrowIv.clearAnimation();
			arrowIv.setAnimation(rotateBack);
			break;
		case RELESE:
			arrowIv.setVisibility(View.VISIBLE);
			headerProgressbar.setVisibility(View.GONE);
			stateTv.setText("松开可以刷新哟~");
			arrowIv.clearAnimation();
			arrowIv.setAnimation(rotateTo180);
			break;
		case REFRESHING:
			topPadding(50);
			arrowIv.setVisibility(View.GONE);
			headerProgressbar.setVisibility(View.VISIBLE);
			stateTv.setText("正在刷新，请稍后…");
			arrowIv.clearAnimation();
			break;

		default:
			break;
		}
	}

	//数据刷新完成
	public void refreshComplete() {
		//回到正常状态，并刷新header界面
		state = NONE;
		isRemark = false;
		refreshViewByState();
		//设置最后一次刷新的时间
		TextView freshtimeTv = (TextView) header.findViewById(R.id.freshtimeTv);
		SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日  hh:mm:ss");
		Date date = new Date(System.currentTimeMillis());
		String lastRefreshTime = format.format(date);
		freshtimeTv.setText("上次刷新时间：" + lastRefreshTime);
	}

	//刷新数据的接口
	public interface IRefreshListener{
		//刷新的方法
		public void onRefresh();
	}

	//设置接口方法
	public void setRefreshInterface(IRefreshListener iRefreshListener) {
		this.iRefreshListener = iRefreshListener;
	}
	
	//加载更多数据的回调接口
	public interface ILoadListener{
		//加载更多的实现方法
		public void onLoad();
	}
	//设置接口方法
	public void setLoadInterface(ILoadListener iLoadListener) {
		this.iLoadListener = iLoadListener;
	}
	
	//加载完成
	public void loadComplete(){
		isLoading = false;
		footer.findViewById(R.id.loadLinearLayout).setVisibility(View.GONE);
	}

}
