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

package hmi.graphics.collada;

import hmi.xml.XMLFormatting;
import hmi.xml.XMLTokenizer;

import java.io.IOException;
import java.util.HashMap;

/**
 * @author Job Zwiers
 */
public class IDREFArray extends ColladaElement
{
    private static final int NR_OF_NAMES_PER_LINE = 3;

    private int count;
    private String[] idrefs;

    public IDREFArray()
    {
        super();
    }

    public IDREFArray(Collada collada, XMLTokenizer tokenizer) throws IOException
    {
        super(collada);
        readXML(tokenizer);
    }

    /**
     * Returns the array with names, of length equal to the count attribute
     */
    public String[] getIDREFS()
    {
        return idrefs;
    }

    /**
     * appends a String of attributes to buf.
     */
    @Override
    public StringBuilder appendAttributes(StringBuilder buf)
    {
        super.appendAttributes(buf);
        appendAttribute(buf, "count", count);
        return buf;
    }

    /**
     * decodes the XML attributes
     */
    @Override
    public void decodeAttributes(HashMap<String, String> attrMap, XMLTokenizer tokenizer)
    {
        count = getRequiredIntAttribute("count", attrMap, tokenizer);
        super.decodeAttributes(attrMap, tokenizer);
    }

    @Override
    public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt)
    {
        appendNewLine(buf, fmt);
        appendStrings(buf, idrefs, ' ', fmt, NR_OF_NAMES_PER_LINE);
        return buf;
    }

    @Override
    public void decodeContent(XMLTokenizer tokenizer) throws IOException
    {
        idrefs = new String[count];
        if (count > 0)
        {
            decodeStringArray(tokenizer.takeCharData(), idrefs);
        }
        getCollada().addIDREFArray(getId(), idrefs);
    }

    /*
     * The XML Stag for XML encoding
     */
    private static final String XMLTAG = "IDREF_array";

    /**
     * The XML Stag for XML encoding
     */
    public static String xmlTag()
    {
        return XMLTAG;
    }

    /**
     * returns the XML Stag for XML encoding
     */
    @Override
    public String getXMLTag()
    {
        return XMLTAG;
    }

}
