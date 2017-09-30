package com.ssj.fugou.game;

import com.dlten.lib.STD;
import com.dlten.lib.file.CConFile;
import com.ssj.fugou.Globals;
import com.ssj.fugou.wnds.WndGame;
import com.ssj.fugou.Globals.*;

public class GameLogic {
	
	private static GameLogic	_instance = null;
	private	WndGame	m_pGameWnd = null;
//	private	Engine	m_pKernel = null;
	private	Engine_debug	m_pKernel = null;
	
	private	int		m_nPlayerChr[] = new int [PLAYER_COUNT];
	private	int		m_nPlacePlayer[] = new int [5];
	private	int		m_nUserSoldiers;
	private	int		m_nNPCSoldiers;
	private	int		m_nUserBaseSoldiers;
	private	int		m_nNPCBaseSoldiers;
	private	int		m_nUserDownSoldiers;
	private	int		m_nNPCDownSoldiers;
	
	private	int		m_nState;
	private	int		m_nPrevState;
	private	int		m_nNextState;
	
	private	int 	m_nFocusCardNo;
	private	int		m_nHandCardsCount;
	private	int		m_nSelCardsCount;
	private	int		m_nSelCardNo[] = new int [MAX_SELCARDS];
	private	HANDCARDS_INFO	m_stHandCards = new HANDCARDS_INFO();
	
	private	int		m_nGamePlayingCount;
	private	int		m_nExchangeKind;
	private	Globals.RULE_INFO   m_infoRule   = new Globals.RULE_INFO();
	private	int		m_nActionCode;
	private	int		m_nActionKind;	
	private	Globals.RESULT_INFO m_infoResult = new Globals.RESULT_INFO();

	
	
	public static final int
	MAX_CPU			= 4,
	MAX_HANDCARDS	= 11,
	CARD_SIZE		= 14,
	MAX_SELCARDS	= 4,
	JOKER_INDEX		= 52,
	NO_CARD			= -1,
	ACTION_NONE		= -1;

	public static final int
		PLAYER_USER = 0,
		PLAYER_CPU1 = 1,
		PLAYER_CPU2 = 2,
		PLAYER_CPU3 = 3,
		PLAYER_CPU4 = 4,
		PLAYER_COUNT = 5;

	public static final int
		__FC_THREETHREE = 0,	// "「３」三枚が最強！！",
		__FC_EIGHTEND   = 1,	// "８切り！！",
		__FC_JOKER      = 2,	// "Joker上がり禁止！！",
		__FC_TWO        = 3,	// "２上がり禁止！！",
		__FC_SPADE      = 4,	// "スペード縛り中！",
		__FC_HEART      = 5,	// "ハート縛り中！",
		__FC_DIAMOND    = 6,	// "ダイヤ縛り中！",
		__FC_CLUB       = 7,	// "クラブ縛り中！",
		__FC_KAKUMEI    = 8;	// "革命中！",

	public static final int
		STATE_NONE               = 0,
		STATE_RULE               = 1,
		STATE_INITIALIZE         = 2,
		STATE_DOING_DEAL         = 3,
		STATE_INITIAL_ATFIRST    = 4,
		STATE_CHOICE_ATFIRST     = 5,		//カードのトレード
		STATE_NPC_CHOICE_ATFIRST = 6,		//カードのトレード
		STATE_AFTER_ATFIRST      = 7,
		STATE_INITIAL_CHOOSING   = 8,
		STATE_CHOOSING           = 9,
		STATE_CHOOSING_ERROR     = 10,
		STATE_NPC_CHOOSING       = 11,
		STATE_ACTION_CHOOSING    = 12,
		STATE_ACTION_DISCARD     = 13,
		STATE_ACTION_PASS        = 14,
		STATE_ACTION_EXCHANGE    = 15,
		STATE_UPDATE_PLAYER      = 16,
		STATE_INITIAL_NAGASI     = 17,
		STATE_NAGASI             = 18,
		STATE_AGARI              = 19,
		STATE_8KIRI              = 20,
		STATE_HASOKU             = 21,
		STATE_REVOL_SET          = 22,
		STATE_REVOL_FREE         = 23,
		STATE_MIYAKOOTI          = 24,
		STATE_SIBARI             = 25,
		STATE_SPADE3             = 26,
		STATE_NOCARD             = 27,
		
		STATE_RESULT             = 28,
		STATE_WIN                = 29,
		STATE_LOOSE              = 30,
		STATE_GAME_END           = 31,
		STATE_READY_NEXTGAME     = 32,
		STATE_READY_EXCHANGE     = 33,
		STATE_READY_RESTART      = 34;
	
	public static String m_debug_szState[] = {
		"STATE_NONE",				// = 0,
		"STATE_RULE",				// = 1,
		"STATE_INITIALIZE",			// = 2,
		"STATE_DOING_DEAL",			// = 3,
		"STATE_INITIAL_ATFIRST",	// = 4,
		"STATE_CHOICE_ATFIRST",		// = 5,		//カードのトレード
		"STATE_NPC_CHOICE_ATFIRST",	// = 6,		//カードのトレード
		"STATE_AFTER_ATFIRST",		// = 7,
		"STATE_INITIAL_CHOOSING",	// = 8,
		"STATE_CHOOSING",			// = 9,
		
		"STATE_CHOOSING_ERROR",		// = 10,
		"STATE_NPC_CHOOSING",		// = 11,
		"STATE_ACTION_CHOOSING",	// = 12,
		"STATE_ACTION_DISCARD",		// = 13,
		"STATE_ACTION_PASS",		// = 14,
		"STATE_ACTION_EXCHANGE",	// = 15,
		"STATE_UPDATE_PLAYER",		// = 16,
		"STATE_INITIAL_NAGASI",		// = 17,
		"STATE_NAGASI",				// = 18,
		"STATE_AGARI",				// = 19,
		
		"STATE_8KIRI",				// = 20,
		"STATE_HASOKU",				// = 21,
		"STATE_REVOL_SET",			// = 22,
		"STATE_REVOL_FREE",			// = 23,
		"STATE_MIYAKOOTI",			// = 24,
		"STATE_SIBARI",				// = 25,
		"STATE_SPADE3",				// = 26,
		"STATE_NOCARD",				// = 27,
		"STATE_RESULT",				// = 28,
		"STATE_WIN",				// = 29,
		
		"STATE_LOOSE",				// = 30,
		"STATE_GAME_END",			// = 31,
		"STATE_READY_NEXTGAME",		// = 32,
		"STATE_READY_EXCHANGE",		// = 33,
		"STATE_READY_RESTART",		// = 34;
	};

	public static final int
		ACT_NONE         = 0,
		ACT_CREATE       = 1,
		ACT_START_GAME   = 2,
		ACT_RESTART_GAME = 3,
		ACT_RESUME_GAME  = 4,
		ACT_INIT         = 5,
		ACT_TO_NEXT      = 6,
		ACT_COMMAND      = 7,
		ACT_GAMEEND      = 8,
		ACT_SETLEVEL     = 9,
		ACT_COUNT        = 10;

	public static String m_debug_szAction[] = {
		"ACT_NONE",
		"ACT_CREATE",
		"ACT_START_GAME",
		"ACT_RESTART_GAME",
		"ACT_RESUME_GAME",
		"ACT_INIT",
		"ACT_TO_NEXT",
		"ACT_COMMAND",
		"ACT_GAMEEND",
		"ACT_SETLEVEL",
		"ACT_COUNT"		
	};
	public static final int
		CMD_LEFT  = 0,
		CMD_RIGHT = 1,
		CMD_UP    = 2,
		CMD_DOWN  = 3,
		CMD_TOUCH = 4,
		CMD_SET   = 5,
		CMD_PASS  = 6;
	private static int nAddSoldiers[] = { 500, 300, 0, 300, 500 };

	public static class HANDCARDS_INFO {
		public boolean	bSetting[] = new boolean[MAX_HANDCARDS];
		public int 	nHandCards[] = new int [MAX_HANDCARDS];
		
		public HANDCARDS_INFO() {
			Init();
		}
		public void Init() {
			STD.MEMSET(bSetting, false);
			STD.MEMSET(nHandCards, 0);
		}
		public void Copy(HANDCARDS_INFO info) {
			STD.MEMCPY(bSetting, info.bSetting);
			STD.MEMCPY(nHandCards, info.nHandCards);
		}
	};

	
	
	
	
	
	public static GameLogic getInstance() {
		if (_instance == null) 
			_instance = new GameLogic();
		return _instance;
	}
	
	public void deleteInstance() {
		if (_instance != null) {
			DeleteEngine();
			_instance = null;
		}
	}

	public GameLogic() {
		Initialize();
	}
	
	public void SS_DeleteEngine() {
		DeleteEngine();
	}

	private boolean CreateEngine() {
//		m_pKernel = new Engine();
		m_pKernel = new Engine_debug();
		return m_pKernel.CreateEngine();
	}

	private void DeleteEngine() {
		m_pKernel.DeleteEngine();
		m_pKernel = null;
	}

	private boolean Initialize() {
		m_pKernel = null;
		
		m_nState = STATE_NONE;
		m_nPrevState = STATE_NONE;
		m_nNextState = STATE_NONE;
		m_nGamePlayingCount = 1;
		InitGameInfo();
		m_nExchangeKind = 0;
		m_nActionCode = ACTION_NONE;
		m_pGameWnd = null;
		m_infoRule.Init();
		m_infoResult.Init();
		return true;
	}
	
	private void StartGame() {
		m_pKernel.StartGame();
		DBG_SetTumi();
	}

	private void RestartGame() {
		m_pKernel.RestartGame();
	}

	private void InitEngineRule() {
		//set game rule
		RULE_INFO	ruleinfo = Globals.g_Global.GetGameRule();
		m_infoRule.Copy(ruleinfo);

		boolean bRuleVals[] = new boolean[Globals.RULE_COUNT];
		for (int i = 0; i < Globals.RULE_COUNT; i ++)
			bRuleVals[i] = m_infoRule.getVals(i);
		m_pKernel.SetRule(bRuleVals);
	}
	
	private void InitGameInfo() {
		int nNPCCountry = Globals.g_Global.GetNPCCountry();
		COUNTRY_INFO info = Globals.GetCountryInfo(nNPCCountry);
		
		m_nUserSoldiers = Globals.g_Global.GetUserSoldiers();
		m_nUserBaseSoldiers = m_nUserSoldiers;
		m_nNPCSoldiers = info.nSoldiers;
		m_nNPCBaseSoldiers = m_nNPCSoldiers;
		m_nPlayerChr[PLAYER_USER] = Globals.g_Global.GetUserChr();
		int nChr;
		for (int i = PLAYER_CPU1; i < PLAYER_COUNT; i ++)
		{
			nChr = Globals.g_Global.GetNPCs(i-PLAYER_CPU1);
			if (nChr == m_nPlayerChr[PLAYER_USER] && Globals.g_Global.GetGameMode() == Globals.GM_UNIFY)
				nChr = Globals.CHR_JINWEIBING;
			m_nPlayerChr[i] = nChr;
		}
			
		
		for (int i = 0; i < PLAYER_COUNT; i ++)
			m_nPlacePlayer[i] = PLAYER_USER + i;
			
		//RULE_INFO rule = GetCountryRuleInfo(nNPCCountry);
		m_nUserDownSoldiers = 0;
		m_nNPCDownSoldiers = 0;
		m_infoResult.Init();
	}

	private void InitOneGame() {
		m_nUserDownSoldiers = 0;
		m_nNPCDownSoldiers = 0;
		m_infoResult.Init();	
	}

	private void InitHandCardsInfo() {
		m_stHandCards.Init();
		m_nHandCardsCount = 0;
		m_nSelCardsCount = 0;
		m_nFocusCardNo = 0;
	}

	private void InitAction() {
		m_nActionCode = ACTION_NONE;
		m_nActionKind = 0;	
	}

	private void SetHandCardsInfo() {
		m_nHandCardsCount = m_pKernel.GetHandCards(SS_GetTurn(), m_stHandCards.nHandCards);
	}

	//********************* Interface-Start ******************************//
	public void SS_SetGameWnd(WndGame pWnd) {
		m_pGameWnd = pWnd;
	}

	public int SS_Action(int nActionNum, int dwParam1, int dwParam2) {	
		if (nActionNum == ACT_TO_NEXT && dwParam2 != m_nState) {
			STD.ASSERT(false);
			return -1;
		}
		
		switch (nActionNum) {
		case ACT_NONE:			break;
		case ACT_CREATE:		Action_Create(dwParam1, dwParam2);			break;
		case ACT_START_GAME:	Action_Start_Game(dwParam1, dwParam2);		break;
		case ACT_RESTART_GAME:	Action_Restart_Game(dwParam1, dwParam2);	break;
		case ACT_RESUME_GAME:	Action_Resume_Game(dwParam1, dwParam2);		break;
		case ACT_INIT:			Action_Init(dwParam1, dwParam2);			break;
		case ACT_TO_NEXT:		Action_ToNext(dwParam1, dwParam2);			break;
		case ACT_COMMAND:		Action_Command(dwParam1, dwParam2);			break;
		case ACT_GAMEEND:		Action_GameEnd(dwParam1, dwParam2);			break;
		case ACT_SETLEVEL:		Action_SetRule(dwParam1, dwParam2);			break;
		}

		DBGLog_Action(nActionNum);
		return 0;
	}

	public void SS_Initialize() {
		Initialize();
	}

	private void SS_InitState() {
		m_nState = STATE_NONE;
		m_nPrevState = STATE_NONE;
		m_nNextState = STATE_NONE;
	}

	public int SS_GetState() {
		return m_nState;
	}

	public void SS_SetState(int nState) {
		m_nState = nState;
	}

	public int SS_GetNextState() {
		return m_nNextState;
	}

	public void SS_SetNextState(int nState) {
		m_nNextState = nState;
	}

	public int SS_GetTurn() {
		int nTurn = m_pKernel.GetTurn();
		if (nTurn == -1)
			nTurn = GetPlayerFromKind(m_nExchangeKind);
		return nTurn;
	}

	/*
	int CGameLogic::SS_GetPlayerFromPlace(int nPlace)
	{
		return m_nPlacePlayer[nPlace];
	}
	*/
	/*
	int CGameLogic::SS_GetPlaceFromPlayer(int nPlayer)
	{
		int nPlace = 0;
		for (int i = 0; i < 5; i ++)	
		{
			if (m_nPlacePlayer[i] == nPlayer)
			{
				nPlace = i;
				break;
			}
		}
		return nPlace;
	}
	*/
	public void SS_InitGameInfo() {
		InitGameInfo();
	}

	public int SS_GetPlayerKind(int nPlayer) {
		return m_pKernel.GetPlayerKind(nPlayer);
	}

	public int SS_GetHandCards(int nPlayer, HANDCARDS_INFO pnCards) {
		int nCount = 0;
		if (nPlayer == SS_GetTurn() && (m_nState == STATE_CHOOSING || m_nPrevState == STATE_CHOOSING || m_nState == STATE_CHOICE_ATFIRST))
		{
			pnCards.Copy(m_stHandCards);
			nCount = m_nHandCardsCount;
		}
		else
		{
			pnCards.Init();
			nCount = m_pKernel.GetHandCards(nPlayer, pnCards.nHandCards);
		}
		return nCount;
	}

	public int SS_GetDiscardCards(DISCARD_CARDS_INFO pInfo[]) {
		int	pCardInfo[] = new int [20*7];
		int nCount =  m_pKernel.GetDiscardCards(pCardInfo);
		if (pInfo != null) {
			int index = 0;
			for (int i = 0; i < nCount; i ++) {
				index = i * 7;
				
				pInfo[i].nFrom     = pCardInfo[index+0];
				pInfo[i].nKind     = pCardInfo[index+1];
				pInfo[i].nCount    = pCardInfo[index+2];
				pInfo[i].nCards[0] = pCardInfo[index+3];
				pInfo[i].nCards[1] = pCardInfo[index+4];
				pInfo[i].nCards[2] = pCardInfo[index+5];
				pInfo[i].nCards[3] = pCardInfo[index+6];
			}
		}
		return nCount;
	}

	private boolean SS_IsUserTurn() {
		if (Globals.DEBUG_AUTO_GAME == true)
			return false;
		else if (Globals.DEBUG_MANUAL_GAME == true)
			return true;
	
		if (Globals.TEST_TOOL == true) {
			if (IsAutoGame())
				return false;
			/*
			int nTestMode = AfxGetTool()->GetTestMode();
			if (nTestMode == TEST_AUTO_GAME)
				return false;
			else if (nTestMode == TEST_MANUAL_GAME)
				return true;
			 */
		}
		int nTurn = m_pKernel.GetTurn();
		return (m_nPlacePlayer[nTurn] == PLAYER_USER) ? true : false;
	}

	private int SS_IsWhoAction() {
		int nTurn = m_pKernel.GetTurn();
		if (Globals.DEBUG_AUTO_GAME == true)
			return (nTurn != -1) ? 1 : nTurn;
		else if (Globals.DEBUG_MANUAL_GAME == true)
			return (nTurn != -1) ? 0 : nTurn;
		
		if (Globals.TEST_TOOL == true) {
			if (IsAutoGame())
				return (nTurn != -1) ? 1 : nTurn;
		}
		
		return nTurn;
	}

	public boolean SS_IsEnableUserAction() {
		return (m_nState == STATE_CHOOSING || m_nState == STATE_CHOICE_ATFIRST || m_nState == STATE_READY_NEXTGAME) ? true : false;
	}

	public int SS_GetGamePlayingCount() {
		//int nCount = m_infoResult.nResultVals[RESULT_GAME]+1;
		return m_nGamePlayingCount;
	}

	public int	SS_GetPlayerFromKind(int nKind) {
		int nPlayer;
		for (nPlayer = PLAYER_USER; nPlayer < PLAYER_COUNT; nPlayer ++)	
		{
			if (m_pKernel.GetPlayerKind(nPlayer) == nKind)
				break;
		}
		return nPlayer;
	}

	public int	SS_GetPlayerChr(int nPlace) {
		int nPlayer = m_nPlacePlayer[nPlace];
		return m_nPlayerChr[nPlayer];
	}

	public boolean SS_IsPassable() {
		int nTurn = m_pKernel.GetTurn();
		if (nTurn == -1)
			return false;
		int nCount = m_pKernel.GetDiscardCandi(nTurn, null);
		return (nCount < 0) ? false : true;
	}

	public boolean SS_IsEnableDiscard() {
//		int nTurn = m_pKernel->GetTurn();
		if ((m_nState == STATE_CHOOSING && m_nSelCardsCount == 0) ||
			(m_nState == STATE_CHOICE_ATFIRST && IsEnableExchange(false) == false))
			return false;
		//int nCount = m_pKernel.GetDiscardCandi(nTurn, null);
		return true;
	}

	public boolean SS_GetRule(int nIndex) {
		return m_infoRule.getVals(nIndex);
	}

	public boolean SS_IsSibariState() {
		return m_pKernel.IsSibariState();	
	}

	public int SS_GetSoldiers(int nPlayer) {
		int nSoldiers = 0;
		if (nPlayer == PLAYER_USER)
			nSoldiers = m_nUserSoldiers;
		else
			nSoldiers = m_nNPCSoldiers;
		
		return nSoldiers;
	}

	public void SS_SetSoldiers(int nPlayer, int nSoldiers) {
		if (nPlayer == PLAYER_USER)
			m_nUserSoldiers = nSoldiers;
		else
			m_nNPCSoldiers = nSoldiers;	
	}

	public int SS_GetDownSoldiers(int nPlayer) {
		int nSoldiers = 0;
		if (nPlayer == PLAYER_USER)
			nSoldiers = m_nUserDownSoldiers;
		else
			nSoldiers = m_nNPCDownSoldiers;
		
		return nSoldiers;
	}

	public int SS_GetSoldiersRate(int nPlayer) {
		int nRate = 0;
		if (nPlayer == PLAYER_USER)
			nRate = (m_nUserSoldiers-m_nUserDownSoldiers) * 100 / m_nUserBaseSoldiers;
		else
			nRate = (m_nNPCSoldiers-m_nNPCDownSoldiers) * 100 / m_nNPCBaseSoldiers;
		return nRate;
	}

	public int SS_GetGameKifuData(byte[] pBuf, int bufsize) {
		if (pBuf == null)
			return -1;
		int nRealSize = m_pKernel.GetGameContext(pBuf, bufsize);
		return nRealSize;
	}

	public boolean SS_SetGameKifuData(byte pBuf[], int pdwSize) {
		if (pBuf == null || pdwSize == 0)
			return false;

		boolean bRet = m_pKernel.SetGameContext(pBuf, pdwSize);
//		m_infoRule = g_Global.GetGameRule();

		boolean bRuleVals[] = new boolean[Globals.RULE_COUNT];
		m_pKernel.GetRule(bRuleVals);
		for (int i = 0; i < Globals.RULE_COUNT; i ++)
			m_infoRule.setVals(i, bRuleVals[i]);
		
		return bRet;
	}

	//********************* Interface-End ********************************//

	private int KernelAction(ACTION action) {
		int act[] = new int[7];
		act[0] = action.nKind;
		act[1] = action.nPlayer;
		act[2] = action.nCount;
		act[3] = action.nIndices[0];
		act[4] = action.nIndices[1];
		act[5] = action.nIndices[2];
		act[6] = action.nIndices[3];
		m_nActionCode = m_pKernel.Action(act);
		m_nActionKind = action.nKind;
		
		int nState = InterpretAction();
		/*
		int nState = -1;
		switch (nRetCode)
		{
		case ACTION_OK:
			{			
				switch (action.nKind)
				{
				case ACTION.EXCHANGE:
					nState = STATE_AFTER_ATFIRST;
					break;
				case ACTION.DISCARD:
					nState = STATE_ACTION_DISCARD;
					break;
				case ACTION.PASS:
					nState = STATE_ACTION_PASS;
					break;
				case ACTION.TURN_NEXT:
					nState = STATE_INITIAL_CHOOSING;
					break;
				}			
			}
			break;
		case ACTION_FAIL:
			nState = STATE_CHOOSING_ERROR;
			break;
		case ACTION_FOUL:
			nState = STATE_HASOKU;
			break;
		case ACTION_REVOLUTION_SET:
			nState = STATE_REVOL_SET;
			break;
		case ACTION_REVOLUTION_FREE:
			nState = STATE_REVOL_FREE;
			break;
		case ACTION_GAME_END:
			nState = STATE_WINORLOOSE;
			break;
		}
		*/
		return nState;
	}

	private int InterpretAction() {
		if (m_nActionCode == ACTION_NONE)
			return STATE_NONE;
		
		int nState = STATE_NONE;
		int nCode = m_nActionCode;
		if (nCode == ACTION_FAIL)
		{
			nState = STATE_CHOOSING_ERROR;
		}
		else if ((nCode & ACTION_GAME_END) != 0)
		{
			nState = STATE_RESULT;
			nCode -= ACTION_GAME_END;		
		}
		else if ((nCode & ACTION_OK) != 0)
		{
			nCode -= ACTION_OK;
			switch (m_nActionKind)		
			{
			case ACTION.EXCHANGE:
				nState = STATE_AFTER_ATFIRST;
				break;
			case ACTION.DISCARD:
				nState = STATE_ACTION_DISCARD;
				break;
			case ACTION.PASS:
				nState = STATE_ACTION_PASS;
				break;
			case ACTION.TURN_NEXT:
				if (m_nPrevState == STATE_AFTER_ATFIRST)
					nState = STATE_READY_RESTART;
				else
					nState = STATE_INITIAL_CHOOSING;
				break;
			default:
				STD.ASSERT(false);
				break;
			}			
		}
		else if ((nCode & ACTION_REVOLUTION_SET) != 0)
		{
			nState = STATE_REVOL_SET;
			nCode -= ACTION_REVOLUTION_SET;
		}
		else if ((nCode & ACTION_REVOLUTION_FREE) != 0)
		{
			nState = STATE_REVOL_FREE;
			nCode -= ACTION_REVOLUTION_FREE;
		}
		else if ((nCode & ACTION_AGARI) != 0)
		{
			nState = STATE_AGARI;
			nCode -= ACTION_AGARI;
		}
		else if ((nCode & ACTION_8KIRI) != 0)
		{
			nState = STATE_8KIRI;
			nCode -= ACTION_8KIRI;		
		}
		else if ((nCode & ACTION_SIBARI) != 0)
		{
			nState = STATE_SIBARI;
			nCode -= ACTION_SIBARI;		
		}
		else if ((nCode & ACTION_FOUL) != 0)
		{
			nState = STATE_HASOKU;
			nCode -= ACTION_FOUL;		
		}
		else if ((nCode & ACTION_DAIFUGOU_FALL) != 0)
		{
			nState = STATE_MIYAKOOTI;
			nCode -= ACTION_DAIFUGOU_FALL;		
		}
		else if ((nCode & ACTION_SPADE3) != 0)
		{
			nState = STATE_SPADE3;
			nCode -= ACTION_SPADE3;
		}
		else
		{
			STD.ASSERT(false);
		}
		
		m_nActionCode = nCode;
		if (m_nActionCode == 0)
			m_nActionCode = ACTION_NONE;
		return nState;
	}
	//********************* Action Proc - Start ********************************//
	private void Action_Create(int dwParam1, int dwParam2) {
		CreateEngine();
//		SetEngineLevel();
	}

	private void Action_SetRule(int dwParam1, int dwParam2) {
		SetEngineLevel();
	}

	private void Action_Start_Game(int dwParam1, int dwParam2) {
		m_pKernel.StartGame();
//		DBG_SaveContext();
//		InitEngineRule();
//		DBG_SetTumi();
		m_nNextState = STATE_RULE;
		//SetGameState(STATE_INITIAL_CHOOSING);
	}

	private void Action_Restart_Game(int dwParam1, int dwParam2) {
		m_nGamePlayingCount ++;
		m_pKernel.RestartGame();
//		DBG_SaveContext();
		InitOneGame();
		InitAction();
		m_nNextState = STATE_READY_EXCHANGE;
	}

	private void Action_Resume_Game(int dwParam1, int dwParam2) {
		//m_pKernel.RestartGame();
		//	DBG_SaveContext();
		InitOneGame();
		InitAction();
		InitHandCardsInfo();
		//InitEngineRule();
		SetHandCardsInfo();
	}

	private void Action_Init(int dwParam1, int dwParam2) {
	}

	private void Action_ToNext(int dwParam1, int dwParam2) {
		SetGameState(m_nNextState);
	}

	private void Action_Command(int dwParam1, int dwParam2) {
		if (SS_IsEnableUserAction() == false)
			return;
		switch (dwParam1)
		{
		case CMD_LEFT:
			m_nFocusCardNo --;
			if (m_nFocusCardNo < 0)
				m_nFocusCardNo = m_nHandCardsCount-1;
			break;
		case CMD_RIGHT:
			m_nFocusCardNo ++;
			if (m_nFocusCardNo > m_nHandCardsCount-1)
				m_nFocusCardNo = 0;
			break;
		case CMD_UP:
			CommandUp();
			break;
		case CMD_DOWN:
			CommandDown();
			break;
		case CMD_TOUCH:
			CommandTouch(dwParam2);
			break;
		case CMD_SET:
			CommandSet();
			break;
		case CMD_PASS:
			CommandPass();
			break;
		}
	}

	private void Action_GameEnd(int dwParam1, int dwParam2) {
		SetGameState(STATE_WIN);
	}
	//command 
	private void CommandUp() {
		if (m_nSelCardsCount < MAX_SELCARDS && m_stHandCards.bSetting[m_nFocusCardNo] == false)
		{
			DISCARD_CANDI candi[] = new DISCARD_CANDI[20];
			for (int i = 0; i < 20; i ++) {
				candi[i] = new DISCARD_CANDI();
			}
			int nPlayer = SS_GetTurn();//m_pKernel->GetTurn();
			int nCount = -1;
			/*
			int	nCandiInfo[20 * 7];
			if (m_nState == STATE_CHOICE_ATFIRST) {//exchange state
				nCount = m_pKernel.GetExchangeCandi(nPlayer, nCandiInfo);
			} else {
				nCount = m_pKernel.GetDiscardCandi(nPlayer, nCandiInfo);
			}
			int index = 0;
			for (int i = 0; i < nCount; i ++) {
				index = i * 7;
				candi[i].nPlayer     = nCandiInfo[index+0];
				candi[i].nCount      = nCandiInfo[index+1];
				candi[i].nKind       = nCandiInfo[index+2];
				candi[i].nIndices[0] = nCandiInfo[index+3];
				candi[i].nIndices[1] = nCandiInfo[index+4];
				candi[i].nIndices[2] = nCandiInfo[index+5];
				candi[i].nIndices[3] = nCandiInfo[index+6];
			}
			 */
			if (nCount == 0)
				return;
			if (nCount == -1)
			{
				m_stHandCards.bSetting[m_nFocusCardNo] = true;				
				m_nSelCardsCount ++;
				/*
				int nTurn = m_pKernel.GetTurn();
				if (m_pKernel.IsValidDiscard(nTurn, m_nSelCardNo, m_nSelCardsCount) == false)
				{
					m_nSelCardsCount --;
					m_nSelCardNo[m_nSelCardsCount] = NO_CARD;
				}
				*/
			}		
			else
			{
				if (m_nSelCardsCount != 0)
					return;
				int nIndex = -1;
				for (int i = 0; i < nCount; i ++)
				{
					if (nIndex != -1)
						break;
					for (int j = 0; j < candi[i].nCount; j ++)
					{
						if (m_nFocusCardNo == candi[i].nIndices[j])
						{
							nIndex = i;
							break;
						}
					}					
				}
				if (nIndex == -1)
					return;
				m_nSelCardsCount = candi[nIndex].nCount;
				for (int i = 0; i < m_nSelCardsCount; i ++)
					m_stHandCards.bSetting[candi[nIndex].nIndices[i]] = true;
			}
		}
	}

	private void CommandDown() {
		if (m_nSelCardsCount > 0 && IsSelectedCard(m_nFocusCardNo) != false)
		{
			DeleteSelectedCard(m_nFocusCardNo);
		}
	}

	private void CommandTouch(int nParam) {
		m_nFocusCardNo = nParam;
		if (IsSelectedCard(nParam))
			CommandDown();
		else
			CommandUp();
	}

	private void CommandSet() {
		int nPlayer = SS_GetTurn();
		int j = 0;
		for (int i = 0; i < m_nHandCardsCount; i ++)
		{
			if (m_stHandCards.bSetting[i])
			{
				m_nSelCardNo[j] = i;
				j ++;
			}				
		}
		STD.ASSERT(j == m_nSelCardsCount);
		if (j == 0)
		{
			m_nNextState = STATE_CHOOSING_ERROR;
			return;
		}
			
		if ( (m_nState == STATE_CHOOSING && m_pKernel.IsValidDiscard(nPlayer, m_nSelCardNo, m_nSelCardsCount) == false) ||
			 (m_nState == STATE_CHOICE_ATFIRST && (IsEnableExchange(true) == false || IsValidExchange() == false) ) )
		{
			//SetGameState(STATE_CHOOSING_ERROR);
			m_nNextState = STATE_CHOOSING_ERROR;
		}
		else
		{
			ACTION act = new ACTION();
			if (m_nState == STATE_CHOICE_ATFIRST)
			{
				act.nKind = ACTION.EXCHANGE;
				act.nCount = m_nSelCardsCount;
				for (int i = 0; i < m_nSelCardsCount; i ++)
					act.nIndices[i] = m_nSelCardNo[i];
			}				
			else
			{
				act.nKind = ACTION.DISCARD;
				act.nCount = m_nSelCardsCount;
				for (int i = 0; i < m_nSelCardsCount; i ++)
					act.nIndices[i] = m_nSelCardNo[i];
			}
				
			act.nPlayer = nPlayer;
			int nState = KernelAction(act);
			/*
			ACTION_RESULT nRetCode = m_pKernel->Action(act);

			int nState = STATE_ACTION_DISCARD;
			switch (nRetCode)
			{
			case ACTION_OK:
				nState = STATE_ACTION_DISCARD;
				break;
			case ACTION_FAIL:
				ASSERT(false);
				nState = STATE_ACTION_CHOOSING;
				break;
			case ACTION_FOUL:
				nState = STATE_HASOKU;
				break;
			case ACTION_REVOLUTION_SET:
				nState = STATE_REVOL_SET;
				break;
			case ACTION_REVOLUTION_FREE:
				nState = STATE_REVOL_FREE;
				break;
			case ACTION_GAME_END:
				nState = STATE_WINORLOOSE;
				break;
			}
			*/
			//SetGameState(nState);	
			m_nNextState = nState;
		}			
	}

	private void CommandPass() {
		int	nPlayer = m_pKernel.GetTurn();
		ACTION act = new ACTION();
		act.nKind = ACTION.PASS;
		act.nPlayer = nPlayer;
		int nState = KernelAction(act);
		/*
		ACTION_RESULT nRetCode = m_pKernel->Action(act);
		int nState = STATE_ACTION_PASS;
		switch (nRetCode)
		{
		case ACTION_OK:
			nState = STATE_ACTION_PASS;
			break;
		case ACTION_FAIL:
			//ASSERT(false);
			nState = STATE_CHOOSING_ERROR;
			break;
		case ACTION_FOUL:
			nState = STATE_HASOKU;
			break;
		case ACTION_REVOLUTION_SET:
			nState = STATE_REVOL_SET;
			break;
		case ACTION_REVOLUTION_FREE:
			nState = STATE_REVOL_FREE;
			break;
		case ACTION_GAME_END:
			nState = STATE_WINORLOOSE;
			break;
		}
		*/
		//SetGameState(nState);	
		m_nNextState = nState;
		//SetGameState(STATE_ACTION_PASS);
	}

	private boolean IsSelectedCard(int nNo) {
	/*
		int nRet = -1;
		for (int i = 0; i < m_nSelCardsCount; i ++)
		{
			if (m_nSelCardNo[i] == nNo)
			{
				nRet = i;
				break;
			}
		}*/
		return m_stHandCards.bSetting[nNo];
	}

	private int DeleteSelectedCard(int nCardNo) {
		int nIndex = 0, i;
		int nPlayer = SS_GetTurn();//m_pKernel.GetTurn();
		//DISCARD_CANDI candi[20];
		//int nCount = m_pKernel.GetDiscardCandi(nPlayer, null);
		//if (nCount == -1)
		{
			m_stHandCards.bSetting[nCardNo] = false;
			m_nSelCardsCount --;		
		}
		/*
		else
		{
			memset(m_stHandCards.bSetting, false, sizeof(m_stHandCards.bSetting));
			m_nSelCardsCount = 0;
		}
		*/
		return 0;	
	}
	//********************* Action Proc - End **********************************//

	//********************* State Proc - Start ********************************//
	private void SetGameState(int nState) {
		if (m_pGameWnd == null)
			return;
		m_nPrevState = m_nState;
		m_nState = nState;
		switch (nState)
		{
		case STATE_NONE:											break;
		case STATE_RULE:				State_Rule();				break;
		case STATE_INITIALIZE:			State_Initialize();			break;
		case STATE_DOING_DEAL:										break;
		case STATE_INITIAL_ATFIRST:		State_Initial_AtFirst();	break;
		case STATE_CHOICE_ATFIRST:		State_Choice_AtFirst();		break;
		case STATE_NPC_CHOICE_ATFIRST:	State_NPC_Choice_AtFirst();	break;
		case STATE_AFTER_ATFIRST:		State_After_AtFirst();		break;
		case STATE_INITIAL_CHOOSING:	State_Initial_Choosing();	break;
		case STATE_CHOOSING:			State_Choosing();			break;
		case STATE_CHOOSING_ERROR:		State_Choosing_Error();		break;
		case STATE_NPC_CHOOSING:		State_NPC_Choosing();		break;
		case STATE_ACTION_CHOOSING:		State_Action_Choosing();	break;
		case STATE_ACTION_DISCARD:		State_Action_Discard();		break;
		case STATE_ACTION_PASS:			State_Action_Pass();		break;
		case STATE_ACTION_EXCHANGE:									break;
		case STATE_UPDATE_PLAYER:		State_Update_Player();		break;
		case STATE_INITIAL_NAGASI:									break;
		case STATE_NAGASI:											break;
		case STATE_AGARI:				State_Agari();				break;
		case STATE_8KIRI:				State_8Kiri();				break;
		case STATE_HASOKU:				State_Hasoku();				break;
		case STATE_REVOL_SET:			State_Revol_Set();			break;
		case STATE_REVOL_FREE:			State_Revol_Free();			break;
		case STATE_MIYAKOOTI:			State_MiyakoOti();			break;
		case STATE_SIBARI:				State_Sibari();				break;
		case STATE_SPADE3:				State_Spade3();				break;
		case STATE_NOCARD:				State_NoCard();				break;
		case STATE_RESULT:				State_Result();				break;
		case STATE_READY_NEXTGAME:		State_ReadyNextGame();		break;
		case STATE_READY_EXCHANGE:		State_ReadyExchange();		break;
		case STATE_READY_RESTART:		State_ReadyRestart();		break;
		case STATE_WIN:					State_Win();				break;
		case STATE_LOOSE:				State_Loose();				break;
		case STATE_GAME_END:			State_GameEnd();			break;
		}
		DBGLog_State(nState);
		
		m_pGameWnd.OnChangeState(nState);
	}
	/*
	void CGameLogic::InitHandCards()
	{
		m_nFocusCardNo = 0;
	}
	*/
	private void State_Rule() {
		InitEngineRule();
		DBG_SetTumi();
		InitOneGame();
		InitAction();
		m_nNextState = STATE_INITIAL_CHOOSING;
	}

	private void State_Initialize() {
		m_nUserSoldiers -= m_nUserDownSoldiers;
		m_nNPCSoldiers -= m_nNPCDownSoldiers;
		
		m_nGamePlayingCount ++;
		if (m_nGamePlayingCount > 999)
			m_nGamePlayingCount = 999;
		m_pKernel.RestartGame();
		InitOneGame();
		InitAction();
		m_nNextState = STATE_READY_EXCHANGE;//STATE_INITIAL_CHOOSING;
	}

	private void State_Initial_AtFirst() {
		int nPlayer = GetPlayerFromKind(m_nExchangeKind);
		if (IsUserExchange(nPlayer))
			m_nNextState = STATE_CHOICE_ATFIRST;
		else
			m_nNextState = STATE_NPC_CHOICE_ATFIRST;
	}

	private void State_Choice_AtFirst() {
		int nPlayer = GetPlayerFromKind(m_nExchangeKind);
		InitHandCardsInfo();
		SetHandCardsInfo();
		m_nNextState = STATE_AFTER_ATFIRST;
	}

	private void State_NPC_Choice_AtFirst() {
		int nPlayer = GetPlayerFromKind(m_nExchangeKind);
		
		int action[] = new int[7];
		m_pKernel.ThinkExchange(nPlayer, action);
		ACTION act = new ACTION();
		act.nKind       = action[0];
		act.nPlayer     = action[1];
		act.nCount      = action[2];
		act.nIndices[0] = action[3];
		act.nIndices[1] = action[4];
		act.nIndices[2] = action[5];
		act.nIndices[3] = action[6];
		
		int nState = KernelAction(act);	
		m_nNextState = nState;
	}

	private void State_After_AtFirst() {
		m_nExchangeKind ++;
		if (m_nExchangeKind == HEIMIN)
			m_nExchangeKind ++;
		if (m_nExchangeKind >= MAX_KIND)
			m_nNextState = STATE_UPDATE_PLAYER;
		else
			m_nNextState = STATE_INITIAL_ATFIRST;
	}

	private void State_Initial_Choosing() {
		InitAction();
		int nWho = SS_IsWhoAction();
		if (nWho == -1)
		{
			m_nExchangeKind = DAIFUGOU;
			m_nNextState = STATE_INITIAL_ATFIRST;
		}
		else if (nWho == 0)
		{
			m_nNextState = STATE_CHOOSING;
			//SetGameState(STATE_CHOOSING);
		}
		else
		{
			m_nNextState = STATE_NPC_CHOOSING;
			//SetGameState(STATE_NPC_CHOOSING);
		}
	}

	private void State_Choosing() {
		InitHandCardsInfo();
		SetHandCardsInfo();	
		m_nNextState = STATE_ACTION_CHOOSING;
	}

	private void State_Choosing_Error() {
		m_nNextState = m_nPrevState;
	}

	private void State_NPC_Choosing() {
		m_nNextState = STATE_ACTION_CHOOSING;
		int nPlayer = SS_GetTurn();//m_pKernel.GetTurn();

		int action[] = new int[7];
		m_pKernel.ThinkDiscard(nPlayer, action);
		ACTION ret = new ACTION();
		ret.nKind       = action[0];
		ret.nPlayer     = action[1];
		ret.nCount      = action[2];
		ret.nIndices[0] = action[3];
		ret.nIndices[1] = action[4];
		ret.nIndices[2] = action[5];
		ret.nIndices[3] = action[6];
		/*
		if (!m_pKernel->IsValidDiscard(ret))
		{
			return;
		}
		*/
		/*
		
		ACTION_RESULT nRet = m_pKernel->Action(ret);
		
		switch (nRet)
		{
		case ACTION_OK:
			if (ret.nKind == ACTION.DISCARD)
				nState = STATE_ACTION_DISCARD;
			else if (ret.nKind == ACTION.PASS)
				nState = STATE_ACTION_PASS;
			else
				nState = STATE_ACTION_PASS;
			break;
		case ACTION_FAIL:
			nState = STATE_ACTION_CHOOSING;
			break;
		case ACTION_FOUL:
			nState = STATE_HASOKU;
			break;
		case ACTION_REVOLUTION_SET:
			nState = STATE_REVOL_SET;
			break;
		case ACTION_REVOLUTION_FREE:
			nState = STATE_REVOL_FREE;
			break;
		case ACTION_GAME_END:
			nState = STATE_WINORLOOSE;
			break;
		}*/
		int nState = KernelAction(ret);
		//SetGameState(nState);
		m_nNextState = nState;
	}

	private void State_Action_Choosing() {
		m_nNextState = STATE_UPDATE_PLAYER;
	}

	private void State_Action_Discard() {
		SetStateFromAction();
		//m_nNextState = STATE_UPDATE_PLAYER;
	}

	private void State_Action_Pass() {
		m_nNextState = STATE_UPDATE_PLAYER;
	}

	private void State_Action_Exchange() {
		m_nNextState = STATE_UPDATE_PLAYER;
	}

	private void State_Update_Player() {
		ACTION act = new ACTION();
		act.nKind = ACTION.TURN_NEXT;
		act.nPlayer = SS_GetTurn();//m_pKernel.GetTurn();
		int nState = KernelAction(act);
		/*
		int nRetCode = m_pKernel->Action(act);
		int nState = STATE_INITIAL_CHOOSING;
		switch (nRetCode)
		{
		case ACTION_OK:
			nState = STATE_INITIAL_CHOOSING;
			break;
		case ACTION_FAIL:
			nState = STATE_ACTION_CHOOSING;
			break;
		case ACTION_FOUL:
			nState = STATE_HASOKU;
			break;
		case ACTION_REVOLUTION_SET:
			nState = STATE_REVOL_SET;
			break;
		case ACTION_REVOLUTION_FREE:
			nState = STATE_REVOL_FREE;
			break;
		case ACTION_GAME_END:
			nState = STATE_WINORLOOSE;
			break;
		}
		*/
		//SetGameState(nState);
		m_nNextState = nState;
	}

	private void SetStateFromAction() {
		int nState = InterpretAction();
		if (nState == STATE_NONE)
			nState = STATE_UPDATE_PLAYER;
		m_nNextState = nState;	
	}

	private void State_8Kiri() {
		SetStateFromAction();
	}

	private void State_Agari() {	
		SetStateFromAction();
	}

	private void State_Hasoku() {
		if (m_pKernel.GetTurn() == PLAYER_USER)
			UpdateGameResult(Globals.RESULT_AGARI);
		SetStateFromAction();
	}

	private void State_Revol_Set() {
		if (m_pKernel.GetTurn() == PLAYER_USER)
			UpdateGameResult(Globals.RESULT_KAKUMEI);
		SetStateFromAction();
	}

	private void State_Revol_Free() {
		SetStateFromAction();
	}

	private void State_MiyakoOti() {
		if (SS_GetPlayerFromKind(DAIFUGOU) == PLAYER_USER)
			UpdateGameResult(Globals.RESULT_MIYAKO);
		SetStateFromAction();
	}

	private void State_Sibari() {
		if (m_pKernel.GetTurn() == PLAYER_USER)
			UpdateGameResult(Globals.RESULT_SIBARI);
		SetStateFromAction();
	}

	private void State_Spade3() {
		if (m_pKernel.GetTurn() == PLAYER_USER)
			UpdateGameResult(Globals.RESULT_SPADE3);
		SetStateFromAction();
	}

	private void State_NoCard() {
		SetStateFromAction();
	}

	private void State_Result() {
		DBG_SaveContext();
		m_pKernel.FinishOneGame();
		//ChangePlacePlayer();
		CalcSoldiers();
		//m_nGamePlayingCount ++;
		UpdateGameResult(Globals.RESULT_GAME);
		UpdateGameResult(Globals.RESULT_KUNSYU + m_pKernel.GetPlayerKind(PLAYER_USER));	
		
		if (Globals.g_Global.GetGameMode() == Globals.GM_UNIFY)
		{
			if (m_nUserSoldiers-m_nUserDownSoldiers <= 0)
				m_nNextState = STATE_LOOSE;
			else if (m_nNPCSoldiers-m_nNPCDownSoldiers <= 0)
				m_nNextState = STATE_WIN;
			else
				m_nNextState = STATE_READY_NEXTGAME;		
		}
		else
			m_nNextState = STATE_GAME_END;
	}

	private void State_ReadyNextGame() {
		m_nNextState = STATE_INITIALIZE;
	}

	private void State_ReadyExchange() {
		m_nNextState = STATE_INITIAL_CHOOSING;
	}

	private void State_ReadyRestart() {
		m_nNextState = STATE_INITIAL_CHOOSING;
	}

	private void State_Win() {
		m_nNextState = STATE_INITIALIZE;
	}

	private void State_Loose() {
		m_nNextState = STATE_INITIALIZE;
	}

	private void State_GameEnd() {	
		m_nNextState = STATE_INITIALIZE;
	}


	//********************* State Proc - End **********************************//
	private void ChangePlacePlayer() {
		int nUserKind = m_pKernel.GetPlayerKind(PLAYER_USER);
		m_nPlacePlayer[0] = PLAYER_USER;
		int nNextKind;
		for (int i = 1; i < PLAYER_COUNT; i ++)
		{
			nNextKind = (nUserKind + (5-i)) % PLAYER_COUNT;
			m_nPlacePlayer[i] = GetPlayerFromKind(nNextKind);
		}
	}

	private int GetPlayerFromKind(int nKind) {
		int nPlayer = PLAYER_USER; 
		for (int i = PLAYER_USER; i < PLAYER_COUNT; i ++)
		{
			if (nKind == m_pKernel.GetPlayerKind(i))
			{
				nPlayer = i;
				break;
			}
		}
		return nPlayer;
	}

	private boolean IsUserExchange(int nPlayer) {
		if (Globals.DEBUG_AUTO_GAME == true)
			return false;
		else if (Globals.DEBUG_MANUAL_GAME == true)
			return false;

		if (Globals.TEST_TOOL == true) {
			if (IsAutoGame())
				return false;
		}

		return (nPlayer == PLAYER_USER) ? true : false;
	}

	private boolean IsEnableExchange(boolean bCardCnt) {
		boolean bRet = false;
		if (m_nState != STATE_CHOICE_ATFIRST)
			return false;
		
		if (bCardCnt == false && m_nSelCardsCount > 0)
			return true;
		
		switch(m_nExchangeKind)
		{
		case DAIFUGOU:
		case DAIHINMIN:
			bRet = (m_nSelCardsCount == 2) ? true : false;
			break;	
		case FUGOU:
		case HINMIN:
			bRet = (m_nSelCardsCount == 1) ? true : false;
			break;
		}
		
		return bRet;
	}

	private boolean IsValidExchange() {
		boolean bRet = false;
		DISCARD_CANDI candi[] = new DISCARD_CANDI [20];
		for (int i = 0; i < 20; i ++) {
			candi[i] = new DISCARD_CANDI();
		}
		
		int nPlayer = SS_GetTurn();//m_pKernel.GetTurn();			
		
		int	nCandiInfo[] = new int [20 * 7];
	 	int nCount = m_pKernel.GetExchangeCandi(nPlayer, nCandiInfo);
		int index = 0;
		for (int i = 0; i < nCount; i ++) {
			index = i * 7;
			candi[i].nPlayer     = nCandiInfo[index+0];
			candi[i].nCount      = nCandiInfo[index+1];
			candi[i].nKind       = nCandiInfo[index+2];
			candi[i].nIndices[0] = nCandiInfo[index+3];
			candi[i].nIndices[1] = nCandiInfo[index+4];
			candi[i].nIndices[2] = nCandiInfo[index+5];
			candi[i].nIndices[3] = nCandiInfo[index+6];
		}
		
	 	if (nCount == -1)
	 		return true;
	 	int i, j;
		for (i = 0; i < nCount; i ++)
		{
			for (j = 0; j < candi[i].nCount; j ++)
			{
				if (m_stHandCards.bSetting[candi[i].nIndices[j]] == false)
					break;
			}
			if (j == candi[i].nCount)
			{					
				bRet = true;
				break;	
			}
		}
		return bRet;
	}

	private void CalcSoldiers() {
		int nSoldiers = 0;
		int nPlayerKind = m_pKernel.GetPlayerKind(PLAYER_USER);
		int nNPCKind = m_pKernel.GetPlayerKind(PLAYER_CPU1);
		CHARATER_INFO userInfo, npcInfo;
		userInfo = Globals.GetCharacterInfo(m_nPlayerChr[PLAYER_USER]);
		npcInfo = Globals.GetCharacterInfo(m_nPlayerChr[PLAYER_CPU1]);

		m_nUserDownSoldiers = 0;
		m_nNPCDownSoldiers = 0;
		//int nNPC = 
		//calc user soldiers
		if (nPlayerKind == DAIFUGOU || nPlayerKind == FUGOU)
		{
			nSoldiers = nAddSoldiers[nPlayerKind] + (Globals.g_nCalcSoldierRate[nPlayerKind] * userInfo.nAttack / 10) - (Globals.g_nCalcSoldierRate[nNPCKind] * npcInfo.nWisdom / 20);
			if (nSoldiers > 0)
			{
				//m_nNPCSoldiers -= nSoldiers;
				m_nNPCDownSoldiers = nSoldiers;
			}			
		}
		else
		{
			nSoldiers = nAddSoldiers[nPlayerKind] + (Globals.g_nCalcSoldierRate[nNPCKind] * npcInfo.nAttack / 10) - (Globals.g_nCalcSoldierRate[nPlayerKind] * userInfo.nWisdom / 20);
			if (nSoldiers > 0)
			{
				//m_nUserSoldiers -= nSoldiers;
				m_nUserDownSoldiers = nSoldiers;
			}			
		}	
	}

	private void UpdateGameResult(int nResultType) {
		if (nResultType >= Globals.RESULT_COUNT)
			return;
		int nVal = m_infoResult.getVals(nResultType);
		nVal ++;	
		m_infoResult.setVals(nResultType, STD.MIN(nVal, Globals.MAX_LIMIT));
	}

	private void SetEngineLevel() {
		for (int i = PLAYER_USER; i < PLAYER_COUNT; i ++)
		{
			CHARATER_INFO info = Globals.GetCharacterInfo(m_nPlayerChr[i]);		
			m_pKernel.SetPlayerLevel(i, info.nLevel-1);
		}
	}
	//********************* Debug Proc - Start **********************************//
	private boolean IsAutoGame() {
		if (Globals.TEST_TOOL == true) {
			return Globals.m_bAutoGame;
		}

		return false;
	}
	//#define SAVE_TUMI
	private boolean DBG_SetTumi() {
		if (Globals.DEBUG_TUMI == false)
			return true;

		if (Globals.DEBUG_NANDTUMI == false) {
			return DBG_LoadTumi();
		} else {
	//		CFile f;
			String	szFileName = "tumikomi.txt";
			/*
			CFile file;
			if (!file.Open(szFileName, CCheckFile::modeNandRead ) )
			{
				DBG_LoadTumi();
				return false;
			}
			
			int nReadBytes = (int) file.GetLength();
			byte pData[] = new byte[nReadBytes+1];
			STD.MEMSET(pData, (byte)0);
			nReadBytes = file.Read(pData, nReadBytes);
			file.Close();
			*/
			byte pData[] = CConFile.read(szFileName);
			int nReadBytes = pData.length;
			if (nReadBytes == -1) {
				DBG_LoadTumi();
				return false;
			}
				
			
			m_pKernel.SetGameContextByTumikomi(pData, nReadBytes);
			pData = null;
			
			//ChangePlacePlayer();
			//set game rule	
			boolean bRuleVals[] = new boolean [Globals.RULE_COUNT];
			m_pKernel.GetRule(bRuleVals);
			for (int i = 0; i < Globals.RULE_COUNT; i ++)
				m_infoRule.setVals(i, bRuleVals[i]);
		}
		return true;
	}

	private boolean DBG_LoadTumi() {
		/*kgh
		char szFileName[256] = "/data/Tumi/tumikomi.txt";
		CFile file;
		if (!file.Open(szFileName, CCheckFile::modeRead ) )
		{
			return false;
		}
		
		int nReadBytes = (int) file.GetLength();
		unsigned char* pData;
		pData = new unsigned char[nReadBytes+1];
		memset(pData, 0, nReadBytes+1);
		nReadBytes = file.Read(pData, nReadBytes);
		file.Close();
//		SaveNandFile("tumikomi.txt", pData, nReadBytes, true);
		
		m_pKernel.SetGameContextByTumikomi((char*)pData, nReadBytes);
		delete[] pData;
		
		//ChangePlacePlayer();
		//set game rule	
		bool bRuleVals[Globals.RULE_COUNT];
		m_pKernel.GetRule(bRuleVals);
		for (int i = 0; i < Globals.RULE_COUNT; i ++)
			m_infoRule.setVals(i, bRuleVals[i]);
	  */
		return true;
	}

	private boolean DBG_SaveContext() {
		if (Globals.DEBUG_CHECKKIFU == false) 
			return true;

		int nSize;
		nSize = m_pKernel.GetGameContext(null, 0);
		byte pData[] = new byte[nSize];	
		m_pKernel.GetGameContext(pData, nSize);
		if (pData == null) {
			return false;
		}
			
		boolean bRet = CConFile.write("AutoKifu.dat", pData);
		pData = null;
		return bRet;
	}

	private void DBGLog_Action(int nActionNum) {
		if (Globals.DEBUG_LOG == false)
			return;
		
		int	nTurn = m_pKernel.GetTurn();
		String	strLog = String.format("Action(%d) : %s\n", nTurn, m_debug_szAction[nActionNum]);
		STD.logout(strLog);
	}

	private void DBGLog_State(int nState) {
		if (Globals.DEBUG_LOG == false)
			return;

		int	nTurn = m_pKernel.GetTurn();
		String	strLog = String.format("State(%d) : %s\n", nTurn, m_debug_szState[nState]);
		STD.logout(strLog);
	}
	
	public void SS_SetGamePlayingCount(int nCount) {
		m_nGamePlayingCount = nCount;
	}
	public Globals.RESULT_INFO SS_GetGameResult() {
		return m_infoResult;
	}
	public Globals.RULE_INFO SS_GetGameRule() {
		return m_infoRule;
	}

	/*
	 * Fugou Kernel
	 */
	public static final int MAX_PLAYERS = 5;

	public static final int
		CARD_3  = 1,
		CARD_4  = 2,
		CARD_5  = 3,
		CARD_6  = 4,
		CARD_7  = 5,
		CARD_8  = 6,
		CARD_9  = 7,
		CARD_10 = 8,
		CARD_J  = 9,
		CARD_Q  = 10,
		CARD_K  = 11,
		CARD_A  = 12,
		CARD_2  = 13,
		CARD_JOKER = 15;

	//player kind
	public static final int
		DAIFUGOU  = 0,	//君主(大富豪)
		FUGOU     = 1,	//軍師(富豪)
		HEIMIN    = 2,	//武将(平民)
		HINMIN    = 3,	//隊長(貧民)
		DAIHINMIN = 4,	//兵卒(大貧民)
		MAX_KIND  = 5;

	//discard cards kind
	// DISCARD_CARDS_KIND
	public static final int
		SINGLE = 0,
		DUAL   = 1,
		TRIPLE = 2,
		QUAD   = 3,
		SEQ_3  = 4,
		SEQ_4  = 5,
		GIVE_1 = 6,				//In this case, DISCARD_CARD_INFO::nTo means receiver.
		GIVE_2 = 7,				//In this case, DISCARD_CARD_INFO::nTo means receiver.
		PASS   = 8;

	//cards sign
	public static final int
		CLUB    = 1 << 0,
		DIAMOND = 1 << 1,
		HEART   = 1 << 2,
		SPADE   = 1 << 3;

	public static final int MAX_CARD_NUM = 15;
	
	// LEVEL
	public static final int
		LEVEL_1 = 0,
		LEVEL_2 = 1,
		LEVEL_3 = 2,
		LEVEL_4 = 3,
		LEVEL_5 = 4;

	public static final int
		ACTION_FAIL            = 0,
		ACTION_OK              = 1 << 0,
		ACTION_FOUL            = 1 << 1,	//反則
		ACTION_REVOLUTION_SET  = 1 << 2,
		ACTION_REVOLUTION_FREE = 1 << 3,
		ACTION_GAME_END        = 1 << 4,
		ACTION_AGARI           = 1 << 5,	//あがり
		ACTION_DAIFUGOU_FALL   = 1 << 6,	//都落ち
		ACTION_8KIRI           = 1 << 7,	//８切り
		ACTION_SIBARI          = 1 << 8,	//しばり
		ACTION_SPADE3          = 1 << 9;	//スペ３
	
	public static int GetCardSign(int a) {
		return (a >> 4) & 0x0F;
	}
	
	public static int GetCardNumber(int a) {
		return (a) & 0x0F;
	}
	
	public static int MakeCard(int a, int b) {
		return ((a) << 4) | ((b) & 0x0F);
	}

	//discard cards info
	public static class DISCARD_CARDS_INFO {
		public int nFrom;
		public int	nKind;
		public int nCount;
		public int nCards[] = new int [4];
		
		public DISCARD_CARDS_INFO() {
			
		}
	};

	//discard candidate
	public static class DISCARD_CANDI {
		public int nPlayer;
		public int nCount;
		public int	nKind;
		public int nIndices[] = new int[4];
		
		public DISCARD_CANDI() {
			
		}
	};

	// typedef unsigned int ACTION_RESULT;
	
	public static class ACTION {
		// ACTION_KIND
		public static final int
			EXCHANGE  = 0,
			DISCARD   = 1,
			PASS      = 2,
			TURN_NEXT = 3;
		
		public int nKind;
		public int nPlayer;
		public int nCount;
		public int nIndices[] = new int [4];
		
		public ACTION() {
			
		}
	};
}
