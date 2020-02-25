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
