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

package hmi.math.digitalfilter;

/**
 * Butterworth digital low-pass filter
 * 
 * @author Herwin van Welbergen
 */
public final class Butterworth
{

    /** */
    private Butterworth()
    {
    }

    /**
     * Butterworth-filters the data
     * 
     * @param fin input data
     * @param fc cutt-off frequency
     * @param fs sample frequency
     * @param pass pass nr
     * @param fout output data
     */
    public static void butterworth(float[] fin, float fc, float fs, int pass, float[] fout)
    {
        double c = Math.pow(Math.pow(2.0, 1.0 / (double) pass) - 1.0, 0.25);
        double sigmaC = Math.tan(Math.PI * fc / fs) / c;
        double k1 = Math.sqrt(2.0 * sigmaC);
        double k2 = sigmaC * sigmaC;
        double a0 = k2 / (1.0 + k1 + k2);
        double a1 = 2.0 * a0;
        double a2 = a0;
        double k3 = (2.0 * a0) / k2;
        double b1 = -2.0 * a0 + k3;
        double b2 = 1.0 - 2.0 * a0 - k3;
        int length = fin.length;
        fout[0] = fin[0];
        fout[1] = fin[1];
        for (int i = 3; i < length; i++)
        {
            fout[i] = (float) (a0 * fin[i] + a1 * fin[i - 1] + a2 * fin[i - 2] + b1 * fout[i - 1] + b2 * fout[i - 2]);
        }
    }

    /**
     * Butterworth-filters the data, assumes the input is aligned in blocks of width floats
     * 
     * @param fin input data
     * @param fc cutt-off frequency
     * @param fs sample frequency
     * @param pass pass nr
     * @param width block sizes
     * @param fout output data
     */
    public static void butterworth(float[] fin, float fc, float fs, int pass, int width, float[] fout)
    {
        double c = Math.pow(Math.pow(2.0, 1.0 / pass) - 1.0, 0.25);
        double sigmaC = Math.tan(Math.PI * fc / fs) / c;
        double k1 = Math.sqrt(2.0 * sigmaC);
        double k2 = sigmaC * sigmaC;
        double a0 = k2 / (1 + k1 + k2);
        double a1 = 2.0 * a0;
        double a2 = a0;
        double k3 = (2.0 * a0) / k2;
        double b1 = -2.0 * a0 + k3;
        double b2 = 1.0 - 2.0 * a0 - k3;
        int length = fin.length / width;
        for (int i = 0; i < width * 2; i++)
        {
            fout[i] = fin[i];
        }
        for (int i = 2; i < length; i++)
        {
            for (int j = 0; j < width; j++)
            {
                fout[i * width + j] = (float) (a0 * fin[i * width + j] + a1 * fin[(i - 1) * width + j] + a2 * fin[(i - 2) * width + j] + b1
                        * fout[(i - 1) * width + j] + b2 * fout[(i - 2) * width + j]);
            }
        }
    }
}
