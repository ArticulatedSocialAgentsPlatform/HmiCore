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

package hmi.graphics.opengl;

import hmi.graphics.scenegraph.VertexAttribute;
import hmi.graphics.util.BufferUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import javax.media.opengl.*;   
/**
 * A GLBasicMesh ...
 * 
 */
public class GLBasicMesh implements GLRenderObject
{
   
   // The indices for indexed data, are kept in an IntBuffer indexBuffer:
   private java.nio.IntBuffer indexBuffer;        // buffer containing mesh indices. 
   private int nrOfIndices;                       // size of indexBuffer, in number of ints, i.e. number of mesh indices.         
   private int indexByteBufferSize;               // size of indexBuffer, in number of bytes.  
   private boolean indexBufferModified;           // "dirty bit" for indexBuffer.              
                              
   protected int nrOfVertices;                      // number of vertices in the vertex data buffers.        
   
   // The vertex attributes are kept in a list of GLVertexAttribute objects:
   private ArrayList<GLVertexAttribute> attributeList = new ArrayList<GLVertexAttribute>(16);
   //private int vertexCoordAttributeIndex;  // attributeList index for the VertexCoord attribute
   //private int normalAttributeIndex;       // attributeList index for the normal attribute
   
   
   private int vertexBufferId;                    // OpenGL id for vertex buffer
   private int indexBufferId;                     // OpenGL id for index buffer
   int geometryType = GL.GL_TRIANGLES;            // type of OpenGL primitives.

   private String id;
   
  

   /**
    * Creates a new GLBasicMesh.
    */
   public GLBasicMesh() {
   }
   
   
   public void setId(String id) {
      this.id = id;
   }
   
   public String getId() {
      return id;
   }
    
   /**
    * sets the type of geometry, like GLC.GL_TRIANGLE_STRIP, or GLC.GL_TRIANGLES.
    * (The latter is the default)
    */
   public void setGeometryType(int type) {
       geometryType = type;  
   }
     
      
   /** 
    * Allocates and sets the index buffer, defining the mesh topology, 
    * by copying the data from indexData to an internal index buffer.
    * When indexData is null, the call has no effect at all.
    */
   public void setIndexData(int[] indexData) {
      if (indexData == null) return;
      nrOfIndices = indexData.length;  
      indexByteBufferSize = 4 * nrOfIndices;
      indexBuffer = BufferUtil.directIntBuffer(nrOfIndices); 
      indexBuffer.put(indexData, 0, nrOfIndices);
      indexBufferModified = true;
   }   
   
    /** Returns the number of indices */
    public int getNrOfIndices() { return nrOfIndices; }
  
//   /*
//    * Adds a new GLVertexAttribute. AttributeName can be one of the predefined OpenGL attribute names,
//    * like VertexCoord, Normal, Color, SecondaryColor, TexCoordi, or it can be a user defined GLSL attribute name.
//    */
//   public void addGLVertexAttribute(String attributeName, float[] vertexData, int dataElementSize, int nrOfVertices) {  
//      //hmi.util.Console.println("GLBasicMesh.addGLVertexAttribute " + attributeName);
//      GLVertexAttribute  attr = new GLVertexAttribute(attributeName, vertexData, dataElementSize, nrOfVertices );
//      attributeList.add(attr);   
//   }


   /*
    * Sets the number of vertices when called for the first time;
    * Checks for consistency for subsequent calls. (All Vertex Attributes should define the
    * same number of vertices)
    */
   private void setNrOfVertices(int nvert) {
      if (nrOfVertices <= 0) {
         nrOfVertices = nvert;
      } else {
         if (nrOfVertices != nvert) {
             throw new RuntimeException("GLBasicMesh: attributes with unequal number of vertices");  
         }   
      }
   }
   

   /** returns the number of vertices  */
   public int getNrOfVertices() { return nrOfVertices; }

  

   /**
    * Adds a new vertex attribute, and returns its index number.
    */
   public int addGLVertexAttribute(VertexAttribute va) {
      //hmi.util.Console.println("GLBasicMesh.addGLVertexAttribute " + va.getName());
      int attrNum = attributeList.size();
      GLVertexAttribute  attr = new GLVertexAttribute(va);
      setNrOfVertices(attr.getNrOfVertices());
      attributeList.add(attr); 
      return attrNum;  
   }


   /**
    * Sets the vertex data for an attribute, identified by its index number
    * The data will be copied.
    */
   public void setVertexData(int attrNum, float[] vertexData) {
      attributeList.get(attrNum).setVertexData(vertexData);  
   }
   
   /**
    * Fills and returns the vertexData float array with the current contents
    * of the vertex data for the specified attribute.
    * The passed in vertexData array can be  null, in which case a new float array is allocated.
    */
   public float[] getVertexData(int attrNum, float[] vertexData) {
      return attributeList.get(attrNum).getVertexData(vertexData);
   }

//  // special purpose set/get data, for DeformableMeshStructure interface:
//   
//   public void setVertexCoordData(float[] vertexData) {
//      attributeList.get(vertexCoordAttributeIndex).setVertexData(vertexData);
//   }
//   
//   public void setNormalData(float[] vertexData) {
//      attributeList.get(normalAttributeIndex).setVertexData(vertexData);
//   }
//   
//   
//   public float[] getVertexCoordData(float[] vertexData) {
//      return attributeList.get(vertexCoordAttributeIndex).getVertexData(vertexData);
//   }
//   
//   public float[] getNormalData(float[] vertexData) {
//      return attributeList.get(normalAttributeIndex).getVertexData(vertexData);
//   }


     
//   public SkinController getSkinController() {
//      SkinController sc = new SkinController(this);
//      sc.setVertexCoordData(getVertexCoordData(null));
//      sc.setNormalData(getNormalData(null));
//      return sc;
//      
//   }



//   /**
//    * Retrieves a named attribute; this could be null if the attribute is not defined for this GMesh.
//    * This method can be used  for fixed-function OpenGL style attributes as well as for user-defined attributes.
//    */
//   public GLVertexAttribute getGLVertexAttribute(String attributeName) {
//      for (GLVertexAttribute attr: attributeList) if (attributeName.equals(attr.getName())) return attr;
//      return null;
//   }
  
//   /** 
//    * sets the vertex secondary color data by copying the floats from colorData. 
//    * Every four consecutive floats define a secondary color.
//    */
//   public void setVertexData(String attributeName, float[] vertexData) { 
//      
// 
//   }  
     
   
      
   /** 
    * initializes the OpenGL ARRAY and ELEMENT_ARRAY buffers.
    * Calculates the buffer offsets for coordinates, normals. colors etc. inside vertexBuffer 
    */
   public void glInit(GLRenderContext glc) {
      //int[] bufNames = new int[2];
      IntBuffer bufNames = BufferUtil.directIntBuffer(2);
      glc.gl2.glGenBuffers(2, bufNames);
      vertexBufferId = bufNames.get(0);
      indexBufferId = bufNames.get(1);
      
      // get current shader prog      
      int[] progarray = new int[1]; // was: int[16] ??
      glc.gl2.glGetIntegerv(GL2.GL_CURRENT_PROGRAM, progarray, 0);
      int prog = progarray[0];      
      
      bindShaderProg(glc, prog);
            
          
   }
   
   /**
    * Binds the attribute indices, and defines the OGL Array Buffer for the specified 
    * OGL Shader program
    */
   public void bindShaderProg(GLRenderContext glc, int prog) {
      // determine the offsets within the VBO, for all (active) attributes:
      int bufferOffset = 0;   
     // hmi.util.Console.println("GLBasicMesh.bindShaderProg  attributeList size: " + attributeList.size());
      for (GLVertexAttribute attr : attributeList) {
         int index = attr.setAttributeIndex(glc, prog); // will set the index for GLSL attributes, no effect on fixed function attributes
         //hmi.util.Console.println(" Attribute index for " + attr.getName() + " = " + index);
         if (index == -1) continue;                    // skip the rest of the loop body if not an active attribute    
         //hmi.util.Console.println(" Attribute offset = " + bufferOffset);                 
         attr.setArrayBufferOffset(bufferOffset);
         //attr.glInit(gl);
         
         bufferOffset += attr.getByteBufferSize();    
        // hmi.util.Console.println("GLBasicMesh bufferOffset after loading attribute " + attr.getAttributeName() + "  : " + bufferOffset);      
      }
      // allocate arraybuffer space      
      int arrayBufferSize = bufferOffset;
      glc.gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vertexBufferId);
      glc.gl.glBufferData(GL.GL_ARRAY_BUFFER, arrayBufferSize, (FloatBuffer)null, GL2.GL_STREAM_DRAW);  
   }
   
   /**
    * renders the mesh, using the vertexBuffer data.
    */
   public void glRender(GLRenderContext glc) {
      glc.gl2.glDisableClientState(GL2.GL_INDEX_ARRAY);
      glc.gl2.glBindBuffer(GL.GL_ARRAY_BUFFER, vertexBufferId); 
      
      for (GLVertexAttribute attr : attributeList) {
//         if (getId()!= null ) {
//            hmi.util.Console.println("GLBasicMesh " + getId() + " glRender attribute: " + attr.getName());
//         }
         attr.glRender(glc);
      }
      
      if (indexBuffer != null) {
         //hmi.util.Console.println("GLBasicMesh.glRender indexBuffer ");
         glc.gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, indexBufferId);
         if (indexBufferModified) {
            indexBuffer.rewind();            
            glc.gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, indexByteBufferSize, indexBuffer, GL.GL_STATIC_DRAW);
            indexBufferModified = false;
         }  
         //gl.glEnableClientState(GLC.GL_INDEX_ARRAY);
         glc.gl2.glDisableClientState(GL2.GL_INDEX_ARRAY);
         glc.gl2.glDrawRangeElements(geometryType, 0, nrOfVertices, nrOfIndices, GL.GL_UNSIGNED_INT, 0L);
        // gl.glDrawRangeElements(GLC.GL_TRIANGLES, 0, nrOfVertices, nrOfIndices, GLC.GL_UNSIGNED_INT, 0L);
      }
//      if (vertexCoordBuffer != null)    gl.glDisableClientState(GLC.GL_VERTEX_ARRAY);

   }


//   public String attributesToString() {
//       StringBuilder buf = new StringBuilder();
//       for (GLVertexAttribute va : attributeList) {
//            buf.append('\n');
//            va.appendTo(buf);  
//       }
//       return buf.toString();
//   }

   /*
    * Appends the Mesh' attributes to the specified StringBuilder.
    * (Introduced for GLBasicMesh extensions like GLSkinnedMesh)
    */
   public StringBuilder appendAttributesTo(StringBuilder buf, int tab) {
       for (GLVertexAttribute va : attributeList) {
           buf.append('\n');
           va.appendTo(buf, tab);  
       }
       return buf;
   }


   public StringBuilder appendTo(StringBuilder buf, int tab) {
      GLUtil.appendSpacesString(buf, tab, "GLBasicMesh \""); buf.append(id) ; buf.append('"');
      if (showDetail()) {
         GLUtil.appendSpacesString(buf, tab, "nrOfVertices: "); buf.append(nrOfVertices);
         GLUtil.appendSpacesString(buf, tab, "nrOfIndices: "); buf.append(nrOfIndices);
      }
      if (showAttributes()) {
         for (GLVertexAttribute va : attributeList) {
            buf.append('\n');
            va.appendTo(buf, tab+GLUtil.TAB);  
         }
      }
      return buf;
   }

   /**
    * toString represents the mesh in String format.
    * The amount of info depends on the settings for showDetail and showAttributes.
    */
   public String toString() {
      StringBuilder buf = appendTo(new StringBuilder(), 0);

      return buf.toString();
   }

   /* boolean flags that determine the amount of detail included by toString() */
   private static boolean showDetail = true;
   private static boolean showAttributes = true;
   
   /**
    * Sets the showDetail mode for toString()
    */
   public static  void setShowDetail(boolean show) {
      showDetail = show;
   }
   
   /**
    * Sets the showAttributes mode for toString()
    */
   public static  void setShowAttributes(boolean show) {
      showAttributes = show;
   }

   public boolean showDetail() { return showDetail; }
   
   public boolean showAttributes() { return showAttributes; }


//   private java.nio.IntBuffer indexBuffer;        // buffer containing mesh indices. 
//   private int nrOfIndices;                       // size of indexBuffer, in number of ints, i.e. number of mesh indices.         
//   private int indexByteBufferSize;               // size of indexBuffer, in number of bytes.  
//   private boolean indexBufferModified;           // "dirty bit" for indexBuffer.              
//                              
//   protected int nrOfVertices;                      // number of vertices in the vertex data buffers.        
//   
//   // The vertex attributes are kept in a list of GLVertexAttribute objects:
//   private ArrayList<GLVertexAttribute> attributeList = new ArrayList<GLVertexAttribute>(16);
//   private int vertexCoordAttributeIndex;  // attributeList index for the VertexCoord attribute
//   private int normalAttributeIndex;       // attributeList index for the normal attribute
//   
//   
//   private int vertexBufferId;                    // OpenGL id for vertex buffer
//   private int indexBufferId;                     // OpenGL id for index buffer
//   int geometryType = GLC.GL_TRIANGLES;            // type of OpenGL primitives.
//
//   private String id;



//   public String toString() {
//      int mode = GLBasicMesh.COORDS | GLBasicMesh.NORMALS | GLBasicMesh.TEXTURE_COORDINATES;
//      return toString(mode);
//      
//   }
//
//  /**
//    * returns a String representation of the Mesh, that is useful for debugging purposes.
//    */
//   public String toString(int mode) {
//      StringBuilder buf = new StringBuilder();
//      buf.append("===============GLBasicMesh=======================");
//      buf.append("\nnrOfVertices = ");   buf.append(nrOfVertices);
//      buf.append("\nnrOfIndices = ");   buf.append(nrOfIndices);
// 
//      buf.append('\n'); 
//      buf.append("\nnrOfTexUnits = " + nrOfTexUnits ); 
//      buf.append("\nnrOfAttributes = " + nrOfAttributes );
//     // buf.append("\nnrOfJointUnits = " + nrOfJointUnits );
//
//      buf.append("\nGLBasicMeshData:\n");
//      for (int vi=0; vi < nrOfVertices; vi++) {
//          appendVertexString(buf, vi, mode);
//          buf.append("\n");      
//      }
//      
//      buf.append("IndexData:\n");
//      for (int i=0; i<nrOfIndices-1; i++) {
//          buf.append(indexBuffer.get(i)); buf.append(", ");
//      }
//      if (nrOfIndices > 0) buf.append(indexBuffer.get(nrOfIndices-1));
//      buf.append('\n');
//
//    
//      return buf.toString();   
//   }

//   /*
//    * append a String representation of a single vertex. mode specifies which vertex components should be included.
//    */
//   private StringBuilder appendVertexString(StringBuilder buf, int vertexIndex, int mode) {
//      int base;
//      if ((mode & GLBasicMesh.COORDS) != 0 && vertexCoordBuffer != null) {
//         base = vertexIndex * GLBasicMesh.VERTEXCOORDSIZE;
//         buf.append(" v[");
//         buf.append(vertexIndex);
//         buf.append("]=(");
//         for (int i=0; i<GLBasicMesh.VERTEXCOORDSIZE-1; i++) { buf.append(vertexCoordBuffer.get(base+i)); buf.append(", "); }
//         buf.append(vertexCoordBuffer.get(base+GLBasicMesh.VERTEXCOORDSIZE-1)); 
//         buf.append(")");
//      }
//      if ((mode & GLBasicMesh.NORMALS) != 0 && normalBuffer != null) {
//         base = vertexIndex * GLBasicMesh.NORMALSIZE;
//         buf.append("  n=(");
//         for (int i=0; i<GLBasicMesh.NORMALSIZE-1; i++) { buf.append(normalBuffer.get(base+i)); buf.append(", "); }
//         buf.append(normalBuffer.get(base+GLBasicMesh.NORMALSIZE-1)); 
//         buf.append(")");
//      }
//      if ((mode & GLBasicMesh.COLORS) != 0 && colorBuffer != null) {
//         base = vertexIndex * GLBasicMesh.COLORSIZE;
//         buf.append("  c=(");
//         for (int i=0; i<GLBasicMesh.COLORSIZE-1; i++) { buf.append(colorBuffer.get(base+i)); buf.append(", "); }
//         buf.append(colorBuffer.get(base+GLBasicMesh.COLORSIZE-1)); 
//         buf.append(")");
//      }
//      if ((mode & GLBasicMesh.SECONDARY_COLORS) != 0 && secondaryColorBuffer != null) {
//         base = vertexIndex * GLBasicMesh.COLORSIZE;
//         buf.append("  sc=(");
//         for (int i=0; i<GLBasicMesh.COLORSIZE-1; i++)  { buf.append(secondaryColorBuffer.get(base+i)); buf.append(", "); }
//         buf.append(secondaryColorBuffer.get(base+GLBasicMesh.COLORSIZE-1)); 
//         buf.append(")");
//      }
//      
//      if ((mode & GLBasicMesh.TEXTURE_COORDINATES) != 0 && nrOfTexUnits > 0) {
//         buf.append("  t=");
//         for (int texUnit=0; texUnit<nrOfTexUnits; texUnit++) {
//            base = vertexIndex * GLBasicMesh.TEXCOORDSIZE;
//            buf.append(" (");
//            for (int i=0; i<GLBasicMesh.TEXCOORDSIZE-1; i++) { buf.append(texCoordBuffer[texUnit].get(base+i) ); buf.append(", "); }
//            buf.append(texCoordBuffer[texUnit].get(base+GLBasicMesh.TEXCOORDSIZE-1));
//            buf.append(") ");
//         }                    
//      }  
//      if ((mode & GLBasicMesh.VERTEX_ATTRIBUTES) != 0  && nrOfAttributes > 0) {
//         buf.append("  a=");
//         for (int attrib=0; attrib<nrOfAttributes; attrib++) {
//            base = vertexIndex * GLBasicMesh.VERTEXATTRIBSIZE;
//            buf.append(" (");
//            for (int i=0; i<GLBasicMesh.VERTEXATTRIBSIZE-1; i++) { buf.append(vertexAttribBuffer[attrib].get(base+i)) ; buf.append(", "); }
//            buf.append(vertexAttribBuffer[attrib].get(base+GLBasicMesh.VERTEXATTRIBSIZE-1));
//            buf.append(") ");
//         }
//      }
//
//      return buf;
//   }

             
}
