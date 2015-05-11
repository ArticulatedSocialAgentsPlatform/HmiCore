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
