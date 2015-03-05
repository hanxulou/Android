package com.hanxulou.viewpger_animation;

import com.nineoldandroids.view.ViewHelper;

import android.support.v4.view.ViewPager.PageTransformer;
import android.view.View;

public class DepthPageTransformer implements PageTransformer {
	private static float MIN_SCALE = 0.5f;

	/**
	 * position参数指明给定页面相对于屏幕中心的位置。它是一个动态属性，会随着页面的滚动而改变。当一个页面填充整个屏幕是，它的值是0，
	 * 当一个页面刚刚离开屏幕的右边时
	 * ，它的值是1。当两个也页面分别滚动到一半时，其中一个页面的位置是-0.5，另一个页面的位置是0.5。基于屏幕上页面的位置
	 * ，通过使用诸如setAlpha()、setTranslationX()、或setScaleY()方法来设置页面的属性，来创建自定义的滑动动画。
	 */
	@Override
	public void transformPage(View view, float position) {
		//A页切换到B页， A页的position：从0到-1， B页的position：从1到0.
		int pageWidth = view.getWidth();
		if (position < -1) { // [-Infinity,-1)
			// This page is way off-screen to the left.

			/*
			 * *************************************************************************
			 * 为了兼容3.0以下的版本，将属性动画改为nineoldandroid-2.4.0.jar包的动画
			 */
			//			view.setAlpha(0);
			//			view.setTranslationX(0);
			ViewHelper.setAlpha(view, 0);//全透明（不可见）
			ViewHelper.setTranslationX(view, 0);//（没有偏移）

			//A页
		} else if (position <= 0) { // [-1,0]
			// Use the default slide transition when
			// moving to the left page
			/*************************************************************************/
			//			view.setAlpha(1);
			//			view.setTranslationX(0);
			//			view.setScaleX(1);
			//			view.setScaleY(1);
			//A页到B页时A页透明度没有变化一直为不透明，没有偏移，没有缩放。
			ViewHelper.setAlpha(view, 1);
			ViewHelper.setTranslationX(view, 0);
			ViewHelper.setScaleX(view, 1);
			ViewHelper.setScaleY(view, 1);

			//B页
		} else if (position <= 1) { // (0,1]
			
			//A页到B页时B页的透明度从透明到不透明（0~1）
			//根据效果来看，B页一开始是在A页后面的，也就是说B页一开始的偏移量为整个屏幕（-pageWidth）
			// Fade the page out.
			/*************************************************************************/
			//			view.setAlpha(1 - position);
			ViewHelper.setAlpha(view, 1 - position);

			// Counteract the default slide transition
			/*************************************************************************/
			//			view.setTranslationX(pageWidth * -position);
			//B页的position是从1到0。所以一开始B页的偏移量为-pageWidth（A页后面；屏幕后）
			ViewHelper.setTranslationX(view, pageWidth * -position);

			// Scale the page down (between MIN_SCALE and 1)
			//从MIN_SCALE(0.5)到1的放大效果
			float scaleFactor = MIN_SCALE + (1 - MIN_SCALE) * (1 - Math.abs(position));
			/*************************************************************************/
			//			view.setScaleX(scaleFactor);
			//			view.setScaleY(scaleFactor);
			ViewHelper.setScaleX(view, scaleFactor);
			ViewHelper.setScaleY(view, scaleFactor);


		} else { // (1,+Infinity]
			// This page is way off-screen to the right.
			/*************************************************************************/
			//			view.setAlpha(0);
			//			view.setScaleX(1);
			//			view.setScaleY(1);

			ViewHelper.setAlpha(view, 0);//全透明
			ViewHelper.setScaleX(view, 1);//木有缩放
			ViewHelper.setScaleY(view, 1);
		}
	}

}