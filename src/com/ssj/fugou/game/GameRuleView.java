package com.ssj.fugou.game;

import com.dlten.lib.STD;
import com.dlten.lib.frmWork.CDialog;
import com.dlten.lib.graphics.CImgObj;
import com.ssj.fugou.Globals;
import com.ssj.fugou.wnds.WndGame;
import com.ssj.fugou.game.GameDoc;

public class GameRuleView extends CDialog {

	private	WndGame	m_controller = null;
	
	private CImgObj	m_imgBg      = null;
	private CImgObj	m_imgRuleBg  = null;
	private CImgObj	m_imgParamRule[][] = new CImgObj[Globals.RULE_COUNT][];
	private CImgObj	m_imgChr     = null;
	private CImgObj	m_imgChrName = null;

    
    public GameRuleView(WndGame pController) {
    	m_controller = pController;
    }
    
	public void OnLoadResource() {
		setString("Game Rule View");
		
		createBackGround();
	}
	
	public void OnDestroy() {
		super.OnDestroy();
		
		unload(m_imgBg);
		unload(m_imgRuleBg);
		
		for (int i = 0; i < Globals.RULE_COUNT; i ++) {
			unload(m_imgParamRule[i][0]);
			unload(m_imgParamRule[i][1]);
			m_imgParamRule[i] = null;
		}
		
		unload(m_imgChr);
		unload(m_imgChrName);
		
	    STD.logHeap();
	}
	public void OnInitWindow() {
	}
	public void OnShowWindow() {
	}
	
	public void OnPaint() {
		// drawing dimmension is just Globals.RES_WIDTH * Globals.RES_HEIGHT
		drawBackGround();
	}
	
	public void OnTouchDown( int x, int y ) {
		OnExit();
	}
	
	public void OnKeyDown( int keycode ) {
		switch (keycode) {
		case KEY_MENU:		OnMenu();						break;
		case KEY_BACK:		OnExit();						break;
		default:			super.OnKeyDown(keycode);		break;
		}
	}
	
	public void OnExit() {
		DestroyWindow( -1 );
	}

	public void OnMenu() {
	}
	
	private void createBackGround() {
		m_imgBg = new CImgObj("COMMON/COMN_blackBg_1.png");
		m_imgBg.setAlpha(128);
		
		m_imgRuleBg = new CImgObj("COMMON/COMN_bg_04.png");
		
		GameLogic	pLogic = m_controller.GetGameLogic();
		//RULE_INFO rule = pLogic->SS_GetGameRule();
		String	str;
		for (int i = 0; i < Globals.RULE_COUNT; i ++) {
			m_imgParamRule[i] = new CImgObj[2];
			
			str = String.format("COMMON/COMN_rule_%d_1.png", i+1);
			m_imgParamRule[i][0] = new CImgObj(str);
			
			str = String.format("COMMON/COMN_rule_%d_2.png", i+1);
			m_imgParamRule[i][1] = new CImgObj(str);
		}
		
		if (Globals.g_Global.GetGameMode() == Globals.GM_UNIFY) {
			//character name
			int nChr = pLogic.SS_GetPlayerChr(GameDoc.PLAYER_CPU1);
			
			str = String.format("D_1/D1_charaImg_1_%02d.png", nChr+1);
			m_imgChr = new CImgObj(str);
			
			str = String.format("D_1/D1_charaName_%02d.png", nChr+1);
			m_imgChrName = new CImgObj(str);
		}
	}
	
	private void drawBackGround() {
		m_imgBg.draw(0, 0);
		m_imgRuleBg.draw(0, 0);
		
	    // Drawing code
		if (Globals.g_Global.GetGameMode() == Globals.GM_UNIFY)	{
			m_imgChr.draw(0, 0);
			m_imgChrName.draw(130*2, 20*2);
		}
		
		int x, y;
		GameLogic	pLogic = m_controller.GetGameLogic();
		Globals.RULE_INFO rule = pLogic.SS_GetGameRule();
		int nIndex = 0, nRuleVal = 0;
		for (int i = 0; i < Globals.RULE_COUNT; i ++) {
			nRuleVal = (rule.getVals(i) == false)? 0 : 1;
			nIndex = 1-nRuleVal;
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
	}
	
	public int show() {
		return DoModal(m_controller);
	}
}
