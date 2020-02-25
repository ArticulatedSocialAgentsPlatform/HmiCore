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
