/*******************************************************************************
 * Copyright (C) 2009-2020 Human Media Interaction, University of Twente, the Netherlands
 *
 * This file is part of the Articulated Social Agents Platform BML realizer (ASAPRealizer).
 *
 * ASAPRealizer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License (LGPL) as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ASAPRealizer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ASAPRealizer.  If not, see http://www.gnu.org/licenses/.
 ******************************************************************************/
package hmi.graphics.opengl.geometry;


import hmi.graphics.opengl.GLRenderContext;
import hmi.graphics.opengl.GLRenderObject;
import hmi.graphics.opengl.GLShader;
import hmi.graphics.util.BufferUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.*; 

/**
 * A simple Sphere object, rendered using direct mode OpenGL
 * 
 * @author Job Zwiers
 */
public class SphereGeometryFaceEditor implements GLRenderObject
{

    private float radius = 5.0f;
    private int numSlices = 32;
    private int numStacks = 16;

    // float[] mat_ambient = new float[] {1.0f, 1.0f, 1.0f};
    // float[] mat_diffuse = new float[] {1.0f, 1.0f, 1.0f};
    // float[] mat_specular = new float[] {0.0f, 0.0f, 0.0f};
    // int texId;
    // //float drho, dtheta, ds, dt;
    // int sphereList;
    private int nrOfVertices;
    private int nrOfTris;
    private float[] vertexData;
    private float[] normalData;
    private float[] texCoordData;
    private int[] indexData;

    private FloatBuffer vertexBuffer;
    private FloatBuffer normalBuffer;
    private FloatBuffer texCoordBuffer;
    private IntBuffer indexBuffer;

    // Buffer ids:
    private int vertexBufferId, normalBufferId, indexBufferId, texCoordBufferId;
    private int dataBufferSize; // size of dataBuffer (in floats)
    private int dataByteBufferSize; // size of dataBuffer in bytes
    private int texCoordBufferSize;
    private int texCoordByteBufferSize;
    private int indexBufferSize;
    private int indexByteBufferSize;

    // attribute indices for shader
    private int vertexIndex;
    private int normalIndex;
    // private int texCoordlIndex;

    private GLShader shader;

    float scalex, scaley, scalez;

    private boolean dirty = false;

    /**
     * Create a new Sphere object
     */
    public SphereGeometryFaceEditor(float radius, int numSlices, int numStacks)
    {
        this.radius = radius;
        this.numSlices = numSlices;
        this.numStacks = numStacks;
        scalex = 1.0f;
        scaley = 1.0f;
        scalez = 1.0f;
        init();
        init();
        shader = new GLShader("blinnBasic");
        shader.setValue("diffuseColor", 1.0f, 1.0f, 1.0f, 1.0f);
    }

    public void setScalex(float scale)
    {
        this.scalex = scale;
        reinit();
    }

    public void setScaley(float scale)
    {
        this.scaley = scale;
        reinit();
    }

    public void setScalez(float scale)
    {
        this.scalez = scale;
        reinit();
    }

    public void setRadius(float radius)
    {
        this.radius = radius;
        reinit();
    }

    private void reinit()
    {
        init();
        dirty = true;
    }

    public void glInit(GLRenderContext glc)
    {
        GL2ES2 gl = glc.gl;
        shader.glInit(glc);
        int[] progarray = new int[1];
        gl.glGetIntegerv(GL2ES2.GL_CURRENT_PROGRAM, progarray, 0);
        int prog = progarray[0];

        vertexIndex = gl.glGetAttribLocation(prog, "mcPosition");
        // System.out.println("SphereGeometry.glInit, vertexIndex = " + vertexIndex);
        normalIndex = gl.glGetAttribLocation(prog, "mcNormal");
        // texCoordlIndex = gl.glGetAttribLocation( prog, "texCoord1");

        int[] bufNames = new int[4];
        gl.glGenBuffers(4, bufNames, 0);
        vertexBufferId = bufNames[0];
        normalBufferId = bufNames[1];
        texCoordBufferId = bufNames[2];
        indexBufferId = bufNames[3];

        loadBuffers(gl);

    }

    private void loadBuffers(GL2ES2 gl)
    {
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vertexBufferId);
        vertexBuffer.rewind();
        gl.glBufferData(GL.GL_ARRAY_BUFFER, dataByteBufferSize, vertexBuffer, GL.GL_STATIC_DRAW);

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, normalBufferId);
        normalBuffer.rewind();
        gl.glBufferData(GL.GL_ARRAY_BUFFER, dataByteBufferSize, normalBuffer, GL.GL_STATIC_DRAW);

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, texCoordBufferId);
        texCoordBuffer.rewind();
        gl.glBufferData(GL.GL_ARRAY_BUFFER, texCoordByteBufferSize, texCoordBuffer, GL.GL_STATIC_DRAW);

        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, indexBufferId);
        indexBuffer.rewind();
        gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, indexByteBufferSize, indexBuffer, GL.GL_STATIC_DRAW);
    }

    public void glRender(GLRenderContext glc)
    {
        GL2ES2 gl = glc.gl;
        if (dirty)
        {
            loadBuffers(gl);
            dirty = false;
        }

        shader.glRender(glc);

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vertexBufferId);
        gl.glEnableVertexAttribArray(vertexIndex);
        gl.glVertexAttribPointer(vertexIndex, 3, GL.GL_FLOAT, false, 0, 0);

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, normalBufferId);
        gl.glVertexAttribPointer(normalIndex, 3, GL.GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(normalIndex);

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, texCoordBufferId);

        // gl.glVertexAttribPointer(texCoordlIndex, 2, GLC.GL_FLOAT, false, 0, 0 );
        // gl.glEnableVertexAttribArray(texCoordlIndex);

        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, indexBufferId);
        glc.gl2.glDrawRangeElements(GL.GL_TRIANGLES, 0, nrOfVertices, indexBufferSize, GL.GL_UNSIGNED_INT, 0L);
    }

    private void init()
    {
        nrOfVertices = (numStacks + 1) * (numSlices + 1);
        nrOfTris = numStacks * numSlices * 2;
        dataBufferSize = 3 * nrOfVertices; // 3 floats per vertex
        texCoordBufferSize = 2 * nrOfVertices;
        indexBufferSize = 3 * nrOfTris;

        dataByteBufferSize = 4 * dataBufferSize;
        texCoordByteBufferSize = 4 * texCoordBufferSize;
        indexByteBufferSize = 4 * indexBufferSize;

        vertexData = new float[dataBufferSize];
        normalData = new float[dataBufferSize];
        texCoordData = new float[texCoordBufferSize];
        indexData = new int[indexBufferSize];

        vertexBuffer = BufferUtil.directFloatBuffer(dataBufferSize);
        normalBuffer = BufferUtil.directFloatBuffer(dataBufferSize);
        texCoordBuffer = BufferUtil.directFloatBuffer(texCoordBufferSize);
        indexBuffer = BufferUtil.directIntBuffer(indexBufferSize);

        double dtheta = Math.PI / numStacks;
        double dphi = (2.0 * Math.PI) / numSlices;
        double ds = 1.0 / numSlices;
        double dt = 1.0 / numStacks;
        double theta, phi;
        float stheta, ctheta, sphi, cphi;
        float x, y, z, s, t;
        int vc = 0; // the vertex counter
        // fill stacks, from bottom to top
        for (int i = 0; i <= numStacks; i++)
        {
            if (i == 0)
            {
                stheta = 0.0f;
                ctheta = -1.0f;
            }
            else if (i == numStacks)
            {
                stheta = 0.0f;
                ctheta = 1.0f;
            }
            else
            {
                theta = Math.PI - i * dtheta;
                stheta = (float) Math.sin(theta);
                ctheta = (float) Math.cos(theta);
            }
            t = (float) (i * dt);
            // hmi.util.Console.println("========================\ni=" + i + "  stheta = " + stheta + "  ctheta=" + ctheta);
            for (int j = 0; j <= numSlices; j++)
            {
                if (j == 0 || j == numSlices)
                {
                    sphi = 0.0f;
                    cphi = 1.0f;
                }
                else
                {
                    phi = j * dphi;
                    sphi = (float) Math.sin(phi);
                    cphi = (float) Math.cos(phi);
                }
                // hmi.util.Console.println("j=" + j + "  sphi = " + sphi + "  cphi=" + cphi);
                x = -sphi * stheta * scalex;
                y = ctheta * scaley;
                z = -cphi * stheta * scalez;
                s = (float) (j * ds);
                // hmi.util.Console.println("x=" + x + "  y = " + y + " z= " + z);
                vertexData[3 * vc] = radius * x;
                vertexData[3 * vc + 1] = radius * y;
                vertexData[3 * vc + 2] = radius * z;
                normalData[3 * vc] = x;
                normalData[3 * vc + 1] = y;
                normalData[3 * vc + 2] = z;
                texCoordData[2 * vc] = s;
                texCoordData[2 * vc + 1] = t;
                vc++;
            }
        }
        int tc = 0; // the triangle counter;
        for (int i = 0; i < numStacks; i++)
        {
            for (int j = 0; j < numSlices; j++)
            {
                // add two triangles
                int lowerleft = (i * (numSlices + 1)) + j;
                int lowerright = lowerleft + 1;
                int upperleft = lowerleft + numSlices + 1;
                int upperright = upperleft + 1;
                indexData[3 * tc] = lowerleft;
                indexData[3 * tc + 1] = lowerright;
                indexData[3 * tc + 2] = upperleft;
                tc++;
                indexData[3 * tc] = upperleft;
                indexData[3 * tc + 1] = lowerright;
                indexData[3 * tc + 2] = upperright;
                tc++;
            }
        }
        // for (int i=0; i< nrOfVertices; i++) {
        // hmi.util.Console.println("vertex " + i + " = (" + vertexData[3*i] + ", " + vertexData[3*i+1] + ", " + vertexData[3*i+2] + ")");
        // }
        vertexBuffer.put(vertexData);
        normalBuffer.put(normalData);
        texCoordBuffer.put(texCoordData);
        indexBuffer.put(indexData);

    }

}
