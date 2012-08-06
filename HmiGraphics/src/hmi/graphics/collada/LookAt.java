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
