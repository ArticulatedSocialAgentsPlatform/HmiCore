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
import java.io.File;
import java.util.*;
import java.util.regex.*;

/**
 * GetJarVersion tries to match the filenames within an embedded ant fileset
 * and to extract a version number consisting of digits and dots. 
 * (This does not include the last dot that is part of the filetype postfix, like .jar)
 * So, for instance, a filename like HmiUtil-3.2.0.jar would yield 3.2.0 as its version string.
 * If more than one file matches, the version string should be identical,
 * or else a log message is produced with a warning.
 * If no file has a match, the reported version string is empty.
 * The version string is assigned to a specified ant property
 * which should be called versionproperty, like:
 * <getjarversion versionproperty="my.version.prop">
 *   <fileset ..../> (Soem fileset containingversioned files)
 * </gethjarversion>
 */
public class GetJarVersion extends Task {
  
   private String versionprop;
   
   private Union resources = new Union();
   
   public void setVersionProp(String property) {
      this.versionprop = property;
   }
     
   public void add(ResourceCollection rc) {
      resources.add(rc);
   }
   
   @Override
   public void execute() throws BuildException {
      if (versionprop==null) {
         throw new BuildException("No versionprop defined");
      }
      Pattern pat = Pattern.compile(".*?([0-9\\.][0-9\\.]+)\\..*?");
      String version="";

      Iterator element = resources.iterator();
      while (element.hasNext()) {
         Resource resource = (Resource) element.next();
         if ( !(resource instanceof FileResource)) {
            throw new BuildException("Not a file: " + resource);
         }
         FileResource fileResource = (FileResource) resource;
         File file = fileResource.getFile();
         if (!file.exists()) {
            throw new BuildException("File not found: " + file);
         }
          
         String fileName = file.getName();
         Matcher matcher = pat.matcher(fileName);
         if (matcher.matches()) {
            String g = matcher.group(1);
            if (version.equals("") || version.equals(g)) {
               version = g;
            } else {
               log("GetJarVersion: INCONSISTENT VERSIONS detected: " + version + " and " + g);  
            }
          } 
      }
      if (versionprop!=null) {
         getProject().setNewProperty(versionprop, version);
      } else {
         log("Jar version: " + version);  
      }
   }
   
   
//   private String incrementMajor(String version) {
//      return incrementVersion(version, 0, 2);
//   }
//   
//   private String incrementMinor(String version) {
//      return incrementVersion(version, 1, 2);
//   }
//   
//   private String incrementMaintenance(String version) {
//      return incrementVersion(version, 2, 3);
//   }
   
   
//   private String getVersionString(String version, int pos, int maxlen) {
//      List<String> vlist = parseVersionString(version);
//      int segpos = pos*2;

//      StringBuilder buf = new StringBuilder();
//    
//      int newlen = vlist.size();
//      int maxseglen = maxlen*2 -1;
//      if (newlen > maxseglen) newlen = maxseglen;
//      log("newlen=" +newlen);
//      log("vlist=" + vlist);
//      for (int seg=0; seg<newlen; seg++) {
//         buf.append(vlist.get(seg));
//      }
//      return buf.toString();
//   }
   
   private String incrementVersion(String version, int pos, int maxlen) {
      List<String> vlist = parseVersionString(version);
      int segpos = pos*2;
      
      
      // increment number at specified position
      if (vlist.size() > segpos) {
         int vnum = (int) Integer.parseInt(vlist.get(segpos));
         String nextnum = "" + (vnum+1);
         vlist.set(segpos, nextnum);
         for (int i=segpos+2; i<vlist.size(); i=i+2) {
            vlist.set(i, "0");
         }
      } else {
         for (int i=vlist.size(); i < segpos-1; i=i+2) {
            vlist.add(".");
            vlist.add("0");
         }
         vlist.add(".");
         vlist.add("1");
      }
     
      
      StringBuilder buf = new StringBuilder();
    
      int newlen = vlist.size();
      int maxseglen = maxlen*2 -1;
      if (newlen > maxseglen) newlen = maxseglen;
      log("newlen=" +newlen);
      log("vlist=" + vlist);
      for (int seg=0; seg<newlen; seg++) {
         buf.append(vlist.get(seg));
      }
      return buf.toString();
   }
   
   private List<String> parseVersionString(String vs) {
        ArrayList<String> segs = new ArrayList<String>();
        Pattern pat = Pattern.compile("([0-9]+)([^0-9]*)");
        Matcher matcher = pat.matcher(vs);
        boolean hasMore = matcher.find();
        try {
           while(hasMore) {
              String num = matcher.group(1);
              segs.add(num);
              String sep = matcher.group(2);
              if (! sep.equals(""))  segs.add(sep);
              hasMore = matcher.find();
           }
        } catch (NumberFormatException ne) {
            log("version string: " + vs + " could not be parsed");
        }   
//        int[] result = new int[nums.size()];
//        for (int i=0; i<result.length; i++) {
//         result[i] = (int) nums.get(i);
//        }
//        return result;
         return segs;
        
   }
   
   private ArrayList<Integer> parseVersionStringx(String vs) {
        ArrayList<Integer> nums = new ArrayList<Integer>(3);
        Pattern pat = Pattern.compile("([0-9]+)");
        Matcher matcher = pat.matcher(vs);
        boolean hasMore = matcher.find();
        try {
           while(hasMore) {
              String num = matcher.group(1);
              nums.add(Integer.parseInt(num));
              hasMore = matcher.find();
           }
        } catch (NumberFormatException ne) {
            log("version string: " + vs + " could not be parsed");
        }   
//        int[] result = new int[nums.size()];
//        for (int i=0; i<result.length; i++) {
//         result[i] = (int) nums.get(i);
//        }
//        return result;
         return nums;
        
   }
      
}
