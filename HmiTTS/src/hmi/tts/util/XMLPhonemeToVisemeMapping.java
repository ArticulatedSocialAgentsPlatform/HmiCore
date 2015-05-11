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

import hmi.xml.*;

import java.util.HashMap;
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
public class XMLPhonemeToVisemeMapping extends XMLStructureAdapter implements PhonemeToVisemeMapping
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
            mappings.put(Integer.valueOf(PhonemeUtil.phonemeStringToInt(phoneme)), Integer.valueOf(viseme));
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
