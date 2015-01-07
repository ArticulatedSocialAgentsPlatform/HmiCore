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

 
package hmi.graphics.opengl.state;

import hmi.graphics.opengl.GLRenderContext;
import hmi.graphics.opengl.GLRenderObject;

/**
 * A list of GLStateComponents. For each StateComponent type, identified by its Id, at most one
 * instance can be part of a GLStateComponentList.
 * @author Job Zwiers
 */
public class GLStateComponentList implements GLRenderObject {

   private int arraySize = 0;       // current size of the arrays.
   private GLStateComponent[] stateComp; // attributes
   private int[] compId;            // attribute ids, corresponding to stateComp.
   private int size = 0;            // current length of list: stateComp[0:size-1] and compId[0:size-1] are defined
   private static final int DEFAULTSIZE = 4;
   
   
   /**
    * Create an empty state component list.
    */
   public GLStateComponentList() {
      
   }
  
   /* (re)allocates the stateComp and compId arrays such that the array sizes are greate or equal than "size" */
   private void ensureArraySize(int requestedSize) {
      if (requestedSize <= arraySize) return;
      if (arraySize == 0) arraySize = DEFAULTSIZE;
      while (arraySize < requestedSize) arraySize *= 2;
      GLStateComponent[] newStateComp = new GLStateComponent[size];
      int[] newCompId = new int[arraySize];
      if (stateComp != null) {
         System.arraycopy(stateComp, 0, newStateComp, 0, size);
         System.arraycopy(compId, 0, newCompId, 0, size);   
      }
      stateComp = newStateComp;
      compId = newCompId;
   }
  
   /**
    * Inserts some GL state component object into the list, such
    * that the list remains sorted by means of the id values.
    */
   public void addStateComponent(GLStateComponent sc) {
       ensureArraySize(size+1);
       int scId = sc.getSCId();
       int index = findIndex(scId);
       for (int i=size-1; i >= index; i--) {
         stateComp[i+1] = stateComp[i];
         compId[i+1] = compId[i];
       }
       compId[index] = scId;
       stateComp[index] = sc;
       size++; 
   }
  
   /**
    * Return the GLStateComponent stored for index id, or null
    * if no list element was found for the specified id
    */
   public GLStateComponent getStateComponent(int scId) {
      int index = findIndex(scId);
      return (compId[index]==scId) ? stateComp[index] : null;  
   }
  
   /*
    * Pre: compId is sorted. 
    * Post: if findIndex(id) == x,
    * then either compId[x] == id (found)
    * else, x == first index such that for all i<x, compId[i] < id.
    * This can be any index in the range [0:size]
    */
   public final int findIndex(int scId) {
      if ( size == 0 || scId <= compId[0] ) return 0;
      // size >= 1  compId[0] < id
      if ( scId > compId[size-1] ) return size;  
      // size >= 1 &&  compId[0] < id <= compId[size-1]
      int low = 0;
      int high = size-1;
      // LoopInvariant: low <= high && compId[low] < id <= compId[high]     
      while (high-low > 1) {
         int mid = (low+high)/2;
         if (compId[mid] == scId) return mid;
         if (compId[mid] < scId) {
            low = mid;
         } else { // id < compId[mid]
            high = mid;
         }
      }
      // low == high-1 && compId[high-1] < id <= compId[high] 
      return high; // appropriate place for insertion
   }
  


   /*
    * XML appendContent method
    */
   public StringBuilder appendContent(StringBuilder buf, int tab) {
      for (int i=0; i<size; i++) {
         buf.append(compId[i]);
         buf.append(" (stateComp) ");
      }
      return buf;
   }


   /**
    * Yields a String representation of the (ids of) the list
    */
   public String toString() {
       StringBuilder buf = new StringBuilder();
       buf.append("GLStateComponentList[");
       appendContent(buf, 0);
       buf.append("]");
       return buf.toString();       
   }


   /**
    * Required by GLRenderObject interface.
    */
   public void glInit(GLRenderContext glc) { 
      for (int i=0; i<size; i++) {
         stateComp[i].glInit(glc);  
      }
   }
   
   /**
    * Required by GLRenderObject interface.
    */
   public void glRender(GLRenderContext glc) {
      for (int i=0; i<size; i++) {
         stateComp[i].glRender(glc);  
      }
   }
                 
}
