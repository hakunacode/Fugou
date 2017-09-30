#include <string.h>
#include "FugouRule.h"

CFugouRule::CFugouRule(void)
{
	SetDefault();
}

CFugouRule::~CFugouRule(void)
{
}

bool CFugouRule::Get( int nRuleKind ) const
{
	if (nRuleKind < MAX_RULE_COUNT)
		return (m_bRuleValue[nRuleKind] == 0)? false : true;
	else
		return false;
}

bool CFugouRule::Set( int nRuleKind, bool value )
{
	if (nRuleKind == PLAYERS)
	{
		value = true;//count = 5;
	}

	if (nRuleKind == JOKER_COUNT)
	{
		value = false;//count = 1
	}

	int bTemp;
	if (nRuleKind < MAX_RULE_COUNT)
	{
		bTemp = m_bRuleValue[nRuleKind];
		m_bRuleValue[nRuleKind] = (value == true)? 1 : 0;
	}
	else
	{
		bTemp = 0;
	}


	return (bTemp == 0)? false : true;
}

//bool CFugouRule::GetRawData( bool* pBuf, size_t* pnSize ) const
//{
//	size_t nSize = sizeof(m_bRuleValue[0])*MAX_RULE_COUNT;
//
//	if (pBuf)
//		memcpy(pBuf, m_bRuleValue, nSize);
//
//	*pnSize = nSize;
//	return true;
//}
//
//bool CFugouRule::SetRawData( const bool* pBuf, size_t nSize )
//{
//	size_t nRealSize = sizeof(m_bRuleValue[0])*MAX_RULE_COUNT;
//	if (nRealSize < nSize)
//		return false;
//
//	memcpy(m_bRuleValue, pBuf, nSize);
//
//	return true;
//}

void CFugouRule::SetDefault()
{
	for (int i = 0; i < MAX_RULE_COUNT; i++)
		m_bRuleValue[i] = 0;
}

void CFugouRule::SetAllFalse()
{
	for (int i = 0; i < MAX_RULE_COUNT; i++)
		m_bRuleValue[i] = 0;
}