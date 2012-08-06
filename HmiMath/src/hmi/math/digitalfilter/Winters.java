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
