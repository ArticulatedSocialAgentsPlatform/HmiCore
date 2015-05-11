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
package hmi.graphics.opengl;

import javax.media.opengl.*; 
import hmi.animation.VJoint;
import hmi.graphics.opengl.geometry.SphereGeometry;
import hmi.graphics.opengl.state.GLMaterial;
import hmi.graphics.opengl.state.NoTexture2DState;
import hmi.math.Vec3f;

/**
 * Debug class used to draw the skeleton from a root VJoint 
 * @author welberge
 */
public class GLSkeleton implements GLRenderObject
{
    private VJoint parent;
    private SphereGeometry sphere;
    private GLMaterial sphereState;
    private GLMaterial lineState;
    private NoTexture2DState noTextureState;
    private final float[] sphereDiffuse = {1,0,0,1};
    private final float[] sphereSpecular = {1,1,1,1};
    private final float[] sphereAmbient = {0,0,0,0};
    private final float[] sphereEmission = {0,0,0,1};
    private final float[] lineDiffuse = {1,1,1,1};
    private final float[] lineAmbient = {1,1,1,1};
    private final float[] lineEmission = {1,1,1,1};
    private final float[] lineSpecular = {1,1,1,1};
    private float[] offset = new float[3]; 
                        
    public GLSkeleton(VJoint p)
    {
        parent = p;        
        sphere = new SphereGeometry(0.02f, 10, 10);
        sphereState = new GLMaterial();        
        sphereState.setDiffuseColor(sphereDiffuse);
        sphereState.setSpecularColor(sphereSpecular);
        sphereState.setAmbientColor(sphereAmbient);
        sphereState.setEmissionColor(sphereEmission);
        lineState = new GLMaterial();
        lineState.setDiffuseColor(lineDiffuse);
        lineState.setAmbientColor(lineAmbient);
        lineState.setEmissionColor(lineEmission);
        lineState.setSpecularColor(lineSpecular);
        noTextureState = new NoTexture2DState();
        Vec3f.set(offset,0,0,0);
    }
    
    public void setOffset(float x, float y, float z)
    {
        Vec3f.set(offset,x,y,z);
    }
    
    private void renderJoints(GLRenderContext glc, VJoint p)
    {
        GL2 gl = glc.gl2;
        float pos[]=new float[3];
        gl.glPushMatrix();        
        p.getPathTranslation(parent, pos);
        gl.glTranslatef(pos[0], pos[1], pos[2]);
        sphere.glRender(glc);
        gl.glPopMatrix();        
        for(VJoint vj:p.getChildren())
        {
            renderJoints(glc, vj);
        }
    }
    
    private void renderLines(GL2 gl, VJoint p)
    {
        float pos[] = new float[3];
        float posChild[] = new float[3];
        
        p.getPathTranslation(parent, pos);
        for(VJoint vj:p.getChildren())
        {
            gl.glVertex3f(pos[0],pos[1],pos[2]);
            vj.getPathTranslation(parent, posChild);
            gl.glVertex3f(posChild[0],posChild[1],posChild[2]);
            renderLines(gl, vj);
        }
    }
    
    @Override
    public void glRender(GLRenderContext glc) 
    {
       GL2 gl = glc.gl2;
       gl.glPushMatrix();
       gl.glTranslatef(offset[0], offset[1], offset[2]);
       noTextureState.glRender(glc);
       sphereState.glRender(glc);    
       renderJoints(glc, parent);
       lineState.glRender(glc);
       gl.glBegin(GL.GL_LINES);
       renderLines(gl, parent);
       gl.glEnd();
       gl.glPopMatrix();
    }    
    
    /**
     * OpenGL initialization.
     */
    @Override
    public void glInit(GLRenderContext glc) 
    {
        sphere.glInit(glc);
        sphereState.glInit(glc);
        lineState.glInit(glc);
    }
}
