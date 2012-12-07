package hmi.animation;

import hmi.xml.XMLStructureAdapter;
import hmi.xml.XMLTokenizer;

import java.io.IOException;
import java.util.HashMap;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableBiMap;

import lombok.Getter;

/**
 * Creates a renaming map from XML<br>
 * @author hvanwelbergen
 */
public class RenamingXMLMap extends XMLStructureAdapter
{
    private BiMap<String, String> renamingMap = HashBiMap.create();
    
    public ImmutableBiMap<String,String> getRenamingMap()
    {
        return ImmutableBiMap.copyOf(renamingMap);
    }
    
    private static class Rename extends XMLStructureAdapter
    {
        @Getter
        String src;
        @Getter
        String dst;
        
        @Override
        public void decodeAttributes(HashMap<String, String> attrMap, XMLTokenizer tokenizer)
        {
            src = getRequiredAttribute("src", attrMap, tokenizer);
            dst = getRequiredAttribute("dst", attrMap, tokenizer);            
        }
        
        private static final String XMLTAG = "rename";

        public static String xmlTag()
        {
            return XMLTAG;
        }

        @Override
        public String getXMLTag()
        {
            return XMLTAG;
        }
        
        public static final String BMLNAMESPACE = "http://www.asap-project.org/renaming";

        @Override
        public String getNamespace()
        {
            return BMLNAMESPACE;
        }
    }
    
    @Override
    public void decodeContent(XMLTokenizer tokenizer) throws IOException
    {
        while (tokenizer.atSTag())
        {
            if(tokenizer.getTagName().equals(Rename.xmlTag()))
            {
                Rename ren = new Rename();
                ren.readXML(tokenizer);
                renamingMap.put(ren.getSrc(),ren.getDst());
            }
            else
            {
                throw new RuntimeException("Unkown content tag in CompoundController XML");
            }
        }
    }
    
    private static final String XMLTAG = "renamingMap";

    public static String xmlTag()
    {
        return XMLTAG;
    }

    @Override
    public String getXMLTag()
    {
        return XMLTAG;
    }
    
    public static final String BMLNAMESPACE = "http://www.asap-project.org/renaming";

    @Override
    public String getNamespace()
    {
        return BMLNAMESPACE;
    }
}
