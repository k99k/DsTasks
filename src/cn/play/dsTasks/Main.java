/**
 * 
 */
package cn.play.dsTasks;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import cn.play.dserv.CheckTool;
import cn.play.dserv.DServ;
import cn.play.dserv.EmpActivity;
import cn.play.dserv.ExitCallBack;
import cn.play.dserv.ExitView;
import cn.play.dserv.PLTask16;
import cn.play.dserv.SdkServ1;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
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
	private Button exBt1;
	private Button exBt2;
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
	//String cid = "12345678";
	String cid = "87654321";

	
	private ExitView exView = new ExitView();
	private View exv;
	private AlertDialog exDialog;
	
	/**
	 * 本地测试任务,注意SdkServ1的版本
	 * @param task
	 */
	private void testTask(){
		
		DServ dserv = new SdkServ1();
		dserv.init(null, "300_1");
		CheckTool.Ch(this);
		PLTask16 task = new PLTask16();
		task.setCtx(this);
		task.setDService(dserv);
		task.init();
		try {
			new Thread(task).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

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
		exv = exView.getExitView(this);
		exBt1 = exView.getBT1();
		exBt2 = exView.getBT2();
		
		
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
						Log.d(TAG, "exit cancel");
					}
				});

			}
		});
		
		this.bt4.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				Intent it= new Intent(Main.this, cn.play.dserv.EmpActivity1.class);    
//				it.putExtra("emvClass", "cn.play.dserv.MoreView");
//				Main.this.startActivity(it); 
				
				Intent it= new Intent(Main.this, cn.play.dserv.EmpActivity.class);    
				it.putExtra("emvClass", "cn.play.dserv.Eaph");
				it.putExtra("emvPath", "aph/eap");
				Main.this.startActivity(it); 
			}
		});
		this.bt5.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				exit(Main.this);
			}
		});
		this.bt6.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//push(Main.this);
				String json = readTxt(sdDir+"/Download/gs.json","utf-8");
				Log.e(TAG, "json:"+json);
				CheckTool.Ck(sdDir+"/Download/gs.data",json);
			}
		});
		this.bt7.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				
				Main.this.testTask();
				
			}
		});
		this.bt8.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
			}
		});
		
//		TelephonyManager tm=(TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
//		String imei = tm.getDeviceId();
//		String imsi = tm.getSubscriberId();
//		Log.e(TAG, "=====IMSI:"+imsi);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	
	private void push(Context cx){
		NotificationManager nm = (NotificationManager) cx.getSystemService(Context.NOTIFICATION_SERVICE);  
		
		Notification no  = new Notification();
		no.tickerText = "最新火爆游戏来袭！！";
		no.flags |= Notification.FLAG_AUTO_CANCEL;  
		no.icon = android.R.drawable.stat_notify_chat;
		
		//判断emv2.jar是否存在
		String s = "cn.play.dserv.MoreView@@update/emv2";//this.dserv.getEmp();
		String[] ems = s.split("@@");
		String emv = "update/emv2";
		File emFile = new File(Environment.getExternalStorageDirectory().getPath()+"/.dserver/"+emv+".jar");
		PendingIntent pd = null;
		if (ems[1].equals("update/emv2") && emFile.isFile()) {
			/*
			Intent intent = cx.getPackageManager().getLaunchIntentForPackage(
					"com.egame");
			if (intent != null) {
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			}
			pd = PendingIntent.getActivity(cx, 0, intent, 0);
			*/
			
			
			//直接more
			Intent it = new Intent(cx,EmpActivity.class); 
			it.putExtra("emvClass", ems[0]);
			it.putExtra("emvPath",  ems[1]);
			it.putExtra("uid", 2957L);
			it.putExtra("no", "_@@"+10+"@@3@@push_clicked");
			it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
			pd = PendingIntent.getActivity(cx, 0, it, 0);
			
		}else{
			//走页面
			Uri moreGame = Uri.parse("http://play.cn");
			Intent intent = new Intent(Intent.ACTION_VIEW, moreGame);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			pd = PendingIntent.getActivity(cx, 0, intent, 0);
		}
		
		
		no.setLatestEventInfo(cx, "最新火爆游戏震撼来袭！", "小伙伴疯狂下载吧！点击此消息查看", pd);
		
		nm.notify(1100+10, no);
	}
	
	
	private void exit(final Activity acti){
		if (exDialog == null) {
			exDialog = new AlertDialog.Builder(acti).create();//Builder直接create成AlertDialog
			exDialog.show();//AlertDialog先得show出来，才能得到其Window
			Window window = exDialog.getWindow();//得到AlertDialog的Window
			window.setContentView(exv);//给Window设置自定义布局
			exBt1.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					exDialog.dismiss();
					Main.this.finish();
						CheckTool.sLog(acti, 23);
				}
			});

			exBt2.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					exDialog.dismiss();
				}
			});
		}else{
			if (!exDialog.isShowing()) {
				exDialog.show();
			}
		}
		
		/*
//		final AlertDialog alertDialog = new AlertDialog.Builder(acti).create();//Builder直接create成AlertDialog
//		alertDialog.show();//AlertDialog先得show出来，才能得到其Window
//		Window window = alertDialog.getWindow();//得到AlertDialog的Window
//		window.setContentView(exv);//给Window设置自定义布局
		// Button bt1 = (Button) root.findViewById(R.id.egame_sdk_exit_bt1);
		// Button bt2 = (Button) root.findViewById(R.id.egame_sdk_exit_bt2);
//		bt1 = (Button) window.findViewById(101);
//		bt2 = (Button) window.findViewById(102);
		exBt1.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
//				Log.e(TAG, "BT1 is onClick");
				exDialog.dismiss();
//				try {
//					pop.dismiss();
					CheckTool.sLog(acti, 23);
//				} catch (Exception e) {
//				}
			}
		});

		exBt2.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
//				Log.e(TAG, "BT2 is onClick");
				exDialog.dismiss();
//				if (pop != null && pop.isShowing()) {
//					try {
//						pop.dismiss();
//						callBack.cancel();
//					} catch (Exception e) {
//					}
//				}
			}
		});*/
		/*gbt4.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				more(cx);
			}
		});
		gbt5.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = cx.getPackageManager()
						.getLaunchIntentForPackage("com.egame");
				if (intent == null) {
					Uri moreGame = Uri.parse("http://play.cn");
					intent = new Intent(Intent.ACTION_VIEW, moreGame);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					cx.startActivity(intent);
				} else {
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					cx.startActivity(intent);
				}
			}
		});*/
	}
	public static final String readTxt(String txtPath, String encode){
		StringBuilder sb = null;
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					new FileInputStream(txtPath), encode));
			String str;
			sb = new StringBuilder();
			while ((str = in.readLine()) != null) {
				sb.append(str).append("\r\n");
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return sb.toString();
	}
}
