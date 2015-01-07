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
/**
 *
 */

package hmi.graphics.opengl;

import hmi.graphics.opengl.state.GLMaterial;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 */
public class GLRenderList implements GLRenderObject {

   private static final GLRenderObject[] EMPTYLIST = new GLRenderObject[0];
   private GLRenderObject[] renderList = EMPTYLIST;
   private int arraySize = 0;       // current size of the arrays.
   private int size = 0;                // size of the List
   private static final int DEFAULTSIZE = 8;
   private static Logger logger = LoggerFactory.getLogger(GLRenderList.class.getName());


   public GLRenderList() {
//      arraySize = DEFAULTSIZE;
//      size = 0;
//      renderList = new GLRenderObject[arraySize];    
   }


   public GLRenderList(int capacity) {
      ensureArraySize(capacity);
   }

   /* (re)allocates the renderList and compId arrays such that the array size isat least  "requestedSize" */
   private void ensureArraySize(int requestedSize) {
      if (requestedSize <= arraySize) return;
      if (arraySize == 0) arraySize = DEFAULTSIZE;
      while (arraySize < requestedSize) arraySize *= 2;
      GLRenderObject[] newRenderList = new GLRenderObject[arraySize];
      System.arraycopy(renderList, 0, newRenderList, 0, size);
      renderList = newRenderList;
   }

   /**
    * returns the size of the list
    */
   public int size() {
      return size;
   }

   /** returns true iff the GLrenderList is empty */
   public boolean isEmpty() { return size==0; }

   /**
    * Adds some GLRenderObject to the list
    */
   public void add(GLRenderObject ro) {
      ensureArraySize(size+1);
      renderList[size] = ro;
      size++;  
   }

   /**
    * Appends all elements from the specified list.
    * This list is allowed to have size 0, or can even be null.
    */
   public void addAll(GLRenderList rlist) {
      if (rlist == null) return;
      ensureArraySize(size + rlist.size);
      for (int i=0; i<rlist.size; i++) {
         renderList[size+i] = rlist.renderList[i];
      }
      size += rlist.size;  
   }

   /**
    * Adds some GLRenderObject to the beginning of the list.
    * That is, this GLRenderObject will be rendered before other objects that are already
    * on the list.
    */
   public void prepend(GLRenderObject ro) {
      ensureArraySize(size+1);
      for (int i=size; i>0; i--) {
         renderList[i] = renderList[i-1];  
      }
      renderList[0] = ro;
      size++;  
   }


   /**
    * Returns list element with index i
    */
   public GLRenderObject get(int i) {
      if (i<0 || i >= size ) return null;
      return renderList[i];  
   }

   /**
    * Required by GLRenderObject interface.
    */
   public void glInit(GLRenderContext glc) { 
      for (int i=0; i<size; i++) {
         renderList[i].glInit(glc);  
      }
   }
   
   /**
    * Required by GLRenderObject interface.
    */
   public void glRender(GLRenderContext glc) {
      for (int i=0; i<size; i++) {
         renderList[i].glRender(glc);  
      }
   }
   
   
   public StringBuilder appendTo(StringBuilder buf, int tab) {
      GLUtil.appendSpacesString(buf, tab, "GLRenderList (");
      for (int i=0; i<size; i++) {
         buf.append("\n");
         GLRenderObject robj = renderList[i];
         if (robj instanceof GLRenderList) {
            ((GLRenderList) robj). appendTo(buf, tab+GLUtil.TAB);
         } else if (robj instanceof GLShape) {
            ((GLShape) robj).appendTo(buf, tab+GLUtil.TAB);
         } else if (robj instanceof GLMaterial) {
            ((GLMaterial) robj).appendTo(buf, tab+GLUtil.TAB);
         } else if (robj instanceof GLSkinnedMesh) {
            ((GLSkinnedMesh) robj).appendTo(buf, tab+GLUtil.TAB);
         } else if (robj instanceof GLBasicMesh) {
            ((GLBasicMesh) robj).appendTo(buf, tab+GLUtil.TAB);
         } else {
              logger.info("GLrenderList.appendTo, unknown class:" + robj.getClass().getName());
            
              buf.append(robj.toString());   
         }
         
      }
      buf.append("\n");
      GLUtil.appendSpaces(buf, tab);
      buf.append(")"); 
      return buf;
   }
   
    @Override
    public String toString() {
      StringBuilder buf = appendTo(new StringBuilder(),0);
      return buf.toString();
   }
   
 
                 
}
