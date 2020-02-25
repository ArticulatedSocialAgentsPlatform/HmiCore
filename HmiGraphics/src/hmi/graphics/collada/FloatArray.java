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
