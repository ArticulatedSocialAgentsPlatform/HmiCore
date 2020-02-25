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
 * Declares a resource that can be used both as the source for texture samples
 * and as target of a render pass.
 * @author Job Zwiers
 */
public class FormatHint extends ColladaElement {
 
   // child elements:
   private String channels; 
   private String range; 
   private String precision; 
   private String option; 
   
   private ArrayList<Extra> extras = new ArrayList<Extra>();
   
   public FormatHint() {
      super();
   }
   
   public FormatHint(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super(collada);    
      readXML(tokenizer);   
   }
 
   @Override
    public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {
      appendTextElement(buf, "channels", channels, fmt);
      appendTextElement(buf, "range", range, fmt);
      appendTextElement(buf, "precision", precision, fmt);
      appendTextElement(buf, "option", option, fmt);        
      appendXMLStructureList(buf, fmt, extras);
      return buf;  
   }

   @Override
   public void decodeContent(XMLTokenizer tokenizer) throws IOException {
      while (tokenizer.atSTag()) {
         String tag = tokenizer.getTagName();
         if (tag.equals(Extra.xmlTag()))  {                
                 extras.add(new Extra(getCollada(), tokenizer));  
         } else if (tag.equals("channels")) {
            channels = tokenizer.takeTextElement("channels");    
         } else if (tag.equals("range")) {
            range = tokenizer.takeTextElement("range");  
         } else if (tag.equals("precision")) {
            precision = tokenizer.takeTextElement("precision");   
         } else if (tag.equals("option")) {
            option = tokenizer.takeTextElement("option");   
         } else {         
            getCollada().warning(tokenizer.getErrorMessage("FormatHint: skip : " + tokenizer.getTagName()));
            tokenizer.skipTag();
         }
      }    
      addColladaNodes(extras);      
   }
 
   /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "format_hint";
 
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
