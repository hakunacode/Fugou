#include "Engine.h"
#include "FugouGame.h"
#include "CandiGen.h"

DISCARD_CANDI CFugouGame::s_temp_candi[200];//200*28 = 5.6KBytes

CFugouGame::CFugouGame(void)
{
	int i;
	for (i = 0; i < MAX_PLAYERS; i++)
		m_vHandCards[i].reserve(20);
	m_vDiscardCards.reserve(20);
	m_vHistory.reserve(500);
	m_vWinners.reserve(10);

	for (i = 0; i < MAX_PLAYERS; i++)
		SetPlayerLevel(i, LEVEL_5);
}

CFugouGame::~CFugouGame(void)
{
}

void CFugouGame::StartNewGame()
{
	LOG("CFugouGame::StartNewGame()");
	//Initialize random seed.
#ifdef ENABLE_RAND
	time_t t;
	time(&t);
	srand(t);
#else
	srand(10);
#endif
	
	m_nPlayerCount = GetRule().Get(CFugouRule::PLAYERS) ? 4 : 5;

	fill(&m_nPlayerKind[0], &m_nPlayerKind[m_nPlayerCount], (int)HEIMIN);
	
	InitOneGame();
}

void CFugouGame::StartContinueGame()
{
	LOG("CFugouGame::StartContinueGame()");
	InitOneGame();
	m_nTurn = -1;
}

int CFugouGame::GetPlayerCount()
{
	return m_nPlayerCount;
}

int CFugouGame::GetPlayerKind( int nPlayer ) const
{
	LOG("CFugouGame::GetPlayerKind(%d)", nPlayer);
	
	if (nPlayer < m_nPlayerCount)
		return m_nPlayerKind[nPlayer];
	else
		return HEIMIN;
}

int CFugouGame::GetHandCards( int nPlayer, int* pnCards ) const
{
	int nCount = (int)m_vHandCards[nPlayer].size();
	if (pnCards)
	{
		for (int i = 0; i < nCount; i++)
			pnCards[i] = m_vHandCards[nPlayer][i];
	}
	
	return nCount;
}

ACTION_RESULT CFugouGame::Action( const ACTION& action )
{
	LOG("CFugouGame::Action(%d, %d, %d)", action.nKind, action.nPlayer, action.nCount);
	
	if (action.nKind == ACTION::TURN_NEXT)
	{
		return NextAction();
	}

	KERNEL_ASSERT(action.nKind == ACTION::EXCHANGE || action.nPlayer == m_nTurn);

	if (m_nTurn < -1)
		return ACTION_FAIL;

	if (action.nKind == ACTION::DISCARD && action.nCount == 0)
		return ACTION_FAIL;

	if (action.nKind == ACTION::EXCHANGE && action.nCount == 0)
		return ACTION_OK;

	if (action.nKind == ACTION::EXCHANGE)
	{
		m_exchnageInfo[action.nPlayer] = action;
		return ACTION_OK;
	}

	ENGINE_DISCARD_CARDS_INFO info;
	info = GetDiscardInfoByAction(action);

	return Action(info);
}

ACTION_RESULT CFugouGame::Action( const ENGINE_DISCARD_CARDS_INFO& info )
{
	LOG("CFugouGame::Action(%d, %d, %d)", info.nFrom, info.nKind, info.nCount);
	
	m_vHistory.push_back(info);

	KERNEL_ASSERT(info.nKind != GIVE_1 && info.nKind != GIVE_2);
	KERNEL_ASSERT(IsValidDiscard(info));
	return NormalAction(info);
}

int CFugouGame::GetDiscardCards( DISCARD_CARDS_INFO* pInfo ) const
{
	int nCount = (int)m_vDiscardCards.size();
	if (pInfo)
	{
		for (int i = 0; i < nCount; i++)
			pInfo[i] = m_vDiscardCards[i];
	}
	
	return nCount;
}

int CFugouGame::GetDiscardCandi( int nPlayer, DISCARD_CANDI* pCandi ) const
{
	
	if (m_nCurrentCard.nKind == PASS || m_nOya == nPlayer && m_vDiscardCards.empty())
		return -1;

	return GetAllDiscardCandi(nPlayer, pCandi);
}

int CFugouGame::GetAllDiscardCandi( int nPlayer, DISCARD_CANDI* pCandi ) const
{
	if (pCandi == NULL)
		pCandi = s_temp_candi;
	CCandiGen cg(m_vHandCards[nPlayer], m_nCurrentCard, m_vDiscardCards, GetRule(), m_bRevolution);

	int nCount = cg(nPlayer, pCandi);
	KERNEL_ASSERT(nCount < 200);

	LOG("CFugouGame::GetAllDiscardCandi(%d)=>%d", nPlayer, nCount);
	
	return nCount;
}

bool CFugouGame::IsValidDiscard( const ENGINE_DISCARD_CARDS_INFO& info ) const
{
	if (info.nKind == PASS && m_nCurrentCard.nKind != PASS)
		return true;

	if (m_nCurrentCard.nKind == PASS && info.nKind == PASS)
		return false;

	if (m_nCurrentCard.nKind != PASS && info.nKind != m_nCurrentCard.nKind)
		return false;

	KERNEL_ASSERT(info.nKind != PASS);
	int card_count = GetCardCountByDiscardKind(info.nKind);

	int nCount = GetAllDiscardCandi(info.nFrom, s_temp_candi);

	ENGINE_DISCARD_CARDS_INFO temp_info = info;
	sort(&temp_info.nCards[0], &temp_info.nCards[card_count], HAND_CARD_COMPARE());

	for (int i = 0; i < nCount; i++)
	{
		DISCARD_CARDS_INFO temp = GetDiscardInfoByCandi(s_temp_candi[i]);
		sort(&temp.nCards[0], &temp.nCards[card_count], HAND_CARD_COMPARE());

		if (memcmp(temp.nCards, temp_info.nCards, sizeof(temp.nCards[0]) * card_count) == 0)
			return true;
	}

	return false;
}

void CFugouGame::InitOneGame()
{
	LOG("CFugouGame::InitOneGame()");
	
	//Initialize random seed.
#ifdef ENABLE_RAND
	time_t t;
	time(&t);
	srand(t);
#else
	srand(10);
#endif
	
	InitVariables();

	Deal_Take();

	int* p = find(&m_nPlayerKind[0], &m_nPlayerKind[m_nPlayerCount], (int)DAIHINMIN);
	if (p != &m_nPlayerKind[m_nPlayerCount])
	{
		m_nIniTurn = (int)(p - m_nPlayerKind);
		m_nTurn = -1;
	}
	else
	{
#if 0
		m_nTurn = -1;
		for (int i = 0; i < m_nPlayerCount; i++)
		{
			vector<int>::iterator p = find(m_vHandCards[i].begin(), m_vHandCards[i].end(), MakeCard(CLUB, CARD_3));
			if (p != m_vHandCards[i].end())
			{
				m_nTurn = i;
				break;
			}
		}
#endif
		m_nTurn = 0;//The first turn is human.
		m_nIniTurn = m_nTurn;
		m_nOya = m_nTurn;
	}

	//save context
	for (int i = 0; i < MAX_PLAYERS; i++)
		m_vIniHandCards[i] = m_vHandCards[i];
}

void CFugouGame::Deal_Take()
{
	LOG("CFugouGame::Deal_Take()");
	
	vector<int>	buf;
	buf.reserve(100);

	GenAllCards(buf);

#ifdef ENABLE_RAND
	random_shuffle(buf.begin(), buf.end());
#else
#endif
	
	int i;
	for (i = 0; i < m_nPlayerCount; i++)
		m_vHandCards[i].clear();

	int index = 0;
	while(index < (int)buf.size())
	{
		for (int i = 0; i < m_nPlayerCount; i++)
		{
			if (index >= (int)buf.size())
				break;
			m_vHandCards[i].push_back(buf[index++]);
		}
	}

	for (i = 0; i < m_nPlayerCount; i++)
		SortHandCards(i);
}

void CFugouGame::SortHandCards( int nPlayer )
{
	LOG("CFugouGame::Deal_Take(%d)", nPlayer);
	
	sort(m_vHandCards[nPlayer].begin(), m_vHandCards[nPlayer].end(), HAND_CARD_COMPARE());
}

ACTION CFugouGame::ThinkDiscard( int nPlayer )
{
	LOG("CFugouGame::ThinkDiscard(%d)", nPlayer);
	
	ACTION ret;
	ret.nPlayer = nPlayer;
	ret.nKind = ACTION::PASS;
	ret.nCount = 0;

	return ret;
}

ACTION CFugouGame::ThinkExchange( int nPlayer )
{
	LOG("CFugouGame::ThinkExchange(%d)", nPlayer);
	
	ACTION ret;
	ret.nPlayer = nPlayer;
	ret.nKind = ACTION::EXCHANGE;
	ret.nCount = 0;

	return ret;
}

int CFugouGame::GetTurn() const
{
	return m_nTurn;
}

int CFugouGame::GetCardCountByDiscardKind( DISCARD_CARDS_KIND kind )
{
	int nCount = 0;
	switch(kind)
	{
	case SINGLE:
	case GIVE_1:
		nCount = 1;
		break;
	case DUAL:
	case GIVE_2:
		nCount = 2;
		break;
	case TRIPLE:
	case SEQ_3:
		nCount = 3;
		break;
	case QUAD:
	case SEQ_4:
		nCount = 4;
		break;
	case PASS:
		nCount = 0;
		break;
	default:
		KERNEL_ASSERT(false);
		break;
	}
	
	LOG("CFugouGame::GetCardCountByDiscardKind(%d)=>%d", kind, nCount);
	return nCount;
}

bool CFugouGame::IsValidCard( int card )
{
	if (card == CARD_JOKER)
		return true;
	if (card < 0)
		return false;
	int number = card & 0x0F;
	if (number == 0 || number == 0x0E)
		return false;
	int sign = card >> 4;
	if (sign != DIAMOND && sign != SPADE && sign != HEART && sign != CLUB)
		return false;
	if (number == CARD_JOKER && sign != 0)
		return false;
	return true;
}

ACTION_RESULT CFugouGame::NormalAction( const ENGINE_DISCARD_CARDS_INFO& info )
{
	LOG("CFugouGame::NormalAction(%d, %d, %d)", info.nFrom, info.nKind, info.nCount);
	
	ACTION_RESULT ret_code = ACTION_OK;

	if (m_nTurn != info.nFrom)
	{
		KERNEL_ASSERT(false);
		return ACTION_FAIL;
	}

	int n = (int)m_vHistory.size();
	if (n >= m_nPlayerCount)
	{
		int pass_count = 0;
		for (int i = n-1; i >= 0; i--)
		{
			if (m_vHistory[i].nKind != PASS)
				break;
			else
				pass_count++;
		}

		pass_count += (int)m_vWinners.size();
		bool bBeforeOyaWin = false;
		if (m_nCurrentCard.nKind != PASS)
		{
			if (find(m_vWinners.begin(), m_vWinners.end(), m_nCurrentCard.nFrom) 
				!= m_vWinners.end())
			{
				pass_count--;
				bBeforeOyaWin = true;
			}
		}

		KERNEL_ASSERT(pass_count < m_nPlayerCount);
		if (pass_count >= (m_nPlayerCount-1))
		{
			KERNEL_ASSERT(m_nCurrentCard.nKind != PASS);
			m_nOya = m_nCurrentCard.nFrom;

			//if all players but self passed
			m_bCommitOtherAllPass = true;//OtherAllPass();
			if (bBeforeOyaWin)
				m_nOya = GetNextTurn(m_nOya);
		}
	}

	//get rid of the cards in hand
	vector<int> &vPlayerCards = m_vHandCards[info.nFrom];
	int nCardCount = GetCardCountByDiscardKind(info.nKind);
	for (int i = 0; i < nCardCount; i++)
	{
		int card = info.nCards[i];
		KERNEL_ASSERT(IsValidCard(card));
		int nRawSize = (int)vPlayerCards.size();
		vPlayerCards.erase(find(vPlayerCards.begin(), vPlayerCards.end(), card));
		KERNEL_ASSERT((nRawSize - (int)vPlayerCards.size()) == 1);
	}

	SortHandCards(info.nFrom);

	//RULE ONLY_8_TOP
	if (GetRule().Get(CFugouRule::ONLY_8_TOP))
	{
		if (IsNContainIdt(info, CARD_8))
		{
			m_bCommitOtherAllPass = true;//OtherAllPass();
			m_bNeverNextTurn = true;
			ret_code |= ACTION_8KIRI;
		}
	}

	if (info.nKind != PASS)
	{
		DISCARD_CARDS_INFO temp;
		temp.nKind = info.nKind;
		temp.nCount = info.nCount;
		temp.nFrom = info.nFrom;
		memcpy(temp.nCards, info.nCards, sizeof(temp.nCards));

		m_vDiscardCards.push_back(temp);

		bool bSpade3 = false;
		if (GetRule().Get(CFugouRule::SPADE_3) && m_vDiscardCards.size() > 1)
		{
			DISCARD_CARDS_INFO temp1 = m_vDiscardCards[m_vDiscardCards.size()-1];
			DISCARD_CARDS_INFO temp2 = m_vDiscardCards[m_vDiscardCards.size()-2];
			if (temp1.nCount == 1 && temp1.nCards[0] == MakeCard(SPADE, CARD_3))
			{
				if (temp2.nCount == 1 && temp2.nCards[0] == CARD_JOKER)
					bSpade3 = true;
			}
		}
		m_nCurrentCard = temp;
		if (bSpade3)
		{
			ret_code |= ACTION_SPADE3;
			KERNEL_ASSERT(m_nCurrentCard.nCount == 1);
			m_nCurrentCard.nCards[0] = CARD_JOKER;
			m_bCommitOtherAllPass = true;
			m_bNeverNextTurn = true;
		}

		if (GetRule().Get(CFugouRule::SIBARI) && m_vDiscardCards.size() > 1)
		{
			DISCARD_CARDS_INFO temp1 = m_vDiscardCards[m_vDiscardCards.size()-1];
			DISCARD_CARDS_INFO temp2 = m_vDiscardCards[m_vDiscardCards.size()-2];
			if (temp1.nCount == 1 && 
				GetCardSign(temp1.nCards[0]) == GetCardSign(temp2.nCards[0]))
			{
				if (!m_bSibari)
				{
					m_bSibari = true;
					ret_code |= ACTION_SIBARI;
				}
			}
		}
	}

	//Winner process
	if (vPlayerCards.empty())
	{
		m_vWinners.push_back(info.nFrom);
		ret_code |= ACTION_AGARI;
		//judge foul discard
		bool bFoul = false;
		if (GetRule().Get(CFugouRule::FORBID_JOKER_AGARI))
		{
			//JOKER foul
			if (info.nKind == SINGLE && info.nCards[0] == CARD_JOKER)
				bFoul = true;
		}

		if (GetRule().Get(CFugouRule::FORBID_2_AGARI))
		{
			//'2' foul
			if (IsNContainIdt(info, CARD_2))
				bFoul = true;
		}

		if (bFoul)
		{
			ret_code |= ACTION_FOUL;
			m_bFoul[info.nFrom] = true;
		}

		if (!bFoul && GetRule().Get(CFugouRule::PRESSURE))
		{
			int nDaiFugou = FindMan(DAIFUGOU);
			if (nDaiFugou >= 0 && info.nFrom != nDaiFugou && GetRealFirstPlayer() == info.nFrom)
			{
				if (!m_vHandCards[nDaiFugou].empty())
					ret_code |= ACTION_DAIFUGOU_FALL;
			}
		}
	}

	//Revolution
	if (info.nKind == QUAD && GetRule().Get(CFugouRule::REVOLUTION))
	{
		bool bJoker = false;
		for (int i = 0; i < 4; i++)
			if (info.nCards[i] == CARD_JOKER)
				bJoker = true;
//		if (!bJoker) //not pure quad, just quad is ok.
		{
			if (m_bRevolution)
			{
				m_bRevolution = false;
				ret_code |= ACTION_REVOLUTION_FREE;
			}
			else
			{
				m_bRevolution = true;
				ret_code |= ACTION_REVOLUTION_SET;
			}
		}
	}

	if (ret_code & ACTION_DAIFUGOU_FALL)
	{
		int nDaiFugou = FindMan(DAIFUGOU);
		KERNEL_ASSERT(nDaiFugou >= 0);
		m_vHandCards[nDaiFugou].clear();
		m_vWinners.push_back(nDaiFugou);
	}

	return FilterActionFlag(ret_code);
}

bool CFugouGame::ExchangeAction()
{
	LOG("CFugouGame::ExchangeAction()");
	
	int n1, n2;
	n1 = FindMan(DAIFUGOU);
	n2 = FindMan(DAIHINMIN);
	KERNEL_ASSERT(n1 >= 0 && n2 >= 0);
	if (n1 < 0 || n2 < 0)
		return false;
	
	if (m_exchnageInfo[n1].nCount == m_exchnageInfo[n2].nCount)
	{
		for (int i = 0; i < m_exchnageInfo[n1].nCount; i++)
		{
			int temp = m_vHandCards[n1][m_exchnageInfo[n1].nIndices[i]];
			m_vHandCards[n1][m_exchnageInfo[n1].nIndices[i]] = 
				m_vHandCards[n2][m_exchnageInfo[n2].nIndices[i]];
			m_vHandCards[n2][m_exchnageInfo[n2].nIndices[i]] = temp;
		}
	}
	else
	{
		KERNEL_ASSERT(false);
		return false;
	}

	n1 = FindMan(FUGOU);
	n2 = FindMan(HINMIN);
	KERNEL_ASSERT(n1 >= 0 && n2 >= 0);
	if (n1 < 0 || n2 < 0)
		return false;

	if (m_exchnageInfo[n1].nCount == m_exchnageInfo[n2].nCount)
	{
		for (int i = 0; i < m_exchnageInfo[n1].nCount; i++)
		{
			int temp = m_vHandCards[n1][m_exchnageInfo[n1].nIndices[i]];
			m_vHandCards[n1][m_exchnageInfo[n1].nIndices[i]] = 
				m_vHandCards[n2][m_exchnageInfo[n2].nIndices[i]];
			m_vHandCards[n2][m_exchnageInfo[n2].nIndices[i]] = temp;
		}
	}
	else
	{
		KERNEL_ASSERT(false);
		return false;
	}

	for (int i = 0; i < m_nPlayerCount; i++)
		SortHandCards(i);

	return true;
}

void CFugouGame::InitVariables()
{
	LOG("CFugouGame::InitVariables()");
	
	memset(m_nRankInOneGame, 0, sizeof(m_nRankInOneGame));
	int i;
	for (i = 0; i < MAX_PLAYERS; i++)
	{
		m_vHandCards[i].clear();
		m_bFoul[i] = false;
		m_exchnageInfo[i].nCount = 0;
	}

	m_vHistory.clear();
	m_vWinners.clear();
	m_bRevolution = false;
	m_bGameEnd = false;

	m_bCommitOtherAllPass = false;
	m_bNeverNextTurn = false;
	m_bSibari = false;
	OtherAllPass();
}

int CFugouGame::GetGameContext(unsigned char* pBuf) const
{
	FUGOU_CONTEXT context;
	SetContext(&context);
	if (pBuf)
		memcpy(pBuf, &context, sizeof(context));
	return sizeof(context);
}

bool CFugouGame::SetGameContext(unsigned char* pBuf, int nSize)
{
	if (pBuf == NULL || nSize < sizeof(FUGOU_CONTEXT))
	{
		KERNEL_ASSERT(false);
		return false;
	}

	FUGOU_CONTEXT context;
	memcpy(&context, pBuf, sizeof(context));

	return SetGameContext(context);
}

DISCARD_CARDS_INFO CFugouGame::GetDiscardInfoByCandi( const DISCARD_CANDI& candi ) const
{
	DISCARD_CARDS_INFO ret;
	ret.nKind = candi.nKind;
	ret.nFrom = candi.nPlayer;

	if (candi.nKind == PASS) {
		LOG("CFugouGame::GetDiscardInfoByCandi(%d, %d, %d)=>%d, %d", candi.nPlayer, candi.nCount, candi.nKind, ret.nFrom, ret.nKind);
		return ret;
	}

	ret.nCount = candi.nCount;
	KERNEL_ASSERT(ret.nCount == GetCardCountByDiscardKind(candi.nKind));
	if (candi.nKind != PASS)
	{
		for (int i = 0; i < candi.nCount; i++)
		{
			ret.nCards[i] = m_vHandCards[candi.nPlayer][candi.nIndices[i]];
			KERNEL_ASSERT(candi.nIndices[i] < (int)m_vHandCards[candi.nPlayer].size());
		}
	}
	
	LOG("CFugouGame::GetDiscardInfoByCandi(%d, %d, %d)=>%d, %d, %d", candi.nPlayer, candi.nCount, candi.nKind, ret.nFrom, ret.nKind, ret.nCount);
	return ret;
}

void CFugouGame::SetPlayerLevel( int nPlayer, LEVEL nLevel )
{
	LOG("CFugouGame::SetPlayerLevel(%d, %d)", nPlayer, nLevel);
	
	KERNEL_ASSERT(nPlayer < MAX_PLAYERS);
	m_nPlayerThinkLevel[nPlayer] = nLevel;
}

int CFugouGame::GetNextTurn(int nTurn) const
{
	LOG("CFugouGame::GetNextTurn(%d)", nTurn);
	if ((int)m_vWinners.size() >= (m_nPlayerCount-1)) {
		LOG("	=>-1");
		return -1;
	}
	
	int i;
	int nKindPlayer[MAX_PLAYERS];
	fill(&nKindPlayer[0], &nKindPlayer[MAX_PLAYERS], -1);

	for (i = 0; i < m_nPlayerCount; i++)
		nKindPlayer[m_nPlayerKind[i]] = i;

	if (true)//if (nKindPlayer[DAIHINMIN] < 0)
	{//if all players is heimin
		do{
			nTurn = (nTurn + 1) % m_nPlayerCount;
		}while(m_vHandCards[nTurn].empty());
	}
	else
	{
		KERNEL_ASSERT(nKindPlayer[DAIHINMIN] >= 0 &&
			nKindPlayer[HINMIN] >= 0 &&
			nKindPlayer[FUGOU] >= 0 &&
			nKindPlayer[DAIFUGOU] >= 0);

		do{
			nTurn = nKindPlayer[(m_nPlayerKind[nTurn] + MAX_KIND - 1) % MAX_KIND];
		}while(m_vHandCards[nTurn].empty());
	}
	KERNEL_ASSERT(nTurn >= 0 && nTurn < m_nPlayerCount);
	LOG("	=>%d", nTurn);
	return nTurn;
}

void my_assert(bool condition)
{
	if (!condition)
		*(int*)0 = 0;
}

bool CFugouGame::IsValidDiscard(int nPlayer, const int* pCardIndex, int nCount) const
{
	if (m_nCurrentCard.nKind == PASS && 
		m_nOya == nPlayer && nCount == 0)
		return false;

	if (nCount == 0)
		return true;

	return GetDiscardInfoByIndices(nPlayer, pCardIndex, nCount);
}

struct RANK_ITEM
{
	int nPlayer;
	int nWeight;
};

struct COMPARE_RANK_ITEM
{
	bool operator()(const RANK_ITEM& i1, const RANK_ITEM& i2) const{
		return i1.nWeight > i2.nWeight;
	}
};

void CFugouGame::MakeRanking()
{
	LOG("CFugouGame::MakeRanking()");
	
	KERNEL_ASSERT((int)m_vWinners.size() >= (m_nPlayerCount-1));
	RANK_ITEM	datas[MAX_PLAYERS];
	int i;
	for (int i = 0; i < m_nPlayerCount; i++)
	{
		datas[i].nPlayer = i;
		datas[i].nWeight = 0;
		if (m_bFoul[i])
			datas[i].nWeight -= MAX_PLAYERS;
		if (m_nPlayerKind[i] == DAIFUGOU && GetRule().Get(CFugouRule::PRESSURE))
		{
			int nRealFirstPlayer = GetRealFirstPlayer();
			if (nRealFirstPlayer >= 0 && i != nRealFirstPlayer)
			{
				datas[i].nWeight -= MAX_PLAYERS * 3;
			}
		}
	}

	for (i = 0; i < (int)m_vWinners.size(); i++)
	{
		int player = m_vWinners[i];
		if (!m_bFoul[player])
			datas[player].nWeight += MAX_PLAYERS - i;
		else
			datas[player].nWeight += i;
	}

	sort(&datas[0], &datas[m_nPlayerCount], COMPARE_RANK_ITEM());

	m_nPlayerKind[datas[0].nPlayer] = DAIFUGOU;
	m_nPlayerKind[datas[1].nPlayer] = FUGOU;
	m_nPlayerKind[datas[2].nPlayer] = HEIMIN;
	m_nPlayerKind[datas[3].nPlayer] = HINMIN;
	m_nPlayerKind[datas[4].nPlayer] = DAIHINMIN;
}

void CFugouGame::FinishOneGame()
{
	MakeRanking();
}

void CFugouGame::OtherAllPass()
{
	m_vDiscardCards.clear();
	m_nCurrentCard.nKind = PASS;
	m_bSibari = false;
}

bool CFugouGame::IsNContainIdt( const ENGINE_DISCARD_CARDS_INFO& info, int n ) const
{
	if (info.nKind == PASS || info.nKind == SEQ_3 || info.nKind == SEQ_4)
		return false;

	int joker_count = 0, card_n_count = 0;
	int discard_card_count = GetCardCountByDiscardKind(info.nKind);
	for (int i = 0; i < discard_card_count; i++)
	{
		if (info.nCards[i] == CARD_JOKER)
			joker_count++;
		if (GetCardNumber(info.nCards[i]) == n)
			card_n_count++;
	}

	if (card_n_count > 0 && (card_n_count+joker_count) == discard_card_count)
		return true;

	return false;
}

ENGINE_DISCARD_CARDS_INFO CFugouGame::GetDiscardInfoByAction( const ACTION& action ) const
{
	LOG("CFugouGame::GetDiscardInfoByAction(%d, %d, %d)", action.nKind, action.nPlayer, action.nCount);
	
	KERNEL_ASSERT(action.nKind != ACTION::TURN_NEXT);

	ENGINE_DISCARD_CARDS_INFO ret;
	ret.nFrom = action.nPlayer;
	
	if (action.nKind == ACTION::DISCARD)
	{
		KERNEL_ASSERT(IsValidDiscard(action.nPlayer, action.nIndices, action.nCount));
		bool bRet = GetDiscardInfoByIndices(action.nPlayer, action.nIndices, action.nCount, &ret);
		KERNEL_ASSERT(bRet);
		return ret;
	}
	else if (action.nKind == ACTION::EXCHANGE)
	{
		if (action.nCount == 1)
		{
			ret.nKind = GIVE_1;
			ret.nCards[0] = m_vHandCards[action.nPlayer][action.nIndices[0]];
		}
		else if (action.nCount == 2)
		{
			ret.nKind = GIVE_2;
			ret.nCards[0] = m_vHandCards[action.nPlayer][action.nIndices[0]];
			ret.nCards[1] = m_vHandCards[action.nPlayer][action.nIndices[1]];
		}
		else
		{
			KERNEL_ASSERT(false);
		}
	}
	else if (action.nKind == ACTION::PASS)
	{
		ret.nKind = PASS;
		ret.nCount = 0;
	}

	return ret;
}

bool CFugouGame::GetDiscardInfoByIndices( int nPlayer, const int* pCardIndex, int nCount, 
										 ENGINE_DISCARD_CARDS_INFO* pInfo ) const
{
	if (nCount <= 0 || nCount > 4)
		return false;

	int estimate_count = GetCardCountByDiscardKind(m_nCurrentCard.nKind);
	if (estimate_count > 0 && estimate_count != nCount)
		return false;

	int i;
	int temp[5];
	for (i = 0; i < nCount; i++)
		temp[i] = pCardIndex[i];

	sort(&temp[0], &temp[nCount]);

	int nCandiCount = GetAllDiscardCandi(nPlayer, s_temp_candi);

	for (i = 0; i < nCandiCount; i++)
	{
		sort(&s_temp_candi[i].nIndices[0], &s_temp_candi[i].nIndices[nCount]);

		if (s_temp_candi[i].nCount == nCount &&
			memcmp(temp, s_temp_candi[i].nIndices, sizeof(temp[0]) * nCount) == 0)
		{
			if (pInfo)
			{
				pInfo->nFrom = s_temp_candi[i].nPlayer;
				pInfo->nKind = s_temp_candi[i].nKind;
				pInfo->nCount = s_temp_candi[i].nCount;
				for (int j = 0; j < s_temp_candi[i].nCount; j++)
					pInfo->nCards[j] = m_vHandCards[nPlayer][s_temp_candi[i].nIndices[j]];
			}
			return true;
		}
	}

	return false;
}

void CFugouGame::GenAllCards( vector<int>& v )
{
	int nSigns[4] = {HEART, DIAMOND, SPADE, CLUB};
	for (int i = 0; i < 4; i++)
		for (int j = 1; j <= 13; j++)
			v.push_back((nSigns[i]<<4)+j);

	v.push_back(CARD_JOKER);
	if (GetRule().Get(CFugouRule::JOKER_COUNT))
		v.push_back(CARD_JOKER);

}

bool CFugouGame::IsRevolution() const
{
	return m_bRevolution;
}

ACTION_RESULT CFugouGame::NextAction()
{
	ACTION_RESULT ret_code = ACTION_OK;

	if (m_nTurn < 0)
	{
		//if exchange state
		if (false == ExchangeAction())
			return ACTION_FAIL;

		m_nTurn = m_nIniTurn;
		m_nOya = m_nTurn;
		m_bSibari = false;
		ret_code = ACTION_OK;

		for (int i = 0; i < MAX_PLAYERS; i++)
			m_vIniHandCards[i] = m_vHandCards[i];
	}
	else
	{
		if (m_bCommitOtherAllPass)
		{
			OtherAllPass();
			m_bCommitOtherAllPass = false;
		}

		if (!m_bNeverNextTurn || m_vHandCards[m_nTurn].empty())
			m_nTurn = GetNextTurn(m_nTurn);

		m_bNeverNextTurn = false;
	}

	if (m_nTurn < 0)
	{
		ret_code |= ACTION_GAME_END;
		m_bGameEnd = true;
	}
	return ret_code;
}

int CFugouGame::FindMan( int nKind ) const
{
	int i;
	for (i = 0; i < m_nPlayerCount; i++)
	{
		if (m_nPlayerKind[i] == nKind)
			break;
	}

	if (i < m_nPlayerCount)
		return i;

	return -1;
}

int CFugouGame::GetExchangeCandi( int nPlayer, DISCARD_CANDI* pCandi ) const
{
	LOG("CFugouGame::GetExchangeCandi(%d)", nPlayer);
	
	if (m_nPlayerKind[nPlayer] == DAIFUGOU || m_nPlayerKind[nPlayer] == FUGOU)
		return -1;

	if (m_nPlayerKind[nPlayer] == HEIMIN)
		return 0;

	int nCount = 0;
	if (m_nPlayerKind[nPlayer] == HINMIN)
	{
		int nMaxWeight = -1;

		for (int i = 0; i < (int)m_vHandCards[nPlayer].size(); i++)
		{
			int nWeight = GetCardNumber(m_vHandCards[nPlayer][i]);
			if (nWeight >= nMaxWeight)
			{
				if (nWeight > nMaxWeight)
				{
					nCount = 0;
					nMaxWeight = nWeight;
				}
				if (pCandi)
				{
					pCandi[nCount].nCount = 1;
					pCandi[nCount].nPlayer = nPlayer;
					pCandi[nCount].nKind = GIVE_1;
					pCandi[nCount].nIndices[0] = i;
				}
				nCount++;
			}
		}
	}

	if (m_nPlayerKind[nPlayer] == DAIHINMIN)
	{
		int nMaxWeight = -1;

		for (int i = 0; i < (int)m_vHandCards[nPlayer].size()-1; i++)
		{
			for (int j = i+1; j < (int)m_vHandCards[nPlayer].size(); j++)
			{
				int nWeight = GetCardNumber(m_vHandCards[nPlayer][i]) + 
					GetCardNumber(m_vHandCards[nPlayer][j]);
				if (nWeight >= nMaxWeight)
				{
					if (nWeight > nMaxWeight)
					{
						nCount = 0;
						nMaxWeight = nWeight;
					}
					if (pCandi)
					{
						pCandi[nCount].nCount = 2;
						pCandi[nCount].nPlayer = nPlayer;
						pCandi[nCount].nKind = GIVE_2;
						pCandi[nCount].nIndices[0] = i;
						pCandi[nCount].nIndices[1] = j;
					}
					nCount++;
				}
			}
		}
	}

	return nCount;
}

bool CFugouGame::IsSibariState() const
{
	return m_bSibari;
}

int CFugouGame::GetRealFirstPlayer() const
{
	int nRealFirstPlayer = -1;
	for (int j = 0; j < (int)m_vWinners.size(); j++)
	{
		if (!m_bFoul[m_vWinners[j]])
		{
			nRealFirstPlayer = m_vWinners[j];
			break;
		}
	}
	return nRealFirstPlayer;
}

void CFugouGame::RestoreContext( const FUGOU_CONTEXT& context )
{
	GetRule() = context.m_Rule;

	m_nPlayerCount = context.m_nPlayerCount;
	m_nIniTurn = context.m_nIniTurn;

	int i;
	for (i = 0; i < MAX_PLAYERS; i++)
	{
		m_nPlayerKind[i] = context.m_nPlayerKind[i];
		m_vIniHandCards[i].clear();
		for (int j = 0; ;j++)
		{
			if (context.m_vIniHandCards[i][j] <= 0)
				break;
			m_vIniHandCards[i].push_back(context.m_vIniHandCards[i][j]);
			KERNEL_ASSERT(j < FUGOU_CONTEXT::MAX_HAND_CARDS);
		}
	}

	m_vHistory.clear();
	KERNEL_ASSERT(context.m_nHistoryCount < FUGOU_CONTEXT::MAX_HISTORY_COUNT);
	for (i = 0; i <  context.m_nHistoryCount; i++)
	{
		m_vHistory.push_back(context.m_vHistory[i]);
	}
}

void CFugouGame::SetContext( FUGOU_CONTEXT* context ) const
{
	context->m_Rule = GetRule();

	context->m_nPlayerCount = m_nPlayerCount;
	context->m_nIniTurn = m_nIniTurn;

	int i;
	for (i = 0; i < MAX_PLAYERS; i++)
	{
		int j;
		context->m_nPlayerKind[i] = m_nPlayerKind[i];
		for (j = 0; j < (int)m_vIniHandCards[i].size(); j++)
		{
			context->m_vIniHandCards[i][j] = m_vIniHandCards[i][j];
		}
		context->m_vIniHandCards[i][j] = -1;
	}

	context->m_nHistoryCount = (int)m_vHistory.size();
	for (i = 0; i <  context->m_nHistoryCount; i++)
	{
		context->m_vHistory[i] = m_vHistory[i];
	}
}

bool CFugouGame::SetGameContextByTumikomi( char* pBuf, int nSize )
{
	FUGOU_CONTEXT context;
	bool bRet = context.SetData(pBuf, nSize);
	if (!bRet)
		return false;

	SetGameContext(context);

	return true;
}

bool CFugouGame::SetGameContext(const FUGOU_CONTEXT& context)
{
	InitVariables();

	RestoreContext(context);

	int i;
	for (i = 0; i < context.m_nPlayerCount; i++)
	{
		m_vHandCards[i] = m_vIniHandCards[i];
		//		SortHandCards(i);
	}
	m_nTurn = m_nIniTurn;

	bool bRet = true;

	if (!m_vHistory.empty())
	{
		vector<ENGINE_DISCARD_CARDS_INFO> vTemp(m_vHistory);
		m_vHistory.clear();
		for (i = 0; i < (int)vTemp.size(); i++)
		{
			ACTION_RESULT ret_code = Action(vTemp[i]);
			if (ret_code == ACTION_FAIL)
			{
				KERNEL_ASSERT(false);
				bRet = false;
				break;
			}
			ACTION temp;
			temp.nKind = ACTION::TURN_NEXT;
			Action(temp);
		}
	}
	else
	{
		if (context.m_theFirstAction.nKind != ACTION::PASS)
		{
			KERNEL_ASSERT(context.m_theFirstAction.nKind == ACTION::DISCARD &&	
				context.m_theFirstAction.nCount > 0);

			Action(context.m_theFirstAction);

			ACTION temp;
			temp.nKind = ACTION::TURN_NEXT;
			Action(temp);
		}
	}

	return bRet;
}

int CFugouGame::FilterActionFlag(int flag)
{
	if ((flag & ACTION_SIBARI) && (flag & ACTION_8KIRI))
		flag &= ~ACTION_SIBARI;
	return flag;
}
