package com.ssj.fugou.sound;

import com.dlten.lib.Sound.SoundManager;
import com.ssj.fugou.R;


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

public class MySoundManager extends SoundManager {
	
	public final static int
		se_attack			= 1,
		se_attack2			= 2,
		se_cancel			= 3,
		se_card				= 4,
		se_cursor			= 5,
		se_error			= 6,
		se_meter			= 7,
		se_revolution		= 8,
		se_set				= 9,
		se_special			= 10,
		
		jingle_area_get		= 11,
		jingle_game_over	= 12,
		jingle_game_start	= 13,
		jingle_lose			= 14,
		jingle_penalty		= 15,
		jingle_win			= 16,
		
		bgm_chara_select    = 17,
		bgm_game            = 18,
		bgm_opening         = 19,
		bgm_select_attack   = 20,
		bgm_select_menu     = 21;

	protected void loadSounds() {
//		mSeManager.addSound( bgm_chara_select,  R.raw.bgm_chara_select);
//		mSeManager.addSound( bgm_game,          R.raw.bgm_game);
//		mSeManager.addSound( bgm_opening,       R.raw.bgm_opening);
//		mSeManager.addSound( bgm_select_attack, R.raw.bgm_select_attack);
//		mSeManager.addSound( bgm_select_menu,   R.raw.bgm_select_menu);

//		mSeManager.addSound(jingle_area_get,	R.raw.jingle_area_get);
//		mSeManager.addSound(jingle_game_over,	R.raw.jingle_game_over);
//		mSeManager.addSound(jingle_game_start,	R.raw.jingle_game_start);
//		mSeManager.addSound(jingle_lose,		R.raw.jingle_lose);
//		mSeManager.addSound(jingle_penalty,		R.raw.jingle_penalty);
//		mSeManager.addSound(jingle_win,			R.raw.jingle_win);
		
		mSeManager.addSound(se_attack,			R.raw.se_attack);
		mSeManager.addSound(se_attack2, 		R.raw.se_attack2);
		mSeManager.addSound(se_cancel,			R.raw.se_cancel);
		mSeManager.addSound(se_card,			R.raw.se_card);
		mSeManager.addSound(se_cursor,			R.raw.se_cursor);
		mSeManager.addSound(se_error,			R.raw.se_error);
		mSeManager.addSound(se_meter,			R.raw.se_meter);
		mSeManager.addSound(se_revolution,		R.raw.se_revolution);
		mSeManager.addSound(se_set,				R.raw.se_set);
		mSeManager.addSound(se_special,			R.raw.se_special);
	}
	
	public void playBGM(int index) {
		int	idResource = -1;
		switch (index) {
		case bgm_chara_select:		idResource = R.raw.bgm_chara_select;	break;
		case bgm_game:				idResource = R.raw.bgm_game;			break;
		case bgm_opening:			idResource = R.raw.bgm_opening;			break;
		case bgm_select_attack:		idResource = R.raw.bgm_select_attack;	break;
		case bgm_select_menu:		idResource = R.raw.bgm_select_menu;		break;
		default:					return;
		}
		
//		if (idxBGM == index)
//			return;
//		
//		if (idxBGM != -1) {
//			mBgmManager.delSound( idxBGM );
//		}
//		mSeManager.addSound( index,   R.raw.idResource);
//		super.playBGM(index);
		
		
		//DEBUG
//		idResource = R.raw.bgm_chara_select;	// NG
//		idResource = R.raw.bgm_game;			// NG
//		idResource = R.raw.bgm_opening;			// NG
//		idResource = R.raw.bgm_select_attack;	// NG
//		idResource = R.raw.bgm_select_menu;		// NG
		
//		idResource = R.raw.jingle_area_get;		// OK
//		idResource = R.raw.jingle_game_over;	// OK
//		idResource = R.raw.jingle_game_start;	// OK
//		idResource = R.raw.jingle_lose;			// OK
//		idResource = R.raw.jingle_penalty;		// OK
//		idResource = R.raw.jingle_win;			// OK
		
//		idResource = R.raw.bgm01_mix;			// OK
//		idResource = R.raw.bgm02_mix;			// OK
		
		super.playBGM(idResource);
	}
	
	public void playBGM(int index, boolean bLoop) {
		int	idResource = -1;
		
		switch (index) {
		case jingle_area_get:		idResource = R.raw.jingle_area_get;		break;
		case jingle_game_over:		idResource = R.raw.jingle_game_over;	break;
		case jingle_game_start:		idResource = R.raw.jingle_game_start;	break;
		case jingle_lose:			idResource = R.raw.jingle_lose;			break;
		case jingle_penalty:		idResource = R.raw.jingle_penalty;		break;
		case jingle_win:			idResource = R.raw.jingle_win;			break;
		default:					return;
		}
		
		super.playBGM(idResource, bLoop);
	}

//	// for DEBUG
//	protected void loadSounds() {
//	}
//	public void playSE(int index) {
//	}
//	public void playBGM(int index) {
//	}
}

