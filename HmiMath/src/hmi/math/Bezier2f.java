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

public class Bezier2f
{

    private Bezier1f xspline;
    private Bezier1f yspline;
    private float low = 0.0f;
    private float high = 1.0f;
    private int np, nseg;

    /**
     * Create a new Bezier curve with interpolated points "points" which is an array of Vec2f elements, that is, every points[i] must be a float[2]
     * array. and a similar array of "controlPoints", also Vec2f elements. The coordinates can be copied, when copy == true, or else they are
     * referenced
     */
    public Bezier2f(float[][] points, float[][] controlPoints)
    {

    }

    /**
     * Creates a new Bezier2f object, where the x and y coordinates of the bezier points and control points are specified in a single float array,
     * alternating between x and y coordinates.
     */
    public Bezier2f(float[] points)
    {
        xspline = new Bezier1f(points, 0, 2);
        yspline = new Bezier1f(points, 1, 2);
        np = points.length / 2;
        nseg = (np - 1) / 3;
    }

    /**
     * Creates a new Bezier2f object, where the x and y coordinates of the bezier points and control points are specified in two separate float
     * arrays, of equal length.
     */
    public Bezier2f(float[] xcoords, float[] ycoords)
    {
        xspline = new Bezier1f(xcoords);
        yspline = new Bezier1f(ycoords);
        np = xcoords.length;
        nseg = (np - 1) / 3;
    }

    /**
     * Creates a ne Bezier2f object from an array of Vec2f points that are interpolated, an array of Vec2f tangent vectors, and a float array
     * containing weights. The points and vectors array should have equal length, and each individual array element should be an hmi.math.Vec2f
     * element, that is, a float[2] array. The weights array should have length twice the length of the points or vectors array -2. A Vec2f point Pi,
     * Vec2f vector Vi and two consecutive weights w0, w1 determine two control points Ci0 = Pi - w0*Vi, Ci1 = Pi + w1*Vi, wehere we imagine that
     * these ly around point Pi, except for the first point (where C00 is omitted) and the last point (where Cn1 is omitted). This then defines an
     * alternating sequence of points and control points, as usual: P0 C01 C10 P1 C11 C20 P2 C21 .... Cn0 Pn
     */
    public static Bezier2f bezier2fFromPointsVectorsWeights(float[][] points2f, float[][] vectors2f, float[] weights)
    {
        int nip = points2f.length; // number of interpolated points
        int np = 3 * nip - 2; // two extra control points per interpolated point, minus 2 for the start and end points
        if (vectors2f.length != nip)
        {
            throw new RuntimeException("Bezier2f.bezier2fFromPointsVectorsWeights: number of vectors (" + vectors2f.length
                    + ") should be equal to number of interpolated points (" + nip + ")");
        }
        int ncp = 2 * nip - 2; // number of control points
        if (weights.length != ncp)
        {
            throw new RuntimeException("Bezier2f.bezier2fFromPointsVectorsWeights: number of weights (" + weights.length + ")  for " + nip
                    + " interpolated points should be equal to : (" + ncp + ")");
        }
        float[] points = new float[2 * np]; // two coordinates per point
        /*
         * float[] control_Before = Vec2f.getVec2f(); float[] control_After = Vec2f.getVec2f();
         */

        // copy interpolated point data, with offset 0, stride 6
        for (int i = 0; i < nip; i++)
        {
            points[6 * i] = points2f[i][Vec2f.X];
            points[6 * i + 1] = points2f[i][Vec2f.Y];
        }

        // calculate control points "after" each interpolated point, except for the last one
        for (int i = 0; i < nip - 1; i++)
        {
            float w = weights[2 * i];
            points[6 * i + 2] = points2f[i][Vec2f.X] + w * vectors2f[i][Vec2f.X];
            points[6 * i + 3] = points2f[i][Vec2f.Y] + w * vectors2f[i][Vec2f.Y];
        }

        // calculate control points "before" each interpolated point, except for the first one
        for (int i = 1; i < nip; i++)
        {
            float w = weights[2 * i - 1];
            points[6 * i - 2] = points2f[i][Vec2f.X] - w * vectors2f[i][Vec2f.X];
            points[6 * i - 1] = points2f[i][Vec2f.Y] - w * vectors2f[i][Vec2f.Y];
        }

        Bezier2f result = new Bezier2f(points);
        return result;
    }

    /**
     * Like bezier2fFromPointsVectorsWeights, except that the number of weight must be equal to the number of interpolated points. Each of these
     * weight is used both for the control point before and the control point after each interpolated point. (So the situation is "symmetric" around
     * each interpolated point).
     */
    public static Bezier2f bezier2fFromPointsVectorsSingleWeights(float[][] points2f, float[][] vectors2f, float[] weights)
    {
        int nip = points2f.length;
        if (weights.length != nip)
        {
            throw new RuntimeException("Bezier2f.bezier2fFromPointsVectorsSingleWeights: number of weights (" + weights.length
                    + ") should be equal to number of interpolated points (" + nip + ")");
        }
        float[] w = new float[2 * nip - 2];
        for (int i = 1; i < nip - 1; i++)
        {
            w[2 * i - 1] = weights[i];
            w[2 * i] = weights[i];
        }
        w[0] = weights[0];
        w[2 * nip - 3] = weights[nip - 1];

        return bezier2fFromPointsVectorsWeights(points2f, vectors2f, w);
    }

    /**
     * Sets the interpolation range for the eval method: for an u value low we are at the first point, for u= high we are at the last point. The
     * default settings are low=0.0f, high = 1.0f
     */
    public void setRange(float low, float high)
    {
        this.low = low;
        this.high = high;
        xspline.setRange(low, high);
        yspline.setRange(low, high);
    }

    public String toString()
    {
        StringBuilder buf = new StringBuilder();
        buf.append("Bezier2f[ ");
        // for (int i=0; i<nseg; i++) {
        // int pi = i;
        // int ci = 2*i;
        // buf.append('('); buf.append(p[i][0]); buf.append(","); buf.append(p[i][1]); buf.append("),  ");
        // buf.append('('); buf.append(c[2*i][0]); buf.append(","); buf.append(c[2*i][1]); buf.append("),  ");
        // buf.append('('); buf.append(c[2*i+1][0]); buf.append(","); buf.append(c[2*i+1][1]); buf.append("),  ");
        // }
        // buf.append('('); buf.append(p[np-1][0]); buf.append(","); buf.append(p[np-1][1]); buf.append(")");
        buf.append(" ]");

        return buf.toString();
    }

    /**
     * Evaluates the Bezier curve and puts the result in a float[2] array result, which is also returned as result value.
     */
    public float[] eval(float[] result, float u)
    {
        if (u < low)
            u = low;
        if (u > high)
            u = high;
        float ru = nseg * (u - low) / (high - low); // 0.0 <= ru <= nseg
        int index = (int) Math.floor(ru);
        if (index >= nseg)
            index = nseg - 1; // special case: u == high, will use the last segment with t == 1.0
        float t = ru - index; // 0 <= t < 1.0

        float s = (1 - t);
        float b0 = s * s * s;
        float b1 = 3 * t * s * s;
        float b2 = 3 * t * t * s;
        float b3 = t * t * t;

        result[0] = xspline.eval4(3 * index, b0, b1, b2, b3);
        result[1] = yspline.eval4(3 * index, b0, b1, b2, b3);

        // System.out.print("eval u=" + u + " ru=" + ru + "  index=" + index + " t=" + t);
        // System.out.println(" eval = (" + result[0] + ", " + result[1] + ")");
        return result;
    }

    private static final float EPS = 0.0000001f;

    /**
     * Asssuming that our Bezier curve is a functional relation of the form y = f(x) finds the function value f(x) for the specified x, provided x is
     * within the range [ p[0][0] ; p[n-1].[0] ], where n denotes the number of points, and p[0][0] = x value of point 0, etc.
     */
    public float evalFX(float x)
    {
        return evalFX(x, low, high);
    }

    /**
     * Asssuming that our Bezier curve is a functional relation of the form y = f(x) for parameter values u within the interval [low, high], evalFX(x)
     * finds the function value y = f(x) The x coordinates are allowed to be either monotoneously increasing or decreasing over the specified
     * interval.
     */
    public float evalFX(float x, float low, float high)
    {
        float ulow, uhigh;
        if (xspline.eval(low) <= xspline.eval(high))
        {
            ulow = low;
            uhigh = high;
        }
        else
        {
            ulow = high;
            uhigh = low;
        }
        if (x <= xspline.eval(ulow))
            return yspline.eval(ulow);
        if (x >= xspline.eval(uhigh))
            return yspline.eval(uhigh);
        // invariant: xlow <= x <= xhigh,
        // invariant: xlow = xspline.eval(ulow) xhigh = xspline.eval(uhigh)
        float umid = (ulow + uhigh) / 2.0f;
        while (Math.abs(uhigh - ulow) > EPS)
        {
            float xmid = xspline.eval(umid);
            if (xmid < x)
            {
                ulow = umid;
            }
            else
            {
                uhigh = umid;
            }
            umid = (ulow + uhigh) / 2.0f;
        }
        return yspline.eval(umid);
    }

}