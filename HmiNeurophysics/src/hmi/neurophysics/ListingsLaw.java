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
