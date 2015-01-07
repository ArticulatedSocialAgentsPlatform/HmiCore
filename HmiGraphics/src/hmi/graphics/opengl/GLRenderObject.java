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
