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
 * @author Job Zwiers
 */
public class TechniqueCommon extends ColladaElement {
   
   private Orthographic orthographic;
   private Perspective perspective;    
   private Directional directional;
       
       
   public TechniqueCommon() {
      super();
   }
   

   public TechniqueCommon(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super(collada);
      readXML(tokenizer); 
   }

   /**
    * decodes the XML attributes
    */
   @Override
   public void decodeAttributes(HashMap<String, String> attrMap, XMLTokenizer tokenizer) {      
      String prof   = getOptionalAttribute("profile", attrMap); // a null value will be interpreted as "COMMON"
      if (prof != null && ! prof.equals("COMMON") ) {  
         getCollada().warning("common_profile with profile attribute: " + prof + "  (ignored)");
      }
      super.decodeAttributes(attrMap, tokenizer);
   }
 

   @Override
   public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {
      appendXMLStructure(buf, fmt, orthographic);
      appendXMLStructure(buf, fmt, perspective);
      appendXMLStructure(buf, fmt, directional);
      return buf;  
   }

   @Override
   public void decodeContent(XMLTokenizer tokenizer) throws IOException {
      while (tokenizer.atSTag()) {
         String tag = tokenizer.getTagName();
         if (tag.equals(Orthographic.xmlTag()))  {                
                 orthographic = new Orthographic(getCollada(), tokenizer);             
         } else if (tag.equals(Perspective.xmlTag()))  {                
                 perspective = new Perspective(getCollada(), tokenizer);  
         } else if (tag.equals(Directional.xmlTag()))  {                
                 directional = new Directional(getCollada(), tokenizer);          
         } else {     
            super.decodeContent(tokenizer);    
            getCollada().warning(tokenizer.getErrorMessage("TechniqueCommon: skip : " + tokenizer.getTagName()));
            tokenizer.skipTag();
         }
      }
      addColladaNode(orthographic);   
      addColladaNode(perspective); 
      addColladaNode(directional); 
      
   }


   /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "technique_common";
 
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
