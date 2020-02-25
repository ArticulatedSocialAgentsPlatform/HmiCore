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
