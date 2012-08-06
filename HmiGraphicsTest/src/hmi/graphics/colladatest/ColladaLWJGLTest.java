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
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import hmi.graphics.opengl.*;
import hmi.graphics.lwjgl.*;
import hmi.graphics.opengl.GLRenderObject;
import hmi.util.ClockListener;
import hmi.util.SystemClock;
import hmi.util.Screen;
import hmi.util.Lib;


/**
 * 
 */
public class ColladaLWJGLTest implements  KeyListener, ActionListener { 
   private JFrame jframe;                // The JFrame for the application
   //private GLCanvas glCanvas;            // The 3D enabled Canvas, inside the JFrame
   private LWJGLRenderer1 renderer;           // The module that renders the 3D scene
   private ColladaTestScene1 scene;      // The 3D scene itself.
   private SystemClock clock;            // The Clock that triggers rendering
   private boolean useVsync = false;      // denotes whether vsync should be used or not. 
   
   public static java.util.logging.Logger logger = java.util.logging.Logger.getLogger("hmi.graphics.jogl");
   
   /**
    * Create a new ColladaLWJGLTest JFrame with a 3D Canvas
    */
   public ColladaLWJGLTest() {   
      jframe = new JFrame("ColladaLWJGLTest");  
      // jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      jframe.addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent e) {
            System.out.println("window closing...");
            destroy();
         }
      });
      jframe.setLocation(350, 150);
      int width = 800;
      int height = 600;
      jframe.setSize(width, height);          
      
//      jframe.setUndecorated(true);
//      Screen.setFullScreen(jframe);
//      Screen.setInvisibleCursor(jframe);
      
      // 
      int FSAA_samples = 1; // 0 or 1 means: no FSAA, 2, 4, or higher: FSAA as specified
      renderer = new LWJGLRenderer1(FSAA_samples, 1, width, height); 
     // Component canvas = renderer.getAWTComponent();
      Canvas canvas = new Canvas();
 //     canvas.setSize(width, height);
      canvas.addKeyListener(this); 
      jframe.addKeyListener(this);
      renderer.setFOVY(40.0);
      renderer.setNear(0.3);
      renderer.setFar(64);
      clock = new SystemClock();
      clock.addClockListener(renderer);
        
      // enable or disable VSync (also depends on driver settings, so might not have any effect)
      // VSync disabled means lower quality for scrolling, and is only useful for framerate measurements.
      renderer.setVsync(useVsync);
      if (! useVsync) clock.setFramerateCounterFrame(jframe);  // no need for a framerate counter when vsync is active
      scene = new ColladaTestScene1(canvas);
      renderer.setScene(scene);
      renderer.setClockListener(scene);
      //hmi.util.Console.println("clock.init...");
      clock.init();
      
//      jframe.add(canvas, BorderLayout.CENTER);
//      JButton jbut = new JButton("Press Me");
//      jbut.addKeyListener(this);
//      jbut.addActionListener(this);
//      jframe.add(jbut, BorderLayout.SOUTH);
      jframe.pack(); // make it displayable even if not yet visible
      
      
      renderer.setDisplayParent(canvas); // only legal when canvas is displayable
     // hmi.util.Console.delay(3000);
      jframe.setSize(width, height);   
     //hmi.util.Console.println("setvisible");
      jframe.setVisible(true); 
//      canvas.requestFocusInWindow(); // requests the focus for keyboard input
           // while (!Display.isCloseRequested()) {
//         while (true) {
//   
//            //GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
//   
//            Display.update(); // system will freeze if you don't do this!!!!!!!!!!!!!!! must execute on thread that created the GL context!!!
//          
//         }
//
   //   hmi.util.Console.delay(3000);
     // hmi.util.Console.println("clock.start...");
      clock.start(); // start the render loop.
     // hmi.util.Console.println("clock.started");
   }   
   
   
   public void destroy() {
      renderer.destroy();
   }
   
   /** 
    * KeyListener callback: exit if the escape key was pressed
    */
   public void keyPressed(KeyEvent e)
   {
      hmi.util.Console.println("keyPressed");
      if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
         destroy();
        //System.exit(0);
      }
   } 
    
   public void actionPerformed(ActionEvent evt) {
      hmi.util.Console.println("action");
     // destroy();
   } 
    
   /* KeyListener callback (ignored)  */
   public void keyReleased(KeyEvent e) {}
   
   /* KeyListener callback (ignored) */
   public void keyTyped(KeyEvent e) {}
   
   
   /**
    * Start the ColladaLWJGLTest prog
    */
   public static void main(String[] arg) {
//      Lib.extractIfRunningFrom("ColladaLWJGLTest.jar");
//      System.setProperty("sun.java2d.noddraw", "true"); // avoid potential interference with (non-Jogl) Java using direct draw
      //System.loadLibrary("opengl32");
       ColladaLWJGLTest ct = new ColladaLWJGLTest();  
      // hmi.util.Console.delay(6000);
      // ct.destroy();
      
   }
         
}
