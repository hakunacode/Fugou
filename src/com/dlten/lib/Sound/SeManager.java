package com.dlten.lib.Sound;

import java.util.HashMap;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

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

public class SeManager {
	
	private static SeManager _instance;
	
	private SoundPool mSoundPool;
	private HashMap<Integer, Integer> mSoundMap;
	private AudioManager mAudioManager;
	private Context mContext;
    private boolean m_bEnable = false;
	private float speed = 1.0f;

	private SeManager() {
		_instance = this;
	}

	public static synchronized SeManager getInstance() {
		if (_instance == null)
			_instance = new SeManager();
		return _instance;
	}

	public void init(Context theContext) {
		mContext = theContext;
		mSoundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
		mSoundMap = new HashMap<Integer, Integer>();
		mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
		m_bEnable = true;
		// m_bEnable = false;
	}
	
	public void destroy() {
		mSoundPool.release();
		mSoundPool = null;
		mSoundMap.clear();
		mAudioManager.unloadSoundEffects();
		_instance = null;
	}

	public void addSound(int index, int SoundID) {
		int idSound = mSoundPool.load(mContext, SoundID, 1);
		mSoundMap.put(index, idSound);
	}
	
	public void delSound(int index) {
		int idSound = mSoundMap.get(index);
		mSoundPool.unload(idSound);
		mSoundMap.remove(index);
	}
	
	public void play(int index) {
		if (!m_bEnable)
			return;
		
		float streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		streamVolume = streamVolume / mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		
		int idSound = mSoundMap.get(index);
		mSoundPool.play(idSound, streamVolume, streamVolume, 1, 0, speed);
	}
	
	public void playLooped(int index) {
		if (!m_bEnable)
			return;
		
	    float streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
	    streamVolume = streamVolume / mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
	    
	    int idSound = mSoundMap.get(index);
	    mSoundPool.play(idSound, streamVolume, streamVolume, 1, -1, speed);
	}	

	public void stop(int index) {
		int seid = mSoundMap.get(index);
		mSoundPool.stop( seid );
	}
	
	public void stopAll() {
		for (int soundIndex : mSoundMap.values()) {
			stop(soundIndex);
		}
	}

    public void setEnable( boolean enable ) {
    	m_bEnable = enable;
		if (!m_bEnable) {
			stopAll();
		}
    }
}

