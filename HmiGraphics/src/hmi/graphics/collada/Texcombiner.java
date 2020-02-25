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


/** 
 * Defines a texture_pipeline command
 * @author Job Zwiers
 */
public class Texcombiner extends ColladaElement {
 
   // attributes: none
   // child elements:
   private Constant constant;
   private RGB rgb;
   private Alpha alpha;
   
   public Texcombiner() {
      super();
   }
   
   public Texcombiner(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super(collada);
      readXML(tokenizer);    
   }
 
   @Override
   public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {
      appendXMLStructure(buf, fmt, constant); 
      appendXMLStructure(buf, fmt, rgb); 
      appendXMLStructure(buf, fmt, alpha); 
      return buf;  
   }

   @Override
   public void decodeContent(XMLTokenizer tokenizer) throws IOException {
      while (tokenizer.atSTag()) {
         String tag = tokenizer.getTagName();
         if (tag.equals(Constant.xmlTag()))  {                
                 constant = new Constant(getCollada(), tokenizer);
         } else if (tag.equals("RGB.XMLTag")) {
            rgb = new RGB(getCollada(), tokenizer);     
         } else if (tag.equals("Alpha.XMLTag")) {
            alpha = new Alpha(getCollada(), tokenizer);     
         } else {         
            getCollada().warning(tokenizer.getErrorMessage("Texcombiner: skip : " + tokenizer.getTagName()));
            tokenizer.skipTag();
         }
      }    
      addColladaNode(constant);
      addColladaNode(rgb);    
      addColladaNode(alpha);   
   }
 
   /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "texcombiner";
 
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
