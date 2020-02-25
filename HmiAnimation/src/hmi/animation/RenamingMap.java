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
package hmi.animation;

import java.io.IOException;

import com.google.common.base.CharMatcher;
import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Iterables;
import com.google.common.io.Resources;

/**
 * Parsers a renaming string (of form "oldname1 newname1"\n"oldname2 newname2") into a oldname->newname bidirectional map. 
 * @author hvanwelbergen
 */
public final class RenamingMap
{
    private RenamingMap(){}
    
    public static BiMap<String, String> renamingMapFromFileOnClasspath(String filename) throws IOException
    {
        return renamingMap(Resources.toString(Resources.getResource(filename), Charsets.UTF_8));
    }
    
    public static BiMap<String,String> renamingMap(String renaming)
    {
        BiMap<String,String>renamingMap = HashBiMap.create();
        Splitter split = Splitter.on(CharMatcher.WHITESPACE.or(CharMatcher.is('\n').or(CharMatcher.is('\r').or(CharMatcher.is('\f'))))).trimResults().omitEmptyStrings();
        String[] splittedStr = Iterables.toArray(split.split(renaming), String.class);
        if(splittedStr.length%2 > 0)
        {
            throw new IllegalArgumentException("renaming string does not contain an even amount of names");
        }
        for(int i=0;i<splittedStr.length/2;i++)
        {
            renamingMap.put(splittedStr[i*2],splittedStr[i*2+1]);
        }
        return renamingMap;
    }
}
