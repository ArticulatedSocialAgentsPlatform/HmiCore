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
import java.util.HashSet;
import java.util.Set;

/** 
 * ValueType defines a scalar, vector, or matrix.
 * @author Job Zwiers
 */
public class ValueType extends ColladaElement {
   
   // attributes: sid, inherited from ColladaElement.
   // one of these will be used:
   private boolean[] bools;
   private int[] ints;
   private float[] floats;
   private String string;
   private int size; // size of the bools/ints/floats array
           
   /** A valuetype has a BaseType, like Int, Float etc */        
   public enum BaseType {Bool, Int, Float, String, None};
   private BaseType baseType; // determines which of the arrays is actually used   
   
   
   
   
   public BaseType getBaseType() {
      return baseType;
   }
   
   public int getSize() {
      return size;
   }
   
   public boolean[] getBools() {
      return bools;
   }
   
   public int[] getInts() {
      return ints;
   }
   
   public float[] getFloats() {
      return floats;
   }
   
   public String getString() {
      return string;
   }
   
   public ValueType() {
      super();
   }
   
   private static final int VEC2_SIZE = 2;
   private static final int VEC3_SIZE = 3;
   private static final int VEC4_SIZE = 4;
   
   private static final int MAT1X1_SIZE = 1;
   private static final int MAT1X2_SIZE = 2;
   private static final int MAT1X3_SIZE = 3;
   private static final int MAT1X4_SIZE = 4;
   
   private static final int MAT2X1_SIZE = 2;
   private static final int MAT2X2_SIZE = 4;
   private static final int MAT2X3_SIZE = 6;
   private static final int MAT2X4_SIZE = 8;
   
   private static final int MAT3X1_SIZE = 3;
   private static final int MAT3X2_SIZE = 6;
   private static final int MAT3X3_SIZE = 9;
   private static final int MAT3X4_SIZE = 12;
   
   private static final int MAT4X1_SIZE = 4;
   private static final int MAT4X2_SIZE = 8;
   private static final int MAT4X3_SIZE = 12;
   private static final int MAT4X4_SIZE = 16;
   
   
   public ValueType(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super();      
      xmlTag = tokenizer.getTagName();
      if (xmlTag.equals("bool"))          { baseType = BaseType.Bool;   size = 1; }     
      else if (xmlTag.equals("bool2"))    { baseType = BaseType.Bool;   size = VEC2_SIZE; }         
      else if (xmlTag.equals("bool3"))    { baseType = BaseType.Bool;   size = VEC3_SIZE; }         
      else if (xmlTag.equals("bool4"))    { baseType = BaseType.Bool;   size = VEC4_SIZE; }     
      else if (xmlTag.equals("int"))      { baseType = BaseType.Int;    size = 1; }      
      else if (xmlTag.equals("int2"))     { baseType = BaseType.Int;    size = VEC2_SIZE; }       
      else if (xmlTag.equals("int3"))     { baseType = BaseType.Int;    size = VEC3_SIZE; }     
      else if (xmlTag.equals("int4"))     { baseType = BaseType.Int;    size = VEC4_SIZE; }     
      else if (xmlTag.equals("float"))    { baseType = BaseType.Float;  size = 1; }     
      else if (xmlTag.equals("float2"))   { baseType = BaseType.Float;  size = VEC2_SIZE; }    
      else if (xmlTag.equals("float3"))   { baseType = BaseType.Float;  size = VEC3_SIZE; }     
      else if (xmlTag.equals("float4"))   { baseType = BaseType.Float;  size = VEC4_SIZE; }  
      else if (xmlTag.equals("float1x1")) { baseType = BaseType.Float;  size = MAT1X1_SIZE; } 
      else if (xmlTag.equals("float1x2")) { baseType = BaseType.Float;  size = MAT1X2_SIZE; } 
      else if (xmlTag.equals("float1x3")) { baseType = BaseType.Float;  size = MAT1X3_SIZE; } 
      else if (xmlTag.equals("float1x4")) { baseType = BaseType.Float;  size = MAT1X4_SIZE; } 
      else if (xmlTag.equals("float2x1")) { baseType = BaseType.Float;  size = MAT2X1_SIZE; }        
      else if (xmlTag.equals("float2x2")) { baseType = BaseType.Float;  size = MAT2X2_SIZE; } 
      else if (xmlTag.equals("float2x3")) { baseType = BaseType.Float;  size = MAT2X3_SIZE; }   
      else if (xmlTag.equals("float2x4")) { baseType = BaseType.Float;  size = MAT2X4_SIZE; }  
      else if (xmlTag.equals("float3x1")) { baseType = BaseType.Float;  size = MAT3X1_SIZE; }
      else if (xmlTag.equals("float3x2")) { baseType = BaseType.Float;  size = MAT3X2_SIZE; }
      else if (xmlTag.equals("float3x3")) { baseType = BaseType.Float;  size = MAT3X3_SIZE; }     
      else if (xmlTag.equals("float3x4")) { baseType = BaseType.Float;  size = MAT3X4_SIZE;}   
      else if (xmlTag.equals("float4x1")) { baseType = BaseType.Float;  size = MAT4X1_SIZE; }    
      else if (xmlTag.equals("float4x2")) { baseType = BaseType.Float;  size = MAT4X2_SIZE; }    
      else if (xmlTag.equals("float4x3")) { baseType = BaseType.Float;  size = MAT4X3_SIZE;}       
      else if (xmlTag.equals("float4x4")) { baseType = BaseType.Float;  size = MAT4X4_SIZE;}     
      else if (xmlTag.equals("string"))   { baseType = BaseType.String; size = 1; }   
      else {
        getCollada().warning(tokenizer.getErrorMessage("Collada.ValueType with unknown baase type: : " + tokenizer.getTagName()));
        //throw new RuntimeException("Collada.ValueType with unknown baase type: " + xmlTag);         
      }      
      readXML(tokenizer); 
   }
 
   @Override
   public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {
      appendNewLine(buf, fmt);
      if (baseType == BaseType.Bool) {
         appendBooleans(buf, bools, ' ', fmt, VEC4_SIZE);
      } else if (baseType == BaseType.Int) {
         appendInts(buf, ints, ' ', fmt, VEC4_SIZE);
      } else if (baseType == BaseType.Float) {
         appendFloats(buf, floats, ' ', fmt, VEC4_SIZE);
      } else if (baseType == BaseType.String) {
         buf.append(string);
      }
      return buf;  
   }

   @Override
   public void decodeContent(XMLTokenizer tokenizer) throws IOException {
      String encoding = tokenizer.takeCharData();
      if (baseType == BaseType.Bool) {
         bools = new boolean[size];
         decodeBooleanArray(encoding, bools, " \t\n\r\f");
      } else if (baseType == BaseType.Int) {
         ints = new int[size];
         decodeIntArray(encoding, ints, " \t\n\r\f");
      } else if (baseType == BaseType.Float) {
         floats = new float[size];
         decodeFloatArray(encoding, floats, " \t\n\r\f");
      } else if (baseType == BaseType.String) {
         string = encoding;
      }
   }
 
//   /**
//    * The XML Stag; defined for all XMLStructureAdapters; here it is not useful,
//    * as it is replaced by the dynamic xmlTag value.
//    */
//   public static String XMLTag = "ValueType";

 
   /**
    * non-static XML tag value
    */
   private String xmlTag; 
   
   private static Set<String> xmlTags = new HashSet<String>();
   
   static {
      xmlTags.add("bool");      xmlTags.add("bool2");     xmlTags.add("bool3");      xmlTags.add("bool4");
      xmlTags.add("int");       xmlTags.add("int2");      xmlTags.add("int3");       xmlTags.add("int4");
      xmlTags.add("float");     xmlTags.add("float2");    xmlTags.add("float3");     xmlTags.add("float4");
      xmlTags.add("float1x1");  xmlTags.add("float1x2");  xmlTags.add("float1x3");   xmlTags.add("float1x4"); 
      xmlTags.add("float2x1");  xmlTags.add("float2x2");  xmlTags.add("float2x3");   xmlTags.add("float2x4");
      xmlTags.add("float3x1");  xmlTags.add("float3x2");  xmlTags.add("float3x3");   xmlTags.add("float3x4");  
      xmlTags.add("float4x1");  xmlTags.add("float4x2");  xmlTags.add("float4x3");   xmlTags.add("float4x4");  
      xmlTags.add("string");   
   }
  
      
   /**
    * returns the XML Stag for XML encoding
    */
   @Override
   public String getXMLTag() {
      return xmlTag;
   }

   /**
    * Returns whether tag is one of the ValueType tags
    */
   public static boolean hasTag(String tag) {
      return xmlTags.contains(tag);  
   }
   

}
