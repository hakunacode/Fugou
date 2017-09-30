
#ifndef _ENGINE_H_
#define _ENGINE_H_

#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <jni.h>

#define MAX_SAVE_KIFU_SIZE		1024*8

#define MAX_CPU			4
#define MAX_HANDCARDS	11
#define CARD_SIZE		14
#define MAX_SELCARDS	4
#define JOKER_INDEX		52
#define NO_CARD			-1
#define ACTION_NONE		-1

// #include "Globals.h"
enum RULE_TYPE
{
	RULE_KAKUMEI = 0,
	RULE_MIYAKO,
	RULE_SPADE3,
	RULE_SIBARI,
	RULE_8KIRI,
	RULE_JOKER,
	RULE_2AGARI,
	RULE_COUNT
};


void TsumiLog(const char* lpszFormat, ...);

// #define _LOG
#define LOG	TsumiLog

#ifdef _LOG
	#define		MAX_LOG_LINES	100
	#define		MAX_LOG_LENGTH	0x100
#endif

#endif
