
package com.dlten.lib.frmWork;

public abstract class CAnimation implements AnimListner {

    public static final int CALLBACK_NONE = 0, //disabled by hrh 2009-0115-	NNS_G2D_ANMCALLBACKTYPE_NONE,
    CALLBACK_LAST_FRM = 1, // アニメーションシーケンスの最終フレーム終了時によびだす
    CALLBACK_SPEC_FRM = 2, // 指定フレームの再生時に呼びだす。
    CALLBACK_EVER_FRM = 3, // 毎フレーム呼び出す。
    CallbackType_MAX = 4;

    public static final short IDA_NONE = -1;


    public void onAnimAction(CAnimation anim, Object param) {
        CAnimation pAnim = anim;
        CEventWnd pWnd = pAnim.m_pParent;

        CEventWnd curWnd = CWndMgr.getInstance().GetCurWnd();
        if ( pWnd==curWnd && pWnd!=null)
            pWnd.PostMessage(CWnd.WM_ANIM_EVENT, pAnim.GetID());
    }

    public void ChangeCallBackType(int callBack, short nAnimID,
                                   int nFrameNum) {
        ChangeCallBackType(this, null, callBack, nFrameNum, nAnimID);
    }

    public void ChangeCallBackType(AnimListner proc, Object param,
                                   int callBack, int nFrameNum,
                                   short nAnimID) {
        m_nAnimID = nAnimID;
        m_pProc = proc;
        m_pParam = param;
        m_callBackType = callBack;
        m_nCallBackFrameNum = nFrameNum;
    }

    //////////////////////////////////////////////////////////////////
    ///			CAnimation Class
    //////////////////////////////////////////////////////////////////
    private CEventWnd m_pParent;
    private short m_nAnimID;

    private int m_nStartFrame;
    private int m_nEndFrame;
    private int m_nCurFrame;
    private boolean m_bLoop;

    private short m_nSpeed; // 1:Slow, 8:Middle, 16:FAST
    private boolean m_bVisible;
    private boolean m_bPause;
    private boolean m_bEnd;
    int m_nDirection;

    private boolean m_bActive;
    private boolean m_bParentRegistered;
    private int m_nSpeedFrame;
    private boolean m_bOwnerDraw;

    // CallBack Function Relation.
    private int m_callBackType;
    private int m_nCallBackFrameNum;
    private AnimListner m_pProc;
    private Object m_pParam;


    public CAnimation() {
        Reset();
    }

    public short GetID() {
        return m_nAnimID;
    }

    public void Create(CEventWnd pParent, int nStartFrame, int nEndFrame,
                       boolean bLoop, boolean bOwnerDraw) {
        Reset();

        m_pParent = pParent;
        m_nStartFrame = nStartFrame;
        m_nEndFrame = nEndFrame;
        m_bLoop = bLoop;
        m_bOwnerDraw = bOwnerDraw;
    }

    private void Reset() {
        m_pParent = null;
        m_nAnimID = IDA_NONE;

        m_nStartFrame = 0;
        m_nEndFrame = 0;
        m_nCurFrame = 0;
        m_bLoop = false;

        m_nSpeed = 8;
		m_nSpeed = 16;	// DEBUG	by hrh 2011-0510
		
        m_bVisible = true;
        m_bPause = false;
        m_bEnd = false;
        m_bOwnerDraw = true;
        m_nDirection = 1;

        m_bActive = false;
        m_bParentRegistered = false;

        m_callBackType = CALLBACK_NONE;
        m_nCallBackFrameNum = 0;
        m_pProc = null;
        m_pParam = null;

        Stop();
    }

    private void Register() {
        if ( (m_pParent!=null) && !m_bParentRegistered) {
            m_pParent.AddAnimation(this);
            m_bParentRegistered = true;
        }
    }

    private void Unregister() {
        if ((m_pParent!=null) && m_bParentRegistered) {
            m_pParent.RemoveAnimation(this);
            m_bParentRegistered = false;
        }
    }

    public void Start() {
        Stop();
        m_nCurFrame = m_nStartFrame;
        m_bPause = false;
        m_bEnd = false;
        m_nSpeedFrame = 0;
        m_nDirection = 1;

        m_bActive = true;

        Register();

        BindResource();

        SetFrame(m_nCurFrame);
    }

    public void Stop() {
        m_bActive = false;
        m_nSpeedFrame = 0;

        Unregister();

        UnbindResource();
    }

    public void Pause() {
        m_bPause = true;
    }

    public void Resume() {
        m_bPause = false;
    }

    public boolean Animate(int dwKeys) {
        boolean bRet = true;
        if (!IsActive()) {
            Start();
        } while (!IsEnd()) {
            if ((m_pParent!=null) && m_bParentRegistered) {
                m_pParent.UpdateWindow();
            } else
                Show();
        }
        return bRet;
    }

    public void Show() {
        Draw();
        UpdateFrame();
    }

    public void Draw() {
        if (GetVisible())
            DrawProc();
    }

    public boolean UpdateFrame() {
        if (!IsActive())
            return false;
        if (IsPaused())
            return false;

        m_nCurFrame = CalcNextFrame();

        EndFrameProc();

        SetFrame(m_nCurFrame);

        CallBackFrameProc();

        return true;
    }

    public void SetFrame(int xx) {
    }

    public void Reverse() {
        m_nDirection = -m_nDirection;
    }

    public int GetDirection() {
        return m_nDirection;
    }

    public void SetVisible(boolean bVisible) {
        m_bVisible = bVisible;
    }

    public final boolean GetVisible() {
        return m_bVisible;
    }

    public void SetSpeed(short nSpeed) {
        m_nSpeed = nSpeed;
    }

    public short GetSpeed() {
        return m_nSpeed;
    }

    public boolean IsPaused() {
        return m_bPause;
    }

    public boolean IsEnd() {
        return (m_bEnd || (m_nEndFrame == m_nStartFrame));
    }

    public boolean IsLoop() {
        return m_bLoop;
    }

    // app develper don't use these functions. for only lib developers.
    public boolean IsDrawBylib() {
        return m_bOwnerDraw;
    }

    public void SetDrawBylib(boolean bOwnerDraw) {
        m_bOwnerDraw = bOwnerDraw;
    }

    public boolean IsActive() {
        return m_bActive;
    }

    protected abstract void DrawProc();

    protected void BindResource() {}

    protected void UnbindResource() {}

    protected int GetCurFrameNum() {
        return m_nCurFrame;
    }

    private int CalcNextFrame() {
        int nNextFrame = m_nCurFrame;
        m_nSpeedFrame += GetSpeed();
        int nStep = m_nSpeedFrame / 8;
        m_nSpeedFrame %= 8;

        nStep *= m_nDirection;
        nNextFrame += nStep;

        return nNextFrame;
    }

    private void EndFrameProc() {
        if (m_nDirection > 0) {
            if (m_nCurFrame > m_nEndFrame - 1) {
                if (m_bLoop) {
                    m_nCurFrame = m_nStartFrame;
                } else {
                    m_nCurFrame = m_nEndFrame - 1;
                    m_bActive = false;
                    m_bEnd = true;
                }
            }
        } else {
            if (m_nCurFrame < m_nStartFrame) {
                if (m_bLoop) {
                    m_nCurFrame = m_nEndFrame - 1;
                } else {
                    m_nCurFrame = m_nStartFrame;
                    m_bActive = false;
                    m_bEnd = true;
                }
            }
        }
    }

    private void CallBackFrameProc() {
        switch (m_callBackType) {
        case CALLBACK_NONE:
            break;
        case CALLBACK_LAST_FRM:
            if (IsEnd()) {
                if (m_pProc != null)
                    m_pProc.onAnimAction(this, m_pParam);
            }
            break;
        case CALLBACK_SPEC_FRM:
            if (m_nCurFrame == m_nCallBackFrameNum) {
                if (m_pProc != null)
                    m_pProc.onAnimAction(this, m_pParam);
            }
            break;
        case CALLBACK_EVER_FRM:
            if (m_pProc != null)
                m_pProc.onAnimAction(this, m_pParam);
            break;
        default:
            break;
        }
    }
}
