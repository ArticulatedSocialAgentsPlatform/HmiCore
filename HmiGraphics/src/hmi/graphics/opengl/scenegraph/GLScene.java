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

package hmi.graphics.opengl.scenegraph;

import hmi.animation.VJoint;
import hmi.graphics.opengl.GLRenderContext;
import hmi.graphics.opengl.GLRenderList;
import hmi.graphics.opengl.GLRenderObject;
import hmi.graphics.opengl.GLShape;
import hmi.graphics.opengl.GLSkinnedMesh;
import hmi.graphics.opengl.GLUtil;
import hmi.graphics.opengl.state.GLMaterial;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A GLScene is a top-level structure that combines various render lists, to be rendered by
 * an OpenGL capable render engine.
 * It includes:
 * 1) a List of VJoints, each acting as a root node for a VJoint based scene graph.
 * 2) a List of SkinnedMeshes, each to be linked to appropriate VJoint nodes.
 * 3) a GLRenderList, containing GLShapes, to be rendered. Linked to appropriate VJoint nodes.
 * @author Job Zwiers
 */
public class GLScene implements GLRenderObject
{
   
   private String id = "";
   
   /** The VJoint list determines the root VJoints for the scene graph.    */
   private ArrayList<VJoint> vjointRoots = new ArrayList<VJoint>();
   
   // We need a list of skinned meshes that reside somewhere inside the scene graph, so we can deform/update when needed.
   private ArrayList<GLSkinnedMesh> skinnedMeshList = new  ArrayList<GLSkinnedMesh>();
   
   /** The GLRenderList shapeList specifies GLRenderObjects to be rendered. */
   private GLRenderList shapeList = new GLRenderList();
 
   private static Logger logger = LoggerFactory.getLogger(GLScene.class.getName());

 
   private GLScene() { }
  
   /**
    * Creates a GLScene with allocated root, with specified name,
    * and with allocated shapeList, with specified capacity.
    */
   public GLScene(String id) {
      this.id = id;
   }
   
   public String getId() { return id; }
  
   /**
    * Adds a VJoint, as one of the scene graph roots
    */
   public void addVJointRoot(VJoint root) {
      vjointRoots.add(root);
   }
  
   /**
    * Returns the list with all scene graph roots
    */
   public List<VJoint> getVJointRoots() {
      return vjointRoots;
   }
  
  
   /**
    * returns a single VJoint that is put on top of all of the scene's root VJoints.
    * Note that for skinned VHs like Armandia, this includes not only the joints for the body and cloth parts,
    * but also the skeleton root joint.
    */
   public VJoint getToplevelVJoint() {
       VJoint sceneRoot = new VJoint(getId());
       for (VJoint root : getVJointRoots() ) {
           sceneRoot.addChild(root); 
       }
       return sceneRoot;  
   }
   
   /**
    * returns a single VJoint that is put on top of all of the scene's root VJoints except for the joint woth specified sid,
    * which is assumed to be the skeleton root
 
    */
   public VJoint getBodyPartsVJoint(String execludedSkeletonRootSid) {
       VJoint sceneRoot = new VJoint(getId());
       for (VJoint root : getVJointRoots() ) {
           if ( ! root.getId().equals(execludedSkeletonRootSid)) {
              sceneRoot.addChild(root); 
           }
       }
       return sceneRoot;  
   }
   
   
    /**
    * returns a single VJoint that is put on top of all of the scene's root VJoints that are included in the List
 
    */
   public VJoint getBodyPartsVJoint(List<String> includedIds) {
       VJoint sceneRoot = new VJoint(getId());
       for (VJoint root : getVJointRoots() ) {
           if ( includedIds.contains(root.getId())) {
              sceneRoot.addChild(root); 
           }
       }
       return sceneRoot;  
   }
   
  
   /**
    * Searches for a VJoint reachable from some of the VJoint root nodes.
    */
   public VJoint getVJoint(String partId) {
      for (VJoint vjrt : vjointRoots) {
         VJoint result = vjrt.getPart(partId);
         if (result != null) return result;
      }
      return null;
   }
  
   
   /**
    * Returns the list with all GLShapes for this GLScene
    */
   public GLRenderList getGLShapeList() {
      return shapeList;
   }
   
  
   
  
   public void prependGLShape(GLShape glShape) {
      shapeList.prepend(glShape);
   }
   
   /**
    * Adds some GLShapes to this GLScene. 
    */
   public void addGLShapes(GLRenderList glShapes) {
      
      shapeList.addAll(glShapes);
   }
   
   
   /**
    * Adds some GLShape to this GLScene.
    */
   public void addGLShape(GLShape glShape) {
      shapeList.add(glShape);
   //   glShape.linkToTransformMatrix(root.getGlobalMatrix());  
   }
   
   
   /**
    * Adds some SkinnedMeshes
    */
   public void addSkinnedMeshes(List<GLSkinnedMesh> skinnedMeshes) {   
      skinnedMeshList.addAll(skinnedMeshes);
   }
   
   
   

   
   private static final float RADIUS = 0.005f;
   private static final int GRID = 16;
   
   public void addSkinnedMeshJointMarkers() {
      addSkinnedMeshJointMarkers(RADIUS, GRID);
   }
   
   public void addSkinnedMeshJointMarkers(float radius, int grid) {
//      for (GLSkinnedMesh glsm : skinnedMeshList) {
//          glsm.addJointMarkers(radius, grid);
//      } 
      if (skinnedMeshList.size() > 0) {
          logger.debug("GLScene addSkinnedMeshJointMarkers for skinnedMesh 0");
          skinnedMeshList.get(0).addJointMarkers(radius, grid);  
      }
      
   }
   
   
   /**
    * sorts the shapeList, where transparency enabled shapes are put at the end of the list
    */
   public void sortGLShapeList() {
      int listSize = shapeList.size();
      
      GLRenderList newList = new GLRenderList(listSize);
      GLRenderList alphaList = new GLRenderList(listSize);
      
      for (int i=0; i<listSize; i++) {
           GLShape shape = (GLShape) shapeList.get(i); 
           int stateListSize = shape.getStateList().size();
           GLMaterial glMat = null;
           for (int j=0; j<stateListSize; j++) {
              GLRenderObject sc = shape.getStateList().get(j);
              if (sc instanceof GLMaterial) {
                 glMat = (GLMaterial) sc;
                 break;
              }
            
           }
           if (glMat != null) {
                  logger.debug(" GLMaterial found for GLShape");
                  if (glMat.getAlphaBlendingEnabled()) {
                     alphaList.add(shape);
                  } else {
                     newList.add(shape);  
                  }
                  
           } else {
                  logger.error(" No GLMaterial found for GLShape");
           }           
       }
       newList.addAll(alphaList);
       shapeList = newList;
   }
   
   
   
//    public void addVJointMarker(VJoint vj) {
//       GLShape markerShape = new GLShape();
//       markerShape.addGLGeometry(new GLNodeMarker());
//       markerShape.linkToTransformMatrix(vj.getGlobalMatrix());
//       shapeList.add(markerShape);
//    }
//    
//    
//    public void addVJointMarkers(VJoint rootJoint) {
//        addVJointMarker(rootJoint); 
//        for (VJoint child : rootJoint.getChildren()) {
//            addVJointMarkers(child);
//        }
//    }
   
   
   
   /**
    * OpenGL initialization.
    */
   @Override
   public void glInit(GLRenderContext glc) {
      shapeList.glInit(glc);
   }
   
   /**
    * Deforms all skinned meshes 
    */
   public void deform() {
      for (GLSkinnedMesh gsm : skinnedMeshList) {
          gsm.deform();  
      }
   }
   
   /**
    * returns the first GLSkinnedMesh, for testing purposes.
    */
   public GLSkinnedMesh getGLSkinnedMesh() {
       if (skinnedMeshList == null || skinnedMeshList.size() == 0) return null;
       return skinnedMeshList.get(0);
   }
   
   /**
    * returns the GLSkinnedMesh number i, for testing purposes.
    */
   public GLSkinnedMesh getGLSkinnedMesh(int i) {
       if (skinnedMeshList == null || skinnedMeshList.size() == 0) return null;
       return skinnedMeshList.get(i);
   }
   
/* ==================================================
     Methods for directly setting the morph targets
   ================================================== */
   
   /* temp
   */
   public   String[] getMorphTargets(int i)
   {
       if (i < 0 || i >=  skinnedMeshList.size()) return null;
       
       return skinnedMeshList.get(i).getMorphTargets();
   }
  
   public List<GLSkinnedMesh> getGLSkinnedMeshList() {
      return skinnedMeshList;
   }
   
   /**
    * Morph all skinned meshes
    */
   public void morph(String targetName, float weight) {
      for (GLSkinnedMesh gsm : skinnedMeshList) {
          gsm.morph(targetName, weight);
      }
   }
   
   /**
    * Morph all skinned meshes
    */
   public void morph(String[] targetNames, float[] weights) {
      for (GLSkinnedMesh gsm : skinnedMeshList) {
          gsm.morph(targetNames, weights);
      }
   }
   
    /**
    * Morph all skinned meshes
    */
   public void morph(int[] targets, float[] weights) {
      for (GLSkinnedMesh gsm : skinnedMeshList) {
          gsm.morph(targets, weights);
      }
   }
   
/* ======================================================
     Methods and attributes for maintaining a set of 
     desired morph targets.
     - Add and remove weight from each target using 
       addMorphTargets() and removeMorphTargets()
     - have GLScene execute the current set of targets
       and weights by calling doMorph()
   ================================================== */

   /** The set of morph targets to be set by doMorph, maintained
   through addMorphTargets and removeMorphTargets */
   private HashMap<String,Float> desiredMorphTargets = new HashMap<String,Float>();
   
   /** Perform morph as specified in desiredMorphTargets */
   public void doMorph()
   {
      String[] desiredTargetNames = new String[desiredMorphTargets.size()];
      float[] desiredTargetWeights = new float[desiredMorphTargets.size()];
      int i = 0;
      for (String targetName:desiredMorphTargets.keySet())
      {
         desiredTargetNames[i] = targetName;
         desiredTargetWeights[i] = desiredMorphTargets.get(targetName);
         i++;
      }
      morph(desiredTargetNames, desiredTargetWeights);
   }
  
   /** Add given weights for given morph targets to the list of desired targets */
   public void addMorphTargets(String[] targetNames, float[] weights)
   {
      float w = 0;
      for (int i = 0; i < targetNames.length; i++) 
      {
         w = weights[i];
         Float fl = desiredMorphTargets.get(targetNames[i]);
         if (fl!=null)
         {
            w+=fl.floatValue();
         }
         if (w==0)
         {
            desiredMorphTargets.remove(targetNames[i]);
         }
         else
         {
            desiredMorphTargets.put(targetNames[i],new Float(w));
         }
      }
   }

   /** Remove given weights for given morph targets from the list of desired targets */
   public void removeMorphTargets(String[] targetNames, float[] weights)
   {
      float w = 0;
      for (int i = 0; i < targetNames.length; i++) 
      {
         w = -weights[i];
         Float fl = desiredMorphTargets.get(targetNames[i]);
         if (fl!=null)
         {
            w+=fl.floatValue();
         }
         if (w==0)
         {
            desiredMorphTargets.remove(targetNames[i]);
         }
         else
         {
            desiredMorphTargets.put(targetNames[i],new Float(w));
         }
      }
   }

   /** Overwrite current morph targets with given targets */
   public void setMorphTargets(String[] targetNames, float[] weights)
   {
      desiredMorphTargets.clear();
      float w = 0;
      for (int i = 0; i < targetNames.length; i++) 
      {
         w = weights[i];
         if (w!=0)
         {
            desiredMorphTargets.put(targetNames[i],new Float(w));
         }
      }
   }
   

   
   /**
    * OpenGL rendering.
    */
   @Override
   public void glRender(GLRenderContext glc) {    
   }
   
   
   
//    /** The VJoint list determines the root VJoints for the scene graph.    */
//   private ArrayList<VJoint> vjointRoots = new ArrayList<VJoint>();
//   
//   // We need a list of skinned meshes that reside somewhere inside the scene graph, so we can deform/update when needed.
//   private ArrayList<GLSkinnedMesh> skinnedMeshList = new  ArrayList<GLSkinnedMesh>();
//   
//   /** The GLRenderList shapeList specifies GLRenderObjects to be rendered. */
//   private GLRenderList shapeList = new GLRenderList();
//   
   /* flag that determines the amount of detail for appendAttributesTo() and toString() */   
   private static boolean showShapelist = true;
   private static boolean showSkinnedMeshes = true;
   private static boolean showScenegraph = true;
   private static int showScenegraphDepth = 10000;
  
   /** Sets the showShapelist mode for toString()  */
   public static  void setShowShapelist(boolean show) {  showShapelist = show;  }
  
   /* denotes whether toString should show the shapelist or not */
   public boolean showShapelist() { return showShapelist;  }
      
   /** Sets the showSkeletons mode for toString()  */
   public static  void setShowSkinnedMeshes(boolean show) { showSkinnedMeshes = show;  }
  
   /* denotes whether toString should show skeletons or not */
   public boolean showSkinnedMeshes() { return showSkinnedMeshes;  }      
   
   /** Sets the showScenegraph mode for toString()  */
   public static  void setShowScenegraph(boolean show) {  showScenegraph = show;  }
  
   /* denotes whether toString should show the scenegraph or not */
   public boolean showScenegraph() { return showScenegraph;  }
   
   /** Sets the showScenegraph mode for toString()  */
   public static  void setShowScenegraphDepth(int depth) {  showScenegraphDepth = depth;  }
  
   /* denotes the max depth to which a scengraph tree will be shown */
   public int showScenegraphDepth() { return showScenegraphDepth;  }
   
   
   /*
    * Appends a string representation ogf GLScene to buf
    */
   public StringBuilder appendTo(StringBuilder buf, int tab) {
       GLUtil.appendNLSpacesString(buf, tab, "GLScene " + id);
       
       if (showScenegraph()) {
         
           GLUtil.appendNLSpacesString(buf, tab, " -------------- Scenegraph -------------- ");
           for (VJoint root : vjointRoots) {
               // GLUtil.appendNLSpacesString(buf, tab, " --- scenegraph Root ---\n");
               // root.appendTo(buf, tab+GLUtil.TAB);
                GLUtil.appendNLSpacesString(buf, tab, " --- skeleton/scenegraph for " + root.getName() + "  ---\n");
                root.appendSkeleton(buf, tab+GLUtil.TAB, 1, showScenegraphDepth());
                
           }
       }
      
       if (showSkinnedMeshes()) {
           GLUtil.appendNLSpacesString(buf, tab, " -------------- SkinnedMeshes --------------");
           for (GLSkinnedMesh skinm : skinnedMeshList) {
                 GLUtil.appendNLSpacesString(buf, tab, " --- SkinnedMesh ---\n");
                skinm.appendTo(buf, tab+GLUtil.TAB);
           }
       }
       if (showShapelist()) {
          GLUtil.appendNLSpacesString(buf, tab, " -------------- ShapeList -------------- ");
          shapeList.appendTo(buf, tab+GLUtil.TAB);
       }
       return buf;  
   }
   
   
   @Override
   public String toString() {
      StringBuilder buf = appendTo(new StringBuilder(), 0);
      return buf.toString();
   }
   
   
   
   
 
           
             
}
