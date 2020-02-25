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
 * Some utility methods for version string management, in particular
 * for incrementing version numbers of various formats.
 */
public class VersionHandler  {
  
   public static boolean isdev = false;
   public static boolean isrc = false;
   public static boolean isalpha = false;
   public static int rcnum = 0;
   public static int devnum = 0;


   /**
    * Calculates a new version from an existing version, a specification for how to calculate 
    * the new version, and possibly a specified dev number.
    * &quot;type&quot; must be one of: alphaVersion, devVersion, minorRelease,
    * majorRelease, minorReleaseCandidate, majorReleaseCandidate, or maintenanceVersion.
    * The specified &quot;type&quot number is used only for type &quot;devVersion&quot,
    * and only when it specifies a number > 0.
    */
   public static String newVersion(String version, String specversion, String type, String dev) {
      if ( ! specversion.isEmpty()) {
          if (checkVersionNumber(specversion))
          {
             return specversion;
          } else {
              return null; 
          }
      }
      
      if (version.startsWith("working@")) version = "0.0";
    
      if (type.equals("alphaVersion")) {
          return alphaVersion(version);
      } else if (type.equals("minorRelease")) {
          return incrementMinor(version);
      } else if (type.equals("majorRelease")) {
           return incrementMajor(version);
      } else if (type.equals("minorReleaseCandidate")) {
           return minorReleaseCandidateVersion(version);
      } else if (type.equals("majorReleaseCandidate")) {
           return majorReleaseCandidateVersion(version);
      } else if ( type.equals("maintenanceVersion")) {
           return incrementMaintenance(version);
      } else if ( type.equals("devVersion")) {
           return newDevVersion(version, dev);
      } else {
          System.out.println("hmi.ant.VersionHandler.newVersion: unknown type: " + type);
          return version;  
      }
   }


   /**
    * Allowed "standard" versions: majornum[.minornum[.maintenancenum]],
    * where the . separator character can also be the - or _ character.
    * Anything else, like -alpha or -rc2, is stripped off. The returned version string 
    * has at least major and minor number, where the minor number 0 will be added if
    * it is missing. (Also, an empty version or something like alpha will be turned into 0.0)
    */
   public static boolean checkVersionNumber(String version) {
      List<String> vlist = parseVersionString(version);
      int vlen = vlist.size();
      int majornum;
      int minornum;
      int maintenancenum;
      //System.out.println("checkVersionNumber vlen=" + vlen);
      if (vlen < 3) {
         return false;
      }
      try {
            majornum = (int) Integer.parseInt(vlist.get(0));
      } catch (NumberFormatException e) {
         System.out.println("no proper major num");
            return false;
      }
      
      String sep0 = vlist.get(1);
      if ( ! (sep0.equals(".") || sep0.equals("-") || sep0.equals("_")) ) {
         return false;
      } 
      try {
            minornum = (int) Integer.parseInt(vlist.get(2));
      } catch (NumberFormatException e) {
            return false;
      }
      if (vlen ==3) return true; // major.minor form
      
      if (vlen > 5) return false;
      // should be major.minor.maintenance or major.minor-devX, major.minor-rcX, major.minor-alphaX, where x is a num
      
      String sep1 = vlist.get(3);
      if (vlen == 4) {
         return sep1.equals("-alpha") || sep1.equals("_alpha"); // ok for major.minor-alpha form 
      }
      
      if ( (sep1.equals(".") ) ) { // should be a maintenance part
         try {
            maintenancenum = (int) Integer.parseInt(vlist.get(4));
         }  catch (NumberFormatException e) {
            return false;
         }
         return true;
      }
      // no maintenance, but vlen == 5, 
      if (sep1.equals("-dev") || sep1.equals("-rc") || sep1.equals("-alpha")) return true;
      if (sep1.equals("_dev") || sep1.equals("_rc") || sep1.equals("_alpha")) return true;
      return false;
     
   }
 

   
   /**
    * Allowed "standard" versions: majornum[.minornum[.maintenancenum]],
    * where the . separator character can also be the - or _ character.
    * Anything else, like -alpha or -rc2, is stripped off. The returned version string 
    * has at least major and minor number, where the minor number 0 will be added if
    * it is missing. (Also, an empty version or something like alpha will be turned into 0.0)
    */
   public static String revertToStandardVersion(String version) {
      List<String> vlist = parseVersionString(version);
      int vlen = vlist.size();
      int majornum;
      int minornum;
      int maintenancenum;
      if (vlen == 0) {
         System.out.println("empty version");
          return "0.1";
      }
      try {
            majornum = (int) Integer.parseInt(vlist.get(0));
      } catch (NumberFormatException e) {
         System.out.println("no proper major num");
            return "0.0";
      }
      if (vlen < 3) {
         System.out.println("major num:" + majornum + " vlen=" + vlen);
         return majornum + ".0";
      }
      String sep0 = vlist.get(1);
      if ( ! (sep0.equals(".") || sep0.equals("-") || sep0.equals("_")) ) {
         return (majornum + ".0");
      } 
      try {
            minornum = (int) Integer.parseInt(vlist.get(2));
      } catch (NumberFormatException e) {
            return (majornum + ".0");
      }
      if (vlen < 5) return majornum + sep0 + minornum;
      
      String sep1 = vlist.get(3);
      if ( ! (sep1.equals(".") || sep1.equals("-") || sep1.equals("_")) ) {
         return (majornum + sep0 + minornum);
      } 
      try {
            maintenancenum = (int) Integer.parseInt(vlist.get(4));
      } catch (NumberFormatException e) {
            return (majornum + sep0 + minornum);
      }
      return (majornum + sep0 + minornum + sep1 + maintenancenum );
   }
 
 
   
   
   /**
    * Returns a new version number that has incremented the major version
    * part of version. If no major version is present, the new version number is 1.0
    * If version is actually a -dev, a -rc or a -alpha version, then the major version
    * is not incremented, but rather the postfix is stripped away.
    */
   public static String incrementMajor(String version) {
      List<String> vlist = parseVersionString(version);
      int vlen = vlist.size();
      if (vlen ==0) return "1.0";
       if (isrc || isalpha || isdev) {
           return revertToStandardVersion(version);
      }
      try {
         int majornum = (int) Integer.parseInt(vlist.get(0));
         majornum++;
         String sep0 = (vlist.size()>=2) ? vlist.get(1) : ".";
         return (majornum + sep0 + 0);
      } catch (NumberFormatException e) {
         System.out.println("Illegal major version number: " + vlist.get(0));
         return version;
      }
   }
   
   /**
    * Returns a new version number that has incremented the minor version
    * part of version. If no minor version is present, the new minor number is 1
    * If version is actually a -dev, a -rc or a -alpha version, then the minor version
    * is not incremented, but rather the postfix is stripped away.
    */
   public static String incrementMinor(String version) {
      List<String> vlist = parseVersionString(version);
      int vlen = vlist.size();
      if (vlen ==0) return "0.1";
      if (isrc || isalpha || isdev) {
           return revertToStandardVersion(version);
      }
      String major = vlist.get(0);
      String sep0 = (vlen>=2) ? vlist.get(1) : ".";
      if (vlen >=3) {
         try {
            int minornum = (int) Integer.parseInt(vlist.get(2));
            minornum++;
            return (major + sep0 + minornum);
         } catch (NumberFormatException e) {
            System.out.println("Illegal minor version number: " + vlist.get(2));
            return version;
         }
      } else {
         return (major + sep0 + 1);
      }
   }
   
      
   /**
    * Returns a new version number that has incremented the maintenance version
    * part of version. If no maintenance version is present, the new maintenance number is 1
    * If version is actually a -dev, a -rc or a -alpha version, then the maintenance version
    * is not incremented, but rather the postfix is stripped away.
    */   
   public static String incrementMaintenance(String version) {
      List<String> vlist = parseVersionString(version);
      int vlen = vlist.size();
      if (vlen ==0) return "0.0.1";
      if (isrc || isalpha|| isdev) {
           return revertToStandardVersion(version);
      }
      String major = vlist.get(0);
      String sep0 = (vlen>=2) ? vlist.get(1) : ".";
      String minor = (vlen>=3) ? vlist.get(2) : "0";
      String sep1 = (vlen>=4) ? vlist.get(3) : ".";
      if (vlen >=5) {
         try {
            int maintenancenum = (int) Integer.parseInt(vlist.get(4));
            maintenancenum++;
            return (major + sep0 + minor + sep1 + maintenancenum );
         } catch (NumberFormatException e) {
            System.out.println("Illegal minor version number: " + vlist.get(2));
            return version;
         }
      } else {
         return (major + sep0 + minor + sep1 + 1);
      }
   }
   
   
 
   /**
    * Returns new major version + "-rc" postfix (increments rc num if already in -rc form)
    */
   public static String majorReleaseCandidateVersion(String version) {
      parseVersionString(version);
      String standardversion = revertToStandardVersion(version) ;
      if (isrc) {     
        rcnum++;
        return( standardversion + "-rc" + rcnum);
      } else {
         return incrementMajor(standardversion) + "-rc1";
      }
   }
   
   /**
    * Returns new minor version + "-rc", or increments rc num if version is already in -rc form
    */
   public static String minorReleaseCandidateVersion(String version) {
      parseVersionString(version);
      String standardversion = revertToStandardVersion(version) ;
      if (isrc) {       
        rcnum++;
        return( standardversion + "-rc" + rcnum);
      } else if (isdev || isalpha) {
         return standardversion + "-rc1";
      } else {
         return incrementMinor(standardversion) + "-rc1";
      }
   }

   /**
    * Returns new minor version + "-dev", or increments dev num if version is already in -dev form
    * If newdev is specified, and >0, that number is used instead, provided it is larger than current devnum
    */
   public static String newDevVersion(String version, String newdev) {
      parseVersionString(version);
      String standardversion = revertToStandardVersion(version) ;
      //System.out.println("newDevVersion(" + version + ", " + newdev + ")");      
      int newdevnum = (int) Integer.parseInt(newdev);
      if (newdevnum > 0) {
         if (newdevnum > devnum) {
            devnum  = newdevnum;
         } else {
           //  0 < newdevnum < devnum: considered illegal
           devnum = devnum + 1; // revert to autoincrement
           System.out.println("WARNING: Manifest version handler: newly specified dev version (" 
              + newdevnum + ") does not follow current dev number; New dev version forced to " + devnum);
         }
      }  else { // newdevnum <= 0, so "not specified". Use autoincrement method 
         devnum = (isdev) ? devnum + 1 : 1;   
      }
      if (isdev || isrc || isalpha) {   
         return( standardversion + "-dev" + devnum);
      } else {
         String newVersion = incrementMinor(standardversion) + "-dev" + devnum;
         //System.out.println("newDevVersion incrementminor, newversion: " + newVersion);    
         return newVersion;
      }
   }

   
   /**
    * Returns new minor version + "-alpha" postfix
    */
   public static String alphaVersion(String version) {
      parseVersionString(version);
      if (isalpha) return version;
      String standardversion = revertToStandardVersion(version);
      return incrementMaintenance(standardversion) + "-alpha";
   }
   

   /* parses vs and determines type  and rc or dev numbers */
   private static List<String> parseVersionString(String vs) {
        ArrayList<String> segs = new ArrayList<String>();
        Pattern pat = Pattern.compile("([0-9]+)([^0-9]*)");
        Matcher matcher = pat.matcher(vs);
        
        Pattern rcpat = Pattern.compile("-rc([0-9]*)");
        Matcher rcmatcher = rcpat.matcher(vs);
        
        Pattern devpat = Pattern.compile("-dev([0-9]*)");
        Matcher devmatcher = devpat.matcher(vs);
         
        Pattern alphapat = Pattern.compile("-alpha");
        Matcher alphamatcher = alphapat.matcher(vs);
        
        isrc = rcmatcher.find();
        if (isrc) {
            String num = rcmatcher.group(1);
            if (num.length() > 0) {
                rcnum = Integer.parseInt(num);
            } else {
               rcnum = 0;
            }
        } else {
            // not rc
        }
        isdev = devmatcher.find();
        if (isdev) {
            String num = devmatcher.group(1);
            if (num.length() > 0) {
                devnum = Integer.parseInt(num);
            } else {
               devnum = 0;
            }
        } else {
            // not dev
        }
        isalpha = alphamatcher.find();
        boolean hasMore = matcher.find();
           while(hasMore) {
              String num = matcher.group(1);
              //System.out.println("matchergroup1=" + num);
              segs.add(num);
              String sep = matcher.group(2);
              //System.out.println("matchergroup2=" + sep);
              if (! sep.equals(""))  segs.add(sep);
              hasMore = matcher.find();
           }
        return segs;    
   }
      
}
