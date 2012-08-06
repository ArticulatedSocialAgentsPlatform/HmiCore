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
package hmi.graphics.colladatest;
import javax.swing.JFrame;
import java.awt.event.*;
import java.awt.*;
import hmi.graphics.opengl.*;
import hmi.graphics.jogl.*;
import hmi.graphics.lwjgl.*;
import hmi.graphics.opengl.GLRenderObject;
import hmi.util.ClockListener;
import hmi.util.SystemClock;
import hmi.util.Screen;
import hmi.util.Lib;
import java.util.*;

/**
 * 
 */
public class ColladaTest implements  KeyListener  { 
   private JFrame jframe;                // The JFrame for the application
   //private GLCanvas glCanvas;            // The 3D enabled Canvas, inside the JFrame
   private JOGLRenderer renderer;           // The module that renders the 3D scene
   private ColladaTestScene1 scene;      // The 3D scene itself.
   private SystemClock clock;            // The Clock that triggers rendering
   private boolean useVsync = false;      // denotes whether vsync should be used or not. 
   
   public static java.util.logging.Logger logger = java.util.logging.Logger.getLogger("hmi.graphics.jogl");
   
   /**
    * Create a new ColladaTest JFrame with a 3D Canvas
    */
   public ColladaTest() {   
      jframe = new JFrame("ColladaTest");  
      jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      jframe.setLocation(850, 150);
      jframe.setSize(800, 600);          
      hmi.util.Console.setSize(700, 1100);
//      jframe.setUndecorated(true);
//      Screen.setFullScreen(jframe);
//      Screen.setInvisibleCursor(jframe);
      
      // 
      int FSAA_samples = 4; // 0 or 1 means: no FSAA, 2, 4, or higher: FSAA as specified
      renderer = new JOGLRenderer(FSAA_samples, 2); 
      Component canvas = renderer.getAWTComponent();
      canvas.addKeyListener(this); 
      renderer.setFOVY(40.0);
      renderer.setNear(0.05); // was 0.3
      renderer.setFar(64);  // was 64
      clock = new SystemClock();
      clock.addClockListener(renderer);
        
      // enable or disable VSync (also depends on driver settings, so might not have any effect)
      // VSync disabled means lower quality for scrolling, and is only useful for framerate measurements.
      renderer.setVsync(useVsync);
      if (! useVsync)
       clock.setFramerateCounterFrame(jframe);  // no need for a framerate counter when vsync is active
      scene = new ColladaTestScene1(canvas);
      renderer.setScene(scene);
      renderer.setClockListener(scene);
      
      jframe.add(canvas);
      jframe.setVisible(true); // This will cause the Jogl to initialize.
      canvas.requestFocusInWindow(); // requests the focus for keyboard input
      clock.start(); // start the render loop.
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
    * Start the ColladaTest prog
    */
   public static void main(String[] arg) {
      // Lib.extractIfRunningFrom("ColladaTest.jar");
      //System.loadLibrary("opengl32");
      System.setProperty("sun.java2d.noddraw", "true"); // avoid potential interference with (non-Jogl) Java using direct draw
      java.util.List<String> params = new ArrayList<String>();
      params.add("-Xms128m");
      params.add("-Xmx1024m");
      if (! Lib.startProcessIfRunningFrom("ColladaTest.jar", "hmi.graphics.colladatest.ColladaTest", params ) ) {    
         //System.out.println("ColladaTest starting ...");
         new ColladaTest();  
      }
   }
         
}
