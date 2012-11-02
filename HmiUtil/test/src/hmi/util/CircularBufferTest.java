package hmi.util;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Unit tests for the circularBuffer
 * @author hvanwelbergen
 *
 */
public class CircularBufferTest
{
    @Test
    public void testAdd()
    {
        CircularBuffer<Integer> b = new CircularBuffer<>(2);
        b.add(1);
        b.add(2);
        assertEquals(1,(int)b.get(0));
        assertEquals(2,(int)b.get(1));
        b.add(3);
        assertTrue(b.size()==2);
        assertEquals(2,(int)b.get(0));
        assertEquals(3,(int)b.get(1));
    }
}
