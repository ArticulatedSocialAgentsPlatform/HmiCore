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
/**
 * @(#) GLShape.java
 * @version 1.0   1/3/2008
 * @author Job Zwiers
 */


package hmi.graphics.opengl;

import hmi.math.Mat4f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import hmi.graphics.opengl.state.GLMaterial;

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
      //hmi.util.Console.println("GLShape " + name);
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


    /**
     * Searches for a GLMaterial with the specified id and returns it, or null when not found
     */
    public GLMaterial getGLMaterial(String id) {
     
        for (int j=0; j< glStateList.size(); j++) {
            GLRenderObject state = glStateList.get(j);
             
            if (state instanceof GLMaterial) {
                 GLMaterial material = (GLMaterial) state;
                 String matName = material.getName();
                // hmi.util.Console.println("material: " + matName);
                 if (matName.equals(id)) {
                      return material;
                 }
            }
        } 
        return null;
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
