package com.hanxulou.leftslidingmenu.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

public class LeftSlidingMenu extends HorizontalScrollView {
	private LinearLayout containerLinearLayoutl;//scrollview中所包含的唯一layout
	private ViewGroup mMenu;//侧滑menu
	private ViewGroup mContent;//主内容

	private int mScreenWidth;//屏幕宽度
	private int mMenuRightPadding = 50;//滑出侧边栏时，主内容的那一点宽度，这里是以dp为单位的
	private int mMenuWidth;//菜单栏的宽度(屏幕宽度减去一小部分的主内容宽度)

	private boolean isFirst;//onMeaSure方法可能调用多次

	public LeftSlidingMenu(Context context, AttributeSet attrs) {
		super(context, attrs);
		//获取屏幕宽度（平时也会用到）
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		mScreenWidth = outMetrics.widthPixels;//还可以获得屏幕宽度

		//		将dp转化为像素值px  单位间的转化可使用（平时也会用到）
		mMenuRightPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources().getDisplayMetrics());
	}

	//决定内部子view的宽高，确定自身的宽高
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (!isFirst) {//不是第一次
			containerLinearLayoutl = (LinearLayout) getChildAt(0);//当中也就只有一个linearlayout子view
			mMenu = (ViewGroup) containerLinearLayoutl.getChildAt(0);//第一个子view就是menu菜单
			mContent = (ViewGroup) containerLinearLayoutl.getChildAt(1);//第2个子view就是主内容布局

			//菜单布局的宽度为屏幕宽度减去侧边栏滑出时主内容占去的那点宽度
			mMenuWidth = mMenu.getLayoutParams().width = mScreenWidth - mMenuRightPadding;
			//主内容布局的宽度为屏幕宽度
			mContent.getLayoutParams().width = mScreenWidth;
			isFirst = true;
		}


		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	//子view的位置  -- 隐藏menu让主内容显示
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);

		if (changed) {//会多次调用到
			//让当前的scrollview滚动到隐藏菜单栏，完全显示主内容布局的地方
			this.scrollTo(mMenuWidth, 0);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {

		switch (ev.getAction()) {
		case MotionEvent.ACTION_UP://只需要判断手指抬起时的状态
			int scrollX = getScrollX();//获得的是隐藏在左边的宽度而不是显示的宽度
			//当滚动使得菜单栏显示出来的宽度小于自身的一半时（隐藏的宽度大于一半） 让菜单栏隐藏主内容布局显示
			if (scrollX >= mMenuWidth / 2) {
				this.smoothScrollTo(mMenuWidth, 0);//这个方法相比于scrollTo方法多了一个自身的滚动动画效果。
			}else {//当滚动使得菜单栏显示出来的宽度大于自身的一半时（隐藏的宽度小于一半） 让菜单栏显示主内容靠边站
				this.smoothScrollTo(0, 0);
			}
			return true;
		}

		return super.onTouchEvent(ev);
	}

}
