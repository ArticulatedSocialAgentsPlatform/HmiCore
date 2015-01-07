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

package hmi.graphics.scenegraph;

import hmi.animation.Hanim;
import hmi.animation.VJoint;
import hmi.animation.VJointUtils;
import hmi.math.Mat4f;
import hmi.math.Vec3f;

import java.util.List;

  
/**
 * A Utility class for Skeleton and HAnim related operations
 * @author Job Zwiers
 */
public final class Skeletons {  
  
  
  /***/
  private Skeletons() {}
  
   /*
    * Assuming that the (global) matrices contain the $V_i$ matrices that will be (left) multiplied with existing
    * inverse bind matrices, this method adapts translation vector t_i' = V_{parent(i)}(t_i) 
    */
   private static void adaptTranslationVectors(VJoint joint) {
       float[] localTranslation = Vec3f.getVec3f();
       float[] rotations = Mat4f.getMat4f();
       
       if (joint.getParent() != null) {
          joint.getTranslation(localTranslation);
          Mat4f.set(rotations, joint.getParent().getGlobalMatrix());
          Mat4f.transformVector(rotations, localTranslation);
          joint.setTranslation(localTranslation);
       }
       for (VJoint child : joint.getChildren()) adaptTranslationVectors(child);
   }
   
   /**
    * Defines the current pose to be the "neutral pose", i.e. the pose assumed when no joint rotations are set. 
    * This will typically be a pose like the HAnim pose. 
    */
   public static void setNeutralPoses(List<GNode> skeletonRoots, List<GSkinnedMesh> skinnedMeshList, List<GNode> roots) {  
      for (GNode skeletonRoot: skeletonRoots) {  
         VJoint rootJoint = skeletonRoot.getVJoint();
    
         adaptTranslationVectors(rootJoint);
         //rootJoint.calculateMatrices();  
      }
      
      for (GSkinnedMesh gsm : skinnedMeshList) {
          gsm.setBindPose();
      } 
      for (GNode skelRoot : skeletonRoots) {
         skelRoot.clearJointRotations(); 
      }      
   }
   
  
    /*******************************************/
    
   /** Blueguy specific: */ 
   
   
   
   public static void setHAnimPoseBLUEGUY(VJoint skeletonRoot) {
           
            float[] armDir = Vec3f.getVec3f(0f, -1f, 0f);
            VJointUtils.alignSegment(skeletonRoot, Hanim.r_shoulder, Hanim.r_elbow, armDir);
            VJointUtils.alignSegment(skeletonRoot,Hanim.l_shoulder, Hanim.l_elbow, armDir);
            if (skeletonRoot.getPartBySid(Hanim.l_thumb1) != null && skeletonRoot.getPartBySid(Hanim.l_thumb2) != null)
            {
                //hmi.util.Console.println("setHAnimPoseBLUEGUY align thumbs ");
                float[] thumbDir = Vec3f.getVec3f(0f, -1f, 1f);
                VJointUtils.alignSegment(skeletonRoot, Hanim.l_thumb1, Hanim.l_thumb2, thumbDir);
                VJointUtils.alignSegment(skeletonRoot, Hanim.l_thumb2, Hanim.l_thumb3, thumbDir);
                VJointUtils.alignSegment(skeletonRoot, Hanim.r_thumb1, Hanim.r_thumb2, thumbDir);
                VJointUtils.alignSegment(skeletonRoot, Hanim.r_thumb2, Hanim.r_thumb3, thumbDir);
            } 
   }
   
   
   
   public static void processHAnim(GNode humanRootGnode, String humanoidRootSid)
   {
      
       // GNode humanRootGnode = gscene.getPartBySid(humanoidRootSid);
       // humanRootGnode.removeLinearTransforms(); // optional ? (at least for blueguy not needed)

//        if (setToHAnim)
//        {
             VJoint skeletonRoot = humanRootGnode.getVJoint();
             
             setHAnimPoseBLUEGUY(skeletonRoot);
            
//            adaptTranslationVectors(skeletonRoot);
//            gsm.setBindPose();  <====== NO equivalent here, since Blueguy has no skinned mesh
//            humanRootGnode.clearJointRotations();
            
           //skeletonRoot.calculateMatrices(); // not necessary; added for similariry with seamless loader
           humanRootGnode.removeLinearTransforms(); // equivalent of setNeutralPose
//        }
        
     }
  
  
}
