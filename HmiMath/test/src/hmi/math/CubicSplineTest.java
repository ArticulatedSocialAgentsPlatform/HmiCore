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
