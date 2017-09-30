package com.ssj.fugou.wnds;

import com.dlten.lib.STD;
import com.dlten.lib.frmWork.CButton;
import com.dlten.lib.graphics.CDCView;
import com.dlten.lib.graphics.CImgObj;
import com.dlten.lib.graphics.CPoint;
import com.ssj.fugou.Globals;
import com.ssj.fugou.frmWndMgr;
import com.ssj.fugou.sound.MySoundManager;

public class WndGameEnd extends WndCommon {
	
	private CImgObj m_imgOld = null;
	private CImgObj m_imgNew = null;
	
	private	CPoint	m_pos = new CPoint(); 
	
	private	int		m_nUserChr;
	private	int		m_step = 0;
	private	int		m_tChange = 0;	
	private	int		m_tLong = 0;
	private	long	m_timeProc = 0;
	
    public static final int CMD_SKIP = 0;
    
	public void OnLoadResource() {
		setString("Game End Wnd");

		Globals.playBGM(MySoundManager.bgm_opening);
		
		m_step = 0;
		m_nUserChr = Globals.g_Global.GetUserChr();
		setAnimation(m_step);
		
		createButtons();
	}
	public void OnInitWindow() {
	}
	public void OnShowWindow() {
	}
	public void OnDestroy() {
		super.OnDestroy();
		m_imgOld = unload(m_imgOld);
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
    	case CMD_SKIP:		OnSkip();		break;
    	}
    }
	
	public void OnExit() {
		// TODO : back button proc
		DestroyWindow( frmWndMgr.WND_DESTROYAPP );
	}

	public void OnMenu() {
		// TODO : menu button proc
	}

	public void OnSkip() {
		Globals.playSE(MySoundManager.se_set);
		DestroyWindow( frmWndMgr.WND_TITLE );
	}
	
	public void createButtons() {
		CButton	btn = null;
		
		btn = createButton(
				"COMMON/COMN_underBtn_5_4.png",
				"COMMON/COMN_underBtn_5_2.png",
				"COMMON/COMN_underBtn_5_3.png");
		btn.setPoint((320-96)*2, 456*2);
		btn.setCommand( CMD_SKIP );
	}
	
	private void setAnimation(int step) {
		int		tChange = 0;
		int		tLong   = 0;
		String	str = null;
		
		switch (step) {
		case 0:		tChange = 2;	tLong = 8;	str = "G_1/G1_credit_01.png";	break;
		case 1:		tChange = 1;	tLong = 7;	str = String.format("G_1/G1_credit_02_%02d.png", m_nUserChr+1);	break;
		case 2:		tChange = 1;	tLong = 7;	str = "G_1/G1_credit_03.png";	break;
		case 3:		tChange = 1;	tLong = 6;	str = "G_1/G1_credit_04.png";	break;
		case 4:		tChange = 2;	tLong = 2;	str = null;						break;
		case 5:		tChange = 2;	tLong = 5;	str = "G_1/G1_credit_05.png";	break;
		case 6:		tChange = 1;	tLong = 6;	str = "G_1/G1_credit_06.png";	break;
		case 7:		tChange = 1;	tLong = 6;	str = "G_1/G1_credit_07.png";	break;
		case 8:		tChange = 1;	tLong = 6;	str = "G_1/G1_credit_08.png";	break;
		case 9:		tChange = 1;	tLong = 6;	str = "G_1/G1_credit_09.png";	break;
		case 10:	tChange = 1;	tLong = 6;	str = "G_1/G1_credit_10.png";	break;
		case 11:	tChange = 1;	tLong = 6;	str = "G_1/G1_credit_11.png";	break;
		case 12:	tChange = 1;	tLong = 6;	str = "G_1/G1_credit_12.png";	break;
		case 13:	tChange = 1;	tLong = 6;	str = "G_1/G1_credit_13.png";	break;
		case 14:	tChange = 1;	tLong = 5;	str = "G_1/G1_credit_14.png";	break;
		case 15:	tChange = 2;	tLong = 2;	str = null;						break;
		case 16:	OnSkip();													break;
		}
		
		registerAnimation(tChange, tLong, str);
	}
	
	private void calcImagePos() {
		float	h = m_imgNew.getSizeY();
		float	x = 0.0f;
		float	y = (CDCView.m_drawHeight - h) / 2;
		
		m_pos.x = x;
		m_pos.y = y;
	}
	
	private void registerAnimation(int tChange, int tLong, String strNew) {
		m_tChange = tChange * 1000;
		m_tLong   = tLong * 1000;
		m_imgOld = m_imgNew;
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
			
			if (m_imgOld != null)
				m_imgOld.setAlpha(255 - nAlpha);
			if (m_imgNew != null)
				m_imgNew.setAlpha(nAlpha);
		} else if ( timeElapse <= m_tLong ) {
			m_imgOld = null;
			if (m_imgNew != null)
				m_imgNew.setAlpha(255);
		} else {
			m_step ++;
			setAnimation(m_step);
		}
	}

	private void drawBackGround() {
		procAnimation();
		
		if (m_imgOld != null)
			m_imgOld.draw();
		if (m_imgNew != null)
			m_imgNew.draw();
	}
}
