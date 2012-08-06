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
/* @author Job Zwiers  
 * @version  0, revision $Revision$,
 * $Date: 2005/11/15 22:29:17 $      
 */

package hmi.graphics.colladatest;
import java.nio.*;
import hmi.graphics.opengl.*;
import hmi.graphics.util.BufferUtil;



/**
 * A simple TorusBuffered object, rendered using direct mode OpenGL
 */
public class TorusBuffered implements GLRenderObject {

  float majorRadius = 5.0f;
  float minorRadius = 1.0f;
  int numMajor = 32;
  int numMinor = 16;
//  int numMajor = 4;
//  int numMinor = 4;
  int vertexCount; // number of vertices
  int dataVertexFloatSize = 3 + 3 + 2; // number of floats per vertex: 3 vertexcoord, 3 normal, 2 texcoord
  int dataVertexByteSize = 4 * dataVertexFloatSize; //number of bytes per vertex
  int dataBufferSize; // size of dataBuffer (in floats)
  int dataByteBufferSize; // size of dataBuffer in bytes
  int bufId;
  double majorStep, minorStep;
  

  FloatBuffer dataBuffer;
  
  
   /**
    * Create a new TorusBuffered object
    */
   public TorusBuffered(float majorRadius, float minorRadius, int numMajor, int numMinor) {
  
      this.majorRadius = majorRadius;
      this.minorRadius = minorRadius;
      this.numMajor = numMajor;
      this.numMinor = numMinor;
      majorStep = 2.0*Math.PI / numMajor;
      minorStep = 2.0*Math.PI / numMinor;
      vertexCount =  2 * numMajor * (numMinor+1);
      dataBufferSize = vertexCount * dataVertexFloatSize; // size of dataBuffer (in floats)
      dataByteBufferSize = 4 *  dataBufferSize;
      dataBuffer = BufferUtil.directFloatBuffer(dataBufferSize);
      //Console.println("dataBufferSize = " + dataBufferSize);
      fillBuffers();
   }
 
   private void fillBuffers() { 
      int p = 0;  // vertex counter
      for (int i=0; i<numMajor; ++i) {
          double a0 = i * majorStep;
          double a1 = a0 + majorStep;
          double x0 = Math.cos(a0);
          double y0 = Math.sin(a0);
          double x1 = Math.cos(a1);
          double y1 = Math.sin(a1);      
          for (int j=0; j<=numMinor; ++j) {
              double b = j * minorStep;
              double c =  -Math.cos(b);
              double r = minorRadius * c + majorRadius;
              double z = -minorRadius *  Math.sin(b);
              int base = dataVertexFloatSize * p;
              // tex coords first point
              dataBuffer.put(base, (float)(i)/(float)(numMajor));
              dataBuffer.put(base+1, (float)(j)/(float)(numMinor));
              // normal first point
              float vN0 = (float) (x0*c);
              float vN1 = (float) (y0*c);
              float vN2 = (float) (z/minorRadius);
//              double vlen =  Math.sqrt(vN0*vN0 + vN1*vN1 + vN2*vN2);
//              hmi.util.Console.println("vlen=" + vlen);
              dataBuffer.put(base+2, vN0); 
              dataBuffer.put(base+3, vN1); 
              dataBuffer.put(base+4, vN2); 
              // vertex coords first point
              dataBuffer.put(base+5, (float)(x0*r)); 
              dataBuffer.put(base+6, (float)(y0*r)); 
              dataBuffer.put(base+7, (float) z); 
              p++;
              base = dataVertexFloatSize * p;
              // tex coords second point
              dataBuffer.put(base, (float)(i+1)/(float)(numMajor));
              dataBuffer.put(base+1, (float)(j)/(float)(numMinor));
              // normal second point
              vN0 = (float) (x1*c);
              vN1 = (float) (y1*c);
              //vlen = (float) Math.sqrt(vN0*vN0 + vN1*vN1 + vN2*vN2);
              dataBuffer.put(base+2, vN0); 
              dataBuffer.put(base+3, vN1); 
              dataBuffer.put(base+4, vN2); 
              // vertex coords second point
              dataBuffer.put(base+5, (float)(x1*r)); 
              dataBuffer.put(base+6, (float)(y1*r)); 
              dataBuffer.put(base+7, (float)z);                            
              p++;
          }         
      }
   }
 
   public void glInit(GLRenderContext gl) {
      dataBuffer.rewind();
      int[] bufNames = new int[1];
      gl.glGenBuffers(1, bufNames);
      bufId = bufNames[0];
      gl.glBindBuffer(GLC.GL_ARRAY_BUFFER, bufId);
      gl.glBufferData(GLC.GL_ARRAY_BUFFER, dataByteBufferSize, dataBuffer, GLC.GL_STATIC_DRAW);
   }
   

   /**
    * render this object
    */
   public void glRender(GLRenderContext gl) {     
      gl.glBindBuffer(GLC.GL_ARRAY_BUFFER, bufId);
      gl.glInterleavedArrays(GLC.GL_T2F_N3F_V3F, 0, 0);
      gl.glDrawArrays(GLC.GL_TRIANGLE_STRIP, 0, vertexCount);      
   }
 
}
