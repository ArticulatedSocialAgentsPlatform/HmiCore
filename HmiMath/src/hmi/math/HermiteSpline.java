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
 * Hermite spline
 * A cubic, acceleration-continue spline going fluently through equidistant (with distant 1) interpolation points.
 * The acceleration in these points is calculated using Catmull-Rom:
 * Px'=0.5*(P(x-1)-P(x+1)) 
 * @author Herwin van Welbergen
 */
public final class HermiteSpline 
{
   private float[] interpolationPoints;
   private float m0;
   private float mn;
   
   /**
    * Constructor, m0, mn and interpolation points have to be set manually when
    * this zero-argument constructor is used
    */
   public HermiteSpline()
   {
      
   }
   /**
    * Constructor, speed=0 at start and end points 
    * @param val points to interpolate through
    */
   public HermiteSpline(float[] val)
   {
      m0=0;
      mn=0;
      interpolationPoints=val;
   }
   
   /**
    * Constructor
    * @param val points to interpolate through
    * @param startSpeed speed at p0
    * @param endSpeed speed at pn
    */
   public HermiteSpline(float[] val,float startSpeed, float endSpeed)
   {
      m0=startSpeed;
      mn=endSpeed;
      interpolationPoints=val;
   }
   
   private static double H0(double t)
   {
      //2*t^3-3*t^2+1;
      return 2*Math.pow(t,3)-3*Math.pow(t,2)+1;
   }
   
   private static double H1(double t)
   {
      //t^3-2*t^2+t;
      return Math.pow(t,3)-2*Math.pow(t,2)+t;
   }
   
   private static double H2(double t)
   {
      //-2*t^3+3*t^2
      return -2*Math.pow(t,3)+3*Math.pow(t,2);
   }
   
   private static double H3(double t)
   {
      //t^3-t^2;
      return Math.pow(t,3)-Math.pow(t,2);
   }
   
   /**
    * Get the interpolation value at time t
    * @param t time
    * @return the interpolation value, interpolationPoints[interpolationPoints.length-1] if t >= interpolationPoints.length-1
    */
   public float eval(float t)
   {
      int tInt = (int)t;
      int t_1 = tInt-1;
      int t_2 = tInt+2;
      float m_0,m_1;
      
      
      if (tInt>=interpolationPoints.length-1)
      {
         return interpolationPoints[interpolationPoints.length-1];
      }
      
      
      if(t_1==-1)
      {
         m_0=m0;
      }
      else
      {
         m_0=0.5f*(interpolationPoints[tInt+1]-interpolationPoints[t_1]);
      }
      
      
      if(t_2>=interpolationPoints.length)
      {
         m_1=mn;
      }
      else
      {
         m_1=0.5f*(interpolationPoints[t_2]-interpolationPoints[tInt]);
      }     
      return getValueCustomSpeed(t-tInt,interpolationPoints[tInt],interpolationPoints[tInt+1],m_0,m_1);
   }
   
   /**
    * Get the interpolation value for custom speed at interpolation points p0 and p1
    * @param t   time 0<=t<=1
    * @param p0 1st interpolation point 
    * @param p1 2nd interpolation point
    * @param m0 speed at p0
    * @param m1 speed at p1
    * @return the interpolation value
    */
   public static float getValueCustomSpeed(float t, float p0, float p1, float m0, float m1)
   {
      return (float)(H0(t)*p0+H1(t)*m0+H2(t)*p1+H3(t)*m1);
   }
   /**
    * Gets the value of the hermite spline at point p0 < t < p1
    * @param t    time
    * @param p_1  point p-1
    * @param p0   point p0
    * @param p1   point p1
    * @param p2   point p2
    * @return the interpolation value
    */
   public static float getValue(float t,float p_1, float p0, float p1, float p2)
   {
      //H0(t)*p0+H1(t)*0.5*(p1-p_1)+H2(t)*p1+H3(t)*0.5*(p2-p0);
      return (float)(H0(t)*p0+H1(t)*0.5*(p1-p_1)+H2(t)*p1+H3(t)*0.5*(p2-p0));
   }
   /**
    * @param interpolationPoints The interpolationPoints to set.
    */
   public void setInterpolationPoints(float[] interpolationPoints) {
      this.interpolationPoints = interpolationPoints;
   }
   /**
    * @param m0 The m0 to set.
    */
   public void setM0(float m0) {
      this.m0 = m0;
   }
   /**
    * @param mn The mn to set.
    */
   public void setMn(float mn) {
      this.mn = mn;
   }
}
