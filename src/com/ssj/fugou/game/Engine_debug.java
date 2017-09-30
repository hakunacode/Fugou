package com.ssj.fugou.game;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

import com.dlten.lib.STD;
import com.dlten.lib.file.CConFile;
import com.ssj.fugou.Globals;

public class Engine_debug {

	private final boolean _DBG = false;
	private final boolean	_LOG = false;
	
	private static final int	MAX_LOG_LINES = 100;
	private Engine	m_pKernel = new Engine();
	
	private String	m_LogBuf[] = new String[MAX_LOG_LINES];
	private int		m_LogCount = 0;
	
	public static class ACTION {
		public static int
			EXCHANGE  = 0,
			DISCARD   = 1,
			PASS      = 2,
			TURN_NEXT = 3;

		int nKind;
		int nPlayer;

		int nCount;
		int nIndices[] = new int[4];
		
		public void set(DataInputStream dis) {
			try {
				nKind = reverse( dis.readInt() );
				nPlayer = reverse( dis.readInt() );
				nCount = reverse( dis.readInt() );
				for (int i = 0; i < 4; i ++) {
					nIndices[i] = reverse( dis.readInt() );
				}
			} catch (Exception e) {
			}
		}
	};

	public static class ENGINE_DISCARD_CARDS_INFO {
		int nFrom;
		int	nKind;
		int nCount;
		int nCards[] = null;
		
		public ENGINE_DISCARD_CARDS_INFO() {
			nFrom = 0;
			nKind = 0;
			nCount = 0;
			nCards = new int[4];
		}
		
		public void set(DataInputStream dis) {
			try {
				nFrom = reverse( dis.readInt() );
				nKind = reverse( dis.readInt() );
				nCount = reverse( dis.readInt() );
				for (int i = 0; i < 4; i ++) {
					nCards[i] = reverse( dis.readInt() );
				}
			} catch (Exception e) {
			}
		}
	};
	
	public static class CFugouRule {
		public static int
		REVOLUTION         =  0,	//革命
		PRESSURE           =  1,	//都落ち
		SPADE_3            =  2,	//スペ３
		SIBARI             =  3,	//しばり
		ONLY_8_TOP         =  4,	//8切り
		FORBID_JOKER_AGARI =  5,	//ジョーカーあがり禁止								
		FORBID_2_AGARI     =  6,	//２あがり禁止								
 		PLAYERS            =  7,	//プレイ人数　5人(false)　or 　4人(true)
		JOKER_COUNT        =  8,	//ジョーカー1枚(false)　or 　2枚(true)
// 		SEQUENCE           =  9,	//階段
// 		SEQ_REVOLUTION     = 10,	//階段革命
// 		J_BACK             = 11,	//イレブンバック（11バック）
// 		SEQ_8_TOP          = 12,	//8切り階段
		MAX_RULE_COUNT     =  9;
		
		int	m_bRuleValue[] = new int[MAX_RULE_COUNT];
		
		public void set(DataInputStream dis) {
			try {
				for (int i = 0; i < MAX_RULE_COUNT; i ++) {
					m_bRuleValue[i] = reverse( dis.readInt() );
				}
			} catch (Exception e) {
			}
		}
	}

	public static class FUGOU_CONTEXT {
		public static int MAX_HAND_CARDS = 20;
		public static int MAX_HISTORY_COUNT = 200;

		int		m_nPlayerCount;
		int		m_nPlayerKind[] = null;
		int		m_vIniHandCards[][] = null;
		int		m_nIniTurn;
		int		m_nHistoryCount;
		ENGINE_DISCARD_CARDS_INFO	m_vHistory[] = null;
		CFugouRule	m_Rule = null;
		ACTION		m_theFirstAction = null;//for Engine Test
		
		public FUGOU_CONTEXT() {
			int i;
			
			m_nPlayerCount = 0;
			m_nPlayerKind = new int [GameLogic.MAX_PLAYERS];
			
			m_vIniHandCards = new int [GameLogic.MAX_PLAYERS][];
			for (i = 0; i < GameLogic.MAX_PLAYERS; i++) {
				m_vIniHandCards[i] = new int [MAX_HAND_CARDS];
			}
			m_nIniTurn = 0;
			m_nHistoryCount = 0;
			
			m_vHistory = new ENGINE_DISCARD_CARDS_INFO[MAX_HISTORY_COUNT];
			for (i = 0; i < MAX_HISTORY_COUNT; i++) {
				m_vHistory[i] = new ENGINE_DISCARD_CARDS_INFO();
			}

			m_Rule = new CFugouRule();

			m_theFirstAction = new ACTION();//for Engine Test
		}
		
		public void set(byte buf[]) {
			try {
				ByteArrayInputStream	bis = new ByteArrayInputStream(buf);
				DataInputStream			dis = new DataInputStream(bis);
				int i, j;
				
				m_nPlayerCount = reverse( dis.readInt() );
				for (i = 0; i < GameLogic.MAX_PLAYERS; i ++) {
					m_nPlayerKind[i] = reverse( dis.readInt() );
				}
				
				for (i = 0; i < GameLogic.MAX_PLAYERS; i ++) {
					for (j = 0; j < MAX_HAND_CARDS; j++) {
						m_vIniHandCards[i][j] = reverse( dis.readInt() );
					}
				}

				m_nIniTurn = reverse( dis.readInt() );
				m_nHistoryCount = reverse( dis.readInt() );
				
				for (i = 0; i < MAX_HISTORY_COUNT; i++) {
					m_vHistory[i].set(dis);
				}

				m_Rule.set(dis);

				m_theFirstAction.set(dis);
				
				dis.close();
				bis.close();
			} catch (Exception e) {
			}
		}
	};
	
	public static int reverse(int val) {
		int	nRet = 0;
		byte	bOld[] = new byte[4];
		byte	bNew[] = new byte[4];
		STD.Int2Bytes(bOld, 0, val);
		bNew[0] = bOld[3];
		bNew[1] = bOld[2];
		bNew[2] = bOld[1];
		bNew[3] = bOld[0];
		nRet = STD.Bytes2Int(bNew, 0);
		
		return nRet;
	}
	
	public static short reverse(short val) {
		short	nRet = 0;
		byte	bOld[] = new byte[2];
		byte	bNew[] = new byte[2];
		STD.Short2Bytes(bOld, 0, val);
		bNew[0] = bOld[1];
		bNew[1] = bOld[0];
		nRet = STD.Bytes2Short(bNew, 0);
		return nRet;
	}
	
	private void DBG_GetContext(boolean bProc) {
		if (_DBG == false)
			return;
		
		if (bProc == false)
			return;
		
		if (_LOG == true) {
			m_LogCount = getLogCount();
			for (int i = 0; i < MAX_LOG_LINES; i ++) {
				m_LogBuf[i] = getLog(i);
			}
		}
		
		int	size[] = new int [18];
		m_pKernel.getSize(size);
		size = null;
		
		int nSize;
		nSize = m_pKernel.GetGameContext(null, 0);
		byte pData[] = new byte[nSize];
		m_pKernel.GetGameContext(pData, nSize);
		FUGOU_CONTEXT	context = new FUGOU_CONTEXT();
		context.set(pData);
		
		context = null;
		pData = null;
	}

	public boolean	CreateEngine() {
		return m_pKernel.CreateEngine();
	}
	public void		DeleteEngine() {
		m_pKernel.DeleteEngine();
	}
	public void		StartGame() {
		m_pKernel.StartGame();
		DBG_GetContext(true);
	}
	public void		RestartGame() {
		m_pKernel.RestartGame();
		DBG_GetContext(true);
	}
	public void		GetRule(boolean out_infoRule[]) {
		m_pKernel.GetRule(out_infoRule);
		DBG_GetContext(true);
	}
	public void		SetRule(boolean in_infoRule[]) {
		m_pKernel.SetRule(in_infoRule);
		DBG_GetContext(true);
	}
	public int		GetHandCards(int in_nPlayer, int out_nHandCards[]) {
		int nRet = m_pKernel.GetHandCards(in_nPlayer, out_nHandCards);
		if (nRet > 0) {
			int	aa = 0;
		}
		DBG_GetContext(false);
		return nRet;
	}
	public int		GetTurn() {
		return m_pKernel.GetTurn();
	}
	public int		GetPlayerKind(int in_nPlayer) {
		return m_pKernel.GetPlayerKind(in_nPlayer);
	}
	public int		GetDiscardCards(int out_pInfo[]) {
		int nRet = m_pKernel.GetDiscardCards(out_pInfo);
		if (nRet > 0) {
			int	aa = 0;
		}
		DBG_GetContext(false);
		return nRet;
	}
	public int		GetDiscardCandi(int in_nTurn, int out_pCandi[]) {
		int nRet = m_pKernel.GetDiscardCandi(in_nTurn, out_pCandi);
		if (nRet > 0) {
			int	aa = 0;
		}
		DBG_GetContext(true);
		return nRet;
	}
	public int		GetExchangeCandi(int in_nPlayer, int out_pCandi[]) {
		int nRet = m_pKernel.GetExchangeCandi(in_nPlayer, out_pCandi);
		if (nRet > 0) {
			int	aa = 0;
		}
		DBG_GetContext(true);
		return nRet;
	}
	public boolean	IsSibariState() {
		return m_pKernel.IsSibariState();
	}
	public int		GetGameContext(byte out_pBuf[], int in_bufsize) {
		return m_pKernel.GetGameContext(out_pBuf, in_bufsize);
	}
	public boolean	SetGameContext(byte in_pBuf[], int in_nSize) {
		return m_pKernel.SetGameContext(in_pBuf, in_nSize);
	}
	public boolean	IsValidDiscard(int in_nPlayer, int in_pCardIndex[], int in_nCount) {
		return m_pKernel.IsValidDiscard(in_nPlayer, in_pCardIndex, in_nCount);
	}
	public void		FinishOneGame() {
		m_pKernel.FinishOneGame();
		DBG_GetContext(true);
	}
	public void		SetPlayerLevel(int in_nPlayer, int in_nLevel) {
		m_pKernel.SetPlayerLevel(in_nPlayer, in_nLevel);
	}
	public int		Action(int in_action[]) {
		int nRet = m_pKernel.Action(in_action);
		DBG_GetContext(true);
		return nRet;
	}
	public void		ThinkExchange(int nPlayer, int out_action[]) {
		m_pKernel.ThinkExchange(nPlayer, out_action);
		DBG_GetContext(true);
	}
	public void		ThinkDiscard(int nPlayer, int out_action[]) {
		m_pKernel.ThinkDiscard(nPlayer, out_action);
		DBG_GetContext(true);
	}
	public int		GetPlayerCount() {
		return m_pKernel.GetPlayerCount();
	}
	public boolean	IsRevolution() {
		return m_pKernel.IsRevolution();
	}
	public boolean	SetGameContextByTumikomi(byte in_pBuf[], int in_nSize) {
		return m_pKernel.SetGameContextByTumikomi(in_pBuf, in_nSize);
	}
	public String	getLog(int nLineNo) {
		return m_pKernel.getLog(nLineNo);
	}
	public int		getLogCount() {
		return m_pKernel.getLogCount();
	}
}
