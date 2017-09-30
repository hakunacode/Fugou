package com.dlten.lib.frmWork;

import java.util.Vector;

import android.os.Handler;
import android.os.Message;

import com.dlten.lib.CBaseView;
import com.dlten.lib.Common;
import com.dlten.lib.graphics.CImage;
import com.dlten.lib.graphics.CImgObj;
import com.dlten.lib.graphics.CPoint;

public abstract class CEventWnd {

    private static CBaseView m_baseView = null;

    protected Vector<CAnimation> m_Anims;
    protected Vector<CButton> m_Btns;
    protected CEventWnd m_pParent;
    protected CEventWnd m_pActiveWnd;
    protected CEventWnd m_pModalessWnd;
    protected CEventWnd m_pForegroundWnd;

    private boolean m_bEnable;

    public CEventWnd() {
        m_Anims = new Vector<CAnimation>(10, 5);
        m_Btns = new Vector<CButton>(10, 5);

        m_bEnable = true;

        m_pParent = null;
        m_pActiveWnd = null;
        m_pModalessWnd = null;
        m_pForegroundWnd = null;

        m_baseView.clearMsgQueue();
    }

    // abstract functions
    public abstract void DrawProcess();
    public abstract void DrawPrevProc();

    // Framework Callback Functions
    public void OnLoadResource() {}
    public void OnInitWindow() {}
    public void OnShowWindow() {}
    public void OnDestroy() {
    	RemoveAllButtons();
    }
    
    public final int RunProc() {
        return m_baseView.RunProc(this);
    }
    
    public void OnSuspend() {}
    public void OnResume() {}

    public int WindowProc(int message, int wParam, int lParam) {
        switch (message) {
        case WM_PAINT:
            UpdateWindow();
            break;
        case WM_COMMAND:
            OnCommand(wParam);
            break;
        case WM_TOUCH_DOWN:
        	if (setFocusButton(wParam, lParam) == false)
        		OnTouchDown(wParam, lParam);
            break;
        case WM_TOUCH_UP:
        	if (clickFocusButton(wParam, lParam) == false)
        		OnTouchUp(wParam, lParam);
            break;
        case WM_TOUCH_MOVE:
        	if (changeFocusButton(wParam, lParam) == false)
        		OnTouchMove(wParam, lParam);
            break;
        case WM_KEY_DOWN:
            OnKeyDown(wParam);
            break;
        case WM_KEY_UP:
            OnKeyUp(wParam);
            break;
        case WM_KEY_PRESS:
            OnKeyPress(wParam);
            break;
        case WM_QUIT:
            break;
        case WM_ANIM_EVENT:
            OnAnimEvent((short) wParam);
            break;
        default:
            break;
        }
        return 0;
    }

    private CButton	m_btnFocus = null;
    private CButton	findButton(int x, int y) {
        CPoint	pt = new CPoint(x, y);
        CButton	btnFocus = null;
        CButton btn;
        for (int i = 0; i < m_Btns.size(); i++) {
        	btn = (CButton) m_Btns.elementAt(i);
        	
           	if (btn.isUseful()   == true &&
           		btn.isInside(pt) == true)
           	{
           		btnFocus = btn; 
           	}
        }
    	return btnFocus;
    }
    private boolean setFocusButton(int x, int y) {
        CButton	btnFocus = findButton(x, y);
        
        // if there is no any focus button.
        if (btnFocus == null) {
        	if (m_btnFocus != null) {
        		m_btnFocus.setNormal();
        		m_btnFocus = null;
        	}
        	return false;
        }
        
        // if there is already a focus button.
        if (btnFocus != m_btnFocus) {
        	if (m_btnFocus != null) {
        		m_btnFocus.setNormal();
        		m_btnFocus = null;
        	}
        }
        
    	btnFocus.setFocus();
    	m_btnFocus = btnFocus;
    	return true;
    }
    
    private boolean changeFocusButton(int x, int y) {
    	if (m_btnFocus == null)
    		return false;
    	
    	if (m_btnFocus.isVisible() == false)
    		return false;
    		
    	if (m_btnFocus.isEnable() == true) {
	    	// return setFocusButton(x, y);
	    	CPoint	pt = new CPoint(x, y);
	        if (m_btnFocus.isInside(pt) == true) {
	        	m_btnFocus.setFocus();
	        } else {
	       		m_btnFocus.setNormal();
	        }
    	}
    	return true;
    }
    
    private boolean clickFocusButton(int x, int y) {
    	if (m_btnFocus == null)
    		return false;

    	if (m_btnFocus.isUseful() == false)
    		return false;

    	CButton	btnFocus = m_btnFocus;
        m_btnFocus = null;
        
        btnFocus.setNormal();
    	CPoint	pt = new CPoint(x, y);
        if (btnFocus.isInside(pt) == true) {
//			PostMessage(WM_COMMAND, btnFocus.getCommand(), 0);
        	SendMessage(WM_COMMAND, btnFocus.getCommand(), 0);	// Modified by hrh 2011-0619
        }
    	return true;
    }
    
    // UI callback functions.
    public void OnKeyDown(int keycode) {}
    public void OnKeyUp(int keycode) {}
    public void OnKeyPress(int keycode) {}
    public void OnTouchDown(int nTouchX, int nTouchY) {}
    public void OnTouchUp(int nTouchX, int nTouchY) {}
    public void OnTouchMove(int nTouchX, int nTouchY) {}
    public void OnCommand(int nCmd) {}
    public void OnAnimEvent(int nAnimID) {}
    public int OnNetEvent(int nEvtType, int nParam, Object objData) { return 0; }

    // for framework call
    public static void setView(CBaseView dc) {
    	m_baseView = dc;
    }
    public static CBaseView getView() {
        return m_baseView;
    }

    public boolean AddAnimation(CAnimation pAnimObj) {
        if (m_Anims.indexOf(pAnimObj) != -1) { // exist this object
            return false;
        }
        m_Anims.addElement(pAnimObj);
        return true;
    }
    public void RemoveAnimation(CAnimation pAnimObj) {
        m_Anims.removeElement(pAnimObj);
    }

    public CButton createButton(String norName, String focName, String disName) {
		CButton	btn = new CButton();
		btn.create(this, norName, focName, disName);
		
		return btn;
    }
    public boolean AddButton(CButton pBtnObj) {
        if (m_Btns.indexOf(pBtnObj) != -1) { // exist this object
            return false;
        }
        m_Btns.addElement(pBtnObj);
        return true;
    }
    public void RemoveButton(CButton pBtnObj) {
        m_Btns.removeElement(pBtnObj);
    }
    public void RemoveAllButtons() {
        CButton btn;
        while (m_Btns.size() > 0) {
        	btn = (CButton) m_Btns.elementAt(0);
        	btn.destroy();
        }
    }
    
    public boolean EnableWindow(boolean bEnable) {
        boolean bTemp = m_bEnable;
        m_bEnable = bEnable;
        return bTemp;
    }
    public boolean IsEnable() {
        return m_bEnable;
    };
    public void NotifyToParentEndRun() {
        if (m_pParent != null) {
            m_pParent.InitKeyState();
        }
    }
    private void InitKeyState() {
        int[] messages = new int[] {WM_KEY_DOWN, WM_KEY_UP};
        m_baseView.DeleteMsgs(messages);
    }

    // window state relations
    public void SetParent(CEventWnd pParent) {
        m_pParent = pParent;
    }
    public CEventWnd SetActiveWnd(CEventWnd pActive) {
        CEventWnd pOldActiveWnd = m_pActiveWnd;
        m_pActiveWnd = pActive;
        return pOldActiveWnd;
    }
    public CEventWnd SetModalessWnd(CEventWnd pModaless) {
        CEventWnd pOldModalessWnd = m_pModalessWnd;
        m_pModalessWnd = pModaless;
        return pOldModalessWnd;
    }
    public CEventWnd SetForegroundWnd(CEventWnd pForegroundWnd) {
        CEventWnd pRet = m_pForegroundWnd;
        m_pForegroundWnd = pForegroundWnd;
        return pRet;
    }


    /////////////////////////////////////////////////////////////////////////
    // functionalities
    /////////////////////////////////////////////////////////////////////////
    // window state relations.
    public void DestroyWindow(int nCode) {
        PostMessage(WM_QUIT, nCode);
    }
    public CEventWnd GetParent() {
        return m_pParent;
    }
    public CEventWnd GetActiveWnd() {
        return m_pActiveWnd;
    }
    public CEventWnd GetForegroundWnd() {
        return m_pForegroundWnd;
    }
    public CEventWnd GetModalessWnd() {
        return m_pModalessWnd;
    }

    // message relations
    public boolean SendMessage(int message) {
    	SendMessage(message, 0);
        return true;
    }
    public boolean SendMessage(int message, int wParam) {
    	SendMessage(message, wParam, 0);
        return true;
    }
    public boolean SendMessage(int message, int wParam, int lParam) {
        WindowProc(message, wParam, lParam);
        return true;
    }
    public boolean PostMessage(int message) {
        return PostMessage(message, 0);
    }
    public boolean PostMessage(int message, int wParam) {
        return PostMessage(message, wParam, 0);
    }
    public boolean PostMessage(int message, int wParam, int lParam) {
        if (!IsEnable() && message == WM_TIMER)
            return false;
        m_baseView.PostMessage(message, wParam, lParam);
        return true;
    }

    public void postActivityMsg( int message, int wParam, int lParam ) {
    	Message msg = new Message();
    	msg.what = message;
    	msg.arg1 = wParam;
    	msg.arg2 = lParam;
    	
    	m_handleUpdateActivity.sendMessage(msg);
    }
    
    private static Handler m_handleUpdateActivity = new Handler(){ 
	    @Override 
	    public void handleMessage(Message msg) { 
	    	HandleActivity gameActivity = (HandleActivity) getView().getActivity();
	    	gameActivity.onRecvMessage(msg.what, msg.arg1, msg.arg2);
	    }
    };

    // Update
    public void UpdateWindow() {
    	m_baseView.update(this);
    }
    public void Invalidate() {
        PostMessage(WM_PAINT);
    }
    
    // for Debug
    public String m_strName = null;
    
    public void setString(String strName) {
//    	if (!m_strName)
//    		m_strName = new String();
    	m_strName =  strName;
    }
    
    public void drawStr(int x, int y, int color, int drawType, String str) {
		CBaseView view = getView();
		view.setColor(color);
		
        x = (int)Common.code2screen(x);
        y = (int)Common.code2screen(y);
		view.drawString(str, x, y, drawType);
	}
    
    public void drawStr(int x, int y, String str) {
    	drawStr(x, y, 0xFFFFFF, ANCHOR_CENTER | ANCHOR_MIDDLE, str);
	}
    
    public void drawRectStr(int x, int y, int width, int hieght, int color, int drawType, String str) {
        x = (int)Common.code2screen(x);
        y = (int)Common.code2screen(y);
        width  = (int)Common.code2screen(width);
        hieght = (int)Common.code2screen(hieght);
        
		CBaseView view = getView();
		view.setColor(color);
		view.drawRectString(str, x, y, width, hieght, 0, -1, drawType);
	}
    
    public void drawRectStr(int x, int y, int color, int drawType, String str) {
        x = (int)Common.code2screen(x);
        y = (int)Common.code2screen(y);
        
		CBaseView view = getView();
		view.setColor(color);
		view.drawRectString(str, x, y, REAL_WIDTH, REAL_HEIGHT, 0, -1, drawType);
	}
    
    protected CImgObj unload(CImgObj img)
    {
    	if (img != null) {
    		img.unload();
    		img = null;
    	}
    	
    	return img;
    }

    public static final int
    WM_PAINT = 2,
    WM_COMMAND = 3,
    WM_TIMER = 4,
    WM_SUSPEND = 5,
    WM_RESUME = 6,
    WM_TOUCH_DOWN = 8,
    WM_TOUCH_UP = 9,
    WM_TOUCH_MOVE = 10,
    WM_KEY_DOWN = 12,
    WM_KEY_UP = 13,
    WM_KEY_PRESS = 14,
    WM_QUIT = 15,
    WM_ANIM_EVENT = 16,
    WM_NET = 17,
    WM_APP_EXIT = 18,
    WM_RESIZE = 19;
    public static final int WM_USER = 0x400;

    public static final int NET_EVT_CONNECTED = 0;
    public static final int NET_EVT_CLOSED = 1;
    public static final int NET_EVT_RECVDATA = 2;

    public static int CODE_WIDTH = CBaseView.CODE_WIDTH;
    public static int CODE_HEIGHT = CBaseView.CODE_HEIGHT;
    public static int REAL_WIDTH = CBaseView.RES_WIDTH;
    public static int REAL_HEIGHT = CBaseView.RES_HEIGHT;
    public static int HALF_WIDTH = CBaseView.HALF_WIDTH;
    public static int HALF_HEIGHT = CBaseView.HALF_HEIGHT;
    public static void prepareDC() {
        CODE_WIDTH  = CBaseView.CODE_WIDTH;
        CODE_HEIGHT = CBaseView.CODE_HEIGHT;
        
        REAL_WIDTH  = CBaseView.RES_WIDTH;
        REAL_HEIGHT = CBaseView.RES_HEIGHT;
        
        HALF_WIDTH  = CBaseView.HALF_WIDTH;
        HALF_HEIGHT = CBaseView.HALF_HEIGHT;
    }

    public static final int ANCHOR_TOP = CImage.ANCHOR_TOP;
    public static final int ANCHOR_MIDDLE = CImage.ANCHOR_MIDDLE;
    public static final int ANCHOR_BOTTOM = CImage.ANCHOR_BOTTOM;
    public static final int ANCHOR_V_FILTER = CImage.ANCHOR_V_FILTER;
    public static final int ANCHOR_LEFT = CImage.ANCHOR_LEFT;
    public static final int ANCHOR_CENTER = CImage.ANCHOR_CENTER;
    public static final int ANCHOR_RIGHT = CImage.ANCHOR_RIGHT;
    public static final int ANCHOR_H_FILTER = CImage.ANCHOR_H_FILTER;

    public static final int
            KEY_NONE = 0,
    KEY_0 = 1 << 0,
    KEY_1 = 1 << 1,
    KEY_2 = 1 << 2,
    KEY_3 = 1 << 3,
    KEY_4 = 1 << 4,
    KEY_5 = 1 << 5,
    KEY_6 = 1 << 6,
    KEY_7 = 1 << 7,
    KEY_8 = 1 << 8,
    KEY_9 = 1 << 9,
    KEY_MENU = 1 << 10,
    KEY_HOME = 1 << 11,
    KEY_BACK = 1 << 12,
    KEY_DIAL = 1 << 13,
    KEY_UP = 1 << 14,
    KEY_DOWN = 1 << 15,
    KEY_LEFT = 1 << 16,
    KEY_RIGHT = 1 << 17,
    KEY_SELECT = 1 << 18;
}
