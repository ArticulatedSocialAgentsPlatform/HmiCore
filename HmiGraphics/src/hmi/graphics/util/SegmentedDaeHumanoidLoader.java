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
 * Helper class to load a seamless humanoid from a dae file. Useful when a fullfledged ElckerlycEnvironment is not needed. On request, after loading,
 * the "null pose" for the humanoid is the H-Anim null pose.
 * 
 * Assumption about the collada file: - the mesh for the segments is directly inside the skeleton joint structure - after applying the joint renaming
 * file, the animation root joint is called HumanoidRoot -
 * 
 * @author Herwin van Welbergen
 * @author Dennis Reidsma
 */
public final class SegmentedDaeHumanoidLoader implements DaeHumanoidLoader
{

    private  VGLNode avatarRenderNode;
    private VJoint avatarAnimationRootJoint;
    private GLScene glScene;
    private  GScene gscene;

    /**
     * @return the avatarAnimationRootJoint
     */
    @Override
    public VJoint getAvatarAnimationRootJoint()
    {
        return avatarAnimationRootJoint;
    }

    /**
     * @return the avatarRenderNode
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
        //return null;
    }




    /**
     * Load a Collada file...
     */
    public SegmentedDaeHumanoidLoader(String id, String colladaResourceDir, String colladaFileName, String jointRenamingFileName, 
                                      float scale, boolean setToHAnim) throws IOException
    {
      
        gscene = SceneIO.readGScene(colladaResourceDir, colladaFileName, (setToHAnim ? "BLUEGUY" : "NONE"));
       
        
        glScene = ScenegraphTranslator.fromGSceneToGLScene(gscene);
        avatarAnimationRootJoint = gscene.getPartBySid(Hanim.HumanoidRoot).getVJoint();
        renameRoot(id, avatarAnimationRootJoint);
        avatarRenderNode = new VGLNode(avatarAnimationRootJoint, glScene.getGLShapeList());
        afterLoad();
    }
    
  

    /* rename the root joint */ 
    private void renameRoot(String id, VJoint avatarAnimationRootJoint  )
    {
       //ensure appropriate IDs: the Sid should be Hanim.HumanoidRoot; ID should be unique for this particular virtual human!
        
        avatarAnimationRootJoint.setId(Hanim.HumanoidRoot+"_"+id);
        avatarAnimationRootJoint.setSid(Hanim.HumanoidRoot);
        avatarAnimationRootJoint.setName("The " + Hanim.HumanoidRoot +" "+id);
    }
  
  
    /* set Elckerlyck specific vars */
    private void afterLoad( ) 
    {
        //fill, regardless of setting in env...
        GLShape state = new GLShape();
        state.addGLState(new GLFill());
        avatarRenderNode.getGLShapeList().prepend(state);
    }
  

    
 

}
