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
 
package hmi.math.digitalfilter;

/**
 * Winter's low-pass filter. This is a 2-pass Butterworth filter, in which the second 'backward' pass 
 * corrects the phase-lag introduced by 1-pass Butterworth filtering.   
 * Implementation from:
 * Winter, David A., Biomechanics and Motor Control of Human Movement, Wiley, 2004
 * @author Herwin van Welbergen
 */
public final class Winters
{
  
    /** */
    private Winters() {}
  
    private static void reverse(float[] b) 
    {
        int left  = 0;          
        int right = b.length-1;
       
        while (left < right) 
        {
           float temp = b[left]; 
           b[left]  = b[right]; 
           b[right] = temp;          
           left++;
           right--;
        }
     }
    
    private static void reverse(float[] b, int width) 
    {
        int left  = 0;          
        int right = b.length/width-1;
       
        while (left < right) 
        {
           for(int i=0;i<width;i++)
           {
               float temp = b[left*width+i]; 
               b[left*width+i]  = b[right*width+i]; 
               b[right*width+i] = temp;
           }
           left++;
           right--;
        }
     }
    
    /**
     * Winters-filters the data, assumes the input is aligned in blocks of width floats
     * @param fin input data
     * @param fc cutt-off frequency
     * @param fs sample frequency
     * @param width the block width
     * @param fout output data
     */
    public static void winters(float[] fin, float fc, float fs, int width, float[] fout)
    {
        float[] ftemp = new float[fin.length];
        Butterworth.butterworth(fin, fc, fs, 2, width, ftemp);
        
        //2nd reverse pass to fix phase lag
        reverse(ftemp, width);        
        Butterworth.butterworth(ftemp, fc, fs, 2, width, fout);
        reverse(fout, width);
    }
    
    /**
     * Winters-filters the data
     * @param fin input data
     * @param fc cutt-off frequency
     * @param fs sample frequency
     * @param fout output data
     */
    public static void winters(float[] fin, float fc, float fs, float[] fout)
    {
        float[] ftemp = new float[fin.length];
        Butterworth.butterworth(fin, fc, fs, 1, fout);
        
        //2nd reverse pass to fix phase lag
        reverse(ftemp);        
        Butterworth.butterworth(ftemp, fc, fs, 2, fout);
        reverse(fout);
    }
}
