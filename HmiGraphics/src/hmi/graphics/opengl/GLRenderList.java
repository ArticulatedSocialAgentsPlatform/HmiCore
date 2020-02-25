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
