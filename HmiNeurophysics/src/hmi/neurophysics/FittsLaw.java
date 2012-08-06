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
