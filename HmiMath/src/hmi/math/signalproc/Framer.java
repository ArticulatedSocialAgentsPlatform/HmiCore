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
