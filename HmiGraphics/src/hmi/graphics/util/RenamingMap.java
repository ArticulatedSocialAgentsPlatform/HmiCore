package hmi.graphics.util;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Iterables;

/**
 * Parsers a renaming string (of form "oldname1 newname1"\n"oldname2 newname2") into a oldname->newname bidirectional map. 
 * @author hvanwelbergen
 */
public final class RenamingMap
{
    private RenamingMap(){}
    
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
