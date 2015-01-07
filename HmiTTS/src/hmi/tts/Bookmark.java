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
 * Bookmark in speech text. Contains a word description of the word after the
 * bookmark and an offset in ms of the bookmark time relative to the start of
 * the speech text. word is null if the bookmark is at the end of the sentence
 * 
 * @author welberge
 */
@Immutable
public final class Bookmark
{
    private final String name;

    private final WordDescription word;

    private final int offset;

    public Bookmark(String n, WordDescription w, int o)
    {
        name = n;
        word = w;
        offset = o;
    }

    @Override
    public String toString()
    {
        return "bookmark: " + name + " wordDescription:" + word + "offset: "
                + offset;
    }

    public String getName()
    {
        return name;
    }

    /**
     * Word description of the word after this bookmark, null for none.
     * @return
     */
    public WordDescription getWord()
    {
        return word;
    }

    public int getOffset()
    {
        return offset;
    }    
}
