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
 * Describes the field of view of an orthographic camera.
 * @author Job Zwiers
 */
public class Orthographic extends ColladaElement {
 
   // attributes: none
   
   // child elements
   private XMag         xmag;
   private YMag         ymag;
   private AspectRatio  aspectRatio;
   private ZNear        znear;
   private ZFar         zfar;
  
     
   public Orthographic(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super(collada);
      readXML(tokenizer);  
   }
 
   @Override
   public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {
      appendXMLStructure(buf, fmt, xmag);
      appendXMLStructure(buf, fmt, ymag);
      appendXMLStructure(buf, fmt, aspectRatio);
      appendXMLStructure(buf, fmt, znear);
      appendXMLStructure(buf, fmt, zfar);
      return buf;  
   }

   @Override
   public void decodeContent(XMLTokenizer tokenizer) throws IOException {
      while (tokenizer.atSTag()) {
         String tag = tokenizer.getTagName();
         if (tag.equals(XMag.xmlTag()))  {                
                 xmag = new XMag(getCollada(), tokenizer);             
         } else if (tag.equals(YMag.xmlTag()))  {                
                 ymag = new YMag(getCollada(), tokenizer);  
         } else if (tag.equals(AspectRatio.xmlTag()))  {                
                 aspectRatio = new AspectRatio(getCollada(), tokenizer);  
         } else if (tag.equals(ZNear.xmlTag()))  {                
                 znear = new ZNear(getCollada(), tokenizer);  
         } else if (tag.equals(ZFar.xmlTag()))  {                
                 zfar = new ZFar(getCollada(), tokenizer);  
         } else {         
            getCollada().warning(tokenizer.getErrorMessage("Orthographic: skip : " + tokenizer.getTagName()));
            tokenizer.skipTag();
         }
      }
      addColladaNode(xmag);   
      addColladaNode(ymag); 
      addColladaNode(aspectRatio); 
      addColladaNode(znear); 
      addColladaNode(zfar);   
   }
 
   /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "orthographic";
 
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
