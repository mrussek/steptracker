package com.mrussek.steptracker.render.mesh;

import android.opengl.GLES20;

import com.mrussek.steptracker.render.Shader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Mesh {

    protected int numCoords;
    protected int vertexCount;
    protected int vertexStride;
    protected int primitiveType;

    protected FloatBuffer vertexBuffer;
    protected float[] color = new float[] {1,1,1,1};

    public Mesh() {}

    public Mesh(int numCoords, float[] vertices, int primitiveType) {
        setVertices(numCoords, vertices, primitiveType);
    }

    public void setVertices(int numCoords, float[] coords, int primitiveType) {
        this.primitiveType = primitiveType;
        this.numCoords = numCoords;

        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (number of coordinate values * 4 bytes per float)
                coords.length * 4);
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());

        // create a floating point buffer from the ByteBuffer
        vertexBuffer = bb.asFloatBuffer();
        // add the coordinates to the FloatBuffer
        vertexBuffer.put(coords);
        // set the buffer to read the first coordinate
        vertexBuffer.position(0);

        vertexCount = coords.length / numCoords;
        vertexStride = numCoords * 4; // 4 bytes per vertex
    }

    public void setColor(float f1, float f2, float f3, float f4) {
        color = new float[] {f1, f2, f3, f4};
    }

    public void render() {
        int program = Shader.current();

        // get handle to vertex shader's attributes
        int vertUniformHandle = GLES20.glGetAttribLocation(program, "vPosition");

        // Enable a handle to the vertices
        GLES20.glEnableVertexAttribArray(vertUniformHandle);

        // Prepare the coordinate data
        GLES20.glVertexAttribPointer(vertUniformHandle, numCoords,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        // get handle to fragment shader's vColor member
        int colorUniformHandle = GLES20.glGetUniformLocation(program, "vColor");

        // Set color for drawing the triangle
        GLES20.glUniform4fv(colorUniformHandle, 1, color, 0);

        // Draw the triangle
        GLES20.glDrawArrays(primitiveType, 0, vertexCount);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(vertUniformHandle);
    }
}
