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
/**
 * 
 */

package hmi.ant;  
import org.apache.tools.ant.*;
import org.apache.tools.ant.types.*;
import org.apache.tools.ant.types.resources.*;
import java.io.*;
import java.util.*;
import java.util.regex.*;

/**
 * GetManifestVersion tries to read the current specification version from the manifest file.
 */
public class GetManifestVersion extends Task {
  
   private String versionprop;
   private String manifestfile;
   
   private Union resources = new Union();
   
   public void setVersionProp(String property) {
      this.versionprop = property;
   }
   
   public void setManifestFile(String manifestfile) {
      this.manifestfile = manifestfile;
   }
   
  
   
   @Override
   public void execute() throws BuildException {
      if (versionprop==null) {
         throw new BuildException("No versionprop defined");
      }
      if (manifestfile==null) {
         throw new BuildException("No manifestfile defined");
      }
      String basedir = getProject().getProperty("basedir");
      //log("basedir: " + basedir);  
      //File mfFile = new File(manifestfile);    
      String version ="0";
      
      LineNumberReader reader = null;
      try {
         reader = new LineNumberReader(new FileReader(basedir + "/" + manifestfile));
         String line = reader.readLine();
         lr: while (line!= null) {
               StringTokenizer tokenizer = new StringTokenizer(line);
               while (tokenizer.hasMoreTokens()) {
                  String token = tokenizer.nextToken();
                  if (token.equals("Specification-Version:")) {
                     if (tokenizer.hasMoreTokens()) {
                       version = tokenizer.nextToken();
                     }
                     reader.close();  
                     break lr;
                  }
               }
             line = reader.readLine();
         }
         reader.close();  
         
      } catch (FileNotFoundException e) {
         throw new BuildException("GetManifestVersion: " + e);
      } catch (IOException io) {
         throw new BuildException("GetManifestVersion: " + io) ; 
      }
  
      getProject().setNewProperty(versionprop, version);
     
   }
   

      
}
