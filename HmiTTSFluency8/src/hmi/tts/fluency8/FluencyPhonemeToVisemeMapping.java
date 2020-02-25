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
package hmi.tts.fluency8;

import hmi.xml.*;

import java.util.HashMap;
import hmi.tts.util.*;
import java.io.IOException;

/**
 * given a phoneme number, return the viseme number.
 * 
 * The mapping is read from a resource file.
 * Note: meaning of phoneme number dependent on TTSGenerator; meaning of viseme number dependent on
 * viseme set, e.g., Disney 13, or IKP
 * 
 * @author Dennis Reidsma
 */
public class FluencyPhonemeToVisemeMapping extends XMLStructureAdapter implements PhonemeToVisemeMapping
{

    private HashMap<Integer, Integer> mappings = new HashMap<Integer, Integer>();
    
    public int getVisemeForPhoneme(int phon)
    {
        if (mappings.get(Integer.valueOf(phon)) != null) return mappings.get(Integer.valueOf(phon)).intValue();
        return -1;
    }

    @Override
    public void decodeContent(XMLTokenizer tokenizer) throws IOException
    {
        while (tokenizer.atSTag())
        {
            String tag = tokenizer.getTagName();
            if (!tag.equals("phoneme")) throw new XMLScanException("Unknown element in PhonemeToVisemeMapping: " + tag);
            HashMap<String, String> attrMap = tokenizer.getAttributes();
            String phoneme = getRequiredAttribute("phoneme", attrMap, tokenizer);

            

            int viseme = getRequiredIntAttribute("viseme", attrMap, tokenizer);
            mappings.put(Integer.valueOf(PhonemeNameToNumber.getPhonemeNumber(phoneme)), Integer.valueOf(viseme));
            tokenizer.takeSTag("phoneme");
            tokenizer.takeETag("phoneme");
        }
    }

    /*
     * The XML Stag for XML encoding
     */
    private static final String XMLTAG = "PhonemeToVisemeMapping";

    /**
     * The XML Stag for XML encoding -- use this static method when you want to see if a given String equals
     * the xml tag for this class
     */
    public static String xmlTag()
    {
        return XMLTAG;
    }

    /**
     * The XML Stag for XML encoding -- use this method to find out the run-time xml tag of an object
     */
    @Override
    public String getXMLTag()
    {
        return XMLTAG;
    }
}
