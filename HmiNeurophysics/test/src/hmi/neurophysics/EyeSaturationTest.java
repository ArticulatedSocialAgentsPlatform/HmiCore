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

import hmi.math.Quat4f;

import org.junit.Test;
import static org.junit.Assert.assertTrue;

public class EyeSaturationTest
{
    @Test
    public void testEyeSaturation()
    {
        float qCurDes[] = Quat4f.getQuat4f();
        float qDes[] = Quat4f.getQuat4f();
        float q[]= Quat4f.getQuat4f();
        Quat4f.set(qCurDes,0.918567f,   -0.3737188f, -0.12871999f, 0.0f);
        Quat4f.set(qDes,0.91856706f, -0.3737188f, -0.12871997f, 0.0f);
        EyeSaturation.sat(qCurDes, qDes, q);
        assertTrue(Math.abs(Quat4f.length(q)-1)<0.0001);
    }
    
    
    public void testIsSaturized()
    {
        assertTrue(EyeSaturation.isSaturized(Quat4f.getIdentity()));
        
        float q[] = new float[4];
        Quat4f.setFromAxisAngle4f(q, 0,1,0,3);
        assertTrue(!EyeSaturation.isSaturized(q));
    }
}
