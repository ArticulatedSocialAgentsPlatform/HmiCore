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

/**
 * A collection of methods for vectors of length 3,
 * represented by float arrays. Vectors can be stored within arrays of length three,
 * or they can be stored inside a larger float array a, together with an
 * integer offset &quot;index&quot; into that array. This represents a vector
 * with components (a[index], a[index+1], a[index+2]).
 * The methods from this class never allocate new arrays; rather, they assume that
 * results are to be stored into some existing &quot;destination&quot; array.
 * @author Job Zwiers, Herwin van Welbergen
 */
public final class Vec3f
{

    /**
     * Length of Vec3f arrays is 3
     */
    public static final int VEC3F_SIZE = 3;

    /**
     * Offset values for Vec4 vectors.
     */
    public static final int X = 0;
    public static final int Y = 1;
    public static final int Z = 2;

    /** */
    private Vec3f()
    {
    }

    /**
     * Returns a new float[3] array with zero components
     */
    public static float[] getVec3f()
    {
        return new float[VEC3F_SIZE];
    }

    /**
     * Returns a new float[3] array with initialized components
     */
    public static float[] getVec3f(float x, float y, float z)
    {
        return new float[] { x, y, z };
    }

    /**
     * Rturns a new float[3] array with initialized components
     */
    public static float[] getVec3f(float[] vec3f)
    {
        return new float[] { vec3f[0], vec3f[1], vec3f[2] };
    }

    /**
     * Rturns a new float[3] array with initialized components, taken at some offset within an existing float array.
     */
    public static float[] getVec3f(float[] vec3f, int offset)
    {
        return new float[] { vec3f[offset], vec3f[offset + 1], vec3f[offset + 2] };
    }

    /**
     * Returns a new float[3] array with zero components
     */
    public static float[] getZero()
    {
        return new float[VEC3F_SIZE];
    }

    /**
     * Returns a new unit vector in the x-direction
     */
    public static float[] getUnitX()
    {
        return new float[] { 1.0f, 0.0f, 0.0f };
    }

    /**
     * Returns a new unit vector in the y-direction
     */
    public static float[] getUnitY()
    {
        return new float[] { 0.0f, 1.0f, 0.0f };
    }

    /**
     * Returns a new unit vector in the x-direction
     */
    public static float[] getUnitZ()
    {
        return new float[] { 0.0f, 0.0f, 1.0f };
    }

    /**
     * Tests for equality of two vectors
     */
    public static boolean equals(float[] a, int aIndex, float[] b, int bIndex)
    {
        return a[aIndex] == b[bIndex] && a[aIndex + 1] == b[bIndex + 1] && a[aIndex + 2] == b[bIndex + 2];
    }

    /**
     * Tests for equality of one vectors and 3 floats
     */
    public static boolean equals(float[] a, float bx, float by, float bz)
    {
        return a[0] == bx && a[1] == by && a[2] == bz;
    }

    /**
     * Tests for equality of two vectors
     */
    public static boolean equals(float[] a, float[] b)
    {
        return a[0] == b[0] && a[1] == b[1] && a[2] == b[2];
    }

    /**
     * Tests for equality of vector components within epsilon.
     */
    public static boolean epsilonEquals(float[] a, int aIndex, float[] b, int bIndex, float epsilon)
    {
        float diff = a[aIndex] - b[bIndex];
        if (Float.isNaN(diff)) return false;
        if ((diff < 0 ? -diff : diff) > epsilon) return false;

        diff = a[aIndex + 1] - b[bIndex + 1];
        if (Float.isNaN(diff)) return false;
        if ((diff < 0 ? -diff : diff) > epsilon) return false;

        diff = a[aIndex + 2] - b[bIndex + 2];
        if (Float.isNaN(diff)) return false;
        if ((diff < 0 ? -diff : diff) > epsilon) return false;
        return true;
    }

    /**
     * Tests for equality of vector components within epsilon.
     */
    public static boolean epsilonEquals(float[] a, float[] b, float epsilon)
    {
        float diff = a[0] - b[0];
        if (Float.isNaN(diff)) return false;
        if ((diff < 0 ? -diff : diff) > epsilon) return false;

        diff = a[1] - b[1];
        if (Float.isNaN(diff)) return false;
        if ((diff < 0 ? -diff : diff) > epsilon) return false;

        diff = a[2] - b[2];
        if (Float.isNaN(diff)) return false;
        if ((diff < 0 ? -diff : diff) > epsilon) return false;

        return true;
    }

    /**
     * Tests for equality of vector components within epsilon.
     */
    public static boolean epsilonEquals(float[] b, float ax, float ay, float az, float epsilon)
    {
        float diff = ax - b[0];
        if (Float.isNaN(diff)) return false;
        if ((diff < 0 ? -diff : diff) > epsilon) return false;

        diff = ay - b[1];
        if (Float.isNaN(diff)) return false;
        if ((diff < 0 ? -diff : diff) > epsilon) return false;

        diff = az - b[2];
        if (Float.isNaN(diff)) return false;
        if ((diff < 0 ? -diff : diff) > epsilon) return false;

        return true;
    }

    /**
     * Returns a String of the form (x, y, z), representing the Vec3f value.
     * */
    public static String toString(float[] a, int index)
    {
        return "(" + a[index] + ", " + a[index + 1] + ", " + a[index + 2] + ")";
    }

    /**
     * Returns a String of the form (x, y, z), representing the Vec3f value.
     * */
    public static String toString(float[] a)
    {
        return "(" + a[0] + ", " + a[1] + ", " + a[2] + ")";
    }

    /**
     * Returns a String of the form (x, y, z), representing the Vec3f value.
     * */
    public static String toString(float[] a, int fieldwidth, int precision)
    {
        return toString(a, "%" + fieldwidth + "." + precision + "f");
    }

    private static final int BUFSIZE = 30;

    /**
     * Returns a String of the form (x, y, z), representing the Vec3f value.
     * */
    public static String toString(float[] a, String fmt)
    {
        StringBuffer buf = new StringBuffer(BUFSIZE);
        buf.append('(');
        buf.append(String.format(fmt, a[0]));
        buf.append(',');
        buf.append(String.format(fmt, a[1]));
        buf.append(',');
        buf.append(String.format(fmt, a[2]));
        buf.append(')');
        return buf.toString();
    }

    /**
     * Resets the Vec3f to (0, 0, 0)
     */
    public static void setZero(float[] dst)
    {
        dst[0] = 0.0f;
        dst[1] = 0.0f;
        dst[2] = 0.0f;
    }

    /**
     * Resets the Vec3f to (0, 0, 0)
     */
    public static void setZero(float[] dst, int dstIndex)
    {
        dst[dstIndex] = 0.0f;
        dst[dstIndex + 1] = 0.0f;
        dst[dstIndex + 2] = 0.0f;
    }

    /**
     * Copies vector src to vector dst. The src and dst array elements
     * should not be aliased.
     */
    public static void set(float[] dst, int dstIndex, float[] src, int srcIndex)
    {
        dst[dstIndex] = src[srcIndex];
        dst[dstIndex + 1] = src[srcIndex + 1];
        dst[dstIndex + 2] = src[srcIndex + 2];
    }

    /**
     * Copies vector src to vector dst.
     */
    public static void set(float[] dst, float[] src)
    {
        dst[0] = src[0];
        dst[1] = src[1];
        dst[2] = src[2];
    }

    /**
     * Sets vector components to specified float values.
     */
    public static void set(float[] dst, int dstIndex, float x, float y, float z)
    {
        dst[dstIndex] = x;
        dst[dstIndex + 1] = y;
        dst[dstIndex + 2] = z;
    }

    /**
     * Sets vector components tp specified float values.
     */
    public static void set(float[] dst, float x, float y, float z)
    {
        dst[0] = x;
        dst[1] = y;
        dst[2] = z;
    }

    /**
     * Sets vector dst to sum of a and b.
     */
    public static void add(float[] dst, int dstIndex, float[] a, int aIndex, float[] b, int bIndex)
    {
        dst[dstIndex] = a[aIndex] + b[bIndex];
        dst[dstIndex + 1] = a[aIndex + 1] + b[bIndex + 1];
        dst[dstIndex + 2] = a[aIndex + 2] + b[bIndex + 2];
    }

    /**
     * Adds vector b to vector a.
     */
    public static void add(float[] dst, int dstIndex, float[] a, int aIndex)
    {
        dst[dstIndex] += a[aIndex];
        dst[dstIndex + 1] += a[aIndex + 1];
        dst[dstIndex + 2] += a[aIndex + 2];
    }

    /**
     * Adds vector a and b, puts result in dest.
     */
    public static void add(float[] dst, float[] a, float[] b)
    {
        dst[0] = a[0] + b[0];
        dst[1] = a[1] + b[1];
        dst[2] = a[2] + b[2];
    }

    /**
     * Adds vector b to vector a.
     */
    public static void add(float[] dst, float[] a)
    {
        dst[0] += a[0];
        dst[1] += a[1];
        dst[2] += a[2];
    }

    /**
     * Adds 3 floats to dst
     */
    public static void add(float[] dst, float ax, float ay, float az)
    {
        dst[0] += ax;
        dst[1] += ay;
        dst[2] += az;
    }

    /**
     * Sets vector dst to a minus b
     */
    public static void sub(float[] dst, int dstIndex, float[] a, int aIndex, float[] b, int bIndex)
    {
        dst[dstIndex] = a[aIndex] - b[bIndex];
        dst[dstIndex + 1] = a[aIndex + 1] - b[bIndex + 1];
        dst[dstIndex + 2] = a[aIndex + 2] - b[bIndex + 2];
    }

    /**
     * Subtracts vector a from vector dst.
     */
    public static void sub(float[] dst, int dstIndex, float[] a, int aIndex)
    {
        dst[dstIndex] -= a[aIndex];
        dst[dstIndex + 1] -= a[aIndex + 1];
        dst[dstIndex + 2] -= a[aIndex + 2];
    }

    /**
     * Subtracts vector b from vector a.
     */
    public static void sub(float[] dst, float[] a, float[] b)
    {
        dst[0] = a[0] - b[0];
        dst[1] = a[1] - b[1];
        dst[2] = a[2] - b[2];
    }

    /**
     * Subtracts vector a from vector dst.
     */
    public static void sub(float[] dst, float[] a)
    {
        dst[0] -= a[0];
        dst[1] -= a[1];
        dst[2] -= a[2];
    }

    /**
     * Substracts 3 floats from dst
     */
    public static void sub(float[] dst, float ax, float ay, float az)
    {
        dst[0] -= ax;
        dst[1] -= ay;
        dst[2] -= az;
    }

    /**
     * Scales a vector dst = scale * src
     */
    public static void scale(float scale, float[] dst, float[] src)
    {
        dst[0] = scale * src[0];
        dst[1] = scale * src[1];
        dst[2] = scale * src[2];
    }

    /**
     * Scales a vector dst = scale * src
     */
    public static void scale(float scale, float[] dst, int dstIndex, float[] src, int srcIndex)
    {
        dst[dstIndex] = scale * src[srcIndex];
        dst[dstIndex + 1] = scale * src[srcIndex + 1];
        dst[dstIndex + 2] = scale * src[srcIndex + 2];
    }

    /**
     * Scales a vector
     */
    public static void scale(float scale, float[] dst, int dstIndex)
    {
        dst[dstIndex] *= scale;
        dst[dstIndex + 1] *= scale;
        dst[dstIndex + 2] *= scale;
    }

    /**
     * Scales a vector
     */
    public static void scale(float scale, float[] dst)
    {
        dst[0] *= scale;
        dst[1] *= scale;
        dst[2] *= scale;
    }

    /**
     * Replaces dst[i] by 1.0f/dst[i]
     */
    public static void invert(float[] dst)
    {
        dst[0] = 1.0f / dst[0];
        dst[1] = 1.0f / dst[1];
        dst[2] = 1.0f / dst[2];
    }

    /**
     * Sets vector a to scaled version of vector b, then adds vector c
     * dst = s*a + b
     */
    public static void scaleAdd(float[] dst, int dstIndex, float scale, float[] a, int aIndex, float[] b, int bIndex)
    {
        dst[dstIndex] = a[aIndex] * scale + b[bIndex];
        dst[dstIndex + 1] = a[aIndex + 1] * scale + b[bIndex + 1];
        dst[dstIndex + 2] = a[aIndex + 2] * scale + b[bIndex + 2];
    }

    /**
     * Sets vector dst to scaled version of vector a, then adds vector b
     * dst = s*a + b
     */
    public static void scaleAdd(float[] dst, float scale, float[] a, float[] b)
    {
        dst[0] = a[0] * scale + b[0];
        dst[1] = a[1] * scale + b[1];
        dst[2] = a[2] * scale + b[2];
    }

    /**
     * First scales vector dst, then and adds vector a.
     * dst = s*dst + a
     */
    public static void scaleAdd(float scale, float[] dst, int dstIndex, float[] a, int aIndex)
    {
        dst[dstIndex] = dst[dstIndex] * scale + a[aIndex];
        dst[dstIndex + 1] = dst[dstIndex + 1] * scale + a[aIndex + 1];
        dst[dstIndex + 2] = dst[dstIndex + 2] * scale + a[aIndex + 2];
    }

    /**
     * First scales vector a, then and a to dst
     * dst = dst + s*a
     */
    public static void scaleAdd(float[] dst, int dstIndex, float scale, float[] a, int aIndex)
    {
        dst[dstIndex] += a[aIndex] * scale;
        dst[dstIndex + 1] += a[aIndex + 1] * scale;
        dst[dstIndex + 2] += a[aIndex + 2] * scale;
    }

    /**
     * First scales vector a, then and a to dst
     * dst = dst + s*a
     */
    public static void scaleAdd(float[] dst, float scale, float[] a)
    {
        dst[0] += a[0] * scale;
        dst[1] += a[1] * scale;
        dst[2] += a[2] * scale;
    }

    /**
     * First scales vector dst, then and adds vector a.
     * dst = s*dst + a
     */
    public static void scaleAdd(float scale, float[] dst, float[] a)
    {
        dst[0] = dst[0] * scale + a[0];
        dst[1] = dst[1] * scale + a[1];
        dst[2] = dst[2] * scale + a[2];
    }

    /**
     * Sets vector dst to the negated version of vector src
     */
    public static void negate(float[] dst, int dstIndex, float[] src, int srcIndex)
    {
        dst[dstIndex] = -src[srcIndex];
        dst[dstIndex + 1] = -src[srcIndex + 1];
        dst[dstIndex + 2] = -src[srcIndex + 2];
    }

    /**
     * Sets vector dst to the negated version of vector src
     */
    public static void negate(float[] dst, float[] src)
    {
        dst[0] = -src[0];
        dst[1] = -src[1];
        dst[2] = -src[2];
    }

    /**
     * Negates a vector
     */
    public static void negate(float[] dst, int dstIndex)
    {
        dst[dstIndex] = -dst[dstIndex];
        dst[dstIndex + 1] = -dst[dstIndex + 1];
        dst[dstIndex + 2] = -dst[dstIndex + 2];
    }

    /**
     * Negates a vector
     */
    public static void negate(float[] dst)
    {
        dst[0] = -dst[0];
        dst[1] = -dst[1];
        dst[2] = -dst[2];
    }

    /**
     * Calculates the cross product of two (3D) vectors defined by array &quot;a&quot;,
     * with index &quot;aIndex&quot;, and array &quot;b&quot;, with offset &quot;bIndex&quot;,
     * and stores the reult vector in array &quot;c&quot;, with offset &quot;cIndex&quot;.
     * Indices are zero-based.
     * The arrays arrays a, b, and dst can be the same; however, the offsets should ensure
     * that the dst elements are not aliased with the a or b elements.
     * dst-vector = a-vector X b-vector
     */
    public static void cross(float[] dst, float[] a, float[] b)
    {
        dst[0] = a[1] * b[2] - a[2] * b[1];
        dst[1] = a[2] * b[0] - a[0] * b[2];
        dst[2] = a[0] * b[1] - a[1] * b[0];
    }

    /**
     * Calculates the cross product of two (3D) vectors defined by array &quot;a&quot;,
     * with index &quot;aIndex&quot;, and array &quot;b&quot;, with offset &quot;bIndex&quot;,
     * and stores the reult vector in array &quot;c&quot;, with offset &quot;cIndex&quot;.
     * Indices are zero-based.
     * The arrays arrays a, b, and dst can be the same; however, the offsets should ensure
     * that the dst elements are not aliased with the a or b elements.
     * dst-vector = a-vector X b-vector
     */
    public static void cross(float[] dst, int dstIndex, float[] a, int aIndex, float[] b, int bIndex)
    {
        dst[dstIndex] = a[aIndex + 1] * b[bIndex + 2] - a[aIndex + 2] * b[bIndex + 1];
        dst[dstIndex + 1] = a[aIndex + 2] * b[bIndex] - a[aIndex] * b[bIndex + 2];
        dst[dstIndex + 2] = a[aIndex] * b[bIndex + 1] - a[aIndex + 1] * b[bIndex];
    }

    /**
     * Calculates the dot product for two vectors of length 3
     */
    public static float dot(float[] a, float[] b)
    {
        return a[0] * b[0] + a[1] * b[1] + a[2] * b[2];
    }

    /**
     * Calculates the dot product for two vectors of length 3
     */
    public static float dot(float[] a, int aIndex, float[] b, int bIndex)
    {
        return a[aIndex] * b[bIndex] + a[aIndex + 1] * b[bIndex + 1] + a[aIndex + 2] * b[bIndex + 2];
    }

    /**
     * returns the square of the vector length
     */
    public static float lengthSq(float[] a, int aIndex)
    {
        return a[aIndex] * a[aIndex] + a[aIndex + 1] * a[aIndex + 1] + a[aIndex + 2] * a[aIndex + 2];
    }

    /**
     * returns the square of the vector length
     */
    public static float lengthSq(float[] a)
    {
        return a[0] * a[0] + a[1] * a[1] + a[2] * a[2];
    }

    /**
     * returns the vector length
     */
    public static float length(float[] a, int aIndex)
    {
        return (float) Math.sqrt(a[aIndex] * a[aIndex] + a[aIndex + 1] * a[aIndex + 1] + a[aIndex + 2] * a[aIndex + 2]);
    }

    /**
     * returns the vector length
     */
    public static float length(float[] a)
    {
        return (float) Math.sqrt(a[0] * a[0] + a[1] * a[1] + a[2] * a[2]);
    }

    public static float angleBetweenVectors(float a[], float b[])
    {
        // cos t = a.b/(||a|||b||)
        return (float) Math.acos(dot(a, b) / (length(a) * length(b)));
    }

    /**
     * Project a on b
     */
    public static void project(float res[], float a[], float b[])
    {
        Vec3f.set(res, b);
        Vec3f.scale(Vec3f.dot(a, b) / Vec3f.dot(b, b), res);
    }

    //get b so that a = b*x
    private static float getScale(float a[], float x[])
    {
        int maxIndex = 0;
        for(int i=1;i<3;i++)
        {
            if(a[i]*a[i]>a[maxIndex]*a[maxIndex])
            {
                maxIndex = i;
            }
        }
        return a[maxIndex]/x[maxIndex];
    }
    /**
     * Decompose a, so that a=res[0]i+res[1]j+res[2]k
     * Assumes that i,j,k are orthogonal and have unit-length
     */
    public static void decompose(float res[], float a[], float i[], float j[], float k[])
    {
        project(res, a, i);        
        float resx = getScale(res, i);
        project(res, a, j);
        float resy = getScale(res, j);
        project(res, a, k);
        float resz = getScale(res, k);
        Vec3f.set(res, resx, resy, resz);
    }

    public static float distanceBetweenPoints(float a[], float b[])
    {
        float xdist = a[0] - b[0];
        float ydist = a[1] - b[1];
        float zdist = a[2] - b[2];
        return (float) Math.sqrt(xdist * xdist + ydist * ydist + zdist * zdist);
    }

    /**
     * Linear interpolates between a sequence of vectors a and b, and puts the result in vector dst:
     * dst = (1-alpha)*a + alpha*b. The arrays a, b, and dst are assumed to have equals length.
     */
    public static void interpolateArrays(float[] dst, float[] a, float[] b, float alpha)
    {
        for (int i = 0; i < dst.length; i++)
        {
            dst[i] = (1 - alpha) * a[i] + alpha * b[i];
        }
    }

    /**
     * Linear interpolates between vector a and b, and puts the result in vector dst:
     * dst = (1-alpha)*a + alpha*b
     */
    public static void interpolate(float[] dst, int dstIndex, float[] a, int aIndex, float[] b, int bIndex, float alpha)
    {
        dst[dstIndex] = (1 - alpha) * a[aIndex] + alpha * b[bIndex];
        dst[dstIndex + 1] = (1 - alpha) * a[aIndex + 1] + alpha * b[bIndex + 1];
        dst[dstIndex + 2] = (1 - alpha) * a[aIndex + 2] + alpha * b[bIndex + 2];
    }

    /**
     * Linear interpolates between vector a and b, and puts the result in vector dst:
     * dst = (1-alpha)*a + alpha*b
     */
    public static void interpolate(float[] dst, float[] a, float[] b, float alpha)
    {
        dst[0] = (1 - alpha) * a[0] + alpha * b[0];
        dst[1] = (1 - alpha) * a[1] + alpha * b[1];
        dst[2] = (1 - alpha) * a[2] + alpha * b[2];
    }

    /**
     * Normalizes a, that is, a = a/|a|
     * @param a vector to be normalized
     * @param aIndex index in a array
     * @param dst vector to receive the result
     * @param dstIndex index in dst array
     */
    public static void normalize(float[] dst, int dstIndex, float[] a, int aIndex)
    {
        float linv = 1.0f / length(a, aIndex);
        dst[dstIndex] = a[aIndex] * linv;
        dst[dstIndex + 1] = a[aIndex + 1] * linv;
        dst[dstIndex + 2] = a[aIndex + 2] * linv;
    }

    /**
     * Normalizes a, that is, a = a/|a|
     * @param a vector to be normalized
     * @param aIndex index in a array
     */
    public static void normalize(float[] a, int aIndex)
    {
        float linv = 1.0f / length(a, aIndex);
        a[aIndex] = a[aIndex] * linv;
        a[aIndex + 1] = a[aIndex + 1] * linv;
        a[aIndex + 2] = a[aIndex + 2] * linv;
    }

    /**
     * Normalizes a, that is, dst = a/|a|
     * @param a vector to be normalized
     * @param dst vector to receive the result
     */
    public static void normalize(float[] dst, float[] a)
    {
        float linv = 1.0f / length(a);
        dst[0] = a[0] * linv;
        dst[1] = a[1] * linv;
        dst[2] = a[2] * linv;
    }

    /**
     * Normalizes a, that is, a = a/|a|
     * @param a vector to be normalized
     */
    public static void normalize(float[] a)
    {
        float linv = 1.0f / length(a);
        a[0] = a[0] * linv;
        a[1] = a[1] * linv;
        a[2] = a[2] * linv;
    }

    /**
     * Performs pointwise multiplication of two vectors:
     * u[i] = u[i]*v[i];
     */
    public static void pmul(float[] u, float[] v)
    {
        u[0] = u[0] * v[0];
        u[1] = u[1] * v[1];
        u[2] = u[2] * v[2];
    }

    /**
     * Find an orthogonal vector to normalized vector v
     */
    public static void findOrthogonal(float ortho[], float v[])
    {
        int minIndex = 0;
        if (v[1] < v[minIndex]) minIndex = 1;
        if (v[2] < v[minIndex]) minIndex = 2;
        float tmp[] = Vec3f.getVec3f();
        switch (minIndex)
        {
        case 0:
            Vec3f.set(tmp, 1, 0, 0);
        case 1:
            Vec3f.set(tmp, 0, 1, 0);
        case 2:
            Vec3f.set(tmp, 0, 0, 1);
        }
        Vec3f.cross(ortho, tmp, v);
    }

    // /**
    // * Determines the Mat3f scaling type of a scaling vector
    // * This can be IDENTITY, UNIFORM, ALIGNED, REFLECTION, SCALED_REFLECTION
    // */
    // public static Mat3f.ScalingType getScalingType(float[] s) {
    // float det = s[0] * s[1] * s[2];
    // if ( det < 0) {
    // if (det == -1.0f) {
    // return Mat3f.ScalingType.REFLECTION;
    // } else {
    // return Mat3f.ScalingType.SCALED_REFLECTION;
    // }
    // }
    // // no reflection, check for uniformity:
    // if ( (Math.abs(s[0] - s[1]) > 0.0f) || (Math.abs(s[1] - s[2]) > 0.0f) ) {
    // return Mat3f.ScalingType.ALIGNED; // non-uniform scaling, but aligned with the axes.
    // }
    // if (Math.abs(s[0] - 1.0f) > 0.0f) {
    // return Mat3f.ScalingType.UNIFORM; // uniform scaling, with some non-unit scaling factor
    // }
    // return Mat3f.ScalingType.IDENTITY; // No scaling at all
    // }

}
