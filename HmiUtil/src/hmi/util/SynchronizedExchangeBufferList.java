
package hmi.util;

import java.util.ArrayList;
import java.util.List;

/**
 * A List of ExchangeBuffers that acts as a (composed) ExchangeBuffer itself.
 * The List elements are not supposed to be Thread-safe, but the List as a whole is Thread safe.
 */
public class SynchronizedExchangeBufferList implements SynchronizedExchangeBuffer {
   
    private List<ExchangeBuffer> exchBuffers = new ArrayList<>();
    

    /**
     * Adds the specified ExchangeBuffer to the List
     */    
    public synchronized void add(ExchangeBuffer buf) {
        exchBuffers.add(buf);
    }
    

    /**
     * A synchronized method that forwards the putData call to
     * all List elements, in List order.
     */
    public final synchronized void putData() {
        for (ExchangeBuffer buf : exchBuffers) {
            buf.putData();
        }
    }

  
    /**
     * A synchronized method that forwards the getData call to
     * all List elements, in List order.
     */
    public final synchronized void getData() {
        for (ExchangeBuffer buf : exchBuffers) {
            buf.getData();
        }
    }
      
}
