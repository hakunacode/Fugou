package com.ssj.fugou.wnds;

import com.dlten.lib.CBaseView;
import com.dlten.lib.STD;
import com.dlten.lib.frmWork.CButton;
import com.dlten.lib.graphics.CImgObj;
import com.dlten.lib.graphics.CPoint;
import com.dlten.lib.graphics.CRect;
import com.ssj.fugou.Globals;
import com.ssj.fugou.frmWndMgr;
import com.ssj.fugou.dlgs.*;
import com.ssj.fugou.sound.MySoundManager;

public class WndSelFNPC extends WndCommon {
	
	private CImgObj m_imgUpBg   = new CImgObj();
	private CImgObj m_imgDownBg = new CImgObj();
	private CImgObj m_imgParamBg[] = null;
	private CImgObj m_imgParamRule[][]  = null; // [7][2];
	private CImgObj m_imgNPCCountry[][] = null; // [4][2];
	private CImgObj m_imgChr     = new CImgObj();
	private CImgObj m_imgChrName = new CImgObj();
	
	private CButton	m_btnCountry[] = new CButton[Globals.COUNTRY_MAX];
	private CButton	m_btnSet       = null;
	
	private	SelMsgView		m_viewMsg = null;
	private	ViewSelFocus	m_viewFocus;
	private	ViewSelFocus	m_viewNPCFocus[] = new ViewSelFocus[Globals.NPC_COUNT];
	
	int				m_nNPCCountry[] = new int [Globals.NPC_COUNT];
	int				m_nNPCIndex;
	int				m_nCharacter;
	int				m_nCountry;
	int				m_nCommand;
	
    public static final int
	CMD_SET     = 0,
	CMD_BACK    = 1,
	CMD_COUNTRY = 2;
    
	public void OnLoadResource() {
		setString("Select Free NPC Wnd");

		Globals.playBGM(MySoundManager.bgm_select_attack);
		
		m_nCountry = Globals.COUNTRY_NONE;
		m_nCharacter = Globals.CHR_NONE;
		
		m_nCountry = Globals.g_Global.GetNPCCountry();
		m_nCharacter = Globals.g_Global.GetNPCChr();
		
		createBackGround();

		for (int i = 0; i < Globals.NPC_COUNT; i ++) {
			m_viewNPCFocus[i] = new ViewSelFocus(this, Globals.FOCUS_TYPE2);
			//m_viewNPCFocus[i].hidden = YES;
		}
		init();
		
		m_viewFocus = new ViewSelFocus(this, Globals.FOCUS_TYPE1);
		setFocusPos();
		
		createButtons();
		updateBtnState();
		
		m_viewMsg = new SelMsgView(this, SelMsgView.SELMSG_NPC_FREE);//kgh
		m_viewMsg.showMsg(SelMsgView.SELMSG_NPC_FREE);
	}
	public void OnInitWindow() {
	}
	public void OnShowWindow() {
	}
	public void OnDestroy() {
		super.OnDestroy();
		
		int	i, j;
		m_viewFocus.destroy();
		m_viewFocus = null;
		for (i = 0; i < m_viewNPCFocus.length; i ++) {
			m_viewNPCFocus[i].destroy();
		}
		m_viewNPCFocus = null;
		m_viewMsg = null;
		
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
		
		for (i = 0; i < m_imgNPCCountry.length; i ++) {
			for (j = 0; j < m_imgNPCCountry[i].length; j ++) {
				m_imgNPCCountry[i][j] = unload(m_imgNPCCountry[i][j]);
			}
			m_imgNPCCountry[i] = null;
		}
		m_imgNPCCountry = null;
		m_imgChr      = unload(m_imgChr);
		m_imgChrName  = unload(m_imgChrName);
	}
	
	public void OnPaint() {
		// drawing dimmension is just Globals.RES_WIDTH * Globals.RES_HEIGHT
		drawBackGround();
		m_viewFocus.draw();
		m_viewNPCFocus[0].draw();
		m_viewNPCFocus[1].draw();
		m_viewNPCFocus[2].draw();
		m_viewNPCFocus[3].draw();
		
		debug_drawStatus();
	}
	
	public void OnTouchDown( int x, int y ) {
		if (Globals.TEST_TOOL == true) {
			CRect	rect = new CRect(0, 0, 320*2, 240*2);
			CPoint	location = new CPoint(x, y);
			if (rect.PtInRect(location) == true) {
				actionAutoGame();
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
    	case CMD_SET:		OnOK();			break;
    	case CMD_BACK:		OnBack();		break;
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
		AfxMsgView	m_viewAfxMsg = new AfxMsgView(this);//kgh
		int	nId = m_viewAfxMsg.showMsgBox("対戦を開始しますか？", AfxMsgView.MB_YESNO);
		if (nId == AfxMsgView.ID_YES) {
			Globals.COUNTRY_INFO info;
			int nChr;
			for (int i = 0; i < 4; i ++)
			{
				info = Globals.GetCountryInfo(m_nNPCCountry[i]);
				nChr = info.nCharacter;
				Globals.g_Global.SetNPCs(i, nChr);
			}
			DestroyWindow( frmWndMgr.WND_GAME );
		} else {
			init();
			updateBtnState();
		}	
	}
	
	public void OnBack() {
		Globals.playSE(MySoundManager.se_error);
		DestroyWindow( frmWndMgr.WND_TITLE );
	}
	
	public void OnCountry(int nCmd) {
		int nId = nCmd - CMD_COUNTRY;
		if (isSelectableCountry(nId) == true) {
			if (setNPCCountry(nId))
				Globals.playSE(MySoundManager.se_set);
			else
				Globals.playSE(MySoundManager.se_error);
		}
		else {
			Globals.playSE(MySoundManager.se_error);
		}

		updateInfo(nId);
		updateBtnState();
	}
	
	private void init() {
		m_nNPCIndex = 0;
		STD.MEMSET(m_nNPCCountry, Globals.COUNTRY_NONE);
		for (int i = 0; i < Globals.NPC_COUNT; i ++) {
			m_viewNPCFocus[i].m_bHidden = true;
		}
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
		m_btnSet = btn;
		
		String	str;
		CPoint	point;
		int nId;
		int nUserCountry = Globals.g_Global.GetUserCountry();
		for (int i = 0; i < Globals.COUNTRY_MAX; i ++)
		{
			point = Globals.getCountryPos(i);
			
			if (nUserCountry == i)
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
		if (getNPCCount() == 4)
			m_btnSet.setVisible(true);
		else
			m_btnSet.setVisible(false);
	}
	
	private void actionAutoGame() {
		if ( Globals.TEST_TOOL == true ) {
			Globals.DEBUG_AUTO_GAME = true;
			
			if (getNPCCount() < 4)
				return;
			Globals.COUNTRY_INFO info;
			int nChr;
			for (int i = 0; i < 4; i ++) {
				info = Globals.GetCountryInfo(m_nNPCCountry[i]);
				nChr = info.nCharacter;
				Globals.g_Global.SetNPCs(i, nChr);
			}
			Globals.m_bAutoGame = true;
			DestroyWindow( frmWndMgr.WND_GAME );
		}
	}
	
	private int getNPCCount() {
		int nCount = 0;
		for (int i = 0; i < Globals.NPC_COUNT; i ++)
		{
			if (m_nNPCCountry[i] != Globals.COUNTRY_NONE)
				nCount ++;
		}
		return nCount;
	}

	private int getNPCCountry(int nIdex) {
		return m_nNPCCountry[nIdex];
	}

	private void updateInfo(int nCountry) {
		m_nCountry = nCountry;
		Globals.COUNTRY_INFO info = Globals.GetCountryInfo(nCountry);
		m_nCharacter = info.nCharacter;
		
		updateChrInfo(nCountry);
		updateBtnState();
		setFocusPos();
	}

	private void setFocusPos() {
		CPoint point = Globals.getCountryPos(m_nCountry);
		//m_viewFocus.frame = CGRectMake(<#CGFloat x#>, <#CGFloat y#>, <#CGFloat width#>, <#CGFloat height#>)
		point.x += 10;
		point.y += 10;
		m_viewFocus.SetPos(point);
	}

	private boolean isSelectableCountry(int nCountry) {
//	#ifdef TEST_TOOL
//	//kgh	if(AfxGetTool()->IsEnbleAllNPC())
////			return TRUE;
//	#endif
		return Globals.g_Global.IsCaptureFreeNPC(nCountry);
	}

	private boolean setNPCCountry(int nNPC) {
		int nCnt = 0, i;
		int nUserCountry = Globals.g_Global.GetUserCountry();
		if (nUserCountry == nNPC)
		{		
			//SetFocusButton(&m_btnCountry[nNPC]);
			//[self updateInfo:nNPC];
			//[self updateBtnState];
			return false;
		}
		for (i = 0; i < 4; i ++)
		{
			if (m_nNPCCountry[i] == nNPC)
			{
				m_nNPCIndex = i;
				//m_nNPCCountry[i] = COUNTRY_NONE;
				setNPCs(Globals.COUNTRY_NONE);
				//SetFocusButton(&m_btnCountry[nNPC]);
				//[self updateInfo:nNPC];
				//[self updateBtnState];
				return true;
			}
			if (m_nNPCCountry[i] != Globals.COUNTRY_NONE)
			{
				nCnt ++;
			}
		}
		if (nCnt >= 4)
			return false;
		if (m_nNPCCountry[m_nNPCIndex] == Globals.COUNTRY_NONE)
		{
			//m_nNPCCountry[m_nNPCIndex] = nNPC;
			setNPCs(nNPC);
			//SetFocusButton(&m_btnCountry[nNPC]);
			//[self updateInfo:nNPC];
			//[self updateBtnState];
		}
		else
		{
			for (i = 0; i < 4; i ++)
			{
				if (m_nNPCCountry[i] == Globals.COUNTRY_NONE)
					break;
			}
			if (i != 4)
			{
				m_nNPCIndex = i;
				//m_nNPCCountry[m_nNPCIndex] = nNPC;
				setNPCs(nNPC);
				//SetFocusButton(&m_btnCountry[nNPC]);
				//[self updateInfo:nNPC];
				//[self updateBtnState];
			}
		}
		return true;
	}

	private void setNPCs(int nNPC) {
		m_nNPCCountry[m_nNPCIndex] = nNPC;
		if (nNPC == Globals.COUNTRY_NONE) {
			m_viewNPCFocus[m_nNPCIndex].m_bHidden = true;
		} else {
			m_viewNPCFocus[m_nNPCIndex].m_bHidden = false;
			CPoint point = Globals.getCountryPos(nNPC);
			point.x += 10;
			point.y += 10;
			m_viewNPCFocus[m_nNPCIndex].SetPos(point);
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
		
		m_imgNPCCountry = new CImgObj[4][];
		for (int i = 0; i < Globals.NPC_COUNT; i ++) {
			m_imgNPCCountry[i] = new CImgObj[2];
			str = String.format("D_1/D1_selectMark_%d_1.png", i+1);
			m_imgNPCCountry[i][0] = new CImgObj(str);
			str = String.format("D_1/D1_selectMark_%d_2.png", i+1);
			m_imgNPCCountry[i][1] = new CImgObj(str);
		}
		updateChrInfo(m_nCountry);
	}
	
	private void drawBackGround() {
		int nCountry = m_nCountry;
		m_imgUpBg.draw(0, 0);
		m_imgDownBg.draw(0, 240*2);
		m_imgChr.draw(0, 0);
		if (isSelectableCountry(nCountry)) {
			m_imgChrName.draw(130*2, 20*2);
		}	
		
//		for (int i = 0; i < 4; i ++) {
//			[m_imgParamBg[i] drawAtPoint:CGPointMake(166, 78+26*i)];
//		}
		
		m_imgParamBg[0].draw(166*2, 78*2);
		
		int nIndex;
		Globals.RULE_INFO rule = Globals.g_Global.GetGameRule();// = GetCountryRuleInfo(nCountry);
		int x, y;
		for (int i = 0; i < Globals.RULE_COUNT; i ++) {
			if (rule.getVals(i))
				nIndex = 0;
			else
				nIndex = 1;

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
		int nBaseX[] = { 274, 296 };
		int nBaseY[] = { 262, 240 };
		
		for (int i = 0; i < 4; i ++)
		{
			if (getNPCCountry(i) != Globals.COUNTRY_NONE)
				nIndex = 0;
			else 
				nIndex = 1;
			x = nBaseX[i/2];
			if (i == 0 || i == 3)
				y = nBaseY[0];
			else
				y = nBaseY[1];
			m_imgNPCCountry[i][nIndex].draw(x*2, y*2);
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
		
		view.setFontSize(nOldSize);
	}

	private void updateChrInfo(int nCountry) {
		//COUNTRY_INFO info = GetCountryInfo(nCountry);
		int nCharacter = m_nCharacter;		
		
		int nMask = 1;
		if (isSelectableCountry(nCountry) == false)
			nMask = 2;
		String	str = String.format("D_1/D1_charaImg_%d_%02d.png", nMask, nCharacter+1);
		m_imgChr = new CImgObj(str);
		//m_imgChr = [UIImage imageWithCGImage:img.CGImage scale:1.0 orientation:UIImageOrientationLeftMirrored];
		str = String.format("D_1/D1_charaName_%02d.png", nCharacter+1);
		m_imgChrName = new CImgObj(str);
	}
}
