package com.ssj.fugou.wnds;

// by hrh 2011;

import com.dlten.lib.Common;
import com.dlten.lib.STD;
import com.dlten.lib.file.CConFile;
import com.dlten.lib.frmWork.CAnimation;
import com.dlten.lib.frmWork.CButton;
import com.dlten.lib.frmWork.CTimerListner;
import com.dlten.lib.frmWork.CWnd;
import com.dlten.lib.graphics.CAnim83;
import com.dlten.lib.graphics.CDCView;
import com.dlten.lib.graphics.CImgObj;
import com.dlten.lib.graphics.CPoint;
import com.dlten.lib.graphics.CRect;
import com.dlten.lib.graphics.CSize;
import com.ssj.fugou.Globals;
import com.ssj.fugou.frmWndMgr;
import com.ssj.fugou.dlgs.AfxMenuView;
import com.ssj.fugou.dlgs.AfxMsgView;
import com.ssj.fugou.dlgs.SelMsgView;
import com.ssj.fugou.game.GameDoc;
import com.ssj.fugou.game.GameLogic;
import com.ssj.fugou.game.GameRuleView;
import com.ssj.fugou.game.GameLogic.HANDCARDS_INFO;
import com.ssj.fugou.sound.MySoundManager;

public class WndGame extends WndCommon implements CTimerListner {

	/*
	 * GameViewController
	 */
	private SelMsgView		m_viewMsg = null;
	private AfxMenuView		m_viewMenuSave = null;
	private AfxMenuView		m_viewMenuNoSave = null;
	private AfxMsgView		m_viewAfxMsg = null;
	private GameRuleView	m_viewRule = null;
//	private LoadingView*	m_viewLoading = null;
	
//	private	int		m_timer = ID_TIMER_2;
//	private	long	m_nBaseTime;
//	private int		m_nMessageBoxID;
//	
//	NSTimer*		_timer;
//	private boolean	m_bValidTimer = false;
	
	private boolean	m_bInitShow;

	private CButton	m_btnPass = null;
	private CButton	m_btnMenu = null;
	private CButton	m_btnSet  = null;
	
	private boolean	m_bShowNotifyMsg;
//	private int		m_nCommand;
	
//	/* window size in points */
//	CGSize	winSizeInPoints_;
//	
//	/* window size in pixels */
//	CGSize	winSizeInPixels_;
	
//	private long	m_nPassTime;
//	private long	m_nSetTime;
	private long	m_nPressTime;
	
    public static final int
	TAG_PASS = 0xf0,
	TAG_MENU = 0xf1,
	TAG_SET  = 0xf2;
	
    public static final int
    	BTN_PASS  = 0x80,
    	BTN_SET   = 0x81,
    	BTN_MENU  = 0x82,
    	BTN_CLOSE = 0x83;

    public static final int
    	MSG_NEXTGAME = 0,
    	MSG_EXCHANG  = 1;
	
	public WndGame() {
		m_pGameCtrl = GameLogic.getInstance();
		m_pGameCtrl.SS_Initialize();
		m_pGameCtrl.SS_SetGameWnd(this);
		m_bContinueGame = Globals.g_Global.IsContinueGame();
		m_bSuspendGame = false;
		m_bShowHandCards = m_bContinueGame;
		m_nMsgType = 0;
		m_nMsgFrame = 0;
		m_nGameCommand = -1;
		m_bAnimMultiRule = false;
		m_bUserHansoku = false;
		m_bLoadBGM = false;
		m_nBgmId = -1;
		
		for (int i = 0; i < mDiscardCards.length; i ++) {
			mDiscardCards[i] = new GameLogic.DISCARD_CARDS_INFO();
		}
	}
	
	public void OnInitWindow() {
		InitGame(false);
		m_pGameCtrl.SS_Action(GameLogic.ACT_CREATE, 0, 0);
		
		m_bSuspendGame = Globals.m_bSuspend;
		if (m_bSuspendGame) {
			//CGameDoc doc;
			//Globals.g_docSuspend.LoadSuspend();
			Globals.g_docSuspend.SetGameKifuToEngine(m_pGameCtrl, true);
			m_pGameCtrl.SS_Action(GameLogic.ACT_SETLEVEL, 0, 0);		
			m_pGameCtrl.SS_Action(GameLogic.ACT_RESUME_GAME, 0, 0);
			Globals.m_bSuspend = false;
			m_bShowHandCards = true;
			for (int i = 0; i < GameDoc.PLAYER_COUNT; i ++) {
				m_bHansoku[i] = Globals.g_docSuspend.GetHansoku(i);
				m_bMiyakoOti[i] = Globals.g_docSuspend.GetMiyakoOti(i);
			}
		} else {
			if (Globals.g_Global.IsContinueGame()) {
				m_bContinueGame = true;
				//kgh g_GameDoc >> m_pGameCtrl;
				Globals.g_GameDoc.SetGameKifuToEngine(m_pGameCtrl, false);
				Globals.g_Global.SetContinueGame(false);
				m_pGameCtrl.SS_Action(GameLogic.ACT_SETLEVEL, 0, 0);
				m_pGameCtrl.SS_Action(GameLogic.ACT_RESTART_GAME, 0, 0);
			} else {
				m_pGameCtrl.SS_Action(GameLogic.ACT_SETLEVEL, 0, 0);
				m_pGameCtrl.SS_Action(GameLogic.ACT_START_GAME, 0, 0);
			}
		}

		//Globals.g_docSuspend.GetGameKifuFromEngine(m_pGameCtrl, TRUE);
		//Globals.g_docSuspend.SetEnableSuspend(FALSE);
		
//		m_pAnimFocus = m_cellDFocus.GetAnim(0);
//		m_pAnimFocus->SetPriority(2);
		
		InitSoldiersInfo();
	}
	
    public void OnLoadResource() {
		setString("Game Wnd");
		
//		Globals.playBGM(MySoundManager.bgm_opening);
		Globals.stopBGM();
		
		createButtons();
		UpdateButtonState();
		
		LoadResource(true);
		
		m_viewMsg = new SelMsgView(this, SelMsgView.SELMSG_NPC_UNIFY);//kgh
		m_viewMenuSave = new AfxMenuView(this, AfxMenuView.MENUTYPE_GAME_SAVE);//kgh
		m_viewMenuNoSave = new AfxMenuView(this, AfxMenuView.MENUTYPE_GAME_NOSAVE);//kgh
		m_viewAfxMsg = new AfxMsgView(this);//kgh

		m_bInitShow = false;

		m_viewRule = new GameRuleView(this);

//		m_viewLoading = [[LoadingView alloc] initWithFrame:CGRectMake(0, 0, 320, 480)];
//		[self.view addSubview:m_viewLoading];
		
		m_bShowNotifyMsg = false;
//		m_nPassTime = 0;
//		m_nSetTime = 0;
		m_nPressTime = 0;
	}
    
	public void OnShowWindow() {
		/*
		SetPlayerKind();
		SetGamePlayingCount();
		SetNPCHandCards();
		SetUserHandCards();*/
		if (m_bContinueGame || m_bSuspendGame)
			PlayGameBGM(MySoundManager.bgm_game);
		if (m_bSuspendGame) {
			UpdateScreenInfo();
			//if (m_pGameCtrl.SS_IsEnableUserAction() == FALSE)
			int nFace;
			HANDCARDS_INFO cardInfo = mHandCards;
			int nCount = 0;
			for (int i = GameDoc.PLAYER_USER; i < GameDoc.PLAYER_COUNT; i ++) {
				nFace = FACE_NORMAL;
				nCount = m_pGameCtrl.SS_GetHandCards(i, cardInfo);
				if (nCount == 0)
					nFace = FACE_GOOD;
				if (m_bHansoku[i] || m_bMiyakoOti[i])
					nFace = FACE_BAD;
				ChangePlayerFace(i, nFace);
			}	
			PostMessage(WM_ACT_TO_NEXT);
			UpdateButtonState();
			//OnChangeState(m_pGameCtrl.SS_GetState());
		}
		else {
			PostMessage(WM_ACT_TO_NEXT);
		}	
		m_bInitShow = true;
	}
	public void OnDestroy() {
		/*
		if (m_bLoadBGM) {
			SoundEngine_UnloadBackgroundMusicTrack();
		}*/
		m_pGameCtrl.SS_DeleteEngine();
		Globals.g_docSuspend.Init();
		//delete m_pGameCtrl;
		
		m_viewMsg = null;
		m_viewMenuSave = null;
		m_viewMenuNoSave = null;
		m_viewAfxMsg = null;
		m_viewRule = null;
		
		LoadResource(false);
		
		super.OnDestroy();
	}
	
	@Override
	public void TimerProc(CWnd pWnd, int nTimerID) {
		OnTimer(nTimerID);
	}
	
	public void OnPaint() {
		// drawing dimmension is just Globals.RES_WIDTH * Globals.RES_HEIGHT
		OnPaint_Top();
		OnPaint_Bottom();
		
		debug_drawStatus();
	}
	
	public void OnTouchDown( int x, int y ) {
		CPoint pos = new CPoint(x, y);
		if (m_bShowRuleInfo) {
			SetShowRuleInfo(false);
			return;
		}
		
		if (m_bShowMsg)
			return;
		
		if (m_pGameCtrl.SS_IsEnableUserAction() == false)
			return;
		
		if (m_pGameCtrl.SS_GetTurn() != GameDoc.PLAYER_USER)
			return;

		if ( Globals.TEST_TOOL == true ) {
			CRect rect = img_COMN_bg_01_top.getRect();
			if (rect.PtInRect(pos))	{
				if (Globals.g_Global.GetGameMode() == Globals.GM_UNIFY)
					m_pGameCtrl.SS_Action(GameLogic.ACT_GAMEEND, 0, 0);
				return;
			}
		}

		CRect rtHandCard = new CRect();
		int width, height;
		//int nCards[MAX_HANDCARDS] = {0};
		HANDCARDS_INFO nHandCards = mHandCards;
		int nCount = m_pGameCtrl.SS_GetHandCards(GameDoc.PLAYER_USER, nHandCards);
		if (nCount <= 0) {
			//ASSERT(FALSE);
			return;
		}
		width = CARD_WIDTH;
		height = CARD_HEIGHT;
		int nIndex = -1;
		for (int i = 0; i < nCount; i ++) {
			x = HandCard_PosX[i];
			y = (i < 5 ? HandCard_PosY[0] : HandCard_PosY[1]);
			rtHandCard.left   = x*2;
			rtHandCard.top    = y*2;
			rtHandCard.width  = width*2;
			rtHandCard.height = height*2;
			if ( rtHandCard.PtInRect(pos) == true)
			{
				nIndex = i;
				break;
			}
		}
		if (nIndex == -1)
			return;
		
		m_pGameCtrl.SS_Action(GameLogic.ACT_COMMAND, GameLogic.CMD_TOUCH, nIndex);
		UpdateButtonState();
	}
	
	public void OnKeyDown( int keycode ) {
		switch (keycode) {
		case KEY_BACK:		OnBack();						break;
		default:			super.OnKeyDown(keycode);		break;
		}
	}
	
    public void OnCommand(int nCmd) {
		long nTick = STD.GetTickCount() - m_nPressTime;

		String	strLog = String.format("Button Press Tick : %d", nTick);
		STD.logout(strLog);
		if (Math.abs(nTick) < 300)
			return;

		m_nPressTime = STD.GetTickCount();
		
		if (m_nGameCommand != -1)
			return;
		
		switch (nCmd) {
			case TAG_PASS:		actionPass();		break;
			case TAG_SET:		actionSet();		break;
			case TAG_MENU:		actionMenu();		break;
			default:								break;
		}	
    }
	
	public void OnBack() {
		// TODO : back button proc
		if ( (Globals.g_Global.GetGameMode() == Globals.GM_UNIFY || Globals.m_bSuspend == true) &&
			Globals.g_docSuspend.IsEnableSuspend() == true ) {
			Globals.saveSuspendGame();
		}
		DestroyWindow( frmWndMgr.WND_DESTROYAPP );
	}
	
	public void createButtons() {
		CButton	btn = null;
		
		btn = createButton(
				"E_1/E1_underBtn_1_4.png",
				"E_1/E1_underBtn_1_2.png",
				"E_1/E1_underBtn_1_3.png");
		btn.setPoint(0, 452*2);
		btn.setCommand( TAG_PASS );
		m_btnPass = btn; 

		btn = createButton(
				"E_1/E1_underBtn_3_4.png",
				"E_1/E1_underBtn_3_2.png",
				"E_1/E1_underBtn_3_3.png");
		btn.setPoint(124*2, 454*2);
		btn.setCommand( TAG_MENU );
		m_btnMenu = btn; 

		btn = createButton(
				"E_1/E1_underBtn_2_4.png",
				"E_1/E1_underBtn_2_2.png",
				"E_1/E1_underBtn_2_3.png");
		btn.setPoint(210*2, 452*2);
		btn.setCommand( TAG_SET );
		m_btnSet = btn; 
	}
	
	public void UpdateButtonState() {
		boolean bEnable = m_pGameCtrl.SS_IsEnableUserAction();
		m_btnMenu.setEnable(bEnable);
		m_btnSet.setEnable(bEnable && m_pGameCtrl.SS_IsEnableDiscard());
		m_btnPass.setEnable(bEnable && m_pGameCtrl.SS_IsPassable());
	}

	private void actionPass() {
		m_pGameCtrl.SS_Action(GameLogic.ACT_COMMAND, GameLogic.CMD_PASS, 0);
		PostMessage(WM_ACT_TO_NEXT);
		m_nGameCommand = WndGame.BTN_PASS;
	}

	private void actionMenu() {
		Globals.playSE(MySoundManager.se_set);
		StopNoCardAnimation();
		int	nId = m_viewMenuNoSave.showMenu();
		procMenuCmd(nId);
	}

	private void actionSet() {
		if (!m_bShowMsg)
		{			
			m_pGameCtrl.SS_Action(GameLogic.ACT_COMMAND, GameLogic.CMD_SET, 0);
			PostMessage(WM_ACT_TO_NEXT);
		}
		else
		{
			PlayGameSE(MySoundManager.se_set);
			if (m_nMsgType == WndGame.MSG_NEXTGAME)
			{
				HideMessage();
				PostMessage(WM_ACT_TO_NEXT);				
			}
			else if (m_nMsgType == WndGame.MSG_EXCHANG && m_nMsgFrame > 0)
			{
				ProcChangeCardMsg();
			}			
		}
		m_nGameCommand = WndGame.BTN_SET;			
	}

	public void gameEnd2FreeMode() {
		int	nId = m_viewAfxMsg.showMsgBox("ゲームを続けますか？", AfxMsgView.MB_YESNO);
		switch (nId) {
		case AfxMsgView.ID_YES:
			PostMessage(WM_ACT_TO_NEXT);
			break;
		case AfxMsgView.ID_NO:
			DestroyWindow( frmWndMgr.WND_TITLE );
			break;
		}
	}

	public void OnGameEnd(boolean bWin) {
		int	nId;
		Globals.g_Global.UpdateUserSoldiers(bWin);
		if (bWin) {
			nId = m_viewAfxMsg.showMsgBox("プレイヤーの兵力が+500に\nなりました。", AfxMsgView.MB_OK);
			if (nId == AfxMsgView.ID_OK) {
				Globals.g_Global.SetCaptureCountry( Globals.g_Global.GetNPCCountry(), true );
				Globals.g_Global.SaveSetInfo(true);
				Globals.g_Global.SetCaptureFreeNPC( Globals.g_Global.GetNPCCountry() );
				Globals.g_Global.SaveFreeNPCs(true);

				Globals.m_bVictoryAnim = true;
				if (Globals.g_Global.GetBGM() == true) {
					Globals.stopBGM();
				}
				DestroyWindow( frmWndMgr.WND_VICTORY );
			}
		} else {
			nId = m_viewAfxMsg.showMsgBox("プレイヤーの兵力が-1000\n減少しました。", AfxMsgView.MB_OK);
			if (nId == AfxMsgView.ID_OK) {
				Globals.g_Global.SaveSetInfo(true);

				Globals.m_bVictoryAnim = false;
				if (Globals.g_Global.GetBGM() == true) {
					Globals.stopBGM();
				}
				
				if (Globals.g_Global.GetUserSoldiers() > 0) {
					DestroyWindow( frmWndMgr.WND_SELUNIFYNPC );
				} else {
					DestroyWindow( frmWndMgr.WND_GAMEOVER );
				}
			}
		}
	}

	// ShowMessage
	public void ShowMessage(int nType) {
		int nMsgType;
		if (nType == MSG_NEXTGAME) {
			nMsgType = SelMsgView.GAMEMSG_NEXTGAME;
		} else {
			nMsgType = SelMsgView.GAMEMSG_EXCHANGE_DAIFUGOU + m_pGameCtrl.SS_GetPlayerKind(GameDoc.PLAYER_USER);
		}
		
		int	nId = m_viewMsg.showMsg( nMsgType );
		if (nId == SelMsgView.GAMEMSG_NEXTGAME) {
			m_bShowNotifyMsg = false;
			PostMessage(WM_ACT_TO_NEXT);
			STD.logHeap();
		}
		else if (nId >= SelMsgView.GAMEMSG_EXCHANGE_DAIFUGOU && nId <= SelMsgView.GAMEMSG_EXCHANGE_DAIHINMIN) {
			ProcChangeCardMsg();
		}
		else if (nId == SelMsgView.GAMEMSG_MENU) {
			onMenu();
		}
	}

	private void onMenu() {
		StopNoCardAnimation();
		m_bShowNotifyMsg = true;
		
		int	nId = m_viewMenuSave.showMenu();
		procMenuCmd(nId);
	}
	
	private void procMenuCmd(int menuCmd) {
		boolean bBGM = Globals.g_Global.GetBGM();
		// [delegate SetSoundInfo];
		Globals.g_docSuspend.GetSaveDataFromGlobal(true);
		
		switch (menuCmd) {
		case AfxMenuView.MENU_STOP:		OnMenuStop();		break;
		case AfxMenuView.MENU_EXIT:		OnMenuExit();		break;
		case AfxMenuView.MENU_RULE:		OnMenuRule();		break;
		case AfxMenuView.MENU_CLOSE:	OnMenuClose();		break;
		}
		
		if (bBGM)
			Globals.playBGM(MySoundManager.bgm_game);
		else
			Globals.stopBGM();
	}
	
	private void OnMenuClose() {
		if (m_bShowNotifyMsg) {
			ShowMessage(MSG_NEXTGAME);
		}
	}
	
	private void OnMenuExit() {
		int nId = m_viewAfxMsg.showMsgBox("ゲームを終了し\nタイトルへ戻りますか？", AfxMsgView.MB_YESNO);
		if (nId == AfxMsgView.ID_YES) {
//disable by hrh 2011-0614			Globals.g_Global.Init();
			Globals.g_Global.SaveSetInfo(true);
			DestroyWindow( frmWndMgr.WND_TITLE );
		} else if (nId == AfxMsgView.ID_NO) {
			OnMenuClose();
		}
	}
	
	private void OnMenuRule() {
		m_viewRule.show();
		OnMenuClose();
	}
	
	private void OnMenuStop() {
		int nId = m_viewAfxMsg.showMsgBox("セーブして中断終了しますか？", AfxMsgView.MB_YESNO);
		if (nId == AfxMsgView.ID_YES) {
			GameDoc		doc = new GameDoc();
			doc.GetGameKifuFromEngine(m_pGameCtrl, false);
			if ( doc.Save() )
				CConFile.delete(Globals.CONTINUE_FILE);
			
			Globals.g_Global.SaveSetInfo(true);
			DestroyWindow( frmWndMgr.WND_TITLE );
		} else if (nId == AfxMsgView.ID_NO) {
			OnMenuClose();
		}
	}

	/*
	 * (non-Javadoc)
	 * GameWnd.java
	 * @see com.dlten.lib.frmWork.CWnd#WindowProc(int, int, int)
	 */
	public int WindowProc(int message, int wParam, int lParam) {
		int	nRet = -1;
		switch (message) {
		
		case WM_ACT_TO_NEXT:
			m_pGameCtrl.SS_Action(GameLogic.ACT_TO_NEXT, 0, m_pGameCtrl.SS_GetState());
			break;
		case WM_ACT_COMMAND:
			m_pGameCtrl.SS_Action(GameLogic.ACT_COMMAND, wParam, m_pGameCtrl.SS_GetState());
			break;
		case WM_EXIT_FROM_FREEMODE:
			gameEnd2FreeMode();
			break;
			
		default:
			nRet = super.WindowProc(message, wParam, lParam);
			break;
		}
		return nRet;
	}

	/*
	 * GameConst.h
	 */
	public static final int
		MAX_CARD		= 11,
		SCREEN_WIDTH	= 320,
		SCREEN_HEIGHT	= 480,

		CARD_WIDTH		= 48,
		CARD_HEIGHT		= 64;

	public static final int
		FACE_NORMAL = 1,
		FACE_GOOD = 2,	
		FACE_BAD = 3;

	public static final int HandCard_PosX[] = {
		1,  46,  91, 136, 181,
		1,  46,  91, 136, 181, 226
	};
	public static final int HandCard_PosY[] = {
		314, 382
	};

	public static final CPoint	Discard_SingleCardPos[] = {
		new CPoint(136,128),
		new CPoint(104,104),
		new CPoint(116, 54),
		new CPoint(152, 70),
		new CPoint(169,105)
	};

	public static final CPoint	Discard_DoubleCardPos[][] = {
		{ new CPoint(126,128), new CPoint(146,128) },
		{ new CPoint( 94,104), new CPoint(114,104) },
		{ new CPoint(106, 54), new CPoint(126, 54) },
		{ new CPoint(142, 70), new CPoint(162, 70) },
		{ new CPoint(159,105), new CPoint(179,105) },
	};

	public static final CPoint	Discard_TripleCardPos[][] = {
		{ new CPoint(116,128), new CPoint(136,128), new CPoint(156,128) },
		{ new CPoint( 84,104), new CPoint(104,104), new CPoint(124,104) },
		{ new CPoint( 96, 54), new CPoint(116, 54), new CPoint(136, 54) },
		{ new CPoint(132, 70), new CPoint(152, 70), new CPoint(172, 70) },
		{ new CPoint(149,105), new CPoint(169,105), new CPoint(189,105) },
	};

	public static final CPoint	Discard_QuadCardPos[] = {
		new CPoint(100,88),
		new CPoint(125,88),
		new CPoint(150,88),
		new CPoint(175,88)
	};

	public static final CPoint Move_NPCCardStartPos[] = {
		new CPoint(160-24, 241),
		new CPoint(32,176),
		new CPoint(32, 46),
		new CPoint(225, 46),
		new CPoint(225,176)
	};

	// public final String szEnemySpeech[CHR_MAX][256]
	public final String szEnemySpeech[] = {
		"我が天を欲しているのではない。天が我を欲しているのだ。",				//曹操孟徳
		"漢王朝再興のためわが身を捧げん。大義は私にこそ存在する。",				//劉備玄徳
		"孫家の血筋を絶やさぬのが、私の努め。このおもい誰にも止められぬ。",		//孫権仲謀
		"名門袁家は漢王朝の柱石とならん。ワシを中心とし、帝を盛り立てるのだ。",	//袁紹本初
		"この子龍いつでもお相手いたそう。我が槍先を避けるにはそれなりの力いるぞ。",	//趙雲子龍
		"兄者の盾になるのが私のつとめ。その前に立ちふさがる者、容赦せん。",		//関羽雲長
		"どーしてもこのさきを通りたいっていうなら。俺様を倒してからいくことだ。",	//張飛益徳
		"我が力の前に全ての者はひざまずくべし。",							//呂布奉先
		"曹操の覇業の道、地ならしをするのが我がさだめ。",						//夏侯惇元譲
		"天下は我が物。全てを手に入れた我に、逆らうことはなんびとも許さん。",		//董卓仲穎
		"私を出し抜こうなど、天下を手玉に取ってからにしてほしいものだ。",			//諸葛亮孔明
		"この俺が水軍を率いる以上、敗北という文字はナシ！！",					//周瑜公瑾
		"ここを通りたくば、全てを差し出せ。",								//山賊
		"戦えと言われれば戦うのみ。",										//兵士
		"陛下の盾とならん。",											//近衛兵
		"すべては漢王朝のため。わたくしの、すべてを捧げるのみ。",				//貂蝉
		"周瑜様の敵になる貴方を絶対に許しません！！",						//小喬
	};

	//debug
	public static final CPoint HandNPCCard_Pos[] = {
		new CPoint(  0,   0),
		new CPoint(  8, 160),
		new CPoint(  8,  12),
		new CPoint(110,  26),
		new CPoint(110, 172)
	};
	public static final int HandNPCCard_X = 12;

	private HANDCARDS_INFO mHandCards = new HANDCARDS_INFO();	// for local HANDCARDS_INFO 
	private GameLogic.DISCARD_CARDS_INFO mDiscardCards[] = new GameLogic.DISCARD_CARDS_INFO[20];

	
	
	
	
	
	/*
	 * GameWnd.h
	 */
	
	public static final int
	WM_ACT_TO_NEXT			= WM_USER+1,
	WM_ACT_COMMAND			= WM_USER+2,
	WM_EXIT_FROM_FREEMODE	= WM_USER+3;
	
	public static final int
		SOLDIER_USER      = 1,
		SOLDIER_CPU1      = 2,
		SOLDIER_DOWN_USER = 3,
		SOLDIER_DOWN_CPU1 = 4;
	
	public static final int
		TM_GAMEEND        = CWnd.ID_TIMER_0,	// SetTimer(TM_GAMEEND, 1000);
		TM_SOLDIERS       = CWnd.ID_TIMER_1,	// SetTimer(TM_SOLDIERS, 50);
//		TM_AFTER_SOLDIERS,
//		TM_EXCHANGECARD   = CWnd.ID_TIMER_2,	// SetTimer(TM_EXCHANGECARD, 2000);
		TM_WAIT           = CWnd.ID_TIMER_3;	// SetTimer(TM_WAIT, 2000);
//		TM_READYRESTART

	public static final int
		BGM_GAME    = 0,
		BGM_START   = 1,
		BGM_PENALTY = 2,
		BGM_WIN     = 3,
		BGM_LOSE    = 4;

	public static final short
		ANIM_START_UP       = 0x100,
		ANIM_START_DOWN     = 0x101,
		ANIM_RESTART_DOWN   = 0x102,
		ANIM_USERCARD_DOWN  = 0x103,
		ANIM_USERCARD_UP    = 0x104,
		ANIM_NPCCARD        = 0x105,
//		ANIM_PASS0          = 0x106,
		ANIM_AGARI          = 0x107,
		ANIM_8KIRI          = 0x108,
		ANIM_HANSOKU        = 0x109,
		ANIM_KAKUMEI_SET    = 0x10A,
		ANIM_KAKUMEI_FREE   = 0x10B,
		ANIM_MIYAKOOTI      = 0x10C,
		ANIM_SPADE3         = 0x10D,
		ANIM_NOCARD         = 0x10E,
		ANIM_SIBARI         = 0x10F,
		ANIM_WIN            = 0x110,
		ANIM_LOOSE          = 0x111,
		ANIM_MIYAKOOTI_DOWN = 0x112,
		ANIM_BATTLEEFF      = 0x113,
		ANIM_PASS0          = 0x114,
		ANIM_PASS1          = 0x115,
		ANIM_PASS2          = 0x116,
		ANIM_PASS3          = 0x117,
		ANIM_PASS4          = 0x118;

	public static final int
		CUTIN_8KIRI     = 0,
		CUTIN_HANSOKU   = 1,
		CUTIN_KAKUMEI   = 2,
		CUTIN_LOSE      = 3,
		CUTIN_MIYAKOOTI = 4,
		CUTIN_NOCARD    = 5,
		CUTIN_SIBARI    = 6,
		CUTIN_SPADE3    = 7,
		CUTIN_WIN       = 8;
	
	private static final int
		MOVE_NPCFRAME = 8,
		MOVE_USERFRAME = 10;
	
	private GameLogic	m_pGameCtrl = null;
	
	CAnim83		m_animUpDiscard[] = new CAnim83[4];
	CImgObj		m_picMoveCard[] = new CImgObj[4];
	
	// CTSAnim
	CAnim83		m_animPass[]       = new CAnim83[5];
	CAnim83		m_animAgari        = null;
	
	CAnim83		m_animMutiRule[]       = new CAnim83[6];
	CAnim83		m_animNoCard[]         = new CAnim83[3];
	CAnim83		m_animBattleEffect[][] = new CAnim83[12][];
	CAnim83		m_animGameResult[]     = new CAnim83[12];
	CAnim83		m_animStartUp[]        = new CAnim83[3];
	CAnim83		m_animStartDown[]      = new CAnim83[4];
	CAnim83		m_animUserMiyako[]     = new CAnim83[3];
	
	boolean		m_bAnimMultiRule;
	
	int			m_nMoveCardCount;
	boolean		m_bShowMenu;
	boolean		m_bShowRuleInfo;
	boolean		m_bMoveCard;	
	boolean		m_bSetRevolution;
	boolean		m_bUserHansoku;

	boolean		m_bHansoku[] = new boolean[GameDoc.PLAYER_COUNT];
	boolean		m_bMiyakoOti[] = new boolean[GameDoc.PLAYER_COUNT];
	
	boolean		m_bContinueGame;
	boolean		m_bSuspendGame;
	
	int			m_nPrevSoldiersRate[] = new int[2];
	int			m_nSoldiersOffsetRate[] = new int[2];
	int			m_nSoldiersAnimFrame;
	boolean		m_bAnimSoldiersRate;
	
	boolean 	m_bShowMsg;
	boolean		m_bShowHandCards;
	
	int			m_nMsgType;
	int 		m_nMsgFrame;
	
	int 		m_nGameCommand;
	long		m_nMeterSeStartTime;
	
	int			m_nBgmId;
	boolean		m_bLoadBGM;
	
	/*************************** resource load and unload - Start ************************/
	private void UnLoadStartAnimRes(boolean bUp) {
		m_bShowHandCards = true;
	}
	
	private void ReLoadUserChr(int nFace) {
		String str = String.format("E_1/E1_bottomChara_%02d_%d.png", m_pGameCtrl.SS_GetPlayerChr(GameDoc.PLAYER_USER)+1, nFace);
		// m_lyt.GetPicturePane("bottomChara").SetTexture(&m_t2g, szBuf);
		img_bottomChara.load(str);
	}
	
	private void LoadGameMain(boolean bSetBg) {
		ReLoadUserChr(FACE_NORMAL);
		LoadNPCChrs();
		if (Globals.g_Global.GetGameMode() == Globals.GM_FREE) {
			img_lifeBg.load("E_1/E1_lifeBg_2.png");
		}
		else {
			img_lifeBg.load("E_1/E1_lifeBg_1.png");
			SetSoldierBarSize();
		}
		SetTextSoldiers();
	}
	
	private void LoadNPCChrs() {
		int nChr;
		for (int i = GameDoc.PLAYER_CPU1; i < GameDoc.PLAYER_COUNT; i ++) {
			nChr = m_pGameCtrl.SS_GetPlayerChr(i);
			LoadNPCChr(i, nChr, FACE_NORMAL);
		}	
	}
	
	private void LoadNPCChr(int nPlayerIndex, int nChr, int nFace) {
		String	szChr;
		
		if (nPlayerIndex == GameDoc.PLAYER_CPU1 || nPlayerIndex == GameDoc.PLAYER_CPU2) {
			szChr = String.format("E_1/E1_topCharaL_%02d_%d.png", nChr+1, nFace);
		}
		else if (nPlayerIndex == GameDoc.PLAYER_CPU3 || nPlayerIndex == GameDoc.PLAYER_CPU4) {
			szChr = String.format("E_1/E1_topCharaR_%02d_%d.png", nChr+1, nFace);
		}
		else {
			return;
		}
		CImgObj	img;
		switch (nPlayerIndex) {
		case 1:		img = img_topChara_1;	break;
		case 2:		img = img_topChara_2;	break;
		case 3:		img = img_topChara_3;	break;
		default:	img = img_topChara_4;	break;
		}
		img.load(szChr);
	}

	private String GetNPCChrName(int nPlayerChr, boolean bLeft) {
		final String szNpcChrName[] = {
			"_01_2",	"_02_1",	"_03_1",
			"_04_1",	"_05_1",	"_06_1",
			"_07_2",	"_08_2",	"_09_1",
			"_10_2",	"_11_2",	"_12_2",
			"_13_2",	"_14_2",	"_15_2",
			"_16_2",	"_17_2",
		};
		String szBuf;
		if (bLeft) {
			szBuf = String.format("E_1/E1_topChara%s%s.png", "L", szNpcChrName[nPlayerChr]);
		} else {
			szBuf = String.format("E_1/E1_topChara%s%s.png", "R", szNpcChrName[nPlayerChr]);
		}
		
		return szBuf;
	}

	private void LoadRule(int nRuleType) {
		String	szBuf;
		if (nRuleType == ANIM_KAKUMEI_SET) {
			int nPlayer = m_pGameCtrl.SS_GetTurn();
			int nPlayerChr = m_pGameCtrl.SS_GetPlayerChr(nPlayer);
			szBuf = GetNPCChrName(nPlayerChr, true);
			img_kakumeiCharaL.load(szBuf);
		}
		else if (nRuleType == ANIM_KAKUMEI_FREE) {
			int nPlayer = m_pGameCtrl.SS_GetTurn();
			int nPlayerChr = m_pGameCtrl.SS_GetPlayerChr(nPlayer);
			szBuf = GetNPCChrName(nPlayerChr, false);
			img_kakumeiCharaR.load(szBuf);
		}
	}
	
	private void UnLoadRule(int nRuleType) {
	}
	
	private void LoadResult(boolean bWin) {
	}

	private void LoadShowRule() {
	}

	private void UnLoadShowRule() {
	}

	private void UnLoadHansoku() {
	}

	private void SetChrPltStartDownAnim(boolean bSet) {
	}
	
	public void LoadResource(boolean bLoad) {
		if ( bLoad ) {
			setImageFiles();
			
			LoadGameMain(false);
			SetPlayerKind();
			SetGamePlayingCount();
			SetNPCHandCards();
			SetUserHandCards();
			SetDiscardCards();
		} else {
			releaseImageFiles();
		}
	}

	public void InitGame(boolean bInitDoc) {
		/*
		m_animPass = null;
		m_animAgari = null;
		m_pAnimNoCardChr = NULL;
		m_pAnimNoCardText = NULL;
		m_pAnimNoCardBg = NULL;
		*/
		m_nMoveCardCount = 0;
		m_bShowMenu = false;
		m_bShowRuleInfo = false;
		m_bMoveCard = false;
		m_bShowMsg = false;
		m_bSetRevolution = false;
		STD.MEMSET(m_bHansoku, false);
		STD.MEMSET(m_bMiyakoOti, false);
		if (bInitDoc)
			Globals.g_docSuspend.InitRuleInfo();
	}

	private void InitSoldiersInfo() {
		if (Globals.g_Global.GetGameMode() != Globals.GM_UNIFY)
			return;
		
		for (int i = 0; i < 2; i ++) {
			m_nPrevSoldiersRate[i] = m_pGameCtrl.SS_GetSoldiersRate(i);
		}
		STD.MEMSET(m_nSoldiersOffsetRate, 0);
		m_nSoldiersAnimFrame = 0;
		m_bAnimSoldiersRate = false;
	}
	
	private void PlayGameBGM(int nBgmId) {
		Globals.playBGM(nBgmId);
	}

	private void PlayGameSE(int nSE) {
		Globals.playSE(nSE);
	}

	private void PlayGameSEEx(int nSE) {
//		Globals.stopBGM();
//		Globals.playSE(nSE);
		
		Globals.playSEEx(nSE);
	}

	private void ChangePlayerFace(int nPlayer, int nFace) {
		if (nPlayer == GameDoc.PLAYER_USER) {
			ReLoadUserChr(nFace);
		} else {
			int nChr = m_pGameCtrl.SS_GetPlayerChr(nPlayer);
			LoadNPCChr(nPlayer, nChr, nFace);
		}
	}

	private void HideMessage() {
		/*
		if (m_bShowMsg == FALSE)
			return;
		m_bShowMsg = FALSE;
		m_bgMsg.UnSetBackGround();
		m_bgMsg.Unload();
		CRect rt;
		rt.SetRect(14, 40, 14+228, 40+104);
		CDC::ClearRect_S(rt);
		SetTextSoldiers();
		 */
	}

	private void OnPaint_Top() {
		//top
		img_COMN_bg_01_top.draw();
		DrawNPCChrs();
		DrawNPCMiyakoOti();
		DrawNPCHansoku();
		DrawNPCsInfo();
		DrawGamePlayingCount();
		DrawNPCFocus();
		DrawNPCHandCards();
		DrawDiscardCards();
		//animation
		if (m_bMoveCard) {
			for (int i = 0; i < m_nMoveCardCount; i ++) {
				m_animUpDiscard[i].Draw();
			}
		}
		if (m_bAnimMultiRule == true && m_bUserHansoku == false) {
			DrawRuleAnimation();
		}
		if (m_pGameCtrl.SS_GetState() == GameLogic.STATE_WIN || m_pGameCtrl.SS_GetState() == GameLogic.STATE_LOOSE) {
			for (int i = 0; i < m_animGameResult.length; i ++) {
				m_animGameResult[i].Draw();
			}
		}
		/*
		if (IsShowRuleInfo())
			DrawGameRuleInfo();
		else
		{
			DrawDiscardCards();
			DrawNPCHandCards();	
			DrawNPCFocus();
			DrawGamePlayingCount();
			DrawNPCsInfo();
			DrawNPCHansoku();
			DrawNPCMiyakoOti();
			DrawNPCChrs();	
		}
		 */
	}
	
	private void OnPaint_Bottom() {
		//bottom
		img_COMN_bg_01_btom.draw();
		DrawUserChr();
		DrawUserHansoku();
		DrawUserInfo();
		DrawSoldiers();
		DrawRuleInfo();
		DrawUserHandCards();
		/*
		DrawFocus();
		DrawUserHandCards();
		DrawRuleInfo();
		DrawSoldiers();
		DrawUserInfo();
		DrawUserHansoku();
		DrawUserChr();
		 */
		img_E1_underBg_1.draw();
	}
	
	private void DrawGameRuleInfo() {
	/*
		if (IsShowRuleInfo() == FALSE)
			return;
		
//		m_cellRuleUpInfo[1].DrawOAM();
		RULE_INFO rule = m_pGameCtrl.SS_GetGameRule();//g_Global.GetGameRule();
		
		if (rule.bKakumei)
			m_cellRuleUpInfo[5].DrawOAM();
		if (rule.bMiyakoOti)
			m_cellRuleUpInfo[6].DrawOAM();
		if (rule.bSpade3)
			m_cellRuleUpInfo[7].DrawOAM();
		if (rule.bSibari)
			m_cellRuleUpInfo[8].DrawOAM();
		if (rule.b8Kiri)
			m_cellRuleUpInfo[9].DrawOAM();
		if (rule.bJokerKinsi)
			m_cellRuleUpInfo[0xa].DrawOAM();
		if (rule.b2AgariKinsi)
			m_cellRuleUpInfo[0xb].DrawOAM();
		m_cellRuleUpInfo[4].DrawOAM();
		/*
		if (g_Global.GetGameMode() == GM_UNIFY)
		{
			//character name
			int nChr = m_pGameCtrl.SS_GetPlayerChr(GameDoc.PLAYER_CPU1);
			m_cellRuleChrName[nChr+0x11].DrawOAM();
			//character
			m_cellRuleChr[1].DrawOAM();		
		}
		*/
	}
	private void DrawNPCsInfo() {
		//cpu - up
		img_symbol_2.draw();
		img_symbol_3.draw();
		img_symbol_4.draw();
		img_symbol_5.draw();
	}

	private void DrawUserInfo() {
		//user - down
		img_symbol_1.draw();
	}

	private void DrawSoldiers() {
		
		if (Globals.g_Global.GetGameMode() == Globals.GM_FREE) {
			img_lifeBg.draw();
		} else {
			//life
			img_lifeBg.draw();
			img_lifeGauge_1.draw();
			img_lifeGauge_2.draw();
			img_lifeText_1.draw();
			img_lifeText_2.draw();
			img_lifeNum_1_1.draw();
			img_lifeNum_1_2.draw();
			img_lifeNum_1_3.draw();
			img_lifeNum_1_4.draw();
			img_lifeNum_1_5.draw();
			img_lifeNum_2_1.draw();
			img_lifeNum_2_2.draw();
			img_lifeNum_2_3.draw();
			img_lifeNum_2_4.draw();
			img_lifeNum_2_5.draw();
			img_lifeNum_3_1.draw();
			img_lifeNum_3_2.draw();
			img_lifeNum_3_3.draw();
			img_lifeNum_3_4.draw();
			img_lifeNum_3_5.draw();
			img_lifeNum_4_1.draw();
			img_lifeNum_4_2.draw();
			img_lifeNum_4_3.draw();
			img_lifeNum_4_4.draw();
			img_lifeNum_4_5.draw();
		}
	}

	private void DrawRuleInfo() {
		if (m_pGameCtrl.SS_GetRule(Globals.RULE_KAKUMEI) && m_bSetRevolution)
			img_gameInfo_1.draw();
		//sibari
		if (m_pGameCtrl.SS_GetRule(Globals.RULE_SIBARI) && m_pGameCtrl.SS_IsSibariState())
			img_gameInfo_2.draw();
	}

	private void DrawGamePlayingCount() {
		//battleCount
		img_E1_batleCountBg.draw();		
		img_E1_battleNum_3.draw();
		img_E1_battleNum_2.draw();
		img_E1_battleNum_1.draw();
	}

	private void DrawFocus() {
		/*
		if (m_pGameCtrl.SS_IsEnableUserAction() == FALSE || m_bShowMenu || 
			m_pGameCtrl.SS_GetTurn() != GameDoc.PLAYER_USER || m_bShowMsg || m_pGameCtrl.SS_GetState() == STATE_READY_EXCHANGE)
			return;
		int nFocus = m_pGameCtrl.SS_GetFocusNo();
		HANDCARDS_INFO stHandCards;
		int nCount = m_pGameCtrl.SS_GetHandCards(GameDoc.PLAYER_USER, &stHandCards);
		int x, y, nOffsetX;
		if (nCount > 1)
			nOffsetX = HandCard_PosX[nCount-1][1] - HandCard_PosX[nCount-1][0];
		else
			nOffsetX = CARD_WIDTH;
		x = HandCard_PosX[nCount-1][nFocus];// + nOffsetX / 2 - 6;
		if (stHandCards.bSetting[nFocus] == TRUE)
			y = HandCard_PosY - 20-16;
		else
			y = HandCard_PosY-16;
		
		m_pAnimFocus->ShowAnimation(x, y);
		//test resource
		 */
	}
	private void DrawUserHandCards() {
		int nState = m_pGameCtrl.SS_GetState();
		if (!m_bShowHandCards)
			return;
		//user
		HANDCARDS_INFO stHandCards = mHandCards;
		int nCount = m_pGameCtrl.SS_GetHandCards(GameDoc.PLAYER_USER, stHandCards);
		int i;
		CImgObj	img;
		for (i = 0; i < nCount; i ++) {		
			if ((m_bMoveCard && stHandCards.bSetting[i]) || (stHandCards.bSetting[i] && nState == GameLogic.STATE_ACTION_DISCARD))
				continue;
			
			img = getUserHandCards(i);
			if (stHandCards.bSetting[i] == true) {
				int x, y;
				x = HandCard_PosX[i];
				y = (i < 5 ? HandCard_PosY[0] : HandCard_PosY[1]) - 10;
				
				img.draw(x*2,y*2);
			} else {
				img.draw();
			}
		}
	}

	private void DrawNPCHandCards() {
		if (m_pGameCtrl.SS_GetState() <= GameLogic.STATE_INITIALIZE)
			return;
		HANDCARDS_INFO stHandCards = mHandCards;
		int nPlayer, nCount, i, nIndex;
		
		//debug
//		if (DEBUG_MODE == true && DEBUG_SHOW_CARDS == true) {
//			for (i = 4; i > 0; i --)
//			{
//				//nPlayer = m_pGameCtrl.SS_GetPlayerFromPlace(i);
//				nCount = m_pGameCtrl.SS_GetHandCards(i, &stHandCards);
//				if (nCount == 0)
//					continue;
//				for (int j = nCount-1; j >= 0; j --)
//				{		
//					if (m_bMoveCard && stHandCards.bSetting[j])
//						continue;
//					nIndex = GetCellNoFromCard(stHandCards.nHandCards[j]);
//					int x = GetNPCHandCardStartPosX(i,nCount)+HandNPCCard_X*j - 4;
//					int y = HandNPCCard_Pos[i].y;
//					if (stHandCards.bSetting[j] == true)
//						y -= 10;
//					
//					m_cellUCard[nIndex].DrawOAM(x, y, 3);
//				}
//			}
//		}

		//cpu
		CImgObj	img;
		for (i = GameDoc.PLAYER_CPU1; i < GameDoc.PLAYER_COUNT; i ++)
		{
			nCount = m_pGameCtrl.SS_GetHandCards(i, stHandCards);
			if (nCount == 0)
				continue;
			
			switch (i) {
			case 1:		img = img_cardInfo_1;	break;
			case 2:		img = img_cardInfo_2;	break;
			case 3:		img = img_cardInfo_3;	break;
			default:	img = img_cardInfo_4;	break;
			}
			
			img.draw();
		}
	}

	private void DrawNPCFocus() {
		/*
		//debug
	#ifdef DEBUG_MODE
		if (m_pGameCtrl.SS_IsEnableUserAction() == FALSE || m_bShowMenu || m_pGameCtrl.SS_GetTurn() == GameDoc.PLAYER_USER)
			return;
		int nPlayer, nCount, nIndex, nPlace;
		HANDCARDS_INFO stHandCards;
		nPlayer = m_pGameCtrl.SS_GetTurn();
		//nPlace = m_pGameCtrl.SS_GetPlaceFromPlayer(nPlayer);
		int nFocus = m_pGameCtrl.SS_GetFocusNo();
		nCount = m_pGameCtrl.SS_GetHandCards(nPlayer, &stHandCards);
		int x = GetNPCHandCardStartPosX(nPlayer,nCount)+HandNPCCard_X*nFocus;
		int y = HandNPCCard_Pos[nPlayer].y+8;
		if (stHandCards.bSetting[nFocus] == TRUE)
			y -= 6;
		
		m_cellUInfo1[1].DrawOAM(x, y, 2);
	#endif	
		 */
	}

	private void DrawDiscardCards() {
		GameLogic.DISCARD_CARDS_INFO cards[] = mDiscardCards;
		int nCount = m_pGameCtrl.SS_GetDiscardCards(cards);
		int nState = m_pGameCtrl.SS_GetState();
		CImgObj	img; 
		for (int i = 0; i < nCount; i ++) {
			if ( (m_bMoveCard || (nState == GameLogic.STATE_NPC_CHOOSING || nState == GameLogic.STATE_CHOOSING)) && i == nCount-1 && cards[i].nFrom == m_pGameCtrl.SS_GetTurn())
				continue;
			
			for (int j = 0; j < cards[i].nCount; j ++) {
				img = getCpuHandCards(cards[i].nFrom, cards[i].nKind, j);
				img.draw();
			}
		}
	}

	private void DrawUserChr() {
		//user
		img_bottomChara.draw();
	}

	private void DrawNPCChrs() {
		img_topChara_1.draw();
		img_topChara_2.draw();
		img_topChara_3.draw();
		img_topChara_4.draw();
	}

	private void DrawNPCHansoku() {
		if ( m_bHansoku[GameDoc.PLAYER_CPU1] )
			img_statusHansoku_1.draw();
		if ( m_bHansoku[GameDoc.PLAYER_CPU2] )
			img_statusHansoku_2.draw();
		if ( m_bHansoku[GameDoc.PLAYER_CPU3] )
			img_statusHansoku_3.draw();
		if ( m_bHansoku[GameDoc.PLAYER_CPU4] )
			img_statusHansoku_4.draw();
	}

	private void DrawUserHansoku() {
		if (m_bAnimMultiRule && m_bUserHansoku) {
			DrawRuleAnimation();
		}
		if (m_bHansoku[GameDoc.PLAYER_USER]) {
			img_hansokuBottom.draw();
		}
	}

	private void DrawNPCMiyakoOti() {
		if ( m_bMiyakoOti[GameDoc.PLAYER_CPU1] )
			img_statusMiyako_1.draw();
		if ( m_bMiyakoOti[GameDoc.PLAYER_CPU2] )
			img_statusMiyako_2.draw();
		if ( m_bMiyakoOti[GameDoc.PLAYER_CPU3] )
			img_statusMiyako_3.draw();
		if ( m_bMiyakoOti[GameDoc.PLAYER_CPU4] )
			img_statusMiyako_4.draw();
	}

//	private void DrawUserMiyakoOti() {
//	}

	private void DrawRuleAnimation() {
		int	nImgCount = m_animMutiRule.length;
		
		for (int i = 0; i < nImgCount; i ++) {
			if (m_animMutiRule[i] != null) {
				m_animMutiRule[i].Draw();
			}
		}
	}
	
	/************************** Set Resource Proc - Start *******************/
	private void UpdateScreenInfo() {
		SetTextSoldiers();
		SetPlayerKind();
		SetGamePlayingCount();
		SetNPCHandCards();
		SetUserHandCards();
	}

	private void SetPlayerKind() {
		CImgObj	img;
		String	szPic;
		int nKind;
		for (int i = GameDoc.PLAYER_USER; i < GameDoc.PLAYER_COUNT; i ++) {
			nKind = m_pGameCtrl.SS_GetPlayerKind(i);
			switch (i) {
			case GameDoc.PLAYER_USER:	img = img_symbol_1;		break;
			case GameDoc.PLAYER_CPU1:	img = img_symbol_2;		break;
			case GameDoc.PLAYER_CPU2:	img = img_symbol_3;		break;
			case GameDoc.PLAYER_CPU3:	img = img_symbol_4;		break;
			default:					img = img_symbol_5;		break;
			}
			szPic = String.format("E_1/E1_symbol_%d.png", nKind+1);
			img.load(szPic);
		}
	}
	private void SetGamePlayingCount() {
		int nCount = m_pGameCtrl.SS_GetGamePlayingCount();
		if (nCount > 999)
			nCount = 999;
		int nOne, nTen, nHund = 0;
		nOne = nCount % 10;	
		nTen = (nCount % 100) / 10;
		nHund = nCount / 100;
		int nNum;
		CImgObj	img;
		String	szPic;
		for (int i = 0; i < 3; i ++) {
			switch (i) {
				case 0:		nNum = nHund;	img = img_E1_battleNum_3;		break;
				case 1:		nNum = nTen;	img = img_E1_battleNum_2;		break;
				default:	nNum = nOne;	img = img_E1_battleNum_1;		break;
			}
			//if (nNum == 0)
			//	continue;
			szPic = String.format("E_1/E1_battleCountNum_%d.png", nNum);
			img.load(szPic);
		}
	}
	private void SetNPCHandCards() {
		HANDCARDS_INFO stHandCards = mHandCards;
		int nPlayer, nCount, i, nIndex;
		//debug
//	#if (defined(DEBUG_MODE) && defined(DEBUG_SHOW_CARDS))
//		for (i = 4; i > 0; i --)
//		{
//			//nPlayer = m_pGameCtrl.SS_GetPlayerFromPlace(i);
//			nCount = m_pGameCtrl.SS_GetHandCards(i, &stHandCards);
//			if (nCount == 0)
//				continue;
//			for (int j = nCount-1; j >= 0; j --)
//			{		
//				if (m_bMoveCard && stHandCards.bSetting[j])
//					continue;
//				nIndex = GetCellNoFromCard(stHandCards.nHandCards[j]);
//				int x = GetNPCHandCardStartPosX(i,nCount)+HandNPCCard_X*j - 4;
//				int y = HandNPCCard_Pos[i].y;
//				if (stHandCards.bSetting[j] == TRUE)
//					y -= 10;
//				
//				m_cellUCard[nIndex].DrawOAM(x, y, 3);
//			}
//		}	
//	#endif	
		
		//cpu
		CImgObj	img;
		String	szPic;
		for (i = GameDoc.PLAYER_CPU1; i < GameDoc.PLAYER_COUNT; i ++) {
			nCount = m_pGameCtrl.SS_GetHandCards(i, stHandCards);
			if (nCount == 0)
				continue;

			switch (i) {
			case GameDoc.PLAYER_CPU1:	img = img_cardInfo_1;	break;
			case GameDoc.PLAYER_CPU2:	img = img_cardInfo_2;	break;
			case GameDoc.PLAYER_CPU3:	img = img_cardInfo_3;	break;
			default:					img = img_cardInfo_4;	break;
			}
			
			if (i < GameDoc.PLAYER_CPU3)
				szPic = String.format("E_1/E1_cardInfoL_%02d.png", nCount);
			else
				szPic = String.format("E_1/E1_cardInfoR_%02d.png", nCount);
			img.load(szPic);
		}
	}
	private void SetUserHandCards() {
		int nState = m_pGameCtrl.SS_GetState();
		if (/*nState <= GameLogic.STATE_INITIALIZE*/!m_bShowHandCards)
			return;
		
		//user
		HANDCARDS_INFO stHandCards = mHandCards;
		int nCount = m_pGameCtrl.SS_GetHandCards(GameDoc.PLAYER_USER, stHandCards);
		int nIndex, i;
		CImgObj	img;
		String	szPic[] = new String[1];
		for (i = 0; i < nCount; i ++) {		
			if ((m_bMoveCard && stHandCards.bSetting[i]) || (stHandCards.bSetting[i] && nState == GameLogic.STATE_ACTION_DISCARD))
				continue;
			
			nIndex = GetCardImageNameFromCard(stHandCards.nHandCards[i], szPic);
			int y = HandCard_PosY[0];
			if (stHandCards.bSetting[i] == true)
				y -= 20;
			
			img = getUserHandCards(i);
			img.load(szPic[0]);	
		}
	}
	private void SetDiscardCards() {
		GameLogic.DISCARD_CARDS_INFO cards[] = mDiscardCards;
		int nCount = m_pGameCtrl.SS_GetDiscardCards(cards);
		int nCellNo;
		int nState = m_pGameCtrl.SS_GetState();
		String	szPic[] = new String[1];
		for (int i = 0; i < nCount; i ++)
		{
			if ( (m_bMoveCard || (nState == GameLogic.STATE_NPC_CHOOSING || nState == GameLogic.STATE_CHOOSING)) && i == nCount-1 && cards[i].nFrom == m_pGameCtrl.SS_GetTurn())
				continue;
			
			for (int j = 0; j <cards[i].nCount; j ++) {
				CImgObj	img = getCpuHandCards(cards[i].nFrom, cards[i].nKind, j);
				nCellNo = GetCardImageNameFromCard(cards[i].nCards[j], szPic);
				img.load(szPic[0]);	
			}		
		}

	}

	private void SetTextSoldiers() {
		if (Globals.g_Global.GetGameMode() == Globals.GM_FREE)
			return;

		SetSoldiersInfo(SOLDIER_USER, m_pGameCtrl.SS_GetSoldiers(GameDoc.PLAYER_USER));
		SetSoldiersInfo(SOLDIER_CPU1, m_pGameCtrl.SS_GetSoldiers(GameDoc.PLAYER_CPU1));
		SetSoldiersInfo(SOLDIER_DOWN_USER, m_pGameCtrl.SS_GetDownSoldiers(GameDoc.PLAYER_USER));
		SetSoldiersInfo(SOLDIER_DOWN_CPU1, m_pGameCtrl.SS_GetDownSoldiers(GameDoc.PLAYER_CPU1));
		if (m_pGameCtrl.SS_GetDownSoldiers(GameDoc.PLAYER_USER) == 0 && 
				m_pGameCtrl.SS_GetDownSoldiers(GameDoc.PLAYER_CPU1) == 0 &&
				m_pGameCtrl.SS_GetState() == GameLogic.STATE_RESULT) {
			if (m_nSoldiersOffsetRate[1] == 0) {
				img_lifeNum_3_1.setVisible( true );
				img_lifeNum_3_2.setVisible( true );
				img_lifeNum_3_2.load("E_1/E1_lifeNum_2_0.png");
			}
			else {
				img_lifeNum_4_1.setVisible( true );
				img_lifeNum_4_2.setVisible( true );
				img_lifeNum_4_2.load("E_1/E1_lifeNum_2_0.png");
			}
		}
	}

	private void SetSoldiersInfo(int nType, int nSoldier) {
		int nNum, nIndex = 0, nStart = 0;
		if (nType == SOLDIER_DOWN_USER || nType == SOLDIER_DOWN_CPU1)
		{
			nStart = 1;
			if (nType == SOLDIER_DOWN_USER)
				img_lifeNum_3_1.setVisible( nSoldier == 0 ? false : true );
			else {
				img_lifeNum_4_1.setVisible( nSoldier == 0 ? false : true );
			}
		}

		CImgObj	img;
		String	szPic;
		for (int i = nStart; i < 5; i ++) {
			nNum = nSoldier % 10;
			nSoldier /= 10;
			if (nType == SOLDIER_DOWN_USER || nType == SOLDIER_DOWN_CPU1)
				nIndex = 6-i;
			else
				nIndex = i+1;
			
			img = getImgLifeNum(nType, nIndex);
			
			if (nSoldier == 0 && nNum == 0) {
				img.setVisible( false );
			} else {
				szPic = String.format("E_1/E1_lifeNum_%d_%d.png", (nType-SOLDIER_USER)/2+1, nNum);
				img.setVisible( true );
				img.load(szPic);
			}
		}	
	}

	private void SetSoldierBarSize() {
		float nSoldierBarW = 100*2;
		float nSoldierBarH = 10*2;
		int nRate;
		CSize size = new CSize();
		CImgObj	img;
		for (int i = 0; i < 2; i ++) {
			if (i == 0)
				img = img_lifeGauge_1;
			else
				img = img_lifeGauge_2;
			
			nRate = 100 - GetSoldiersRate(i);
			if (nRate < 0)
				nRate = 0;
			
			size.w = (nSoldierBarW * (float)nRate) / 100;
			size.h = nSoldierBarH;
			img.setSize(size.w, size.h);
		}
	}

	/************************** Animation Proc - Start *******************/
	private void SetStartUpAnimation() {
//		m_animStartUp.Create( (void*)m_pGameView, &m_lyt, &m_t2g, (char*)"E_1_gameStartAni_1", TRUE );
//		m_animStartUp.ChangeCallBackType(CALLBACK_LAST_FRM, ANIM_START_UP);
//		m_animStartUp.Start();
		
		String	szBuf;
		int	nImgCount = m_animStartUp.length;
		int	chrPlayer = m_pGameCtrl.SS_GetPlayerChr(GameDoc.PLAYER_CPU1);
		
		img_gameStartBg = new CImgObj("E_1/E1_gameStartBg_1.png");
		szBuf = String.format("%s", GetNPCChrName(chrPlayer, true));
		img_gameStartChara = new CImgObj(szBuf);
		szBuf = String.format("E_1/E1_gameStartText_%02d.png", chrPlayer+1);
		img_gameStartText = new CImgObj(szBuf);
		
		int i = 0;
		for (i = 0; i < nImgCount; i ++) {
			m_animStartUp[i] = new CAnim83();
		}
		
		int[][] pPtList = null;
		pPtList = new int [][] {	{-640,  98},	{   0,  98},	};
		m_animStartUp[0].Create( this, CAnim83.CREATE_ANIM_LINE_ACCELER, img_gameStartBg,    pPtList, pPtList.length, 0, 4, 96 );
		pPtList = new int [][] {	{-288, 124},	{   0, 124},	};
		m_animStartUp[1].Create( this, CAnim83.CREATE_ANIM_LINE_ACCELER, img_gameStartChara, pPtList, pPtList.length, 5, 4, 91 );
		pPtList = new int [][] {	{-464, 136},	{ 176, 136},	};
		m_animStartUp[2].Create( this, CAnim83.CREATE_ANIM_LINE_ACCELER, img_gameStartText,  pPtList, pPtList.length, 3, 4, 93 );
		
		m_animStartUp[0].ChangeCallBackType(CAnimation.CALLBACK_LAST_FRM, ANIM_START_UP, 0);
		
		for (i = 0; i < nImgCount; i ++) {
			m_animStartUp[i].Start();
		}
		
		//SetStartDownAnimation(true);//test-kgh
	}
	
	private void SetStopUpAnimation() {
		int	nImgCount = m_animStartUp.length;
		for (int i = 0; i < nImgCount; i ++) {
			if (m_animStartUp[i] != null) {
				m_animStartUp[i].Stop();
				m_animStartUp[i] = null;
			}
		}
		img_gameStartBg = null;
		img_gameStartChara = null;
		img_gameStartText = null;
	}

	private void SetStartDownAnimation(boolean bUp) {
//		m_animStartDown.Create( this, (char*)"E_1_gameStartAni_2", TRUE );
//		m_animStartDown.ChangeCallBackType(CALLBACK_LAST_FRM, ANIM_START_DOWN);
//		m_animStartDown.Start();

		img_battleStart_1 = new CImgObj("E_1/E1_battleStart_1.png");
		img_battleStart_2 = new CImgObj("E_1/E1_battleStart_2.png");
		img_battleStart_3 = new CImgObj("E_1/E1_battleStart_3.png");
		img_battleStart_4 = new CImgObj("E_1/E1_battleStart_4.png");
		
		int	i;
		int	nImgCount = m_animStartDown.length;
		for (i = 0; i < nImgCount; i++) {
			m_animStartDown[i] = new CAnim83();
		}
		
		int[][] pPtList = null;
		pPtList = new int [][] {	{ 640, 960}, {  56, 660}, {  56, 660}, {  56, 660}, {  56, 660}, {  56, 660}, {-304, 960},	};
		m_animStartDown[0].Create( this, CAnim83.CREATE_ANIM_LINE_ACCELER, img_battleStart_1, pPtList, pPtList.length, 0, 60, 6 );
		pPtList = new int [][] {	{ 640, 960}, { 184, 660}, { 184, 660}, { 184, 660}, { 184, 660}, { 184, 660}, {-304, 960},	};
		m_animStartDown[1].Create( this, CAnim83.CREATE_ANIM_LINE_ACCELER, img_battleStart_2, pPtList, pPtList.length, 2, 60, 4 );
		pPtList = new int [][] {	{ 640, 960}, { 304, 660}, { 304, 660}, { 304, 660}, { 304, 660}, { 304, 660}, {-304, 960},	};
		m_animStartDown[2].Create( this, CAnim83.CREATE_ANIM_LINE_ACCELER, img_battleStart_3, pPtList, pPtList.length, 4, 60, 2 );
		pPtList = new int [][] {	{ 640, 960}, { 432, 660}, { 432, 660}, { 432, 660}, { 432, 660}, { 432, 660}, {-304, 960},	};
		m_animStartDown[3].Create( this, CAnim83.CREATE_ANIM_LINE_ACCELER, img_battleStart_4, pPtList, pPtList.length, 6, 60, 0 );
		
		m_animStartDown[0].ChangeCallBackType(CAnimation.CALLBACK_LAST_FRM, ANIM_START_DOWN, 0);
		
		for (i = 0; i < nImgCount; i++) {
			m_animStartDown[i].Start();
		}
	}

	private void SetStopDownAnimation() {
		int	nImgCount = m_animStartDown.length;
		for (int i = 0; i < nImgCount; i++) {
			if (m_animStartDown[i] != null) {
				m_animStartDown[i].Stop();
				m_animStartDown[i] = null;
			}
		}
		
		img_battleStart_1 = null;
		img_battleStart_2 = null;
		img_battleStart_3 = null;
		img_battleStart_4 = null;
	}
	
	private void Set8KiriAnimation() {
//		m_animMutiRule.Create( this, (char*)"E_1_8kiriAni_1", FALSE );
//		m_animMutiRule.ChangeCallBackType(CALLBACK_LAST_FRM, ANIM_8KIRI);
//		m_animMutiRule.Start();
		
		int	i;
		int	nImgCount = 2;
		for (i = 0; i < nImgCount; i++) {
			m_animMutiRule[i] = new CAnim83();
		}

		int pPtList[][][] = new int [][][] {
                {	{ 334,-188}, {   0, 146}, {   0, 146}, {   0, 146}, {   0, 146}, {   0, 146}, {   0, 146}, {   0, 146}, {   0, 146}, {-333, 480},	},
                {	{-110, 480}, { 224, 146}, { 224, 146}, { 224, 146}, { 224, 146}, { 224, 146}, { 224, 146}, { 224, 146}, { 224, 146}, { 558,-188},	},
		};
		m_animMutiRule[0].Create( this, CAnim83.CREATE_ANIM_LINE_ACCELER, img_8kiri_1, pPtList[0], pPtList[0].length, 0, 50, 0 );
		m_animMutiRule[1].Create( this, CAnim83.CREATE_ANIM_LINE_ACCELER, img_8kiri_2, pPtList[1], pPtList[1].length, 0, 50, 0 );
		
		m_animMutiRule[0].ChangeCallBackType(CAnimation.CALLBACK_LAST_FRM, ANIM_8KIRI, 0);
		
		for (i = 0; i < nImgCount; i++) {
			m_animMutiRule[i].SetDrawBylib(false);
			m_animMutiRule[i].Start();
		}
	}
	
	private void Stop8KiriAnimation() {
//		m_animMutiRule.Stop();
	}
	
	private void SetHansokuAnimation() {
//		if (m_pGameCtrl.SS_GetTurn() == GameDoc.PLAYER_USER) {
//			m_bUserHansoku = TRUE;
//			m_animMutiRule.Create( this, (char*)"E_1_hansokuAni_2", FALSE );
//		} else {
//			m_animMutiRule.Create( this, (char*)"E_1_hansokuAni_1", FALSE );
//		}
//		m_animMutiRule.ChangeCallBackType(CALLBACK_LAST_FRM, ANIM_HANSOKU);
//		m_animMutiRule.Start();
		
		CImgObj	img;
		int	y0 = 0;
		if (m_pGameCtrl.SS_GetTurn() == GameDoc.PLAYER_USER) {
			m_bUserHansoku = true;
			y0 = 480;
			img = img_hansokuBottom;
		} else {
			y0 = 0;
			img = img_hansokuTop;
		}
		int	i;
		int	nImgCount = 1;
		for (i = 0; i < nImgCount; i++) {
			m_animMutiRule[i] = new CAnim83();
		}

		int pPtList[][][] = new int [][][] {
                {	{ 640, y0+160}, { 174, y0+160}, { 174, y0+160}, { 174, y0+160}, { 174, y0+160},
                	{ 174, y0+160}, { 174, y0+160}, { 174, y0+160}, { 174, y0+160}, { 174, y0+160}, 
                	{ 174, y0+160}, { 174, y0+160}, { 174, y0+160}, { 174, y0+160}, {-640, y0+160},	},
		};
		m_animMutiRule[0].Create( this, CAnim83.CREATE_ANIM_LINE_ACCELER, img, pPtList[0], pPtList[0].length, 0, 150, 0 );
		
		m_animMutiRule[0].ChangeCallBackType(CAnimation.CALLBACK_LAST_FRM, ANIM_HANSOKU, 0);
		
		for (i = 0; i < nImgCount; i++) {
			m_animMutiRule[i].SetDrawBylib(false);
			m_animMutiRule[i].Start();
		}
	}
	
	private void StopHansokuAnimation() {
//		m_animMutiRule.Stop();
	}
	
	private void SetKakumeiAnimation_set() {
//		m_animMutiRule.Create( this, (char*)"E_1_kakumeiAni_1", FALSE );
//		m_animMutiRule.ChangeCallBackType(CALLBACK_LAST_FRM, ANIM_KAKUMEI_SET);
//		m_animMutiRule.Start();
		
		int	i;
		int	nImgCount = 4;
		for (i = 0; i < nImgCount; i++) {
			m_animMutiRule[i] = new CAnim83();
		}

		int pPtList[][][] = new int [][][] {
                {	{-640,  98}, {   0,  98}, {   0,  98}, {   0,  98}, {   0,  98},
                	{   0,  98}, {   0,  98}, {   0,  98}, {   0,  98}, {   0,  98},
                	{   0,  98}, {   0,  98}, {   0,  98}, {   0,  98}, { 640,  98}, },
                	
                {	{-300, 128}, {   0, 128}, {   0, 128}, {   0, 128}, {   0, 128},
                	{   0, 128}, {   0, 128}, {   0, 128}, {   0, 128}, {   0, 128},
                	{   0, 128}, {   0, 128}, {   0, 128}, {   0, 128}, {-300, 128}, },
                	
                {	{-200, 160}, {-200, 160}, { 240, 160}, { 240, 160}, { 240, 160},
            		{ 240, 160}, { 240, 160}, { 240, 160}, { 240, 160}, { 240, 160},
            		{ 240, 160}, { 240, 160}, { 240, 160}, { 240, 160}, { 640, 160}, },
                	
                {	{-200, 160}, {-200, 160}, { 400, 160}, { 400, 160}, { 400, 160},
        			{ 400, 160}, { 400, 160}, { 400, 160}, { 400, 160}, { 400, 160},
        			{ 400, 160}, { 400, 160}, { 400, 160}, { 400, 160}, { 640, 160}, },
		};
		m_animMutiRule[0].Create( this, CAnim83.CREATE_ANIM_LINE_ACCELER, img_kakumeiBg_1,   pPtList[0], pPtList[0].length, 20, 75, 0 );
		m_animMutiRule[1].Create( this, CAnim83.CREATE_ANIM_LINE_ACCELER, img_kakumeiCharaL, pPtList[1], pPtList[1].length, 20, 75, 0 );
		m_animMutiRule[2].Create( this, CAnim83.CREATE_ANIM_LINE_ACCELER, img_kakumei_1_1,   pPtList[2], pPtList[2].length, 20, 75, 0 );
		m_animMutiRule[3].Create( this, CAnim83.CREATE_ANIM_LINE_ACCELER, img_kakumei_1_2,   pPtList[3], pPtList[3].length, 20, 75, 0 );
		
		m_animMutiRule[0].ChangeCallBackType(CAnimation.CALLBACK_LAST_FRM, ANIM_KAKUMEI_SET, 0);
		
		for (i = 0; i < nImgCount; i++) {
			m_animMutiRule[i].SetDrawBylib(false);
			m_animMutiRule[i].Start();
		}
	}
	private void SetKakumeiAnimation_free() {
//		m_animMutiRule.Create( this, (char*)"E_1_kakumeiAni_2", FALSE );
//		m_animMutiRule.ChangeCallBackType(CALLBACK_LAST_FRM, ANIM_KAKUMEI_FREE);
//		m_animMutiRule.Start();

		int	i;
		int	nImgCount = 6;
		for (i = 0; i < nImgCount; i++) {
			m_animMutiRule[i] = new CAnim83();
		}

		int pPtList[][][] = new int [][][] {
                {	{ 640,  98}, {   0,  98}, {   0,  98}, {   0,  98}, {   0,  98},
                	{   0,  98}, {   0,  98}, {   0,  98}, {   0,  98}, {   0,  98},
                	{   0,  98}, {   0,  98}, {   0,  98}, {   0,  98}, {-640,  98}, },
                	
                {	{ 652, 128}, { 352, 128}, { 352, 128}, { 352, 128}, { 352, 128},
            		{ 352, 128}, { 352, 128}, { 352, 128}, { 352, 128}, { 352, 128},
            		{ 352, 128}, { 352, 128}, { 352, 128}, { 352, 128}, { 652, 128}, },
                	
                {	{ 640, 160}, { 640, 160}, { -14, 160}, { -14, 160}, { -14, 160},
           			{ -14, 160}, { -14, 160}, { -14, 160}, { -14, 160}, { -14, 160},
           			{ -14, 160}, { -14, 160}, { -14, 160}, { -14, 160}, {-160, 160}, },
                	
                {	{ 640, 160}, { 640, 160}, {  96, 160}, {  96, 160}, {  96, 160},
       				{  96, 160}, {  96, 160}, {  96, 160}, {  96, 160}, {  96, 160},
       				{  96, 160}, {  96, 160}, {  96, 160}, {  96, 160}, {-160, 160}, },

                {	{ 640, 160}, { 640, 160}, { 206, 160}, { 206, 160}, { 206, 160},
       				{ 206, 160}, { 206, 160}, { 206, 160}, { 206, 160}, { 206, 160},
       				{ 206, 160}, { 206, 160}, { 206, 160}, { 206, 160}, {-160, 160}, },
                	
                {	{ 640, 160}, { 640, 160}, { 306, 160}, { 306, 160}, { 306, 160},
   					{ 306, 160}, { 306, 160}, { 306, 160}, { 306, 160}, { 306, 160},
   					{ 306, 160}, { 306, 160}, { 306, 160}, { 306, 160}, {-160, 160}, },
		};
		m_animMutiRule[0].Create( this, CAnim83.CREATE_ANIM_LINE_ACCELER, img_kakumeiBg_2,   pPtList[0], pPtList[0].length, 20, 75, 0 );
		m_animMutiRule[1].Create( this, CAnim83.CREATE_ANIM_LINE_ACCELER, img_kakumeiCharaR, pPtList[1], pPtList[1].length, 20, 75, 0 );
		m_animMutiRule[2].Create( this, CAnim83.CREATE_ANIM_LINE_ACCELER, img_kakumei_2_1,   pPtList[2], pPtList[2].length, 20, 75, 0 );
		m_animMutiRule[3].Create( this, CAnim83.CREATE_ANIM_LINE_ACCELER, img_kakumei_2_2,   pPtList[3], pPtList[3].length, 20, 75, 0 );
		m_animMutiRule[4].Create( this, CAnim83.CREATE_ANIM_LINE_ACCELER, img_kakumei_2_3,   pPtList[4], pPtList[4].length, 20, 75, 0 );
		m_animMutiRule[5].Create( this, CAnim83.CREATE_ANIM_LINE_ACCELER, img_kakumei_2_4,   pPtList[5], pPtList[5].length, 20, 75, 0 );
		
		m_animMutiRule[0].ChangeCallBackType(CAnimation.CALLBACK_LAST_FRM, ANIM_KAKUMEI_FREE, 0);
		
		for (i = 0; i < nImgCount; i++) {
			m_animMutiRule[i].SetDrawBylib(false);
			m_animMutiRule[i].Start();
		}
	}
	
	private void StopKakumeiAnimation() {
//		m_animMutiRule.Stop();
	}
	
	private void SetMiyakootiAnimation() {
//		m_animMutiRule.Create( this, (char*)"E_1_miyakoOtiAni_1", FALSE );
//		m_animMutiRule.ChangeCallBackType(CALLBACK_LAST_FRM, ANIM_MIYAKOOTI);
//		m_animMutiRule.Start();
		
		int	i;
		int	nImgCount = 4;
		for (i = 0; i < nImgCount; i++) {
			m_animMutiRule[i] = new CAnim83();
		}

		int pPtList[][][] = new int [][][] {
                {	{ 146,-480}, { 146,-480}, { 146,-480}, { 146,-480}, { 146,-480},
                	{ 146,   0}, { 146,   0}, { 146,   0}, { 146,   0}, { 146,   0},
                	{ 146,   0}, { 146,   0}, { 146,   0}, { 146,   0}, { 146,   0},
                	{ 146,   0}, { 146,   0}, { 146,   0}, { 146, 240}, { 146, 480}, },
                	
                {	{ 244,-152}, { 244,-152}, { 244,-152}, { 244,-152}, { 244,-152},
                	{ 244,  20}, { 244,  20}, { 244,  20}, { 244,  20}, { 244,  20},
                	{ 244,  20}, { 244,  20}, { 244,  20}, { 244,  20}, { 244,  20},
                	{ 244,  20}, { 244,  20}, { 244,  20}, { 244, 240}, { 244, 480}, },
                	
                {	{ 244,-152}, { 244,-152}, { 244,-152}, { 244,-152}, { 244,-152},
                	{ 244, 164}, { 244, 164}, { 244, 164}, { 244, 164}, { 244, 164},
                	{ 244, 164}, { 244, 164}, { 244, 164}, { 244, 164}, { 244, 164},
                	{ 244, 164}, { 244, 164}, { 244, 164}, { 244, 240}, { 244, 480}, },
                	
                {	{ 244,-152}, { 244,-152}, { 244,-152}, { 244,-152}, { 244,-152},
                	{ 244, 308}, { 244, 308}, { 244, 308}, { 244, 308}, { 244, 308},
                	{ 244, 308}, { 244, 308}, { 244, 308}, { 244, 308}, { 244, 308},
                	{ 244, 308}, { 244, 308}, { 244, 308}, { 244, 240}, { 244, 480}, },
		};
		m_animMutiRule[0].Create( this, CAnim83.CREATE_ANIM_LINE_ACCELER, img_miyakoOtiBg, pPtList[0], pPtList[0].length, 0, 80, 0 );
		m_animMutiRule[1].Create( this, CAnim83.CREATE_ANIM_LINE_ACCELER, img_miyakoOti_1, pPtList[1], pPtList[1].length, 0, 80, 0 );
		m_animMutiRule[2].Create( this, CAnim83.CREATE_ANIM_LINE_ACCELER, img_miyakoOti_2, pPtList[2], pPtList[2].length, 0, 80, 0 );
		m_animMutiRule[3].Create( this, CAnim83.CREATE_ANIM_LINE_ACCELER, img_miyakoOti_3, pPtList[3], pPtList[3].length, 0, 80, 0 );
		
		m_animMutiRule[0].ChangeCallBackType(CAnimation.CALLBACK_LAST_FRM, ANIM_MIYAKOOTI, 0);
		
		for (i = 0; i < nImgCount; i++) {
			m_animMutiRule[i].SetDrawBylib(false);
			m_animMutiRule[i].Start();
		}
	}
	
	private void StopMiyakootiAnimation() {
//		m_animMutiRule.Stop();
	}
	
	private void SetSibariAnimation() {
//		m_animMutiRule.Create( this, (char*)"E_1_sibariAni_1", FALSE );
//		m_animMutiRule.ChangeCallBackType(CALLBACK_LAST_FRM, ANIM_SIBARI);
//		m_animMutiRule.Start();
		
		int	i;
		int	nImgCount = 2;
		for (i = 0; i < nImgCount; i++) {
			m_animMutiRule[i] = new CAnim83();
		}

		int pPtList[][][] = new int [][][] {
                {	{   0, -96}, {   0, -96}, {   0, -96}, {   0, -96}, {   0, -96}, 
                	{   0, 144}, {   0, 144}, {   0, 144}, {   0, 144}, {   0, 144}, 
                	{   0, 144}, {   0, 144}, {   0, 144}, {   0, 144}, {   0, 144}, {   0, 144}, {-640, 146}, },
                	
                {	{   0, 480}, {   0, 480}, {   0, 480}, {   0, 480}, {   0, 480}, 
                	{   0, 240}, {   0, 240}, {   0, 240}, {   0, 240}, {   0, 240},
                	{   0, 240}, {   0, 240}, {   0, 240}, {   0, 240}, {   0, 240}, {   0, 240}, {-640, 146},	},
		};
		m_animMutiRule[0].Create( this, CAnim83.CREATE_ANIM_LINE_ACCELER, img_sibari_1, pPtList[0], pPtList[0].length, 0, 67, 0 );
		m_animMutiRule[1].Create( this, CAnim83.CREATE_ANIM_LINE_ACCELER, img_sibari_2, pPtList[1], pPtList[1].length, 0, 67, 0 );
		
		m_animMutiRule[0].ChangeCallBackType(CAnimation.CALLBACK_LAST_FRM, ANIM_SIBARI, 0);
		
		for (i = 0; i < nImgCount; i++) {
			m_animMutiRule[i].SetDrawBylib(false);
			m_animMutiRule[i].Start();
		}
	}
	
	private void StopSibariAnimation() {
//		m_animMutiRule.Stop();
	}
	
	private void SetSpade3Animation() {
//		m_animMutiRule.Create( this, (char*)"E_1_spade3Ani_1", FALSE );
//		m_animMutiRule.ChangeCallBackType(CALLBACK_LAST_FRM, ANIM_SPADE3);
//		m_animMutiRule.Start();
		
		int	i;
		int	nImgCount = 3;
		for (i = 0; i < nImgCount; i++) {
			m_animMutiRule[i] = new CAnim83();
		}

		int pPtList[][][] = new int [][][] {
                {	{ 200, 120}, { 260, 180}, { 140, 180}, { 136, 180}, { 132, 180}, { 128, 180}, { 124, 180}, { 120, 180}, {-120, 180}, },
                {	{ 200, 120}, { 260, 180}, { 260, 180}, { 260, 180}, { 260, 180}, { 260, 180}, { 260, 180}, { 260, 180}, { 260,-120}, },
                {	{ 200, 120}, { 260, 180}, { 380, 180}, { 384, 180}, { 388, 180}, { 392, 180}, { 396, 180}, { 400, 180}, { 640, 180}, },
		};
		m_animMutiRule[0].Create( this, CAnim83.CREATE_ANIM_LINE_ACCELER, img_spade3_1, pPtList[0], pPtList[0].length, 0, 45, 0 );
		m_animMutiRule[1].Create( this, CAnim83.CREATE_ANIM_LINE_ACCELER, img_spade3_2, pPtList[1], pPtList[1].length, 0, 45, 0 );
		m_animMutiRule[2].Create( this, CAnim83.CREATE_ANIM_LINE_ACCELER, img_spade3_3, pPtList[2], pPtList[2].length, 0, 45, 0 );
		
		m_animMutiRule[0].ChangeCallBackType(CAnimation.CALLBACK_LAST_FRM, ANIM_SPADE3, 0);
		
		for (i = 0; i < nImgCount; i++) {
			m_animMutiRule[i].SetDrawBylib(false);
			m_animMutiRule[i].Start();
		}
	}
	
	private void StopSpade3Animation() {
//		m_animMutiRule.Stop();
	}
	
	private void SetRuleAnimation(int nAnimIndex) {
		SetNPCHandCards();
		SetUserHandCards();
		
		m_bAnimMultiRule = true;
		switch (nAnimIndex)
		{
		case ANIM_8KIRI:		Set8KiriAnimation();		break;
		case ANIM_HANSOKU:		SetHansokuAnimation();		break;
		case ANIM_KAKUMEI_SET:	SetKakumeiAnimation_set();	break;
		case ANIM_KAKUMEI_FREE:	SetKakumeiAnimation_free();	break;
		case ANIM_MIYAKOOTI:	SetMiyakootiAnimation();	break;
		case ANIM_SIBARI:		SetSibariAnimation();		break;
		case ANIM_SPADE3:		SetSpade3Animation();		break;
		case ANIM_NOCARD:		break;
		case ANIM_LOOSE:		break;
		case ANIM_WIN:			break;
		}
	}

	private void StopRuleAnimation(int nAnimIndex) {
		m_bAnimMultiRule = false;
		
//		switch (nAnimIndex)
//		{
//		case ANIM_8KIRI:		Stop8KiriAnimation();		break;
//		case ANIM_HANSOKU:		StopHansokuAnimation();		break;
//		case ANIM_KAKUMEI_SET:	StopKakumeiAnimation();		break;
//		case ANIM_KAKUMEI_FREE:	StopKakumeiAnimation();		break;
//		case ANIM_MIYAKOOTI:	StopMiyakootiAnimation();	break;
//		case ANIM_SIBARI:		StopSibariAnimation();		break;
//		case ANIM_SPADE3:		StopSpade3Animation();		break;
//		case ANIM_NOCARD:		break;
//		case ANIM_LOOSE:		break;
//		case ANIM_WIN:			break;
//		}
		
		int	nImgCount = m_animMutiRule.length;
		
		for (int i = 0; i < nImgCount; i ++) {
			if (m_animMutiRule[i] != null) {
				m_animMutiRule[i].Stop();
				m_animMutiRule[i] = null;
			}
		}
	}

	private void SetResultAnimation(boolean bWin) {
//		String	szAnim;
//		int nCallId;
//		if (bWin) {
//			szAnim = "E_1_gameEndAni_1";
//			nCallId = ANIM_WIN;
//		}
//		else {
//			szAnim = "E_1_gameEndAni_2";
//			nCallId = ANIM_LOOSE;
//		}

//		m_animGameResult.Create( this, (char*)szAnim, FALSE );
//		m_animGameResult.ChangeCallBackType(CALLBACK_LAST_FRM, nCallId);
//		m_animGameResult.Start();

		int	nImgCount = m_animGameResult.length;
		CImgObj		img[] = null;
		
		short nCallId;
		if (bWin) {
			nCallId = ANIM_WIN;
			img = new CImgObj[] {
					img_gameEndWin_01,	img_gameEndWin_02,	img_gameEndWin_03,	img_gameEndWin_04,	img_gameEndWin_05,	img_gameEndWin_06,
					img_gameEndWin_07,	img_gameEndWin_08,	img_gameEndWin_09,	img_gameEndWin_10,	img_gameEndWin_11,	img_gameEndWin_12,
				};
		}
		else {
			nCallId = ANIM_LOOSE;
			img = new CImgObj[] {
					img_gameEndLose_01,	img_gameEndLose_02,	img_gameEndLose_03,	img_gameEndLose_04,	img_gameEndLose_05,	img_gameEndLose_06,
					img_gameEndLose_07,	img_gameEndLose_08,	img_gameEndLose_09,	img_gameEndLose_10,	img_gameEndLose_11,	img_gameEndLose_12,
				};
		}

		for (int i = 0; i < nImgCount; i ++) {
			m_animGameResult[i] = new CAnim83();
		}
		
		int[][][] pPtList = new int [][][] {
			{	{   0, 480},	{   0,   0},	},
			{	{ 640,   0},	{ 160,   0},	},
			{	{ 640,   0},	{ 320,   0},	},
			{	{ 640,   0},	{ 480,   0},	},
			{	{   0, 480},	{   0, 160},	},
			{	{ 160, 480},	{ 160, 160},	},
			{	{ 640, 160},	{ 320, 160},	},
			{	{ 640, 160},	{ 480, 160},	},
			{	{   0, 480},	{   0, 320},	},
			{	{ 160, 480},	{ 160, 320},	},
			{	{ 320, 480},	{ 320, 320},	},
			{	{ 640, 320},	{ 480, 320},	},
		};
		
		m_animGameResult[ 0].Create( this, CAnim83.CREATE_ANIM_LINE_ACCELER, img[ 0], pPtList[ 0], pPtList[ 0].length,  0, 4, 56 );
		m_animGameResult[ 1].Create( this, CAnim83.CREATE_ANIM_LINE_ACCELER, img[ 1], pPtList[ 1], pPtList[ 1].length,  4, 4, 52 );
		m_animGameResult[ 2].Create( this, CAnim83.CREATE_ANIM_LINE_ACCELER, img[ 2], pPtList[ 2], pPtList[ 2].length, 12, 4, 44 );
		m_animGameResult[ 3].Create( this, CAnim83.CREATE_ANIM_LINE_ACCELER, img[ 3], pPtList[ 3], pPtList[ 3].length, 28, 4, 28 );
		m_animGameResult[ 4].Create( this, CAnim83.CREATE_ANIM_LINE_ACCELER, img[ 4], pPtList[ 4], pPtList[ 4].length,  8, 4, 48 );
		m_animGameResult[ 5].Create( this, CAnim83.CREATE_ANIM_LINE_ACCELER, img[ 5], pPtList[ 5], pPtList[ 5].length, 16, 4, 40 );
		m_animGameResult[ 6].Create( this, CAnim83.CREATE_ANIM_LINE_ACCELER, img[ 6], pPtList[ 6], pPtList[ 6].length, 20, 4, 36 );
		m_animGameResult[ 7].Create( this, CAnim83.CREATE_ANIM_LINE_ACCELER, img[ 7], pPtList[ 7], pPtList[ 7].length, 36, 4, 20 );
		m_animGameResult[ 8].Create( this, CAnim83.CREATE_ANIM_LINE_ACCELER, img[ 8], pPtList[ 8], pPtList[ 8].length, 24, 4, 32 );
		m_animGameResult[ 9].Create( this, CAnim83.CREATE_ANIM_LINE_ACCELER, img[ 9], pPtList[ 9], pPtList[ 9].length, 32, 4, 24 );
		m_animGameResult[10].Create( this, CAnim83.CREATE_ANIM_LINE_ACCELER, img[10], pPtList[10], pPtList[10].length, 40, 4, 16 );
		m_animGameResult[11].Create( this, CAnim83.CREATE_ANIM_LINE_ACCELER, img[11], pPtList[11], pPtList[11].length, 44, 4, 12 );
		
		m_animGameResult[0].ChangeCallBackType(CAnimation.CALLBACK_LAST_FRM, nCallId, 0);
		
		for (int i = 0; i < nImgCount; i ++) {
			m_animGameResult[i].SetDrawBylib(false);
			m_animGameResult[i].Start();
		}
	}

	private void SetNoCardAnimation() {
//		char szAnim[32];
//		sprintf(szAnim, "E_1_noCardAni_%d", GetNoCardAnimId());
//		//m_lyt.GetPicturePane("").SetTexture(&m_t2g, )
//		m_animNoCard.Create( this, (char*)szAnim, TRUE );
//		m_animNoCard.ChangeCallBackType(CALLBACK_LAST_FRM, ANIM_NOCARD);
//		m_animNoCard.Start();

		int	idAnim = GetNoCardAnimId();
		int	nImgCount = m_animNoCard.length;
		CImgObj		img[] = new CImgObj[nImgCount];

		for (int i = 0; i < nImgCount; i ++) {
			m_animNoCard[i] = new CAnim83();
		}
		
		switch (idAnim) {
		case 1:		img = new CImgObj[] {	img_noCardBg_1,		img_noCardText_1_1,		img_noCardText_2_1,		};	break;
		case 2:		img = new CImgObj[] {	img_noCardBg_2,		img_noCardText_1_2,		img_noCardText_2_2,		};	break;
		case 3:		img = new CImgObj[] {	img_noCardBg_3,		img_noCardText_1_3,		img_noCardText_2_3,		};	break;
		case 4:		img = new CImgObj[] {	img_noCardBg_4,		img_noCardText_1_4,		img_noCardText_2_4,		};	break;
		case 5:		img = new CImgObj[] {	img_noCardBg_5,		img_noCardText_1_5,		img_noCardText_2_5,		};	break;
		case 6:		img = new CImgObj[] {	img_noCardBg_6,		img_noCardText_1_7,		img_noCardText_2_6,		};	break;
		default:	img = new CImgObj[] {	img_noCardBg_7,		img_noCardText_1_7,		img_noCardText_2_7,		};	break;
		}
		
		int[][][] pPtList = new int [][][] {
				//	0				5
				{	{-304, 528},	{  64, 528},	},
				{	{-304, 536},	{ 172, 536},	{ 184, 536},	{ 184, 528},	{ 178, 540},	{ 188, 540},
					{ 187, 533},	{ 176, 540},	{ 172, 534},	{ 171, 543},	{ 192, 538},	{ 172, 536},	},
				{	{-304, 580},	{ 172, 580},	{ 176, 580},	{ 180, 580},	{ 184, 580},	{ 188, 580},	},
				
				{	{ 900, 3000},	{ 900, 3000},	},
				{	{-304, 560},	{ 172, 560},	{ 176, 560},	{ 180, 560},	{ 184, 560},	{ 188, 560},	},
			};
		if ( idAnim != 6) {
			m_animNoCard[0].Create( this, CAnim83.CREATE_ANIM_LINE_ACCELER, img[0],    pPtList[0], pPtList[0].length,   0,  5, 215 );
			m_animNoCard[1].Create( this, CAnim83.CREATE_ANIM_LINE_ACCELER, img[1],    pPtList[1], pPtList[1].length,  28, 24, 168 );
			m_animNoCard[2].Create( this, CAnim83.CREATE_ANIM_LINE_ACCELER, img[2],    pPtList[2], pPtList[2].length,  90, 12, 118 );
		} else {
			m_animNoCard[0].Create( this, CAnim83.CREATE_ANIM_LINE_ACCELER, img[0],    pPtList[0], pPtList[0].length,   0,  5, 215 );
			m_animNoCard[1].Create( this, CAnim83.CREATE_ANIM_LINE_ACCELER, img[1],    pPtList[3], pPtList[3].length,   0,  5, 215 );
			m_animNoCard[2].Create( this, CAnim83.CREATE_ANIM_LINE_ACCELER, img[2],    pPtList[4], pPtList[4].length,  28, 24, 168 );
		}
		
		m_animNoCard[0].ChangeCallBackType(CAnimation.CALLBACK_LAST_FRM, ANIM_NOCARD, 0);
		
		for (int i = 0; i < nImgCount; i ++) {
			m_animNoCard[i].Start();
		}
	}

	public void StopNoCardAnimation() {
		for (int i = 0; i < m_animNoCard.length; i ++) {
			if (m_animNoCard[i] != null) {
				m_animNoCard[i].Stop();
				m_animNoCard[i] = null;
			}
		}
	}

	private void SetUserMiyakoAnimation() {
//		m_animUserMiyako.Create( this, (char*)"E_1_miyakoOtiAni_2", TRUE );
//		m_animUserMiyako.ChangeCallBackType(CALLBACK_LAST_FRM, ANIM_MIYAKOOTI_DOWN);
//		m_animUserMiyako.Start();
		
		int	i;
		int	nImgCount = m_animUserMiyako.length;
		for (i = 0; i < nImgCount; i++) {
			m_animUserMiyako[i] = new CAnim83();
		}

		int pPtList[][][] = new int [][][] {
                {	{  92, 328}, {  92, 401}, {  92, 474}, {  92, 546}, {  92, 619},
                	{  92, 692}, {  92, 686}, {  92, 679}, {  92, 673}, {  92, 666},
                	{  92, 660}, {  92, 658}, {  92, 656}, {  92, 663}, {  92, 670},
                	{  92, 678}, {  92, 685}, {  92, 692}, {  92, 685}, {  92, 678},
                	{  92, 682}, {  92, 685}, {  92, 689}, {  92, 692}, {  92, 688},
                	{  92, 684}, {  92, 692}, {  92, 688}, {  92, 692}, {  92, 692},	},
                	
                {	{ 244, 328}, { 244, 401}, { 244, 474}, { 244, 546}, { 244, 619},
                	{ 244, 692}, { 244, 686}, { 244, 679}, { 244, 673}, { 244, 666},
                	{ 244, 660}, { 244, 658}, { 244, 656}, { 244, 663}, { 244, 670},
                	{ 244, 678}, { 244, 685}, { 244, 692}, { 244, 685}, { 244, 678},
                	{ 244, 682}, { 244, 685}, { 244, 689}, { 244, 692}, { 244, 688},
                	{ 244, 684}, { 244, 692}, { 244, 688}, { 244, 692}, { 244, 692},	},
                	
                {	{ 396, 328}, { 396, 401}, { 396, 474}, { 396, 546}, { 396, 619},
                	{ 396, 692}, { 396, 686}, { 396, 679}, { 396, 673}, { 396, 666},
                	{ 396, 660}, { 396, 658}, { 396, 656}, { 396, 663}, { 396, 670},
                	{ 396, 678}, { 396, 685}, { 396, 692}, { 396, 685}, { 396, 678},
                	{ 396, 682}, { 396, 685}, { 396, 689}, { 396, 692}, { 396, 688},
                	{ 396, 684}, { 396, 692}, { 396, 688}, { 396, 692}, { 396, 692},	},
		};
		m_animUserMiyako[0].Create( this, CAnim83.CREATE_ANIM_LINE_ACCELER, img_miyakoOti_4, pPtList[0], pPtList[0].length, 0, 30, 30 );
		m_animUserMiyako[1].Create( this, CAnim83.CREATE_ANIM_LINE_ACCELER, img_miyakoOti_5, pPtList[1], pPtList[1].length, 4, 30, 26 );
		m_animUserMiyako[2].Create( this, CAnim83.CREATE_ANIM_LINE_ACCELER, img_miyakoOti_6, pPtList[2], pPtList[2].length, 8, 30, 22 );
		
		m_animUserMiyako[0].ChangeCallBackType(CAnimation.CALLBACK_LAST_FRM, ANIM_MIYAKOOTI_DOWN, 0);
		
		for (i = 0; i < nImgCount; i++) {
			m_animUserMiyako[i].Start();
		}
	}

	private void StopUserMiyakoAnimation() {
		if (m_bMiyakoOti[0] == false)
			return;
		int	nImgCount = m_animUserMiyako.length;
		for (int i = 0; i < nImgCount; i++) {
			if (m_animUserMiyako[i] != null) {
				m_animUserMiyako[i].Stop();
				m_animUserMiyako[i] = null;
			}
		}
	}

	private void SetBattleEffectAnimation(boolean bNPC) {
//		String	szAnim;
//		if (bNPC)
//			szAnim = "E_1_battleEffectAni_1";
//		else
//			szAnim = "E_1_battleEffectAni_2";
//
//		m_animBattleEffect.Create( this, (char*)szAnim, TRUE );
//		m_animBattleEffect.ChangeCallBackType(CALLBACK_LAST_FRM, ANIM_BATTLEEFF);
//		m_animBattleEffect.Start();
		
		int pPtList[][][] = null;
		int	x, y, y0;
		int	nImgCount = 4;
		int	nAniCount = m_animBattleEffect.length;
		int	s;	// start frame
		int	e;	// end frame
		
		if (bNPC)
			y0 = 0;
		else
			y0 = -54;

		int i, j;
		int	pPanePos[][] = new int [][] {
				{ 64, 528},	{112, 544},	{188, 552},	{ 38, 548},	{152, 538},	{ 80, 550},
				{132, 524},	{100, 552},	{160, 560},	{116, 540},	{154, 546},	{ 28, 544},
			};
		int		startFrame[] = new int[] {
				0,    9,  13,  24,  42,  73,
				86, 100, 106,  54,  36,  65,
			};
		
		CImgObj		img[] = new CImgObj[]  {
				img_btEff_01_1,
				img_btEff_01_2,
				img_btEff_01_3,
				img_btEff_01_4,
			};
		for (i = 0; i < nAniCount; i++) {
			m_animBattleEffect[i] = new CAnim83[nImgCount];

			for (j = 0; j < nImgCount; j ++) {
				m_animBattleEffect[i][j] = new CAnim83();
			}
			
			x = pPanePos[i][0];
			y = pPanePos[i][1] + y0;
			s = startFrame[i];
			e = 120 - 14 - s;
			pPtList = new int [][][] {
					{	{x,3000},	{x, y},	{x, y},	{x, y},	{x,3000},	{x,3000},	{x, y},	{x,3000}	},
					{	{x,3000},	{x, y},	{x, y},	{x, y},	{x,3000},	},
					{	{x,3000},	{x, y},	{x, y},	{x, y},	{x,3000},	},
					{	{x,3000},	{x, y},	{x, y},	{x, y},	{x, y},	{x,3000}	},
				};
			m_animBattleEffect[i][0].Create( this, CAnim83.CREATE_ANIM_LINE_ACCELER, img[0], pPtList[0], pPtList[0].length,  s+1, 7, e+6 );
			m_animBattleEffect[i][1].Create( this, CAnim83.CREATE_ANIM_LINE_ACCELER, img[1], pPtList[1], pPtList[1].length,  s+3, 5, e+6 );
			m_animBattleEffect[i][2].Create( this, CAnim83.CREATE_ANIM_LINE_ACCELER, img[2], pPtList[2], pPtList[2].length,  s+6, 5, e+3 );
			m_animBattleEffect[i][3].Create( this, CAnim83.CREATE_ANIM_LINE_ACCELER, img[3], pPtList[3], pPtList[3].length,  s+8, 6, e+0 );
		}
		
		m_animBattleEffect[0][0].ChangeCallBackType(CAnimation.CALLBACK_LAST_FRM, ANIM_BATTLEEFF, 0);
		
		for (i = 0; i < nAniCount; i++) {
			for (j = 0; j < m_animBattleEffect[i].length; j ++) {
				m_animBattleEffect[i][j].Start();
			}
		}
	}

	private void StopBattleEffectAnimation(boolean bNPC) {
		for (int i = 0; i < m_animBattleEffect.length; i++) {
			for (int j = 0; j < m_animBattleEffect[i].length; j ++) {
				if (m_animBattleEffect[i][j] != null) {
					m_animBattleEffect[i][j].Stop();
					m_animBattleEffect[i][j] = null;
				}
			}
			m_animBattleEffect[i] = null;
		}
	}

	private boolean MoveUserDiscardCards() {
		if (Globals.TEST_TOOL == true) {
			if (Globals.m_bAutoGame == true)
				return false;
		}

		int nPlayer = m_pGameCtrl.SS_GetTurn();//m_pGameCtrl.SS_GetTurn();
		if (nPlayer != GameDoc.PLAYER_USER)
			return false;
		
		GameLogic.DISCARD_CARDS_INFO cards[] = mDiscardCards;
		int nCount = m_pGameCtrl.SS_GetDiscardCards(cards);
		if (nCount <= 0)
			return false;
		
		StopNoCardAnimation();
		
		int nCellNo;
		m_nMoveCardCount = cards[nCount-1].nCount;
		m_bMoveCard = true;
		int nIndex;
		
		String	szCardName[] = new String[1];
		int[][] pPtList = null;
		CPoint ptArrayUpPlace[] = new CPoint[2];

		for (int i = 0; i < m_nMoveCardCount; i ++)
		{
			nCellNo = GetCardImageNameFromCard(cards[nCount-1].nCards[i], szCardName);
			m_picMoveCard[i].load(szCardName[0]);
			
			nIndex = GetHandCardsIndex(cards[nCount-1].nCards[i]);
			STD.ASSERT(nIndex != -1);
			
			ptArrayUpPlace[0] = Move_NPCCardStartPos[nPlayer];
			ptArrayUpPlace[1] = GetDiscardPos(nPlayer,cards[nCount-1].nKind, i);

			/*
			m_animDownDiscard[i].CreateAnim( CREATE_ANIM_LINE, pObjDown, ptArrayDownPlace, sizeof(ptArrayDownPlace)/sizeof(CPoint), MOVE_USERFRAME/2 );
			if (i == 0)
				m_animDownDiscard[0].ChangeCallBackType( CAnim83::CALLBACK_LAST_FRM, ANIM_USERCARD_DOWN );
			m_animDownDiscard[i].StartAnimation( this );
			*/
			
			m_animUpDiscard[i] = new CAnim83();
			pPtList = new int [][] {
					{(int)ptArrayUpPlace[0].x*2,  (int)ptArrayUpPlace[0].y*2},
					{(int)ptArrayUpPlace[1].x*2,  (int)ptArrayUpPlace[1].y*2},
			};
			m_animUpDiscard[i].Create( this, CAnim83.CREATE_ANIM_LINE_ACCELER, m_picMoveCard[i],    pPtList, pPtList.length, 0, MOVE_NPCFRAME, 0 );
			if (i == 0) {
				m_animUpDiscard[i].ChangeCallBackType( CAnimation.CALLBACK_LAST_FRM, ANIM_USERCARD_UP, 0 );
			}
			m_animUpDiscard[i].SetDrawBylib(false);
			m_animUpDiscard[i].Start();
		}
		
		PlayGameSE(MySoundManager.se_card);
		return true;	
	}

	private boolean MoveNPCDiscardCards() {
		int nPlayer = m_pGameCtrl.SS_GetTurn();//m_pGameCtrl.SS_GetTurn();
		if (nPlayer == GameDoc.PLAYER_USER)
			return false;
		
		GameLogic.DISCARD_CARDS_INFO cards[] = mDiscardCards;
		int nCount = m_pGameCtrl.SS_GetDiscardCards(cards);
		if (nCount <= 0)
			return false;

		int nCellNo;
		m_nMoveCardCount = cards[nCount-1].nCount;
		m_bMoveCard = true;
		
		String	szCardName[] = new String[1];
		CPoint ptArrayPlace[] = new CPoint[2];
		int[][] pPtList = null;
		
		for (int i = 0; i < cards[nCount-1].nCount; i ++)
		{
			nCellNo = GetCardImageNameFromCard(cards[nCount-1].nCards[i], szCardName);
			m_picMoveCard[i].load(szCardName[0]);
			
			ptArrayPlace[0] = Move_NPCCardStartPos[nPlayer];
			ptArrayPlace[1] = GetDiscardPos(nPlayer,cards[nCount-1].nKind, i);
			
			m_animUpDiscard[i] = new CAnim83();
			pPtList = new int [][] {
				{(int)ptArrayPlace[0].x*2,  (int)ptArrayPlace[0].y*2},
				{(int)ptArrayPlace[1].x*2,  (int)ptArrayPlace[1].y*2},
			};
			m_animUpDiscard[i].Create( this, CAnim83.CREATE_ANIM_LINE_ACCELER, m_picMoveCard[i],    pPtList, pPtList.length, 0, MOVE_NPCFRAME, 0 );
			if (i == 0) {
				m_animUpDiscard[i].ChangeCallBackType( CAnimation.CALLBACK_LAST_FRM, ANIM_NPCCARD, 0 );
			}
			m_animUpDiscard[i].SetDrawBylib(false);
			m_animUpDiscard[i].Start();
		}
		PlayGameSE(MySoundManager.se_card);
		return true;
	}

	private void SetPassAnimation() {
		int nPlayer = m_pGameCtrl.SS_GetTurn();
		m_animPass[nPlayer] = new CAnim83();
		int pPtList[][][] = new int [][][] {
			{	{ 240, 700},	{ 240, 672},	},	// "E_1_passAni_5"
			{	{  80, 374},	{ 110, 374},	},	// "E_1_passAni_1"
			{	{  80, 114},	{ 110, 114},	},	// "E_1_passAni_2"
			{	{ 411, 114},	{ 386, 114},	},	// "E_1_passAni_3"
			{	{ 411, 374},	{ 386, 374},	},	// "E_1_passAni_4"
		};
		
		CImgObj	img[] = new CImgObj[] {
			img_passPlayer,	img_topCharaPass_1,	img_topCharaPass_2,	img_topCharaPass_3,	img_topCharaPass_4,
		};
		
		short	msg[] = new short[] {
			ANIM_PASS0,		ANIM_PASS1,			ANIM_PASS2,			ANIM_PASS3,			ANIM_PASS4,
		};
		
		
		// m_animPass.Create( this, (char*)szAnim, TRUE );
		m_animPass[nPlayer].Create( this, CAnim83.CREATE_ANIM_LINE_ACCELER, img[nPlayer], pPtList[nPlayer], pPtList[nPlayer].length, 0, 6, 24 );
		m_animPass[nPlayer].ChangeCallBackType(CAnimation.CALLBACK_LAST_FRM, msg[nPlayer], 0);
		m_animPass[nPlayer].Start();
	}
	
	private void StopPassAnimation(int nPlayer) {
		if (m_animPass[nPlayer] != null) {
			m_animPass[nPlayer].Stop();
			m_animPass[nPlayer] = null;
		}
	}
	
	private void SetAgariAnimation() {
		int nPlayer = m_pGameCtrl.SS_GetTurn();
		CImgObj	img = null;
		m_animAgari = new CAnim83();
		int[][] pPtList = null;
		
		switch ( nPlayer ) {
		// "E_1_agariAni_1"
		case GameDoc.PLAYER_CPU1:	img = img_topCharaAgari_1;	pPtList = new int [][] {	{  48, 398},	{  48, 368},	};		break;
		// "E_1_agariAni_2"
		case GameDoc.PLAYER_CPU2:	img = img_topCharaAgari_2;	pPtList = new int [][] {	{  48, 138},	{  48, 108},	};		break;
		// "E_1_agariAni_3"
		case GameDoc.PLAYER_CPU3:	img = img_topCharaAgari_3;	pPtList = new int [][] {	{ 382, 138},	{ 382, 108},	};		break;
		// "E_1_agariAni_4"
		case GameDoc.PLAYER_CPU4:	img = img_topCharaAgari_4;	pPtList = new int [][] {	{ 382, 398},	{ 382, 368},	};		break;
		// "E_1_agariAni_5"
		case GameDoc.PLAYER_USER:
		default:					img = img_agariPlayer;		pPtList = new int [][] {	{ 146, 650},	{ 146, 620},	};		break;
		}
		
		// m_animAgari.Create( this, (char*)szAnim, TRUE );
		m_animAgari.Create( this, CAnim83.CREATE_ANIM_LINE_ACCELER, img, pPtList, pPtList.length, 0, 6, 29 );
		m_animAgari.ChangeCallBackType(CAnimation.CALLBACK_LAST_FRM, ANIM_AGARI, 0);
		m_animAgari.Start();
	}
	
	private void StopAgariAnimation() {
		if (m_animAgari != null) {
			m_animAgari.Stop();
			m_animAgari = null;
		}
	}

	/************************** Animation Proc - End *******************/
	private CPoint GetDiscardPos(int nPlace, int nKind, int nIndex) {
		CPoint pos = new CPoint(0, 0);
		if (nPlace >= GameDoc.PLAYER_COUNT || nIndex > 3 || nKind > GameLogic.SEQ_4) {
			STD.ASSERT(false);
			return pos;
		}
			
		switch (nKind)
		{
		case GameLogic.SINGLE:	pos = Discard_SingleCardPos[nPlace];			break;
		case GameLogic.DUAL:	pos = Discard_DoubleCardPos[nPlace][nIndex];	break;
		case GameLogic.TRIPLE:
		case GameLogic.SEQ_3:	pos = Discard_TripleCardPos[nPlace][nIndex];	break;
		case GameLogic.QUAD:
		case GameLogic.SEQ_4:	pos = Discard_QuadCardPos[nIndex];				break;
		}
		return pos;
	}

	private int GetCardImageNameFromCard(int nCard, String szBuf[]) {
		int nIndex, nKind;
		
		nKind = GameLogic.GetCardSign(nCard);
		int nCardNum = GameLogic.GetCardNumber(nCard);
		if (nCardNum == GameLogic.CARD_JOKER) {
			szBuf[0] = "E_1/E1_card_joker.png";
			return GameLogic.CARD_JOKER;
		} else if (nCardNum >= GameLogic.CARD_A) {
			nIndex = nCardNum-GameLogic.CARD_A+1;
		} else {
			nIndex = nCardNum-GameLogic.CARD_3+3;
		}

		switch (nKind) {
		case GameLogic.DIAMOND:	szBuf[0] = String.format("E_1/E1_card_dia_%02d.png",   nIndex);	break;
		case GameLogic.SPADE:	szBuf[0] = String.format("E_1/E1_card_spade_%02d.png", nIndex);	break;
		case GameLogic.HEART:	szBuf[0] = String.format("E_1/E1_card_heart_%02d.png", nIndex);	break;
		case GameLogic.CLUB:	szBuf[0] = String.format("E_1/E1_card_club_%02d.png",  nIndex);	break;
		//case JOKER: nIndex = 0x34; break;
		}

		return nIndex;
	}

	private int GetHandCardsIndex(int nCard) {
		int nIndex = -1;
		HANDCARDS_INFO stHandCards = mHandCards;
		int nHandCount = m_pGameCtrl.SS_GetHandCards(m_pGameCtrl.SS_GetTurn(), stHandCards);
		for (int i = 0; i < nHandCount; i ++)
		{
			if (stHandCards.bSetting[i] == false)
				continue;
			if (stHandCards.nHandCards[i] == nCard)
			{
				nIndex = i;
				break;
			}
		}
		return nIndex;
	}

	private int GetNoCardAnimId() {
		int nChr = m_pGameCtrl.SS_GetPlayerChr(GameDoc.PLAYER_USER)+1;
		int nId = 0;
		switch (nChr)
		{
		case 2:
		case 3:
		case 4:
			nId = 1;
			break;
		case 5:
		case 6:
		case 7:
		case 8:
		case 9:
			nId = 2;
			break;
		case 13:
			nId = 3;
			break;
		case 11:
		case 12:
			nId = 4;
			break;
		case 1:
		case 10:
			nId = 5;
			break;
		case 14:
		case 15:
			nId = 6;
			break;
		case 16:
		case 17:
			nId = 7;
			break;
		}
		//nId -= 1;
		if (nId <1 || nId > 7)
			return -1;
		return nId;
	}

	private int GetSoldiersRate(int nPlayer) {
		int nOffset = (m_nSoldiersOffsetRate[nPlayer] == 0) ? 0 : (m_nSoldiersOffsetRate[nPlayer] - m_nSoldiersAnimFrame);
		int nRate = 100 - m_pGameCtrl.SS_GetSoldiersRate(nPlayer) - nOffset;
		return nRate;
	}

	private boolean IsShowRuleInfo() { 
		return m_bShowRuleInfo;
	}

	private void SetShowRuleInfo(boolean bSet) {
		if (bSet)
		{
			//UnLoadGameMain();
			//WaitVBlankIntr();
			LoadShowRule();
			m_bShowRuleInfo = true;
		}
		else
		{
			UnLoadShowRule();
			//m_bShowRuleInfo = FALSE;
			//WaitVBlankIntr();
			//LoadGameMain(TRUE);
		}
		UpdateButtonState();
	}

	public void ProcChangeCardMsg() {
//		if (m_nMsgType != WndGame.MSG_EXCHANG || !m_bShowMsg)
//			return;
		
		HideMessage();
		SetTextSoldiers();
		PostMessage(WM_ACT_TO_NEXT);
	}

	public void OnKeyPress( int key ) {
//		if (m_pGameCtrl.SS_IsEnableUserAction())
//		{
//			KeyCommand(key);
//	#ifdef DEBUG_MODE
//		#ifndef DEBUG_MANUAL_GAME
//			if (IsKeyPress(PAD_BUTTON_A) && (key & PAD_BUTTON_X))
//			{
//				#ifdef DEBUG_SHOW_CARDS
//					#undef DEBUG_SHOW_CARDS
//				#else		
//					#define DEBUG_SHOW_CARDS
//				#endif
//			}
//		#endif
//	#endif
//		}	
	}

	private void KeyCommand(int key) {
		/*
		switch (key)
		{
		case PAD_KEY_LEFT:
			m_pGameCtrl.SS_Action(GameLogic.ACT_COMMAND, CMD_LEFT);
			break;
		case PAD_KEY_RIGHT:
			m_pGameCtrl.SS_Action(GameLogic.ACT_COMMAND, CMD_RIGHT);
			break;
		case PAD_KEY_UP:
			m_pGameCtrl.SS_Action(GameLogic.ACT_COMMAND, CMD_UP);
			UpdateButtonState();
			break;
		case PAD_KEY_DOWN:
			m_pGameCtrl.SS_Action(GameLogic.ACT_COMMAND, CMD_DOWN);
			UpdateButtonState();
			break;
		}
		 */
	}


	private long GetElapsedTime( long timePrev )
	{
		long	time_base = 0;
		long	timeCur = STD.GetTickCount();
		
		time_base = timeCur - timePrev;
		
		return time_base;
	}
	
	public void OnTimer( int nTimerID ) {
		switch (nTimerID) {
		case TM_GAMEEND:
			KillTimer(TM_GAMEEND);
			
			if ( Globals.DEBUG_AUTO_GAME ) {
				PostMessage(WM_ACT_TO_NEXT);
				return;
			}
			if ( Globals.TEST_TOOL ) {
				if (Globals.m_bAutoGame) {
					PostMessage(WM_ACT_TO_NEXT);
					return;
				}
			}
			PostMessage(WM_EXIT_FROM_FREEMODE);
			break;
		case TM_SOLDIERS:
			{
				int nPlayer = (m_nSoldiersOffsetRate[0] != 0) ? 0 : 1;
				int nMax = m_nSoldiersOffsetRate[nPlayer];
				m_nSoldiersAnimFrame ++;
				SetSoldierBarSize();
				if (m_nSoldiersAnimFrame > nMax || GetSoldiersRate(nPlayer) >= 100)
				{
					KillTimer(TM_SOLDIERS);
					//StopEM();
					if ( Globals.g_Global.GetSE() ) {
						long nTick = GetElapsedTime(m_nMeterSeStartTime);
						if (nTick < 2000) {
							STD.sleep(2000-nTick);
						}
						Globals.stopSEEx();
					}
						
					InitSoldiersInfo();
					SetTimer(TM_WAIT, 2000, this);
					//Sleep(2000);
					//PostMessage(WM_ACT_TO_NEXT);
				}
			}
			break;
		case TM_WAIT:
			{
				KillTimer(TM_WAIT);
				int nState = m_pGameCtrl.SS_GetState();
				if (nState == GameLogic.STATE_RESULT)
					PostMessage(WM_ACT_TO_NEXT);
				else if (nState == GameLogic.STATE_READY_RESTART)
					SetStartDownAnimation(false);
				else 
					STD.ASSERT(false);
			}
			break;
// by hrh 2011-0510
//		case TM_EXCHANGECARD:
//			m_nMsgFrame ++;
//			if (m_nMsgFrame >= 2)
//			{
//				KillTimer(TM_EXCHANGECARD);
//				ProcChangeCardMsg();
//			}
//			break;
		}
	}

	public void OnAnimEvent( int nAnimID ) {
		switch (nAnimID)
		{
		case ANIM_START_UP:
			SetStopUpAnimation();
			
			SetStartDownAnimation(true);
			//UnLoadStartAnimRes();
			//PostMessage(WM_ACT_TO_NEXT);
			break;
		case ANIM_START_DOWN:
			SetStopDownAnimation();
			
			PlayGameBGM(MySoundManager.bgm_game);
			m_bShowHandCards = true;
			UpdateScreenInfo();
			UnLoadStartAnimRes(true);
			PostMessage(WM_ACT_TO_NEXT);
			break;
		case ANIM_RESTART_DOWN:
			UnLoadStartAnimRes(false);
			SetChrPltStartDownAnim(false);
			PostMessage(WM_ACT_TO_NEXT);
			break;
		case ANIM_PASS0:
			StopPassAnimation(0);
			PostMessage(WM_ACT_TO_NEXT);
			break;
		case ANIM_PASS1:
			StopPassAnimation(1);
			PostMessage(WM_ACT_TO_NEXT);
			break;
		case ANIM_PASS2:
			StopPassAnimation(2);
			PostMessage(WM_ACT_TO_NEXT);
			break;
		case ANIM_PASS3:
			StopPassAnimation(3);
			PostMessage(WM_ACT_TO_NEXT);
			break;
		case ANIM_PASS4:
			StopPassAnimation(4);
			PostMessage(WM_ACT_TO_NEXT);
			break;
		case ANIM_NPCCARD:		
			for (int i = 0; i < m_nMoveCardCount; i ++)
			{
				// m_picMoveCard[i].Unload();
				m_animUpDiscard[i].Stop();
				m_animUpDiscard[i] = null;
			}			
			m_bMoveCard = false;
			m_nMoveCardCount = 0;
			//SetNPCHandCards();
			//SetUserHandCards();
			SetDiscardCards();
			PostMessage(WM_ACT_TO_NEXT);		
			break;
// by hrh 2011-0510
//		case ANIM_USERCARD_DOWN:
//			for (int i = 0; i < m_nMoveCardCount; i ++)
//			{
//				//m_animDownDiscard[i].Stop();
//				m_animUpDiscard[i].Start();
//			}
//			break;
		case ANIM_USERCARD_UP:		
			for (int i = 0; i < m_nMoveCardCount; i ++)
			{
				// m_picMoveCard[i].Unload();
				m_animUpDiscard[i].Stop();
				m_animUpDiscard[i] = null;
			}
			m_bMoveCard = false;
			m_nMoveCardCount = 0;
			SetNPCHandCards();
			SetUserHandCards();
			SetDiscardCards();
			PostMessage(WM_ACT_TO_NEXT);		
			break;
		case ANIM_AGARI:
			StopAgariAnimation();
			PostMessage(WM_ACT_TO_NEXT);
			break;
		case ANIM_8KIRI:
			StopRuleAnimation(ANIM_8KIRI);
			UnLoadRule(CUTIN_KAKUMEI);
			PostMessage(WM_ACT_TO_NEXT);
			break;
		case ANIM_HANSOKU:
			StopRuleAnimation(ANIM_HANSOKU);
			UnLoadRule(CUTIN_HANSOKU);
			//Sleep(400);
			PlayGameBGM(MySoundManager.bgm_game);
			m_bUserHansoku = false;
			int nTurn = m_pGameCtrl.SS_GetTurn();
			m_bHansoku[nTurn] = true;
			//Globals.g_docSuspend.SetHansoku(m_pGameCtrl.SS_GetTurn(), TRUE);
			PostMessage(WM_ACT_TO_NEXT);
			break;
		case ANIM_KAKUMEI_SET:
			StopRuleAnimation(ANIM_KAKUMEI_SET);
			UnLoadRule(CUTIN_KAKUMEI);
			m_bSetRevolution = true;
			PostMessage(WM_ACT_TO_NEXT);
			break;
		case ANIM_KAKUMEI_FREE:
			StopRuleAnimation(ANIM_KAKUMEI_FREE);
			UnLoadRule(CUTIN_KAKUMEI);
			m_bSetRevolution = false;
			PostMessage(WM_ACT_TO_NEXT);
			break;
		case ANIM_MIYAKOOTI:
			{
				StopRuleAnimation(ANIM_MIYAKOOTI);
				UnLoadRule(CUTIN_MIYAKOOTI);
				int nPlayer = m_pGameCtrl.SS_GetPlayerFromKind(GameLogic.DAIFUGOU); 
				m_bMiyakoOti[nPlayer] = true;
				//Globals.g_docSuspend.SetMiyakoOti(nPlayer, TRUE);
				if (nPlayer == GameDoc.PLAYER_USER)
				{
					if (m_bHansoku[GameDoc.PLAYER_USER])
					{
						int k = 0;
						k ++;
						//int nTurn = m_pGameCtrl.SS_GetTurn();
					}
					//ASSERT(m_bHansoku[GameDoc.PLAYER_USER] == FALSE);
					SetUserMiyakoAnimation();
				}			
				else
				{
					//Sleep(400);
					PlayGameBGM(MySoundManager.bgm_game);
					PostMessage(WM_ACT_TO_NEXT);
				}			
			}
			break;
		case ANIM_MIYAKOOTI_DOWN:
			PlayGameBGM(MySoundManager.bgm_game);
			PostMessage(WM_ACT_TO_NEXT);
			break;
		case ANIM_SPADE3:
			StopRuleAnimation(ANIM_SPADE3);
			UnLoadRule(CUTIN_SPADE3);
			PostMessage(WM_ACT_TO_NEXT);
			break;
		case ANIM_SIBARI:
			StopRuleAnimation(ANIM_SIBARI);
			UnLoadRule(CUTIN_SIBARI);
			PostMessage(WM_ACT_TO_NEXT);
			break;
		case ANIM_NOCARD:
			StopNoCardAnimation();
			//PostMessage(WM_ACT_TO_NEXT);
			break;
		case ANIM_WIN:
			PlayGameSEEx(MySoundManager.jingle_win);
			OnGameEnd(true);		
			break;
		case ANIM_LOOSE:
			PlayGameSEEx(MySoundManager.jingle_lose);
			OnGameEnd(false);
			break;
		case ANIM_BATTLEEFF:
			{
				StopBattleEffectAnimation(m_nSoldiersOffsetRate[1] == 0);
				SetTextSoldiers();
				SetTimer(TM_SOLDIERS, 50, this);
				// PlayGameSEEx(MySoundManager.se_meter);
				PlayGameSE(MySoundManager.se_meter);
				m_nMeterSeStartTime = STD.GetTickCount();
			}
			break;
		}
	}

	public GameLogic GetGameLogic() {	
		return m_pGameCtrl;
	}
	private int GetState() {
		return m_pGameCtrl.SS_GetState();
	}

	public void OnChangeState(int nState) {
		switch (nState)
		{
		case GameLogic.STATE_NONE:														break;
		case GameLogic.STATE_RULE:					ChangeState_Rule();					break;
		case GameLogic.STATE_INITIALIZE:			ChangeState_Initialize();			break;
		case GameLogic.STATE_DOING_DEAL:												break;
		case GameLogic.STATE_INITIAL_ATFIRST:		ChangeState_Initial_AtFirst();		break;
		case GameLogic.STATE_CHOICE_ATFIRST:		ChangeState_Choice_AtFirst();		break;
		case GameLogic.STATE_NPC_CHOICE_ATFIRST:	ChangeState_NPC_Choice_AtFirst();	break;
		case GameLogic.STATE_AFTER_ATFIRST:			ChangeState_After_AtFirst();		break;
		case GameLogic.STATE_INITIAL_CHOOSING:		ChangeState_InitialChoosing();		break;
		case GameLogic.STATE_CHOOSING:				ChangeState_Choosing();				break;
		case GameLogic.STATE_CHOOSING_ERROR:		ChangeState_ChoosingError();		break;
		case GameLogic.STATE_NPC_CHOOSING:			ChangeState_NPCChoosing();			break;
		case GameLogic.STATE_ACTION_CHOOSING:											break;
		case GameLogic.STATE_ACTION_DISCARD:		ChangeState_ActDiscard();			break;
		case GameLogic.STATE_ACTION_PASS:			ChangeState_ActPass();				break;
		case GameLogic.STATE_ACTION_EXCHANGE:											break;
		case GameLogic.STATE_UPDATE_PLAYER:			ChangeState_UpdatePlayer();			break;
		case GameLogic.STATE_INITIAL_NAGASI:											break;
		case GameLogic.STATE_NAGASI:													break;
		case GameLogic.STATE_AGARI:					ChangeState_Agari();				break;
		case GameLogic.STATE_8KIRI:					ChangeState_8Kiri();				break;
		case GameLogic.STATE_HASOKU:				ChangeState_Hasoku();				break;
		case GameLogic.STATE_REVOL_SET:				ChangeState_RevolSet();				break;
		case GameLogic.STATE_REVOL_FREE:			ChangeState_RevolFree();			break;
		case GameLogic.STATE_MIYAKOOTI:				ChangeState_MiyakoOti();			break;
		case GameLogic.STATE_SIBARI:				ChangeState_Sibari();				break;
		case GameLogic.STATE_SPADE3:				ChangeState_Spade3();				break;
		case GameLogic.STATE_NOCARD:				ChangeState_NoCard();				break;
		case GameLogic.STATE_RESULT:				ChangeState_Result();				break;
		case GameLogic.STATE_READY_NEXTGAME:		ChangeState_ReadyNextGame();		break;
		case GameLogic.STATE_READY_EXCHANGE:		ChangeState_ReadyExchange();		break;
		case GameLogic.STATE_READY_RESTART:			ChangeState_ReadyRestart();			break;
		case GameLogic.STATE_WIN:					ChangeState_Win();					break;
		case GameLogic.STATE_LOOSE:					ChangeState_Loose();				break;
		case GameLogic.STATE_GAME_END:				ChangeState_GameEnd();				break;
		}
		m_nGameCommand = -1;
		UpdateButtonState();
		if (m_pGameCtrl.SS_IsEnableUserAction() && m_pGameCtrl.SS_GetTurn() == GameDoc.PLAYER_USER) {
			if (m_pGameCtrl.SS_GetState() != GameLogic.STATE_CHOICE_ATFIRST) {
				Globals.g_docSuspend.GetGameKifuFromEngine(m_pGameCtrl, true);
				for (int i = 0; i < GameDoc.PLAYER_COUNT; i ++) {
					Globals.g_docSuspend.SetHansoku(i, m_bHansoku[i]);
					Globals.g_docSuspend.SetMiyakoOti(i, m_bMiyakoOti[i]);
				}
			}
		}
		DBGLog_ChangeState(nState);
	}

	private void ChangeState_Rule() {
		if (m_bContinueGame || m_bSuspendGame)
		{
			PlayGameBGM(MySoundManager.bgm_game);
			PostMessage(WM_ACT_TO_NEXT);
		}
		else
		{
			PlayGameSEEx(MySoundManager.jingle_game_start);
			SetStartUpAnimation();
		}
	}


	private void ChangeState_Initialize() {
		UnLoadHansoku();
		InitGame(true);
		LoadNPCChrs();
		ReLoadUserChr(FACE_NORMAL);
		UpdateScreenInfo();
		PostMessage(WM_ACT_TO_NEXT);
	}

	private void ChangeState_Initial_AtFirst() {
		PostMessage(WM_ACT_TO_NEXT);
	}

	private void ChangeState_Choice_AtFirst() {
		//PostMessage(WM_ACT_TO_NEXT);
	}

	private void ChangeState_NPC_Choice_AtFirst() {
		PostMessage(WM_ACT_TO_NEXT);
	}

	private void ChangeState_After_AtFirst() {
		StopNoCardAnimation();
		PostMessage(WM_ACT_TO_NEXT);
	}

	private void ChangeState_InitialChoosing() {
		PostMessage(WM_ACT_TO_NEXT);
	}

	private void ChangeState_Choosing() {
	}

	private void ChangeState_ChoosingError() {
		StopNoCardAnimation();
		
		PlayGameSE(MySoundManager.se_error);
		SetNoCardAnimation();
		PostMessage(WM_ACT_TO_NEXT);
	}

	private void ChangeState_NPCChoosing() {
		PostMessage(WM_ACT_TO_NEXT);
	}

	private void ChangeState_ActChoosing() {
		PostMessage(WM_ACT_TO_NEXT);
	}

	private void ChangeState_ActDiscard() {
		if (MoveNPCDiscardCards() == false)
		{
			if (MoveUserDiscardCards() == false)
				PostMessage(WM_ACT_TO_NEXT);	
		}	
	}

	private void ChangeState_ActPass() {
		SetPassAnimation();
		
		StopNoCardAnimation();
	}

	private void ChangeState_UpdatePlayer() {
		SetNPCHandCards();
		SetUserHandCards();
		//SetDiscardCards();
		PostMessage(WM_ACT_TO_NEXT);
	}

	private void ChangeState_Agari() {
		SetAgariAnimation();
		ChangePlayerFace(m_pGameCtrl.SS_GetTurn(), FACE_GOOD);
//		PostMessage(WM_ACT_TO_NEXT);
	}

	private void ChangeState_8Kiri() {
		LoadRule(CUTIN_8KIRI);
		// PlayGameSEEx(MySoundManager.se_special);
		PlayGameSE(MySoundManager.se_special);
		SetRuleAnimation(ANIM_8KIRI);
//		PostMessage(WM_ACT_TO_NEXT);
	}

	private void ChangeState_Hasoku() {
		LoadRule(CUTIN_HANSOKU);
		PlayGameSEEx(MySoundManager.jingle_penalty);
		SetRuleAnimation(ANIM_HANSOKU);
		ChangePlayerFace(m_pGameCtrl.SS_GetTurn(), FACE_BAD);
//		PostMessage(WM_ACT_TO_NEXT);
	}

	private void ChangeState_RevolSet() {
		LoadRule(ANIM_KAKUMEI_SET);
		PlayGameSE(MySoundManager.se_revolution);
		SetRuleAnimation(ANIM_KAKUMEI_SET);
	/*
		m_bSetRevolution = TRUE;
		PostMessage(WM_ACT_TO_NEXT);
		*/
	}

	private void ChangeState_RevolFree() {
		LoadRule(ANIM_KAKUMEI_FREE);
		PlayGameSE(MySoundManager.se_revolution);
		SetRuleAnimation(ANIM_KAKUMEI_FREE);
		/*
		m_bSetRevolution = FALSE;
		PostMessage(WM_ACT_TO_NEXT);
		*/
	}

	private void ChangeState_MiyakoOti() {
		LoadRule(CUTIN_MIYAKOOTI);
		PlayGameSEEx(MySoundManager.jingle_penalty);
		SetRuleAnimation(ANIM_MIYAKOOTI);
		ChangePlayerFace(m_pGameCtrl.SS_GetPlayerFromKind(GameLogic.DAIFUGOU), FACE_BAD);
//		PostMessage(WM_ACT_TO_NEXT);
	}

	private void ChangeState_Sibari() {
		LoadRule(CUTIN_SIBARI);
		// PlayGameSEEx(MySoundManager.se_special);
		PlayGameSE(MySoundManager.se_special);
		SetRuleAnimation(ANIM_SIBARI);
//		PostMessage(WM_ACT_TO_NEXT);
	}

	private void ChangeState_Spade3() {
		LoadRule(CUTIN_SPADE3);
		SetRuleAnimation(ANIM_SPADE3);
//		PostMessage(WM_ACT_TO_NEXT);
	}

	private void ChangeState_NoCard() {
		PostMessage(WM_ACT_TO_NEXT);
	}

	private void ChangeState_Result() {
		//m_pGameCtrl.SS_Action(ACT_RESTART_GAME);
		//InitGame();
		StopUserMiyakoAnimation();
		UpdateGameResult();
		Globals.g_Global.SaveGameResult(true);
		
		if (Globals.g_Global.GetGameMode() == Globals.GM_UNIFY) {
			int nFace = FACE_NORMAL;
			for (int i = 0; i < GameDoc.PLAYER_COUNT; i ++) {
				
				switch (m_pGameCtrl.SS_GetPlayerKind(i)) {
				case GameLogic.DAIFUGOU:
				case GameLogic.FUGOU:		nFace = FACE_GOOD;		break;
				case GameLogic.HEIMIN:		nFace = FACE_NORMAL;	break;
				case GameLogic.HINMIN:
				case GameLogic.DAIHINMIN:	nFace = FACE_BAD;		break;
				}
				ChangePlayerFace(i, nFace);
			}	
				
			for (int i = 0; i < 2; i ++) {
				m_nSoldiersOffsetRate[i] = m_nPrevSoldiersRate[i] - m_pGameCtrl.SS_GetSoldiersRate(i);
			}
			SetBattleEffectAnimation(m_nSoldiersOffsetRate[1] == 0);
			PlayGameSE(MySoundManager.se_attack2);
			/*
			SetTextSoldiers();
			SetTimer(TM_SOLDIERS, 50, this);
			 */
		}
		else {
			PostMessage(WM_ACT_TO_NEXT);
		}
	}

	private void ChangeState_ReadyNextGame() {
//		int nRet = AfxMessageBox("", MB_NONE);
		ShowMessage(WndGame.MSG_NEXTGAME);
	}

	private void ChangeState_ReadyExchange() {
//		int nRet = AfxMessageBox("", MB_NONE);
		ShowMessage(WndGame.MSG_EXCHANG);
		m_nMsgFrame = 0;
		//kgh SetTimer(TM_EXCHANGECARD, 2000, this);
	}

	private void ChangeState_ReadyRestart() {
		SetChrPltStartDownAnim(true);
		SetTimer(TM_WAIT, 2000, this);
	}


	private void ChangeState_Win() {
		LoadResult(true);
		SetResultAnimation(true);
//		PostMessage(WM_ACT_TO_NEXT);
	}

	private void ChangeState_Loose() {
		LoadResult(false);
		SetResultAnimation(false);
//		PostMessage(WM_ACT_TO_NEXT);
	}

	private void ChangeState_GameEnd() {
		//save proc
		SetTimer(TM_GAMEEND, 1000, this);
	}

	private void UpdateGameResult() {
		Globals.RESULT_INFO info = m_pGameCtrl.SS_GetGameResult();
		for (int i = 0; i < Globals.RESULT_COUNT; i ++)
			Globals.g_Global.AddGameResultVal(i, info.getVals(i));	
	}
	/////////////////////debug///////////////////////////
	private int GetNPCHandCardStartPosX(int nPlace, int nCount) {
		int x = 0;
		if (nPlace <= 2)
			x = 8;
		else
			x = Common.CODE_WIDTH - CARD_WIDTH - (HandNPCCard_X*nCount) + 28;
		
		return x;
	}

	private boolean IsAutoGame() {
		if ( Globals.TEST_TOOL ) {
			return Globals.m_bAutoGame;
		} else {
			return false;
		}
	}
	private void DBGLog_ChangeState(int nState) {
		if ( !Globals.DEBUG_LOG )
			return;
		
		int	nTurn = m_pGameCtrl.SS_GetTurn();
		String	strLog = String.format("Change State (%d): %s\n", nTurn, GameLogic.m_debug_szState[nState]);
		STD.logout(strLog);
	}
	
	private CImgObj getUserHandCards(int index) {
		CImgObj	img;
		
		switch (index) {
		case 0:		img = img_card_00;	break;
		case 1:		img = img_card_01;	break;
		case 2:		img = img_card_02;	break;
		case 3:		img = img_card_03;	break;
		case 4:		img = img_card_04;	break;
		case 5:		img = img_card_05;	break;
		case 6:		img = img_card_06;	break;
		case 7:		img = img_card_07;	break;
		case 8:		img = img_card_08;	break;
		case 9:		img = img_card_09;	break;
		default:	img = img_card_10;	break;
		}
		
		return img;
	}
	
	private CImgObj getCpuHandCards(int nPlace, int count, int index) {
		int		nCardNo = 15+nPlace;
		switch (count) {
			case GameLogic.SINGLE:	nCardNo = 15+nPlace;			break;
			case GameLogic.DUAL:	nCardNo = 20+nPlace*2+index;	break;
			case GameLogic.TRIPLE:
			case GameLogic.SEQ_3:	nCardNo = 30+nPlace*3+index;	break;
			case GameLogic.QUAD:
			case GameLogic.SEQ_4:	nCardNo = 45+index;				break;
		}
		
		CImgObj	img;
		switch (nCardNo) {
		case 15:	img = img_card_15;	break;
		case 16:	img = img_card_16;	break;
		case 17:	img = img_card_17;	break;
		case 18:	img = img_card_18;	break;
		case 19:	img = img_card_19;	break;
		
		case 20:	img = img_card_20;	break;
		case 21:	img = img_card_21;	break;
		case 22:	img = img_card_22;	break;
		case 23:	img = img_card_23;	break;
		case 24:	img = img_card_24;	break;
		case 25:	img = img_card_25;	break;
		case 26:	img = img_card_26;	break;
		case 27:	img = img_card_27;	break;
		case 28:	img = img_card_28;	break;
		case 29:	img = img_card_29;	break;
		
		case 30:	img = img_card_30;	break;
		case 31:	img = img_card_31;	break;
		case 32:	img = img_card_32;	break;
		case 33:	img = img_card_33;	break;
		case 34:	img = img_card_34;	break;
		case 35:	img = img_card_35;	break;
		case 36:	img = img_card_36;	break;
		case 37:	img = img_card_37;	break;
		case 38:	img = img_card_38;	break;
		case 39:	img = img_card_39;	break;
		case 40:	img = img_card_40;	break;
		case 41:	img = img_card_41;	break;
		case 42:	img = img_card_42;	break;
		case 43:	img = img_card_43;	break;
		case 44:	img = img_card_44;	break;
		
		case 45:	img = img_card_45;	break;
		case 46:	img = img_card_46;	break;
		case 47:	img = img_card_47;	break;
		default:	img = img_card_48;	break;
		}
		return img;
	}
	
	private CImgObj getImgLifeNum(int nType, int index) {
		CImgObj	img;
		switch (nType) {
		case 1:
			switch (index) {
			case 1:		img = img_lifeNum_1_1;		break;
			case 2:		img = img_lifeNum_1_2;		break;
			case 3:		img = img_lifeNum_1_3;		break;
			case 4:		img = img_lifeNum_1_4;		break;
			default:	img = img_lifeNum_1_5;		break;
			}
			break;
		case 2:
			switch (index) {
			case 1:		img = img_lifeNum_2_1;		break;
			case 2:		img = img_lifeNum_2_2;		break;
			case 3:		img = img_lifeNum_2_3;		break;
			case 4:		img = img_lifeNum_2_4;		break;
			default:	img = img_lifeNum_2_5;		break;
			}
			break;
		case 3:
			switch (index) {
			case 1:		img = img_lifeNum_3_1;		break;
			case 2:		img = img_lifeNum_3_2;		break;
			case 3:		img = img_lifeNum_3_3;		break;
			case 4:		img = img_lifeNum_3_4;		break;
			default:	img = img_lifeNum_3_5;		break;
			}
			break;
		default:
			switch (index) {
			case 1:		img = img_lifeNum_4_1;		break;
			case 2:		img = img_lifeNum_4_2;		break;
			case 3:		img = img_lifeNum_4_3;		break;
			case 4:		img = img_lifeNum_4_4;		break;
			default:	img = img_lifeNum_4_5;		break;
			}
			break;
		}
		return img;
	}

	// --------------top------------//
	CImgObj	img_COMN_bg_01_top;
	//battle count
	CImgObj	img_E1_batleCountBg;		
	CImgObj	img_E1_battleNum_3;
	CImgObj	img_E1_battleNum_2;
	CImgObj	img_E1_battleNum_1;
	//character
	CImgObj	img_topChara_1;
	CImgObj	img_topChara_2;
	CImgObj	img_topChara_3;
	CImgObj	img_topChara_4;
	//cardinfo
	CImgObj	img_cardInfo_1;
	CImgObj	img_cardInfo_2;
	CImgObj	img_cardInfo_3;
	CImgObj	img_cardInfo_4;
	//status Hansoku
	CImgObj	img_statusHansoku_1;
	CImgObj	img_statusHansoku_2;
	CImgObj	img_statusHansoku_3;
	CImgObj	img_statusHansoku_4;
	//status MiyakoOti
	CImgObj	img_statusMiyako_1;
	CImgObj	img_statusMiyako_2;
	CImgObj	img_statusMiyako_3;
	CImgObj	img_statusMiyako_4;
	//chara pass
	CImgObj	img_topCharaPass_1;
	CImgObj	img_topCharaPass_2;
	CImgObj	img_topCharaPass_3;
	CImgObj	img_topCharaPass_4;
	//agari
	CImgObj	img_topCharaAgari_1;
	CImgObj	img_topCharaAgari_2;
	CImgObj	img_topCharaAgari_3;
	CImgObj	img_topCharaAgari_4;
	//hansoku top
	CImgObj	img_hansokuTop;
	//8kiri
	CImgObj	img_8kiri_1;
	CImgObj	img_8kiri_2;
	//sibari
	CImgObj	img_sibari_1;
	CImgObj	img_sibari_2;
	//spade3
	CImgObj	img_spade3_1;
	CImgObj	img_spade3_2;
	CImgObj	img_spade3_3;
	//kakumei
	CImgObj	img_kakumeiBg_1;
	CImgObj	img_kakumeiCharaL;
	CImgObj	img_kakumei_1_1;
	CImgObj	img_kakumei_1_2;
	CImgObj	img_kakumeiBg_2;
	CImgObj	img_kakumeiCharaR;
	CImgObj	img_kakumei_2_1;
	CImgObj	img_kakumei_2_2;
	CImgObj	img_kakumei_2_3;
	CImgObj	img_kakumei_2_4;
	//miyakoOti
	CImgObj	img_miyakoOtiBg;
	CImgObj	img_miyakoOti_1;
	CImgObj	img_miyakoOti_2;
	CImgObj	img_miyakoOti_3;
	//game start		
	CImgObj	img_gameStartBg;
	CImgObj	img_gameStartChara;
	CImgObj	img_gameStartText;
	//game end win
	CImgObj	img_gameEndWin_01;
	CImgObj	img_gameEndWin_02;
	CImgObj	img_gameEndWin_03;
	CImgObj	img_gameEndWin_04;
	CImgObj	img_gameEndWin_05;
	CImgObj	img_gameEndWin_06;
	CImgObj	img_gameEndWin_07;
	CImgObj	img_gameEndWin_08;
	CImgObj	img_gameEndWin_09;
	CImgObj	img_gameEndWin_10;
	CImgObj	img_gameEndWin_11;
	CImgObj	img_gameEndWin_12;
	//game end lose
	CImgObj	img_gameEndLose_01;
	CImgObj	img_gameEndLose_02;
	CImgObj	img_gameEndLose_03;
	CImgObj	img_gameEndLose_04;
	CImgObj	img_gameEndLose_05;
	CImgObj	img_gameEndLose_06;
	CImgObj	img_gameEndLose_07;
	CImgObj	img_gameEndLose_08;
	CImgObj	img_gameEndLose_09;
	CImgObj	img_gameEndLose_10;
	CImgObj	img_gameEndLose_11;
	CImgObj	img_gameEndLose_12;
	// --------------bottom------------//
	CImgObj	img_COMN_bg_01_btom;
	// underBtn
	CImgObj	img_E1_underBg_1;
	CImgObj	img_bottomChara;
	//life
	CImgObj	img_lifeBg;
	CImgObj	img_lifeGauge_1;
	CImgObj	img_lifeGauge_2;
	CImgObj	img_lifeText_1;
	CImgObj	img_lifeText_2;
	CImgObj	img_lifeNum_1_1;
	CImgObj	img_lifeNum_1_2;
	CImgObj	img_lifeNum_1_3;
	CImgObj	img_lifeNum_1_4;
	CImgObj	img_lifeNum_1_5;
	CImgObj	img_lifeNum_2_1;
	CImgObj	img_lifeNum_2_2;
	CImgObj	img_lifeNum_2_3;
	CImgObj	img_lifeNum_2_4;
	CImgObj	img_lifeNum_2_5;
	CImgObj	img_lifeNum_3_1;
	CImgObj	img_lifeNum_3_2;
	CImgObj	img_lifeNum_3_3;
	CImgObj	img_lifeNum_3_4;
	CImgObj	img_lifeNum_3_5;
	CImgObj	img_lifeNum_4_1;
	CImgObj	img_lifeNum_4_2;
	CImgObj	img_lifeNum_4_3;
	CImgObj	img_lifeNum_4_4;
	CImgObj	img_lifeNum_4_5;
	//battle start
	CImgObj	img_battleStart_1;
	CImgObj	img_battleStart_2;
	CImgObj	img_battleStart_3;
	CImgObj	img_battleStart_4;
	//symbol
	CImgObj	img_symbol_1;
	CImgObj	img_symbol_2;
	CImgObj	img_symbol_3;
	CImgObj	img_symbol_4;
	CImgObj	img_symbol_5;
	//battle effect1
	CImgObj	img_btEff_01_1;
	CImgObj	img_btEff_01_2;
	CImgObj	img_btEff_01_3;
	CImgObj	img_btEff_01_4;
//	CImgObj	img_btEff_02_1;
//	CImgObj	img_btEff_02_2;
//	CImgObj	img_btEff_02_3;
//	CImgObj	img_btEff_02_4;
//	CImgObj	img_btEff_03_1;
//	CImgObj	img_btEff_03_2;
//	CImgObj	img_btEff_03_3;
//	CImgObj	img_btEff_03_4;
//	CImgObj	img_btEff_04_1;
//	CImgObj	img_btEff_04_2;
//	CImgObj	img_btEff_04_3;
//	CImgObj	img_btEff_04_4;
//	CImgObj	img_btEff_05_1;
//	CImgObj	img_btEff_05_2;
//	CImgObj	img_btEff_05_3;
//	CImgObj	img_btEff_05_4;
//	CImgObj	img_btEff_06_1;
//	CImgObj	img_btEff_06_2;
//	CImgObj	img_btEff_06_3;
//	CImgObj	img_btEff_06_4;
//	CImgObj	img_btEff_07_1;
//	CImgObj	img_btEff_07_2;
//	CImgObj	img_btEff_07_3;
//	CImgObj	img_btEff_07_4;
//	CImgObj	img_btEff_08_1;
//	CImgObj	img_btEff_08_2;
//	CImgObj	img_btEff_08_3;
//	CImgObj	img_btEff_08_4;
//	CImgObj	img_btEff_09_1;
//	CImgObj	img_btEff_09_2;
//	CImgObj	img_btEff_09_3;
//	CImgObj	img_btEff_09_4;
//	CImgObj	img_btEff_10_1;
//	CImgObj	img_btEff_10_2;
//	CImgObj	img_btEff_10_3;
//	CImgObj	img_btEff_10_4;
//	CImgObj	img_btEff_11_1;
//	CImgObj	img_btEff_11_2;
//	CImgObj	img_btEff_11_3;
//	CImgObj	img_btEff_11_4;
//	CImgObj	img_btEff_12_1;
//	CImgObj	img_btEff_12_2;
//	CImgObj	img_btEff_12_3;
//	CImgObj	img_btEff_12_4;
	//battle effect1
//	CImgObj	img_btEff_13_1;
//	CImgObj	img_btEff_13_2;
//	CImgObj	img_btEff_13_3;
//	CImgObj	img_btEff_13_4;
//	CImgObj	img_btEff_14_1;
//	CImgObj	img_btEff_14_2;
//	CImgObj	img_btEff_14_3;
//	CImgObj	img_btEff_14_4;
//	CImgObj	img_btEff_15_1;
//	CImgObj	img_btEff_15_2;
//	CImgObj	img_btEff_15_3;
//	CImgObj	img_btEff_15_4;
//	CImgObj	img_btEff_16_1;
//	CImgObj	img_btEff_16_2;
//	CImgObj	img_btEff_16_3;
//	CImgObj	img_btEff_16_4;
//	CImgObj	img_btEff_17_1;
//	CImgObj	img_btEff_17_2;
//	CImgObj	img_btEff_17_3;
//	CImgObj	img_btEff_17_4;
//	CImgObj	img_btEff_18_1;
//	CImgObj	img_btEff_18_2;
//	CImgObj	img_btEff_18_3;
//	CImgObj	img_btEff_18_4;
//	CImgObj	img_btEff_19_1;
//	CImgObj	img_btEff_19_2;
//	CImgObj	img_btEff_19_3;
//	CImgObj	img_btEff_19_4;
//	CImgObj	img_btEff_20_1;
//	CImgObj	img_btEff_20_2;
//	CImgObj	img_btEff_20_3;
//	CImgObj	img_btEff_20_4;
//	CImgObj	img_btEff_21_1;
//	CImgObj	img_btEff_21_2;
//	CImgObj	img_btEff_21_3;
//	CImgObj	img_btEff_21_4;
//	CImgObj	img_btEff_22_1;
//	CImgObj	img_btEff_22_2;
//	CImgObj	img_btEff_22_3;
//	CImgObj	img_btEff_22_4;
//	CImgObj	img_btEff_23_1;
//	CImgObj	img_btEff_23_2;
//	CImgObj	img_btEff_23_3;
//	CImgObj	img_btEff_23_4;
//	CImgObj	img_btEff_24_1;
//	CImgObj	img_btEff_24_2;
//	CImgObj	img_btEff_24_3;
//	CImgObj	img_btEff_24_4;
	//card top
	//card-1
	CImgObj	img_card_11;
	CImgObj	img_card_12;
	CImgObj	img_card_13;
	CImgObj	img_card_14;
	CImgObj	img_card_15;
	CImgObj	img_card_16;
	CImgObj	img_card_17;
	CImgObj	img_card_18;
	CImgObj	img_card_19;
	//card-2
	CImgObj	img_card_20;
	CImgObj	img_card_21;
	CImgObj	img_card_22;
	CImgObj	img_card_23;
	CImgObj	img_card_24;
	CImgObj	img_card_25;
	CImgObj	img_card_26;
	CImgObj	img_card_27;
	CImgObj	img_card_28;
	CImgObj	img_card_29;
	//card-3
	CImgObj	img_card_30;
	CImgObj	img_card_31;
	CImgObj	img_card_32;
	CImgObj	img_card_33;
	CImgObj	img_card_34;
	CImgObj	img_card_35;
	CImgObj	img_card_36;
	CImgObj	img_card_37;
	CImgObj	img_card_38;
	CImgObj	img_card_39;
	CImgObj	img_card_40;
	CImgObj	img_card_41;
	CImgObj	img_card_42;
	CImgObj	img_card_43;
	CImgObj	img_card_44;
	//card-4
	CImgObj	img_card_45;
	CImgObj	img_card_46;
	CImgObj	img_card_47;
	CImgObj	img_card_48;
	//card bottom
	CImgObj	img_card_00;
	CImgObj	img_card_01;
	CImgObj	img_card_02;
	CImgObj	img_card_03;
	CImgObj	img_card_04;
	CImgObj	img_card_05;
	CImgObj	img_card_06;
	CImgObj	img_card_07;
	CImgObj	img_card_08;
	CImgObj	img_card_09;
	CImgObj	img_card_10;
	//hansoku top
	CImgObj	img_hansokuBottom;
	//pass player
	CImgObj	img_passPlayer;
	//agari player
	CImgObj	img_agariPlayer;
	//sibari
	CImgObj	img_gameInfo_2;
	//kakumei
	CImgObj	img_gameInfo_1;		
	//no card
	CImgObj	img_noCardBg_1;
	CImgObj	img_noCardText_1_1;
	CImgObj	img_noCardText_2_1;
	CImgObj	img_noCardBg_2;
	CImgObj	img_noCardText_1_2;
	CImgObj	img_noCardText_2_2;
	CImgObj	img_noCardBg_3;
	CImgObj	img_noCardText_1_3;
	CImgObj	img_noCardText_2_3;
	CImgObj	img_noCardBg_4;
	CImgObj	img_noCardText_1_4;
	CImgObj	img_noCardText_2_4;
	CImgObj	img_noCardBg_5;
	CImgObj	img_noCardText_1_5;
	CImgObj	img_noCardText_2_5;
	CImgObj	img_noCardBg_6;
	CImgObj	img_noCardText_2_6;
	CImgObj	img_noCardBg_7;
	CImgObj	img_noCardText_1_7;
	CImgObj	img_noCardText_2_7;
	// miyakoOti bottom
	CImgObj	img_miyakoOti_4;
	CImgObj	img_miyakoOti_5;
	CImgObj	img_miyakoOti_6;

	private void setImageFiles() {
		for (int i = 0; i < 4; i ++) {
			m_picMoveCard[i] = new CImgObj("E_1/E1_card_club_01.png");
		}
		
		img_COMN_bg_01_top = new CImgObj("COMMON/COMN_bg_01.png");
		//battle count
		img_E1_batleCountBg = new CImgObj("E_1/E1_battleCountBg_1.png");		
		img_E1_battleNum_3 = new CImgObj("E_1/E1_battleCountNum_0.png");
		img_E1_battleNum_2 = new CImgObj("E_1/E1_battleCountNum_0.png");
		img_E1_battleNum_1 = new CImgObj("E_1/E1_battleCountNum_1.png");
		//character
		img_topChara_1 = new CImgObj("E_1/E1_topCharaL_01_1.png");
		img_topChara_2 = new CImgObj("E_1/E1_topCharaL_01_1.png");
		img_topChara_3 = new CImgObj("E_1/E1_topCharaR_01_1.png");
		img_topChara_4 = new CImgObj("E_1/E1_topCharaR_01_1.png");
		//cardinfo
		img_cardInfo_1 = new CImgObj("E_1/E1_cardInfoL_11.png");
		img_cardInfo_2 = new CImgObj("E_1/E1_cardInfoL_11.png");
		img_cardInfo_3 = new CImgObj("E_1/E1_cardInfoR_11.png");
		img_cardInfo_4 = new CImgObj("E_1/E1_cardInfoR_11.png");
		//status Hansoku
		img_statusHansoku_1 = new CImgObj("E_1/E1_statusHansoku_1.png");
		img_statusHansoku_2 = new CImgObj("E_1/E1_statusHansoku_1.png");
		img_statusHansoku_3 = new CImgObj("E_1/E1_statusHansoku_1.png");
		img_statusHansoku_4 = new CImgObj("E_1/E1_statusHansoku_1.png");
		//status MiyakoOti
		img_statusMiyako_1 = new CImgObj("E_1/E1_statusMiyakoOti_1.png");
		img_statusMiyako_2 = new CImgObj("E_1/E1_statusMiyakoOti_1.png");
		img_statusMiyako_3 = new CImgObj("E_1/E1_statusMiyakoOti_1.png");
		img_statusMiyako_4 = new CImgObj("E_1/E1_statusMiyakoOti_1.png");
		//chara pass
		img_topCharaPass_1 = new CImgObj("E_1/E1_pass_1.png");
		img_topCharaPass_2 = new CImgObj("E_1/E1_pass_1.png");
		img_topCharaPass_3 = new CImgObj("E_1/E1_pass_1.png");
		img_topCharaPass_4 = new CImgObj("E_1/E1_pass_1.png");
		//agari
		img_topCharaAgari_1 = new CImgObj("E_1/E1_agari_1.png");
		img_topCharaAgari_2 = new CImgObj("E_1/E1_agari_1.png");
		img_topCharaAgari_3 = new CImgObj("E_1/E1_agari_1.png");
		img_topCharaAgari_4 = new CImgObj("E_1/E1_agari_1.png");
		//hansoku top
		img_hansokuTop = new CImgObj("E_1/E1_hansoku_1.png");
		//8kiri
		img_8kiri_1 = new CImgObj("E_1/E1_8kiri_1.png");
		img_8kiri_2 = new CImgObj("E_1/E1_8kiri_2.png");
		//sibari
		img_sibari_1 = new CImgObj("E_1/E1_sibari_1.png");
		img_sibari_2 = new CImgObj("E_1/E1_sibari_2.png");
		//spade3
		img_spade3_1 = new CImgObj("E_1/E1_spade3_1.png");
		img_spade3_2 = new CImgObj("E_1/E1_spade3_2.png");
		img_spade3_3 = new CImgObj("E_1/E1_spade3_3.png");
		//kakumei
		img_kakumeiBg_1 = new CImgObj("E_1/E1_kakumeiBg_1.png");
		img_kakumeiCharaL = new CImgObj("E_1/E1_topCharaL_01_1.png");
		img_kakumei_1_1 = new CImgObj("E_1/E1_kakumeiText_1.png");
		img_kakumei_1_2 = new CImgObj("E_1/E1_kakumeiText_2.png");
		img_kakumeiBg_2 = new CImgObj("E_1/E1_kakumeiBg_1.png");
		img_kakumeiCharaR = new CImgObj("E_1/E1_topCharaR_01_1.png");
		img_kakumei_2_1 = new CImgObj("E_1/E1_kakumeiText_1.png");
		img_kakumei_2_2 = new CImgObj("E_1/E1_kakumeiText_2.png");
		img_kakumei_2_3 = new CImgObj("E_1/E1_kakumeiText_3.png");
		img_kakumei_2_4 = new CImgObj("E_1/E1_kakumeiText_4.png");
		//miyakoOti
		img_miyakoOtiBg = new CImgObj("E_1/E1_miyakoOtiBg_1.png");
		img_miyakoOti_1 = new CImgObj("E_1/E1_miyakoOtiText_1.png");
		img_miyakoOti_2 = new CImgObj("E_1/E1_miyakoOtiText_2.png");
		img_miyakoOti_3 = new CImgObj("E_1/E1_miyakoOtiText_3.png");
		//game start		
//		img_gameStartBg = new CImgObj("E_1/E1_gameStartBg_1.png");
//		img_gameStartChara = new CImgObj("E_1/E1_topCharaL_01_2.png");
//		img_gameStartText = new CImgObj("E_1/E1_gameStartText_01.png");
		//game end win
		img_gameEndWin_01 = new CImgObj("E_1/E1_gameEndWin_01.png");
		img_gameEndWin_02 = new CImgObj("E_1/E1_gameEndWin_02.png");
		img_gameEndWin_03 = new CImgObj("E_1/E1_gameEndWin_03.png");
		img_gameEndWin_04 = new CImgObj("E_1/E1_gameEndWin_04.png");
		img_gameEndWin_05 = new CImgObj("E_1/E1_gameEndWin_05.png");
		img_gameEndWin_06 = new CImgObj("E_1/E1_gameEndWin_06.png");
		img_gameEndWin_07 = new CImgObj("E_1/E1_gameEndWin_07.png");
		img_gameEndWin_08 = new CImgObj("E_1/E1_gameEndWin_08.png");
		img_gameEndWin_09 = new CImgObj("E_1/E1_gameEndWin_09.png");
		img_gameEndWin_10 = new CImgObj("E_1/E1_gameEndWin_10.png");
		img_gameEndWin_11 = new CImgObj("E_1/E1_gameEndWin_11.png");
		img_gameEndWin_12 = new CImgObj("E_1/E1_gameEndWin_12.png");
		//game end lose
		img_gameEndLose_01 = new CImgObj("E_1/E1_gameEndLose_01.png");
		img_gameEndLose_02 = new CImgObj("E_1/E1_gameEndLose_02.png");
		img_gameEndLose_03 = new CImgObj("E_1/E1_gameEndLose_03.png");
		img_gameEndLose_04 = new CImgObj("E_1/E1_gameEndLose_04.png");
		img_gameEndLose_05 = new CImgObj("E_1/E1_gameEndLose_05.png");
		img_gameEndLose_06 = new CImgObj("E_1/E1_gameEndLose_06.png");
		img_gameEndLose_07 = new CImgObj("E_1/E1_gameEndLose_07.png");
		img_gameEndLose_08 = new CImgObj("E_1/E1_gameEndLose_08.png");
		img_gameEndLose_09 = new CImgObj("E_1/E1_gameEndLose_09.png");
		img_gameEndLose_10 = new CImgObj("E_1/E1_gameEndLose_10.png");
		img_gameEndLose_11 = new CImgObj("E_1/E1_gameEndLose_11.png");
		img_gameEndLose_12 = new CImgObj("E_1/E1_gameEndLose_12.png");
		// --------------bottom------------//
		img_COMN_bg_01_btom = new CImgObj("COMMON/COMN_bg_01.png");
		// underBtn
		img_E1_underBg_1 = new CImgObj("E_1/E1_underBg_1.png");
		img_bottomChara = new CImgObj("E_1/E1_bottomChara_01_1.png");
		//life
		img_lifeBg = new CImgObj("E_1/E1_lifeBg_1.png");
		img_lifeGauge_1 = new CImgObj("E_1/E1_lifeGauge_1.png");
		img_lifeGauge_2 = new CImgObj("E_1/E1_lifeGauge_2.png");
		img_lifeText_1 = new CImgObj("E_1/E1_lifeText_1.png");
		img_lifeText_2 = new CImgObj("E_1/E1_lifeText_2.png");
		img_lifeNum_1_1 = new CImgObj("E_1/E1_lifeNum_1_0.png");
		img_lifeNum_1_2 = new CImgObj("E_1/E1_lifeNum_1_0.png");
		img_lifeNum_1_3 = new CImgObj("E_1/E1_lifeNum_1_0.png");
		img_lifeNum_1_4 = new CImgObj("E_1/E1_lifeNum_1_0.png");
		img_lifeNum_1_5 = new CImgObj("E_1/E1_lifeNum_1_0.png");
		img_lifeNum_2_1 = new CImgObj("E_1/E1_lifeNum_1_0.png");
		img_lifeNum_2_2 = new CImgObj("E_1/E1_lifeNum_1_0.png");
		img_lifeNum_2_3 = new CImgObj("E_1/E1_lifeNum_1_0.png");
		img_lifeNum_2_4 = new CImgObj("E_1/E1_lifeNum_1_0.png");
		img_lifeNum_2_5 = new CImgObj("E_1/E1_lifeNum_1_0.png");
		img_lifeNum_3_1 = new CImgObj("E_1/E1_lifeNum_2_m.png");
		img_lifeNum_3_2 = new CImgObj("E_1/E1_lifeNum_2_0.png");
		img_lifeNum_3_3 = new CImgObj("E_1/E1_lifeNum_2_0.png");
		img_lifeNum_3_4 = new CImgObj("E_1/E1_lifeNum_2_0.png");
		img_lifeNum_3_5 = new CImgObj("E_1/E1_lifeNum_2_0.png");
		img_lifeNum_4_1 = new CImgObj("E_1/E1_lifeNum_2_m.png");
		img_lifeNum_4_2 = new CImgObj("E_1/E1_lifeNum_2_0.png");
		img_lifeNum_4_3 = new CImgObj("E_1/E1_lifeNum_2_0.png");
		img_lifeNum_4_4 = new CImgObj("E_1/E1_lifeNum_2_0.png");
		img_lifeNum_4_5 = new CImgObj("E_1/E1_lifeNum_2_0.png");
		//battle start
//		img_battleStart_1 = new CImgObj("E_1/E1_battleStart_1.png");
//		img_battleStart_2 = new CImgObj("E_1/E1_battleStart_2.png");
//		img_battleStart_3 = new CImgObj("E_1/E1_battleStart_3.png");
//		img_battleStart_4 = new CImgObj("E_1/E1_battleStart_4.png");
		//symbol
		img_symbol_1 = new CImgObj("E_1/E1_symbol_1.png");
		img_symbol_2 = new CImgObj("E_1/E1_symbol_1.png");
		img_symbol_3 = new CImgObj("E_1/E1_symbol_1.png");
		img_symbol_4 = new CImgObj("E_1/E1_symbol_1.png");
		img_symbol_5 = new CImgObj("E_1/E1_symbol_1.png");
		//battle effect1
		img_btEff_01_1 = new CImgObj("E_1/E1_battleEffect_1.png");
		img_btEff_01_2 = new CImgObj("E_1/E1_battleEffect_2.png");
		img_btEff_01_3 = new CImgObj("E_1/E1_battleEffect_3.png");
		img_btEff_01_4 = new CImgObj("E_1/E1_battleEffect_4.png");
//		img_btEff_02_1 = new CImgObj("E_1/E1_battleEffect_1.png");
//		img_btEff_02_2 = new CImgObj("E_1/E1_battleEffect_2.png");
//		img_btEff_02_3 = new CImgObj("E_1/E1_battleEffect_3.png");
//		img_btEff_02_4 = new CImgObj("E_1/E1_battleEffect_4.png");
//		img_btEff_03_1 = new CImgObj("E_1/E1_battleEffect_1.png");
//		img_btEff_03_2 = new CImgObj("E_1/E1_battleEffect_2.png");
//		img_btEff_03_3 = new CImgObj("E_1/E1_battleEffect_3.png");
//		img_btEff_03_4 = new CImgObj("E_1/E1_battleEffect_4.png");
//		img_btEff_04_1 = new CImgObj("E_1/E1_battleEffect_1.png");
//		img_btEff_04_2 = new CImgObj("E_1/E1_battleEffect_2.png");
//		img_btEff_04_3 = new CImgObj("E_1/E1_battleEffect_3.png");
//		img_btEff_04_4 = new CImgObj("E_1/E1_battleEffect_4.png");
//		img_btEff_05_1 = new CImgObj("E_1/E1_battleEffect_1.png");
//		img_btEff_05_2 = new CImgObj("E_1/E1_battleEffect_2.png");
//		img_btEff_05_3 = new CImgObj("E_1/E1_battleEffect_3.png");
//		img_btEff_05_4 = new CImgObj("E_1/E1_battleEffect_4.png");
//		img_btEff_06_1 = new CImgObj("E_1/E1_battleEffect_1.png");
//		img_btEff_06_2 = new CImgObj("E_1/E1_battleEffect_2.png");
//		img_btEff_06_3 = new CImgObj("E_1/E1_battleEffect_3.png");
//		img_btEff_06_4 = new CImgObj("E_1/E1_battleEffect_4.png");
//		img_btEff_07_1 = new CImgObj("E_1/E1_battleEffect_1.png");
//		img_btEff_07_2 = new CImgObj("E_1/E1_battleEffect_2.png");
//		img_btEff_07_3 = new CImgObj("E_1/E1_battleEffect_3.png");
//		img_btEff_07_4 = new CImgObj("E_1/E1_battleEffect_4.png");
//		img_btEff_08_1 = new CImgObj("E_1/E1_battleEffect_1.png");
//		img_btEff_08_2 = new CImgObj("E_1/E1_battleEffect_2.png");
//		img_btEff_08_3 = new CImgObj("E_1/E1_battleEffect_3.png");
//		img_btEff_08_4 = new CImgObj("E_1/E1_battleEffect_4.png");
//		img_btEff_09_1 = new CImgObj("E_1/E1_battleEffect_1.png");
//		img_btEff_09_2 = new CImgObj("E_1/E1_battleEffect_2.png");
//		img_btEff_09_3 = new CImgObj("E_1/E1_battleEffect_3.png");
//		img_btEff_09_4 = new CImgObj("E_1/E1_battleEffect_4.png");
//		img_btEff_10_1 = new CImgObj("E_1/E1_battleEffect_1.png");
//		img_btEff_10_2 = new CImgObj("E_1/E1_battleEffect_2.png");
//		img_btEff_10_3 = new CImgObj("E_1/E1_battleEffect_3.png");
//		img_btEff_10_4 = new CImgObj("E_1/E1_battleEffect_4.png");
//		img_btEff_11_1 = new CImgObj("E_1/E1_battleEffect_1.png");
//		img_btEff_11_2 = new CImgObj("E_1/E1_battleEffect_2.png");
//		img_btEff_11_3 = new CImgObj("E_1/E1_battleEffect_3.png");
//		img_btEff_11_4 = new CImgObj("E_1/E1_battleEffect_4.png");
//		img_btEff_12_1 = new CImgObj("E_1/E1_battleEffect_1.png");
//		img_btEff_12_2 = new CImgObj("E_1/E1_battleEffect_2.png");
//		img_btEff_12_3 = new CImgObj("E_1/E1_battleEffect_3.png");
//		img_btEff_12_4 = new CImgObj("E_1/E1_battleEffect_4.png");
		//battle effect1
//		img_btEff_13_1 = new CImgObj("E_1/E1_battleEffect_1.png");
//		img_btEff_13_2 = new CImgObj("E_1/E1_battleEffect_2.png");
//		img_btEff_13_3 = new CImgObj("E_1/E1_battleEffect_3.png");
//		img_btEff_13_4 = new CImgObj("E_1/E1_battleEffect_4.png");
//		img_btEff_14_1 = new CImgObj("E_1/E1_battleEffect_1.png");
//		img_btEff_14_2 = new CImgObj("E_1/E1_battleEffect_2.png");
//		img_btEff_14_3 = new CImgObj("E_1/E1_battleEffect_3.png");
//		img_btEff_14_4 = new CImgObj("E_1/E1_battleEffect_4.png");
//		img_btEff_15_1 = new CImgObj("E_1/E1_battleEffect_1.png");
//		img_btEff_15_2 = new CImgObj("E_1/E1_battleEffect_2.png");
//		img_btEff_15_3 = new CImgObj("E_1/E1_battleEffect_3.png");
//		img_btEff_15_4 = new CImgObj("E_1/E1_battleEffect_4.png");
//		img_btEff_16_1 = new CImgObj("E_1/E1_battleEffect_1.png");
//		img_btEff_16_2 = new CImgObj("E_1/E1_battleEffect_2.png");
//		img_btEff_16_3 = new CImgObj("E_1/E1_battleEffect_3.png");
//		img_btEff_16_4 = new CImgObj("E_1/E1_battleEffect_4.png");
//		img_btEff_17_1 = new CImgObj("E_1/E1_battleEffect_1.png");
//		img_btEff_17_2 = new CImgObj("E_1/E1_battleEffect_2.png");
//		img_btEff_17_3 = new CImgObj("E_1/E1_battleEffect_3.png");
//		img_btEff_17_4 = new CImgObj("E_1/E1_battleEffect_4.png");
//		img_btEff_18_1 = new CImgObj("E_1/E1_battleEffect_1.png");
//		img_btEff_18_2 = new CImgObj("E_1/E1_battleEffect_2.png");
//		img_btEff_18_3 = new CImgObj("E_1/E1_battleEffect_3.png");
//		img_btEff_18_4 = new CImgObj("E_1/E1_battleEffect_4.png");
//		img_btEff_19_1 = new CImgObj("E_1/E1_battleEffect_1.png");
//		img_btEff_19_2 = new CImgObj("E_1/E1_battleEffect_2.png");
//		img_btEff_19_3 = new CImgObj("E_1/E1_battleEffect_3.png");
//		img_btEff_19_4 = new CImgObj("E_1/E1_battleEffect_4.png");
//		img_btEff_20_1 = new CImgObj("E_1/E1_battleEffect_1.png");
//		img_btEff_20_2 = new CImgObj("E_1/E1_battleEffect_2.png");
//		img_btEff_20_3 = new CImgObj("E_1/E1_battleEffect_3.png");
//		img_btEff_20_4 = new CImgObj("E_1/E1_battleEffect_4.png");
//		img_btEff_21_1 = new CImgObj("E_1/E1_battleEffect_1.png");
//		img_btEff_21_2 = new CImgObj("E_1/E1_battleEffect_2.png");
//		img_btEff_21_3 = new CImgObj("E_1/E1_battleEffect_3.png");
//		img_btEff_21_4 = new CImgObj("E_1/E1_battleEffect_4.png");
//		img_btEff_22_1 = new CImgObj("E_1/E1_battleEffect_1.png");
//		img_btEff_22_2 = new CImgObj("E_1/E1_battleEffect_2.png");
//		img_btEff_22_3 = new CImgObj("E_1/E1_battleEffect_3.png");
//		img_btEff_22_4 = new CImgObj("E_1/E1_battleEffect_4.png");
//		img_btEff_23_1 = new CImgObj("E_1/E1_battleEffect_1.png");
//		img_btEff_23_2 = new CImgObj("E_1/E1_battleEffect_2.png");
//		img_btEff_23_3 = new CImgObj("E_1/E1_battleEffect_3.png");
//		img_btEff_23_4 = new CImgObj("E_1/E1_battleEffect_4.png");
//		img_btEff_24_1 = new CImgObj("E_1/E1_battleEffect_1.png");
//		img_btEff_24_2 = new CImgObj("E_1/E1_battleEffect_2.png");
//		img_btEff_24_3 = new CImgObj("E_1/E1_battleEffect_3.png");
//		img_btEff_24_4 = new CImgObj("E_1/E1_battleEffect_4.png");
		//card top
		//card-1
		img_card_11 = new CImgObj("E_1/E1_card_club_01.png");
		img_card_12 = new CImgObj("E_1/E1_card_club_01.png");
		img_card_13 = new CImgObj("E_1/E1_card_club_01.png");
		img_card_14 = new CImgObj("E_1/E1_card_club_01.png");
		img_card_15 = new CImgObj("E_1/E1_card_club_01.png");
		img_card_16 = new CImgObj("E_1/E1_card_club_01.png");
		img_card_17 = new CImgObj("E_1/E1_card_club_01.png");
		img_card_18 = new CImgObj("E_1/E1_card_club_01.png");
		img_card_19 = new CImgObj("E_1/E1_card_club_01.png");
		//card-2
		img_card_20 = new CImgObj("E_1/E1_card_club_01.png");
		img_card_21 = new CImgObj("E_1/E1_card_club_01.png");
		img_card_22 = new CImgObj("E_1/E1_card_club_01.png");
		img_card_23 = new CImgObj("E_1/E1_card_club_01.png");
		img_card_24 = new CImgObj("E_1/E1_card_club_01.png");
		img_card_25 = new CImgObj("E_1/E1_card_club_01.png");
		img_card_26 = new CImgObj("E_1/E1_card_club_01.png");
		img_card_27 = new CImgObj("E_1/E1_card_club_01.png");
		img_card_28 = new CImgObj("E_1/E1_card_club_01.png");
		img_card_29 = new CImgObj("E_1/E1_card_club_01.png");
		//card-3
		img_card_30 = new CImgObj("E_1/E1_card_club_01.png");
		img_card_31 = new CImgObj("E_1/E1_card_club_01.png");
		img_card_32 = new CImgObj("E_1/E1_card_club_01.png");
		img_card_33 = new CImgObj("E_1/E1_card_club_01.png");
		img_card_34 = new CImgObj("E_1/E1_card_club_01.png");
		img_card_35 = new CImgObj("E_1/E1_card_club_01.png");
		img_card_36 = new CImgObj("E_1/E1_card_club_01.png");
		img_card_37 = new CImgObj("E_1/E1_card_club_01.png");
		img_card_38 = new CImgObj("E_1/E1_card_club_01.png");
		img_card_39 = new CImgObj("E_1/E1_card_club_01.png");
		img_card_40 = new CImgObj("E_1/E1_card_club_01.png");
		img_card_41 = new CImgObj("E_1/E1_card_club_01.png");
		img_card_42 = new CImgObj("E_1/E1_card_club_01.png");
		img_card_43 = new CImgObj("E_1/E1_card_club_01.png");
		img_card_44 = new CImgObj("E_1/E1_card_club_01.png");
		//card-4
		img_card_45 = new CImgObj("E_1/E1_card_club_01.png");
		img_card_46 = new CImgObj("E_1/E1_card_club_01.png");
		img_card_47 = new CImgObj("E_1/E1_card_club_01.png");
		img_card_48 = new CImgObj("E_1/E1_card_club_01.png");
		//card bottom
		img_card_00 = new CImgObj("E_1/E1_card_club_01.png");
		img_card_01 = new CImgObj("E_1/E1_card_club_01.png");
		img_card_02 = new CImgObj("E_1/E1_card_club_01.png");
		img_card_03 = new CImgObj("E_1/E1_card_club_01.png");
		img_card_04 = new CImgObj("E_1/E1_card_club_01.png");
		img_card_05 = new CImgObj("E_1/E1_card_club_01.png");
		img_card_06 = new CImgObj("E_1/E1_card_club_01.png");
		img_card_07 = new CImgObj("E_1/E1_card_club_01.png");
		img_card_08 = new CImgObj("E_1/E1_card_club_01.png");
		img_card_09 = new CImgObj("E_1/E1_card_club_01.png");
		img_card_10 = new CImgObj("E_1/E1_card_club_01.png");
		//hansoku bottom
		img_hansokuBottom = new CImgObj("E_1/E1_hansoku_1.png");
		//pass player
		img_passPlayer = new CImgObj("E_1/E1_pass_2.png");
		//agari player
		img_agariPlayer = new CImgObj("E_1/E1_agari_2.png");
		//sibari
		img_gameInfo_2 = new CImgObj("E_1/E1_gameInfo_2.png");
		//kakumei
		img_gameInfo_1 = new CImgObj("E_1/E1_gameInfo_1.png");		
		//no card
		img_noCardBg_1     = new CImgObj("E_1/E1_noCardBg_3.png");
		img_noCardText_1_1 = new CImgObj("E_1/E1_noCardText_1_1.png");
		img_noCardText_2_1 = new CImgObj("E_1/E1_noCardText_2_1.png");
		img_noCardBg_2     = new CImgObj("E_1/E1_noCardBg_2.png");
		img_noCardText_1_2 = new CImgObj("E_1/E1_noCardText_1_2.png");
		img_noCardText_2_2 = new CImgObj("E_1/E1_noCardText_2_1.png");
		img_noCardBg_3     = new CImgObj("E_1/E1_noCardBg_1.png");
		img_noCardText_1_3 = new CImgObj("E_1/E1_noCardText_1_3.png");
		img_noCardText_2_3 = new CImgObj("E_1/E1_noCardText_2_2.png");
		img_noCardBg_4     = new CImgObj("E_1/E1_noCardBg_3.png");
		img_noCardText_1_4 = new CImgObj("E_1/E1_noCardText_1_4.png");
		img_noCardText_2_4 = new CImgObj("E_1/E1_noCardText_2_3.png");
		img_noCardBg_5     = new CImgObj("E_1/E1_noCardBg_3.png");
		img_noCardText_1_5 = new CImgObj("E_1/E1_noCardText_1_5.png");
		img_noCardText_2_5 = new CImgObj("E_1/E1_noCardText_2_3.png");
		img_noCardBg_6     = new CImgObj("E_1/E1_noCardBg_2.png");
		img_noCardText_2_6 = new CImgObj("E_1/E1_noCardText_2_3.png");
		img_noCardBg_7     = new CImgObj("E_1/E1_noCardBg_2.png");
		img_noCardText_1_7 = new CImgObj("E_1/E1_noCardText_1_7.png");
		img_noCardText_2_7 = new CImgObj("E_1/E1_noCardText_2_3.png");
		// miyakoOti bottom
		img_miyakoOti_4 = new CImgObj("E_1/E1_miyakoOtiText_1.png");
		img_miyakoOti_5 = new CImgObj("E_1/E1_miyakoOtiText_2.png");
		img_miyakoOti_6 = new CImgObj("E_1/E1_miyakoOtiText_3.png");
		
		setImagePositions();
	}
	private void releaseImageFiles() {
		// --------------top------------//
		img_COMN_bg_01_top = null;
		//battle count
		img_E1_batleCountBg = null;
		img_E1_battleNum_3 = null;
		img_E1_battleNum_2 = null;
		img_E1_battleNum_1 = null;
		//character
		img_topChara_1 = null;
		img_topChara_2 = null;
		img_topChara_3 = null;
		img_topChara_4 = null;
		//cardinfo
		img_cardInfo_1 = null;
		img_cardInfo_2 = null;
		img_cardInfo_3 = null;
		img_cardInfo_4 = null;
		//status Hansoku
		img_statusHansoku_1 = null;
		img_statusHansoku_2 = null;
		img_statusHansoku_3 = null;
		img_statusHansoku_4 = null;
		//status MiyakoOti
		img_statusMiyako_1 = null;
		img_statusMiyako_2 = null;
		img_statusMiyako_3 = null;
		img_statusMiyako_4 = null;
		//chara pass
		img_topCharaPass_1 = null;
		img_topCharaPass_2 = null;
		img_topCharaPass_3 = null;
		img_topCharaPass_4 = null;
		//agari
		img_topCharaAgari_1 = null;
		img_topCharaAgari_2 = null;
		img_topCharaAgari_3 = null;
		img_topCharaAgari_4 = null;
		//hansoku top
		img_hansokuTop = null;
		//8kiri
		img_8kiri_1 = null;
		img_8kiri_2 = null;
		//sibari
		img_sibari_1 = null;
		img_sibari_2 = null;
		//spade3
		img_spade3_1 = null;
		img_spade3_2 = null;
		img_spade3_3 = null;
		//kakumei
		img_kakumeiBg_1 = null;
		img_kakumeiCharaL = null;
		img_kakumei_1_1 = null;
		img_kakumei_1_2 = null;
		img_kakumeiBg_2 = null;
		img_kakumeiCharaR = null;
		img_kakumei_2_1 = null;
		img_kakumei_2_2 = null;
		img_kakumei_2_3 = null;
		img_kakumei_2_4 = null;
		//miyakoOti
		img_miyakoOtiBg = null;
		img_miyakoOti_1 = null;
		img_miyakoOti_2 = null;
		img_miyakoOti_3 = null;
		//game start		
		img_gameStartBg = null;
		img_gameStartChara = null;
		img_gameStartText = null;
		//game end win
		img_gameEndWin_01 = null;
		img_gameEndWin_02 = null;
		img_gameEndWin_03 = null;
		img_gameEndWin_04 = null;
		img_gameEndWin_05 = null;
		img_gameEndWin_06 = null;
		img_gameEndWin_07 = null;
		img_gameEndWin_08 = null;
		img_gameEndWin_09 = null;
		img_gameEndWin_10 = null;
		img_gameEndWin_11 = null;
		img_gameEndWin_12 = null;
		//game end lose
		img_gameEndLose_01 = null;
		img_gameEndLose_02 = null;
		img_gameEndLose_03 = null;
		img_gameEndLose_04 = null;
		img_gameEndLose_05 = null;
		img_gameEndLose_06 = null;
		img_gameEndLose_07 = null;
		img_gameEndLose_08 = null;
		img_gameEndLose_09 = null;
		img_gameEndLose_10 = null;
		img_gameEndLose_11 = null;
		img_gameEndLose_12 = null;
		// --------------bottom------------//
		img_COMN_bg_01_btom = null;
		// underBtn
		img_E1_underBg_1 = null;
		img_bottomChara = null;
		//life
		img_lifeBg = null;
		img_lifeGauge_1 = null;
		img_lifeGauge_2 = null;
		img_lifeText_1 = null;
		img_lifeText_2 = null;
		img_lifeNum_1_1 = null;
		img_lifeNum_1_2 = null;
		img_lifeNum_1_3 = null;
		img_lifeNum_1_4 = null;
		img_lifeNum_1_5 = null;
		img_lifeNum_2_1 = null;
		img_lifeNum_2_2 = null;
		img_lifeNum_2_3 = null;
		img_lifeNum_2_4 = null;
		img_lifeNum_2_5 = null;
		img_lifeNum_3_1 = null;
		img_lifeNum_3_2 = null;
		img_lifeNum_3_3 = null;
		img_lifeNum_3_4 = null;
		img_lifeNum_3_5 = null;
		img_lifeNum_4_1 = null;
		img_lifeNum_4_2 = null;
		img_lifeNum_4_3 = null;
		img_lifeNum_4_4 = null;
		img_lifeNum_4_5 = null;
		//battle start
		img_battleStart_1 = null;
		img_battleStart_2 = null;
		img_battleStart_3 = null;
		img_battleStart_4 = null;
		//symbol
		img_symbol_1 = null;
		img_symbol_2 = null;
		img_symbol_3 = null;
		img_symbol_4 = null;
		img_symbol_5 = null;
		//battle effect1
		img_btEff_01_1 = null;
		img_btEff_01_2 = null;
		img_btEff_01_3 = null;
		img_btEff_01_4 = null;
//		img_btEff_02_1 = null;
//		img_btEff_02_2 = null;
//		img_btEff_02_3 = null;
//		img_btEff_02_4 = null;
//		img_btEff_03_1 = null;
//		img_btEff_03_2 = null;
//		img_btEff_03_3 = null;
//		img_btEff_03_4 = null;
//		img_btEff_04_1 = null;
//		img_btEff_04_2 = null;
//		img_btEff_04_3 = null;
//		img_btEff_04_4 = null;
//		img_btEff_05_1 = null;
//		img_btEff_05_2 = null;
//		img_btEff_05_3 = null;
//		img_btEff_05_4 = null;
//		img_btEff_06_1 = null;
//		img_btEff_06_2 = null;
//		img_btEff_06_3 = null;
//		img_btEff_06_4 = null;
//		img_btEff_07_1 = null;
//		img_btEff_07_2 = null;
//		img_btEff_07_3 = null;
//		img_btEff_07_4 = null;
//		img_btEff_08_1 = null;
//		img_btEff_08_2 = null;
//		img_btEff_08_3 = null;
//		img_btEff_08_4 = null;
//		img_btEff_09_1 = null;
//		img_btEff_09_2 = null;
//		img_btEff_09_3 = null;
//		img_btEff_09_4 = null;
//		img_btEff_10_1 = null;
//		img_btEff_10_2 = null;
//		img_btEff_10_3 = null;
//		img_btEff_10_4 = null;
//		img_btEff_11_1 = null;
//		img_btEff_11_2 = null;
//		img_btEff_11_3 = null;
//		img_btEff_11_4 = null;
//		img_btEff_12_1 = null;
//		img_btEff_12_2 = null;
//		img_btEff_12_3 = null;
//		img_btEff_12_4 = null;
		//battle effect1
//		img_btEff_13_1 = null;
//		img_btEff_13_2 = null;
//		img_btEff_13_3 = null;
//		img_btEff_13_4 = null;
//		img_btEff_14_1 = null;
//		img_btEff_14_2 = null;
//		img_btEff_14_3 = null;
//		img_btEff_14_4 = null;
//		img_btEff_15_1 = null;
//		img_btEff_15_2 = null;
//		img_btEff_15_3 = null;
//		img_btEff_15_4 = null;
//		img_btEff_16_1 = null;
//		img_btEff_16_2 = null;
//		img_btEff_16_3 = null;
//		img_btEff_16_4 = null;
//		img_btEff_17_1 = null;
//		img_btEff_17_2 = null;
//		img_btEff_17_3 = null;
//		img_btEff_17_4 = null;
//		img_btEff_18_1 = null;
//		img_btEff_18_2 = null;
//		img_btEff_18_3 = null;
//		img_btEff_18_4 = null;
//		img_btEff_19_1 = null;
//		img_btEff_19_2 = null;
//		img_btEff_19_3 = null;
//		img_btEff_19_4 = null;
//		img_btEff_20_1 = null;
//		img_btEff_20_2 = null;
//		img_btEff_20_3 = null;
//		img_btEff_20_4 = null;
//		img_btEff_21_1 = null;
//		img_btEff_21_2 = null;
//		img_btEff_21_3 = null;
//		img_btEff_21_4 = null;
//		img_btEff_22_1 = null;
//		img_btEff_22_2 = null;
//		img_btEff_22_3 = null;
//		img_btEff_22_4 = null;
//		img_btEff_23_1 = null;
//		img_btEff_23_2 = null;
//		img_btEff_23_3 = null;
//		img_btEff_23_4 = null;
//		img_btEff_24_1 = null;
//		img_btEff_24_2 = null;
//		img_btEff_24_3 = null;
//		img_btEff_24_4 = null;
		//card top
		//card-1
		img_card_11 = null;
		img_card_12 = null;
		img_card_13 = null;
		img_card_14 = null;
		img_card_15 = null;
		img_card_16 = null;
		img_card_17 = null;
		img_card_18 = null;
		img_card_19 = null;
		//card-2
		img_card_20 = null;
		img_card_21 = null;
		img_card_22 = null;
		img_card_23 = null;
		img_card_24 = null;
		img_card_25 = null;
		img_card_26 = null;
		img_card_27 = null;
		img_card_28 = null;
		img_card_29 = null;
		//card-3
		img_card_30 = null;
		img_card_31 = null;
		img_card_32 = null;
		img_card_33 = null;
		img_card_34 = null;
		img_card_35 = null;
		img_card_36 = null;
		img_card_37 = null;
		img_card_38 = null;
		img_card_39 = null;
		img_card_40 = null;
		img_card_41 = null;
		img_card_42 = null;
		img_card_43 = null;
		img_card_44 = null;
		//card-4
		img_card_45 = null;
		img_card_46 = null;
		img_card_47 = null;
		img_card_48 = null;
		//card bottom
		img_card_00 = null;
		img_card_01 = null;
		img_card_02 = null;
		img_card_03 = null;
		img_card_04 = null;
		img_card_05 = null;
		img_card_06 = null;
		img_card_07 = null;
		img_card_08 = null;
		img_card_09 = null;
		img_card_10 = null;
		//sibari
		img_gameInfo_1 = null;		
		img_gameInfo_2 = null;
		//hansoku bottom
		img_hansokuBottom = null;
		//pass player
		img_passPlayer = null;
		//agari player
		img_agariPlayer = null;
		//no card
		img_noCardBg_1 = null;
		img_noCardText_1_1 = null;
		img_noCardText_2_1 = null;
		img_noCardBg_2 = null;
		img_noCardText_1_2 = null;
		img_noCardText_2_2 = null;
		img_noCardBg_3 = null;
		img_noCardText_1_3 = null;
		img_noCardText_2_3 = null;
		img_noCardBg_4 = null;
		img_noCardText_1_4 = null;
		img_noCardText_2_4 = null;
		img_noCardBg_5 = null;
		img_noCardText_1_5 = null;
		img_noCardText_2_5 = null;
		img_noCardBg_6 = null;
		img_noCardText_2_6 = null;
		img_noCardBg_7 = null;
		img_noCardText_1_7 = null;
		img_noCardText_2_7 = null;
		// miyakoOti bottom
		img_miyakoOti_4 = null;
		img_miyakoOti_5 = null;
		img_miyakoOti_6 = null;
	}
	private void setImagePositions() {
		// --------------top------------//
		img_COMN_bg_01_top.moveTo(0, 0);
		//battle count
		img_E1_batleCountBg.moveTo(210, 0);
		img_E1_battleNum_3.moveTo(330, 6);
		img_E1_battleNum_2.moveTo(348, 6);
		img_E1_battleNum_1.moveTo(366, 6);
		//character
		img_topChara_1.moveTo(  0, 240);
		img_topChara_2.moveTo(  0, -20);
		img_topChara_3.moveTo(352, -20);
		img_topChara_4.moveTo(352, 240);
		//cardinfo
		img_cardInfo_1.moveTo(  0, 436);
		img_cardInfo_2.moveTo(  0, 176);
		img_cardInfo_3.moveTo(496, 176);
		img_cardInfo_4.moveTo(496, 436);
		//status Hansoku
		img_statusHansoku_1.moveTo(  4, 418);
		img_statusHansoku_2.moveTo(  4, 158);
		img_statusHansoku_3.moveTo(412, 158);
		img_statusHansoku_4.moveTo(412, 418);
		//status MiyakoOti
		img_statusMiyako_1.moveTo( 24, 418);
		img_statusMiyako_2.moveTo( 24, 158);
		img_statusMiyako_3.moveTo(432, 158);
		img_statusMiyako_4.moveTo(432, 418);
		//chara pass
		img_topCharaPass_1.moveTo( 80, 374);
		img_topCharaPass_2.moveTo( 80, 114);
		img_topCharaPass_3.moveTo(416, 114);
		img_topCharaPass_4.moveTo(416, 374);
		//agari
		img_topCharaAgari_1.moveTo( 48, 398);
		img_topCharaAgari_2.moveTo( 48, 138);
		img_topCharaAgari_3.moveTo(382, 138);
		img_topCharaAgari_4.moveTo(382, 398);
		//symbol
		img_symbol_2.moveTo(128, 240);
		img_symbol_3.moveTo(128,   0);
		img_symbol_4.moveTo(404,   0);
		img_symbol_5.moveTo(404, 240);
		//8kiri
		img_8kiri_1.moveTo(333,-188);
		img_8kiri_2.moveTo(110, 480);
		//sibari
		img_sibari_1.moveTo(0, -96);
		img_sibari_2.moveTo(0, 480);
		//spade3
		img_spade3_1.moveTo(200, 120);
		img_spade3_2.moveTo(200, 120);
		img_spade3_3.moveTo(200, 120);
		//miyakoOti
		img_miyakoOtiBg.moveTo(146,-480);
		img_miyakoOti_1.moveTo(244,-152);
		img_miyakoOti_2.moveTo(244,-152);
		img_miyakoOti_3.moveTo(244,-152);
		//kakumei
		img_kakumeiBg_1.moveTo(-640, 98);
		img_kakumeiCharaL.moveTo(-300, 128);
		img_kakumei_1_1.moveTo(-200, 160);
		img_kakumei_1_2.moveTo(-200, 160);
		img_kakumeiBg_2.moveTo(640, 98);
		img_kakumeiCharaR.moveTo(652, 128);
		img_kakumei_2_1.moveTo(640, 160);
		img_kakumei_2_2.moveTo(640, 160);
		img_kakumei_2_3.moveTo(640, 160);
		img_kakumei_2_4.moveTo(640, 160);
		//hansoku top
//		img_hansokuTop.moveTo(28, 80);
		//game start		
//		img_gameStartBg.moveTo(-640, 98);
//		img_gameStartChara.moveTo(-288, 128);
//		img_gameStartText.moveTo(-464, 136);
		//game end win
		img_gameEndWin_01.moveTo(  0, 480);
		img_gameEndWin_02.moveTo(640,   0);
		img_gameEndWin_03.moveTo(640,   0);
		img_gameEndWin_04.moveTo(640,   0);
		img_gameEndWin_05.moveTo(  0, 480);
		img_gameEndWin_06.moveTo(160, 480);
		img_gameEndWin_07.moveTo(640, 160);
		img_gameEndWin_08.moveTo(640, 160);
		img_gameEndWin_09.moveTo(  0, 480);
		img_gameEndWin_10.moveTo(160, 480);
		img_gameEndWin_11.moveTo(320, 480);
		img_gameEndWin_12.moveTo(640, 320);
		//game end lose
		img_gameEndLose_01.moveTo(  0, 480);
		img_gameEndLose_02.moveTo(640,   0);
		img_gameEndLose_03.moveTo(640,   0);
		img_gameEndLose_04.moveTo(640,   0);
		img_gameEndLose_05.moveTo(  0, 480);
		img_gameEndLose_06.moveTo(160, 480);
		img_gameEndLose_07.moveTo(640, 160);
		img_gameEndLose_08.moveTo(640, 160);
		img_gameEndLose_09.moveTo(  0, 480);
		img_gameEndLose_10.moveTo(160, 480);
		img_gameEndLose_11.moveTo(320, 480);
		img_gameEndLose_12.moveTo(640, 320);
		// --------------bottom------------//
		img_COMN_bg_01_btom.moveTo(0, 480);
		// underBtn
		img_E1_underBg_1.moveTo(0, 904);
		
		img_bottomChara.moveTo(256, 480);
		//life
		img_lifeBg.moveTo(0, 480);
		img_lifeGauge_1.moveTo(36, 566);
		img_lifeGauge_2.moveTo(36, 512);
		img_lifeText_1.moveTo(14, 538);
		img_lifeText_2.moveTo(14, 484);
		img_lifeNum_1_1.moveTo(210, 538);
		img_lifeNum_1_2.moveTo(184, 538);
		img_lifeNum_1_3.moveTo(158, 538);
		img_lifeNum_1_4.moveTo(132, 538);
		img_lifeNum_1_5.moveTo(106, 538);
		img_lifeNum_2_1.moveTo(210, 484);
		img_lifeNum_2_2.moveTo(184, 484);
		img_lifeNum_2_3.moveTo(158, 484);
		img_lifeNum_2_4.moveTo(132, 484);
		img_lifeNum_2_5.moveTo(106, 484);
		img_lifeNum_3_1.moveTo(242, 538);
		img_lifeNum_3_2.moveTo(268, 538);
		img_lifeNum_3_3.moveTo(294, 538);
		img_lifeNum_3_4.moveTo(320, 538);
		img_lifeNum_3_5.moveTo(346, 538);
		img_lifeNum_4_1.moveTo(242, 484);
		img_lifeNum_4_2.moveTo(268, 484);
		img_lifeNum_4_3.moveTo(294, 484);
		img_lifeNum_4_4.moveTo(320, 484);
		img_lifeNum_4_5.moveTo(346, 484);
		//symbol
		img_symbol_1.moveTo(252, 480);
		//battle effect1
		img_btEff_01_1.moveTo(0, 0);
		img_btEff_01_2.moveTo(0, 0);
		img_btEff_01_3.moveTo(0, 0);
		img_btEff_01_4.moveTo(0, 0);
//		img_btEff_02_1.moveTo(0, 0);
//		img_btEff_02_2.moveTo(0, 0);
//		img_btEff_02_3.moveTo(0, 0);
//		img_btEff_02_4.moveTo(0, 0);
//		img_btEff_03_1.moveTo(0, 0);
//		img_btEff_03_2.moveTo(0, 0);
//		img_btEff_03_3.moveTo(0, 0);
//		img_btEff_03_4.moveTo(0, 0);
//		img_btEff_04_1.moveTo(0, 0);
//		img_btEff_04_2.moveTo(0, 0);
//		img_btEff_04_3.moveTo(0, 0);
//		img_btEff_04_4.moveTo(0, 0);
//		img_btEff_05_1.moveTo(0, 0);
//		img_btEff_05_2.moveTo(0, 0);
//		img_btEff_05_3.moveTo(0, 0);
//		img_btEff_05_4.moveTo(0, 0);
//		img_btEff_06_1.moveTo(0, 0);
//		img_btEff_06_2.moveTo(0, 0);
//		img_btEff_06_3.moveTo(0, 0);
//		img_btEff_06_4.moveTo(0, 0);
//		img_btEff_07_1.moveTo(0, 0);
//		img_btEff_07_2.moveTo(0, 0);
//		img_btEff_07_3.moveTo(0, 0);
//		img_btEff_07_4.moveTo(0, 0);
//		img_btEff_08_1.moveTo(0, 0);
//		img_btEff_08_2.moveTo(0, 0);
//		img_btEff_08_3.moveTo(0, 0);
//		img_btEff_08_4.moveTo(0, 0);
//		img_btEff_09_1.moveTo(0, 0);
//		img_btEff_09_2.moveTo(0, 0);
//		img_btEff_09_3.moveTo(0, 0);
//		img_btEff_09_4.moveTo(0, 0);
//		img_btEff_10_1.moveTo(0, 0);
//		img_btEff_10_2.moveTo(0, 0);
//		img_btEff_10_3.moveTo(0, 0);
//		img_btEff_10_4.moveTo(0, 0);
//		img_btEff_11_1.moveTo(0, 0);
//		img_btEff_11_2.moveTo(0, 0);
//		img_btEff_11_3.moveTo(0, 0);
//		img_btEff_11_4.moveTo(0, 0);
//		img_btEff_12_1.moveTo(0, 0);
//		img_btEff_12_2.moveTo(0, 0);
//		img_btEff_12_3.moveTo(0, 0);
//		img_btEff_12_4.moveTo(0, 0);
		//battle effect2
//		img_btEff_13_1.moveTo(0, 0);
//		img_btEff_13_2.moveTo(0, 0);
//		img_btEff_13_3.moveTo(0, 0);
//		img_btEff_13_4.moveTo(0, 0);
//		img_btEff_14_1.moveTo(0, 0);
//		img_btEff_14_2.moveTo(0, 0);
//		img_btEff_14_3.moveTo(0, 0);
//		img_btEff_14_4.moveTo(0, 0);
//		img_btEff_15_1.moveTo(0, 0);
//		img_btEff_15_2.moveTo(0, 0);
//		img_btEff_15_3.moveTo(0, 0);
//		img_btEff_15_4.moveTo(0, 0);
//		img_btEff_16_1.moveTo(0, 0);
//		img_btEff_16_2.moveTo(0, 0);
//		img_btEff_16_3.moveTo(0, 0);
//		img_btEff_16_4.moveTo(0, 0);
//		img_btEff_17_1.moveTo(0, 0);
//		img_btEff_17_2.moveTo(0, 0);
//		img_btEff_17_3.moveTo(0, 0);
//		img_btEff_17_4.moveTo(0, 0);
//		img_btEff_18_1.moveTo(0, 0);
//		img_btEff_18_2.moveTo(0, 0);
//		img_btEff_18_3.moveTo(0, 0);
//		img_btEff_18_4.moveTo(0, 0);
//		img_btEff_19_1.moveTo(0, 0);
//		img_btEff_19_2.moveTo(0, 0);
//		img_btEff_19_3.moveTo(0, 0);
//		img_btEff_19_4.moveTo(0, 0);
//		img_btEff_20_1.moveTo(0, 0);
//		img_btEff_20_2.moveTo(0, 0);
//		img_btEff_20_3.moveTo(0, 0);
//		img_btEff_20_4.moveTo(0, 0);
//		img_btEff_21_1.moveTo(0, 0);
//		img_btEff_21_2.moveTo(0, 0);
//		img_btEff_21_3.moveTo(0, 0);
//		img_btEff_21_4.moveTo(0, 0);
//		img_btEff_22_1.moveTo(0, 0);
//		img_btEff_22_2.moveTo(0, 0);
//		img_btEff_22_3.moveTo(0, 0);
//		img_btEff_22_4.moveTo(0, 0);
//		img_btEff_23_1.moveTo(0, 0);
//		img_btEff_23_2.moveTo(0, 0);
//		img_btEff_23_3.moveTo(0, 0);
//		img_btEff_23_4.moveTo(0, 0);
//		img_btEff_24_1.moveTo(0, 0);
//		img_btEff_24_2.moveTo(0, 0);
//		img_btEff_24_3.moveTo(0, 0);
//		img_btEff_24_4.moveTo(0, 0);
		//card top
		//card-1
		img_card_11.moveTo( 64, 352);
		img_card_12.moveTo( 64,  92);
		img_card_13.moveTo(450,  92);
		img_card_14.moveTo(450, 352);
		
		img_card_15.moveTo(272, 256);
		img_card_16.moveTo(208, 208);
		img_card_17.moveTo(232, 108);
		img_card_18.moveTo(304, 140);
		img_card_19.moveTo(338, 210);
		//card-2
		img_card_20.moveTo(252, 256);
		img_card_21.moveTo(292, 256);
		img_card_22.moveTo(188, 208);
		img_card_23.moveTo(228, 208);
		img_card_24.moveTo(212, 108);
		img_card_25.moveTo(252, 108);
		img_card_26.moveTo(284, 140);
		img_card_27.moveTo(324, 140);
		img_card_28.moveTo(318, 210);
		img_card_29.moveTo(358, 210);
		//card-3
		img_card_30.moveTo(232, 256);
		img_card_31.moveTo(272, 256);
		img_card_32.moveTo(312, 256);
		img_card_33.moveTo(168, 208);
		img_card_34.moveTo(208, 208);
		img_card_35.moveTo(248, 208);
		img_card_36.moveTo(192, 108);
		img_card_37.moveTo(232, 108);
		img_card_38.moveTo(272, 108);
		img_card_39.moveTo(264, 140);
		img_card_40.moveTo(304, 140);
		img_card_41.moveTo(344, 140);
		img_card_42.moveTo(298, 210);
		img_card_43.moveTo(338, 210);
		img_card_44.moveTo(378, 210);
		//card-4
		img_card_45.moveTo(200, 176);
		img_card_46.moveTo(250, 176);
		img_card_47.moveTo(300, 176);
		img_card_48.moveTo(350, 176);
		//card bottom
		img_card_00.moveTo(  2, 628);
		img_card_01.moveTo( 92, 628);
		img_card_02.moveTo(182, 628);
		img_card_03.moveTo(272, 628);
		img_card_04.moveTo(362, 628);
		img_card_05.moveTo(  2, 764);
		img_card_06.moveTo( 92, 764);
		img_card_07.moveTo(182, 764);
		img_card_08.moveTo(272, 764);
		img_card_09.moveTo(362, 764);
		img_card_10.moveTo(452, 764);

		img_gameInfo_1.moveTo( 24, 576);
		img_gameInfo_2.moveTo(120, 576);
		//battle start
//		img_battleStart_1.moveTo(640, 960);
//		img_battleStart_2.moveTo(640, 960);
//		img_battleStart_3.moveTo(640, 960);
//		img_battleStart_4.moveTo(640, 960);
		
		//hansoku bottom
//		img_hansokuBottom.moveTo(28, 560);
		//pass player
		img_passPlayer.moveTo(240, 672);
		//agari player
		img_agariPlayer.moveTo(146, 650);
		
		//no card
		img_noCardBg_1.moveTo(0, 3000);
		img_noCardText_1_1.moveTo(0, 3000);
		img_noCardText_2_1.moveTo(0, 3000);
		img_noCardBg_2.moveTo(0, 3000);
		img_noCardText_1_2.moveTo(0, 3000);
		img_noCardText_2_2.moveTo(0, 3000);
		img_noCardBg_3.moveTo(0, 3000);
		img_noCardText_1_3.moveTo(0, 3000);
		img_noCardText_2_3.moveTo(0, 3000);
		img_noCardBg_4.moveTo(0, 3000);
		img_noCardText_1_4.moveTo(0, 3000);
		img_noCardText_2_4.moveTo(0, 3000);
		img_noCardBg_5.moveTo(0, 3000);
		img_noCardText_1_5.moveTo(0, 3000);
		img_noCardText_2_5.moveTo(0, 3000);
		img_noCardBg_6.moveTo(0, 3000);
		img_noCardText_2_6.moveTo(0, 3000);
		img_noCardBg_7.moveTo(0, 3000);
		img_noCardText_1_7.moveTo(0, 3000);
		img_noCardText_2_7.moveTo(0, 3000);
		
		// miyakoOti bottom
		img_miyakoOti_4.moveTo( 92, 328);
		img_miyakoOti_5.moveTo(244, 328);
		img_miyakoOti_6.moveTo(396, 328);
	}
}
