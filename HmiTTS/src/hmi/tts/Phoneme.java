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
package hmi.tts;

import net.jcip.annotations.Immutable;

/**
 * Phoneme
 * @author welberge 
 */
@Immutable
public final class Phoneme
{
    private final int number; // /SAPI phoneme number
    private final boolean stress; // /stressed in current word?
    private final int duration; // /duration in ms

    /**
     * Constructor
     * 
     * @param number
     *            phoneme number
     * @param duration
     *            duration in ms
     */
    public Phoneme(int number, int duration, boolean stress)
    {
        this.duration = duration;
        this.number = number;
        this.stress = stress;
    }

    /**
     * Get the duration of this phoneme
     * @return duration in ms
     */
    public int getDuration()
    {
        return duration;
    }
    
    /**
     * @return SAPI phoneme number
     */
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
        return "phoneme: " + number + " duration: " + duration + " stress:"
                + stress + "\n";
    }
}
