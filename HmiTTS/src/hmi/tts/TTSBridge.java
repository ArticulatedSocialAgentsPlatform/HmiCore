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

import java.io.IOException;

/**
 * Generic interface to a TTSGenerator
 * @author welberge
 */
public interface TTSBridge
{
    /**
     * Speaks out the sentence, does the appropriate callbacks, fills out visime,
     * @param text the text or script to speak
     */
    TTSTiming speak(String text) throws TTSException;

    /**
     * Generates a file containing the spoken sentence, does the apropiate
     * callbacks, fills out visime, phoneme, bookmark and word lists.
     * 
     * @param text
     *            the text or script to speak
     * @throws FileNotFoundException
     * @throws IOException
     */
    TTSTiming speakToFile(String text, String filename) throws IOException, TTSException;

    /**
     * Gets the timing of the text, fills out visime, phoneme,bookmark and word
     * lists
     * 
     * @param text
     *            the text or script to get the timing of
     */
    TTSTiming getTiming(String text) throws TTSException;
}
