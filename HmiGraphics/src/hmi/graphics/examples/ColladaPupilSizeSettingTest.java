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
