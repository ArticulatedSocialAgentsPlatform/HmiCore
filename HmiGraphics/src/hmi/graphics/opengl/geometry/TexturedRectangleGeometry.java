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

import javax.media.opengl.GL2;

/**
 * A rectangle with a texture coordinates, drawn at the origin, facing in the Z direction
 * @author Herwin
 */
public class TexturedRectangleGeometry implements GLRenderObject
{
    private float left, right;
    private float up, down;
    
    public TexturedRectangleGeometry(float width, float height)
    {
        up =  0.5f* height;
        down = -0.5f* height;
        left = -0.5f*width;
        right = 0.5f*width;
    }
    
    public TexturedRectangleGeometry(float []center, float width, float height)
    {
        this(width, height);
    }
    
    @Override
    public void glInit(GLRenderContext glc)
    {
    }

    @Override
    public void glRender(GLRenderContext glc)
    {
        GL2 gl2 = glc.gl2;
        gl2.glBegin(GL2.GL_QUADS);
        {
            gl2.glNormal3f(0, 0, 1);
            gl2.glTexCoord2f(0, 1);
            gl2.glVertex3f(left, up,0);
            
            
            gl2.glNormal3f(0, 0, 1);
            gl2.glTexCoord2f(0, 0);
            gl2.glVertex3f(left, down, 0);            
            
            gl2.glNormal3f(0, 0, 1);
            gl2.glTexCoord2f(1, 0);
            gl2.glVertex3f(right, down, 0);            
            
            gl2.glNormal3f(0, 0, 1);
            gl2.glTexCoord2f(1, 1);
            gl2.glVertex3f(right, up, 0);            
        }
        gl2.glEnd();
    }

}
