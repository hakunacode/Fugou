package com.ssj.fugou.wnds;

import com.dlten.lib.STD;
import com.dlten.lib.graphics.CDCView;
import com.dlten.lib.graphics.CImgObj;
import com.dlten.lib.graphics.CPoint;
import com.ssj.fugou.frmWndMgr;


public class WndLogo extends WndCommon {
	
	private CImgObj m_imgOld = null;
	private CImgObj m_imgNew = null;
	
	private	CPoint	m_pos = new CPoint(); 
	
	private	int		m_step = 0;
	private	int		m_tChange = 0;	
	private	int		m_tLong = 0;
	private	long	m_timeProc = 0;
	
    
	public void OnLoadResource() {
		setString("logo Wnd");
		
		m_step = 0;
		setAnimation(m_step);
	}
	public void OnInitWindow() {
	}
	public void OnShowWindow() {
	}
	public void OnDestroy() {
		m_imgOld = unload(m_imgOld);
		m_imgNew = unload(m_imgNew);
		
		super.OnDestroy();
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
	
	public void OnExit() {
		// TODO : back button proc
		// DestroyWindow( frmWndMgr.WND_DESTROYAPP );
	}

	public void OnMenu() {
		// TODO : menu button proc
	}

	public void OnGotoTitle() {
		DestroyWindow( frmWndMgr.WND_TITLE );
	}
	
	private void setAnimation(int step) {
		int		tChange = 0;
		int		tLong   = 0;
		String	str = null;
		
		switch (step) {
		case 0:		tChange = 1;	tLong = 2;	str = "logo/startImg_1.png";		break;
		case 1:		tChange = 1;	tLong = 1;	str = null;							break;
		case 2:		OnGotoTitle();													break;
		}
		
		registerAnimation(tChange, tLong, str);
	}
	
	private void calcImagePos() {
		float	h = m_imgNew.getSizeY();
		float	x = 0.0f;
		float	y = (480*2 - h) / 2;
		
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
