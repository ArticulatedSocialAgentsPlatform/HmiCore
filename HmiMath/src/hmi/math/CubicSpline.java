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
 * A C2 continuous spline that goes through all control points
 * Assumptions:
 * The spline is not closed 
 * y''(0)=0 
 * y''(n-1)=0
 * The point distribution is uniform 
 * @author welberge
 */
public class CubicSpline
{
   private float[] a;
   private float[] b;
   private float[] c;
   private float[] d;
   private float[] y;
   private float[] D;
   
   private float[] as;
   private float[] bs;
   private float[] cs;
   private float[] ds;
   
   private int n = 0;
   
   /**
    * Constructor 
    * The interpolated 2D point are: (0, points[0]), (1, points[1]), ..., (n-1, points[n-1])
    * @param points interpolation points
    */
   public CubicSpline(float[] points)
   {
      y = points;
      n = points.length;
      a = new float[n-1];
      b = new float[n-1];
      c = new float[n-1];
      d = new float[n-1];
      
      as = new float[n];
      bs = new float[n];
      cs = new float[n];
      ds = new float[n];
      D = new float[n];    
      
      as[0]=0;
      bs[0]=2;
      cs[0]=1;
      ds[0]=3*(y[1]-y[0]);
      as[n-1]=1;
      bs[n-1]=2;
      cs[n-1]=0;
      ds[n-1]=3*(y[n-1]-y[n-2]);
      for(int i = 1;i<n-1;i++)
      {
         as[i]=1;
         bs[i]=4;
         cs[i]=1;
         ds[i]=3*(y[i+1]-y[i-1]);
      }
      Mat.tridiagonalSolve(as, bs, cs, ds, D);
      for(int i=0;i<n-1;i++)
      {
         a[i]=y[i];
         b[i]=D[i];
         c[i]=3*(y[i+1]-y[i])-2*D[i]-D[i+1];
         d[i]=2*(y[i]-y[i+1])+D[i]+D[i+1];
      }
   }
   
    /**
     * Constructor
     * @param points interpolation points
     */
   /*
    public CubicSpline(float[] points, int width)
    {
        y = points;
        n = points.length;
            
        a = new float[n-1];
        b = new float[n-1];
        c = new float[n-1];
        d = new float[n-1];
        
        as = new float[n];
        bs = new float[n];
        cs = new float[n];
        ds = new float[n];
        D = new float[n];       
        
        n = points.length/width;
        for(int i=0;i<width;i++)
        {
            as[i]=0;
            bs[i]=2;
            cs[i]=1;
            ds[i]=3*(y[i+width]-y[i]);
            as[(n-1)*width+i]=1;
            bs[(n-1)*width+i]=2;
            cs[(n-1)*width+i]=0;
            ds[(n-1)*width+i]=3*(y[(n-1)*width+i]-y[(n-2)*width+i]);
        }
        for(int i = 1;i<n-1;i++)
        {
            for(int j=0;j<width;j++)
            {
                as[i*width+j]=1;
                bs[i*width+j]=4;
                cs[i*width+j]=1;
                ds[i*width+j]=3*(y[(i+1)*width+j]-y[(i-1)*width+j]);
            }
        }
        
        //Fix this part for good striped spline construction
        Mat.tridiagonalSolve(as, bs, cs, ds, D,width);
        
        for(int i=0;i<n-1;i++)
        {
            for(int j=0;j<width;j++)
            {
                a[i*width+j]=y[i*width+j];
                b[i*width+j]=D[i*width+j];
                c[i*width+j]=3*(y[(i+1)*width+j]-y[i*width+j])-2*D[i*width+j]-D[(i+1)*width+j];
                d[i*width+j]=2*(y[i*width+j]-y[(i+1)*width+j])+D[i*width+j]+D[(i+1)*width+j];
            }
        }
    }
    */
    
   /**
    * Evaluates the spline 
    * @param time time, scaled for length, 0<=time<=1
    * @return the value at time
    */
   public float eval(float time)
   {
      float t = time*n;
      int i = (int)t;
      t = t-i;
      if(i<0)return y[0];
      if(i>=n-1)return y[n-1];
      
      return a[i] + b[i]*t + c[i]*t*t + d[i]*t*t*t; 
   }
   
   /**
    * Evaluates the first derivative  of the spline
    * @param time time, scaled for length, 0<=time<=1
    * @return the value of the first derivative of the spline at time
    */
   public float evalDiff(float time)
   {
      float t = time*n;
      int i = (int)t;
      t = t-i;
      if(i<0)
      {
         i=0;
         t=0;
      }
      if(i>=n-1)
      {
         i=n-2;
         t=1;
      }
      
      return b[i] + 2*c[i]*t + 3*d[i]*t*t; 
   }
    
    /**
     * Evaluates the first derivative  of the spline
     * @param time time, scaled for length, 0<=time<=1
     * @param h timestep between two points in the spline
     * @return the value of the first derivative of the spline at time
     */
    public float evalDiff(float time, float h)
    {
        float t = time*n;
        int i = (int)t;
        t = t-i;
        if(i<0)
        {
            i=0;
            t=0;
        }
        if(i>=n-1)
        {
            i=n-2;
            t=1;
        }        
        return (b[i] + 2*c[i]*t + 3*d[i]*t*t)/h; 
    }
   
   /**
    * Evaluates the 2nd derivative  of the spline
    * @param time time, scaled for length, 0<=time<=1
    * @return the value of the 2nd derivative of the spline at time
    */
   public float evalDiff2(float time)
   {
      float t = time*n;
      int i = (int)t;
      t = t-i;
      if(i<0)return 0;
      if(i>=n-1)return 0;        
      return 2*c[i] + 6*d[i]*t; 
   }
    
    /**
     * Evaluates the 2nd derivative  of the spline
     * @param time time, scaled for length, 0<=time<=1
     * @return the value of the 2nd derivative of the spline at time
     */
    public float evalDiff2(float time, float h)
    {
        float t = time*n;
        int i = (int)t;
        t = t-i;
        if(i<0)return 0;
        if(i>=n-1)return 0;        
        return (2*c[i] + 6*d[i]*t)/(h*h); 
    }
}
