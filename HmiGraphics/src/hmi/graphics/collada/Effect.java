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
import java.util.List;

/** 
 * Provides a self-contained description of a Collada effect.
 * @author Job Zwiers
 */
public class Effect extends ColladaElement {
 
   // attributes: id, name, inherited from ColladaElement
 
   // child elements:
   private Asset asset;
   private ArrayList<Annotate> annotateList = new ArrayList<Annotate>();
   private ArrayList<ColladaImage> imageList= new ArrayList<ColladaImage>(); // images no longer allowed in Collada 1.5
   private ArrayList<Newparam> newparamList= new ArrayList<Newparam>();
   private ArrayList<ProfileCOMMON> profileCommonList= new ArrayList<ProfileCOMMON>();
   private ArrayList<ProfileCG> profileCGList= new ArrayList<ProfileCG>();
   private ArrayList<ProfileGLSL> profileGLSLList= new ArrayList<ProfileGLSL>();
   private ArrayList<Extra> extras = new ArrayList<Extra>();
   
   public Effect() {
      super();
   }
   
   public Effect(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super(collada);    
      readXML(tokenizer); 
   }
   
   public List<Newparam> getNewparamList() { return newparamList; }
   
   public List<ProfileCOMMON> getProfileCOMMONList() { return profileCommonList; }
   public List<ProfileGLSL> getProfileGLSLList() { return profileGLSLList; }
   
 
   @Override
   public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {
      appendXMLStructure(buf, fmt, asset);
      appendXMLStructureList(buf, fmt, annotateList);
      appendXMLStructureList(buf, fmt, imageList);
      appendXMLStructureList(buf, fmt, newparamList);
      appendXMLStructureList(buf, fmt, profileCommonList);
      appendXMLStructureList(buf, fmt, profileCGList);
      appendXMLStructureList(buf, fmt, profileGLSLList);
      appendXMLStructureList(buf, fmt, extras);
      return buf;  
   }

   @Override
   public void decodeContent(XMLTokenizer tokenizer) throws IOException {
      while (tokenizer.atSTag()) {
         String tag = tokenizer.getTagName();
         if (tag.equals(Asset.xmlTag()))  {                
                 asset = new Asset(getCollada(), tokenizer);  
         } else if (tag.equals(Annotate.xmlTag()))  {                
                 annotateList.add(new Annotate(getCollada(), tokenizer)); 
         } else if (tag.equals(ColladaImage.xmlTag()))  {                
                 imageList.add(new ColladaImage(getCollada(), tokenizer)); 
         } else if (tag.equals(Newparam.xmlTag()))  {                
                 newparamList.add(new Newparam(getCollada(), tokenizer));  
         } else if (tag.equals(ProfileCOMMON.xmlTag()))  {                
                 profileCommonList.add(new ProfileCOMMON(getCollada(), tokenizer));  
         } else if (tag.equals(ProfileCG.xmlTag()))  {                
                 profileCGList.add(new ProfileCG(getCollada(), tokenizer));  
         } else if (tag.equals(ProfileGLSL.xmlTag()))  {                
                 profileGLSLList.add(new ProfileGLSL(getCollada(), tokenizer));
         } else if (tag.equals(Extra.xmlTag()))  {                
                 extras.add(new Extra(getCollada(), tokenizer)); 
         } else {         
            getCollada().warning(tokenizer.getErrorMessage("Effect: skip : " + tokenizer.getTagName()));
            tokenizer.skipTag();
         }
      }  
      addColladaNode(asset); 
      addColladaNodes(annotateList);
      addColladaNodes(imageList); 
      addColladaNodes(newparamList); 
      addColladaNodes(profileCommonList); 
      addColladaNodes(profileCGList); 
      addColladaNodes(profileGLSLList); 
      addColladaNodes(extras);          
   }

   /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "effect";
 
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
