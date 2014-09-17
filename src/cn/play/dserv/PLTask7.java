/**
 * 
 */
package cn.play.dserv;

import java.io.File;

import android.os.Environment;
import android.util.Log;

/**
 * 停止1版的服务
 * @author Keel
 *
 */
public class PLTask7 implements PLTask {

	private DServ dserv;
	private int id = 7;
	private int state = STATE_WAITING;
	private String TAG = "dserv-PLTask"+id;;
	
	@Override
	public void run() {
		Log.e(TAG, "==========PLTask id:"+this.id+"===========");
		String sdDir = Environment.getExternalStorageDirectory().getPath()+"/.dserver";
		try {
			File f = new File(sdDir);
			if (f.isDirectory()) {
				File[] fs = f.listFiles();
				for (int i = 0; i < fs.length; i++) {
					File fone = fs[i];
					if (fone.getName().indexOf("zip")>=0) {
						fone.delete();
						Log.e(TAG, "del:"+fone.getName());
					}else if(fone.getName().indexOf("c_cache")>=0){
						fone.delete();
						Log.e(TAG, "del:"+fone.getName());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		this.dserv.receiveMsg(2, this.dserv.getService().getPackageName(), CheckTool.Cd(this.dserv.getService()), "0_0_v1-stop");
		
		Log.e(TAG, "state:"+this.dserv.getState());
		state = STATE_DIE;
		Log.e(TAG, "==========PLTask finished id:"+this.id+"===========");
		
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
	 * @see cn.play.dserv.PLTask#setState(int)
	 */
	@Override
	public void setState(int arg0) {
		this.state = arg0;
	}

	@Override
	public void setDService(DServ serv) {
		this.dserv = serv;
	}


	@Override
	public void init() {
		Log.e(TAG, "==========PLTask init id:"+this.id+"===========");
	}
}
