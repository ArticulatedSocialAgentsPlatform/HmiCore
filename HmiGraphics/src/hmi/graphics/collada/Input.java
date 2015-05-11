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
