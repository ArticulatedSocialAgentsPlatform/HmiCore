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
    public abstract TTSTiming speakBML(String text)throws TTSException;

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
    public abstract TTSTiming getBMLTiming(String s)throws TTSException;

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
