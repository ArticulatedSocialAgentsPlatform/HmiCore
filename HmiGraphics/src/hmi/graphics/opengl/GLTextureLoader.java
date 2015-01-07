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
/* 
 */

package hmi.graphics.opengl;

import hmi.util.ResourcePool;
import hmi.util.Resources;

import java.awt.Graphics2D;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.imageio.ImageIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.media.opengl.*; 

/**
 * A class for loading image data for GLTexture objects.
 */
public class GLTextureLoader
{

    private static Logger logger = LoggerFactory.getLogger(GLTextureLoader.class.getName());


    /************************************************************/
    /* Static methods for loading/caching GLTexture objects */
    /************************************************************/

    /**
     * Adds the specified directory as one of the resource directories for GLTextures
     */
    public static void addTextureDirectory(String dir)
    {
        pool.addResourceDirectory(dir);
    }

    /**
     * Tries to get a GLTexture with the specified resource name, either from the cache or else from loading (and caching) an image from one of the
     * texture directories. The image is flipped vertically and when necessary rescaled to a power-of-two size.
     */
    public static GLTexture getGLTexture(String resourceName)
    {
        return getGLTexture(0, resourceName, true, true);
    }

    /**
     * Tries to get a GLTexture with the specified resource name, either from the cache or else from loading (and caching) an image from one of the
     * texture directories. The image is flipped vertically and when necessary rescaled to a power-of-two size.
     */
    public static GLTexture getGLTexture(int texUnit, String resourceName)
    {
        return getGLTexture(texUnit, resourceName, true, true);
    }

    /**
     * Tries to get a GLTexture with the specified resource name. GLTexture objects found and loaded will be cached. The flip and rescale attributes
     * are used when the texture is loaded for the first time (not when a cached version is retrieved). They denote whether the image should be
     * flipped vertically (usually they should for OpenGL texturing), and whether a non-power-of-two image should be rescaled to the next power-of-two
     * size. (Default is to rescale)
     */
    public static GLTexture getGLTexture(int texUnit, String resourceName, boolean flip, boolean rescale)
    {
        try
        {
            GLTexture glt = (GLTexture) pool.getResource(resourceName, new Object[] { flip, rescale });
            if (glt != null)
                glt.setTextureUnit(texUnit);
            return glt;
        }
        catch (IOException e)
        {
            logger.error("GLTextures: " + e);
            return null;
        }
    }

    private static ResourcePool pool = new ResourcePool();

    public static final String DEFAULT_TEXTURE_RESOURCE_DIR = "textures";
    static
    {
        ResourcePool.ResourceLoader loader = new ResourcePool.ResourceLoader()
        {
            /* The actual method for loading of texture resources */
            public Object loadResource(Resources res, String resourceName, Object[] par) throws IOException
            {
                boolean flip = (Boolean) par[0];
                boolean rescale = (Boolean) par[1];
                return readFromResourceFile(res, resourceName, rescale, flip);
            }
        };
        pool.setResourceLoader(loader);
        pool.addResourceDirectory(DEFAULT_TEXTURE_RESOURCE_DIR); // add default Pool for DEFAULT_TEXTURE_RESOURCE_DIR directory
    }

    private static String currentFileName = null; // store file name for error messages

    /*********************************************************************************/
    /* Static methods for loading texture images and creation of GLTexture objects */
    /*********************************************************************************/

    /**
     * Like readFromStream, where the InputStream is obtained from the specified resource file name (a OS path).
     */
    public static GLTexture readFromFile(String fileName, boolean rescale, boolean flip) throws IOException
    {
        currentFileName = fileName;
        InputStream is = new BufferedInputStream(new FileInputStream(fileName));
        return readFromStream(is, rescale, flip, fileName);
    }

    /**
     * Like readFromStream, where the InputStream is obtained from the specified file.
     */
    public static GLTexture readFromFile(File file, boolean rescale, boolean flip) throws IOException
    {
        currentFileName = file.getName();
        InputStream is = new BufferedInputStream(new FileInputStream(file));
        return readFromStream(is, rescale, flip, currentFileName);
    }

    /**
     * Like readFromStream, where the InputStream is obtained from the specified resource file.
     */
    public static GLTexture readFromResourceFile(Resources resources, String resourceName, boolean rescale, boolean flip) throws IOException
    {
        currentFileName = resourceName;
        InputStream is = resources.getInputStream(resourceName);
        return readFromStream(is, rescale, flip, resourceName);
    }

    /**
     * Creates a new GLTexture by reading image data from the specified InputStream. The image is rescaled to a power-of-two (when necessary) when the
     * rescale parameter is true. Also, the image is flipped vertically when flip is true. Returns null when the InputStream is null;
     */
    public static GLTexture readFromStream(InputStream is, boolean rescale, boolean flip, String imageFile) throws IOException
    {
        if (is == null)
            return null;
        BufferedImage image = ImageIO.read(is);
        GLTexture texture = convertBufferedImage(image, rescale, flip, imageFile);
        // texData = TextureIO.newTextureData(is, false, null); JOGL specific
        is.close();
        image.flush();
        currentFileName = null;
        return texture;
    }

    /**
     * Flips the supplied BufferedImage vertically.
     */
    public static void flipImageVertically(BufferedImage image)
    {
        WritableRaster raster = image.getRaster();
        Object scanline1 = null;
        Object scanline2 = null;
        for (int i = 0; i < image.getHeight() / 2; i++)
        {
            scanline1 = raster.getDataElements(0, i, image.getWidth(), 1, scanline1);
            scanline2 = raster.getDataElements(0, image.getHeight() - i - 1, image.getWidth(), 1, scanline2);
            raster.setDataElements(0, i, image.getWidth(), 1, scanline2);
            raster.setDataElements(0, image.getHeight() - i - 1, image.getWidth(), 1, scanline1);
        }
    }

    /**
     * Converts a BufferedImage into a TextureImageData object with type/format and size that suits OpenGL
     */
    private static GLTexture convertBufferedImage(BufferedImage image, boolean rescale, boolean flip, String imageFile)
    {

        byte[] data = null;
        if (flip)
            flipImageVertically(image);
        int bwidth = image.getWidth();
        int bheight = image.getHeight();
        boolean hasAlpha = image.getColorModel().hasAlpha();
        int type = 0;
        int format = 0;

        int texWidth, texHeight;
        boolean conversionNeeded = false;
        if (rescale)
        {
            texWidth = getPowerOfTwo(bwidth);
            texHeight = getPowerOfTwo(bheight);
            String fn = currentFileName == null ? "<?>" : currentFileName;
            conversionNeeded = (texWidth != bwidth || texHeight != bheight);
            if (conversionNeeded)
                logger.warn("Texture " + fn + " original size: " + bwidth + "X" + bheight + "  resized: " + texWidth + "X" + texHeight);
        }
        else
        {
            texWidth = bwidth;
            texHeight = bheight;
            conversionNeeded = false;
        }

        /* check for (a few) simple types where we need no conversion */
        switch (image.getType())
        {
        case BufferedImage.TYPE_3BYTE_BGR:
            logger.debug("TYPE_3BYTE_BGR-GLC.GL_BGR");
            format = GL2.GL_BGR;
            type = GL.GL_UNSIGNED_BYTE;
            break;
        case BufferedImage.TYPE_CUSTOM:
            logger.debug("TYPE_CUSTOM");
            ColorModel cm = image.getColorModel();
            if (cm.equals(rgbModel))
            {
                logger.debug("rgbColorModel");
                format = GL.GL_RGB;
                type = GL.GL_UNSIGNED_BYTE;
            }
            else if (cm.equals(rgbaModel))
            {
                logger.debug("rgbaColorModel");
                format = GL.GL_RGBA;
                type = GL.GL_UNSIGNED_BYTE;
            }
            else
            {
                logger.info("GLTextureLoader: custom color model");
                conversionNeeded = true;
            }
            break;
        default:
            conversionNeeded = true;
        }
        if (conversionNeeded)
        {
            /*
             * all cases where we either do not recognize the image type, or where the size is non-power-of-two, we convert by "drawing" the texture
             * in a new BufferedImage, with well known type and format, and appropriate width and height
             */
            // hmi.util.Console.println("texture conversion...");
            try
            {
                BufferedImage texImage = null;

                if (image.getColorModel().hasAlpha())
                {
                    WritableRaster raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, texWidth, texHeight, 4, null);
                    texImage = new BufferedImage(rgbaModel, raster, false, null);
                    format = GL.GL_RGBA;
                    type = GL.GL_UNSIGNED_BYTE;
                }
                else
                {
                    WritableRaster raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, texWidth, texHeight, 3, null);
                    texImage = new BufferedImage(rgbModel, raster, false, null);
                    format = GL.GL_RGB;
                    type = GL.GL_UNSIGNED_BYTE;
                }
                ((Graphics2D) texImage.getGraphics()).drawImage(
                        image,
                        new AffineTransformOp(AffineTransform.getScaleInstance((double) texWidth / (double) bwidth, (double) texHeight
                                / (double) bheight), AffineTransformOp.TYPE_BILINEAR), 0, 0);
                data = ((DataBufferByte) texImage.getRaster().getDataBuffer()).getData();
                texImage.flush();
            }
            catch (Exception e)
            {
                throw new RuntimeException("Unable to convert texture data " + e);
            }
        }
        else
        {
            data = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        }
        ByteBuffer imageBuffer = ByteBuffer.allocateDirect(data.length);
        imageBuffer.order(ByteOrder.nativeOrder());
        imageBuffer.put(data, 0, data.length);
        imageBuffer.rewind();
        GLTexture texture = new GLTexture(texWidth, texHeight, format, type, hasAlpha, imageBuffer, imageFile);
        return texture;
    }

    /*
     * Returns the smallest power of two larger than or equal to size
     */
    private static int getPowerOfTwo(int size)
    {
        int powerOfTwo = 2;
        while (powerOfTwo < size)
            powerOfTwo *= 2;
        return powerOfTwo;
    }

    static ComponentColorModel rgbaModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB), new int[] { 8, 8, 8, 8 }, true, false,
            ComponentColorModel.TRANSLUCENT, DataBuffer.TYPE_BYTE);

    static ComponentColorModel rgbModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB), new int[] { 8, 8, 8, 0 }, false, false,
            ComponentColorModel.OPAQUE, DataBuffer.TYPE_BYTE);

}
