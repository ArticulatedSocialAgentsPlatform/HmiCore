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
import java.util.HashMap;

/** 
 * @author Job Zwiers
 */
public class TechniqueCommon extends ColladaElement {
   
   private Orthographic orthographic;
   private Perspective perspective;    
   private Directional directional;
       
       
   public TechniqueCommon() {
      super();
   }
   

   public TechniqueCommon(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super(collada);
      readXML(tokenizer); 
   }

   /**
    * decodes the XML attributes
    */
   @Override
   public void decodeAttributes(HashMap<String, String> attrMap, XMLTokenizer tokenizer) {      
      String prof   = getOptionalAttribute("profile", attrMap); // a null value will be interpreted as "COMMON"
      if (prof != null && ! prof.equals("COMMON") ) {  
         getCollada().warning("common_profile with profile attribute: " + prof + "  (ignored)");
      }
      super.decodeAttributes(attrMap, tokenizer);
   }
 

   @Override
   public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {
      appendXMLStructure(buf, fmt, orthographic);
      appendXMLStructure(buf, fmt, perspective);
      appendXMLStructure(buf, fmt, directional);
      return buf;  
   }

   @Override
   public void decodeContent(XMLTokenizer tokenizer) throws IOException {
      while (tokenizer.atSTag()) {
         String tag = tokenizer.getTagName();
         if (tag.equals(Orthographic.xmlTag()))  {                
                 orthographic = new Orthographic(getCollada(), tokenizer);             
         } else if (tag.equals(Perspective.xmlTag()))  {                
                 perspective = new Perspective(getCollada(), tokenizer);  
         } else if (tag.equals(Directional.xmlTag()))  {                
                 directional = new Directional(getCollada(), tokenizer);          
         } else {     
            super.decodeContent(tokenizer);    
            getCollada().warning(tokenizer.getErrorMessage("TechniqueCommon: skip : " + tokenizer.getTagName()));
            tokenizer.skipTag();
         }
      }
      addColladaNode(orthographic);   
      addColladaNode(perspective); 
      addColladaNode(directional); 
      
   }


   /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "technique_common";
 
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
