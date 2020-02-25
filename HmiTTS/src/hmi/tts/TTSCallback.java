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

/**
 * Callback interface that captures events in TTSGenerator.speak 
 * @author Herwin
 */
public interface TTSCallback
{
    /**
     * Wordboundry callback
     * 
     * @param offset
     *            offset, in characters of the string that is spoken
     * @param length
     *            length, in characters, of the word
     */
    void wordBoundryCallback(int offset, int length);

    /**
     * Phoneme callback
     * 
     * @param phoneme
     *            phoneme number
     * @param duration
     *            phoneme duration (in ms)
     * @param nextPhoneme
     *            phoneme number of the next phoneme
     * @param stress
     *            stressed?
     */
    void phonemeCallback(int phoneme, int duration, int nextPhoneme,
            boolean stress);

    /**
     * Bookmark callback called whenever a bookmark &lt;bookmark
     * mark=&qout;name&qoute;&gt; is reached
     * 
     * @param bookmark
     *            name of the bookmark
     */
    void bookmarkCallback(String bookmark);

    /**
     * Phoneme callback
     * 
     * @param duration
     *            visime duration (in ms)
     * @param nextVisime
     *            visime number of the next phoneme
     */
    void visimeCallback(int visime, int duration, int nextVisime,
            boolean stress);

    /**
     * 
     * @param offset
     *            offset, in characters of the string that is spoken
     * @param length
     *            length of the sentence, in characters
     */
    void sentenceBoundryCallback(int offset, int length);

    /**
     * Return true to cancel speaking
     * 
     * @return true to cancel speaking, false otherwise
     */
    boolean stopCallback();
}
