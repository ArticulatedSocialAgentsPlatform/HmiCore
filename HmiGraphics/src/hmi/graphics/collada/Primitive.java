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

import hmi.xml.XMLStructureAdapter;
import hmi.xml.XMLTokenizer;

import java.io.IOException;

/** 
 * @author Job Zwiers
 */
public class Primitive extends ColladaElement
{
 
   private float[] vector = null;
   
   public Primitive() {
      super(); 
   }
 
   public Primitive(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super(collada);
      readXML(tokenizer); 
   }
 
   
 
 
   @Override
   public boolean hasContent() {
       return false;
   }
   

   /**
    * appends a String of attributes to buf.
    */
   @Override
   public StringBuilder appendAttributeString(StringBuilder buf) {
      super.appendAttributeString(buf);
      if (vector != null) {
         appendAttribute(buf, "vector", vector, ' ');
      }
      return buf;
   }



   /**
    * decodes a single attribute, as encoded by appendAttributeString()
    */
   @Override
   public boolean decodeAttribute(String attrName, String valCode, XMLTokenizer tokenizer) {
      if (attrName.equals("vector")) {
         vector = XMLStructureAdapter.decodeFloatArray(valCode, " ,\r\n\t\f");
         return true;
      } 
      return super.decodeAttribute(attrName, valCode, tokenizer);        
   }


// 
//   /**
//    * The XML Stag for XML encoding, and the class name
//    */
//   public static String XMLTag = "P";
//   public static String CLASSNAME = "hmi.graphics.collada";
//   public static Class<hmi.graphics.collada.Primitive> CL = hmi.graphics.collada.Primitive.class;
// 
//   /**
//    * returns the XML Stag for XML encoding
//    */
//   @Override
//   public String getXMLTag() {
//      return XMLTag;
//   }



}
