package com.dlten.lib.frmWork;

/**
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * @author not attributable
 * @version 1.0
 */
public interface MainThreadListner {
    public abstract void onRecvMessage( int nMsg, int wParam, int lParam );
}
