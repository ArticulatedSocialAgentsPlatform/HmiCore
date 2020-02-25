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
package hmi.util;

/**
 * A ClockListener can receive callbacks, in the form of
 * time(currentTime) calls, made by some Clock object.
 * Such callbacks will often arrive in more or less regular intervals,
 * and with varying precision, but no guarantees can be given in this respect.
 * @author Job Zwiers
 */
public interface ClockListener
{
   
   /**
    * initTime() is called before the Clock starts running, and sends some initial time value.
    * This will often equal the time send for the first regular time() call.
    * This is done on the same clock  Thread that is going to send the regular time() calls. 
    */
   void initTime(double initTime);
   
   
    /**
     * time() is called, with the &quot;current time&quot; specified in seconds.
     */
    void time(double currentTime);   
}
