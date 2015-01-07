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
