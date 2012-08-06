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

/**
 * class for Contributor element inside Asset element
 * @author Job Zwiers
 */
public class Contributor extends ColladaElement {
   public Contributor() {
      super();
   }
   
   public Contributor(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super(collada); 
      readXML(tokenizer);
   }
   
   private String author, authoringTool, comments, copyright, sourceData;
   
   @Override
   public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {
      appendTextElement(buf, "author", author, fmt);
      appendTextElement(buf, "authoring_tool", authoringTool, fmt);
      appendTextElement(buf, "comments", comments, fmt);
      appendTextElement(buf, "copyright", copyright, fmt);
      appendTextElement(buf, "source_data", sourceData, fmt);
      return buf;  
   }

   @Override
   public void decodeContent(XMLTokenizer tokenizer) throws IOException {
      while (tokenizer.atSTag()) {
         String tag = tokenizer.getTagName();
         if (tag.equals("author"))              author = tokenizer.takeTextElement("author");
         else if (tag.equals("authoring_tool")) authoringTool = tokenizer.takeTextElement("authoring_tool");
         else if (tag.equals("comments"))       comments = tokenizer.takeTextElement("comments");
         else if (tag.equals("copyright"))      copyright = tokenizer.takeTextElement("copyright");            
         else if (tag.equals("source_data"))    sourceData = tokenizer.takeTextElement("source_data");
         else {         
            getCollada().warning(tokenizer.getErrorMessage("Contributor: skip : " + tokenizer.getTagName()));
            tokenizer.skipTag();
         }
      }         
   }
     
   /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "contributor";
 
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
