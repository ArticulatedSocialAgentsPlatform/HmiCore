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

import hmi.math.Vec4f;
import hmi.xml.XMLFormatting;
import hmi.xml.XMLTokenizer;

import java.io.IOException;

/** 
 * Generic float vector of the form <sometag> 1.1 2.2  .... 3.3 </sometag>
 * It is a base class for CommonColor, Max3D_bounding_min, Max3D_bounding_max etcetera
 * @author Job Zwiers 
 */
public class ColladaFloatVector extends ColladaElement {

   private float[] vec;  // generic vector. Not allocated here (should be done in subclasses, or by decodeContent)
     
   public ColladaFloatVector() {
      super();
   }
   
   /**
    * Constructor used to pass in the Collada parameter
    */
   public ColladaFloatVector(Collada collada) {
      super(collada);
   }
   
   /**
    * Return the float vector array
    */
   public float[] getVec() {
      return vec;
   }
   
   /**
    * Sets the float vector array
    */
   public void setVec(float[] vec) {
      this.vec = vec;
   }
   
   
   @Override
   public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {
      appendNewLine(buf, fmt);
      appendFloats(buf, vec, ' ', fmt, Vec4f.VEC4F_SIZE);
      return buf;  
   }

   @Override
   public void decodeContent(XMLTokenizer tokenizer) throws IOException {
      vec = decodeFloatArray(tokenizer.takeCharData(), vec);  // will allocate vec if it is null, otherwise, it will use vec
   }
 

}
