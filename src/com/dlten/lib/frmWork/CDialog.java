
package com.dlten.lib.frmWork;

import com.dlten.lib.STD;

public abstract class CDialog extends CEventWnd {

    private boolean m_bModal;

    public CDialog() {
        m_pParent = null;
        m_bModal = true;
    }

    public void DrawWindowName() {
    	if (m_strName != null) {
    		drawStr(0, 50, 0x00FF00, ANCHOR_LEFT | ANCHOR_TOP, m_strName);
    	}
    }
    
    public void DrawProcess() {
        if (m_bModal)
            GetParent().DrawProcess();

        OnPaint();
//        DrawWindowName();

        int	i;
        CAnimation animObj;
        for (i = 0; i < m_Anims.size(); i++) {
            animObj = (CAnimation) m_Anims.elementAt(i);
            if (animObj.IsDrawBylib())
                animObj.Draw();
            animObj.UpdateFrame();
        }
        
        CButton btn;
        for (i = 0; i < m_Btns.size(); i++) {
        	btn = (CButton) m_Btns.elementAt(i);
           	btn.Draw();
        }
    }
    public void DrawPrevProc() {
        if (m_bModal)
            GetParent().DrawPrevProc();
    }
    public int OnNetEvent(int nEvtType, int nParam, Object objData) {
        return -1;
    }

    public void OnPaint() {}
	public void OnTouchDown( int x, int y ) {}
	public void OnKeyDown( int keycode ) {}

    public int DoModal(CEventWnd pParent) {
        m_bModal = true;
        return CWndMgr.getInstance().DialogDoModal(this, pParent);
    }
    public void Modaless(CEventWnd pParent) {
        m_bModal = false;
        CWndMgr.getInstance().DialogModaless(this, pParent);
    }
    public void Destroy() {
        STD.ASSERT(m_bModal == false);
        m_bModal = true;
        CWndMgr.getInstance().DialogDestroy(this, GetParent());
    }
}
