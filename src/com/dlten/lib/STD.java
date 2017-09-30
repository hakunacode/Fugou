package com.dlten.lib;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Random;

import android.os.Build;
import android.util.Log;

public class STD {

	// MEMSET
    public static void MEMSET(boolean[] bArrData, boolean bVal) {
        if (bArrData == null)
            return;
        for (int i = 0; i < bArrData.length; i++)
            bArrData[i] = bVal;
    }
    public static void MEMSET(byte[] byArrData, byte byVal) {
        if (byArrData == null)
            return;
        for (int i = 0; i < byArrData.length; i++)
            byArrData[i] = byVal;
    }
    public static void MEMSET(char[] chArrData, char chVal) {
        if (chArrData == null)
            return;
        for (int i = 0; i < chArrData.length; i++)
            chArrData[i] = chVal;
    }
    public static void MEMSET(short[] shArrData, short shVal) {
        if (shArrData == null)
            return;
        for (int i = 0; i < shArrData.length; i++)
            shArrData[i] = shVal;
    }
    public static void MEMSET(int[] nArrData, int nVal) {
        if (nArrData == null)
            return;
        for (int i = 0; i < nArrData.length; i++)
            nArrData[i] = nVal;
    }

    public static void MEMSET(byte[][] byArrData, byte byVal) {
        int i, j;
        if (byArrData == null)
            return;

        for (i = 0; i < byArrData.length; i++) {
            if (byArrData[i] == null)
                continue;

            for (j = 0; j < byArrData[i].length; j++)
                byArrData[i][j] = byVal;
        }
    }
    public static void MEMSET(char[][] chArrData, char chVal) {
        int i, j;
        if (chArrData == null)
            return;

        for (i = 0; i < chArrData.length; i++) {
            if (chArrData[i] == null)
                continue;

            for (j = 0; j < chArrData[i].length; j++)
                chArrData[i][j] = chVal;
        }
    }
    public static void MEMSET(short[][] shArrData, short shVal) {
        int i, j;
        if (shArrData == null)
            return;

        for (i = 0; i < shArrData.length; i++) {
            if (shArrData[i] == null)
                continue;

            for (j = 0; j < shArrData[i].length; j++)
                shArrData[i][j] = shVal;
        }
    }
    public static void MEMSET(int[][] nArrData, int nVal) {
        int i, j;
        if (nArrData == null)
            return;

        for (i = 0; i < nArrData.length; i++) {
            if (nArrData[i] == null)
                continue;

            for (j = 0; j < nArrData[i].length; j++)
                nArrData[i][j] = nVal;
        }
    }


    // MEMCPY by hrh
    public static void MEMCPY(byte[] nArrDest, int dstIndex, byte[] nArrSrc,
                              int srcIndex, int nCount) {
        if (dstIndex >= nArrDest.length)
            return;

        if (srcIndex >= nArrSrc.length)
            return;

        int nLen = Math.min(nArrDest.length - dstIndex,
                            nArrSrc.length - srcIndex);
        nLen = Math.min(nLen, nCount);
        for (int i = 0; i < nLen; i++)
            nArrDest[dstIndex + i] = nArrSrc[srcIndex + i];
    }

    // MEMCPY
    public static void MEMCPY(byte[] byArrDest, byte[] byArrSrc, int nCount) {
        int nLen = Math.min(byArrDest.length, byArrSrc.length);
        nLen = Math.min(nLen, nCount);
        for (int i = 0; i < nLen; i++)
            byArrDest[i] = byArrSrc[i];
    }
    public static void MEMCPY(short[] shArrDest, short[] shArrSrc, int nCount) {
        int nLen = Math.min(shArrDest.length, shArrSrc.length);
        nLen = Math.min(nLen, nCount);
        for (int i = 0; i < nLen; i++)
            shArrDest[i] = shArrSrc[i];
    }
    public static void MEMCPY(int[] nArrDest, int[] nArrSrc, int nCount) {
        int nLen = Math.min(nArrDest.length, nArrSrc.length);
        nLen = Math.min(nLen, nCount);
        for (int i = 0; i < nLen; i++)
            nArrDest[i] = nArrSrc[i];
    }

    public static void MEMCPY(boolean[] bArrDest, boolean[] bArrSrc) {
        int nLen = Math.min(bArrDest.length, bArrSrc.length);
        for (int i = 0; i < nLen; i++)
            bArrDest[i] = bArrSrc[i];
    }
    public static void MEMCPY(byte[] byArrDest, byte[] byArrSrc) {
        int nLen = Math.min(byArrDest.length, byArrSrc.length);
        for (int i = 0; i < nLen; i++)
            byArrDest[i] = byArrSrc[i];
    }
    public static void MEMCPY(char[] chArrDest, char[] chArrSrc) {
        int nLen = Math.min(chArrDest.length, chArrSrc.length);
        for (int i = 0; i < nLen; i++)
            chArrDest[i] = chArrSrc[i];
    }
    public static void MEMCPY(short[] shArrDest, short[] shArrSrc) {
        int nLen = Math.min(shArrDest.length, shArrSrc.length);
        for (int i = 0; i < nLen; i++)
            shArrDest[i] = shArrSrc[i];
    }
    public static void MEMCPY(int[] nArrDest, int[] nArrSrc) {
        int nLen = Math.min(nArrDest.length, nArrSrc.length);
        for (int i = 0; i < nLen; i++)
            nArrDest[i] = nArrSrc[i];
    }

    public static void MEMCPY(byte[][] byArrDest, byte[][] byArrSrc) {
        int i, j;
        for (i = 0; i < byArrDest.length; i++) {
            if (i >= byArrSrc.length)
                break;
            for (j = 0; j < byArrDest[i].length; j++) {
                if (j >= byArrSrc[i].length)
                    break;
                byArrDest[i][j] = byArrSrc[i][j];
            }
        }
    }
    public static void MEMCPY(char[][] chArrDest, char[][] chArrSrc) {
        int i, j;
        for (i = 0; i < chArrDest.length; i++) {
            if (i >= chArrSrc.length)
                break;
            for (j = 0; j < chArrDest[i].length; j++) {
                if (j >= chArrSrc[i].length)
                    break;
                chArrDest[i][j] = chArrSrc[i][j];
            }
        }
    }
    public static void MEMCPY(short[][] shArrDest, short[][] shArrSrc) {
        int i, j;
        for (i = 0; i < shArrDest.length; i++) {
            if (i >= shArrSrc.length)
                break;
            for (j = 0; j < shArrDest[i].length; j++) {
                if (j >= shArrSrc[i].length)
                    break;
                shArrDest[i][j] = shArrSrc[i][j];
            }
        }
    }
    public static void MEMCPY(int[][] nArrDest, int[][] nArrSrc) {
        int i, j;
        for (i = 0; i < nArrDest.length; i++) {
            if (i >= nArrSrc.length)
                break;
            for (j = 0; j < nArrDest[i].length; j++) {
                if (j >= nArrSrc[i].length)
                    break;
                nArrDest[i][j] = nArrSrc[i][j];
            }
        }
    }


    // String relations
    public static int STRCMP(String strSrc, String strDest) {
        if (strSrc.equals(strDest))
            return 0;
        else
            return strSrc.compareTo(strDest);
    }
    public static byte[] str2ByteArray(String str, int nLen) {
        byte[] byRet = new byte[nLen];
        byte[] byStr;
        if (str != null)
            byStr = str.getBytes();
        else
            byStr = new byte[0];

        for (int i = 0; i < byRet.length; i++) {
            if (i >= byStr.length)
                byRet[i] = 0;
            else
                byRet[i] = byStr[i];
        }

        return byRet;
    }
    public static String dis2Str(DataInputStream dis, int nLen) {
        String strRet = "";
        byte[] byStr = new byte[nLen];
        STD.MEMSET(byStr, (byte) 0);

        try {
            dis.read(byStr);
        } catch (IOException e) {
        }

        int nStrLen = 0;
        for (int i = 0; i < byStr.length; i++) {
            if (byStr[i] == 0)
                break;
            else
                nStrLen++;
        }

        strRet = new String(byStr, 0, nStrLen);
        return strRet;
    }
    public final String[] splitString( String strSrc, char chBound ) {
        String[] strResult = new String[200];
        String strBuf = "";
        int nCurIndex = 0;
        char chCur;

        int i;
        for( i = 0 ; i < strSrc.length() ; i++ ) {
                chCur = strSrc.charAt(i);
                if( chCur == chBound ) {
                        strResult[nCurIndex] = strBuf;
                        strBuf = "";
                        nCurIndex++;
                } else {
                        if( chCur != '\r' )
                        strBuf += chCur;
                }
        }
        if( strBuf != "" ) {
                strResult[nCurIndex] = strBuf;
                nCurIndex++;
        }

        String[] strs = new String[nCurIndex];
        for( i = 0 ; i < nCurIndex ; i++ ) {
                strs[i] = strResult[i];
        }

        return strs;
    }
    public static String getnString( int nValue, int n ) {
    	String strResult;
    	int i;

    	if( nValue >= 0 )
    		strResult = "" + nValue;
    	else
    		strResult = "";

    	for( i = strResult.length() ; i < n ; i++ ) {
    		if( nValue >= 0 )
    			strResult = "0" + strResult;
    		else
    			strResult = "-" + strResult;
    	}

    	return strResult;
    }
    public static String getCommaString( int nValue ) {
    	String str = "";
    	if( nValue == 0 )
    		return "0";

    	while ( nValue > 0 ) {
    		if( str == "" )
    			str = "" + getModStr(nValue, 1000);
    		else
    			str = getModStr(nValue, 1000) + "," + str;
    		nValue /= 1000;
    	}
    	return str;
    }
    public static String getModStr( int nMaster, int nSlave ) {
    	String strResult = "";
    	if( nMaster < nSlave )
    		return "" + (nMaster % nSlave);

    	int nBuf = nSlave;
    	while( nBuf > 1) {
    		strResult += (nMaster % nBuf) / (nBuf/10);
    		nBuf /= 10;
    	}
    	return strResult;
    }
    public static String getPercentString( int nValue ) {
    	String str;
    	if( nValue >= 10000 )
    		str = "100.00%";
    	else {
    		str = (nValue % 10) + "%";
    		nValue /= 10;
    		str = "." + (nValue % 10) + str;
    		nValue /= 10;
    		str = nValue + str;
    	}

    	return str;
    }



    // array->val, val->array
    public static long Bytes2Long(byte[] src, int offset) {
        return ((src[offset + 7]&0xFF) << 56) | ((src[offset + 6]&0xFF) << 48) |
                ((src[offset + 5]&0xFF) << 40) | ((src[offset + 4]&0xFF) << 32) |
                ((src[offset + 3]&0xFF) << 24) | ((src[offset + 2]&0xFF) << 16) |
                ((src[offset + 1]&0xFF) << 8) | (src[offset]&0xFF);
    }
    public static int Bytes2Int(byte[] src, int offset) {
        return ((src[offset + 3]&0xFF) << 24) | ((src[offset + 2]&0xFF) << 16) |
                ((src[offset + 1]&0xFF) << 8) | (src[offset]&0xFF);
    }
    public static short Bytes2Short(byte[] src, int offset) {
        return (short) (((src[offset + 1]&0xFF) << 8) | (src[offset]&0xFF));
    }
    public static void Long2Bytes( byte[] dest, int offset, long val ) {
        dest[offset + 7] = (byte) ((val >> 56) & 0xFF);
        dest[offset + 6] = (byte) ((val >> 48) & 0xFF);
        dest[offset + 5] = (byte) ((val >> 40) & 0xFF);
        dest[offset + 4] = (byte) ((val >> 32) & 0xFF);
        dest[offset + 3] = (byte) ((val >> 24) & 0xFF);
        dest[offset + 2] = (byte) ((val >> 16) & 0xFF);
        dest[offset + 1] = (byte) ((val >> 8) & 0xFF);
        dest[offset + 0] = (byte) ((val >> 0) & 0xFF);
    }
    public static void Int2Bytes(byte[] dest, int offset, int value) {
        dest[offset + 3] = (byte) ((value & 0xff000000) >> 24);
        dest[offset + 2] = (byte) ((value & 0x00ff0000) >> 16);
        dest[offset + 1] = (byte) ((value & 0x0000ff00) >> 8);
        dest[offset + 0] = (byte) ((value & 0x000000ff));
    }
    public static void Short2Bytes(byte[] dest, int offset, short value) {
        dest[offset + 1] = (byte) ((value & 0x0000ff00) >> 8);
        dest[offset ] = (byte) ((value & 0x000000ff));
    }
    public static byte[] getByteArrayFromLong( long val ) {
        byte[] byData = new byte[8];
        for( int i = 0 ; i < 8 ; i++ ) {
            byData[i] = (byte) ((val >>> (i*8)) & 0xFF);
        }
        return byData;
    }
    public static long getLongFromByteArray( byte[] byData ) {
        long val = 0;
        int nLen = byData.length;
        if( nLen > 8 )
            nLen = 8;
        for( int i = 0 ; i < nLen ; i++ ) {
            val = val | ( ((long)(byData[i] & 0xFF)) << (i*8) );
        }
        return val;
    }

    // DWORD, WORD, BYTE making
    public static short MAKEWORD( byte a, byte b ) {
        return (short) ( ((b&0xFF) << 8) | (a&0xFF) );
    }
    public static long MAKELONG( short a, short b ) {
        return (long) ( ((b&0xFFFF) << 16) | (a&0xFFFF) );
    }
    public static short LOWORD( int l ) {
        return (short) (l & 0xFFFF);
    }
    public static short HIWORD( int l ) {
        return (short) ((l>>>16) & 0xFFFF);
    }
    public static byte LOBYTE( int w ) {
        return (byte) (w & 0xFF);
    }
    public static byte HIBYTE( int w ) {
        return (byte) (((w&0xFFFF)>>>8) & 0xFF);
    }
    
    // MAX
    public static byte MAX( byte a, byte b ) {
    	return ( a >= b ? a : b );
    }
    public static short MAX( short a, short b ) {
    	return ( a >= b ? a : b );
    }
    public static int MAX( int a, int b ) {
    	return ( a >= b ? a : b );
    }
    public static long MAX( long a, long b ) {
    	return ( a >= b ? a : b );
    }
    public static float MAX( float a, float b ) {
    	return ( a >= b ? a : b );
    }
    // MIN
    public static byte MIN( byte a, byte b ) {
    	return ( a <= b ? a : b );
    }
    public static short MIN( short a, short b ) {
    	return ( a <= b ? a : b );
    }
    public static int MIN( int a, int b ) {
    	return ( a <= b ? a : b );
    }
    public static long MIN( long a, long b ) {
    	return ( a <= b ? a : b );
    }
    public static float MIN( float a, float b ) {
    	return ( a <= b ? a : b );
    }


    // System functions.
    public static final void sleep( long lTime ) {
        try {
            Thread.sleep( lTime );
        } catch ( InterruptedException e ) {
        }
    }
    public static long GetTickCount() {
        return System.currentTimeMillis();
    }
    public static void DeviceInfo() {
		Log.e("BOARD", Build.BOARD);
		Log.e("BRAND", Build.BRAND);
//		Log.e("CPU_ABI", Build.CPU_ABI);
		Log.e("DEVICE", Build.DEVICE);
		Log.e("DISPLAY", Build.DISPLAY);
		Log.e("FINGERPRINT", Build.FINGERPRINT);
		Log.e("HOST", Build.HOST);
		Log.e("ID", Build.ID);
//		Log.e("MANUFACTURER", Build.MANUFACTURER);
		Log.e("MODEL", Build.MODEL);
		Log.e("PRODUCT", Build.PRODUCT);
		Log.e("TAGS", Build.TAGS);
		Log.e("TYPE", Build.TYPE);
		Log.e("USER", Build.USER);
	}
    public final static String getHeapState() {
        long lUsing = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long lTotal = Runtime.getRuntime().totalMemory();
        String strRet = "" + (lUsing/1024) + "/" + (lTotal/1024);
        return strRet;
    }
	public static void logHeap() {
//		int		availProc = Runtime.getRuntime().availableProcessors();
		///*
		long	max   = Runtime.getRuntime().maxMemory();   
		long	total = Runtime.getRuntime().totalMemory();
		long	free  = Runtime.getRuntime().freeMemory();
		long	use   = total - free;
		STD.logout("Heap - (" +
				" m : " + Long.toString(max)   + "," +
				" t : " + Long.toString(total) + "," +
				" f : " + Long.toString(free)  + "," +
				" u : " + Long.toString(use)   +
				")");
		//*/

//		ActivityManager activityManager = (ActivityManager)getSystemService(ACTIVITY_SERVICE);   
//		MemoryInfo mi = new MemoryInfo();
//		activityManager.getMemoryInfo(mi);
//		STD.logout("memory free : " + mi.availMem);

		System.gc();
	}
    public static void logout( String strLog ) {
        // System.out.println( strLog );
//		android.util.Log.d("slib", strLog);
    }
    public static void printStackTrace( Exception e ) {
    	if( e == null )
    		return;
    	StackTraceElement[] stack = e.getStackTrace();
    	if( stack == null )
    		return;
    	
    	int i;
    	logout("Exception =" + e.toString());
    	for( i = 0 ; i < stack.length ; i++ ) {
    		if( stack[i] != null )
    			STD.logout(stack[i].toString());
    	}
    	logout("Exception Stack End");
    }

    // ETC
    private static Random m_rnd;
    public static void initRand() {
    	m_rnd = new Random();
    	m_rnd.setSeed(System.currentTimeMillis());
    }
    public static int rand( int nMax ) {
    	int nTemp = m_rnd.nextInt();
    	nTemp = Math.abs(nTemp);
    	nTemp = nTemp % nMax;
    	return nTemp;
    }
    public static void ASSERT(boolean value) {
        try {
            if (value == false) {
                throw new Exception("ASSERT");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static int[] SETAEERECT(int x, int y, int width, int height) {
        int[] rect = new int[] {x, y, width, height};
        return rect;
    }
}
