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
 * Splits an array into frames
 * @author herwinvw
 * 
 */
public final class Framer
{
    private Framer()
    {
    }

    public final static double[][] frame(double input[], int frameSize, int frameStep)
    {
        int l = input.length / frameStep;
        int oversize = ((l - 1) * frameStep + frameSize)-input.length;
        l -= Math.ceil( (double)oversize / (double)frameStep);

        double[][] output = new double[l][];
        int k = 0;
        for (int i = 0; i < l * frameStep; i += frameStep)
        {
            output[k] = new double[frameSize];
            for (int j = 0; j < frameSize; j++)
            {
                output[k][j] = input[i + j];
            }
            k++;
        }
        return output;
    }
}
