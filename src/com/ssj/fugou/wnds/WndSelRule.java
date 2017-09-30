package com.ssj.fugou.wnds;


import com.dlten.lib.frmWork.CButton;
import com.dlten.lib.graphics.CImgObj;
import com.dlten.lib.graphics.CPoint;
import com.ssj.fugou.Globals;
import com.ssj.fugou.frmWndMgr;
import com.ssj.fugou.dlgs.AfxMsgView;
import com.ssj.fugou.sound.MySoundManager;

public class WndSelRule extends WndCommon {

	private CImgObj m_imgUpBg   = new CImgObj();
	private CImgObj m_imgDownBg = new CImgObj();
	private CImgObj m_imgMsg    = new CImgObj();
	private CImgObj m_imgTitle  = new CImgObj();
	
	private CButton	m_btnRule[] = new CButton[Globals.RULE_COUNT];
	private Globals.RULE_INFO	m_stGameRule = new Globals.RULE_INFO();
	int				m_nCommand;
	
    public static final int
	CMD_SET  = 0,
	CMD_BACK = 1,
	CMD_RULE = 2,
	ID_MSG_RULE = 0x20;
    
	public void OnLoadResource() {
		setString("Select Rule Wnd");

		Globals.playBGM(MySoundManager.bgm_opening);
		
		Globals.RULE_INFO	rule = Globals.g_Global.GetGameRule();
		m_stGameRule.Copy(rule);
		
		createBackGround();
		createButtons();
	}
	public void OnInitWindow() {
	}
	public void OnShowWindow() {
	}
	public void OnDestroy() {
		super.OnDestroy();
		
		m_imgUpBg   = unload(m_imgUpBg);
		m_imgDownBg = unload(m_imgDownBg);
		m_imgMsg    = unload(m_imgMsg);
		m_imgTitle  = unload(m_imgTitle);
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
    	if (nCmd < ID_MSG_RULE) {
        	switch (nCmd) {
        	case CMD_SET:		OnOK();			break;
        	case CMD_BACK:		OnBack();		break;
        	default:			OnRule(nCmd);	break;
        	}
    	} else {
        	m_nCommand = nCmd;
        	procCommand();
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
		int nId = m_viewAfxMsg.showMsgBox("選択したルールで\nゲームを始めますか？", AfxMsgView.MB_YESNO);
		if (nId == AfxMsgView.ID_YES) {
			Globals.g_Global.SetGameRule(m_stGameRule);
			DestroyWindow( frmWndMgr.WND_SELCHR );
		}
	}
	
	public void OnBack() {
		Globals.playSE(MySoundManager.se_cancel);
		DestroyWindow( frmWndMgr.WND_TITLE );
	}
	
	private void OnRule(int cmd) {
		Globals.playSE(MySoundManager.se_set);
		int nId = cmd - CMD_RULE;
		m_stGameRule.setVals(nId, !m_stGameRule.getVals(nId));
		changeBtnImage(nId);
	}
	
	private CPoint getRulePos(int nIndex) {
		int x[] = {	 20, 120, 220,  70, 170,  70, 170	};
		int y[] = {	308, 308, 308, 352, 352, 396, 396	};
		return new CPoint(x[nIndex], y[nIndex]);	
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
		for (int i = 0; i < Globals.RULE_COUNT; i ++) {
			point = getRulePos(i);
			str = String.format("COMMON/COMN_rule_%d_1.png", i+1);
			
			btn = createButton(str, str, str);
			btn.setPoint(point.x*2, point.y*2);
			btn.setCommand( CMD_RULE + i );
			m_btnRule[i] = btn; 
			changeBtnImage(i);
		}
	}
	
	private void createBackGround() {
		m_imgUpBg.load("COMMON/COMN_bg_07.png");
		m_imgDownBg.load("COMMON/COMN_bg_06.png");
		m_imgMsg.load("F_1/F1_mes_1.png");
		m_imgTitle.load("F_1/F1_title_1.png");
	}
	
	private void drawBackGround() {
		m_imgUpBg.draw(0, 0);
		m_imgDownBg.draw(0, 240*2);
		m_imgMsg.draw(40*2, 152*2);
		m_imgTitle.draw(16*2, 240*2);
	}
	
	private void changeBtnImage(int nIndex) {
		int nId = 1;
		if (m_stGameRule.getVals(nIndex))
			nId = 1;
		else
			nId = 2;
		
		String	str;
		str = String.format("COMMON/COMN_rule_%d_%d.png", nIndex+1, nId);
		
		m_btnRule[nIndex].setImage_Normal(str);
		m_btnRule[nIndex].setImage_Focus(str);
		m_btnRule[nIndex].setImage_Disable(str);
	}
	
	private void procCommand() {
		int nId = m_nCommand;
		if (nId == (ID_MSG_RULE+AfxMsgView.ID_YES))
		{
			Globals.g_Global.SetGameRule(m_stGameRule);
			DestroyWindow( frmWndMgr.WND_SELCHR );
		}
	}
}
