package com.ssj.fugou.test;

import android.content.Intent;
import android.net.Uri;

import com.dlten.lib.CBaseView;
import com.dlten.lib.frmWork.CWnd;
import com.ssj.fugou.frmWndMgr;

public class CTest2Wnd extends CWnd {
	
	public void OnPaint() {
		CBaseView view = getView();
		view.setColor(0x00FF00);
		view.drawString("Test2 Wnd", 100, 200, ANCHOR_LEFT|ANCHOR_TOP);
	}
	
	public void OnTouchDown( int x, int y ) {
    	Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"));
    	getView().getActivity().startActivity(intent);
	}
	
	public void OnKeyDown( int keycode ) {
		if( keycode == KEY_MENU )
			; // TODO : menu button proc
		else if( keycode == KEY_BACK )
			DestroyWindow( frmWndMgr.WND_DESTROYAPP ); // TODO : back button proc
	}
}
