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
 * An array of floats.
 * The data is available as a public attribute &quot;floats&quot; of type float[]
 * the number of floats (i.e. the length of the floats array) is available
 * as public attribute &quot;count&quot;
 * @author Job Zwiers
 */
public class FloatArray extends ColladaElement
{

    public static final int DEFAULT_DIGITS = 6;
    public static final int DEFAULT_MAGNITUDE = 38;
    private static final int NR_OF_FLOATS_PER_LINE = 3;

    private int count;
    private int digits = DEFAULT_DIGITS;
    private int magnitude = DEFAULT_MAGNITUDE;
    private float[] floats;

    public FloatArray()
    {
        super();
    }

    public FloatArray(Collada collada, XMLTokenizer tokenizer) throws IOException
    {
        super(collada);
        readXML(tokenizer);
    }

    /**
     * appends a String of attributes to buf.
     */
    @Override
    public StringBuilder appendAttributes(StringBuilder buf)
    {
        super.appendAttributes(buf);
        appendAttribute(buf, "count", count);
        if (digits != DEFAULT_DIGITS) appendAttribute(buf, "digits", digits);
        if (magnitude != DEFAULT_MAGNITUDE) appendAttribute(buf, "magnitude", magnitude);
        return buf;
    }

    /**
     * decodes the XML attributes
     */
    @Override
    public void decodeAttributes(HashMap<String, String> attrMap, XMLTokenizer tokenizer)
    {
        count = getRequiredIntAttribute("count", attrMap, tokenizer);
        digits = getOptionalIntAttribute("digits", attrMap, DEFAULT_DIGITS);
        magnitude = getOptionalIntAttribute("magnitude", attrMap, DEFAULT_MAGNITUDE);
        super.decodeAttributes(attrMap, tokenizer);
    }

    @Override
    public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt)
    {
        appendNewLine(buf, fmt);
        appendFloats(buf, floats, ' ', fmt, NR_OF_FLOATS_PER_LINE);
        return buf;
    }

    @Override
    public void decodeContent(XMLTokenizer tokenizer) throws IOException
    {
        floats = new float[count];
        if (count > 0)
        {
            decodeFloatArray(tokenizer.takeCharData(), floats);
        }
        getCollada().addFloatArray(getId(), floats);
    }

    /*
     * The XML Stag for XML encoding
     */
    private static final String XMLTAG = "float_array";

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
