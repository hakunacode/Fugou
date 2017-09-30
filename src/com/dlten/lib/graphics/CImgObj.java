package com.dlten.lib.graphics;


import android.graphics.Matrix;

import com.dlten.lib.Common;
import com.dlten.lib.STD;

public class CImgObj {

	public static final int 
		MODE_BITMAP  = 0,
		MODE_TEXTURE = 1;
	private static int DRAW_MODE = MODE_TEXTURE;
	
	private CImage	m_imgParent;
	
	private CRect m_rect = new CRect();			// code pixel
	private CRect m_rtTemp = new CRect();
	private int m_nAnchor;
	private int m_nFilterColor;
	private int m_nAlpha;
	private float m_scaleX, m_scaleY;
	private int m_rotate; // 0~359
	private boolean m_visible;

	public static void SetMode(int mode) {
		DRAW_MODE = mode;
	}
	
	public CImgObj() {
		init();
	}
	public CImgObj( String strAsset ) {
		init();
		load( strAsset );
	}
	private void init() {
		m_imgParent = null;
		setAnchor( CImage.ANCHOR_LEFT | CImage.ANCHOR_TOP );
		setFilterColor( 0xFFFFFF );
		setAlpha( 0xFF );
		rotateTo( 0 );
		setScaleX(1.0f);
		setScaleY(1.0f);
		setVisible(true);
	}
	public void load( String strAsset ) {
		if (strAsset == null) {
			STD.logout("CImgObj.load()	File name is null!");
			return;
		}
		
		CRect rect = new CRect(m_rect.left, m_rect.top, m_rect.width, m_rect.height);
		int nAnchor = m_nAnchor;
		int nFilterColor = m_nFilterColor;
		int nAlpha = m_nAlpha;
		float scaleX = m_scaleX;
		float scaleY = m_scaleY;
		int rotate = m_rotate;
		boolean visible = m_visible;
		
		boolean	bReload = false;
		if (m_imgParent != null) {
			bReload = true;
		}
		
		CImage img = null;
		switch (DRAW_MODE) {
		case MODE_BITMAP:		img = (CImage) new CImageBitmap();		break;
		case MODE_TEXTURE:		
		default:				img = (CImage) new CImageTexture();		break;
		}
		img.load(strAsset, 1, 1);
		setParent(img);
		
		if (bReload == true) {
			m_rect.left    = rect.left;
			m_rect.top     = rect.top;
			m_rect.width   = rect.width;
			m_rect.height  = rect.height;
			m_nAnchor      = nAnchor;
			m_nFilterColor = nFilterColor;
			m_nAlpha       = nAlpha;
			m_scaleX       = scaleX;
			m_scaleY       = scaleY;
			m_rotate       = rotate;
			m_visible      = visible;
		}
	}
    public void unload() {
    	if (m_imgParent == null)
    		return;
    	
    	m_imgParent.unload();
    	m_imgParent = null;
		m_rect.SetRectEmpty();
    }
	public void setParent( CImage imgParent ) {
		setParent( imgParent, 0 );
	}
	public void setParent( CImage imgParent, int nIndex ) {
		m_imgParent = imgParent;
		m_rect.SetRectEmpty();
		updateSize();
	}
	private void updateSize() {
		if( m_imgParent != null ) {
			m_rect.width  = Common.res2code( m_imgParent.getCWidth() );
			m_rect.height = Common.res2code( m_imgParent.getCHeight() );
		}
	}
	private void updateWidth() {
		if( m_imgParent != null ) {
			m_rect.width = Common.res2code( m_imgParent.getCWidth() );
		}
	}
	private void updateHeight() {
		if( m_imgParent != null ) {
			m_rect.height = Common.res2code( m_imgParent.getCHeight() );
		}
	}
//	public CImage getParent() {
//		return m_imgParent;
//	}
	
	public void draw() {
		drawImpl(m_rect.left, m_rect.top);
	}
	public void draw( CPoint pt ) {
		if( pt == null )
			return;
		draw( pt.x, pt.y );
	}
	public void draw(float x, float y) {
		drawImpl(x, y);
	}
	
	private void drawImpl( float fPosX, float fPosY ) {
		if( m_imgParent == null )
			return;
		
		if (getVisible() == false)
			return;
    	
    	fPosX = Common.code2screen(fPosX);
    	fPosY = Common.code2screen(fPosY);
    	
		// reflect : scale, rotate with matrix
		Matrix matrix = calcMatrix( fPosX, fPosY );
		
		m_imgParent.drawImage( fPosX, fPosY, m_scaleX, m_scaleY, m_nAlpha, matrix );
	}

	private Matrix calcMatrix( float fPosX, float fPosY ) {
		int	width  = m_imgParent.getCWidth();
		int	height = m_imgParent.getCHeight();
		
		float[] offset = CImage.getLeftTopPos(width, height, m_nAnchor);
		Matrix matrix = new Matrix();
		
		if( m_rotate != 0 )
			matrix.postRotate(m_rotate, -offset[0], -offset[1]);
		
		if( m_scaleX != 1 || m_scaleY != 1 )
			matrix.postScale(m_scaleX, m_scaleY, -offset[0], -offset[1]);
		
		matrix.postTranslate(fPosX+offset[0], fPosY+offset[1]);
		
		return matrix;
	}
	public void moveTo(float x, float y) {
		m_rect.left = x;
		m_rect.top = y;
	}
	public void moveTo( CPoint pt ) {
		moveTo(pt.x, pt.y);
	}
	public void move(float dx, float dy) {
		m_rect.OffsetRect(dx, dy);
	}

	public CPoint getPos() {
		return getRect().TopLeft();
	}
	public CPoint getCenterPos() {
		return getRect().CenterPoint();
	}
	public CRect getRect() {
		m_rtTemp.CopyRect(m_rect);
		float[] posOffset = CImage.getLeftTopPos(m_rtTemp.width, m_rtTemp.height, m_nAnchor);
		m_rtTemp.OffsetRect(posOffset[0], posOffset[1]);
		return m_rtTemp;
	}
	
	public void setAnchor( int nAnchor ) {
		m_nAnchor = nAnchor;
	}
	public int getAnchor() {
		return m_nAnchor;
	}
	
	public void setFilterColor( int nColor ) {
		m_nFilterColor = nColor;
	}
	public int getFilterColor() {
		return m_nFilterColor;
	}
	
	public void setAlpha( int nAlpha ) {
		m_nAlpha = nAlpha;
	}
	public int getAlpha() {
		return m_nAlpha;
	}
	
	public void rotate(int dr) {
		m_rotate += dr;
		checkImageChange();
	}
	public void rotateTo(int r) {
		m_rotate = r;
		checkImageChange();
	}

	public void setScale(float scaleX, float scaleY) {
		setScaleX(scaleX);
		setScaleY(scaleY);
	}
	public void setScaleX(float scaleX)	{
		updateWidth();
		m_scaleX = scaleX;
		m_rect.width = m_rect.width * scaleX;
		checkImageChange();
	}
	public void setScaleY(float scaleY) {
		updateHeight();
		m_scaleY = scaleY;
		m_rect.height = m_rect.height * scaleY;
		checkImageChange();
	}
	public float getScaleX() {
		return m_scaleX;
	}
	public float getScaleY() {
		return m_scaleY;
	}

	public void setSize(float sizeX, float sizeY) {
		setSizeX(sizeX);
		setSizeY(sizeY);
	}
	public void setSizeX(float sizeX)	{
		updateWidth();
		m_scaleX = sizeX / m_rect.width;
		m_rect.width = sizeX; 
		checkImageChange();
	}
	public void setSizeY(float sizeY) {
		updateHeight();
		m_scaleY = sizeY / m_rect.height;
		m_rect.height = sizeY;
		checkImageChange();
	}
	public float getSizeX() {
		return m_rect.width;
	}
	public float getSizeY() {
		return m_rect.height;
	}
	
	private boolean checkImageChange() {
		boolean bImgChanged;
		if( m_scaleX != 1.0f || m_scaleY != 1.0f || m_rotate != 0 )
			bImgChanged = true;
		else
			bImgChanged = false;
		
		return bImgChanged;
	}
	public boolean getVisible() {
		return m_visible;
	}
	public void setVisible(boolean visible) {
		m_visible = visible;
	}
}
