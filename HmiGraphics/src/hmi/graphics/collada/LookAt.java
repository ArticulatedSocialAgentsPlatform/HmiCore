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

import hmi.math.Mat3f;
import hmi.math.Mat4f;
import hmi.math.Quat4f;
import hmi.math.Vec3f;
import hmi.math.Vec4f;
import hmi.xml.XMLFormatting;
import hmi.xml.XMLTokenizer;

import java.io.IOException;

/** 
 * LookAt defines a transform matrix, specified by a 9-vector containg:
 * eye position, center (or &quot;interest point&quot;), and up-vector, in that order.
 * @author Job Zwiers
 */
public class LookAt extends TransformNode {
   
   // attributes: sid, inherited from ColladaElement.
   private float[] eyepos = Vec3f.getVec3f();
   private float[] center = Vec3f.getVec3f();
   private float[] upvec  = Vec3f.getVec3f();
   
   public LookAt() {
      super();
   }
   
   public LookAt(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super(collada); 
      readXML(tokenizer); 
   }
 
   /**
    * Returns the 4X4 OpenGL style lookat matrix, in row-major order.
    */
   @Override
   public float[] getMat4f() {
      if (super.getMat4f()==null) {
          setMat4f(Mat4f.getLookAtMatrix(eyepos, center, upvec));  
      }
      return super.getMat4f();
   }
 
   /**
    * returns the rotation part of the LookAt matrix, in quaternion form
    */
   public float[] getLookAtRotation4f() {
      float[] mt = getMat4f(); // ensures matrix contain the lookat 4x4 matrix
      float[] q = Quat4f.getQuat4f();
      Quat4f.setFromMat4f(q, mt);
      return q;
   }
 
   /**
    * returns the translation part of the LookAt matrix
    */
   public float[] getLookAtTranslation3f() {
      float[] mt = getMat4f(); // ensures matrix contain the lookat 4x4 matrix
      float[] t = Vec4f.getVec4f();
      Mat4f.getTranslation(t, mt);
      return t;
   }
 
   private static final int FLOATSPERROW = 3;
 
   @Override
   public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {
      appendNewLine(buf, fmt);
      appendFloats(buf, eyepos, ' ', fmt, FLOATSPERROW);
      appendNewLine(buf, fmt);
      appendFloats(buf, center, ' ', fmt, FLOATSPERROW);
      appendNewLine(buf, fmt);
      appendFloats(buf, upvec,  ' ', fmt, FLOATSPERROW);      
      return buf;  
   }

   @Override
   public void decodeContent(XMLTokenizer tokenizer) throws IOException {
      float[] lookatparams = new float[Mat3f.MAT3F_SIZE];
      decodeFloatArray(tokenizer.takeCharData(), lookatparams);
      eyepos[0] = lookatparams[0]; eyepos[1] = lookatparams[1]; eyepos[2] = lookatparams[2];
      center[0] = lookatparams[Vec3f.VEC3F_SIZE]; center[1] = lookatparams[Vec3f.VEC3F_SIZE+1]; center[2] = lookatparams[Vec3f.VEC3F_SIZE+2];
      upvec[0]  = lookatparams[2*Vec3f.VEC3F_SIZE]; upvec[1]  = lookatparams[2*Vec3f.VEC3F_SIZE+1]; upvec[2]  = lookatparams[2*Vec3f.VEC3F_SIZE+2];
      
   }
 
   /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "lookat";
 
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
