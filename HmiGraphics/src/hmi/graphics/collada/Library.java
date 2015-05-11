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

import java.util.List;

/** 
 * Generic Libraray, super class for Library-XYZ elements
 * @param <X> type of library,like Node, AnimationClip etc.
 * @author Job Zwiers
 */
public class Library<X extends ColladaElement> extends ColladaElement {
 
   public Library() {
     super();
   } 
     
 
 
   public Library (Collada collada)  {
      super(collada);  
   }
 
   /**
    * 
    */
   public List<X> getLibContentList() {
      return null;
   }
 
   public X getLibItem(String id) {     
      List<X> libContentList = getLibContentList();       
      if (libContentList.size() == 0) {
         getCollada().warning("Collada: empty library");  
      }
      for (X libItem : libContentList) {
         if (libItem.getId().equals(id)) return libItem;  
      }
      return null;
   }
 

}
