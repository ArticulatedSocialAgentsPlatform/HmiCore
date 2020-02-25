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
 * 
 */
public class FindMain extends Task {
  
   private String mainclassprop;
   private String mainlistprop;
   private String mainpreference;
   private String mainclass = "";
   
   private Union resources = new Union();
   
   public void setMainclassProp(String property) {
      this.mainclassprop = property;
   }
   
   public void setMainlistProp(String property) {
      this.mainlistprop = property;
   }
     
   public void setMainpref(String pref) {
      this.mainpreference = pref;
   }  
     
   public void add(ResourceCollection rc) {
      //System.out.println("add resource collection");
      resources.add(rc);
   }
   
   @Override
   public void execute() throws BuildException {
      if (mainclassprop==null && mainlistprop==null) {
         throw new BuildException("Findmain: No mainclassprop or mainlistprop defined");
      }
//      if (mainpreference == null) {
//         System.out.println("Null pref");
//      } else {
//         System.out.println("pref: " + mainpreference);  
//      }
      
//      if (mainclassprop!=null && mainlistprop!=null) {
//         throw new BuildException("Findmain: mainclass and mainlist cannot be used together");
//      }
      Pattern mainPat = Pattern.compile("[ ]*public[ ]+static[ ]+void[ ]+main[ ]*[(]");
      Pattern packagePat = Pattern.compile(".*[/\\\\]src[/\\\\](.*).java");
      Pattern slashPat = Pattern.compile("[/\\\\]");
      Pattern basenamePat = Pattern.compile(".*[.]([^.]+)");
      //String mainclass="";
      List<String> candidates = new ArrayList<String>(4);
     
      boolean hasMain = false;
      Iterator element = resources.iterator();
      while (! hasMain && element.hasNext()) {
         Resource resource = (Resource) element.next();
         if ( !(resource instanceof FileResource)) {
            throw new BuildException("Not a file: " + resource);
         }
         FileResource fileResource = (FileResource) resource;
         File file = fileResource.getFile();
         if (!file.exists()) {
            throw new BuildException("File not found: " + file);
         }
         String fileName = file.getPath();
        // System.out.println("File: " + fileName);
         LineNumberReader reader = null;
         
         try {
            reader = new LineNumberReader(new FileReader(fileName));
            String line = reader.readLine();
            while (line!= null ) {
               Matcher mainMatcher = mainPat.matcher(line);
               hasMain = mainMatcher.find();
               
               if (hasMain) {
                   //mainclass = fileName;
                   Matcher packageMatcher = packagePat.matcher(fileName);
                   boolean pmatch = packageMatcher.find();
                   if (pmatch) {
                      Matcher slashMatcher = slashPat.matcher(packageMatcher.group(1));
                      String fullclassname = slashMatcher.replaceAll(".");
                      
                      Matcher bnm = basenamePat.matcher(fullclassname);
                      String basename = "";
                      if (bnm.find()) {
                         basename = bnm.group(1);
                        // System.out.println("basename=" + basename);
                      }
                      if (candidates.isEmpty() || (mainpreference!= null && mainpreference.equals(basename))) {
                         mainclass = fullclassname;
                        // System.out.println("set mainclass=" + mainclass);
                      }
                      candidates.add(fullclassname);
                   }
               }
               line = reader.readLine();
               
            }
            reader.close();  
         } catch (FileNotFoundException e) {
            throw new BuildException("FindMain: " + e);
         } catch (IOException io) {
            throw new BuildException("FindMain: " + io) ; 
         }
         
         
        
      }
    
      if (mainclassprop!=null) {
         getProject().setNewProperty(mainclassprop, mainclass);
      }
      if (mainlistprop != null) {
          StringBuffer choicelist = new StringBuffer();
          if (candidates.size() > 0) choicelist.append(candidates.get(0));
          for (int i=1; i<candidates.size(); i++) {
             choicelist.append(",");
             choicelist.append(candidates.get(i)); 
          }
          
          getProject().setNewProperty(mainlistprop, choicelist.toString());
       
      }
   }
   
   


      
}
