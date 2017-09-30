package com.dlten.lib.graphics;

import static android.opengl.GLES10.GL_CLAMP_TO_EDGE;
import static android.opengl.GLES10.GL_COLOR_ARRAY;
import static android.opengl.GLES10.GL_CW;
import static android.opengl.GLES10.GL_DEPTH_TEST;
import static android.opengl.GLES10.GL_DITHER;
import static android.opengl.GLES10.GL_FASTEST;
import static android.opengl.GLES10.GL_FLOAT;
import static android.opengl.GLES10.GL_LINEAR;
import static android.opengl.GLES10.GL_PERSPECTIVE_CORRECTION_HINT;
import static android.opengl.GLES10.GL_REPLACE;
import static android.opengl.GLES10.GL_SMOOTH;
import static android.opengl.GLES10.GL_TEXTURE0;
import static android.opengl.GLES10.GL_TEXTURE_2D;
import static android.opengl.GLES10.GL_TEXTURE_COORD_ARRAY;
import static android.opengl.GLES10.GL_TEXTURE_ENV;
import static android.opengl.GLES10.GL_TEXTURE_ENV_MODE;
import static android.opengl.GLES10.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES10.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES10.GL_TEXTURE_WRAP_S;
import static android.opengl.GLES10.GL_TEXTURE_WRAP_T;
import static android.opengl.GLES10.GL_TRIANGLE_FAN;
import static android.opengl.GLES10.GL_UNSIGNED_SHORT;
import static android.opengl.GLES10.GL_VERTEX_ARRAY;
import static android.opengl.GLES10.glActiveTexture;
import static android.opengl.GLES10.glBindTexture;
import static android.opengl.GLES10.glClearColor;
import static android.opengl.GLES10.glColorPointer;
import static android.opengl.GLES10.glDeleteTextures;
import static android.opengl.GLES10.glDisable;
import static android.opengl.GLES10.glDisableClientState;
import static android.opengl.GLES10.glDrawElements;
import static android.opengl.GLES10.glEnable;
import static android.opengl.GLES10.glEnableClientState;
import static android.opengl.GLES10.glFrontFace;
import static android.opengl.GLES10.glGenTextures;
import static android.opengl.GLES10.glHint;
import static android.opengl.GLES10.glShadeModel;
import static android.opengl.GLES10.glTexCoordPointer;
import static android.opengl.GLES10.glTexEnvf;
import static android.opengl.GLES10.glTexParameterf;
import static android.opengl.GLES10.glVertexPointer;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import com.dlten.lib.STD;
import com.dlten.lib.file.CResFile;
import com.dlten.lib.opengl.CBeijingGLDCView;
import com.dlten.lib.opengl.CMyGLDCView;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.opengl.GLUtils;

/**
 * <p>Title: CImage class</p>
 *
 * <p>Description: Image split & managing class</p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * @version 1.0
 */
public class CImageTexture extends CImage {
    
    private int m_textureID = -1;
	private CRect m_rectTexture = new CRect();
	
    public CImageTexture() {
    	m_textureID = -1;
    	
		CDCView dc = CImage.getDCView();
    	SC_RES_WIDTH  = dc.getResWidth();
    	SC_RES_HEIGHT = dc.getResHeight();
    }
    
	public CRect getTextureRect() {
		return m_rectTexture;
	}
    
    public int getTextureID() {
    	return m_textureID;
    }
    
    protected int loadResource( Bitmap img ) {
    	
    	unloadResource();
    	
    	super.loadResource(img);

    	int	width  = adjustValue(m_totalWidth);
    	int height = adjustValue(m_totalHeight);

    	if (m_totalWidth != width || m_totalHeight != height) {
	    	Bitmap bmpTexture;
			try {
				Bitmap.Config	config;
				if (img.hasAlpha()) {
//					config = Bitmap.Config.ARGB_4444;
					config = Bitmap.Config.ARGB_8888;
				} else {
					config = Bitmap.Config.RGB_565;
				}
				bmpTexture = Bitmap.createBitmap(width, height, config);
			}
			catch(Exception e) {
				int n = 1;
				n = n + 1;
				return -1;
			}
			
			Canvas canvas = new Canvas(bmpTexture);
			canvas.drawBitmap(img, 0, 0, null);
			img.recycle();
			img = bmpTexture;
    	}

    	STD.ASSERT(m_textureID == -1);
    	
		int textureID = createTexture(img);
		m_textureID = textureID;
		
		initTexture();

		return textureID;
    }
    
    protected void unloadResource() {
    	if (m_textureID != -1) {
    		deleteTexture(m_textureID);
    		m_textureID = -1;
    	}
    	super.unloadResource();
    }
    
    public void drawImage( float fPosX, float fPosY, float fScaleX, float fScaleY, int nAlpha, Matrix matrix ) {
    	float	x = fPosX;
    	float	y = fPosY;
    	float	w = m_totalWidth * fScaleX;
    	float	h = m_totalHeight * fScaleY;
		drawImage(m_textureID, x, y, w, h, nAlpha);
    }
    
    private int adjustValue( int val ) {
    	if      (val <=    2)	val = 2;
    	else if (val <=    4)	val = 4;
    	else if (val <=    8)	val = 8;
    	else if (val <=   16)	val = 16;
    	else if (val <=   32)	val = 32;
    	else if (val <=   64)	val = 64;
    	else if (val <=  128)	val = 128;
    	else if (val <=  256)	val = 256;
    	else if (val <=  512)	val = 512;
    	else if (val <= 1024)	val = 1024;
    	
    	return val;
    }

    private int createTexture(Bitmap img) {

    	int	width  = img.getWidth();
    	int height = img.getHeight();

    	m_rectTexture.left = 0;
    	m_rectTexture.top  = 0;
    	m_rectTexture.width  = (float)m_totalWidth  / (float)width;
    	m_rectTexture.height = (float)m_totalHeight / (float)height;
    	
//		glDisable(GL_DITHER);
//
//        /*
//         * Some one-time OpenGL initialization can be made here
//         * probably based on features of this particular context
//         */
//        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_FASTEST);
//
//        glClearColor(0, 0, 0, 1);
//        glShadeModel(GL_SMOOTH);
//        glEnable(GL_DEPTH_TEST);
        glEnable(GL_TEXTURE_2D);

        /*
         * Create our texture. This has to be done each time the
         * surface is created.
         */

        int[] textures = new int[1];
        glGenTextures(1, textures, 0);

        glBindTexture(GL_TEXTURE_2D, textures[0]);

        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

//        glTexEnvf(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_REPLACE);

		GLUtils.texImage2D(GL_TEXTURE_2D, 0, img, 0);
        
        return textures[0];
    }
    private void deleteTexture(int textureID) {
        int[] textures = new int[1];
        textures[0] = textureID;
        glDeleteTextures(1, textures, 0);
	}
    
    private void initTexture() {
        m_vbb.order(ByteOrder.nativeOrder());
        m_tbb.order(ByteOrder.nativeOrder());
        m_ibb.order(ByteOrder.nativeOrder());
        
        mFVertexBuffer = m_vbb.asFloatBuffer();
        mTexBuffer     = m_tbb.asFloatBuffer();
        mIndexBuffer   = m_ibb.asShortBuffer();
        
    	float left, top, right, bottom;
    	left   = m_rectTexture.left;
    	top    = m_rectTexture.top;
    	right  = m_rectTexture.Right();
    	bottom = m_rectTexture.Bottom();
    	mTexBuffer.put(left);	mTexBuffer.put(top);
    	mTexBuffer.put(left);	mTexBuffer.put(bottom);
    	mTexBuffer.put(right);	mTexBuffer.put(bottom);
    	mTexBuffer.put(right);	mTexBuffer.put(top);
        mTexBuffer.position(0);
        
        mIndexBuffer.put((short) 0);
        mIndexBuffer.put((short) 1);
        mIndexBuffer.put((short) 2);
        mIndexBuffer.put((short) 3);
        mIndexBuffer.position(0);
        
        
        
        m_vbb0.order(ByteOrder.nativeOrder());
        m_cbb0.order(ByteOrder.nativeOrder());

        mFVertexBuffer0 = m_vbb0.asFloatBuffer();
        
        mFVertexBuffer0.put(-0.5f); mFVertexBuffer0.put( 0.5f); mFVertexBuffer0.put(0);
        mFVertexBuffer0.put(-0.5f); mFVertexBuffer0.put(-0.5f); mFVertexBuffer0.put(0);
        mFVertexBuffer0.put( 0.5f); mFVertexBuffer0.put(-0.5f); mFVertexBuffer0.put(0);
        mFVertexBuffer0.put( 0.5f); mFVertexBuffer0.put( 0.5f); mFVertexBuffer0.put(0);
        mFVertexBuffer0.position(0);
    }

    private int SC_RES_WIDTH;
    private int SC_RES_HEIGHT;
	private ByteBuffer m_vbb = ByteBuffer.allocateDirect(4 * 3 * 4);
	private ByteBuffer m_tbb = ByteBuffer.allocateDirect(4 * 2 * 4);
	private ByteBuffer m_ibb = ByteBuffer.allocateDirect(4 * 2);
    private FloatBuffer mFVertexBuffer;
    private FloatBuffer mTexBuffer;
    private ShortBuffer mIndexBuffer;

	private ByteBuffer m_vbb0 = ByteBuffer.allocateDirect(4 * 3 * 4);
	private ByteBuffer m_cbb0 = ByteBuffer.allocateDirect(4 * 4 * 4);
    private FloatBuffer mFVertexBuffer0;
    private FloatBuffer mColorBuffer0;
    public void drawImage( int textureID, float x, float y, float w, float h, int nAlpha) {
        
//        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, textureID);
        
    	float zero = 0;
    	float left, top, right, bottom;

    	left   =  x / SC_RES_WIDTH  - 0.5f;
    	top    = (y / SC_RES_HEIGHT - 0.5f) * -1;
    	right  =  (x + w) / SC_RES_WIDTH  - 0.5f;
    	bottom = ((y + h) / SC_RES_HEIGHT - 0.5f) * -1;

    	mFVertexBuffer.put(left);  	mFVertexBuffer.put(top);   	mFVertexBuffer.put(zero);
    	mFVertexBuffer.put(left);  	mFVertexBuffer.put(bottom);	mFVertexBuffer.put(zero);
    	mFVertexBuffer.put(right); 	mFVertexBuffer.put(bottom);	mFVertexBuffer.put(zero);
    	mFVertexBuffer.put(right); 	mFVertexBuffer.put(top);	mFVertexBuffer.put(zero);
    	mFVertexBuffer.position(0);

        
        glEnable(GL_TEXTURE_2D);
        
//      glFrontFace(GL_CW);
        glEnableClientState(GL_VERTEX_ARRAY);
        glVertexPointer(3, GL_FLOAT, 0, mFVertexBuffer);
        
        glEnableClientState(GL_TEXTURE_COORD_ARRAY);
        glTexCoordPointer(2, GL_FLOAT, 0, mTexBuffer);
        
        glDrawElements(GL_TRIANGLE_FAN, 4, GL_UNSIGNED_SHORT, mIndexBuffer);
        
        if(nAlpha < 255) {
        	float	fAlpha = (float)nAlpha / 255.0f;
            mColorBuffer0 = m_cbb0.asFloatBuffer();
            for(int i = 0; i < 4; i ++) {
                mColorBuffer0.put(0);
                mColorBuffer0.put(0);
                mColorBuffer0.put(0);
                mColorBuffer0.put(1 - fAlpha);
            }
            mColorBuffer0.position(0);

            glEnableClientState(GL_COLOR_ARRAY);
            glColorPointer(4, GL_FLOAT, 0, mColorBuffer0);

	        glDrawElements(GL_TRIANGLE_FAN, 4, GL_UNSIGNED_SHORT, mIndexBuffer);

            glDisableClientState(GL_COLOR_ARRAY);
        }
        
        glDisableClientState(GL_TEXTURE_COORD_ARRAY);
        glDisableClientState(GL_VERTEX_ARRAY);
    }
}

