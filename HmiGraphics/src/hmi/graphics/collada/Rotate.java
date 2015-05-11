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
