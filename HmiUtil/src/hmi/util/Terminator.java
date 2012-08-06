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
 * A Terminator is an interface that requires a "terminate" method.
 * The intention is that a Terminator performs actions immediately before
 * a JVM is terminated, and allows for a "clean termination" procedure.
 * @author Job Zwiers 
 */
public interface Terminator
{
 
   /**
    * signal that the system is about to terminate; This object should
    * perform the necessary actions for "clean termination", without
    * exiting the system as a whole.
    * The boolean value returned can be used to signal whether termination
    * was succesful or not.
    */
   boolean terminate();
      
}

