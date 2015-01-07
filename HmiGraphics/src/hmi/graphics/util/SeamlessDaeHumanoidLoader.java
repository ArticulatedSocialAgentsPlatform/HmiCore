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

import hmi.animation.Hanim;
import hmi.animation.VJoint;
import hmi.graphics.opengl.GLShape;
import hmi.graphics.opengl.scenegraph.GLScene;
import hmi.graphics.opengl.scenegraph.ScenegraphTranslator;
import hmi.graphics.opengl.scenegraph.VGLNode;
import hmi.graphics.opengl.state.GLFill;
import hmi.graphics.scenegraph.GScene;

import java.io.IOException;


/**
 * Helper class to load a seamless humanoid from a dae file. Useful when a fullfledged ElckerlycEnvironment is not needed.
 * On request, after loading, the "null pose" for the humanoid is the H-Anim null pose.
 *
 * Assumption about the collada file: 
 * - after applying the joint renaming file, the animation root joint is called HumanoidRoot
 *
 * @author Herwin van Welbergen
 * @author Dennis Reidsma
 */
public final class SeamlessDaeHumanoidLoader implements DaeHumanoidLoader
{
    private  VJoint avatarAnimationRootJoint;
    private  VGLNode avatarRenderNode;
  //  private  VJoint avatarRenderRootJoint;
    private  GLScene glScene;
    private  GScene gscene;
    
    /**
     * @return the avatarAnimationRootJoint, which is the root of a VJoint tree, to be used for body animation purposes.
     */
    @Override
    public VJoint getAvatarAnimationRootJoint()
    {
        return avatarAnimationRootJoint;
    }

    
    /**
     * @return the avatarRenderNode, containing the shapeList (A GLRenderList). The VJoint doesn't matter, and is actually null
     */
    @Override
    public VGLNode getAvatarRenderNode()
    {
        return avatarRenderNode;
    }

   
    /**
     * @return the GLScene
     */
    @Override
    public GLScene getGLScene()
    {
        return glScene;
    }
    
    /**
     * @return the GLScene
     */
    public GScene getGScene()
    {
        return gscene;
    }
    
    /**
     * Load a Collada file...
     */
    public SeamlessDaeHumanoidLoader(String id, String colladaResourceDir, String colladaFileName, String jointRenamingFileName, float scale,
        boolean setToHAnim, String colladaRenderRootNode) throws IOException
    {
        gscene = SceneIO.readGScene(colladaResourceDir, colladaFileName, (setToHAnim ? "ARMANDIA" : "NONE"));
        glScene = ScenegraphTranslator.fromGSceneToGLScene(gscene);
        avatarAnimationRootJoint = glScene.getVJoint(Hanim.HumanoidRoot); 
        renameRoot(id);
        // the avatarAnimationRootJoint is not important here; it could be null!
        avatarRenderNode = new VGLNode(avatarAnimationRootJoint, glScene.getGLShapeList());
        afterLoad();
    }
    
    /**
     * Load a Collada file...
     */
    public SeamlessDaeHumanoidLoader(String id, String colladaFile, String jointRenamingFile, float scale,
        boolean setToHAnim, String colladaRenderRootNode) throws IOException
    {
        gscene = SceneIO.readGScene(colladaFile, (setToHAnim ? "ARMANDIA" : "NONE"));
        glScene = ScenegraphTranslator.fromGSceneToGLScene(gscene);
        avatarAnimationRootJoint = glScene.getVJoint(Hanim.HumanoidRoot); 
        renameRoot(id);
        // the avatarAnimationRootJoint is not important here; it could be null!
        avatarRenderNode = new VGLNode(avatarAnimationRootJoint, glScene.getGLShapeList());
        afterLoad();
    }
    
    
    /* Rename the root joint */
    private void renameRoot(String id )
    {
       //ensure appropriate IDs: the Sid should be Hanim.HumanoidRoot; ID should be unique for this particular virtual human!
        avatarAnimationRootJoint.setId(Hanim.HumanoidRoot+"_"+id);
        avatarAnimationRootJoint.setSid(Hanim.HumanoidRoot);
        avatarAnimationRootJoint.setName("The " + Hanim.HumanoidRoot +" "+id);
    }
    
    
    /* set Elckerlyck specific structures */
    private void afterLoad() 
    {
        //fill, regardless of setting in env...
        GLShape state = new GLShape();
        state.addGLState(new GLFill());
        avatarRenderNode.getGLShapeList().prepend(state);
    }
    
}
