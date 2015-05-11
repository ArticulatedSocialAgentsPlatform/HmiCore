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

import org.junit.Test;

/**
 * Unit tests for moving avg
 * @author herwinvw
 *
 */
public class MovingAverageTest
{
    private static final double PRECISION = 0.00001;

    @Test
    public void test()
    {
        assertArrayEquals(new double[] { 1, (1 + 2 + 3) / 3d, (2 + 3 + 4) / 3d, (3 + 4 + 5) / 3d, (4 + 5 + 6) / 3d, 6 },
                MovingAverage.movingAverageFilter(new double[] { 1, 2, 3, 4, 5, 6 }, 3), PRECISION);
    }

    @Test
    public void testOneElement()
    {
        assertArrayEquals(new double[] { 1 }, MovingAverage.movingAverageFilter(new double[] { 1 }, 3), PRECISION);
    }

    @Test
    public void testEvenFrameWidth()
    {
        assertArrayEquals(new double[] { 1, (1 + 2 + 3 + 4) / 4d, (2 + 3 + 4 + 5) / 4d, (3 + 4 + 5 + 6) / 4d, 5, 6 },
                MovingAverage.movingAverageFilter(new double[] { 1, 2, 3, 4, 5, 6 }, 4), PRECISION);
    }
}
