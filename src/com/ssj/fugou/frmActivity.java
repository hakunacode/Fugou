package com.ssj.fugou;

import android.os.Bundle;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import com.dlten.lib.frmWork.HandleActivity;
import com.dlten.lib.frmWork.CWnd;
// import com.dlten.lib.opengl.CBeijingGLDCView;

public class frmActivity extends HandleActivity implements View.OnClickListener {
	
	private frmView			m_viewMain = null;
	private WebView			m_viewHelp = null;
	private LinearLayout	m_lytHelp  = null;
	private boolean			m_bShowHelp = false;
	private final String	m_strHelpUrl = "file:///android_asset/help/index.htm";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
        
        LinearLayout layout = (LinearLayout) findViewById(R.id.lytMain);
        createView_Main(layout);
        createView_Help(layout);
        
        showHelp(false);
    }
    
	@Override
	protected void onDestroy() {
		if( m_viewMain != null )
			m_viewMain.Finish();
		m_viewMain = null;
		super.onDestroy();
	}

	// override keyproc functions.
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if( m_viewMain == null )
			return super.onKeyDown(keyCode, event);
		
		switch( keyCode ) {
		case KeyEvent.KEYCODE_MENU:
			m_viewMain.PostMessage( CWnd.WM_KEY_DOWN, CWnd.KEY_MENU, 0 );
			break;
		case KeyEvent.KEYCODE_BACK:
			if (m_bShowHelp) {
				showHelp(false);
			} else {
				m_viewMain.PostMessage( CWnd.WM_KEY_DOWN, CWnd.KEY_BACK, 0 );
			}
			break;
		default:
			return super.onKeyDown(keyCode, event);
		}
		return true;
	}

	public void onRecvMessage( int nMsg, int wParam, int lParam ) {
		switch( nMsg ) {
		case MSG_CHANGE_VIEW:
			if( wParam == 0 ) // show main view
				showHelp( false );
			else
				showHelp( true );
			break;
		default:
			break;
		}
	}
	
	public void showHelp(boolean bShow) {
		if( bShow ) {
	        m_viewHelp.loadUrl(m_strHelpUrl);
	        
			m_viewMain.setVisibility(View.GONE);
			m_viewHelp.setVisibility(View.VISIBLE);
			m_lytHelp.setVisibility(View.VISIBLE);
		}
		else {
			m_viewMain.setVisibility(View.VISIBLE);
			m_viewHelp.setVisibility(View.GONE);
			m_lytHelp.setVisibility(View.GONE);
		}
		m_bShowHelp = bShow;
		
		LinearLayout layout = (LinearLayout) findViewById(R.id.lytMain);
		layout.invalidate();
	}
	
	private void createView_Main(LinearLayout layout) {
        LinearLayout.LayoutParams mainViewParam =
            new LinearLayout.LayoutParams(
            		LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        
        m_viewMain = new frmView(this);
        layout.addView(m_viewMain, mainViewParam);
//        layout.invalidate();
	}
	
	private void createView_Help(LinearLayout layout) {
		m_lytHelp = (LinearLayout)findViewById(R.id.wndhelp_toolbar);
        Button btn;
        btn = (Button)findViewById(R.id.wndhelp_btn_back);		btn.setOnClickListener(this);

        //ListView lst;
        m_viewHelp = (WebView)findViewById(R.id.wndhelp_webview);
        m_viewHelp.loadUrl(m_strHelpUrl);
        m_viewHelp.getSettings().setJavaScriptEnabled(true);
        
        /*
        m_viewHelp = new WebView(this);
        m_viewHelp.loadUrl(m_strHelpUrl);
        m_viewHelp.getSettings().setJavaScriptEnabled(true);
        
        LinearLayout.LayoutParams helpViewParam =
            new LinearLayout.LayoutParams(
            		LayoutParams.FILL_PARENT, Globals.SC_HEIGHT);
        layout.addView(m_viewHelp, helpViewParam);
//        layout.invalidate();
		*/
    }
	
	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch( id ) {
			case R.id.wndhelp_btn_back:
				if (m_bShowHelp)
					showHelp(false);
				break;
		}
	}

	public static final int MSG_CHANGE_VIEW = 1;
}
