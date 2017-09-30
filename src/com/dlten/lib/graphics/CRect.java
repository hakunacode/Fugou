package com.dlten.lib.graphics;

public class CRect {
    public float left;
    public float top;
    public float width;
    public float height;


    public CRect() {
    	left = 0;
    	top = 0;
    	width = 0;
    	height = 0;
    }
    public CRect(float l, float t, float w, float h) {
    	left = l;
    	top = t;
    	width = w;
    	height = h;
    }
    public CRect(final CRect srcRect) {
    	left = srcRect.left;
    	width = srcRect.width;
    	top = srcRect.top;
    	height = srcRect.height;
    }
    public CRect(CPoint point, CSize size) {
    	left = point.x;
    	top = point.y;
    	width = size.w;
    	height = size.h;
    }
    public CRect(CPoint topLeft, CPoint bottomRight) {
    	left = topLeft.x;
    	top = topLeft.y;
		width = bottomRight.x - topLeft.x;
		height = bottomRight.y - topLeft.y;
	}
    public float Right() {
    	return left + width;
    }
    public float Bottom() {
    	return top + height;
    }
    public CSize Size() {
    	return new CSize(width, height);
    }
    public CPoint TopLeft() {
    	return new CPoint(left, top);
    }
    public CPoint BottomRight() {
    	return new CPoint(left+width, top+height);
    }
    public CPoint CenterPoint() {
    	return new CPoint(left+width/2, top+height/2);
    }
    public void SwapLeftRight() {
    	left = left+width;
    	width = -width;
    }
    public boolean IsRectEmpty() { 
    	if (width <= 0)
    		return true;
    	if (height <= 0)
    		return true;
    	return false;
    }
    public boolean IsRectNull()
		{ return (left == 0 && width == 0 && top == 0 && height == 0); }
    public boolean PtInRect(CPoint point){
    	if (left > point.x || (left+width) <= point.x)
    		return false;
    	if (top > point.y || (top+height) <= point.y)
    		return false;
    	return true;
    }
    public void SetRect(float l, float t, float w, float h) {
    	left = l;
    	top = t;
    	width = w;
    	height = h;
    }
    public void SetRect(CPoint topLeft, CPoint bottomRight) {
    	SetRect(topLeft.x, topLeft.y, bottomRight.x, bottomRight.y);
    }
    public void SetRectEmpty() {
    	SetRect(0, 0, 0, 0);
    }
    public void CopyRect(CRect rectSrc) {
    	if (rectSrc == null)
    		return;
    	left = rectSrc.left;
    	top = rectSrc.top;
    	width = rectSrc.width;
    	height = rectSrc.height;
    }
    public boolean EqualRect(CRect rect) {
    	if (rect == null)	
    		return false;
    	
    	if (left == rect.left &&
    		top == rect.top &&
    		width == rect.width &&
    		height == rect.height)
    		return true;
    		
    	return false;
    }
    public void InflateRect(float dx, float dy) {
    	left -= dx;
    	top -= dy;
    	width += 2*dx;
    	height += 2*dy;
    }
    public void InflateRect(CSize size) {
    	InflateRect(size.w, size.h);
    }
    public void DeflateRect(float x, float y) {
    	InflateRect(-x, -y);
    }
    public void DeflateRect(CSize size) {
    	InflateRect(-size.w, -size.h);
    }
    public void OffsetRect(float dx, float dy) {
    	left += dx;
    	top += dy;
    }
    public void OffsetRect(CPoint point) {
    	OffsetRect(point.x, point.y);
    }
    public void OffsetRect(CSize size) {
    	OffsetRect(size.w, size.h);
    }
    public boolean IntersectRect(CRect rectSrc1, CRect rectSrc2) {
    	if (rectSrc1 == null || rectSrc2 == null)
    		return false;
    	left = Math.max(rectSrc1.left, rectSrc2.left);
    	float right = Math.min(rectSrc1.Right(), rectSrc2.Right());
    	width = right - left;
    	if (left > right)
    	{
    		SetRectEmpty();
    		return false;
    	}
    	top = Math.max(rectSrc1.top, rectSrc2.top);
    	float bottom = Math.min(rectSrc1.Bottom(), rectSrc2.Bottom());
    	height = bottom - top;
    	if (top > bottom)
    	{
    		SetRectEmpty();
    		return false;
    	}
    	return true;
    }
    public boolean UnionRect(CRect rectSrc1, CRect rectSrc2) {
    	if (rectSrc1 == null || rectSrc2 == null)
    		return false;
    	left = Math.max(Math.max(rectSrc1.Right(), rectSrc2.Right()), Math.max(rectSrc1.left, rectSrc2.left));
    	float right = Math.max(Math.max(rectSrc1.left, rectSrc2.left), Math.max(rectSrc1.Right(), rectSrc2.Right()));
    	width = right - left;
    	top = Math.max(Math.max(rectSrc1.Bottom(), rectSrc2.Bottom()), Math.max(rectSrc1.top, rectSrc2.top));
    	float bottom = Math.max(Math.max(rectSrc1.top, rectSrc2.top), Math.max(rectSrc1.Bottom(), rectSrc2.Bottom()));
    	height = bottom - top;
    	return true;
    }
	
	// out-of-line CRect, CSize, etc. helpers
	
    public void NormalizeRect() {
		if (width < 0) {
			left = left + width;
			width = -width;
		}
		if (height < 0) {
			top = top + height;
			height = -height;
		}
	}
    public void InflateRect(CRect rect)	{
		left -= rect.left;
		top -= rect.top;
		width += (rect.left + 2*rect.width);
		height += (rect.top + 2*rect.height);
	}
    public void InflateRect(float l, float t, float r, float b)	{
		left -= l;
		top -= t;
		width += (l+r);
		height += (t+b);
	}
    public void DeflateRect(CRect rect)	{
		left += rect.left;
		top += rect.top;
		width -= (rect.left + 2*rect.width);
		height -= (rect.top + 2*rect.height);
	}
    public void DeflateRect(float l, float t, float r, float b)	{
		left += l;
		top += t;
		width -= (l+r);
		height -= (t+b);
	}
}
