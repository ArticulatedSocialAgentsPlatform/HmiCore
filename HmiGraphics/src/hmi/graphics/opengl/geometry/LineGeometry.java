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

import javax.media.opengl.*; 
import hmi.graphics.opengl.GLRenderContext;
import hmi.graphics.opengl.GLRenderObject;

/**
 * A set of lines, rendered using direct mode OpenGL
 */
public class LineGeometry implements GLRenderObject 
{
    float vertices[];
    
    /**
     * Constructor
     * @param vertices the line vertices, 6 floats per line 
     */
    public LineGeometry(float []vertices)
    {
        this.vertices = vertices;
    }
    
    public void glInit(GLRenderContext glc)
    {
        
    }

    public void glRender(GLRenderContext glc)
    {
        GL2 gl = glc.gl2;
        gl.glBegin(GL.GL_LINES);
        gl.glVertex3f(vertices[0],vertices[1],vertices[2]);
        gl.glVertex3f(vertices[3],vertices[4],vertices[5]);
        gl.glEnd();
    }
    
}
