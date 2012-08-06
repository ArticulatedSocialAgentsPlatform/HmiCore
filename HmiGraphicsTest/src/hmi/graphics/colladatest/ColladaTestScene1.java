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
/* 
 * The render scene for the LightingShaders demo
 */

package hmi.graphics.colladatest;

import java.awt.Component;
import java.io.*;
import java.util.ArrayList;
import hmi.graphics.opengl.*;
import hmi.util.ClockListener;
import hmi.util.Resources;
import hmi.xml.*;
import hmi.graphics.collada.*;
import hmi.graphics.collada.scenegraph.*;
import hmi.graphics.scenegraph.*;
import hmi.graphics.opengl.*;
import hmi.graphics.opengl.state.*;
import hmi.graphics.opengl.scenegraph.*;
import hmi.animation.VJoint;
import hmi.animation.SkeletonInterpolator;
import hmi.math.*;
import hmi.graphics.util.*;
//import hmi.graphics.colladatest.renderobjects.*;

/**
 * Scene is a render object that includes the complete &quot;scene&quot;
 * with sphere mapping. It also includes a simple form of keyboard based
 * NavigationScene: use the W/A/S/D keys for translation. 
 * PgUp and PgDown move in the vertical direction
 * The left/right/up/dn arrow keys are used for rotation of the light source.
 */
public class ColladaTestScene1  implements GLRenderObject, ClockListener {  
   private VGLNode shadowScene;
   private VGLNode nonShadowScene;
   private VJoint sceneRoot;
   private VJoint viewpoint;
   
   
   private OpenGLState openglState;        // An object for some global OGL settings
   private NavigationScene glNav;        // An object for keyboard based NavigationScene, of the camera viewpoint, and the position of the light
   private Background background;  // Define the background for rendering
   private Ground ground;      // A simple blue ground for the scene
   
   private GroundPlane groundplane;
   
   private PositionControl lightPositionControl;
   
   private SimpleLight light0;
   
   private ShadowState shadowState = new ShadowState(0.4f);
   private RenderpassState renderpassState = new RenderpassState();
   private float[] shadowMatrix = new float[16];
   private float[] lightPos     = new float[] { -1.5f, 2.5f, 1.0f, 1.0f };  // Point source

   private SkeletonInterpolator ski;
   private VJoint avatarRootJoint;
   private VJoint skeletonRoot;
   private VJoint skelbranchRoot;
   private VJoint origRoot;
   GLScene avatarRenderStruct;
   
   public enum Avatar {Woman, Dirigent, Armandia, ArmandiaBoring, ArmandiaWalking};


    private GLShader specShader, fog2Shader, textureShader, multiTexShader;
  
   /**
    * Create a Scene object. The glDrawable will be linked
    * to the key-based NavigationScene.
    */
   public ColladaTestScene1(Component canvas) {       
      
    
    
      
      sceneRoot = new VJoint("sceneRoot");
      shadowScene = new VGLNode("ShadowScene", 8);
      nonShadowScene = new VGLNode("NonShadowScene", 8);
      sceneRoot.addChild(shadowScene.getRoot());
      sceneRoot.addChild(nonShadowScene.getRoot());
    //  viewpoint = new VJoint("viewpoint");
      
      openglState = new OpenGLState();      
      background = new Background(0.2f, 0.2f, 0.3f);  // Set a simple background color  
      glNav = new  NavigationScene(canvas, sceneRoot);
      glNav.setPosition(0.0f, 1.5f, 1.5f);   // The (initial) position of the camera.
      //sceneRoot.setTranslation(0.0f, -1.5f, 2.0f);
      light0 = new SimpleLight(GLC.GL_LIGHT0, "Light0");    //Create a SimpleLight object, that encapsulates settings for OGL light GL_LIGHT0
      //light0.setAmbientColor(0.3f, 0.3f, 0.3f);
      SimpleLightState lightState = light0.getState();
      lightState.setAmbientColor(0.0f, 0.0f, 0.0f);
      
      lightState.setSpecularColor(0.0f, 0.0f, 0.0f);
      lightState.setDiffuseColor(1f, 1f, 1f);
      light0.getRoot().setTranslation(lightPos);
      SimpleLightGeometry lightGeom = light0.getGeometry();
      lightGeom.setVisible(true);                   // Show the light object as a small sphere
      lightState.setEnabled(true);

      sceneRoot.addChild(light0.getRoot());
      
      float height = 0.0f;
      ground = new Ground("Ground", height);  
      nonShadowScene.addChild(ground);
      //ground.addAppearance(new FixedColor(0.0f, 0.0f, 1.0f));    
//
//      specShader = new GLShader("specular");
//      textureShader = new GLShader("textured", "TextureSampler", "U1I");
////       multiTexShader = new GLShader("earth");
//      
//      torus = new Torus("Torus", "crate.bmp");
//      
      
      
//      shadowScene.addChild(torus);
      
      //torus.getRoot().setTranslation(0.0f, 0.5f, -2.0f);
//
//     shadowScene.addChild(torus);
//      torusAnimator = new FancyAnimator(torus);
//  
//      orb = new FlyingOrb(0.1f, 32, 32);
//      orb.getRoot().setTranslation(0.0f, 0.2f, -2.0f);
//      shadowScene.addChild(orb);
//      orbRotator = new RotationAnimator(orb);
//      
//      
//      
//      sphere = new Sphere("Sphere", "orb.png",  0.6f, 32, 32);
//      sphere.getRoot().setTranslation(1.0f, 0.4f, -2.0f);
//      shadowScene.addChild(sphere);
//  
      groundplane = new GroundPlane("GroundPlane", 0.0f, -0.39f, -2.0f, 1.0f, 1.0f);  
     // nonShadowScene.addChild(groundplane);    
      
      lightPositionControl = new PositionControl(canvas);
    
      lightPositionControl.setVJoint(light0.getRoot());
      light0.setShadowPlane( 0.0f, height,  0.0f ,
                            10.0f, height,  0.0f ,
                            5.0f, height, -5.0f );
      light0.setShadowMatrix(shadowMatrix);
      
 
      
      
      String animFile = "animTest2.xml";
      
      
      String daeFile = null;
      String textureDir = null;
      String shaderDir = null;
      String colladaDir = null;
      String renamingFile = "woman23-renaming.txt";
      String mesh_node_id = null;
      String skeleton_root_id = null;
      String skeleton_branch_id = null;
      float[] tr = null;
      float scale = 1.0f;
      
     //Avatar avatar = Avatar.Woman;
      //Avatar avatar = Avatar.Dirigent;
      //Avatar avatar = Avatar.ArmandiaBoring;
      //Avatar avatar = Avatar.ArmandiaWalking;
      Avatar avatar = Avatar.Armandia;
      
      switch (avatar)  {
      
      case Woman:     daeFile = "woman23-toplevel.xml";  
                      colladaDir = "aXYZ_Metropoly2/dae";
                      textureDir ="aXYZ_Metropoly2/TextureMaps";
                      shaderDir="";
                      renamingFile = "woman23-renaming.txt";
                      mesh_node_id = "CWom0023-Mesh-node";
                      skeleton_root_id = "CWom0023-Pelvis-node";
                      skeleton_branch_id = "CWom0023-Bip-node";
                      tr = new float[] {0.0f, 0.0f, 0.0f};
                      scale = 0.6f;
                      break;
      case Dirigent:  daeFile = "dirigent_bindpose.DAE";  
                      textureDir = "textures";   // ----------fixme
                      shaderDir="shaders";
                      mesh_node_id = "Dirigent-node";
                      skeleton_root_id = "Bip01_Pelvis-node";
                      skeleton_branch_id = "Bip01-node";
                     // tr = new float[] {0.0f, 35.0f, -67.0f};
                      tr = new float[] {0.0f, 0.0f, -2.0f};
                      scale = 0.02f;
                      break;
      case Armandia:  daeFile = "armandia-toplevel.dae";  
                      colladaDir = "armandia/dae";
                      textureDir ="armandia/maps";
                      shaderDir="armandia/shaders";
                      renamingFile = "armandia-renaming.txt";
                      mesh_node_id = "Body_NG-node";
                     // mesh_node_id="Skirt_B_-node";
                      skeleton_root_id = "Bip01_Bassin-node";
                      skeleton_branch_id = "Bip01-node";
                    //  tr = new float[] {0.0f, 0.0f, 0.0f};
                      tr = new float[] {0.0f, 0.0f, 0.0f};
                      scale = 0.01f;
                      break;
       case ArmandiaBoring:    
                      daeFile = "armandia_boring_black.dae";
                      colladaDir = "armandia/dae";
                      textureDir ="armandia/maps";
                      shaderDir="armandia/shaders";
                      renamingFile = "armandia-renaming.txt";
                      mesh_node_id = "Body_NG-node";
                     // mesh_node_id="Skirt_B_-node";
                      skeleton_root_id = "Bip01_Bassin-node";
                      skeleton_branch_id = "Bip01-node";
                    //  tr = new float[] {0.0f, 0.0f, 0.0f};
                      tr = new float[] {0.0f, 0.0f, 0.0f};
                      scale = 0.01f;
                      break;
       case ArmandiaWalking:    
                      daeFile = "armandia_walking.dae";
                      colladaDir = "armandia/dae";
                      textureDir ="armandia/maps";
                      shaderDir="armandia/shaders";
                      renamingFile = "armandia-renaming.txt";
                      mesh_node_id = "Body_NG-node";
                     // mesh_node_id="Skirt_B_-node";
                      skeleton_root_id = "Bip01_Bassin-node";
                      skeleton_branch_id = "Bip01-node";
                    //  tr = new float[] {0.0f, 0.0f, 0.0f};
                      tr = new float[] {0.0f, 0.0f, 0.0f};
                      scale = 0.01f;
                      break;
      }
      
      GLTextureLoader.addTextureDirectory(textureDir);
      GLShaderProgramLoader.addShaderDirectory(shaderDir);
      
      ColladaReader.addColladaDirectory(colladaDir);
    //  try {       
         avatarRenderStruct = ColladaReader.getGLScene(daeFile, renamingFile, scale) ;
         if (avatarRenderStruct==null) {
             hmi.util.Console.println("Null avatarRenderStruct, quit...");
             //System.exit(0);  
         }
        // avatarRenderStruct = ColladaReader.read("dae", daeFile, renamingFile, scale);
//      } catch (IOException e) {
//           hmi.util.Console.println("Collada: " + e);  
//      }    

     
      //VJoint pull = avatarRenderStruct.getVJoint("Skirt_B_-node");  // VJoint with the avatar mesh geometry 
      avatarRootJoint = avatarRenderStruct.getVJoint(mesh_node_id);  // VJoint with the avatar mesh geometry 
      skeletonRoot = avatarRenderStruct.getVJoint(skeleton_root_id); // original skeleton root, child of skelbranchroot
      skelbranchRoot = avatarRenderStruct.getVJoint(skeleton_branch_id); // the renderstruct root that contains the skeleton (as a child)
     
      GLRenderList shapeList = avatarRenderStruct.getGLShapeList();
//      hmi.util.Console.println("shapeList size = " + shapeList.size());
//      for (int i=0; i<shapeList.size(); i++) {
//          GLShape shape = (GLShape) shapeList.get(i);
//          shape.printInfo();
//          //hmi.util.Console.println("GLShape " + shape.getInfo());  
//      }
      
//      GLShape wimpers = (GLShape) shapeList.get(14);
//      wimpers.printInfo(GLShape.STATE | GLShape.GEOM);

      
       
      VGLNode avatarNode = new VGLNode(avatarRootJoint, shapeList);
      
  
   
      shadowScene.addChild(avatarNode);
   
//      String[] additionalNodes = new String[]{"ey01-node","ey02-node","Stiletto_Heels_01-node","Stiletto_Heels_02-node","pull_B_-node","Skirt_B_-node","Necklace-node","hair_C_-node"};
//      for (String nextNode:additionalNodes) {
//         VGLNode vglNode = new VGLNode(avatarRenderStruct.getVJoint(nextNode), null);
//         shadowScene.addChild(vglNode);                
//      }
   
      float[] tv = new float[3];
      skeletonRoot.getTranslation(tv);


      if (skeletonRoot != null) {
           VJoint l_shoulder = skeletonRoot.getPart("l_shoulder");
           VJoint r_shoulder = skeletonRoot.getPart("r_shoulder");
           VJoint l_elbow = skeletonRoot.getPart("l_elbow");
           
           VJoint l_knee = skeletonRoot.getPart("l_knee");
           
           VJoint l_index0 = skeletonRoot.getPart("l_index0");
           VJoint l_index1 = skeletonRoot.getPart("l_index1");
           VJoint l_index2 = skeletonRoot.getPart("l_index2");
           VJoint l_index3 = skeletonRoot.getPart("l_index3");
           VJoint pelvis = skeletonRoot.getPart("sacroiliac");
           VJoint spine = skeletonRoot.getPart("vl5");
//           
//         
          float[] rot_r_shoulder =  Quat4f.getQuat4fFromAxisAngleDegrees(0.0f, 0.0f, 1.0f, -30.0f);
          if (r_shoulder != null)  r_shoulder.setRotation(rot_r_shoulder);
         
          float[] rot_l_shoulder =  Quat4f.getQuat4fFromAxisAngleDegrees(0.0f, 0.0f, 1.0f, 80.0f);
          if (l_shoulder != null)  l_shoulder.setRotation(rot_l_shoulder);
         
          float[] rotel_l_elbow = Quat4f.getQuat4fFromAxisAngleDegrees(1.0f, 0.0f, 0.0f, -70.0f);
          if (l_elbow != null)  l_elbow.setRotation(rotel_l_elbow);
//         
          float[] rotel_l_index0= Quat4f.getQuat4fFromAxisAngleDegrees(0.0f, 0.0f, 1.0f, -20.0f);
          if (l_index0 != null)  l_index0.setRotation(rotel_l_index0);
          if (l_index1 != null)  l_index1.setRotation(rotel_l_index0);
          if (l_index2 != null)  l_index2.setRotation(rotel_l_index0);
          if (l_index3 != null)  l_index3.setRotation(rotel_l_index0);
//         
          float[] rotPelvis =  Quat4f.getQuat4fFromAxisAngleDegrees(0.0f, 1.0f, 0.0f, 10.0f);        
//          if (pelvis != null) pelvis.rotate(rotPelvis);
         
          float[] rotSpine = Quat4f.getQuat4fFromAxisAngleDegrees(0.0f, 1.0f, 0.0f, 30.0f);     
//          if (spine != null) spine.rotate(rotSpine);
      }
          
          
      //Body_NG-mesh-morpher-Body_chest_L-0 
      //Body_NG-mesh-morpher-yeux_NG01-1 
      //Body_NG-mesh-morpher-A-2 
      //Body_NG-mesh-morpher-O-3 
      //Body_NG-mesh-morpher-U-4 
      //Body_NG-mesh-morpher-E-5 
      //Body_NG-mesh-morpher-I-6 
      //Body_NG-mesh-morpher-F-7 
      //Body_NG-mesh-morpher-P_B-8 
      //Body_NG-mesh-morpher-Smile01-9 
      //Body_NG-mesh-morpher-Pff-10 
      //Body_NG-mesh-morpher-T_severe-11 
      //Body_NG-mesh-morpher-T_severe02-12 
      //Body_NG-mesh-morpher-T_dent-13 
      //Body_NG-mesh-morpher-T_trist-14 
      //Body_NG-mesh-morpher-T_trist02-15 
      //Body_NG-mesh-morpher-HAA-16 
      //Body_NG-mesh-morpher-Face01-17 
      //Body_NG-mesh-morpher-Face02-18 
      //Body_NG-mesh-morpher-Hum-19 
      //Body_NG-mesh-morpher-Wink-20
      
      String[] targetNames= new String[] {"Body_NG-mesh-morpher-Body_chest_L-0", "Body_NG-mesh-morpher-A-2" };
      float[] weights = new float[] {1.0f, 0.01f};  
       
     //  avatarRenderStruct.morph(targetNames, weights);
   
                 
//       ski = SkeletonInterpolator.read("animations", animFile);
//         
//       ski.setTarget(avatarRootJoint);
     
   }   
   
   private hmi.util.Console.Counter timeCounter = hmi.util.Console.getCounter("Time", -1, 200);
   
   
   /**
    * The callback method that will be called by the clock, initializing time.
    */
   public synchronized void initTime(double t) { 
      //hmi.util.Console.println("initTime");
      glNav.initTime(t);
   }
   
   
   /**
    * The callback method that will be called by the clock, for every time tick.
    */
   public synchronized void time(double t) {   
      
     // hmi.util.Console.println("time");
//      torusAnimator.time(t);
//      orbRotator.time(t);
//  

      glNav.time(t);  
      lightPositionControl.time(t);
////      //ski.time(t);
      sceneRoot.calculateMatrices();
      if (skelbranchRoot != null) {
         //skeletonRoot.calculateMatrices();
         skelbranchRoot.calculateMatrices();
         avatarRenderStruct.deform();
      }
//       if (  timeCounter.cycleCounter()) {
//          hmi.util.Console.println("Avatar Skeleton:\n" + avatarRootJoint.showSkeleton(2));
//       }
   }
   
   /**
    * OpenGL/Jogl initialization, with "current" OpenGL context gl.
    * Called when the OpenGL system is initialized, so, in principle, only once.
    */
   public synchronized void glInit(GLRenderContext glc) {                   
      openglState.glInit(glc);
      background.glInit(glc);
      light0.glInit(glc);
      shadowScene.glInit(glc);
      shadowState.glInit(glc);
      nonShadowScene.glInit(glc);
      glNav.glInit(glc);
  //    ski.time(0.0f);
   }
   
   
   /**
    * OpenGL/Jogl render pass, called for every frame that is being rendered.
    */
   public synchronized void glRender(GLRenderContext glc) {  
     // hmi.util.Console.println("ColladaTestScen1/glRender");
      openglState.glRender(glc);
      background.glRender(glc);
      glNav.glRender(glc);
      lightPositionControl.glRender(glc);
      light0.glRender(glc);
//      
      nonShadowScene.glRender(glc);
     
//      // First draw shadows
////      gl.glScalef(0.01f, 0.01f, 0.01f);
      shadowState.glRender(glc);
   
       
      shadowScene.getRoot().setLocalMatrix(shadowMatrix); 
       
      sceneRoot.calculateMatrices();
      glc.setPass(GLRenderContext.SHADOWPASS);
      shadowScene.glRender(glc);
        
      renderpassState.glRender(glc);
 
      shadowScene.getRoot().setLocalMatrix(Mat4f.getIdentity()); 
      sceneRoot.calculateMatrices();
      

      glc.setPass(GLRenderContext.RENDERPASS);
      shadowScene.glRender(glc);
     // hmi.graphics.opengl.GLUtil.reportGLErrors(glc);
   }  
      
      
}     



