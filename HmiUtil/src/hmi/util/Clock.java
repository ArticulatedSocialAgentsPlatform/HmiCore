//
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

//
package hmi.util;

/**
 * A Clock is an object that delivers time stamps at 
 * (more or less) regular intervals to a set of subscribers.
 * The time is delivered in the form of a "long" integer, in milliseconds.
 * The interval between two clock "ticks" and the precision of the time value itself 
 * are not guaranteed, and are system dependent.
 * @author Job Zwiers
 */
public interface Clock {
   
   /**
    * adds "listener" to the collection of ClockListeners
    * that receive time(currentTime) callbacks.
    */
   void addClockListener(ClockListener listener);
   
   /**
    * Gets this Clock's current media time in seconds. 
    */
   double getMediaSeconds();
    
}
