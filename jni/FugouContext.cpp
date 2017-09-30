#include "FugouContext.h"
#include "FugouGame.h"
#include "FugouRule.h"

typedef size_t	MY_SIZE_T;

FUGOU_CONTEXT::FUGOU_CONTEXT()
{
	m_theFirstAction.nKind = ACTION::PASS;
}

bool IsOneOf(char c, char* ones);
void CommentProcess(char* buf);
void RemoveSpace(char* buf, int size);
int GetToken(char* buf, char* token, int def);
int GetCardID(char* p);
void GetHandCard(char* buf, char* pStartToken, int *pBuf);
void GetMarksData(char* buf, char* pStartToken, vector<int>&v);

//load from string FOR DEBUG
bool FUGOU_CONTEXT::SetData( char* pszData, int size )
{
	pszData[size] = '\0';//sorry

	CommentProcess(pszData);
	RemoveSpace(pszData, size);

	//Hand Cards
	GetHandCard(pszData, (char*)"HAND0=", m_vIniHandCards[0]);
	GetHandCard(pszData, (char*)"HAND1=", m_vIniHandCards[1]);
	GetHandCard(pszData, (char*)"HAND2=", m_vIniHandCards[2]);
	GetHandCard(pszData, (char*)"HAND3=", m_vIniHandCards[3]);
	GetHandCard(pszData, (char*)"HAND4=", m_vIniHandCards[4]);

	m_nPlayerKind[0] = DAIFUGOU;
	m_nPlayerKind[1] = FUGOU;
	m_nPlayerKind[2] = HEIMIN;
	m_nPlayerKind[3] = HINMIN;
	m_nPlayerKind[4] = DAIHINMIN;

	m_Rule.SetDefault();
	m_Rule.Set(CFugouRule::REVOLUTION,         GetToken(pszData, (char*)"REVOLUTION=", 0)!=0);
	m_Rule.Set(CFugouRule::PRESSURE,           GetToken(pszData, (char*)"PRESSURE=", 0)!=0);
	m_Rule.Set(CFugouRule::SPADE_3,            GetToken(pszData, (char*)"SPADE_3=", 0)!=0);
	m_Rule.Set(CFugouRule::SIBARI,             GetToken(pszData, (char*)"SIBARI=", 0)!=0);
	m_Rule.Set(CFugouRule::ONLY_8_TOP,         GetToken(pszData, (char*)"ONLY_8_TOP=", 0)!=0);
	m_Rule.Set(CFugouRule::FORBID_JOKER_AGARI, GetToken(pszData, (char*)"FORBID_JOKER_AGARI=", 0)!=0);
	m_Rule.Set(CFugouRule::FORBID_2_AGARI,     GetToken(pszData, (char*)"FORBID_2_AGARI=", 0)!=0);

	vector<int> v;
	GetMarksData(pszData, (char*)"DISCARD=", v);

	m_nPlayerCount = 5;
	m_nHistoryCount = 0;
	m_nIniTurn = 0;
	m_theFirstAction.nKind = ACTION::PASS;
	if (!v.empty())
	{
		m_nIniTurn = v[0];
		if (v.size() > 1)
		{
			ACTION action;
			action.nPlayer = m_nIniTurn;
			action.nKind = ACTION::DISCARD;
			action.nCount = (int)v.size() - 1;
			for (int i = 1; i < (int)v.size(); i++)
				action.nIndices[i-1] = v[i];
			//save the first action
			m_theFirstAction = action;
		}
	}

	return true;
}

bool IsOneOf(char c, char* ones)
{
	if (ones == NULL)
		return false;

	if (c == '\0')
		return false;

	unsigned int i;
	for (i = 0; i < strlen(ones); i++)
	{
		if (ones[i] == c)
			return true;
	}

	return false;
}

void CommentProcess(char* buf)
{
	if (buf == NULL)
		return;

	char* p;
	while ((p = strchr(buf, '#')) != NULL)
	{
		while (IsOneOf(*p, (char*)"\r\n") != true)
		{
			if (*p == '\0')
				break;

			*p++ = ' ';
		}
	}
}

void RemoveSpace(char* buf, int size)
{
	for (int i = 0; i < size; i++)
	{
		if (IsOneOf(buf[i], (char*)" \t"))
		{
			memmove(&buf[i], &buf[i+1], size-i-1);
			i--;
			size--;
		}
	}
}

int GetToken(char* buf, char* token, int def)
{
	char* p = strstr(buf, token);
	if (p == NULL)
		return def;
	p += strlen(token);

	return atoi(p);
}

struct PAI_TOKEN
{
	char szToken[8];
	int	id;
};

int GetCardID(char* p)
{
	static PAI_TOKEN pai_token[] = 
	{
		{"1S", MakeCard(SPADE, CARD_A)},  {"2S", MakeCard(SPADE, CARD_2)}, {"3S", MakeCard(SPADE, CARD_3)},
		{"4S", MakeCard(SPADE, CARD_4)},  {"5S", MakeCard(SPADE, CARD_5)}, {"6S", MakeCard(SPADE, CARD_6)},
		{"7S", MakeCard(SPADE, CARD_7)},  {"8S", MakeCard(SPADE, CARD_8)}, {"9S", MakeCard(SPADE, CARD_9)},
		{"0S", MakeCard(SPADE, CARD_10)}, {"JS", MakeCard(SPADE, CARD_J)}, {"QS", MakeCard(SPADE, CARD_Q)},
		{"KS", MakeCard(SPADE, CARD_K)},

		{"1H", MakeCard(HEART, CARD_A)},  {"2H", MakeCard(HEART, CARD_2)}, {"3H", MakeCard(HEART, CARD_3)},
		{"4H", MakeCard(HEART, CARD_4)},  {"5H", MakeCard(HEART, CARD_5)}, {"6H", MakeCard(HEART, CARD_6)},
		{"7H", MakeCard(HEART, CARD_7)},  {"8H", MakeCard(HEART, CARD_8)}, {"9H", MakeCard(HEART, CARD_9)},
		{"0H", MakeCard(HEART, CARD_10)}, {"JH", MakeCard(HEART, CARD_J)}, {"QH", MakeCard(HEART, CARD_Q)},
		{"KH", MakeCard(HEART, CARD_K)},

		{"1D", MakeCard(DIAMOND, CARD_A)},  {"2D", MakeCard(DIAMOND, CARD_2)}, {"3D", MakeCard(DIAMOND, CARD_3)},
		{"4D", MakeCard(DIAMOND, CARD_4)},  {"5D", MakeCard(DIAMOND, CARD_5)}, {"6D", MakeCard(DIAMOND, CARD_6)},
		{"7D", MakeCard(DIAMOND, CARD_7)},  {"8D", MakeCard(DIAMOND, CARD_8)}, {"9D", MakeCard(DIAMOND, CARD_9)},
		{"0D", MakeCard(DIAMOND, CARD_10)}, {"JD", MakeCard(DIAMOND, CARD_J)}, {"QD", MakeCard(DIAMOND, CARD_Q)},
		{"KD", MakeCard(DIAMOND, CARD_K)},

		{"1C", MakeCard(CLUB, CARD_A)},  {"2C", MakeCard(CLUB, CARD_2)}, {"3C", MakeCard(CLUB, CARD_3)},
		{"4C", MakeCard(CLUB, CARD_4)},  {"5C", MakeCard(CLUB, CARD_5)}, {"6C", MakeCard(CLUB, CARD_6)},
		{"7C", MakeCard(CLUB, CARD_7)},  {"8C", MakeCard(CLUB, CARD_8)}, {"9C", MakeCard(CLUB, CARD_9)},
		{"0C", MakeCard(CLUB, CARD_10)}, {"JC", MakeCard(CLUB, CARD_J)}, {"QC", MakeCard(CLUB, CARD_Q)},
		{"KC", MakeCard(CLUB, CARD_K)},

		{"JK", CARD_JOKER},
	};

	for (int i = 0; i < sizeof(pai_token) / sizeof(pai_token[0]); i++)
	{
		if (strncmp(p, pai_token[i].szToken, 2) == 0)
		{
			return pai_token[i].id;
		}
	}

	return -1;
}

void GetHandCard(char* buf, char* pStartToken, int* pBuf)
{
	char* pYamaStart = strstr(buf, pStartToken);
	if (pYamaStart == NULL)
		return;

	pYamaStart += strlen(pStartToken);

	char* pYama = pYamaStart;
	int id;

	while (IsOneOf(*pYama, (char*)"\r\n\t,"))
		pYama++;

	int save_index = 0;
	while((id = GetCardID(pYama)) > 0)
	{
		pBuf[save_index++] = id;
		pYama += 2;
		if ((*pYama == 0) || (IsOneOf(*pYama, (char*)"=")))
			break;

		char c = *pYama;
		if (c >= 'A' && c <= 'Z')
			break;

		if (c >= 'a' && c <= 'z')
			break;

		while (IsOneOf(*pYama, (char*)"\r\n\t,"))
			pYama++;
	}
	pBuf[save_index] = -1;
}

void GetMarksData(char* buf, char* pStartToken, vector<int>&v)
{
	char* pMarksStart = strstr(buf, pStartToken);
	if (pMarksStart == NULL)
		return;

	v.clear();
	char *pMarks = pMarksStart + strlen(pStartToken);

	while (IsOneOf(*pMarks, (char*)"\r\n\t,"))
		pMarks++;

	for (int i = 0; i < 4; i++)
	{
		if (*pMarks == '\0' || *pMarks == '\r' || *pMarks == '\n')
			break;

		v.push_back(atoi(pMarks));
		while (IsOneOf(*pMarks, (char*)"0123456789"))
			pMarks++;
		while (IsOneOf(*pMarks, (char*)","))
			pMarks++;
	}
}
