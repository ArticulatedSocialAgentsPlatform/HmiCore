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
