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

/** 
 * @author Job Zwiers 
 */
public class Geometry extends ColladaElement {
    
   private Asset asset;
   private Mesh mesh;
   // public Convex_Mesh convex_mesh;
   // public Spline spline;
   private ArrayList<Extra> extras = new ArrayList<Extra>();
   // id etc from ColladaElement
   

   /**
    * Default constructor
    */   
   public Geometry() {
      super();
   }

   /**
    * Constructor used to create a Geometry Object from XML
    */   
   public Geometry(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super(collada);
      readXML(tokenizer); 
   }

 
   public Mesh getMesh() {
      return mesh;
   }
 
 
   @Override
   public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {
      appendXMLStructure(buf, fmt, asset);
      appendXMLStructure(buf, fmt, mesh);
     // appendXMLStructure(buf, fmt, convex_mesh);
     // appendXMLStructure(buf, fmt, spline);
      appendXMLStructureList(buf, fmt, extras);
      return buf;  
   }

   @Override
   public void decodeContent(XMLTokenizer tokenizer) throws IOException {
      while (tokenizer.atSTag()) {
         String tag = tokenizer.getTagName();
         if (tag.equals(Asset.xmlTag()))  {                
                 asset = new Asset(getCollada(), tokenizer);  
         } else if (tag.equals(Mesh.xmlTag()))  {                
                 mesh = new Mesh(getCollada(), tokenizer);  
//         } else if (tag.equals(Convex_Mesh.xmlTag()))  {                
//                 convex_mesh = new Convex_Mesh(tokenizer); 
//         } else if (tag.equals(Spline.xmlTag()))  {                
//                 spline = new Spline(tokenizer);  
         } else if (tag.equals(Extra.xmlTag()))  {                
                 extras.add(new Extra(getCollada(), tokenizer));    
         } else {         
            getCollada().warning(tokenizer.getErrorMessage("Geometry: skip : " + tokenizer.getTagName()));
            tokenizer.skipTag();
         }
      }  
      addColladaNode(asset);  
      addColladaNode(mesh);  
      addColladaNodes(extras);      
   }

 
   /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "geometry";
 
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
