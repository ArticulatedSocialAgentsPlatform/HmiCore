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


package hmi.util;

/**
 * Utility functions for OS-specific properties
 * @author Herwin
 */
public final class OS
{
    public static final String MAC = "mac";
    public static final String LINUX = "linux";
    public static final String WINDOWS = "win";
    
    private OS()
    {
        
    }
    
    /**
     * tests for compatibility with the OS as defined by 
     * System.getProperty("os.name")
     */
    public static boolean equalsOS(String os)
    {
        return System.getProperty("os.name").toLowerCase().contains(os);
    }
    
    /**
     * return a System.getProperty("line.separator")
     */
    public static String getNewline()
    {
        return System.getProperty("line.separator");
    }
}
