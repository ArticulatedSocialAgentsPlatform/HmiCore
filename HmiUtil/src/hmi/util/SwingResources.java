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
