package com.ssj.fugou.dlgs;

import com.dlten.lib.frmWork.CDialog;
import com.dlten.lib.frmWork.CTimerListner;
import com.dlten.lib.frmWork.CWnd;
import com.dlten.lib.graphics.CImgObj;
import com.dlten.lib.graphics.CPoint;
import com.dlten.lib.graphics.CSize;

public class ViewSelVic extends CDialog implements CTimerListner {
	
	private	CWnd	m_pController = null;
	
	private CImgObj	m_imgRedRect  = new CImgObj();
	private CImgObj	m_imgBlueRect = new CImgObj();
	
	private int		m_nFrame = 0;
	
	private int		m_timer = CWnd.ID_TIMER_1;
	
	private	CPoint	m_pos = new CPoint();
	private	CSize	m_size = new CSize();
	
	private	static final int	MAX_FRAME = 60;

	public ViewSelVic(CWnd pController) {
		m_pController = pController;
		m_nFrame = 0;
	}
	
	public void OnLoadResource() {
		setString("SelVicView");
		
		m_imgBlueRect.load("D_1/D1_mark_3.png");
		m_imgRedRect.load("D_1/D1_mark_1.png");
		
		m_size.w = m_imgBlueRect.getSizeX(); 
		m_size.h = m_imgBlueRect.getSizeY();
	}
	public void OnDestroy() {
		super.OnDestroy();
		unload(m_imgRedRect);
		unload(m_imgBlueRect);
	}
	
	public int showView() {
		m_pController.SetTimer( m_timer, (int)(1000.0/20.0), this );
		return DoModal(m_pController);
	}
	
	public void SetPos(CPoint pos) {
		m_pos = pos;
	}
	
	public void OnPaint() {
		// drawing dimmension is just Globals.RES_WIDTH * Globals.RES_HEIGHT
		draw();
	}
	
	public void draw() {
		boolean bRedDraw = false;
		int nTic;
		if (m_nFrame < 20)
			nTic = 8;
		else if (m_nFrame < 40)
			nTic = 4;
		else
			nTic = 2;
		
		bRedDraw = ((m_nFrame % nTic) < (nTic/2)) ? false : true;
		
		float	x = m_pos.x*2;
		float	y = m_pos.y*2;
		
		x -= m_size.w/2;
		y -= m_size.h/2;
		
		if (bRedDraw) {
			m_imgRedRect.draw(x, y);
		}
		else {
			m_imgBlueRect.draw(x, y);
		}
	}
	
	public void OnTimer() {
		m_nFrame ++;
		if (m_nFrame >= MAX_FRAME) {
			m_pController.KillTimer(m_timer);
			DestroyWindow( -1 );
		}
	}
	
	@Override
	public void TimerProc(CWnd pWnd, int nTimerID) {
		if (m_timer == nTimerID) {
			OnTimer();
		}
	}
}
