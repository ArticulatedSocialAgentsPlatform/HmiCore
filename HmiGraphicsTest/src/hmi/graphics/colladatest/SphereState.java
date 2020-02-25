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
/* @author Job Zwiers  
 * @version  0, revision $Revision$,
 * $Date: 2005/11/15 22:29:17 $      
 */

package hmi.graphics.colladatest;
import hmi.graphics.opengl.*;

/**
 * A simple Sphere object, rendered using direct mode OpenGL
 */
public class SphereState implements GLRenderObject {

  
  float[] mat_ambient = new float[] {1.0f, 1.0f, 1.0f};
  float[] mat_diffuse = new float[] {1.0f, 1.0f, 1.0f};
  float[] mat_specular = new float[] {0.0f, 0.0f, 0.0f};
  int texId;
  
  
   /**
    * Create a new Sphere object
    */
   public SphereState(String resourcedir) {
     
   }
 

 
   public void glInit(GLRenderContext gl) {

      String textureFile = "Orb.png";
     // String textureFile = "chromic.jpg";   
      texId = Texture.loadGLTexture(gl, "textures", textureFile, GLC.GL_LINEAR_MIPMAP_LINEAR, GLC.GL_RGB, false);
//      texId = Texture.loadGLTexture(gl, textureFile, GLC.GL_LINEAR_MIPMAP_LINEAR, GLC.GL_RGB, false);
      
   }
   
     
     
   public void glRender(GLRenderContext gl) {
      //System.out.println("SphereState.glRender");
//      hmi.util.Console.println("SphereState", -1, 200, "SphereState.glRender");
      //gl.glPushAttrib(GLC.GL_LIGHTING_BIT|GLC.GL_TEXTURE_BIT); // save lighting and texture attributes on the stack
      gl.glEnable(GLC.GL_TEXTURE_2D); 
      gl.glBindTexture(GLC.GL_TEXTURE_2D, texId);
      gl.glEnable(GLC.GL_LIGHTING);
      gl.glMaterialfv(GLC.GL_FRONT, GLC.GL_AMBIENT, mat_ambient);
      gl.glMaterialfv(GLC.GL_FRONT, GLC.GL_DIFFUSE, mat_diffuse);
      gl.glMaterialfv(GLC.GL_FRONT, GLC.GL_SPECULAR, mat_specular);
      gl.glMaterialf(GLC.GL_FRONT, GLC.GL_SHININESS, 4.0f);
      gl.glTexEnvi(GLC.GL_TEXTURE_ENV, GLC.GL_TEXTURE_ENV_MODE, GLC.GL_MODULATE);
      // gl.glTexEnvi(GLC.GL_TEXTURE_ENV, GLC.GL_TEXTURE_ENV_MODE, GLC.GL_REPLACE);
   } 
      
  
}
