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
 * A Float element can appear as child of a CommonFloatOrParamType element.
 * It specifies a float value, possibly with an sid attribute.
 * Also used as base class for elements like xfov etc.
 * @author Job Zwiers
 */
public class ColladaFloat extends ColladaElement {


   private float val;
   
   // attributes: sid, inherited from ColladaElement
   
   public ColladaFloat() {
      super();
   }
   
   /**
    * ColladaFloat with initialized val attribute
    */
   public ColladaFloat(float val) {
      super();
      this.val = val;
   }
   
   public ColladaFloat(Collada collada) {
      super(collada); 
   }
   
   public ColladaFloat(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super(collada); 
      readXML(tokenizer); 
   }
 
   /**
    * Returns the float value
    */
   public float getFloatVal() {
      return val;
   }

   /**
    * Sets the float value
    */
   public void setFloatVal(float val) {
      this.val = val;
   }

   @Override
   public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {
      appendNewLine(buf, fmt.getIndentedTab());
      buf.append(val);
      return buf;  
   }

   @Override
   public void decodeContent(XMLTokenizer tokenizer) throws IOException {
      val = decodeFloat(tokenizer.takeCharData());
   }
 
   
 
   /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "float";
 
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
