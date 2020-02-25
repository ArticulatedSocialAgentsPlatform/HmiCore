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
package hmi.xml;

import java.util.ArrayList;

/**
 * XMLNameSpaceStack is a stack of XMLNameSpace elements, including search ops
 * 
 * @author Job Zwiers
 */
public class XMLNameSpaceStack
{

    /**
     * Default constructor, allocates an empty stack.
     */
    public XMLNameSpaceStack()
    {
    }

    /**
     * Returns the prefix currently associated with the specified namespace. It
     * is assumed that namespace is an &quot;interned&quot; String If there is
     * no associated prefix, null is returned.
     */
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "ES_COMPARING_PARAMETER_STRING_WITH_EQ", justification = "String is interned")
    public String getPrefix(String namespace)
    {
        for (int index = namespaceStack.size() - 1; index >= 0; index--)
        {
            XMLNameSpace nsp = namespaceStack.get(index);
            if (nsp.getNamespace() == namespace) return nsp.getPrefix();
        }
        return null;
    }

    /**
     * Returns the namespace currently associated with the specified prefix. It
     * is assumed that prefix is an &quot;interned&quot; String If there is no
     * associated namespace, null is returned.
     */
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "ES_COMPARING_PARAMETER_STRING_WITH_EQ", justification = "String is interned")
    public String getNameSpace(String prefix)
    {
        for (int index = namespaceStack.size() - 1; index >= 0; index--)
        {
            XMLNameSpace nsp = namespaceStack.get(index);
            if (nsp.getPrefix() == prefix) return nsp.getNamespace();
        }
        return null;
    }
    
    /**
     * Pushes a &quot;mark&quot; on the stack, informally denoting the position where a new STag element has started.
     * This is used later on to pop a number of pushes together
     */
    public void pushMark() 
    {
       markStack.add(namespaceStack.size()); // records first "empty" namespace stack position
    }

    /**
     * Pops elements from the stack up to and including the last mark.
     */
    public void popMark() {
        int topmarkindex = markStack.size() - 1;
        int markedIndex = markStack.get(topmarkindex);
        markStack.remove(topmarkindex);
        int topIndex = namespaceStack.size() - 1;
        for (int index=topIndex; index >= markedIndex; index--) namespaceStack.remove(index);
        
    }

    /**
     * Push a new XMLNameSpace on the stack.
     * When isNewElement is true, the count stack is also pushed, otherwise,
     * the current top the the count stack is incremented.
     */
    public void pushXMLNameSpace(XMLNameSpace xmlNamespace)
    {
        namespaceStack.add(xmlNamespace);
    }

    /**
     * Pop the top XMLNameSpace from the stack, and return it.
     */
    public XMLNameSpace popXMLNameSpace()
    {
        int topIndex = namespaceStack.size() - 1;
        XMLNameSpace top = namespaceStack.get(topIndex);
        namespaceStack.remove(topIndex);
        return top;
    }

    private ArrayList<XMLNameSpace> namespaceStack = new ArrayList<XMLNameSpace>();
    private ArrayList<Integer> markStack = new ArrayList<Integer>(); // stack for keeping track of namespaceStack counts for "mark" calls.

}
