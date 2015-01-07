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

package hmi.graphics.collada.scenegraph;
import hmi.graphics.collada.LookAt;
import hmi.graphics.collada.Matrix;
import hmi.graphics.collada.Rotate;
import hmi.graphics.collada.Scale;
import hmi.graphics.collada.Skew;
import hmi.graphics.collada.TransformNode;
import hmi.graphics.collada.Translate;
import hmi.graphics.scenegraph.GNode;
import hmi.math.Mat3f;
import hmi.math.Mat4f;
import hmi.math.Quat4f;
import hmi.math.Vec3f;

import java.util.List;



/** 
 * Translates a sequence of transforms
 * @author Job Zwiers
 */
public final class TransformTranslator {
   
   private static float[] m =Mat4f.getMat4f();   // the complete transform matrix  
   private static float[] t = Vec3f.getVec3f();    // Vec3f  translation vector 
   private static float[] q = Quat4f.getQuat4f();    // Quat4f rotation quaternion
   private static float[] svec = Vec3f.getVec3f(); // Vec3f scaling vector
   private static float[] smat = Mat3f.getMat3f(); // Mat3f scaling/skewing matrix
  
   private static int stage = 0; // 0 = Id, 1= T,  2 = T o R, 3 = T o R o Scale, 4= T o R o Skew, 5 = M
   
   private static final int STAGE0 = 0;
   private static final int STAGE1 = 1;
   private static final int STAGE2 = 2;
   private static final int STAGE3 = 3;
   private static final int STAGE4 = 4;
   private static final int STAGE5 = 5;
  
   
   /***/
   private TransformTranslator() {}
   
   
   /* Sets the matrix from the rotation, translation, scale thus far */
   private static void setMatrix() {
      if (stage <= STAGE3) {
         Mat4f.setFromTRSVec3f(m, t, q, svec);
      } else {
         Mat4f.setFromTRSMat3f(m, t, q, smat);       
      }
   }
   
   /**
    * Translates a list of TransformNodes in a transform matrix and, if possible,
    * a decomposition in a rotation, translation and scaling. The results are used to set the
    * transforms for a specified GNode.
    */
   public static void setTransform(List<TransformNode> transforms, GNode gnode) {
      
      Mat4f.setIdentity(m);
      Vec3f.set(t, 0.0f, 0.0f, 0.0f);
      Quat4f.setIdentity(q);
      Vec3f.set(svec, 1.0f, 1.0f, 1.0f);
      Mat3f.setIdentity(smat);
      stage = 0;
      // As long as transforms are specified in the order: Translation, Rotation, Scale/Skew,
      // we can maintain the factorization. Otherwise, a 4X4 matrix is created, and decomposed
      // at the end. This is encoded by "stages" For instance, stage 2 means: we have seen a rotation
      // possibly precede by a translation, but as yet no scaling. 
  
      if (transforms != null) {
         for (TransformNode transform : transforms) {
            if (transform instanceof Translate) {
               Translate tr = (Translate) transform;
               switch (stage) {
                  case STAGE0: Vec3f.set(t, tr.getTranslationVec3f()); stage = STAGE1;  break; // set translation vector
                  case STAGE1: Vec3f.add(t, tr.getTranslationVec3f()); break; // add translation vector
                  case STAGE2: Vec3f.add(t, Quat4f.transformVec3f(q, tr.getTranslationVec3f())); break; // add rotated vector
                  case STAGE3: case STAGE4: setMatrix();  Mat4f.mul(m, tr.getMat4f()); stage = STAGE5; break;
                  case STAGE5: Mat4f.mul(m, tr.getMat4f());
                  default: break; // should not happen
               }   
            } else if (transform instanceof Rotate ) {
               Rotate rt = (Rotate) transform;
               switch (stage) {
                  case STAGE0: case STAGE1: Quat4f.set(q, rt.getRotationQuat4f()); stage=STAGE2; break; // set rotation quaternion
                  case STAGE2: Quat4f.mul(q, rt.getRotationQuat4f());  break; // multiply with existing quaternion
                  case STAGE3: case STAGE4: setMatrix();   Mat4f.mul(m, rt.getMat4f()); stage = STAGE5; break;
                  case STAGE5: Mat4f.mul(m, rt.getMat4f());
                  default: break; // should not happen
               }
            } else if (transform instanceof Scale) {
               Scale sc = (Scale) transform;
               switch (stage) {
                  case STAGE0: case STAGE1: case STAGE2:   Vec3f.set(svec, sc.getScaleVec3f());  stage=STAGE3; break; // set scale vector
                  case STAGE3: Vec3f.pmul(svec, sc.getScaleVec3f()); break;  // multiply (pointwise) with existing scale vector
                  case STAGE4: setMatrix();    Mat4f.mul(m, sc.getMat4f()); stage = STAGE5; break;
                  case STAGE5: Mat4f.mul(m, sc.getMat4f());  
                  default: break; // should not happen
               }
            } else if (transform instanceof Skew) {
               Skew sk = (Skew) transform;
               switch (stage) {
                  case STAGE0: case STAGE1: case STAGE2: case STAGE3: case STAGE4:  setMatrix();   Mat4f.mul(m, sk.getMat4f()); stage = STAGE5; break;
                  case STAGE5: Mat4f.mul(m, sk.getMat4f()); 
                  default: break; // should not happen
               }          
            } else if (transform instanceof Matrix) {
               Matrix mt = (Matrix) transform;
               switch (stage) {
                  case STAGE0: Mat4f.set(m, mt.getMat4f()); stage = STAGE5; break;
                  case STAGE1: case STAGE2: case STAGE3: case STAGE4:  setMatrix(); Mat4f.mul(m, mt.getMat4f()); stage = STAGE5; break;
                  case STAGE5: Mat4f.mul(m, mt.getMat4f()); 
                  default: break; // should not happen
               }              
            } else if (transform instanceof LookAt) {
               LookAt lk = (LookAt) transform;
               switch (stage) {
                  case STAGE0: Vec3f.set(t, lk.getLookAtTranslation3f());
                          Quat4f.set(q, lk.getLookAtRotation4f()); 
                          stage = STAGE2; break;
                  case STAGE1: Vec3f.add(t, lk.getLookAtTranslation3f());
                          Quat4f.set(q, lk.getLookAtRotation4f()); 
                          stage = STAGE2; break;
                  case STAGE2: case STAGE3: case STAGE4: 
                          setMatrix();    Mat4f.mul(m, lk.getMat4f()); stage = STAGE5; break;
                  case STAGE5: Mat4f.mul(m, lk.getMat4f()); 
                  default: break; // should not happen
               }
            } else { // should not happen
               transform.getCollada().warning("Translator: unknown transform type, ignored");
            }
         }          
      }
      // Now we have either a decomposition (stage 0, 1, 2, 3, 4), or a matrix (stage 5)
      if (stage < STAGE5) {
//         System.out.println("TransformTranslator: \nt = " + Vec3f.toString(t) + 
//           "\nq = " + Quat4f.toString(q) + "\nsvec=" + Vec3f.toString(svec) +
//           "\nsmatrix=\n" + Mat3f.toString(smat));
         gnode.setTranslation(t);
         gnode.setRotation(q);
         // setting the scale will also set the scalingtype
         if (stage == STAGE3)  gnode.setScale(svec);
         if (stage == STAGE4)  gnode.setSkewMatrix(smat);
         //float[] lm = gnode.getLocalMatrix(); // not realy needed, but initialized the local matrix of gnode.   
        
      } else {
         gnode.setLocalTransform(m); // will also decompose and set scalingType, if possible.
         //float[] lm = gnode.getLocalMatrix(); // not realy needed, but initialized the local matrix of gnode.   
      }
    
   }
 
// 
//   /**
//    * provides the corresponding GNode transform type for a given Collada TransformNode
//    */
//   public GNode.TransformType getType(TransformNode tnode) {
//      if (tnode instanceof Translate) {
//          return GNode.TransformType.TRANSLATE;       
//      } else if (tnode instanceof Rotate) {
//          return GNode.TransformType.ROTATE;
//      } else if (tnode instanceof Scale) {
//          return GNode.TransformType.CONGRUENCE;
//      } else if (tnode instanceof Skew) {
//          return GNode.TransformType.AFFINE;
//      } else if (tnode instanceof Matrix) {
//          return GNode.TransformType.PROJECTIVE;
//      } else if (tnode instanceof LookAt) {
//          return GNode.TransformType.AFFINE;
//      } else { // should not happen
//          throw new RuntimeException("Translator: unknown TransformNode type"); 
//      }
//   }
// 

}
