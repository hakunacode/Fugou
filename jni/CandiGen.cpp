#include "CandiGen.h"
#include "FugouKernel.h"
#include "FugouGame.h"

 int CCandiGen::m_nWeight1[MAX_CARD_NUM+1] = {//normal weight
 //	   3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13,  1,  2,
// 	0, 12, 13, 1, 2, 3, 4, 5, 6, 7, 8,  9,  10, 11, 14, 15
	 0, 1, 2, 3, 4, 5, 6, 7, 8,  9,  10, 11, 12, 13, 14, 15
 };
 
 int CCandiGen::m_nWeight2[MAX_CARD_NUM+1] = {//revolution weight
// 	0, 2, 1, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 14, 15
	 0, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 14, 15
 };

CCandiGen::CCandiGen( const vector<int>& hand_cards, const DISCARD_CARDS_INFO& before_discard, 
					 const vector<DISCARD_CARDS_INFO>& history,
					 const CFugouRule& rule, bool bRevolution ) :
	m_before_discard(before_discard), 
	m_history(history),
	m_rule(rule), 
	m_bRevolution(bRevolution)
{
	int i;

	for (i = 0; i < (int)hand_cards.size(); i++)
	{
		m_org_cards[i] = hand_cards[i];
		m_cards[i] = hand_cards[i];
	}
	m_cards_count = (int)hand_cards.size();

	for (i = 0; i < m_cards_count; i++)
	{
		int num = GetCardNumber(m_cards[i]);
		if (bRevolution)
			m_cards[i] = MakeCard(GetCardSign(m_cards[i]), m_nWeight2[num]);
	}

	int nCount = CFugouGame::GetCardCountByDiscardKind(m_before_discard.nKind);
	for (i = 0; i < nCount; i++)
	{
		int num = GetCardNumber(m_before_discard.nCards[i]);
		if (bRevolution)
			m_before_discard.nCards[i] = 
				MakeCard(GetCardSign(m_before_discard.nCards[i]), m_nWeight2[num]);
	}
}

CCandiGen::~CCandiGen(void)
{
}

int CCandiGen::operator()( int nPlayer, DISCARD_CANDI* pCandi )
{
	if (m_cards_count == 0)
		return 0;

	if (m_before_discard.nKind == PASS)
	{
		int nCount = 0;
		nCount += GenSeq4Candi(nPlayer, pCandi+nCount);
		nCount += GenIdtxCandi(nPlayer, 4, pCandi+nCount);
		nCount += GenSeq3Candi(nPlayer, pCandi+nCount);
		nCount += GenIdtxCandi(nPlayer, 3, pCandi+nCount);
		nCount += GenIdtxCandi(nPlayer, 2, pCandi+nCount);
		nCount += GenIdtxCandi(nPlayer, 1, pCandi+nCount);
		return nCount;
	}

	int nCount = 0;
	switch(m_before_discard.nKind)
	{
	case SINGLE:
		nCount = GenIdtxCandi(nPlayer, 1, pCandi);
		break;
	case DUAL:
		nCount = GenIdtxCandi(nPlayer, 2, pCandi);
		break;
	case TRIPLE:
		nCount = GenIdtxCandi(nPlayer, 3, pCandi);
		break;
	case QUAD:
		nCount = GenIdtxCandi(nPlayer, 4, pCandi);
		break;
	case SEQ_3:
		nCount = GenSeq3Candi(nPlayer, pCandi);
		break;
	case SEQ_4:
		nCount = GenSeq4Candi(nPlayer, pCandi);
		break;
	}
	return nCount;
}

int CCandiGen::GenSeq3Candi( int nPlayer, DISCARD_CANDI* pCandi ) const
{
	return 0;//
	int i;
	int ret_count = 0;

	int sign = (!m_bRevolution) ? 1 : -1;
	int before_weight = GetCandiWeight(m_before_discard);

	for (i = 0; i < m_cards_count; i++)
	{
		int card_sign = GetCardSign(m_cards[i]);
		int number = GetCardNumber(m_cards[i]);

		if ((number+sign*2) > 14 || (number+sign*2) < 0)
			continue;

		DISCARD_CARDS_INFO info;
		info.nFrom = nPlayer;
		info.nKind = SEQ_3;
		info.nCards[0] = m_cards[i];

		if (ExistInHand(MakeCard(card_sign,number+1*sign)) &&
			ExistInHand(MakeCard(card_sign,number+2*sign)))
		{
			info.nCards[1] = MakeCard(card_sign,number+1*sign);
			info.nCards[2] = MakeCard(card_sign,number+2*sign);
			if (GetCandiWeight(info) > before_weight)
				pCandi[ret_count++] = GetDiscardCandiByInfo(info);
		}

		if (ExistInHand(MakeCard(card_sign,number+1*sign)) &&
			ExistInHand(CARD_JOKER))
		{
			info.nCards[1] = MakeCard(card_sign,number+1*sign);
			info.nCards[2] = CARD_JOKER;
			if (GetCandiWeight(info) > before_weight)
				pCandi[ret_count++] = GetDiscardCandiByInfo(info);
		}

		if (ExistInHand(MakeCard(card_sign,number+2*sign)) &&
			ExistInHand(CARD_JOKER))
		{
			info.nCards[1] = MakeCard(card_sign,number+2*sign);
			info.nCards[2] = CARD_JOKER;
			if (GetCandiWeight(info) > before_weight)
				pCandi[ret_count++] = GetDiscardCandiByInfo(info);
		}
	}
	return ret_count;
}

int CCandiGen::GenSeq4Candi( int nPlayer, DISCARD_CANDI* pCandi ) const
{
	return 0;//
	int i;
	int ret_count = 0;

	int sign = (!m_bRevolution) ? 1 : -1;
	int before_weight = GetCandiWeight(m_before_discard);

	for (i = 0; i < m_cards_count; i++)
	{
		int card_sign = GetCardSign(m_cards[i]);
		int number = GetCardNumber(m_cards[i]);

		DISCARD_CARDS_INFO info;
		info.nFrom = nPlayer;
		info.nKind = SEQ_4;
		info.nCards[0] = m_cards[i];

		if ((number+sign*3) > 13 || (number+sign*3) <= 0)
			continue;

		if (ExistInHand(MakeCard(card_sign,number+1*sign)) &&
			ExistInHand(MakeCard(card_sign,number+2*sign)) &&
			ExistInHand(MakeCard(card_sign,number+3*sign)))
		{
			info.nCards[1] = MakeCard(card_sign,number+1*sign);
			info.nCards[2] = MakeCard(card_sign,number+2*sign);
			info.nCards[3] = MakeCard(card_sign,number+3*sign);
			if (GetCandiWeight(info) > before_weight)
				pCandi[ret_count++] = GetDiscardCandiByInfo(info);
		}

		if (ExistInHand(MakeCard(card_sign,number+1*sign)) &&
			ExistInHand(MakeCard(card_sign,number+2*sign)) &&
			ExistInHand(CARD_JOKER))
		{
			info.nCards[1] = MakeCard(card_sign,number+1*sign);
			info.nCards[2] = MakeCard(card_sign,number+2*sign);
			info.nCards[3] = CARD_JOKER;
			if (GetCandiWeight(info) > before_weight)
				pCandi[ret_count++] = GetDiscardCandiByInfo(info);
		}

		if (ExistInHand(MakeCard(card_sign,number+1*sign)) &&
			ExistInHand(CARD_JOKER) &&
			ExistInHand(MakeCard(card_sign,number+3*sign)))
		{
			info.nCards[1] = MakeCard(card_sign,number+1*sign);
			info.nCards[2] = CARD_JOKER;
			info.nCards[3] = MakeCard(card_sign,number+3*sign);
			if (GetCandiWeight(info) > before_weight)
				pCandi[ret_count++] = GetDiscardCandiByInfo(info);
		}

		if (ExistInHand(CARD_JOKER) &&
			ExistInHand(MakeCard(card_sign,number+2*sign)) &&
			ExistInHand(MakeCard(card_sign,number+3*sign)))
		{
			info.nCards[1] = CARD_JOKER;
			info.nCards[2] = MakeCard(card_sign,number+2*sign);
			info.nCards[3] = MakeCard(card_sign,number+3*sign);
			if (GetCandiWeight(info) > before_weight)
				pCandi[ret_count++] = GetDiscardCandiByInfo(info);
		}
	}
	return ret_count;
}

struct IsUnequalSign {
	IsUnequalSign(const int* cards, int nSign) :
		m_nSign(nSign),
		m_cards(cards)	{
	}
	int m_nSign;
	const int* m_cards;
	bool operator()(const DISCARD_CANDI& candi) const
	{
		int card = m_cards[candi.nIndices[0]];
		if (candi.nKind == SINGLE && GetCardSign(card) == m_nSign)
			return false;
		if (card == CARD_JOKER)
			return false;
		return true;
	}
};

int CCandiGen::GenIdtxCandi( int nPlayer, int n, DISCARD_CANDI* pCandi ) const
{
	int ret_count = 0;

	int i;
	int counts[MAX_CARD_NUM+1];
	int joker_index = -1;
	memset(counts, 0, sizeof(counts));

	for (i = 0; i < m_cards_count; i++)
	{
		int p = GetCardNumber(m_cards[i]);
		KERNEL_ASSERT(p >= 0 && p <= 15);
		counts[p]++;

		if (p == CARD_JOKER)//JOKER
			joker_index = i;
	}

	int before_weight = GetCandiWeight(m_before_discard);
	for (i = 1; i <= MAX_CARD_NUM; i++)
	{
		if (counts[i] >= n)
		{
			DISCARD_CARDS_INFO info;
			for (int j = 0; j < n; j++)
				info.nCards[j] = i;
			info.nKind = GetDiscardKindFromN(n);
			int weight = GetCandiWeight(info);
			if (weight > before_weight)
			{
				int before_count = ret_count;
				ret_count += GenC_N_M_Candi(i, n, pCandi+ret_count);
				for (int k = before_count; k < ret_count; k++)
				{
					DISCARD_CANDI* widget = pCandi+k;
					widget->nPlayer = nPlayer;
					widget->nKind = GetDiscardKindFromN(n);
					widget->nCount = n;
					sort(&widget->nIndices[0], &widget->nIndices[n]);
				}
			}
		}

		if (counts[i] >= (n-1) && counts[CARD_JOKER] > 0 && i != CARD_JOKER)//If you have the JOKER card in hand,
		{
			DISCARD_CARDS_INFO info;
			for (int j = 0; j < n; j++)
				info.nCards[j] = i;
			info.nKind = GetDiscardKindFromN(n);
			int weight = GetCandiWeight(info);
			if (weight > before_weight)
			{
				int before_count = ret_count;
				ret_count += GenC_N_M_Candi(i, n-1, pCandi+ret_count);
				for (int k = before_count; k < ret_count; k++)
				{
					DISCARD_CANDI* widget = pCandi+k;
					widget->nPlayer = nPlayer;
					widget->nKind = GetDiscardKindFromN(n);
					widget->nCount = n;
					widget->nIndices[n-1] = joker_index;
					KERNEL_ASSERT(joker_index >= 0);
					sort(&widget->nIndices[0], &widget->nIndices[n]);
				}
			}
		}
	}

	//RULE SPADE_3
	if (m_rule.Get(CFugouRule::SPADE_3) && !m_history.empty())
	{
		DISCARD_CARDS_INFO last_discard = m_history[m_history.size()-1];
		if (last_discard.nKind == SINGLE && last_discard.nCards[0] == CARD_JOKER)
		{
			//find spade_3
			for (int i = 0; i < m_cards_count; i++)
			{
				if (m_org_cards[i] == MakeCard(SPADE, CARD_3))
				{
					pCandi[ret_count].nKind = SINGLE;
					pCandi[ret_count].nPlayer = nPlayer;
					pCandi[ret_count].nCount = 1;
					pCandi[ret_count].nIndices[0] = i;
					ret_count++;
					break;
				}
			}
		}
	}

	//RULE SAME_ORDER
	if (m_rule.Get(CFugouRule::SIBARI) && m_history.size() > 1)
	{
		DISCARD_CARDS_INFO last_discard = m_history[m_history.size()-1];
		DISCARD_CARDS_INFO semi_last_discard = m_history[m_history.size()-2];
		if (last_discard.nKind == SINGLE && semi_last_discard.nKind == SINGLE)
		{
			if (GetCardSign(last_discard.nCards[0]) == 
				GetCardSign(semi_last_discard.nCards[0]))
			{
				ret_count = (int)(remove_if(&pCandi[0], &pCandi[ret_count], 
					IsUnequalSign(m_cards, GetCardSign(last_discard.nCards[0]))) - &pCandi[0]);
			}
		}
	}
	return ret_count;
}

int CCandiGen::GetCandiWeight( const DISCARD_CARDS_INFO& candi ) const
{
	if (candi.nKind == PASS)
		return -1;

	int buf[10];
	int nCount = CFugouGame::GetCardCountByDiscardKind(candi.nKind);
	for (int i = 0; i < nCount; i++)
		buf[i] = GetCardNumber(candi.nCards[i]);

	sort(&buf[0], &buf[nCount]);

	int ret = buf[0];
	if (candi.nKind == SEQ_3 && ret > (13-2+1))
		ret = 13-2+1;
	if (candi.nKind == SEQ_4 && ret > (13-3+1))
		ret = 13-3+1;

	return ret;
}

DISCARD_CARDS_INFO CCandiGen::GetDiscardInfoByCandi( DISCARD_CANDI& candi ) const
{
	DISCARD_CARDS_INFO ret;
	int nCount = CFugouGame::GetCardCountByDiscardKind(candi.nKind);
	KERNEL_ASSERT(nCount == candi.nCount);

	for (int i = 0; i < nCount; i++)
		ret.nCards[i] = m_cards[candi.nIndices[i]];

	ret.nFrom = candi.nPlayer;
	ret.nKind = candi.nKind;
	return ret;
}

DISCARD_CANDI CCandiGen::GetDiscardCandiByInfo( DISCARD_CARDS_INFO& info ) const
{
	DISCARD_CANDI ret;
	int nCount = CFugouGame::GetCardCountByDiscardKind(info.nKind);

	int i;

	int temp[100];
	for (i = 0; i < m_cards_count; i++)
		temp[i] = m_cards[i];

	for (i = 0; i < nCount; i++)
	{
		int card = info.nCards[i];
		ret.nIndices[i] = -1;
		int j;
		for (j = 0; j < m_cards_count; j++)
		{
			if ((card > 14 && temp[j] == card) ||
				(card <= 14 && GetCardNumber(temp[j]) == card))
			{
				temp[j] = -1;
				ret.nIndices[i] = j;
				break;
			}
		}
		KERNEL_ASSERT(ret.nIndices[i] >= 0);
	}

	sort(&ret.nIndices[0], &ret.nIndices[nCount]);

	ret.nCount = nCount;
	ret.nKind = info.nKind;
	ret.nPlayer = info.nFrom;

	return ret;
}

DISCARD_CARDS_KIND CCandiGen::GetDiscardKindFromN( int n )
{
	DISCARD_CARDS_KIND ret = PASS;
	switch(n)
	{
	case 1:	ret = SINGLE;
		break;
	case 2: ret = DUAL;
		break;
	case 3: ret = TRIPLE;
		break;
	case 4: ret = QUAD;
		break;
	}

	KERNEL_ASSERT(ret != PASS);
	return ret;
}

int CCandiGen::GenC_N_M_Candi( int card_num, int m, DISCARD_CANDI* pCandi ) const
{
	if (m == 0)
		return 0;

	KERNEL_ASSERT(m <= 4);
	int card_indices[10];
	int nCount = 0;
	for (int i = 0; i < m_cards_count; i++)
	{
		if (GetCardNumber(m_cards[i]) == card_num)
			card_indices[nCount++] = i;
	}
	KERNEL_ASSERT(nCount >= m);

	int	buf[5];
	int ret_count = Recursive(card_indices, nCount, buf, 0, m, pCandi);
	KERNEL_ASSERT(ret_count > 0);
	return ret_count;
}

int CCandiGen::Recursive( int* data, int data_count, 
						 int* index_path, int depth, int target_depth, DISCARD_CANDI* pCandi ) const
{
	int ret_count = 0;
	if (depth == target_depth)
	{
		DISCARD_CANDI temp;
		for (int j = 0; j < target_depth; j++)
			temp.nIndices[j] = data[index_path[j]];
		pCandi[ret_count++] = temp;
		return ret_count;
	}

	int start_count = 0;
	if (depth > 0)
		start_count = index_path[depth-1] + 1;
	for (int i = start_count; i < data_count-target_depth + depth + 1; i++)
	{
		index_path[depth] = i;
		ret_count += Recursive(data, data_count, index_path, depth+1, target_depth, pCandi+ret_count);
	}

	return ret_count;
}

bool CCandiGen::ExistInHand( int nCard ) const
{
	return find(&m_cards[0], &m_cards[m_cards_count], nCard) != &m_cards[m_cards_count];
}
