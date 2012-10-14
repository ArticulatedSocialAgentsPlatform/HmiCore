package hmi.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
    
    /**
     * Get the first class of type U in the collection
     */
    public static <T, U> U getFirstClassOfType(Collection<T> l, Class<U> type)
    {
        for(T o:l)
        {
            if (type.isInstance(o))
            {
                return type.cast(o);
            }
        }
        return null;
    }
    
    /**
     * Get the all classes of type U in the collection
     */
    public static <T, U> List<U> getClassesOfType(Collection<T> l, Class<U> type)
    {
        List<U> list = new ArrayList<U>();
        for(T o:l)
        {
            if (type.isInstance(o))
            {
                list.add(type.cast(o));
            }
        }
        return list;
    }
}
