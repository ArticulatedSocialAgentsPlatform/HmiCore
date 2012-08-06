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
/* @author Job Zwiers  
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/04 14:42:37 $      
 */

package hmi.graphics.colladatest;

import java.awt.image.*;
import java.awt.image.BufferedImage; 
import java.awt.color.*;

import java.awt.geom.*;
import java.io.*;
import java.nio.*;
import javax.imageio.*;
import java.util.*;
import hmi.util.*;
import hmi.graphics.opengl.*;

 
/**
 * A class for loading and initializing texture maps.
 */
public class Texture  { 
   
   private static String sharedResourceDir = "textures";
   private  static Resources sharedResources = new Resources(sharedResourceDir);
   static java.util.logging.Logger logger = java.util.logging.Logger.getLogger("hmi.graphics");
   
   
   /**
    * Defined the directory containing texture files.
    * The directory is a resource directory, so, should be present on the Java 
    * runtime classpath
    */
   public static void setResources(String textureResourceDir) {
      sharedResources = new Resources(textureResourceDir);
   }
   
   /**
    * auxiliary method for reading the raw image from a resource file. 
    * The file is searched for in directories that are on the Java classpath.
    * By convention, this includes directories like "data" or "resource".
    */
   private  static BufferedImage loadImage(Resources resources,  String resourceName ) throws IOException { 
       InputStream is = resources.getInputStream(resourceName);
       
       if (is == null) {
         throw new RuntimeException("Texture: could not open inputstream for " + resourceName);
       }
       BufferedImage bufferedImage = null;
       bufferedImage = ImageIO.read(is); 
  
       // Flip Image (normal images are upside down, because they are specified in "screen space") 
       AffineTransform tx = AffineTransform.getScaleInstance(1, -1); 
       tx.translate(0, -bufferedImage.getHeight(null)); 
       AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR); 
       bufferedImage = op.filter(bufferedImage, null); 
       return bufferedImage; 
   } 

   /**
    * auxiliary method for converting from all sorts of image formats to the format we want for OpenGL
    */
   private static ByteBuffer convertImageData( BufferedImage bufferedImage, boolean rescale) //throws TextureFormatException 
   { 
       ByteBuffer imageBuffer = null;  
       int texWidth, texHeight;
       try {
         WritableRaster raster;
         BufferedImage texImage;
         int bwidth = bufferedImage.getWidth();
         int bheight = bufferedImage.getHeight();
         if (rescale) {
            texWidth = 2;  while (texWidth < bwidth) texWidth *= 2;
            texHeight = 2; while (texHeight < bheight) texHeight *= 2;
         } else {     
            texWidth = bwidth;
            texHeight = bheight;
         }
        
         if (bufferedImage.getColorModel().hasAlpha()) {
             raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, texWidth, texHeight, 4, null);  
             texImage = new BufferedImage(glAlphaColorModel, raster, false, properties);
         } else {
             raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, texWidth, texHeight, 3, null);  
             texImage = new BufferedImage(glColorModel, raster, false, properties);
         }
         texImage.getGraphics().drawImage(bufferedImage, 0, 0, null);
         byte[] data = ((DataBufferByte)texImage.getRaster().getDataBuffer()).getData();      
         imageBuffer = ByteBuffer.allocateDirect(data.length);
         imageBuffer.order(ByteOrder.nativeOrder());
         imageBuffer.put(data, 0, data.length);
         
       } catch (Exception e) {
           throw new RuntimeException("Unable to convert texture data " + e); 
       }
       imageBuffer.rewind();
       return imageBuffer;  
   }


   
   /**
    * requests a single OpenGL texture "name" (i.e. some int, used by OpenGL to identify a Texture)
    */
   private static int createTextureID(GLBinding gl) { 
      int[] ids = new int[1]; 
      gl.glGenTextures(1, ids); 
      return ids[0]; 
   } 

   

   /**
    * loads a texture from file, and creates a mipmapped texture object, identified
    * by the texture ID that is returned. Magnification filtering is linear,
    * minification filtering is linear_mipmap_linear. wrap mode is repeat.
    * The internal format is GL_RGB, and rescaling (to power-of-two width and height)
    * will be done, if needed.
    */
   public static int loadGLTexture(GLBinding gl, String resourceName)  {
       return loadGLTexture(gl, resourceName, GLC.GL_LINEAR_MIPMAP_LINEAR, GLC.GL_RGB, true);   
   }

   public static int loadGLTexture(GLBinding gl, String resourceName, int filterType, int internalFormat, boolean rescale) { 
      return loadGLTexture(gl, sharedResources, resourceName, filterType, internalFormat, rescale);  
   }

   public static int loadGLTexture(GLBinding gl, String resourceDir, String resourceName, int filterType, int internalFormat, boolean rescale)   { 
      return loadGLTexture(gl, new Resources(resourceDir), resourceName, filterType, internalFormat, rescale);  
   }

   /**
    * loads a texture from file, and creates a mipmapped texture object, identified
    * by the texture ID that is returned. Magnification filtering is linear,
    * minification filtering is specified, and must be one of the OpenGL modes,
    * like GLC.GL_NEAREST, GLC.GL_LINEAR, GLC.GL_NEAREST_MIPMAP_NEAREST, 
    * GLC.GL_LINEAR_MIPMAP_NEAREST, GLC.GL_NEAREST_MIPMAP_LINEAR, or GLC.GL_LINEAR_MIPMAP_LINEAR.
    * wrap mode is repeat. The rescale parameter specifies whether the texture must be rescaled
    * in case it has width and height that are not powers of two. 
    */
   public static int loadGLTexture(GLBinding gl, Resources resources, String resourceName, int filterType, int internalFormat, boolean rescale)  { 
      BufferedImage  bufferedImage = null;
      try {
         bufferedImage = loadImage(resources, resourceName); 
      } catch (IOException e) {
         logger.warning("Texture: could not load " + resourceName);
         return 0;  
      } catch (RuntimeException re) {
          logger.warning("Texture: could not load " + resourceName);
          return 0; 
      }
      
      ByteBuffer textureBuffer = convertImageData( bufferedImage, rescale ); // ensures GL_RGB format
      int sourceFormat = bufferedImage.getColorModel().hasAlpha() ? GLC.GL_RGBA : GLC.GL_RGB;      

      int textureId = createTextureID(gl); 
      
      gl.glBindTexture(GLC.GL_TEXTURE_2D, textureId ); 
//      gl.glPixelStorei(GLC.GL_UNPACK_ALIGNMENT, 4);
//      gl.glPixelStorei(GLC.GL_UNPACK_LSB_FIRST, GLC.GL_TRUE);
      gl.glTexParameteri(GLC.GL_TEXTURE_2D, GLC.GL_TEXTURE_WRAP_S, GLC.GL_REPEAT); 
      gl.glTexParameteri(GLC.GL_TEXTURE_2D, GLC.GL_TEXTURE_WRAP_T, GLC.GL_REPEAT); 
      gl.glTexParameteri(GLC.GL_TEXTURE_2D, GLC.GL_TEXTURE_MAG_FILTER, GLC.GL_LINEAR); // Magnification always GL_LINEAR
      gl.glTexParameteri(GLC.GL_TEXTURE_2D,GLC.GL_TEXTURE_MIN_FILTER,filterType);
//      gl.glTexParameteri(GLC.GL_TEXTURE_2D,GLC.GL_TEXTURE_MIN_FILTER,GLC.GL_LINEAR);
//      gl.glTexParameteri(GLC.GL_TEXTURE_2D, GLC.GL_TEXTURE_WRAP_S, GLC.GL_CLAMP_TO_EDGE);
//      gl.glTexParameteri(GLC.GL_TEXTURE_2D, GLC.GL_TEXTURE_WRAP_T, GLC.GL_CLAMP_TO_EDGE);
      if (filterType == GLC.GL_NEAREST || filterType == GLC.GL_LINEAR) {
         //hmi.util.Console.println("NON-MIPMAP");
//         gl.glTexImage2D(GLC.GL_TEXTURE_2D, ZEROLEVEL, internalFormat, 
//                         bufferedImage.getWidth(), bufferedImage.getHeight(), 
//                         NOBORDER, sourceFormat, GLC.GL_UNSIGNED_BYTE, textureBuffer );     
                         
                  gl.glTexImage2D(GLC.GL_TEXTURE_2D, ZEROLEVEL, GLC.GL_RGBA, 
                         bufferedImage.getWidth(), bufferedImage.getHeight(), 
                         NOBORDER, GLC.GL_RGB, GLC.GL_UNSIGNED_BYTE, textureBuffer );                                       
      } else  {
         //hmi.util.Console.println("MIPMAP");
         gl.glTexParameteri(GLC.GL_TEXTURE_2D, GLC.GL_GENERATE_MIPMAP, GLC.GL_TRUE);
         
         gl.glTexImage2D(GLC.GL_TEXTURE_2D, 0, GLC.GL_RGBA, 
                         bufferedImage.getWidth(), bufferedImage.getHeight(), 
                         0, GLC.GL_RGB, GLC.GL_UNSIGNED_BYTE, textureBuffer);        
         
//         gl.glTexParameteri(GLC.GL_TEXTURE_2D,GLC.GL_TEXTURE_MIN_FILTER,filterType);
//         int wd = bufferedImage.getWidth();
//         int ht = bufferedImage.getHeight();
//         glu.gluBuild2DMipmaps(GLC.GL_TEXTURE_2D, internalFormat,bufferedImage.getWidth(), bufferedImage.getHeight(), sourceFormat, GLC.GL_UNSIGNED_BYTE, textureBuffer ); 
      }     
      return textureId;
   }

   static final int ZEROLEVEL = 0;
   static final int NOBORDER = 0;
   private static ComponentColorModel glAlphaColorModel = 
   new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB), new int[]{8, 8, 8, 8}, true, false,
       ComponentColorModel.TRANSLUCENT, DataBuffer.TYPE_BYTE);
       
   private static ComponentColorModel glColorModel = 
   new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB), new int[]{8, 8, 8, 0}, false, false,
       ComponentColorModel.OPAQUE, DataBuffer.TYPE_BYTE);
              
   private static Hashtable properties = new Hashtable();
      
} 
