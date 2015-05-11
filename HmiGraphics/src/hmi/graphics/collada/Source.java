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

/** 
 * A Collada Source defines an Accessor to some data array, like a FloatArray or IntArray, 
 * and usually includes that data array.
 * It provides access to a &quot;homogeneous&quot; version of that data, i.e. a simple 
 * Java array, without offset or strides. This data corrsponds to the (named) fields specified
 * in the Accessor. 
 * @author Job Zwiers
 */
public class Source extends ColladaElement {

   // attributes: id, name inherited from ColladaElement
   private TechniqueCommonSource techniqueCommon;
   //public Accessor accessor; // only child of techniqueCommon.
   private ArrayList<TechniqueCore> techniques;
   private FloatArray floatArray;
   private IntArray intArray;
   private BoolArray boolArray;
   private NameArray nameArray;
   private IDREFArray idrefArray;
   
   
   /**
    * default constructor
    */
   public Source() {
      super();
   }
   
   /**
    * creates a new Collada Source object, and reconstructs it from XML.
    */
   public Source(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super(collada); 
      readXML(tokenizer); 
   }
 
   /**
    * method that should be called in order to resolve the source data array,
    * before the accessor can be used to actually access data.
    */
   public void resolve() {
      techniqueCommon.getAccessor().resolve();
   }  
    
   /**
    * returns the (resolved) Accessor from the Technique(_Common) child
    */
   public Accessor getAccessor() {
      if (techniqueCommon == null || techniqueCommon.getAccessor() == null) {
          throw new RuntimeException("Source.getAccessor: null technique/accessor");  
      }
      techniqueCommon.getAccessor().resolve();
      return  techniqueCommon.getAccessor();
   }
 
   /**
    * Returns the number of named parameters, as specified by the Accessor for this Source.
    */
   public int getNrOfNamedParams() {
      return  techniqueCommon.getAccessor().getNrOfNamedParams(); 
   }
 
   /**
    * returns the float data from from the Collada FloatArray in a Java float array, as specified
    * by the Accessor of this Source. See the description of Accessor.getHomogeneousFloatData()
    */
   public float[] getHomogeneousFloatData(String[] fieldNames) {
      if (techniqueCommon == null) getCollada().warning("Source " + getId() + " : null technique");
      if (techniqueCommon.getAccessor() == null) getCollada().warning("Source " + getId() + " : null technique.accessor");
      float[] result = techniqueCommon.getAccessor().getHomogeneousFloatData(fieldNames);
      if (result == null) getCollada().warning("Source " + getId() + " : null floats");
      return result;
   }
 
   /**
    * returns the float data from from the Collada FloatArray in a Java float array, as specified
    * by the Accessor of this Source. See the description of Accessor.getHomogeneousFloatData()
    */
   public float[] getHomogeneousFloatData() {
    return getHomogeneousFloatData(null);
   }
 
 
   /**
    * Like getHomogeneousFloatData, but accesses IntArray data
    */
   public int[] getHomogeneousIntData(String[] fieldNames) {
      if (techniqueCommon == null) getCollada().warning("Source " + getId() + " : null technique");
      if (techniqueCommon.getAccessor() == null) getCollada().warning("Source " + getId() + " : null technique.accessor");
      int[] result = techniqueCommon.getAccessor().getHomogeneousIntData(fieldNames);
      if (result == null) getCollada().warning("Source " + getId() + " : null ints");
      return result;
   } 


   /**
    * Like getHomogeneousFloatData, but accesses IntArray data
    */
   public int[] getHomogeneousIntData() {
      return getHomogeneousIntData(null);
   } 

   /**
    * Like getHomogeneousFloatData, but accesses BoolArray data
    */
   public boolean[] getHomogeneousBoolData() {
      return techniqueCommon.getAccessor().getHomogeneousBoolData();
   } 
   
   /**
    * Like getHomogeneousFloatData, but accesses NameArray data
    */
   public String[] getHomogeneousNameData() {
      String[] result = techniqueCommon.getAccessor().getHomogeneousNameData();
      return result;
   } 

   /**
    * Like getHomogeneousFloatData, but accesses IDREFArray data
    */
   public String[] getHomogeneousIDREFData() {
      return techniqueCommon.getAccessor().getHomogeneousIDREFData();
   } 

   @Override
   public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {  
      appendXMLStructure(buf, fmt, floatArray);   
      appendXMLStructure(buf, fmt, intArray); 
      appendXMLStructure(buf, fmt, boolArray);
      appendXMLStructure(buf, fmt, nameArray); 
      appendXMLStructure(buf, fmt, idrefArray); 
      appendXMLStructure(buf, fmt, techniqueCommon);
      appendXMLStructureList(buf, fmt, techniques);
      return buf;  
   }

   @Override
   public void decodeContent(XMLTokenizer tokenizer) throws IOException {
      while (tokenizer.atSTag()) {
         String tag = tokenizer.getTagName();
         if (tag.equals(TechniqueCore.xmlTag()))  {
               if (techniques == null) techniques = new ArrayList<TechniqueCore>(2);
               techniques.add(new TechniqueCore(getCollada(), tokenizer));
         } else if (tag.equals(TechniqueCommonSource.xmlTag()))  techniqueCommon = new TechniqueCommonSource(getCollada(), tokenizer);
         else if (tag.equals(FloatArray.xmlTag()))       floatArray = new FloatArray(getCollada(), tokenizer);
         else if (tag.equals(IntArray.xmlTag()))         intArray = new IntArray(getCollada(), tokenizer);
         else if (tag.equals(BoolArray.xmlTag()))        boolArray = new BoolArray(getCollada(), tokenizer);
         else if (tag.equals(NameArray.xmlTag()))        nameArray = new NameArray(getCollada(), tokenizer);
         else if (tag.equals(IDREFArray.xmlTag()))       idrefArray = new IDREFArray(getCollada(), tokenizer);                 
         else {         
            getCollada().warning(tokenizer.getErrorMessage("Source: skip : " + tokenizer.getTagName()));
            tokenizer.skipTag();
         }
      }     
      addColladaNode(techniqueCommon);
      addColladaNodes(techniques);
      addColladaNode(floatArray);
      addColladaNode(intArray);
      addColladaNode(boolArray);
      addColladaNode(nameArray);
      addColladaNode(idrefArray);
      getCollada().addSource(getId(), this);
      
   }

 
   /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "source";
 
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
