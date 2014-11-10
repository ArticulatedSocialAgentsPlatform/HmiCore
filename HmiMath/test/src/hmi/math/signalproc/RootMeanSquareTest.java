package hmi.math.signalproc;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Unit tests for root mean square
 * @author herwinvw
 *
 */
public class RootMeanSquareTest
{
    private static final double PRECISION = 0.00001;
    
    @Test
    public void rootMeanSquareTest()
    {
        assertEquals((1+4+9)/3d, RootMeanSquare.rootMeanSquare(new double[]{1,2,3}),PRECISION);
    }
}
