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