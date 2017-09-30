package com.dlten.lib.graphics;


// import java.io.IOException;
// import java.io.InputStream;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
// import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
// import android.opengl.GLES10;
import android.opengl.GLUtils;
import android.os.SystemClock;
import static android.opengl.GLES10.*;

import com.dlten.lib.file.CResFile;
import com.dlten.lib.graphics.CBmpManager;

public class CRenderer implements GLSurfaceView.Renderer {
	
	private int m_textureID;

	public CRenderer() {
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glDisable(GL_DITHER);
        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_FASTEST);

        glClearColor(.5f, .5f, .5f, 1);
        glShadeModel(GL_SMOOTH);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_TEXTURE_2D);

        /*
         * Create our texture. This has to be done each time the
         * surface is created.
         */
        int[] textures = new int[1];
        glGenTextures(1, textures, 0);
        m_textureID = textures[0];

        glBindTexture(GL_TEXTURE_2D, m_textureID);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexEnvf(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_REPLACE);

        Bitmap bitmap = CBmpManager.loadImage(CResFile.load("test.png"));
        GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);
        bitmap.recycle();
	}
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0, 0, width, height);

        /*
        * Set our projection matrix. This doesn't have to be done
        * each time we draw, but usually a new projection needs to
        * be set when the viewport is resized.
        */

        float ratio = (float) width / height;
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glFrustumf(-ratio, ratio, -1, 1, 3, 7);
	}
	@Override
	public void onDrawFrame(GL10 gl) {
        /*
         * By default, OpenGL enables features that improve quality
         * but reduce performance. One might want to tweak that
         * especially on software renderer.
         */
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

        GLU.gluLookAt(gl, 0, 0, -5, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        glEnableClientState(GL_VERTEX_ARRAY);
        glEnableClientState(GL_TEXTURE_COORD_ARRAY);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, m_textureID);
        glTexParameterx(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameterx(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

        long time = SystemClock.uptimeMillis() % 4000L;
        float angle = 0.090f * ((int) time);

        glRotatef(angle, 0, 0, 1.0f);

        // mTriangle.draw(gl);
	}
}
