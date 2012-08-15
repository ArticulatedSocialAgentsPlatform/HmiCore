package hmi.util;

import java.util.Collection;

/**
 * Utils for collections
 * @author hvanwelbergen
 *
 */
public class CollectionUtils
{
    private CollectionUtils(){}
    
    /**
     * Ensures that the list has at least size n (by filling it with zeros until it has)
     */
    public static void ensureSize(Collection<?> l, int n)
    {
        while (l.size()<n)
        {
            l.add(null);
        }
    }
}
