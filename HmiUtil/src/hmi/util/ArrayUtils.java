package hmi.util;

import java.util.Collection;

public class ArrayUtils
{
    private ArrayUtils(){}
    
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
