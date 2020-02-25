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
package hmi.tts.util;

/**
 * Stores a BML text sync and the offset it has in the speech text, in words
 * The offset is the number of the the word after the sync (equivalently, the number of words before the sync). 
 * @author Herwin
 */
public class SyncAndOffset
{
    private final String sync;
    public String getSync()
    {
        return sync;
    }
    public int getOffset()
    {
        return offset;
    }
    private final int offset;
    public SyncAndOffset(String sync, int offset)
    {
        this.sync = sync;
        this.offset = offset;
    }
}

