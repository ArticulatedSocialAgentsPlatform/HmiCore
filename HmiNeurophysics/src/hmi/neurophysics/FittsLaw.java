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
package hmi.neurophysics;

/**
 * @author welberge
 *         Fitts' Law<br>
 *         Fitts' law predicts the time required to move from a certain start point to a target area.
 *         It is used to model rapid, aimed pointing actions.<br>
 *         T = a + b log_2(1+D/W)
 */
public final class FittsLaw
{

    /** */
    private FittsLaw()
    {
    }

    /**
     * Get the time needed for a 3d pointing action, using a variation on Fitts' Law:<br>
     * ID=log2(D/min(W,H)+1)<br>
     * T=a+b*ID<br>
     * @param a emperically determined constant
     * @param b emperically determined constant
     * @param D pointing distance
     * @param W width of the area to point to
     * @param H height of the area to point to
     * @return the time needed to point
     */
    public static double getTime(double a, double b, double D, double W, double H)
    {
        // ID=log2 (D/min(W,H)+1)
        double ID = Math.log(D / (W < H ? W : H) + 1) / Math.log(2);

        // T=a+b . ID
        return (a + b * ID);
    }

    /**
     * Fitts' law neglecting the size of the target yields a
     * simple logarithmic relation between amplitude and duration.
     * From the ACE system.<br>
     * T = a + b log2(1+D/W) => T = b log(D)/log(2) => b log(D) 
     */
    public static double getHandTrajectoryDuration(double amplitude)
    {
        
        if (amplitude > 0)
        {
            //double b = 0.12;
            double b = 0.24;
            double lgAmp = Math.log(amplitude*100 + 1);
            return b * lgAmp;
        }
        else
        {
            return 0;
        }
    }
}
