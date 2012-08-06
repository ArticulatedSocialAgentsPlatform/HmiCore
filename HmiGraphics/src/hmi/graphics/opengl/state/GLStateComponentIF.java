/*******************************************************************************
 * Copyright (C) 2009 Human Media Interaction, University of Twente, the Netherlands
 * 
 * This file is part of the Elckerlyc BML realizer.
 * 
 * Elckerlyc is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Elckerlyc is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Elckerlyc.  If not, see http://www.gnu.org/licenses/.
 ******************************************************************************/

package hmi.graphics.opengl.state;

import hmi.graphics.opengl.GLRenderContext;

/**
 * A GLStateComponentIF is a wrapper for GL attributes that are set by means of a call like glXYZ(glEnum, glId, glValf), where glEnum and glId are
 * ints, glValf is a float value. Examples: glLightf with glEnum == light, glId == GL_SPOT_EXPONENT, GL_SPOT_CUTOFF, GL_CONSTANT_ATTENUATION,
 * GL_LINEAR_ATTENUATION, GL_QUADRATIC_ATTENUATION. glMaterialfv with glEnumi == face, glId == GL_SHININESS glTexParameterfv with glEnumi == target,
 * glId == GL_TEXTURE_MIN_LOD, GL_TEXTURE_MAX_LOD
 * 
 * @author Job Zwiers
 */
public class GLStateComponentIF implements GLStateComponent
{

    private int glId; // OpenGL Id
    private int scId; // GLStateComponent id
    private int glEnumi; // light, face, target ...
    private float glVal; // float value

    /**
     * Create a new GLStateComponentIF.
     */
    public GLStateComponentIF(int glEnumi, int glId, float glVal)
    {
        this.glId = glId;
        scId = GLState.getSCId(glId);
        checkLegal(scId);
        this.glEnumi = glEnumi;
        this.glVal = glVal;
    }

    /* check whether it is one of the known cases */
    private void checkLegal(int scId)
    {
        if (GLState.LIGHT_GROUP <= scId && scId < GLState.LIGHT_GROUP + GLState.LIGHT_GROUP_SIZE)
            return;
        if (GLState.MATERIAL_GROUP <= scId && scId < GLState.MATERIAL_GROUP + GLState.MATERIAL_GROUP_SIZE)
            return;
        if (GLState.TEXTURE_GROUP <= scId && scId < GLState.TEXTURE_GROUP + GLState.TEXTURE_GROUP_SIZE)
            return;
        throw new IllegalArgumentException("GLStateComponentIF: unknown scId: " + scId);
    }

    public final int getSCId()
    {
        return scId;
    }

    public void glInit(GLRenderContext gl)
    {
        glRender(gl);
    }

    public void glRender(GLRenderContext glc)
    {
        // assume LIGHT_GROUP < MATERIAL_GROUP < TEXTURE_GROUP
        if (scId < GLState.MATERIAL_GROUP)
        {
            glc.gl2.glLightf(glEnumi, glId, glVal); // glEnumi == light
        }
        else if (scId < GLState.TEXTURE_GROUP)
        {
            glc.gl2.glMaterialf(glEnumi, glId, glVal); // glEnumi == face
        }
        else
        {
            glc.gl.glTexParameterf(glEnumi, glId, glVal); // glEnumi == target
        }
    }

    public String toString()
    {
        return ("<" + GLState.getGLName(glId) + " (" + GLState.getGLName(glEnumi) + ") = " + glVal + ">");
    }

}
