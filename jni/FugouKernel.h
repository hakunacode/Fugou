#ifndef __FUGOUKERNEL__
#define __FUGOUKERNEL__

#include "./FugouRule.h"

#define	ENABLE_RAND
#define MAX_PLAYERS	5

enum{
	CARD_3 = 1,
	CARD_4,
	CARD_5,
	CARD_6,
	CARD_7,
	CARD_8,
	CARD_9,
	CARD_10,
	CARD_J,
	CARD_Q,
	CARD_K,
	CARD_A,
	CARD_2,
	CARD_JOKER = 15,
};

//player kind
enum {
	DAIFUGOU = 0,	//君主(大富豪)
	FUGOU,			//軍師(富豪)
	HEIMIN,			//武将(平民)
	HINMIN,			//隊長(貧民)
	DAIHINMIN,		//兵卒(大貧民)
	MAX_KIND
};

//discard cards kind
enum DISCARD_CARDS_KIND {
	SINGLE = 0,
	DUAL,
	TRIPLE,
	QUAD,
	SEQ_3,
	SEQ_4,
	GIVE_1,				//In this case, DISCARD_CARD_INFO::nTo means receiver.
	GIVE_2,				//In this case, DISCARD_CARD_INFO::nTo means receiver.
	PASS,
};

//cards sign
enum {
	CLUB = 1 << 0,
	DIAMOND = 1 << 1,
	HEART = 1 << 2,
	SPADE = 1 << 3,
};

#define MAX_CARD_NUM 15

#define GetCardSign(a) (((a)>>4)&0x0F)
#define GetCardNumber(a) ((a)&0x0F)
#define MakeCard(a, b) (((a)<<4)|((b)&0x0F))

//discard cards info
struct DISCARD_CARDS_INFO
{
	int nFrom;
	DISCARD_CARDS_KIND	nKind;
	int nCount;
	int nCards[4];
};

//discard candidate
struct DISCARD_CANDI
{
	int nPlayer;
	int nCount;
	DISCARD_CARDS_KIND	nKind;
	int nIndices[4];
};

enum LEVEL {
	LEVEL_1,
	LEVEL_2,
	LEVEL_3,
	LEVEL_4,
	LEVEL_5
};

typedef unsigned int ACTION_RESULT;
enum {
	ACTION_FAIL = 0,
	ACTION_OK = 1 << 0,
	ACTION_FOUL = 1 << 1,			//反則
	ACTION_REVOLUTION_SET = 1 << 2,
	ACTION_REVOLUTION_FREE = 1 << 3,
	ACTION_GAME_END = 1 << 4,
	ACTION_AGARI = 1 << 5,			//あがり
	ACTION_DAIFUGOU_FALL = 1 << 6,	//都落ち
	ACTION_8KIRI = 1 << 7,			//８切り
	ACTION_SIBARI = 1 << 8,			//しばり
	ACTION_SPADE3 = 1 << 9,			//スペ３
};

struct ACTION
{
	enum ACTION_KIND{
		EXCHANGE,
		DISCARD,
		PASS,
		TURN_NEXT
	};

	ACTION_KIND nKind;
	int nPlayer;

	int nCount;
	int nIndices[4];
};

class CFugouContext;

class CFugouKernel
{
public:
	static CFugouKernel* Create();
	static void Free(CFugouKernel* p);

	CFugouKernel();
	virtual ~CFugouKernel();

	CFugouRule&		GetRule() const;

	virtual void	StartNewGame() = 0;
	virtual void	StartContinueGame() = 0;
	virtual int		GetPlayerCount() = 0;
	virtual int		GetPlayerKind(int nPlayer) const = 0;
	virtual	int		GetTurn() const = 0;			
		//if nTurn == -1 -> means exchange state. In order to exit from exchange state, please call Action(ACTION::TURN_NEXT).
	virtual int		GetHandCards(int nPlayer, int* pnCards) const = 0;
	virtual ACTION_RESULT	Action(const ACTION& action) = 0;
	virtual int		GetDiscardCards(DISCARD_CARDS_INFO* pInfo) const = 0;
	virtual int		GetDiscardCandi(int nPlayer, DISCARD_CANDI* pCandi) const = 0;	
		//In the first discard state the return value is less than 0.
	virtual int		GetExchangeCandi(int nPlayer, DISCARD_CANDI* pCandi) const = 0;
	virtual bool	IsValidDiscard(int nPlayer, const int* pCardIndex, int nCount) const = 0;
	virtual ACTION	ThinkDiscard(int nPlayer) = 0;
	virtual ACTION	ThinkExchange(int nPlayer) = 0;
	virtual void	FinishOneGame() = 0;
	virtual void	SetPlayerLevel(int nPlayer, LEVEL nLevel) = 0;
	virtual bool	IsRevolution() const = 0;
	virtual bool	IsSibariState() const = 0;

	virtual	int		GetGameContext(unsigned char* pBuf) const = 0;
	virtual	bool	SetGameContext(unsigned char* pBuf, int nSize) = 0;

	//For test
	virtual	bool	SetGameContextByTumikomi(char* pBuf, int nSize) = 0;

private:
	mutable	CFugouRule		m_Rule;
};

#endif //__FUGOUKERNEL__
