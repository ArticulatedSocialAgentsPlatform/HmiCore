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
package hmi.graphics.opengl.renderobjects;

import javax.media.opengl.*; 
import hmi.graphics.opengl.GLRenderContext;
import hmi.graphics.opengl.GLRenderObject;
import hmi.graphics.opengl.GLShader;
import hmi.graphics.opengl.state.GLFill;
import hmi.graphics.opengl.state.NoTexture2DState;


/**
 * Renders a basic checkerboard ground of specified dimensions.
 * @author Herwin van Welbergen
 */
public class GLCheckerBoardGround implements GLRenderObject
{

    private NoTexture2DState noTextureState;
    private GLFill fillState;
    private final float[] whiteDiffuse = { 1, 1, 1, 1 };
    private final float[] whiteSpecular = { 1, 1, 1, 1 };
    private final float[] whiteAmbient = { 0, 0, 0, 0 };
    private final float[] whiteEmission = { 0, 0, 0, 1 };
    private final float[] greyDiffuse = { 0.2f, 0.2f, 0.2f, 1 };
    private final float[] greyAmbient = { 0, 0, 0, 1 };
    private final float[] greyEmission = { 0, 0, 0, 1 };
    private final float[] greySpecular = { 1, 1, 1, 1 };
    float height;
    float tileWidth;
    GLShader lightBlinnShader = null;
    GLShader darkBlinnShader = null;

    public GLCheckerBoardGround(float w, float h)
    {
        noTextureState = new NoTexture2DState();
        fillState = new GLFill();
        lightBlinnShader = new GLShader("colorShader");
        darkBlinnShader = new GLShader("colorShader");
        height = h;
        tileWidth = w;
    }

    public void glInit(GLRenderContext glc)
    {
        lightBlinnShader.setValue("color", whiteDiffuse);
        lightBlinnShader.setValue("ambientColor", whiteAmbient);
        lightBlinnShader.setValue("diffuseColor", whiteDiffuse);
        lightBlinnShader.setValue("specularColor", whiteSpecular);
        lightBlinnShader.glInit(glc);
        darkBlinnShader.setValue("color", greyDiffuse);
        darkBlinnShader.setValue("emissiveColor", greyEmission);
        darkBlinnShader.setValue("ambientColor", greyAmbient);
        darkBlinnShader.setValue("diffuseColor", greyDiffuse);
        darkBlinnShader.setValue("specularColor", greySpecular);
        darkBlinnShader.glInit(glc);
        noTextureState.glInit(glc);
        fillState.glInit(glc);
    }

    public void glRender(GLRenderContext glc)
    {
      
        GL2 gl = glc.gl2;
        fillState.glRender(glc);
        noTextureState.glRender(glc);
        darkBlinnShader.glRender(glc);
        gl.glBegin(GL2.GL_QUADS);
        for (int z = -15; z < 15; z++)
        {
            for (int x = -15 + (Math.abs(z) % 2); x < 15 - (Math.abs(z) % 2); x += 2)
            {
                gl.glVertex3f(x * tileWidth, height, z * tileWidth);
                gl.glNormal3f(0, 1, 0);

                gl.glVertex3f(x * tileWidth, height, (z + 1) * tileWidth);
                gl.glNormal3f(0, 1, 0);

                gl.glVertex3f((x + 1) * tileWidth, height, (z + 1) * tileWidth);
                gl.glNormal3f(0, 1, 0);

                gl.glVertex3f((x + 1) * tileWidth, height, z * tileWidth);
                gl.glNormal3f(0, 1, 0);
            }
        }
        gl.glEnd();
        lightBlinnShader.glRender(glc);
        gl.glBegin(GL2.GL_QUADS);
        for (int z = -15; z < 15; z++)
        {
            for (int x = -15 + (Math.abs(z + 1) % 2); x < 15 - (Math.abs(z + 1) % 2); x += 2)
            {
                gl.glVertex3f(x * tileWidth, height, z * tileWidth);
                gl.glNormal3f(0, 1, 0);

                gl.glVertex3f(x * tileWidth, height, (z + 1) * tileWidth);
                gl.glNormal3f(0, 1, 0);

                gl.glVertex3f((x + 1) * tileWidth, height, (z + 1) * tileWidth);
                gl.glNormal3f(0, 1, 0);

                gl.glVertex3f((x + 1) * tileWidth, height, z * tileWidth);
                gl.glNormal3f(0, 1, 0);
            }
        }
        gl.glEnd();
    }

}
