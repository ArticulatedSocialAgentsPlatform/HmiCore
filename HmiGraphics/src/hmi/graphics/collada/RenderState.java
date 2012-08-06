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
import java.util.HashSet;
import java.util.Set;

/** 
 * @author Job Zwiers
 */
public class RenderState extends ColladaElement {
   
   // attributes: sid, inherited from ColladaElement.
   // one of these will be used:
   private boolean[] bools;
   private int[] ints;
   private float[] floats;
   private String string;
   private int size; // size f the bools/ints/floats array
   
   private int baseType; // determines which of the arrays is actually used   
   private static final int BOOL   = 0;
   private static final int INT    = 1;
   private static final int FLOAT  = 2;
   private static final int STRING = 3;
   
   
   public RenderState() {
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
   
   public RenderState(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super();      
      xmlTag = tokenizer.getTagName();
      if (xmlTag.equals("alpha_func"))    { baseType = STRING; size = 1; }     
      else if (xmlTag.equals("bool2"))    { baseType = BOOL;   size = VEC2_SIZE; }         
      else if (xmlTag.equals("bool3"))    { baseType = BOOL;   size = VEC3_SIZE; }         
      else if (xmlTag.equals("bool4"))    { baseType = BOOL;   size = VEC4_SIZE; }     
      else if (xmlTag.equals("int"))      { baseType = INT;    size = 1; }      
      else if (xmlTag.equals("int2"))     { baseType = INT;    size = VEC2_SIZE; }       
      else if (xmlTag.equals("int3"))     { baseType = INT;    size = VEC3_SIZE; }     
      else if (xmlTag.equals("int4"))     { baseType = INT;    size = VEC4_SIZE; }     
      else if (xmlTag.equals("float"))    { baseType = FLOAT;  size = 1; }     
      else if (xmlTag.equals("float2"))   { baseType = FLOAT;  size = VEC2_SIZE; }    
      else if (xmlTag.equals("float3"))   { baseType = FLOAT;  size = VEC3_SIZE; }     
      else if (xmlTag.equals("float4"))   { baseType = FLOAT;  size = VEC4_SIZE; }  
      else if (xmlTag.equals("float1x1")) { baseType = FLOAT;  size = MAT1X1_SIZE; } 
      else if (xmlTag.equals("float1x2")) { baseType = FLOAT;  size = MAT1X2_SIZE; } 
      else if (xmlTag.equals("float1x3")) { baseType = FLOAT;  size = MAT1X3_SIZE; } 
      else if (xmlTag.equals("float1x4")) { baseType = FLOAT;  size = MAT1X4_SIZE; } 
      else if (xmlTag.equals("float2x1")) { baseType = FLOAT;  size = MAT2X1_SIZE; }        
      else if (xmlTag.equals("float2x2")) { baseType = FLOAT;  size = MAT2X2_SIZE; } 
      else if (xmlTag.equals("float2x3")) { baseType = FLOAT;  size = MAT2X3_SIZE; }   
      else if (xmlTag.equals("float2x4")) { baseType = FLOAT;  size = MAT2X4_SIZE; }  
      else if (xmlTag.equals("float3x1")) { baseType = FLOAT;  size = MAT3X1_SIZE; }
      else if (xmlTag.equals("float3x2")) { baseType = FLOAT;  size = MAT3X2_SIZE; }
      else if (xmlTag.equals("float3x3")) { baseType = FLOAT;  size = MAT3X3_SIZE; }     
      else if (xmlTag.equals("float3x4")) { baseType = FLOAT;  size = MAT3X4_SIZE;}   
      else if (xmlTag.equals("float4x1")) { baseType = FLOAT;  size = MAT4X1_SIZE; }    
      else if (xmlTag.equals("float4x2")) { baseType = FLOAT;  size = MAT4X2_SIZE; }    
      else if (xmlTag.equals("float4x3")) { baseType = FLOAT;  size = MAT4X3_SIZE;}       
      else if (xmlTag.equals("float4x4")) { baseType = FLOAT;  size = MAT4X4_SIZE;}     
      else if (xmlTag.equals("string"))   { baseType = STRING; size = 1; }     
      else {
        getCollada().warning(tokenizer.getErrorMessage("Collada_Render_State with unknown base type: " + tokenizer.getTagName()));
        //throw new RuntimeException("Collada.RenderState with unknown baase type: " + xmlTag);         
      }      
      readXML(tokenizer); 
   }
 
  private static final int ELEMENTSPERLINE = 4;
 
   @Override
   public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {
      appendNewLine(buf, fmt);
      if (baseType == BOOL) {
         appendBooleans(buf, bools, ' ', fmt, ELEMENTSPERLINE);
      } else if (baseType == INT) {
         appendInts(buf, ints, ' ', fmt, ELEMENTSPERLINE);
      } else if (baseType == FLOAT) {
         appendFloats(buf, floats, ' ', fmt, ELEMENTSPERLINE);
      } else if (baseType == STRING) {
         buf.append(string);
      }
      return buf;  
   }

   @Override
   public void decodeContent(XMLTokenizer tokenizer) throws IOException {
      String encoding = tokenizer.takeCharData();
      if (baseType == BOOL) {
         bools = new boolean[size];
         decodeBooleanArray(encoding, bools, " \t\n\r\f");
      } else if (baseType == INT) {
         ints = new int[size];
         decodeIntArray(encoding, ints, " \t\n\r\f");
      } else if (baseType == FLOAT) {
         floats = new float[size];
         decodeFloatArray(encoding, floats, " \t\n\r\f");
      } else if (baseType == STRING) {
         string = encoding;
      }
   }
 

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



}
