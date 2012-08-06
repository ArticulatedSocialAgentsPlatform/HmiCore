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
 * Point light source, that can occur in a technique_common element.
 * @author Job Zwiers
 */
public class Point extends ColladaElement {
 
 
   // attributes: none
     
   // child elements:
   private CommonColor color;
   private ConstantAttenuation constantAttenuation;
   private LinearAttenuation linearAttenuation;
   private QuadraticAttenuation quadraticAttenuation;
   
   public Point() {
      super();
   }
     
   public Point(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super(collada);
      readXML(tokenizer); 
   }
  
   @Override
   public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {
      appendXMLStructure(buf, fmt, color);
      appendXMLStructure(buf, fmt, constantAttenuation);
      appendXMLStructure(buf, fmt, linearAttenuation);
      appendXMLStructure(buf, fmt, quadraticAttenuation);
      return buf;  
   }

   @Override
   public void decodeContent(XMLTokenizer tokenizer) throws IOException {
      while (tokenizer.atSTag()) {
         String tag = tokenizer.getTagName();
         if (tag.equals(CommonColor.xmlTag()))  {                
                 color = new CommonColor(getCollada(), tokenizer); 
         } else if (tag.equals(ConstantAttenuation.xmlTag()))  {                
                 constantAttenuation = new ConstantAttenuation(getCollada(), tokenizer);
         } else if (tag.equals(LinearAttenuation.xmlTag()))  {                
                 linearAttenuation = new LinearAttenuation(getCollada(), tokenizer); 
         } else if (tag.equals(QuadraticAttenuation.xmlTag()))  {                
                 quadraticAttenuation = new QuadraticAttenuation(getCollada(), tokenizer); 
         } else {         
            getCollada().warning(tokenizer.getErrorMessage("Point: skip : " + tokenizer.getTagName()));
            tokenizer.skipTag();
         }
      }
      addColladaNode(color);
      addColladaNode(constantAttenuation);
      addColladaNode(linearAttenuation);
      addColladaNode(quadraticAttenuation);
   }
 
   /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "point";
 
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
}
