
package com.dlten.lib.frmWork;

import com.dlten.lib.graphics.CImgObj;
import com.dlten.lib.graphics.CPoint;
import com.dlten.lib.graphics.CRect;

public class CToggleButton extends CButton {
	private	CEventWnd m_parent = null;
	
	private	CImgObj m_nor = null;
	private	CImgObj m_foc = null;
	private	CImgObj m_dis = null;
	
	private	CPoint	m_pos = new CPoint();
	private CRect	m_rect = new CRect();
	
	private boolean	m_bEnable  = false;
	private boolean m_bVisible = false;
	private int	m_state   = BS_NORMAL;
	private int	m_command = CMD_NONE;
	
    public CToggleButton() {
    }
    
    public CToggleButton(CEventWnd parent, CPoint point, CImgObj nor, CImgObj foc, CImgObj dis) {
    	create(parent, point, nor, foc, dis);
    }

    public CToggleButton(CEventWnd parent, CImgObj nor, CImgObj foc, CImgObj dis) {
    	create(parent, new CPoint(0, 0), nor, foc, dis);
    }
    
    public void create(CEventWnd parent, String norName, String focName, String disName) {
    	CImgObj nor = new CImgObj( norName );
    	CImgObj foc = new CImgObj( focName );
    	CImgObj dis = new CImgObj( disName );
    	
    	create(parent, new CPoint(0, 0), nor, foc, dis);
    }
    
    public void create(CEventWnd parent, CPoint point, String norName, String focName, String disName) {
    	CImgObj nor = new CImgObj( norName );
    	CImgObj foc = new CImgObj( focName );
    	CImgObj dis = new CImgObj( disName );
    	
    	create(parent, point, nor, foc, dis);
    }
    
    public void create(CEventWnd parent, CPoint point, CImgObj nor, CImgObj foc, CImgObj dis) {
    	m_nor = nor;
    	m_foc = foc;
    	m_dis = dis;
    	
    	m_parent = parent;
    	m_parent.AddButton(this);
    	
    	setPoint(point);
    	setEnable(true);
    	setVisible(true);
    	setNormal();
    }
    
    public void destroy() {
    	m_parent.RemoveButton(this);
    	
//    	m_nor.unload();
//    	m_foc.unload();
//    	m_dis.unload();
    	
    	m_pos = null;
    	m_nor = null;
    	m_foc = null;
    	m_dis = null;
    	
    	m_pos = null;
    	m_rect = null;
    }
    
    public void Draw() {
    	if ( !getVisible() )
    		return;
    	
    	if ( !isEnable() ) {
    		m_dis.draw(m_pos);
    		return;
    	}
    	
    	switch (m_state) {
    	case BS_NORMAL:		m_nor.draw(m_pos);	break;
    	case BS_FOCUS:		m_foc.draw(m_pos);	break;
    	default:			break;
    	}
    }
    
    public CPoint getPoint() {
    	return m_pos;
    }

    public void setPoint(float x, float y) {
    	m_pos.x = x;
    	m_pos.y = y;
    	setRect();
    }
    
    public void setPoint(CPoint point) {
    	m_pos = point;
    	setRect();
    }

    public CRect getRect() {
    	return m_rect;
    }

    private void setRect() {
    	m_rect.left   = m_pos.x;
    	m_rect.top    = m_pos.y;
    	m_rect.width  = m_nor.getSizeX();
    	m_rect.height = m_nor.getSizeY();
    }

    public boolean isInside(CPoint pt) {
    	return m_rect.PtInRect(pt);
    }

    public boolean isEnable() {
    	return m_bEnable;
    }
    
    public void setEnable(boolean enable) {
    	m_bEnable = enable;
    }
    
    public boolean getVisible() {
    	return m_bVisible;
    }
    
    public void setVisible(boolean bVisible) {
    	m_bVisible = bVisible;
    }
    
    public int getState() {
    	return m_state;
    }
    
    public void setState(int state) {
    	m_state = state;
    }
    
    public void setNormal() {
    	m_state = BS_NORMAL;
    }
    
    public void setFocus() {
   		m_state = BS_FOCUS;
    }
    
    public int getCommand() {
    	return m_command;
    }
    
    public void setCommand(int cmd) {
    	m_command = cmd;
    }
    
    public static final int
    	CMD_NONE   = -1,
    	
    	BS_NORMAL  = 1,
    	BS_FOCUS   = 2;
}
