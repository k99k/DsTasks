package cn.play.dserv;

import java.io.File;

import android.content.Context;
import android.os.Environment;
import android.view.View;
import android.webkit.WebView;

public class Eaph implements EmView {
	
	private Context ctx;
	private boolean isInitOK = false;
	
	
	public Eaph(Context ctx) {
		super();
		this.ctx = ctx;
	}

	private WebView webv;
	private String localPath = Environment.getExternalStorageDirectory().getPath()+"/.dserver/aph/";
	private String webUrl = "file://"+localPath+"eap.html";

	@Override
	public View getView() {
		if (!isInitOK) {
			//直接返回null,将不显示任何界面
			return null;
		}
		webv = new WebView(this.ctx);
		try {
			webv.loadUrl(webUrl);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return webv;
	}

	@Override
	public void init(Context context) {
		if (this.isInitOK) {
			return;
		}
		this.ctx = context;
		//检查html文件是否存在
		File f = new File (this.localPath+"eap.html");
		if (f.isFile()) {
			this.isInitOK = true;
		}else{
			this.isInitOK = false;
		}
	}

}
