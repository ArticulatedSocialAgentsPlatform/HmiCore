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
 * Declares a resource that can be used both as the source for texture samples
 * and as target of a render pass.
 * @author Job Zwiers
 */
public class Surface extends ColladaElement {
 
   // attributes: 
   private String type; // UNTYPED, 1D, 2D, 3D, CUBE, DEPTH, RECT.
 
   
   // child elements:
   private String format; // platform specific texel format, like R8G8B8A8 or sRGB
   private FormatHint formatHint;
   //private float[] size;
   //private float viewportRatio = 1.0f; // default is 1. Not allowed when size is specified
   private int mipLevels;
   private String mipmapGenerate;
   private InitFrom initFrom;
   private InitVolume initVolume;
   
   //private Generator generator; 
   private ArrayList<Extra> extras = new ArrayList<Extra>();
   
   public Surface() {
      super();
   }
   
   public Surface(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super(collada);
      readXML(tokenizer);  
   }
 
   public InitFrom getInitFrom() { return initFrom; }
   
   public InitVolume getInitVolume() { return initVolume; }             
 
   /**
    * appends a String of attributes to buf.
    */
   @Override
   public StringBuilder appendAttributes(StringBuilder buf) {
      super.appendAttributes(buf);
      appendAttribute(buf, "type", type);
      return buf;
   }

   /**
    * decodes the XML attributes
    */
   @Override
   public void decodeAttributes(HashMap<String, String> attrMap, XMLTokenizer tokenizer) {     
      type = getOptionalAttribute("type", attrMap); // required in GLSL
      super.decodeAttributes(attrMap, tokenizer);
   }
 
   @Override
    public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {
      appendTextElement(buf, "format", format, fmt);     
      appendTextElement(buf, "mipmapGenerate", mipmapGenerate, fmt);
      appendXMLStructure(buf, fmt,  formatHint);
      appendXMLStructure(buf, fmt,  initFrom);
      appendXMLStructure(buf, fmt,  initVolume);
      appendIntElement(buf, "mipLevels", mipLevels, fmt);
      appendXMLStructureList(buf, fmt, extras);
      return buf;  
   }

   @Override
   public void decodeContent(XMLTokenizer tokenizer) throws IOException {
      while (tokenizer.atSTag()) {
         String tag = tokenizer.getTagName();
         if (tag.equals(Extra.xmlTag()))  {                
                 extras.add(new Extra(getCollada(), tokenizer));  
         } else if (tag.equals("FormatHint.XMLTag")) {
            formatHint = new FormatHint(getCollada(), tokenizer);     
         } else if (tag.equals("format")) {
            format = tokenizer.takeTextElement("format");    
         } else if (tag.equals("mipmapGenerate")) {
            mipmapGenerate = tokenizer.takeTextElement("mipmapGenerate");  
         } else if (tag.equals("mipLevels")) {
            mipLevels = tokenizer.takeIntElement("mipLevels");   
         } else if (tag.equals(InitFrom.xmlTag())) {
            initFrom = new InitFrom(getCollada(), tokenizer);  
         } else if (tag.equals(InitVolume.xmlTag())) {
            initVolume = new InitVolume(getCollada(), tokenizer);  
         } else {         
            getCollada().warning(tokenizer.getErrorMessage("Surface: skip : " + tokenizer.getTagName()));
            tokenizer.skipTag();
         }
      }    
      addColladaNode(formatHint);
      addColladaNode(initFrom);
      addColladaNode(initVolume);
      addColladaNodes(extras);      
   }
 
   /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "surface";
 
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
