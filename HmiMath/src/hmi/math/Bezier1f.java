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


//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;



public class Bezier1f { //implements Evaluator1f1f {

    //private Logger logger = LoggerFactory.getLogger(Bezier1f.class.getName());
    /** A Bezier curve is specified by 3n+1 points, alternatiing interpolated points P and (non-interpolated)
    * control points C, like: P C C P C C P C C P
    * Internally we complement this by an extra conrol point at the begin and the end of this seqiuence: C P C C P C C P C C P C
    * in order to facilitate concatenation of Bezier curves.
    * (In that case, we need two control points between the end point from the first, and the start point of the second curve.)
    *    * points (px[i], py[i]), for i=0, ... , n-1
    * control points (cx[j], cy[j]), for j = 0, ..., 2n-1
    */
   private float[] p;
 
   private int np; // number of points
   private int nseg; // number of segments
   private float low = 0.0f;
   private float high = 1.0f; // range for u values.  For u=low we are at the first point, for u=high we are at the last one.
   
   /**
    * Create a new Bezier1f curve, by specyfying interpolated values and control points in a single array.
    * The interpolated points P and control points C alternate like the following pattern:
    * P C C P C C P .... P C C P
    */
   public Bezier1f(float[] points) {
      this(points, 0, 1);
   }
   
   /**
    * Create a new Bezier1f curve, by specyfying interpolated values and control points in a single array,
    * where the first value is found at points[offset], and where values are separated by a distance equal to stride.
    * (When values are tightly packed, i.e. without gaps, set offset == 0 and stride == 1)
    * The interpolated points P and control points C alternate like the following pattern:
    * P C C P C C P .... P C C P
    */
   public Bezier1f(float[] points, int offset, int stride) {
      if (points == null) {
        throw new RuntimeException("Bezier curve with null points array");  
      }
      np = ((int)points.length/stride) - ((int)offset/stride);
      p = new float[np];
      for (int i=0; i<np; i++) {
         p[i] = points[offset + i*stride];  
      }   
      nseg = (np-1)/3;
   }
   
   /**
    * Sets the interpolation range for the eval method: for an u value low we are at the first
    * point, for u= high we are at the last point.
    * The default settings are low=0.0f, high = 1.0f
    */
   public void setRange(float ulow, float uhigh) {
      this.low = ulow;
      this.high = uhigh;
   }
   
   
   public String toString() {
      StringBuilder buf = new StringBuilder();
      buf.append("Bezier[ ");
      for (int i=0; i<np-1; i++) {
         buf.append(p[i]); buf.append(", ");
      }
      buf.append(p[np-1]); 
      buf.append(" ]");
      return buf.toString();  
   }
   
   /**
    * Evaluates the Bezier curve for parameter value u
    */
   public float eval(float u) {
      if ( u < low) u = low;
      if (u > high) u = high;
      float ru = nseg * (u-low)/(high-low); // 0.0 <= ru <= nseg
      
      int index = (int) Math.floor(ru);
      if (index >= nseg) index = nseg-1; // special case: u == high, will use the last segment with t == 1.0
      float t = ru - index;  // 0 <= t < 1.0
      
      float s = (1-t);
      float b0 = s * s * s;
      float b1 = 3 * t * s * s;
      float b2 = 3 * t * t * s;
      float b3 = t * t * t;
      
//      
//      System.out.print("eval u=" + u + " ru=" + ru + "  index=" + index + " t=" + t);
//      System.out.println(" eval = " + eval4(3*index, b0, b1, b2, b3) );
      return eval4(3*index, b0, b1, b2, b3);
      
      
   }
   
   protected float eval4(int i, float b0, float b1, float b2, float b3) {
      return b0 * p[i] + b1 * p[i+1] + b2 * p[i+2] + b3 * p[i+3];
   }
   
   
   private static final float EPS = 0.0000001f;
   
   /**
    * Asssuming that our Bezier curve is an invertible function x = f(u)
    * evalInverse returns  u = f^{-1}(x)
    */
   public float evalInverse(float x) {
      float xlow  = p[0];
      float xhigh = p[np-1];
      float ulow  = low;    // ulow and uhigh are the u values associated with xlow and xhigh.
      float uhigh = high;
      
     if (xlow > xhigh) { // swap low/high pairs
         float tmp = xhigh;
         xhigh = xlow;
         xlow = tmp;
         tmp = uhigh;
         uhigh = ulow;
         ulow = tmp;
      }
   
      if (x <= xlow) return ulow;
      if (x>= xhigh) return uhigh;
      
     // after possible swapping, we now have eval(ulow) == xlow <= x <= xhigh == eval(uhigh)
     // Note that ulow need not be smaller than uhigh. (if f is monotoneous decreasing, we have uhigh <= ulow)
      
      float umid = (ulow+uhigh)/2.0f;
      float udiff = Math.abs(uhigh - ulow);
      float xmid = eval(umid);
      
      while (udiff > EPS) {
         if (xmid < x) {
            ulow = umid;
            xlow = xmid;
         } else {
            uhigh = umid;
            xhigh = xmid;      
         }
         umid = (ulow+uhigh)/2.0f;
         xmid = eval(umid);
         udiff = Math.abs(uhigh - ulow);
      }
      //logger.debug("evalInverse {} = {}",x,umid);
      return umid ;
   }
   
   
}