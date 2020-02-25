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

import java.util.regex.*;
import java.util.HashMap;

import hmi.tts.Bookmark;
import hmi.tts.WordDescription;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Various utils to manipulate BML text in the speech behaviour
 * @author Herwin
 */
public final class BMLTextUtil
{
    private final static Logger log = LoggerFactory.getLogger(BMLTextUtil.class.getName());

    private BMLTextUtil()
    {
        
    }
    
    public static String stripSyncNameSpace(String text)
    {
        String textNoNs = text.replaceAll("xmlns\\s*=\\s*\"[a-zA-Z][a-zA-Z0-9\\-_:\\.\\/]*\"", " ");
        textNoNs = textNoNs.replaceAll("xmlns:[a-zA-Z][a-zA-Z0-9\\-_]*\\s*=\\s*\"[a-zA-Z][a-zA-Z0-9\\-_:\\.\\/]*\"", " ");
        return textNoNs;
    }
    /**
     * Strip &ltsync&gt's from text
     * @param text text to strip
     * @return stripped text
     */
    public static String stripSyncs(String text)
    {
        String textNoNs = stripSyncNameSpace(text);
        String textNoSync = textNoNs.replaceAll("<sync\\s+id\\s*=\\s*\"[a-zA-Z][a-zA-Z0-9\\-_]*\"\\s*/?>", " ");
        textNoSync = textNoSync.replaceAll("</sync>", " ");
        return textNoSync;
    }
    
    public static List<SyncAndOffset> getSyncAndOffsetList(String text, int numberOfWords)
    {
        text = stripSyncNameSpace(text);
        List<SyncAndOffset> syncAndOffsetList = new ArrayList<SyncAndOffset>(); 
        
        int index = text.indexOf("<sync");      
        while(index!=-1)
        {
            String str = text.substring(0,index);
            String strAfter = text.substring(index,text.length());
            String syncId = strAfter.replaceAll("<sync\\s+id\\s*=\\s*\"", "");
            syncId = syncId.replaceAll("\".*", "");            
            
            //replace syncs before
            String strNoSync2 = str.replaceAll("<sync\\s+id\\s*=\\s*\"[a-zA-Z][a-zA-Z0-9\\-_]*\"\\s*/?>", "");
            strNoSync2 = strNoSync2.replaceAll("</sync>", "");
            String strSplit[] = Iterables.toArray(Splitter.on(" ").omitEmptyStrings().split(strNoSync2), String.class);
            
            if(strSplit.length==numberOfWords)
            {
                syncAndOffsetList.add(new SyncAndOffset(syncId,strSplit.length));
            }
            else
            {
                syncAndOffsetList.add(new SyncAndOffset(syncId,strSplit.length));                                
            }
            index = text.indexOf("<sync",index+1);
        }
        return syncAndOffsetList;
    }
    
    public static void getBookmarks(String text, Collection<WordDescription>desc, Collection<Bookmark>bookmarks)
    {
        text = stripSyncNameSpace(text);
        int index = text.indexOf("<sync");      
        while(index!=-1)
        {
            String str = text.substring(0,index);
            String strAfter = text.substring(index,text.length());
            String syncId = strAfter.replaceAll("<sync\\s+id\\s*=\\s*\"", "");
            syncId = syncId.replaceAll("\".*", "");            
            
            //replace syncs before
            String strNoSync2 = str.replaceAll("<sync\\s+id\\s*=\\s*\"[a-zA-Z][a-zA-Z0-9\\-_]*\"\\s*/?>", "");
            strNoSync2 = strNoSync2.replaceAll("</sync>", "");
            strNoSync2 = strNoSync2.replaceAll("\\s", "");
            
            StringBuffer wdStr = new StringBuffer("");
            int timeOffset = 0;
            
            //this doesn't really work because of expansions of 16 => sixteen in the word description etc.
            for(WordDescription d:desc)
            {
                if(wdStr.toString().equals(strNoSync2))
                {
                    bookmarks.add(new Bookmark(syncId, d, timeOffset));
                    break;
                }
                wdStr.append(d.getWord().trim());
                timeOffset += d.getDuration();                
            }
            index = text.indexOf("<sync",index+1);
        }
    }
    
    /**
     * Converts BML speech text to SSML speech text, that is: sync => mark
     */
    public static String BMLToSSML(String text)
    {
        text = stripSyncNameSpace(text);
        String str = text.replaceAll("<sync\\s+id", "<mark name");
        return str.replaceAll("</sync>", "</mark>");
    }
    
    /**
     * Converts BML speech text to SAPI speech text, that is: sync => bookmark
     */
    public static String BMLToSAPI(String text)
    {
        text = stripSyncNameSpace(text);
        String str = text.replaceAll("<sync\\s+id", "<bookmark mark");
        return str.replaceAll("</sync>", "</bookmark>");
    }

    /**
     * Converts BML speech text to Fluency speech text, that is: <sync id="..."/> or <sync id="..."></sync> with various whitespacing becomes => \bookmark=...\ 
     BECAUSE FLUENCY WANTS NUMERIC IDS, WHICH ARE FORBIDDING IN BML, WE NEED TO STRIP ALL NON-NUMERIC FROM THE ID!
     */
    public static String BMLToFluency(String text, HashMap<String,String> fluencySyncToBmlSync)
    {
        String str = stripSyncNameSpace(text);
        //capture 1 of the match is the id!
        String regex = "<sync\\s+id\\s*=\\s*\\\"([^\\\"]*)\\\"\\s*(\\/\\>|\\>([^\\<])*\\<\\/sync\\>)";
        //first get all matches to the sync pattern
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(str);
        //grab all $1 (that is, the actual ids) and put them in syncIds
        ArrayList<String> syncIds = new ArrayList<String>();
        while (m.find())
        {
            String nextId = m.group(1);
            //log.info("found sync id in input: {}",nextId);
            syncIds.add(nextId);
        }
        //for every syncId in the input, strip the non-digits from the syncid
        for (String id:syncIds)
        {
            String cleanId = ""+fluencySyncToBmlSync.size();
            //System.out.println("cid:"+cleanId);
            str = str.replaceAll("id\\s*=\\s*\\\""+id+"\\\"","id=\\\""+cleanId+"\\\"");
            fluencySyncToBmlSync.put(cleanId,id);
        }
        /* in remaining string, use the matcher to remove the <sync etc> in favour of \bookmark=...\ */
        str=str.replaceAll(regex," \\\\bookmark=$1\\\\ ");
        //log.info("resulting text after replacing bml syncs by fluency syncs: {}",str);
        return str;
    }
}
