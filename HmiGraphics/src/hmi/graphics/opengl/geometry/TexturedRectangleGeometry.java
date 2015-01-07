/*******************************************************************************
 * The MIT License (MIT)
 * Copyright (c) 2015 University of Twente
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *******************************************************************************/
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
