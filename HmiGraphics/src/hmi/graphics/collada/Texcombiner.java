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
 * Defines a texture_pipeline command
 * @author Job Zwiers
 */
public class Texcombiner extends ColladaElement {
 
   // attributes: none
   // child elements:
   private Constant constant;
   private RGB rgb;
   private Alpha alpha;
   
   public Texcombiner() {
      super();
   }
   
   public Texcombiner(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super(collada);
      readXML(tokenizer);    
   }
 
   @Override
   public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {
      appendXMLStructure(buf, fmt, constant); 
      appendXMLStructure(buf, fmt, rgb); 
      appendXMLStructure(buf, fmt, alpha); 
      return buf;  
   }

   @Override
   public void decodeContent(XMLTokenizer tokenizer) throws IOException {
      while (tokenizer.atSTag()) {
         String tag = tokenizer.getTagName();
         if (tag.equals(Constant.xmlTag()))  {                
                 constant = new Constant(getCollada(), tokenizer);
         } else if (tag.equals("RGB.XMLTag")) {
            rgb = new RGB(getCollada(), tokenizer);     
         } else if (tag.equals("Alpha.XMLTag")) {
            alpha = new Alpha(getCollada(), tokenizer);     
         } else {         
            getCollada().warning(tokenizer.getErrorMessage("Texcombiner: skip : " + tokenizer.getTagName()));
            tokenizer.skipTag();
         }
      }    
      addColladaNode(constant);
      addColladaNode(rgb);    
      addColladaNode(alpha);   
   }
 
   /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "texcombiner";
 
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
