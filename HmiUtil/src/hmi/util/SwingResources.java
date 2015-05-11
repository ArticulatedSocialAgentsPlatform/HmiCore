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

package hmi.util;

import java.io.*;
import java.net.URL;
import javax.swing.*;
import org.slf4j.*;

/**
Adds some swing functionality to Resources
 * @author Dennis Reidsma
 */
public class SwingResources extends Resources{
  
 


   /**
    * Create a new Resources object, for a specified resource directory.
    * This directory is searched for on the run time classpath.
    */
   public SwingResources(String resourceDir) {  
    super(resourceDir);
   }
   
   
   /**
    * Creates a new Resources object for a local directory, specified in the form of a Java File
    * object. (So resourceDirFile should refer to a directory, rather than to a file).
    * The resourceDirFile File should actually specify a local directory on the system where the jvm is
    * currently running, and will be resolved in the standard way for local system files and directories.
    * In order to maintain platform and system independence, resourceDirFile should have been obtained,
    * for instance, by means of a JFileChooser, rather than via a hard-coded directory path.
    */ 
   public SwingResources(File resourceDirFile)  {
    super(resourceDirFile);
   }
   
   /**
    * Returns an ImageIcon stored as a resource file
    */
   public ImageIcon getImageIcon(String fileName) {
       URL imageURL = getURL(fileName);
       if (imageURL == null) {
           hmi.util.Console.println("Resources.getImageIcon: null  icon URL");
           return null;
       } else {
          return new ImageIcon(imageURL);
       }
   }


    /**
    * Returns an ImageIcon stored as a resource file
    */
   public ImageIcon getImageIcon(ClassLoader cl, String fileName) {
       URL imageURL = getURL(cl, fileName);
       hmi.util.Console.println("Resources\nurl=" + imageURL);
       if (imageURL == null) {
           hmi.util.Console.println("Resources.getImageIcon: null  icon URL");
           return null;
       } else {
         return null;
         // return new ImageIcon(imageURL);
       }
   }

   public Icon getIcon(URL url) {
      Icon ico  = new ImageIcon(url); 
      return ico; 
   }  
     
     
   

  
}
