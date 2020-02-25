/*******************************************************************************
 * Copyright (C) 2009-2020 Human Media Interaction, University of Twente, the Netherlands
 *
 * This file is part of the Articulated Social Agents Platform BML realizer (ASAPRealizer).
 *
 * ASAPRealizer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License (LGPL) as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ASAPRealizer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ASAPRealizer.  If not, see http://www.gnu.org/licenses/.
 ******************************************************************************/
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
