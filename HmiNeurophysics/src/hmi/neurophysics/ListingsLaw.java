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
import hmi.math.Vec4f;

/**
 * Calculates the eye position given a gaze direction, using Listing's Law.
 * 
 * Theoretical background:<br> 
 * Tweed, D. (1997). Three-dimensional model of
 * the human eye-head saccadic system. The Journal of Neurophysiology,
 * 77 (2), pp. 654-666.
 * 
 * @author welberge
 */
public final class ListingsLaw
{
    private ListingsLaw(){}
    
    private static final float[] FORWARD = {0,0,0,1};   //default forward gaze direction in a H-anim skeleton
    
    /**
     * Calculates a natural, unsaturized eye rotation to gaze in direction dir
     * @param dir the direction to gaze to, normalized, in eye coordinates
     * @param result output: the eye rotation quaternion
     */
    public static void listingsEye(float[] dir, float[] result)
    {
        //qeh = (-dir FORWARD)^0.5
        Quat4f.set(result, 0, dir[0],dir[1],dir[2]);
        Vec4f.scale(-1, result);        
        Quat4f.mul(result, FORWARD);
        Quat4f.pow(result, 0.5f);        
    }
}
