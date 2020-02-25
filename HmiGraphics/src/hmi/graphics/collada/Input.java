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
 * Declares the input semantics of a data source.
 * @author Job Zwiers
 */
public class Input extends ColladaElement {
 
   // attributes: 
   private String semantic;
   private String sourceURL;
   private int offset = -1;  // negative value denotes: undefined value
   private int set = -1;     // negative value denotes: undefined value
   
   
   public Input() {
      super();
   }
   
   public Input(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super(collada);      
      readXML(tokenizer);  
   }
 
 
   /**
    * Returns the semantic attribute
    */
   public String getSemantic() {
      return semantic;
   }
 
 
   /**
    * Returns the source URL  attribute
    */
   public String getSource() {
      return sourceURL;
   }
   
   /**
    * Returns the offset attribute.
    * (-1 if not defined)
    */
   public int getOffset() {
      return offset;
   }
   
   /**
    * Returns the set attribute
    * (-1 if not defined)
    */
   public int getSet() {
      return set;
   }
 
   /**
    * returns false, to denote that Inputs are encoded by means of empty XML elements
    */
   @Override
   public boolean hasContent() { return false; }

   /**
    * appends a String of attributes to buf.
    */
   @Override
   public StringBuilder appendAttributes(StringBuilder buf) {
      super.appendAttributes(buf);
      appendAttribute(buf, "semantic", semantic);
      appendAttribute(buf, "source", sourceURL);
      if (offset >= 0) appendAttribute(buf, "offset", offset);
      if (set >= 0) appendAttribute(buf, "set", set);
      return buf;
   }


   /**
    * decodes the XML attributes
    */
   @Override
   public void decodeAttributes(HashMap<String, String> attrMap, XMLTokenizer tokenizer) {      
      semantic     = getRequiredAttribute("semantic", attrMap, tokenizer);
      sourceURL    = getRequiredAttribute("source", attrMap, tokenizer);
      offset       = getOptionalIntAttribute("offset", attrMap, -1);
      set          = getOptionalIntAttribute("set", attrMap, -1);
      super.decodeAttributes(attrMap, tokenizer);
   }
 
   /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "input";
 
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
