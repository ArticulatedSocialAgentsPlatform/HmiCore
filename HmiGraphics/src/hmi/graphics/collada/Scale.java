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
public class Scale extends TransformNode {

   private float[] scaleVec = Vec3f.getVec3f(1.0f, 1.0f, 1.0f);
   
   
   public Scale() {
      super();
   }
   
   public Scale(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super(collada); 
      readXML(tokenizer); 
   }
 
   /**
    * Returns the 4X4 scaling matrix, in row-major order.
    */
   @Override
   public float[] getMat4f() {
      if (super.getMat4f()==null) setMat4f(Mat4f.getScalingMatrix(scaleVec));
      return super.getMat4f();
   }
 
   /**
    * Returns the scaling vector in a Vec3f array.
    */
   public float[] getScaleVec3f() {
      return scaleVec;
   }


   @Override
   public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {
      appendNewLine(buf, fmt);
      appendFloats(buf, scaleVec, ' ', fmt, Vec3f.VEC3F_SIZE);
      return buf;  
   }

   @Override
   public void decodeContent(XMLTokenizer tokenizer) throws IOException {
      decodeFloatArray(tokenizer.takeCharData(), scaleVec);
   }
 
 
 
   /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "scale";
 
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
