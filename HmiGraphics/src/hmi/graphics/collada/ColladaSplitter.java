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
   String outDirGeometriesPath;  // directory path for geometries
   String outDirNodesPath;       // directory path for visual scenes nodes
   String baseName;         // base name part of inFileName
   File inFile;             // input File
   BufferedReader in;       // input Reader
   String line;             // current input line
   int lineCounter = 0;     // counts number of lines read.
   File outDir;             // directory for output files
   File outDirGeometries;   // directory for geometry files
   File outDirNodes;        // directory for node files
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
  // <node id="Figure_2BODY_2_node" name="Body" sid="Figure_2BODY_2_node" type="JOINT">
   Pattern startNodePattern =  Pattern.compile(".*<node.*\\Wid\\s*=\\s*\"([a-zA-Z0-9-_]*)\".*>.*");
   Pattern endNodePattern =  Pattern.compile(".*</node(.*)>.*");
 
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
      outDirNodesPath = outDirPath + "/nodes";
      
           
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
         outDirNodes = new File(outDirNodesPath);
         if (! (outDirNodes.exists()) ) {
            if (outDirNodes.mkdir()) {
               System.out.println("Directory: " + outDirNodesPath + " created");
            } else {
               System.out.println("Could not create Directory: " + outDirNodesPath);
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
      
      if (libraryType.equals("geometries")) {
         System.out.println("Geometries library");
         splitGeometries(outLibrary);
      } else if (libraryType.equals("visual_scenes")) {
         System.out.println("Visual Scenes library");
         splitVisualScenesLibrary(outLibrary);
      } else {
         splitDefaultLibrary(outLibrary);
      }
   }
   
   /* assumption: we are at the first line of a <library-Type section. 
    * all library lines are consumed, line is left at the last </library_xyz line
    */
   private void splitDefaultLibrary(PrintWriter outLibrary) throws IOException {
      while(line != null) {       
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
   
   /* assumption: we are at the first line of a <library_geometries> section. 
    * all library lines are consumed, line is left at the last </library_geometries> line
    */
   private void splitGeometries(PrintWriter outLibrary) throws IOException {
       Matcher startGeom;
       while(line != null) {
         if ((startGeom = startGeometryPattern.matcher(line)).matches()) {
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
   
   
      /* assumption: we are at the first line of a <library_visual_scenes> section. 
    * all library lines are consumed, line is left at the last </library_visual_scenes> line
    */
   private void splitVisualScenesLibrary(PrintWriter outLibrary) throws IOException {
      PrintWriter outNode = outLibrary;
      Matcher startNode;
      Matcher endNode;
      int nodeLevel = 0;
      int splitLevel = 1;
      while(line != null) { 
         if ((startNode = startNodePattern.matcher(line)).matches()) {   
             //System.out.println(" node level = " + nodeLevel);
             nodeLevel++;
             if (nodeLevel == splitLevel+1) {
                String nodeId= startNode.group(1); 
                //System.out.println("split Node " + nodeId);
                String nodeFileName = nodeId + ".xml";
                String nodeFilePath = outDirNodesPath + "/" + nodeFileName;
                outLibrary.println("   <? include file=\"" + outDirName + "/nodes/" + nodeFileName + "\" ?>");  
                outNode = new PrintWriter(nodeFilePath);
                           
             }   
             outNode.println(line);      
         } else if ((endNode = endNodePattern.matcher(line)).matches()) {   
             //System.out.println(" node level = " + nodeLevel);
             outNode.println(line);
             nodeLevel--;
             if (nodeLevel == splitLevel) {
                
               //System.out.println("End split Node ");
               
               outNode.close();
               outNode = outLibrary;    
             }
         } else {
            outNode.println(line);
            Matcher endLibraryMatcher = endLibraryPattern.matcher(line);
            if (endLibraryMatcher.matches()) {
               System.out.println(" node level = " + nodeLevel);
               return;                  
            }
         }
         line = in.readLine();
         lineCounter++;
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
