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

import hmi.math.Mat4f;
import hmi.math.Quat4f;
import hmi.math.Vec4f;
import hmi.xml.XMLFormatting;
import hmi.xml.XMLTokenizer;

import java.io.IOException;

/** 
 * @author Job Zwiers
 */
public class Rotate extends TransformNode {

   // axis and angle of the rotation.
   // N.B. angle is in degrees!
   private float[] axisAngleDegrees = new float[] {0.0f, 1.0f, 0.0f, 0.0f};
   private float[] q = Quat4f.getQuat4f(); // the rotation quaternion, derivbed from axisAngleDegrees.
   
   
   public Rotate() {
      super();
   }
   
   public Rotate(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super(collada); 
      readXML(tokenizer); 
   }
 
   /**
    * returns the axis and angle in a float[4] array.
    * The axis need not be normalized, and the angle is in degrees, not radians.
    * So: use getAxisAngle instead.
    */
   public float[] getAxisAngleDegrees() {
      return axisAngleDegrees;
   }
 
   private static final double DEGREESPERRAD = 180.0;
 
 
 
   /**
    * sets the rotation by means of axis and angle, in radians
    */
   public void setAxisAngleDegrees(float[] aa) {
      Vec4f.set(axisAngleDegrees, aa);
   }
   
   
   /**
    * sets the rotation by means of axis and angle, in radians
    */
   public void setAxisAngleRadians(float[] aa) {
      setAxisAngleDegrees(aa);
      axisAngleDegrees[Vec4f.W] *= (float) (DEGREESPERRAD/Math.PI);
   }
   
   /**
    * returns the rotation quaternion, in a float[4] array
    */
   public float[] getRotationQuat4f() {
      return q;
   }
 
   /**
    * Returns the 4X4 rotation matrix, in row-major order.
    */
   @Override
   public float[] getMat4f() {
      if (super.getMat4f() == null) {
         float[] mt =Mat4f.getIdentity();
         Mat4f.setRotationFromAxisAngle4f(mt, axisAngleDegrees);
         setMat4f(mt);
      }
      return super.getMat4f();
   }
 
   @Override
   public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {
      appendNewLine(buf, fmt);
      appendFloats(buf, axisAngleDegrees, ' ', fmt, Vec4f.VEC4F_SIZE);
      return buf;  
   }
   
   @Override
   public void decodeContent(XMLTokenizer tokenizer) throws IOException {
      decodeFloatArray(tokenizer.takeCharData(), axisAngleDegrees);
      Quat4f.setFromAxisAngleDegrees(q, axisAngleDegrees, axisAngleDegrees[Vec4f.W]);
   }
 
  
   /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "rotate";
 
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
