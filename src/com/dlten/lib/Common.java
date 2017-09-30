package com.dlten.lib;

import android.content.Context;

import com.dlten.lib.STD;
import com.dlten.lib.graphics.CDCView;

public class Common {
	// constants.
    public static float FPS = 30.0f;
	
	// Screen & Resource relations.
	public static int CODE_WIDTH  = 640;	// width  in code
	public static int CODE_HEIGHT = 960;	// height in code
	
	public static int RES_WIDTH  = 480;		// width  of resource	640;
	public static int RES_HEIGHT = 720;		// height of resource	960; // 1140;
	
	public static int SC_WIDTH;				// px screen_width
	public static int SC_HEIGHT;			// px screen_height
	
	private static float m_fScaleRes;		// screen_size / res_size
	private static float m_fScaleCode;		// screen_size / code_size
	
	private static float m_fDensity;		// screen_width(pxl) / screen_width(dip)
	
	private static int m_nOffsetHeight;		// screen_pixel unit.
	
	public static void Initialize(Context context, int nScreenWidth, int nScreenHeight) {
		SC_WIDTH  = nScreenWidth;
		SC_HEIGHT = nScreenHeight;
		
		float	fScaleX = (float)nScreenWidth /RES_WIDTH;
		float	fScaleY = (float)nScreenHeight/RES_HEIGHT;
		m_fScaleRes = STD.MIN(fScaleX, fScaleY);
		
		fScaleX = (float)nScreenWidth /CODE_WIDTH;
		fScaleY = (float)nScreenHeight/CODE_HEIGHT;
		m_fScaleCode = STD.MIN(fScaleX, fScaleY);
		
		CDCView.setCodeWidth(CODE_WIDTH);
		CDCView.setCodeHeight(CODE_HEIGHT);
		
		CDCView.setResWidth(RES_WIDTH);
		CDCView.setResHeight(RES_HEIGHT);
		
		CDCView.setScaleRes(m_fScaleRes);
		CDCView.setScaleCode(m_fScaleCode);
		
		m_nOffsetHeight = (int)(RES_HEIGHT*m_fScaleRes)-nScreenHeight;
		m_fDensity = context.getResources().getDisplayMetrics().density;
		STD.logout("Density=" + m_fDensity);
	}
	public static void Finalize() {
	}
	
	public static float getScaleRes() {
		return m_fScaleRes;
	}
	public static int getDiffHeight() {
		return m_nOffsetHeight;
	}
	public static int getHeightDip() {
		return (int) pixel2dip(SC_HEIGHT);
	}

	
	public static float RES_DIP_HEIGHT() {
		return res2dip( RES_HEIGHT );
	}
	public static float RES_DIP_WIDTH() {
		return res2dip( RES_WIDTH );
	}
	public static float SC_DIP_HEIGHT() {
		return pixel2dip( SC_HEIGHT );
	}
	public static float SC_DIP_WIDTH() {
		return pixel2dip( SC_WIDTH );
	}

	public static float code2res( float codePx ) {
		float	screenPx = code2screen( codePx );
		float	resPx = screen2res( screenPx );
		return resPx;
	}
	public static float res2code( float resPx ) {
		float	screenPx = res2screen( resPx );
		float	codePx = screen2code( screenPx );
		return codePx;
	}
	
	public static float code2screen( float codePx ) {
		return (codePx*m_fScaleCode);
	}
	public static float screen2code( float screenPx ) {
		return (screenPx/m_fScaleCode);
	}
	
	public static float res2screen( float resPx ) {
		return (resPx*m_fScaleRes);
	}
	public static float screen2res( float screenPx ) {
		return (screenPx/m_fScaleRes);
	}
	public static float pixel2dip(float pixels) {
		float dips = pixels/m_fDensity;
	    return dips;
	}
	public static float dip2pixel(float dips)	{
	    float den = m_fDensity;
	    if(den != 1)
	        return (int)(dips*den+0.5);
	    return dips;
	}
	public static float res2dip( float resPx ) {
		float sPx = res2screen(resPx);
		return pixel2dip(sPx);
	}
	public static float dip2res( float dips ) {
		float sPx = dip2pixel(dips);
		return screen2res(sPx);
	}
}
