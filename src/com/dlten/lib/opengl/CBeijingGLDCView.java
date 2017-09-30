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

public class CBeijingGLDCView extends CDCView {
    
    public CBeijingGLDCView(Context context) {
        super(context);

        initCBaseView();
    }
    public CBeijingGLDCView( Context context, AttributeSet attrs ) {
		super( context, attrs );

		initCBaseView();
	}
    private void initCBaseView() {
        CImgObj.SetMode(CImgObj.MODE_TEXTURE);
    }

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
        	if (surfaceInitialized()) {
        		clear();
        		
        		pWnd.DrawPrevProc();
        		pWnd.DrawProcess();
                
        		updateGraphics();
        	}
        } catch (Exception e) {
        	STD.printStackTrace(e);
        }
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

	private EglHelper mEglHelper;

	public void GLThread_run_start() {
		mEglHelper = new EglHelper();
		/*
		* Specify a configuration for our opengl session and grab the
		* first configuration that matches is
		*/
		mEglHelper.start(null);

		GLThread_local_gl = (GL10) mEglHelper.createSurface(m_holder);
		setCamera();
	}
	
	public void GLThread_run_end() {
		mEglHelper.finish();
	}

	
    public void updateGraphics() {
		/*
		* Once we're done with GL, we need to call swapBuffers()
		* to instruct the system to display the rendered frame
		*/
		mEglHelper.swap();
    }

	private GL10 GLThread_local_gl = null;
	
	public boolean surfaceInitialized() {
		return (GLThread_local_gl == null) ? false : true;
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

        glClear(GL_COLOR_BUFFER_BIT | GL_ALPHA_BITS | GL_DEPTH_BUFFER_BIT);

        /*
         * Now we're ready to draw some 3D objects
         */

        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();

        GLU.gluLookAt(GLThread_local_gl, 0, 0, 5, 0, 0, 0, 0, 1, 0);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA,GL_ONE_MINUS_SRC_ALPHA);
    }
    
}
