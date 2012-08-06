/*******************************************************************************
 * Copyright (C) 2009 Human Media Interaction, University of Twente, the Netherlands
 * 
 * This file is part of the Elckerlyc BML realizer.
 * 
 * Elckerlyc is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Elckerlyc is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Elckerlyc.  If not, see http://www.gnu.org/licenses/.
 ******************************************************************************/

package hmi.util;

/**
 * Transforms are objects that define a "mapping" from Objects to Object.
 * They are used, for instance, by FilterSet, to define mappings on Sets.
 * The transform could modify the Object "in place", or not.
 * In both cases the modified Object must be returned.
 * @param <S> Type of Objects to be transformed
 * @param <T> Type of result of transform
 * @author Job Zwiers
 */
public interface Transform<S, T> {
   
   /**
    * transform is the method that should transform S-typed Objects into T-typed Objects.
    */
   T  transform(S obj);

}
