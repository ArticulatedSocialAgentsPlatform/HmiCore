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
