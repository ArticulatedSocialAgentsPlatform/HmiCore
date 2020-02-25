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
