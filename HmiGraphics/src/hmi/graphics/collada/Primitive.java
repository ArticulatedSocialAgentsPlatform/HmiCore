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
