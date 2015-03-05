package com.hanxulou.viewpger_animation;

import java.util.HashMap;
import java.util.Map;

import com.nineoldandroids.view.ViewHelper;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

/*
 * 
 * 实现与DepthPageTransformer一样的动画效果
 * 添加的动画效果与viewpager原有的平滑切换动画效果重叠（adapter中没有很好的移除view，destroyItem方法（pageradapter中的方法））
 * 
 */
public class MyViewPager extends ViewPager {

	private View mLeftView, mRightView;
	private float mTrans, mScale;

	private static final float MIN_SCALE = 0.5f;

	private Map<Integer, View> mChildView = new HashMap<Integer, View>();

	public MyViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);


	}

	public MyViewPager(Context context) {
		super(context);
	}

	@Override
	protected void onPageScrolled(int position, float offset, int offsetPixels) {
		//第0页到第1页position=0； offset为0~1；第1页到第2页规律一致（position=1）
		//第1页到第0页position=0； offset为1~0；第2页到第1页规律一致（position=1）
		//通过position获得mLeftView(当前显示的前一个)和mRightView(当前显示的后一个)（mLeftView=position； mRightView=position+1）

		mLeftView = mChildView.get(position);
		mRightView = mChildView.get(position + 1);

		animationStack(mLeftView, mRightView, offset, offsetPixels);

		super.onPageScrolled(position, offset, offsetPixels);

	}

	private void animationStack(View leftView, View rightView,
			float offset, int offsetPixels) {

		if (rightView != null) {
			mScale = (1 - MIN_SCALE) * offset + MIN_SCALE;
			mTrans = -getWidth() - getPageMargin() + offsetPixels;//offsetPixels为左右滑动的距离
			
			ViewHelper.setScaleX(rightView, mScale);
			ViewHelper.setScaleY(rightView, mScale);
			ViewHelper.setTranslationX(rightView, mTrans);
		}
			if (leftView != null) {
				leftView.bringToFront();//保证leftView始终在前面（如果rightView在前面那么leftView就会被遮盖）
			}
	}

	public void setViewForPosition(View view, int position){
		mChildView.put(position, view);
	}

	public void removeViewForPosition(int position){
		mChildView.remove(position);
	}
	

}
