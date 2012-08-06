/*******************************************************************************
 * Copyright (C) 2009 Human Media Interaction, University of Twente, the Netherlands
 * 
 * This file is part of the Elckerlyc BML realizer.
 * 
 * Elckerlyc is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Elckerlyc is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Elckerlyc.  If not, see http://www.gnu.org/licenses/.
 ******************************************************************************/
package hmi.graphics.collada;

import hmi.xml.XMLFormatting;
import hmi.xml.XMLTokenizer;

import java.io.IOException;

/**
 * Collision box, centered at (0,0,0), size is given in half extends on each axis 
 * @author welberge
 */
public class Box extends ColladaElement
{
    private HalfExtends halfExtends = null;
    
    public Box() {
        super();
        setHalfExtends(new HalfExtends());        
    }
    
    public Box(Collada collada, XMLTokenizer tokenizer) throws IOException {
        super(collada);   
        readXML(tokenizer); 
    }
    
   @Override
    public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {
        appendXMLStructure(buf, fmt, getHalfExtends());
        return buf;  
     }

     @Override
    public void decodeContent(XMLTokenizer tokenizer) throws IOException {
        while (tokenizer.atSTag()) {
           String tag = tokenizer.getTagName();
           if (tag.equals(HalfExtends.xmlTag())) {                
               setHalfExtends(new HalfExtends(getCollada(), tokenizer)); 
           } else {         
              getCollada().warning(tokenizer.getErrorMessage("Box: skip : " + tokenizer.getTagName()));
              tokenizer.skipTag();
           }
        }
        addColladaNode(getHalfExtends());        
     }
   
   /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "box";
 
   /**
    * The XML Stag for XML encoding
    */
   public static String xmlTag() { return XMLTAG; }
 
   /**
    * returns the XML Stag for XML encoding
    */
   @Override
   public String getXMLTag() {
      return XMLTAG;
   }

public void setHalfExtends(HalfExtends halfExtends)
{
    this.halfExtends = halfExtends;
}

public HalfExtends getHalfExtends()
{
    return halfExtends;
}
}
