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

import hmi.util.Resources;
import hmi.xml.XMLTokenizer;

import java.io.BufferedReader;
import java.io.IOException;


/** 
 * include is an XML element that (temporarily) switches 
 * to a different Reader, specified by means of a  URL(s).
 * The current tokenizer state is pushed on a stack, so
 * settings made while processing the included XML has no
 * effect on the context of the <include> element.
 * The EndOfDocument, expected at the end of the included
 * XML, is silently removed, by setting the popOnEndOfDocument
 * attribute to true. So, in general, the effect is as if the
 * included contents is physically inserted within the current 
 * input, like a C style include file.
 * @author Job Zwiers
 */

public class Include extends ColladaElement {
   
   private String url = "";
   private Resources resources;
   
   public Include() {
      super(); 
   }
   
   public Include(Resources resources) {
      this();
      if (resources == null) {
         logger.error("Collada include with null resources ");
      }
      this.resources = resources;
   }

   
   public void setURL(String url) {
      this.url = url;
   }
   
   /**
    * Sets the (Resources) file name for the include element
    */
   public void setFile(String resourceFileName) {
      this.url = resourceFileName;
   }
   
   /**
    *
    */
   public boolean switchReader(XMLTokenizer tokenizer) {
      if (url == null || url.length() == 0) {
         logger.error("Collada include: null or empty url");
         return false;
      }
      if (resources == null) {
         logger.error("Collada include: null Resources");
         return false;
      }
      BufferedReader reader = resources.getReader(url);
      tokenizer.pushReader(reader);
      tokenizer.setpopOnEndOfDocument(true); // will be restored on pop
      return true;
   }

   /**
    * executed after all the ETag has been read.
    */
   public void postProcess(XMLTokenizer tokenizer) {
      boolean switched = switchReader(tokenizer);
      if (!switched) {
         logger.error("include: Could not switch input stream");
      } 
   }
  
   /**
    * decodes the content of the included URL
    */
   public void decodeContent(XMLTokenizer tokenizer) throws IOException {
   
   }

   /**
    * appends a String of Group attributes to buf.
    * Attributes: 
    */
   public StringBuilder appendAttributeString(StringBuilder buf) {
      super.appendAttributeString(buf);
      return buf;
   }

   /**
    * decodes a single attribute, as encoded by appendAttributeString()
    */
   public boolean decodeAttribute(String attrName, String valCode, XMLTokenizer tokenizer) {
      if (attrName.equals("url")) {
          String urlspec = valCode.trim();
          if (urlspec.length() == 0) {
            logger.error("Empty URL at line " + tokenizer.getLine());
          } else {
            setURL(urlspec); 
          }          
          return true;
      } else if (attrName.equals("file")) {
          String fileName = valCode.trim();
          if (fileName.length() == 0) {
             logger.error("Empty file name at line " + tokenizer.getLine());
          } else {
             setFile(fileName); 
          }  
          return true;        
      } else {
         return super.decodeAttribute(attrName, valCode, tokenizer);
      }  
   } 
 

   /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "include";
 
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
