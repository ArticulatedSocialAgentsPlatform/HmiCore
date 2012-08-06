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
 * AnimationClip information
 * @author Job Zwiers
 */
public class AnimationClip extends ColladaElement {
 
   // attributes: id, name inherited from ColladaElement
   private double start; // time in seconds of start of clip. Same time base as used in key frames. default: 0.0
   private double end;   // end time, in seconds. default: time of longest animation (-1 used to denote this situation)
   
   // child elements:
   private Asset asset;
   private ArrayList<InstanceAnimation> instanceAnimations = new ArrayList<InstanceAnimation>();
   private ArrayList<Extra> extras = new ArrayList<Extra>();
   
   public AnimationClip() {
      super();
   }
   
   public AnimationClip(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super(collada);    
      readXML(tokenizer);     
   }
  

   /**
    * appends a String of attributes to buf.
    */
   @Override
   public StringBuilder appendAttributes(StringBuilder buf) {
      super.appendAttributes(buf);
      if (start != 0.0) appendAttribute(buf, "start", start);
      if (end >=0.0) appendAttribute(buf, "end", end);
      return buf;
   }


   /**
    * decodes the XML attributes
    */
   @Override
   public void decodeAttributes(HashMap<String, String> attrMap, XMLTokenizer tokenizer) {     
      start = getOptionalDoubleAttribute("start", attrMap, 0.0);
      end   = getOptionalDoubleAttribute("end", attrMap, -1.0);
      super.decodeAttributes(attrMap, tokenizer);
   }


   @Override
   public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {
      appendXMLStructure(buf, fmt, asset);
      appendXMLStructureList(buf, fmt, instanceAnimations);
      appendXMLStructureList(buf, fmt, extras);
      return buf;  
   }

   @Override
   public void decodeContent(XMLTokenizer tokenizer) throws IOException {
      while (tokenizer.atSTag()) {
         String tag = tokenizer.getTagName();
         if (tag.equals(Asset.xmlTag()))  {                
                 asset = new Asset(getCollada(), tokenizer);  
         } else if (tag.equals(InstanceAnimation.xmlTag()))  {                
                 instanceAnimations.add(new InstanceAnimation(getCollada(), tokenizer)); 
         } else if (tag.equals(Extra.xmlTag()))  {                
                 extras.add(new Extra(getCollada(), tokenizer));    
         } else {         
            getCollada().warning(tokenizer.getErrorMessage("AnimationClip: skip : " + tokenizer.getTagName()));
            tokenizer.skipTag();
         }
      }  
      addColladaNode(asset);
      addColladaNodes(instanceAnimations);
      addColladaNodes(extras);
   }

   /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "animation_clip";
 
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
