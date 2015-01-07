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
package hmi.math.signalproc;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Unit tests for Framer
 * @author herwinvw
 *
 */
public class FramerTest
{
    private static final double PRECISION = 0.00001;

    @Test
    public void testStepOne()
    {
        double frames[][] = Framer.frame(new double[] { 1, 2, 3, 4, 5, 6 }, 2, 1);
        assertEquals(5, frames.length);
        assertArrayEquals(new double[] { 1, 2 }, frames[0], PRECISION);
        assertArrayEquals(new double[] { 2, 3 }, frames[1], PRECISION);
        assertArrayEquals(new double[] { 3, 4 }, frames[2], PRECISION);
        assertArrayEquals(new double[] { 4, 5 }, frames[3], PRECISION);
        assertArrayEquals(new double[] { 5, 6 }, frames[4], PRECISION);
    }

    @Test
    public void testStepTwo()
    {
        double frames[][] = Framer.frame(new double[] { 1, 2, 3, 4, 5, 6 }, 2, 2);
        assertEquals(3, frames.length);
        assertArrayEquals(new double[] { 1, 2 }, frames[0], PRECISION);
        assertArrayEquals(new double[] { 3, 4 }, frames[1], PRECISION);
        assertArrayEquals(new double[] { 5, 6 }, frames[2], PRECISION);
    }

    @Test
    public void testStepTwoIncompleteFrame()
    {
        double frames[][] = Framer.frame(new double[] { 1, 2, 3, 4, 5, 6, 7 }, 2, 2);
        assertEquals(3, frames.length);
        assertArrayEquals(new double[] { 1, 2 }, frames[0], PRECISION);
        assertArrayEquals(new double[] { 3, 4 }, frames[1], PRECISION);
        assertArrayEquals(new double[] { 5, 6 }, frames[2], PRECISION);
    }

    @Test
    public void testStepTwoFrameThree()
    {
        double frames[][] = Framer.frame(new double[] { 1, 2, 3, 4, 5, 6, 7 }, 3, 2);
        assertEquals(3, frames.length);
        assertArrayEquals(new double[] { 1, 2, 3 }, frames[0], PRECISION);
        assertArrayEquals(new double[] { 3, 4, 5 }, frames[1], PRECISION);
        assertArrayEquals(new double[] { 5, 6, 7 }, frames[2], PRECISION);
    }

    @Test
    public void testStepTwoFrameThreeIncompleteFrame()
    {
        double frames[][] = Framer.frame(new double[] { 1, 2, 3, 4, 5, 6 }, 3, 2);
        assertEquals(2, frames.length);
        assertArrayEquals(new double[] { 1, 2, 3 }, frames[0], PRECISION);
        assertArrayEquals(new double[] { 3, 4, 5 }, frames[1], PRECISION);
    }

    @Test
    public void testStepTwoFrameFiveIncompleteFrame()
    {
        double frames[][] = Framer.frame(new double[] { 1, 2, 3, 4, 5, 6 }, 5, 2);
        assertEquals(1, frames.length);
        assertArrayEquals(new double[] { 1, 2, 3, 4, 5 }, frames[0], PRECISION);
    }

    @Test
    public void testStepFiveFrameTwoIncompleteFrame()
    {
        double frames[][] = Framer.frame(new double[] { 1, 2, 3, 4, 5, 6, 7, 8 }, 2, 5);
        assertEquals(2, frames.length);
        assertArrayEquals(new double[] { 1, 2 }, frames[0], PRECISION);
        assertArrayEquals(new double[] { 6, 7 }, frames[1], PRECISION);
    }
}
