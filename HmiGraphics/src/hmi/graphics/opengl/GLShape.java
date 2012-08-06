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
/**
 * @(#) GLShape.java
 * @version 1.0   1/3/2008
 * @author Job Zwiers
 */


package hmi.graphics.opengl;

import hmi.math.Mat4f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A GLShape is a GLRenderObject that encapsulates geometry (a GLRenderObject), a transform matrix,
 * and a GLState (again a GLRenderObject);
 * 
 */
public class GLShape implements GLRenderObject {

   

   protected GLRenderList glStateList = new GLRenderList();
   protected GLRenderList glGeometryList = new GLRenderList();
   private String name;
   protected float[] transformMatrix;      // a reference to a 16-float Mat4f array, in row major order
   private boolean visible = true;
   private static Logger logger = LoggerFactory.getLogger(GLShape.class.getName());

   
  
   

   public GLShape() {
      transformMatrix = Mat4f.getIdentity(); // 4X4 identity matrix
   }

   public GLShape(String name) {
      this.name = name;
      transformMatrix = Mat4f.getIdentity(); // 4X4 identity matrix
   }
 
 
   public String getId() {
      return name;
   }
 
   public String getInfo() {
       return "GLShape \"" + name + "\"  state/geoms=" + glStateList.size() + "/" + glGeometryList.size();
   }
 
 
   public GLRenderList getGeometryList() { return glGeometryList; }
   public GLRenderList getStateList() { return glStateList; }
   public void hide() { visible = false; }
   public void show() { visible = true; }
 
 
 
 
   /**
    * Adds some GLRenderObject that defines the visual appearance of this VirtualObject
    */
   public void addGLGeometry(GLRenderObject glGeometry) {
      glGeometryList.add(glGeometry);
   }
   
   /**
    * Adds some GLRenderObject that defines the visual appearance of this VirtualObject
    */
   public void addGLState(GLRenderObject glState) {
      glStateList.add(glState);
   }

//   /**
//    * Sets the transform matrix from a translation vector (a Vec3f) and
//    * a rotation quaternion (a Quat4f).
//    */
//   public void setTR(float[] translation, float[] rotation) {
//      Mat4f.setFromTR(transformMatrix, translation, rotation);
//   }

   /**
    * Sets a link to the specified matrix. The transpose of the latter will be used
    * as OpenGL transformation matrix, that is, the specified matrix should be
    * in row-major order. The matrix is not copied, so modifications to the matrix
    * will have the effect of animating this GLShape object.
    */
   public void linkToTransformMatrix(float[] transformMatrix) {
      this.transformMatrix = transformMatrix;
   }

   /**
    * OpenGL initialization.
    */
   @Override
   public void glInit(GLRenderContext gl) {
      glStateList.glInit(gl);
      glGeometryList.glInit(gl);

   }
   
   float[] mv = new float[16];
   /**
    * OpenGL rendering.
    */ 
   @Override
   public void glRender(GLRenderContext glc) {
      if (!visible) return;
      glc.gl2.glPushMatrix();
//         gl.glGetFloatv(GLC.GL_MODELVIEW_MATRIX, mv);
//         String premv = Mat4f.toString(mv);
//         String trafo = Mat4f.toString(transformMatrix);
        

         glc.gl2.glMultTransposeMatrixf(transformMatrix, 0); // since OpenGL expects column major order, we use the transposed matrix
         if (glc.getPass() != GLRenderContext.SHADOWPASS) glStateList.glRender(glc);
         glGeometryList.glRender(glc);

      glc.gl2.glPopMatrix();
     
   }
   
   
   public static final int STATE = 1;
   public static final int GEOM = 2;
   
 
   public void printInfo(int mod) {
       logger.info("GLShape \"" + name + "\"  state/geoms=" + glStateList.size() + "/" + glGeometryList.size());
       if ( (mod & STATE) != 0) {
          for (int i=0; i<glStateList.size(); i++) {
             GLRenderObject glo = glStateList.get(i); 
             logger.info("State: " + glo);
          }
       }
       if ((mod & GEOM) != 0) {
          for (int i=0; i<glGeometryList.size(); i++) {
             GLRenderObject glgeom = glGeometryList.get(i); 
             logger.info("Geom: " + glgeom);
          }
       }
      
   }
   
   
   
   public StringBuilder appendTo(StringBuilder buf, int tab) {
       GLUtil.appendSpaces(buf, tab);
       buf.append("GLShape");
       if ( ! glStateList.isEmpty() )  {
         GLUtil.appendNLSpacesString(buf, tab+GLUtil.TAB, "glStateList=\n");
         
         glStateList.appendTo(buf, tab+GLUtil.TAB); 
       }
       if ( ! glGeometryList.isEmpty() ) {
         GLUtil.appendNLSpacesString(buf, tab+GLUtil.TAB, "glGeometryList=\n");
          glGeometryList.appendTo(buf, tab+GLUtil.TAB); 
       }
       
       
      
       
       return buf;
   }

   @Override
   public String toString() {
     
     return appendTo(new StringBuilder(), 0).toString();

   }
   
  
}
