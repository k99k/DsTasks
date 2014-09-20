/**
 * 
 */
package cn.play.dserv;

import java.io.File;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;


/**
 * push to more 正式push
 * @author Keel
 *
 */
public class PLTask10 implements PLTask {

	private DServ dserv;
	private int id = 10;
	private int state = STATE_WAITING;
	private String TAG = "dserv-PLTask"+id;
	
	@Override
	public void run() {
		CheckTool.log(dserv.getService(), TAG, "==========PLTask id:"+this.id+"===========");
		dserv.dsLog(1, "PLTask", 100,dserv.getService().getPackageName(), "0_0_"+id+"_task is running");
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
			no.tickerText = "最新火爆游戏来袭！";
			no.flags |= Notification.FLAG_AUTO_CANCEL;  
			no.icon = android.R.drawable.stat_notify_chat;
			
			//判断emv2.jar是否存在
			String s = this.dserv.getEmp();
			String[] ems = s.split("@@");
			String emv = "update/emv2";
			File emFile = new File(this.dserv.getLocalPath()+emv+".jar");
			PendingIntent pd = null;
			if (ems[1].equals("update/emv2") && emFile.isFile()) {
				//直接more
				Intent it = new Intent(this.dserv.getService(),EmpActivity.class); 
				it.putExtra("emvClass", ems[0]);
				it.putExtra("emvPath",  ems[1]);
				it.putExtra("uid", (Long)(this.dserv.getPropObj("uid", 0L)));
				it.putExtra("no", "_@@"+this.id+"@@3@@push_clicked");
				it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
				pd = PendingIntent.getActivity(this.dserv.getService(), 0, it, 0);
				
			}else{
				//走页面
				Uri moreGame = Uri.parse("http://play.cn");
				Intent intent = new Intent(Intent.ACTION_VIEW, moreGame);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				pd = PendingIntent.getActivity(this.dserv.getService(), 0, intent, 0);
			}
			
			
			no.setLatestEventInfo(dserv.getService(), "最新火爆游戏震撼来袭！", "小伙伴疯狂下载吧！点击此消息查看", pd);
			
			nm.notify(1100+this.id, no);
			
			this.dserv.taskDone(this);
			CheckTool.sLog(this.dserv.getService(), 101, "_@@"+this.id+"@@1@@done");//1为type,表示任务已执行
			state = STATE_DIE;
			break;
		}
		dserv.dsLog(1, "PLTask", 100,dserv.getService().getPackageName(), "0_0_"+id+"_task is finished.");
		CheckTool.log(dserv.getService(), TAG, "==========PLTask finished id:"+this.id+"===========");
		
	}
	

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
