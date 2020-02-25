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
 * TCB spline
 * A non-uniform spline with tensity, continuity and bias controls
 * C1 continous, interpolates the points
 * {link http://hmi.ewi.utwente.nl/aigaion/?page=publication&kind=single&ID=358} 
 * @author Herwin van Welbergen
 */
public class TCBSpline 
{
  private float[] interpolationPoints;
  private float[] interpolationTimes;
  private float[] tension;
  private float[] continuity;
  private float[] bias;
  
  private float m0;
  private float mn;
  
  /**
   * Constructor, m0, mn and interpolation points have to be set manually when
   * this zero-argument constructor is used
   */
  public TCBSpline()
  {
    
  }
  
  /**
   * Constructor
   * @param val interpolation points
   * @param time interpolation times
   * @param t tension
   * @param c continuity
   * @param b bias
   * @param startSpeed start speed
   * @param endSpeed end speed
   */
  public TCBSpline(float[] val, float[] time, float[] t, float[] c, float[] b, float startSpeed, float endSpeed)
  {
    m0=startSpeed;
    mn=endSpeed;
    interpolationTimes=time;
    interpolationPoints=val;
    tension=t;
    continuity=c;
    bias=b;
  }
  
  private static double h0(double t)
  {
    //2*t^3-3*t^2+1;
    return 2.0*Math.pow(t,3.0)-3.0*Math.pow(t,2.0)+1.0;
  }
  
  private static double h1(double t)
  {
    //t^3-2*t^2+t;
    return Math.pow(t,3.0)-2.0*Math.pow(t,2.0)+t;
  }
  
  private static double h2(double t)
  {
    //-2*t^3+3*t^2
    return -2.0*Math.pow(t,3.0)+3.0*Math.pow(t,2.0);
  }
  
  private static double h3(double t)
  {
    //t^3-t^2;
    return Math.pow(t,3.0)-Math.pow(t,2.0);
  }
  
  /**
   * Get the interpolation value at time t
   * @param t time
   * @return the interpolation value, interpolationPoints[interpolationPoints.length-1] if t >= interpolationPoints.length-1
   */
  public float eval(float t)
  {
    int index = -2;
    for(int i=0;i<interpolationTimes.length;i++)
    {
      if(interpolationTimes[i]>t)
      {
        index = i-1;
        break;
      }
    }   
    float ds1,dd0;
    
    
    if (index>=interpolationPoints.length-1 || index == -2)
    {
      return interpolationPoints[interpolationPoints.length-1];
    }
    
    if (index<0)
    {
      return interpolationPoints[0];
    }
    
    float n0=interpolationTimes[index+1]-interpolationTimes[index];
    
    if(index == 0)
    {
      dd0=m0;
    }   
    else
    {
      dd0=
        (1.0f-tension[index])*(1.0f+continuity[index])*(1.0f+bias[index])*0.5f*
        (interpolationPoints[index]-interpolationPoints[index-1])+
        (1.0f-tension[index])*(1.0f-continuity[index])*(1.0f-bias[index])*0.5f*
        (interpolationPoints[index+1]-interpolationPoints[index]);
      float n1=interpolationTimes[index]-interpolationTimes[index-1];
      dd0=dd0*(2.0f*n1/(n1+n0));
    }
    
    if(index+2 > interpolationPoints.length-1)
    {
      ds1=mn;
    }   
    else
    {
      
      ds1=
        (1.0f-tension[index+1])*(1.0f-continuity[index+1])*(1.0f+bias[index+1])*0.5f*
        (interpolationPoints[index+1]-interpolationPoints[index])+
        (1.0f-tension[index+1])*(1.0f+continuity[index+1])*(1.0f-bias[index+1])*0.5f*
        (interpolationPoints[index+2]-interpolationPoints[index+1]);
      float n1=interpolationTimes[index+2]-interpolationTimes[index+1];
      ds1=ds1*(2.0f*n1/(n0+n1));     
    }
    float tinterp=t-interpolationTimes[index];
    tinterp/=n0;
    return getValueCustomSpeed(tinterp,interpolationPoints[index],interpolationPoints[index+1],dd0,ds1);
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
    return (float)(h0(t)*p0+h1(t)*m0+h2(t)*p1+h3(t)*m1);
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

  /**
   * @param bias the bias to set
   */
  public void setBias(float[] bias)
  {
    this.bias = bias;
  }

  /**
   * @param continuity the continuity to set
   */
  public void setContinuity(float[] continuity)
  {
    this.continuity = continuity;
  }

  /**
   * @param interpolationTimes the interpolationTimes to set
   */
  public void setInterpolationTimes(float[] interpolationTimes)
  {
    this.interpolationTimes = interpolationTimes;
  }

  /**
   * @param tension the tension to set
   */
  public void setTension(float[] tension)
  {
    this.tension = tension;
  }
}
