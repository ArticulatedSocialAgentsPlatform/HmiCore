package hmi.tts.util;

import hmi.tts.Bookmark;
import hmi.tts.WordDescription;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Various utils to manipulate BML text in the speech behaviour
 * @author Herwin
 */
public final class BMLTextUtil
{
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
        int wordOffset = 0;
        while(index!=-1)
        {
            String str = text.substring(0,index);
            String strAfter = text.substring(index,text.length());
            String syncId = strAfter.replaceAll("<sync\\s+id\\s*=\\s*\"", "");
            syncId = syncId.replaceAll("\".*", "");            
            
            //replace syncs before
            String strNoSync2 = str.replaceAll("<sync\\s+id\\s*=\\s*\"[a-zA-Z][a-zA-Z0-9\\-_]*\"\\s*/?>", "");
            strNoSync2 = strNoSync2.replaceAll("</sync>", "");
            String strSplit[] = strNoSync2.split(" ");
            
            if(strSplit.length==numberOfWords)
            {
                syncAndOffsetList.add(new SyncAndOffset(syncId,wordOffset+strSplit.length-1));
            }
            else
            {
                syncAndOffsetList.add(new SyncAndOffset(syncId,wordOffset+strSplit.length));                                
            }
            wordOffset+=strSplit.length;
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
}
