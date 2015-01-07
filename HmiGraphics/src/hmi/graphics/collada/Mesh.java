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
