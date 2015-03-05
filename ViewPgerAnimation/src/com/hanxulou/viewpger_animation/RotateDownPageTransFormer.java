package com.hanxulou.viewpger_animation;

import com.nineoldandroids.view.ViewHelper;

import android.support.v4.view.ViewPager.PageTransformer;
import android.view.View;

public class RotateDownPageTransFormer implements PageTransformer {

	private final static float MAX_ROTATE = 20f;
	private float mRotate;
	
	//从A页切换到B页时，A页的角度变化为0到-20， B页的角度变化为20到0。
	@Override
	public void transformPage(View view, float position) {
		int pageWidth = view.getWidth();
		
		if (position < -1) { // [-Infinity,-1)
			// This page is way off-screen to the left.

			/*
			 * *************************************************************************
			 * 为了兼容3.0以下的版本，将属性动画改为nineoldandroid-2.4.0.jar包的动画
			 */
//			ViewHelper.setAlpha(view, 0);
			ViewHelper.setRotation(view, 0);//木有旋转

			//A页效果； position从0~-1
		} else if (position <= 0) { // [-1,0]
			// Use the default slide transition when
			// moving to the left page
			/*************************************************************************/
			mRotate = position * MAX_ROTATE;//从0到-20
			//设置旋转中心点为：屏幕底部的中间
			ViewHelper.setPivotX(view, pageWidth / 2);
			ViewHelper.setPivotY(view, view.getMeasuredHeight());//内容的高度(全屏则为屏幕底部)
			ViewHelper.setRotation(view, mRotate);
			
			//B页效果； position从1~0
		} else if (position <= 1) { // (0,1]
			
			// Fade the page out.
			/*************************************************************************/
			mRotate = position * MAX_ROTATE;//从20到0
			//设置旋转中心点为：屏幕底部的中间
			ViewHelper.setPivotX(view, pageWidth / 2);
			ViewHelper.setPivotY(view, view.getMeasuredHeight());//内容的高度(全屏则为屏幕底部)
			ViewHelper.setRotation(view, mRotate);
			

		} else { // (1,+Infinity]
			// This page is way off-screen to the right.
			/*************************************************************************/

			ViewHelper.setRotation(view, 0);//木有旋转
		}
	}

}
