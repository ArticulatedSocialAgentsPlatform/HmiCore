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

    public EyeLidMorpher(String[] morphTargets)
    {
        this.morphTargets = morphTargets;
    }

    public void setEyeLidMorph(float[] qLeftEye, float[] qRightEye, FaceController faceController)
    {
        float rpyL[] = Vec3f.getVec3f();
        float rpyR[] = Vec3f.getVec3f();
        Quat4f.getRollPitchYaw(qRightEye, rpyL);
        Quat4f.getRollPitchYaw(qLeftEye, rpyR);
        float morphValue = rpyL[1] + rpyR[1];
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
