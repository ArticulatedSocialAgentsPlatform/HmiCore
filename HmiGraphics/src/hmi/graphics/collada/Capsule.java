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
 * Cylinder, centered on the Y-axis, capped by half-spheres at both ends
 * 
 *  For now, uses a single radius, like in sphere rather than the 2-valued radius of capsule
 * @author welberge
 */
public class Capsule extends ColladaElement
{
    private Radius radius = new Radius();
    private Height height = new Height();
    
    public Capsule() {
        super();
    }
    
    public Capsule(Collada collada, XMLTokenizer tokenizer) throws IOException {
        super(collada);      
        readXML(tokenizer);  
    }
    
   @Override
    public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {
        appendXMLStructure(buf, fmt, getRadius());
        appendXMLStructure(buf, fmt, getHeight());
        return buf;  
     }

     @Override
    public void decodeContent(XMLTokenizer tokenizer) throws IOException {
        while (tokenizer.atSTag()) {
           String tag = tokenizer.getTagName();
           if (tag.equals(Radius.xmlTag()))   {                
               setRadius(new Radius(getCollada(), tokenizer)); 
           } else if (tag.equals(Height.xmlTag()))  {                
               setHeight(new Height(getCollada(), tokenizer)); 
           }   else   {         
              getCollada().warning(tokenizer.getErrorMessage("Capsule: skip : " + tokenizer.getTagName()));
              tokenizer.skipTag();
           }
        }
        addColladaNode(getRadius());        
     }
   
   /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "capsule";
 
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

public void setHeight(Height height)
{
    this.height = height;
}

public Height getHeight()
{
    return height;
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
