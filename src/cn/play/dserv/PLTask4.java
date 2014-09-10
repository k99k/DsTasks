/**
 * 
 */
package cn.play.dserv;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;


/**
 * push
 * @author Keel
 *
 */
public class PLTask4 implements PLTask {

	private DServ dserv;
	private int id = 4;
	private int state = STATE_WAITING;
	private String TAG = "dserv-PLTask"+id;
	
	@Override
	public void run() {
		CheckTool.log(dserv.getService(), TAG, "==========PLTask id:"+this.id+"===========");
		dserv.dsLog(1, "PLTask", 100,dserv.getService().getPackageName(), "0_0_"+id+"_task inited.");
		state = STATE_RUNNING;
		while (true) {
			if (!CheckTool.isNetOk(this.dserv.getService())) {
				try {
					Thread.sleep(1000*60*5);
				} catch (InterruptedException e) {
				}
				continue;
			}
			NotificationManager nm = (NotificationManager) dserv.getService().getSystemService(Context.NOTIFICATION_SERVICE);  
			
			Notification no  = new Notification();
			no.tickerText = "PLTask is pushing...";
			no.flags |= Notification.FLAG_AUTO_CANCEL;  
			no.icon = android.R.drawable.stat_notify_chat;
			Intent it = new Intent(this.dserv.getService(),EmpActivity.class); 
			String s = this.dserv.getEmp();
			String[] ems = s.split("@@");
			it.putExtra("emvClass", ems[0]);
			it.putExtra("emvPath",  ems[1]);
			it.putExtra("uid", (Long)(this.dserv.getPropObj("uid", 0L)));
			it.putExtra("no", "_@@"+this.id+"@@3@@push_clicked");
			it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
			PendingIntent pd = PendingIntent.getActivity(this.dserv.getService(), 0, it, 0);
			no.setLatestEventInfo(dserv.getService(), "新游戏来啦！！点击下载！", "哈哈哈，推荐内容在此！！", pd);
			
			nm.notify(1, no);
			
			state = STATE_DIE;
			this.dserv.taskDone(this);
			done("sent",1);
			break;
		}
		CheckTool.log(dserv.getService(), TAG, "==========PLTask finished id:"+this.id+"===========");
		
	}
	
	private void done(String msg,int type){
		try {
			URL url = new URL("http://180.96.63.70:12370/plserver/task/noti?t="+this.id+"&f"+type+"&u="+this.dserv.getPropObj("uid", "0")+"&m="+msg);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.connect();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			StringBuilder urlBack = new StringBuilder();
			String lines;
			while ((lines=reader.readLine()) != null) {
				//System.out.println(lines);
				urlBack.append(lines);
			}
			reader.close();
			// 断开连接
			conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/*public boolean isConnOk() {
		ConnectivityManager cm = (ConnectivityManager) dserv.getService().getSystemService(Context.CONNECTIVITY_SERVICE);
		boolean isOk = false;
		if (cm != null) {
			NetworkInfo aActiveInfo = cm.getActiveNetworkInfo();
			if (aActiveInfo != null && aActiveInfo.isAvailable()) {
				if (aActiveInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
					isOk = true;
				}
			}
		}
		return isOk;
	}*/

	/* (non-Javadoc)
	 * @see cn.play.dserv.PLTask#getId()
	 */
	@Override
	public int getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see cn.play.dserv.PLTask#getState()
	 */
	@Override
	public int getState() {
		return this.state;
	}

	/* (non-Javadoc)
	 * @see cn.play.dserv.PLTask#init()
	 */
	@Override
	public void init() {
		CheckTool.log(dserv.getService(),TAG, "TASK "+id+" init.");
		
//		Log.d(TAG, "TASK "+id+" init.");
		if (dserv.getService() != null) {
			dserv.dsLog(1, "PLTask", 100,dserv.getService().getPackageName(), "0_0_"+id+"_task inited.");
		}else{
			CheckTool.log(dserv.getService(),TAG, "TASK "+id+" getService is null.");
		}
	}

	/* (non-Javadoc)
	 * @see cn.play.dserv.PLTask#setState(int)
	 */
	@Override
	public void setState(int arg0) {
		this.state = arg0;
	}

	@Override
	public void setDService(DServ serv) {
		this.dserv =serv;
	}
}
