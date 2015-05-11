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
