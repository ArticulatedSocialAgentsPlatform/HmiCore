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

/** 
 * Declaration of generic control information, for either Skins or Morphs.
 * @author Job Zwiers
 */
public class Controller extends ColladaElement {
    
   private Asset asset;
   private Skin skin; // either skin or morph will be defined.
   private Morph morph;
   private ArrayList<Extra> extras;
   

   /**
    * Default constructor
    */   
   public Controller() {
      super();
   }

   /**
    * Constructor used to create a Controller Object from XML
    */   
   public Controller(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super(collada);
      readXML(tokenizer); 
   }


   public Skin getSkin() {
       return skin;  
   }
 
   public Morph getMorph() {
       return morph;  
   }
 
   @Override
   public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {
      appendXMLStructure(buf, fmt, asset);
      appendXMLStructure(buf, fmt, skin);
      appendXMLStructure(buf, fmt, morph);
      appendXMLStructureList(buf, fmt, extras);
      return buf;  
   }

   @Override
   public void decodeContent(XMLTokenizer tokenizer) throws IOException {
      while (tokenizer.atSTag()) {
         String tag = tokenizer.getTagName();
         if (tag.equals(Asset.xmlTag()))  {                
                 asset = new Asset(getCollada(), tokenizer);  
         } else if (tag.equals(Skin.xmlTag()))  {                
                 skin = new Skin(getCollada(), tokenizer); addColladaNode(skin);  
         } else if (tag.equals(Morph.xmlTag()))  {                
                 morph = new Morph(getCollada(), tokenizer);  
         } else if (tag.equals(Extra.xmlTag()))  {                
                 extras.add(new Extra(getCollada(), tokenizer));    
         } else {         
            getCollada().warning(tokenizer.getErrorMessage("Controller: skip : " + tokenizer.getTagName()));
            tokenizer.skipTag();
         }
      }    
      addColladaNode(asset); 
      addColladaNode(skin); 
      addColladaNode(morph); 
      addColladaNodes(extras);      
   }

 
   /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "controller";
 
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
