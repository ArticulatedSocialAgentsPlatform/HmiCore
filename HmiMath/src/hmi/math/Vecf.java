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

package hmi.math;

import com.google.common.primitives.Floats;

/**
 * A collection of methods for vectors of arbitrary length
 * represented by float arrays.
 * @author Herwin van Welbergen, Job Zwiers
 */
public final class Vecf
{

    /** */
    private Vecf()
    {
    }

    /**
     * Returns a new float[] array with zero components
     */
    public static float[] getVecf(int len)
    {
        return new float[len];
    }

    /**
     * Rturns a new float[3] array with initialized components
     */
    public static float[] getVecf(float[] vecf)
    {
        float[] result = new float[vecf.length];
        System.arraycopy(vecf, 0, result, 0, vecf.length);
        return result;
    }

    /**
     * Tests for equality of two vectors
     */
    public static boolean equals(float[] a, float[] b)
    {
        if (a == null && b == null) return true;
        if (a == null || b == null || a.length != b.length) return false;
        for (int i = 0; i < a.length; i++)
        {
            if (a[i] != b[i]) return false;
        }
        return true;
    }

    /**
     * Tests for equality of vector components within epsilon.
     */
    public static boolean epsilonEquals(float[] a, float[] b, float epsilon)
    {
        if (a == null && b == null) return true;
        if (a == null || b == null || a.length != b.length) return false;
        for (int i = 0; i < a.length; i++)
        {
            float diff = a[i] - b[i];
            if (Float.isNaN(diff)) return false;
            if ((diff < 0 ? -diff : diff) > epsilon) return false;
        }
        return true;
    }

    /**
     * Returns a String of the form (v[0], v[1], ....)
     * */
    public static String toString(float[] config)
    {
        StringBuilder buf = new StringBuilder();
        buf.append('(');
        if (config == null)
        {
            buf.append("null");
        }
        else
        {
            int len = config.length;
            if (len > 0) buf.append(config[0]);
            for (int i = 1; i < len; i++)
            {
                buf.append(", ");
                buf.append(config[i]);
            }

        }
        buf.append(')');
        return buf.toString();
    }

    /**
     * Copies vector src to vector dst.
     */
    public static void set(float[] dst, float[] src)
    {
        for (int i = 0; i < dst.length; i++)
            dst[i] = src[i];
    }

    /**
     * Adds vector a and b, puts result in dest.
     */
    public static void add(float[] dst, float[] a, float[] b)
    {
        for (int i = 0; i < dst.length; i++)
            dst[i] = a[i] + b[i];
    }

    /**
     * First scales vector a, then and a to dst
     * dst = dst + s*a
     */
    public static void scaleAdd(float[] dst, float scale, float[] a)
    {
        for (int i = 0; i < dst.length; i++)
        {
            dst[i] += a[i] * scale;
        }
    }

    /**
     * Adds vector b to vector a.
     */
    public static void add(float[] dst, float[] a)
    {
        for (int i = 0; i < dst.length; i++)
            dst[i] += a[i];
    }

    /**
     * Adds vector a and b, puts result in dest.
     */
    public static void sub(float[] dst, float[] a, float[] b)
    {
        for (int i = 0; i < dst.length; i++)
            dst[i] = a[i] - b[i];
    }

    /**
     * Adds vector b to vector a.
     */
    public static void sub(float[] dst, float[] a)
    {
        for (int i = 0; i < dst.length; i++)
            dst[i] -= a[i];
    }

    /**
     * Scales a vector
     */
    public static void scale(float scale, float[] dst)
    {
        for (int i = 0; i < dst.length; i++)
            dst[i] *= scale;
    }

    /**
     * Scales a vector
     */
    public static void scale(float scale, float[] dst, float[] src)
    {
        for (int i = 0; i < dst.length; i++)
            dst[i] = scale * src[i];
    }

    /**
     * Scales a vector
     */
    public static void pmul(float[] dst, float[] a, float[] b)
    {
        for (int i = 0; i < dst.length; i++)
            dst[i] = a[i] * b[i];
    }

    /**
     * Sets vector dst to the negated version of vector src
     */
    public static void negate(float[] dst, float[] src)
    {
        for (int i = 0; i < dst.length; i++)
            dst[i] = -src[i];
    }

    /**
     * Negates a vector
     */
    public static void negate(float[] dst)
    {
        for (int i = 0; i < dst.length; i++)
            dst[i] = -dst[i];
    }

    /**
     * Calculates the dot product for two vectors of length 3
     */
    public static float dot(float[] a, float[] b)
    {
        float d = 0.0f;
        for (int i = 0; i < a.length; i++)
            d += a[i] * b[i];
        return d;
    }

    /**
     * returns the square of the vector length
     */
    public static float lengthSq(float[] a)
    {
        return dot(a, a);
    }

    /**
     * returns the vector length
     */
    public static float length(float[] a)
    {
        return (float) Math.sqrt(dot(a, a));
    }

    /**
     * Linear interpolates between vector a and b, and puts the result in vector dst:
     * dst = (1-alpha)*a + alpha*b
     */
    public static void interpolate(float[] dst, float[] a, float[] b, float alpha)
    {
        for (int i = 0; i < a.length; i++)
            dst[i] = (1 - alpha) * a[i] + alpha * b[i];
    }

    /**
     * Normalizes a, that is, dst = a/|a|
     * @param a vector to be normalized
     * @param dst vector to receive the result
     */
    public static void normalize(float[] dst, float[] a)
    {
        float linv = 1.0f / length(a);
        scale(linv, dst, a);
    }

    /**
     * Normalizes a, that is, a = a/|a|
     * @param a vector to be normalized
     */
    public static void normalize(float[] a)
    {
        float linv = 1.0f / length(a);
        scale(linv, a);
    }

    public static void setZero(float[] dst)
    {
        for (int i = 0; i < dst.length; i++)
        {
            dst[i] = 0;
        }
    }

    public static double sum(float[] a)
    {
        double sum = 0;
        for (float f : a)
        {
            sum += f;
        }
        return sum;
    }

    public static double average(float[] a)
    {
        return sum(a) / a.length;
    }

    public static double max(float a[])
    {
        return Floats.max(a);
    }

    public static double min(float a[])
    {
        return Floats.min(a);
    }

    /**
     * Normalize the elements of a so that they are all between -1 and 1 and the biggest 'peak' is either -1 or 1<br>
     * a = a.-avg(a)
     * a./= max(abs(max(a)), abs(min(a)))
     */
    public static void normalizeElements(float a[])
    {
        double avg = average(a);
        for (int i = 0; i < a.length; i++)
        {
            a[i] -= avg;
        }
        Vecf.scale(1f / (float) Math.max(Math.abs(max(a)), Math.abs(min(a))), a);
    }
}
