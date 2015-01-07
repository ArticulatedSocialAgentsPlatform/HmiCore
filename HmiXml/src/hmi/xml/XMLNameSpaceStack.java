/*******************************************************************************
 * The MIT License (MIT)
 * Copyright (c) 2015 University of Twente
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *******************************************************************************/

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
