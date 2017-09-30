package com.ssj.fugou.wnds;

import com.dlten.lib.CBaseView;
import com.dlten.lib.frmWork.CButton;
import com.dlten.lib.graphics.CImgObj;
import com.ssj.fugou.Globals;
import com.ssj.fugou.frmWndMgr;
import com.ssj.fugou.dlgs.AfxMsgView;
import com.ssj.fugou.sound.MySoundManager;

public class WndResult extends WndCommon {
	
	private CImgObj m_imgUpBg   = new CImgObj();
	private CImgObj m_imgDownBg = new CImgObj();
	private CImgObj m_imgTitle  = new CImgObj();
	private CImgObj m_imgItemBg[] = new CImgObj[RESULT_COUNT];
	
	private	AfxMsgView		m_viewAfxMsg = null;
	
    public static final int
	CMD_INIT   = 0,
	CMD_BACK   = 1;
    
    public static final int
    RESULT_COUNT = 11;
    
	public void OnLoadResource() {
		setString("Result Wnd");
		
		Globals.playBGM(MySoundManager.bgm_opening);

		createBackGround();
		createButtons();
	}
	public void OnInitWindow() {
	}
	public void OnShowWindow() {
	}
	public void OnDestroy() {
		super.OnDestroy();
		
		m_viewAfxMsg = null;
		
		int	i;
		m_imgUpBg    = unload(m_imgUpBg);
		m_imgDownBg  = unload(m_imgDownBg);
		m_imgTitle   = unload(m_imgTitle);
		for (i = 0; i < m_imgItemBg.length; i ++) {
			m_imgItemBg[i] = unload(m_imgItemBg[i]);
		}
		m_imgItemBg = null;
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
    	case CMD_INIT:			OnInit();		break;
    	case CMD_BACK:			OnBack();		break;
    	}
    }
	
	public void OnExit() {
		// TODO : back button proc
		DestroyWindow( frmWndMgr.WND_DESTROYAPP );
	}

	public void OnMenu() {
		// TODO : menu button proc
	}

	public void OnBack() {
		Globals.playSE(MySoundManager.se_cancel);
		DestroyWindow( frmWndMgr.WND_TITLE );
	}
	
	public void OnInit() {
		Globals.playSE(MySoundManager.se_set);
		
		m_viewAfxMsg = new AfxMsgView(this);
		int	nId = m_viewAfxMsg.showMsgBox("戦績を初期化します。\nよろしいですか？", AfxMsgView.MB_YESNO);
		if (nId == AfxMsgView.ID_YES){
			Globals.g_Global.InitGameResult();
			Globals.g_Global.SaveGameResult(true);
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
				"COMMON/COMN_underBtn_4_4.png",
				"COMMON/COMN_underBtn_4_2.png",
				"COMMON/COMN_underBtn_4_3.png");
		btn.setPoint((320-96)*2, 456*2);
		btn.setCommand( CMD_INIT );
	}
	
	
	private void createBackGround() {
		m_imgUpBg.load("COMMON/COMN_bg_07.png");
		m_imgDownBg.load("COMMON/COMN_bg_06.png");
		m_imgTitle.load("I_1/I1_title_1.png");
		
		CImgObj		imgObj = null;
		String		strFile = null;
		for (int i = 0; i < RESULT_COUNT; i ++) {
			strFile = String.format("I_1/I1_param_%02d.png", i+1);
			imgObj = new CImgObj();
			imgObj.load(strFile);
			m_imgItemBg[i] = imgObj;
		}
	}
	
	private void drawBackGround() {
		m_imgUpBg.draw(0, 0);
		m_imgDownBg.draw(0, 240*2);
		m_imgTitle.draw(16*2, 240*2);
		
		Globals.RESULT_INFO info = Globals.g_Global.GetGameResult();
		
		CBaseView view = getView();
		int	nOldSize = view.setFontSize(16*2);
		
		int x = 6;
		int	y = 284;
		int	result = 0;
		String	str;
		
		for (int i = 0; i < RESULT_COUNT; i ++) {
			x = 6+(i/6)*156;
			if (i > 5) {
				y = 310+(i%6)*26;
			}			
			else {
				y = 284+(i%6)*26;
			}
			
			result = 0;
			result = info.getVals(i);
			m_imgItemBg[i].draw(x*2, y*2);
			str = String.format("%d回", result);
			
			x = x+144;
			y = y+12;
    		drawStr(x*2, y*2, 0xFFFFFF, ANCHOR_RIGHT | ANCHOR_MIDDLE, str);
		}
		
		view.setFontSize(nOldSize);
	}
}
