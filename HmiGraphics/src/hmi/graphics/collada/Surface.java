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
import java.util.HashMap;


/** 
 * Declares a resource that can be used both as the source for texture samples
 * and as target of a render pass.
 * @author Job Zwiers
 */
public class Surface extends ColladaElement {
 
   // attributes: 
   private String type; // UNTYPED, 1D, 2D, 3D, CUBE, DEPTH, RECT.
 
   
   // child elements:
   private String format; // platform specific texel format, like R8G8B8A8 or sRGB
   private FormatHint formatHint;
   //private float[] size;
   //private float viewportRatio = 1.0f; // default is 1. Not allowed when size is specified
   private int mipLevels;
   private String mipmapGenerate;
   private InitFrom initFrom;
   private InitVolume initVolume;
   
   //private Generator generator; 
   private ArrayList<Extra> extras = new ArrayList<Extra>();
   
   public Surface() {
      super();
   }
   
   public Surface(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super(collada);
      readXML(tokenizer);  
   }
 
   public InitFrom getInitFrom() { return initFrom; }
   
   public InitVolume getInitVolume() { return initVolume; }             
 
   /**
    * appends a String of attributes to buf.
    */
   @Override
   public StringBuilder appendAttributes(StringBuilder buf) {
      super.appendAttributes(buf);
      appendAttribute(buf, "type", type);
      return buf;
   }

   /**
    * decodes the XML attributes
    */
   @Override
   public void decodeAttributes(HashMap<String, String> attrMap, XMLTokenizer tokenizer) {     
      type = getOptionalAttribute("type", attrMap); // required in GLSL
      super.decodeAttributes(attrMap, tokenizer);
   }
 
   @Override
    public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {
      appendTextElement(buf, "format", format, fmt);     
      appendTextElement(buf, "mipmapGenerate", mipmapGenerate, fmt);
      appendXMLStructure(buf, fmt,  formatHint);
      appendXMLStructure(buf, fmt,  initFrom);
      appendXMLStructure(buf, fmt,  initVolume);
      appendIntElement(buf, "mipLevels", mipLevels, fmt);
      appendXMLStructureList(buf, fmt, extras);
      return buf;  
   }

   @Override
   public void decodeContent(XMLTokenizer tokenizer) throws IOException {
      while (tokenizer.atSTag()) {
         String tag = tokenizer.getTagName();
         if (tag.equals(Extra.xmlTag()))  {                
                 extras.add(new Extra(getCollada(), tokenizer));  
         } else if (tag.equals("FormatHint.XMLTag")) {
            formatHint = new FormatHint(getCollada(), tokenizer);     
         } else if (tag.equals("format")) {
            format = tokenizer.takeTextElement("format");    
         } else if (tag.equals("mipmapGenerate")) {
            mipmapGenerate = tokenizer.takeTextElement("mipmapGenerate");  
         } else if (tag.equals("mipLevels")) {
            mipLevels = tokenizer.takeIntElement("mipLevels");   
         } else if (tag.equals(InitFrom.xmlTag())) {
            initFrom = new InitFrom(getCollada(), tokenizer);  
         } else if (tag.equals(InitVolume.xmlTag())) {
            initVolume = new InitVolume(getCollada(), tokenizer);  
         } else {         
            getCollada().warning(tokenizer.getErrorMessage("Surface: skip : " + tokenizer.getTagName()));
            tokenizer.skipTag();
         }
      }    
      addColladaNode(formatHint);
      addColladaNode(initFrom);
      addColladaNode(initVolume);
      addColladaNodes(extras);      
   }
 
   /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "surface";
 
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
