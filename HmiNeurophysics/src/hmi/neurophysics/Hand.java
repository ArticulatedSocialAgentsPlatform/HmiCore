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
package hmi.neurophysics;

/**
 * Hand constraints for the left and right H-Anim hand. 
 * @author Herwin
 *
 */
public final class Hand
{
    /**
     * Get the rotation of finger joint 3 given that of joint 2
     */
    public static final double getDIPRotation(double PIPRotation)
    {
        return (2d/3d)*PIPRotation;
    }
    
    /**
     * Get the rotation of finger joint 2 given that of joint 3
     */
    public static final double getPIPRotation(double DIPRotation)
    {
        return (3d/2d)*DIPRotation;
    }
    
    public static double getMinimumFingerFlexionPIP()
    {
        return Math.toRadians(0d);
    }
    
    public static double getMaximumFingerFlexionPIP()
    {
        return Math.toRadians(110d);
    }
    
    public static double getMaximumFingerFlexionDIP()
    {
        return Math.toRadians(90d);
    }
    
    public static double getMinimumFingerFlexionDIP()
    {
        return Math.toRadians(0d);
    }
    
    public static double getMinimumFingerFlexionMCP()
    {
        return Math.toRadians(0d);
    }
    
    public static double getMaximumFingerFlexionMCP()
    {
        return Math.toRadians(90d);
    }
    
    //spreading angle
    public static double getMaximumFingerAbduction()
    {
        return Math.toRadians(15d);
    }
    
    //bringing together angle
    public static double getMinimumFingerAbduction()
    {
        return Math.toRadians(-15d);
    }
    
    
    //guestimated values below
    public static double getMaximumTMCAbduction()
    {
        return Math.toRadians(60);
    }
    
    public static double getMinimumTMCAbduction()
    {
        return Math.toRadians(-40);
    }
    
    public static double getMinimumTMCFlexion()
    {
        return Math.toRadians(0);
    }
    
    public static double getMaximumTMCFlexion()
    {
        return Math.toRadians(170);
    }
}
