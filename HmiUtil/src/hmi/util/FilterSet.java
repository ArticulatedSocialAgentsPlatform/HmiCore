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
