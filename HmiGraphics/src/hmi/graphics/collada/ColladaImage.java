/*******************************************************************************
 * Copyright (C) 2009-2020 Human Media Interaction, University of Twente, the Netherlands
 *
 * This file is part of the Articulated Social Agents Platform BML realizer (ASAPRealizer).
 *
 * ASAPRealizer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License (LGPL) as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ASAPRealizer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ASAPRealizer.  If not, see http://www.gnu.org/licenses/.
 ******************************************************************************/
package hmi.graphics.collada;

import hmi.xml.XMLFormatting;
import hmi.xml.XMLTokenizer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/** 
 * Storage of graphical representations, like raster data.
 * @author Job Zwiers
 */
public class ColladaImage extends ColladaElement {
 
   // attributes: id, name inherited from ColladaElement
   private String format; // optional image format
   private int height = -1;  // negative values denote unspecified values
   private int width = -1;
   private int depth = 1; // default value, also for 2D images
  
   
   // child elements:
   private Asset asset;         // optional
   private Data data;           // either Data or initFrom must be present (not both)
   private String initFrom;
   private ArrayList<Extra> extras = new ArrayList<Extra>();
   
   
   public ColladaImage() {
      super();
   }
   
   public ColladaImage(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super(collada);
      readXML(tokenizer); 
   }
 
   public String getInitFrom() {
      return initFrom;
   }
 
   /**
    * appends the XML content
    */
   @Override
   public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {  
      appendXMLStructure(buf, fmt, asset); 
      appendXMLStructure(buf, fmt, data);
      appendTextElement(buf, "init_from", initFrom, fmt);   
      appendXMLStructureList(buf, fmt, extras); 
      return buf;
   }

   /**
    * decodes the XML content
    */
   @Override
   public void decodeContent(XMLTokenizer tokenizer) throws IOException {
      while (tokenizer.atSTag()) {
         String tag = tokenizer.getTagName();
         if (tag.equals(Asset.xmlTag()))  {                
                 asset = new Asset(getCollada(), tokenizer);             
         } else if (tag.equals(Data.xmlTag()))  {                
                 data = new Data(getCollada(), tokenizer);   
         } else if (tag.equals("init_from"))  {                
                 initFrom = tokenizer.takeTextElement("init_from");   
         } else if (tag.equals(Extra.xmlTag()))  {                
                 extras.add(new Extra(getCollada(), tokenizer));      
         } else {         
            getCollada().warning(tokenizer.getErrorMessage("ColladaImage: skip : " + tokenizer.getTagName()));
            tokenizer.skipTag();
         }
      }
      addColladaNode(asset);
      addColladaNode(data);      
      addColladaNodes(extras);
   }
   
   

   /**
    * appends a String of attributes to buf.
    */
@Override
   public StringBuilder appendAttributes(StringBuilder buf) {
      super.appendAttributes(buf);
      appendAttribute(buf, "format", format);
      if (height >=0) appendAttribute(buf, "height", height);
      if (width >=0)  appendAttribute(buf, "width", width);
      if (depth != 1) appendAttribute(buf, "depth", depth);  
      return buf;
   }


   /**
    * decodes the XML attributes
    */
   @Override
   public void decodeAttributes(HashMap<String, String> attrMap, XMLTokenizer tokenizer) {
      format = getOptionalAttribute("format", attrMap);
      height = getOptionalIntAttribute("height", attrMap, -1);
      width = getOptionalIntAttribute("width", attrMap, -1);
      depth = getOptionalIntAttribute("depth", attrMap, 1);
      super.decodeAttributes(attrMap, tokenizer);
   }


 
   /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "image";
 
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
