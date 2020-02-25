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
import java.lang.reflect.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Lib contains some utils for dealing with the system library path.
 * (This is the library dealing with stuff like DLL's );
 * @author Job Zwiers
 */
public final class Lib {
   
   /*
    * Disable Lib Object construction
    */
   private Lib() {}
   
   /**
    * Attempt to add the specified path to the java library path,
    * and enforces this java library path to be reloaded when the next 
    * System.loadLibrary is executed.  
    */
   public static void addLibraryPath(String path) {
      Class<?> sysclass = ClassLoader.class; 
      try {
         String oldPath = System.getProperty("java.library.path");
         
         Field field = sysclass.getDeclaredField("sys_paths");
         boolean accessible = field.isAccessible();
         if (!accessible)
           field.setAccessible(true);
         //Object original = field.get(sysclass);
         /* Reset it to null so that whenever "System.loadLibrary" is
          * called it will be reconstructed with the changed value. */
         field.set(sysclass, null);
         
         /* Change the value and load the library. */
         System.setProperty("java.library.path", path+File.pathSeparator+oldPath);
      } catch (Exception ex) {
         ex.printStackTrace();
      }
   }
   
   /**
    * Checks whether the result of getCodeSourceLocation from this hmi.util.Lib class
    * ends with a String matching the specified regular expression. 
    * For instance, isRunningFrom(hmi.graphics.jogl.FirstOpenGL.class, "-demo.jar")
    * would return true when we are running FirstOpenGL from a jar file like
    * HmiGraphics-demo.jar, but it would return false when running from a
    * location like .../build/classes. or from a jar file like HmiGraphics-2.1.0.jar
    */
   public static boolean isRunningFrom(String regex) {
      String location = getCodeSourceLocation(hmi.util.Lib.class);
      String pex = ".*" + regex;
      return Pattern.matches(pex, location);   
   }
   
   /**
    * Checks whether the result of getCodeSourceLocation from the specified Class
    * ends with a String matching the specified regular expression. 
    * For instance, isRunningFrom(hmi.graphics.jogl.FirstOpenGL.class, "-demo.jar")
    * would return true when we are running FirstOpenGL from a jar file like
    * HmiGraphics-demo.jar, but it would return false when running from a
    * location like .../build/classes. or from a jar file like HmiGraphics-2.1.0.jar
    */
   public static boolean isRunningFrom(Class<?> cl, String regex) {
      String location = getCodeSourceLocation(cl);
      String pex = ".*" + regex;
      return Pattern.matches(pex, location);   
   }
   
   /**
    * Return the location of class or jar file from which the specified Class is running.
    * For instance, getCodeSourceLocation(hmi.graphics.jogl.FirstOpenGL.class) would
    * return a location String ending in /build/classes, when running from a local ant build,
    * or ending in something like /HmiGraphics1.2.3-demo.jar, when running from an executable jar
    */
   public static String getCodeSourceLocation(Class<?> cl) {
   try {
         URI jarUri = cl.getProtectionDomain().getCodeSource().getLocation().toURI();
         return jarUri.toString();
      } catch (URISyntaxException e) {
         hmi.util.Console.println("Lib.getCodeSourceLocation: "+ e);
         return "";          
      }
   }
 
 
  public static final String TMPDIR;
  
  static {
    String javatmpdir = System.getProperty("java.io.tmpdir");
    String filesep = System.getProperty("file.separator");
    if (javatmpdir.endsWith(filesep)) {
        TMPDIR = javatmpdir;
    } else {
        TMPDIR = javatmpdir + filesep;
    }
  }
  
  /* Extracts the jar file name, like HmiGraphics-demo, from
    * a full path, like C:\aa\bb/HmiGraphics-demo.jar 
    * So, without the path, and without the .jar extension
    */
   public static String getJarName(String jarpath) {    
      Pattern pat = Pattern.compile(".*?[/\\\\]?([^/\\\\]*)\\.jar");  
      Matcher matcher = pat.matcher(jarpath);
      if (matcher.matches()) {
         return matcher.group(1); 
      } else {
         return "";
      } 
   }
  
   /**
    * extracts java.library.path files, if running from a code source that matches the specified
    * regular expression
    */
   public static List<String> extractIfRunningFrom(String regex) {
      String jar = hmi.util.UnJar.getJarForClass(hmi.util.Lib.class);
      String dir = TMPDIR + getJarName(jar);
      return extractIfRunningFrom(regex, dir, true);
   }
  
  
   /**
    * extracts java.library.path files, if running from a code source that matches the specified
    * regular expression
    */
   public static List<String> extractIfRunningFrom(String regex, boolean addToLibraryPath) {
      String jar = hmi.util.UnJar.getJarForClass(hmi.util.Lib.class);
      String dir = TMPDIR + getJarName(jar);
      return extractIfRunningFrom(regex, dir, addToLibraryPath);
   }
  
   /**
    * extracts java.library.path files, if running from a code source that matches the specified
    * regular expression
    */
   public static List<String> extractIfRunningFrom(String regex, String extractDir, boolean addToLibraryPath) {
      if (isRunningFrom(hmi.util.Lib.class, regex)) {
         return extractLib(hmi.util.Lib.class, extractDir, addToLibraryPath);
      } else {
         return Collections.emptyList();
      } 
   }
   
   
   /**
    * extracts java.library.path files, if running from a code source that matches the specified
    * regular expression, and adds them to the library path
    */
   public static List<String> extractIfRunningFrom(String regex, String extractDir) {
      return extractIfRunningFrom(regex, extractDir, true);
   }
   
  
   /**
    * UnJars dll files from the jar file into java.io.tmpdir, and adds them to the java.library.path.
    * It is assumed that this hmi.util.Lib class resides in the same jar file as where the library files
    * are to be found.
    * Should be run from the main method, before any of the libraries is actually needed. 
    */
   public static void extractLib(boolean addToLibraryPath) {
      String jar = hmi.util.UnJar.getJarForClass(hmi.util.Lib.class);
      String dir = TMPDIR + getJarName(jar);
      extractLib(jar, dir, addToLibraryPath);
   }
  
   /**
    * UnJars dll files from the jar file into java.io.tmpdir, and adds them to the java.library.path.
    * Should be run from the main method, before any of the libraries is actually needed. 
    */
   public static List<String> extractLib(Class<?> cl, boolean addToLibraryPath) {
      String jar = hmi.util.UnJar.getJarForClass(hmi.util.Lib.class);
      String dir = TMPDIR + getJarName(jar);
      return extractLib(jar, dir, addToLibraryPath);
   }
  
   /**
    * UnJars dll files from the jar file where hmi.util.Lib is found, and adds them to the java.library.path.
    * Should be run from the main method, before any of the libraries is actually needed. 
    */
   public static List<String> extractLib(String extractDir, boolean addToLibraryPath) {
      String jar = hmi.util.UnJar.getJarForClass(hmi.util.Lib.class);
      return extractLib(jar, extractDir, addToLibraryPath);
   }
  
   /**
    * UnJars dll files from the jar file, and adds them to the java.library.path.
    * Should be run from the main method, before any of the libraries is actually needed. 
    */
   public static List<String> extractLib(Class<?> cl, String extractDir, boolean addToLibraryPath) {
      return extractLib(hmi.util.UnJar.getJarForClass(cl), extractDir, addToLibraryPath);
   }
  
   
   
   /**
    * UnJars dll files from the jar file, and adds them to the java.library.path.
    * The file names of files actually extracted are returned in a List.
    * For the complete list of matching files, the corresponding base directory
    * is added to the library path.
    * Should be run from the main method, before any of the libraries is actually needed. 
    */
   public static List<String> extractLib(String jar, String extractDir, boolean addToLibraryPath) {
      String os = System.getProperty("os.name");  
      //hmi.util.Console.println("OS: " + os); 
      String fileType = "(\\.dll)|(\\.manifest)";
      if ( Pattern.matches(".*Windows.*", os) ) {
         fileType = "(\\.dll)|(\\.manifest)";
      } else {
         fileType = "\\.so";  
      }
      File extDir = new File(extractDir);
      if ( ! extDir.mkdirs()) {
        System.out.println("Could not create " + extractDir + " directory");  
      }
      List<String> extractedFiles = hmi.util.UnJar.extract(jar, fileType, extractDir);
      //hmi.util.Console.println("extractLib, extracted files: " + extractedFiles);
      if (addToLibraryPath) {
         List<String> listedFiles = hmi.util.UnJar.list(jar, fileType, extractDir);
         Pattern pat = Pattern.compile("(.*?)[/\\\\]?[^/\\\\]*");
         // get the directory parts of the extracted files:
         Set<String> libpathset = new HashSet<String>();
         //hmi.util.Console.println("extractLib,  size = " + listedFiles.size() + " execute for loop...");
         for (String file : listedFiles) {
             Matcher matcher = pat.matcher(file);
            if (matcher.matches()) {
               String libdir = matcher.group(1);
               libpathset.add(libdir);       
            } 
         }
         // add libdirs for all listed paths, not just for the extracted files. 
         for (String libdir : libpathset) {
            //hmi.util.Console.println("add library path: " + libdir);
            addLibraryPath(libdir);
         }
      }
      return extractedFiles;
     
   }
   
   
   
   
   
   
   /**
    * Starts up a Java program, pacakges within a jar file, within a new and separate Java process.
    * This allows for specifying java jvm command line options, like -Xmx1024m, that cannot be specified
    * within a jar manifest file. Similar is the java.livrary.path option, which is included here
    * by means of the libPath argument.
    */
   public static void startProc(String jarName, String libPath, String mainClass, List<String> params) {  
      List<String> procbuilderargs = new ArrayList<String>();
      procbuilderargs.add("java");
      procbuilderargs.add("-cp");
      procbuilderargs.add(jarName);
      if (libPath != null && ! libPath.equals("") ) procbuilderargs.add("-Djava.library.path="+libPath);
      procbuilderargs.addAll(params);
      procbuilderargs.add("-DisProc=true");
      procbuilderargs.add(mainClass);
     
      try {
         ProcessBuilder p = new ProcessBuilder(procbuilderargs);
         p.redirectErrorStream(true);
   
         Process proc = p.start();
         final LineNumberReader reader = new LineNumberReader( new InputStreamReader(proc.getInputStream()));
         
         Thread captureerrors = new Thread() {
            public void run() {
               boolean running = true;
               try {
                  while (running) {
                     String line = reader.readLine();
                     if (line == null) {
                        running = false;
                     } else {
                        System.out.println(line);
                     }
                  }
               } catch (Exception e) {
                   System.out.println("Thread exception: " + e);  
               }
            }
         };
         captureerrors.start();
  
      } catch (Throwable e) {
          System.out.println ("Exception: " +e);
      }
 
   }
   
   /**
    * Checks whether this Lib class is being accessed from a jar file that matches the specified regular expression.
    * If so, and no -DisProc=true attribute has been set, it extracts java.library.path files (i.e. either .dll or .so files) from the jar file,
    * and starts a new Java process with the jar files on the classpath, the extracted files
    * on the java.library.path, the parameters, specified in the param List, as "java command line arguments", an extra -DisProc=true is added,
    * and finally the specified mainClass is used as the java "main" class to run.  The call returns "true" in this case.
    * When the startProcessIfRunningFrom call does not result in a new Java process, either because the Lib class was not part 
    * of a jar file (that matches the regular expression), or becauase the -DisProc=true prevented it, the result returned is "false".
    * Typical example: 
    *    params.add("-Xmx1024m");
    *    if (! Lib.startProcessIfRunningFrom("ColladaTest.jar", "hmi.graphics.colladatest.ColladaTest", params) ) {    
    *       new ColladaTest();  
    *    }
    */
   public static boolean startProcessIfRunningFrom(String regex, String mainClass, List<String> params) {
      String jar = UnJar.getJarForClass(hmi.util.Lib.class);
      String jarName = hmi.util.Lib.getJarName(jar);
      String extractDir = Lib.TMPDIR + jarName;
      String isProcProp = System.getProperty("isProc");
      boolean isProc = Boolean.valueOf(isProcProp);
     // System.out.println("isProcProp: " + isProcProp + " isProc:" + isProc);
      if (! isProc && isRunningFrom(hmi.util.Lib.class, regex)) {
         extractLib(hmi.util.Lib.class, extractDir, false);
         startProc(jarName+".jar", extractDir+"/lib",mainClass, params );
         return true;
      } else {
         return false;  
         
      } 
   }
   
   
}
