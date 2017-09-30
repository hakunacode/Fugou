package com.ssj.fugou.wnds;

import com.dlten.lib.CBaseView;
import com.dlten.lib.STD;
import com.dlten.lib.file.CConFile;
import com.dlten.lib.frmWork.CButton;
import com.dlten.lib.graphics.CDCView;
import com.dlten.lib.graphics.CImgObj;
import com.dlten.lib.graphics.CPoint;
import com.dlten.lib.graphics.CRect;
import com.ssj.fugou.Globals;
import com.ssj.fugou.frmWndMgr;
import com.ssj.fugou.dlgs.*;
import com.ssj.fugou.sound.MySoundManager;

public class WndSelUNPC extends WndCommon {
	
	private CImgObj m_imgUpBg   = new CImgObj();
	private CImgObj m_imgDownBg = new CImgObj();
	private CImgObj m_imgParamBg[] = null;
	private CImgObj m_imgParamRule[][] = null;
	private CImgObj m_imgChr      = new CImgObj();
	private CImgObj m_imgChrName  = new CImgObj();
	private CImgObj m_imgBlueRect = new CImgObj();
	private CImgObj m_imgRedRect  = new CImgObj();
	
	private CButton	m_btnCountry[] = new CButton[Globals.COUNTRY_MAX];
	private CButton	m_btnMenu  = null;
	private CButton	m_btnSet   = null;
	
	private ViewSelFocus	m_viewFocus;
	private	SelMsgView		m_viewMsg = null;
	private	AfxMenuView		m_viewMenu = null;
	private	AfxMsgView		m_viewAfxMsg = null;
	private	ViewSelVic		m_viewVic = null;
	
	private int				m_nCharacter;
	private int				m_nCountry;
	private int				m_nWndType;
	
    public static final int
	CMD_SET     = 0,
	CMD_BACK    = 1,
	CMD_COUNTRY = 2;
    
    public static final int
	UNIFY_WIN = 0,
	UNIFY_NPC = 1;
    
	public void OnLoadResource() {
		setString("Select Unify NPC Wnd");

		if (Globals.m_bVictoryAnim)
			m_nWndType = UNIFY_WIN;
		else
			m_nWndType = UNIFY_NPC;
		
		if (m_nWndType == UNIFY_NPC)
			Globals.playBGM(MySoundManager.bgm_select_attack);
		else
			Globals.playSEEx(MySoundManager.jingle_area_get);

		m_nCountry = Globals.g_Global.GetNPCCountry();
		if (Globals.g_Global.IsCaptureCountry(m_nCountry)) {
			m_nCharacter = Globals.g_Global.GetUserChr();
		} else {
			Globals.COUNTRY_INFO info = Globals.GetCountryInfo(m_nCountry);
			m_nCharacter = info.nCharacter;		
		}
		
		createBackGround();
		createButtons();

		m_viewFocus = new ViewSelFocus(this, Globals.FOCUS_TYPE1);
		m_viewFocus.m_bHidden = true;
		setFocusPos();
		
		m_viewVic = new ViewSelVic(this);
		m_viewMsg = new SelMsgView(this, SelMsgView.SELMSG_NPC_UNIFY);
		m_viewMenu = new AfxMenuView(this, AfxMenuView.MENUTYPE_SELECT);
		m_viewAfxMsg = new AfxMsgView(this);
		
		if (m_nWndType == UNIFY_NPC) {
			m_viewFocus.m_bHidden = false;
			updateBtnState();
			setFocusPos();
			m_viewMsg.showMsg(SelMsgView.SELMSG_NPC_UNIFY);
			if (Globals.TEST_TOOL == true) {
				if (Globals.DEBUG_AUTO_SCREEN == true) {
					debug_SelectCountry();
				}
			}
		} else {
			showButton(m_nWndType);
			showCountryButton(m_nWndType);

			setVicPos();
			m_viewVic.showView();
			STD.sleep(1500);	// added by hrh 2011-0705	for jingle_area_get
			onVicAnimEvent();
			
			if (Globals.TEST_TOOL == true) {
				if (Globals.DEBUG_AUTO_SCREEN == true) {
					PostMessage(WM_COMMAND, CMD_BACK);
				}
			}
		}
	}
	public void debug_SelectCountry() {
		int	 nId;
		for (int i = 0; i < Globals.COUNTRY_MAX; i ++)
		{
			if (Globals.g_Global.IsCaptureCountry(i))
				nId = 3;
			else if (isSelectableCountry(i))
				nId = 1;
			else
				nId = 2;
			
			if (nId == 1) {
				PostMessage(WM_COMMAND, CMD_COUNTRY + i);
				break;
			}
		}
	}
	public void OnInitWindow() {
	}
	public void OnShowWindow() {
	}
	public void OnDestroy() {
		super.OnDestroy();
		
		if (Globals.DEBUG_AUTO_SCREEN == true) {
			Globals.DEBUG_AUTO_GAME = true;
			Globals.m_bAutoGame = true;
		} else {
			Globals.DEBUG_AUTO_GAME = false;
			Globals.m_bAutoGame = false;
		}
		
		m_viewFocus.destroy();
		m_viewFocus = null;
		
		m_viewMsg = null;
		m_viewMenu = null;
		m_viewAfxMsg = null;
		m_viewVic = null;
		
		int	i, j;
		m_imgUpBg = unload(m_imgUpBg);
		m_imgDownBg = unload(m_imgDownBg);
		for (i = 0; i < m_imgParamBg.length; i ++) {
			m_imgParamBg[i] = unload(m_imgParamBg[i]);
		}
		m_imgParamBg = null;
		
		for (i = 0; i < m_imgParamRule.length; i ++) {
			for (j = 0; j < m_imgParamRule[i].length; j ++) {
				m_imgParamRule[i][j] = unload(m_imgParamRule[i][j]);
			}
			m_imgParamRule[i] = null;
		}
		m_imgParamRule = null;
		m_imgChr      = unload(m_imgChr);
		m_imgChrName  = unload(m_imgChrName);
		m_imgBlueRect = unload(m_imgBlueRect);
		m_imgRedRect  = unload(m_imgRedRect);
	}
	
	public void OnPaint() {
		// drawing dimmension is just Globals.RES_WIDTH * Globals.RES_HEIGHT
		drawBackGround();
		m_viewFocus.draw();
		m_viewVic.draw();
		
		debug_drawStatus();
	}
	
	public void OnTouchDown( int x, int y ) {
		if (Globals.TEST_TOOL == true) {
			CRect	rect  = new CRect(0, 0, 320*2, 240*2);
			
			CPoint	location = new CPoint(x, y);
			if (rect.PtInRect(location) == true) {
				boolean bCapture;
				int nCountry = m_nCountry+1;
				if (nCountry >= Globals.COUNTRY_MAX)
					nCountry -= 2;
				
				for (int i = 0; i < Globals.COUNTRY_MAX; i ++) {
					if (i == nCountry)
						bCapture = false;
					else
						bCapture = true;
					Globals.g_Global.SetCaptureCountry(i, bCapture);
				}
				changeCountryImage();
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
		Globals.playSE(MySoundManager.se_set);
		
		int	nId = m_viewAfxMsg.showMsgBox("この国に攻め込みますか？", AfxMsgView.MB_YESNO);
		if (nId == AfxMsgView.ID_YES){
			Globals.g_Global.SetNPCCountry(m_nCountry);
			Globals.RULE_INFO ruleInfo = Globals.GetCountryRuleInfo(m_nCountry);//
			Globals.g_Global.SetGameRule(ruleInfo);
			Globals.g_Global.SaveSetInfo(true);
			DestroyWindow( frmWndMgr.WND_GAME );
		}	
	}
	
	public void OnBack() {
		Globals.playSE(MySoundManager.se_set);

		int nMsgId = m_viewMenu.showMenu();
		
		boolean bBGM = Globals.g_Global.GetBGM();
		if (bBGM)
			Globals.playBGM(MySoundManager.bgm_select_attack);
		else
			Globals.stopBGM();
		
		switch (nMsgId) {
		case AfxMenuView.MENU_SAVE:		OnMenuSave();	break;
		case AfxMenuView.MENU_EXIT:		OnMenuExit();	break;
		}
		if (Globals.TEST_TOOL == true) {
			if (Globals.DEBUG_AUTO_SCREEN == true) {
				if (nMsgId == AfxMenuView.MENU_CLOSE) {
					debug_SelectCountry();
				}
			}
		}
	}
	
	public void OnCountry(int nCmd) {
		int nId = nCmd - CMD_COUNTRY;
		if (nId == m_nCountry || isAdjoinCountry(nId) == false)
			return;
		Globals.playSE(MySoundManager.se_cursor);
		updateInfo(nId);
		
		if (Globals.TEST_TOOL == true) {
			if (Globals.DEBUG_AUTO_SCREEN == true) {
				PostMessage(WM_COMMAND, CMD_SET);
			}
		}
	}
	
	public void OnMenuSave() {
		int nMsgId = m_viewAfxMsg.showMsgBox("現在の状態を保存しますか？", AfxMsgView.MB_YESNO);
		if (nMsgId == AfxMsgView.ID_YES) {
			boolean bRet = Globals.g_Global.SaveContinueData(true);
			Globals.g_Global.SaveSetInfo(true);
			if (bRet) {
				CConFile.delete( Globals.KIFU_FILE );
			}
		}
	}
	
	public void OnMenuExit() {
		int nMsgId = m_viewAfxMsg.showMsgBox("ゲームを終了します。\nよろしいですか？", AfxMsgView.MB_YESNO);
		if (nMsgId == AfxMsgView.ID_YES) {
			Globals.g_Global.Init();
			Globals.g_Global.SaveSetInfo(true);
			DestroyWindow( frmWndMgr.WND_TITLE );
		}	
	}
	
	public void createButtons() {
		CButton	btn = null;
		
		btn = createButton(
				"COMMON/COMN_underBtn_1_4.png",
				"COMMON/COMN_underBtn_1_2.png",
				"COMMON/COMN_underBtn_1_3.png");
		btn.setPoint(0, 456*2);
		btn.setCommand( CMD_BACK );
		m_btnMenu = btn;

		btn = createButton(
				"COMMON/COMN_underBtn_3_4.png",
				"COMMON/COMN_underBtn_3_2.png",
				"COMMON/COMN_underBtn_3_3.png");
		btn.setPoint((320-96)*2, 456*2);
		btn.setCommand( CMD_SET );
		m_btnSet = btn; 
		
		String	str;
		CPoint	point;
		int nId;
		for (int i = 0; i < Globals.COUNTRY_MAX; i ++)
		{
			point = Globals.getCountryPos(i);
			
			if (Globals.g_Global.IsCaptureCountry(i))
				nId = 3;
			else if (isSelectableCountry(i))
				nId = 1;
			else
				nId = 2;
			
			str = String.format("D_1/D1_mark_%d.png", nId);
			
			btn = createButton(str, str, str);
			btn.setPoint(point.x*2, point.y*2);
			btn.setCommand( CMD_COUNTRY + i );
			m_btnCountry[i] = btn;
		}
	}
	
	private void updateBtnState() {
		//	m_btnContinue.hidden = m_bTitleWnd;
		m_btnSet.setVisible( !Globals.g_Global.IsCaptureCountry(m_nCountry) );
	}

	private void showButton(int nType) {
		boolean bHide = (nType == UNIFY_WIN) ? true : false;
		
		m_viewFocus.m_bHidden = bHide;
		
		m_btnMenu.setEnable( !bHide );
		m_btnSet.setEnable( !bHide );
		
		m_btnMenu.setVisible( !bHide );
		m_btnSet.setVisible( !bHide );
		if (bHide == false)
		{
//			[NSThread sleepForTimeInterval:1.4];
			Globals.playBGM(MySoundManager.bgm_select_attack);
		}
	}

	private void showCountryButton(int nType) {
		boolean bHide = (nType == UNIFY_WIN) ? true : false;
		for (int i = 0; i < Globals.COUNTRY_MAX; i ++)
			m_btnCountry[i].setVisible( !bHide );
	}
	
	private void updateInfo(int nCountry) {
		m_nCountry = nCountry;
		if (Globals.g_Global.GetGameMode() == Globals.GM_UNIFY &&
			Globals.g_Global.IsCaptureCountry(m_nCountry)) {
			m_nCharacter = Globals.g_Global.GetUserChr();
		} else {
			Globals.COUNTRY_INFO info = Globals.GetCountryInfo(nCountry);
			m_nCharacter = info.nCharacter;		
		}
		updateChrInfo(nCountry);
		updateBtnState();
		setFocusPos();
	}

	private void setFocusPos() {
		CPoint point = Globals.getCountryPos(m_nCountry);
		point.x += 10;
		point.y += 10;
		m_viewFocus.SetPos(point);
	}

	private void setVicPos() {
		CPoint point = Globals.getCountryPos(m_nCountry);
		point.x += 10;
		point.y += 10;
		m_viewVic.SetPos(point);
	}
	
	private Boolean isSelectableCountry(int nCountry) {
		int nNextCountry;
		boolean bRet = false;
		for (int i = Globals.DIRECT_LEFT; i < Globals.DIRECT_COUNT; i ++)
		{
			nNextCountry = Globals.GetAdjoinCountry(nCountry, i);
			if (nNextCountry != Globals.COUNTRY_NONE && Globals.g_Global.IsCaptureCountry(nNextCountry))
			{				
				bRet = true;
				break;
			}
		}
		
		return bRet;
	}

	private boolean isAdjoinCountry(int nNPC) {
		for (int i = 0; i < Globals.COUNTRY_MAX; i ++) {
			if (Globals.g_Global.IsCaptureCountry(i) == false)
				continue;
			if (i == nNPC)
				return true;
			for (int j = Globals.DIRECT_LEFT; j < Globals.DIRECT_COUNT; j ++) {
				if (Globals.GetAdjoinCountry(i, j) == nNPC) {				
					return true;
				}
			}
		}
		return false;
	}

	private boolean isAdjoinCountry(int nCountry, int nBaseCountry) {
		if (Globals.g_Global.IsCaptureCountry(nCountry))
			return false;
		if (nCountry == nBaseCountry)
			return false;
		for (int j = Globals.DIRECT_LEFT; j < Globals.DIRECT_COUNT; j ++) {
			if (Globals.GetAdjoinCountry(nCountry, j) == nBaseCountry) {
				return true;
			}
		}
		return false;
	}

	private void onVicAnimEvent() {
		m_nWndType = UNIFY_NPC;
		showCountryButton(m_nWndType);
		procVicAnimAfter();
	}

	private void procVicAnimAfter() {
		int nCount = 0;
		for (int i = 0; i < Globals.COUNTRY_MAX; i ++) {
			if (Globals.g_Global.IsCaptureCountry(i))
				nCount ++;
		}
		
		if (nCount >= Globals.COUNTRY_MAX) {
			STD.sleep(1400);
			DestroyWindow( frmWndMgr.WND_GAMEEND );
		} else {
			//m_nWndType = UNIFY_NPC;
			showButton(m_nWndType);
			setFocusPos();
			updateBtnState();
		}
	}
	
	private void changeCountryImage() {
		if ( Globals.TEST_TOOL == true ) {
			String	str;
			int nId;
			for (int i = 0; i < Globals.COUNTRY_MAX; i ++)
			{
				if (Globals.g_Global.IsCaptureCountry(i))
					nId = 3;
				else if (isSelectableCountry(i) == true)
					nId = 1;
				else
					nId = 2;
				
				str = String.format("D_1/D1_mark_%d.png", nId);

				m_btnCountry[i].setImage_Normal(str);
				m_btnCountry[i].setImage_Focus(str);
				m_btnCountry[i].setImage_Disable(str);
			}
		}
	}
	
	private void createBackGround() {
		m_imgUpBg.load("COMMON/COMN_bg_04.png");
		m_imgDownBg.load("COMMON/COMN_bg_05.png");
		
		String	str;
		m_imgParamBg = new CImgObj[4];
		for (int i = 0; i < 4; i ++) {
			str = String.format("D_1/D1_param_%d.png", i+1);
			m_imgParamBg[i] = new CImgObj(str);
		}
		
		m_imgParamRule = new CImgObj[7][];
		for (int i = 0; i < 7; i ++) {
			m_imgParamRule[i] = new CImgObj[2];
			str = String.format("COMMON/COMN_rule_%d_1.png", i+1);
			m_imgParamRule[i][0] = new CImgObj(str);
			
			str = String.format("COMMON/COMN_rule_%d_2.png", i+1);
			m_imgParamRule[i][1] = new CImgObj(str);
		}
		m_imgBlueRect.load("D_1/D1_mark_3.png");
		m_imgRedRect.load("D_1/D1_mark_1.png");
		updateChrInfo(m_nCountry);
	}
	
	private void drawBackGround() {
		m_imgUpBg.draw(0, 0);
		m_imgDownBg.draw(0, 240*2);
		m_imgChr.draw(0, 0);
		m_imgChrName.draw(130*2, 20*2);
		for (int i = 0; i < 4; i ++) {
			m_imgParamBg[i].draw(166*2, (78+26*i)*2);
		}
		
		int nIndex;
		int nCountry = m_nCountry;
		Globals.RULE_INFO rule = Globals.GetCountryRuleInfo(nCountry);
		//memcpy(rule, &GetCountryRuleInfo(nCountry), sizeof(rule));
		int x, y;
		for (int i = 0; i < Globals.RULE_COUNT; i ++) {
			if (Globals.g_Global.IsCaptureCountry(nCountry) == false) {
				if (rule.getVals(i) == true)
					nIndex = 0;
				else
					nIndex = 1;
			} else {
				nIndex = 1;
			}
			
			if (i < 4) {
				x = 80*i;
				y = 196;
			}
			else {
				x = 40+(i-4)*80;
				y = 218;
			}
			m_imgParamRule[i][nIndex].draw(x*2, y*2);		
		}
		drawChrInfoText();
		CPoint	pos;
		if (m_nWndType == UNIFY_WIN) {
			int nNPC = Globals.g_Global.GetNPCCountry();
			for (int i = 0; i < Globals.COUNTRY_MAX; i ++) {
				pos = Globals.getCountryPos(i);
				if (Globals.g_Global.IsCaptureCountry(i))
					m_imgBlueRect.draw(pos.x*2, pos.y*2);
				else if (isSelectableCountry(i) && (isAdjoinCountry(i, nNPC) == false))
					m_imgRedRect.draw(pos.x*2, pos.y*2);
			}
		}
	}
	
	private void drawChrInfoText() {
		String	str;
		int x, y;
		int nCountry = m_nCountry;

		CBaseView view = getView();
		int	nOldSize = view.setFontSize(16*2);
		
		x = 166+111;
		y = 78+12;
		str = Globals.GetCountryName(nCountry);
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
		int nCharacter = m_nCharacter;		

		String	str = String.format("D_1/D1_charaImg_1_%02d.png", nCharacter+1);
		m_imgChr.load(str);
		str = String.format("D_1/D1_charaName_%02d.png", nCharacter+1);
		m_imgChrName.load(str);
	}
}
