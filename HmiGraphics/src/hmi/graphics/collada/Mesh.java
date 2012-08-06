/*******************************************************************************
 * Copyright (C) 2009 Human Media Interaction, University of Twente, the Netherlands
 * 
 * This file is part of the Elckerlyc BML realizer.
 * 
 * Elckerlyc is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Elckerlyc is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Elckerlyc.  If not, see http://www.gnu.org/licenses/.
 ******************************************************************************/

package hmi.graphics.collada;

import hmi.xml.XMLFormatting;
import hmi.xml.XMLTokenizer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** 
 * A Collada Mesh defines and combines data Sources, vertices, and geometry for tesselation.
 * @author Job Zwiers
 */
public class Mesh extends ColladaElement {
   private ArrayList<Source> sources = new ArrayList<Source>();
   private Vertices vertices;
   private static final int LISTSIZE = 4;
   private ArrayList<PrimitiveMeshElement> primitiveMeshElements = new ArrayList<PrimitiveMeshElement>(LISTSIZE);
   
   
   
   /**
    * A Mesh has a type, like Triangles, Tristrips, Polygons etc.
    */
   public enum MeshType {Undefined, Triangles, Trifans, Tristrips, Polygons, Polylist, Lines, Linestrips};   
   
   /**
    * Default constructor.
    */        
   public Mesh() {
      super();
   }

   /**
    * Constructor used to create a Mesh Object from XML.
    */         
   public Mesh(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super(collada); 
      readXML(tokenizer); 
   }
 
   /**
    * prints a report.
    */
   @Override
   public void  reportNode(String reportType) {
      if (! reportType.startsWith("Mesh")) return;
      getCollada().warning(toString());  
   }
 
   /**
    * Returns the list of Collada Sources for this Mesh.
    */
   public List<Source> getSources() {
      return sources;
   }
 
   /**
    * Returns the Collada Vertices for this Mesh.
    */
   public Vertices getVertices() {
      return vertices;
   }
   
   /**
    * Returns the Collada primitive mesh elements, like Triangles, Polygons, PolyList etcetera, for this Mesh.
    */
   public List<PrimitiveMeshElement> getPrimitiveMeshElements() {
      return primitiveMeshElements;
   }

   @Override
   public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {
      appendXMLStructureList(buf, fmt, sources);
      appendXMLStructure(buf, fmt, vertices);
      appendXMLStructureList(buf, fmt, primitiveMeshElements);  
      return buf;  
   }

   @Override
   public void decodeContent(XMLTokenizer tokenizer) throws IOException {
      while (tokenizer.atSTag()) {
         String tag = tokenizer.getTagName();
         if (tag.equals(Source.xmlTag()))  {                
                 sources.add(new Source(getCollada(), tokenizer));     
         } else if (tag.equals(Vertices.xmlTag()))  {                
                 vertices = new Vertices(getCollada(), tokenizer); 
         } else if (tag.equals(Polygons.xmlTag()))  {                
                 Polygons polygons = new Polygons(getCollada(), tokenizer);  
                 primitiveMeshElements.add(polygons);  
         } else if (tag.equals(PolyList.xmlTag()))  {                
                 PolyList polylist = new PolyList(getCollada(), tokenizer);  
                 primitiveMeshElements.add(polylist);   
         } else if (tag.equals(Triangles.xmlTag()))  {                
                 Triangles triangles = new Triangles(getCollada(), tokenizer);  
                 primitiveMeshElements.add(triangles);   
         } else {         
            getCollada().warning(tokenizer.getErrorMessage("Mesh: skip : " + tokenizer.getTagName()));
            tokenizer.skipTag();
         }
      }    
      if (vertices == null) vertices = new Vertices(); // empty vertices 
      addColladaNodes(sources);
      addColladaNode(vertices);
      addColladaNodes(primitiveMeshElements);  
   }
 
   /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "mesh";
 
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
