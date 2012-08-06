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

/**
 * Scene is a render object that includes the complete &quot;scene&quot;
 * with sphere mapping. It also includes a simple form of keyboard based
 * NavigationScene: use the W/A/S/D keys for translation. 
 * PgUp and PgDown move in the vertical direction
 * The left/right/up/dn arrow keys are used for rotation of the light source.
 */
public class ColladaTestScene2  implements GLRenderObject, ClockListener {  
   private VGLNode shadowScene;
   private VGLNode nonShadowScene;
   private VJoint sceneRoot;
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
   
  
   /**
    * Create a Scene object. The glDrawable will be linked
    * to the key-based NavigationScene.
    */
   public ColladaTestScene2(Component canvas) {       
      
      sceneRoot = new VJoint("sceneRoot");
      shadowScene = new VGLNode("ShadowScene", 8);
      nonShadowScene = new VGLNode("NonShadowScene", 8);
      sceneRoot.addChild(shadowScene.getRoot());
      sceneRoot.addChild(nonShadowScene.getRoot());
      
      openglState = new OpenGLState();      
      background = new Background(0.2f, 0.2f, 0.3f);  // Set a simple background color  
      glNav = new  NavigationScene(canvas, sceneRoot);
      glNav.setPosition(0.0f, 0.8f, 2.0f);   // The (initial) position of the camera.
      light0 = new SimpleLight(GLC.GL_LIGHT0, "Light0");    //Create a SimpleLight object, that encapsulates settings for OGL light GL_LIGHT0
      //light0.setAmbientColor(0.3f, 0.3f, 0.3f);
      SimpleLightState lightState = light0.getState();
      lightState.setAmbientColor(0.0f, 0.0f, 0.0f);
      
      lightState.setSpecularColor(0.0f, 0.0f, 0.0f);
      lightState.setDiffuseColor(0.7f, 0.7f, 0.7f);
      light0.getRoot().setTranslation(lightPos);
      SimpleLightGeometry lightGeom = light0.getGeometry();
      lightGeom.setVisible(true);                   // Show the light object as a small sphere
      lightState.setEnabled(true);

      sceneRoot.addChild(light0.getRoot());
      
      float height = 0.0f;
      ground = new Ground("Ground", height);  
      nonShadowScene.addChild(ground);
      //ground.addAppearance(new FixedColor(0.0f, 0.0f, 1.0f));    

   
      
      lightPositionControl = new PositionControl(canvas);
    
      lightPositionControl.setVJoint(light0.getRoot());
      light0.setShadowPlane( 0.0f, height,  0.0f ,
                            10.0f, height,  0.0f ,
                            5.0f, height, -5.0f );
      light0.setShadowMatrix(shadowMatrix);
      
 
   
   }
   
   /**
    * The callback method that will be called by the clock, initializing time.
    */
   public synchronized void initTime(double t) { 
      glNav.initTime(t);
      lightPositionControl.initTime(t);
   }
   
   /**
    * The callback method that will be called by the clock, for every time tick.
    */
   public synchronized void time(double t) {   
      
      
//      torusAnimator.time(t);
//      orbRotator.time(t);
//  

      glNav.time(t);  
      lightPositionControl.time(t);
      //ski.time(t);
      sceneRoot.calculateMatrices();
//       if (  timeCounter.cycleCounter()) {
//          hmi.util.Console.println("Avatar Skeleton:\n" + avatarRootJoint.showSkeleton(2));
//       }
   }
   
   /**
    * OpenGL/Jogl initialization, with "current" OpenGL context gl.
    * Called when the OpenGL system is initialized, so, in principle, only once.
    */
   public synchronized void glInit(GLRenderContext gl) {                   
      openglState.glInit(gl);
      background.glInit(gl);
      light0.glInit(gl);
      shadowScene.glInit(gl);
      shadowState.glInit(gl);
      nonShadowScene.glInit(gl);
  //    ski.time(0.0f);
   }
   
   
   /**
    * OpenGL/Jogl render pass, called for every frame that is being rendered.
    */
   public synchronized void glRender(GLRenderContext gl) {  
      openglState.glRender(gl);
      background.glRender(gl);
      
      lightPositionControl.glRender(gl);
      light0.glRender(gl);
      
      nonShadowScene.glRender(gl);
     
      // First draw shadows
//      gl.glScalef(0.01f, 0.01f, 0.01f);
      shadowState.glRender(gl);
   
       
//      shadowScene.getRoot().setLocalMatrix(shadowMatrix); 
       
//      sceneRoot.calculateMatrices();
//      gl.setPass(GLRenderContext.SHADOWPASS);
//      shadowScene.glRender(gl);
        
      renderpassState.glRender(gl);
 
      shadowScene.getRoot().setLocalMatrix(Mat4f.getIdentity()); 
      sceneRoot.calculateMatrices();

      gl.setPass(GLRenderContext.RENDERPASS);
      shadowScene.glRender(gl);
      hmi.graphics.opengl.GLUtil.reportGLErrors(gl);
   }  
      
      
}     



