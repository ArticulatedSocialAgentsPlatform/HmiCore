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
 * A GLStateComponentII is a wrapper for GL texture state attributes Examples: glTexParameteri with glEnumi == target, glId == GL_TEXTURE_WRAP_S,
 * GL_TEXTURE_WRAP_T, GL_TEXTURE_WRAP_R, GL_TEXTURE_BASE_LEVEL, GL_TEXTURE_MAX_LEVEL
 * 
 * @author Job Zwiers
 */
public final class GLTextureStateComponent implements GLStateComponent
{

    private int glId; // OpenGL Id
    private int scId; // GLStateComponent id
    private int target, glEnumi; // two int values

    /**
     * Create a new GLTextureStateComponent.
     */
    public GLTextureStateComponent(int target, int glId, int glEnumi)
    {
        this.glId = glId;
        scId = GLState.getSCId(glId);
        checkLegal(scId);
        this.target = target;
        this.glEnumi = glEnumi;
    }

    /* check whether it is one of the known cases */
    private void checkLegal(int scId)
    {
        if (GLState.TEXTURE_GROUP <= scId && scId < GLState.TEXTURE_GROUP + GLState.TEXTURE_GROUP_SIZE)
            return;
        throw new IllegalArgumentException("GLTextureStateComponent: unknown scId: " + scId);
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
        // if (GLState.TEXTURE_GROUP <= glId && glId < GLState.TEXTURE_GROUP+GLState.TEXTURE_GROUP_SIZE) {
        glc.gl.glTexParameteri(target, glId, glEnumi);
    }

    public String toString()
    {
        return ("<" + GLState.getGLName(glId) + " (" + GLState.getGLName(target) + ") = " + glEnumi + ">");
    }

}
