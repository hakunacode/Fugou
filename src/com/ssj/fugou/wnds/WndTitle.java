package com.ssj.fugou.wnds;

import com.dlten.lib.frmWork.CButton;
import com.dlten.lib.graphics.CImgObj;
import com.ssj.fugou.Globals;
import com.ssj.fugou.frmWndMgr;
import com.ssj.fugou.frmActivity;
import com.ssj.fugou.game.GameDoc;
import com.ssj.fugou.sound.MySoundManager;

public class WndTitle extends WndCommon {
	
	private CImgObj m_imgUpBg   = new CImgObj();
	private CImgObj m_imgDownBg = new CImgObj();
	private CImgObj m_imgTitle  = new CImgObj();
	
	private CButton	m_btnGameUnify = null;
	private CButton	m_btnGameFree  = null;
	private CButton	m_btnScore     = null;
	private CButton	m_btnManual    = null;
	private CButton	m_btnNew       = null;
	private CButton	m_btnContinue  = null;
	private CButton	m_btnModoru    = null;
	
	private boolean	m_bTitleWnd = true;
	private boolean	m_bExistContinue = false;
	private boolean	m_bExistKifu = false;
	
    public static final int
	CMD_GAME_UNIFY = 0,
	CMD_GAME_FREE  = 1,
	CMD_SCORE      = 2,
	CMD_MANUAL     = 3,
	CMD_NEW        = 4,
	CMD_CONTINUE   = 5,
	CMD_BACK       = 6;
    
	public void OnLoadResource() {
		setString("Title Wnd");

		Globals.playBGM(MySoundManager.bgm_opening);
		
		loadSaveData();
		
		Globals.m_bVictoryAnim = false;
		m_bTitleWnd = true;
		
		createBackGround();
		createButtons();
		updateButtons();
	}
	public void OnInitWindow() {
	}
	public void OnShowWindow() {
	}
	
	public void OnDestroy() {
		m_imgUpBg   = unload(m_imgUpBg);
		m_imgDownBg = unload(m_imgDownBg);
		m_imgTitle  = unload(m_imgTitle);
		
		super.OnDestroy();
	}
	public void OnPaint() {
		// drawing dimmension is just Globals.RES_WIDTH * Globals.RES_HEIGHT
		drawBackGround();
		
		debug_drawStatus();
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
    	case CMD_GAME_UNIFY:	OnGameUnify();	break;
    	case CMD_GAME_FREE:		OnGameFree();	break;
    	case CMD_SCORE:			OnScore();		break;
    	case CMD_MANUAL:		OnManual();		break;
    	case CMD_NEW:			OnNew();		break;
    	case CMD_CONTINUE:		OnContinue();	break;
    	case CMD_BACK:			OnBack();		break;
    	}
    }
	
	private void loadSaveData() {
		GameDoc doc = new GameDoc();	
		m_bExistKifu = doc.Load();
		
		m_bExistContinue = Globals.g_Global.LoadContinueData();	
	}
	
	public void OnExit() {
		DestroyWindow( frmWndMgr.WND_DESTROYAPP );
	}

	public void OnMenu() {
	}

	public void OnGameUnify() {
		m_bTitleWnd = false;
		updateButtons();
		Globals.playSE(MySoundManager.se_set);
	}
	public void OnGameFree() {
		Globals.g_Global.InitGameRule();
		Globals.g_Global.SetGameMode(Globals.GM_FREE);
		Globals.playSE(MySoundManager.se_set);
		DestroyWindow( frmWndMgr.WND_SELRULE );
	}
	public void OnScore() {
		Globals.playSE(MySoundManager.se_set);
		DestroyWindow( frmWndMgr.WND_RESULT );
	}
	public void OnManual() {
		Globals.playSE(MySoundManager.se_set);
		
		// DestroyWindow( frmWndMgr.WND_HELP );
		postActivityMsg( frmActivity.MSG_CHANGE_VIEW, 1, 0 ); // goto Help
	}
	public void OnNew() {
		Globals.g_Global.Init();
		Globals.g_Global.SetGameMode(Globals.GM_UNIFY);
		Globals.playSE(MySoundManager.se_set);
		DestroyWindow( frmWndMgr.WND_SELCHR );
	}
	public void OnContinue() {
		if( m_bExistKifu )
		{
			Globals.g_GameDoc.Load();
			Globals.g_Global.SetContinueGame(true);
			Globals.g_Global.SetGameMode(Globals.GM_UNIFY);
			Globals.playSE(MySoundManager.se_set);
			DestroyWindow( frmWndMgr.WND_GAME );
		}
		else if (m_bExistContinue)
		{
			Globals.g_Global.SetContinueDataToGlobal(null);
			Globals.g_Global.SetGameMode(Globals.GM_UNIFY);
			Globals.playSE(MySoundManager.se_set);
			DestroyWindow( frmWndMgr.WND_SELUNIFYNPC );
		}
	}
	public void OnBack() {
		m_bTitleWnd = true;
		updateButtons();

		Globals.playSE(MySoundManager.se_cancel);
	}
	
	public void createButtons() {
		CButton	btn = null;
		
		btn = createButton(
				"B_1/B1_menuBtn_1_4.png",
				"B_1/B1_menuBtn_1_2.png",
				"B_1/B1_menuBtn_1_4.png");
		btn.setPoint(0, 240*2);
		btn.setCommand( CMD_GAME_UNIFY );
		m_btnGameUnify = btn;

		btn = createButton(
				"B_1/B1_menuBtn_2_4.png",
				"B_1/B1_menuBtn_2_2.png",
				"B_1/B1_menuBtn_2_4.png");
		btn.setPoint(160*2, 240*2);
		btn.setCommand( CMD_GAME_FREE );
		m_btnGameFree = btn;

		btn = createButton(
				"B_1/B1_menuBtn_3_4.png",
				"B_1/B1_menuBtn_3_2.png",
				"B_1/B1_menuBtn_3_4.png");
		btn.setPoint(80*2, 340*2);
		btn.setCommand( CMD_SCORE );
		m_btnScore = btn;

		btn = createButton(
				"B_1/B1_menuBtn_4_4.png",
				"B_1/B1_menuBtn_4_2.png",
				"B_1/B1_menuBtn_4_4.png");
		btn.setPoint(0, 380*2);
		btn.setCommand( CMD_MANUAL );
		m_btnManual = btn;

		btn = createButton(
				"B_1/B2_menuBtn_1_4.png",
				"B_1/B2_menuBtn_1_2.png",
				"B_1/B2_menuBtn_1_4.png");
		btn.setPoint(0, 296*2);
		btn.setCommand( CMD_NEW );
		m_btnNew = btn;

		btn = createButton(
				"B_1/B2_menuBtn_2_4.png",
				"B_1/B2_menuBtn_2_2.png",
				"B_1/B2_menuBtn_2_3.png");
		btn.setPoint(160*2, 296*2);
		btn.setCommand( CMD_CONTINUE );
		m_btnContinue = btn;

		btn = createButton(
				"COMMON/COMN_underBtn_2_4.png",
				"COMMON/COMN_underBtn_2_2.png",
				"COMMON/COMN_underBtn_2_3.png");
		btn.setPoint(0, 456*2);
		btn.setCommand( CMD_BACK );
		m_btnModoru = btn;
	}
	
	
	private void createBackGround() {
		m_imgUpBg.load("COMMON/COMN_bg_07.png");
		m_imgDownBg.load("COMMON/COMN_bg_06.png");
		m_imgTitle.load("B_1/B2_title_1.png");
	}
	
	private void drawBackGround() {
		m_imgUpBg.draw(0, 0);
		m_imgDownBg.draw(0, 240*2);
		if (m_bTitleWnd == false)
			m_imgTitle.draw(16*2, 240*2);
	}
	
	public void updateButtons() {
		m_btnGameUnify.setVisible(m_bTitleWnd);
		m_btnGameFree .setVisible(m_bTitleWnd);
		m_btnScore    .setVisible(m_bTitleWnd);
		m_btnManual   .setVisible(m_bTitleWnd);

		m_btnNew      .setVisible(!m_bTitleWnd);
		m_btnContinue .setVisible(!m_bTitleWnd);
		m_btnModoru   .setVisible(!m_bTitleWnd);
		
		if (m_bTitleWnd == false)
			m_btnContinue.setEnable( m_bExistContinue | m_bExistKifu );
	}
}
