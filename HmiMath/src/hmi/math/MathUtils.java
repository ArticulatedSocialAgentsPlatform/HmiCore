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
package hmi.math;

/**
 * Misc. math functions that don't fit anywhere else (yet).
 * @author welberge
 */
public final class MathUtils {
   
   /***/
   private MathUtils() {}
   
   /**
    * Calculates the tanh of x
    * @param x
    * @return the tanh of x
    */
   public static double tanh(double x) {
      //(e^2x - 1)/ (e^2x + 1)
      return (Math.exp(2*x)-1)/(Math.exp(2*x)+1);     
   }
}
