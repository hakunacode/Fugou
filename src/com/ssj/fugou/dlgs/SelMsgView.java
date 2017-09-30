package com.ssj.fugou.dlgs;

import com.dlten.lib.CBaseView;
import com.dlten.lib.STD;
import com.dlten.lib.frmWork.CButton;
import com.dlten.lib.frmWork.CDialog;
import com.dlten.lib.frmWork.CWnd;
import com.dlten.lib.graphics.CDCView;
import com.dlten.lib.graphics.CImgObj;
import com.dlten.lib.graphics.CPoint;
import com.dlten.lib.graphics.CRect;
import com.ssj.fugou.Globals;
import com.ssj.fugou.sound.MySoundManager;

public class SelMsgView extends CDialog {
	
	private	CWnd	m_controller = null;
	
	private CButton	m_btnMenu = null;
	private CButton	m_btnSet  = null;
	
	private CImgObj	m_imgBg    = new CImgObj();
	private CImgObj	m_imgMsgBg = new CImgObj();
	
	private int		m_nType;
	private String	m_strMsg;
	public	boolean	hidden = false;
	
	private	long	m_timeStart = 0;
	private	long	m_timeElapse = 2000;
	
	
	
    public static final int
	SELMSG_CHR_UNIFY = 0,
	SELMSG_CHR_FREE  = 1,
	SELMSG_NPC_UNIFY = 2,
	SELMSG_NPC_FREE  = 3,
	GAMEMSG_NEXTGAME = 4,
	GAMEMSG_EXCHANGE_DAIFUGOU  = 5,
	GAMEMSG_EXCHANGE_FUGOU     = 6,
	GAMEMSG_EXCHANGE_HEIMIN    = 7,
	GAMEMSG_EXCHANGE_HINMIN    = 8,
	GAMEMSG_EXCHANGE_DAIHINMIN = 9;
    
    public static final int
    GAMEMSG_MENU	= 0xFF,
    GAMEMSG_SET		= 0xF0;
    
    public static final int
    CMD_MENU	= 0,
    CMD_SET		= 1;

    
    public SelMsgView(CWnd pController, int nType) {
    	m_controller = pController;
    	m_nType = nType;
    }
    
	public void OnLoadResource() {
		setString("Select Message Wnd");
		
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
		OnTimer();
		
		if (Globals.TEST_TOOL == true) {
			if (Globals.DEBUG_AUTO_SCREEN == true) {
	    		drawStr(CDCView.CODE_WIDTH/2, CDCView.CODE_HEIGHT/2, 0x00FF00, ANCHOR_CENTER | ANCHOR_MIDDLE, "Auto Screen");
			}
		}
	}
	
	public void OnTouchDown( int x, int y ) {
		if (m_nType == GAMEMSG_NEXTGAME)
			return;
		
		int width  = (int)m_imgMsgBg.getSizeX();
		int height = (int)m_imgMsgBg.getSizeY();
		CRect rect = new CRect((320*2-width)/2, (480*2-height)/2, width, height);
		CPoint location = new CPoint(x, y);
		
		if (rect.PtInRect(location)) {
			hideMsg();
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
    	switch (nCmd) {
    	case CMD_MENU:		actionMenu();	break;
    	case CMD_SET:		actionSet();	break;
    	}
    }
	
	public void OnExit() {
		DestroyWindow(-1);
	}

	public void OnMenu() {
	}
	
	private void actionMenu() {
		Globals.playSE(MySoundManager.se_set);
		DestroyWindow( GAMEMSG_MENU );
	}

	private void actionSet() {
		Globals.playSE(MySoundManager.se_set);
		hideMsg();
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
		
		drawRectStr(x+3, y+3, width, height, 0x000000, ANCHOR_CENTER | ANCHOR_MIDDLE, m_strMsg);
		drawRectStr(x  , y  , width, height, 0xFFFFFF, ANCHOR_CENTER | ANCHOR_MIDDLE, m_strMsg);
		
		view.setFontSize(nOldSize);
	}
	
	public void createButtons() {
		CButton	btn = null;
		
		btn = createButton(
				"E_1/E1_underBtn_3_4.png",
				"E_1/E1_underBtn_3_2.png",
				"E_1/E1_underBtn_3_3.png");
		btn.setPoint(124*2, 454*2);
		btn.setCommand( CMD_MENU );
		m_btnMenu = btn;
		
		btn = createButton(
				"E_1/E1_underBtn_2_4.png",
				"E_1/E1_underBtn_2_2.png",
				"E_1/E1_underBtn_2_3.png");
		btn.setPoint(210*2, 452*2);
		btn.setCommand( CMD_SET );
		m_btnSet = btn;
	}
	
	private void updateBtnStatus() {
		boolean bShow = true;
		if (m_nType != GAMEMSG_NEXTGAME)
			bShow = false;
		m_btnMenu.setVisible(bShow);
		m_btnSet.setVisible(bShow);
	}
	
	public int showMsg(int nType) {
		m_nType = nType;
		setMsgString(m_nType);
		
		hidden = false;
		if (m_nType != GAMEMSG_NEXTGAME) {
			m_timeStart = STD.GetTickCount();
		} else {
			if (Globals.TEST_TOOL == true) {
				if (Globals.DEBUG_AUTO_SCREEN == true) {
					m_timeStart = STD.GetTickCount();
				}
			}
		}
		
		return DoModal(m_controller);
	}
	
	private void setMsgString(int nType) {
		String	str;
		switch (nType) {
		case SELMSG_CHR_UNIFY:			str = "天下統一を共に目指す\nキャラクターを選択してください。";		break;
		case SELMSG_CHR_FREE:			str = "大富豪を共に楽しむ\nキャラクターを選択してください。";		break;
		case SELMSG_NPC_UNIFY:			str = "攻め込む場所を\n選択してください。";					break;
		case SELMSG_NPC_FREE:			str = "対戦する相手を\n四人選択してください。";					break;
		case GAMEMSG_NEXTGAME:			str = "次の戦いに進むときは決定を\n保存などを行うときはメニューを\nタップしてください。";	break;
		case GAMEMSG_EXCHANGE_DAIFUGOU:	str = "交換するカードを\n２枚選択してください。";				break;
		case GAMEMSG_EXCHANGE_FUGOU:	str = "交換するカードを\n１枚選択してください。";				break;
		case GAMEMSG_EXCHANGE_HEIMIN:	str = "カード交換中です。";									break;
		case GAMEMSG_EXCHANGE_HINMIN:	str = "手札で最も強いカードを\n１枚選択してください。";			break;
		case GAMEMSG_EXCHANGE_DAIHINMIN:str = "手札で最も強いカードを\n２枚選択してください。";			break;
		default:						str = "";												break;
		}
		m_strMsg = str;
	}

	public void hideMsg() {
		if (m_timeStart != 0) {
			m_timeStart = 0;
		}
		
		DestroyWindow( m_nType );
	}

	public void OnTimer() {
		if (m_timeStart == 0)
			return;
		
		long	timeElapse = STD.GetTickCount() - m_timeStart;
		if (timeElapse < m_timeElapse)
			return;
		
		if (hidden == false) {
			hideMsg();
		}	
	}
}
