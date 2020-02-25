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
