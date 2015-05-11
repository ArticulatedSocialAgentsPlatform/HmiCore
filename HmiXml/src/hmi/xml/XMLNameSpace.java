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

/**
 * XMLNameSpace defines an XML name space, together with a short abbreviating label.
 * @author Job Zwiers
 */
public final class XMLNameSpace
{

    /*
     * default constructor
     */
    @SuppressWarnings("unused")
    private XMLNameSpace()
    {
    }

    /**
     * XMLnamespace with (only) the full namespace String defined
     */
    public XMLNameSpace(String namespace)
    {
        this.namespace = namespace.intern();
    }

    /**
     * XMLnamespace with a namespace label as well as the full namespace String
     */
    public XMLNameSpace(String prefix, String namespace)
    {
        this.namespace = null;
        this.prefix = null;
        if (namespace != null)
        {
            this.namespace = namespace.intern();
        }
        if (prefix != null)
        {
            this.prefix = prefix.intern();
        }
    }

    /**
     * Set the abbreviating label for this namespace
     */
    public void setPrefix(String prefix)
    {
        this.prefix = prefix.intern();
    }

    /**
     * return the full namespace String
     */
    public String getNamespace()
    {
        return namespace;
    }

    /**
     * Return the abbreviating label for this namespace, which could be null
     */
    public String getPrefix()
    {
        return prefix;
    }

    private String namespace;
    private String prefix;

}
