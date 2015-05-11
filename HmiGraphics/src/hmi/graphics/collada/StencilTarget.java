/*******************************************************************************
 * The MIT License (MIT)
 * Copyright (c) 2015 University of Twente
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *******************************************************************************/

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
