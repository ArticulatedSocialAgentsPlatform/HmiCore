/*******************************************************************************
 * Copyright (C) 2009 Human Media Interaction, University of Twente, the Netherlands
 * 
 * This file is part of the Elckerlyc BML realizer.
 * 
 * Elckerlyc is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Elckerlyc is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Elckerlyc.  If not, see http://www.gnu.org/licenses/.
 ******************************************************************************/
package hmi.math;

import static org.junit.Assert.*;

import org.junit.Test;

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
        //System.out.println(Quat4f.toString(q)+" , "+Quat4f.toString(q1));
        
        
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
