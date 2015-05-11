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
