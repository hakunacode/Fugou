package com.dlten.lib.graphics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.Paint;
import android.util.AttributeSet;

import com.dlten.lib.STD;
import com.dlten.lib.frmWork.CEventWnd;

public abstract class CBmpDCView extends CDCView {
    
    private static Paint m_paintColor = new Paint();
    
    // font relation
    private Typeface m_fontStyle;
    private Paint m_font;
    
    
    public CBmpDCView(Context context) {
        super(context);
        initBmpDCView();
    }
    public CBmpDCView( Context context, AttributeSet attrs )
	{
		super( context, attrs );
		initBmpDCView();
	}
    private void initBmpDCView() {
        initFont();
        
        CImgObj.SetMode(CImgObj.MODE_BITMAP);
    }
    
    public void prepareDC() {
    	super.prepareDC();
    	
        clear(0);
        
//        m_paintColor.setAntiAlias(true);
        m_paintColor.setAntiAlias(false);
        m_paintColor.setDither(true);
    }

    
    //////////////////////////////////////////////////////////////////
    // All of UI callback functions
    ////////////////////////////////////////////////////////////////// 

	// implementing update.
    synchronized public void update( CEventWnd pWnd ) {
    	if( pWnd == null )
    		return;
    	Canvas canvasTemp = m_holder.lockCanvas();
        if( canvasTemp == null ) {
        	STD.ASSERT(false);
        	return;
        }

        try {
        	clear();
        	pWnd.DrawPrevProc();
        	pWnd.DrawProcess();
        } catch (Exception e) {
        	STD.printStackTrace(e);
        }

        updateGraphics( canvasTemp );
        m_holder.unlockCanvasAndPost(canvasTemp);
    }
    protected Rect m_rtTSrc = new Rect();
    protected Rect m_rtTDst = new Rect();
    public final void updateGraphics( Canvas canvasUpdate ) {
    	/*
    	m_rtTSrc.left   = 0;
    	m_rtTSrc.top    = 0;
    	m_rtTSrc.right  = m_rtTSrc.left + REAL_WIDTH;
    	m_rtTSrc.bottom = m_rtTSrc.top  + REAL_HEIGHT;
        
    	m_rtTDst.left   = m_nDblOffsetX;
    	m_rtTDst.top    = m_nDblOffsetY;
    	m_rtTDst.right  = m_rtTDst.left + m_drawWidth;
    	m_rtTDst.bottom = m_rtTDst.top  + m_drawHeight;
        
    	canvasUpdate.drawBitmap(m_bmpDblBuffer, m_rtTSrc, m_rtTDst, m_paintColor);
    	*/

    	/*
        // 이미지를 회전시킨다
        Bitmap rotateBitmap = Bitmap.createBitmap(m_bmpDblBuffer, 0, 0, m_bmpDblBuffer.getWidth(), m_bmpDblBuffer.getHeight(), m_matrix, true);
       
        // View 사이즈에 맞게 이미지를 조절한다.
        // Bitmap resize = Bitmap.createScaledBitmap(rotateBitmap, m_drawWidth, m_drawHeight, true);
    	// canvasUpdate.drawBitmap(rotateBitmap, m_nDblOffsetX, m_nDblOffsetY, m_paintColor);
        canvasUpdate.drawBitmap(rotateBitmap, m_nDblOffsetX, m_nDblOffsetY, m_paintColor);
        */
    	
    	
    	canvasUpdate.drawBitmap(m_bmpDblBuffer, m_nDblOffsetX, m_nDblOffsetY, m_paintColor);
    }
    
    // Drawing to Graphics relation
    public final int[] getRGBData( int x, int y, int w, int h ) {
    	try {
	    	Bitmap imgTarget = m_bmpDblBuffer; // this.getDrawingCache();
	    	return CBmpManager.getRGBData(imgTarget, x, y, w, h);
    	} catch (Exception e) {
    		e.printStackTrace();
    		return null;
    	}
    }
    private Bitmap bmpBackup = null;
    public final void captureDisp() {
    	bmpBackup = Bitmap.createBitmap(m_bmpDblBuffer); //this.getDrawingCache();
    }
    public final void restoreDisp() {
    	if( bmpBackup != null )
    		m_canvas.drawBitmap(bmpBackup, 0, 0, null);
    }

    
    // Original Image Drawing.
    public final void drawImage( Bitmap img, float nPosX, float nPosY ) {
        if ( img == null )
            return;
        m_canvas.drawBitmap(img, nPosX, nPosY, m_paintColor);
    }
    public final void drawImage( Bitmap img, Rect rtSrc, RectF rtDest ) {
        if ( img == null )
            return;
        m_canvas.drawBitmap(img, rtSrc, rtDest, m_paintColor);
    }
    public void drawImage( Bitmap img, Matrix matrix ) {
    	m_canvas.drawBitmap(img, matrix, m_paintColor);
    }
    private Rect m_rtSrc = new Rect();
    private RectF m_rtDst = new RectF();
    public final void drawImage( Bitmap img, CRect rtSrc, CRect rtDest ) {
        if ( img == null )
            return;
        if( rtSrc == null || rtDest == null )
        	return;
        
        m_rtSrc.set((int)rtSrc.left, (int)rtSrc.top, 
        		(int)(rtSrc.left+rtSrc.width), (int)(rtSrc.top+rtSrc.height));
        m_rtDst.set(rtDest.left, rtDest.top, 
        		rtDest.left+rtDest.width, rtDest.top+rtDest.height);
        m_canvas.drawBitmap(img, m_rtSrc, m_rtDst, m_paintColor);
    }
    // String drawing
    public void drawString( String str, float nPosX, float nPosY, int nAnchor ) {
        if( str == null )
            return;
        
        int nGap = 0;
        int nHAnchor = nAnchor & ANCHOR_H_FILTER;
        Paint.Align newAlign;
        if( nHAnchor == ANCHOR_RIGHT )
        	newAlign = Paint.Align.RIGHT;
        else if (nHAnchor == ANCHOR_HCENTER )
        	newAlign = Paint.Align.CENTER;
        else
        	newAlign = Paint.Align.LEFT;
        m_font.setTextAlign(newAlign);

        if( (nAnchor & ANCHOR_VCENTER) != 0 ) {
            nAnchor = nAnchor & (~ANCHOR_VCENTER) | ANCHOR_BOTTOM;
            nGap = -FONT_BASELINE-FONT_H/2;
        }
        else if( (nAnchor & ANCHOR_BOTTOM) != 0 ) {
            nAnchor = nAnchor & (~ANCHOR_BOTTOM) | ANCHOR_BOTTOM;
            nGap = -FONT_BASELINE-FONT_H;
        }
        else { // if( (nAnchor & ANCHOR_TOP) != 0 ) {
            nAnchor = nAnchor & (~ANCHOR_TOP) | ANCHOR_BOTTOM;
            nGap = -FONT_BASELINE;
        }
        nPosY = nPosY + nGap;

        m_font.setColor(m_paintColor.getColor());
        m_canvas.drawText(str, nPosX + m_nWndOffsetX, nPosY + m_nWndOffsetY, m_font);
        // m_canvas.drawString( str, nPosX, nPosY, nAnchor );
    }

    protected final void setClip(int[] rect ) {
        setClip(rect[0], rect[1], rect[2], rect[3]);
    }
    protected final void setClip(int x, int y, int width, int height) {
        m_canvas.clipRect(x + m_nWndOffsetX, y + m_nWndOffsetY, 
        		x + m_nWndOffsetX + width, y + m_nWndOffsetY + height, Region.Op.REPLACE);
    }
    protected final void clearClip() {
    	m_canvas.clipRect(m_nWndOffsetX, m_nWndOffsetY, 
    			m_nWndOffsetX+m_drawWidth, m_nWndOffsetY+m_drawHeight, Region.Op.REPLACE);
    }
    protected final void copyArea(int x, int y, int width, int height, 
    		int dx, int dy) {
    	copyArea(x, y, width, height, dx, dy, ANCHOR_LEFT | ANCHOR_TOP );
    }
    protected final void copyArea(int x, int y, int width, int height, 
    		int dx, int dy, int nAnchor) {
    	x += m_nWndOffsetX;
    	y += m_nWndOffsetY;
    	dx += m_nWndOffsetX;
    	dy += m_nWndOffsetY;
    	
    	int[] imgData = getRGBData(x, y, width, height);
    	float[] offset = CBmpManager.getLeftTopPos(width, height, nAnchor);
    	m_canvas.drawBitmap(imgData, 0, width, 
    			dx+offset[0], dy+offset[1], 
    			width, height, true, null);
    }
    public final void setLineWidth( float nWidth ) {
    	m_paintColor.setStrokeWidth(nWidth);
    }
    public final void drawLine( float nLeft, float nTop, float nRight, float nBottom ) {
        m_canvas.drawLine( nLeft + m_nWndOffsetX, nTop + m_nWndOffsetY,
                nRight + m_nWndOffsetX, nBottom + m_nWndOffsetY, m_paintColor );
    }
    protected final void drawRect( int[] rect ) {
    	drawRect( rect[0], rect[1], rect[2], rect[3] );
    }
    protected final void drawRect( int nLeft, int nTop, int nWidth, int nHeight ) {
    	m_paintColor.setStyle(Paint.Style.STROKE);
    	m_paintColor.setStrokeWidth(1);
        m_canvas.drawRect( nLeft + m_nWndOffsetX, nTop + m_nWndOffsetY,
        		nLeft + m_nWndOffsetX + nWidth, nTop + m_nWndOffsetY + nHeight, m_paintColor );
    }

    protected void fillRect( int nLeft, int nTop, int nWidth, int nHeight ) {
    	m_paintColor.setStyle(Paint.Style.FILL);
        m_canvas.drawRect( nLeft + m_nWndOffsetX, nTop + m_nWndOffsetY,
        		nLeft + m_nWndOffsetX + nWidth, nTop + m_nWndOffsetY + nHeight, m_paintColor );
    }

    public void setARGB( int nAlpha, int nR, int nG, int nB ) {
    	m_paintColor.setARGB(nAlpha, nR, nG, nB);
    }
    public void setAlpha( int nAlpha ) {
    	m_paintColor.setAlpha(nAlpha);
    	/*
    	float fAlpha = ((float) nAlpha)/256;
    	float[] fMatrix = new float[] {
    			1, 0, 0, 0, 0,
    			0, 1, 0, 0, 0,
    			0, 0, 1, 0, 0,
    			0, 0, 0, fAlpha, 0
    	};
    	ColorMatrixColorFilter colorFilter = 
    		new ColorMatrixColorFilter(fMatrix);
    	m_paintColor.setColorFilter(colorFilter);
    	*/
    }
    public void setRotate( float fDegress, int x, int y ) {
    	m_canvas.rotate(fDegress, x, y);
    }
    private void initFont() {
        // make font
        m_fontStyle = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL);
        m_font = new Paint();
        m_font.setTypeface(m_fontStyle);
        m_font.setTextSize(m_nFontSize);
//        m_font.setAntiAlias(true);
        m_font.setAntiAlias(false);
        m_font.setDither(true);
        FONT_BASELINE = (int) m_font.getFontMetrics().descent;
        FONT_H = (int) (m_font.getFontMetrics().top - m_font.getFontMetrics().bottom);
        setFont(m_font);
        
        setFontSize(m_nFontSize);
    }
    protected final void setFont( Paint font ) {
        m_font = font;
    	if( m_font == null )
    		return;
        FONT_H =  (int) m_font.getTextSize();
        FONT_BASELINE = (int) m_font.ascent();
    }
    protected final void setFont() {
    	m_font.setTextSize(m_nFontSize);
        setFont(m_font);
    }
    protected final Paint getFont() {
        return m_font;
    }
    protected final int getFontWidth( Paint font ) {
    	float[] fWidth = new float[1];
    	font.getTextWidths("W", fWidth);
        return (int) (fWidth[0]);
    }
    protected final int getStrWidth( String str ) {
    	if( m_font == null || str == null || str.length() <= 0 )
    		return 0;

    	float fRet = 0;
    	float[] fWidth = new float[str.length()];
    	m_font.getTextWidths(str, fWidth);
    	for( int i = 0 ; i < fWidth.length ; i++ )
    		fRet += fWidth[i];
    	
    	return (int) fRet;
    }
}
