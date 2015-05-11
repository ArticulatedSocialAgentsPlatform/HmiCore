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
import org.slf4j.*;


/**
 * Resources are objects that serve as an ``anchor'' for loading files
 * called ``resources''. Such files are assumed to be at a fixed place 
 * within the directory structure associated with a project, so no interactive
 * file dialog is needed. 
 * They are, however, not assumed to be at a fixed place within the file
 * system of the OS, so as to avoid undesirable system dependencies.
 * Think of a situation where an application is packaged in the form of a single 
 * executable jar file, that has to run from within some unknown directory
 * on an unknown machine. In this case, about the only location that we can rely on
 * is the jar archive itself, so resource files should be specified relative to the jar archive.
 * A Resources object determines a data directory inside such a jar archive, and will open
 * files inside this directory, via the getReader() or getFileStream method.
 * By convention the name of this data directory follows the package structure, so
 * for example, the data used by some class inside the project.vmr package shouold read data from
 * files inside the project/vmr data directory.  
 * When the application is not run from a jar archive, the same data directory should be available,
 * and should be included in the java run time classpath. By convention, we use a &quot;data&quot;
 * or &quot;resources&quot; directory inside java projects, which is included in the the java classpath,
 * either inside the Java development tool or inside the ant build scripts.
 * @author Job Zwiers
 */
public class Resources {
  
  private Resources() {}


   /**
    * Create a new Resources object, for a specified resource directory.
    * This directory is searched for on the run time classpath.
    */
   public Resources(String resourceDir) {  
      this.resourceDir = resourceDir;
      resourceRoot = adjustPath(resourceDir);
   }
   
   
   /**
    * Creates a new Resources object for a local directory, specified in the form of a Java File
    * object. (So resourceDirFile should refer to a directory, rather than to a file).
    * The resourceDirFile File should actually specify a local directory on the system where the jvm is
    * currently running, and will be resolved in the standard way for local system files and directories.
    * In order to maintain platform and system independence, resourceDirFile should have been obtained,
    * for instance, by means of a JFileChooser, rather than via a hard-coded directory path.
    */ 
   public Resources(File resourceDirFile)  {
      if ( ! resourceDirFile.isDirectory()) {
          logger.error("Resources: File " + resourceDirFile + " is not a directory");  
      }
      localDir = true;
      try {
         resourceDir = resourceDirFile.getCanonicalPath();
         resourceRoot = null;
         resourceRoot = adjustPath(resourceDir);
      } catch (IOException e) {
         logger.error("Resources exception: " + e);  
      }
   }
   

   /**
    * returns the resource directory for this Resources object
    */
   public String getResourceDirectory() {
      return resourceDir;
   }


   /**
    * Like getReader(filename), but returns a buffered InputStream, rather than
    * a buffered Reader. Returns null when the resource file could not be found.
    */
   public BufferedInputStream getInputStream(String fileName) {
      InputStream unbuffered = getUnbufferedInputStream(fileName);
      if (unbuffered == null) {
         return null;
      } else {
         return new BufferedInputStream(unbuffered);
      }
   }

   /*
    * Like getReader(filename), but returns an InputStream, rather than
    * a buffered Reader. Returns null when the resource file could not be found.
    */
   private InputStream getUnbufferedInputStream(String fileName) {
      String resource; 
      if (fileName == null || fileName.length() == 0) return null;
      char firstChar = fileName.charAt(0);
      if (firstChar == '/' || firstChar == '\\') {
         resource = fileName;
      } else {
            resource = resourceRoot + fileName;    
      }       
      InputStream stream = null;
      if (localDir) {
          try {
             stream = new FileInputStream(resource);
          } catch (FileNotFoundException e) {
             logger.error("Resources: " + e);
          }
      } else {
          stream = loader.getResourceAsStream(resource);
      } 
      if (logging && stream == null) {
        logger.error("Cannot find resource file: " + resource);
      }
      return stream;    
   }

   /**
    * Opens a file, and returns a buffered Reader for that file.
    * The file is searched for in the resource directory determined by this Resources object.
    * 
    * Assume that this method is called for a class with name C that extends
    * "Resources". Call the package prefix for this C class "package".
    * If "filename" is a relative path, i.e. if it does not start with a
    * "/", then the package prefix is prepended, and the resulting file
    * is searched for on the classpath. 
    * For the HMI projects,
    * we assume that the data directory is included in the classpath.
    * If "filename" is an absolute file name", than the package prefix is 
    * ignored, and the file is searched for on the class path.
    * Returns null when the resource file could not be found.
    */
   public BufferedReader getReader(String fileName) {
      InputStream stream = getUnbufferedInputStream(fileName);
      if (stream == null) {
         return null;
      } 
      return new BufferedReader(new InputStreamReader(stream));
   }


   /**
    * Static method for opening a BufferedReader for a resource file.
    */
   public static BufferedReader getResourceReader(String resourceFileName) {
      if (resourceFileName == null ) {
         logger.error("Cannot open null resource file");
         return null;
      }
      if (resourceFileName.length() == 0) {
         logger.error("Cannot open resource file\"\" ");
         return null;
      }
      InputStream stream = loader.getResourceAsStream(resourceFileName);
      if (stream == null) {
         logger.error("Cannot find resource file: " + resourceFileName);
         return null;
      }
      return new BufferedReader(new InputStreamReader(stream)); 
   }
   


   /**
    * Returns the URL for the specified resource file
    */
   public URL getURL(String fileName) {
      return getURL(loader, fileName);
   }

   /**
    * Returns the URL for the specified resource file
    */
   public URL getURL(ClassLoader cl, String fileName) {
      String resource; 
      if (fileName == null || fileName.length() == 0) return null;
      char firstChar = fileName.charAt(0);
      if (firstChar == '/' || firstChar == '\\') {
         resource = fileName;
      } else {
         resource = resourceRoot + fileName;    
      }       
      return cl.getResource(resource);
   }

     
     
   
    /**
    * Tries to read the specified resource file, and returns the contents as a String.
    * If the file cannot be opened, a null result is returned.
    */
   public static String readResource(String fileName) throws IOException  {
      if (fileName == null || fileName.length()==0) return null;
      BufferedReader reader = getResourceReader(fileName);
      if (reader != null) {
         StringBuffer buf = new StringBuffer();
         int ch = reader.read();
         while (ch >=0) {
            buf.append((char)ch);
            ch = reader.read();
         }
         return(buf.toString());   
      } else {
         return null;
      }
   }

     
     
   
   /**
    * Tries to read the specified file, and returns the contents as a String.
    * If the file cannot be opened, a null result is returned.
    */
   public String read(String fileName) throws IOException  {
      if (fileName == null || fileName.length()==0) return null;
      BufferedReader reader = getReader(fileName);
      if (reader != null) {
         StringBuffer buf = new StringBuffer();
         int ch = reader.read();
         while (ch >=0) {
            buf.append((char)ch);
            ch = reader.read();
         }
         return(buf.toString());   
      } else {
         return null;
      }
   }

   /**
    * Turns logging on or off
    */
   public void setLogging(boolean logging) {
      this.logging = logging;
   }


   /*
    * replaces \\ by / and appends a / at the end, if necessary
    * idea: rootPath could be specified in DOS format.
    * It should be converted to a normal path.
    */
   private  String adjustPath(String rootPath) {
      String result = rootPath.replace('\\', '/');
      int len = result.length();
      if (len > 0 && result.charAt(len-1) != '/') {
         result = result + '/';
      }
      return result;
   }

   /**
    * returns  String representation
    */
   public String toString() {
      return "Resources(" + resourceDir + ")";  
   }
 
 
   /**
    * returns the resource root directory for this Resources object
    */
   public String getResourceRoot() {
       return resourceRoot;  
   }

   private String resourceRoot = ""; // root directory of resources, within the java/data directory
   private static ClassLoader loader = hmi.util.Resources.class.getClassLoader();   
  
   private static Logger logger = LoggerFactory.getLogger("hmi.util");
   private boolean logging = false;
   private String resourceDir="";
   private boolean localDir = false;
  // private static java.util.logging.Logger logger = java.util.logging.Logger.getLogger("hmi.util.Resources");


  
}
