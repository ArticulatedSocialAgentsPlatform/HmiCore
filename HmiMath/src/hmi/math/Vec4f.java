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
 * A collection of static methods for vectors of length 4, represented by
 * float arrays.
 * @author Job Zwiers, Herwin van Welbergen
 */
public final class Vec4f {

   /** */
   private Vec4f() {}

   /**
    * Length of Vec4f arrays is 4
    */
   public static final int VEC4F_SIZE = 4;

   /**
    * Offset values for Vec4 vectors. Names are appropriate for homogeneous coordinates and colors
    */   
   public static final int X = 0;
   public static final int Y = 1;
   public static final int Z = 2;
   public static final int W = 3;
   
   public static final int R = 0;
   public static final int G = 1;
   public static final int B = 2;
   public static final int A = 3;

   /**
     * Returns a new float[4] array with zero components
     */
    public static float[] getVec4f() {
       return new float[VEC4F_SIZE];
    }
   
    /**
     * Returns a new float[4] array with initialized components
     */
    public static float[] getVec4f(float x, float y, float z, float a) {
       return new float[] {x, y, z, a};
    }
   
    /**
     * Rturns a new float[4] array with initialized components
     */
    public static float[] getVec4f(float[] vec4f) {
       return new float[] {vec4f[0], vec4f[1], vec4f[2], vec4f[3]};
    }
   

    /**
     * Tests for equality of two vectors
     */
    public static boolean equals(float[] a, int aIndex, float[] b, int bIndex){
       return a[aIndex] == b[bIndex] && a[aIndex+1] == b[bIndex+1] && a[aIndex+2] == b[bIndex+2] && a[aIndex+3] == b[bIndex+3];  
    }
    
    /**
     * Tests for equality of two vectors
     */
    public static boolean equals(float[] a, float[] b){
       return a[0] == b[0] && a[1] == b[1] && a[2] == b[2] && a[3] == b[3];  
    }
    
    /**
     * Tests for equality of vector components within epsilon.
     */
    public static boolean epsilonEquals(float[] a, int aIndex, float[] b, int bIndex, float epsilon){
       float diff = a[aIndex] - b[bIndex];
       if(Float.isNaN(diff)) return false;
       if((diff<0 ? -diff : diff) > epsilon) return false;

       diff = a[aIndex+1] - b[bIndex+1];
       if(Float.isNaN(diff)) return false;
       if((diff<0 ? -diff : diff) > epsilon) return false;

       diff = a[aIndex+2] - b[bIndex+2];
       if(Float.isNaN(diff)) return false;
       if((diff<0 ? -diff : diff) > epsilon) return false;

       diff = a[aIndex+3] - b[bIndex+3];
       if(Float.isNaN(diff)) return false;
       if((diff<0 ? -diff : diff) > epsilon) return false;
       
       return true;  
    }
    
    /**
     * Tests for equality of vector components within epsilon.
     */
    public static boolean epsilonEquals(float[] a, float[] b, float epsilon){
       float diff = a[0] - b[0];
       if(Float.isNaN(diff)) return false;
       if((diff<0 ? -diff : diff) > epsilon) return false;

       diff = a[1] - b[1];
       if(Float.isNaN(diff)) return false;
       if((diff<0 ? -diff : diff) > epsilon) return false;

       diff = a[2] - b[2];
       if(Float.isNaN(diff)) return false;
       if((diff<0 ? -diff : diff) > epsilon) return false;

       diff = a[3] - b[3];
       if(Float.isNaN(diff)) return false;
       if((diff<0 ? -diff : diff) > epsilon) return false;
       
       return true;  
    }
    
    /**
     * Tests for equality of vector components within epsilon.
     */
    public static boolean epsilonEquals(float[] a, float b0, float b1, float b2, float b3, float epsilon){
       float diff = a[0] - b0;
       if(Float.isNaN(diff)) return false;
       if((diff<0 ? -diff : diff) > epsilon) return false;

       diff = a[1] - b1;
       if(Float.isNaN(diff)) return false;
       if((diff<0 ? -diff : diff) > epsilon) return false;

       diff = a[2] - b2;
       if(Float.isNaN(diff)) return false;
       if((diff<0 ? -diff : diff) > epsilon) return false;

       diff = a[3] - b3;
       if(Float.isNaN(diff)) return false;
       if((diff<0 ? -diff : diff) > epsilon) return false;
       
       return true;  
    }
    
    /**
     * Resets the Vec4f to (0, 0, 0, 0)
     */
    public static void setZero(float[] dst) {
      dst[0] = 0.0f;  dst[1] = 0.0f;  dst[2] = 0.0f;  dst[3] = 0.0f;
    }
    
    
    /**
     * Copies vector src to vector dst
     */
    public static void set(float[] dst, int dstIndex, float[] src, int srcIndex) {
       dst[dstIndex]   = src[srcIndex];
       dst[dstIndex+1] = src[srcIndex+1];
       dst[dstIndex+2] = src[srcIndex+2];
       dst[dstIndex+3] = src[srcIndex+3];
    }
    
    /**
     * Copies vector src to vector dst
     */
    public static void set(float[] dst, float[] src) {
       dst[0] = src[0];
       dst[1] = src[1];
       dst[2] = src[2];
       dst[3] = src[3];
    }
    
    /**
     * Sets vector components  to specified float values.
     */
    public static void set(float[] dst, int dstIndex, float x, float y, float z, float w){
       dst[dstIndex]   = x;
       dst[dstIndex+1] = y;
       dst[dstIndex+2] = z;
       dst[dstIndex+3] = w;
    }
    
    /**
     * Sets vector components tp specified float values.
     */
    public static void set(float[] dst, float x, float y, float z, float w){
       dst[0] = x;
       dst[1] = y;
       dst[2] = z;
       dst[3] = w;
    }
    
    /**
     * Sets vector dst to sum of a and b. 
     */
    public static void add(float[] dst, int dstIndex, float[] a, int aIndex, float[] b, int bIndex) {
       dst[dstIndex]   = a[aIndex]   + b[bIndex]; 
       dst[dstIndex+1] = a[aIndex+1] + b[bIndex+1];
       dst[dstIndex+2] = a[aIndex+2] + b[bIndex+2];
       dst[dstIndex+3] = a[aIndex+3] + b[bIndex+3];
    } 
    
    /**
     * Adds vector b to vector a. 
     */
    public static void add(float[] dst, int dstIndex, float[] a, int aIndex) {
       dst[dstIndex]   += a[aIndex]; 
       dst[dstIndex+1] += a[aIndex+1];
       dst[dstIndex+2] += a[aIndex+2];
       dst[dstIndex+3] += a[aIndex+3];
    }
      
    /**
     * Adds vector a and b, puts result in dest.
     */
    public static void add(float[] dst, float[] a, float[] b) {
       dst[0] = a[0] + b[0]; 
       dst[1] = a[1] + b[1];
       dst[2] = a[2] + b[2];
       dst[3] = a[3] + b[3];
    }
    
    /**
     * Adds vector a to vector dst
     */
    public static void add(float[] dst, float[] a) {
       dst[0] += a[0]; 
       dst[1] += a[1];
       dst[2] += a[2];
       dst[3] += a[3];
    }
    
    /**
     * Sets vector dst to a minus b 
     */
    public static void sub(float[] dst, int dstIndex, float[] a, int aIndex, float[] b, int bIndex) {
       dst[dstIndex]   = a[aIndex]   - b[bIndex]; 
       dst[dstIndex+1] = a[aIndex+1] - b[bIndex+1];
       dst[dstIndex+2] = a[aIndex+2] - b[bIndex+2];
       dst[dstIndex+3] = a[aIndex+3] - b[bIndex+3];
    }
    
    /**
     * Subtracts vector a from vector dst. 
     */
    public static void sub(float[] dst, int dstIndex, float[] a, int aIndex) {
       dst[dstIndex]   -= a[aIndex]; 
       dst[dstIndex+1] -= a[aIndex+1];
       dst[dstIndex+2] -= a[aIndex+2];
       dst[dstIndex+3] -= a[aIndex+3];
    }
    
     /**
     * Adds vector a and b, puts result in dest.
     */
    public static void sub(float[] dst, float[] a, float[] b) {
       dst[0] = a[0] - b[0]; 
       dst[1] = a[1] - b[1];
       dst[2] = a[2] - b[2];
       dst[3] = a[3] - b[3];
    }
    
    /**
     * Subtracts vector a from vector dst. 
     */
    public static void sub(float[] dst, float[] a) {
       dst[0] -= a[0]; 
       dst[1] -= a[1];
       dst[2] -= a[2];
       dst[3] -= a[3];
    }
    
    /**
     * Scales a vector 
     */
    public static void scale(float scale, float[] dst, int dstIndex) {
       dst[dstIndex]   *= scale;
       dst[dstIndex+1] *= scale;
       dst[dstIndex+2] *= scale;
       dst[dstIndex+3] *= scale;
    }
    
     /**
     * Scales a vector 
     */
    public static void scale(float scale, float[] dst) {
       dst[0] *= scale;
       dst[1] *= scale;
       dst[2] *= scale;
       dst[3] *= scale;
    }
    
    /**
     * Sets vector a to scaled version of vector b, then adds vector c
     * a = s*b + c
     */
    public static void scaleAdd(float[] dst, int dstIndex, float scale, float[] a, int aIndex, float[] b, int bIndex) {
       dst[dstIndex]   = a[aIndex]*scale   + b[bIndex]; 
       dst[dstIndex+1] = a[aIndex+1]*scale + b[bIndex+1];
       dst[dstIndex+2] = a[aIndex+2]*scale + b[bIndex+2];
       dst[dstIndex+3] = a[aIndex+3]*scale + b[bIndex+3];
    }
    
    /**
     * Sets vector dst to scaled version of vector a, then adds vector b
     * dst = s*a + b
     */
    public static void scaleAdd(float[] dst, float scale, float[] a, float[] b) {
       dst[0] = a[0]*scale + b[0]; 
       dst[1] = a[1]*scale + b[1];
       dst[2] = a[2]*scale + b[2];
       dst[3] = a[3]*scale + b[3];
    }
    
    /**
     * First scales vector dst, then and adds vector a.
     * dst = s*dst + a
     */
    public static void scaleAdd(float scale, float[] dst, int dstIndex, float[] a, int aIndex) {
       dst[dstIndex]   = dst[dstIndex]*scale   + a[aIndex]; 
       dst[dstIndex+1] = dst[dstIndex+1]*scale + a[aIndex+1];
       dst[dstIndex+2] = dst[dstIndex+2]*scale + a[aIndex+2];
       dst[dstIndex+3] = dst[dstIndex+3]*scale + a[aIndex+3];
    }
    
    /**
     * First scales vector dst, then and adds vector a.
     * dst = s*dst + a
     */
    public static void scaleAdd(float scale, float[] dst, float[] a) {
       dst[0] = dst[0]*scale + a[0]; 
       dst[1] = dst[1]*scale + a[1];
       dst[2] = dst[2]*scale + a[2];
       dst[3] = dst[3]*scale + a[3];
    }
    
    /**
     * Sets vector dst to the negated version of vector src
     */
    public static void negate(float[] dst, int dstIndex, float[] src, int srcIndex) {
        dst[dstIndex]   = -src[srcIndex];
        dst[dstIndex+1] = -src[srcIndex+1];
        dst[dstIndex+2] = -src[srcIndex+2];
        dst[dstIndex+3] = -src[srcIndex+3];
    }
    
    /**
     * Sets vector dst to the negated version of vector src
     */
    public static void negate(float[] dst, float[]src) {
        dst[0] = -src[0];
        dst[1] = -src[1];
        dst[2] = -src[2];
        dst[3] = -src[3];
    }
    
    /**
     * Negates a vector
     */
    public static void negate(float[] dst, int dstIndex) {
        dst[dstIndex]   = -dst[dstIndex];
        dst[dstIndex+1] = -dst[dstIndex+1];
        dst[dstIndex+2] = -dst[dstIndex+2];
        dst[dstIndex+3] = -dst[dstIndex+3];
    }
    
    /**
     * Negates a vector
     */
    public static void negate(float[] dst) {
        dst[0] = -dst[0];
        dst[1] = -dst[1];
        dst[2] = -dst[2];
        dst[3] = -dst[3];
    }
    
//    /**
//     * Calculates the cross product of two (3D) vectors defined by array &quot;a&quot;, 
//     * with index &quot;aIndex&quot;, and array &quot;b&quot;, with offset &quot;bIndex&quot;, 
//     * and stores the reult vector in array &quot;c&quot;, with offset &quot;cIndex&quot;.
//     * Indices are zero-based. 
//     * The arrays arrays a, b, and dst can be the same; however, the offsets should ensure
//     * that the dst elements are not aliased with the a or b elements.
//     * dst-vector  = a-vector X b-vector
//     */
//    public static void cross(float[] dst, float[] a, float[] b) {
//        dst[0] = a[1] * b[2] - a[2] * b[1];  
//        dst[1] = a[2] * b[0] - a[0] * b[2];  
//        dst[2] = a[0] * b[1] - a[1] * b[0];  
//    } 
//    
//    
//    /**
//     * Calculates the cross product of two (3D) vectors defined by array &quot;a&quot;, 
//     * with index &quot;aIndex&quot;, and array &quot;b&quot;, with offset &quot;bIndex&quot;, 
//     * and stores the reult vector in array &quot;c&quot;, with offset &quot;cIndex&quot;.
//     * Indices are zero-based.
//     * The arrays arrays a, b, and dst can be the same; however, the offsets should ensure
//     * that the dst elements are not aliased with the a or b elements.
//     * dst-vector  = a-vector X b-vector
//     */
//    public static void cross(float[] dst, int dstIndex, float[] a, int aIndex, float[] b, int bIndex) {
//        dst[dstIndex]   = a[aIndex+1] * b[bIndex+2] - a[aIndex+2] * b[bIndex+1];  
//        dst[dstIndex+1] = a[aIndex+2] * b[bIndex  ] - a[aIndex]   * b[bIndex+2];  
//        dst[dstIndex+2] = a[aIndex]   * b[bIndex+1] - a[aIndex+1] * b[bIndex];  
//    }
    
   
    /**
     * Calculates the dot product for two vectors of length 4
     */
    public static float dot(float[] a, float[] b) {
        return a[0]*b[0] + a[1]*b[1] + a[2]*b[2] + a[3]*b[3];
    }
   
    /**
     * Calculates the dot product for two vectors of length 4
     */
    public static float dot(float[] a, int aIndex, float[] b, int bIndex) {
        return a[aIndex]*b[bIndex] + a[aIndex+1]*b[bIndex+1] + a[aIndex+2]*b[bIndex+2] + a[aIndex+3]*b[bIndex+3];
    }
   
  
   /**
    * returns the square of the vector length
    */
   public static float lengthSq(float[] a, int aIndex) {
       return a[aIndex]*a[aIndex] + a[aIndex+1]*a[aIndex+1] + a[aIndex+2]*a[aIndex+2] + a[aIndex+3]*a[aIndex+3];  
   }
    
   /**
    * returns the square of the vector length
    */
   public static float lengthSq(float[] a) {
       return a[0]*a[0] + a[1]*a[1] + a[2]*a[2] + a[3]*a[3];  
   }

   /**
    * returns the vector length
    */
   public static float length(float[] a, int aIndex) {
       return (float) Math.sqrt(a[aIndex]*a[aIndex] + a[aIndex+1]*a[aIndex+1] + a[aIndex+2]*a[aIndex+2] + a[aIndex+3]*a[aIndex+3]);  
   }
   
   /**
    * returns the vector length
    */
   public static float length(float[] a) {
       return (float) Math.sqrt(a[0]*a[0] + a[1]*a[1] + a[2]*a[2] + a[3]*a[3]);  
   }
    
   
   /**
    * Linear interpolates between vector a and b, and puts the result in vector dst:
    * dst = (1-alpha)*a + alpha*b
    */
   public static void interpolate(float[] dst, int dstIndex, float[] a, int aIndex, float[] b, int bIndex, float alpha ){
       dst[dstIndex]   = (1-alpha)*a[aIndex]   + alpha*b[bIndex];
       dst[dstIndex+1] = (1-alpha)*a[aIndex+1] + alpha*b[bIndex+1];
       dst[dstIndex+2] = (1-alpha)*a[aIndex+2] + alpha*b[bIndex+2];
       dst[dstIndex+3] = (1-alpha)*a[aIndex+3] + alpha*b[bIndex+3];
   }
   
   /**
    * Linear interpolates between vector a and b, and puts the result in vector dst:
    * dst = (1-alpha)*a + alpha*b
    */
   public static void interpolate(float[] dst, float[] a, float[] b, float alpha ){
       dst[0] = (1-alpha)*a[0] + alpha*b[0];
       dst[1] = (1-alpha)*a[1] + alpha*b[1];
       dst[2] = (1-alpha)*a[2] + alpha*b[2];
       dst[3] = (1-alpha)*a[3] + alpha*b[3];
   }
   
   /**
    * Normalizes a, that is, a = a/|a|
    * @param a vector to be normalized
    * @param aIndex index in a array
    * @param dst vector to receive the result
    * @param dstIndex index in dst array
    */
   public static void normalize(float[] dst, int dstIndex, float[] a, int aIndex){
     float linv = 1.0f/length(a, aIndex);
     dst[dstIndex]   = a[aIndex] * linv;
     dst[dstIndex+1] = a[aIndex+1] * linv;
     dst[dstIndex+2] = a[aIndex+2] * linv;
      dst[dstIndex+3] = a[aIndex+3] * linv;
   } 
   
   
   /**
    * Normalizes a, that is, a = a/|a|
    * @param a vector to be normalized
    * @param aIndex index in a array
    */
   public static void normalize(float[] a, int aIndex){
     float linv = 1.0f/length(a, aIndex);
     a[aIndex] = a[aIndex] * linv;
     a[aIndex+1] = a[aIndex+1] * linv;
     a[aIndex+2] = a[aIndex+2] * linv;
      a[aIndex+3] = a[aIndex+3] * linv;
   }
   
   /**
    * Normalizes a, that is, dst  = a/|a|
    * @param a vector to be normalized
    * @param dst vector to receive the result
    */
   public static void normalize(float[] dst, float[] a){
     float linv = 1.0f/length(a);
     dst[0] = a[0] * linv;
     dst[1] = a[1] * linv;
     dst[2] = a[2] * linv;
      dst[3] = a[3] * linv;
   }
   
   /**
    * Normalizes a, that is, a = a/|a|
    * @param a vector to be normalized
    */
   public static void normalize(float[] a){
     float linv = 1.0f/length(a);
     a[0] = a[0] * linv;
     a[1] = a[1] * linv;
     a[2] = a[2] * linv;
      a[3] = a[3] * linv;
   }

    /**
    * Performs pointwise multiplication of two vectors:
    * u[i] = u[i]*v[i];
    */
   public static void pmul(float[] u, float[] v) {
      u[0] = u[0] * v[0];
      u[1] = u[1] * v[1];
      u[2] = u[2] * v[2];
      u[3] = u[3] * v[3];
   } 


    public static String toString(float[]a, int aIndex) {
        return "(" + a[aIndex] + ", " + a[aIndex+1] + ", " + a[aIndex+2] + ", " + a[aIndex+3] + ")";
       
    }
   
   public static String toString(float[]a ) {
        return "(" + a[0] + ", " + a[1] + ", " + a[2] + ", " + a[3] + ")";    
    }
   
   
    /**
     * Returns a String of the form (x, y, z), representing the Vec3f value.
     * */
    public static String toString(float[] a, int fieldwidth, int precision) {
      return toString(a, "%" + fieldwidth + "." + precision + "f");
    }
    
    
    private static final int BUFSIZE = 30;
    /**
     * Returns a String of the form (x, y, z), representing the Vec3f value.
     * */
    public static String toString(float[] a, String fmt) {
        StringBuffer buf = new StringBuffer(BUFSIZE);
        buf.append('(');
        buf.append(String.format(fmt, a[0]));
        buf.append(',');
        buf.append(String.format(fmt, a[1]));
        buf.append(',');
        buf.append(String.format(fmt, a[2]));  
        buf.append(',');
        buf.append(String.format(fmt, a[3]));
        buf.append(')');
        return buf.toString();
    }
   
}
