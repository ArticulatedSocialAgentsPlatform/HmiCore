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
