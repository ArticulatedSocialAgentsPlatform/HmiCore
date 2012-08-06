package hmi.util;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the systemclock
 * @author hvanwelbergen
 *
 */
public class SystemClockTest
{
    SystemClock sysClock;
    
    @Before
    public void setup()
    {
        sysClock = new SystemClock();
    }
    
    @After
    public void tearDown()
    {
        sysClock.terminate();
    }
    
    @Test
    public void testRate() throws InterruptedException
    {
        sysClock.start();
        sysClock.setRate(2);                
        Thread.sleep(2000);        
        assertEquals(4,sysClock.getMediaSeconds(),0.4);
    }
}
