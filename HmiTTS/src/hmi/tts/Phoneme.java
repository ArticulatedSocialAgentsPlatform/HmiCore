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
