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
import java.util.ArrayList;
import java.util.HashMap;

/** 
 * Alpha portion of a texture_pipeline command (combiner-mode texturing)
 * @author Job Zwiers
 */
public class Alpha extends ColladaElement {
 
   // attributes: 
   private String operator; // optional
   private float scale = -1.0f; // optional . -1: unspecified value
   
   // child elements:
   // 1 to 3 arguments possible
   private ArrayList<Argument> arguments = new ArrayList<Argument>();
 
   public Alpha() {
      super();
   }
   
   public Alpha(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super(collada);    
      readXML(tokenizer); 
   }  

   /**
    * appends a String of attributes to buf.
    */
   @Override
   public StringBuilder appendAttributes(StringBuilder buf) {

      super.appendAttributes(buf);
      appendAttribute(buf, "operator", operator);
      if (scale >= 0) appendAttribute(buf, "scale", scale);
      return buf;
   }

   /**
    * decodes the XML attributes
    */
   @Override
   public void decodeAttributes(HashMap<String, String> attrMap, XMLTokenizer tokenizer) {     
      operator = getOptionalAttribute("operator", attrMap);
      scale    = getOptionalFloatAttribute("scale", attrMap, -1.0f);
      super.decodeAttributes(attrMap, tokenizer);
   }

   @Override
   public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {
      appendXMLStructureList(buf, fmt, arguments);
      return buf;  
   }

   @Override
   public void decodeContent(XMLTokenizer tokenizer) throws IOException {
      while (tokenizer.atSTag()) {
         String tag = tokenizer.getTagName();
         if (tag.equals(Argument.xmlTag()))  {                
                 arguments.add(new Argument(getCollada(), tokenizer));  
         } else {         
            getCollada().warning(tokenizer.getErrorMessage("Alpha: skip : " + tokenizer.getTagName()));
            tokenizer.skipTag();
         }
      }  
      addColladaNodes(arguments);
   }


   /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "alpha";
 
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
