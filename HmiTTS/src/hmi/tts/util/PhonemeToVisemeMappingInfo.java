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