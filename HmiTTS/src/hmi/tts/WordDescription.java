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

import java.util.*;

import net.jcip.annotations.Immutable;

/**
 * @author welberge Describes the phonetic structure of a word
 */
@Immutable
public final class WordDescription
{
    private final List<Phoneme> phonemes; ///the phonemes

    private final List<Visime> visimes; /// the visimes

    private final String word; ///the word

    public List<Phoneme> getPhonemes()
    {
        return phonemes;
    }

    public List<Visime> getVisimes()
    {
        return visimes;
    }

    public String getWord()
    {
        return word;
    }

    /**
     * constructor
     * 
     * @param _word
     *            word
     * @param _phonemes
     *            phonemes in the word
     */
    public WordDescription(String word, List<Phoneme> phonemes, List<Visime> visimes)
    {
        this.word = word;
        this.phonemes = phonemes;
        this.visimes = visimes;
    }

    /**
     * Get the time needed to speak this word
     * 
     * @return the time needed to speak this word, in ms
     */
    public int getDuration()
    {
        int duration = 0;
        for (Phoneme p : phonemes)
        {
            duration += p.getDuration();
        }
        if ((duration==0) && (visimes!=null))
        {
        	//XXX: the Fluency voices have no phonemes, but they do have visemes. So here I get the duration from the visemes. /DR
            for (Visime v : visimes)
            {
                duration += v.getDuration();
            }
        	
        }
        return duration;
    }

    /**
     * String representation of the word
     */
    @Override
    public String toString()
    {
        return "Word: " + word + "\n" + phonemes.toString() + "\n" + visimes.toString() + "\n";
    }

    /**
     * Gets the phonological peak of the word
     * 
     * @return the phonological peak of the word, null if not found
     */
    public PhonologicalPeak getPhonologicalPeak()
    {
        // peak phoneme is the first stressed phoneme
        int offset = 0;
        for (Phoneme p : phonemes)
        {
            if (p.isStressed())
            {
                return new PhonologicalPeak(p, offset);
            }
            offset += p.getDuration();
        }

        // not found
        return null;
    }
}
