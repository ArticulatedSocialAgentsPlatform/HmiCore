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
/* @author Job Zwiers  
 * @version  0, revision $Revision$,
 * $Date: 2005/11/15 22:29:17 $      
 */

package hmi.graphics.opengl.renderobjects;

import javax.media.opengl.*; 
import hmi.graphics.opengl.GLRenderContext;
import hmi.graphics.opengl.GLRenderObject;



/**
 * A GLRenderObject for seeting material parameters.
 */
public class MaterialState implements GLRenderObject {

  public float[] mat_emission = new float[] {0.0f, 0.0f, 0.0f, 1.0f};
  public float[] mat_ambient = new float[] {0.0f, 0.0f, 0.0f, 1.0f};
  public float[] mat_diffuse = new float[] {0.0f, 0.0f, 0.0f, 1.0f};
  public float[] mat_specular = new float[] {0.0f, 0.0f, 0.0f, 1.0f};
  
  /**
    * Create a new MaterialState
    */
   public MaterialState() {
   }
 
   public void glInit(GLRenderContext gl) {
      
   }
      
   public void glRender(GLRenderContext glc) {
      GL2 gl = glc.gl2;
//      gl.glEnable(GLC.GL_LIGHTING);
      //gl.glDisable(GLC.GL_LIGHTING);
      gl.glMaterialfv(GL.GL_FRONT, GL2.GL_EMISSION, mat_emission, 0);
      gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, mat_ambient, 0);
      gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE, mat_diffuse, 0);
      gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL2.GL_SPECULAR, mat_specular, 0);
      gl.glMaterialf(GL.GL_FRONT_AND_BACK, GL2.GL_SHININESS, 4.0f);
   } 
      
  
}
