
#include "Engine.h"
#include "FugouKernel.h"

CFugouKernel*	m_pKernel = NULL;

#ifdef __cplusplus
extern "C" {
#endif

	JNIEXPORT jboolean	JNICALL Java_com_ssj_fugou_game_Engine_CreateEngine(JNIEnv* env, jobject thiz);
	JNIEXPORT void		JNICALL Java_com_ssj_fugou_game_Engine_DeleteEngine(JNIEnv* env, jobject thiz);
	JNIEXPORT void		JNICALL Java_com_ssj_fugou_game_Engine_StartGame(JNIEnv* env, jobject thiz);
	JNIEXPORT void		JNICALL Java_com_ssj_fugou_game_Engine_RestartGame(JNIEnv* env, jobject thiz);
	JNIEXPORT void		JNICALL Java_com_ssj_fugou_game_Engine_GetRule(JNIEnv* env, jobject thiz, jbooleanArray out_infoRule);
	JNIEXPORT void		JNICALL Java_com_ssj_fugou_game_Engine_SetRule(JNIEnv* env, jobject thiz, jbooleanArray in_infoRule);
	JNIEXPORT jint		JNICALL Java_com_ssj_fugou_game_Engine_GetHandCards(JNIEnv* env, jobject thiz, jint in_nPlayer, jintArray out_nHandCards);
	JNIEXPORT jint		JNICALL Java_com_ssj_fugou_game_Engine_GetTurn(JNIEnv* env, jobject thiz);
	JNIEXPORT jint		JNICALL Java_com_ssj_fugou_game_Engine_GetPlayerKind(JNIEnv* env, jobject thiz, jint in_nPlayer);
	JNIEXPORT jint		JNICALL Java_com_ssj_fugou_game_Engine_GetDiscardCards(JNIEnv* env, jobject thiz, jintArray out_pInfo);
	JNIEXPORT jint		JNICALL Java_com_ssj_fugou_game_Engine_GetDiscardCandi(JNIEnv* env, jobject thiz, jint in_nTurn, jintArray out_pCandi);
	JNIEXPORT jint		JNICALL Java_com_ssj_fugou_game_Engine_GetExchangeCandi(JNIEnv* env, jobject thiz, jint in_nPlayer, jintArray out_pCandi);
	JNIEXPORT jboolean	JNICALL Java_com_ssj_fugou_game_Engine_IsSibariState(JNIEnv* env, jobject thiz);
	JNIEXPORT jint		JNICALL Java_com_ssj_fugou_game_Engine_GetGameContext(JNIEnv* env, jobject thiz, jbyteArray out_pBuf, jint in_bufsize);
	JNIEXPORT jboolean	JNICALL Java_com_ssj_fugou_game_Engine_SetGameContext(JNIEnv* env, jobject thiz, jbyteArray in_pBuf, jint in_nSize);
	JNIEXPORT jboolean	JNICALL Java_com_ssj_fugou_game_Engine_IsValidDiscard(JNIEnv* env, jobject thiz, jint in_nPlayer, jintArray in_pCardIndex, jint in_nCount);
	JNIEXPORT void		JNICALL Java_com_ssj_fugou_game_Engine_FinishOneGame(JNIEnv* env, jobject thiz);
	JNIEXPORT void		JNICALL Java_com_ssj_fugou_game_Engine_SetPlayerLevel(JNIEnv* env, jobject thiz, jint in_nPlayer, jint in_nLevel);
	JNIEXPORT jint		JNICALL Java_com_ssj_fugou_game_Engine_Action(JNIEnv* env, jobject thiz, jintArray in_action);
	JNIEXPORT void		JNICALL Java_com_ssj_fugou_game_Engine_ThinkExchange(JNIEnv* env, jobject thiz, jint in_nPlayer, jintArray out_action);
	JNIEXPORT void		JNICALL Java_com_ssj_fugou_game_Engine_ThinkDiscard(JNIEnv* env, jobject thiz, jint in_nPlayer, jintArray out_action);
	JNIEXPORT jint		JNICALL Java_com_ssj_fugou_game_Engine_GetPlayerCount(JNIEnv* env, jobject thiz);
	JNIEXPORT jboolean	JNICALL Java_com_ssj_fugou_game_Engine_IsRevolution(JNIEnv* env, jobject thiz);
	JNIEXPORT jboolean	JNICALL Java_com_ssj_fugou_game_Engine_SetGameContextByTumikomi(JNIEnv* env, jobject thiz, jbyteArray in_pBuf, jint in_nSize);
	JNIEXPORT void		JNICALL Java_com_ssj_fugou_game_Engine_getSize(JNIEnv* env, jobject thiz, jintArray out_pBuf);
	//For test
	JNIEXPORT jstring	JNICALL Java_com_ssj_fugou_game_Engine_getLog(JNIEnv* env, jobject thiz, jint nLineNo);
	//For test
	JNIEXPORT jint	JNICALL Java_com_ssj_fugou_game_Engine_getLogCount(JNIEnv* env, jobject thiz);
#ifdef __cplusplus
}
#endif

// bool Engine_CreateEngine()
JNIEXPORT jboolean JNICALL Java_com_ssj_fugou_game_Engine_CreateEngine(JNIEnv* env, jobject thiz)
{
	m_pKernel = CFugouKernel::Create();//AfxGetApp()->m_pKernel;//CFugouKernel::Create();
	return (m_pKernel == NULL) ? false : true;
}

// void Engine_DeleteEngine()
JNIEXPORT void	JNICALL Java_com_ssj_fugou_game_Engine_DeleteEngine(JNIEnv* env, jobject thiz)
{
	if (m_pKernel == NULL)
		return;

	CFugouKernel::Free(m_pKernel);
	m_pKernel = NULL;
}

// void Engine_StartGame()
JNIEXPORT void	JNICALL Java_com_ssj_fugou_game_Engine_StartGame(JNIEnv* env, jobject thiz)
{
	m_pKernel->StartNewGame();
}

// void Engine_RestartGame()
JNIEXPORT void	JNICALL Java_com_ssj_fugou_game_Engine_RestartGame(JNIEnv* env, jobject thiz)
{
	m_pKernel->StartContinueGame();
}

// void Engine_GetRule(bool infoRule[])
JNIEXPORT void	JNICALL Java_com_ssj_fugou_game_Engine_GetRule(JNIEnv* env, jobject thiz, jbooleanArray out_infoRule)
{
	jboolean infoRule[RULE_COUNT];

	//set game rule
	CFugouRule& rule = m_pKernel->GetRule();
	for (int i = 0; i < RULE_COUNT ; i ++) {
		infoRule[i] = rule.Get(i);
	}

	env->SetBooleanArrayRegion(out_infoRule, 0, RULE_COUNT, infoRule);
}

// void Engine_SetRule(bool infoRule[])
JNIEXPORT void	JNICALL Java_com_ssj_fugou_game_Engine_SetRule(JNIEnv* env, jobject thiz, jbooleanArray in_infoRule)
{
	jboolean infoRule[RULE_COUNT];

	env->GetBooleanArrayRegion(in_infoRule, 0, RULE_COUNT, infoRule);

	//set game rule
	CFugouRule& rule = m_pKernel->GetRule();
	for (int i = 0; i < RULE_COUNT ; i ++) {
		rule.Set(i, infoRule[i]);
	}
}

// int Engine_GetHandCards(int nPlayer, int nHandCards[])
JNIEXPORT jint	JNICALL Java_com_ssj_fugou_game_Engine_GetHandCards(JNIEnv* env, jobject thiz, jint in_nPlayer, jintArray out_nHandCards)
{
	int nPlayer = in_nPlayer;
	int nHandCards[MAX_HANDCARDS];
	int* pnHandCards = NULL;

	if (out_nHandCards != NULL) {
		pnHandCards = nHandCards;
	}

	int nCount = m_pKernel->GetHandCards(nPlayer, pnHandCards);

	if (out_nHandCards != NULL) {
		env->SetIntArrayRegion(out_nHandCards, 0, MAX_HANDCARDS, nHandCards);
	}

	return (jint)nCount;
}

// int Engine_GetTurn()
JNIEXPORT jint	JNICALL Java_com_ssj_fugou_game_Engine_GetTurn(JNIEnv* env, jobject thiz)
{
	return (jint)m_pKernel->GetTurn();
}

// int Engine_GetPlayerKind(int nPlayer)
JNIEXPORT jint	JNICALL Java_com_ssj_fugou_game_Engine_GetPlayerKind(JNIEnv* env, jobject thiz, jint in_nPlayer)
{
	return (jint)m_pKernel->GetPlayerKind(in_nPlayer);
}

// int Engine_GetDiscardCards(int pInfo[])
JNIEXPORT jint	JNICALL Java_com_ssj_fugou_game_Engine_GetDiscardCards(JNIEnv* env, jobject thiz, jintArray out_pInfo)
{
	DISCARD_CARDS_INFO	cardInfo[20];

	DISCARD_CARDS_INFO* pCardInfo = NULL;
	if (out_pInfo != NULL) {
		pCardInfo = cardInfo;
	}

	int	nCount = m_pKernel->GetDiscardCards(pCardInfo);

	int	pInfo[20*7];
	if (out_pInfo != NULL) {
		int index = 0;
		for (int i = 0; i < nCount; i ++) {
			index = i * 7;
			pInfo[index+0] = cardInfo[i].nFrom;
			pInfo[index+1] = (int)cardInfo[i].nKind;
			pInfo[index+2] = cardInfo[i].nCount;
			pInfo[index+3] = cardInfo[i].nCards[0];
			pInfo[index+4] = cardInfo[i].nCards[1];
			pInfo[index+5] = cardInfo[i].nCards[2];
			pInfo[index+6] = cardInfo[i].nCards[3];
		}
		env->SetIntArrayRegion(out_pInfo, 0, 20*7, pInfo);
	}
	return (jint)nCount;
}

// int Engine_GetDiscardCandi(int nTurn, int* pCandi)
JNIEXPORT jint	JNICALL Java_com_ssj_fugou_game_Engine_GetDiscardCandi(JNIEnv* env, jobject thiz, jint in_nTurn, jintArray out_pCandi)
{
	DISCARD_CANDI	candi[20];

	DISCARD_CANDI*	pCandiInfo = NULL;
	if (out_pCandi != NULL) {
		pCandiInfo = candi;
	}

	int nCount = m_pKernel->GetDiscardCandi(in_nTurn, pCandiInfo);

	int	pCandi[20*7];
	if (out_pCandi != NULL) {
		int index = 0;
		for (int i = 0; i < nCount; i ++) {
			index = i * 7;
			pCandi[index+0] = candi[i].nPlayer;
			pCandi[index+1] = candi[i].nCount;
			pCandi[index+2] = (int)candi[i].nKind;
			pCandi[index+3] = candi[i].nIndices[0];
			pCandi[index+4] = candi[i].nIndices[1];
			pCandi[index+5] = candi[i].nIndices[2];
			pCandi[index+6] = candi[i].nIndices[3];
		}
		env->SetIntArrayRegion(out_pCandi, 0, 20*7, pCandi);
	}
	return (jint)nCount;
}

// int Engine_GetExchangeCandi(int nPlayer, int* pCandi)
JNIEXPORT jint	JNICALL Java_com_ssj_fugou_game_Engine_GetExchangeCandi(JNIEnv* env, jobject thiz, jint in_nPlayer, jintArray out_pCandi)
{
	DISCARD_CANDI candi[20];

	DISCARD_CANDI*	pCandiInfo = NULL;
	if (out_pCandi != NULL) {
		pCandiInfo = candi;
	}

	int nCount = m_pKernel->GetExchangeCandi(in_nPlayer, pCandiInfo);

	int	pCandi[20*7];
	if (out_pCandi != NULL) {
		int index = 0;
		for (int i = 0; i < nCount; i ++) {
			index = i * 7;
			pCandi[index+0] = candi[i].nPlayer;
			pCandi[index+1] = candi[i].nCount;
			pCandi[index+2] = (int)candi[i].nKind;
			pCandi[index+3] = candi[i].nIndices[0];
			pCandi[index+4] = candi[i].nIndices[1];
			pCandi[index+5] = candi[i].nIndices[2];
			pCandi[index+6] = candi[i].nIndices[3];
		}
		env->SetIntArrayRegion(out_pCandi, 0, 20*7, pCandi);
	}
	return nCount;
}

// bool Engine_IsSibariState()
JNIEXPORT jboolean	JNICALL Java_com_ssj_fugou_game_Engine_IsSibariState(JNIEnv* env, jobject thiz)
{
	return m_pKernel->IsSibariState();
}

// int Engine_GetGameContext(unsigned char pBuf[])
JNIEXPORT jint	JNICALL Java_com_ssj_fugou_game_Engine_GetGameContext(JNIEnv* env, jobject thiz, jbyteArray out_pBuf, jint in_bufsize)
{
	unsigned char* buf = new unsigned char[in_bufsize];
	unsigned char* pBuf = NULL;

	if (out_pBuf != NULL)
		pBuf = buf;

	int	nSize = m_pKernel->GetGameContext(pBuf);

	if (out_pBuf != NULL)
		env->SetByteArrayRegion(out_pBuf, 0, in_bufsize, (jbyte*)buf);

	delete[] buf;
	return (jint)nSize;
}

// bool Engine_SetGameContext(unsigned char pBuf[], int nSize)
JNIEXPORT jboolean	JNICALL Java_com_ssj_fugou_game_Engine_SetGameContext(JNIEnv* env, jobject thiz, jbyteArray in_pBuf, jint in_nSize)
{
	unsigned char buf[MAX_SAVE_KIFU_SIZE];
	env->GetByteArrayRegion(in_pBuf, 0, MAX_SAVE_KIFU_SIZE, (jbyte*)buf);

	return m_pKernel->SetGameContext(buf, in_nSize);
}

// bool Engine_IsValidDiscard(int nPlayer, int pCardIndex[], int nCount)
JNIEXPORT jboolean	JNICALL Java_com_ssj_fugou_game_Engine_IsValidDiscard(JNIEnv* env, jobject thiz, jint in_nPlayer, jintArray in_pCardIndex, jint in_nCount)
{
	jint cardIndex[MAX_SELCARDS];
	env->GetIntArrayRegion(in_pCardIndex, 0, MAX_SELCARDS, cardIndex);

	return m_pKernel->IsValidDiscard(in_nPlayer, cardIndex, in_nCount);
}

// void Engine_FinishOneGame()
JNIEXPORT void	JNICALL Java_com_ssj_fugou_game_Engine_FinishOneGame(JNIEnv* env, jobject thiz)
{
	m_pKernel->FinishOneGame();
}

// void Engine_SetPlayerLevel(int nPlayer, int nLevel)
JNIEXPORT void	JNICALL Java_com_ssj_fugou_game_Engine_SetPlayerLevel(JNIEnv* env, jobject thiz, jint in_nPlayer, jint in_nLevel)
{
	m_pKernel->SetPlayerLevel(in_nPlayer, (LEVEL)in_nLevel);
}

// int Engine_Action(int action[7])
JNIEXPORT jint	JNICALL Java_com_ssj_fugou_game_Engine_Action(JNIEnv* env, jobject thiz, jintArray in_action)
{
	int	action[7];
	env->GetIntArrayRegion(in_action, 0, 7, action);

	ACTION act;
	act.nKind       = (ACTION::ACTION_KIND)action[0];
	act.nPlayer     = action[1];
	act.nCount      = action[2];
	act.nIndices[0] = action[3];
	act.nIndices[1] = action[4];
	act.nIndices[2] = action[5];
	act.nIndices[3] = action[6];
	return (jint)m_pKernel->Action( act );
}

// void Engine_ThinkExchange(int nPlayer, int action[7])
JNIEXPORT void	JNICALL Java_com_ssj_fugou_game_Engine_ThinkExchange(JNIEnv* env, jobject thiz, jint in_nPlayer, jintArray out_action)
{
	ACTION act = m_pKernel->ThinkExchange(in_nPlayer);

	int	action[7];
	action[0] = (int)act.nKind;
	action[1] = act.nPlayer;
	action[2] = act.nCount;
	action[3] = act.nIndices[0];
	action[4] = act.nIndices[1];
	action[5] = act.nIndices[2];
	action[6] = act.nIndices[3];
	env->SetIntArrayRegion(out_action, 0, 7, action);
}

// void Engine_ThinkDiscard(int nPlayer, int action[7])
JNIEXPORT void	JNICALL Java_com_ssj_fugou_game_Engine_ThinkDiscard(JNIEnv* env, jobject thiz, jint in_nPlayer, jintArray out_action)
{
	ACTION act = m_pKernel->ThinkDiscard(in_nPlayer);

	int	action[7];
	action[0] = act.nKind;
	action[1] = act.nPlayer;
	action[2] = act.nCount;
	action[3] = act.nIndices[0];
	action[4] = act.nIndices[1];
	action[5] = act.nIndices[2];
	action[6] = act.nIndices[3];
	env->SetIntArrayRegion(out_action, 0, 7, action);
}

// int Engine_GetPlayerCount()
JNIEXPORT jint	JNICALL Java_com_ssj_fugou_game_Engine_GetPlayerCount(JNIEnv* env, jobject thiz)
{
	return (jint)m_pKernel->GetPlayerCount();
}

// bool Engine_IsRevolution()
JNIEXPORT jboolean	JNICALL Java_com_ssj_fugou_game_Engine_IsRevolution(JNIEnv* env, jobject thiz)
{
	return (jboolean)m_pKernel->IsRevolution();
}

//For test
// bool Engine_SetGameContextByTumikomi(char pBuf[], int nSize)
JNIEXPORT jboolean	JNICALL Java_com_ssj_fugou_game_Engine_SetGameContextByTumikomi(JNIEnv* env, jobject thiz, jbyteArray in_pBuf, jint in_nSize)
{
	char* buf = new char[in_nSize];
	env->GetByteArrayRegion(in_pBuf, 0, in_nSize, (jbyte*)buf);

	jboolean	bRet = (jboolean)m_pKernel->SetGameContextByTumikomi(buf, in_nSize);

	delete[] buf;
	return bRet;
}

//For test
JNIEXPORT void	JNICALL Java_com_ssj_fugou_game_Engine_getSize(JNIEnv* env, jobject thiz, jintArray out_pBuf)
{
	int	size[18] = {
		sizeof(char),			// 1
		sizeof(short),			// 2
		sizeof(int),			// 4

		sizeof(unsigned char),	// 1
		sizeof(unsigned short),	// 2
		sizeof(unsigned int),	// 4

		sizeof(bool),			// 1
		sizeof(long),			// 4
		sizeof(float),			// 4
		sizeof(double),			// 8

		sizeof(jboolean),		// 1
		sizeof(jbyte),			// 1
		sizeof(jchar),			// 2
		sizeof(jshort),			// 2

		sizeof(jint),			// 4
		sizeof(jlong),			// 8
		sizeof(jfloat),			// 4
		sizeof(jdouble),		// 8
	};
	env->SetIntArrayRegion(out_pBuf, 0, 18, size);
}

#ifdef _LOG
	char	szLogBuf[MAX_LOG_LINES][MAX_LOG_LENGTH] = {0, };
	unsigned int	_TsumiLog = 0;
#endif

//For test
JNIEXPORT jstring	JNICALL Java_com_ssj_fugou_game_Engine_getLog(JNIEnv* env, jobject thiz, jint nLineNo)
{
#ifdef _LOG
	if (nLineNo < 0 || nLineNo >= MAX_LOG_LINES)
		return NULL;
	jstring strRet = env->NewStringUTF(szLogBuf[nLineNo]);
	return strRet;
#else
	return NULL;
#endif
}

//For test
JNIEXPORT jint	JNICALL Java_com_ssj_fugou_game_Engine_getLogCount(JNIEnv* env, jobject thiz)
{
#ifdef _LOG
	return _TsumiLog;
#else
	return -1;
#endif
}
void TsumiLog(const char* lpszFormat, ...)
{//SUB00497600
#ifdef _LOG
	_TsumiLog ++;

	//if (_TsumiLog >= 30000000)
	//if (m_nAttackOrDefences < 2000000)
	//	return;

	if (_TsumiLog == 0x00001544)
	{
		int aa = 0;
	}

	va_list		args;
	va_start(args, lpszFormat);

	char	szTemp[MAX_LOG_LENGTH] = {0, };
	char	szMsg[MAX_LOG_LENGTH] = {0, };
	int		nLineNo = _TsumiLog % MAX_LOG_LINES;

	vsprintf(szTemp, lpszFormat, args);
	snprintf(szMsg, MAX_LOG_LENGTH-1, "%6d:%s", _TsumiLog, szTemp);
	char*	szBuf = szLogBuf[nLineNo];
	snprintf(szBuf, MAX_LOG_LENGTH-1, "%s", szMsg);

	#ifdef _DEBUG
//		//Read Log
//		char	szLine[1000] = {0, };
//		try
//		{
//			fgets(szLine, sizeof(szLine), fpTsumiLog);
//			if (strncmp(szLine, szMsg, strlen(szMsg)) != 0)
//				throw 0;
//		}
	#else
//		//Write Log
//		/*
//		 fprintf(fpTsumiLog, "%6d:", _TsumiLog);
//		 vfprintf(fpTsumiLog, lpszFormat, args);
//		 fprintf(fpTsumiLog, "\n");
//		 */
	#endif //_DEBUG

	va_end(args);

#else

	(void)lpszFormat;

#endif //_LOG
}

