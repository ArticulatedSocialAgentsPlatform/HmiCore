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
/**
 * @(#) GLRenderObject.java
 * @version 1.1   30/5/2012
 * @author Job Zwiers
 */


package hmi.graphics.opengl;

/**
 * Interface for all objects that can be &quot;rendered&quot; in a Jogl GL window.
 * This does not necessarily imply that any direct visualization is associated 
 * with the object, but rather that the &quot;glInit&quot; method can be called, 
 * while the OpenGL context is initializing, and that the &quot;glRender&quot; method 
 * can be called for every frame that is being rendered.
 * These methods will be called when a &quot;current&quot; OpenGL context is available,
 * passed on via the GLRenderContext parameter.
 * Note that OpenGL &quot;initialization&quot; typically happens only once, but might be called several times,
 * so the glInit method should be able to deal with repreated calls. The glRender call on the other hand
 * will be called repeatedly, typically once for every frame being rendered.
 * 
 */
public interface GLRenderObject {

   /**
    * Called during OpenGL initialization.
    */
   void glInit(GLRenderContext gl);
   
   /**
    * Called during openGL rendering.
    */
   void glRender(GLRenderContext gl);                 
}
