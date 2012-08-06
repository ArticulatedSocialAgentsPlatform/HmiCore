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
package hmi.math;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test cases for SpatialVec
 * @author welberge
 *
 */
public class SpatialVecTest
{

    @Before
    public void setUp() throws Exception
    {
    }

    @After
    public void tearDown() throws Exception
    {
    }

    @Test
    public void testSet()
    {
        //fail("Not yet implemented");
    }

    @Test
    public void testAddFloatArrayFloatArrayFloatArray()
    {
        //fail("Not yet implemented");
    }

    @Test
    public void testAddFloatArrayFloatArray()
    {
        //fail("Not yet implemented");
    }

    @Test
    public void testDot()
    {
        //fail("Not yet implemented");
    }

    @Test
    public void testCross()
    {
        float a[]=new float[6];
        float b[]=new float[6];
        float dst[] = new float[6];
        for(int i=0;i<6;i++)
        {
            a[i] = i+1;
            b[i] = 3-i;
        }
        //axa=0
        SpatialVec.cross(dst, a, a);
        //System.out.println(SpatialVec.toString(dst));
        assertTrue(SpatialVec.epsilonEquals(dst, SpatialVec.ZERO, 0.0001f));
        
        //dst = axb => a.dst=b.dst=0
        SpatialVec.cross(dst, a, b);
        assertTrue(Math.abs(SpatialVec.dot(a,dst))<=0.00001);
        assertTrue(Math.abs(SpatialVec.dot(b,dst))<=0.00001);
    }
    
    @Test
    public void testCrossIndexed()
    {
        //Same test on indexed version
        float a[] = new float[12];
        float b[] = new float[12];
        float dst[] = new float[12];
        for(int i=0;i<6;i++)
        {
            a[i] = 0;
            b[i] = 0;
        }
        for(int i=6;i<12;i++)
        {
            a[i] = i+1;
            b[i] = 3-i;
        }
        //axa=0
        SpatialVec.cross(dst,6, a,6, a,6);
        //System.out.println(SpatialVec.toString(dst));
        assertTrue(SpatialVec.epsilonEquals(dst, 6, SpatialVec.ZERO, 0, 0.0001f));
        
        //dst = axb => a.dst=b.dst=0
        SpatialVec.cross(dst,6, a,6, b,6);
        assertTrue(Math.abs(SpatialVec.dot(a,6,dst,6))<=0.00001);
        assertTrue(Math.abs(SpatialVec.dot(b,6,dst,6))<=0.00001); 
    }
    
    
    @Test
    public void testCrossForce()
    {
        float a[]=new float[6];
        float b[]=new float[6];
        float dst[] = new float[6];
        for(int i=0;i<6;i++)
        {
            a[i] = i+1;
            b[i] = 3-i;
        }        
        
        //dst = axb => a.dst=b.dst=0
        SpatialVec.crossForce(dst, a, b);
        assertTrue(Math.abs(SpatialVec.dot(a,dst))<=0.00001);
        assertTrue(Math.abs(SpatialVec.dot(b,dst))<=0.00001);
    }
    
    @Test
    public void testCrossForceIndexed()
    {
        //Same test on indexed version
        float a[] = new float[12];
        float b[] = new float[12];
        float dst[] = new float[12];
        for(int i=0;i<6;i++)
        {
            a[i] = 0;
            b[i] = 0;
        }
        for(int i=6;i<12;i++)
        {
            a[i] = i+1;
            b[i] = 3-i;
        }       
        
        //dst = axb => a.dst=b.dst=0
        SpatialVec.crossForce(dst,6, a,6, b,6);
        assertTrue(Math.abs(SpatialVec.dot(a,6,dst,6))<=0.00001);
        assertTrue(Math.abs(SpatialVec.dot(b,6,dst,6))<=0.00001); 
    }
}
