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
