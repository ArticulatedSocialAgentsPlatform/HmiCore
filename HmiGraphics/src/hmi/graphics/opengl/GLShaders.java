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

import hmi.util.Resources;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A utility class for GLShaders, in particular for loading and caching GLShader objects.
 * Basic usage: 1) GLShaders.getGLShader(shaderFile) will retrieve (and cache) a shader
 * within the default shader resource directory. (Default for HmiShared projects: the resource/shaders directory)
 * 2) GLShaders.addShaderDirectory(shaderDir) adds the shaderDir directory for searching shader files.
 * All shader directories are loaded as Java resources, implying that the path where some shader directory is located
 * must be included on the Java classpath. Such is the case for the resource directory within a HmiShared style project.
 */
public class GLShaders
{

    private static Logger logger = LoggerFactory.getLogger(GLShaders.class.getName());

    /**
     * Adds the specified directory to the (front of the) list of resource directories to be searched
     * When some resource is actually present in more than one directory in the list, the version from
     * the directory that was added latest will take preference.
     */
    public static void addShaderDirectory(String shaderDir)
    {
        Pool pool = findPool(shaderDir);
        if (pool != null) return;
        pool = new Pool(shaderDir);
        poolList.add(0, pool);
    }

    /**
     * Tries to get a GLShader from the specified shader directory, with the specified shader name
     * GLShader objects found and loaded will be cached in the shader pool associated with the
     * specified resource directory.
     */
    public static GLShader getGLShader(String shaderDir, String shaderName)
    {
        Pool pool = findPool(shaderDir);
        if (pool == null)
        {
            pool = new Pool(shaderDir);
            poolList.add(0, pool);
        }
        try
        {
            return pool.getGLShader(shaderName);
        }
        catch (IOException e)
        {
            logger.error("GLShaders: " + e);
            return null;
        }
    }

    /**
     * Tries to get a GLShader with the specified name from any of the current list of
     * shader directories.
     * GLTexture objects found and loaded will be cached in the texture pool associated with the
     * (first) resource directory where it was found.
     */
    public static GLShader getGLShader(String shaderName)
    {
        GLShader shader = null;
        for (Pool pool : poolList)
        {
            shader = pool.getCachedGLShader(shaderName);
            if (shader != null) return shader;
        }
        // not found at all: return null
        return null;
    }

    /*
     * Return a Pool for the specified resource directory,
     * or null, when there is no such pool
     */
    private static Pool findPool(String resourceDir)
    {
        for (Pool pool : poolList)
        {
            if (pool.getResourceDir().equals(resourceDir))
            {
                return pool;
            }
        }
        return null;
    }

    private static List<Pool> poolList = new ArrayList<Pool>(); // The list of GLTexture Pools

    public static final String DEFAULT_SHADER_RESOURCE_DIR = "shaders";
    static
    {
        addShaderDirectory(DEFAULT_SHADER_RESOURCE_DIR); // add default Pool for DEFAULT_SHADER_RESOURCE_DIR directory
    }

    /*
     * A class for "pools" of GLShaders
     */
    private static final class Pool
    {

        /* private constructor , to disable creation of Pool objects outside GLShaders */
        private Pool()
        {
        }

        /*
         * Creates a new GLTexture Pool for the specified resource directory.
         */
        private Pool(String resourceDir)
        {
            this.resourceDir = resourceDir;
            this.resources = new Resources(resourceDir);
        }

        /* Returns the resource directory for this Pool */
        public String getResourceDir()
        {
            return resourceDir;
        }

        /*
         * returns the GLTexture for the specified resource from the Resources associated with this
         * Pool instance. Either an already cached GLTexture, or a newly loaded version
         * will be returned, unless no such resource could be found, in which case a null value is returned.
         */
        public GLShader getGLShader(String shaderName) throws IOException
        {
            GLShader shader = shaderCache.get(shaderName);
            if (shader != null)
            {
                return shader;
            }
            else
            {
                return loadGLShader(shaderName);
            }
        }

        /*
         * returns the GLShader for the specified resource from the Resources associated with this
         * Pool instance, which could be null.
         */
        private GLShader getCachedGLShader(String shaderName)
        {
            return shaderCache.get(shaderName);
        }

        /*
         * Tries to load the GLShader for the specified resource from the Resources associated with this
         * Pool instance. If found, the new GLShader is stored in the cache, and returned
         * as result. If not found, a null value is returned.
         */
        private GLShader loadGLShader(String shaderName) throws IOException
        {
            GLShader shader = null;
            if (shader != null) shaderCache.put(shaderName, shader);
            return shader;
        }

        private String resourceDir;
        private Resources resources;

        private Map<String, GLShader> shaderCache = new HashMap<String, GLShader>();

    }

}
