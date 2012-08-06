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
 * Helper class to load a humanoid from a dae file. 
 * On request, after loading, the "null pose" for the humanoid is the H-Anim null pose.
 *
 * Assumption about the collada file: 
 * - after applying the joint renaming file, the animation root joint is called HumanoidRoot
 *
 * @author Herwin van Welbergen
 * @author Dennis Reidsma
 */
public final class HumanoidLoader 
{
    private  VJoint avatarAnimationRootJoint;
    private  VGLNode avatarRenderNode;
  //  private  VJoint avatarRenderRootJoint;
    private  GLScene glScene;
    private  GScene gscene;
    
    public GScene getGScene()
    {
        return gscene;
    }
    /**
     * @return the avatarAnimationRootJoint, which is the root of a VJoint tree, to be used for body animation purposes.
     */
  
    public VJoint getAvatarAnimationRootJoint()
    {
        return avatarAnimationRootJoint;
    }

    
    /**
     * @return the avatarRenderNode, containing the shapeList (A GLRenderList). The VJoint doesn't matter, and is actually null
     */
 
    public VGLNode getAvatarRenderNode()
    {
        return avatarRenderNode;
    }

   
    /**
     * @return the GLScene
     */

    public GLScene getGLScene()
    {
        return glScene;
    }
    

    /**
     * Load a scene file, either .dae or .bin format
     * postProcessing can be "ARMANDIA", "BLUEGUY", or ""/null/"NONE"
     */
    public HumanoidLoader(String id, String resourceDir, String fileName, String postProcessing) throws IOException
    {
        gscene = SceneIO.readGScene(resourceDir, fileName, postProcessing);
        glScene = ScenegraphTranslator.fromGSceneToGLScene(gscene);
        avatarAnimationRootJoint = glScene.getVJoint(Hanim.HumanoidRoot); 
        renameRoot(id);
        // the avatarAnimationRootJoint is not important here; it could be null!
        avatarRenderNode = new VGLNode(avatarAnimationRootJoint, glScene.getGLShapeList());
        afterLoad();
    }
    
    /**
     * Load a scene file, either .dau or .bin, without postprocessing.
     */
    public HumanoidLoader(String id, String resourceDir, String fileName) throws IOException
    {
       this(id, resourceDir, fileName, "NONE");
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
