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
package hmi.graphics.collada;

import hmi.xml.XMLFormatting;
import hmi.xml.XMLTokenizer;

import java.io.IOException;

/** 
 * Spot light source, that can occur in a technique_common element.
 * @author Job Zwiers
 */
public class Spot extends ColladaElement {
 
 
   // attributes: none
     
   // child elements:
   private CommonColor color;
   private ConstantAttenuation constantAttenuation;
   private LinearAttenuation linearAttenuation;
   private QuadraticAttenuation quadraticAttenuation;
   private FalloffAngle falloffAngle;
   private FalloffExponent falloffExponent;
   
   
   public Spot() {
      super();
   }
     
   public Spot(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super(collada);
      readXML(tokenizer); 
   }
  
   @Override
   public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {
      appendXMLStructure(buf, fmt, color);
      appendXMLStructure(buf, fmt, constantAttenuation);
      appendXMLStructure(buf, fmt, linearAttenuation);
      appendXMLStructure(buf, fmt, quadraticAttenuation);
      appendXMLStructure(buf, fmt, falloffAngle);
      appendXMLStructure(buf, fmt, falloffExponent);
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
         } else if (tag.equals(FalloffAngle.xmlTag()))  {                
                 falloffAngle = new FalloffAngle(getCollada(), tokenizer); 
         } else if (tag.equals(FalloffExponent.xmlTag()))  {                
                 falloffExponent = new FalloffExponent(getCollada(), tokenizer); 
         } else {         
            getCollada().warning(tokenizer.getErrorMessage("Spot: skip : " + tokenizer.getTagName()));
            tokenizer.skipTag();
         }
      }
      addColladaNode(color);
      addColladaNode(constantAttenuation);
      addColladaNode(linearAttenuation);
      addColladaNode(quadraticAttenuation);
      addColladaNode(falloffAngle);
      addColladaNode(falloffExponent);
   }
 
   /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "spot";
 
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
