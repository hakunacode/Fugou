package com.ssj.fugou.wnds;

import com.dlten.lib.CBaseView;
import com.dlten.lib.frmWork.CButton;
import com.dlten.lib.graphics.CDCView;
import com.dlten.lib.graphics.CImgObj;
import com.dlten.lib.graphics.CPoint;
import com.dlten.lib.graphics.CRect;
import com.ssj.fugou.Globals;
import com.ssj.fugou.frmWndMgr;
import com.ssj.fugou.dlgs.*;
import com.ssj.fugou.sound.MySoundManager;

public class WndSelChr extends WndCommon {
	
	private CImgObj m_imgUpBg      = new CImgObj();
	private CImgObj m_imgDownBg    = new CImgObj();
	private CImgObj m_imgParamBg[] = new CImgObj[4];
	private CImgObj m_imgChr       = new CImgObj();
	private CImgObj m_imgChrName   = new CImgObj();
	
	private CButton	m_btnCountry[] = new CButton[Globals.COUNTRY_MAX];
	
	private ViewSelFocus	m_viewFocus = null;
	private SelMsgView		m_viewMsg = null;
	
	private int		m_nCharacter;
	private int		m_nCountry;
	
    public static final int
	CMD_SET     = 0,
	CMD_BACK    = 1,
	CMD_COUNTRY = 2;
    
	public void OnLoadResource() {
		setString("Select Character Wnd");

		Globals.playBGM(MySoundManager.bgm_chara_select);
		
		if (Globals.g_Global.GetGameMode() == Globals.GM_UNIFY)
		{
			m_nCountry = Globals.g_Global.GetUserCountry();
			m_nCharacter = Globals.g_Global.GetUserChr();		
		}
		else
		{
			m_nCountry = Globals._XIANGYANG;
			Globals.COUNTRY_INFO info = Globals.GetCountryInfo(m_nCountry);
			m_nCharacter = info.nCharacter;		
		}
		
		createBackGround();
		createButtons();
		
		m_viewFocus = new ViewSelFocus(this, Globals.FOCUS_TYPE1);
		setFocusPos();
		
		int nType;
		if (Globals.g_Global.GetGameMode() == Globals.GM_UNIFY)
			nType = SelMsgView.SELMSG_CHR_UNIFY;
		else
			nType = SelMsgView.SELMSG_CHR_FREE;
		m_viewMsg = new SelMsgView(this, nType);//kgh
		m_viewMsg.showMsg(nType);
	}
	public void OnInitWindow() {
	}
	public void OnShowWindow() {
	}
	public void OnDestroy() {
		super.OnDestroy();
		
		m_viewFocus.destroy();
		m_viewFocus = null;
		m_viewMsg = null;
		
		int	i;
		m_imgUpBg    = unload(m_imgUpBg);
		m_imgDownBg  = unload(m_imgDownBg);
		for (i = 0; i < m_imgParamBg.length; i ++) {
			m_imgParamBg[i] = unload(m_imgParamBg[i]);
		}
		m_imgParamBg = null;
		m_imgChr     = unload(m_imgChr);
		m_imgChrName = unload(m_imgChrName);
	}
	
	public void OnPaint() {
		// drawing dimmension is just Globals.RES_WIDTH * Globals.RES_HEIGHT
		drawBackGround();
		m_viewFocus.draw();
		
		debug_drawStatus();
	}
	
	public void OnTouchDown( int x, int y ) {
		if (Globals.TEST_TOOL == true) {
			CRect	rect  = new CRect(0, 0, 320*2, 240*2);
			CPoint	location = new CPoint(x, y);
			if (rect.PtInRect(location) == true) {
				Globals.DEBUG_AUTO_SCREEN = !Globals.DEBUG_AUTO_SCREEN;
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
    	switch (nCmd) {
    	case CMD_SET:		OnOK();				break;
    	case CMD_BACK:		OnBack();			break;
    	default:			OnCountry(nCmd);	break;
    	}
    }
	
	public void OnExit() {
		// TODO : back button proc
		DestroyWindow( frmWndMgr.WND_DESTROYAPP );
	}

	public void OnMenu() {
		// TODO : menu button proc
	}

	public void OnOK() {
		if (isSelectableCountry(m_nCountry) == false) {
			Globals.playSE(MySoundManager.se_error);
			return;
		}
		
		Globals.playSE(MySoundManager.se_set);
		
		Globals.g_Global.SetUserCountry(m_nCountry);
		Globals.g_Global.SetNPCCountry(m_nCountry);
		Globals.g_Global.SetCaptureCountry(m_nCountry, true);
		
		Globals.m_bVictoryAnim = false;

		if (Globals.g_Global.GetGameMode() == Globals.GM_UNIFY)
			DestroyWindow( frmWndMgr.WND_SELUNIFYNPC );
		else
			DestroyWindow( frmWndMgr.WND_SELFREENPC );
	}
	
	public void OnBack() {
		Globals.playSE(MySoundManager.se_error);
		DestroyWindow( frmWndMgr.WND_TITLE );
	}
	
	public void OnCountry(int nCmd) {
		int nId = nCmd - CMD_COUNTRY;
		if (nId == m_nCountry)
			return;
		Globals.playSE(MySoundManager.se_cursor);
		updateInfo(nId);
	}
	
	public void createButtons() {
		CButton	btn = null;
		
		btn = createButton(
				"COMMON/COMN_underBtn_2_4.png",
				"COMMON/COMN_underBtn_2_2.png",
				"COMMON/COMN_underBtn_2_3.png");
		btn.setPoint(0, 456*2);
		btn.setCommand( CMD_BACK );

		btn = createButton(
				"COMMON/COMN_underBtn_3_4.png",
				"COMMON/COMN_underBtn_3_2.png",
				"COMMON/COMN_underBtn_3_3.png");
		btn.setPoint((320-96)*2, 456*2);
		btn.setCommand( CMD_SET );
		
		String	str;
		CPoint	point;
		int nId;
		for (int i = 0; i < Globals.COUNTRY_MAX; i ++)
		{
			if (isSelectableCountry(i) == false)
				nId = 2;
			else 
				nId = 4;
			
			point = Globals.getCountryPos(i);
			str = String.format("D_1/D1_mark_%d.png", nId);
			
			btn = createButton(str, str, str);
			btn.setPoint(point.x*2, point.y*2);
			btn.setCommand( CMD_COUNTRY + i );
			m_btnCountry[i] = btn;
		}
	}
	
	private boolean isSelectableCountry(int nCountry) {
		if (Globals.g_Global.GetGameMode() == Globals.GM_UNIFY) {
			if (Globals.g_Global.IsCaptureCountry(nCountry)) {
				//ASSERT(FALSE);
				return false;
			}
			
			Globals.COUNTRY_INFO info = Globals.GetCountryInfo(nCountry);
			int nCharacter = info.nCharacter;
			if (nCharacter == Globals.CHR_SHANZEI ||
				nCharacter == Globals.CHR_BINGSHI ||
				nCharacter == Globals.CHR_JINWEIBING) {
				return false;
			}
		} else {
			return Globals.g_Global.IsCaptureFreeNPC(nCountry);
		}
		
		return true;
	}
	
//	private boolean isAdjoinCountry(int nNPC) {
//		for (int i = 0; i < Globals.COUNTRY_MAX; i ++) {
//			if (Globals.g_Global.IsCaptureCountry(i) == false)
//				continue;
//			
//			if (i == nNPC)
//				return true;
//			
//			for (int j = Globals.DIRECT_LEFT; j < Globals.DIRECT_COUNT; j ++) {
//				if (Globals.GetAdjoinCountry(i, j) == nNPC) {				
//					return true;
//				}
//			}
//		}
//		return false;
//	}
	
	private void updateInfo(int nCountry) {
		m_nCountry = nCountry;
		Globals.COUNTRY_INFO info = Globals.GetCountryInfo(nCountry);
		m_nCharacter = info.nCharacter;
		updateChrInfo(nCountry);
		setFocusPos();
	}
	
	private void setFocusPos() {
		CPoint point = Globals.getCountryPos(m_nCountry);
		point.x += 10;
		point.y += 10;
		m_viewFocus.SetPos(point);
	}
	
	private void createBackGround() {
		m_imgUpBg.load("COMMON/COMN_bg_03.png");
		m_imgDownBg.load("COMMON/COMN_bg_05.png");
		
		String	str;
		for (int i = 0; i < 4; i ++) {
			str = String.format("D_1/D1_param_%d.png", i+1);
			m_imgParamBg[i] = new CImgObj(str);
		}
		updateChrInfo(m_nCountry);
	}
	
	private void drawBackGround() {
		m_imgUpBg.draw(0, 0);
		m_imgDownBg.draw(0, 240*2);
		m_imgChr.draw(60*2, 0);
		m_imgChrName.draw(0, 20*2);
		
		int nParamCount = 4;
		if (Globals.g_Global.GetGameMode() == Globals.GM_FREE)
			nParamCount = 1;
		
		for (int i = 0; i < nParamCount; i ++) {
			m_imgParamBg[i].draw(6*2, (78+26*i)*2);
		}
		drawChrInfoText();
	}
	
	private void drawChrInfoText() {
		String	str;
		int x, y;
		int nCountry = m_nCountry;

		CBaseView view = getView();
		int	nOldSize = view.setFontSize(16*2);
		
		x = 117;
		y = 78+12;
		str = String.format("%s", Globals.GetCountryName(nCountry));
		drawStr(x*2, y*2, str);
		if (Globals.g_Global.GetGameMode() == Globals.GM_UNIFY) {
			Globals.COUNTRY_INFO info = Globals.GetCountryInfo(nCountry);
			int nSoldiers = 0;
			if (Globals.g_Global.IsCaptureCountry(nCountry))
				nSoldiers = Globals.g_Global.GetUserSoldiers();
			else
				nSoldiers = info.nSoldiers;
			
			str = String.format("%d", nSoldiers);
			y += 26;
			drawStr(x*2, y*2, str);
			
			Globals.CHARATER_INFO chrInfo = Globals.GetCharacterInfo(m_nCharacter);
			str = String.format("%d", chrInfo.nAttack);
			y += 26;
			drawStr(x*2, y*2, str);
			
			str = String.format("%d", chrInfo.nWisdom);
			y += 26;
			drawStr(x*2, y*2, str);
		}
		view.setFontSize(nOldSize);
	}
	
	private void updateChrInfo(int nCountry) {
		Globals.COUNTRY_INFO info = Globals.GetCountryInfo(nCountry);
		int nCharacter = 1;
		nCharacter = info.nCharacter;
		int nMask = 1;
		if (Globals.g_Global.GetGameMode() == Globals.GM_FREE && !isSelectableCountry(nCountry))
			nMask = 2;
		String	str = String.format("D_1/C1_charaImg_%d_%02d.png", nMask, nCharacter+1);
		m_imgChr.load(str);
		str = String.format("D_1/D1_charaName_%02d.png", nCharacter+1);
		m_imgChrName.load(str);
	}
}
