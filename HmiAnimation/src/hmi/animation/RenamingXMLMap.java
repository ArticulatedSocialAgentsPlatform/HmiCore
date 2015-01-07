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
