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


package hmi.graphics.opengl;

import hmi.util.ClockListener;

import java.awt.Component;
import java.awt.event.KeyListener;

import javax.media.opengl.*;
import javax.media.opengl.awt.*;



/**
 * A basic renderer with basic support for animation.
 * @author Job Zwiers 
 */
public class GLRendererV0  implements GLEventListener, ClockListener{
      
   //private static java.util.logging.Logger logger = java.util.logging.Logger.getLogger("hmi.graphics.jogl.gRendererV0");
   private GLAutoDrawable glDrawable;         // The GLDrawable where the 3D scene should be rendered
   private GLRenderObject scene;              // The render object containing the complete 3D "scene".
   private GLRenderContext glc;               // The context, containing the current GL context.
   private ClockListener clockListener;       // An optional object, reacting to clock ticks.
 
   private volatile double mediaTime;     // current media time, in seconds; set by clock thread, used by OpenGL thread   
   private volatile boolean joglInitialized = false; // keeps track of openGL/Jogl initialization
   
   // useVsync denotes whether the vsync mode of the graphics driver should be set on or off, 
   // provided the driver allows applications to set this property. Turning vsync off is useful (only) for performance measurements.
   private boolean useVsync = true; 
   
   public static final double DEFAULT_FOVY = 27.0;
   public static final double DEFAULT_NEAR = 1.0;
   public static final double DEFAULT_FAR = 256.0;
   private double fovy = DEFAULT_FOVY;      // field of view in y direction, specified in degrees(!)
   private double aspect;                   // width/height of viewport
   private int width, height;               // width and height of the viewport. (Usualy the same dimension as the window)
   private double left, right, bottom, top; // coordinates of clipping planes (measured at the "near" distance)
   private double near = DEFAULT_NEAR;      // distance to near clipping plane
   private double far = DEFAULT_FAR;        // distance to far clipping plane.
   // bits "lost" because of far/near setting: 2log(far/near). 
   
   
   /**
    * Create a new renderer, using an existing GLDrawable (i.e. a GLCanvas or GLJPanel) that should
    * have its capabilities set, like wheter antialiasing should be used, how many stencilbits etcetera.
    */
   public GLRendererV0(GLAutoDrawable glDrawable) {        
      this.glDrawable = glDrawable;
      if (glDrawable == null) throw new RuntimeException("RendererV0: null glDrawable");
     // glDrawable.setGL(new DebugGL(glDrawable.getGL()));

      glDrawable.addGLEventListener(this);
      glc = new GLRenderContext();
   }   
 
   /**
    * Create a new renderer, using a new GLCanvas with capabilities set to default values,
    * except for the antialiasing setting.
    * The numFSAA_Samples parameter determines the (FSAA) antialiasting seting: 0 or 1 means no antialiasing,
    * values like 2, 4, or 8 will use FSAA with the number of samples as specified. 
    * The number of stencilbits is specified as well. A 0 value here means that no stencil buffer is used
    */
   public GLRendererV0(int numFSAASamples, int numStencilBits) {           
      GLProfile glp = GLProfile.getDefault();    
      GLCapabilities caps = new GLCapabilities(glp);
      if (numFSAASamples < 2) {
         caps.setSampleBuffers(false); // disable full screen antialiasing
      } else {
         caps.setSampleBuffers(true); // enable full screen antialiasing (FSAA)
         caps.setNumSamples(numFSAASamples);       // number of samples for FSAA
         caps.setSampleBuffers(true);
      }
      if (numStencilBits > 0) caps.setStencilBits(numStencilBits);         
      glDrawable = new GLCanvas(caps);  
      glDrawable.addGLEventListener(this);
      glc = new GLRenderContext();
   }   
 
 
 
   /**
    * Create a new renderer, like RendererV0(numFSAA_Samples, 0), i.e. with FSAA specified
    * but no stencil buffer
    */
   public GLRendererV0(int numFSAA_Samples) {        
      this(numFSAA_Samples, 0);
   }   
 
 
   /**
    * Turn on the Jogl DebugGL mode.
    */
   public void useDebugGL() {
      glDrawable.setGL(new DebugGL3bc(glDrawable.getGL().getGL3bc())); 
   }
   
   /**
    * Adds a KeyListener to the internal GLCanvas or GLJPanel
    */
   public void addKeyListener(KeyListener listener) {
      if (glDrawable instanceof GLCanvas) {
         ((GLCanvas) glDrawable).addKeyListener(listener);
      } else if(glDrawable instanceof GLJPanel) {
         ((GLJPanel) glDrawable).addKeyListener(listener);
         
      } else {
          throw new IllegalArgumentException("JOGLRendere.addKeyListener called for unknown type of GLAutoDrawable" );
      }
   }
 
 
   /**
    * return the internal GLCanvas or GLJPanel, in the form of an AWT Component,
    * that could be added to an AWT or Swing Component.
    */
   public Component getAWTComponent() {
      return (Component) glDrawable;
   }
 
   /**
    * Sets the 3D scene object, to be rendered.
    */
   public void setScene(GLRenderObject scene) {
      this.scene = scene;     
   }
 
   /**
    * Sets theClockListener
    */
   public void setClockListener(ClockListener listener) {
      clockListener = listener;
   }
 
   /**
    * The time method required by the ClockListener interface;
    * It sets the current media time, and then causes a Jogl display() call.
    */
   public void initTime(double t) {
      mediaTime = t;
      if (clockListener != null) clockListener.initTime(t);
   }
 
   /**
    * The time method required by the ClockListener interface;
    * It sets the current media time, and then causes a Jogl display() call.
    */
   public void time(double t) {
      mediaTime = t;
      if (joglInitialized) { 
         if (clockListener != null) clockListener.time(mediaTime);
         glDrawable.display();
      }
   }
   
 
   /**
    * Sets the vsync mode
    */
   public void setVsync(boolean useVsync) {
      this.useVsync = useVsync;
   }
  
   /**
    * GLEventListener callback, called by a Jogl GLCanvas upon OpenGL context creation.
    * It initializes the render engine.
    */
   public void init(final GLAutoDrawable glDrawable) {
      joglInitialized = false; // reset to false when init is called again. 
      
   
      
      glc.setGL(glDrawable);
      glc.gl.setSwapInterval(useVsync ? 1 : 0);    
      if (scene == null) {
         return;
        // throw new RuntimeException("RendererV0.init(): null scene");  
      }
      scene.glInit(glc);     
      joglInitialized = true; // allow time callbacks
   }
      
   /**
    * Jogl callback for rendering the screen.
    * The current media time is forwarded to the scene, by calling its time() method,
    * thereafter, the scne is rendered, by means of calling its glRender() method. 
    */
   public void display(GLAutoDrawable glDrawable) {      
      
      glc.setGL(glDrawable);
      if (scene != null) scene.glRender(glc);        
   }
   
  

   /* required by GLEventListener */
   public void dispose(GLAutoDrawable drawable) { }


   /**
    * Sets the field of view in the Y direction, specified in degrees(!)
    */
   public void setFOVY(double fovy) {
      this.fovy = fovy;
   }
      

   /**
    * Sets the (positive) distance to the near clipping plane.
    */
   public void setNear(double near) {
      this.near = near;
   }
   
   /**
    * Sets the (positive) distance to the far clipping plane.
    */
   public void setFar(double far) {
      this.far = far;
   }

   private static final double FULLCIRCLE_DEGREES = 360.0;

   /* 
    * calculates the left, right, top and bottom values
    * from given values for near, width, height, and fovy
    */
   private void calculateFrustumFromFOVY() {
      aspect = (double)width/(double)height;
      top = near * Math.tan(fovy * Math.PI/FULLCIRCLE_DEGREES);
      bottom = -top;
      right = aspect * top;
      left = -right;
      //hmi.util.Console.println("left, right = " + left + ", " + right);
      
      //double wd = right-left;
//      left +=20;
//      right +=20;
//      left = right;
//      right = left + wd;
      //hmi.util.Console.println("left, right = " + left + ", " + right);
   }

   /**
    * GLEventListener method; Called when the glDrawable has been resized.
    */
   public void reshape(GLAutoDrawable glDrawable, int x, int y, int w, int h) {
      GL2 gl2 = glDrawable.getGL().getGL2();
      reshape2(gl2, x, y, w, h);
    }
   public void reshape2(GL2 gl2, int x, int y, int w, int h) {
      this.width = w;
      this.height = h;
      if(height <= 0)  height = 1;
      gl2.glViewport(0, 0, width, height);   
              
      gl2.glMatrixMode(GL2.GL_PROJECTION);        
      gl2.glLoadIdentity();  
      calculateFrustumFromFOVY();     
      gl2.glFrustum(left, right, bottom, top, near, far);    
      gl2.glMatrixMode(GL2.GL_MODELVIEW);    
      gl2.glLoadIdentity();         
   }
    
    
   /**
    * Should not be necessary, but can be called manually after all Jogl activity has finished,
    * in order to clean up temp resources. 
    */
   public void shutDown() 
   {
      GLProfile.shutdown(GLProfile.ShutdownType.COMPLETE);
   } 
    
         
}
