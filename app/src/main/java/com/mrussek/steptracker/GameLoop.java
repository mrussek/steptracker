package com.mrussek.steptracker;

import android.opengl.GLSurfaceView;

import com.mrussek.steptracker.render.Shader;
import com.mrussek.steptracker.render.Texture;
import com.mrussek.steptracker.render.mesh.TexMesh;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.*;

/**
 * Created by Peter on 10/31/17.
 */

public class GameLoop implements GLSurfaceView.Renderer {

    private static TexMesh quad, lineQuad;
    private float cycle;

    static {
        quad = new TexMesh();
        quad.setVertices(2, new float[] { 1,1, -1,1, -1,-1, 1,-1 }, GL_TRIANGLE_FAN);
        quad.setTexCoords(new float[] { 1,0, 0,0, 0,1, 1,1 });
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        cycle = 0;
        Texture.createTextures();
        glEnable(GL_CULL_FACE);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        Shader.load("quadVert", "quadFrag", "quad");
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        //cycle += .005;
        cycle %= 1;

        float c = -cycle*2+1;
        lineQuad = new TexMesh();
        lineQuad.setVertices(2, new float[] { 1,1, -1,1, -1,c, 1,c }, GL_TRIANGLE_FAN);
        lineQuad.setTexCoords(new float[] { 1,0, 0,0, 0,cycle, 1,cycle });

        Shader.use("quad");
        Texture.bindFiltered("map");
        quad.render();
        Texture.bindFiltered("line");
        lineQuad.render();
    }

    public void setSteps(int steps) {
        int maxSteps = 100;
        cycle = (float)steps/maxSteps;
    }
}
