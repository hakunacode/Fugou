#ifndef __CANDIGEN__
#define __CANDIGEN__

#include <vector>
#include <algorithm>
#include <string>

using namespace std;

#include "FugouKernel.h"

class CCandiGen
{
public:
	CCandiGen(const vector<int>& hand_cards, const DISCARD_CARDS_INFO& before_discard, 
		const vector<DISCARD_CARDS_INFO>& history,
		const CFugouRule& rule, bool bRevolution);
	~CCandiGen();
	int operator()(int nPlayer, DISCARD_CANDI* pCandi);

private:
	int		GenIdtxCandi(int nPlayer, int n, DISCARD_CANDI* pCandi) const;
	int		GenSeq3Candi(int nPlayer, DISCARD_CANDI* pCandi) const;
	int		GenSeq4Candi(int nPlayer, DISCARD_CANDI* pCandi) const;
	static DISCARD_CARDS_KIND		GetDiscardKindFromN(int n);
	int		GetCandiWeight(const DISCARD_CARDS_INFO& pCandi) const;
	DISCARD_CARDS_INFO GetDiscardInfoByCandi(DISCARD_CANDI& candi) const;
	DISCARD_CANDI GetDiscardCandiByInfo(DISCARD_CARDS_INFO& info) const;
	int		GenC_N_M_Candi(int card_num, int m, DISCARD_CANDI* pCandi) const;
	int		Recursive(int* data, int data_count, 
		int* index_path, int depth, int target_depth, DISCARD_CANDI* pCandi) const;
	bool	ExistInHand( int nCard ) const;

	int						m_cards[MAX_CARD_NUM*6];
	int						m_org_cards[MAX_CARD_NUM*6];
	int						m_cards_count;
	const vector<DISCARD_CARDS_INFO>& m_history;
	DISCARD_CARDS_INFO		m_before_discard;
	const CFugouRule&		m_rule;
	bool m_bRevolution;

public:
 	static	int m_nWeight1[MAX_CARD_NUM+1];
 	static	int m_nWeight2[MAX_CARD_NUM+1];
};

#endif //__CANDIGEN__