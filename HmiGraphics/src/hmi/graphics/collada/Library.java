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
