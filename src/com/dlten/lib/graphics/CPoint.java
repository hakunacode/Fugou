package com.dlten.lib.graphics;

public class CPoint {
    public float  x;
    public float  y;


    public CPoint() {
    	x = 0;
    	y = 0;
   	}
    public CPoint(float initX, float initY) {
    	x = initX;
    	y = initY;
    }
    public CPoint(int initX, int initY) {
    	x = (float)initX;
    	y = (float)initY;
    }
    public CPoint(CPoint initPt) {
    	x = initPt.x;
    	y = initPt.y;
    }
    public CPoint(CSize initSize) {
    	x = initSize.w;
    	y = initSize.h;
    }
    public void Offset(float xOffset, float yOffset) {
    	x += xOffset;
    	y += yOffset;
    }
    public void Offset(int xOffset, int yOffset) {
    	x += (float)xOffset;
    	y += (float)yOffset;
    }
    public void Offset(CPoint point) {
    	x += point.x;
    	y += point.y;
    }
    public void Offset(CSize size) {
    	x += size.w;
    	y += size.h;
    }
}
