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
