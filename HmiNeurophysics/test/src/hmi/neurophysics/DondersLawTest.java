/*******************************************************************************
 * Copyright (C) 2009 Human Media Interaction, University of Twente, the Netherlands
 * 
 * This file is part of the Elckerlyc BML realizer.
 * 
 * Elckerlyc is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Elckerlyc is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Elckerlyc.  If not, see http://www.gnu.org/licenses/.
 ******************************************************************************/
package hmi.neurophysics;

import static org.junit.Assert.assertTrue;
import hmi.math.Quat4f;

import org.junit.Test;

public class DondersLawTest
{
    @Test
    public void testDondersHead()
    {
        float q[] = new float[4];
        float dir[] = {0,0,1};
        DondersLaw.dondersHead(dir, q);
        System.out.println("q:"+Quat4f.toString(q));
        assertTrue(Quat4f.epsilonEquals(Quat4f.getIdentity(), q, 0.01f));
    }
}
