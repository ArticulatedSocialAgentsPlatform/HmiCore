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
