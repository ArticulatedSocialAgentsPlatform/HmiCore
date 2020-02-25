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
