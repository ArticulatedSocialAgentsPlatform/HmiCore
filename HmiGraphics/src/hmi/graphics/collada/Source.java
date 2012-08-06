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
