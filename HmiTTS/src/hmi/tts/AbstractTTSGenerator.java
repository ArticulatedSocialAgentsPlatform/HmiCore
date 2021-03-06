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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Abstract implementation for Text-To-Speech generators.
 * @author Herwin
 */
public abstract class AbstractTTSGenerator
{
    /** This callback is supposed to be set only once (see also callback below) */
    protected TTSCallback permanentCallback = null;

    /**
     * In the speechengine package of Herwin, this callback is replaced all the
     * time for new speechunits, during setup as well as during speak() time.
     * Therefore, the permanentCallback is added -- that's a callback that is
     * set only once (!)
     */
    protected TTSCallback callback = null;

    protected List<WordDescription> wordDescriptions = new ArrayList<WordDescription>();

    protected Collection<Bookmark> bookmarks = new ArrayList<Bookmark>();

    protected Collection<Visime> visimes = new ArrayList<Visime>();

    private int volume = 100; // /speech volume in % (0..100)

    private int rate = 0; // /speech rate (-10..10)

    /**
     * Sets up the callbacks
     * 
     * @param cb
     *            callback class
     */
    public void setCallback(TTSCallback cb)
    {
        callback = cb;
    }

    /**
     * Sets up the permanent callback
     * 
     * @param cb
     *            callback class
     */
    public void setPermanentCallback(TTSCallback cb)
    {
        permanentCallback = cb;
    }

    public abstract TTSTiming speak(String text) throws TTSException;

    /**
     * Speaks out the BML sentence, does the appropriate callbacks, fills out
     * visime, phoneme, bookmark and word lists
     * 
     * @param text
     *            the text or script to speak
     */
    public abstract TTSTiming speakBML(String text) throws TTSException;

    /**
     * Generates a file containing the spoken sentence, does the appropriate
     * callbacks, fills out visime, phoneme, bookmark and word lists.
     * 
     * @param text
     *            the text or script to speak
     * @throws FileNotFoundException
     * @throws IOException
     */
    public abstract TTSTiming speakToFile(String text, String filename) throws IOException, TTSException;

    /**
     * Generates a file containing the spoken BML sentence, does the appropriate
     * callbacks, fills out visime, phoneme, bookmark and word lists.
     * 
     * @param text
     *            the text or script to speak
     * @throws IOException
     */
    public abstract TTSTiming speakBMLToFile(String text, String filename) throws IOException, TTSException;

    /**
     * Gets the timing of the text, fills out visime, phoneme,bookmark and word
     * lists
     * 
     * @param text
     *            the text or script to get the timing of
     */
    public abstract TTSTiming getTiming(String text) throws TTSException;

    /**
     * Sets the speaker
     * 
     * @param speaker
     *            speaker name
     */
    public abstract void setVoice(String speaker);

    /**
     * Get the current speaker
     * @return
     */
    public abstract String getVoice();

    /**
     * Get the timing of a BML speech behavior
     * 
     * @param b
     *            the bml speech behavior
     */
    public abstract TTSTiming getBMLTiming(String s) throws TTSException;

    /**
     * Get the duration of the last spoken/timed text
     * 
     * @return duration in s
     */

    public abstract String[] getVoices();

    /**
     * @return the volume
     */
    public int getVolume()
    {
        return volume;
    }

    /**
     * @param volume
     *            the volume to set, specified in % (0..100)
     */
    public void setVolume(int v)
    {
        volume = v;
    }

    /**
     * @return the rate
     */
    public int getRate()
    {
        return rate;
    }

    /**
     * @param rate
     *            the rate to set, -10..10
     */
    public void setRate(int r)
    {
        rate = r;
    }
}
