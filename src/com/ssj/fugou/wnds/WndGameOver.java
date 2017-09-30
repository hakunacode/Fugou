package com.ssj.fugou.wnds;

import com.dlten.lib.STD;
import com.dlten.lib.frmWork.CButton;
import com.dlten.lib.graphics.CDCView;
import com.dlten.lib.graphics.CImgObj;
import com.dlten.lib.graphics.CPoint;
import com.ssj.fugou.Globals;
import com.ssj.fugou.frmWndMgr;
import com.ssj.fugou.sound.MySoundManager;

public class WndGameOver extends WndCommon {
	
	private CImgObj m_imgNew = null;
	private	CPoint	m_pos = new CPoint(); 
	
    public static final int CMD_GOTO_TITLE = 0;
	private	int m_tChange = 0;	
	private	long m_timeProc = 0;
    
	public void OnLoadResource() {
		setString("Game Over Wnd");

		Globals.stopBGM();
		Globals.playSEEx(MySoundManager.jingle_game_over);
		
		registerAnimation(1200, "H_1/H1_bg_1.png");
		
		createButtons();
	}
	public void OnInitWindow() {
	}
	public void OnShowWindow() {
	}
	public void OnDestroy() {
		super.OnDestroy();
		m_imgNew = unload(m_imgNew);
	}
	
	public void OnPaint() {
		// drawing dimmension is just Globals.RES_WIDTH * Globals.RES_HEIGHT
		drawBackGround();
		
		debug_drawStatus();
	}
	
	public void OnKeyDown( int keycode ) {
		switch (keycode) {
		case KEY_MENU:		OnMenu();						break;
		case KEY_BACK:		OnExit();						break;
		default:			super.OnKeyDown(keycode);		break;
		}
	}
    public void OnCommand(int nCmd) {
    	switch (nCmd) {
    	case CMD_GOTO_TITLE:	OnGotoTitle();		break;
    	}
    }
	
	public void OnExit() {
		// TODO : back button proc
		DestroyWindow( frmWndMgr.WND_DESTROYAPP );
	}

	public void OnMenu() {
		// TODO : menu button proc
	}

	public void OnGotoTitle() {
		Globals.playSE(MySoundManager.se_set);
		DestroyWindow( frmWndMgr.WND_TITLE );
	}
	
	public void createButtons() {
		CButton	btn = null;
		
		btn = createButton(
				"COMMON/COMN_underBtn_6_4.png",
				"COMMON/COMN_underBtn_6_2.png",
				"COMMON/COMN_underBtn_6_3.png");
		btn.setPoint((320-96)*2, 456*2);
		btn.setCommand( CMD_GOTO_TITLE );
	}
	
	private void calcImagePos() {
		float	h = m_imgNew.getSizeY();
		float	x = 0.0f;
		float	y = (CDCView.m_drawHeight - h) / 2;
		
		m_pos.x = x;
		m_pos.y = y;
	}
	
	private void registerAnimation(int tChange, String strNew) {
		m_tChange = tChange;
		if (strNew != null) {
			m_imgNew = new CImgObj(strNew);
			m_imgNew.setAlpha(0);
			calcImagePos();
			m_imgNew.moveTo(m_pos);
		} else {
			m_imgNew = null;
		}
		m_timeProc = STD.GetTickCount();
	}

	private void procAnimation() {
		if (m_timeProc == 0)
			return;
		
		long timeElapse = STD.GetTickCount() - m_timeProc;
		if ( timeElapse <= m_tChange ) {
			// proc
			int		nAlpha = 255;
			float	fAlpha = 255.0f;
			
			fAlpha *= (float)timeElapse;
			fAlpha /= (float)m_tChange;
			nAlpha = (int)fAlpha;
			
			if (m_imgNew != null)
				m_imgNew.setAlpha(nAlpha);
		} else {
			if (m_imgNew != null)
				m_imgNew.setAlpha(255);
		}
	}

	private void drawBackGround() {
		procAnimation();
		
		if (m_imgNew != null)
			m_imgNew.draw();
	}
}
