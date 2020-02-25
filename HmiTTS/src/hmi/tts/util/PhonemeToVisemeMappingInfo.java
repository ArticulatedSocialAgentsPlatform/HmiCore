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
package hmi.tts.util;

import hmi.util.Resources;
import hmi.xml.XMLScanException;
import hmi.xml.XMLStructureAdapter;
import hmi.xml.XMLTokenizer;

import java.io.IOException;
import java.util.HashMap;

import lombok.Getter;

/**
 * Can read the PhonemeToVisemeMapping XML, useful for binding loaders 
 * @author hvanwelbergen
 *
 */
public class PhonemeToVisemeMappingInfo extends XMLStructureAdapter
{
    @Getter
    private PhonemeToVisemeMapping mapping;

    public PhonemeToVisemeMappingInfo()
    {
        mapping = new NullPhonemeToVisemeMapping();
    }

    public void decodeAttributes(HashMap<String, String> attrMap, XMLTokenizer tokenizer)
    {
        String resources = getRequiredAttribute("resources", attrMap, tokenizer);
        String filename = getRequiredAttribute("filename", attrMap, tokenizer);
        XMLPhonemeToVisemeMapping xmlmapping = new XMLPhonemeToVisemeMapping();
        try
        {
            xmlmapping.readXML(new Resources(resources).getReader(filename));
        }
        catch (IOException e)
        {
            XMLScanException ex = new XMLScanException(e.getMessage());
            ex.initCause(e);
            throw ex;
        }
        mapping = xmlmapping;
    }

    public String getXMLTag()
    {
        return XMLTAG;
    }

    public static final String XMLTAG = "PhonemeToVisemeMapping";
}