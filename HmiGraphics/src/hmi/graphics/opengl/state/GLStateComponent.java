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
package hmi.graphics.opengl.state;

import hmi.graphics.opengl.GLRenderObject;

/**
 * Interface for RenderObjects that are intended to set OpenGL state.
 * This interface is a simple extension of GLRenderObject, that requires a unique 
 * identifying id, called the scid, in the form of an integer. 
 * @author Job Zwiers
 */
public interface GLStateComponent extends GLRenderObject {

   
   /**
    * Returns an integer that uniquely identifies the type of the GLStateComponent.
    */
   int getSCId();
                 
}
