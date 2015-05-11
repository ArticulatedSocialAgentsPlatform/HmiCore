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
 * \class BSpline2f
 * \brief Two-dimensional bezier curve.
 *
 * The Bspline offers a precalculated bezier curve with a given amount of segments.
 * Providing an offset value between 0.0 ~ 1.0 the 2 dimensional coordinate is provided.
 * @author Niels Nijdam
 */
public class BSpline2f {

    private float[] points = null;
    private int offsetPoints;
    private float[] controlPoints = null; //relative offset from points
    private int offsetControlPoints;
    /*final private float point1[]; //absolute point
    final private float point2[];
    final private float controlPoint1R[]; // relative offset
    final private float controlPoint2R[];
     */
    final private float[] controlPoint1A = new float[2]; // absolute offset
    final private float[] controlPoint2A = new float[2];
    final private float[] a = new float[4];
    final private float[] b = new float[4];
    private int segments = 32; //default value
    private float[] xCoords;
    private float[] yCoords;

    public BSpline2f() {
    }

    public BSpline2f(float[] points, int offsetPoints, float[] controlPoints, int offsetControlPoints, int segments) {
        this.points = points;
        this.offsetPoints = offsetPoints;
        this.controlPoints = controlPoints;
        this.offsetControlPoints = offsetControlPoints;
        this.segments = segments;
        updateAbsolute();
        updateBSpline();
        calculateSpline();
    }

    /**
     * The constructor.
     * \brief creates the bezier curve.
     * \param point1 two dimensinal coordinate for the first point.
     * \param point2 the second 2d coord.
     * \param controlPoint1 first curve control point coordinate 
     * \param controlPoint2 second curve control point coordinate.
     * \param segment provides the amount of line segments to build an internal representation of the bezier curve.
    public BSpline2f(float point1[], float point2[], float controlPoint1[], float controlPoint2[], int segments) {
    this.point1 = point1;
    this.point2 = point2;
    this.segments = segments;
    this.controlPoint1R = controlPoint1;
    this.controlPoint2R = controlPoint2;
    updateAbsolute();
    updateBSpline();
    calculateSpline();
    }*/
    /**
     * The copy constructor.
     * \brief creates a copy.
     * \param bSpline2f the curve to be copied.
     */
    public BSpline2f(BSpline2f bSpline2f) {
        this.points = bSpline2f.getPoints();
        this.offsetPoints = bSpline2f.getOffsetPoints();
        this.controlPoints = bSpline2f.getControlPoints();
        this.offsetControlPoints = bSpline2f.getOffsetControlPoints();

        this.segments = bSpline2f.getSegments();
        updateAbsolute();
        updateBSpline();
        calculateSpline();
    }

    /**
     * \brief precalculate bspline algorithm variables.
     * Recalculate certain algorithm computations which don't need to be recalculated everytime.
     * Only when changing one or more of the four points, this method needs to be invoked.
     * Note that if you are using the precalculated spline, it needs also to be recalculated.
     * Most of the time whenever you change a coordinate perform a updateBSpline(); calculateSpline();
     */
    public void updateBSpline() {
        updateAbsolute();
        a[0] = (-points[offsetPoints] + (3f * controlPoint1A[0]) - (3f * controlPoint2A[0]) + points[offsetPoints + 2]);
        a[1] = ((3f * points[offsetPoints]) - (6f * controlPoint1A[0]) + (3f * controlPoint2A[0]));
        a[2] = ((-3f * points[offsetPoints]) + (3f * controlPoint1A[0]));
        a[3] = (points[offsetPoints]);

        b[0] = (-points[offsetPoints + 1] + (3f * controlPoint1A[1]) - (3f * controlPoint2A[1]) + points[offsetPoints + 3]);
        b[1] = ((3f * points[offsetPoints + 1]) - (6f * controlPoint1A[1]) + (3f * controlPoint2A[1]));
        b[2] = ((-3f * points[offsetPoints + 1]) + (3f * controlPoint1A[1]));
        b[3] = (points[offsetPoints + 1]);
    }

    /**
     * \brief calculates an internal set of coordinates (segments) 
     * An internal representation of the Bezier curve. The detail quality of the curve is given by the amount of line segments.
     */
    public void calculateSpline() {
        this.xCoords = new float[this.segments];
        this.yCoords = new float[this.segments];

        this.xCoords[0] = a[3];
        this.yCoords[0] = b[3];

        for (int i = 1; i < segments - 1; i++) {
            float t;

            t = ((float) i) / ((float) segments);
            // ((At+B)t+C)t+D
            this.xCoords[i] = (a[3] + t * (a[2] + t * (a[1] + t * a[0])));
            this.yCoords[i] = (b[3] + t * (b[2] + t * (b[1] + t * b[0])));
        }
        this.xCoords[segments - 1] = points[offsetPoints + 2];
        this.yCoords[segments - 1] = points[offsetPoints + 3];
    }

    /**
     * \brief calculate a point on the BSpline.
     * \param offset an offset in the range 0.0 ~ 1.0 (0.0 = point1 and 1.0 = point2).
     * \return The coordinates on the Bezier curve at the given offset.
     * Used for dynamic retrival of a coordinate on the curve.
     */
    public float[] getSplineCoord(float offset) {
        return new float[]{
            (a[3] + offset * (a[2] + offset * (a[1] + offset * a[0]))), (b[3] + offset * (b[2] + offset * (b[1] + offset * b[0])))
        };
    }

    /**
     * \brief calculate a point on the BSpline.
     * \param offset an offset in the range 0.0 ~ 1.0 (0.0 = point1 and 1.0 = point2).
     * \param target2f the target array where the new values need to be stored into.
     * Used for dynamic retrival of a coordinate on the curve.
     */
    public void getSplineCoord(float offset, float[] target2f) {
        target2f[0] = (a[3] + offset * (a[2] + offset * (a[1] + offset * a[0])));
        target2f[1] = (b[3] + offset * (b[2] + offset * (b[1] + offset * b[0])));
    }

    /**
     * \brief updates the absolute positions of the control points.
     * \param offset an offset in the range 0.0 ~ 1.0 (0.0 = point1 and 1.0 = point2).
     * \param target2f the target array where the new values need to be stored into.
     * Used for dynamic retrival of a coordinate on the curve. 
     */
    public void updateAbsolute() {
        controlPoint1A[0] = points[offsetPoints] + controlPoints[offsetControlPoints];
        controlPoint1A[1] = points[offsetPoints + 1] + controlPoints[offsetControlPoints + 1];

        controlPoint2A[0] = points[offsetPoints + 2] + controlPoints[offsetControlPoints + 2];
        controlPoint2A[1] = points[offsetPoints + 3] + controlPoints[offsetControlPoints + 3];
    }

    /**
     * \brief returns absolute position of control point 1.
     * \return the absolute coordinate control point 1.
     */
    public float[] getControlPoint1A() {
        return controlPoint1A;
    }

    /**
     * \brief returns absolute position of controlpoint2.
     * \return the absolute coordinate controlpoint2.
     */
    public float[] getControlPoint2A() {
        return controlPoint2A;
    }

    /**
     * \brief returns the amount of line segments.
     * \return the amount of line segments.
     */
    public int getSegments() {
        return segments;
    }

    /**
     * \brief returns the array containing the x values from the precalculated Bezier curve (array size == segments).
     * \return array containing x coordinate values.
     */
    public float[] getXCoords() {
        return xCoords;
    }

    /**
     * \brief returns the array containing the y values from the precalculated Bezier curve (array size == segments).
     * \return array containing y coordinate values.
     */
    public float[] getYCoords() {
        return yCoords;
    }

    public float[] getPoints() {
        return points;
    }

    public void setPoints(float[] points) {
        this.points = points;
    }

    public int getOffsetPoints() {
        return offsetPoints;
    }

    public void setOffsetPoints(int offsetPoints) {
        this.offsetPoints = offsetPoints;
    }

    public float[] getControlPoints() {
        return controlPoints;
    }

    public void setControlPoints(float[] controlPoints) {
        this.controlPoints = controlPoints;
    }

    public int getOffsetControlPoints() {
        return offsetControlPoints;
    }

    public void setOffsetControlPoints(int offsetControlPoints) {
        this.offsetControlPoints = offsetControlPoints;
    }

    public void setSegments(int segments) {
        this.segments = segments;
    }

    public float getPoint1X() {
        return points[offsetPoints];
    }

    public float getPoint1Y() {
        return points[offsetPoints + 1];
    }

    public float getPoint2X() {
        return points[offsetPoints + 2];
    }

    public float getPoint2Y() {
        return points[offsetPoints + 3];
    }

    public float getControlPoint1X() {
        return controlPoints[offsetControlPoints];
    }

    public float getControlPoint1Y() {
        return controlPoints[offsetControlPoints + 1];
    }

    public float getControlPoint2X() {
        return controlPoints[offsetControlPoints + 2];
    }

    public float getControlPoint2Y() {
        return controlPoints[offsetControlPoints + 3];
    }

    public void setPoint1X(float value) {
        points[offsetPoints] = value;
    }

    public void setPoint1Y(float value) {
        points[offsetPoints + 1] = value;
    }

    public void setPoint2X(float value) {
        points[offsetPoints + 2] = value;
    }

    public void setPoint2Y(float value) {
        points[offsetPoints + 3] = value;
    }

    public void setControlPoint1X(float value) {
        controlPoints[offsetControlPoints] = value;
    }

    public void setControlPoint1Y(float value) {
        controlPoints[offsetControlPoints + 1] = value;
    }

    public void setControlPoint2X(float value) {
        controlPoints[offsetControlPoints + 2] = value;
    }

    public void setControlPoint2Y(float value) {
        controlPoints[offsetControlPoints + 3] = value;
    }
}
