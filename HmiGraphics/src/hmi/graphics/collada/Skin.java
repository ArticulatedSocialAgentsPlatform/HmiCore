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
 * Declares the data required to blend between sets of static meshes.
 * @author Job Zwiers
 */
public class Skin extends ColladaElement {
 
   // attributes: 
   private String source;  // uri reference to the base/bind-shape mesh (static or morphed), form: #url. required.

   //child elements: 

   private BindShapeMatrix bindShapeMatrix; // optional
   // at least three sources are required: Joints, Weights, "Inv_bind_mats"
   private ArrayList<Source> sources = new ArrayList<Source>();
   private Joints joints; // required
   private ColladaVertexWeights vertexWeights; // required
   private ArrayList<Extra> extras = new ArrayList<Extra>();

   /**
    * Default constructor
    */        
   public Skin() {
      super();
   }


   /**
    * Constructor used to create a Skin Object from XML
    */         
   public Skin(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super(collada);
      readXML(tokenizer); 
   }
 
   /**
    * returns the source
    */
   public String getSource() {
      return source;  
   }
 
   /**
    * Returns the joint names, or null if not defined.
    */
   public String[] getJointSIDs() {
       if (joints == null) {
           getCollada().warning("Collada Translator: skin for " + source + " has no joints"); 
           return null;
       }
       return joints.getJointSIDs();  
   }
 
   public float[] getInvBindMatrices() {
    if (joints == null) {
           getCollada().warning("Collada Translator: skin for " + source + " has no joints"); 
           return null;
       }
       return joints.getInvBindMatrices();  
   }
 
 
   public int[] getJointIndices() {
      if (vertexWeights == null) {
           getCollada().warning("Collada Translator: skin for " + source + " has no vertex weights"); 
           return null;
      }  
      return vertexWeights.getJointIndices();
   }
 
   public float[] getJointWeights() {
      if (vertexWeights == null) {
           getCollada().warning("Collada Translator: skin for " + source + " has no vertex weights"); 
           return null;
      }  
      return vertexWeights.getJointWeights();
   }
 
 
   public int[] getVCount() {
      if (vertexWeights == null) {
           getCollada().warning("Collada Translator: skin for " + source + " has no vertex weights"); 
           return null;
      }  
      return vertexWeights.getVCount();
   }   
   
 
   public float[] getBindShapeMatrix() {
      return bindShapeMatrix.getMat4f();
   }
 
 
   /**
    * returns the Geometry from the libraries, for the source url
    * This will return null when the source url refers to a morph controller, rather than a geometry
    */
   public Geometry getGeometry() {
      if (getCollada() == null || getCollada().getLibrariesGeometries() == null) return null;
       return getCollada().getLibItem(getCollada().getLibrariesGeometries(), source);     
   }
 
   /**
    * returns the (morph-) Controller from the libraries, for the source url,
    * or null, when the source url refers to a geometry.
    */
   public Controller getController() {
      if (getCollada() == null || getCollada().getLibrariesControllers() == null) return null;
       return getCollada().getLibItem(getCollada().getLibrariesControllers(), source);     
   }
 
   /**
    * appends a String of attributes to buf.
    */
   @Override
   public StringBuilder appendAttributes(StringBuilder buf) {
      super.appendAttributes(buf);
      appendAttribute(buf, "source", source);   
      return buf;
   }

   /**
    * decodes the XML attributes
    */
   @Override
   public void decodeAttributes(HashMap<String, String> attrMap, XMLTokenizer tokenizer) {
      source = getOptionalAttribute("source", attrMap);
      super.decodeAttributes(attrMap, tokenizer);
   }
 
   @Override
   public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {
      appendXMLStructure(buf, fmt, bindShapeMatrix);
      appendXMLStructureList(buf, fmt, sources);
      appendXMLStructure(buf, fmt, joints);
      appendXMLStructure(buf, fmt, vertexWeights);
      appendXMLStructureList(buf, fmt, extras);  
      return buf;  
   }

   @Override
   public void decodeContent(XMLTokenizer tokenizer) throws IOException {
      while (tokenizer.atSTag()) {
         String tag = tokenizer.getTagName();
         if (tag.equals(BindShapeMatrix.xmlTag()))  {                
            bindShapeMatrix = new BindShapeMatrix(getCollada(), tokenizer);
         } else if (tag.equals(Source.xmlTag()))  {    
            sources.add(new Source(getCollada(), tokenizer)); 
         } else if (tag.equals(Joints.xmlTag()))  {                
            joints = new Joints(getCollada(), tokenizer);
         } else if (tag.equals(ColladaVertexWeights.xmlTag()))  {                
            vertexWeights = new ColladaVertexWeights(getCollada(), tokenizer);
         } else if (tag.equals(Extra.xmlTag()))  {                
            extras.add(new Extra(getCollada(), tokenizer)); 
         } else {         
            getCollada().warning(tokenizer.getErrorMessage("Skin: skip : " + tokenizer.getTagName()));
            tokenizer.skipTag();
         }
      }    
      addColladaNode(bindShapeMatrix);
      addColladaNodes(sources);
      addColladaNode(joints);
      addColladaNode(vertexWeights);
      addColladaNodes(extras);  
   }
 
   /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "skin";
 
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
