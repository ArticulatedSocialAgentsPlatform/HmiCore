/*******************************************************************************
 * Copyright (C) 2009-2020 Human Media Interaction, University of Twente, the Netherlands
 *
 * This file is part of the Articulated Social Agents Platform BML realizer (ASAPRealizer).
 *
 * ASAPRealizer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License (LGPL) as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ASAPRealizer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ASAPRealizer.  If not, see http://www.gnu.org/licenses/.
 ******************************************************************************/
/* 
 * The render scene for the LightingShaders demo
 
 */

package hmi.graphics.scenegraphtest;


import hmi.util.ClockListener;

import hmi.graphics.scenegraph.*;

/**
 * TestScene 
 * @author Job Zwiers
 */
public class TestScene  extends GScene implements ClockListener {  
//   private VGLNode shadowScene;
//   private VGLNode nonShadowScene;
//   private VJoint sceneRoot;
//   private OpenGLState openglState;        // An object for some global OGL settings
//   private NavigationScene glNav;        // An object for keyboard based NavigationScene, of the camera viewpoint, and the position of the light
//   private Background background;  // Define the background for rendering
//   private Ground ground;      // A simple blue ground for the scene
//   private Torus torus;
//   
//   private FancyAnimator torusAnimator;
//   private FlyingOrb orb;
//   private RotationAnimator orbRotator;
//   
//   private Sphere sphere;
//   private GroundPlane groundplane;
//   
//   private PositionControl lightPositionControl;
//   
//   private SimpleLight light0;
//   
//   private ShadowState shadowState = new ShadowState(0.4f);
//   private RenderpassState renderpassState = new RenderpassState();
//   private float[] shadowMatrix = new float[16];
//   private float[] lightPos     = new float[] { -1.5f, 2.5f, 1.0f, 1.0f };  // Point source
//
//   private SkeletonInterpolator ski;
//   private VJoint avatarRootJoint;
   
  
   /**
    * Create a Scene object. The glDrawable will be linked
    * to the key-based NavigationScene.
    */
   public TestScene() { 
//   public TestScene(Component canvas) {       
      
//      sceneRoot = new VJoint("sceneRoot");
//      shadowScene = new VGLNode("ShadowScene", 8);
//      nonShadowScene = new VGLNode("NonShadowScene", 8);
//      sceneRoot.addChild(shadowScene.getRoot());
//      sceneRoot.addChild(nonShadowScene.getRoot());
//      
//      openglState = new OpenGLState();      
//      background = new Background(0.2f, 0.2f, 0.3f);  // Set a simple background color  
//      glNav = new  NavigationScene(canvas, sceneRoot);
//      glNav.setPosition(0.0f, 0.8f, 2.0f);   // The (initial) position of the camera.
//      light0 = new SimpleLight(GL.GL_LIGHT0, "Light0");    //Create a SimpleLight object, that encapsulates settings for OGL light GL_LIGHT0
//      //light0.setAmbientColor(0.3f, 0.3f, 0.3f);
//      SimpleLightState lightState = light0.getState();
//      lightState.setAmbientColor(0.0f, 0.0f, 0.0f);
//      
//      lightState.setSpecularColor(0.0f, 0.0f, 0.0f);
//      lightState.setDiffuseColor(0.7f, 0.7f, 0.7f);
//      light0.getRoot().setTranslation(lightPos);
//      SimpleLightGeometry lightGeom = light0.getGeometry();
//      lightGeom.setVisible(true);                   // Show the light object as a small sphere
//      lightState.setEnabled(true);
//
//      sceneRoot.addChild(light0.getRoot());
//      
//      float height = 0.0f;
//      ground = new Ground("Ground", height);  
//      nonShadowScene.addChild(ground);
//      //ground.addAppearance(new FixedColor(0.0f, 0.0f, 1.0f));    
//
//      
//      torus = new Torus("Torus", "crate.bmp");
//      torus.getRoot().setTranslation(0.0f, 0.5f, -2.0f);
//
//      shadowScene.addChild(torus);
////      torusAnimator = new FancyAnimator(torus);
////  
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
//      groundplane = new GroundPlane("GroundPlane", 0.0f, -0.39f, 0.0f, 1.0f, 1.0f);  
//      nonShadowScene.addChild(groundplane);    
      
//      lightPositionControl = new PositionControl(canvas);
//    
//      lightPositionControl.setVJoint(light0.getRoot());
//      light0.setShadowPlane( 0.0f, height,  0.0f ,
//                            10.0f, height,  0.0f ,
//                            5.0f, height, -5.0f );
//      light0.setShadowMatrix(shadowMatrix);
      
 
   
   }
   
    /**
    * The callback method that will be called by the clock, for every time tick.
    */
   public synchronized void initTime(double t) {  }
   
   /**
    * The callback method that will be called by the clock, for every time tick.
    */
   public synchronized void time(double t) {   
      
      
//      torusAnimator.time(t);
//      orbRotator.time(t);
//  
//
//      glNav.time(t);  
//      lightPositionControl.time(t);
//      //ski.time(t);
//      sceneRoot.calculateMatrices();
//       if (  timeCounter.cycleCounter()) {
//          hmi.util.Console.println("Avatar Skeleton:\n" + avatarRootJoint.showSkeleton(2));
//       }
   }
   
  
      
      
}     



