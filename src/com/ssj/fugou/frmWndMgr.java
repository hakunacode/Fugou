package com.ssj.fugou;

import com.dlten.lib.STD;
import com.dlten.lib.file.CConFile;
import com.dlten.lib.frmWork.CWnd;
import com.dlten.lib.frmWork.CWndMgr;
import com.dlten.lib.graphics.CImage;
import com.ssj.fugou.test.*;
import com.ssj.fugou.wnds.*;

public class frmWndMgr extends CWndMgr {
	
	private frmActivity m_activity;
	private frmView m_view;

    public frmWndMgr( frmActivity activity, frmView dc ) {
    	super(dc);
    	
    	m_activity = activity;
    	m_view = dc;
    	
    	m_view.calcFps(true);
    }
    
    protected void Initialize() {
    	super.Initialize();
    	
    	STD.initRand();
    	CConFile.Initialize(m_activity);
    	CImage.Initialize(m_view);
    	Globals.createGlobalValues( m_activity );
    }
    protected void Finalize() {
    	super.Finalize();
    	Globals.deleteGlobalValues();
    	m_activity.finish();
    }
	@Override
	protected void runProc() {
        int nState = WND_LOGO;
//		nState = WND_TEST1;
//		nState = WND_GAME;

        if (Globals.m_bSuspend == true) {
        	nState = WND_GAME;

        	Globals.g_Global.Init();
        	Globals.g_Global.Load();
        	Globals.g_Global.SetGameMode(Globals.GM_UNIFY);
        }
        
        while (nState > 0) {
            nState = NewWindow(nState);
        }
	}

	@Override
	protected CWnd createWindow(int nWndID, int nParam) {
        CWnd ret = null;
        switch( nWndID ) {
        
        case WND_LOGO:				ret = new WndLogo();			break;
        case WND_TITLE:				ret = new WndTitle();			break;
        case WND_SELRULE:			ret = new WndSelRule();			break;
        case WND_SELFREENPC:		ret = new WndSelFNPC();			break;
        case WND_SELCHR:			ret = new WndSelChr();			break;
        case WND_SELUNIFYNPC:		ret = new WndSelUNPC();			break;
        case WND_GAME:				ret = new WndGame();			break;
        case WND_VICTORY:			ret = new WndSelUNPC();			break;
        case WND_RESULT:			ret = new WndResult();			break;
        case WND_GAMEOVER:			ret = new WndGameOver();		break;
        case WND_GAMEEND:			ret = new WndGameEnd();			break;
        
        case WND_TEST1:				ret = new CTest1Wnd();			break;
        case WND_TEST2:				ret = new CTest2Wnd();			break;
        }
        
        Runtime.getRuntime().gc();	// added 2011-0523
        
        return ret;
	}


    public static final int
    WND_DESTROYAPP	= 0,
    
    WND_LOGO		= 3,
    WND_TITLE		= 4,
    WND_SELRULE		= 5,
    WND_SELFREENPC	= 6,
    WND_SELCHR		= 7,
    WND_UNIFYMENU	= 8,
    WND_SELUNIFYNPC	= 9,
    WND_GAME		= 10,
    WND_VICTORY		= 11,
    WND_RESULT		= 12,
    WND_GAMEOVER	= 13,
    WND_GAMEEND		= 14,
    
    WND_TEST1		= 1,
    WND_TEST2		= 2;
}
