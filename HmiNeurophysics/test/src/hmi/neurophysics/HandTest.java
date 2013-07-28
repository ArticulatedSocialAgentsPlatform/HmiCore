package hmi.neurophysics;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
/**
 * Unit tests for Hand
 * @author Herwin
 */
public class HandTest
{
    private static final double PRECISION = 0.001;    
    
    @Test
    public void testDIPAndPIPRotation()
    {
        assertEquals(10d, Hand.getPIPRotation(Hand.getDIPRotation(10)), PRECISION);
        assertEquals(10d, Hand.getDIPRotation(Hand.getPIPRotation(10)), PRECISION);
    }
}
