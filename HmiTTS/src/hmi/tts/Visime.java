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
package hmi.tts;

import net.jcip.annotations.Immutable;

/**
 * Holds Visime information 
 * @author Herwin
 */
@Immutable
public final class Visime
{
    private final int duration; // /duration in ms

    private final int number; // /visime number

    private final boolean stress; // /stressed in current word?

    
    /**
     * Constructor
     * 
     * @param _number
     *            visime number
     * @param _duration
     *            duration in ms
     */
    public Visime(int number, int duration, boolean stress)
    {
        this.duration = duration;
        this.number = number;
        this.stress = stress;
    }

    
    public int getDuration()
    {
        return duration;
    }

    public int getNumber()
    {
        return number;
    }

    public boolean isStressed()
    {
        return stress;
    }


    /**
     * Get string representation
     */
    @Override
    public String toString()
    {
        return "visime: " + number + " duration: " + duration + " stress:" + stress + "\n";
    }
}
