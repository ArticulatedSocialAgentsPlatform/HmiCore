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
import java.util.ArrayList;
import java.util.HashMap;


/** 
 * Defines a set of texturing commands for regular and combiner mode.
 * @author Job Zwiers
 */
public class TexturePipeline extends ColladaElement {
 
   // attributes: sid, inherited from ColladaElement, optional for newparam and setparam, not valid for pass
   private String  param;   // optional for pass not valid for newparam and setparam
      
   // child elements:
   private ArrayList<Texcombiner> texcombinerList = new ArrayList<Texcombiner>();
   private ArrayList<Texenv> texenvList = new ArrayList<Texenv>();
   private ArrayList<Extra> extras = new ArrayList<Extra>();
   
   
   public TexturePipeline() {
      super();
   }
   
   public TexturePipeline(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super(collada);
      readXML(tokenizer);  
   }
 
    /**
    * appends a String of attributes to buf.
    */
   @Override
   public StringBuilder appendAttributes(StringBuilder buf) {
      super.appendAttributes(buf);
      appendAttribute(buf, "param", param);
      return buf;
   }

   /**
    * decodes the XML attributes
    */
   @Override
   public void decodeAttributes(HashMap<String, String> attrMap, XMLTokenizer tokenizer) {     
      param = getOptionalAttribute("param", attrMap); 
      super.decodeAttributes(attrMap, tokenizer);
   }
  
   @Override
   public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {
      appendXMLStructureList(buf, fmt, texcombinerList);
      appendXMLStructureList(buf, fmt, texenvList);
      appendXMLStructureList(buf, fmt, extras);
      return buf;  
   }

   @Override
   public void decodeContent(XMLTokenizer tokenizer) throws IOException {
      while (tokenizer.atSTag()) {
         String tag = tokenizer.getTagName();
         if (tag.equals(Texcombiner.xmlTag()))  {                
                 texcombinerList.add(new Texcombiner(getCollada(), tokenizer));
         } else if (tag.equals(Texenv.xmlTag()))  {                
                 texenvList.add(new Texenv(getCollada(), tokenizer));
         } else if (tag.equals(Extra.xmlTag()))  {                
                 extras.add(new Extra(getCollada(), tokenizer));
         } else {         
            getCollada().warning(tokenizer.getErrorMessage("TexturePipeline: skip : " + tokenizer.getTagName()));
            tokenizer.skipTag();
         }
      }    
      addColladaNodes(texcombinerList);
      addColladaNodes(texenvList);
      addColladaNodes(extras);
   }
 
   /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "texture_pipeline";
 
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
