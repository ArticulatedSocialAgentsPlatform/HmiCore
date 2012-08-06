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
import hmi.math.Vec3f;
import hmi.xml.XMLFormatting;
import hmi.xml.XMLTokenizer;

import java.io.IOException;

/** 
 * @author Job Zwiers
 */
public class Translate extends TransformNode {

   private float[] translation = new float[] {0.0f, 0.0f, 0.0f};
   
   
   public Translate() {
      super();
   }
   
   public Translate(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super(collada); 
      readXML(tokenizer); 
   }
 
 
   public float[] getTranslation() {
      return translation;
   }
 
   
   public void setTranslation(float[] t) {
      translation = t;  
   }
   
   /**
    * Returns the 4X4 translation matrix, in row-major order.
    */
   @Override
   public float[] getMat4f() {
      if (super.getMat4f()==null) setMat4f(Mat4f.getTranslationMatrix(translation));
      return super.getMat4f();
   }
 
   /**
    * Returns the rotation component of the transform in a Vec3f array which could be null,
    */
   public float[] getTranslationVec3f() {
       return translation;  
   }
 
   @Override
   public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {
      appendNewLine(buf, fmt);
      appendFloats(buf, translation, ' ', fmt, Vec3f.VEC3F_SIZE);
      return buf;  
   }
   
   @Override
   public void decodeContent(XMLTokenizer tokenizer) throws IOException {
      decodeFloatArray(tokenizer.takeCharData(), translation);
   }
 
   /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "translate";
 
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
