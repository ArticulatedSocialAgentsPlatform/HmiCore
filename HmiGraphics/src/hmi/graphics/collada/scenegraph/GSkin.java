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

package hmi.graphics.collada.scenegraph;

import hmi.graphics.scenegraph.VertexWeights;
import hmi.math.Mat4f;
import hmi.xml.XMLFormatting;
import hmi.xml.XMLStructureAdapter;
import hmi.xml.XMLTokenizer;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
 
/**
 * GSkin represents mesh skinning information: joint names and, per mesh vertex, a number
 * of jointIndex/jointWeight pairs. The number of such pairs is kept, per vertex, in vcount.
 * @author Job Zwiers
 */
public final class GSkin extends XMLStructureAdapter {
   
   private String[] jointNames;              // joint names.
   
   private float[] bindShapeMatrix;        // 4x4 Matrix, transform to be applied to the mesh, before binding.
   private float[] invBindMatrices;          // all inverse bind matrices, packed together, 16 floats per matrix.

   private int[]   jointIndices;             // joint indices, packed together in one array
   private float[] jointWeights;             // joint weights, packed together in one array
   private int[] vcount;                     // vcount[v] = number of associated jointindex/weight pairs, for  vertex v.

//   private ColladaVertexWeights vertexWeights;
  
   
   /***/
   private GSkin() {
    
   }
   
   public GSkin(XMLTokenizer tokenizer) throws IOException {
       this();
       readXML(tokenizer);  
   }
   
   
   /**
    * Sets the jointIndices array.
    */
   public void setJointIndices(int[] jointIndices) {
      this.jointIndices = jointIndices;

   }  
     
   /**
    * Returns the jointIndices array.
    */
   public int[] getJointIndices() {
       return jointIndices;  
   }
   
   /**
    * Sets the jointWeights array.
    * Also sets the size to jointIndices.length
    */
   public void setJointWeights(float[] jointWeights) {
      this.jointWeights = jointWeights;
   }  
     
   /**
    * Returns the jointWeights array.
    */
   public float[] getJointWeights() {
       return jointWeights;  
   }
  
   
   public void setVCount(int[] vcount) {
      this.vcount = vcount;
   }
   
   public int[] getVCount() {
      return vcount;
   }
  
   
   /**
    * Creates a new VertexWeight by copying jointIndices, jointWeights, and count data
    */
   public VertexWeights createVertexWeights() {
       int[] vc = Arrays.copyOf(vcount, vcount.length);
       int[] ji = Arrays.copyOf(jointIndices, jointIndices.length);
       float[] jw = Arrays.copyOf(jointWeights, jointWeights.length);
       return new VertexWeights(vc, ji, jw);
   }
   
   
   /**
    * Sets a 4x4 matrix  in row major order(a Mat4f element)
    */
   public void setBindShapeMatrix(float[] m4x4) {
      this.bindShapeMatrix = m4x4;
   }
   
   /**
    * Returns the bind shape matrix, which could be null
    */ 
   public float[] getBindShapeMatrix() {
      return bindShapeMatrix;
   }
   
   
   /**
    * Sets the jointNames array
    */
   public void setJointNames(String[] jointNames) {
      this.jointNames = jointNames;
   }
   
   /**
    * Returns  the jointNames array
    */
   public String[] getJointNames() {
      return jointNames;
   }
   
   
   
   /**
    * Sets the inverse bind matrices
    */
   public void setInvBindMatrices(float[] invBindMatrices) {
       this.invBindMatrices = invBindMatrices; 
   }
   
   public float[] getInvBindMatrices() {
      return invBindMatrices;
   }
   
   
  


   /**
    * appends the id and sid XML attributes to buf.
    */
   @Override
   public String toString() {
       return toXMLString();  
   }





   private static final int JOINTNAMESPERLINE = 20;
  
   /**
    * appends the id and sid XML attributes to buf.
    */
   @Override
   public StringBuilder appendAttributeString(StringBuilder buf, XMLFormatting fmt) {
      fmt.indent();
      buf.append('\n'); appendSpaces(buf, fmt);
      appendAttribute(buf, "joints", jointNames, ' ', fmt, JOINTNAMESPERLINE); 
      buf.append('\n'); appendSpaces(buf, fmt);
      appendAttribute(buf, "invbindmatrices", invBindMatrices, ' ', fmt, Mat4f.MAT4F_SIZE);     
      fmt.unIndent();
      return buf;
   } 
    
   /**
    * decodes the id and sid XML attributes.
    */
   @Override
   public void decodeAttributes(HashMap<String, String> attrMap, XMLTokenizer tokenizer) {      
      
//      name  = getOptionalAttribute("name", attrMap);
//      if (name != null) name = name.intern();
//      vertexDataSize = getOptionalIntAttribute("vertexDataSize", attrMap, 0);
//      indexDataSize = getOptionalIntAttribute("indexDataSize", attrMap, 0);
      String jointNameAttr = getRequiredAttribute("joints", attrMap, tokenizer);
      jointNames = decodeStringArray(jointNameAttr);
      
      String invBindMatricesAttr = getRequiredAttribute("invbindmatrices", attrMap, tokenizer);
      invBindMatrices = decodeFloatArray(invBindMatricesAttr);
      
   
      
      super.decodeAttributes(attrMap, tokenizer);
   }

   /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "GSkin";
 
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
