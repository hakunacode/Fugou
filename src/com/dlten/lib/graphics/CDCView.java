package com.dlten.lib.graphics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.util.AttributeSet;

import com.dlten.lib.Common;
import com.dlten.lib.STD;
import com.dlten.lib.frmWork.CEventWnd;

public abstract class CDCView extends SurfaceView {

    // graphics relation
    protected SurfaceHolder m_holder;
    protected Canvas m_canvas;
    protected Bitmap m_bmpDblBuffer;
    
    public static float m_fScaleRes;
    public static float m_fScaleCode;
    
    public static int m_drawWidth;
    public static int m_drawHeight;
    
    public static int CODE_WIDTH;
    public static int CODE_HEIGHT;
    
    public static int RES_WIDTH;
    public static int RES_HEIGHT;
    
    public static int HALF_WIDTH;
    public static int HALF_HEIGHT;
    
	public static int SC_WIDTH;
	public static int SC_HEIGHT;
	
    protected static int m_nWndOffsetX;		// screen pixel
    protected static int m_nWndOffsetY;		// screen pixel
    
    protected static int m_nDblOffsetX;		// screen pixel
    protected static int m_nDblOffsetY;		// screen pixel
    
    private static int m_bufWidth  = 1024;
    private static int m_bufHeight = 1024;
    
    // font relation
    protected int m_nFontSize = 36;
    protected int FONT_H;
    protected int FONT_BASELINE;
    
    public static void setCodeWidth( int nCodeWidth ) {
    	CODE_WIDTH = nCodeWidth;
    }
    public static int getCodeWidth() {
    	return CODE_WIDTH;
    }
    public static void setCodeHeight( int nCodeHeight ) {
    	CODE_HEIGHT = nCodeHeight;
    }
    public static int getCodeHeight() {
    	return CODE_HEIGHT;
    }
    
    public static void setResWidth( int nResWidth ) {
    	RES_WIDTH = nResWidth;
    }
    public static int getResWidth() {
    	return RES_WIDTH;
    }
    public static void setResHeight( int nResHeight ) {
    	RES_HEIGHT = nResHeight;
    }
    public static int getResHeight() {
    	return RES_HEIGHT;
    }
    
    public static int getDrawWidth() {
    	return m_drawWidth;
    }
    public static int getDrawHeight() {
    	return m_drawHeight;
    }
    public static void setScaleCode( float fScale ) {
    	m_fScaleCode = fScale;
    }
    public static float getScaleCode() {
    	return m_fScaleCode;
    }
    public static void setScaleRes( float fScale ) {
    	m_fScaleRes = fScale;
    }
    public static float getScaleRes() {
    	return m_fScaleRes;
    }
    public static int getDblOffsetX() {
    	return m_nDblOffsetX;
    }
    public static int getDblOffsetY() {
    	return m_nDblOffsetY;
    }
    public int getCodePosX(int drawPosX) {
		int resPosX = (int) Common.screen2code(drawPosX - m_nDblOffsetX);
		
		return resPosX;
    }
    public int getCodePosY(int drawPosY) {
		int resPosY = (int) Common.screen2code(drawPosY - m_nDblOffsetY);
		
    	return resPosY;
    }
    public static int getBufWidth() {
    	return m_bufWidth;
    }
    public static int getBufHeight() {
    	return m_bufHeight;
    }
    
    
    public CDCView(Context context) {
        super(context);
        init();
    }
    public CDCView( Context context, AttributeSet attrs ) {
		super( context, attrs );
		init();
	}
    
    private void init() {
    	m_holder = getHolder();
        m_holder.setType(SurfaceHolder.SURFACE_TYPE_GPU);        
    }
    
    // Added by hrh 2011-0525_<
	public void surfaceChanged( SurfaceHolder holder, int format, int width, int height ) {
	}
	public void surfaceCreated( SurfaceHolder holder ) {
	}
	public void surfaceDestroyed( SurfaceHolder holder ) {
	}
    public void onPause() {
    }
    public void onResume() {
    }
    public void setEvent(Runnable r) {
    }
    // Added by hrh 2011-0525_>
    
    //////////////////////////////////////////////////////////////////
    // All of UI callback functions
    ////////////////////////////////////////////////////////////////// 

	// implementing update.
    synchronized public void update( CEventWnd pWnd ) {}
    
    public SurfaceHolder getSurfaceHolder() {
    	return m_holder;	//mHolder;
   	}
    
	Matrix m_matrix = new Matrix();
	
    public void prepareDC() {
        int nBufWidth = getWidth();
        int nBufHeight = getHeight();

		SC_WIDTH = nBufWidth;
		SC_HEIGHT = nBufHeight;
		
        m_drawWidth  = (int) Common.res2screen((float)RES_WIDTH);
        m_drawHeight = (int) Common.res2screen((float)RES_HEIGHT);
        
//        int nHeightLimit = REAL_WIDTH * 854/480;
//    	REAL_HEIGHT = nBufHeight * REAL_WIDTH / nBufWidth;
//    	if( REAL_HEIGHT > nHeightLimit )
//    		REAL_HEIGHT = nHeightLimit;
    	
        HALF_WIDTH  = RES_WIDTH/2;
        HALF_HEIGHT = RES_HEIGHT/2;

    	m_nWndOffsetX = 0; //(nBufWidth - REAL_WIDTH) / 2;
        m_nWndOffsetY = 0; //(nBufHeight - REAL_HEIGHT) / 2;

        m_nDblOffsetX = (nBufWidth  - m_drawWidth) / 2;
        m_nDblOffsetY = (nBufHeight - m_drawHeight) / 2;
        
        STD.logout("init(" + nBufWidth + ", " + nBufHeight + ")");
    	STD.logout("Drawing(" + RES_WIDTH + ", " + RES_HEIGHT + ")");

    	
    	
    	m_matrix.postScale(m_fScaleRes, m_fScaleRes);
//    	m_matrix.postTranslate(m_nDblOffsetX, m_nDblOffsetY);
    	
    	createDoubleBuffer();
    }
    
    public void releaseDC() {}
    
    private void createDoubleBuffer() {
//    	m_bufWidth = REAL_WIDTH;
//    	m_bufHeight = REAL_HEIGHT;
    	m_bufWidth  = 1024;
    	m_bufHeight = 1024;
    	
    	m_bmpDblBuffer = Bitmap.createBitmap(m_bufWidth, m_bufHeight, Bitmap.Config.RGB_565);
        m_canvas = new Canvas(m_bmpDblBuffer);
    }

    public void setARGB( int nAlpha, int nR, int nG, int nB ) {}					////////
    public void setAlpha( int nAlpha ) {}											////////
    protected void fillRect( int nLeft, int nTop, int nWidth, int nHeight ) {}		////////
    public void setRotate( float fDegress, int x, int y ) {}						////////
    public void drawString( String str, float nPosX, float nPosY, int nAnchor ) {}	////////
    protected void setFont() {}														////////
    public void drawImage( Bitmap img, Matrix matrix ) {}						////////
    public void drawImage( int textureID, float x, float y, float w, float h, float scaleX, float scaleY, CRect rectTexture ) {}	////////


    
    
    protected final int MAKE_RGB( int nR, int nG, int nB ) {
    	int nColor = ((nR & 0xFF) << 16) | ((nG & 0xFF) << 8) | ((nB & 0xFF) << 0);
    	return nColor;
    }
    public void clear() {
        clear(0);
    }
    public final void clear( int nColor ) {
        setColor(nColor);
        fillRect(0, 0, m_bufWidth, m_bufHeight);
    }

    public final void setColor( int nColor ) {
        int nR = (nColor >>> 16) & 0xFF;
        int nG = (nColor >>> 8) & 0xFF;
        int nB = nColor & 0xFF;

        setColor( nR, nG, nB );
    }
    public final void setColor( int nR, int nG, int nB ) {
    	setARGB(0xFF, nR, nG, nB);
    }
    protected final void fillRect( int[] rect ) {
    	fillRect( rect[0], rect[1], rect[2], rect[3] );
    }
    public final void setRotate( float fDegress ) {
    	setRotate( fDegress, 0, 0 );
    }
    // String drawing
    public final void drawString( String str, int nPosX, int nPosY ) {
    	drawString(str, nPosX, nPosY, ANCHOR_LEFT | ANCHOR_TOP );
    }
    private static final char RETURN_CHAR = '\n';
    public int drawRectString(String str, int x, int y, int width, int height, int cur, int maxlines, int align) {
    	if (str == null)
    		return -1;
    	
    	int length = str.length();
    	if ( length == 0 || cur > length-1 )
    		return -1;
    	
    	if (maxlines < 0)
    		maxlines = 50;

    	int	nLineHieght = FONT_H + 6;
    	char[] chArray = str.toCharArray();
    	int nCharCount = 0, offsetY = 0;
    	int nLines = 0;
    	String[] strArray = new String[maxlines];
//    	float	fWidths[] = new float[1];
    	int		nLineWidth = 0;
    	while (nLines < maxlines && cur < length) {
	    	nCharCount = 0;
	    	while (chArray[cur] == ' ' || chArray[cur] == RETURN_CHAR)
	    		cur++;
	    	
	    	if (offsetY + FONT_H > height)
	    		break;
	    	
	    	while (cur + nCharCount < length) {
		    	nCharCount++;
//		    	m_font.getTextWidths(chArray, cur, nCharCount, fWidths);
//		    	nLineWidth = (int)fWidths[0];
		    	if (nLineWidth > width &&
			    	chArray[cur+nCharCount-1] != '。' &&
			    	chArray[cur+nCharCount-1] != '）' &&
			    	chArray[cur+nCharCount-1] != '?' )
		    	{
			    	nCharCount--;
			    	break;
		    	}
		    	
		    	if (chArray[cur + nCharCount - 1] == RETURN_CHAR) {
		    		nCharCount--;
			    	break;
		    	}
	    	}
	    	strArray[nLines] = new String(chArray, cur, nCharCount);
	    	if (nCharCount == 0)
	    		break;
	    	offsetY += nLineHieght;
	    	cur += nCharCount;
	    	nLines++;
    	}

    	int drawX;
    	int option;
    	if ((align & ANCHOR_H_FILTER) == ANCHOR_RIGHT) {
	    	drawX = x + width;
	    	option = ANCHOR_RIGHT | ANCHOR_TOP;
    	} else if ((align & ANCHOR_H_FILTER) == ANCHOR_CENTER) {
	    	drawX = x + width / 2;
	    	option = ANCHOR_CENTER | ANCHOR_TOP;
    	} else {
	    	drawX = x;
	    	option = ANCHOR_LEFT | ANCHOR_TOP;
    	}
    	if ((align & ANCHOR_V_FILTER) == ANCHOR_TOP) {
    		offsetY = 2;
    	} else if ((align & ANCHOR_V_FILTER) == ANCHOR_MIDDLE) {
    		offsetY = (height - offsetY) / 2;
    	} else {
    		offsetY = height - offsetY;
    	}

    	// offsetY = (height - offsetY) / 2;
    	for (int i = 0; i < nLines; i++) {
	    	drawString(strArray[i], drawX, y + offsetY, option);
	    	offsetY += nLineHieght;
    	}
    	if (cur > length - 1)
    		return -1;
    	else
    		return cur;
    }
    public final int setFontSize(int nSize) {
    	nSize = (int)Common.code2screen(nSize);
    	
    	int	nOldSize = m_nFontSize;
    	
    	if (m_nFontSize != nSize) {
        	m_nFontSize = nSize;
    		setFont();
    	}
    	return (int)Common.screen2code(nOldSize);
    }
    public final int getFontSize() {
    	return (int)Common.screen2code(m_nFontSize);
    }
    
    
    
    
    // drawing relation
    public static final int ANCHOR_TOP      = CBmpManager.ANCHOR_TOP;
    public static final int ANCHOR_MIDDLE   = CBmpManager.ANCHOR_MIDDLE;
    public static final int ANCHOR_VCENTER  = CBmpManager.ANCHOR_MIDDLE;
    public static final int ANCHOR_BOTTOM   = CBmpManager.ANCHOR_BOTTOM;
    public static final int ANCHOR_LEFT     = CBmpManager.ANCHOR_LEFT;
    public static final int ANCHOR_HCENTER  = CBmpManager.ANCHOR_CENTER;
    public static final int ANCHOR_CENTER   = CBmpManager.ANCHOR_CENTER;
    public static final int ANCHOR_RIGHT    = CBmpManager.ANCHOR_RIGHT;

    public static final int ANCHOR_V_FILTER = CBmpManager.ANCHOR_V_FILTER;
    public static final int ANCHOR_H_FILTER = CBmpManager.ANCHOR_H_FILTER;
}
