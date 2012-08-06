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

import hmi.graphics.util.BufferUtil;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.media.opengl.*;


/**
 * A GLSL shader program wrapper, in the form of a GLRenderObject. A GLShaderProgram encapsulates a GLSL shader program, but it does not handle
 * particular settings for its uniform variables. The latter aspect is delegated to the GLShader class. GLShaderProgram does contain a few utility
 * methods for obtaining uniform locations, and for extracting uniform declarations from shader code. Basically, GLShaderProgram deals with the shader
 * *texts*, up to and including compilation and linking, and unform declarations within these shader texts.
 */
public class GLShaderProgram// implements GLRenderObject
{

    private String vstext, fstext; // program text for vertex shader and fragment shader
    private int shaderId = 0; // non-positive value denotes: not initialized
    private String shaderName;
    private static Logger logger = LoggerFactory.getLogger(GLShaderProgram.class.getName());


    /**
     * Default constructor
     */
    public GLShaderProgram()
    {
    }

    /**
     * Creates a new GLShaderProgram, with specified vertex shader and fragment shader texts, and specified uniform names.
     */
    public GLShaderProgram(String shaderName, String vstext, String fstext)
    {
        this.vstext = vstext;
        this.fstext = fstext;
        this.shaderName = shaderName;
    }

    /**
     * Returns the OpenGL id for the shader program.
     */
    public int getShaderId()
    {
        return shaderId;
    }

    /**
     * Returns the array with OpenGL uniform locations
     */
    public int[] getUniformLocations(GLRenderContext glc, String... uniformNames)
    {
        if (uniformNames == null)
            return new int[0];
        int[] uLocation = new int[uniformNames.length];
        // System.out.println("getUniformLocations shaderName=" + shaderName + "  shaderId=" + shaderId + " uniformNames.length=" +
        // uniformNames.length );
        for (int i = 0; i < uLocation.length; i++)
        {
            // System.out.println("glGetUniformLocation(" + shaderId + ", " + uniformNames[i] + ")");
            uLocation[i] = glc.gl.glGetUniformLocation(shaderId, uniformNames[i]);
        }
        return uLocation;
    }

    /**
     * OpenGL initialization: an OpenGL id is created for this shader program, The shader is compiled and linked.
     */
    public void glInit(GLRenderContext glc)
    {
        if (shaderId <= 0)
        {
            shaderId = createShaderProgram(glc.gl, vstext, fstext);
        }
    }

    /**
     * Activates thsi shader program as the current shader.
     */
    public final void glRender(GLRenderContext glc)
    {
        glc.gl.glUseProgram(shaderId);
    }

    /***********************************************************************************/
    /* Static methods for compiling and linking shader programs */
    /***********************************************************************************/

    /*
     * Compiles vertex/frament program texts, allocates a shader program, attaches the compiles vertex and fragment shaders of the program, and links
     * the program. The program handle is returned. If an error occurs, -1 is returned.
     */
    private int createShaderProgram(GL2ES2 gl, String vertexShaderText, String fragmentShaderText)
    {
        int program = gl.glCreateProgram();
        if (vertexShaderText != null)
        {
            int vertexShader = compileShader(gl, vertexShaderText, GL2ES2.GL_VERTEX_SHADER);
            if (vertexShader < 0)
                return -1;
            gl.glAttachShader(program, vertexShader);
        }
        if (fragmentShaderText != null)
        {
            int fragmentShader = compileShader(gl, fragmentShaderText, GL2ES2.GL_FRAGMENT_SHADER);
            if (fragmentShader < 0)
                return -1;
            gl.glAttachShader(program, fragmentShader);
        }
        gl.glLinkProgram(program);
        IntBuffer linkStatus = BufferUtil.directIntBuffer(1);
        gl.glGetProgramiv(program, GL2ES2.GL_LINK_STATUS, linkStatus);
        if (linkStatus.get(0) != GL.GL_TRUE)
        {
            logger.error("Shader \"" + shaderName + "\" Linking problem: ");
            IntBuffer infologlength = BufferUtil.directIntBuffer(1);
            gl.glGetShaderiv(program, GL2ES2.GL_INFO_LOG_LENGTH, infologlength);
            int loglen = infologlength.get(0);
            ByteBuffer infolog = BufferUtil.directByteBuffer(loglen);
            IntBuffer lenBuf = BufferUtil.directIntBuffer(1);
            gl.glGetProgramInfoLog(program, loglen, lenBuf, infolog);
            String logstr = BufferUtil.toString(infolog);
            logger.error(logstr);
            return -1;
        }
        return program;
    }

    /*
     * Compiles a shader program text, and returns the shader handle. If an error occurs, -1 is returned.
     */
    private int compileShader(GL2ES2 gl, String shaderText, int shaderType)
    {
        int shader = gl.glCreateShader(shaderType);
        gl.glShaderSource(shader, 1, new String[] { shaderText }, (int[]) null, 0);
        gl.glCompileShader(shader);
        IntBuffer compileStatus = BufferUtil.directIntBuffer(1);
        gl.glGetShaderiv(shader, GL2ES2.GL_COMPILE_STATUS, compileStatus);
        if (compileStatus.get(0) != GL.GL_TRUE)
        {
            logger.error("GLSL Shader \"" + shaderName + "\" Compilation problem, status:  " + compileStatus.get(0));
            IntBuffer infologlength = BufferUtil.directIntBuffer(1);
            gl.glGetShaderiv(shader, GL2ES2.GL_INFO_LOG_LENGTH, infologlength);
            int loglen = infologlength.get(0);
            ByteBuffer infolog = BufferUtil.directByteBuffer(loglen);
            gl.glGetShaderInfoLog(shader, loglen, null, infolog);
            String logstr = BufferUtil.toString(infolog);
            logger.error("GLSL Shader Compilation Problem: " + logstr);
            return -1;
        }
        return shader;
    }

    /*
     * Returns the vertex shader uniform followed by the fragment shader uniform within the two Lists. The number of names added is returned.
     */
    public int getShaderUniforms(List<String> types, List<String> names)
    {
        List<String> uniformLines = getUniformLines(vstext + fstext);
        return getUniforms(uniformLines, types, names);
    }

    static Pattern uniformLinePattern = Pattern.compile("^\\s*uniform\\s+(\\w.*);", Pattern.MULTILINE); // matches: uniform (declarations);
    static Pattern declPattern = Pattern.compile("(\\w+)\\s*(.*)"); // matches (type) (vars)
    static Pattern varPattern = Pattern.compile("(\\w+)([\\[\\]\\d]*)");
    
    /**
     * Detects lines with uniform declarations in shader text Returns a List of Strings, where each String contains the text after the uniform
     * specified, up to (not including) the ; at the end Eaxh of these has the form: type var-0, var-1, ... var-n where each var has optionally an
     * array postfix of the form [nn]
     */
    public static List<String> getUniformLines(String shader)
    {
        ArrayList<String> result = new ArrayList<String>();
        Matcher m = uniformLinePattern.matcher(shader);
        boolean uniformRecognized = m.find();
        while (uniformRecognized)
        {
            String decls = m.group(1);
            result.add(decls);
            uniformRecognized = m.find();
        }
        return result;
    }

       /**
     * Parses a list of declarations decls. Each String is assumed to be like type var-0, var-1, ... var-n Returns the results in two Lists (types and
     * name) The type String is the type used within the shader code, except that an array type is added to the base type, For instance, a declation
     * like uniform vec3 lightPos[1]; within the shader text ends up in the type List as "vec3[1]"and in the names List as "lightPos". The number of
     * elements added to each of the two Lists is equal to the number of variable names encountered in tghe declarations. This number is returned as
     * result
     */
    public static int getUniforms(List<String> decls, List<String> types, List<String> names)
    {
        int n_added = 0;
        for (String decl : decls)
        {
            Matcher mdecl = declPattern.matcher(decl);
            if (!mdecl.matches())
            {
                logger.error("GLShaderProgram -- unrecognized uniform declaration: " + decl);
                continue;
            }
            String type = mdecl.group(1);
            String vars = mdecl.group(2);

            Matcher mvar = varPattern.matcher(vars);
            boolean varRecognized = mvar.find();
            while (varRecognized)
            {
                String var = mvar.group(1);
                String arrayDec = mvar.group(2);
                types.add(type + arrayDec);
                names.add(var);
                n_added++;
                varRecognized = mvar.find();
            }
        }
        return n_added;
    }


 
}
