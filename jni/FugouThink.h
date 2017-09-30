#ifndef __FUGOU_THINK__
#define __FUGOU_THINK__

#include "fugougame.h"

#define THINK_MAX_CANDIS 300
#define THINK_MAX_UNITS 20
#define SCORE_MAX 100000000
#define SCORE_MIN -100000000
#define THINK_MAX_SEARCH_DEPTH 10

class CFugouThink :	public CFugouGame
{
public:
	CFugouThink(void);
	virtual ~CFugouThink(void);

	ACTION	ThinkDiscard(int nPlayer);
	ACTION	ThinkExchange(int nPlayer);

private:
	int GenAllCandis(DISCARD_CANDI* pCandi, DISCARD_CARDS_KIND nSearchKind) const;
	void CombineRecursive(int depth, int* combine_info, int combine_info_count, int* use_info);
	int GetCombineScore(DISCARD_CANDI* pCandi, int* combine_info, int combine_info_count);
	int GetStaticEval(bool bSeeFoul = true);
	bool IsThisTopCandi(const DISCARD_CANDI& candi) const;
	int GetCandiEval(const DISCARD_CANDI& candi) const;
	int GetCandiWeight(const DISCARD_CANDI& candi) const;
	int TumiSearch(int depth, DISCARD_CANDI* pResultCandi, DISCARD_CARDS_KIND nSearchKind, int nPreCandiWeight = -1);
	void MakeMove(int depth, const DISCARD_CANDI& candi);
	void UnmakeMove(int depth);
	void SortCandi(int depth, DISCARD_CANDI* pCandi, int nCount);
	void PrepareThink(int nPlayer);
	bool CheckFoul() const;
	bool IsBadRevolution(int depth, const DISCARD_CANDI& candi);
	bool IsRevolutionCandi(const DISCARD_CANDI& candi) const;
	void MakeRevolutionCards();
	ACTION ThinkGiveCandi(int nPlayer, int nCardNum);
	int SearchGiveCard(int depth, int target_depth, int *pnBestBranch, int* pnHistory);
	void PrepareEngineParam(LEVEL level);
	void Cunning1(int nPlayer, vector<int>& v);

	vector<int>		m_engine_cards;
	vector<int>		m_engine_raw_seq;
	vector<int>		m_other_cards;
	DISCARD_CANDI	m_candis[THINK_MAX_CANDIS*5];
	int				m_nCandiCount;
	int				m_nCombineCount;
	int				m_nMaxCombineScore;

	int				m_nCandiBufferIndex;
	int				m_nThinkPlayer;

	int				m_nHandCardsHistory[THINK_MAX_SEARCH_DEPTH][THINK_MAX_UNITS];

	int				m_nHandCardCount[MAX_PLAYERS];

	//engine parameters
	bool			m_bSeeFuture;
	bool			m_bExchangeConsiderRevolution;
	bool			m_bThinkOfRevolution;
	bool			m_bConsiderOtherWin;
	bool			m_bConsiderDiscardCards;
	bool			m_bConsiderFoul;
	int				m_nEvalKind;//0 -> weakest, 2 -> strongest
};


#endif //__FUGOU_THINK__