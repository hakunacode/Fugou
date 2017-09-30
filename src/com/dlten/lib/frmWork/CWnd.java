
package com.dlten.lib.frmWork;

import java.util.Timer;
import java.util.TimerTask;

import com.dlten.lib.CBaseView;

public abstract class CWnd extends CEventWnd {

    public static final int ID_TIMER_0 = 0;
    public static final int ID_TIMER_1 = 1;
    public static final int ID_TIMER_2 = 2;
    public static final int ID_TIMER_3 = 3;
    public static final int ID_TIMER_4 = 4;
    private static final int ID_TIMER_COUNT = 5;

    private CTimerTask[] m_tasks = new CTimerTask[ID_TIMER_COUNT];
    private Timer[] m_timers = new Timer[ID_TIMER_COUNT];

    public CWnd() {
        for( int i = 0 ; i < m_tasks.length ; i++ ) {
            m_tasks[i] = new CTimerTask(this);
            m_timers[i] = new Timer();
        }
	}
    
    private void DrawWindowName() {
    	// for DEBUG
    	if (m_strName != null) {
    		drawStr(0, 0, 0x00FF00, ANCHOR_LEFT | ANCHOR_TOP, m_strName);
    	}
    }
    
	static int fpscount = 0;
    private void DrawFPS() {
    	CBaseView	view = getView();
		String	str = String.format("%.1f ", view.getFPS()); 
		drawStr(CODE_WIDTH, 0, 0x00FF00, ANCHOR_RIGHT | ANCHOR_TOP, str);

//		if (fpscount % 30 == 0)
//			STD.logout(str);
//		fpscount ++;
    }
    
    public void DrawPrevProc() {
        if (GetModalessWnd() != null) {
            GetModalessWnd().DrawPrevProc();
        }
    }
    public void DrawProcess() {
        OnPaint();
//        DrawWindowName();	// for DEBUG
//        DrawFPS();		// for DEBUG

        int i;
        int	count;
        CAnimation animObj;
        
        count = m_Anims.size();
        for (i = 0; i < count; i++) {
            animObj = (CAnimation) m_Anims.elementAt(i);
            if (animObj.IsDrawBylib())
                animObj.Draw();
            animObj.UpdateFrame();
        }

        CButton btn;
        count = m_Btns.size();
        for (i = 0; i < count; i++) {
        	btn = (CButton) m_Btns.elementAt(i);
           	btn.Draw();
        }
        
        if (GetModalessWnd() != null) {
            GetModalessWnd().DrawProcess();
        }
    }

    public void OnShowWindow() {
    	super.OnShowWindow();
    }
    public void OnDestroy() {
    	super.OnDestroy();
    	
        for( int i = 0 ; i < m_tasks.length ; i++ ) {
            m_tasks[i].cancel();
            m_timers[i].cancel();

            m_tasks[i] = null;
            m_timers[i] = null;
        }
        m_tasks = null;
        m_timers = null;
    }
    public void OnPaint(){}
    public void OnTimer( int nTimerID ){}
    public int OnNetEvent(int nEvtType, int nParam, Object objData) {
        return -1;
    }

    public int WindowProc(int message, int wParam, int lParam) {
        if (message == WM_TIMER) {
            OnTimer((int) wParam);
            return 0;
        }
        return super.WindowProc(message, wParam, lParam);
    }

    // Using Functions.
    public boolean SetTimer( int nTimerID, int nDelay ) {
        return SetTimer( nTimerID, nDelay, null );
    }
    public boolean SetTimer( int nTimerID, int nDelay, CTimerListner listner ) {
        if( nTimerID < 0 || nTimerID >= ID_TIMER_COUNT )
            return false;

        m_tasks[nTimerID] = new CTimerTask(this);
        m_tasks[nTimerID].SetTimer( nTimerID, listner );
        m_timers[nTimerID].scheduleAtFixedRate( m_tasks[nTimerID], 100, nDelay );

        return true;
    }
    public void KillTimer( int nTimerID ) {
        // m_timers[nTimerID].scheduleAtFixedRate( null, 0, 0 );
        m_tasks[nTimerID].KillTimer();
    }
    public final int SwitchWindow( int nWndID ) {
        return SwitchWindow( nWndID, 0 );
    }
    public final int SwitchWindow( int nWndID, int nParam ) {
        return CWndMgr.getInstance().SwitchingWnd(nWndID, nParam);
    }
    public final void Exit( int nRetCode ) {
        DestroyWindow(0);
    }
}

class CTimerTask extends TimerTask {

    private CWnd m_pWnd = null;
    private CTimerListner m_pListner;
    private int m_nTimerID;

    public CTimerTask( CWnd pWnd ) {
        m_pWnd = pWnd;
        m_pListner = null;
        m_nTimerID = -1;
    }

    public boolean SetTimer( int nTimerID, CTimerListner listner ) {
        m_nTimerID = nTimerID;
        m_pListner = listner;
        return true;
    }
    public void KillTimer() {
        this.cancel();
    }

    public void run() {
        if( m_pListner == null ) {
            if( m_pWnd != null )
                m_pWnd.PostMessage(CWnd.WM_TIMER, m_nTimerID, 0);
        } else {
            m_pListner.TimerProc(m_pWnd, m_nTimerID);
        }
    }
}

