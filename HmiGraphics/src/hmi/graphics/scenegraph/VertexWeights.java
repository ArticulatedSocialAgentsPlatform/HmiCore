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
package hmi.graphics.scenegraph;

import hmi.math.Vec4f;
import hmi.util.BinUtil;
import hmi.util.BinaryExternalizable;
import hmi.util.Diff;
import hmi.xml.XMLFormatting;
import hmi.xml.XMLStructureAdapter;
import hmi.xml.XMLTokenizer;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 
/**
 * A VertexWeight object couples GMesh vertices to one or more joints, with a certain weight.
 * The number of joints associated which a single vertex is variable, and specified with the jcount parameter of
 * the constructor. Joints are referred to by means of joint indices, resolved within the GMesh to actual joint names.
 * @author Job Zwiers
 */
public class VertexWeights extends XMLStructureAdapter implements BinaryExternalizable, Diff.Differentiable {
   private String name = "vertexweights";    // fixed name, to maintain consistency with VertexAttributes
   private int[] jcount;                     // jcount[v] = number of associated jointindex/weight pairs, for vertex v.
   private int[] jointIndices;               // joint indices, packed together in one array
   private float[] jointWeights;             // joint weights, packed together in one array
   
   private int[] offsets;                    // offsets in jointIndices and jointWeights; the indices and weights for vertex v are stored
                                             // jointIndices[offsets[v]], .., jointIndices[offsets[v] + jcount[v] - 1] and
                                             // jointWeights[offsets[v]], .., jointWeights[offsets[v] + jcount[v] - 1]
                                             // offsets are redundant data: they are calculated from the jcount array.
   private static Logger logger = LoggerFactory.getLogger(VertexWeights.class.getName());
   
   /** 
    * Default constructor, with null data arrays
    */ 
   public VertexWeights() {
   }
   
   /**
    * Creates a new VertexWeight object: jcounts specifies, for every GMesh vertex, the number of associated
    * joints. The jointIndices array contains the actual joint indices, arranged into small (variable length) segments 
    * with lengths specified by the jcount parameter. Each element of a segment refers to a single joint. 
    * The jointWeights array has a structure similar to jointIndices. It specifies the weights for the vertex-to-joint links. 
    */
   public VertexWeights(int[] jcount, int[] jointIndices, float[] jointWeights) {
      this.jcount = jcount;
      this.jointIndices = jointIndices;
      this.jointWeights = jointWeights;
      offsets = new int[jcount.length];
      calculateOffsets();
   }
   
   /**
    * Creates a new VertexWeights and reads the data from the XMLTokenizer.
    */
   public VertexWeights(XMLTokenizer tokenizer) throws IOException {
      this();
      readXML(tokenizer);  
   }
   
   
   /**
    * show differences
    */
   public String showDiff(Object vwObject) {
      VertexWeights vw = (VertexWeights) vwObject;
      if (vw==null) return "VertexWeights " + name + ", diff: null VertexWeights";
      String diff = Diff.showDiff("VertexWeights", name, vw.name);
      if (diff != "") return diff;
      diff = Diff.showDiff("VertexWeights " + name + ", diff jcount", jcount, vw.jcount);
      if (diff != "") return diff;
      diff = Diff.showDiff("VertexWeights " + name + ", diff jointIndices", jointIndices, vw.jointIndices);
      if (diff != "") return diff;
      diff = Diff.showDiff("VertexWeights " + name + ", diff jointWeights", jointWeights, vw.jointWeights);
      if (diff != "") return diff;
      diff = Diff.showDiff("VertexWeights " + name + ", diff offsets", offsets, vw.offsets);
      if (diff != "") return diff;
      return "";
   }
   
   /**
    * Returns the name, which is always &quot;vertexweights&quot;
    */
   public String getName() {
      return name;  
   }
   
   /*
    * calculates offsets in offsets array, so vertex v has jointIndices and jointWeights starting at offsets[v]
    * returns sum of all jcounts
    */
   private int calculateOffsets() {
      int p = 0;
      for (int i=0; i<jcount.length; i++) {
         offsets[i] = p;
         p+= jcount[i];   
      }
      return p; 
   }
     
   /**
    * Sets the jointIndices
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
    * Sets the jointWeights
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
  
   /**
    * Sets the jcount data
    */
   public void setJCounts(int[] jcount) {
      this.jcount = jcount;  
      if (jcount != null) {
         offsets = new int[jcount.length];
         calculateOffsets();
      }
   }
  
   public int[] getJCounts() {
      return jcount;
   }
        
   private static final float DEFAULT_EPSMIN = 0.1f;     
   private static final float DEFAULT_EPSPLUS = 0.05f;     
        
   /* Used by GMesh to eliminate  attribute-specific indexing, by expanding the vertexData array,
    * and remapping the data. 
    * Effect: data'[i] == data[map[i]], for i in [0..m-1], where
    *  m = number of distinct vertices 
    * remapped data: jcount, jointIndices, jointWeights.
    */
   protected void remapData(int nrOfDistinctVertices, int[] map) {
      logger.debug("VertexWeights.remapData");
      checkAccumulatedWeights(DEFAULT_EPSMIN, DEFAULT_EPSPLUS, true);
      int[] oldVcount = jcount;
      int[] oldIndices = jointIndices;
      float[] oldWeights = jointWeights;   
      int[] oldOffsets = offsets;
     
      jcount = new int[nrOfDistinctVertices]; 
      for (int i=0; i < nrOfDistinctVertices; i++) {
          jcount[i] = oldVcount[map[i]];  
      }
      offsets = new int[nrOfDistinctVertices];
      int newSize = calculateOffsets();
      jointIndices = new int[newSize];
      jointWeights = new float[newSize];
      for (int i=0; i < nrOfDistinctVertices; i++) {
         int oldOffset = oldOffsets[map[i]];
         int newOffset = offsets[i];
         int vc = jcount[i];
         for (int j=0; j<vc; j++) {
            jointIndices[newOffset+j] = oldIndices[oldOffset+j]; 
            jointWeights[newOffset+j] = oldWeights[oldOffset+j]; 
         }  
      }
      checkAccumulatedWeights(DEFAULT_EPSMIN, DEFAULT_EPSPLUS, true);
   }

   /**
    * Checks whether the accumulated weights for vertices is between  1.0f - epsmin  and 1.0f + epsplus
    * If this is the case for all vertices, the result is true, else, false is returned.
    * Normally, the accumulated weighs should be very close to 1.0.
    */
   public boolean checkAccumulatedWeights(float epsmin, float epsplus, boolean showdeviations) {
      boolean result = true;
      int p = 0;
      for (int i=0; i<jcount.length; i++) {
         float accumulatedWeight = 0.0f;
         for (int j=0; j<jcount[i]; j++) {
            accumulatedWeight += jointWeights[p+j];
         }
         if (accumulatedWeight < 1.0f - epsmin || accumulatedWeight > 1.0f + epsplus) {
            result = false;
            if (showdeviations) {
               
                logger.error("checkAccumulatedWeights, vertex " + i + " accumulated weight: " + accumulatedWeight);
            }
            
         }
         p+= jcount[i];   
      } 
     // if (showdeviations) hmi.util.Console.println(result , "checkAccumulatedWeights OK", "checkAccumulatedWeights NOT OK");
      return result;
   }



   /**
    * Returns a float array filled with vertex colors, encoding the vertex weight information.
    */
   public float[] getVertexWeightColors(boolean useWeights, float[][] colorCoding) {
      int nrOfVertices = jcount.length;
      float[] colors = new float[Vec4f.VEC4F_SIZE*nrOfVertices];
      float[] accuColor = Vec4f.getVec4f();
      int p = 0;
      for (int i=0; i<jcount.length; i++) {
         hmi.math.Vec4f.set(accuColor, 0f, 0f, 0f, 0f);
         for (int j=0; j<jcount[i]; j++) {
            int index = jointIndices[p+j];
            float w = (useWeights) ? jointWeights[p+j] : 1.0f;
            hmi.math.Vec4f.scaleAdd(accuColor, w, colorCoding[index], accuColor);
         }
         hmi.math.Vec4f.set(colors, Vec4f.VEC4F_SIZE*i, accuColor, 0);
         p+= jcount[i];   
      }
      return colors;
   }


   /**
    * Equivalent to toXMLString
    */
   @Override
   public String toString() {
       return toXMLString();  
   }
   
   private static final int JCOUNTSPERLINE = 60;
   private static final int INDICESPERLINE = 60;
   private static final int WEIGHTSPERLINE = 30;


   /** Appends content part of XML encoding */
   @Override
   public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {
      if (jcount != null) {
         appendIntArrayElement(buf,  "jcount", jcount, ' ', fmt, JCOUNTSPERLINE);
      }
      if (jointIndices != null) {
         appendIntArrayElement(buf,  "jointindices", jointIndices, ' ', fmt, INDICESPERLINE);
      }
      if (jointWeights != null) {
         appendFloatArrayElement(buf,  "jointweights", jointWeights, ' ', fmt, WEIGHTSPERLINE);
      }
      return buf;
   }

   /**
    * Decodes content part of XML encoding
    */
   @Override
   public void decodeContent(XMLTokenizer tokenizer) throws IOException {
      while (tokenizer.atSTag()) {
         String tag = tokenizer.getTagName();  
         if (tag.equals("jcount")) {
            int count = getRequiredIntAttribute("count", tokenizer.getAttributes(), tokenizer);
            int[] jc = new int[count];
            tokenizer.takeSTag("jcount");
            decodeIntArray(tokenizer.takeOptionalCharData(), jc);
            setJCounts(jc);
            tokenizer.takeETag("jcount");
         } else if (tag.equals("jointindices")) {
            int count = getRequiredIntAttribute("count", tokenizer.getAttributes(), tokenizer);
            jointIndices = new int[count];
            tokenizer.takeSTag("jointindices");
            decodeIntArray(tokenizer.takeOptionalCharData(), jointIndices);
            tokenizer.takeETag("jointindices");
         } else if (tag.equals("jointweights")) {
            int count = getRequiredIntAttribute("count", tokenizer.getAttributes(), tokenizer);
            jointWeights = new float[count];
            tokenizer.takeSTag("jointweights");
            decodeFloatArray(tokenizer.takeOptionalCharData(), jointWeights);
            tokenizer.takeETag("jointweights");
         } else {
            logger.warn(tokenizer.getErrorMessage("VertexWeights: skip : " + tokenizer.getTagName()));
            tokenizer.skipTag();
         }
      }   
   }   

   /**
    * Writes a binary encoding to dataOut
    */
   public void writeBinary(DataOutput dataOut) throws IOException {
      dataOut.writeUTF(name); 
      BinUtil.writeIntArray(dataOut, jcount);
      BinUtil.writeIntArray(dataOut, jointIndices);
      BinUtil.writeFloatArray(dataOut, jointWeights);
   }
    
   /**
    * Reads a binary encoding from dataIn
    */ 
   public void readBinary(DataInput dataIn) throws IOException {
      name = dataIn.readUTF().intern();  
      setJCounts(BinUtil.readIntArray(dataIn));
      setJointIndices(BinUtil.readIntArray(dataIn));
      setJointWeights(BinUtil.readFloatArray(dataIn));
   } 
    
 
   /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "vertexweights";
 
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
