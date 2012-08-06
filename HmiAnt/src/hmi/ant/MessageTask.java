/**
 * 
 */

package hmi.ant;  
import org.apache.tools.ant.Task;

/**
 * 
 */
public class MessageTask extends Task {
  
   private String message = "";
   
   public void setMessage(String message) {
      this.message = message;
   }
   
   @Override
   public void execute() {
      log(message);
   }
      
}
