
package com.dlten.lib.frmWork;

import com.dlten.lib.CBaseView;
import com.dlten.lib.STD;
import com.dlten.lib.graphics.CDCView;

public abstract class CWndMgr implements Runnable {
    private static CWndMgr _instance = null;
    public static CWndMgr getInstance() {
        if( _instance == null ) {
            STD.ASSERT(false);
        }
        return _instance;
    }

    private Thread m_thread;
    private CDCView m_DCView;
    private CBaseView m_baseView;

    private CWnd m_pCurShowWnd;
    private CEventWnd m_pOldModaless;
    private CEventWnd m_pOldFore;

    public CWndMgr( CBaseView view ) {
        _instance = this;

        m_thread = null;
        m_DCView = view;
        m_baseView = view;
        CEventWnd.setView(m_baseView);

        m_pCurShowWnd = null;
        m_pOldModaless = null;
        m_pOldFore = null;
    }
    
    public final void start() {
    	m_thread = new Thread(this);
    	if( m_thread == null )
    		return;
    	m_thread.start();
    }
    public final void stop() {
    	if( m_thread == null )
    		return;
        boolean retry = true;
        while (retry) {
            try {
            	m_thread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }
    	m_thread = null;
    }
    public final void suspend() {
    	if( m_thread == null )
    		return;
    	if( m_pCurShowWnd != null )
    		m_pCurShowWnd.OnSuspend();
    }
    public final void resume() {
    	if( m_thread == null )
    		return;
    	if( m_pCurShowWnd != null )
    		m_pCurShowWnd.OnResume();
    }
    public final void run() {
    	Initialize();
    	runProc();
    	Finalize();
    }
    public final void SendMessage(int message, int wParam, int lParam) {
    	if( m_pCurShowWnd == null )
    		return;
    	m_pCurShowWnd.SendMessage(message, wParam, lParam);
    }

    public final CWnd GetCurWnd() {
        return m_pCurShowWnd;
    }
    public int SwitchWindow(int index) {
        return 0;
    }
    public int SwitchWindow(int index, int nParam) {
        return 0;
    }

    protected void Initialize() {
    	m_DCView.prepareDC();
    	CEventWnd.prepareDC();
    }
    protected abstract void runProc();
    protected void Finalize() {
    	m_DCView.releaseDC();
    }
    protected abstract CWnd createWindow( int nWndID, int nParam );



    ///////////////////////////////////////////
    //
    // Common Window Event Process
    //
    ///////////////////////////////////////////
    protected final int NewWindow(int nWndID) {
        return NewWindow(nWndID, 0);
    }
    protected final int NewWindow(int nWndID, int nParam) {
        m_pCurShowWnd = createWindow(nWndID, nParam);
        if( m_pCurShowWnd == null )
            return -1;
        int nRet = WndEventLoop(m_pCurShowWnd);
        m_pCurShowWnd = null;

        return nRet;
    }
    private final int WndEventLoop(CWnd pWnd) {
    	
        m_pCurShowWnd = pWnd;
        pWnd.SetActiveWnd(pWnd);

        pWnd.OnInitWindow();
        pWnd.OnLoadResource();
        
        if (pWnd.m_strName != null)	STD.logout("	Show(" + pWnd.m_strName + ")");
        pWnd.OnShowWindow();
        
        if (pWnd.m_strName != null)	STD.logout("	Run(" + pWnd.m_strName + ")");
        int nRet = pWnd.RunProc();
        
        if (pWnd.m_strName != null)	STD.logout("	Destroy(" + pWnd.m_strName + ")");
        pWnd.OnDestroy();

        return nRet;
    }


    ///////////////////////////////////////////
    //
    // Switch Window Process
    //
    ///////////////////////////////////////////
    protected final int SwitchingWnd(int nWndID) {
        return SwitchingWnd(nWndID, 0);
    }
    protected final int SwitchingWnd(int nWndID, int nParam)
    {
        CWnd pNewWnd = createWindow(nWndID, nParam);
        int nRet = SwitchWindow_Proc(pNewWnd, m_pCurShowWnd);

        return nRet;
    }

    protected final void SwitchWinodw_Prepare() {
        m_pCurShowWnd.OnDestroy();
    }
    private final int SwitchWindow_Proc(CWnd pNewWnd, CWnd pBeforeWnd) {
        pNewWnd.SetParent(pBeforeWnd);
        int nRet = WndEventLoop(pNewWnd); // in here "m_pCurShowWnd = pNewWnd" is act.;
        m_pCurShowWnd = pBeforeWnd;
        return nRet;
    }
    protected final void SwitchWinodw_Finish() {
        m_pCurShowWnd.OnShowWindow();
    }


    ///////////////////////////////////////////
    //
    // Dialog Process
    //
    ///////////////////////////////////////////
    public final int DialogDoModal(CDialog pDialogWnd, CEventWnd pParent) {
        pDialogWnd.SetParent(pParent);
        boolean bEnable = pDialogWnd.GetParent().EnableWindow(false);

        CEventWnd pOldActive = pParent.SetActiveWnd(pDialogWnd);
        CEventWnd pOldFore = pParent.SetForegroundWnd(pDialogWnd);
        pDialogWnd.OnInitWindow();
        pDialogWnd.OnLoadResource();
        
        if (pDialogWnd.m_strName != null)	STD.logout("	Show(" + pDialogWnd.m_strName + ")");
        pDialogWnd.OnShowWindow();
        
        if (pDialogWnd.m_strName != null)	STD.logout("	Run(" + pDialogWnd.m_strName + ")");
        int nRet = pDialogWnd.RunProc();
        
        if (pDialogWnd.m_strName != null)	STD.logout("	Destroy(" + pDialogWnd.m_strName + ")");
        pDialogWnd.OnDestroy();

        pParent.SetForegroundWnd(pOldFore);
        pParent.SetActiveWnd(pOldActive);
        pDialogWnd.GetParent().EnableWindow(bEnable);

        return nRet;
    }
    public final void DialogModaless(CDialog pDialogWnd, CEventWnd pParent) {
        pDialogWnd.SetParent(pParent);
        m_pOldModaless = pParent.SetModalessWnd(pDialogWnd);
        m_pOldFore = pParent.SetForegroundWnd(pDialogWnd);
        pDialogWnd.OnInitWindow();
        pDialogWnd.OnLoadResource();
        pDialogWnd.OnShowWindow();
    }
    public final void DialogDestroy(CDialog pDialogWnd, CEventWnd pParent) {
        pParent.SetForegroundWnd(m_pOldFore);
        pParent.SetModalessWnd(m_pOldModaless);
        pDialogWnd.OnDestroy();
        pDialogWnd.GetParent().UpdateWindow();
    }
}
