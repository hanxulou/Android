package com.hanxulou.leftslidingmenu.view;

import com.hanxulou.leftslidingmenu.R;
import com.nineoldandroids.view.ViewHelper;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

public class LeftSlidingMenu_QQ extends HorizontalScrollView {
	private LinearLayout containerLinearLayoutl;//scrollview中所包含的唯一layout
	private ViewGroup mMenu;//侧滑menu
	private ViewGroup mContent;//主内容

	private int mScreenWidth;//屏幕宽度
	private int mMenuRightPadding = 50;//滑出侧边栏时，主内容的那一点宽度，这里是以dp为单位的
	private int mMenuWidth;//菜单栏的宽度(屏幕宽度减去一小部分的主内容宽度)

	private boolean isFirst;//onMeaSure方法可能调用多次
	private boolean isOpen;//用来判断是否打开了menu，即侧滑界面是否正在显示中

	//使用了自定义属性时调用此构造方法
	public LeftSlidingMenu_QQ(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		//获取自己定义的属性
		TypedArray ta = context.getTheme().obtainStyledAttributes(attrs, R.styleable.LeftSlidingMenu, defStyle, 0);
		int count = ta.getIndexCount();
		for (int i = 0; i < count; i++) {
			int attr = ta.getIndex(i);
			switch (attr) {
			case R.styleable.LeftSlidingMenu_rightPadding://使用自定义的属性
				//如果布局文件中给了rightPadding值则使用它，如果没用则使用默认的50
				mMenuRightPadding = 
				ta.getDimensionPixelSize(attr, 
						(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources().getDisplayMetrics()));
				//转化为像素的方法下面有介绍
				break;

			}
		}
		ta.recycle();//要记得释放

		//获取屏幕宽度（平时也会用到）
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		mScreenWidth = outMetrics.widthPixels;//还可以获得屏幕宽度

		//将dp转化为像素值px  单位间的转化可使用（平时也会用到）
		//		mMenuRightPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources().getDisplayMetrics());
	}

	public LeftSlidingMenu_QQ(Context context) {
		this(context, null);//调用两个参数的构造方法
	}

	//没有使用自定义属性时调用此构造方法
	public LeftSlidingMenu_QQ(Context context, AttributeSet attrs) {
		this(context, attrs, 0);//调用三个参数的构造方法
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
				isOpen = false;
			}else {//当滚动使得菜单栏显示出来的宽度大于自身的一半时（隐藏的宽度小于一半） 让菜单栏显示主内容靠边站
				this.smoothScrollTo(0, 0);
				isOpen = true;
			}
			return true;
		}

		return super.onTouchEvent(ev);
	}

	//打开菜单，即显示侧滑界面
	public void openMenu() {
		if (isOpen) {
			return;
		}
		this.smoothScrollTo(0, 0);
		isOpen = true;
	}
	//关闭菜单，即隐藏侧滑界面让主界面显示
	public void closeMenu() {
		if (!isOpen) {
			return;
		}
		this.smoothScrollTo(mMenuWidth, 0);
		isOpen = false;
	}
	//切换菜单，即当菜单是打开状态时调用此方法就关闭菜单，反之关闭状态调用此方法就打开
	public void switchMenu() {
		if (isOpen) {
			closeMenu();
		}else {
			openMenu();
		}
	}

	/*
	 * 当发生滚动时触发此方法
	 * 
	 * 属性动画是android3.0之后才引入的，为了是低版本的系统能够兼容所以使用了nineoldandroids这个jar包（搜一下就知道了）
	 */
	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);

		//l的初始值为mMenuWidth，即菜单关闭的时候。打开菜单的过程中l的值逐渐趋向于0.
		float distance = l * 1.0f / mMenuWidth;//所以distance的值的范围为1~0.
		/*
		 * 调用属性动画TranslationX实现抽屉式效果--打开菜单时的动画效果
		 *设置偏移的距离--偏移的距离即为隐藏的的宽度
		 */
		ViewHelper.setTranslationX(mMenu, mMenuWidth * distance * 0.6f);//这里translationX的值则为mMenuWidth~0即隐藏的宽度变化值
		
		//菜单区域缩放效果--大致为0.5~1.0
		float menuZoom = 1.0f - 0.5f * distance;
		ViewHelper.setScaleX(mMenu, menuZoom);
		ViewHelper.setScaleY(mMenu, menuZoom);
		
		//菜单区域透明度动画效果--大致为0.5~1.0
		float alpha = 1.0f - 0.5f * distance;
		ViewHelper.setAlpha(mMenu, alpha);
		
		//主内容区域缩放效果--大致为1.0~0.8
		float contentZoom = 0.8f + 0.2f * distance;
		ViewHelper.setScaleX(mContent, contentZoom);
		ViewHelper.setScaleY(mContent, contentZoom);
		//设置缩放的中心点，默认为几何中心为中心点。这里设置主内容布局最左边的中间点为缩放中心
		ViewHelper.setPivotX(mContent, 0);
		ViewHelper.setPivotY(mContent, mContent.getHeight() / 2);
		

	}

}
