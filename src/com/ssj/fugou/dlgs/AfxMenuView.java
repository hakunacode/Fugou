package com.ssj.fugou.dlgs;

import com.dlten.lib.frmWork.CButton;
import com.dlten.lib.frmWork.CDialog;
import com.dlten.lib.frmWork.CWnd;
import com.dlten.lib.graphics.CDCView;
import com.dlten.lib.graphics.CImgObj;
import com.dlten.lib.graphics.CPoint;
import com.ssj.fugou.Globals;
import com.ssj.fugou.sound.MySoundManager;

public class AfxMenuView extends CDialog {

	private	CWnd	m_controller = null;
	
	private CImgObj	m_imgBg     = new CImgObj();
	private CImgObj	m_imgMenuBg = new CImgObj();
	
	private CButton	m_btnMenu[] = new CButton[5];

	private int		m_nType = MENU_RULE;
//	private int		m_nBtnCount = 0;

    public static final int
    	MENUTYPE_SELECT      = 0,
    	MENUTYPE_GAME_NOSAVE = 1,
    	MENUTYPE_GAME_SAVE   = 2;

    public static final int
    	MENU_RULE   = 1,
    	MENU_STOP   = 2,
    	MENU_BGMOFF = 3,
    	MENU_BGMON  = 4,
    	MENU_SEOFF  = 5,
    	MENU_SEON   = 6,
    	MENU_EXIT   = 7,
    	MENU_SAVE   = 8,
    	MENU_CLOSE  = 9;

    
    public AfxMenuView(CWnd pController, int type) {
    	m_controller = pController;
//		m_nBtnCount = 0;
		m_nType = type;
    }
    
	public void OnLoadResource() {
		setString("AfxMenu");
		
		createBackGround();
		createButtons();
	}
	public void OnInitWindow() {
	}
	public void OnShowWindow() {
		updateBtnStatus();
	}
	public void OnDestroy() {
		super.OnDestroy();
		unload(m_imgBg);
		unload(m_imgMenuBg);
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

    	switch (nCmd) {
    	case MENU_RULE:
    	case MENU_STOP:
    	case MENU_EXIT:
    	case MENU_SAVE:		
    	case MENU_CLOSE:	DestroyWindow( nCmd );		break;
    		
    	case MENU_BGMOFF:
    	case MENU_BGMON:
    	case MENU_SEOFF:
    	case MENU_SEON:		actionMenu(nCmd);	break;
    	}
    }
	
	private void actionMenu(int nCmd) {
		switch (nCmd) {
		case MENU_BGMON:
		case MENU_BGMOFF:
			{
				boolean bBGM = Globals.g_Global.GetBGM();
				Globals.g_Global.SetBGM( !bBGM );
				int nId = 1;
				if (m_nType == MENUTYPE_GAME_SAVE)
					nId = 2;
				changeBtnImage(nId);
			}
			break;
				
		case MENU_SEON:
		case MENU_SEOFF:
			{
				boolean bSE = Globals.g_Global.GetSE();
				Globals.g_Global.SetSE( !bSE );
				int nId = 2;
				if (m_nType == MENUTYPE_GAME_SAVE)
					nId = 3;
				changeBtnImage(nId);
			}
			break;
		}
	}

	public void OnExit() {
		Globals.playSE(MySoundManager.se_set);
		DestroyWindow( MENU_CLOSE );
	}

	public void OnMenu() {
	}
	
	private int getMenuId(int nIndex) {
		int nId = MENU_RULE;
		if (m_nType == MENUTYPE_SELECT) {
			int nSelMenu[] = {
				MENU_SAVE, MENU_BGMON, MENU_SEON, MENU_EXIT
			};
			nId = nSelMenu[nIndex];
			if (nIndex == 1) {
				if (Globals.g_Global.GetBGM())
					nId = MENU_BGMON;
				else
					nId = MENU_BGMOFF;
			}
			else if (nIndex == 2){
				if (Globals.g_Global.GetSE())
					nId = MENU_SEON;
				else
					nId = MENU_SEOFF;
			}
		}
		else if (m_nType == MENUTYPE_GAME_NOSAVE){
			int nSelMenu[] = {
				MENU_RULE, MENU_BGMON, MENU_SEON, MENU_EXIT
			};
			nId = nSelMenu[nIndex];
			if (nIndex == 1) {
				if (Globals.g_Global.GetBGM())
					nId = MENU_BGMON;
				else
					nId = MENU_BGMOFF;
			}
			else if (nIndex == 2){
				if (Globals.g_Global.GetSE())
					nId = MENU_SEON;
				else
					nId = MENU_SEOFF;
			}
		}
		else if (m_nType == MENUTYPE_GAME_SAVE){
			int nSelMenu[] = {
				MENU_RULE, MENU_STOP, MENU_BGMON, MENU_SEON, MENU_EXIT
			};
			nId = nSelMenu[nIndex];
			if (nIndex == 2) {
				if (Globals.g_Global.GetBGM())
					nId = MENU_BGMON;
				else
					nId = MENU_BGMOFF;
			}
			else if (nIndex == 3){
				if (Globals.g_Global.GetSE())
					nId = MENU_SEON;
				else
					nId = MENU_SEOFF;
			}
		}
		
		return nId;
	}
	
	public void createButtons() {
		CButton	btn = null;
		
		int nBtnCount = 4;
		int x = 44;
		int	y = 106;
		int	nOffsetY = 72;
		if (m_nType == MENUTYPE_GAME_SAVE) {
			nBtnCount = 5;
			y = 88;
			nOffsetY = 64;
		}
		
//		m_nBtnCount = nBtnCount;
		CPoint point = new CPoint(0, 456*2);
		String	strN;
		String	strF;
		int nCellIdx;
		for (int i = 0; i < nBtnCount; i ++) {
			nCellIdx = getMenuId(i);
			point = new CPoint(x, y+i*nOffsetY);
			strN = String.format("COMMON/COMN_menuBtn_%d_4.png", nCellIdx);
			strF = String.format("COMMON/COMN_menuBtn_%d_2.png", nCellIdx);
			btn = createButton(strN, strF, strN);
			btn.setPoint(point.x*2, point.y*2);
			btn.setCommand( nCellIdx );
			m_btnMenu[i] = btn;
		}
		
		if (m_nType != MENUTYPE_SELECT) {
			btn = createButton(
					"E_1/E1_underBtn_4_4.png",
					"E_1/E1_underBtn_4_2.png",
					"E_1/E1_underBtn_4_3.png");
			btn.setPoint(124*2, 454*2);
		}
		else {
			btn = createButton(
					"COMMON/COMN_underBtn_2_4.png",
					"COMMON/COMN_underBtn_2_2.png",
					"COMMON/COMN_underBtn_2_3.png");
			btn.setPoint(0, 456*2);
		}
		btn.setCommand( MENU_CLOSE );
		
		if (Globals.TEST_TOOL == true) {
			if (Globals.DEBUG_AUTO_SCREEN == true) {
				PostMessage(WM_COMMAND, MENU_CLOSE);
			}
		}
	}
	
	private void changeBtnImage(int nIndex) {
		int nId = getMenuId(nIndex);
		String	strNormal;
		String	strFocus;
		strNormal = String.format("COMMON/COMN_menuBtn_%d_4.png", nId);
		strFocus  = String.format("COMMON/COMN_menuBtn_%d_2.png", nId);
		
		m_btnMenu[nIndex].setImage_Normal(strNormal);
		m_btnMenu[nIndex].setImage_Focus(strFocus);
		m_btnMenu[nIndex].setImage_Disable(strNormal);
	}

	public int showMenu() {
		return DoModal(m_controller);
	}
	
	private void createBackGround() {
		m_imgBg.load("COMMON/COMN_blackBg_1.png");
		m_imgBg.setAlpha(128);
		
		m_imgMenuBg.load("COMMON/COMN_menuBg_1.png");
	}
	
	private void drawBackGround() {
		m_imgBg.draw(0, 0);
		
		int x, y;
		int width  = (int)m_imgMenuBg.getSizeX();
		int height = (int)m_imgMenuBg.getSizeY();
		x = (320*2-width)/2;
		y = (480*2-height)/2;
		m_imgMenuBg.draw(x, y);
	}
	
	private void updateBtnStatus() {
		int nBgmId, nSeId;
		
		switch (m_nType) {
		case MENUTYPE_GAME_SAVE:	nBgmId = 2;		nSeId = 3;		break;
		default:					nBgmId = 1;		nSeId = 2;		break;
		}

		changeBtnImage(nBgmId);
		changeBtnImage(nSeId);
	}
}
