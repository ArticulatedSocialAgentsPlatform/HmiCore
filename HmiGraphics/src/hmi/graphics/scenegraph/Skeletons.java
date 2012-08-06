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
import hmi.math.Mat4f;
import hmi.math.Quat4f;
import hmi.math.Vec3f;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

  
/**
 * A Utility class for Skeleton and HAnim related operations
 * @author Job Zwiers
 */
public final class Skeletons {  
  
  
  private static Logger logger = LoggerFactory.getLogger(Skeletons.class.getName());
  /***/
  private Skeletons() {}
  
   
  
  
     private static void alignSegment1(VJoint skeletonRoot, String parentSid, String childSid, float[] dir)
    {
        Vec3f.normalize(dir);
        VJoint parentJoint =skeletonRoot.getPartBySid(parentSid);
        VJoint childJoint = skeletonRoot.getPartBySid(childSid);
        float[] a = childJoint.getRelativePositionFrom(parentJoint);
        float[] q = Quat4f.getFromVectors(a, dir);
        parentJoint.setRotation(q);
    }
  
  
   /* auxiliary method for aligning some body segment inside a skeleton structure with a specified direction vector */
   /* The parent joint is rotated */
   private static void alignSegment(VJoint skeletonRoot, String parentSid, String childSid, float[] vec) {
      VJoint parent = skeletonRoot.getPartBySid(parentSid);
      VJoint child = skeletonRoot.getPartBySid(childSid);
      if (parent != null && child != null) {
         float[] a = child.getRelativePositionFrom(parent);
         float[] q = Quat4f.getFromVectors(a, vec);
//         hmi.util.Console.println("====================================");
//         hmi.util.Console.println("align " + parentSid + "--" + childSid + " with " + Vec3f.toString(vec));
//         hmi.util.Console.println("relative position: " + Vec3f.toString(a, 4, 3) + " rotation: " + Quat4f.explainQuat4f(q, 4, 3));
         
         float[] r = Quat4f.getQuat4f();
         parent.getPathRotation(null, r);
         float[] rinv = Quat4f.getQuat4f();
         Quat4f.conjugate(rinv, r);
         float[] s = Quat4f.getQuat4f();
       
         Quat4f.mul(s, rinv, q);
         Quat4f.mul(s, r);
         //parent.rotate(s); // pre multiplies parent rotation with s.// seems to work, but probably just luck
         parent.insertRotation(s); // post multiplies parent rotation with s.
         
//         Quat4f.transformVec3f(q, a);
//         hmi.util.Console.println("rotated relative position: " + Vec3f.toString(a, 4, 3) + " new relative position:" +  Vec3f.toString(ar, 4, 3));
         
      } else {
         logger.error("No " + parentSid  + " or " + childSid + " for skeleton " + skeletonRoot.getName());
      }
   }
  
  
   /* auxiliary method for aligning some body segment inside a skeleton structure with a specified direction vector */
   /* The parent joint is rotated, the grandchild joint is rotated an equal amount backwards, so that just the segments,
  / * starting at parent, up to but not including grandchild are rotated "in isolation" */
   private static void alignIsolatedSegments(VJoint skeletonRoot, String parentSid, String childSid, String grandchildSid, float[] vec) {
      VJoint parent = skeletonRoot.getPartBySid(parentSid);
      VJoint child = skeletonRoot.getPartBySid(childSid);
      VJoint grandchild = skeletonRoot.getPartBySid(grandchildSid);
      if (parent != null && child != null) {
         float[] a = child.getRelativePositionFrom(parent);
         float[] q = Quat4f.getFromVectors(a, vec);
//         hmi.util.Console.println("====================================");
//         hmi.util.Console.println("align " + parentSid + "--" + childSid + " with " + Vec3f.toString(vec));
//         hmi.util.Console.println("relative position: " + Vec3f.toString(a, 4, 3) + " rotation: " + Quat4f.explainQuat4f(q, 4, 3));
         
         float[] r = Quat4f.getQuat4f();
         parent.getPathRotation(null, r);
         float[] rinv = Quat4f.getQuat4f();
         Quat4f.conjugate(rinv, r);
         float[] s = Quat4f.getQuat4f();
       
         Quat4f.mul(s, rinv, q);
         Quat4f.mul(s, r);
         parent.rotate(s);
         Quat4f.inverse(s);
         grandchild.rotate(s);
         
//         Quat4f.transformVec3f(q, a);
//         hmi.util.Console.println("rotated relative position: " + Vec3f.toString(a, 4, 3) + " new relative position:" +  Vec3f.toString(ar, 4, 3));
         
      } else {
         logger.error("No " + parentSid  + " or " + childSid + " for skeleton " + skeletonRoot.getName());
      }
   }
  
  
    
   /**
    * Tries to set the skeleton in the HAnim neutral pose. 
    * It is assumed that joints have joint sids according to the HAnim standard.
    * 
    * Assumes the ankles are aligned correctly for 'stable' standing, re-aligns them so that the ankle stance is maintained.
    */
   public static void setHAnimPose(VJoint skeletonRoot) {
      // NB the order of setting the alignments is important: work from root to leaf nodes
      float[] downVec = Vec3f.getVec3f(0f, -1f, 0f);

      // left arms/hand/fingers:
      alignSegment(skeletonRoot, hmi.animation.Hanim.l_shoulder, hmi.animation.Hanim.l_elbow, downVec);
      alignSegment(skeletonRoot, hmi.animation.Hanim.l_elbow, hmi.animation.Hanim.l_wrist, downVec);
      
      alignSegment(skeletonRoot, hmi.animation.Hanim.l_index1, hmi.animation.Hanim.l_index2, downVec);
      alignSegment(skeletonRoot, hmi.animation.Hanim.l_index2, hmi.animation.Hanim.l_index3, downVec);
      alignSegment(skeletonRoot, hmi.animation.Hanim.l_index3, hmi.animation.Hanim.l_index_distal_tip, downVec);
      
      alignSegment(skeletonRoot, hmi.animation.Hanim.l_middle1, hmi.animation.Hanim.l_middle2, downVec);
      alignSegment(skeletonRoot, hmi.animation.Hanim.l_middle2, hmi.animation.Hanim.l_middle3, downVec);
      alignSegment(skeletonRoot, hmi.animation.Hanim.l_middle3, hmi.animation.Hanim.l_middle_distal_tip, downVec);
      
      alignSegment(skeletonRoot, hmi.animation.Hanim.l_ring1, hmi.animation.Hanim.l_ring2, downVec);
      alignSegment(skeletonRoot, hmi.animation.Hanim.l_ring2, hmi.animation.Hanim.l_ring3, downVec);
      alignSegment(skeletonRoot, hmi.animation.Hanim.l_ring3, hmi.animation.Hanim.l_ring_distal_tip, downVec);
      
      alignSegment(skeletonRoot, hmi.animation.Hanim.l_pinky1, hmi.animation.Hanim.l_pinky2, downVec);
      alignSegment(skeletonRoot, hmi.animation.Hanim.l_pinky2, hmi.animation.Hanim.l_pinky3, downVec);
      alignSegment(skeletonRoot, hmi.animation.Hanim.l_pinky3, hmi.animation.Hanim.l_pinky_distal_tip, downVec);
      
      // right arms/hand/fingers:
      alignSegment(skeletonRoot, hmi.animation.Hanim.r_shoulder, hmi.animation.Hanim.r_elbow, downVec);
      alignSegment(skeletonRoot, hmi.animation.Hanim.r_elbow, hmi.animation.Hanim.r_wrist, downVec);
      
      alignSegment(skeletonRoot, hmi.animation.Hanim.r_index1, hmi.animation.Hanim.r_index2, downVec);
      alignSegment(skeletonRoot, hmi.animation.Hanim.r_index2, hmi.animation.Hanim.r_index3, downVec);
      alignSegment(skeletonRoot, hmi.animation.Hanim.r_index3, hmi.animation.Hanim.r_index_distal_tip, downVec);
      
      alignSegment(skeletonRoot, hmi.animation.Hanim.r_middle1, hmi.animation.Hanim.r_middle2, downVec);
      alignSegment(skeletonRoot, hmi.animation.Hanim.r_middle2, hmi.animation.Hanim.r_middle3, downVec);
      alignSegment(skeletonRoot, hmi.animation.Hanim.r_middle3, hmi.animation.Hanim.r_middle_distal_tip, downVec);
      
      alignSegment(skeletonRoot, hmi.animation.Hanim.r_ring1, hmi.animation.Hanim.r_ring2, downVec);
      alignSegment(skeletonRoot, hmi.animation.Hanim.r_ring2, hmi.animation.Hanim.r_ring3, downVec);
      alignSegment(skeletonRoot, hmi.animation.Hanim.r_ring3, hmi.animation.Hanim.r_ring_distal_tip, downVec);
      
      alignSegment(skeletonRoot, hmi.animation.Hanim.r_pinky1, hmi.animation.Hanim.r_pinky2, downVec);
      alignSegment(skeletonRoot, hmi.animation.Hanim.r_pinky2, hmi.animation.Hanim.r_pinky3, downVec);
      alignSegment(skeletonRoot, hmi.animation.Hanim.r_pinky3, hmi.animation.Hanim.r_pinky_distal_tip, downVec);
   
      // legs/feet:
      // align hip-knee with downvec, but correct "backwards" at the ankle joints (so we use alignIsolatedSegments rather than alignSegment)
      // so, we are assuming that ankles/feet were correctly positioned on the ground already, and we don't want to change anything there.
      // For Armandia, this takes into account the stilleto "high heels" position of the feet.
      alignIsolatedSegments(skeletonRoot, hmi.animation.Hanim.l_hip, hmi.animation.Hanim.l_knee, hmi.animation.Hanim.l_ankle, downVec);
      alignIsolatedSegments(skeletonRoot, hmi.animation.Hanim.r_hip, hmi.animation.Hanim.r_knee, hmi.animation.Hanim.r_ankle, downVec);
   }
   
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
         rootJoint.calculateMatrices();  
      }
      
      for (GSkinnedMesh gsm : skinnedMeshList) {
          gsm.setBindPose();
      } 
      for (GNode skelRoot : skeletonRoots) {
         skelRoot.clearJointRotations(); 
      }      
   }
   
  
    /*******************************************/
    
   public static void processHAnim(GNode humanRootGnode, String humanoidRootSid)
   {
      
       // GNode humanRootGnode = gscene.getPartBySid(humanoidRootSid);
        humanRootGnode.removeLinearTransforms(); // optional ? (at least for blueguy not needed)

//        if (setToHAnim)
//        {
            VJoint skeletonRoot = humanRootGnode.getVJoint();
            float[] armDir = Vec3f.getVec3f(0f, -1f, 0f);
            alignSegment(skeletonRoot, Hanim.r_shoulder, Hanim.r_elbow, armDir);
            alignSegment(skeletonRoot,Hanim.l_shoulder, Hanim.l_elbow, armDir);
            if (humanRootGnode.getPartBySid(Hanim.l_thumb1) != null && humanRootGnode.getPartBySid(Hanim.l_thumb2) != null)
            {
                float[] thumbDir = Vec3f.getVec3f(0f, -1f, 1f);
                alignSegment(skeletonRoot, Hanim.l_thumb1, Hanim.l_thumb2, thumbDir);
                alignSegment(skeletonRoot, Hanim.l_thumb2, Hanim.l_thumb3, thumbDir);
                alignSegment(skeletonRoot, Hanim.r_thumb1, Hanim.r_thumb2, thumbDir);
                alignSegment(skeletonRoot, Hanim.r_thumb2, Hanim.r_thumb3, thumbDir);
            }
            skeletonRoot.calculateMatrices(); // not necessary; added for similariry with seamless loader
            humanRootGnode.removeLinearTransforms(); // equivalent of setNeutralPose
//        }
        
     }
  
  
}
