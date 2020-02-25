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

import java.util.Collections;
import java.util.List;

import net.jcip.annotations.Immutable;

/**
 * Timing info for sentences spoken by a TTS generator.
 * @author welberge
 */
@Immutable
public final class TimingInfo implements TTSTiming
{
    private final List<WordDescription> wordDescriptions;
    private final List<Bookmark> bookmarks;
    private final List<Visime> visimes;
    private final Prosody prosody;

    @Override
    public Prosody getProsody()
    {
        return prosody;
    }

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

    public TimingInfo(List<WordDescription> wd, List<Bookmark> bms, List<Visime> vis, Prosody pros)
    {
        wordDescriptions = wd;
        bookmarks = bms;
        visimes = vis;
        prosody = pros;
    }

    public TimingInfo(List<WordDescription> wd, List<Bookmark> bms, List<Visime> vis)
    {
        this(wd, bms, vis, null);
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
        return wordDescriptions.toString() + " " + bookmarks.toString();
    }
}
