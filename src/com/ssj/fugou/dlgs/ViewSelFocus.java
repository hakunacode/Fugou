package com.ssj.fugou.dlgs;

import com.dlten.lib.frmWork.CTimerListner;
import com.dlten.lib.frmWork.CWnd;
import com.dlten.lib.graphics.CImgObj;
import com.dlten.lib.graphics.CPoint;
import com.dlten.lib.graphics.CSize;
import com.ssj.fugou.Globals;

public class ViewSelFocus implements CTimerListner {
	
	private	CWnd	m_parent = null;
	private CImgObj	m_imgFocus[] = new CImgObj[MAX_FRAME];
	
	private int		m_nType = Globals.FOCUS_TYPE1;
	private int		m_nFrame = 0;
	
	private int		m_timer = CWnd.ID_TIMER_0;
	private	CPoint	m_pos = new CPoint();
	private	CSize	m_size = new CSize();
	
	public	boolean m_bHidden = false;
	
	private	static final int	MAX_FRAME = 10;

	public ViewSelFocus(CWnd parent, int nType) {
		m_parent = parent;
		init(nType);
	}
	private void init(int nType) {
		m_nType = nType;//nType;
		m_nFrame = 0;
		
		String	str;
		for (int i = 0; i < MAX_FRAME; i ++) {
			str = String.format("D_1/D1_focus_%d_%02d.png", m_nType, i+1);
			m_imgFocus[i] = new CImgObj(str);
		}
		
		m_size.w = m_imgFocus[0].getSizeX(); 
		m_size.h = m_imgFocus[0].getSizeY();
		
		m_parent.SetTimer( m_timer, (int)(1000.0/18.0), this );
	}
    protected CImgObj unload(CImgObj img)
    {
    	if (img != null) {
    		img.unload();
    		img = null;
    	}
    	
    	return img;
    }
	public void destroy() {
		for (int i = 0; i < m_imgFocus.length; i ++) {
			m_imgFocus[i] = unload(m_imgFocus[i]);
		}
		m_imgFocus = null;
	}
	
	public void SetPos(CPoint pos) {
		m_pos = pos;
	}
	
	public void draw() {
		if (m_bHidden)
			return;
		
		float	x = m_pos.x*2;
		float	y = m_pos.y*2;
		
		x -= m_size.w/2;
		y -= m_size.h/2;
		
		m_imgFocus[m_nFrame].draw(x, y);
	}
	
	public void OnTimer() {
		m_nFrame ++;
		if (m_nFrame >= MAX_FRAME) {
			m_nFrame = 0;
		}
	}
	
	@Override
	public void TimerProc(CWnd pWnd, int nTimerID) {
		if (m_timer == nTimerID) {
			OnTimer();
		}
	}
}
