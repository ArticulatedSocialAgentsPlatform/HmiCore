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
