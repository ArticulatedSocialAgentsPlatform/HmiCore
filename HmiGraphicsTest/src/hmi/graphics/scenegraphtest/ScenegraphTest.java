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
package hmi.graphics.scenegraphtest;
import javax.swing.JFrame;
import java.awt.event.*;
//import hmi.graphics.opengl.*;
//import hmi.graphics.opengl.GLRenderObject;
//import hmi.util.ClockListener;
//import hmi.util.SystemClock;
//import hmi.util.Screen;
import hmi.util.Lib;
//import hmi.graphics.jogl.*;

/**
 * @author Job Zwiers
 */
public class ScenegraphTest implements  KeyListener  { 
   private JFrame jframe;                // The JFrame for the application
//   private GLCanvas glCanvas;            // The 3D enabled Canvas, inside the JFrame
//   private JOGLRenderer renderer;           // The module that renders the 3D scene
//   private GLTestScene scene;      // The 3D scene itself.
//   private SystemClock clock;            // The Clock that triggers rendering
//   private boolean useVsync = true;      // denotes whether vsync should be used or not. 
//   
//   public static java.util.logging.Logger logger = java.util.logging.Logger.getLogger("hmi.graphics.jogl");
   
   /**
    * Create a new ScenegraphTest JFrame with a 3D Canvas
    */
   public ScenegraphTest() {   
//      jframe = new JFrame("ScenegraphTest");  
//      jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//      jframe.setLocation(350, 150);
//      jframe.setSize(800, 600);          
//      
////      jframe.setUndecorated(true);
////      Screen.setFullScreen(jframe);
////      Screen.setInvisibleCursor(jframe);
//      
//      GLCapabilities cap = new GLCapabilities(); 
//      cap.setSampleBuffers(false); // enable or disable full screen antialiasing
//      cap.setNumSamples(4);       // full screen antialiasing/multisampling setting, in case antialiasing is enabled,  
//      cap.setStencilBits(1);         
//      glCanvas = new GLCanvas(cap);  
//      glCanvas.addKeyListener(this);  // we want to capture "escape key" events, in order to exit
//      //glCanvas.setAutoSwapBufferMode(false);
//      
//      // Create a Render module, triggered by a clock.
//      renderer = new JOGLRenderer(glCanvas); 
//      renderer.setFOVY(40.0);
//      renderer.setNear(0.3);
//      renderer.setFar(64);
//      clock = new SystemClock();
//      clock.addClockListener(renderer);
//        
//      // enable or disable VSync (also depends on driver settings, so might not have any effect)
//      // VSync disabled means lower quality for scrolling, and is only useful for framerate measurements.
//      renderer.setVsync(useVsync);
//      if (! useVsync) clock.setFramerateCounterFrame(jframe);  // no need for a framerate counter when vsync is active
//      scene = new GLTestScene(glCanvas);
//      renderer.setScene(scene);
//      renderer.setClockListener(scene);
//      jframe.add(glCanvas);
//      jframe.setVisible(true); // This will cause the Jogl to initialize.
//      glCanvas.requestFocusInWindow(); // requests the focus for keyboard input
//      clock.start(); // start the render loop.
   }   
   
   
   /** 
    * KeyListener callback: exit if the escape key was pressed
    */
   public void keyPressed(KeyEvent e)
   {
      if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
        System.exit(0);
      }
   } 
    
   /* KeyListener callback (ignored)  */
   public void keyReleased(KeyEvent e) {}
   
   /* KeyListener callback (ignored) */
   public void keyTyped(KeyEvent e) {}
   
   
   /**
    * Start the ScenegraphTest prog
    */
   public static void main(String[] arg) {
      Lib.extractIfRunningFrom("ScenegraphTest.jar");
      System.setProperty("sun.java2d.noddraw", "true"); // avoid potential interference with (non-Jogl) Java using direct draw
      //System.loadLibrary("opengl32");
       new ScenegraphTest();  
   }
         
}
