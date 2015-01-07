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
import java.util.ArrayList;
import java.util.HashMap;

/** 
 * Storage of graphical representations, like raster data.
 * @author Job Zwiers
 */
public class ColladaImage extends ColladaElement {
 
   // attributes: id, name inherited from ColladaElement
   private String format; // optional image format
   private int height = -1;  // negative values denote unspecified values
   private int width = -1;
   private int depth = 1; // default value, also for 2D images
  
   
   // child elements:
   private Asset asset;         // optional
   private Data data;           // either Data or initFrom must be present (not both)
   private String initFrom;
   private ArrayList<Extra> extras = new ArrayList<Extra>();
   
   
   public ColladaImage() {
      super();
   }
   
   public ColladaImage(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super(collada);
      readXML(tokenizer); 
   }
 
   public String getInitFrom() {
      return initFrom;
   }
 
   /**
    * appends the XML content
    */
   @Override
   public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {  
      appendXMLStructure(buf, fmt, asset); 
      appendXMLStructure(buf, fmt, data);
      appendTextElement(buf, "init_from", initFrom, fmt);   
      appendXMLStructureList(buf, fmt, extras); 
      return buf;
   }

   /**
    * decodes the XML content
    */
   @Override
   public void decodeContent(XMLTokenizer tokenizer) throws IOException {
      while (tokenizer.atSTag()) {
         String tag = tokenizer.getTagName();
         if (tag.equals(Asset.xmlTag()))  {                
                 asset = new Asset(getCollada(), tokenizer);             
         } else if (tag.equals(Data.xmlTag()))  {                
                 data = new Data(getCollada(), tokenizer);   
         } else if (tag.equals("init_from"))  {                
                 initFrom = tokenizer.takeTextElement("init_from");   
         } else if (tag.equals(Extra.xmlTag()))  {                
                 extras.add(new Extra(getCollada(), tokenizer));      
         } else {         
            getCollada().warning(tokenizer.getErrorMessage("ColladaImage: skip : " + tokenizer.getTagName()));
            tokenizer.skipTag();
         }
      }
      addColladaNode(asset);
      addColladaNode(data);      
      addColladaNodes(extras);
   }
   
   

   /**
    * appends a String of attributes to buf.
    */
@Override
   public StringBuilder appendAttributes(StringBuilder buf) {
      super.appendAttributes(buf);
      appendAttribute(buf, "format", format);
      if (height >=0) appendAttribute(buf, "height", height);
      if (width >=0)  appendAttribute(buf, "width", width);
      if (depth != 1) appendAttribute(buf, "depth", depth);  
      return buf;
   }


   /**
    * decodes the XML attributes
    */
   @Override
   public void decodeAttributes(HashMap<String, String> attrMap, XMLTokenizer tokenizer) {
      format = getOptionalAttribute("format", attrMap);
      height = getOptionalIntAttribute("height", attrMap, -1);
      width = getOptionalIntAttribute("width", attrMap, -1);
      depth = getOptionalIntAttribute("depth", attrMap, 1);
      super.decodeAttributes(attrMap, tokenizer);
   }


 
   /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "image";
 
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
