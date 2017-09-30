package com.dlten.lib.graphics;

import com.dlten.lib.file.CResFile;

import android.graphics.Bitmap;
import android.graphics.Matrix;

/**
 * <p>Title: CImage class</p>
 *
 * <p>Description: Image split & managing class</p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * @version 1.0
 */
public class CImage {
	protected int m_totalWidth  = 0;	// resource pixel
	protected int m_totalHeight = 0;	// resource pixel
	
	protected int m_nCWidth  = 0;	// resource pixel
	protected int m_nCHeight = 0;	// resource pixel

	protected byte m_byRows = 0;
	protected byte m_byCols = 0;
    
	// static members.
	private static CDCView m_dcView = null;
	
    public static void Initialize( CDCView dcView ) {
    	m_dcView = dcView;
        CBmpManager.Initialize();
    }
    public static CDCView getDCView() {
    	return m_dcView;
    }
    
    public static float[] getLeftTopPos( float fWidth, float fHeight, int nAnchor ) {
    	return CBmpManager.getLeftTopPos(fWidth, fHeight, nAnchor);
    }
    
    
    public CImage() {
    }
    // Load from file path in "asset" folder
    public void load( String strAsset ) {
    	load( strAsset, 1, 1 );
    }
    public void load( String strAsset, int nCol, int nRow ) {
    	byte[] byResult = CResFile.load(strAsset);
    	load( byResult );
        setColRows( nCol, nRow );
    }
    
    // Load from resource id
    public void load( int nResID ) {
    	load( nResID, 1, 1 );
    }
    public void load( int nResID, int nCol, int nRow ) {
    	byte[] byResult = CResFile.load(nResID);
    	
    	load( byResult );
        setColRows( nCol, nRow );
        
        byResult = null;
    }
    // Load from bitmap
    public void load( Bitmap bmp ) {
    	load( bmp, 1, 1 );
    }
    public void load( Bitmap bmp, int nCol, int nRow ) {
    	loadResource( bmp );
        setColRows( nCol, nRow );
    }
    
    // Load from byte array
    protected void load( byte[] byData ) {
    	Bitmap bmp = CBmpManager.loadImage(byData);
    	loadResource( bmp );
    	
//    	bmp.recycle();
//    	bmp = null;
    }
    
    public void unload() {
    	unloadResource();
    }
    
    protected int loadResource( Bitmap img ) {
    	m_totalWidth  = img.getWidth();
    	m_totalHeight = img.getHeight();
    	
    	return -1;
    }
    
    public void setColRows( int nCol, int nRow ) {
        m_byRows = (byte) nRow;
        m_byCols = (byte) nCol;

        if( m_byRows <= 0 )
            m_byRows = 1;
        if( m_byCols <= 0 )
            m_byCols = 1;

        calcSize();
        reloadImages();
    }
    
    protected void calcSize() {
        m_nCWidth  = m_totalWidth  / m_byCols;
        m_nCHeight = m_totalHeight / m_byRows;
    }

    public int getWidth() {
    	return m_totalWidth;
    }
    public int getHeight() {
    	return m_totalHeight;
    }

    public int getCols() {
    	return m_byCols;
    }
    public int getRows() {
    	return m_byRows;
    }

    public int getCWidth() {
        return m_nCWidth;
    }
    public int getCHeight() {
        return m_nCHeight;
    }
    
    protected void unloadResource() {}
    protected void reloadImages() {}
    public void drawImage( float fPosX, float fPosY, float fScaleX, float fScaleY, int nAlpha, Matrix matrix ) {}
    
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
