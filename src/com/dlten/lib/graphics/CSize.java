package com.dlten.lib.graphics;

public class CSize {
    public float w;
    public float h;

    public CSize() {
    }
    public CSize(float initCX, float initCY) { 
    	w = initCX;
    	h = initCY;
    }
    public CSize(CSize initSize) {
    	w=initSize.w;
    	h=initSize.h;
    }
    public CSize(CPoint initPt) {
    	w = initPt.x;
    	h = initPt.y;
    }
    
    public float getWidth() {
        return w;
    }

    public float getHeight() {
        return h;
    }
    
    public String toString() {
        return "<" + w + ", " + h + ">";
    }
    
    public static CSize make(float w, float h) {
        return new CSize(w, h);
    }

    public static CSize zero() {
        return new CSize(0, 0);
    }

    public static boolean equalToSize(CSize s1, CSize s2) {
        return s1.w == s2.w && s1.h == s2.h;
    }
}
