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
import java.util.List;

/** 
 * @author Job Zwiers
 */
public class LibraryAnimations extends Library<Animation> {
 
   // attributes: id, name inherited from ColladaElement
     
   // child elements:
   private Asset asset;
   private ArrayList<Animation> animations = new ArrayList<Animation>();
   private ArrayList<Extra> extras = new ArrayList<Extra>();
   
   public LibraryAnimations(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super(collada); 
      readXML(tokenizer); 
   }
   
   public List<Animation> getLibContentList() {
      return animations;
   }
 
   /**
    * appends the XML content
    */
   @Override
   public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {    
      appendXMLStructure(buf, fmt, asset);
      appendXMLStructureList(buf, fmt, animations);
      appendXMLStructureList(buf, fmt, extras);  
      return buf;
   }
   
   @Override
   public void decodeContent(XMLTokenizer tokenizer) throws IOException {
      while (tokenizer.atSTag()) {
         String tag = tokenizer.getTagName();
         if (tag.equals(Asset.xmlTag()))  {                
                 asset = new Asset(getCollada(), tokenizer); 
         } else if (tag.equals(Animation.xmlTag()))  {                
                 animations.add(new Animation(getCollada(), tokenizer)); 
         } else if (tag.equals(Extra.xmlTag()))  {                
                 extras.add(new Extra(getCollada(), tokenizer));  
         } else {         
            getCollada().warning(tokenizer.getErrorMessage("LibraryAnimations: skip : " + tokenizer.getTagName()));
            tokenizer.skipTag();
         }
      }
      addColladaNode(asset);
      addColladaNodes(animations);   
      addColladaNodes(extras);   
   }
   

   /**
    * appends a String of attributes to buf.
    */
   @Override
   public StringBuilder appendAttributes(StringBuilder buf) {
      super.appendAttributes(buf);
      return buf;
   }


   /**
    * decodes the XML attributes
    */
   @Override
   public void decodeAttributes(HashMap<String, String> attrMap, XMLTokenizer tokenizer) {
      super.decodeAttributes(attrMap, tokenizer);
   }


 
   /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "library_animations";
 
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
