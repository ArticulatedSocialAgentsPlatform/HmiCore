package hmi.math.signalproc;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;

/**
 * Unit tests for moving avg
 * @author herwinvw
 *
 */
public class MovingAverageTest
{
    private static final double PRECISION = 0.00001;

    @Test
    public void test()
    {
        assertArrayEquals(new double[] { 1, (1 + 2 + 3) / 3d, (2 + 3 + 4) / 3d, (3 + 4 + 5) / 3d, (4 + 5 + 6) / 3d, 6 },
                MovingAverage.movingAverageFilter(new double[] { 1, 2, 3, 4, 5, 6 }, 3), PRECISION);
    }

    @Test
    public void testOneElement()
    {
        assertArrayEquals(new double[] { 1 }, MovingAverage.movingAverageFilter(new double[] { 1 }, 3), PRECISION);
    }

    @Test
    public void testEvenFrameWidth()
    {
        assertArrayEquals(new double[] { 1, (1 + 2 + 3 + 4) / 4d, (2 + 3 + 4 + 5) / 4d, (3 + 4 + 5 + 6) / 4d, 5, 6 },
                MovingAverage.movingAverageFilter(new double[] { 1, 2, 3, 4, 5, 6 }, 4), PRECISION);
    }
}
