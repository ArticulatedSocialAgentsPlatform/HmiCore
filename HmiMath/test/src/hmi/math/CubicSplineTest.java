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
package hmi.math;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CubicSplineTest
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
   public void testEval()
   {
      float[]points = {1,2,3,4,-7,5,10,3,6.5f,100};
      CubicSpline cs = new CubicSpline(points);    
      
      //does it actually interpolate the points?
      for(int i=0;i<points.length;i++)
      {
         float val = cs.eval((float)i/points.length)-points[i];
         assertTrue(val*val<0.000001);       
      }
   }

   @Test
   public void testEvalDiff()
   {
      //fail("Not yet implemented");
   }

   @Test
   public void testEvalDiff2()
   {
      float[]points = {1,2,3,4,-7,5,10,3,6.5f,100};
      CubicSpline cs = new CubicSpline(points);    
      
      //y''(0)=0
      float val = cs.evalDiff2(0);
      assertTrue(val*val<0.000001);
      
      //y''(n-1)=0
      val = cs.evalDiff2(1);
      assertTrue(val*val<0.000001);
   }
   
   @Test
   public void testSinusSpline()
   {
       float points[] = new float[1000];
       for(int i=0;i<1000;i++)
       {
           float t = i*0.01f;
           points[i] = (float)Math.sin(t);
       }
       
       CubicSpline cs = new CubicSpline(points);
       for(int i=0;i<10000;i++)
       {
           float real = (float)Math.sin(i*0.001f);
           float interp = cs.eval(i/10000f);
           //System.out.println("real: "+real);
           //System.out.println("interp: "+interp);
           assertTrue(Math.abs(real-interp)<0.01);
           
           float realDiff = (float)Math.cos(i*0.001f);
           //float interpDiff = cs.evalDiff((float)i/10000f)/0.01f;
           float interpDiff = cs.evalDiff(i/10000f,0.01f);
           //System.out.println("realdiff:   "+realDiff);
           //System.out.println("interpdiff: "+interpDiff);           
           assertTrue(Math.abs(realDiff-interpDiff)<0.01);       
           
           if(i*0.1f >= 998) continue;
           float realDiff2 = -(float)Math.sin(i*0.001f);
           //float interpDiff2 = cs.evalDiff2((float)i/10000f)/(0.01f*0.01f);
           float interpDiff2 = cs.evalDiff2(i/10000f,0.01f);
           //System.out.println("realdiff2:   "+realDiff2);
           //System.out.println("interpdiff2: "+interpDiff2);
           //System.out.println("i            "+i+" "+(float)i/10000f);
           assertTrue(Math.abs(realDiff2-interpDiff2)<0.15);       
       }
   }
}
