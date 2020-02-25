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
 * Saccade information.
 * From http://www.liv.ac.uk/~pcknox/teaching/Eymovs/params.htm<br>
 * see also http://en.wikipedia.org/wiki/Saccade
 * @author welberge
 *
 */
public final class Saccade
{
    private Saccade(){}
    /**
     * http://www.liv.ac.uk/~pcknox/teaching/Eymovs/params.htm<br>
     * For normal subjects the relationship between saccade amplitude and duration is fairly linear. 
     * The equation of the line through normal subject data is usually 2.2A+21 (where A is the saccade amplitude).
     */
    public static double getSaccadeDuration(double angle)
    {
        double angleDeg = Math.toDegrees(angle);
        return 0.0022*angleDeg;
    }
}
