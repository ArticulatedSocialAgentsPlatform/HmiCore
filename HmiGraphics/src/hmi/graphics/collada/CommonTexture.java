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
 * Storage of graphical representations, like raster data.
 * @author Job Zwiers
 */
public class CommonTexture extends ColladaElement {
 
   // attributes: id, name inherited from ColladaElement
   private String sampler2D; 
   private String texCoord = null;
   
   
   // child elements:
   private Extra extra;         // optional
   
   
   public CommonTexture() {
      super();
   }
   
   public CommonTexture(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super(collada);
      readXML(tokenizer); 
   }
 
   /**
    * appends the XML content
    */
   @Override
   public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {  
      appendXMLStructure(buf, fmt, extra); 
      return buf;
   }

   /**
    * decodes the XML content
    */
   @Override
   public void decodeContent(XMLTokenizer tokenizer) throws IOException {
      while (tokenizer.atSTag()) {
         String tag = tokenizer.getTagName();
         if (tag.equals(Extra.xmlTag()))  {     // could include <technique profile="Maya">, for instance           
            extra = new Extra(getCollada(), tokenizer);               
         } else {         
            getCollada().warning(tokenizer.getErrorMessage("CommonTexture: skip : " + tokenizer.getTagName()));
            tokenizer.skipTag();
         }
      }
      addColladaNode(extra);
   }
   
   /**
    * Returns the texture attribute, that refers to an Sampler2D actually.
    */
   public String getSampler2D() {
      return sampler2D;
   }
   
   /**
    * Returns tghe texCoord attribute
    */
   public String getTexCoord() {
      return texCoord;
   }
   
   public Max3DProfile getMax3DProfile() { return extra==null ? null : extra.getMax3DProfile();}
   
   public FColladaProfile getFColladaProfile() { return extra==null ? null : extra.getFColladaProfile();}
   
   public MayaProfile getMayaProfile() {  return extra==null ? null : extra.getMayaProfile();}
   
   public RenderMonkeyProfile getRenderMonkeyProfileProfile() { return extra==null ? null : extra.getRenderMonkeyProfileProfile();}
   

   /**
    * appends a String of attributes to buf.
    */
   @Override
   public StringBuilder appendAttributes(StringBuilder buf) {
      super.appendAttributes(buf);
      appendAttribute(buf, "texture", sampler2D);
      appendAttribute(buf, "texCoord", texCoord);
      return buf;
   }


   /**
    * decodes the XML attributes
    */
   @Override
   public void decodeAttributes(HashMap<String, String> attrMap, XMLTokenizer tokenizer) {
      sampler2D = getRequiredAttribute("texture", attrMap, tokenizer);
      texCoord = getRequiredAttribute("texcoord", attrMap, tokenizer);
      super.decodeAttributes(attrMap, tokenizer);
   }

   /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "texture";
 
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
