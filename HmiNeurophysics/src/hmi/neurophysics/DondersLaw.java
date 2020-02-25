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
package hmi.neurophysics;

import hmi.math.Quat4f;

/**
 * Calculates a natural the head rotation to gaze in direction a target direction.
 * 
 * Theoretical background:<br>
 * Tweed, D. (1997). Three-dimensional model of the human eye-head saccadic system. The Journal of Neurophysiology, 77 (2), pp. 654-666.
 * 
 * @author welberge
 */
public final class DondersLaw
{
    private static final double DELTA_H = 0.9;
    private static final double DELTA_V = 0.3;
    private static final double DELTA_T = -0.15;

    private static final float[] FORWARD = { 0, 0, 0, 1 }; // default forward gaze direction in a H-anim skeleton

    private DondersLaw()
    {
    }

    /**
     * Calculates a natural the head rotation to gaze in direction dir
     * 
     * @param dir the direction to gaze to, normalized, in head coordinates
     * @param result output: the head rotation quaternion
     */
    public static void dondersHead(float[] dir, float[] result)
    {
        // x = (-dir * (0,0,0,1))^0.5
        Quat4f.set(result, 0, -dir[0], -dir[1], -dir[2]);
        Quat4f.mul(result, FORWARD);
        Quat4f.pow(result, 0.5f);

        // y = (0,x.y*0.3,x.z*0.9,x.y*x.z*-0.15)
        result[Quat4f.x] *= DELTA_V;
        result[Quat4f.y] *= DELTA_H;
        result[Quat4f.z] *= DELTA_T;

        // normalize y
        result[Quat4f.s] = (float) Math.sqrt(1 - (result[Quat4f.x] * result[Quat4f.x] + result[Quat4f.y] * result[Quat4f.y] + result[Quat4f.z]
                * result[Quat4f.z]));
    }
}
