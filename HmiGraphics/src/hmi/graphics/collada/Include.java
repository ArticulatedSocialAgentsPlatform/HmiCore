/*******************************************************************************
 * The MIT License (MIT)
 * Copyright (c) 2015 University of Twente
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *******************************************************************************/

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
