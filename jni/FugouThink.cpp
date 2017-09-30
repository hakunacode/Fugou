#include "FugouThink.h"
#include "CandiGen.h"
//#include "../log/DebugLog.hpp"

CFugouThink::CFugouThink(void)
{
	m_engine_cards.reserve(40);
	m_engine_raw_seq.reserve(40);
	m_other_cards.reserve(200);
}

CFugouThink::~CFugouThink(void)
{
}

ACTION CFugouThink::ThinkDiscard( int nPlayer )
{
	PrepareThink(nPlayer);

	DISCARD_CANDI ret_candi;
	TumiSearch(0, &ret_candi, m_nCurrentCard.nKind);

	ACTION ret;
	ret.nPlayer = nPlayer;
	if (ret_candi.nKind == PASS)
	{
		ret.nKind = ACTION::PASS;
		ret.nCount = 0;
	}
	else
	{
		ret.nKind = ACTION::DISCARD;
		ret.nCount = ret_candi.nCount;
		for (int i = 0; i < ret.nCount; i++)
			ret.nIndices[i] = ret_candi.nIndices[i];
	}

	return ret;
}

ACTION CFugouThink::ThinkExchange( int nPlayer )
{
	KERNEL_ASSERT(!m_bRevolution);

	ACTION ret;
	ret.nPlayer = nPlayer;
	ret.nKind = ACTION::EXCHANGE;

	if (m_nPlayerKind[nPlayer] == HEIMIN)
	{
		ret.nCount = 0;
		return ret;
	}

	int hand_card_count = (int)m_vHandCards[nPlayer].size();
	switch (m_nPlayerKind[nPlayer])
	{
	case DAIHINMIN:
		ret.nCount = 2;
		ret.nIndices[0] = hand_card_count-2;
		ret.nIndices[1] = hand_card_count-1;
		break;
	case HINMIN:
		ret.nCount = 1;
		ret.nIndices[0] = hand_card_count-1;
		break;
	case FUGOU:
		ret = ThinkGiveCandi(nPlayer, 1);
		break;
	case DAIFUGOU:
		ret = ThinkGiveCandi(nPlayer, 2);
		break;
	default:
		KERNEL_ASSERT(false);
		break;
	}

	return ret;
}

int CFugouThink::GenAllCandis( DISCARD_CANDI* pCandi, DISCARD_CARDS_KIND nSearchKind ) const
{
	CFugouRule rule;
	rule.SetAllFalse();

	vector<DISCARD_CARDS_INFO> history;

	DISCARD_CARDS_INFO before_discard;
	before_discard.nKind = nSearchKind;

	CCandiGen cg(m_engine_cards, before_discard, history, rule, false);

	return cg(0, pCandi);
}

void CFugouThink::CombineRecursive( int depth, int* combine_info, int combine_info_count, int* use_info )
{
	DISCARD_CANDI* myCandis = m_candis+m_nCandiBufferIndex;
	if (depth >= (int)m_engine_cards.size())
	{
		m_nCombineCount++;

		int nCombineScore = GetCombineScore(myCandis, combine_info, combine_info_count);
		if (nCombineScore > m_nMaxCombineScore)
			m_nMaxCombineScore = nCombineScore;

#ifdef WIN32
		int temp[20];
		memset(temp, 0, sizeof(temp));

		int i;
		for (i = 0; i < combine_info_count; i++)
			for (int j = 0; j < myCandis[combine_info[i]].nCount; j++)
				temp[myCandis[combine_info[i]].nIndices[j]]++;

		for (i = 0; i < (int)m_engine_cards.size(); i++)
		{
			KERNEL_ASSERT(temp[i] == 1);
		}
#endif
		return;
	}

	//already use
	if (use_info[depth] > 0)
	{
		CombineRecursive(depth+1, combine_info, combine_info_count, use_info);
		return;
	}

	int i;
	for (i = 0; i < m_nCandiCount; i++)
	{
		bool bValid = true;
		bool bContain = false;

		for (int j = 0; j < myCandis[i].nCount; j++)
		{
			if (use_info[myCandis[i].nIndices[j]])
			{
				bValid = false;
				break;
			}

			if (myCandis[i].nIndices[j] == depth)
				bContain = true;
		}

		if (bValid && bContain)
		{
			combine_info[combine_info_count] = i;
			for (int j = 0; j < myCandis[i].nCount; j++)
				use_info[myCandis[i].nIndices[j]]++;

			CombineRecursive(depth+1, combine_info, combine_info_count+1, use_info);

			for (int j = 0; j < myCandis[i].nCount; j++)
				use_info[myCandis[i].nIndices[j]]--;
		}
	}
}

int CFugouThink::GetStaticEval( bool bSeeFoul )
{
	if (m_bConsiderFoul && bSeeFoul && CheckFoul())
		return SCORE_MIN;

	DISCARD_CANDI* myCandis = m_candis+m_nCandiBufferIndex;

	m_nCandiCount = GenAllCandis(myCandis, PASS);
	KERNEL_ASSERT(m_nCandiCount < THINK_MAX_CANDIS);

	int combine_info[THINK_MAX_UNITS];
	int use_info[THINK_MAX_UNITS];
	memset(use_info, 0, sizeof(use_info));
	m_nCombineCount = 0;

	m_nMaxCombineScore = SCORE_MIN;
	CombineRecursive(0, combine_info, 0, use_info);

	return m_nMaxCombineScore;
}

int CFugouThink::GetCombineScore( DISCARD_CANDI* pCandi, int* combine_info, int combine_info_count )
{
	if (combine_info_count == 0)
		return SCORE_MAX;

	if (m_nEvalKind == 0)
	{
		int ret_score;
		ret_score = (20-(int)m_engine_cards.size())*8;
		return ret_score;
	}
	else if (m_nEvalKind == 1)
	{
		int score = 0;
		bool bAllWeak = true;
		for (int i = 0; i < combine_info_count; i++)
		{
			int ret = GetCandiEval(pCandi[combine_info[i]]);
			score += ret;
			if (ret > 8)
				bAllWeak = false;
		}

		if (bAllWeak)
			score -= 2 * (int)m_engine_cards.size();

		int ret_score;
		if (m_engine_cards.size() > 3)
			ret_score = score / combine_info_count + (20-(int)m_engine_cards.size())*3/2;
		else
			ret_score = score / combine_info_count + (20-(int)m_engine_cards.size())*2;

		if (find(m_engine_cards.begin(), m_engine_cards.end(), (int)CARD_JOKER)
			!= m_engine_cards.end())
			ret_score += 8;

		return ret_score;
	}
	else if (m_nEvalKind == 2)
	{
		int score = 1000000;
		bool bAllWeak = true;
		for (int i = 0; i < combine_info_count; i++)
		{
			int ret = GetCandiEval(pCandi[combine_info[i]]);
			score = score * ret / 100;
		}
		return score;
	}

	return 0;
}

bool CFugouThink::IsThisTopCandi( const DISCARD_CANDI& candi ) const
{
	if (candi.nKind == PASS)
		return false;

	if (m_bConsiderOtherWin)
	{
		bool bAllHandCardShort = true;
		for (int i = 0; i < m_nPlayerCount; i++)
		{
			if (i == m_nThinkPlayer)
				continue;

			if (m_nHandCardCount[i] >= candi.nCount)
			{
				bAllHandCardShort = false;
				break;
			}
		}

		if (bAllHandCardShort)
			return true;
	}

	CFugouRule rule;
	rule.SetAllFalse();

	vector<DISCARD_CARDS_INFO> history;

	DISCARD_CARDS_INFO before_discard;
	before_discard.nKind = candi.nKind;
	before_discard.nFrom = candi.nPlayer;

	if (candi.nKind != PASS)
	{
		before_discard.nCount = candi.nCount;
		for (int i = 0; i < candi.nCount; i++)
		{
			before_discard.nCards[i] = m_engine_cards[candi.nIndices[i]];
		}
	}

	CCandiGen cg(m_other_cards, before_discard, history, rule, false);

	return cg(0, s_temp_candi) == 0;
}

int CFugouThink::GetCandiEval( const DISCARD_CANDI& candi ) const
{
	/*
	int nWeight = GetCandiWeight(candi);

	if (nWeight == 15)//joker
		nWeight += 20;
	else if (nWeight >= 13)
		nWeight += 17;
	else if (nWeight >= 11)
		nWeight += 14;
	else if (nWeight > 5)
		nWeight += 2;
	*/

	int nCandiWeight = GetCandiWeight(candi);

	int nRetValue = 100;
	switch(nCandiWeight)
	{
	case CARD_3:
		nRetValue = 10;//0.10
		break;
	case CARD_4:
		nRetValue = 15;//0.15
		break;
	case CARD_5:
		nRetValue = 20;//0.20
		break;
	case CARD_6:
		nRetValue = 25;//0.25
		break;
	case CARD_7:
		nRetValue = 30;//0.30
		break;
	case CARD_8:
		nRetValue = 40;//0.40
		break;
	case CARD_9:
		nRetValue = 50;//0.50
		break;
	case CARD_10:
		nRetValue = 60;//0.60
		break;
	case CARD_J:
		nRetValue = 70;//0.70
		break;
	case CARD_Q:
		nRetValue = 80;//0.80
		break;
	case CARD_K:
		nRetValue = 90;//0.90
		break;
	case CARD_A:
		nRetValue = 100;//1.00
		break;
	case CARD_2:
		nRetValue = 120;//1.20
		break;
	case CARD_JOKER:
		nRetValue = 250;//1.50
		break;
	default:
		nRetValue = 100;//1.0
		break;
	}

	return nRetValue;
}

int CFugouThink::GetCandiWeight( const DISCARD_CANDI& candi ) const
{
	if (candi.nKind == PASS)
		return 0;

	KERNEL_ASSERT(candi.nKind != PASS);
	int nMax = -1, nMin = 999;
	for (int i = 0; i < candi.nCount; i++)
	{
		int card = GetCardNumber(m_engine_cards[candi.nIndices[i]]);
		if (m_bRevolution)
			card = CCandiGen::m_nWeight2[card];
		if (card > nMax)
			nMax = card;
		if (card < nMin)
			nMin = card;
	}

	if (candi.nKind == SEQ_3 || candi.nKind == SEQ_4)
	{
		return nMax;
	}

	return nMin;
}

int CFugouThink::TumiSearch( int depth, DISCARD_CANDI* pResultCandi, DISCARD_CARDS_KIND nSearchKind, int nPreCandiWeight )
{
	KERNEL_ASSERT(depth < THINK_MAX_SEARCH_DEPTH);

	if (m_engine_cards.empty())
		return SCORE_MAX;

	if (depth > 0 && m_bConsiderFoul && CheckFoul())
		return SCORE_MIN;

	DISCARD_CANDI* myCandi = m_candis+m_nCandiBufferIndex;

	int nCount;
	if (depth == 0)
		nCount = GetAllDiscardCandi(m_nThinkPlayer, myCandi);
	else
		nCount = GenAllCandis(myCandi, nSearchKind);

	if (nCount <= 0)
	{
		pResultCandi->nKind = PASS;
		pResultCandi->nCount = 0;
		return SCORE_MIN;
	}

	if (depth == 0 && nSearchKind != PASS)
	{
		myCandi[nCount].nKind = PASS;
		myCandi[nCount].nCount = 0;
		nCount++;
	}

	int nTempIndex = m_nCandiBufferIndex;
	m_nCandiBufferIndex += nCount;
	KERNEL_ASSERT(m_nCandiBufferIndex < sizeof(m_candis)/sizeof(m_candis[0]));

	//SortCandi(depth, myCandi, nCount);

	int max_score = SCORE_MIN-1;
	for (int i = 0; i < nCount; i++)
	{
		bool isTopCandi = IsThisTopCandi(myCandi[i]);
		int nCandiWeight = GetCandiWeight(myCandi[i]);

		int score = 0;
		if (m_bThinkOfRevolution && depth == 0 && IsRevolutionCandi(myCandi[i]) && IsBadRevolution(depth, myCandi[i]))
			score = SCORE_MIN;

		if (score >= 0)
		{
			MakeMove(depth, myCandi[i]);

			if (m_bSeeFuture && m_engine_cards.size() < 8 && myCandi->nKind != PASS)
			{
				DISCARD_CANDI temp;
				if (isTopCandi)
				{
					if (nCandiWeight != nPreCandiWeight)
					{
						score = TumiSearch(depth+1, &temp, PASS, (depth == 0)?-1:nCandiWeight);
					}
				}
				// 			else if (depth == 0 || nSearchKind == PASS)
				// 			{
				// 				score = TumiSearch(depth+1, &temp, myCandi[i].nKind);
				// 			}
			}

			if (score < SCORE_MAX)
				score = GetStaticEval();

			UnmakeMove(depth);
		}

		if (score > max_score)
		{
			max_score = score;
			*pResultCandi = myCandi[i];

			if (max_score == SCORE_MAX)
				break;
		}
	}

	m_nCandiBufferIndex = nTempIndex;

	return max_score;
}

void CFugouThink::MakeMove( int depth, const DISCARD_CANDI& candi )
{
	int i;
	for (i = 0; i < (int)m_engine_cards.size(); i++)
	{
		m_nHandCardsHistory[depth][i] = m_engine_cards[i];
	}
	m_nHandCardsHistory[depth][i] = 0;//end sign

	if (candi.nKind != PASS)
	{
		for (int i = 0; i < candi.nCount; i++)
			m_engine_cards[candi.nIndices[i]] = -1;
		m_engine_cards.erase(remove(m_engine_cards.begin(), m_engine_cards.end(), -1), 
			m_engine_cards.end());
	}
}

void CFugouThink::UnmakeMove( int depth )
{
	m_engine_cards.clear();
	for (int i = 0; m_nHandCardsHistory[depth][i] > 0; i++)
		m_engine_cards.push_back(m_nHandCardsHistory[depth][i]);
}

void CFugouThink::PrepareThink( int nPlayer )
{
	PrepareEngineParam(m_nPlayerThinkLevel[nPlayer]);

	int i;
	m_engine_cards.assign(m_vHandCards[nPlayer].begin(), m_vHandCards[nPlayer].end());
	m_engine_raw_seq.clear();
	for (i = 0; i < (int)m_engine_cards.size(); i++)
		m_engine_raw_seq.push_back(i);

	m_nThinkPlayer = nPlayer;

	m_nCandiBufferIndex = 0;

	m_other_cards.clear();
#if 1
	Cunning1(nPlayer, m_other_cards);
#else
	GenAllCards(m_other_cards);
	if (m_bConsiderDiscardCards)
	{
		//get rest card info
		vector<int>::iterator find_result;
		for (i = 0; i < (int)m_vHistory.size(); i++)
		{
			ENGINE_DISCARD_CARDS_INFO &info = m_vHistory[i];
			if (info.nKind != PASS && info.nKind != GIVE_1 && info.nKind != GIVE_2)
			{
				for (int j = 0; j < info.nCount; j++)
				{
					find_result = find(m_other_cards.begin(), m_other_cards.end(), info.nCards[j]);
					if (find_result != m_other_cards.end())
						m_other_cards.erase(find_result);
				}
			}
		}

		for (i = 0; i < (int)m_vHandCards[nPlayer].size(); i++)
		{
			find_result = find(m_other_cards.begin(), m_other_cards.end(), m_vHandCards[nPlayer][i]);
			if (find_result != m_other_cards.end())
				m_other_cards.erase(find_result);
		}
	}
#endif
	sort(m_other_cards.begin(), m_other_cards.end(), HAND_CARD_COMPARE());

	//
	for (int i = 0; i < m_nPlayerCount; i++)
		m_nHandCardCount[i] = (int)m_vHandCards[i].size();
}

bool CFugouThink::CheckFoul() const
{
	if (GetRule().Get(CFugouRule::FORBID_2_AGARI))
	{
		int _2_count = 0, joker_count = 0, else_count = 0;
		for (int i = 0; i < (int)m_engine_cards.size(); i++)
		{
			if (GetCardNumber(m_engine_cards[i]) == CARD_2)
				_2_count++;
			else if (m_engine_cards[i] == CARD_JOKER)
				joker_count++;
			else
				else_count++;
		}

		if (_2_count > 0 && else_count == 0)
			return true;
	}

	if (GetRule().Get(CFugouRule::FORBID_JOKER_AGARI))
	{
		if (m_engine_cards.size() == 1)
		{
			if (m_engine_cards[0] == CARD_JOKER)
				return true;
		}
	}

	return false;
}

bool CFugouThink::IsBadRevolution(int depth, const DISCARD_CANDI& candi)
{
	MakeMove(depth, candi);

	int normal_score = GetStaticEval(false);
	MakeRevolutionCards();
	int revolution_score = GetStaticEval(false);
	MakeRevolutionCards();

	UnmakeMove(depth);

	if (revolution_score < normal_score)
		return true;

	return false;
}

bool CFugouThink::IsRevolutionCandi(const DISCARD_CANDI& candi) const
{
	if (candi.nKind != QUAD)
		return false;
	//for (int i = 0; i < candi.nCount; i++)
	//{
	//	if (m_engine_cards[candi.nIndices[i]] == CARD_JOKER)
	//		return false;
	//}
	return true;
}

void CFugouThink::MakeRevolutionCards()
{
	for (int i = 0; i < (int)m_engine_cards.size(); i++)
	{
		int num = GetCardNumber(m_engine_cards[i]);
		m_engine_cards[i] = 
			MakeCard(GetCardSign(m_engine_cards[i]), CCandiGen::m_nWeight2[num]);
	}
}

ACTION CFugouThink::ThinkGiveCandi( int nPlayer, int nCardNum )
{
	ACTION ret;
	ret.nKind = ACTION::EXCHANGE;
	ret.nPlayer = nPlayer;
	ret.nCount = nCardNum;

	PrepareThink(nPlayer);

	int nHistory[10];

	ret.nIndices[0] = ret.nIndices[1] = -1;

	SearchGiveCard(0, nCardNum, ret.nIndices, nHistory);

	if (nCardNum > 0)
	{
		KERNEL_ASSERT(ret.nIndices[0] >= 0);
	}

	if (nCardNum > 1)
	{
		KERNEL_ASSERT(ret.nIndices[1] >= 0);
	}

	return ret;
}

int CFugouThink::SearchGiveCard( int depth, int target_depth, int *pnBestBranch, int* pnHistory )
{
	if (depth >= target_depth)
		return GetStaticEval(false);

	int nBestBranch[10];
	int max_score = SCORE_MIN-1;

	for (int i = 0; i < (int)m_engine_cards.size(); i++)
	{
		int card = m_engine_cards[i];
		int raw_index = m_engine_raw_seq[i];
		pnHistory[depth] = card;
		m_engine_cards.erase(m_engine_cards.begin()+i);
		m_engine_raw_seq.erase(m_engine_raw_seq.begin()+i);

		int ret_score = SearchGiveCard(depth+1, target_depth, nBestBranch, pnHistory);

		if (m_bExchangeConsiderRevolution && depth > 0 && pnHistory[depth-1] == card)
			ret_score -= 100;

		if (ret_score > max_score)
		{
			max_score = ret_score;
			for (int j = depth+1; j < target_depth;j++)
				pnBestBranch[j] = nBestBranch[j];
			pnBestBranch[depth] = raw_index;
		}

		m_engine_cards.insert(m_engine_cards.begin()+i, card);
		m_engine_raw_seq.insert(m_engine_raw_seq.begin()+i, raw_index);
	}
	return max_score;
}

void CFugouThink::PrepareEngineParam( LEVEL level )
{
	switch(level)
	{
	case LEVEL_1:
		m_bSeeFuture = false;
		m_bExchangeConsiderRevolution = false;
		m_bThinkOfRevolution = false;
		m_bConsiderDiscardCards = false;
		m_bConsiderOtherWin = false;
		m_bConsiderFoul = (arc4random() % 3) == 0;//false;
#ifdef ENABLE_RAND
#else
		m_bConsiderFoul = false;
#endif
		m_nEvalKind = 0;
		break;
	case LEVEL_2:
		m_bSeeFuture = true;
		m_bExchangeConsiderRevolution = false;
		m_bThinkOfRevolution = false;
		m_bConsiderDiscardCards = false;
		m_bConsiderOtherWin = false;
		m_bConsiderFoul = (arc4random() % 2) == 0;//false;
#ifdef ENABLE_RAND
#else
		m_bConsiderFoul = false;
#endif
		m_nEvalKind = 1;
		break;
	case LEVEL_3:
		m_bSeeFuture = true;
		m_bExchangeConsiderRevolution = true;
		m_bThinkOfRevolution = true;
		m_bConsiderDiscardCards = false;
		m_bConsiderOtherWin = false;
		m_bConsiderFoul = true;
		m_nEvalKind = 1;
		break;
	case LEVEL_4:
		m_bSeeFuture = true;
		m_bExchangeConsiderRevolution = true;
		m_bThinkOfRevolution = true;
		m_bConsiderDiscardCards = true;
		m_bConsiderOtherWin = false;
		m_bConsiderFoul = true;
		m_nEvalKind = 2;
		break;
	case LEVEL_5://The strongest
		m_bSeeFuture = true;
		m_bExchangeConsiderRevolution = true;
		m_bThinkOfRevolution = true;
		m_bConsiderDiscardCards = true;
		m_bConsiderOtherWin = true;
		m_bConsiderFoul = true;
		m_nEvalKind = 2;
		break;
	default:
		m_bSeeFuture = true;
		m_bExchangeConsiderRevolution = true;
		m_bThinkOfRevolution = true;
		m_bConsiderDiscardCards = true;
		m_bConsiderOtherWin = true;
		m_bConsiderFoul = true;
		m_nEvalKind = 2;
	}
}

void CFugouThink::Cunning1( int nPlayer, vector<int>& v )
{
	v.clear();
	for (int i = 0; i < m_nPlayerCount; i++)
	{
		if (i == nPlayer)
			continue;
		v.insert(v.begin(), m_vHandCards[i].begin(), m_vHandCards[i].end());
	}
}

struct CANDI_INFO {
	DISCARD_CANDI candi;
	int score;
	bool operator <(const CANDI_INFO& right) const {
		return score > right.score;
	}
};

void CFugouThink::SortCandi( int depth, DISCARD_CANDI* pCandi, int nCount )
{
	static CANDI_INFO buf[100];

	int i;
	for (i = 0; i < nCount; i++)
	{
		MakeMove(depth, pCandi[i]);
		buf[i].candi = pCandi[i];
		buf[i].score = GetStaticEval();
		UnmakeMove(depth);
	}

	sort(&buf[0], &buf[nCount]);

	for (i = 0; i < nCount; i++)
		pCandi[i] = buf[i].candi;
}
