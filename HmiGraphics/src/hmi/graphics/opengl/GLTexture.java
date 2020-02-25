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
package hmi.graphics.opengl;

import hmi.graphics.util.BufferUtil;

import java.nio.Buffer;
import java.nio.IntBuffer;
import javax.media.opengl.*; 

 
/**
 * A wrapper for OpenGL texture objects, in the form of an GLRenderObject.
 */
public class GLTexture implements GLRenderObject  { 
   
   private int textureId = -1;
   private int textureUnit = 0;
   private int glTextureUnit = GL.GL_TEXTURE0 + textureUnit;
   private int minificationFilter = GL.GL_LINEAR_MIPMAP_LINEAR;
   private int magnificationFilter = GL.GL_LINEAR;
   private float anisotropy = 1.0f;   // 1.0 = no anisotropy
   private int wrap_S = GL.GL_REPEAT;
   private int wrap_T = GL.GL_REPEAT;
   private int genMipMap = GL.GL_TRUE;
   private int width, height;
   private int format, type;
   private boolean hasAlpha;
   private Buffer imageBuffer;
   private int internalFormat;
   
   private String imageFile = null;
   
   static final int ZEROLEVEL = 0;
   static final int NOBORDER = 0; 
   
   
   public static float max_anisotropy; 
   
   
   /**
    * Default constructor
    */
   public GLTexture() {
   }
   
   /**
    * Creates a new GLTexture object with specified texture parameters
    * The imageFile name is optional, but still useful for debugging purposes.
    */
   public GLTexture(int width, int height, int format, int type, boolean hasAlpha, Buffer image, String imageFile) {
      this.width = width;
      this.height = height;
      this.format = format;
      this.type = type;
      this.hasAlpha = hasAlpha;
      this.imageBuffer = image;
      this.imageFile = imageFile;
   }
   
   

   /**
    * Sets the (optional) image file name, for debugging purposes
    */
   public void setImageFileName(String imageFile) {
      this.imageFile = imageFile;  
   }
  
   /**
    * returns the image file name
    */
   public String getImageFileName() {
      return imageFile;
   }
   
   public String toString() {
      if (imageFile == null) {
         return "(<texture>";
      } else {
         return imageFile;
      }
   }
   
   public int getWidth() {
      return width;
   }
   
   public int getHeight() {
      return height;
   }
   
   public int getFormat() {
      return format;
   }
   
   public int getType() {
      return type;
   }
   
   public boolean hasAlpha() {
      return hasAlpha;
   }
   
   public Buffer getImageBuffer() {
      return imageBuffer;
   }
   
   public void setFormat(int format) {
      this.format =format;
   }
   
   
   public void setWidth(int width) {
      this.width = width;
   }
   
   public void setHeight(int height) {
      this.height = height;
   }
   
   public void setImageBuffer(Buffer buf) {
      this.imageBuffer = buf;
   }
   
   /**
    * Sets the minification filter. The default value is GLC.GL_LINEAR_MIPMAP_LINEAR
    */
   public void setMinificationFilter(int minificationFilter) {
      this.minificationFilter = minificationFilter;
   }
   
    /**
    * Sets the magnification filter. The default value is GLC.GL_LINEAR
    */
   public void setMagnificationFilter(int magnificationFilter) {
      this.magnificationFilter = magnificationFilter;
   }
   
   /**
    * Sets the anisotropy valeu. The default value is 1.0 (no anisotropic correction)
    */
   public void setAnisotropy(float anisotropy) {
      this.anisotropy = anisotropy;
   }
   
   /**
    * Sets the wrap mode for the S direction. The deafult is GLC.GL_REPEAT;
    */
   public void setWrap_S(int wrapMode) {
      wrap_S = wrapMode;
   }
   
    /**
    * Sets the wrap mode for the T direction. The deafult is GLC.GL_REPEAT;
    */
   public void setWrap_T(int wrapMode) {
      wrap_T = wrapMode;
   }
   
   /**
    * Sets the mipmap generate mode. The default is GLC.GL_TRUE
    */
   public void setGenerateMipMap(boolean generateMipMap) {
      genMipMap = (generateMipMap ? GL.GL_TRUE : GL.GL_FALSE);
   }
   
   /**
    * Sets the texture unit to be used for this texture.
    * The default value is 0
    */
   public void setTextureUnit(int tu) {
//      hmi.util.Console.println("GLTexture.setTextureUnit for " + imageFile + " to:" + tu);
      textureUnit = tu;
      glTextureUnit = GL.GL_TEXTURE0 + textureUnit;    
   }
   
   /**
    * Returns the OpenGL texture id.
    */
   public int getId() {
      return textureId;
   }
   
   /**
    * The OpenGL initialization.
    * It generates a texture id, and passes on all current texture parameters to OGL.
    */
   public void glInit(GLRenderContext glc) {
      if (textureId >= 0) return; 
      GL2ES2 gl = glc.gl;
      //int[] ids = new int[1]; 
      IntBuffer ids = BufferUtil.directIntBuffer(1); 
      gl.glGenTextures(1, ids); 
      textureId = ids.get(0);
      internalFormat = hasAlpha ? GL.GL_RGBA : GL.GL_RGB;

//      float[] fa = new float[1];
//      gl.glGetFloatv(GLC.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, fa);
//      max_anisotropy = fa[0];
      gl.glActiveTexture(glTextureUnit);
      gl.glBindTexture(GL.GL_TEXTURE_2D, textureId ); 
      gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, wrap_S); 
      gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, wrap_T); 
      gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, magnificationFilter); 
      gl.glTexParameteri(GL.GL_TEXTURE_2D,GL.GL_TEXTURE_MIN_FILTER,minificationFilter);
//      gl.glTexParameteri(GLC.GL_TEXTURE_2D, GLC.GL_TEXTURE_MAG_FILTER, GLC.GL_NEAREST); 
//      gl.glTexParameteri(GLC.GL_TEXTURE_2D,GLC.GL_TEXTURE_MIN_FILTER,GLC.GL_NEAREST);
      
      if (anisotropy != 1.0f) gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAX_ANISOTROPY_EXT, anisotropy);

      
      gl.glTexParameteri(GL.GL_TEXTURE_2D, GL2.GL_GENERATE_MIPMAP, genMipMap);
      gl.glTexImage2D(GL.GL_TEXTURE_2D, ZEROLEVEL, internalFormat, 
                         width, height,  NOBORDER, format, type, imageBuffer );             
   }  
  
   /**
    * The OpenGL rendering.
    * sets the correct texture unit and binds the texture.
    */
   public final void glRender(GLRenderContext glc) {
     // if (textureId != currentTextureId)  {
         glc.gl.glActiveTexture(glTextureUnit);
         glc.gl.glBindTexture(GL.GL_TEXTURE_2D, textureId ); 
     //    currentTextureId = textureId;
     // }
      
   }
   
//   /**
//    * Equivalent to glRender
//    */
//   public final void bind(GLRenderContext gl) {
//      gl.glActiveTexture(glTextureUnit);
//      gl.glBindTexture(GLC.GL_TEXTURE_2D, textureId ); 
//     // currentTextureId = textureId;
//   }
   
  // private static int currentTextureId = -1;


} 
