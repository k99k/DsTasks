package cn.play.dserv;

import java.lang.reflect.Constructor;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class EmpActivity1 extends Activity {

	public EmpActivity1() {
	}
	private static final String TAG  ="dserv-EmpActivity";
//	private String localDexPath = Environment.getExternalStorageDirectory().getPath()+"/.dserver/emv.jar";
//	private String dexOutputDir = "/data/data/cn.play.dserv";//getApplicationInfo().dataDir;

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String emvClass = this.getIntent().getStringExtra("emvClass");
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		WindowManager.LayoutParams.FLAG_FULLSCREEN);
		try {
			Class clazz = Class.forName(emvClass);
			if (clazz != null) {
				Constructor construct = clazz.getConstructor(Context.class);  
				EmView emv = (EmView)construct.newInstance(this);
				View v = emv.getView();
				this.setContentView(v);
			}
		} catch (Exception e) {
			e.printStackTrace();
			finish();
		}
	}

	   @Override
		public void onConfigurationChanged(Configuration newConfig) {
			// 解决横竖屏切换导致重载的问题
			super.onConfigurationChanged(newConfig);
			if(newConfig.orientation==Configuration.ORIENTATION_LANDSCAPE){  
			    //横向  
			}else{  
			    //竖向  
			}  
		}
		
	
	
}
