package com.dlten.lib.graphics;

import java.io.InputStream;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class CBmpManager {

	// Initialize
	private static BitmapFactory.Options m_sBmpOption = new BitmapFactory.Options();
	public static void Initialize() {
    	m_sBmpOption.inPreferredConfig = Bitmap.Config.RGB_565;
	}
	
    // Loading
    public static Bitmap loadImage( Context context, String strAssetName ) {
    	byte[] byResult = null;
    	try {
    		InputStream is = context.getAssets().open(strAssetName);
    		if (is == null)
    			return null;
    		byResult = new byte[is.available()];
    		is.read(byResult);
    		is.close();
    	} catch (Exception e ) {
			byResult = null;
    	}
    	return loadImage(byResult);
    }
    public static Bitmap loadImage( Resources res, int resID ) {
    	Bitmap img = null;
    	img = BitmapFactory.decodeResource( res, resID, m_sBmpOption );
        return img;
    }
    public static Bitmap loadImage( byte[] byData ) {
    	if( byData == null )
    		return null;
    	return loadImage( byData, 0, byData.length );
    }
    public static Bitmap loadImage( byte[] byData, int nOffset, int nLen ) {
    	Bitmap img = null;
        try {
            img = BitmapFactory.decodeByteArray(byData, nOffset, nLen, m_sBmpOption);
            return img;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return img;
    }
    
    // get bitmap datas.
    public static int[] getRGBData(Bitmap img) {
    	if( img == null )
    		return null;
    	int w = img.getWidth();
    	int h = img.getHeight();
    	
    	return getRGBData(img, 0, 0, w, h);
    }
    public static int[] getRGBData(Bitmap img, int x, int y, int w, int h ) {
    	if( img == null )
    		return null;

    	int nLen = w * h;
    	int[] nRet = new int[nLen];

    	try {
    		img.getPixels(nRet, 0, w, x, y, w, h);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}

    	return nRet;
    }

    // get bitmap datas.
    public static float[] getLeftTopPos( float fWidth, float fHeight, int nAnchor ) {
    	float[] pos = new float[2];

        float fPosX = 0;
        float fPosY = 0;

        switch( nAnchor & ANCHOR_H_FILTER ) {
            case ANCHOR_LEFT:
                break;
            case ANCHOR_CENTER:
                pos[0] = fPosX - fWidth / 2;
                break;
            case ANCHOR_RIGHT:
                pos[0] = fPosX - fWidth;
                break;
        }
        switch( nAnchor & ANCHOR_V_FILTER ) {
            case ANCHOR_TOP:
                break;
            case ANCHOR_MIDDLE:
                pos[1] = fPosY - fHeight / 2;
                break;
            case ANCHOR_BOTTOM:
                pos[1] = fPosY - fHeight;
                break;
        }

        return pos;
    }

    public static final int ANCHOR_TOP = 0x01;
    public static final int ANCHOR_MIDDLE = 0x02;
    public static final int ANCHOR_BOTTOM = 0x04;
    public static final int ANCHOR_LEFT = 0x10;
    public static final int ANCHOR_CENTER = 0x20;
    public static final int ANCHOR_RIGHT = 0x40;

    public static final int ANCHOR_V_FILTER = ANCHOR_TOP | ANCHOR_MIDDLE | ANCHOR_BOTTOM;
    public static final int ANCHOR_H_FILTER = ANCHOR_LEFT | ANCHOR_CENTER | ANCHOR_RIGHT;
    
    public static final int TRANS_NONE = 0;
    public static final int TRANS_MIRROR = 1;
    public static final int TRANS_ROT180 = 2;
    public static final int TRANS_MIRROR_ROT180 = 3;
    public static final int TRANS_MIRROR_ROT90 = 4;
}
