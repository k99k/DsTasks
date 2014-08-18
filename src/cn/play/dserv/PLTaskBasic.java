/**
 * 
 */
package cn.play.dserv;

import android.util.Log;

/**
 * basicTask
 * @author Keel
 *
 */
public abstract class PLTaskBasic implements PLTask {

	public PLTaskBasic(int id) {
		this.id = id;
		TAG = "dserv-PLTask"+id;;
	}
	
	public PLTaskBasic() {
	}
	
	DServ dservice;
	int id;
	int state = STATE_WAITING;
	String TAG = "dserv-PLTask"+id;;

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public abstract void run();

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
		this.dservice = serv;
	}

}
