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
