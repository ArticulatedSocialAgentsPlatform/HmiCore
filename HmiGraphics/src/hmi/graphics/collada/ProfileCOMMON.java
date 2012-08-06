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
import java.util.List;

/** 
 * Opens a block of platform-independent declarations for the common, fixed-function shader.
 * @author Job Zwiers
 */
public class ProfileCOMMON extends ColladaElement {
 
   // attributes: id, inherited from ColladaElement
   private String platform; // optional, for profile_CG, profile_GLES
 
   // child elements:
   private Asset asset; // optional
   private ArrayList<ColladaImage> imageList = new ArrayList<ColladaImage>();
   private ArrayList<Newparam> newparamList = new ArrayList<Newparam>();
   private ArrayList<TechniqueFX> techniqueList = new ArrayList<TechniqueFX>(); // required. For ProfileCOMMON: just one allowed 
   private ArrayList<Extra> extras = new ArrayList<Extra>();
   
   public ProfileCOMMON() {
      super();
   }
   
   public ProfileCOMMON(Collada collada) {
      super(collada);   
   }
   
   public ProfileCOMMON(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super(collada);   
      readXML(tokenizer); 
   }

   public List<Newparam> getNewparamList() { return newparamList; }
   
   public List<ColladaImage> getImageList() { return imageList; }
   
   public TechniqueFX getTechniqueFX() { 
      if (techniqueList == null || techniqueList.size() == 0) return null;
      return techniqueList.get(0);
   }

   /**
    * appends a String of attributes to buf.
    */
   @Override
   public StringBuilder appendAttributes(StringBuilder buf) {
      super.appendAttributes(buf);
      appendAttribute(buf, "platform", platform);
      return buf;
   }

   /**
    * decodes the XML attributes
    */
   @Override
   public void decodeAttributes(HashMap<String, String> attrMap, XMLTokenizer tokenizer) {      
      platform   = getOptionalAttribute("platform", attrMap);
      super.decodeAttributes(attrMap, tokenizer);
   }
 
   @Override
   public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {
      appendXMLStructure(buf, fmt, asset);
      appendXMLStructureList(buf, fmt, imageList);
      appendXMLStructureList(buf, fmt, newparamList);
      appendXMLStructureList(buf, fmt, techniqueList);
      appendXMLStructureList(buf, fmt, extras);
      return buf;  
   }

   @Override
   public void decodeContent(XMLTokenizer tokenizer) throws IOException {
      while (tokenizer.atSTag()) {
         decodeElement(tokenizer);
      }      
      addElements();
   }
 
   public void decodeElement(XMLTokenizer tokenizer) throws IOException {
       String tag = tokenizer.getTagName();
         if (tag.equals(Asset.xmlTag()))  {                
             asset = new Asset(getCollada(), tokenizer);    
         } else if (tag.equals(ColladaImage.xmlTag()))  {
             imageList.add(new ColladaImage(getCollada(), tokenizer));              
         } else if (tag.equals(Newparam.xmlTag()))  {
             newparamList.add(new Newparam(getCollada(), tokenizer));  
         } else if (tag.equals(TechniqueFX.xmlTag()))  {                
             techniqueList.add(new TechniqueFX(getCollada(), tokenizer));  
         } else if (tag.equals(Extra.xmlTag()))  {
             extras.add(new Extra(getCollada(), tokenizer)); 
         } else {         
            getCollada().warning(tokenizer.getErrorMessage("Profile_*: skip : " + tokenizer.getTagName()));
            tokenizer.skipTag();
         }       
   }
 
    public void addElements() {
      addColladaNode(asset);
      addColladaNodes(imageList);  
      addColladaNodes(newparamList);
      addColladaNodes(techniqueList);
      addColladaNodes(extras);
    }
 
 
 
   /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "profile_COMMON";
 
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
