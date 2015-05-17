package hmi.tts;

import java.util.List;

/**
 * Timing info for sentences spoken by a TTS generator. 
 * @author welberge
 */
public interface TTSTiming
{
    List<WordDescription> getWordDescriptions();
    List<Visime> getVisimes();
    List<Bookmark> getBookmarks();
    Bookmark getBookmark(String id);
    double getDuration();
}
