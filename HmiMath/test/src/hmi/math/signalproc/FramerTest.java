package hmi.math.signalproc;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Unit tests for Framer
 * @author herwinvw
 *
 */
public class FramerTest
{
    private static final double PRECISION = 0.00001;

    @Test
    public void testStepOne()
    {
        double frames[][] = Framer.frame(new double[] { 1, 2, 3, 4, 5, 6 }, 2, 1);
        assertEquals(5, frames.length);
        assertArrayEquals(new double[] { 1, 2 }, frames[0], PRECISION);
        assertArrayEquals(new double[] { 2, 3 }, frames[1], PRECISION);
        assertArrayEquals(new double[] { 3, 4 }, frames[2], PRECISION);
        assertArrayEquals(new double[] { 4, 5 }, frames[3], PRECISION);
        assertArrayEquals(new double[] { 5, 6 }, frames[4], PRECISION);
    }

    @Test
    public void testStepTwo()
    {
        double frames[][] = Framer.frame(new double[] { 1, 2, 3, 4, 5, 6 }, 2, 2);
        assertEquals(3, frames.length);
        assertArrayEquals(new double[] { 1, 2 }, frames[0], PRECISION);
        assertArrayEquals(new double[] { 3, 4 }, frames[1], PRECISION);
        assertArrayEquals(new double[] { 5, 6 }, frames[2], PRECISION);
    }

    @Test
    public void testStepTwoIncompleteFrame()
    {
        double frames[][] = Framer.frame(new double[] { 1, 2, 3, 4, 5, 6, 7 }, 2, 2);
        assertEquals(3, frames.length);
        assertArrayEquals(new double[] { 1, 2 }, frames[0], PRECISION);
        assertArrayEquals(new double[] { 3, 4 }, frames[1], PRECISION);
        assertArrayEquals(new double[] { 5, 6 }, frames[2], PRECISION);
    }
    
    @Test
    public void testStepTwoFrameThree()
    {
        double frames[][] = Framer.frame(new double[] { 1, 2, 3, 4, 5, 6, 7 }, 3, 2);
        assertEquals(3, frames.length);
        assertArrayEquals(new double[] { 1, 2, 3 }, frames[0], PRECISION);
        assertArrayEquals(new double[] { 3, 4, 5 }, frames[1], PRECISION);
        assertArrayEquals(new double[] { 5, 6, 7 }, frames[2], PRECISION);
    }
    
    @Test
    public void testStepTwoFrameThreeIncompleteFrame()
    {
        double frames[][] = Framer.frame(new double[] { 1, 2, 3, 4, 5, 6 }, 3, 2);
        assertEquals(2, frames.length);
        assertArrayEquals(new double[] { 1, 2, 3 }, frames[0], PRECISION);
        assertArrayEquals(new double[] { 3, 4, 5 }, frames[1], PRECISION);        
    }
}
