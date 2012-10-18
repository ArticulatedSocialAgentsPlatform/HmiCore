package hmi.neurophysics;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Unit tests for the BiologicalSwivelCostsEvaluator
 * @author hvanwelbergen
 *
 */
public class BiologicalSwivelCostsEvaluatorTest
{
    private static final double PRECISION = 0.001;
    
    @Test
    public void testL()
    {
        //values for Vince
        BiologicalSwivelCostsEvaluator eval = new BiologicalSwivelCostsEvaluator(1.3, 4.44, 0.45, -0.5);
        
        //values obtained from C++ implementation
        assertEquals(1,eval.getBiologicalCostsOfElbowSwivel(0),PRECISION);          //outside range : 1
        assertEquals(0.36774,eval.getBiologicalCostsOfElbowSwivel(2),PRECISION);
        assertEquals(1,eval.getBiologicalCostsOfElbowSwivel(-2),PRECISION);         //outside range : 1
    }
    
    @Test
    public void testR()
    {
        //values for Vince
        BiologicalSwivelCostsEvaluator eval = new BiologicalSwivelCostsEvaluator(2*3.14 - 4.44, 2*3.14 + 1.3, 0.45, -0.5);
        
        //values obtained from C++ implementation
        assertEquals(1,eval.getBiologicalCostsOfElbowSwivel(0),PRECISION);          //outside range : 1
        assertEquals(0.976139,eval.getBiologicalCostsOfElbowSwivel(3),PRECISION);          
        assertEquals(0.204925,eval.getBiologicalCostsOfElbowSwivel(4),PRECISION);
    }
}
