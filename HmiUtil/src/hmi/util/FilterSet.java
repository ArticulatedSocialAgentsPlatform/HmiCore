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

/**
 * A FilterSet is an implementation of Set, based upon LinkedHashSet,
 * that adds the filter(Predicate) method, and a map method.
 * @param <E> base type for the set
 * @author Job Zwiers
 */
public class FilterSet<E> extends LinkedHashSet<E>
//implements Predicate<E>
{
   
  /**
   * FilterSet.Transforms are objects that define a "mapping" from Objects to Object.
   * The transform could modify the Object "in place", or not.
   * In both cases the modified Object must be returned.
   */ 
  public interface Transform<S, T> {
     T  transform(S obj);
  }

  /**
   * FilterSet.Predicates are Objects that implement a boolean test on Objects,
   * in the form of their "valid" method. If some Object obj satifies
   * some predicate pred, then pred.valid(obj) should yield "true".
   */
  public interface Predicate<T> {
     boolean valid(T obj);
  } 
   
   /**
    * Constructs a new, empty linked hash set with the default initial capacity (16) 
    * and load factor (0.75).
    */
   public FilterSet() {
       super();     
   }

   /**
    * Constructs a new linked hash set with the same elements as the specified collection.
    */
   public FilterSet(Collection<E> col) {
       super(col);     
   }


   /**
    * Constructs a new, empty linked hash set with the specified initial capacity 
    * and the default load factor (0.75).
    */
   public FilterSet(int initialCapacity) {
       super(initialCapacity);     
   }
   
   /**
    * Constructs a new, empty linked hash set with the specified initial capacity and load factor.
    */
   public FilterSet(int initialCapacity, float loadFactor) {
       super(initialCapacity, loadFactor);     
   }

   /**
    * returns an Iterator for all Set elements that satisfy the given Predicate.
    */
   public Iterator<E> filter(final Predicate<E> pred) {
       return new Iterator<E>() {
          public  boolean hasNext() { 
             if (objUsed) toNext();
             return (obj != null);
          }
          public E next() {
              if (objUsed) toNext();
              objUsed = true;
              return obj;
          }
          public void remove() {
              setIter.remove();
          } 
          /* advance to next selected VirtualObject, 
           * or null, if there is no such Object anymore.  */
          private void toNext() {
             objUsed = false;
             while (setIter.hasNext()) {              
                obj = setIter.next();
                if (pred.valid(obj)) return; 
             } 
             obj = null;
          }
          private Iterator<E> setIter = iterator();
          private E obj;
          private boolean objUsed = true;
       };
   }

   /**
    * Transforms the set, by applying the given Transform on each Set element.
    * The transformation is "in place", i.e., no Set elements are added or removed,
    * but rather each element is modified by the Transform.
    */
   public void map(final Transform<E, E> t) {
      Iterator<E> setIter = iterator();
      while (setIter.hasNext()) {
         t.transform(setIter.next());  
      }  
   }

//   /**
//    * This method turns the Set into a Predicate itself, defined to be "valid"
//    * for Object obj iff the Object obj belongs to the Set.
//    */
//   public boolean valid(E obj) {
//      return contains(obj);
//   }

   public static final long serialVersionUID = 0;

  
}
