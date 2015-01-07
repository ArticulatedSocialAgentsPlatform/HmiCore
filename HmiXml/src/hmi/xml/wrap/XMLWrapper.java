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

package hmi.xml.wrap;

import hmi.xml.XMLStructure;

/**
 * XMLWrapper is an extension of XMLStructure; it adds the unwrap method.
 * XMLWrapper is intended for XMLStructures that are really wrappers for 
 * other Java classes, that are not XMLStructures themselves. 
 * The reason for this could be that some class already inherits from some 
 * non-XMLStructure class, or that it is existing code, or a standrad java class
 * like String etc.

 * public Object unwrap();
 * public Class wrappedClass();
 * @param <T> base class being wrapped
 * @author Job Zwiers
 */
public interface XMLWrapper<T> extends XMLStructure
{
     
  /**
   * Many XMLStructure Objects are really "wrappers" for other Objects.
   * Considering an XMLStructure as a "wrapper" for some Object,
   * the unwrap method yields this original Object.
   */
  T unwrap();
  
}
