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
