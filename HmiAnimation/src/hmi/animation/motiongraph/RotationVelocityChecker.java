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
package hmi.animation.motiongraph;

import hmi.animation.SkeletonInterpolator;
import hmi.math.Quat4f;
import hmi.math.Vec3f;

public class RotationVelocityChecker implements TransitionChecker
{
    private final float epsilonRotation;
    private final float epsilonVelocity;

    public RotationVelocityChecker(float epsilonRotation, float epsilonVelocity)
    {
        this.epsilonRotation = epsilonRotation;
        this.epsilonVelocity = epsilonVelocity;
    }

    private float[] getAngularVelocity(SkeletonInterpolator ski, int frame)
    {
        float[] config, qrate;
        float[] avel = new float[ski.getConfigSize() / 4 * 3];

        if (frame + 1 < ski.size())
        {
            config = ski.getConfig(frame);
            qrate = ski.getConfig(frame + 1);
        }
        else if (frame - 1 >= 0)
        {
            config = ski.getConfig(frame - 1);
            qrate = ski.getConfig(frame);
        }
        else
        {
            return avel;
        }

        Vec3f.sub(qrate, config);
        for (int i = 0; i < ski.getConfigSize() / 4; i++)
        {
            Quat4f.setAngularVelocityFromQuat4f(avel, i * 3, config, i * 4, qrate, i * 4);
        }
        return avel;
    }

    @Override
    public boolean allowTransition(SkeletonInterpolator skiIn, SkeletonInterpolator skiOut, int frameIn, int frameOut)
    {
        float configIn[] = skiIn.getConfig(frameIn);
        float configOut[] = skiOut.getConfig(frameOut);
        for (int i = 0; i < skiIn.getConfigSize() / 4; i++)
        {
            if (!Quat4f.epsilonRotationEquivalent(configIn, i * 4, configOut, i * 4, epsilonRotation))
            {
                return false;
            }
        }

        float[] avelIn = getAngularVelocity(skiIn, frameIn);
        float[] avelOut = getAngularVelocity(skiOut, frameOut);
        for (int i = 0; i < skiIn.getConfigSize() / 4; i++)
        {
            if (!Vec3f.epsilonEquals(avelIn, i*3, avelOut, i*3, epsilonVelocity))
            {
                return false;
            }
        }
        return true;
    }

}
