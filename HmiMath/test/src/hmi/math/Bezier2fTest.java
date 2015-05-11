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
/*
 * Bezier2f JUnit test
 */

package hmi.math;

import org.junit.*;


/**
 * JUnit test for hmi.math.Bezier2f
 */
public class Bezier2fTest {
    
    public Bezier2fTest() {
    }

    @Before
    public void setUp()  { // common initialization, executed for every test.
    }

    @After
    public void tearDown() {
    }

    @Test
    public void basics() {       
       float[] px = new float[] {0.0f, 2.0f, 3.0f, 6.0f, 9.0f, 10.0f, 15.0f};
       float[] py = new float[] {0.0f, 2.0f, 2.0f, 0.0f, -2.0f, 4.0f, 0.0f};

       Bezier2f bez = new Bezier2f(px, py);
       float[] p = new float[2];

       bez.eval(p, 0.0f);
       bez.eval(p, 0.4f);
       bez.eval(p, 0.5f);
       bez.eval(p, 0.8f);
       bez.eval(p, 1.0f);
    } 

    @Test
    public void mergedCoordTest() {       
       float[] points = new float[] {0.0f, 0.0f,   2.0f, 2.0f,   3.0f, 2.0f,   6.0f, 0.0f,   9.0f, -2.0f,   10.0f, 4.0f, 15.0f, 0.0f};
    
       Bezier2f bez = new Bezier2f(points);
       float[] p = new float[2];

       bez.eval(p, 0.0f);
       bez.eval(p, 0.4f);
       bez.eval(p, 0.5f);
       bez.eval(p, 0.8f);
       bez.eval(p, 1.0f);
    } 

    @Test
    public void pointsVectorsWeightsTest() {
       float[] p0 = new float[]{0.0f, 0.0f};
       float[] v0 = new float[]{1.0f, 1.0f};
      
       float[] p1 = new float[]{6.0f, 0.0f};
       float[] v1 = new float[]{3.0f, -2.0f};
     
       float[] p2 = new float[]{15.0f, 0.0f};
       float[] v2 = new float[]{5.0f, -4.0f};
      
      
       float[][] points = new float[][]{p0, p1, p2};
       float[][] vectors = new float[][]{v0, v1, v2};
       float[] weights = new float[] { 2.0f, 1.0f, 1.0f, 1.0f};
       
       Bezier2f bez = Bezier2f.bezier2fFromPointsVectorsWeights(points, vectors, weights);
       float fminus = bez.evalFX(-1.0f);
       System.out.println("fminus=" + fminus);
       float f0 = bez.evalFX(0.0f);
       System.out.println("f0=" + f0);
      
       float f3 = bez.evalFX(3f);
       System.out.println("f3=" + f3);
      
       float f6 = bez.evalFX(6.0f);
       System.out.println("f6=" + f6);
      
       float f8 = bez.evalFX(8.0f);
       System.out.println("f8=" + f8);
      
       float f12 = bez.evalFX(12.0f);
       System.out.println("f12=" + f12);
      
       float f15 = bez.evalFX(15.0f);
       System.out.println("f15=" + f15);
      
    }

    @Test
    public void pointsVectorsSingleWeightsTest() {
       float[] p0 = new float[]{0.0f, 0.0f};
       float[] v0 = new float[]{1.0f, 1.0f};
      
       float[] p1 = new float[]{6.0f, 0.0f};
       float[] v1 = new float[]{3.0f, -2.0f};
     
       float[] p2 = new float[]{15.0f, 0.0f};
       float[] v2 = new float[]{5.0f, -4.0f};
      
      
       float[][] points = new float[][]{p0, p1, p2};
       float[][] vectors = new float[][]{v0, v1, v2};
       float[] weights = new float[] { 2.0f, 1.0f, 1.0f};
       
       Bezier2f bez = Bezier2f.bezier2fFromPointsVectorsSingleWeights(points, vectors, weights);
       float fminus = bez.evalFX(-1.0f);
       System.out.println("fminus=" + fminus);
       float f0 = bez.evalFX(0.0f);
       System.out.println("f0=" + f0);
      
       float f3 = bez.evalFX(3f);
       System.out.println("f3=" + f3);
      
       float f6 = bez.evalFX(6.0f);
       System.out.println("f6=" + f6);
      
       float f8 = bez.evalFX(8.0f);
       System.out.println("f8=" + f8);
      
       float f12 = bez.evalFX(12.0f);
       System.out.println("f12=" + f12);
      
       float f15 = bez.evalFX(15.0f);
       System.out.println("f15=" + f15);
      
    }


    @Test
    public void evalFXTest() {
       float[] px = new float[] {0.0f, 2.0f, 3.0f, 6.0f, 9.0f, 10.0f, 15.0f};
       float[] py = new float[] {0.0f, 2.0f, 2.0f, 0.0f, -2.0f, 4.0f, 0.0f};

       Bezier2f bez = new Bezier2f(px, py);
      // float[] p = new float[2];
       
       float fminus = bez.evalFX(-1.0f);
       System.out.println("fminus=" + fminus);
       float f0 = bez.evalFX(0.0f);
       System.out.println("f0=" + f0);
      
       float f3 = bez.evalFX(3f);
       System.out.println("f3=" + f3);
      
       float f6 = bez.evalFX(6.0f);
       System.out.println("f6=" + f6);
      
       float f8 = bez.evalFX(8.0f);
       System.out.println("f8=" + f8);
      
       float f12 = bez.evalFX(12.0f);
       System.out.println("f12=" + f12);
      
       float f15 = bez.evalFX(15.0f);
       System.out.println("f15=" + f15);
   }
   
   
   
  
}
