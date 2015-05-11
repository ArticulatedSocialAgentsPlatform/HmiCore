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
