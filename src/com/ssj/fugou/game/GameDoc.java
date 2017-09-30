package com.ssj.fugou.game;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import com.dlten.lib.STD;
import com.dlten.lib.file.CConFile;
import com.ssj.fugou.Globals;
import com.ssj.fugou.game.GameLogic;

public class GameDoc {
	
	public static final int MAX_SAVE_KIFU_SIZE = 1024*8;
	
	public static final int 
		PLAYER_USER  = 0,
		PLAYER_CPU1  = 1,
		PLAYER_CPU2  = 2,
		PLAYER_CPU3  = 3,
		PLAYER_CPU4  = 4,
		PLAYER_COUNT = 5;

	private class KIFU_DATA {
		long	dwCheckSum;
		
		Globals.SaveData	stSaveData = new Globals.SaveData();
		int		nGameCount;
		int		nCurrentUserSoldiers;
		int		nCurrentNPCSoldiers;
		int		nKifuSize;
		byte	pKifuData[] = new byte [GameDoc.MAX_SAVE_KIFU_SIZE];
		
		public void Init() {
			dwCheckSum = 0;
			stSaveData.Init();
			nGameCount = 0;
			nCurrentUserSoldiers = 0;
			nCurrentNPCSoldiers = 0;
			nKifuSize = 0;
			STD.MEMSET(pKifuData, (byte)0);
		}
		
		public boolean setFromByteArray(byte[] buf) {
			boolean	bRet = false;
	        try {
	            ByteArrayInputStream baos = new ByteArrayInputStream(buf);
	            DataInputStream dis = new DataInputStream(baos);

	    		dwCheckSum = dis.readLong();
	    		
	    		stSaveData.nUserCountry = dis.readInt();
	    		stSaveData.nUserSoldiers = dis.readInt();
	    		stSaveData.nNPCCountry = dis.readInt();
	    		for (int i = 0; i < Globals.COUNTRY_MAX; i ++) {
	    			stSaveData.bCountryState[i] = dis.readBoolean();
	    		}
	    		stSaveData.bBGM = dis.readBoolean();
	    		stSaveData.bSE = dis.readBoolean();
	    		
	    		nGameCount = dis.readInt();
	    		nCurrentUserSoldiers = dis.readInt();
	    		nCurrentNPCSoldiers = dis.readInt();
	    		nKifuSize = dis.readInt();
	    		for (int i = 0; i < MAX_SAVE_KIFU_SIZE; i ++) {
	    			pKifuData[i] = dis.readByte();
	    		}

	            dis.close();
	            baos.close();
	            
	    		if (nKifuSize < 0 || nKifuSize >= MAX_SAVE_KIFU_SIZE) {
	    			return false;
	    		}		
			
				//check sum
				long checkSum = GetCheckSum();
				if (checkSum != dwCheckSum) {
					return false;
				}		
	            bRet = true;
	        }
	        catch (Exception e) {
	        	STD.printStackTrace(e);
	            bRet = false;
	        }
	        
	        return bRet;
		}
		
		public byte[] setToByteArray() {
			byte[] buf = null;
	        try {
	            ByteArrayOutputStream baos = new ByteArrayOutputStream();
	            DataOutputStream dos = new DataOutputStream(baos);

	    		dos.writeLong(dwCheckSum);
	    		
	    		dos.writeInt(stSaveData.nUserCountry);
	    		dos.writeInt(stSaveData.nUserSoldiers);
	    		dos.writeInt(stSaveData.nNPCCountry);
	    		for (int i = 0; i < Globals.COUNTRY_MAX; i ++) {
	    			dos.writeBoolean(stSaveData.bCountryState[i]);
	    		}
	    		dos.writeBoolean(stSaveData.bBGM);
	    		dos.writeBoolean(stSaveData.bSE);
	    		
	    		dos.writeInt(nGameCount);
	    		dos.writeInt(nCurrentUserSoldiers);
	    		dos.writeInt(nCurrentNPCSoldiers);
	    		dos.writeInt(nKifuSize);
	    		for (int i = 0; i < MAX_SAVE_KIFU_SIZE; i ++) {
	    			dos.writeByte(pKifuData[i]);
	    		}

	            buf = baos.toByteArray();
	            dos.close();
	            baos.close();
	        } catch (Exception e) {
	        	STD.printStackTrace(e);
	        }
	        return buf;
		}
	};
	
	private class SUSPEND_DATA {
		boolean		m_bEnableSuspend;
		KIFU_DATA	m_kifu = new KIFU_DATA();
		int			m_nState;
		int			m_nNextState;
		int			m_nReserve;
		boolean		m_bHansoku[] = new boolean[PLAYER_COUNT];
		boolean		m_bMiyakoOti[] = new boolean[PLAYER_COUNT];
		
		public void Init() {
			m_bEnableSuspend = false;
			m_kifu.Init();
			m_nState = 0;
			m_nNextState = 0;
			m_nReserve = 0;
			STD.MEMSET(m_bHansoku, false);
			STD.MEMSET(m_bMiyakoOti, false);
		}
		
		public boolean setFromByteArray(byte[] buf) {
			boolean	bRet = false;
	        try {
	            ByteArrayInputStream baos = new ByteArrayInputStream(buf);
	            DataInputStream dis = new DataInputStream(baos);
	    		
	    		m_bEnableSuspend = dis.readBoolean();
	    		
				byte	bufKifu[] = m_kifu.setToByteArray();
	    		dis.read(bufKifu);
	    		m_kifu.setFromByteArray(bufKifu);
	    		
	    		m_nState = dis.readInt();
	    		m_nNextState = dis.readInt();
	    		m_nReserve = dis.readInt();

	    		for (int i = 0; i < PLAYER_COUNT; i ++) {
	    			m_bHansoku[i] = dis.readBoolean();
	    		}
	    		for (int i = 0; i < PLAYER_COUNT; i ++) {
	    			m_bMiyakoOti[i] = dis.readBoolean();
	    		}

	            dis.close();
	            baos.close();
	            
	        	if (m_kifu.nKifuSize < 0 || m_kifu.nKifuSize >= MAX_SAVE_KIFU_SIZE) {
	        		return false;
	        	}
	        	
				//check sum
				long checkSum = GetCheckSum();
				if (checkSum != m_kifu.dwCheckSum) {
					return false;
				}
	            bRet = true;
	        }
	        catch (Exception e) {
	        	STD.printStackTrace(e);
	            bRet = false;
	        }
	        
	        return bRet;
		}
		
		public byte[] setToByteArray() {
			byte[] buf = null;
	        try {
	            ByteArrayOutputStream baos = new ByteArrayOutputStream();
	            DataOutputStream dos = new DataOutputStream(baos);

	            dos.writeBoolean(m_bEnableSuspend);
	            
				byte	bufKifu[] = m_kifu.setToByteArray();
	    		dos.write(bufKifu);
	    		
	            dos.writeInt(m_nState);
	            dos.writeInt(m_nNextState);
	            dos.writeInt(m_nReserve);

	    		for (int i = 0; i < PLAYER_COUNT; i ++) {
	    			dos.writeBoolean(m_bHansoku[i]);
	    		}
	    		for (int i = 0; i < PLAYER_COUNT; i ++) {
	    			dos.writeBoolean(m_bMiyakoOti[i]);
	    		}

	            buf = baos.toByteArray();
	            dos.close();
	            baos.close();
	        } catch (Exception e) {
	        	STD.printStackTrace(e);
	        }
	        return buf;
		}
	};
	private KIFU_DATA		m_kifuData = new KIFU_DATA();
	private SUSPEND_DATA	m_suspendData = new SUSPEND_DATA();
	private boolean			m_bRightKifu;
	
	public GameDoc() {
		m_bRightKifu = false;
		Init();
	}

	public void Init() {
		m_kifuData.Init();
		m_suspendData.Init();
		m_bRightKifu = false;
	}
	public void InitRuleInfo() {
		for (int i = 0; i < PLAYER_COUNT; i ++) {
			m_suspendData.m_bHansoku[i] = false;
			m_suspendData.m_bMiyakoOti[i] = false;
		}
	}
	
	public boolean Load() {
		return LoadKifu(Globals.KIFU_FILE);
	}
	public boolean Save() {
		return SaveKifu(Globals.KIFU_FILE);
	}
	
	public boolean LoadKifu(String fname) {
		m_bRightKifu = false;
		
        byte[] buf = CConFile.read(fname);
        if (buf != null) {
            m_bRightKifu = m_kifuData.setFromByteArray(buf);
        }

		return m_bRightKifu;
	}
	public boolean SaveKifu(String fname) {
		boolean		bRet = false;
        byte[] buf = m_kifuData.setToByteArray();
        if (buf != null) {
        	bRet = CConFile.write(fname, buf);
        }
        
        return bRet;
	}

	public boolean LoadSuspend() {
		m_bRightKifu = false;
		
        byte[] buf = CConFile.read(Globals.SUSPEND_FILE);
        if (buf != null) {
            m_bRightKifu = m_suspendData.setFromByteArray(buf);
        }

		return m_bRightKifu;
	}
	public boolean SaveSuspend() {
		boolean		bRet = false;
        byte[] buf = m_suspendData.setToByteArray();
        if (buf != null) {
        	bRet = CConFile.write(Globals.SUSPEND_FILE, buf);
        }
        
        return bRet;
	}

	// public boolean GetGameKifuFromEngine(CGameLogic* pInterface, boolean bSuspend = FALSE);
	public boolean GetGameKifuFromEngine(GameLogic pInterface, boolean bSuspend) {
		m_bRightKifu = false;
		KIFU_DATA	pKifuData;
		if (bSuspend == false) {
			pKifuData = m_kifuData;
		}
		else {
			pKifuData = m_suspendData.m_kifu;
			m_suspendData.m_nState = pInterface.SS_GetState();
			m_suspendData.m_nNextState = pInterface.SS_GetNextState();
			SetEnableSuspend(true);
		}

		Globals.g_Global.SetContinueDataFromGlobal(pKifuData.stSaveData);
		pKifuData.nGameCount           = pInterface.SS_GetGamePlayingCount();//+1;kgh
		pKifuData.nCurrentUserSoldiers = pInterface.SS_GetSoldiers(PLAYER_USER) - pInterface.SS_GetDownSoldiers(PLAYER_USER);
		pKifuData.nCurrentNPCSoldiers  = pInterface.SS_GetSoldiers(PLAYER_CPU1) - pInterface.SS_GetDownSoldiers(PLAYER_CPU1);
		
		pKifuData.nKifuSize = pInterface.SS_GetGameKifuData(pKifuData.pKifuData, GameDoc.MAX_SAVE_KIFU_SIZE);

		//check sum
		pKifuData.dwCheckSum = GetCheckSum();
		
		m_bRightKifu = true;
		return true;
	}
	
	// public boolean SetGameKifuToEngine(CGameLogic* pInterface, boolean bSuspend = FALSE);
	public boolean SetGameKifuToEngine(GameLogic pInterface, boolean bSuspend) {
		KIFU_DATA	pKifuData;
		if (bSuspend == false) {
			pKifuData = m_kifuData;
		}
		else {
			pKifuData = m_suspendData.m_kifu;
		}
		Globals.SaveData	pData = pKifuData.stSaveData;
		Globals.g_Global.SetContinueDataToGlobal(pData);
		Globals.RULE_INFO ruleInfo;//
		ruleInfo = Globals.GetCountryRuleInfo(pKifuData.stSaveData.nNPCCountry);
		Globals.g_Global.SetGameRule(ruleInfo);
		if (bSuspend) {
			Globals.g_Global.SetBGM(pData.bBGM);
			Globals.g_Global.SetSE(pData.bSE);
		}
		pInterface.SS_InitGameInfo();
		pInterface.SS_SetGamePlayingCount(pKifuData.nGameCount);	
		pInterface.SS_SetSoldiers(PLAYER_USER, pKifuData.nCurrentUserSoldiers);
		pInterface.SS_SetSoldiers(PLAYER_CPU1, pKifuData.nCurrentNPCSoldiers);
		pInterface.SS_SetGameKifuData(pKifuData.pKifuData, pKifuData.nKifuSize);
		if (bSuspend) {
			/*
			int nState = m_suspendData.m_nState;
			if (nState == STATE_NONE)
				nState = m_suspendData.m_nNextState;
			 */
			pInterface.SS_SetState(m_suspendData.m_nState);
			pInterface.SS_SetNextState(m_suspendData.m_nState);
		}
		return true;
	}
	
	// public boolean GetSaveDataFromGlobal(boolean bSuspend = TRUE);
	public boolean GetSaveDataFromGlobal(boolean bSuspend) {
		Globals.SaveData	pSaveData;
		if (bSuspend == false) {
			pSaveData = m_kifuData.stSaveData;
		}
		else {
			pSaveData = m_suspendData.m_kifu.stSaveData;
		}
		Globals.g_Global.SetContinueDataFromGlobal(pSaveData);
		return true;
	}
	
	public boolean IsEnableSuspend() {
		return m_suspendData.m_bEnableSuspend;
	}
	public void SetEnableSuspend(boolean bEnable) {
		m_suspendData.m_bEnableSuspend = bEnable;
	}
	
	// public void SetHansoku(int nPlayer, boolean bSet = TRUE)
	public void SetHansoku(int nPlayer, boolean bSet) {
		m_suspendData.m_bHansoku[nPlayer] = bSet;
	}
	public boolean GetHansoku(int nPlayer) {
		return m_suspendData.m_bHansoku[nPlayer];
	}
	
	// public void SetMiyakoOti(int nPlayer, boolean bSet = TRUE)
	public void SetMiyakoOti(int nPlayer, boolean bSet) {
		m_suspendData.m_bMiyakoOti[nPlayer] = bSet;
	}
	public boolean GetMiyakoOti(int nPlayer) {
		return m_suspendData.m_bMiyakoOti[nPlayer];
	}
	
	public final boolean IsRightKifu() {
		return m_bRightKifu;
	}

	public Globals.SaveData GetSaveData()	{
		return m_kifuData.stSaveData;
	}
	
	private long GetCheckSum() {
		return 0;
	}
}
