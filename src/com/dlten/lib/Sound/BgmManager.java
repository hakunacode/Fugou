package com.dlten.lib.Sound;

import com.dlten.lib.STD;
import android.content.Context;
import android.media.MediaPlayer;

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


public class BgmManager {
	
	private static Context mContext = null;
	
    private static final int BGM_PLAYER = 0;
    private static final int SE_PLAYER = 1;
    private static BgmManager _instance = null;
    private MediaPlayer m_players[] = new MediaPlayer[2];

    private int m_nBgmNum = STOP_SOUND;
    private int m_nEffNum = STOP_SOUND;
//    private boolean m_bBgmLoop = true;

    public BgmManager() {
        _instance = this;
    }
    public static BgmManager getInstance() {
        if (_instance == null)
        	_instance = new BgmManager();
       	return _instance;
    }
    public void init( Context context ) {
    	mContext = context;
    }
    
    public void destroy() {
        playBGM(STOP_SOUND);
    }
    
    private MediaPlayer createPlayer(int nSndID) {
    	MediaPlayer player = null;
        try {
        	player = MediaPlayer.create(mContext, nSndID);
//        	player.prepare();
        } catch (Exception e) {
            STD.printStackTrace(e);
            player = null;
        }
        return player;
    }

    public void blockWhileSe() {
        if( m_nEffNum < 0 || m_nEffNum >= m_players.length )
            return;
        
        while( m_players[SE_PLAYER].isPlaying()) {
        	STD.sleep(30);
        }
    }


    
    
    
    
    
    
    
    public void stopBGM() {
        if( m_nBgmNum < 0 )
            return;

        try{
            m_players[BGM_PLAYER].stop();
        } catch( Exception e ) {
            STD.printStackTrace(e);
        }
        
        m_nBgmNum = STOP_SOUND;
//        m_bBgmLoop = true;
    }
    public void pauseBGM() {
        if( m_nBgmNum < 0 )
            return;

        try{
            m_players[BGM_PLAYER].pause();
        } catch( Exception e ) {
            STD.printStackTrace(e);
        }
    }
    public void resumeBGM() {
        if( m_nBgmNum < 0 )
            return;
        
//        playBGM_force(m_nBgmNum, m_bBgmLoop);
        try{
        	m_players[BGM_PLAYER].start();
        } catch( Exception e ) {
            STD.printStackTrace(e);
        }
    }
    
    
    
    
    
//    
//    public void stopBGM() {
////      if( m_nBgmNum < 0 || m_nBgmNum >= m_players.length )
////          return;
//      if( m_nBgmNum < 0 )
//          return;
//
//      try{
//          if ( m_players[BGM_PLAYER].isPlaying() ) {
//              m_players[BGM_PLAYER].stop();
//          }
//      } catch( Exception e ) {
//      	STD.printStackTrace( e );
//      }
//      
//      m_nBgmNum = STOP_SOUND;
//      m_bBgmLoop = true;
//  }
//  public void pauseBGM() {
////      if( m_nBgmNum < 0 || m_nBgmNum >= m_players.length )
////          return;
//      if( m_nBgmNum < 0 )
//          return;
//
//      try{
//          if ( m_players[BGM_PLAYER].isPlaying() ) {
//              m_players[BGM_PLAYER].pause();
//          }
//      } catch( Exception e ) {
//      	STD.printStackTrace( e );
//      }
//  }
//  public void resumeBGM() {
//      if( m_nBgmNum < 0 )
//          return;
//      
//      try{
////        playBGM_force(m_nBgmNum, m_bBgmLoop);
//      	m_players[BGM_PLAYER].start();
//      } catch( Exception e ) {
//      	STD.printStackTrace( e );
//      }
//  }
  
  
    
    
    
    
  
    public void playBGM(int nBgmIndex) {
        playBGM(nBgmIndex, true);
    }

    public void playBGM(int nBgmIndex, boolean bLoop) {
    	if (!m_bEnable)
    		return;
    	
        if( nBgmIndex < 0 )
            return;

        if( m_nBgmNum == nBgmIndex )
        	return;
        
        try {
            stopBGM();
            stopSE();
            if( m_players[BGM_PLAYER] != null ) {
                m_players[BGM_PLAYER].release();
                m_players[BGM_PLAYER] = null;
            }
            m_players[BGM_PLAYER] = createPlayer(nBgmIndex);

        } catch (Exception e) {
            STD.printStackTrace(e);
        }
        
        playBGM_force( nBgmIndex, bLoop );
    }
    private void playBGM_force( int nBgmIndex, boolean bLoop ) {
    	if (!m_bEnable)
    		return;
    	
        if(nBgmIndex < 0)
            return;

        try {
              m_players[BGM_PLAYER].setLooping(bLoop);
              m_players[BGM_PLAYER].start();

              m_nBgmNum = nBgmIndex;
//              m_bBgmLoop = bLoop;
        } catch (Exception e) {
            STD.printStackTrace(e);
        }
    }

    public void playSE(int nSeIndex) {
    	if (!m_bEnable)
    		return;
    	
        if (nSeIndex < 0)
            return;

        stopSE();
        try {
            resetPlayer(nSeIndex);
            // m_players[SE_PLAYER].setLooping(false);
            m_players[SE_PLAYER].start();

            m_nEffNum = nSeIndex;
        } catch (Exception e) {
            STD.printStackTrace(e);
        }
    }
    public void stopSE() {
    	if (m_nEffNum < 0)
    		return;
    	
        try {
//        	if (m_players[SE_PLAYER].isPlaying())
//        		m_players[SE_PLAYER].stop();
        	while (m_players[SE_PLAYER].isPlaying())	{}
        } catch (Exception e) {
            STD.printStackTrace(e);
        }
        
        m_nEffNum = STOP_SOUND;
    }

    void setVolume(MediaPlayer player, int nVol) {
    	player.setVolume(nVol, nVol);
    }
    
    private void resetPlayer( int nIndex ) {
        if( m_players[SE_PLAYER] != null ) {
            m_players[SE_PLAYER].release();
            m_players[SE_PLAYER] = null;
        }
        m_players[SE_PLAYER] = createPlayer(nIndex);
    }

    public static final int STOP_SOUND = -1;
    public static final int PAUSE_SOUND = -2;
    
    private boolean m_bEnable;
    public void setEnable( boolean nIndex ) {
    	m_bEnable = nIndex;
		if (!m_bEnable) {
			stopBGM();
			stopSE();
		}
    }
}
