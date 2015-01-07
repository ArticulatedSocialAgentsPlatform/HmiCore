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
 * Ensures that the eye stays within its rotation bounds. Theoretical background:<br>
 * Tweed, D. (1997). Three-dimensional model of the human eye-head saccadic system. <br>
 * The Journal of Neurophysiology, 77 (2), pp. 654-666.<br>
 * @author Herwin van Welbergen
 */
public final class EyeSaturation
{
    private EyeSaturation()
    {
    }

    private static final float RADIUS = 0.12f;

    public static boolean isSaturized(float[] q)
    {
        float alpha = q[Quat4f.x] * q[Quat4f.x] + q[Quat4f.y] * q[Quat4f.y];
        float maxTorsion = 0.25f * (float) Math.sqrt(0.15f - alpha);
        return (alpha < RADIUS * RADIUS && Math.abs(q[Quat4f.z]) < maxTorsion);
    }

    /**
     * Saturizes eye position
     * 
     * @param qCurDes current desired eye position
     * @param qDes desired end position of the eye (after the head is moved as well), assumed to be saturized
     * @param result saturized eye pos
     */
    public static void sat(float[] qCurDes, float[] qDes, float[] result)
    {
        float alpha = qCurDes[Quat4f.x] * qCurDes[Quat4f.x] + qCurDes[Quat4f.y] * qCurDes[Quat4f.y];
        Quat4f.set(result, qCurDes);
        if (alpha > RADIUS * RADIUS)
        {
            float beta = qCurDes[Quat4f.x] * qDes[Quat4f.x] + qCurDes[Quat4f.y] * qDes[Quat4f.y];
            float gamma = qDes[Quat4f.x] * qDes[Quat4f.x] + qDes[Quat4f.y] * qDes[Quat4f.y];
            float a = alpha - 2 * beta + gamma;
            float b = 2 * (alpha - beta);
            float c = alpha - (RADIUS * RADIUS);
            float D = b * b - 4 * a * c;
            if (D < 0)
                D = 0;
            float x = (-b + (float) Math.sqrt(D)) / (2 * a);

            result[Quat4f.x] -= qDes[Quat4f.x];
            result[Quat4f.y] -= qDes[Quat4f.y];
            result[Quat4f.z] -= qDes[Quat4f.z];
            Vec4f.scale(x, result);
            Quat4f.add(result, qCurDes);
            alpha = RADIUS * RADIUS;
        }
        float maxTorsion = 0.25f * (float) Math.sqrt(0.15f - alpha);
        if (Math.abs(result[Quat4f.z]) > maxTorsion)
        {
            result[Quat4f.z] = Math.signum(result[Quat4f.z]) * maxTorsion;
        }

        // normalize
        result[Quat4f.s] = (float) Math.sqrt(1 - (result[Quat4f.x] * result[Quat4f.x] + result[Quat4f.y] * result[Quat4f.y] + result[Quat4f.z]
                * result[Quat4f.z]));
    }
}
