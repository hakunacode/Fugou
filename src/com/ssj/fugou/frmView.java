package com.ssj.fugou;

import android.content.Context;
import android.util.AttributeSet;

import com.dlten.lib.*;
import com.dlten.lib.frmWork.CWndMgr;

/**
 * <p>Title: frmView</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: dlten</p>
 *
 * @author tendroid
 * @version 1.0
 */
public class frmView extends CBaseView {
	
	public frmView(Context context, AttributeSet attrs){
		super( context, attrs );
		Initialize();
	}
	public frmView(Context context){
		super(context);
		Initialize();
	}
	private void Initialize() {
		start();
		setFPS(1000.0f / Common.FPS);
	}
	public void Finish() {
		stop();
	}
	
	public CWndMgr createWndMgr() {
		frmActivity activity = (frmActivity) getActivity();
		return new frmWndMgr( activity, this );
	}

}
