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
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Creates a Fifo List of fixed size. When new elements are added to a full list, the first elements are removed.
 *  
 * @author welberge
 */
public class CircularBuffer<E> implements List<E>
{
    private final ArrayList<E>buffer;
    private final int size;
    
    public CircularBuffer(int size)
    {
        buffer = new ArrayList<E>(size);
        this.size = size;
    }
    
    public E get(int index)
    {
        return buffer.get(index);
    }
    
    @Override
    public int size()
    {
        return buffer.size();
    }
    
    @Override
    public Iterator<E> iterator()
    {
        return buffer.iterator();
    }
    
    private void fixSize()
    {
        while(buffer.size()>size)
        {
            buffer.remove(0);
        }
    }
    
    @Override
    public boolean add(E element)
    {        
        if(buffer.add(element))
        {
            fixSize();
            return true;
        }
        return false;
    }
    
    @Override
    public void add(int index, E element)
    {
        buffer.add(index,element);
        fixSize();
    }
    
    @Override
    public boolean addAll(Collection<? extends E> elements)
    {
        if(buffer.addAll(elements))
        {
            fixSize();
            return true;
        }
        return false;
    }
    @Override
    public boolean addAll(int index, Collection<? extends E> elements)
    {
        if(buffer.addAll(index,elements))
        {
            fixSize();
            return true;
        }
        return false;
    }
    
    @Override
    public void clear()
    {
        buffer.clear();        
    }
    
    @Override
    public boolean contains(Object element)
    {
        return buffer.contains(element);
    }
    
    @Override
    public boolean containsAll(Collection<?> elements)
    {
        return buffer.containsAll(elements);
    }
    
    @Override
    public int indexOf(Object element)
    {
        return buffer.indexOf(element);
    }
    
    @Override
    public boolean isEmpty()
    {
        return buffer.isEmpty();
    }
    
    @Override
    public int lastIndexOf(Object element)
    {
        return buffer.lastIndexOf(element);
    }
    
    @Override
    public ListIterator<E> listIterator()
    {
        return buffer.listIterator();
    }
    @Override
    public ListIterator<E> listIterator(int index)
    {
        return buffer.listIterator(index);
    }
    
    @Override
    public boolean remove(Object element)
    {
        return buffer.remove(element);
    }
    @Override
    public E remove(int element)
    {
        return buffer.remove(element);
    }
    
    @Override
    public boolean removeAll(Collection<?> elements)
    {
        return buffer.removeAll(elements);        
    }
    @Override
    public boolean retainAll(Collection<?> elements)
    {
        return buffer.retainAll(elements);
    }
    @Override
    public E set(int index, E element)
    {
        return buffer.set(index, element);
    }
    @Override
    public List<E> subList(int index1, int index2)
    {
        return buffer.subList(index1,index2);
    }
    @Override
    public Object[] toArray()
    {
        return buffer.toArray();
    }
    @Override
    public <T> T[] toArray(T[] element)
    {
        return buffer.toArray(element);
    }
}
