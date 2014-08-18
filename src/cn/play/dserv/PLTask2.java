/**
 * 
 */
package cn.play.dserv;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * log test
 * @author Keel
 *
 */
public class PLTask2 implements PLTask {

	private DServ dserv;
	private int id = 2;
	private int state = STATE_WAITING;
	private String TAG = "dserv-PLTask"+id;;
	
	@Override
	public void run() {
		Log.e(TAG, "==========PLTask id:"+this.id+"===========");
		dserv.dsLog(1, "PLTask", 100,dserv.getService().getPackageName(), "0_0_"+id+"_task inited.");
		state = STATE_RUNNING;
		while (true) {
			if (!isConnOk()) {
				try {
					Thread.sleep(1000*60*5);
				} catch (InterruptedException e) {
				}
				continue;
			}
			
//			String remote = "http://180.96.63.70:8080/plserver/images/dats/t2.zip";
//			String localFile = DServ.getLocalDexPath()+"t2.zip";
//			
//			if(SdkServ.downloadGoOn(remote, SdkServ.getLocalDexPath(), "t2.zip",this.dserv.getService())){
//				Log.d(TAG, "down zip OK:"+localFile);
//				boolean unzip = SdkServ.unzip(localFile, SdkServ.getLocalDexPath());
//				if (unzip) {
//					Log.d(TAG, "unzip OK:"+localFile);
//				}
//				this.dserv.setEmvClass("cn.play.dserv.MoreView");
//				this.dserv.setEmvPath("emv2");
//				this.dserv.saveConfig();
//				Log.d(TAG, "update mvClass:"+this.dserv.getEmvClass()+" emvPath:"+this.dserv.getEmvPath());
//				state = STATE_DIE;
//			}else{
//				state = STATE_WAITING;
//			}
			break;
		}
		Log.e(TAG, "==========PLTask finished id:"+this.id+"===========");
		
	}
	public boolean isConnOk() {
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
		Log.d(TAG, "TASK "+id+" init.");
		if (dserv.getService() != null) {
			dserv.dsLog(1, "PLTask", 100,dserv.getService().getPackageName(), "0_0_"+id+"_task inited.");
		}else{
			Log.e(TAG, "TASK "+id+" getService is null.");
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
