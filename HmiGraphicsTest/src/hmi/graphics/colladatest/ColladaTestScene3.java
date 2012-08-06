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
public class ColladaTestScene3  implements GLRenderObject, ClockListener {  
   
   private OpenGLState openglState;        // An object for some global OGL settings
   
   private Background background;  // Define the background for rendering
   private Ground ground;      // A simple blue ground for the scene
   
   
   private SimpleLight light0;
   private float[] lightPos     = new float[] { -1.5f, 2.5f, 1.0f, 1.0f };  // Point source
   
  
   
  
   /**
    * Create a Scene object. The glDrawable will be linked
    * to the key-based NavigationScene.
    */
   public ColladaTestScene3(Component canvas) {       
      
     
      
      openglState = new OpenGLState();      
      background = new Background(0.2f, 0.2f, 0.3f);  // Set a simple background color  
      
      light0 = new SimpleLight(GLC.GL_LIGHT0, "Light0");    //Create a SimpleLight object, that encapsulates settings for OGL light GL_LIGHT0
      //light0.setAmbientColor(0.3f, 0.3f, 0.3f);
      SimpleLightState lightState = light0.getState();
      lightState.setAmbientColor(0.0f, 0.0f, 0.0f);
      
      lightState.setSpecularColor(0.0f, 0.0f, 0.0f);
      lightState.setDiffuseColor(0.7f, 0.7f, 0.7f);
      
      light0.getRoot().setTranslation(lightPos);
      
      lightState.setEnabled(true);

      
      
      float height = 0.0f;
      ground = new Ground("Ground", height);  
    
          
          
   
   }   
   
   private hmi.util.Console.Counter timeCounter = hmi.util.Console.getCounter("Time", -1, 200);
   
   
   /**
    * The callback method that will be called by the clock, initializing time.
    */
   public synchronized void initTime(double t) { 
     
   }
   
   
   /**
    * The callback method that will be called by the clock, for every time tick.
    */
   public synchronized void time(double t) {   
      

   }
   
   /**
    * OpenGL/Jogl initialization, with "current" OpenGL context gl.
    * Called when the OpenGL system is initialized, so, in principle, only once.
    */
   public synchronized void glInit(GLRenderContext glc) {                   
      openglState.glInit(glc);
      background.glInit(glc);
   //   light0.glInit(glc);
   
   }
   
   
   /**
    * OpenGL/Jogl render pass, called for every frame that is being rendered.
    */
   public synchronized void glRender(GLRenderContext glc) {  
      openglState.glRender(glc);
      background.glRender(glc);
      
//      lightPositionControl.glRender(glc);
//      light0.glRender(glc);
//      
    
   }  
      
      
}     



