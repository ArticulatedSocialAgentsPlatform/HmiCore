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
 * Collision sphere, centered at (0,0,0), with radius radius 
 * @author welberge
 */
public class Sphere extends ColladaElement
{
    private Radius radius = new Radius();
    
    public Sphere() {
        super();
    }
    
    public Sphere(Collada collada,  XMLTokenizer tokenizer) throws IOException {
        super(collada);
        readXML(tokenizer);   
    }
    
    @Override
    public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {
        appendXMLStructure(buf, fmt, getRadius());
        return buf;  
    }

    @Override
    public void decodeContent(XMLTokenizer tokenizer) throws IOException {
        while (tokenizer.atSTag()) {
           String tag = tokenizer.getTagName();
           if (tag.equals(Radius.xmlTag()))  
           {                
               setRadius(new Radius(getCollada(), tokenizer)); 
           }
           else 
           {         
              getCollada().warning(tokenizer.getErrorMessage("Sphere: skip : " + tokenizer.getTagName()));
              tokenizer.skipTag();
           }
        }
        addColladaNode(getRadius());        
    }
   
     /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "sphere";
 
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

public void setRadius(Radius radius)
{
    this.radius = radius;
}

public Radius getRadius()
{
    return radius;
}
}
