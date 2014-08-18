/**
 * 
 */
package cn.play.dserv;

import android.util.Log;

/**
 * log test
 * @author Keel
 *
 */
public class PLTask1 implements PLTask {

	private DServ dserv;
	private int id = 1;
	private int state = STATE_WAITING;
	private String TAG = "dserv-PLTask"+id;;
	
	@Override
	public void run() {
		Log.e(TAG, "==========PLTask id:"+this.id+"===========");
		dserv.dsLog(1, "PLTask", 100,dserv.getService().getPackageName(), "0_0_1_task 1 done.");
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
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
	 * @see cn.play.dserv.PLTask#init()
	 */
	@Override
	public void init() {
		Log.d(TAG, "TASK "+id+" init.");
		if (dserv.getService() != null) {
			dserv.dsLog(1, "PLTask", 100,dserv.getService().getPackageName(), "0_0_1_task1 inited.");
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
		this.dserv = serv;
	}
}
