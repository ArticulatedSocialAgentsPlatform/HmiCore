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
import java.util.HashMap;

/** 
 * Joints and weights for a skin
 * @author Job Zwiers
 */
public class ColladaVertexWeights extends ColladaElement {
    
   private ArrayList<Input> inputs = new ArrayList<Input>();
   private ArrayList<Extra> extras = new ArrayList<Extra>(2); 
   private V v;             // per joint, list of joint/weight pairs
   private VCount vcount;   // number of joints per vertex
   private int count;       // attribute: number of vertices in the mesh
   
   private String[] jointNames = null;
   private String jointSourceId = null;
   private String weightSourceId = null;
   private float[] indexedWeights;
   private float[] jointWeights;
   private int[] jointIndices;
   private int stride;
   private int jointOffset;
   private int weightOffset;
   private int indexLen;
   private boolean resolved = false;
   

   /**
    * Default constructor
    */     
   public ColladaVertexWeights() {
      super();
   }

   /**
    * Constructor used to create a ColladaVertexWeights Object from XML
    */      
   public ColladaVertexWeights(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super(collada);
      readXML(tokenizer); 
   }
 
 
   /**
    * Tries to resolve the reference for the input with joint semantic 
    */
   public void resolve() {
      resolved = true;
      for (Input inp : inputs) {
         if (inp.getSemantic().equals("JOINT")) {
            jointSourceId = urlToId(inp.getSource());
         }  
         if (inp.getSemantic().equals("WEIGHT")) {
            weightSourceId = urlToId(inp.getSource());
         }  
      }
      if (jointSourceId == null) {
         throw new RuntimeException("<vertex_weights> element: no <input> with JOINT semantic specified");
      }
      Source jointSource = getCollada().getSource(jointSourceId);
      jointNames = jointSource.getHomogeneousNameData();
      if (weightSourceId == null) {
         throw new RuntimeException("<vertex_weights> element: no <input> with WEIGHT semantic specified");
      }
      Source weightSource = getCollada().getSource(weightSourceId);
      indexedWeights = weightSource.getHomogeneousFloatData();
   }
 
   /**
    * Return a String array with joint names
    */
   public String[] getJointNames() {
      if (jointNames == null) resolve();
      return jointNames;
   }
   
   /**
    * Returns an array with the joint indices
    */
   public int[] getJointIndices() {
      if ( ! resolved ) resolve();    
      jointIndices = new int[indexLen];
      for (int i=0; i<indexLen; i++) {
         jointIndices[i] = v.getIndex(jointOffset + stride*i);  
      }
      return jointIndices;
   }
   
   /**
    * Returns an array with the joint weights
    */
   public float[] getJointWeights() {
      if ( ! resolved ) resolve();    
      jointWeights = new float[indexLen];   
      for (int i=0; i<indexLen; i++) {
         jointWeights[i] = indexedWeights[v.getIndex(weightOffset + stride*i)];  
      }
      return jointWeights;
   }
 
   public int[] getVCount() {
      return vcount.getCounts();
   }
 
 
   /**
    * appends a String of attributes to buf.
    */
   @Override
   public StringBuilder appendAttributes(StringBuilder buf) {
      super.appendAttributes(buf);
      appendAttribute(buf, "count", count);
      return buf;
   }


   /**
    * decodes the XML attributes
    */
   @Override
   public void decodeAttributes(HashMap<String, String> attrMap, XMLTokenizer tokenizer) {  
      count   = getRequiredIntAttribute("count", attrMap, tokenizer);      
      super.decodeAttributes(attrMap, tokenizer);
   }
 
 
   @Override
   public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {
      appendXMLStructureList(buf, fmt, inputs);
      appendXMLStructure(buf, fmt, vcount);
      appendXMLStructure(buf, fmt, v);
      appendXMLStructureList(buf, fmt, extras);
      return buf;  
   }

   @Override
   public void decodeContent(XMLTokenizer tokenizer) throws IOException {
      int maxOffset = 0;
      while (tokenizer.atSTag()) {
         String tag = tokenizer.getTagName();
         if (tag.equals(Input.xmlTag()))  {   
             Input inp = new Input(getCollada(), tokenizer);         
             inputs.add(inp);   
             if (inp.getOffset() > maxOffset) maxOffset = inp.getOffset();
             String sem = inp.getSemantic();
             if (sem.equals("JOINT")) {
                 jointOffset = inp.getOffset();
             } else if (sem.equals("WEIGHT")) {
                 weightOffset = inp.getOffset();
             } else {
                 getCollada().warning("Vertex_weights, unknown input semantics: " + sem);           
             }
         } else if (tag.equals(Extra.xmlTag()))  {                
             extras.add(new Extra(getCollada(), tokenizer));   
         } else if (tag.equals(VCount.xmlTag()))  {                
             vcount = new VCount(getCollada(), tokenizer);
         } else if (tag.equals(V.xmlTag()))  {                
             v = new V(getCollada(), tokenizer);
         } else {         
            getCollada().warning(tokenizer.getErrorMessage("ColladaVertexWeights: skip : " + tokenizer.getTagName()));
            tokenizer.skipTag();
         }
      }    
      addColladaNodes(inputs);
      addColladaNodes(extras);
      addColladaNode(vcount);
      addColladaNode(v);  
      stride = maxOffset+1;
      indexLen = v.getNrIndices()/stride;
   }
 
   /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "vertex_weights";
 
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
