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

/** 
 * An extension of ColladaElement, for all Collada transforms:
 * Matrix, Rotate, Translate, Scale, Skew, LookAt
 * @author Job Zwiers
 */
public class TransformNode extends ColladaElement {
   
   private float[] matrix = null; // 4x4 matrix
   
   /**
    * Constructor
    */
   public TransformNode()   {
      super(); 
   }
   
   
   /**
    * Constructor
    */
   public TransformNode(Collada collada) {
      super(collada); 
   }
   
   /**
    * Calculates and returns the 4 X 4 transformation matrix, in row major order,
    * within a length 16 float array.
    */
   public float[] getMat4f() {
       return matrix;  
   }
   
   /**
    * Sets the matrix
    */
   public void setMat4f(float[] matrix) {
      this.matrix = matrix;
   }
   
   
}
