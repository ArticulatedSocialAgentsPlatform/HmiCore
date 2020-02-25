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
package hmi.util;
import java.util.*;
//import java.io.*;

/**
 * Id's are objects that uniquely characterize objects.
 * Id's can only be obtained via the static forName() method. 
 * This method ensures that the Id object for a given String is unique.
 * As a consequence  one may assume that Id's can be compared for equality by means
 * of the "==" test.
 * This results in very efficient equality testing for Id's, even when these are based
 * on (long) Strings like URI's etc.
 * @author Job Zwiers 
 */
 
public final class Id implements Cloneable
{

   /*
    * The private constructor for Id's.
    * It can be called only via the public static getId method.
    */
   private Id(String idString) {
      this.idString = idString;
      hash = idString.hashCode();
   }

   /**
    * The String representation of this Id object.
    * Note that this String uniquely characterizes the Id object.
    */
   @Override
   public String toString() {
      return idString;
   }

   /**
    * equals for Id is automatically based upon "==" testing
    */

   /**
    * optimized hashCode, uses cached hash code of the id String
    */
   @Override
   public int hashCode() {
      return hash;
   }

   /**
    * Real cloning an Id is not permitted, as it would violate
    * the assumption that Id's are unique objects.
    * The Id.clone() operation still allows one to call clone()
    * for Id Objects, but simply returns the Id object itself.
    */ 
   @Override
   public Object clone() {
      return  this ; 
   }

   /**
    * returns the Id object for idString. Repeated calls for the same String
    * will return the same object.
    */
   public  static synchronized Id forName(String idString) {
      if (idString == null) return null;
      Id id =  ids.get(idString);
      if (id == null)  {
         id = new Id(idString);
         ids.put(idString, id); 
      }
      return id;      
   }

   /**
    * Equivalent to Id.forName. returns the Id object for idString. Repeated calls for the same String
    * will return the same object.
    */
   public static synchronized Id getId(String idString) {
      return forName(idString);
   }

   /* instance attributes: */
   private String idString;
   private int hash;

   /* class attributes: */
   private static final int ID_HASHMAP_SIZE = 200;
   private static Map<String, Id> ids = new HashMap<String, Id>(ID_HASHMAP_SIZE); /* The collection of all allocated Id's. */  
 
}