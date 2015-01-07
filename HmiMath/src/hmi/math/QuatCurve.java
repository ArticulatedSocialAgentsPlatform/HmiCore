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
 * Constructs quaternion curves (splines, Bezier curves)
 * 
 * Implements algorithms from:
 * Kim, Myoung-Jun, Kim, Myung-Soo and Shin, Sung Yong, 
 * A general construction scheme for unit quaternion curves with simple high order derivatives, 
 * in: SIGGRAPH '95: Proceedings of the 22nd annual conference on Computer graphics and interactive techniques, 
 * pages 369--376, ACM, 1995
 * @author Herwin van Welbergen
 */
public final class QuatCurve
{
  
    /***/
    private QuatCurve() {}
  
    /**
     * Creates a Hermite spline through 2 quaternions with specified angular start and end velocities
     * 
     * b1(t) = 1-(1-t)^3
     * b2(t) = 3t^2-2t^3
     * b3(t) = t^3
     * w1 = wStart/3
     * w3 = wEnd/3 
     * w2 = log(exp(w1)^-1 qStart^-1 qEnd exp(w3))
     * q = q0 exp(w1 b1(t))exp(w2 b2(t))exp(w3 b3(t))
     * 
     * Kim's exponential map is defined as 
     * e^v = (cos(theta), sin(theta)vNorm), with theta = |v|
     * Grassia's exponential map (as used in Quat4f) is defined as
     * e^v = (cos(theta/2), sin(theta/2)vNorm), with theta = |v|, so
     * e^(v*2) = Kim's e^v  
     * We correct the angular velocities for this by making them twice as big as in Kim's original formulation 
     *  
     * @param qStart    rotation at start point (Quat4f)
     * @param qEnd      rotation at end point (Quat4f)
     * @param wStart    angular velocity at start point (Vec3f)
     * @param wEnd      angular velocity at end point   (Vec3f)
     * @param t         interpolation time, 0 &lt;= t &lt;= 1 
     * @param q         output: interpolated quaternion
     */
    public static void hermite(float[] qStart, float[] qEnd, 
                               float[] wStart, float[] wEnd, 
                               float t, float[] q)
    {
        double b1 = 1.0 - Math.pow((1.0-t),3.0);
        double b2 = 3.0 * t * t - 2.0*t*t*t;
        double b3 = t*t*t;
        
        float[] w1=Vec3f.getZero();
        float[] w2=Vec3f.getZero();
        float[] w3=Vec3f.getZero();
        float[] qTemp1=Quat4f.getQuat4f();
        float[] qTemp2=Quat4f.getQuat4f();
        float[] qTemp3=Quat4f.getQuat4f();
        /*
        Vec3f.scale(1f/1.5f, w1,wStart);        
        Vec3f.scale(1f/1.5f, w3,wEnd);
        */
        Vec3f.scale(1.0f/3.0f, w1, wStart);        
        Vec3f.scale(1.0f/3.0f, w3, wEnd);
        
        //qTemp1 = exp(w1)
        Quat4f.exp(qTemp1, w1);
        //qTemp1 = exp(w1)^-1
        Quat4f.conjugate(qTemp1);
        
        //qTemp1 = exp(w1)^-1 * qa^-1
        Quat4f.mulConjugateRight(qTemp1, qStart);
        
        //qTemp1 = exp(w1)^-1 * qa^-1 * qEnd
        Quat4f.mul(qTemp1, qEnd);
        
        //qTemp1 = exp(w1)^-1 * qa^-1 * qEnd * exp(w3)
        Quat4f.exp(qTemp2, w3);
        Quat4f.conjugate(qTemp2);
        Quat4f.mul(qTemp1, qTemp2);
        
        //w2 = log(exp(w1)^-1 * qa^-1 * qEnd * exp(w3))
        Quat4f.normalize(qTemp1);
        Quat4f.log(w2, qTemp1);
        
        //w1 = w1 b1(t) etc etc
        Vec3f.scale((float)b1, w1);
        Vec3f.scale((float)b2, w2);
        Vec3f.scale((float)b3, w3);
        
        //q = q0 exp(w1 b1(t))exp(w2 b2(t))exp(w3 b3(t))
        Quat4f.exp(qTemp1, w1);
        Quat4f.exp(qTemp2, w2);
        Quat4f.exp(qTemp3, w3);
        Quat4f.set(q, qStart);
        Quat4f.mul(q, qTemp1);
        Quat4f.mul(q, qTemp2);
        Quat4f.mul(q, qTemp3);
    }
    
    
    /**
     * Creates a Hermite spline through 2 quaternions with specified angular start and end velocities
     * 
     * b1(t) = 1-(1-t)^3
     * b2(t) = 3t^2-2t^3
     * b3(t) = t^3
     * w1 = wStart/3
     * w3 = wEnd/3 
     * w2 = log(exp(w1)^-1 qStart^-1 qEnd exp(w3))
     * q = q0 exp(w1 b1(t))exp(w2 b2(t))exp(w3 b3(t))
     * 
     * Kim's exponential map is defined as 
     * e^v = (cos(theta), sin(theta)vNorm), with theta = |v|
     * Grassia's exponential map (as used in Quat4f) is defined as
     * e^v = (cos(theta/2), sin(theta/2)vNorm), with theta = |v|, so
     * e^(v*2) = Kim's e^v  
     * We correct the angular velocities for this by making them twice as big as in Kim's original formulation 
     *  
     * @param qStart    rotation at start point (Quat4f)
     * @param qEnd      rotation at end point (Quat4f)
     * @param wStart    angular velocity at start point (Vec3f)
     * @param wEnd      angular velocity at end point   (Vec3f)
     * @param t         interpolation time, 0 &lt;= t &lt;= 1 
     * @param q         output: interpolated quaternion
     */
    public static void hermite(float[] qStart, int qStartIndex, 
                               float[] qEnd, int qEndIndex, 
                               float[] wStart, int wStartIndex, 
                               float[] wEnd, int wEndIndex, 
                               float t, float[] q, int qIndex)
    {
        double b1 = 1.0 - Math.pow((1.0-t),3.0);
        double b2 = 3.0 * t * t - 2.0*t*t*t;
        double b3 = t*t*t;
        
        float[] w1=Vec3f.getZero();
        float[] w2=Vec3f.getZero();
        float[] w3=Vec3f.getZero();
        float[] qTemp1=Quat4f.getQuat4f();
        float[] qTemp2=Quat4f.getQuat4f();
        float[] qTemp3=Quat4f.getQuat4f();
        /*
        Vec3f.scale(1f/1.5f, w1,wStart);        
        Vec3f.scale(1f/1.5f, w3,wEnd);
        */
        Vec3f.scale(1.0f/3.0f, w1, 0, wStart, wStartIndex);        
        Vec3f.scale(1.0f/3.0f, w3, 0, wEnd, wEndIndex);
        
        //qTemp1 = exp(w1)
        Quat4f.exp(qTemp1, w1);
        //qTemp1 = exp(w1)^-1
        Quat4f.conjugate(qTemp1);
        
        //qTemp1 = exp(w1)^-1 * qa^-1
        Quat4f.mulConjugateRight(qTemp1, 0, qStart, qStartIndex);
        
        //qTemp1 = exp(w1)^-1 * qa^-1 * qEnd
        Quat4f.mul(qTemp1, 0, qEnd, qEndIndex);
        
        //qTemp1 = exp(w1)^-1 * qa^-1 * qEnd * exp(w3)
        Quat4f.exp(qTemp2, w3);
        Quat4f.conjugate(qTemp2);
        Quat4f.mul(qTemp1, qTemp2);
        
        //w2 = log(exp(w1)^-1 * qa^-1 * qEnd * exp(w3))
        Quat4f.normalize(qTemp1);
        Quat4f.log(w2, qTemp1);
        
        //w1 = w1 b1(t) etc etc
        Vec3f.scale((float)b1, w1);
        Vec3f.scale((float)b2, w2);
        Vec3f.scale((float)b3, w3);
        
        //q = q0 exp(w1 b1(t))exp(w2 b2(t))exp(w3 b3(t))
        Quat4f.exp(qTemp1, w1);
        Quat4f.exp(qTemp2, w2);
        Quat4f.exp(qTemp3, w3);
        Quat4f.set(q, qIndex, qStart, qStartIndex);
        Quat4f.mul(q, qIndex, qTemp1,0);
        Quat4f.mul(q, qIndex, qTemp2,0);
        Quat4f.mul(q, qIndex, qTemp3,0);
    }
}
