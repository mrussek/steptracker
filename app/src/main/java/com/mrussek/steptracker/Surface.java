package com.mrussek.steptracker;

import android.content.Context;
import android.opengl.GLSurfaceView;

public class Surface extends GLSurfaceView {

    public final GameLoop renderer;

    public Surface(Context context) {
        super(context);

        // Create an OpenGL ES 2.0 context
        // (int redSize, int greenSize, int blueSize, int alphaSize, int depthSize, int stencilSize)
        setEGLConfigChooser(8, 8, 8, 8, 16, 8);
        setEGLContextClientVersion(2);
        setPreserveEGLContextOnPause(true);

        renderer = new GameLoop();

        // Render the view only when there is a change in the drawing data
        //setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        // crashes
        //setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(renderer);
    }
}