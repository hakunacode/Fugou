package com.dlten.lib;

import java.util.Vector;
import java.util.Enumeration;

import android.content.Context;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.util.AttributeSet;

import com.dlten.lib.frmWork.HandleActivity;
import com.dlten.lib.frmWork.CEventWnd;
import com.dlten.lib.frmWork.CWnd;
import com.dlten.lib.frmWork.CWndMgr;
import com.dlten.lib.graphics.CBmpDCView;
//import com.dlten.lib.graphics.CDCView;
//import com.dlten.lib.opengl.CBeijingBmpGLDCView;
//import com.dlten.lib.opengl.CBeijingGLDCView;
//import com.dlten.lib.opengl.CMyGLDCView;

// public abstract class CBaseView extends CDCView implements SurfaceHolder.Callback
public abstract class CBaseView extends CBmpDCView implements SurfaceHolder.Callback
// public abstract class CBaseView extends CMyGLDCView implements SurfaceHolder.Callback
// public abstract class CBaseView extends CBeijingBmpGLDCView implements SurfaceHolder.Callback
// public abstract class CBaseView extends CBeijingGLDCView implements SurfaceHolder.Callback
{

    private HandleActivity m_activity;
    private CWndMgr m_wndMgr;
    
    // event proc relation.
    private boolean m_bThreadFlag = false;
    private boolean m_bSuspended = true;
    private Vector<Object> m_msgQueue;
    // Personal for Key event processing
    private int m_nCurKey = 0; //KEY_NONE;
    private int m_nDelay;
    private SurfaceHolder m_holder;
    
    public CBaseView(Context context) {
        super(context);
        m_activity = (HandleActivity) context;
        initCBaseView();
    }
    public CBaseView( Context context, AttributeSet attrs )
	{
		super( context, attrs );
		m_activity = (HandleActivity) context;
		initCBaseView();
	}
	public HandleActivity getActivity() {
    	return m_activity;
    }
    private void initCBaseView() {
    	setFocusableInTouchMode(true);
    	setFocusable(true);
    	m_wndMgr = null;
    	m_msgQueue = new Vector<Object>( 200, 40 );

    	m_holder = getHolder();
        m_holder.addCallback(this);
    }
    public void prepareDC() {
    	// wait for showing view.
    	while( m_bSuspended );

    	super.prepareDC();
    }
    
    // framework event proc relation.
    public final void start() {
    	m_bSuspended = true;
    	m_wndMgr = createWndMgr();
    	if( m_wndMgr == null )
    		return;
    	m_bThreadFlag = true;
    	m_wndMgr.start();
    	
		setFPS(1000.0f / Common.FPS);
    }
    public final void stop() {
    	if( m_wndMgr == null )
    		return;
    	m_bThreadFlag = false;
    	m_wndMgr.stop();
    }
    public final void suspend() {
    	if( m_wndMgr == null )
    		return;
    	m_wndMgr.suspend();
    	m_bSuspended = true;
    }
    public final void resume() {
    	if( m_wndMgr == null )
    		return;
    	m_wndMgr.resume();
    	m_bSuspended = false;
    }
    protected abstract CWndMgr createWndMgr();
    public CWndMgr getWndMgr() {
    	return m_wndMgr;
    }
    
    //////////////////////////////////////////////////////////////////
    // All of UI callback functions
    ////////////////////////////////////////////////////////////////// 
	@Override
	public boolean onTouchEvent( MotionEvent event ) {
		if( m_bSuspended )
			return false;

		int nPosX = getCodePosX( (int)event.getX() );
		int nPosY = getCodePosY( (int)event.getY() );
		
		switch( event.getAction() ) {
		case MotionEvent.ACTION_DOWN:
			addMessage( CWnd.WM_TOUCH_DOWN, nPosX, nPosY );
			break;
		case MotionEvent.ACTION_UP:
			addMessage( CWnd.WM_TOUCH_UP, nPosX, nPosY );
			break;
		case MotionEvent.ACTION_MOVE:
			if( m_wndMgr != null )
				m_wndMgr.SendMessage(CWnd.WM_TOUCH_MOVE, nPosX, nPosY);
			break;
		case MotionEvent.ACTION_CANCEL:
			addMessage( CWnd.WM_TOUCH_UP, nPosX, nPosY );
			break;
		case MotionEvent.ACTION_OUTSIDE:
			addMessage( CWnd.WM_TOUCH_UP, nPosX, nPosY );
			break;
		}
		return true;
	}
	// SurfaceView callback
	@Override
	protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
		super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
    	STD.logout("onFocusChanged = (" + getWidth() + ", " + getHeight() + ")");
	}
    public void onSizeChanged(int w, int h, int oldw, int oldh ) {
    	STD.logout("onSizeChanged = (" + w + ", " + h + ", " + oldw + ", " + oldh);
    }
    // SurfaceHolder.CallBack callbacks
    @Override
	public void surfaceChanged( SurfaceHolder holder, int format, int width, int height ) {
    	super.surfaceChanged(holder, format, width, height);
	}
	@Override
	public void surfaceCreated( SurfaceHolder holder ) {
		super.surfaceCreated(holder);
		resume();
	}
	@Override
	public void surfaceDestroyed( SurfaceHolder holder ) {
		super.surfaceDestroyed(holder);
    	suspend();
	}

	// Added by hrh 2011-0523_<
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }
	
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }
    
    public void onPause() {
    	super.onPause();
    }

    public void onResume() {
        super.onResume();
    }


    public void setEvent(Runnable r) {
    	super.setEvent(r);
    }
    // Added by hrh 2011-0523_>
	

	///////////////////////////////////////////////////////////////
	private final void intervalProc() {
        int nDiff = (int) (System.currentTimeMillis() - m_lPrevFrameTime);
        int nCurCycle = CYCLE_TIME;
        if( nDiff >= 5000 )
        	return;
        if( nDiff < 0 )
        	nDiff = 0;
        if (nDiff < CYCLE_TIME) {
        	STD.sleep(CYCLE_TIME - nDiff);
        	nCurCycle = CYCLE_TIME;
        } else if (nDiff < CYCLE_TIME2) {
        	STD.sleep(CYCLE_TIME2 - nDiff);
        	nCurCycle = CYCLE_TIME2;
        } else if (nDiff < CYCLE_TIME3) {
        	STD.sleep(CYCLE_TIME3 - nDiff);
        	nCurCycle = CYCLE_TIME3;
        } else
        	nCurCycle = nDiff;
        	
        m_fFPS = (float)1000/nCurCycle;
        if( m_fFPS <= 0 )
        	m_fFPS = 1;

        if (m_bLogFlag)
            STD.logout("FPS = " + m_fFPS);
        
        updateFrameTime();
	}
    private long m_lPrevFrameTime = 0;
    protected final void waitFrame( int nTimeGap1, int nTimeGap2 ) {
    	waitTime( m_lPrevFrameTime, nTimeGap1, nTimeGap2 );
    }
    protected final void updateFrameTime() {
    	m_lPrevFrameTime = System.currentTimeMillis();
    }
    protected final void waitTime( long lPrevTime, int nTimeGap1, int nTimeGap2 ) {
        long lDiff = (System.currentTimeMillis() - lPrevTime);
        int nLastCycle;
        if( lDiff >= 500 )
        	return;
        
        if (lDiff < nTimeGap1) {
        	STD.sleep(nTimeGap1 - lDiff);
        	nLastCycle = nTimeGap1;
        } else {
        	nLastCycle = (int) lDiff;
        }
        	
        
        // FPS calc
        if( m_bCalcFps ) {
            if( m_lUpdateTimes>=10*1000 ) {
            	m_lUpdateTimes = 0;
            	m_nFrameCount = 0;
            }
            if (m_bLogFlag)
                STD.logout("Cycle = " + nLastCycle);
            // m_lUpdateTimes += (int) lDiff;
            m_lUpdateTimes += nLastCycle;
            m_nFrameCount++;
            m_fCycle = (float) m_lUpdateTimes / m_nFrameCount;
            m_fFPS = (float)1000*m_nFrameCount/m_lUpdateTimes;
            if( m_fFPS <= 0 )
            	m_fFPS = 1;
        }
    }
    private boolean m_bLogFlag = false;
    public final void inverseLogFlag() {
        m_bLogFlag = !m_bLogFlag;
    }
    private boolean m_bCalcFps = false;
    public final void calcFps(boolean bCalc) {
    	m_bCalcFps = bCalc;
    }
    
    
    public final void PostMessage(int msg, int wParam, int lParam) {
        addMessage(msg, wParam, lParam);
    }
    protected final void addMessage(int msg) {
        addMessage(msg, 0, 0);
    }
    protected final void addMessage(int msg, int wParam, int lParam) {
        addMessage(new int[] {msg, wParam, lParam});
    }
    protected final void addMessage(int[] msg) {
        if (m_msgQueue == null)
            return;
        synchronized (m_msgQueue) {
            m_msgQueue.addElement(msg);
        }
    }
    public final void clearMsgQueue() {
        Object obj;
        int[] bufMessage;
        Vector<Object> tempQueue = new Vector<Object>(5);
        synchronized (m_msgQueue) {
            // extract WM_NET datas.
            for (Enumeration<Object> e = m_msgQueue.elements(); e.hasMoreElements(); ) {
                obj = e.nextElement();
                if (obj == null) {
                    STD.ASSERT(false);
                    continue;
                }
                try {
                    bufMessage = (int[]) obj;
                } catch (ClassCastException ex) {
                    STD.ASSERT(false);
                    continue;
                }
                if (bufMessage[MSG] == CWnd.WM_NET) {
                    tempQueue.addElement(obj);
                    try {
                        obj = e.nextElement();
                    } catch (Exception ee) {
                        STD.ASSERT(false);
                        obj = null;
                    }
                    tempQueue.addElement(obj);
                }
            }
            // copy datas.
            m_msgQueue.removeAllElements();
            for (Enumeration<Object> e = tempQueue.elements(); e.hasMoreElements(); ) {
                obj = e.nextElement();
                m_msgQueue.addElement(obj);
            }
        }
    }
    private boolean isExistSameMsg(int nMsg) {
        int[] bufMessage;
        boolean bFind = false;
        synchronized (m_msgQueue) {
            for (Enumeration<Object> e = m_msgQueue.elements(); e.hasMoreElements(); ) {
                try {
                    bufMessage = (int[]) e.nextElement();
                } catch (ClassCastException ex) {
                    continue;
                }
                if (bufMessage != null && bufMessage[MSG] == nMsg)
                    bFind = true;
            }
        }
        return bFind;
    }
    private boolean isNextMsg(int nMsg) {
        synchronized (m_msgQueue) {
            if (m_msgQueue.size() >= 1) {
                if (((int[]) m_msgQueue.elementAt(0))[MSG] == nMsg)
                    return true;
            }
        }
        return false;
    }
    public void NotifyNetEvents(int nEvtType, int nParam, Object objData) {
        synchronized (m_msgQueue) {
            m_msgQueue.addElement(new int[] {CWnd.WM_NET, nEvtType, nParam});
            m_msgQueue.addElement(objData);
        }
    }
    private Object popFirstMsg() {
        synchronized (m_msgQueue) {
            if (m_msgQueue.isEmpty())
                return null;

            Object objRet = m_msgQueue.elementAt(0);
            m_msgQueue.removeElementAt(0);
            return objRet;
        }
    }
    public void DeleteMsgs(int[] messages) {
        synchronized (m_msgQueue) {
            Vector<Object> msgs = new Vector<Object>(40, 5);
            while (!m_msgQueue.isEmpty()) {
                int[] msg = (int[]) m_msgQueue.elementAt(0);
                m_msgQueue.removeElementAt(0);
                if (!isExistMsg(msg[MSG], messages)) {
                    msgs.addElement(msg);
                    if (msg[MSG] == CWnd.WM_NET && m_msgQueue.size() > 0) {
                        Object objTemp = m_msgQueue.elementAt(0);
                        m_msgQueue.removeElementAt(0);
                        msgs.addElement(objTemp);
                    }
                } else {
                    if (msg[MSG] == CWnd.WM_NET && m_msgQueue.size() > 0) {
                        m_msgQueue.removeElementAt(0);
                    }
                }
            }

            while (!msgs.isEmpty()) {
                Object msg = msgs.elementAt(0);
                msgs.removeElementAt(0);
                m_msgQueue.addElement(msg);
            }
        }
    }
    private boolean isExistMsg(int msg, int[] messages) {
        if (messages == null)
            return false;

        for (int i = 0; i < messages.length; i++) {
            if (msg == messages[i])
                return true;
        }
        return false;
    }


    private static final int MSG = 0;
    private static final int WPARAM = 1;
    private static final int LPARAM = 2;
    private static final int KEY_EVENT_INTERVAL = 100;
    public int RunProc(CEventWnd pWnd) {
        while (m_bThreadFlag) {
            try {
                if (m_bSuspended) {
                    STD.sleep(50);
                    updateFrameTime();
                    continue;
                }
                Object objTemp;
                objTemp = popFirstMsg();
                if (objTemp != null) {
                    int[] msg = (int[]) objTemp;

                    switch (msg[MSG]) {
                    case CWnd.WM_RESIZE:
                    	// super.prepareDC();
                    	break;
                    case CWnd.WM_NET:
                        Object objData = null;
                        objData = popFirstMsg();
                        int nret = pWnd.OnNetEvent(msg[WPARAM], msg[LPARAM], objData);
                        if( msg[WPARAM] == CWnd.NET_EVT_RECVDATA && nret < 0 ) {
                            STD.ASSERT(false);
                        }
                        break;
                    case CWnd.WM_QUIT:
                        pWnd.NotifyToParentEndRun();
                        return (int) msg[WPARAM];
                    case CWnd.WM_KEY_DOWN:
                        if (true == isExistSameMsg(msg[MSG])) {
                            break;
                        }

                        m_nCurKey = msg[WPARAM];
                        m_nDelay = 0;
                        pWnd.WindowProc(msg[MSG], msg[WPARAM], msg[LPARAM]);

                        if (m_nCurKey == CWnd.KEY_BACK ||
                            m_nCurKey == CWnd.KEY_MENU ||
                            m_nCurKey == CWnd.KEY_SELECT)
                            m_nCurKey = CWnd.KEY_NONE;
                        break;
                    case CWnd.WM_KEY_UP:
                        if (msg[WPARAM] == m_nCurKey)
                            m_nCurKey = CWnd.KEY_NONE;
                        pWnd.WindowProc(msg[MSG], msg[WPARAM], msg[LPARAM]);
                        break;
                    case CWnd.WM_TOUCH_DOWN:
                    case CWnd.WM_TOUCH_UP:
                        if (true == isExistSameMsg(msg[MSG]))
                            break;
                        pWnd.WindowProc(msg[MSG], msg[WPARAM], msg[LPARAM]);
                    	break;
                    case CWnd.WM_PAINT:
                        if (isNextMsg(CWnd.WM_PAINT))
                            break;
                    default:
                        pWnd.WindowProc(msg[MSG], msg[WPARAM], msg[LPARAM]);
                        break;
                    }

                    // Added by hrh 2011-0619_<
                    switch (msg[MSG]) {
                    case CWnd.WM_KEY_DOWN:
                    case CWnd.WM_TOUCH_DOWN:
                    case CWnd.WM_TOUCH_UP:
                    	int	delMsgs[] = new int[] { msg[MSG] };
                        DeleteMsgs(delMsgs);
                        break;
                    }
                    // Added by hrh 2011-0619_>
                    
                } else {
                    // beforeTimer();
                    if (m_nCurKey != CWnd.KEY_NONE) {
                        if (m_nDelay >= KEY_EVENT_INTERVAL * 3) {
                            pWnd.WindowProc(CWnd.WM_KEY_PRESS, m_nCurKey, 0);
                            m_nDelay = KEY_EVENT_INTERVAL * 3;
                        } else {
                            m_nDelay += KEY_EVENT_INTERVAL;
                        }
                        STD.sleep(KEY_EVENT_INTERVAL);
                    } else {
                        // idleProcess();
                        // pWnd.UpdateWindow();
                    }
                }

                // perFrameProc();
                pWnd.UpdateWindow();
                // intervalProc();
                // waitFrame(CYCLE_TIME, CYCLE_TIME);
                // waitFrame((int)m_fCycle, (int)m_fCycle);
                waitFrame((int)m_fUserCycle, (int)m_fUserCycle);
                updateFrameTime();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return 0;
    }
    
    private float m_fUserCycle = 1000.0f / Common.FPS;
    public void setFPS(float fCycle) {
    	m_fUserCycle = fCycle;
    }
    private float m_fCycle = 20;
    private float m_fFPS = 50;
    private int m_nFrameCount = 0;
    private long m_lUpdateTimes = 0;
    public float getFPS() {
    	return m_fFPS;
    }
    
    public static int CYCLE_TIME = 20;
    public static int CYCLE_TIME2 = 32;
    public static int CYCLE_TIME3 = 64;
    
    private void perFrameProc() {
    	waitFrame(16, 20);
    }
}
