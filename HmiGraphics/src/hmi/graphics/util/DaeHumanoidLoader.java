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
 
package hmi.graphics.util;

import hmi.animation.VJoint;
import hmi.graphics.opengl.scenegraph.GLScene;
import hmi.graphics.opengl.scenegraph.VGLNode;

/**
 * Interface for helper classes to load a humanoid from a dae file. Useful when a fullfledged ElckerlycEnvironment is not needed.
 * 
 * @author Herwin van Welbergen
 * @author Dennis Reidsma
 */
public interface DaeHumanoidLoader 
{
    
    /**
     * @return the avatarAnimationRootJoint
     */
    VJoint getAvatarAnimationRootJoint();
    /**
     * @return the avatarRenderNode
     */
    VGLNode getAvatarRenderNode();
//    /**
//     * @return the avatarRenderRootJoint
//     */
//    VJoint getAvatarRenderRootJoint();

    /**
     * @return the GLScene
     */
    GLScene getGLScene();
    
   // GScene getGScene();
}
