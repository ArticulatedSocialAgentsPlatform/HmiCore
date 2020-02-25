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
