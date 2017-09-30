package com.dlten.lib.file;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import android.content.Context;

import com.dlten.lib.STD;

/**
 * Context dependent file implementation(Read/Write available.)
 *
 */
public class CConFile {
	private static Context m_context;
	public static void Initialize( Context context ) {
		m_context = context;
		CResFile.Initialize(context);
	}
	public static Context getAppContext() {
		return m_context;
	}
	
    public static final boolean isFileExist( String strName ) {
    	try {
    		FileInputStream fis = m_context.openFileInput(strName);
        	if( fis == null )
        		return false;
        	fis.close();
        	return true;
    	} catch (FileNotFoundException e) {
    		STD.logout("isFileExist : file not exist. " + strName);
    		return false;
    	} catch (IOException e) {
    		return true;
    	} catch (Exception e) {
    		return false;
    	}
    }
    public static final boolean write( String strName, byte[] byData ) {
        byte[] byRealData = new byte[byData.length + 8];
        byte[] byDate = new byte[8];
        STD.Long2Bytes(byDate, 0, System.currentTimeMillis());
        int i;
        for( i = 0 ; i < 8 ; i++ ) {
            byRealData[i] = byDate[i];
        }
        for( i = 0 ; i < byData.length; i++ ) {
            byRealData[i + 8] = byData[i];
        }

        FileOutputStream fos = null;
    	try {
        	fos = m_context.openFileOutput(strName, Context.MODE_PRIVATE);
        	if( fos == null )
        		return false;
        	
        	fos.write(byRealData);
        	fos.close();
        	return true;
    	} catch (FileNotFoundException e) {
    		STD.logout("saveFile : file not found. " + strName);
    		return false;
    	} catch (Exception e) {
    		STD.logout("saveFile : file writing failed. " + strName);
    		return false;
    	}
    }
    public static final byte[] read( String strName ) {
    	boolean bResult = false;
    	
    	try {
    		FileInputStream fis = m_context.openFileInput(strName);
        	if( fis == null )
        		return null;

            byte[] byData = new byte[fis.available()];
        	fis.read(byData);
        	fis.close();
            
            byte[] byRealData = null;
            if( byData == null || byData.length <= 8 )
                bResult = false;
            else {
                byRealData = new byte[byData.length - 8];
                for( int i = 0 ; i < byRealData.length; i++ ) {
                    byRealData[i] = byData[i + 8];
                }
                bResult = true;
            }

            if( true == bResult )
                return byRealData;
            else
                return null;
    	} catch (FileNotFoundException e) {
    		STD.logout("readFile : file not found. " + strName);
    		return null;
    	} catch (Exception e) {
    		STD.logout("readFile : file reading failed. " + strName);
    		return null;
    	}
    }
    public static final boolean delete( String strName ) {
    	return m_context.deleteFile(strName);
    }
    public static final Calendar getFileTime( String strName ) {
    	try {
    		FileInputStream fis = m_context.openFileInput(strName);
        	if( fis == null )
        		return null;

            byte[] byData = new byte[fis.available()];
        	fis.read(byData);
        	fis.close();
            
            Calendar cal = Calendar.getInstance();
            long lDate = STD.Bytes2Long(byData, 0);
            if( lDate > 0 )
                cal.setTime( new Date(lDate) );
            else
                cal = null;
            
            return cal;
    	} catch (FileNotFoundException e) {
    		STD.logout("readFile : file not found. " + strName);
    		return null;
    	} catch (Exception e) {
    		STD.logout("readFile : file reading failed. " + strName);
    		return null;
    	}
    }




    public static byte[] loadBinaryRes( int nResID ) {
    	byte[] byResult = null;
    	try {
    		InputStream is = m_context.getResources().openRawResource(nResID);
    		if (is == null)
    			return null;
    		byResult = new byte[is.available()];
    		is.read(byResult);
    		is.close();
    	} catch (Exception e ) {
            STD.logout("reading binary error. nResID = " + nResID);
    		byResult = null;
    	}
    	return byResult;
    }
    public static byte[] loadBinaryAssets( String strFileName ) {
    	byte[] byResult = null;
    	try {
    		InputStream is = m_context.getAssets().open(strFileName);
    		if (is == null)
    			return null;
    		byResult = new byte[is.available()];
    		is.read(byResult);
    		is.close();
    	} catch (Exception e ) {
            STD.logout("strFileName = " + strFileName);
    		byResult = null;
    	}
    	return byResult;
    }
}
