#include "FugouThink.h"

CFugouRule& CFugouKernel::GetRule() const
{
	return m_Rule;
}

CFugouKernel::CFugouKernel()
{

}

CFugouKernel::~CFugouKernel()
{

}

CFugouKernel* CFugouKernel::Create()
{
	return new CFugouThink;
}

void CFugouKernel::Free( CFugouKernel* p )
{
	delete p;
}