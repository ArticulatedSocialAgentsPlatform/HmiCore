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

import java.io.BufferedReader;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A few static utility methods for loading and compiling GLSL shaders
 */
public class GLShaderProgramLoader
{


    private static Logger logger = LoggerFactory.getLogger(GLShaderProgramLoader.class.getName());


    /************************************************************/
    /* Static methods for loading/caching GLShader objects */
    /************************************************************/



    /**
     * Adds the specified directory as one of the resource directories for GLShaders
     */
    public static void addShaderDirectory(String dir)
    {
        pool.addResourceDirectory(dir);
    }

    /**
     * Tries to get a GLShader with the specified resource name. GLShader objects found and loaded will be cached. 
     */
    public static GLShaderProgram getGLShaderProgram(String resourceName)
    {
        try
        {
            return (GLShaderProgram) pool.getResource(resourceName);
        }
        catch (IOException e)
        {
            logger.error("GLShaderProgram: " + e);
            return null;
        }
    }

    private static ResourcePool pool = new ResourcePool();

    public static final String DEFAULT_SHADER_RESOURCE_DIR = "shaders";
    static
    {
        ResourcePool.ResourceLoader loader = new ResourcePool.ResourceLoader()
        {
            /* The actual method for loading of texture resources */
            public Object loadResource(Resources res, String resourceName, Object[] par) throws IOException
            {
                return readFromResourceFile(res, resourceName);
            }
        };
        pool.setResourceLoader(loader);
        pool.addResourceDirectory(DEFAULT_SHADER_RESOURCE_DIR); // add default Pool for DEFAULT_TEXTURE_RESOURCE_DIR directory
    }

    /**
     * Clears the pool of shader resource directories.
     * Usefull to get rid of the initial default shader directory
     */
    public static void clearShaderPool() {
       pool.clear();
    }
    
    /***********************************************************************************/
    /* Static methods for loading shader files and compilation into GLShader objects */
    /***********************************************************************************/

    /**
     * Loads a shader text from file, and returns the shader program text. The shader is searched for inside a resources directory.
     */
    private static String readShaderFromResourceFile(Resources resources, String shaderName)
    {
        BufferedReader br = resources.getReader(shaderName);
        if (br == null)
        {
            logger.info("GLShaderProgramLoader: Could not find shader file: " + shaderName + " in resources: " + resources);
            return null;
        }
        StringBuilder buf = new StringBuilder();
        try
        {
            String line = br.readLine();
            while (line != null)
            {
                buf.append(line);
                buf.append('\n');
                line = br.readLine();
            }
            return buf.toString();
        }
        catch (IOException e)
        {
            logger.error("GLShaderLoader.loadShaderText: " + e);
            return null;
        }
    }

    /**
     * Loads a shader program, consisting of a vertex shader and a fragment shader, from two files, located within a resources directory. The vertex
     * shader is supposed to be in a file with name shaderProgramName.vs, the fragment shader in shaderProgramName.fs The GLSL program handle is
     * returned, or -1 when some error occurs.
     */
    private static GLShaderProgram readFromResourceFile(Resources resources, String shaderProgramName)
    {
        String vstext = readShaderFromResourceFile(resources, shaderProgramName + ".vs");
        String fstext = readShaderFromResourceFile(resources, shaderProgramName + ".fs");
        if (vstext == null || fstext == null)
            return null;
        GLShaderProgram prog = new GLShaderProgram(shaderProgramName, vstext, fstext);
        return prog;
    }

}
