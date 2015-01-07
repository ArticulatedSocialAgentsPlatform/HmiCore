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
