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
package hmi.graphics.examples;
import javax.swing.JFrame;
import java.awt.event.*;
import java.awt.*;
import hmi.graphics.opengl.*;
import hmi.graphics.opengl.GLRenderObject;
import hmi.util.ClockListener;
import hmi.util.SystemClock;
import hmi.util.FramerateCounter;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ColladaPupilSizeSettingTest   { 
   private JFrame jframe;               
   private GLRendererV0 renderer;           

   private GLClockedRenderObject scene;
   private SystemClock clock;           
   private static Logger logger=LoggerFactory.getLogger("examples");
   public ColladaPupilSizeSettingTest() {   
      jframe = new JFrame("ColladaPupilSizeSettingTest");  
      jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      jframe.setLocation(850, 150);
      jframe.setSize(800, 600);          
      hmi.util.Console.setSize(700, 1100);
      renderer = new GLRendererV0(0); 
      Component canvas = renderer.getAWTComponent();
      renderer.setFOVY(40.0);
      renderer.setNear(0.01); 
      renderer.setFar(50); 
      clock = new SystemClock();
      clock.addClockListener(renderer);
      renderer.setVsync(false);
      clock.addClockListener(new FramerateCounter(jframe));
      scene = new ColladaTestScenePupilSizeSetting(canvas);
      
      renderer.setScene(scene);
      renderer.setClockListener(scene);
      
      jframe.add(canvas);
      jframe.setVisible(true); 
      canvas.requestFocusInWindow(); 
      clock.start(); 
   }   
 

   public static void main(String[] arg) {
      System.setProperty("sun.java2d.noddraw", "true"); // avoid potential interference with (non-Jogl) Java using direct draw
      new ColladaPupilSizeSettingTest();  
   }
         
}
