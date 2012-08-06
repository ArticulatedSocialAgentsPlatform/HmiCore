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
import java.util.HashMap;

/** 
 * An array of integers.
 * The data is available as a public attribute &quot;ints&quot; of type int[]
 * the number of integers (i.e. the length of the ints array) is available
 * as public attribute &quot;count&quot; 
 * @author Job Zwiers
 */
public class IntArray extends ColladaElement {

   private static final int DEFAULT_MIN_INCLUSIVE = -2147483648;
   private static final int DEFAULT_MAX_INCLUSIVE = 2147483647;
   //private static final int DEFAULT_MAGNITUDE = 38;
   private static final int NR_OF_INTS_PER_LINE = 3;
      
   private int count; 
   private int minInclusive = DEFAULT_MIN_INCLUSIVE;
   private int maxInclusive = DEFAULT_MAX_INCLUSIVE;
   private int[] ints;
   
   
   public IntArray() {
      super();
   }
   
   public IntArray(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super(collada);
      readXML(tokenizer); 
   }
 
   /**
    * appends a String of attributes to buf.
    */
   @Override
   public StringBuilder appendAttributes(StringBuilder buf) {
      super.appendAttributes(buf);
      appendAttribute(buf, "count", count);
      if (minInclusive != DEFAULT_MIN_INCLUSIVE) appendAttribute(buf, "minInclusive", minInclusive);   
      if (maxInclusive != DEFAULT_MAX_INCLUSIVE) appendAttribute(buf, "maxInclusive", maxInclusive);     
      return buf;
   }


   /**
    * decodes the XML attributes
    */
   @Override
   public void decodeAttributes(HashMap<String, String> attrMap, XMLTokenizer tokenizer) {  
      count   = getRequiredIntAttribute("count", attrMap, tokenizer);    
      minInclusive  = getOptionalIntAttribute("minInclusive", attrMap, DEFAULT_MIN_INCLUSIVE);  
      maxInclusive  = getOptionalIntAttribute("maxInclusive", attrMap, DEFAULT_MAX_INCLUSIVE);  
      super.decodeAttributes(attrMap, tokenizer);
   }

   @Override
   public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {
      appendNewLine(buf, fmt);
      appendInts(buf, ints, ' ', fmt, NR_OF_INTS_PER_LINE);
      return buf;  
   }

   @Override
   public void decodeContent(XMLTokenizer tokenizer) throws IOException {
      ints = new int[count];
      decodeIntArray(tokenizer.takeCharData(), ints);
      getCollada().addIntArray(getId(), ints);
   }


 
   /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "int_array";
 
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
