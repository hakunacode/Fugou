package com.dlten.lib.Sound;

import android.content.Context;

/**
 * <p>Title: Android Fugou</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2011</p>
 *
 * <p>Company: dl</p>
 *
 * @author hrh
 * @version 1.0
 */

public class SoundManager {
	
	protected BgmManager mBgmManager = null;
	protected SeManager mSeManager = null;
	
    private boolean m_bEnable = false;

	protected SoundManager() {
		mBgmManager = BgmManager.getInstance();
		mSeManager  = SeManager.getInstance();
	}

	public void init(Context context) {
		mBgmManager.init(context);
		mSeManager.init(context);
		loadSounds();
		
		setEnable(true);
	}

	public void destroy() {
		mBgmManager.destroy();
		mSeManager.destroy();
		
		mBgmManager = null;
		mSeManager = null;
	}
	
	protected void loadSounds() {}
	
	public void playBGM(int index) {
		if (!m_bEnable)
			return;
		
		mBgmManager.playBGM(index);
	}
	
	public void playBGM(int index, boolean bLoop) {
		if (!m_bEnable)
			return;
		
		mBgmManager.playBGM(index, bLoop);
	}
	
	public void stopBGM() {
		if (!m_bEnable)
			return;
		
		mBgmManager.stopBGM();
	}
	
	public void pauseBGM() {
		mBgmManager.pauseBGM();
	}
	
	public void resumeBGM() {
		mBgmManager.resumeBGM();
	}
  
	public void playSE(int index) {
		if (!m_bEnable)
			return;
		
		mSeManager.play(index);
	}
	
	public void stopSE(int index) {
		mSeManager.stop(index);
	}

	public void stopSE() {
		mSeManager.stopAll();
	}
	
    public void setEnable( boolean enable ) {
    	m_bEnable = enable;
    	
		mBgmManager.setEnable(m_bEnable);
		mSeManager.setEnable(m_bEnable);
    }
}

