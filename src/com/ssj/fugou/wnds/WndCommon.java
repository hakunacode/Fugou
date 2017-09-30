package com.ssj.fugou.wnds;

import com.dlten.lib.STD;
import com.dlten.lib.frmWork.CWnd;
import com.dlten.lib.graphics.CDCView;
import com.ssj.fugou.Globals;

public class WndCommon extends CWnd {
    
    public void OnSuspend() {
    	Globals.pauseBGM();
    }
    public void OnResume() {
    	Globals.resumeBGM();
    }
    
	public void OnDestroy() {
	    STD.logHeap();
		
		super.OnDestroy();
	}

	public void debug_drawStatus() {
		if (Globals.TEST_TOOL == true) {
			if (Globals.DEBUG_AUTO_SCREEN == true) {
	    		drawStr(CDCView.CODE_WIDTH/2, CDCView.CODE_HEIGHT/2, 0x00FF00, ANCHOR_CENTER | ANCHOR_MIDDLE, "Auto Screen");
			}
		}
	}
}
