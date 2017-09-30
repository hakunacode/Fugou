package com.dlten.lib.graphics;

import com.dlten.lib.STD;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

public class CGLDCView extends GLSurfaceView {

    public static int REAL_WIDTH = 480;
    public static int REAL_HEIGHT;
    public static int HALF_WIDTH;
    public static int HALF_HEIGHT;
    private int m_nWndOffsetX;
    private int m_nWndOffsetY;
    private int m_nDblOffsetX;
    private int m_nDblOffsetY;
    
    private CRenderer m_renderer = null;

    public CGLDCView(Context context) {
        super(context);
        initCBaseView();
    }
    public CGLDCView( Context context, AttributeSet attrs )
	{
		super( context, attrs );
		initCBaseView();
	}
    private void initCBaseView() {
    	m_renderer = new CRenderer();
    	this.setRenderer(m_renderer);
    }
    public void prepareDC() {
        int nBufWidth = getWidth();
        int nBufHeight = getHeight();
    	
        int nHeightLimit = REAL_WIDTH * 854/480;
    	REAL_HEIGHT = nBufHeight * REAL_WIDTH / nBufWidth;
    	if( REAL_HEIGHT > nHeightLimit )
    		REAL_HEIGHT = nHeightLimit;
    	STD.logout("init(" + nBufWidth + ", " + nBufHeight + ")");
    	STD.logout("Drawing(" + REAL_WIDTH + ", " + REAL_HEIGHT + ")");
        HALF_WIDTH = REAL_WIDTH/2;
        HALF_HEIGHT = REAL_HEIGHT/2;


    	m_nWndOffsetX = 0; //(nBufWidth - REAL_WIDTH) / 2;
        m_nWndOffsetY = 0; //(nBufHeight - REAL_HEIGHT) / 2;

        m_nDblOffsetX = 0; //(nBufWidth - REAL_WIDTH) / 2;
        m_nDblOffsetY = 0; //(nBufHeight - REAL_HEIGHT) / 2;
    }
    
    public static void setResWidth( int nResWidth ) {
    	REAL_WIDTH = nResWidth;
    }

}









































