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
 * Specifies which surface will receive stencil information from the output of this pass.
 * @author Job Zwiers
 */
public class StencilTarget extends ColladaElement {
   
   // attributes: 
   private int index;   // which of multiple render targets is being set. default 0. optional
   private int slice;   // indexes a subimage in a target surface default 0. optional
   private int mip;     // default 0. optional
   private String face; // enumeration: POSITIVE_X, NEGATIVE_X, ... NEGATIVE_Z. default : POSITIVE_X; optional.
      
   // content:
   private String surface; 
      
   public StencilTarget() {
      super();
   }
   
   public StencilTarget(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super(collada);
      readXML(tokenizer); 
   }
 
   /**
    * appends a String of attributes to buf.
    */
   @Override
   public StringBuilder appendAttributes(StringBuilder buf) {
      super.appendAttributes(buf);
      if (index != 0) appendAttribute(buf, "index", index);
      if (slice != 0) appendAttribute(buf, "slice", slice);
      if (mip != 0) appendAttribute(buf, "mip", mip);  
      appendAttribute(buf, "face", face);       
      return buf;
   }

   /**
    * decodes the XML attributes
    */
   @Override
   public void decodeAttributes(HashMap<String, String> attrMap, XMLTokenizer tokenizer) {     
      index = getOptionalIntAttribute("index", attrMap, 0);
      slice = getOptionalIntAttribute("slice", attrMap, 0);
      mip = getOptionalIntAttribute("mip", attrMap, 0);
      face = getOptionalAttribute("face", attrMap);
      super.decodeAttributes(attrMap, tokenizer);
   }
 
   @Override
   public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {
      appendNewLine(buf, fmt);
      buf.append(surface);
      return buf;  
   }
   
   @Override
   public void decodeContent(XMLTokenizer tokenizer) throws IOException {
      surface = tokenizer.takeTrimmedCharData();
   }
 
   /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "stencil_target";
 
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
