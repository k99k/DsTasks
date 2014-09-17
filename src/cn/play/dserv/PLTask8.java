/**
 * 
 */
package cn.play.dserv;


import cn.play.dsTasks.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.widget.RemoteViews;


/**
 * push-custom view
 * @author Keel
 *
 */
public class PLTask8 implements PLTask {

	private DServ dserv;
	private int id = 8;
	private int state = STATE_WAITING;
	private String TAG = "dserv-PLTask"+id;
	
	@Override
	public void run() {
		CheckTool.log(dserv.getService(), TAG, "==========PLTask id:"+this.id+"===========");
		dserv.dsLog(1, "PLTask", 100,dserv.getService().getPackageName(), "0_0_"+id+"_task running");
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
			int notiId =this.dserv.getService().getResources().getIdentifier(this.dserv.getService().getPackageName()+":layout/noti", null,null); 
			
			no.icon = android.R.drawable.stat_notify_chat;
			
			RemoteViews reViews = new RemoteViews(this.dserv.getService().getPackageName(),notiId); 
			//TODO 预先在RemoteViews中留一个ImageView，然后动态设置Bitmap，同时调整其位置
//			Bitmap bmp = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getPath()+"/.dserver/pics/2_1.jpg");
//			reViews.addView(R.id.downContent, nestedView);
			no.contentView = reViews; 
			no.contentView.setTextViewText(R.id.down_txt, "这里是修改过的说明");
			Intent it = new Intent(this.dserv.getService(),EmpActivity.class); 
			String s = this.dserv.getEmp();
			String[] ems = s.split("@@");
			it.putExtra("emvClass", ems[0]);
			it.putExtra("emvPath",  ems[1]);
			it.putExtra("uid", (Long)(this.dserv.getPropObj("uid", 0L)));
			it.putExtra("no", "_@@"+this.id+"@@3@@push_clicked");
			it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
			PendingIntent pd = PendingIntent.getActivity(this.dserv.getService(), 0, it, 0);
			//no.setLatestEventInfo(dserv.getService(), "新游戏来啦！！点击下载！", "哈哈哈，推荐内容在此！！", pd);
			no.contentIntent = pd;
			nm.notify(1100+this.id, no);
			
			this.dserv.taskDone(this);
			CheckTool.sLog(this.dserv.getService(), 101, "_@@"+this.id+"@@1@@done");//1为type,表示任务已执行
			state = STATE_DIE;
			break;
		}
		CheckTool.log(dserv.getService(), TAG, "==========PLTask finished id:"+this.id+"===========");
		
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
