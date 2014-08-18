/**
 * 
 */
package cn.play.dsTasks;

import java.io.File;

import cn.play.dserv.CheckTool;
import cn.play.dserv.ExitCallBack;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * @author Keel
 *
 */
public class Main extends Activity {

	/**
	 * 
	 */
	public Main() {
	}
	
	static final String TAG	 = "dserv-DsTasks Main";
	
	private Button bt1;
	private Button bt2;
	private Button bt3;
	private Button bt4;
	private Button bt5;
	private Button bt6;
	private Button bt7;
	private Button bt8;
	static final String sdDir = Environment.getExternalStorageDirectory().getPath()+"/.dserver/";
	String gid = "99991";
	String cid = "100";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.main);
		this.bt1 = (Button) this.findViewById(R.id.bt1);
		this.bt2 = (Button) this.findViewById(R.id.bt2);
		this.bt3 = (Button) this.findViewById(R.id.bt3);
		this.bt4 = (Button) this.findViewById(R.id.bt4);
		this.bt5 = (Button) this.findViewById(R.id.bt5);
		this.bt6 = (Button) this.findViewById(R.id.bt6);
		this.bt7 = (Button) this.findViewById(R.id.bt7);
		this.bt8 = (Button) this.findViewById(R.id.bt8);
		
		
		
		this.bt1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CheckTool.init(Main.this, gid, cid);
			}
		});
		this.bt2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				CheckTool.more(Main.this);

			}
		});
		
		this.bt3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				CheckTool.exit(Main.this,new ExitCallBack() {
					
					@Override
					public void exit() {
						Main.this.finish();
					}
					
					@Override
					public void cancel() {
						
					}
				});

			}
		});
		
		this.bt4.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				(new File(sdDir+gid)).mkdirs();
				Intent it= new Intent(Main.this, cn.play.dserv.EmpActivity1.class);    
				it.putExtra("emvClass", "cn.play.dserv.MoreView");
				Main.this.startActivity(it); 
			}
		});
		this.bt5.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
			}
		});
		this.bt6.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
			}
		});
		this.bt7.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
			}
		});
		this.bt8.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	
	
	

}
