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