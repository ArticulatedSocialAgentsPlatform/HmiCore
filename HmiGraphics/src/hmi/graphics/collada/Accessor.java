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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/** 
 * Collada Accessor nodes provide access to some data array, like a FloatArray or an IntArray.
 * The actual type of the accessor is determined from the type of its array. We assume that parameters
 * have the same type. 
 * The data array must first be &quot;resolved&quot; before it can be accessed, by calling
 * the resolve() method. 
 * @author Job Zwiers
 */
public class Accessor extends ColladaElement {
 
   private int count, offset, stride;
   private String sourceURL; // N.B. NOT the url of a Collada *Source*, but rather of a *float_array* or ...
   private int arrayType;
   private String arrayId;
   
   
   // depending on type, one of these will be used:
   private float[] floats;
   private int[] ints;
   private boolean[] bools;
   private String[] names;
   private String[] idrefs;
   
   private boolean resolved = false;
   
   private static final int LISTSIZE = 4;
   private ArrayList<Param> params = new ArrayList<Param>(LISTSIZE);
   private int nrOfNamedParams; // number of parameters with defined name attribute (unnamed parameters are to be ignored)
   private int[] paramOffsets;      // offsets of *named* parameters inside a single element (parameters without name are to be ignored)
   private int[] paramSizes;        // size, in number of basic elements, per parameter
      
      
   
   // some field names, like x, y, z, s, t etc. are treated as special cases, for speedup. 
   // This often avoids allocating the fieldOffsets and fieldSizes Maps.      
   private int xOffset = -1; // 1st coord
   private int yOffset = -1; // 2nd coord
   private int zOffset = -1; // 3rd coord
   private int wOffset = -1; // 4th coord
   //private int uOffset = -1; // 1st generic
   //private int vOffset = -1; // 2nd generic
   private int sOffset = -1; // 1st texture
   private int tOffset = -1; // 2nd texture
   private int pOffset = -1; // 3rd texture
   private int qOffset = -1; // 4th texture
   private int aOffset = -1; // alpha
   private int rOffset = -1; // red
   private int gOffset = -1; // green 
   private int bOffset = -1; // blue
   
   // The "size" of most fields is just 1. In particular for the following "special cases": 
   private static final String FLOATFIELDNAMES = "abgpqrstuvwxyz";
   
   // All other "non-special" cases are stored in Maps:   
   private Map<String, Integer> fieldOffsets;  // ofssets
   private Map<String, Integer> fieldSizes;    // sizes
   
   // temp vars for the getHomogeneousX() methods:   
   private int nrOfPars;
   private int[] parOffsets;  
   private int[] parSizes;   
   private int blockSize;
   private int totalLength;
   
   
   /**
    * Default constructor
    */     
   public Accessor() {
      super();
   }

   /**
    * Constructor used to create an Accessor Object from XML
    */         
   public Accessor(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super(collada); 
      readXML(tokenizer); 
   }
   
   /**
    * method that should be called in order to resolve the source data array,
    * before the accessor can be used to actually access data.
    */
   public void resolve() {
      resolved = true;
      if (arrayId != null) return;
      arrayId = urlToId(sourceURL);
      arrayType = getCollada().getArrayType(arrayId);
      if (arrayType == 0) {
         throw new RuntimeException("Accessor with undefined data array/type");   
      }
      if (arrayType == Collada.FLOAT ) {
           floats = getCollada().getFloatArray(arrayId);  
       } else if (arrayType == Collada.INT ) {
           ints = getCollada().getIntArray(arrayId);
       } else if (arrayType == Collada.BOOL ) {
           bools = getCollada().getBoolArray(arrayId);
       } else if (arrayType == Collada.NAME ) {
           names = getCollada().getNameArray(arrayId);
       } else if (arrayType == Collada.IDREF ) {
           idrefs = getCollada().getIDREFArray(arrayId);
       } else {
           getCollada().warning("Accessor: don't know how to resolve for array " + arrayId + " type : " + arrayType);
       }  
   }
   
   /**
    * Returns the number of named parameters for this Accessor
    */
   public int getNrOfNamedParams() {
      return nrOfNamedParams;
   }


   // common calculation of vars for getHomogeneousX() methods.
   private void calcTmpVars(String[] fieldNames) {
      if ( ! resolved ) resolve();
      
      if (fieldNames != null) {
         nrOfPars = fieldNames.length;
         parOffsets = getFieldOffsets(fieldNames);
         parSizes = getFieldSizes(fieldNames);
      } else {
         nrOfPars = nrOfNamedParams;
         parOffsets = paramOffsets;
         parSizes = paramSizes;      
      }       
      blockSize = 0;    
      for (int i=0; i<nrOfPars; i++) {
         blockSize += parSizes[i];
      }       
      totalLength = count * blockSize;
  }


   /**
    * returns the float data from from the Collada FloatArray in a Java float array.
    * The number of floats equals the number of (named) parameters times the &quot;count&quot; 
    * attribute of this accessor.
    * The FloatArray is accessed in accordance with the offset and stride attributes of this Accessor;
    * No such offset and stride apply to the destination, i.e. the result is a simple,
    * homogeneous array of consecutive data.
    * If the result turns out to be an exact copy of the floats FloatArray, a reference to the floats
    * array of the latter is returned. Otherwise, a new float array is allocated, and the requied
    * data is copied.
    */
   public float[] getHomogeneousFloatData(String[] fieldNames) {
      calcTmpVars(fieldNames);
      if (offset == 0 && stride == blockSize && totalLength == floats.length) {
          // easy case: we need all of floats.
          return Arrays.copyOf(floats, floats.length);           
      }
      // messy case: either we have non trivial offset and/or stride, or maybe there are unnamed Params.
      float[] result = new float[totalLength];
      int resultCount=0;
      for (int index=0; index<count; index++) {
         int base = offset + index*stride;
         for (int p=0; p<nrOfPars; p++) {
            int psize = parSizes[p];
            for (int i=0; i<psize; i++) {
               result[resultCount+i] = floats[base + parOffsets[p] + i];
            }
            resultCount += psize;
         }
      }
      return result;
   }


   /**
    * Like getHomogeneousFloatData(null);
    */
   public float[] getHomogeneousFloatData() {
      return getHomogeneousFloatData(null);
   }
   
   
   /**
    * Like getHomogeneousFloatData, but accesses IntArray data.
    */
   public int[] getHomogeneousIntData(String[] fieldNames) {
      calcTmpVars(fieldNames);
      if (offset == 0 && stride == blockSize && totalLength == ints.length) {
          return Arrays.copyOf(ints, ints.length);            
      }
      // messy case: either we have non trivial offset and/or stride, or maybe there are unnamed Params.
      int[] result = new int[totalLength];
      int resultCount=0;
      for (int index=0; index<count; index++) {
         int base = offset + index*stride;
         for (int p=0; p<nrOfPars; p++) {
            int psize = parSizes[p];
            for (int i=0; i<psize; i++) {
               result[resultCount+i] = ints[base + parOffsets[p] + i];
            }
            resultCount += psize;
         }
      }
      return result;
   }
   
   /**
    * Like getHomogeneousFloatData, but accesses BoolArray data.
    */
   public boolean[] getHomogeneousBoolData(String[] fieldNames) {
      calcTmpVars(fieldNames);
      if (offset == 0 && stride == blockSize && totalLength == bools.length) {
          return Arrays.copyOf(bools, bools.length);           
      }
      // messy case: either we have non trivial offset and/or stride, or maybe there are unnamed Params.
      boolean[] result = new boolean[totalLength];
      int resultCount=0;
      for (int index=0; index<count; index++) {
         int base = offset + index*stride;
         for (int p=0; p<nrOfPars; p++) {
            int psize = parSizes[p];
            for (int i=0; i<psize; i++) {
               result[resultCount+i] = bools[base + parOffsets[p] + i];
            }
            resultCount += psize;
         }
      }
      return result;
   }


   /**
    * Like getHomogeneousFloatData, but accesses BoolArray data.
    */
   public boolean[] getHomogeneousBoolData() {
       return getHomogeneousBoolData(null);
   }

   /**
    * Like getHomogeneousFloatData, but accesses NameArray data.
    */
   public String[] getHomogeneousNameData(String[] fieldNames) {
      calcTmpVars(fieldNames);
      if (offset == 0 && stride == blockSize && totalLength == names.length) {
         String[] result = Arrays.copyOf(names, names.length);
         return result;            
      }
      // messy case: either we have non trivial offset and/or stride, or maybe there are unnamed Params.
      String[] result = new String[totalLength];
      int resultCount=0;
      for (int index=0; index<count; index++) {
         int base = offset + index*stride;
         for (int p=0; p<nrOfPars; p++) {
            int psize = parSizes[p];
            for (int i=0; i<psize; i++) {
               result[resultCount+i] = names[base + parOffsets[p] + i];
            }
            resultCount += psize;
         }
      }
      return result;
   }


   /**
    * Like getHomogeneousFloatData, but accesses NameArray data.
    */
   public String[] getHomogeneousNameData() {
       return getHomogeneousNameData(null);
   }

   /**
    * Like getHomogeneousFloatData, but accesses IDREFArray data.
    */
   public String[] getHomogeneousIDREFData(String[] fieldNames) {
      calcTmpVars(fieldNames);
      if (offset == 0 && stride == blockSize && totalLength == idrefs.length) {
          return Arrays.copyOf(idrefs, idrefs.length);           
      }
      // messy case: either we have non trivial offset and/or stride, or maybe there are unnamed Params.
      String[] result = new String[totalLength];
      int resultCount=0;
      for (int index=0; index<count; index++) {
         int base = offset + index*stride;
         for (int p=0; p<nrOfPars; p++) {
            int psize = parSizes[p];
            for (int i=0; i<psize; i++) {
               result[resultCount+i] = idrefs[base + parOffsets[p] + i];
            }
            resultCount += psize;
         }
      }
      return result;
   }

   /**
    * Like getHomogeneousFloatData, but accesses IDREFArray data.
    */
   public String[] getHomogeneousIDREFData() {
      return idrefs;
      // return  getHomogeneousIDREFData(null);
   }
     
   
   

   /*
    * returns the offset for a named field, to be used with one of the methods like getFloatField(offset, index)
    * If the field is not defined, -1 is returned.
    */
   private int getFieldOffset(String fieldName) {
      if (fieldName.equals("x") || fieldName.equals("X")) {
         return xOffset;
      } else if (fieldName.equals("y") || fieldName.equals("Y")) {
         return yOffset;
      } else if (fieldName.equals("z") || fieldName.equals("Z")) {
         return zOffset;
      } else if (fieldName.equals("w") || fieldName.equals("W")) {
         return wOffset;
      } else if (fieldName.equals("s") || fieldName.equals("S")) {
         return sOffset;
      } else if (fieldName.equals("t") || fieldName.equals("T")) {
         return tOffset;
      } else if (fieldName.equals("p") || fieldName.equals("P")) {
         return pOffset;
      } else if (fieldName.equals("q") || fieldName.equals("Q")) {
         return qOffset;
      } else if (fieldName.equals("r") || fieldName.equals("R")) {
         return rOffset;
      } else if (fieldName.equals("g") || fieldName.equals("G")) {
         return gOffset;
      } else if (fieldName.equals("b") || fieldName.equals("B")) {
         return bOffset;
      } else if (fieldName.equals("a") || fieldName.equals("A")) {
         return aOffset;
      } else {      
         Integer fo = fieldOffsets.get(fieldName);
         if (fo==null) {
            return -1;
         } 
         return fo;          
      }   
   }
   
   /*
    * returns the size for a named field
    * If the field is not defined, -1 is returned.
    */
   private int getFieldSize(String fieldName) {
      if (fieldName.length() == 1 && FLOATFIELDNAMES.indexOf(fieldName.charAt(0)) >= 0) {
          return 1;  
      } else {      
         Integer fs = fieldSizes.get(fieldName);
         if (fs==null) {
            return -1;
         } 
         return fs;          
      }   
   }
   
   private static final int[] EMPTYFIELD_OFFSETS = new int[0];
   private static final int[] EMPTYFIELD_SIZES = new int[0];
   
   /*
    * returns an array of field offsets, that corresponds to the array of field names.
    * To be used in combination with methods like getHomogeneousFloatData
    */
   private int[] getFieldOffsets(String[] fieldNames) {
       if (fieldNames == null) return EMPTYFIELD_OFFSETS;
       int[] fieldOffsets = new int[fieldNames.length];
       for (int fn=0; fn<fieldNames.length; fn++) {
          fieldOffsets[fn] =  getFieldOffset(fieldNames[fn]);
       }
       return fieldOffsets;
   }   
      
   /*
    * returns an array of field offsets, that corresponds to the array of field names.
    * To be used in combination with methods like getHomogeneousFloatData
    */
   private int[] getFieldSizes(String[] fieldNames) {
       if (fieldNames == null) return EMPTYFIELD_SIZES;
       int[] fieldSizes = new int[fieldNames.length];
       for (int fn=0; fn<fieldNames.length; fn++) {
          fieldSizes[fn] =  getFieldSize(fieldNames[fn]);
       }
       return fieldSizes;
   }       
      
 
   /**
    * appends a String of attributes to buf.
    */
   @Override
   public StringBuilder appendAttributes(StringBuilder buf) {
      super.appendAttributes(buf);
      appendAttribute(buf, "count", count);
      if (offset > 0) appendAttribute(buf, "offset", offset);
      if (stride > 1) appendAttribute(buf, "stride", stride);
      appendAttribute(buf, "source", sourceURL);
      return buf;
   }

   /**
    * decodes the XML attributes
    */
   @Override
   public void decodeAttributes(HashMap<String, String> attrMap, XMLTokenizer tokenizer) {      
      count     = getRequiredIntAttribute("count", attrMap, tokenizer);
      offset    = getOptionalIntAttribute("offset", attrMap, 0);
      stride    = getOptionalIntAttribute("stride", attrMap, 1);
      sourceURL = getRequiredAttribute("source", attrMap, tokenizer);
      super.decodeAttributes(attrMap, tokenizer);
   }
 
   @Override
   public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {
      appendXMLStructureList(buf, fmt, params);
      return buf;  
   }

   @Override
   public void decodeContent(XMLTokenizer tokenizer) throws IOException {
      while (tokenizer.atSTag()) {
         String tag = tokenizer.getTagName();
         if (tag.equals(Param.xmlTag()))  {  
             Param param = new Param(getCollada(), tokenizer); 
             params.add(param);
             if (param.getName() != null) nrOfNamedParams++;
             
         } else {         
            getCollada().warning(tokenizer.getErrorMessage("Accessor: skip : " + tokenizer.getTagName()));
            tokenizer.skipTag();
         }
      }   
      addColladaNodes(params);  
      if (nrOfNamedParams == 0) {
         getCollada().warning(tokenizer.getErrorMessage("Warning: Collada Accessor with no (named) parameters)"));  
      }
      paramOffsets = new int[nrOfNamedParams];
      paramSizes = new int[nrOfNamedParams];
      int pcount = 0;
      int offset = 0;
      for (int parIndex=0; parIndex<params.size(); parIndex++) { // we need the parIndex
         Param param = params.get(parIndex);    
         String parName = param.getName();
         if (parName != null) {
            paramOffsets[pcount] = offset;
            paramSizes[pcount] = param.getSize();
            if (parName.equals("x") || parName.equals("X")) {
               xOffset = offset;
            } else if (parName.equals("y") || parName.equals("Y")) {
               yOffset = offset;
            } else if (parName.equals("z") || parName.equals("Z")) { 
               zOffset = offset;
            } else if (parName.equals("w") || parName.equals("W")) { 
               wOffset = offset;
            } else if (parName.equals("s") || parName.equals("S")) { 
               sOffset = offset;
            } else if (parName.equals("t") || parName.equals("T")) { 
               tOffset = offset;
            } else if (parName.equals("p") || parName.equals("P")) { 
               pOffset = offset;
            } else if (parName.equals("q") || parName.equals("Q")) { 
               qOffset = offset;
            } else if (parName.equals("r") || parName.equals("R")) { 
               rOffset = offset;
            } else if (parName.equals("g") || parName.equals("G")) { 
               gOffset = offset;
            } else if (parName.equals("b") || parName.equals("B")) { 
               bOffset = offset;
            } else if (parName.equals("a") || parName.equals("A")) { 
               aOffset = offset;
            } else {
               if (fieldOffsets == null) fieldOffsets = new HashMap<String, Integer>();
               fieldOffsets.put(parName, offset);
               if (fieldSizes == null) fieldSizes = new HashMap<String, Integer>();
               fieldSizes.put(parName, paramSizes[pcount]);
            }    
            offset += paramSizes[pcount];
            pcount++;       
         } 
//         else {
//             //Collada.warning("Warning: Accessor with unnamed parameter: skipped");  
//         }
      }
   }
 
   /**
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "accessor";

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
