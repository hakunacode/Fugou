package com.ssj.fugou.test;

import com.dlten.lib.frmWork.CWnd;
import com.dlten.lib.graphics.CImgObj;
import com.ssj.fugou.frmWndMgr;

public class CTest1Wnd extends CWnd {
	
	private CImgObj m_imgBG = new CImgObj();
	
	public void OnLoadResource() {
		m_imgBG.load("logo/startImg_1.png");
	}
	public void OnInitWindow() {
	}
	public void OnShowWindow() {
	}
	public void OnDestroy() {
	}
	
	public void OnPaint() {
		// drawing dimmension is just Globals.RES_WIDTH * Globals.RES_HEIGHT

		m_imgBG.draw(0, 0);
		
//		CBaseView view = getView();
//		
//		CImageTexture	img = (CImageTexture)m_imgBG.getParent();
//		int textureID;
//		CRect	rectTexture;
//		
//		textureID   = img.getTextureID();
//		rectTexture = img.getTextureRect();
//		
//		int		x = 0;
//		int		y = 0;
//		int		w = (int)m_imgBG.getSizeX();
//		int		h = (int)m_imgBG.getSizeY();
//		float	fScaleX = m_imgBG.getScaleX();
//		float	fScaleY = m_imgBG.getScaleY();
//		
//		view.setTexture( textureID );
//		view.drawImage(x, y, w, h, fScaleX, fScaleY, rectTexture); // for CGLDCView
//		view.updateGraphics();
	}
	
	public void OnTouchDown( int x, int y ) {
//		DestroyWindow( frmWndMgr.WND_TEST2 );
	}
	
	public void OnKeyDown( int keycode ) {
		if( keycode == KEY_MENU )
			; // TODO : menu button proc
		else if( keycode == KEY_BACK )
			DestroyWindow( frmWndMgr.WND_DESTROYAPP ); // TODO : back button proc
	}
}
