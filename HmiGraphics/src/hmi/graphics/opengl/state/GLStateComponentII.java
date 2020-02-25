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
package hmi.graphics.opengl.state;

import hmi.graphics.opengl.GLRenderContext;
import javax.media.opengl.*; 

/**
 * A GLStateComponentII is a wrapper for GL attributes that are set by means of a call like glXYZ(target, glId, glEnumi) or glXYZ(target, glEnumi)
 * 
 * Examples: glPolygonMode GL_POLYGON_MODE glColorMaterial glTexParameteri with glEnumi == target, glId == GL_TEXTURE_WRAP_S, GL_TEXTURE_WRAP_T,
 * GL_TEXTURE_WRAP_R, GL_TEXTURE_BASE_LEVEL, GL_TEXTURE_MAX_LEVEL
 * 
 * @author Job Zwiers
 */
public final class GLStateComponentII implements GLStateComponent
{

    private int glId; // OpenGL Id
    private int scId; // GLStateComponent id
    private int target, glEnumi; // two int values

    /**
     * Create a new GLStateComponentII.
     */
    public GLStateComponentII(int target, int glId, int glEnumi)
    {
        this.glId = glId;
        scId = GLState.getSCId(glId);
        checkLegal(glId);
        this.target = target;
        this.glEnumi = glEnumi;
    }

    /* check whether it is one of the known cases */
    private void checkLegal(int glId)
    {
        if (glId == GL2.GL_POLYGON_MODE || glId == GL2.GL_COLOR_MATERIAL_PARAMETER)
            return;
        if (GLState.TEXTURE_GROUP <= glId && glId < GLState.TEXTURE_GROUP + GLState.TEXTURE_GROUP_SIZE)
            return;
        throw new IllegalArgumentException("GLStateComponentII: unknown glId: " + glId);
    }

    public int getSCId()
    {
        return scId;
    }

    public void glInit(GLRenderContext glc)
    {
        glRender(glc);
    }

    public void glRender(GLRenderContext glc)
    {
        // assume LIGHT_GROUP < MATERIAL_GROUP < TEXTURE_GROUP
        if (GLState.TEXTURE_GROUP <= glId && glId < GLState.TEXTURE_GROUP + GLState.TEXTURE_GROUP_SIZE)
        {
            glc.gl.glTexParameteri(target, glId, glEnumi);
        }
        else if (glId == GL2.GL_COLOR_MATERIAL_PARAMETER)
        { // combined with GL_COLOR_MATERIAL_FACE
            glc.gl2.glColorMaterial(target, glEnumi); // (face, mode)
        }
        else if (glId == GL2.GL_POLYGON_MODE)
        {
            glc.gl2.glPolygonMode(target, glEnumi); // (face, mode)
        }
    }

    public String toString()
    {
        return ("<" + GLState.getGLName(glId) + " (" + GLState.getGLName(target) + ") = " + glEnumi + ">");
    }

}
