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
 * Animation information
 * @author Job Zwiers
 */
public class Animation extends ColladaElement {
 
   // attributes: (id, name inherited from ColladaElement)
   
   // child elements:
   private Asset asset;
   private ArrayList<Animation> animations = new ArrayList<Animation>();
   private ArrayList<Source> sources = new ArrayList<Source>();
   private ArrayList<Sampler> samplers = new ArrayList<Sampler>();
   private ArrayList<Channel> channels = new ArrayList<Channel>();
   private ArrayList<Extra> extras = new ArrayList<Extra>();
   
   public Animation() {
      super();
   }
   
   public Animation(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super(collada); 
      readXML(tokenizer); 
   }
  
   @Override
   public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {
      appendXMLStructure(buf, fmt, asset);
      appendXMLStructureList(buf, fmt, animations);
      appendXMLStructureList(buf, fmt, sources);
      appendXMLStructureList(buf, fmt, samplers);
      appendXMLStructureList(buf, fmt, channels);
      appendXMLStructureList(buf, fmt, extras);
      return buf;  
   }

   @Override
   public void decodeContent(XMLTokenizer tokenizer) throws IOException {
      while (tokenizer.atSTag()) {
         String tag = tokenizer.getTagName();
         if (tag.equals(Asset.xmlTag()))  {                
                 asset = new Asset(getCollada(), tokenizer);  
         } else if (tag.equals(Animation.xmlTag()))  {                
                 animations.add(new Animation(getCollada(), tokenizer)); 
         } else if (tag.equals(Source.xmlTag()))  {                
                 sources.add(new Source(getCollada(), tokenizer)); 
         } else if (tag.equals(Sampler.xmlTag()))  {                
                 samplers.add(new Sampler(getCollada(), tokenizer)); 
         } else if (tag.equals(Channel.xmlTag()))  {                
                 channels.add(new Channel(getCollada(), tokenizer)); 
         } else if (tag.equals(Extra.xmlTag()))  {                
                 extras.add(new Extra(getCollada(), tokenizer));    
         } else {         
            getCollada().warning(tokenizer.getErrorMessage("Animation: skip : " + tokenizer.getTagName()));
            tokenizer.skipTag();
         }
      }  
      addColladaNode(asset);
      addColladaNodes(animations);
      addColladaNodes(sources);
      addColladaNodes(samplers);
      addColladaNodes(channels);
      addColladaNodes(extras);
   }


   /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "animation";
 
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
