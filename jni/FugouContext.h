#ifndef __FUGOUCONTEXT__
#define __FUGOUCONTEXT__

#include "FugouKernel.h"
#include "FugouRule.h"

#include <vector>
using namespace std;

//discard cards info
struct ENGINE_DISCARD_CARDS_INFO
{
	int nFrom;
	DISCARD_CARDS_KIND	nKind;
	int nCount;
	int nCards[4];
};

class CFugouGame;

struct FUGOU_CONTEXT
{
	FUGOU_CONTEXT();

	bool	SetData(char* pszData, int size);

	enum {
		MAX_HAND_CARDS = 20,
		MAX_HISTORY_COUNT = 200
	};

	int				m_nPlayerCount;
	int				m_nPlayerKind[MAX_PLAYERS];
	int				m_vIniHandCards[MAX_PLAYERS][MAX_HAND_CARDS];
	int				m_nIniTurn;
	int				m_nHistoryCount;
	ENGINE_DISCARD_CARDS_INFO	m_vHistory[MAX_HISTORY_COUNT];

	CFugouRule		m_Rule;

	ACTION			m_theFirstAction;//for Engine Test
};

#endif