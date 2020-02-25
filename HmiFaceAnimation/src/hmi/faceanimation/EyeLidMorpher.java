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
package hmi.faceanimation;

import hmi.math.Quat4f;
import hmi.math.Vec3f;

/**
 * Rotates the eyelids, given left and right eye joint rotations<br>
 * Based on:<br>
 * C. Evinger, K. A. Manning and P. A. Sibony, Eyelid movements. Mechanisms and normal data (1991), in: Investigative ophthalmology and visual science, 32:2(387--400):<br>
 * Eye lid rotation angle is linearly related to the 'up/down gaze angle' (and angles seem also roughly the same).
 * @author hvanwelbergen
 */
public class EyeLidMorpher
{
    private final String[] morphTargets;
    private float morphWeight;

    public EyeLidMorpher(String[] morphTargets)
    {
        this.morphTargets = morphTargets;
        this.morphWeight = 1.0f;
    }
    public EyeLidMorpher(String[] morphTargets, float weight)
    {
        this.morphTargets = morphTargets;
        this.morphWeight = weight;
    }

    public void setEyeLidMorph(float[] qLeftEye, float[] qRightEye, FaceController faceController)
    {
        float rpyL[] = Vec3f.getVec3f();
        float rpyR[] = Vec3f.getVec3f();
        Quat4f.getRollPitchYaw(qRightEye, rpyL);
        Quat4f.getRollPitchYaw(qLeftEye, rpyR);
        float morphValue = (rpyL[1] + rpyR[1]) * this.morphWeight;
        float values[] = new float[morphTargets.length];
        for (int i = 0; i < morphTargets.length; i++)
        {
            float current = faceController.getCurrentWeight(morphTargets[i]);            
            if (Math.abs(current) > Math.abs(morphValue))
            {
                values[i] = current;
            }
            else
            {
                values[i] = morphValue;
            }
        }
        faceController.setMorphTargets(morphTargets, values);        
    }
}
