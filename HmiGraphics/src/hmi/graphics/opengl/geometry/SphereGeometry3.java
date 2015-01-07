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
/*  
 */

package hmi.graphics.opengl.geometry;

import hmi.graphics.opengl.GLRenderContext;
import hmi.graphics.opengl.GLRenderObject;
import hmi.graphics.util.BufferUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.*; 

/**
 * A simple Sphere object, rendered using direct mode OpenGL
 */
public class SphereGeometry3 implements GLRenderObject {  
  
  float radius = 5.0f;
  int numSlices = 32;
  int numStacks = 16;
  
  
  float[] mat_ambient = new float[] {1.0f, 1.0f, 1.0f};
  float[] mat_diffuse = new float[] {1.0f, 1.0f, 1.0f};
  float[] mat_specular = new float[] {0.0f, 0.0f, 0.0f};
  int texId;
  //float drho, dtheta, ds, dt;
//  int sphereList;
  int nrOfVertices ;
  int nrOfTris;
  float[] vertexData;
  float[] normalData;
  float[] texCoordData;
  int[] indexData;

  FloatBuffer vertexBuffer;
  FloatBuffer normalBuffer;
  FloatBuffer texCoordBuffer;
  IntBuffer indexBuffer;
  
 // FloatBuffer combinedBuffer;
  
  int vertexBufferId, normalBufferId, indexBufferId, texCoordBufferId;
  int combinedBufferId;
  
  int combinedSize; // size of combinedBuffer in floats
  int combinedByteBufferSize; // // size of combinedBuffer in bytes
  
  int dataBufferSize; // size of dataBuffer (in floats)
  int dataByteBufferSize; // size of dataBuffer in bytes
  int texCoordBufferSize;
  int texCoordByteBufferSize;
  int indexBufferSize;
  int indexByteBufferSize;
  
  int vertexOffset, normalOffset, texcoordOffset;
  int glArrayBufferSize;
  
  
  private boolean textured = true;
  
  
  private static SphereGeometry3 sphere;
   
  public static void makeSphere(float radius, int numSlices, int numStacks) {
    sphere = new SphereGeometry3(radius, numSlices, numStacks);
  }
  
  public static SphereGeometry3 getSphere(float radius, int numSlices, int numStacks) {
    if (sphere == null) makeSphere(radius, numSlices, numStacks);
    return sphere;
  }
  
   /**
    * Create a new Sphere object
    */
   public SphereGeometry3(float radius, int numSlices, int numStacks) {
      this.radius = radius;
      this.numSlices = numSlices;
      this.numStacks = numStacks;
      init();
      
   }
 

 
   public void setTextured(boolean textured) {
      this.textured = textured;
   }
 
   public void glInit(GLRenderContext glc) {

      GL2 gl = glc.gl2;
      int[] bufNames = new int[5];
      gl.glGenBuffers(5, bufNames, 0);

      indexBufferId = bufNames[3];
      combinedBufferId = bufNames[4];
    
      gl.glBindBuffer(GL.GL_ARRAY_BUFFER, combinedBufferId);
      gl.glBufferData(GL.GL_ARRAY_BUFFER, glArrayBufferSize, (FloatBuffer)null, GL.GL_STATIC_DRAW);  
      vertexBuffer.rewind();
      gl.glBufferSubData(GL.GL_ARRAY_BUFFER, vertexOffset, dataByteBufferSize, vertexBuffer);
      normalBuffer.rewind();
      gl.glBufferSubData(GL.GL_ARRAY_BUFFER, normalOffset, dataByteBufferSize, normalBuffer);
      texCoordBuffer.rewind();
      gl.glBufferSubData(GL.GL_ARRAY_BUFFER, texcoordOffset, texCoordByteBufferSize, texCoordBuffer);
      
      
      gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, indexBufferId);
      indexBuffer.rewind();
      gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, indexByteBufferSize, indexBuffer, GL.GL_STATIC_DRAW);
      gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
      gl.glEnableClientState(GL2.GL_NORMAL_ARRAY);
   //   int texUnit = 0;
   //   glc.glClientActiveTexture(GLC.GL_TEXTURE0 + texUnit);
       gl.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);


   }
   
     
     
   public void glRender(GLRenderContext glc) {

        GL2 gl = glc.gl2;
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, combinedBufferId);
        
        gl.glNormalPointer(GL.GL_FLOAT, 0, normalOffset);
        gl.glTexCoordPointer(2, GL.GL_FLOAT, 0, texcoordOffset);
        gl.glVertexPointer(3, GL.GL_FLOAT, 0, vertexOffset);
        
        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, indexBufferId);
        gl.glDrawElements(GL.GL_TRIANGLES, indexBufferSize, GL.GL_UNSIGNED_INT, 0L);
 
   } 
   
   
   
   
   private void init() {
      nrOfVertices = (numStacks+1) * (numSlices+1);
      nrOfTris = numStacks * numSlices * 2;
      dataBufferSize = 3*nrOfVertices; // 3 floats per vertex
      texCoordBufferSize = 2*nrOfVertices;
      indexBufferSize = 3*nrOfTris;
      
      dataByteBufferSize = 4*dataBufferSize;
      texCoordByteBufferSize = 4* texCoordBufferSize;
      indexByteBufferSize = 4* indexBufferSize;
      
      glArrayBufferSize = dataByteBufferSize + dataByteBufferSize + texCoordByteBufferSize;
      
      vertexData = new float[dataBufferSize];
      normalData = new float[dataBufferSize];
      texCoordData = new float[texCoordBufferSize]; 
      indexData = new int[indexBufferSize];
      
      vertexBuffer = BufferUtil.directFloatBuffer(dataBufferSize);
      normalBuffer = BufferUtil.directFloatBuffer(dataBufferSize);
      texCoordBuffer = BufferUtil.directFloatBuffer(texCoordBufferSize);
      indexBuffer = BufferUtil.directIntBuffer(indexBufferSize);
      
      vertexOffset = 0;
      normalOffset = vertexOffset + dataByteBufferSize;
      
      texcoordOffset = normalOffset + dataByteBufferSize ;
      
      double dtheta =  Math.PI / numStacks ;
      double dphi =  ( 2.0 * Math.PI) /  numSlices;
      double ds = 1.0 / numSlices;
      double dt = 1.0 / numStacks;
      double theta, phi;
      float stheta, ctheta, sphi, cphi;
      float x, y, z, s, t;
      int vc = 0; // the vertex counter
      // fill stacks, from bottom to top
      for (int i = 0; i<=numStacks; i++) {
         if (i==0 ) {
            stheta = 0.0f;   ctheta = -1.0f;  
         } else if (i==numStacks) {
            stheta = 0.0f;   ctheta = 1.0f; 
         } else {
            theta = Math.PI - i*dtheta;
            stheta = (float) Math.sin(theta);   ctheta = (float) Math.cos(theta);      
         }
         t = (float) (i*dt);  
//         hmi.util.Console.println("========================\ni=" + i + "  stheta = " + stheta + "  ctheta=" + ctheta);
         for (int j=0; j<=numSlices; j++) {
            if (j==0 || j==numSlices ) {
                sphi = 0.0f;   cphi = 1.0f;
            } else {
                phi = j * dphi;
                sphi = (float) Math.sin(phi);   cphi = (float) Math.cos(phi);
            }
//            hmi.util.Console.println("j=" + j + "  sphi = " + sphi + "  cphi=" + cphi);
            x =  -sphi * stheta;
            y =   ctheta;
            z =  -cphi * stheta;
            s = (float) (j*ds);
//            hmi.util.Console.println("x=" + x + "  y = " + y + " z= " + z);
            vertexData[3*vc]   = radius * x;
            vertexData[3*vc+1] = radius * y;
            vertexData[3*vc+2] = radius * z;
            normalData[3*vc]   = x;
            normalData[3*vc+1] = y;
            normalData[3*vc+2] = z;
            texCoordData[2*vc] = s;
            texCoordData[2*vc+1] = t;
            vc++;
         }
      }
      int tc = 0; // the triangle counter;
      for (int i = 0; i<numStacks; i++) { 
         for (int j=0; j<numSlices; j++) {
           // add two triangles
            int lowerleft =  (i*(numSlices+1)) + j; 
            int lowerright = lowerleft+1;
            int upperleft = lowerleft + numSlices+1;
            int upperright = upperleft+1;
            indexData[3*tc] =  lowerleft;
            indexData[3*tc+1] = lowerright;
            indexData[3*tc+2] = upperleft;
            tc++;
            indexData[3*tc] = upperleft;
            indexData[3*tc+1] = lowerright;
            indexData[3*tc+2] = upperright;
            tc++;
         }
      } 
//      for (int i=0; i< nrOfVertices; i++) {
//          hmi.util.Console.println("vertex " + i  + " = (" + vertexData[3*i] + ", " +   vertexData[3*i+1] + ", " +  vertexData[3*i+2] + ")"); 
//      }
      vertexBuffer.put(vertexData);
      normalBuffer.put(normalData);
      texCoordBuffer.put(texCoordData);
      indexBuffer.put(indexData);
     
   }
 
 
 
}
