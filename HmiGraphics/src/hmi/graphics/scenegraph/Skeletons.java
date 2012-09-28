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
