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

import java.util.Collections;
import java.util.List;

import net.jcip.annotations.Immutable;

/**
 * Timing info for sentences spoken by a TTS generator. 
 * @author welberge
 */
@Immutable
public final class TimingInfo
{
    private final List<WordDescription> wordDescriptions;
    private final List<Bookmark> bookmarks;
    private final List<Visime> visimes;
    
    public List<WordDescription> getWordDescriptions()
    {
        return Collections.unmodifiableList(wordDescriptions);
    }

    public List<Bookmark> getBookmarks()
    {
        return Collections.unmodifiableList(bookmarks);
    }

    public List<Visime> getVisimes()
    {
        return Collections.unmodifiableList(visimes);        
    }

    /**
     * Get bookmark with id id, null if no bookmark with id exists
     */
    public Bookmark getBookmark(String id)
    {
        for (Bookmark b : bookmarks)
        {
            if (b.getName().equals(id))
            {
                return b;
            }
        }
        return null;
    }
    
    public TimingInfo(List<WordDescription> wd, List<Bookmark> bms, List<Visime> vis)
    {
        wordDescriptions = wd;
        bookmarks = bms;
        visimes = vis;
    }
    
    public double getDuration()
    {
        double duration = 0;
        for (WordDescription wd : wordDescriptions)
        {
            duration += wd.getDuration() / 1000.0;
        }
        return duration;
    }
    
    @Override
    public String toString()
    {
        return wordDescriptions.toString()+" "+bookmarks.toString();
    }
}
