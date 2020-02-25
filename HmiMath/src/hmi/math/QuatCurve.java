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
