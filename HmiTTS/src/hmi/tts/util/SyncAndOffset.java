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

