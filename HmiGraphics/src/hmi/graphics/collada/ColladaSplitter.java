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

package hmi.graphics.collada;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Split up a Collada dae file into smaller, more manageable, parts
 * 
 * @author Job Zwiers
 */
public class ColladaSplitter
{
   
   String resDir;           // path for i/o resource directory (location for inFile and outDir)
   String inFilePath;       // path for input file
   String outDirName;       // directory for output results
   String outDirPath;       // directory path for split results
   String outDirGeometriesPath;       // directory path for geometries
   String baseName;         // base name part of inFileName
   File inFile;             // input File
   BufferedReader in;       // input Reader
   String line;             // current input line
   int lineCounter = 0;     // counts number of lines read.
   File outDir;             // directory for output files
   File outDirGeometries;   // directory for geometry files
   String toplevelFileName; // name of top-level dae file for split results
   String toplevelFilePath; // full path for top-level file
   private PrintWriter outToplevel; // Writer for top-level output
   PrintWriter outLibrary;  // Writer for current library
   private Map<String, PrintWriter> printWriters = new HashMap<String, PrintWriter>();
   
   // <library_visual_scenes>
   Pattern startLibraryPattern =  Pattern.compile(".*<library_(.*)>.*");
   Pattern endLibraryPattern =  Pattern.compile(".*</library_.*");
   // <geometry id="Kevin-mesh" name="Kevin">
   Pattern startGeometryPattern =  Pattern.compile(".*<geometry.*\\Wid\\s*=\\s*\"([a-zA-Z0-9-_]*)\".*>.*");
   Pattern endGeometryPattern =  Pattern.compile(".*</geometry(.*)>.*");
 
   /**
    * Searches for the dae file within the given Resource directory. 
    */
   public  void split(String resourceDir, String inFileName, String dirName) {
      if (inFileName == null) {
          System.out.println("ColladaSplitter: <Null> input file");
          System.exit(0);  
      }
      int dotpos = inFileName.lastIndexOf('.');
      baseName = inFileName.substring(0, dotpos); 
      if (dirName == null) {
         this.outDirName = baseName + "-libraries";
         System.out.println("(null) outDir = " + this.outDirName);
      } else {
         this.outDirName = dirName;
         System.out.println("outDir = " + outDirName);
         
      }
      
      //this.outDirName = (outDirName==null) ?  : outDirName;
      
      System.out.println("ColladaSplitter start...");
      resDir = (resourceDir == null || resourceDir.equals("")) ? "" :  resourceDir.replace('\\', '/') + "/";
      inFilePath = resDir + inFileName;  
      outDirPath = resDir + outDirName;  
      
      System.out.println("inFilePath=" + inFilePath);
      System.out.println("outDirPath=" + outDirPath);
      
      outDirGeometriesPath = outDirPath + "/geometries";
      
           
      inFile = new File(inFilePath);
      try {
         in = new BufferedReader(new FileReader(inFile));
         if (in == null) {
            System.out.println("ColladaSplitter: Could not find file: " + inFile);
            System.exit(0);
         }
         
         outDir = new File(outDirPath);
         if (! (outDir.exists()) ) {
            if (outDir.mkdir()) {
               System.out.println("Directory: " + outDirPath + " created");
            } else {
               System.out.println("Could not create Directory: " + outDirPath);
               System.exit(0);
            }   
         }
         outDirGeometries = new File(outDirGeometriesPath);
         if (! (outDirGeometries.exists()) ) {
            if (outDirGeometries.mkdir()) {
               System.out.println("Directory: " + outDirGeometriesPath + " created");
            } else {
               System.out.println("Could not create Directory: " + outDirGeometriesPath);
               System.exit(0);
            }   
         }
         
         toplevelFileName = baseName + "-toplevel.dae"; // top-level output file name
         toplevelFilePath = resDir + toplevelFileName;
         outToplevel = new PrintWriter(toplevelFilePath);
     
         // detect startLibraryPattern in input and split accordingly...  
         line = in.readLine();
         while(line != null) {
            Matcher matcher = startLibraryPattern.matcher(line);
            if (matcher.matches()) {
               String libraryType= matcher.group(1);       
               splitLibrary(libraryType);
            } else {
               outToplevel.println(line);
            }
            line = in.readLine();
            lineCounter++;
         }
         outToplevel.close();
         for (Map.Entry<String, PrintWriter> outChannel : printWriters.entrySet() ) {
             outChannel.getValue().close();
         }
         
         
         System.out.println("ColladaSplitter finished (" + lineCounter + " lines read)");
      } catch (IOException e) {
         System.out.println("ColladaSplitter: " + e);
      }
   }
   
   
   
   /* assumption: we are at the first line of a <library-Type section. This type is passed on as parameter.
    * The input line is a shared variable.
    * all library lines are consumed, line is left at the last </library_xyz line
    */
   private void splitLibrary(String libraryType) throws IOException {
      
      String outName =  "library-" + libraryType + ".xml";
      outToplevel.println("   <? include file=\"" + outDirName + "/" + outName  + "\" ?>");
      
      outLibrary = printWriters.get(outName);
      
      if (outLibrary == null) {
         try {
            outLibrary = new PrintWriter(outDirPath + "/" + outName);
            printWriters.put(outName, outLibrary);
         } catch (FileNotFoundException e) {
            System.out.println("splitLibrary: " + e);
            System.exit(0);
         }
      }
      boolean isGeometries = (libraryType.equals("geometries"));
      Matcher startGeom;
      while(line != null) {
         if (isGeometries && (startGeom = startGeometryPattern.matcher(line)).matches()) {
            String geomId= startGeom.group(1);     
            splitGeometry(geomId);
            line = in.readLine();
            lineCounter++;
         } else {
            outLibrary.println(line);
            Matcher endLibraryMatcher = endLibraryPattern.matcher(line);
            if (endLibraryMatcher.matches()) {
               return;                  
            } else {
               line = in.readLine();
               lineCounter++;
            } 
         }  
      }
   }
   
   /*
    * Assumption: we are on a  <geometry> input line.
    * This geometry is written to a separate file (and an "<? include" to the outLibrary File)
    * We stop while on the closing </geometry> line
    */
   private void splitGeometry(String geomId) throws IOException {
      String geomFileName = geomId + ".xml";
      String geomFilePath = outDirGeometriesPath + "/" + geomFileName;
      outLibrary.println("   <? include file=\"" + outDirName + "/geometries/" + geomFileName + "\" ?>");
      
      //outLibrary.println("   <? include file=\"" + outDirGeometriesPath + "/" + geomFileName  + "\" ?>");
      PrintWriter outGeometry = new PrintWriter(geomFilePath);
      while(line != null) {
         outGeometry.println(line);       
         Matcher endGeometryMatcher = endGeometryPattern.matcher(line);
         if (endGeometryMatcher.matches()) {
            outGeometry.close();
            return;                  
         } else {
            line = in.readLine();
            lineCounter++;
         }       
      }  
   }
   
   
   
   public static void main(String[] arg) {  
       //String humanoidResources = "C:/JavaProjects/HmiResource/HmiHumanoidEmbodiments/resource/Humanoids/billie/dae";
       //String colladaFile="billiesplittest.dae"; 
       //String colladaFile="billie.dae"; 
       //String inFileName=colladaFile;
       //String outDirName="billieSplitted";
       
       String humanoidResources = null;
       String inFileName = null;
       String outDirName = null;
       
       switch (arg.length) 
       {
//            case 0: inFileName=colladaFile; outFileName="splitted-" + colladaFile; break;
          case 1: humanoidResources = null; inFileName=arg[0]; outDirName=null; break;
          case 2: humanoidResources = arg[0]; inFileName=arg[1]; outDirName=null; break;
          case 3: humanoidResources = arg[0]; inFileName=arg[1]; outDirName=arg[2]; break;
          default: System.out.println("provide conversion arguments:  [<resourcedir file>] <inFile> [<dirName>] "); System.exit(0);
       }
       
       //System.out.println("ColladaSplitter for  " + humanoidResources + ", converting " + inFileName + " to " + outDirName);
         new ColladaSplitter().split(humanoidResources, inFileName, outDirName);
   }

}
