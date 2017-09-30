package com.dlten.lib.opengl;

import static android.opengl.GLES10.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGL11;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL;
import javax.microedition.khronos.opengles.GL10;

import com.dlten.lib.STD;
import com.dlten.lib.frmWork.CEventWnd;
import com.dlten.lib.graphics.CBmpDCView;
import com.dlten.lib.graphics.CBmpManager;
import com.dlten.lib.graphics.CDCView;
import com.dlten.lib.graphics.CImageTexture;
import com.dlten.lib.graphics.CImgObj;
import com.dlten.lib.graphics.CRect;
import com.ssj.fugou.Globals;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.opengl.GLDebugHelper;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public class CBeijingBmpGLDCView extends CBmpDCView {
    
    private float m_fAlpha;
    
    public CBeijingBmpGLDCView(Context context) {
        super(context);

        initCBaseView();
    }
    public CBeijingBmpGLDCView( Context context, AttributeSet attrs ) {
		super( context, attrs );

		initCBaseView();
	}
    private void initCBaseView() {
        // CImgObj.SetMode(CImgObj.MODE_TEXTURE);
        
        GLThread_Constructor();

        m_vbb.order(ByteOrder.nativeOrder());
        m_tbb.order(ByteOrder.nativeOrder());
        m_ibb.order(ByteOrder.nativeOrder());

        m_vbb0.order(ByteOrder.nativeOrder());
        m_cbb0.order(ByteOrder.nativeOrder());
        m_ibb0.order(ByteOrder.nativeOrder());

        mFVertexBuffer0 = m_vbb0.asFloatBuffer();
        mIndexBuffer0   = m_ibb0.asShortBuffer();
        
        mFVertexBuffer0.put(-0.5f); mFVertexBuffer0.put( 0.5f); mFVertexBuffer0.put(0);
        mFVertexBuffer0.put(-0.5f); mFVertexBuffer0.put(-0.5f); mFVertexBuffer0.put(0);
        mFVertexBuffer0.put( 0.5f); mFVertexBuffer0.put(-0.5f); mFVertexBuffer0.put(0);
        mFVertexBuffer0.put( 0.5f); mFVertexBuffer0.put( 0.5f); mFVertexBuffer0.put(0);
        mFVertexBuffer0.position(0);
        
        mIndexBuffer0.put((short)0);
        mIndexBuffer0.put((short)1);
        mIndexBuffer0.put((short)2);
        mIndexBuffer0.put((short)3);
        mIndexBuffer0.position(0);
    }
    CImageTexture	m_img = new CImageTexture();
    public void prepareDC() {
    	super.prepareDC();
        
        GLThread_run_start();
    }
    public void releaseDC() {
    	GLThread_run_end();
    }
    
    //////////////////////////////////////////////////////////////////
    // All of UI callback functions
    ////////////////////////////////////////////////////////////////// 

	// implementing update.
    synchronized public final void update( CEventWnd pWnd ) {
    	if( pWnd == null )
    		return;

    	try {
        	GLThread_run_loop();

        	if (surfaceInitialized()) {
        		clear();
        		
                glActiveTexture(GL_TEXTURE0);
        		pWnd.DrawPrevProc();
        		pWnd.DrawProcess();

                m_img.load(m_bmpDblBuffer);
                m_img.drawImage(0, 0, 1, 1, 255, null);
                
//        		updateGraphics();
        	}
        } catch (Exception e) {
        	STD.printStackTrace(e);
        }
    }
    
    @Override
	public void surfaceChanged( SurfaceHolder holder, int format, int w, int h ) {
    	GLThread_onWindowResize(w, h);
	}
	@Override
	public void surfaceCreated( SurfaceHolder holder ) {
		GLThread_surfaceCreated();
	}
	@Override
	public void surfaceDestroyed( SurfaceHolder holder ) {
    	GLThread_surfaceDestroyed();
	}
    @Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		// TODO Auto-generated method stub
		super.onWindowFocusChanged(hasWindowFocus);
		GLThread_onWindowFocusChanged(hasWindowFocus);
	}


    
    
    //=======================================================================
	private class EglHelper {
		public EglHelper() {

		}

		/**
		* Initialize EGL for a given configuration spec.
		* 
		* @param configSpec
		*/
		public void start(int[] configSpec) {
			/*
			* Get an EGL instance
			*/
			mEgl = (EGL10) EGLContext.getEGL();

			/*
			* Get to the default display.
			*/
			mEglDisplay = mEgl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);

			/*
			* We can now initialize EGL for that display
			*/
			int[] version = new int[2];
			mEgl.eglInitialize(mEglDisplay, version);

			EGLConfig[] configs = new EGLConfig[1];
			int[] num_config = new int[1];
			mEgl.eglChooseConfig(mEglDisplay, configSpec, configs, 1,
			num_config);
			mEglConfig = configs[0];

			/*
			* Create an OpenGL ES context. This must be done only once, an
			* OpenGL context is a somewhat heavy object.
			*/
			mEglContext = mEgl.eglCreateContext(mEglDisplay, mEglConfig,
			EGL10.EGL_NO_CONTEXT, null);

			mEglSurface = null;
		}

		/*
		* Create and return an OpenGL surface
		*/
		public GL createSurface(SurfaceHolder holder) {
			/*
			* The window size has changed, so we need to create a new
			* surface.
			*/
			if (mEglSurface != null) {

				/*
				* Unbind and destroy the old EGL surface, if there is one.
				*/
				mEgl.eglMakeCurrent(mEglDisplay, EGL10.EGL_NO_SURFACE,
				EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
				mEgl.eglDestroySurface(mEglDisplay, mEglSurface);
			}

			/*
			* Create an EGL surface we can render into.
			*/
			mEglSurface = mEgl.eglCreateWindowSurface(mEglDisplay,
			mEglConfig, holder, null);

			/*
			* Before we can issue GL commands, we need to make sure the
			* context is current and bound to a surface.
			*/
			mEgl.eglMakeCurrent(mEglDisplay, mEglSurface, mEglSurface,
			mEglContext);


			GL gl = mEglContext.getGL();
			//if (mGLWrapper != null) {
			//	gl = mGLWrapper.wrap(gl);
			//}
			return gl;
		}

		/**
		* Display the current render surface.
		* 
		* @return false if the context has been lost.
		*/
		public boolean swap() {
			mEgl.eglSwapBuffers(mEglDisplay, mEglSurface);

			/*
			* Always check for EGL_CONTEXT_LOST, which means the context and
			* all associated data were lost (For instance because the device
			* went to sleep). We need to sleep until we get a new surface.
			*/
			return mEgl.eglGetError() != EGL11.EGL_CONTEXT_LOST;
		}

		public void finish() {
			if (mEglSurface != null) {
				mEgl.eglMakeCurrent(mEglDisplay, EGL10.EGL_NO_SURFACE,
				EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
				mEgl.eglDestroySurface(mEglDisplay, mEglSurface);
				mEglSurface = null;
			}
			if (mEglContext != null) {
				mEgl.eglDestroyContext(mEglDisplay, mEglContext);
				mEglContext = null;
			}
			if (mEglDisplay != null) {
				mEgl.eglTerminate(mEglDisplay);
				mEglDisplay = null;
			}
		}

		EGL10 mEgl;
		EGLDisplay mEglDisplay;
		EGLSurface mEglSurface;
		EGLConfig mEglConfig;
		EGLContext mEglContext;
	}

	// GLThread
	private volatile boolean mDone;
	private boolean mPaused;
	private boolean mHasFocus;
	private boolean mHasSurface;
	private boolean mContextLost;
	private int mWidth;
	private int mHeight;
	//private Renderer mRenderer;
//	private ArrayList<Runnable> mEventQueue = new ArrayList<Runnable>();
	private EglHelper mEglHelper;

	// GLSurfaceView
	private static final Semaphore sEglSemaphore = new Semaphore(1);
	private boolean mSizeChanged = true;
	//private GLThread mGLThread;
	//private GLWrapper mGLWrapper;


	public void GLThread_surfaceCreated() {
		synchronized (this) {
			mHasSurface = true;
			mContextLost = false;
			notify();
		}
	}

	public void GLThread_surfaceDestroyed() {
		synchronized (this) {
			mHasSurface = false;
			notify();
		}
	}

	public void GLThread_onWindowResize(int w, int h) {
		synchronized (this) {
			mWidth = w;
			mHeight = h;
			mSizeChanged = true;
		}
	}

	public void GLThread_onWindowFocusChanged(boolean hasFocus) {
//		synchronized (this) {
			mHasFocus = true;
//			mHasFocus = hasFocus;
//			if (mHasFocus == true) {
//				notify();
//			}
//		}
	}

	public void GLThread_Constructor(/*Renderer renderer*/) {
		//super();
		mDone = false;
		mWidth = 0;
		mHeight = 0;
		//mRenderer = renderer;
		//setName("GLThread");
	}

	private boolean GLThread_needToWait() {
		return (mPaused || ( !mHasFocus) || ( !mHasSurface) || mContextLost)
			&& ( !mDone);
	}
	
	public void GLThread_run_start() {
		try {
			try {
				sEglSemaphore.acquire();
			}
			catch (InterruptedException e) {
				return;
			}
		}
		catch (Exception e) {
			// fall thru and exit normally
			return;
		}

		mEglHelper = new EglHelper();
		/*
		* Specify a configuration for our opengl session and grab the
		* first configuration that matches is
		*/
		mEglHelper.start(null);

		GLThread_local_gl = null;
		GLThread_local_tellRendererSurfaceCreated = true;
		GLThread_local_tellRendererSurfaceChanged = true;
	}
	
	public void GLThread_run_end() {
		mEglHelper.finish();
		sEglSemaphore.release();
	}
	
	public void GLThread_run_loop() throws InterruptedException {
		if(mDone)
			return;

		/*
		* Update the asynchronous state (window size)
		*/
		int w, h;
		boolean changed;
		boolean needStart = false;
		synchronized (this) {
			//Runnable r;
			//while ((r = getEvent()) != null) {
			//	r.run();
			//}
			if (mPaused) {
				mEglHelper.finish();
				needStart = true;
			}
			if (GLThread_needToWait()) {
				while (GLThread_needToWait()) {
					wait();
				}
			}
			if (mDone) {
				return;
			}
			changed = mSizeChanged;
			w = mWidth;
			h = mHeight;
			mSizeChanged = false;
		}
		if (needStart) {
			mEglHelper.start(null);
			GLThread_local_tellRendererSurfaceCreated = true;
			changed = true;
		}
		if (changed) {
			GLThread_local_gl = (GL10) mEglHelper.createSurface(m_holder);
			GLThread_local_tellRendererSurfaceChanged = true;
		}
		if (GLThread_local_tellRendererSurfaceCreated) {
			setCamera();
			GLThread_local_tellRendererSurfaceCreated = false;
		}
		if (GLThread_local_tellRendererSurfaceChanged) {
			GLThread_local_tellRendererSurfaceChanged = false;
		}
		if ((w > 0) && (h > 0)) {
			/* draw a frame here */
			//*********mRenderer.drawFrame(GLThread_local_gl);

			/*
			* Once we're done with GL, we need to call swapBuffers()
			* to instruct the system to display the rendered frame
			*/
			mEglHelper.swap();
		}
	}

	private GL10 GLThread_local_gl = null;
	private boolean GLThread_local_tellRendererSurfaceCreated;
	private boolean GLThread_local_tellRendererSurfaceChanged;
	
	public boolean surfaceInitialized() {
		return (GLThread_local_gl == null) ? false : true;
	}

	private Bitmap m_CurTextureBitmap;
	private Canvas m_CurTextureCanvas;
	public int createTexture(int width, int height) {
		try {
			m_CurTextureBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);//RGB_565, ARGB_4444, ARGB_8888, ALPHA_8
		}
		catch(Exception e) {
			int n = 1;
			n = n + 1;
		}
		m_CurTextureCanvas = new Canvas(m_CurTextureBitmap);

		glDisable(GL_DITHER);

        /*
         * Some one-time OpenGL initialization can be made here
         * probably based on features of this particular context
         */
        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_FASTEST);

        glClearColor(0, 0, 0, 1);
        glShadeModel(GL_SMOOTH);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_TEXTURE_2D);

        /*
         * Create our texture. This has to be done each time the
         * surface is created.
         */

        int[] textures = new int[1];
        glGenTextures(1, textures, 0);

        int TextureID = textures[0];
        glBindTexture(GL_TEXTURE_2D, TextureID);

        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

        glTexEnvf(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_REPLACE);

        return TextureID;
	}

	public void deleteTexture(int textureID) {
        int[] textures = new int[1];
        textures[0] = textureID;
        glDeleteTextures(1, textures, 0);
	}

	private void setCamera() {
        glViewport(m_nDblOffsetX, m_nDblOffsetY, SC_WIDTH-2*m_nDblOffsetX, SC_HEIGHT-2*m_nDblOffsetY);

        /*
        * Set our projection matrix. This doesn't have to be done
        * each time we draw, but usually a new projection needs to
        * be set when the viewport is resized.
        */

        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glFrustumf(-0.25f, 0.25f, -0.25f, 0.25f, 2.5f, 5.5f);
	}
	
	// private void setTexture(int nTextureID)
	public void setTexture(int nTextureID)
	{
        glBindTexture(GL_TEXTURE_2D, nTextureID);
	}

	
	private static final int MAX_RECT_COUNT = 200;
	private ByteBuffer m_vbb = ByteBuffer.allocateDirect(MAX_RECT_COUNT * 4 * 3 * 4);
	private ByteBuffer m_tbb = ByteBuffer.allocateDirect(MAX_RECT_COUNT * 4 * 2 * 4);
	private ByteBuffer m_ibb = ByteBuffer.allocateDirect(MAX_RECT_COUNT * 4 * 2);
    private FloatBuffer mFVertexBuffer;
    private FloatBuffer mTexBuffer;
    private ShortBuffer mIndexBuffer;

	private ByteBuffer m_vbb0 = ByteBuffer.allocateDirect(4 * 3 * 4);
	private ByteBuffer m_cbb0 = ByteBuffer.allocateDirect(4 * 4 * 4);
	private ByteBuffer m_ibb0 = ByteBuffer.allocateDirect(4 * 2);
    private FloatBuffer mFVertexBuffer0;
    private FloatBuffer mColorBuffer0;
    private ShortBuffer mIndexBuffer0;

    public void clear() {
        /*
         * By default, OpenGL enables features that improve quality
         * but reduce performance. One might want to tweak that
         * especially on software renderer.
         */
        glDisable(GL_DITHER);

        glTexEnvx(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);

        /*
         * Usually, the first thing one might want to do is to clear
         * the screen. The most efficient way of doing this is to use
         * glClear().
         */

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        /*
         * Now we're ready to draw some 3D objects
         */

        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();

        GLU.gluLookAt(GLThread_local_gl, 0, 0, 5, 0, 0, 0, 0, 1, 0);

        glEnableClientState(GL_VERTEX_ARRAY);
        glEnableClientState(GL_TEXTURE_COORD_ARRAY);

        mFVertexBuffer = m_vbb.asFloatBuffer();
        mTexBuffer     = m_tbb.asFloatBuffer();
        mIndexBuffer   = m_ibb.asShortBuffer();

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA,GL_ONE_MINUS_SRC_ALPHA);
        
        m_fAlpha = 1;
    }

    public void drawImage( int textureID, float x, float y, float w, float h, float scaleX, float scaleY, CRect rectTexture ) {
        glBindTexture(GL_TEXTURE_2D, textureID);
        
    	float zero = 0;
    	float left, top, right, bottom;

    	left   = x / RES_WIDTH - 0.5f;
    	top    = 0.5f - y / RES_HEIGHT;
    	right  = (x + w * scaleX) / RES_WIDTH - 0.5f;
    	bottom = 0.5f - (y + h * scaleY) / RES_HEIGHT;

    	mFVertexBuffer.put(left);  	mFVertexBuffer.put(top);   	mFVertexBuffer.put(zero);
    	mFVertexBuffer.put(left);  	mFVertexBuffer.put(bottom);	mFVertexBuffer.put(zero);
    	mFVertexBuffer.put(right); 	mFVertexBuffer.put(bottom);	mFVertexBuffer.put(zero);
    	mFVertexBuffer.put(right); 	mFVertexBuffer.put(top);	mFVertexBuffer.put(zero);
    	
    	left   = rectTexture.left;
    	top    = rectTexture.top;
    	right  = rectTexture.Right();
    	bottom = rectTexture.Bottom();
    	mTexBuffer.put(left);	mTexBuffer.put(top);
    	mTexBuffer.put(left);	mTexBuffer.put(bottom);
    	mTexBuffer.put(right);	mTexBuffer.put(bottom);
    	mTexBuffer.put(right);	mTexBuffer.put(top);
    	
        mIndexBuffer.put((short) 0);
        mIndexBuffer.put((short) 1);
        mIndexBuffer.put((short) 2);
        mIndexBuffer.put((short) 3);
        
        
        
        
        
    	mFVertexBuffer.position(0);
        mTexBuffer.position(0);

        glFrontFace(GL_CW);
        glVertexPointer(3, GL_FLOAT, 0, mFVertexBuffer);
        glEnable(GL_TEXTURE_2D);
        glTexCoordPointer(2, GL_FLOAT, 0, mTexBuffer);

        
        
        mIndexBuffer.position(0);
        glDrawElements(GL_TRIANGLE_FAN, 4, GL_UNSIGNED_SHORT, mIndexBuffer);
        
        
        
        
        if(m_fAlpha != 1) {
            mColorBuffer0 = m_cbb0.asFloatBuffer();
            for(int i = 0; i < 4; i ++) {
                mColorBuffer0.put(0);
                mColorBuffer0.put(0);
                mColorBuffer0.put(0);
                mColorBuffer0.put(1 - m_fAlpha);
            }
            mColorBuffer0.position(0);

            glDisableClientState(GL_TEXTURE_COORD_ARRAY);
            glEnableClientState(GL_COLOR_ARRAY);
            glDisable(GL_TEXTURE_2D);

            glVertexPointer(3, GL_FLOAT, 0, mFVertexBuffer0);
            glColorPointer(4, GL_FLOAT, 0, mColorBuffer0);

	        glDrawElements(GL_TRIANGLE_FAN, 4, GL_UNSIGNED_SHORT, mIndexBuffer0);

            glEnableClientState(GL_TEXTURE_COORD_ARRAY);
            glDisableClientState(GL_COLOR_ARRAY);
            glEnable(GL_TEXTURE_2D);
        }
    }
    
    public void setAlpha( int nAlpha ) {
    	m_fAlpha = (float)nAlpha / 255.0f;
    }
}
