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

package hmi.graphics.scenegraph;

import hmi.math.Mat3f;
import hmi.math.Mat4f;
import hmi.math.Vec3f;
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
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 
/**
 * A VertexAttribute defines the data for a single mesh vertex attribute, which can
 * be either a predefined OpenGL attribute or a user-defined GLSL attribute.
 * A VertexAttribute can be indexed data, in which case the index is specific
 * for this VertexAttribute. (The alternative is to maintain a common index in GMesh,
 * in which case the VertexData-specific index is removed, by means of the deindexify method.
 * The plain vertex data is kept in an float array; the actual list size is are returned by 
 * the getVertexDataSize method (less then or equal to  the  the physical vertexData array length)
 * A single attribute value will (usually) occupy between 1 and 4 floats. The actual number
 * is kept as the attributeValueSize.
 * The number of distinct attribute values is returned by getNrOfValues(). 
 * It is by definition equal to vertexDataSize/attributeValueSize;
 * The index data is kept in an int array, where the list size is returned by getNrOfIndices(),
 * which is less than or equal to the physical length of the int array returned by getIndices().
 * The vertexData and indexData arrays can be set directly, or thay can be modified via the 
 * addAttributeValuef* and addIndex methods. (These methods take care of allocating
 * and (if necessary) resizing the data arrays.) 
 * @author Job Zwiers
 */
public class VertexAttribute extends XMLStructureAdapter implements BinaryExternalizable, Diff.Differentiable {
   /* The OpenGL or user-defined/GLSL attribute name: */
   private String name;    
   /* The actual attribute floating point data for this attribute: */
   private float[] vertexData;
   /* The size of the vertex data, in number of floats: */
   private int vertexDataSize;
   /* The size of the index data, in number of indices used: */
   private int indexDataSize;
   /* (Optional) indices into the vertexData array: */
   private int[] indexData;    
   /* The number of floats per attribute value: */
   private int attributeValueSize;  
 
   private static Logger logger = LoggerFactory.getLogger(VertexAttribute.class.getName());
 
   /**
    * show differences
    */
   public String showDiff(Object vaObj) {
      VertexAttribute va = (VertexAttribute) vaObj;
      if (va==null) return "VertexAttribute " + name + ", diff: null VertexAttribute";
      String diff = Diff.showDiff("VertexAttribute", name, va.name);
      if (diff != "") return diff;
      diff = Diff.showDiff("VertexAttribute " + name + ", diff vertexData", vertexData, va.vertexData);
      if (diff != "") return diff;
      diff = Diff.showDiff("VertexAttribute " + name + ", diff indexData", indexData, va.indexData);
      if (diff != "") return diff;
      diff = Diff.showDiff("VertexAttribute " + name + ", diff attributeValueSize", attributeValueSize, va.attributeValueSize);
      if (diff != "") return diff;
      diff = Diff.showDiff("VertexAttribute " + name + ", diff vertexDataSize", vertexDataSize, va.vertexDataSize);
      if (diff != "") return diff;
      diff = Diff.showDiff("VertexAttribute " + name + ", diff indexDataSize", indexDataSize, va.indexDataSize);   
      if (diff != "") return diff; 
      return "";
   }
   
   public VertexAttribute() {
      name="";
      attributeValueSize = -1;
      vertexDataSize = -1;
      indexDataSize = -1;  
   }
   
   /** 
    * The constructor, defining the attribute name.
    * The atributeValueSize is not defined, and
    * no vertexData or indexData is allocated.
    */
   public VertexAttribute(String name) {   
      this.name = name; 
      attributeValueSize = -1;
      vertexDataSize = -1;
      indexDataSize = -1;  
    
   }
   
   /** 
    * The constructor, defining the attribute name 
    * and the size of a single attribute value, in number of floats.
    * No vertexData or indexData is allocated.
    */
   public VertexAttribute(String name, int attributeValueSize) {   
      this.name = name; 
      this.attributeValueSize = attributeValueSize;
      vertexDataSize = -1;
      indexDataSize = -1;  
   }
   
   /** 
    * The constructor, defining the attribute name 
    * and the size of a single attribute value, in number of floats.
    * No indexData is allocated.
    */
   public VertexAttribute(String name, int attributeValueSize, float[] vertexData) {   
      this.name = name; 
      this.attributeValueSize = attributeValueSize;
      this.vertexData = vertexData;
      vertexDataSize = vertexData.length;
      indexDataSize = -1;  
   }
   
   /** 
    * The constructor, defining the attribute name 
    * and the size of a single attribute value, in number of floats.
    */
   public VertexAttribute(String name, int attributeValueSize, float[] vertexData, int[] indexData) {   
      this.name = name; 
      this.attributeValueSize = attributeValueSize;
      this.vertexData = vertexData;
      vertexDataSize = vertexData.length;
      this.indexData = indexData;
      indexDataSize = indexData.length;  
   }
   
   
   /**
    * Reads VertexAttribute from an XML scenegraph encoding
    */
   public VertexAttribute(XMLTokenizer tokenizer) throws IOException {
       this();
       readXML(tokenizer);  
   }
   
   /**
    * sets the attribute name
    */
   public void setName(String name) {
      this.name = name;  
   }
   
   /**
    * Returns the attribute name
    */
   public String getName() {
      return name;  
   }
   
   /**
    * Returns the attribute value size.
    * This value is usually 1, 2, or 4.
    */
   public int getAttributeValueSize() {
      return attributeValueSize;  
   }
   
   /**
    * Sets the attribute value size
    */
   public void setAttributeValueSize(int attributeValueSize) {
      this.attributeValueSize = attributeValueSize;  
   }
      

   /**
    * Sets the vertexData array.
    * Also sets vertexDataSize to vertexData.length
    */
   public void setVertexData(float[] vertexData) {
      this.vertexData = vertexData;
      this.vertexDataSize = (vertexData == null) ? -1 : vertexData.length;  
   }  
     
   /**
    * Returns the vertexData array.
    */
   public float[] getVertexData() {
      return vertexData;  
   }
   
   /**
    * Returns the size of the vertexData. (<= vertexData.length)
    */
   public int getVertexDataSize() {
      return vertexDataSize;
   }
   
   /**
    * Returns the number of vertex data values,
    * that is, vertexDataSize / attributeValueSize
    */
   public int getNrOfValues() {
      if (vertexDataSize < 0 || attributeValueSize < 0) return -1;
      return vertexDataSize/attributeValueSize;
   }  
     
   
   /**
    * Sets the indexData array.
    * Also sets indexDataSize to indexData.length
    */
   public void setIndexData(int[] indexData) {
      this.indexData = indexData;
      this.indexDataSize = (indexData==null) ? -1 : indexData.length;  
   }    
      
   /**
    * Add an index to the indexData array;
    */
   public void addIndex(int idx) {
      ensureIndexDataArraySize(indexDataSize+1);
      indexData[indexDataSize] = idx; 
      indexDataSize+=1;
   }
   
     
   /**
    * Returns true iff this VertexAttribute has (attribute-specific) index data.
    */
   public boolean hasIndex() {
      return indexData != null;
   }
   
   /**
    * Returns the indexData array.
    */
   public int[] getIndexData() {
       return indexData;  
   }
   
   /**
    * Returns the number of indices, that is, size of the indexData. (<= indexData.length)
    */
   public int getNrOfIndices() {
       return indexDataSize;
   }
   
   /**
    * Add a single float to the vertexData array;
    */
   public void addAttributeValue1f(float x) {
      ensureVertexDataArraySize(vertexDataSize+1);
      vertexData[vertexDataSize] = x; 
      vertexDataSize+=1;
   }
   
   /**
    * Add a two floats to the vertexData array;
    */
   public void addAttributeValue2f(float x, float y) {
      ensureVertexDataArraySize(vertexDataSize+2);
      vertexData[vertexDataSize]   = x; 
      vertexData[vertexDataSize+1] = y;
      vertexDataSize+=2;
   }
   
   /**
    * Add a three floats to the vertexData array;
    */
   public void addAttributeValue3f(float x, float y, float z) {
      ensureVertexDataArraySize(vertexDataSize+Vec3f.VEC3F_SIZE);
      vertexData[vertexDataSize]   = x; 
      vertexData[vertexDataSize+1] = y;
      vertexData[vertexDataSize+2] = z;
      vertexDataSize+=Vec3f.VEC3F_SIZE;
   }
   
   /**
    * Add a four floats to the vertexData array;
    */
   public void addAttributeValue4f(float x, float y, float z, float w) {
      ensureVertexDataArraySize(vertexDataSize+Vec4f.VEC4F_SIZE);
      vertexData[vertexDataSize]   = x; 
      vertexData[vertexDataSize+1] = y;
      vertexData[vertexDataSize+2] = z;
      vertexData[vertexDataSize+3] = w;
      vertexDataSize+=Vec4f.VEC4F_SIZE;
   }  
      
   /*
    * Check and if necessary (re)allocate vertexData array, guaranteeing  vertexData.length >= requestedSize 
    */  
   private void ensureVertexDataArraySize(int requestedSize) {
      if (vertexData != null && vertexData.length >= requestedSize) return;
      if (vertexData == null) { // initialize, set vertexDataSize to 0
          vertexData = new float[requestedSize];
          vertexDataSize = 0;
      } else { // reallocate in new array with double the length. vertexDataSize unchanged.
         float[] oldVertexData = vertexData;
         vertexData = new float[2*oldVertexData.length];
         System.arraycopy(oldVertexData, 0, vertexData, 0, vertexDataSize);
      }
   }     
   
   /* 
    * Check and if necessary (re)allocate indexData array, guaranteeing  indexData.length >= requestedSize 
    */
   private void ensureIndexDataArraySize(int requestedSize) {
      if (indexData != null && indexData.length >= requestedSize) return;
      if (indexData == null) { // initialize, set indexDataSize to 0
          indexData = new int[requestedSize];
          indexDataSize = 0;
      } else { // reallocate in new array with double the length. indexDataSize unchanged.
         int[] oldIndexData = indexData;
         indexData = new int[2*oldIndexData.length];
         System.arraycopy(oldIndexData, 0, indexData, 0, indexDataSize);
      }
   }       
         
   /* Used by GMesh to eliminate  attribute-specific indexing, by expanding the vertexData array,
    * and remapping the data. 
    * The vertexData array will be completely filled with actual data, i.e. vertexDataSize == vertexData.length
    * Effect: vertexData'[j] == vertexData[map[j]], for j in [0..m-1], where
    *  m = number of distinct vertices 
    */
   protected void remapData(int nrOfDistinctVertices, int[] map) {
      float[] oldVertexData = vertexData;
      logger.debug("deIndexify " + name + " old size = " + vertexDataSize + " new size = " + (nrOfDistinctVertices * attributeValueSize));
      vertexDataSize = nrOfDistinctVertices * attributeValueSize;    
      vertexData = new float[vertexDataSize];
      for (int i=0; i<nrOfDistinctVertices; i++) {
          int offset = i*attributeValueSize;
          int offsetOld = map[i]*attributeValueSize;
          for (int p=0; p<attributeValueSize; p++) {
              vertexData[offset+p] = oldVertexData[offsetOld+p]; 
          }  
      }
   }

   /**
    * Transforms the attribute values, using the specified 4x4 matrix.
    * It is assumed that the attribute size is either 3, or 4.
    * In the latter case, the 4th value is not modified; basically we assume that
    * it is the 1.0 value for homogeneous coordinates. 
    * The length 3 vectors are transformed by means of the rotation/scaling part of m4x4, and translated
    * by the elements from the last column. The bottom row of m4x4 is ignored, and need not even
    * be present. (I.e. m4x4 can be a 4x4 matrix, of length 16, or a 3x4 matrix, of length 12)
    */
   public void affineTransform(float[] mat4X4) {       
      // only transform first three elements per coordinate
      int nrOfValues = getNrOfValues();
      for (int i=0; i<nrOfValues; i++) {
          int offset = i*attributeValueSize;
          Mat4f.transformPoint(mat4X4, vertexData, offset);
      }
   }

   /**
    * Transforms the attribute values, using the specified 3x3 matrix.
    * It is assumed that the attribute size is 3.
    */
   public void linearTransform(float[] mat3X3) {
      // only transform first three elements per coordinate
      int nrOfValues = getNrOfValues();
      for (int i=0; i<nrOfValues; i++) {
          int offset = i*attributeValueSize;
          Mat3f.transformVec3f(mat3X3, vertexData, offset);
      }
   }

   
  
   


   /**
    * appends the id and sid XML attributes to buf.
    */
   @Override
   public String toString() {
       return toXMLString();  
   }

   private static boolean showVertexAttributeData = true;
   
   public static void setShowVertexAttributeData(boolean show) {
      showVertexAttributeData = show;
   }

   public static boolean showVertexAttributeData() {
      return showVertexAttributeData;
   }

   private static final int DATAITEMSPERLINE = 30;
   private static final int INDICESPERLINE = 30;
  


   /**
    * appends the  attributes to buf.
    */
   @Override
   public StringBuilder appendAttributeString(StringBuilder buf,  XMLFormatting fmt) {
      appendAttribute(buf, "name", name);
      appendAttribute(buf, "count", getNrOfValues());   
      appendAttribute(buf, "size", attributeValueSize);
      if (indexDataSize >= 0) appendAttribute(buf, "indexcount", indexDataSize);
      return buf;
   } 
    
   /**
    * decodes the attributes. 
    */
   @Override
   public void decodeAttributes(HashMap<String, String> attrMap, XMLTokenizer tokenizer) {      
      name  = getRequiredAttribute("name", attrMap, tokenizer);
      if (name != null) name = name.intern();
      int count = getRequiredIntAttribute("count", attrMap, tokenizer);
      attributeValueSize = getRequiredIntAttribute("size", attrMap, tokenizer);
      vertexDataSize = count * attributeValueSize;
      indexDataSize = getOptionalIntAttribute("indexcount", attrMap, -1);
      super.decodeAttributes(attrMap, tokenizer);
   }

   /**
    * Appends the attribute values to an XML encoding
    */
   @Override
   public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {
      if (vertexData != null) {
          buf.append('\n'); 
          appendSpaces(buf, fmt);
          appendFloats(buf, vertexData, ' ', fmt, DATAITEMSPERLINE);
      }
      return buf;
   }
 
 
   /**
    * Decodes the attribute values from an XML encoding
    */
   @Override
   public void decodeContent(XMLTokenizer tokenizer) throws IOException {
      vertexData = decodeFloatArray(tokenizer.takeCharData());
   }

 
   /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "vertexattribute";
 
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
 
   /**
    * Writes a binary encoding to dataOut
    */
   public void writeBinary(DataOutput dataOut) throws IOException {
      dataOut.writeUTF(name);  
      BinUtil.writeIntArray(dataOut, indexData);
      dataOut.writeInt(attributeValueSize);
      BinUtil.writeFloatArray(dataOut, vertexData);
   }
    
   /**
    * Reads a binary encoding from dataIn
    */ 
   public void readBinary(DataInput dataIn) throws IOException {
      name = dataIn.readUTF().intern();  
      setIndexData(BinUtil.readIntArray(dataIn));
      attributeValueSize = dataIn.readInt();
      setVertexData(BinUtil.readFloatArray(dataIn));
   } 
      
} 
