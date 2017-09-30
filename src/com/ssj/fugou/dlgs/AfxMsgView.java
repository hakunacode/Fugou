package com.ssj.fugou.dlgs;

import com.dlten.lib.CBaseView;
import com.dlten.lib.frmWork.CButton;
import com.dlten.lib.frmWork.CDialog;
// import com.dlten.lib.frmWork.CEventWnd;
import com.dlten.lib.frmWork.CWnd;
import com.dlten.lib.graphics.CDCView;
import com.dlten.lib.graphics.CImgObj;
import com.ssj.fugou.Globals;
import com.ssj.fugou.sound.MySoundManager;

public class AfxMsgView extends CDialog {

	private	CWnd	m_controller = null;
	
	private CImgObj	m_imgBg    = new CImgObj();
	private CImgObj	m_imgMsgBg = new CImgObj();
	
	private CButton	m_btnYes = null;
	private CButton	m_btnNo = null;
	private CButton	m_btnOk = null;
	private	String		m_strMsg;
	private int		m_nType = MB_OK;
	
    public static final int
	MB_YESNO = 0,
	MB_OK = 1;

    public static final int
	ID_NONE = 0,
	ID_OK   = 1,
	ID_YES  = 2,
	ID_NO   = 3;

    
    public AfxMsgView(CWnd pController) {
    	m_controller = pController;
    	m_nType = MB_OK;
    }
    
	public void OnLoadResource() {
		setString("AfxMessageBox");
		
		createBackGround();
		createButtons();
		updateBtnStatus();
	}
	public void OnInitWindow() {
	}
	public void OnShowWindow() {
		updateBtnStatus();
	}
	public void OnDestroy() {
		super.OnDestroy();
		unload(m_imgBg);
		unload(m_imgMsgBg);
	}
	
	public void OnPaint() {
		// drawing dimmension is just Globals.RES_WIDTH * Globals.RES_HEIGHT
		drawBackGround();
		if (Globals.TEST_TOOL == true) {
			if (Globals.DEBUG_AUTO_SCREEN == true) {
	    		drawStr(CDCView.CODE_WIDTH/2, CDCView.CODE_HEIGHT/2, 0x00FF00, ANCHOR_CENTER | ANCHOR_MIDDLE, "Auto Screen");
			}
		}
	}
	
	public void OnKeyDown( int keycode ) {
		switch (keycode) {
		case KEY_MENU:		OnMenu();						break;
		case KEY_BACK:		OnExit();						break;
		default:			super.OnKeyDown(keycode);		break;
		}
	}
    public void OnCommand(int nCmd) {
    	Globals.playSE(MySoundManager.se_set);
    	DestroyWindow( nCmd );
    }
	
	public void OnExit() {
		int nCmd = ID_NO;
		if (m_nType == MB_OK)
			nCmd = ID_OK;

		DestroyWindow( nCmd );
	}

	public void OnMenu() {
	}
	
	public void createButtons() {
		CButton	btn = null;
		
		btn = createButton(
				"COMMON/COMN_mesBtn_1_4.png",
				"COMMON/COMN_mesBtn_1_2.png",
				"COMMON/COMN_mesBtn_1_3.png");
		btn.setPoint(124*2, 270*2);
		btn.setCommand( ID_OK );
		m_btnOk = btn;
		
		btn = createButton(
				"COMMON/COMN_mesBtn_2_4.png",
				"COMMON/COMN_mesBtn_2_2.png",
				"COMMON/COMN_mesBtn_2_3.png");
		btn.setPoint(78*2, 270*2);
		btn.setCommand( ID_YES );
		m_btnYes = btn;
		
		btn = createButton(
				"COMMON/COMN_mesBtn_3_4.png",
				"COMMON/COMN_mesBtn_3_2.png",
				"COMMON/COMN_mesBtn_3_3.png");
		btn.setPoint(170*2, 270*2);
		btn.setCommand( ID_NO );
		m_btnNo = btn;
		
		if (Globals.TEST_TOOL == true) {
			if (Globals.DEBUG_AUTO_SCREEN == true) {
				if (m_nType == MB_OK)
					PostMessage(WM_COMMAND, ID_OK);
				else
					PostMessage(WM_COMMAND, ID_YES);
			}
		}
	}
	
	private void createBackGround() {
		m_imgBg.load("COMMON/COMN_blackBg_1.png");
		m_imgBg.setAlpha(128);
		
		m_imgMsgBg.load("COMMON/COMN_mesWind_1.png");
	}
	
	private void drawBackGround() {
		m_imgBg.draw(0, 0);
		
		int x, y;
		int width  = (int)m_imgMsgBg.getSizeX();
		int height = (int)m_imgMsgBg.getSizeY();
		x = (320*2-width)/2;
		y = (480*2-height)/2;
		m_imgMsgBg.draw(x, y);

		CBaseView view = getView();
		int	nOldSize = view.setFontSize(16*2);

		drawRectStr(x+3, y+3, width, height-64, 0x000000, ANCHOR_CENTER | ANCHOR_MIDDLE, m_strMsg);
		drawRectStr(x  , y  , width, height-64, 0xFFFFFF, ANCHOR_CENTER | ANCHOR_MIDDLE, m_strMsg);
		
		view.setFontSize(nOldSize);
	}
	
	private void updateBtnStatus() {
		boolean bOk = (m_nType == MB_OK) ? true : false;
		
		m_btnOk.setVisible( bOk );
		m_btnYes.setVisible( !bOk );
		m_btnNo.setVisible( !bOk );
	}
	
	public int showMsgBox(String str, int nType) {
		m_strMsg = str;
		m_nType = nType;
		
		return DoModal(m_controller);
	}
}
