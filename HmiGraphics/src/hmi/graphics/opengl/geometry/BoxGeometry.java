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
import hmi.math.Vec3f;
import javax.media.opengl.*; 

/**
 * A simple box object, centered at origin rendered using direct mode OpenGL
 */
public class BoxGeometry implements GLRenderObject
{
    float vMin[] = new float[3];
    float vMax[] = new float[3];
    float origin[] = new float[3];

    /**
     * Constructor
     * 
     * @param xSize x dimension
     * @param ySize y dimension
     * @param zSize z dimension
     */
    public BoxGeometry(float xSize, float ySize, float zSize)
    {
        vMin[0] = -xSize * 0.5f;
        vMin[1] = -ySize * 0.5f;
        vMin[2] = -zSize * 0.5f;
        vMax[0] = xSize * 0.5f;
        vMax[1] = ySize * 0.5f;
        vMax[2] = zSize * 0.5f;
        Vec3f.set(origin, 0, 0, 0);
    }

    public BoxGeometry(float halfExtends[])
    {
        setHalfExtends(halfExtends);
        Vec3f.set(origin, 0, 0, 0);
    }

    /**
     * Constructor
     * 
     * @param halfExtends size (in half extends) of the box
     * @param orig box center
     */
    public BoxGeometry(float halfExtends[], float[] orig)
    {
        setHalfExtends(halfExtends);
        Vec3f.set(origin, orig);
    }

    /**
     * Constructor
     * 
     * @param halfExtends size (in half extends) of the box
     * @param ox box center x
     * @param oy box center y
     * @param oz box center z
     */
    public BoxGeometry(float halfExtends[], float ox, float oy, float oz)
    {
        setHalfExtends(halfExtends);
        Vec3f.set(origin, ox, oy, oz);
    }

    public void setHalfExtends(float halfExtends[])
    {
        Vec3f.set(vMax, halfExtends);
        Vec3f.scale(-1f, vMin, halfExtends);
    }

    @Override
    public void glInit(GLRenderContext glc)
    {

    }

    @Override
    public void glRender(GLRenderContext glc)
    {
        GL2 gl2 = glc.gl2;
        gl2.glTranslatef(origin[0], origin[1], origin[2]);
        gl2.glBegin(GL2.GL_QUADS);
        {
            // Draw Front side
            gl2.glNormal3f(0, 0, 1);
            gl2.glVertex3f(vMin[0], vMin[1], vMax[2]);
            gl2.glNormal3f(0, 0, 1);
            gl2.glVertex3f(vMax[0], vMin[1], vMax[2]);
            gl2.glNormal3f(0, 0, 1);
            gl2.glVertex3f(vMax[0], vMax[1], vMax[2]);
            gl2.glNormal3f(0, 0, 1);
            gl2.glVertex3f(vMin[0], vMax[1], vMax[2]);

            // Draw Back side
            gl2.glNormal3f(0, 0, -1);
            gl2.glVertex3f(vMin[0], vMin[1], vMin[2]);
            gl2.glNormal3f(0, 0, -1);
            gl2.glVertex3f(vMin[0], vMax[1], vMin[2]);
            gl2.glNormal3f(0, 0, -1);
            gl2.glVertex3f(vMax[0], vMax[1], vMin[2]);
            gl2.glNormal3f(0, 0, -1);
            gl2.glVertex3f(vMax[0], vMin[1], vMin[2]);

            // Draw Left side
            gl2.glNormal3f(-1, 0, 0);
            gl2.glVertex3f(vMin[0], vMax[1], vMin[2]);
            gl2.glNormal3f(-1, 0, 0);
            gl2.glVertex3f(vMin[0], vMin[1], vMin[2]);
            gl2.glNormal3f(-1, 0, 0);
            gl2.glVertex3f(vMin[0], vMin[1], vMax[2]);
            gl2.glNormal3f(-1, 0, 0);
            gl2.glVertex3f(vMin[0], vMax[1], vMax[2]);

            // Draw Right side
            gl2.glNormal3f(1, 0, 0);
            gl2.glVertex3f(vMax[0], vMax[1], vMin[2]);
            gl2.glNormal3f(1, 0, 0);
            gl2.glVertex3f(vMax[0], vMax[1], vMax[2]);
            gl2.glNormal3f(1, 0, 0);
            gl2.glVertex3f(vMax[0], vMin[1], vMax[2]);
            gl2.glNormal3f(1, 0, 0);
            gl2.glVertex3f(vMax[0], vMin[1], vMin[2]);

            // Draw Up side
            gl2.glNormal3f(0, 1, 0);
            gl2.glVertex3f(vMin[0], vMax[1], vMax[2]);
            gl2.glNormal3f(0, 1, 0);
            gl2.glVertex3f(vMax[0], vMax[1], vMax[2]);
            gl2.glNormal3f(0, 1, 0);
            gl2.glVertex3f(vMax[0], vMax[1], vMin[2]);
            gl2.glNormal3f(0, 1, 0);
            gl2.glVertex3f(vMin[0], vMax[1], vMin[2]);

            // Draw Down side
            gl2.glNormal3f(0, -1, 0);
            gl2.glVertex3f(vMin[0], vMin[1], vMax[2]);
            gl2.glNormal3f(0, -1, 0);
            gl2.glVertex3f(vMin[0], vMin[1], vMin[2]);
            gl2.glNormal3f(0, -1, 0);
            gl2.glVertex3f(vMax[0], vMin[1], vMin[2]);
            gl2.glNormal3f(0, -1, 0);
            gl2.glVertex3f(vMax[0], vMin[1], vMax[2]);
        }
        gl2.glEnd();
    }
}
