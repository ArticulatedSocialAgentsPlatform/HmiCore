package hmi.math.signalproc;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Unit tests for the HammingWindow
 * @author herwinvw
 */
public class HammingWindowTest
{
    private static final double PRECISION = 0.00001;
    
    @Test
    public void test()
    {
        double output[] = HammingWindow.apply(new double[] { 1, 1, 1, 1, 1 });
        assertEquals(1d, output[2], PRECISION);
        assertEquals(output[1], output[3], PRECISION);
        assertEquals(output[0], output[4], PRECISION);
    }
}
