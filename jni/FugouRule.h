#pragma once

#include <stdlib.h>

class CFugouRule
{
public:
	CFugouRule(void);
	~CFugouRule(void);

	enum{
		REVOLUTION,			//革命
		PRESSURE,			//都落ち
		SPADE_3,			//スペ３
		SIBARI,				//しばり
		ONLY_8_TOP,			//8切り
		FORBID_JOKER_AGARI,	//ジョーカーあがり禁止								
		FORBID_2_AGARI,		//２あがり禁止								
 		PLAYERS,			//プレイ人数　5人(false)　or 　4人(true)
		JOKER_COUNT,		//ジョーカー1枚(false)　or 　2枚(true)
// 		SEQUENCE,			//階段
// 		SEQ_REVOLUTION,		//階段革命
// 		J_BACK,				//イレブンバック（11バック）
// 		SEQ_8_TOP,			//8切り階段
		MAX_RULE_COUNT,
	};

	void SetDefault();
	void SetAllFalse();

	bool Get(int nRuleKind) const;
	bool Set(int nRuleKind, bool value);//The return value is before-state value.

//	bool GetRawData(bool* pBuf, size_t* pnSize) const;	//The return value is buffer size. if pBuf=NULL, it return only size value.
//	bool SetRawData(const bool* pBuf, size_t nSize);

private:
	int	m_bRuleValue[MAX_RULE_COUNT];
};
