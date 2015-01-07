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
 * Diff JUnit test
 */

package hmi.util;

import org.junit.*;

/**
 * JUnit test for hmi.util.Diff
 */
public class DiffTest {
    
    public DiffTest() {
    }

    @Before
    public void setUp()  { // common initialization, executed for every test.
    }

    @After
    public void tearDown() {
    }

    @Test
    public void diffTest() {       
       int[] a0 = null; int[] b0 = null;
       int[] a1 = new int[] { 1, 2, 3};
       int[] b1 = new int[] { 1, 4, 5};
       String d1 = Diff.showDiff("a0/b0", a0, b0);
       //System.out.println("d1=" + d1);
       
       String d2 = Diff.showDiff("a1/b0", a1, b0);
       //System.out.println("d2=" + d2);
       
       String d3 = Diff.showDiff("a1/b1", a1, b1);
       //System.out.println("d3=" + d3);
       
       
       
       String d4 = Diff.showDiff("a1/a1", a1, a1);
       //System.out.println("d4=" + d4);
       
       
       float[] f1 = new float[] {1.0f, 2.0f, 3.0f, 4.0f};
       float[] f2 = new float[] {1.0f, 2.0f, 3.0f};
       
       String d5 = Diff.showDiff("f1/f2", f1, f2);
       //System.out.println("d5=" + d5);
       
       String[] s1 = new String[] {"aap", "noot", "mies"};
       String[] s2 = new String[] {"aap", "noot", "nies"};
       String d6 = Diff.showDiff("s1/s2", s1, s2);
       //System.out.println("d6=" + d6);
       
       
     
    } 

  
}
