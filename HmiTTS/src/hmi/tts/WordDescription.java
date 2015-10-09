/*******************************************************************************
 * The MIT License (MIT)
 * Copyright (c) 2015 University of Twente
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *******************************************************************************/
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
