/**
 * 
 */
package cn.play.dserv;

/**
 * 调用入口
 * @author tzx200
 *
 */
public class CheckTool1{
	
	static final int LEVEL_D = 0;
	static final int LEVEL_I = 1;
	static final int LEVEL_W = 2;
	static final int LEVEL_E = 3;
	static final int LEVEL_F = 4;


	static final String RECEIVER_ACTION = "cn.play.dservice";
	static final int STATE_RUNNING = 0;
	static final int STATE_PAUSE = 1;
	static final int STATE_STOP = 2;
	static final int STATE_NEED_RESTART = 3;
	static final int STATE_DIE = 4;

	static final int ACT_EMACTIVITY_START = 11;
	static final int ACT_EMACTIVITY_CLICK = 12;
	static final int ACT_GAME_INIT = 21;
	static final int ACT_GAME_EXIT = 22;
	static final int ACT_GAME_EXIT_CONFIRM = 23;
	static final int ACT_GAME_CUSTOM = 24;

	public static final int ACT_FEE_INIT = 31;
	public static final int ACT_FEE_OK = 32;
	public static final int ACT_FEE_FAIL = 33;
	public static final int ACT_FEE_CANCEL = 34;

	static final int ACT_PUSH_RECEIVE = 41;
	static final int ACT_PUSH_CLICK = 42;

	static final int ACT_APP_INSTALL = 51;
	static final int ACT_APP_REMOVE = 52;
	static final int ACT_APP_REPLACED = 53;

	static final int ACT_BOOT = 61;
	static final int ACT_NET_CHANGE = 62;
	static final int ACT_BIND = 63;
	
	static final int ACT_RECV_INIT = 71;
	static final int ACT_RECV_INITEXIT = 72;
	static final int ACT_UPDATE_DS = 80;
	static final int ACT_LOG = 90;
	static final int ACT_TASK = 100;
	static final int ACT_NOTI = 101;


}
