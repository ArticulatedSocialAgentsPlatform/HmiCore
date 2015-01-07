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


/** 
 * Declares a two-dimensional texture sampler
 * @author Job Zwiers
 */
public class SamplerDEPTH extends ColladaElement {
 
   // attributes: none
 
   // child elements:
   private Source source;  // required
   private String wrapS; // default value: WRAP 
   private String wrapT; // default value: WRAP only for sample3D SamplerDEPTH, SamplerDEPTH, samplerDEPTH
   private String wrapP; // default value: WRAP  only for sample3D, SamplerDEPTH, samplerRECT
   private String minfilter; // optional
   private String magfilter; // optional
   private String mipfilter; // optional
   private BorderColor borderColor;
   private int mipmapMaxlevel; // default 0
   private float mipmapBias;
   private ArrayList<Extra> extras = new ArrayList<Extra>();
   
   public SamplerDEPTH() {
      super();
   }
   
   public SamplerDEPTH(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super(collada);
      readXML(tokenizer);     
   }
 
   @Override
    public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {
      appendXMLStructure(buf, fmt, source);
      appendTextElement(buf, "wrap_s", wrapS, fmt);
      appendTextElement(buf, "wrap_t", wrapT, fmt);
      appendTextElement(buf, "wrap_p", wrapP, fmt);
      appendTextElement(buf, "minfilter", minfilter, fmt);
      appendTextElement(buf, "mipfilter", mipfilter, fmt);
      appendTextElement(buf, "magfilter", magfilter, fmt);
      appendXMLStructure(buf, fmt,  borderColor);
      appendIntElement(buf, "mipmap_maxlevel", mipmapMaxlevel, fmt);
      appendFloatElement(buf, "mipmap_bias", mipmapBias, fmt);
      appendXMLStructureList(buf, fmt, extras);
      return buf;  
   }

   @Override
   public void decodeContent(XMLTokenizer tokenizer) throws IOException {
      while (tokenizer.atSTag()) {
         String tag = tokenizer.getTagName();
         if (tag.equals(Extra.xmlTag()))  {                
                 extras.add(new Extra(getCollada(), tokenizer));  
         } else if (tag.equals("Source.XMLTag")) {
            source = new Source(getCollada(), tokenizer);     
         } else if (tag.equals("wrap_s")) {
            wrapS = tokenizer.takeTextElement("wrap_s");    
         } else if (tag.equals("wrap_t")) {
            wrapT = tokenizer.takeTextElement("wrap_t");  
         } else if (tag.equals("wrap_p")) {
            wrapP = tokenizer.takeTextElement("wrap_p");  
         } else if (tag.equals("minfilter")) {
            minfilter = tokenizer.takeTextElement("minfilter");  
         } else if (tag.equals("magfilter")) {
            magfilter = tokenizer.takeTextElement("magfilter");        
         } else if (tag.equals("mipfilter")) {
            mipfilter = tokenizer.takeTextElement("mipfilter");      
         } else if (tag.equals("border_color")) {
            borderColor = new BorderColor(getCollada(), tokenizer); 
         } else if (tag.equals("mipmap_maxlevel")) {
            mipmapMaxlevel = tokenizer.takeIntElement("mipmap_maxlevel");   
         } else if (tag.equals("mipmap_bias")) {
            mipmapBias = tokenizer.takeFloatElement("mipmap_bias");           
         } else {         
            getCollada().warning(tokenizer.getErrorMessage("SamplerDEPTH: skip : " + tokenizer.getTagName()));
            tokenizer.skipTag();
         }
      }    
      addColladaNode(source);   
      addColladaNode(borderColor);
      addColladaNodes(extras);      
   }
 
   /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "samplerDEPTH";
 
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
