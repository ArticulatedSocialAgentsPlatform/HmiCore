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

import hmi.xml.XMLTokenizer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/** 
 * @author Job Zwiers 
 */
public class LibraryImages extends Library<ColladaImage> {
 
   // attributes: id, name inherited from ColladaElement
     
   // child elements:
   private Asset asset;
   private ArrayList<ColladaImage> images = new ArrayList<ColladaImage>();
   private ArrayList<Extra> extras = new ArrayList<Extra>();
   
   
   public LibraryImages(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super(collada); 
      readXML(tokenizer); 
   }
   
   public List<ColladaImage> getLibContentList() {
      return images;
   }
 
   @Override
   public void decodeContent(XMLTokenizer tokenizer) throws IOException {
      while (tokenizer.atSTag()) {
         String tag = tokenizer.getTagName();
         if (tag.equals(Asset.xmlTag()))  {                
                 asset = new Asset(getCollada(), tokenizer); 
         } else if (tag.equals(ColladaImage.xmlTag()))  {                
                 images.add(new ColladaImage(getCollada(), tokenizer)); 
         } else if (tag.equals(Extra.xmlTag()))  {                
                 extras.add(new Extra(getCollada(), tokenizer));    
         } else {         
            getCollada().warning(tokenizer.getErrorMessage("LibraryImages: skip : " + tokenizer.getTagName()));
            tokenizer.skipTag();
         }
      }
      addColladaNode(asset);
      addColladaNodes(images);   
      addColladaNodes(extras);   
   }

 
   /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "library_images";
 
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
