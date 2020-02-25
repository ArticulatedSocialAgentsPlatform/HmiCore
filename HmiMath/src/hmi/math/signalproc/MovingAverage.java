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
