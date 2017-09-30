package com.ssj.fugou.game;

public class Engine {

	static {
	    System.loadLibrary("Engine");
	}
	
	public native boolean	CreateEngine();
	public native void		DeleteEngine();
	public native void		StartGame();
	public native void		RestartGame();
	public native void		GetRule(boolean out_infoRule[]);
	public native void		SetRule(boolean in_infoRule[]);
	public native int		GetHandCards(int in_nPlayer, int out_nHandCards[]);
	public native int		GetTurn();
	public native int		GetPlayerKind(int in_nPlayer);
	public native int		GetDiscardCards(int out_pInfo[]);
	public native int		GetDiscardCandi(int in_nTurn, int out_pCandi[]);
	public native int		GetExchangeCandi(int in_nPlayer, int out_pCandi[]);
	public native boolean	IsSibariState();
	public native int		GetGameContext(byte out_pBuf[], int in_bufsize);
	public native boolean	SetGameContext(byte in_pBuf[], int in_nSize);
	public native boolean	IsValidDiscard(int in_nPlayer, int in_pCardIndex[], int in_nCount);
	public native void		FinishOneGame();
	public native void		SetPlayerLevel(int in_nPlayer, int in_nLevel);
	public native int		Action(int in_action[]);
	public native void		ThinkExchange(int nPlayer, int out_action[]);
	public native void		ThinkDiscard(int nPlayer, int out_action[]);
	public native int		GetPlayerCount();
	public native boolean	IsRevolution();
	public native boolean	SetGameContextByTumikomi(byte in_pBuf[], int in_nSize);
	
	//For test
	public native void		getSize(int out_pBuf[]);
	public native String	getLog(int nLineNo);
	public native int		getLogCount();
}
