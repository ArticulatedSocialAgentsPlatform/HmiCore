
package hmi.util;

/**
 * An ExchangeBuffer is capable of buffering some data,
 * meant to be exchanged between processes or Threads. 
 * The ExchangeBuffer interface does not require Thread-safety
 * itself, in order to allow for easy composition of ExchangeBuffers.
 * However, for exchange of data between processes that (might) run on
 different Threads, a synchronized implementation has to be used.
 */
public interface ExchangeBuffer {

    /**
     * Buffers data obtained from some data source.
     */
    void putData();
    
    /**
     * Retrieves buffered data and transfers it to some destination.
     */
    void getData();
    
}
