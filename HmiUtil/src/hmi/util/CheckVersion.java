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
/*
 
 */

package hmi.util;

import javax.swing.JOptionPane;

/**
 *
 * @author zwiers
 */
public final class CheckVersion {
   
   /*
    * disable creation of CheckVersion Objects
    */
   private CheckVersion() {};
   
   
   /**
    * Checks whether the current specification version meets the specified required version;
    * if not, a RuntimeException is thrown.
    * No check is performed when manifest info is not available.
    */
   public static void requireVersion(String packageName, String requiredVersion) {
      Package pack = Package.getPackage(packageName);      
      String specificationVersion = pack.getSpecificationVersion();
      if ( specificationVersion == null) return; // no check possible, so assume ok
      if (isCompatibleWith(requiredVersion, specificationVersion)) return;
      String msg = "Package " + packageName + " Version " + pack.getSpecificationVersion() 
        + " does not meet the required version " + requiredVersion;
      JOptionPane.showMessageDialog(null, msg, "Package Info", JOptionPane.PLAIN_MESSAGE);
      throw new RuntimeException(msg); 
   } 
    
   /**
    * Returns the specification version from the Manifest.mf file, if available,
    * or else an empty String.
    */ 
   public static String getVersion(String packageName) {
       Package pack = Package.getPackage(packageName);
      String result = pack.getSpecificationVersion();
      return (result==null) ? "" : result;
   } 
   
   
   /**
    * checks whether the package specification version is compatible with a certain desired version.
    * &quot;isCompatibleWith(desiredversion)&quot; return true iff the desiredVersion is smaller or equal
    * than the package specification version, where "smaller than" is determined by the lexicographic
    * order on major, minor, and micro version numbers. 
    * (Missing numbers are considered to be 0). For instance, when the package specification version
    * would be 2.1, then some examples of compatible desired versions are: 1, 1.0, 1.6, 2.0.4,  2.1, 2.1.0,
    * whereas desired versions like 2.2, 3.0, or 2.1.1 would not be compatible.
    */
   public static boolean isCompatibleWith(String desiredVersion, String specificationVersion) {
       //String specificationVersion = pack.getSpecificationVersion();
       if (specificationVersion == null) return true; // no spec version available, so assume ok
       String[] desiredNums = desiredVersion.split("[.]");
       String[] specificationNums= specificationVersion.split("[.]");  
       int desired, specified;     
       try {
          for (int vn=0; vn<desiredNums.length; vn++) {
             //System.out.println("  desired num " + vn + ": " + desiredNums[vn] + " specification num: " + specificationNums[vn]);
             desired = Integer.valueOf(desiredNums[vn]);
             if (vn<specificationNums.length) {
               specified = Integer.valueOf(specificationNums[vn]);
             } else {
                specified = 0;
             }
         
             if ( desired < specified ) return true;
             if ( desired > specified ) return false;              
          }
          return true;
       } catch (NumberFormatException e) {
           System.out.println("CheckVersion.isCompatibelWith method: illegal version numbers: " + desiredVersion + " / " + specificationVersion);
           return false;
       }
   }
   
 
   
}
