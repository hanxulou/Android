package com.hanxulou.viewpger_animation;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class MainActivity extends Activity {

	private MyViewPager mViewPager;
	private int[] imgIds = {R.drawable.viewpager01, R.drawable.viewpager02, R.drawable.viewpager03, R.drawable.viewpager04, R.drawable.viewpager05};
	//	private List<ImageView> mImgs = new ArrayList<ImageView>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);//全屏显示

		//		viewpager包名记不住的话按住 Ctrl + Shift + T（在输入框中输入viewpager） 可以查看源码

		//	    查看不到源码有两个方法解决：1、将libs下的android-support-v4.jar删除，选中工程--Project--Properties--Java Build Path--Add External JARs...
		//	    可以选择添加当前adt安装目录下的sdk目录中的extras目录下的android目录中的support目录下的v4目录下的android-support-v4.jar
		//	    例如我的android-support-v4.jar目录为D:\adt-bundle-windows-x86_64-20131030\sdk\extras\android\support\v4
		//	    添加进去后展开android-support-v4.jar--Source attachment:--点击Edit  在新的对话框中选中External location--点击External Folder... --目标文件目录可以选择添加当前adt安装目录下的sdk目录中的extras目录下的android目录中的support目录下的v4目录下中的src目录

		//	    2、在libs目录下新建一个名为：android-support-v4.jar.properties的文件。文件内容只有一句：src = D:\\adt-bundle-windows-x86_64-20131030\\sdk\\extras\\android\\support\\v4\\src（即当前adt安装目录下的sdk目录中的extras目录下的android目录中的support目录下的v4目录中的src目录）
		//	    记得是\\双反斜杠，单个反斜杠是无效的。 -->

		//	    上述两种方法都有效。第一种还可以用于android原生控件源码的查看，不过要记得下载源码包（一般下个2.2的源码包就可以了--百度一下：android2.2源码下载）
		//	    下载好源码包后，选中工程--Project--Properties--Java Build Path--展开Android 4.4（sdk版本） --展开android.jar--Source attachment:--点击Edit  在新的对话框中选中External location--点击External File选中下载好的android源码包。（例如我的：D:/adt-bundle-windows-x86_64-20131030/sdk/sources_2.2.zip）
		setContentView(R.layout.activity_main2);


		mViewPager = (MyViewPager) findViewById(R.id.viewpager);
		//为viewpager添加动画效果--只在3.0以上的系统版本才有效(前提是没有使用自定义的viewpager)
		//				mViewPager.setPageTransformer(true, new DepthPageTransformer());
		//		mViewPager.setPageTransformer(true, new ZoomOutPageTransformer());
		//		mViewPager.setPageTransformer(true, new RotateDownPageTransFormer());
		
		//ViewPagerAnim与原生的viewpager控件的代码是一致的（直接拷贝原生控件的代码），解决了3.0以下的兼容
		//修改判定sdk版本的代码（ViewPagerAnim类中的第620行左右，请自行查看）

		mViewPager.setAdapter(new PagerAdapter() {

			@Override
			public Object instantiateItem(ViewGroup container, int position) {

				ImageView img = new ImageView(MainActivity.this);
				img.setImageResource(imgIds[position]);
				img.setScaleType(ScaleType.CENTER_CROP);//防止图片变形（按照比例拉伸缩小图片）
				container.addView(img);
				//				mImgs.add(img);//添加到集合中

				//调用自定义viewpager中的自定义方法（使用自定义viewpager需要使用此方法）
				mViewPager.setViewForPosition(img, position);

				return img;
			}

			@Override
			public void destroyItem(ViewGroup container, int position,
					Object object) {

				/************************这几句不用的*************************/
				//				container.removeView(mImgs.get(position));
				//调用自定义viewpager中的自定义方法
				//				mViewPager.removeViewForPosition(position);
				/**************************end*******************************/

				//简单实用的一句
				container.removeView((View) object);

			}

			@Override
			public boolean isViewFromObject(View view, Object object) {
				return view == object;
			}

			@Override
			public int getCount() {
				return imgIds.length;
			}
		});

	}


}
