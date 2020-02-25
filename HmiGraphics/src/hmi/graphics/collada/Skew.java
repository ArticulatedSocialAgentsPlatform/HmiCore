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

import hmi.math.Mat3f;
import hmi.math.Mat4f;
import hmi.math.Vec3f;
import hmi.xml.XMLFormatting;
import hmi.xml.XMLTokenizer;

import java.io.IOException;

/** 
 * Skew defines a Collada skewing transform, specified in &quot;Renderman&quot; style.
 * It is defined by a translation vector (vertices are shifted parallel to this vector),
 * and a rotated vector plus rotation angle. The skewing operation should
 * have the effect of rotating this latter vector, in the direction as denoted by the tranlation vector.
 * The angle is specified in degrees. The Renderman/Collada specification is by means of a
 * seven-tuple: angle, rotated vector, translation vector.
 * @author Job Zwiers
 */
public class Skew extends TransformNode {

   private float angle; // angle (in degrees) between rotated vector and its image
   private float[] rvec = Vec3f.getVec3f(0.0f, 1.0f, 0.0f); // imaginary vector to be "rotated"
   private float[] tvec = Vec3f.getVec3f(0.0f, 1.0f, 0.0f); // translation direction
   private float[] mat3f; // 3X3 skewing matrix;
   
   // Renderman spec:
   // RiSkew ( RtFloat angle, RtFloat dx1, RtFloat dy1, RtFloat dz1,
   // RtFloat dx2, RtFloat dy2, RtFloat dz2 )
   // Concatenate a skew onto the current transformation. This operation shifts all points
   // along lines parallel to the axis vector (dx2, dy2, dz2). Points along the axis vector
   // (dx1, dy1, dz1) are mapped onto the vector (x, y, z), where angle speci?es the angle
   // (in degrees) between the vectors (dx1, dy1, dz1) and (x, y, z), The two axes are not
   // required to be perpendicular, however it is an error to specify an angle that is greater
   // than or equal to the angle between them. A negative angle can be speci?ed, but it
   // must be greater than 180 degrees minus the angle between the two axes.
   
   // In Collada: Skew(angle, rvec, tvec).
   
   
   
   public Skew() {
      super();
   }
   
   public Skew(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super(collada); 
      readXML(tokenizer); 
   }
 
   /**
    * Returns the 4X4 skewing matrix, according to the Renderman specification, in row-major order.
    */
   @Override
   public float[] getMat4f() {
      if (super.getMat4f()==null) {
         setMat4f(Mat4f.getSkewMatrix(angle, rvec, tvec));
      }
      return super.getMat4f();
   }
 
   /**
    * Returns the 3X3 skewing matrix, according to the Renderman specification, in row-major order.
    */
   public float[] getSkewMat3f() {
      if (mat3f == null) mat3f = Mat3f.getSkewMatrix(angle, rvec, tvec);
      return mat3f;
   }
 
 
   @Override
   public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {
      appendNewLine(buf, fmt);
      buf.append(angle); appendNewLine(buf, fmt);
      appendFloats(buf, rvec, ' ', fmt, Vec3f.VEC3F_SIZE); appendNewLine(buf, fmt);
      appendFloats(buf, tvec, ' ', fmt, Vec3f.VEC3F_SIZE);
      return buf;  
   }

   @Override
   public void decodeContent(XMLTokenizer tokenizer) throws IOException {
      float[] angleRvecTvec = new float[1 + 2*Vec3f.VEC3F_SIZE];
      decodeFloatArray(tokenizer.takeCharData(), angleRvecTvec);
      angle = angleRvecTvec[0];
      Vec3f.set(rvec, 0, angleRvecTvec, 1); // rvec(0,1,2) =    angleRvecTvec(1,2,3)
      Vec3f.set(tvec, 0, angleRvecTvec, 1 + Vec3f.VEC3F_SIZE); // tvec(0,1,2) =    angleRvecTvec(4,5,6)
   }
 
   /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "skew";
 
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
