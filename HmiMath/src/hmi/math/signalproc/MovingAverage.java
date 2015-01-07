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

/**
 * Moving average filter
 * @author herwinvw
 *
 */
public final class MovingAverage
{
    private MovingAverage(){}
    
    /**
     * Calculates a moving average with the given windowSize over the input. If the windowSize is even, the average is calculated
     * with an extra frame on the right side. Start and end values that fall outside the window are copied.
     */
    public static final double[] movingAverageFilter(double input[], int windowSize)
    {
        double[] output = new double[input.length];
        int leftWidth = (windowSize - 1) / 2;
        int rightWidth = windowSize - 1 - leftWidth;

        for (int i = 0; i < leftWidth; i++)
        {
            output[i] = input[i];
        }
        for (int i = leftWidth; i < input.length - rightWidth; i++)
        {
            double average = 0;
            for (int j = i - leftWidth; j <= i + rightWidth; j++)
            {
                average += input[j];
            }
            output[i] = average / windowSize;
        }
        for (int i = input.length - rightWidth; i < input.length; i++)
        {
            output[i] = input[i];
        }
        return output;
    }
}
