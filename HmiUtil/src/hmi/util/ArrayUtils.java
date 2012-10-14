package hmi.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Utils for arrays
 * @author hvanwelbergen
 *
 */
public class ArrayUtils
{
    private ArrayUtils(){}
    
    /**
     * Get the first class of type U in the collection
     */
    public static <T, U> U getFirstClassOfType(T[] l, Class<U> type)
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
    public static <T, U> List<U> getClassesOfType(T[] l, Class<U> type)
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
