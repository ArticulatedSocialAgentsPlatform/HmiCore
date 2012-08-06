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

import hmi.math.Mat4f;
import hmi.xml.XMLFormatting;
import hmi.xml.XMLTokenizer;

import java.io.IOException;

/** 
 * Matrix defines a 4 X 4 transform matrix, specified by 16 floats, in row-major order.
 * @author Job Zwiers
 */
public class Matrix extends TransformNode {
   
   // attributes: sid, inherited from ColladaElement.
   //public float[] matrix inherited from TransformNode
   
   public Matrix() {
      super();
   }
   
   public Matrix(Collada collada) {
      super(collada); 
   }
   
   public Matrix(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super(collada); 
      readXML(tokenizer); 
   }

   // from TransformNode: float[]  getMat4f()
 
   private static final int FLOATSPERROW = 4;
 
 
   @Override
   public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {
      appendNewLine(buf, fmt);
      appendFloats(buf, getMat4f(), ' ', fmt, FLOATSPERROW);
      
      return buf;  
   }

   @Override
   public void decodeContent(XMLTokenizer tokenizer) throws IOException {
      setMat4f(Mat4f.getMat4f());
      decodeFloatArray(tokenizer.takeCharData(), getMat4f());
   }
 
   /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "matrix";
 
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
