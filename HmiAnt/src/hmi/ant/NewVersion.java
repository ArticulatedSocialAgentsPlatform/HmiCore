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
//org.apache.tools.ant.types
import java.io.File;
import java.util.*;
import java.util.regex.*;

/**
 * 
 */
public class NewVersion extends Task {
  
   private String version;
   private String newversionprop;
   private String type;
   private String devnum;
   private String specversion;
  
   public void setVersion(String version) {
      this.version = version;
   }
   
   public void setNewversionprop (String newversionprop) {
      this.newversionprop = newversionprop;
   }
   
   public void setType (String type) {
      this.type = type;
   }
   
   public void setDevNum (String devnum) {
      this.devnum = devnum;
   }
   
   public void setSpecificationVersion (String specversion) {
      this.specversion = specversion;
   }
   
   @Override
   public void execute() throws BuildException {
      if (version==null) {
         throw new BuildException("No version defined");
      }
      if (newversionprop==null) {
         throw new BuildException("No newversionprop defined");
      }
      
      if (type==null) {
         throw new BuildException("No newversion type defined");
      }
      if (devnum==null) {
          devnum="-1";
      }
      if (specversion==null) {
          specversion="";
      }
      
      String nextVersion = "";
      nextVersion = VersionHandler.newVersion(version, specversion, type, devnum);
      System.out.println("NewVersion: " + nextVersion);
      if (nextVersion != null) getProject().setNewProperty(newversionprop, nextVersion);
   }
}
