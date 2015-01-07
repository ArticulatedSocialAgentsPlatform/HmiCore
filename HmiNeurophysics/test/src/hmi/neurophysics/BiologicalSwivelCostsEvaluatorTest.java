/*******************************************************************************
 * The MIT License (MIT)
 * Copyright (c) 2015 University of Twente
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *******************************************************************************/
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
    
    private BiologicalSwivelCostsEvaluator getVinceEvalLeft()
    {
        return new BiologicalSwivelCostsEvaluator(1.3, 4.44, 0.45, -0.5);
    }
    
    private BiologicalSwivelCostsEvaluator getVinceEvalRight()
    {
        return new BiologicalSwivelCostsEvaluator(2*3.14 - 4.44, 2*3.14 + 1.3, 0.45, -0.5);
    }
    
    @Test
    public void testL()
    {
        BiologicalSwivelCostsEvaluator eval =  getVinceEvalLeft();
        
        //values obtained from C++ implementation
        assertEquals(1,eval.getBiologicalCostsOfElbowSwivel(0),PRECISION);          //outside range : 1
        assertEquals(0.36774,eval.getBiologicalCostsOfElbowSwivel(2),PRECISION);
        assertEquals(1,eval.getBiologicalCostsOfElbowSwivel(-2),PRECISION);         //outside range : 1
    }
    
    @Test
    public void testR()
    {
        BiologicalSwivelCostsEvaluator eval = getVinceEvalRight();
        
        //values obtained from C++ implementation
        assertEquals(1,eval.getBiologicalCostsOfElbowSwivel(0),PRECISION);          //outside range : 1
        assertEquals(0.976139,eval.getBiologicalCostsOfElbowSwivel(3),PRECISION);          
        assertEquals(0.204925,eval.getBiologicalCostsOfElbowSwivel(4),PRECISION);
    }
    
    @Test
    public void testRMinCosts()
    {
        BiologicalSwivelCostsEvaluator eval = getVinceEvalRight();
        
        assertEquals(4.14,eval.getSwivelAngleWithMinCost(0),PRECISION);
        assertEquals(4.14,eval.getSwivelAngleWithMinCost(4.14),PRECISION);
        assertEquals(4.14,eval.getSwivelAngleWithMinCost(-1),PRECISION);
        assertEquals(4.24,eval.getSwivelAngleWithMinCost(6),PRECISION);
    }
    
    @Test
    public void testLMinCosts()
    {
        BiologicalSwivelCostsEvaluator eval = getVinceEvalLeft();
        
        assertEquals(2.3,eval.getSwivelAngleWithMinCost(0),PRECISION);
        assertEquals(2.4,eval.getSwivelAngleWithMinCost(4),PRECISION);
        assertEquals(2.3,eval.getSwivelAngleWithMinCost(1.3),PRECISION);
    }
}
