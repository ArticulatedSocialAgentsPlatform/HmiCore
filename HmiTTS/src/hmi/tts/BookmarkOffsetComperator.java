package hmi.tts;

import java.util.Comparator;

/**
 * Compares bookmarks by their offset time
 * @author Herwin
 *
 */
public class BookmarkOffsetComperator implements Comparator<Bookmark>
{

    @Override
    public int compare(Bookmark o1, Bookmark o2)
    {
        if (o1.getOffset() < o2.getOffset())
            return -1;
        if (o1.getOffset() > o2.getOffset())
            return 1;
        return 0;
    }

}
