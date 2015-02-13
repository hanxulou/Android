package com.hanxulou.leftslidingmenu;

import com.hanxulou.leftslidingmenu.view.LeftSlidingMenu_QQ;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.Window;

public class MainActivity extends Activity {

	private LeftSlidingMenu_QQ leftSlidingMenu;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);//actionbarÒþ²Ø--È«ÆÁÏÔÊ¾
		setContentView(R.layout.activity_main);
		
		leftSlidingMenu = (LeftSlidingMenu_QQ) findViewById(R.id.leftSlidingMenu);
	}
	
	//ÇÐ»»²Ëµ¥
	public void switchMenu(View view){
		leftSlidingMenu.switchMenu();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
