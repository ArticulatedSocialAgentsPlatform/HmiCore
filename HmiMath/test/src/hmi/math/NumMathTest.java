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

public class NumMathTest
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
    public void testDiff()
    {
        //sin'(x)=cos(x)
        double sinBuf[] = new double[100];
        double sincosBuf[] = new double[200];
        double sinDiffBuf[] = new double[100];
        double sincosDiffBuf[] = new double[200];
        for(int i=0;i<100;i++)
        {
            sinBuf[i]=Math.sin(i*0.01);
            sincosBuf[i*2]=Math.sin(i*0.01);
            sincosBuf[i*2+1]=Math.cos(i*0.01);
        }
        NumMath.diff(sinDiffBuf, sinBuf, 0.01);
        for(int i=0;i<99;i++)
        {
            //System.out.println("sin'(x), cos(x): "+sinDiffBuf[i]+","+Math.cos(i*0.01));
            assertTrue(Math.abs(sinDiffBuf[i]-Math.cos(i*0.01))<0.0005);
        }
        
        NumMath.diff(sincosDiffBuf, sincosBuf, 0.01,2);
        for(int i=1;i<99;i++)
        {
            //System.out.println("sin'(x), cos(x): "+sincosDiffBuf[i*2]+","+Math.cos(i*0.01));
            //System.out.println("cos'(x), sin(x): "+sincosDiffBuf[i*2+1]+","+Math.sin(i*0.01));
            assertTrue(Math.abs(sincosDiffBuf[i*2]-Math.cos(i*0.01))<0.0005);
            assertTrue(Math.abs(sincosDiffBuf[i*2+1]+Math.sin(i*0.01))<0.0005);
        }
        //first values are allowed to have less precision
        assertTrue(Math.abs(sincosDiffBuf[0]-Math.cos(0))<0.005);
        assertTrue(Math.abs(sincosDiffBuf[1]+Math.sin(0))<0.005);
    }

    @Test
    public void testDiff2()
    {
        //sin''(x)=-sin(x)
        double sinBuf[] = new double[100];
        double sincosBuf[] = new double[200];
        double sinDiff2Buf[] = new double[100];
        double sincosDiff2Buf[] = new double[200];
        for(int i=0;i<100;i++)
        {
            sinBuf[i]=Math.sin(i*0.01);
            sincosBuf[i*2]=Math.sin(i*0.01);
            sincosBuf[i*2+1]=Math.cos(i*0.01);
        }
        NumMath.diff2(sinDiff2Buf, sinBuf, 0.01);
        for(int i=1;i<99;i++)
        {
            //System.out.println("sin''("+i*0.01+"), sin("+i*0.01+"): "+sinDiff2Buf[i]+","+Math.sin(i*0.01));
            assertTrue(Math.abs(sinDiff2Buf[i]+Math.sin(i*0.01))<0.0005);
        }
        for(int i=1;i<99;i++)
        {
            //System.out.println("sin("+i*0.01+")="+Math.sin(i*0.01));
            //System.out.println("sin''("+i*0.01+")="+NumMath.diff2(sinBuf[i-1], sinBuf[i],sinBuf[i+1], 0.01));
            assertTrue(Math.abs(NumMath.diff2(sinBuf[i-1], sinBuf[i],sinBuf[i+1], 0.01)+Math.sin(i*0.01))<0.0005);            
        }
        
        NumMath.diff2(sincosDiff2Buf, sincosBuf, 0.01,2);
        for(int i=1;i<99;i++)
        {
            //System.out.println("sin''(x), sin(x): "+sincosDiff2Buf[i*2]+","+Math.sin(i*0.01));
            //System.out.println("cos''(x), cos(x): "+sincosDiff2Buf[i*2+1]+","+Math.cos(i*0.01));
            assertTrue(Math.abs(sincosDiff2Buf[i*2]+Math.sin(i*0.01))<0.0005);
            assertTrue(Math.abs(sincosDiff2Buf[i*2+1]+Math.cos(i*0.01))<0.0005);
        }
    }
    
    @Test
    public void testInterpolate()
    {
        double sinBuf[] = new double[100];
        double sincosBuf[] = new double[200];
        double dst[] = new double[2];
        for(int i=0;i<100;i++)
        {
            sinBuf[i]=Math.sin(i*0.01);
            sincosBuf[i*2]=Math.sin(i*0.01);
            sincosBuf[i*2+1]=Math.cos(i*0.01);
        }
        assertTrue(Math.abs(NumMath.interpolate(sinBuf, 0.01, 0.5)-Math.sin(0.5))<0.0005);
        assertTrue(Math.abs(NumMath.interpolate(sinBuf, 0.01, 0.511)-Math.sin(0.511))<0.0005);
        
        NumMath.interpolate(dst,sincosBuf, 0.01, 0.5);
        assertTrue(Math.abs(dst[0]-Math.sin(0.5))<0.0005);  
        assertTrue(Math.abs(dst[1]-Math.cos(0.5))<0.0005);
        
        NumMath.interpolate(dst,sincosBuf, 0.01, 0.511);
        assertTrue(Math.abs(dst[0]-Math.sin(0.511))<0.0005);  
        assertTrue(Math.abs(dst[1]-Math.cos(0.511))<0.0005);
        
        //boundary checks
        NumMath.interpolate(dst,sincosBuf, 0.01, 0);
        assertTrue(Math.abs(dst[0]-Math.sin(0))<0.0005);  
        assertTrue(Math.abs(dst[1]-Math.cos(0))<0.0005);
        
        NumMath.interpolate(dst,sincosBuf, 0.01, -1);
        assertTrue(Math.abs(dst[0]-Math.sin(0))<0.0005);  
        assertTrue(Math.abs(dst[1]-Math.cos(0))<0.0005);
        
        NumMath.interpolate(dst,sincosBuf, 0.01f, 0.99f);
        assertTrue(Math.abs(dst[0]-Math.sin(0.99))<0.01);  
        assertTrue(Math.abs(dst[1]-Math.cos(0.99))<0.01);
        
        NumMath.interpolate(dst,sincosBuf, 0.01, 1);        
        assertTrue(Math.abs(dst[0]-Math.sin(1))<0.01);  
        assertTrue(Math.abs(dst[1]-Math.cos(1))<0.01);
        
        NumMath.interpolate(dst,sincosBuf, 0.01, 2);
        assertTrue(Math.abs(dst[0]-Math.sin(1))<0.01);  
        assertTrue(Math.abs(dst[1]-Math.cos(1))<0.01);
    }
    
    @Test
    public void testInterpolateFloat()
    {
        float sinBuf[] = new float[100];
        float sincosBuf[] = new float[200];
        float dst[] = new float[2];
        for(int i=0;i<100;i++)
        {
            sinBuf[i]=(float)Math.sin(i*0.01);
            sincosBuf[i*2]=(float)Math.sin(i*0.01);
            sincosBuf[i*2+1]=(float)Math.cos(i*0.01);
        }
        assertTrue(Math.abs(NumMath.interpolate(sinBuf, 0.01f, 0.5f)-Math.sin(0.5))<0.0005);
        assertTrue(Math.abs(NumMath.interpolate(sinBuf, 0.01f, 0.511f)-Math.sin(0.511))<0.0005);
        
        NumMath.interpolate(dst,sincosBuf, 0.01f, 0.5f);
        assertTrue(Math.abs(dst[0]-Math.sin(0.5))<0.0005);  
        assertTrue(Math.abs(dst[1]-Math.cos(0.5))<0.0005);
        
        NumMath.interpolate(dst,sincosBuf, 0.01f, 0.511f);
        assertTrue(Math.abs(dst[0]-Math.sin(0.511))<0.0005);  
        assertTrue(Math.abs(dst[1]-Math.cos(0.511))<0.0005);
        
        //boundary checks
        NumMath.interpolate(dst,sincosBuf, 0.01f, 0f);
        assertTrue(Math.abs(dst[0]-Math.sin(0))<0.0005);  
        assertTrue(Math.abs(dst[1]-Math.cos(0))<0.0005);
        
        NumMath.interpolate(dst,sincosBuf, 0.01f, -1f);
        assertTrue(Math.abs(dst[0]-Math.sin(0))<0.0005);  
        assertTrue(Math.abs(dst[1]-Math.cos(0))<0.0005);
        
        NumMath.interpolate(dst,sincosBuf, 0.01f, 0.99f);
        assertTrue(Math.abs(dst[0]-Math.sin(0.99))<0.01);  
        assertTrue(Math.abs(dst[1]-Math.cos(0.99))<0.01);
        
        NumMath.interpolate(dst,sincosBuf, 0.01f, 1f);        
        assertTrue(Math.abs(dst[0]-Math.sin(1))<0.01);  
        assertTrue(Math.abs(dst[1]-Math.cos(1))<0.01);
        
        NumMath.interpolate(dst,sincosBuf, 0.01f, 2f);
        assertTrue(Math.abs(dst[0]-Math.sin(1))<0.01);  
        assertTrue(Math.abs(dst[1]-Math.cos(1))<0.01);
    }
}
