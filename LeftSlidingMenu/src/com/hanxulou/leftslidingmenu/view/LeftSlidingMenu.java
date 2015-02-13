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
	private LinearLayout containerLinearLayoutl;//scrollview����������Ψһlayout
	private ViewGroup mMenu;//�໬menu
	private ViewGroup mContent;//������

	private int mScreenWidth;//��Ļ���
	private int mMenuRightPadding = 50;//���������ʱ�������ݵ���һ���ȣ���������dpΪ��λ��
	private int mMenuWidth;//�˵����Ŀ��(��Ļ��ȼ�ȥһС���ֵ������ݿ��)

	private boolean isFirst;//onMeaSure�������ܵ��ö��

	public LeftSlidingMenu(Context context, AttributeSet attrs) {
		super(context, attrs);
		//��ȡ��Ļ��ȣ�ƽʱҲ���õ���
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		mScreenWidth = outMetrics.widthPixels;//�����Ի����Ļ���

		//		��dpת��Ϊ����ֵpx  ��λ���ת����ʹ�ã�ƽʱҲ���õ���
		mMenuRightPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources().getDisplayMetrics());
	}

	//�����ڲ���view�Ŀ�ߣ�ȷ������Ŀ��
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (!isFirst) {//���ǵ�һ��
			containerLinearLayoutl = (LinearLayout) getChildAt(0);//����Ҳ��ֻ��һ��linearlayout��view
			mMenu = (ViewGroup) containerLinearLayoutl.getChildAt(0);//��һ����view����menu�˵�
			mContent = (ViewGroup) containerLinearLayoutl.getChildAt(1);//��2����view���������ݲ���

			//�˵����ֵĿ��Ϊ��Ļ��ȼ�ȥ���������ʱ������ռȥ���ǵ���
			mMenuWidth = mMenu.getLayoutParams().width = mScreenWidth - mMenuRightPadding;
			//�����ݲ��ֵĿ��Ϊ��Ļ���
			mContent.getLayoutParams().width = mScreenWidth;
			isFirst = true;
		}


		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	//��view��λ��  -- ����menu����������ʾ
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);

		if (changed) {//���ε��õ�
			//�õ�ǰ��scrollview���������ز˵�������ȫ��ʾ�����ݲ��ֵĵط�
			this.scrollTo(mMenuWidth, 0);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {

		switch (ev.getAction()) {
		case MotionEvent.ACTION_UP://ֻ��Ҫ�ж���ָ̧��ʱ��״̬
			int scrollX = getScrollX();//��õ�����������ߵĿ�ȶ�������ʾ�Ŀ��
			//������ʹ�ò˵�����ʾ�����Ŀ��С�������һ��ʱ�����صĿ�ȴ���һ�룩 �ò˵������������ݲ�����ʾ
			if (scrollX >= mMenuWidth / 2) {
				this.smoothScrollTo(mMenuWidth, 0);//������������scrollTo��������һ������Ĺ�������Ч����
			}else {//������ʹ�ò˵�����ʾ�����Ŀ�ȴ��������һ��ʱ�����صĿ��С��һ�룩 �ò˵�����ʾ�����ݿ���վ
				this.smoothScrollTo(0, 0);
			}
			return true;
		}

		return super.onTouchEvent(ev);
	}

}
