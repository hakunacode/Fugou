
package com.dlten.lib.graphics;

import com.dlten.lib.STD;
import com.dlten.lib.frmWork.CAnimation;
import com.dlten.lib.frmWork.CEventWnd;

public class CAnim83 extends CAnimation {

	public static final int CREATE_ANIM_LINE_UNIFORM = 0;	// uniform motion (등속운동)
	public static final int CREATE_ANIM_LINE_ACCELER = 1;	// accelerated motion (가속운동)
	public static final int CREATE_ANIM_COUNT = 2;
	
	private static final int ANIM_OBJ_GAME       = 0;
	private static final int ANIM_OBJ_GTFPICTURE = 1;
	private static final int ANIM_OBJ_GTFDATA    = 2;
	private static final int ANIM_OBJ_PANE       = 3;
	private static final int ANIM_OBJ_IMG_OBJ    = 4;
	private static final int ANIM_OBJ_COUNT      = 5;
	
	
	private ANIM_ELEMENT[] m_pAnimation;
	private int m_nFrameCount;
	
	public CAnim83() {
		m_pAnimation = null;
		m_nFrameCount = 0;
		ResetAnimation();
	}
	
	public void ResetAnimation() {
	}
	
	public void SetFrame(int xx) {
		int nCurFrame = GetCurFrameNum();
		
		if( nCurFrame < 0 || nCurFrame >= m_nFrameCount )
			return ;
		
		Object pObj = m_pAnimation[nCurFrame].pAnimObj;
		int x = m_pAnimation[nCurFrame].x;
		int y = m_pAnimation[nCurFrame].y;
		
		if( pObj == null )
			return;
		
		switch( m_pAnimation[nCurFrame].animObjType )
		{
		case ANIM_OBJ_GAME:
			break;
		case ANIM_OBJ_GTFPICTURE:
			{
			    CGtfPicture pAnimObj = (CGtfPicture ) pObj;
			    pAnimObj.SetPos(x, y);
			}
			break;
		case ANIM_OBJ_GTFDATA:
			break;
		case ANIM_OBJ_PANE:
			break;
		case ANIM_OBJ_IMG_OBJ:
			{
				CImgObj pAnimObj = ( CImgObj ) pObj;
				pAnimObj.moveTo(x, y);
			}
		break;
		default:
			break;
		}
	}
	
	public void DrawProc()
	{
		int nCurFrame = GetCurFrameNum();
		
		if( nCurFrame < 0 || nCurFrame >= m_nFrameCount )
			return ;
		
		Object pObj = m_pAnimation[nCurFrame].pAnimObj;
		
		if( pObj == null )
			return;
		
		switch( m_pAnimation[nCurFrame].animObjType )
		{
		case ANIM_OBJ_GAME:
			break;
		case ANIM_OBJ_GTFPICTURE:
			{
			    CGtfPicture pAnimObj = (CGtfPicture ) pObj;
			    pAnimObj.Draw();
			}
			break;
		case ANIM_OBJ_GTFDATA:
			break;
		case ANIM_OBJ_PANE:
			{
//				CTSPane* pPane = (CTSPane *) pObj;
//				pPane->Draw(x, y);
			}
			break;
		case ANIM_OBJ_IMG_OBJ:
			{
				CImgObj pAnimObj = ( CImgObj ) pObj;
				pAnimObj.draw();
			}
		break;
		default:
			break;
		}
	}
	
	int[] GetPos() {
		int nCurFrame = GetCurFrameNum();
		int[] ptRet = new int[] {m_pAnimation[nCurFrame].x, m_pAnimation[nCurFrame].y};
		return ptRet;
	}
	
	public void Create( CEventWnd pParent, int animType, CGtfPicture pImg, int[][] pPtList, int nPtCount,
						int nStartGapFrames, int nFrames, int nEndGapFrames )
	{
		int animObjType = ANIM_OBJ_GTFPICTURE;
		int nParam1 = 0;
		int nParam2 = 0;
		Create(pParent, animType, pImg, animObjType, nParam1, nParam2, pPtList, nPtCount, nStartGapFrames, nFrames, nEndGapFrames);
	}
	
	public void Create( CEventWnd pParent, int animType, CImgObj pImg, int[][] pPtList, int nPtCount,
						int nStartGapFrames, int nFrames, int nEndGapFrames )
	{
		int animObjType = ANIM_OBJ_IMG_OBJ;
		int nParam1 = 0;
		int nParam2 = 0;
		Create(pParent, animType, pImg, animObjType, nParam1, nParam2, pPtList, nPtCount, nStartGapFrames, nFrames, nEndGapFrames);
	}
	public void Create( CEventWnd pParent, int animType, Object pAnimObj, int animObjType, int nParam1, int nParam2,
						int[][] pPtList, int nPtCount, int nStartGapFrames, int nFrames, int nEndGapFrames)
	{
		STD.ASSERT( nPtCount > 1 );
		
		ResetAnimation();
		CreateAnimElements(animType, pAnimObj, animObjType, nParam1, nParam2, pPtList, nPtCount, nStartGapFrames, nFrames, nEndGapFrames);
		
		switch( animType )
		{
		case CREATE_ANIM_LINE_UNIFORM:
			SetLineAnimProps_Uniform( m_pAnimation, nStartGapFrames, pPtList, nPtCount, nFrames );
			break;
		case CREATE_ANIM_LINE_ACCELER:
			SetLineAnimProps_Accelerated( m_pAnimation, nStartGapFrames, pPtList, nPtCount, nFrames );
			break;
		default:
			break;
		}
		
		super.Create( pParent, 0, m_nFrameCount, false, true); // regist to window
	}
	public void CreateAnimElements( int animType, Object pAnimObj, int animObjType, int nParam1, int nParam2,
									int[][] pPtList, int nPtCount, int nStartGapFrames, int nFrames, int nEndGapFrames)
	{
		m_nFrameCount = nStartGapFrames + nFrames + nEndGapFrames;
		m_pAnimation = null;
		m_pAnimation = new ANIM_ELEMENT[m_nFrameCount];
		int i;
		for( i = 0 ; i < m_nFrameCount ; i++ ) {
			m_pAnimation[i] = new ANIM_ELEMENT();
			// m_pAnimation[i].Reset();
		}
		
		for( i = 0 ; i < nStartGapFrames; i++ ) {
			m_pAnimation[i].pAnimObj = pAnimObj;
			m_pAnimation[i].animObjType = animObjType;
			m_pAnimation[i].x = pPtList[0][0];
			m_pAnimation[i].y = pPtList[0][1];
			m_pAnimation[i].nParam1 = nParam1;
			m_pAnimation[i].nParam2 = nParam2;
		}
		for( i = nStartGapFrames ; i < nFrames+nStartGapFrames; i++ ) {
			m_pAnimation[i].pAnimObj = pAnimObj;
			m_pAnimation[i].animObjType = animObjType;
			m_pAnimation[i].x = pPtList[0][0];
			m_pAnimation[i].y = pPtList[0][1];
			m_pAnimation[i].nParam1 = nParam1;
			m_pAnimation[i].nParam2 = nParam2;
		}
		for( i = nFrames+nStartGapFrames ; i < m_nFrameCount; i++ ) {
			m_pAnimation[i].pAnimObj = pAnimObj;
			m_pAnimation[i].animObjType = animObjType;
			m_pAnimation[i].x = pPtList[nPtCount-1][0];
			m_pAnimation[i].y = pPtList[nPtCount-1][1];
			m_pAnimation[i].nParam1 = nParam1;
			m_pAnimation[i].nParam2 = nParam2;
		}
	}
	void SetLineAnimProps_Uniform( ANIM_ELEMENT[] pAnim, int nStartIdx, int[][] pPtList, int nPtCount, int nFrames )
	{
		int[] nArrDistances = new int[nPtCount-1];
		int[] nArrFrames = new int[nPtCount-1];
		int i;
		int nDistX, nDistY;
		
		int nTotalDistance = 0;
		for( i = 0 ; i < nPtCount - 1; i++ ) {
			nDistX = pPtList[i+1][0] - pPtList[i][0];
			nDistY = pPtList[i+1][1] - pPtList[i][1];
			// nArrDistances[i] = static_cast<int>(std::sqrt((nDistX*nDistX)+(nDistY*nDistY)));
			nArrDistances[i] = (nDistX*nDistX) + (nDistY*nDistY);
			nTotalDistance += nArrDistances[i];
		}
		int nFrameSum = 0;
		for( i = nPtCount - 2 ; i > 0; i-- ) {
			if( nTotalDistance == 0 )
				nArrFrames[i] = 0;
			else
				nArrFrames[i] = nArrDistances[i]*nFrames / nTotalDistance;
			nFrameSum += nArrFrames[i];
		}
		nArrFrames[0] = nFrames - nFrameSum;
		
		nFrameSum = 0;
		for( i = 0 ; i < nPtCount - 1; i++ ) {
			for( int j = 0 ; j < nArrFrames[i] ; j++ ) {
				if( nFrameSum >= nFrames ) {
					STD.ASSERT(false);
					continue;
				}
				pAnim[nStartIdx+nFrameSum].x = pPtList[i][0]+(pPtList[i+1][0]-pPtList[i][0])*j/nArrFrames[i];
				pAnim[nStartIdx+nFrameSum].y = pPtList[i][1]+(pPtList[i+1][1]-pPtList[i][1])*j/nArrFrames[i];
				nFrameSum++;
			}
		}
	}
	
	void SetLineAnimProps_Accelerated( ANIM_ELEMENT[] pAnim, int nStartIdx, int[][] pPtList, int nPtCount, int nFrames )
	{
		int[] nArrFrames    = new int[nPtCount-1];
		int	nCount = nPtCount - 1;
		int	nStartFrame = 0;
		int	nStopFrame  = 0;
		int i;
		
		int nFrameSum = 0;
		for( i = nCount-1 ; i > 0; i-- ) {
			nStartFrame = nFrames * (i-1) / nCount;
			nStopFrame  = nFrames * i / nCount;
			nArrFrames[i] = nStopFrame - nStartFrame;
			nFrameSum += nArrFrames[i];
		}
		nArrFrames[0] = nFrames - nFrameSum;
		
		nFrameSum = 0;
		for( i = 0 ; i < nPtCount - 1; i++ ) {
			for( int j = 0 ; j < nArrFrames[i] ; j++ ) {
				if( nFrameSum >= nFrames ) {
					STD.ASSERT(false);
					continue;
				}
				pAnim[nStartIdx+nFrameSum].x = pPtList[i][0]+(pPtList[i+1][0]-pPtList[i][0])*j/nArrFrames[i];
				pAnim[nStartIdx+nFrameSum].y = pPtList[i][1]+(pPtList[i+1][1]-pPtList[i][1])*j/nArrFrames[i];
				nFrameSum++;
			}
		}
	}
	
	
	
	class ANIM_ELEMENT {
	    Object pAnimObj;
	    int animObjType;
	    int x;
	    int y;
	    int nParam1;
	    int nParam2;
	
	    public ANIM_ELEMENT() {
	    	Reset();
	    }
	    
	    public void Reset() {
	        pAnimObj = null;
	        animObjType = 0;
	        x = 0;
	        y = 0;
	        nParam1 = 0;
	        nParam2 = 0;
	    }
	}
	class CGtfPicture {
	    void SetPos( int x, int y ) {}
	    void Draw( int x, int y ) {}
	    void Draw() {}
	}
}
