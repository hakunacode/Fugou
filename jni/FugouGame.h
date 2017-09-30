#ifndef __FUGOUGAME__
#define __FUGOUGAME__

#include <vector>
#include <algorithm>
using namespace std;

#include "FugouKernel.h"
#include "FugouContext.h"

void my_assert(bool condition);

#ifdef WIN32
	#include <assert.h>
	#define KERNEL_ASSERT	assert
	#include <windows.h>
#else
	#define KERNEL_ASSERT(a)
#endif

//CFugouGame class represents the FUGOU game logic.

class CFugouGame : public CFugouKernel
{
public:
	CFugouGame(void);
	virtual ~CFugouGame(void);

	friend class CCandiGen;
protected:
	//virtual functions
	void	StartNewGame();
	void	StartContinueGame();
	int		GetPlayerCount();
	int		GetPlayerKind(int nPlayer) const;
	int		GetTurn() const;
	int		GetHandCards(int nPlayer, int* pnCards) const;
	ACTION_RESULT	Action(const ACTION& action);
	int		GetDiscardCards(DISCARD_CARDS_INFO* pInfo) const;
	int		GetDiscardCandi(int nPlayer, DISCARD_CANDI* pCandi) const;
	int		GetExchangeCandi(int nPlayer, DISCARD_CANDI* pCandi) const;
	bool	IsValidDiscard(int nPlayer, const int* pCardIndex, int nCount) const;
	ACTION	ThinkDiscard(int nPlayer);
	ACTION	ThinkExchange(int nPlayer);
	void	FinishOneGame();
	int		GetGameContext(unsigned char* pBuf) const;
	bool	SetGameContext(unsigned char* pBuf, int nSize);
	bool	SetGameContextByTumikomi(char* pBuf, int nSize);
	void	SetPlayerLevel(int nPlayer, LEVEL nLevel);
	bool	IsRevolution() const;
	bool	IsSibariState() const;

	//our helpful functions
	bool	SetGameContext(const FUGOU_CONTEXT& context);
	bool	IsValidDiscard(const ENGINE_DISCARD_CARDS_INFO& info) const;
	void	InitOneGame();
	void	InitVariables();
	void	Deal_Take();
	void	SortHandCards(int nPlayer);
	static int	GetCardCountByDiscardKind(DISCARD_CARDS_KIND kind);
	static bool	IsValidCard(int card);
	ACTION_RESULT	NormalAction( const ENGINE_DISCARD_CARDS_INFO& info );
	bool	ExchangeAction();
	int		GetNextTurn(int nTurn) const;
	void	MakeRanking();
	void	OtherAllPass();
	bool	IsNContainIdt(const ENGINE_DISCARD_CARDS_INFO& info, int n) const;
	int		GetAllDiscardCandi(int nPlayer, DISCARD_CANDI* pCandi) const;
	DISCARD_CARDS_INFO GetDiscardInfoByCandi(const DISCARD_CANDI& candi) const;
	ENGINE_DISCARD_CARDS_INFO GetDiscardInfoByAction(const ACTION& action) const;
	ACTION_RESULT	Action(const ENGINE_DISCARD_CARDS_INFO& info);
	bool	GetDiscardInfoByIndices(int nPlayer, const int* pCardIndex, int nCount, 
		ENGINE_DISCARD_CARDS_INFO* pInfo = NULL) const;
	void GenAllCards(vector<int>& v);
	ACTION_RESULT NextAction();
	int FindMan( int nKind ) const;
	int GetRealFirstPlayer() const;
	void RestoreContext(const FUGOU_CONTEXT& context);
	void SetContext(FUGOU_CONTEXT* context) const;

	int FilterActionFlag(int flag);

	//member variables
	int				m_nPlayerCount;
	int				m_nPlayerKind[MAX_PLAYERS];
	vector<int>		m_vIniHandCards[MAX_PLAYERS];
	int				m_nIniTurn;
	vector<ENGINE_DISCARD_CARDS_INFO>	m_vHistory;

	vector<int>	m_vHandCards[MAX_PLAYERS];
	int		m_nRankInOneGame[MAX_PLAYERS];
	vector<DISCARD_CARDS_INFO>	m_vDiscardCards;
	int		m_nTurn;
	DISCARD_CARDS_INFO		m_nCurrentCard;
	int						m_nOya;
	vector<int>	m_vWinners;
	bool	m_bRevolution;
	bool	m_bGameEnd;
	LEVEL	m_nPlayerThinkLevel[MAX_PLAYERS];
	bool	m_bFoul[MAX_PLAYERS];
	bool	m_bCommitOtherAllPass;
	bool	m_bNeverNextTurn;
	bool	m_bSibari;

	static DISCARD_CANDI s_temp_candi[200];//200*28 = 5.6KBytes

	ACTION	m_exchnageInfo[MAX_PLAYERS];
};

struct HAND_CARD_COMPARE {
	bool operator()(const int& left, const int& right) const {
		int n1 = ((left&0xF) << 4) + ((left>>4)&0x0F);
		int n2 = ((right&0xF) << 4) + ((right>>4)&0x0F);
		return n1 < n2;
	}
};

#endif	//__FUGOUGAME__