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

package hmi.graphics.opengl.scenegraph;

import hmi.animation.VJoint;
import hmi.graphics.opengl.GLBasicMesh;
import hmi.graphics.opengl.GLRenderList;
import hmi.graphics.opengl.GLShape;
import hmi.graphics.opengl.GLSkinnedMesh;
import hmi.graphics.opengl.state.GLMaterial;
import hmi.graphics.scenegraph.GMaterial;
import hmi.graphics.scenegraph.GMesh;
import hmi.graphics.scenegraph.GNode;
import hmi.graphics.scenegraph.GScene;
import hmi.graphics.scenegraph.GShape;
import hmi.graphics.scenegraph.GSkinnedMesh;
import hmi.graphics.scenegraph.VertexAttribute;
import hmi.graphics.scenegraph.VertexWeights;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Translates hmi.graphics.scenegraph components into hmi.graphics.opengl counterparts.
 * @author Job Zwiers
 */
public final class ScenegraphTranslator {
  
     private static Logger logger = LoggerFactory.getLogger(ScenegraphTranslator.class.getName());

     /***/
     private ScenegraphTranslator() {}
  
     /**
      * Translates a GScene into a GLScene
      */
     public static GLScene fromGSceneToGLScene(GScene gscene) {
      GLScene glScene = new GLScene(gscene.getId());
      List<GNode> rootGNodes = gscene.getRootNodes();
      for (GNode rootGNode : rootGNodes) {
        // System.out.println("rootnode: " + rootGNode.getId());
        // rootGNode.removeScaling(); // NB does not correctly handle reflection (via scaling with <0 factors)
         GLRenderList shapeList = new GLRenderList();
         List<GLSkinnedMesh> skinnedMeshes = new ArrayList<GLSkinnedMesh>();
         VJoint root = fromGNodeToVJoint(rootGNode, shapeList, skinnedMeshes);
         glScene.addVJointRoot(root);
         glScene.addGLShapes(shapeList);
         glScene.addSkinnedMeshes(skinnedMeshes);
        // hmi.util.Console.println("GSceneToGLScene, skinned meshes: " + skinnedMeshes.size());
         
         
      }
      return glScene;  
   }
  
  
  
   /**
    * Creates a new GLBasicMesh, from data obtained from the specified GMesh.
    * The GMesh type should be either Triangles or PolyList. 
    * In the latter case, the GMesh will be triangulated first.
    * The GMesh should include indices; they will be unified into
    * a shared index for all attributes, if necessary.
    * Note that triangulation and index unification have side effects on the GMesh parameter.
    */ 
   public static GLBasicMesh fromGMeshToGLBasicMesh(GMesh gmesh) {
      GLBasicMesh glbm = new GLBasicMesh();
      addGLBasicMeshAttributes(gmesh, glbm);
     // hmi.util.Console.println("GMeshToGLBasicMesh: " + gmesh.getId());
      glbm.setId(gmesh.getId());
      return glbm;
   }
      
      
   private static boolean addVertexWeightColors = false;
      
   public static GLSkinnedMesh fromGSkinnedMeshToGLSkinnedMesh(GSkinnedMesh gmesh) {
      GLSkinnedMesh glsm = new GLSkinnedMesh();
      addGLBasicMeshAttributes(gmesh, glsm);
      //hmi.util.Console.println("GSkinnedMeshToGLSkinnedMesh: " + gmesh.getId());
      glsm.setId(gmesh.getId());
      
      // SIDs and names are not essential for GLSkinnedmeshes, but are useful for debugging purposes
      glsm.setJointSIDs(gmesh.getJointSIDs());
      glsm.setJointNames(gmesh.getJointNames());
      glsm.setVJoints(gmesh.getVJoints());
      glsm.setInverseBindMatrices(gmesh.getInvBindMatrices());
      glsm.setParentIndex(gmesh.getParentIndex());
      
      VertexWeights vw = gmesh.getVertexWeights();
      glsm.setJointVertexWeights(vw.getJCounts(), vw.getJointIndices(), vw.getJointWeights());
      
      
      List<String> attrnames = gmesh.getVertexAttributeNameList(-1);
//      hmi.util.Console.println("gmesh " + gmesh.getId() + " attr names:" );
//      for (String atname: attrnames) hmi.util.Console.print(atname + "  ");
//      hmi.util.Console.println();
      
      // morphs...
      String[] morphTargets = gmesh.getMorphTargets();
     
      if (morphTargets != null) {
         
//         for (int i=0; i<morphTargets.length; i++) hmi.util.Console.println(morphTargets[i] + "  ");
//         hmi.util.Console.println("==============");
         
         gmesh.checkMorphTargetConsistency("texCoord1");
         
         
         glsm.setMorphTargets(morphTargets);
       
         float[][] morphData = gmesh.getMorphData("mcPosition");
         if (morphData == null) {
            logger.error(gmesh.getId() + ": Morphed mesh with null morph data");
         } else {
            glsm.setVertexCoordMorphData(morphData);
         }
         
        // float[][] morphData = gmesh.getMorphData("mcPosition");
         
      }       
      
      
      
      
      
      return glsm; 
   }

     
   /* Adds the GLBasicMesh attributes, derived from a GMesh or GSkinnedMesh, to glmesh, which is a GLBasicMesh, possibly even a GLSkinnedMesh */  
   private static void addGLBasicMeshAttributes(GMesh gmesh, GLBasicMesh glmesh) {  
//      gmesh.unifyIndices();  
//      if (gmesh.getMeshType() == GMesh.MeshType.Polylist || gmesh.getMeshType() == GMesh.MeshType.Polygons) {
//         gmesh.triangulate(); 
//      } 
//      if (gmesh.getMeshType() != GMesh.MeshType.Triangles) {
//         throw new IllegalArgumentException("ScenegraphTranslator: requires the GMesh to be Polygons, Polylist or Triangles");
//      }
      
 
//      gmesh.cleanupTriangles(0.0001f);
      
//      if ( ! gmesh.checkIndexIntegrity() ) {
//         throw new IllegalArgumentException("ScenegraphTranslator: GMesh with out-of-range indices");
//      }
//      
//      gmesh.cleanupTriangles(0.001f);
//      
//      if ( ! gmesh.checkIndexIntegrity() ) {
//         throw new IllegalArgumentException("ScenegraphTranslator: GMesh with out-of-range indices");
//      }
//      if ( ! gmesh.checkTriangleIntegrity(0.0f) ) {
//         hmi.util.Console.println("------------------> ScenegraphTranslator: problematic triangles <------------------");
//        // throw new IllegalArgumentException("ScenegraphTranslator: problematic triangles");
//      }
      glmesh.setIndexData(gmesh.getIndexData());
      java.util.ArrayList<VertexAttribute> attributeList = gmesh.getVertexAttributeList();

//          if (gmesh.getId().equals("Body_NG-mesh-4")) {
//            hmi.util.Console.println("Body_NG-mesh-4" + gmesh);
//          }


      if (addVertexWeightColors) { 
         GSkinnedMesh gm = (GSkinnedMesh)gmesh;
         float[][] colorCoding = new float[][]{{1.0f, 0.0f, 0.0f}, {0.0f, 0.0f, 1.0f}, {0.0f, 0.0f, 1.0f} };
         //String[] coloredJoints = new String[] { "Bone1", "Bone2", "Bone3"}; // use for woman23.dae
         //String[] coloredJoints = new String[] { "Bone1", "Bone2", "Bone3"};
         String[] coloredJoints = new String[] { "Bone2", "Bone1"};
         gm.addVertexWeightColors(true, coloredJoints, colorCoding);
         
      }

      for (VertexAttribute va : attributeList) {
         //String attrName = va.getName();
       //  if ( ! attrName.equals("Color")) {  // TODO: remove this restriction
            glmesh.addGLVertexAttribute(va);
       
       //  }
      }      
      
 
   }   
  
   /**
    * Translates a scenegraph GShape into an opengl GLShape
    */
   public static GLShape fromGShapeToGLShape(GShape gshape, List<GLSkinnedMesh> glSkinnedMeshes) {
      GMesh gmesh = gshape.getGMesh();
      GLShape glshape = new GLShape(gshape.getName());
      GMaterial gmaterial = gshape.getGMaterial();

      GLMaterial glmaterial = GMaterialTranslator.fromGMaterialToGLMaterial(gmaterial);   
      //GLRenderList  glmaterial = GMaterialTranslator.GMaterialToRenderList(gmaterial);        
        
      glshape.addGLState(glmaterial);
          
      if (gmesh instanceof GSkinnedMesh) {
         GSkinnedMesh gsm = (GSkinnedMesh)gmesh;
         GLSkinnedMesh glsm = fromGSkinnedMeshToGLSkinnedMesh(gsm);
         glshape.addGLGeometry(glsm);
         glSkinnedMeshes.add(glsm);
      } else {
         GLBasicMesh glbm = fromGMeshToGLBasicMesh(gmesh);
         glshape.addGLGeometry(glbm);
      }
      return glshape;  
   }
     
   /**
    * Translates a GNode base scenegraph into a VJoint based scenegraph plus 
    * a list of GLShape RenderObjects. The latter are linked to the VJoints,
    * via their transform matrices.
    * The root VJoint is returned. The GLRenderObjects are added to a shapeList.
    */
   public static VJoint fromGNodeToVJoint(GNode gnode, GLRenderList glShapeList, List<GLSkinnedMesh> glSkinnedMeshes ) {
      VJoint vjoint = gnode.getVJoint();
      float[] jointMatrix = vjoint.getGlobalMatrix();   
      List<GShape> gshapeList = gnode.getGShapes();
      if (gshapeList != null) {
         for (GShape gshape : gshapeList) {
            GLShape glshape = fromGShapeToGLShape(gshape, glSkinnedMeshes); 
            glshape.linkToTransformMatrix(jointMatrix);
            glShapeList.add(glshape);
         }
      }
      // recursively, process all child nodes:
      List<GNode> gnodeList = gnode.getChildNodes();
      if (gnodeList != null) {
         for (GNode gchild : gnodeList ) {
            //VJoint child = fromGNodeToVJoint(gchild, glShapeList, glSkinnedMeshes);
            fromGNodeToVJoint(gchild, glShapeList, glSkinnedMeshes);
            //vjoint.addChild(child);      // redundant: link should already be present
         }
      }
      return vjoint;
   }
             
}
