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
