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
 * Spot light source, that can occur in a technique_common element.
 * @author Job Zwiers
 */
public class Spot extends ColladaElement {
 
 
   // attributes: none
     
   // child elements:
   private CommonColor color;
   private ConstantAttenuation constantAttenuation;
   private LinearAttenuation linearAttenuation;
   private QuadraticAttenuation quadraticAttenuation;
   private FalloffAngle falloffAngle;
   private FalloffExponent falloffExponent;
   
   
   public Spot() {
      super();
   }
     
   public Spot(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super(collada);
      readXML(tokenizer); 
   }
  
   @Override
   public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {
      appendXMLStructure(buf, fmt, color);
      appendXMLStructure(buf, fmt, constantAttenuation);
      appendXMLStructure(buf, fmt, linearAttenuation);
      appendXMLStructure(buf, fmt, quadraticAttenuation);
      appendXMLStructure(buf, fmt, falloffAngle);
      appendXMLStructure(buf, fmt, falloffExponent);
      return buf;  
   }

   @Override
   public void decodeContent(XMLTokenizer tokenizer) throws IOException {
      while (tokenizer.atSTag()) {
         String tag = tokenizer.getTagName();
         if (tag.equals(CommonColor.xmlTag()))  {                
                 color = new CommonColor(getCollada(), tokenizer); 
         } else if (tag.equals(ConstantAttenuation.xmlTag()))  {                
                 constantAttenuation = new ConstantAttenuation(getCollada(), tokenizer);
         } else if (tag.equals(LinearAttenuation.xmlTag()))  {                
                 linearAttenuation = new LinearAttenuation(getCollada(), tokenizer); 
         } else if (tag.equals(QuadraticAttenuation.xmlTag()))  {                
                 quadraticAttenuation = new QuadraticAttenuation(getCollada(), tokenizer); 
         } else if (tag.equals(FalloffAngle.xmlTag()))  {                
                 falloffAngle = new FalloffAngle(getCollada(), tokenizer); 
         } else if (tag.equals(FalloffExponent.xmlTag()))  {                
                 falloffExponent = new FalloffExponent(getCollada(), tokenizer); 
         } else {         
            getCollada().warning(tokenizer.getErrorMessage("Spot: skip : " + tokenizer.getTagName()));
            tokenizer.skipTag();
         }
      }
      addColladaNode(color);
      addColladaNode(constantAttenuation);
      addColladaNode(linearAttenuation);
      addColladaNode(quadraticAttenuation);
      addColladaNode(falloffAngle);
      addColladaNode(falloffExponent);
   }
 
   /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "spot";
 
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
