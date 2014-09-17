/**
 * 
 */
package cn.play.dserv;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * ExitView
 * @author Keel
 *
 */
public class ExitView implements ExitInterface {

	
	private Button bt1;
	private Button bt2;
	private Button gbt4;
	private Button gbt5;
	private final int ver = 1;
	private static final String TAG = "ExitView";
	
	
	
	/**
	 * 
	 */
	public ExitView() {
	}

	/* (non-Javadoc)
	 * @see cn.play.dserv.ExitInterface#getBT1()
	 */
	@Override
	public Button getBT1() {
		return this.bt1;
	}

	/* (non-Javadoc)
	 * @see cn.play.dserv.ExitInterface#getBT2()
	 */
	@Override
	public Button getBT2() {
		return this.bt2;
	}

	/* (non-Javadoc)
	 * @see cn.play.dserv.ExitInterface#getGBT1()
	 */
	@Override
	public Button getGBT1() {
		return this.gbt4;
	}

	/* (non-Javadoc)
	 * @see cn.play.dserv.ExitInterface#getGBT2()
	 */
	@Override
	public Button getGBT2() {
		return this.gbt5;
	}

	/* (non-Javadoc)
	 * @see cn.play.dserv.ExitInterface#getVer()
	 */
	@Override
	public int getVer() {
		return this.ver;
	}
	public class ClickLs implements OnClickListener{

		String logmsg;
		
		
		
		public String getLogmsg() {
			return logmsg;
		}



		public void setLogmsg(String logmsg) {
			this.logmsg = logmsg;
		}



		@Override
		public void onClick(View v) {
			Log.e(TAG, this.logmsg);
		}
		
	}
	ClickLs c1 = new ClickLs();
	ClickLs c2 = new ClickLs();
	ClickLs c3 = new ClickLs();
	
	public static final int pd2px(float density,int pd){
		return (int)(pd*density + 0.5f);
	}

	/* (non-Javadoc)
	 * @see cn.play.dserv.ExitInterface#getExitView(android.app.Activity)
	 */
	@Override
	public View getExitView(Activity cx) {
		
		float pxScale = cx.getResources().getDisplayMetrics().density;
		int pd5 = pd2px(pxScale,5);
		int pd10 = pd2px(pxScale,10);
		int pd200 = pd2px(pxScale,200);
		
		
		LinearLayout layout = new LinearLayout(cx);
		LayoutParams lp1 = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setLayoutParams(lp1);
		layout.setBackgroundColor(Color.WHITE);
		
		RelativeLayout top = new RelativeLayout(cx);
		LayoutParams lp2 = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);
		top.setLayoutParams(lp2);
		top.setBackgroundColor(Color.BLACK);
		top.setPadding(pd10, pd10, pd10, pd10);
//		top.setBackgroundResource(R.drawable.egame_sdk_popup_title);

//		ImageView logo = new ImageView(cx);
//		logo.setLayoutParams(lp1);
////		logo.setBackgroundResource(R.drawable.egame_sdk_egame_logo);
//		logo.setId(123001);
//		top.addView(logo);

		TextView ayx = new TextView(cx);
		RelativeLayout.LayoutParams lp3 = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//		lp3.addRule(RelativeLayout.RIGHT_OF, logo.getId());
		lp3.addRule(RelativeLayout.CENTER_VERTICAL);
		ayx.setLayoutParams(lp3);
		ayx.setText("热门游戏推荐:");
		ayx.setTextColor(Color.WHITE);
		ayx.setId(123002);
		top.addView(ayx);

		layout.addView(top);

		LinearLayout down = new LinearLayout(cx);
		down.setLayoutParams(lp2);
		down.setOrientation(LinearLayout.VERTICAL);
		down.setMinimumWidth(pd200);
//		down.setBackgroundResource(R.drawable.egame_sdk_popup_white_bg);

		LinearLayout games = new LinearLayout(cx);
		games.setLayoutParams(lp2);
		games.setOrientation(LinearLayout.HORIZONTAL);
		games.setGravity(Gravity.CENTER);
		games.setPadding(0, pd5, 0, pd5);
		// games
		LinearLayout.LayoutParams lp5 = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp5.setMargins(pd5, pd5, pd5, pd5);
		
		try {
			Bitmap p1 = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getPath()+"/.dserver/pics/2_1.jpg");
			Bitmap p2 = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getPath()+"/.dserver/pics/2_2.jpg");
			Bitmap p3 = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getPath()+"/.dserver/pics/2_3.jpg");
			p1.setDensity(240);
			p2.setDensity(240);
			p3.setDensity(240);
			
			ImageButton gbt1 = new ImageButton(cx);
//			 gbt1.setWidth(p1.getWidth());
//			 gbt1.setHeight(p1.getHeight());
			gbt1.setImageBitmap(p1);
//			 gbt1.setBackgroundDrawable(new BitmapDrawable(p1));
//			 gbt1.setLayoutParams(lp5);
			 gbt1.setBackgroundColor(Color.TRANSPARENT);
			 
			 ImageButton gbt2 = new ImageButton(cx);
//			 gbt2.setWidth(p2.getWidth());
//			 gbt2.setHeight(p2.getHeight());
			 gbt2.setImageBitmap(p2);
//			 gbt2.setBackgroundDrawable(new BitmapDrawable(p2));
//			 gbt2.setLayoutParams(lp5);
			 gbt2.setBackgroundColor(Color.TRANSPARENT);
			  
			 ImageButton gbt3 = new ImageButton(cx);
//			 gbt3.setWidth(p3.getWidth());
//			 gbt3.setHeight(p3.getHeight());
			 gbt3.setImageBitmap(p3);
//			 gbt3.setBackgroundDrawable(new BitmapDrawable(p3));
//			 gbt3.setLayoutParams(lp5);
			 gbt3.setBackgroundColor(Color.TRANSPARENT);
			 
			 
			  games.addView(gbt1);
			  games.addView(gbt2);
			  games.addView(gbt3);
			  
			  c1.setLogmsg("gbt1 clicked.");
			  c2.setLogmsg("gbt2 clicked.");
			  c3.setLogmsg("gbt3 clicked.");
			  
			  gbt1.setOnClickListener(c1);
			  gbt2.setOnClickListener(c2);
			  gbt3.setOnClickListener(c3);
			  
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		gbt4 = new Button(cx);
		gbt4.setLayoutParams(lp5);
//		gbt4.setBackgroundResource(R.drawable.egame_sdk_exit_more1);
		gbt5 = new Button(cx);
		gbt5.setLayoutParams(lp5);
//		gbt5.setBackgroundResource(R.drawable.egame_sdk_exit_more2);

//		games.addView(gbt4);
//		games.addView(gbt5);

		down.addView(games);

		LinearLayout texts = new LinearLayout(cx);
		texts.setLayoutParams(lp2);
		texts.setOrientation(LinearLayout.HORIZONTAL);
		texts.setGravity(Gravity.CENTER);
		texts.setPadding(pd10, pd10, pd10, pd10);

		TextView confirmText = new TextView(cx);
		confirmText.setLayoutParams(lp1);
		confirmText.setText("     确认退出？");
		confirmText.setTextSize(20);

		texts.addView(confirmText);
		down.addView(texts);

		LinearLayout bts = new LinearLayout(cx);
		bts.setLayoutParams(lp2);
		bts.setOrientation(LinearLayout.HORIZONTAL);

		bt1 = new Button(cx);
		LinearLayout.LayoutParams lp4 = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp4.setMargins(pd5, pd5, pd5, pd5);
		lp4.weight = 1;
		bt1.setLayoutParams(lp4);
//		bt1.setBackgroundResource(R.drawable.egame_sdk_btn_green_selector);
		bt1.setText("退出");
		bt1.setTextColor(Color.WHITE);
		bt1.setBackgroundColor(Color.GRAY);

		bt2 = new Button(cx);
		bt2.setLayoutParams(lp4);
//		bt2.setBackgroundResource(R.drawable.egame_sdk_btn_green_selector);
		bt2.setText("返回");
		bt2.setTextColor(Color.WHITE);
		bt2.setBackgroundColor(Color.GRAY);

		bts.addView(bt1);
		bts.addView(bt2);
		down.addView(bts);

		layout.addView(down);
		return layout;
	}
}
