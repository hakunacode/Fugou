package com.dlten.lib.file;

import java.io.InputStream;

import com.dlten.lib.STD;

import android.content.Context;

/**
 * Context dependent Resource loader implementation(ReadOnly)
 *
 */
public class CResFile {
	private static Context m_context;
	public static void Initialize( Context context ) {
		m_context = context;
	}
	public static Context getAppContext() {
		return m_context;
	}
	
    public static byte[] load( String strAsset ) {
    	byte[] byResult = null;
    	try {
    		InputStream is = m_context.getAssets().open(strAsset);
    		if (is != null) {
        		byResult = new byte[is.available()];
        		is.read(byResult);
        		is.close();
    		}
    	} catch (Exception e ) {
    		STD.logout("loadBitmap failed : name=" + strAsset);
    		STD.printStackTrace(e);
			byResult = null;
    	}
    	return byResult;
    }
    public static byte[] load( int nResID ) {
    	byte[] byResult = null;
    	try {
    		InputStream is = m_context.getResources().openRawResource(nResID);
    		if (is != null) {
        		byResult = new byte[is.available()];
        		is.read(byResult);
        		is.close();
    		}
    	} catch (Exception e ) {
    		STD.logout("loadBitmap failed : id=" + nResID);
    		STD.printStackTrace(e);
    		byResult = null;
    	}
    	return byResult;
    }
}
