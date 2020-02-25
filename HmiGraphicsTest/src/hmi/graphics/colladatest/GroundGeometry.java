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
package hmi.graphics.colladatest;
import hmi.graphics.opengl.*;




/**
 * A simple Ground object, 
 */
public class GroundGeometry implements GLRenderObject {

   int texId;
   int groundList;
   float height = 0.0f;
 
   public GroundGeometry(float height) {
      this.height = height;
 
   }
 
 
   public void glInit(GLRenderContext gl) {
      String textureFile = "Grass.png";
      //String textureFile = "Chromic.jpg";
      //hmi.util.Console.println("Ground1: glInit");
      texId = Texture.loadGLTexture(gl, textureFile, GLC.GL_LINEAR_MIPMAP_LINEAR, GLC.GL_RGB, false);
      groundList = gl.glGenLists(1);
      gl.glNewList(groundList, GLC.GL_COMPILE);
         render(gl);
      gl.glEndList();
      //hmi.util.Console.println("Ground1: glInit");
   }
   
   
   public void glRender(GLRenderContext gl) {
      //hmi.util.Console.println("Ground1: glRender");
      gl.glCallList(groundList);
   }
   
   /**
    * render this object
    */
   private void render(GLRenderContext gl) {
      
      
      int fExtent = 20;
      int fStep = 1;
      float y = height;
      float s = 0.0f;
      float t = 0.0f;
      float texStep = 1.0f / (((float)fExtent) * 0.075f);
      
      gl.glEnable(GLC.GL_TEXTURE_2D);    
      //gl.glDisable(GLC.GL_TEXTURE_2D);   
      gl.glTexEnvi(GLC.GL_TEXTURE_ENV, GLC.GL_TEXTURE_ENV_MODE, GLC.GL_REPLACE);
      //gl.glTexEnvi(GLC.GL_TEXTURE_ENV, GLC.GL_TEXTURE_ENV_MODE, GLC.GL_MODULATE);
      gl.glBindTexture  (GLC.GL_TEXTURE_2D, texId);
      gl.glTexParameterf(GLC.GL_TEXTURE_2D, GLC.GL_TEXTURE_WRAP_S, GLC.GL_REPEAT);
      gl.glTexParameterf(GLC.GL_TEXTURE_2D, GLC.GL_TEXTURE_WRAP_T, GLC.GL_REPEAT);
      gl.glColor3f(1.0f, 1.0f, 1.0f);
      for(int iStrip = -fExtent; iStrip <= fExtent; iStrip += fStep) {
          t = 0.0f;
          gl.glBegin(GLC.GL_TRIANGLE_STRIP);
      
              for(int iRun = fExtent; iRun >= -fExtent; iRun -= fStep) {
                  gl.glTexCoord2f(s, t);
                  gl.glColor3f(1.0f, 1.0f, 1.0f);
                  gl.glNormal3f(0.0f, 1.0f, 0.0f);   // All Point up
                  gl.glVertex3f(iStrip, y, iRun);
                  
                  gl.glTexCoord2f(s + texStep, t);
                  gl.glColor3f(1.0f, 1.0f, 1.0f);
                  gl.glNormal3f(0.0f, 1.0f, 0.0f);   // All Point up
                  gl.glVertex3f(iStrip + fStep, y, iRun);
                  
                  t += texStep;
               }
          gl.glEnd();
          s += texStep;
       }
   }
 
}
