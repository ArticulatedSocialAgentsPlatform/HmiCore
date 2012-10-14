
package hmi.util;

import java.util.ArrayList;
import java.util.List;

/**
 * A List of ExchangeBuffers that acts as a (composed) ExchangeBuffer itself.
 * The List elements are not supposed to be Thread-safe, and the List itself
 * is also not Thread-safe.
 */
public class ExchangeBufferList implements ExchangeBuffer {
   
    private List<ExchangeBuffer> exchBuffers = new ArrayList<>();
    

    /**
     * Adds the specified ExchangeBuffer to the List
     */    
    public void add(ExchangeBuffer buf) {
        exchBuffers.add(buf);
    }
    

    /**
     * The putData method of the ExchangeBuffer List simply forwards the call to
     * all List elements, in List order.
     */
    public  void putData() {
        for (ExchangeBuffer buf : exchBuffers) {
            buf.putData();
        }
    }

  
    /**
     * The getData method of the ExchangeBuffer List simply forwards the call to
     * all List elements, in List order.
     */
    public  void getData() {
        for (ExchangeBuffer buf : exchBuffers) {
            buf.getData();
        }
    }
      
}
