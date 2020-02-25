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

import hmi.xml.XMLTokenizer;

import java.io.IOException;
import java.util.HashMap;

/** 
 * Color Attribute of fixed-function shader elements inside profile_COMMON effects.
 * @author Job Zwiers
 */
public class Transparent extends CommonColorOrTextureType { 
 
   private String opaque = "A_ONE"; // See Collada spec: common_color_or_texturetype, page 8-25
   // A_ONE (default): take transparency info from alpha channel, where 1.0 is opaque
   // RGB_ZERO: take transparency from RGB, where 0.0 is opaque, each channel modulated independently

   public Transparent() {
      super();
   }
     
   public Transparent(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super(collada); 
      readXML(tokenizer); 
   }
  
   /**
    * returns either &quot;A_ONE&quot; or &quot;RGB_ZERO&quot;
    */
   public String getOpaqueMode() {
      return opaque;
   }
 
    /**
    * appends a String of attributes to buf.
    */
   @Override
   public StringBuilder appendAttributes(StringBuilder buf) {
      super.appendAttributes(buf);
      appendAttribute(buf, "opaque", opaque);
      return buf;
   }


   /**
    * decodes the XML attributes
    */
   @Override
   public void decodeAttributes(HashMap<String, String> attrMap, XMLTokenizer tokenizer) {      
      opaque = getOptionalAttribute("opaque", attrMap, "A_ONE");
      super.decodeAttributes(attrMap, tokenizer);
   }
 
   /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "transparent";
 
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
