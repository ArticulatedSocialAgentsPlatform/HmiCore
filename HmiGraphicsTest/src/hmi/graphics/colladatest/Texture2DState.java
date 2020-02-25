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
package hmi.graphics.colladatest;
import hmi.graphics.opengl.*;



/**
 * A GLRenderObject for setting texture2D parameters.
 */
public class Texture2DState implements GLRenderObject {

  int texId;
  String texResourceDir;
  String textureFile;
  public static String defaultTextureResourceDir = "textures";
  
 
  /**
    * Create a new Texture2D state object
    */
   public Texture2DState(String textureFile) {
      this(defaultTextureResourceDir, textureFile);
   }
 
  
   /**
    * Create a new Texture2D state object
    */
   public Texture2DState(String texResourceDir, String textureFile) {
        this.texResourceDir = texResourceDir;
        this.textureFile = textureFile;
   }
 

 
   public void glInit(GLRenderContext gl) {
      texId = Texture.loadGLTexture(gl, texResourceDir, textureFile, GLC.GL_LINEAR_MIPMAP_LINEAR, GLC.GL_RGB, false);
   }
   
     
     
   public void glRender(GLRenderContext gl) {
      gl.glEnable(GLC.GL_TEXTURE_2D); 
      gl.glBindTexture(GLC.GL_TEXTURE_2D, texId);
      gl.glTexEnvi(GLC.GL_TEXTURE_ENV, GLC.GL_TEXTURE_ENV_MODE, GLC.GL_MODULATE);
//      gl.glTexEnvi(GLC.GL_TEXTURE_ENV, GLC.GL_TEXTURE_ENV_MODE, GLC.GL_REPLACE);
   } 
      
  
}
