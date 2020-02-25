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
