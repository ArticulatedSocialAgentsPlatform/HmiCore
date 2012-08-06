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
 * @author Job Zwiers
 */
public class Height extends ColladaElement
{
    private float height = 1;
    
    public Height() {
        super();
    }
    
    public Height(Collada collada, XMLTokenizer tokenizer) throws IOException {
       super(collada);
       readXML(tokenizer); 
    }
  

    @Override
    public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {
        buf.append(getHeight());
        return buf;  
    }

    @Override
    public void decodeContent(XMLTokenizer tokenizer) throws IOException 
    {
        setHeight(decodeFloat(tokenizer.takeCharData()));        
    }
  
    /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "height";
 
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

public void setHeight(float height)
{
    this.height = height;
}

public float getHeight()
{
    return height;
}
}
