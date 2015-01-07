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
