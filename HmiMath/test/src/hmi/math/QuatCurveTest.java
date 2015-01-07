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

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Unit tests for the QuatCurve
 * @author hvanwelbergen
 *
 */
public class QuatCurveTest
{
    @Test
    public void hermite()
    {
        float q0[] = new float[4];
        float q1[] = new float[4];
        float w[] = new float[3];
        float w0[] = new float[3];
        float w1[] = new float[3];
        float q[] = new float[4];
        Vec3f.set(w0,0,0,0);
        Vec3f.set(w1,0,0,0);        
        Quat4f.setFromAxisAngle4f(q0, 0.2f,0.4f,0.8f,0.78f);
        Quat4f.setFromAxisAngle4f(q1, 0.34f,0.1f,0.2f,0.3f);        
        
        QuatCurve.hermite(q0, q1, w0, w1, 0, q);        
        assertTrue(Quat4f.epsilonEquals(q, q0, 0.00001f));
        
        QuatCurve.hermite(q0, q1, w0, w1, 1, q);
        assertTrue(Quat4f.epsilonEquals(q, q1, 0.00001f));
        
        
        Vec3f.set(w0,1,2,3);
        Vec3f.set(w1,3,1,1);
        QuatCurve.hermite(q0, q1, w0, w1, 1, q);
        assertTrue(Quat4f.epsilonEquals(q, q1, 0.00001f));
        
        
        Quat4f.setFromAxisAngle4f(q0, 1f,0f,0f,0.01f);
        Quat4f.setFromAxisAngle4f(q1, 1f,0f,0f,0.78f);  
        Vec3f.set(w0,0.5f,0f,0f);
        Vec3f.set(w1,0.8f,0,0);
        float qt0[] = new float[4];
        float qt1[] = new float[4];
        float qt2[] = new float[4];
        float qrate[] = new float[4];
        QuatCurve.hermite(q0, q1, w0, w1, 0.0f, qt0);
        QuatCurve.hermite(q0, q1, w0, w1, 0.00001f, qt1);
        QuatCurve.hermite(q0, q1, w0, w1, 0.00002f, qt2);
        for(int i=0;i<4;i++)
        {
            qrate[i] = NumMath.diff(qt0[i], qt2[i], 0.00001f);
        }
        Quat4f.setAngularVelocityFromQuat4f(w, qt1, qrate);
        //System.out.println(Vec3f.toString(w) +" "+Vec3f.toString(w0));
        assertTrue(Vec3f.epsilonEquals(w, w0, 0.01f));
        
        QuatCurve.hermite(q0, q1, w0, w1, 0.998f, qt0);
        QuatCurve.hermite(q0, q1, w0, w1, 0.999f, qt1);
        QuatCurve.hermite(q0, q1, w0, w1, 1f, qt2);
        for(int i=0;i<4;i++)
        {
            qrate[i] = NumMath.diff(qt0[i], qt2[i], 0.001f);
        }
        Quat4f.setAngularVelocityFromQuat4f(w, qt1, qrate);
        //System.out.println(Vec3f.toString(w) +" "+Vec3f.toString(w1));
        assertTrue(Vec3f.epsilonEquals(w, w1, 0.01f));
        
        float qStart[] = new float[4];
        float qEnd[] = new float[4];
        Quat4f.setFromAxisAngle4f(qStart, 1,0,0,(float)(Math.PI*0.75));
        Quat4f.setFromAxisAngle4f(qEnd, 1,0,0,(float)(Math.PI*0.5));
        float wStart[] = new float[3];
        float wEnd[] = new float[3];
        Vec3f.set(wStart,(float)Math.PI*0.75f,0,0);
        Vec3f.set(wEnd,-(float)Math.PI*0.125f*0.5f,0,0);
        QuatCurve.hermite(qStart, qEnd, wStart, wEnd, 0.998f, qt0);
        QuatCurve.hermite(qStart, qEnd, wStart, wEnd, 0.999f, qt1);
        QuatCurve.hermite(qStart, qEnd, wStart, wEnd, 1f, qt2);
        for(int i=0;i<4;i++)
        {
            qrate[i] = NumMath.diff(qt0[i], qt2[i], 0.001f);
        }
        Quat4f.setAngularVelocityFromQuat4f(w, qt1, qrate);
        assertTrue(Vec3f.epsilonEquals(w, wEnd, 0.01f));
        
        
        Quat4f.set(qStart, 0.8036457f, -0.5951081f,5.4295924E-8f,-8.3679566E-7f);
        Quat4f.setIdentity(qEnd);
        Vec3f.setZero(wStart);
        Vec3f.setZero(wEnd);
        QuatCurve.hermite(qStart, 0, qEnd, 0, wStart, 0, wEnd, 0, 1, q,0);
        assertTrue(Quat4f.epsilonEquals(q, qEnd, 0.01f));
        
        //interpolation between equal quaternions        
        /* breaks in swingdemo:
        t:0.0
        qStart: (0.9553366, 0.0, 0.0, 0.29552022)
        qEnd:   (0.9553365, 0.0, 0.0, 0.29552022)
        q:      (NaN, NaN, NaN, NaN)
        */
        
        
        Quat4f.set(qStart, 0.9553366f, 0.0f, 0.0f, 0.29552022f);
        Quat4f.set(qEnd,   0.9553365f, 0.0f, 0.0f, 0.29552022f);
        Vec3f.setZero(wStart);
        Vec3f.setZero(wEnd);
        QuatCurve.hermite(qStart, 0, qEnd, 0, wStart, 0, wEnd, 0, 1, q,0);
        System.out.println(Quat4f.toString(q));
        assertTrue(Quat4f.epsilonEquals(q, qEnd, 0.01f));        
        
        QuatCurve.hermite(qStart, qEnd, wStart, wEnd, 0, q);
        System.out.println(Quat4f.toString(q));
        assertTrue(Quat4f.epsilonEquals(q, qEnd, 0.01f));
    }
}
