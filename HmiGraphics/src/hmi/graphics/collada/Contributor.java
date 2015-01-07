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
