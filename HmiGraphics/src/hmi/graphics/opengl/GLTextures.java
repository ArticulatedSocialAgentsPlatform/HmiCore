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
/*
 */

package hmi.graphics.opengl;
import hmi.util.ResourcePool;
import hmi.util.Resources;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 
/**
 * A utility class for GLTextures, in particular for loading and caching GLTexture objects.
 * Basic usage: 1) GLTextures.getGLTexture(imageFile) will retrieve (and cache) a texture file
 * within the default texture resource directory. (Default for HmiShared projects: the resource/textures directory)
 * 2) GLTextures.addTextureDirectory(textureDir) adds the textureDir directory for searching textures image files.
 * All texture directories are loaded as Java resources, implying that the path where some texture directory is located
 * must be included on the Java classpath. Such is the case for the resource directory within a HmiShared style project.
 */
public class GLTextures { 
   
   private static Logger logger = LoggerFactory.getLogger(GLTextures.class.getName());

   
   public static void addTextureDirectory(String dir) {
     pool.addResourceDirectory(dir);
   }

   /**
    * Tries to get a GLTexture with the specified resource name, either from the cache
    * or else from loading (and caching) an image from one of the texture directories.
    * The image is flipped vertically and when necessary rescaled to a power-of-two size.
    */
   public  static GLTexture getGLTexture(String resourceName) {
      return getGLTexture(resourceName, true, true);
   }

   /**
    * Tries to get a GLTexture with the specified resource name.
    * GLTexture objects found and loaded will be cached.
    * The flip and rescale attributes are used when the texture is
    * loaded for the first time (not when a cached version is retrieved).
    * They denote whether the image should be flipped vertically (usually they should
    * for OpenGL texturing), and whether a non-power-of-two image should be rescaled
    * to the next power-of-two size. (Default is to rescale)
    */
   public  static GLTexture getGLTexture(String resourceName, boolean flip, boolean rescale)  {
      try {
         return (GLTexture) pool.getResource(resourceName, new Object[]{ flip, rescale});
      } catch (IOException e) {
          logger.error("GLTextures: " + e);  
          return null;
      }
   }

   private static ResourcePool pool  = new ResourcePool();
   
   public static final String DEFAULT_TEXTURE_RESOURCE_DIR = "textures";
   static {
      ResourcePool.ResourceLoader  loader = 
      new ResourcePool.ResourceLoader() {   
          /* The actual method for loading of texture resources    */
          public Object loadResource(Resources res, String resourceName, Object[] par) throws IOException {
             boolean flip = (Boolean) par[0];
             boolean rescale = (Boolean) par[1];
             return GLTextureLoader.readFromResourceFile(res, resourceName, rescale, flip ) ;
          }
      };
      pool.setResourceLoader(loader);
      pool.addResourceDirectory(DEFAULT_TEXTURE_RESOURCE_DIR);   // add default Pool for DEFAULT_TEXTURE_RESOURCE_DIR directory
   }
   
} 
