/*******************************************************************************
 * Copyright (C) 2009 Human Media Interaction, University of Twente, the Netherlands
 * 
 * This file is part of the Elckerlyc BML realizer.
 * 
 * Elckerlyc is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Elckerlyc is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Elckerlyc.  If not, see http://www.gnu.org/licenses/.
 ******************************************************************************/

package hmi.graphics.collada;

import hmi.xml.XMLFormatting;
import hmi.xml.XMLTokenizer;

import java.io.IOException;
import java.util.ArrayList;


/** 
 * Declares a two-dimensional texture sampler
 * @author Job Zwiers
 */
public class SamplerRECT extends ColladaElement {
 
   // attributes: none
 
   
   // child elements:
   private Source source;  // required
   private String wrapS; // default value: WRAP 
   private String wrapT; // default value: WRAP 
   private String wrapP; // default value: WRAP  
   private String minfilter; // optional
   private String magfilter; // optional
   private String mipfilter; // optional
   private BorderColor borderColor;
   private int mipmapMaxlevel; // default 0
   private float mipmapBias;
   private ArrayList<Extra> extras = new ArrayList<Extra>();
   
   public SamplerRECT() {
      super();
   }
   
   public SamplerRECT(Collada collada, XMLTokenizer tokenizer) throws IOException {
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
            getCollada().warning(tokenizer.getErrorMessage("SamplerRECT: skip : " + tokenizer.getTagName()));
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
   private static final String XMLTAG = "samplerRECT";
 
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
