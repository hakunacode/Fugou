package com.dlten.lib.graphics;

import com.dlten.lib.STD;

import android.graphics.Bitmap;
import android.graphics.Matrix;

/**
 * <p>Title: CImageBitma class</p>
 *
 * <p>Description: Image split & managing class</p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * @version 1.0
 */
public class CImageBitmap extends CImage {
    private Bitmap m_img = null;
    
    public CImageBitmap() {
    	super();
    }
    
    protected int loadResource( Bitmap img ) {
//    	if ((m_img != null) && (bmp == null))
//    		m_img.recycle();

    	super.loadResource(img);
    	/*
    	m_img = img;
    	*/
    	
    	///*
    	if (CDCView.m_fScaleRes != 1.0f) {
	    	CDCView dc = CImage.getDCView();
	        Bitmap rotateBitmap = null;
	        try {
		        rotateBitmap = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), dc.m_matrix, true);
	        } catch (Exception e) {
	            STD.printStackTrace(e);
	        }
	    	m_img = rotateBitmap;
//	    	img.recycle();
    	} else {
    		m_img = img;
    	}
    	//*/
    	
    	return 1;
    }
    protected void unloadResource() {
    	if (m_img != null) {
    		m_img.recycle();
    		m_img = null;
    	}
    	super.unloadResource();
    }

    public void drawImage( float fPosX, float fPosY, float fScaleX, float fScaleY, int nAlpha, Matrix matrix ) {
    	CDCView dc = CImage.getDCView();
		
		// reflect alpha
		if( nAlpha != 0xFF ) {
			dc.setAlpha(nAlpha);
		}
		
		// TODO : reflect color_filter
		
		dc.drawImage(m_img, matrix);
		
		// reflect alpha
		if( nAlpha != 0xFF ) {
			dc.setAlpha(0xFF);
		}
    }

    protected Bitmap getImage() {
    	return m_img;
    }

    public int[] getRGBData() {
    	if (m_img == null )
    		return null;
    	return getRGBData( 0, 0, m_img.getWidth(), m_img.getHeight() );
    }
    public int[] getRGBData(int x, int y, int w, int h ) {
    	return CBmpManager.getRGBData(m_img, x, y, w, h);
    }
}
