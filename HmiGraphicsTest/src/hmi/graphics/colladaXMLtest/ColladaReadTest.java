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
package hmi.graphics.colladaXMLtest;

import hmi.util.*;
import hmi.xml.*;
import hmi.graphics.collada.*;

import java.io.*;
import java.util.*;

/**
 * ColladReadTest tries to read a large Collada file, for testing purposes
 * @author Job Zwiers
 */
public final class ColladaReadTest {

   /***/
   public  ColladaReadTest() {
      String resourcedir ="armandia/dae";
      String daeFile = "armandia-toplevel.dae"; 
      Resources resources = new Resources(resourcedir);
      
      Stopwatch sw = new Stopwatch("collada timer");
      try {
        BufferedReader colladaReader = resources.getReader(daeFile);
        //hmi.util.Console.println(colladaReader==null, "Null colladaReader", "colladaReader OK");
        hmi.util.Console.println("Start reading...");
        sw.start();
        Collada col = new Collada(new XMLTokenizer(colladaReader), resources);  
        sw.showSeconds("test1: ");
        colladaReader = resources.getReader(daeFile);
        sw.start();
        col = new Collada(new XMLTokenizer(colladaReader), resources);  
        sw.showSeconds("test2: ");
        colladaReader = resources.getReader(daeFile);
        sw.start();
        col = new Collada(new XMLTokenizer(colladaReader), resources);  
        sw.showSeconds("test3: ");
        colladaReader = resources.getReader(daeFile);
        sw.start();
        col = new Collada(new XMLTokenizer(colladaReader), resources);  
        sw.showSeconds("test4: ");
        colladaReader = resources.getReader(daeFile);
        sw.start();
        col = new Collada(new XMLTokenizer(colladaReader), resources);  
        sw.showSeconds("test5: ");
        hmi.util.Console.println("Collada file read test finished");
      } catch (IOException e) {
         hmi.util.Console.println("ColladaReadTest: " + e);  
      }
   }

 
   public static void main(String[] arg) {
      //hmi.util.Console.println("colladaReadTest start...");  
      ColladaReadTest test = new ColladaReadTest();
      
   }

   
}
