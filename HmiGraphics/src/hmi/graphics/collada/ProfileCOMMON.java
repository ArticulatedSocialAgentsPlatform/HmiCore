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
 * Opens a block of platform-independent declarations for the common, fixed-function shader.
 * @author Job Zwiers
 */
public class ProfileCOMMON extends ColladaElement {
 
   // attributes: id, inherited from ColladaElement
   private String platform; // optional, for profile_CG, profile_GLES
 
   // child elements:
   private Asset asset; // optional
   private ArrayList<ColladaImage> imageList = new ArrayList<ColladaImage>();
   private ArrayList<Newparam> newparamList = new ArrayList<Newparam>();
   private ArrayList<TechniqueFX> techniqueList = new ArrayList<TechniqueFX>(); // required. For ProfileCOMMON: just one allowed 
   private ArrayList<Extra> extras = new ArrayList<Extra>();
   
   public ProfileCOMMON() {
      super();
   }
   
   public ProfileCOMMON(Collada collada) {
      super(collada);   
   }
   
   public ProfileCOMMON(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super(collada);   
      readXML(tokenizer); 
   }

   public List<Newparam> getNewparamList() { return newparamList; }
   
   public List<ColladaImage> getImageList() { return imageList; }
   
   public TechniqueFX getTechniqueFX() { 
      if (techniqueList == null || techniqueList.size() == 0) return null;
      return techniqueList.get(0);
   }

   /**
    * appends a String of attributes to buf.
    */
   @Override
   public StringBuilder appendAttributes(StringBuilder buf) {
      super.appendAttributes(buf);
      appendAttribute(buf, "platform", platform);
      return buf;
   }

   /**
    * decodes the XML attributes
    */
   @Override
   public void decodeAttributes(HashMap<String, String> attrMap, XMLTokenizer tokenizer) {      
      platform   = getOptionalAttribute("platform", attrMap);
      super.decodeAttributes(attrMap, tokenizer);
   }
 
   @Override
   public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {
      appendXMLStructure(buf, fmt, asset);
      appendXMLStructureList(buf, fmt, imageList);
      appendXMLStructureList(buf, fmt, newparamList);
      appendXMLStructureList(buf, fmt, techniqueList);
      appendXMLStructureList(buf, fmt, extras);
      return buf;  
   }

   @Override
   public void decodeContent(XMLTokenizer tokenizer) throws IOException {
      while (tokenizer.atSTag()) {
         decodeElement(tokenizer);
      }      
      addElements();
   }
 
   public void decodeElement(XMLTokenizer tokenizer) throws IOException {
       String tag = tokenizer.getTagName();
         if (tag.equals(Asset.xmlTag()))  {                
             asset = new Asset(getCollada(), tokenizer);    
         } else if (tag.equals(ColladaImage.xmlTag()))  {
             imageList.add(new ColladaImage(getCollada(), tokenizer));              
         } else if (tag.equals(Newparam.xmlTag()))  {
             newparamList.add(new Newparam(getCollada(), tokenizer));  
         } else if (tag.equals(TechniqueFX.xmlTag()))  {                
             techniqueList.add(new TechniqueFX(getCollada(), tokenizer));  
         } else if (tag.equals(Extra.xmlTag()))  {
             extras.add(new Extra(getCollada(), tokenizer)); 
         } else {         
            getCollada().warning(tokenizer.getErrorMessage("Profile_*: skip : " + tokenizer.getTagName()));
            tokenizer.skipTag();
         }       
   }
 
    public void addElements() {
      addColladaNode(asset);
      addColladaNodes(imageList);  
      addColladaNodes(newparamList);
      addColladaNodes(techniqueList);
      addColladaNodes(extras);
    }
 
 
 
   /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "profile_COMMON";
 
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
