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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A wrapper for OpenGL shaders, in the form of an GLRenderObject. In fact a wrapper around the GLShaderProgram, with added functionality for defining
 * and setting uniforms. A GLShader can share the same GLShaderProgram with some other GLShader, yet it can set its own individual uniform values.
 */
public class GLShader implements GLRenderObject
{

    private static Logger logger = LoggerFactory.getLogger(GLShader.class.getName());

    private GLShaderProgram prog;
    private String shaderProgName;
    private int progId = -1;

    private int nrOfUniforms;
    private String[] uName;  // Uniform names.
    private int[] uType;     // Uniform (full) type, like, for instance, U1I, or U4F
    private int[] uBaseType; // Uniform base type: I, IV, F, FV
    private int[] uSize;     // Uniform size: 1, 2, 3, 4
    // private int[] uCount; // Uniform count, for IV and FV array types
    private int[] uLocation; // uniform locations in shader program.

    private int[] uOffset;
    private int[] intValue;
    private float[] floatValue;
    
    private ArrayList<String> declaredTypes;

    public static final int I = 0;
    public static final int F = 1;
    public static final int IV = 2;
    public static final int FV = 3;

    public static final int U1I = 1;
    public static final int U2I = 2;
    public static final int U3I = 3;
    public static final int U4I = 4;

    public static final int U1F = 5;
    public static final int U2F = 6;
    public static final int U3F = 7;
    public static final int U4F = 8;

    public static final int U1B = 9;
    public static final int U2B = 10;
    public static final int U3B = 11;
    public static final int U4B = 12;

    

    /**
     * Creates a new GLShader, with specified shader program name, and shader uniform names and parameter types. 
     * The uniform names and types must be specified (as Strings) one after another, alternating between name and type. 
     * Allowed parameter types: &quot;int&quot;, &quot;float&quot;, &quot;bool&quot;, &quot;vec2&quot;, &quot;vec3&quot;, 
     * &quot;sampler1D&quot;, &quot;sampler2D&quot; etc.
     * or, alternatively: 1i, 2i, 3i, 4i, 1f, 2f, 3f, 4f 
     * The &quot;uniformNamesAndTypes&quot; parameter is optional; when absent, the vertex shader and fragment shader texts are
     * used to extract uniform definitions. The order of uniforms (used for the setValues method) is the order of declaration within the shader texts,
     * where the unifoms from the vertex shader precede those of the fragment shader.
     */
    public GLShader(String shaderProgName, String... uniformNamesAndTypes)
    {
        this.shaderProgName = shaderProgName;
        //logger.debug("new GLShader ( " + shaderProg + "...)");
        prog = GLShaderProgramLoader.getGLShaderProgram(shaderProgName);
        if (prog == null)
        {
            logger.error("GLShader: Could not find/load shader program for shader " + shaderProgName);
            return;
        }
        List<String> declared = new ArrayList<String>(uniformNamesAndTypes.length);
        for (int i = 0; i < uniformNamesAndTypes.length; i++)
            declared.add(uniformNamesAndTypes[i]);
        
        // First split declared parameters in names and types:
        ArrayList<String> declaredNames = new ArrayList<String>();
        declaredTypes = new ArrayList<String>();
        getUniforms(declared, declaredTypes, declaredNames); // get the declared types and names into the two lists.
        
        // Next, extrcat uniform names and types from the actual shader texts:
        ArrayList<String> shaderNames = new ArrayList<String>();
        ArrayList<String> shaderTypes = new ArrayList<String>();
        prog.getShaderUniforms(shaderTypes, shaderNames);

        int nru = declaredNames.size();
        int nrup = shaderNames.size();

         //hmi.util.Console.println("-----Shader \"" + shaderProgName + "\"");
         //hmi.util.Console.println("declaredNames:");
         //for (String s : declaredNames) hmi.util.Console.print( s + "  ");
        // hmi.util.Console.println();
        // hmi.util.Console.println("declaredTypes:");
        // for (String s : declaredTypes) hmi.util.Console.print( s + "  ");
        // hmi.util.Console.println();
         //hmi.util.Console.println("shaderNames:");
        // for (String s : shaderNames) hmi.util.Console.print( s + "  ");
        // hmi.util.Console.println("\n=================");
        // hmi.util.Console.println("shaderTypes:");
        // for (String s : shaderTypes) hmi.util.Console.print( s + "  ");
        // hmi.util.Console.println();

        if (nru == 0)
        {
            declaredNames = shaderNames;
            declaredTypes = shaderTypes;
            nru = nrup;
        }
        else
        {
            // check consistency: each explicit uniform should occur inside the shader, with the correct type
            // but some shader uniforms might be omitted, and the order might be different.
            for (int i = 0; i < nru; i++)
            {
                String nm = declaredNames.get(i);
                boolean found = false;
                for (int j = 0; j < nrup; j++)
                {
                    if (shaderNames.get(j).equals(nm))
                    {
                        found = true;
                        if (!shaderTypes.get(j).equals(declaredTypes.get(i)))
                        {
                            logger.error("GLShader warning: uniform\"" + nm + "\" within shader \"" + shaderProgName
                                    + "\" has ambiguous types: " + declaredTypes.get(i) + " versus " + shaderTypes.get(j) + " (in shader)");
                        }
                    }
                }
                if (!found)
                {
                    logger.error("GLShader warning: uniform \"" + nm + "\" does not occur within shader \"" + shaderProgName + "\"");
                }
            }
        }
        // By now we have the uniform names and type within uniformNamesAndTypes, although this might be an empty array
        // types must be converted into types XY that occur within the glUniformXY method names for setting uniform values.

        String[] uniformNames = new String[nru];
        int[] uniformTypes = new int[nru];
        for (int i = 0; i < nru; i++)
        {
            uniformNames[i] = declaredNames.get(i);
            String stp = declaredTypes.get(i);
            int tp = 0;
            // if (stp.equals("1i")) tp=U1I;
            // else if (stp.equals("2i")) tp=U2I;
            // else if (stp.equals("3i")) tp=U3I;
            // else if (stp.equals("4i")) tp=U4I;
            // else if (stp.equals("1f")) tp=U1F;
            // else if (stp.equals("2f")) tp=U2F;
            // else if (stp.equals("3f")) tp=U3F;
            // else if (stp.equals("4f")) tp=U4F;
            if (stp.equals("int"))
                tp = U1I;
            else if (stp.equals("float"))
                tp = U1F;
            else if (stp.equals("bool"))
                tp = U1B;
            else if (stp.equals("uint"))
                tp = U1I;
            else if (stp.equals("ivec2"))
                tp = U2I;
            else if (stp.equals("ivec3"))
                tp = U3I;
            else if (stp.equals("ivec4"))
                tp = U4I;
            else if (stp.equals("vec2"))
                tp = U2F;
            else if (stp.equals("vec3"))
                tp = U3F;
            else if (stp.equals("vec4"))
                tp = U4F;
            else if (stp.equals("sampler1D"))
                tp = U1I;
            else if (stp.equals("sampler2D"))
                tp = U1I;
            else if (stp.equals("sampler3D"))
                tp = U1I;
            else if (stp.equals("samplerCube"))
                tp = U1I;

            uniformTypes[i] = tp;
        }
        init(uniformNames, uniformTypes);
    }

    /**
     * Initializes the GLShader object, where the shader program name, and shader uniforms and types are specified. The shader program is loaded (by
     * means of GLShaderProgramLoader).
     */
    private void init(String[] uniformNames, int[] uniformTypes)
    {

        nrOfUniforms = (uniformNames == null) ? 0 : uniformNames.length;
        if (nrOfUniforms == 0)
        {

        }
        uName = uniformNames;
        uType = uniformTypes;
        // String uniforms = appendUniforms(appendUniforms(null, vstext), fstext).toString();
        // hmi.util.Console.println("Uniforms: " + uniforms);

        uOffset = new int[nrOfUniforms];
        uSize = new int[nrOfUniforms];
        uBaseType = new int[nrOfUniforms];
        int ioc = 0;
        int foc = 0;
        for (int i = 0; i < nrOfUniforms; i++)
        {
            int uniformType = uType[i];
            switch (uniformType)
            {
            case U1I:
                uOffset[i] = ioc;
                ioc += 1;
                uBaseType[i] = I;
                uSize[i] = 1;
                break;
            case U2I:
                uOffset[i] = ioc;
                ioc += 2;
                uBaseType[i] = I;
                uSize[i] = 2;
                break;
            case U3I:
                uOffset[i] = ioc;
                ioc += 3;
                uBaseType[i] = I;
                uSize[i] = 3;
                break;
            case U4I:
                uOffset[i] = ioc;
                ioc += 4;
                uBaseType[i] = I;
                uSize[i] = 4;
                break;

            case U1F:
                uOffset[i] = foc;
                foc += 1;
                uBaseType[i] = F;
                uSize[i] = 1;
                break;
            case U2F:
                uOffset[i] = foc;
                foc += 2;
                uBaseType[i] = F;
                uSize[i] = 2;
                break;
            case U3F:
                uOffset[i] = foc;
                foc += 3;
                uBaseType[i] = F;
                uSize[i] = 3;
                break;
            case U4F:
                uOffset[i] = foc;
                foc += 4;
                uBaseType[i] = F;
                uSize[i] = 4;
                break;
            }
        }
        intValue = new int[ioc];
        floatValue = new float[foc];
    }


   /**
    * returns the shader program name
    */
   public String getProgramName() {
      return shaderProgName;
   }

  /* flag thaty determined the amount of detail for appendAttributesTo() and toString() */   
   private static boolean showDetail = true;
  
   /**
    * Sets the showDetail mode for toString()
    */
   public static  void setShowDetail(boolean show) {
      showDetail = show;
   }
  

   /* denotes whether toString should show detail or mot */
   public boolean showDetail() { return showDetail;  }


    public StringBuilder appendTo(StringBuilder buf, int tab) {
       //  buf.append("shader ");
        buf.append(shaderProgName);
        if (showDetail()) {
           buf.append("(");
           for (int i = 0; i < nrOfUniforms; i++)
           {
               buf.append(declaredTypes.get(i));
               buf.append(' ');
               buf.append(uName[i]);
               buf.append(", ");
           }     
        }
        buf.append(")");
        return buf;
    }

    public String toString()
    {
        StringBuilder buf = appendTo(new StringBuilder(), 0);
        return buf.toString();
    }

    /**
     * Sets the value(s) for a single uniform, referenced by name,
     * with a float[] defining the values. 
     */
    public void setValue(String uniformName, float[] vec)
    {
        for (int i = 0; i < nrOfUniforms; i++)
        {
            if (uName[i].equals(uniformName))
            {
                int offset = uOffset[i];
                int size = uSize[i];
                switch (uBaseType[i])
                {
                case I:
                    logger.error("GLShader.setValue: illegal format, using floats for int parameter");
                    break;
                case F:
                    for (int j = 0; j < size; j++)
                        floatValue[offset + j] =  vec[j];
                    break;
                }
                return;
            }
        }
        // not found;
    }


    /**
     * Sets the value(s) for a single uniform, referenced by name
     */
    public void setValue(String uniformName, Object... vals)
    {
        for (int i = 0; i < nrOfUniforms; i++)
        {
            if (uName[i].equals(uniformName))
            {
                int offset = uOffset[i];
                int size = uSize[i];
                switch (uBaseType[i])
                {
                case I:
                    for (int j = 0; j < size; j++)
                        intValue[offset + j] = (Integer) vals[j];
                    break;
                case F:
                    for (int j = 0; j < size; j++)
                        floatValue[offset + j] = (Float) vals[j];
                    break;
                }
                return;
            }
        }
        // not found;
    }

    /**
     * Sets uniform values, specified by an array of value Objects, which should match the uniform types for this GLShader
     */
    public void setValues(Object... vals)
    {
        int voffset = 0;
        for (int i = 0; i < nrOfUniforms; i++)
        {
            int offset = uOffset[i];
            int size = uSize[i];
            // int count = uCount[i];
            switch (uBaseType[i])
            {
            case I:
                for (int j = 0; j < size; j++)
                    intValue[offset + j] = (Integer) vals[voffset + j];
                break;
            case F:
                for (int j = 0; j < size; j++)
                    floatValue[offset + j] = (Float) vals[voffset + j];
                break;
            // case IV: for (int j=0; j<count*size; j++) intValue[offset+j] = (Integer) vals[voffset+j]; break;
            // case FV: for (int j=0; j<count*size; j++) floatValue[offset+j] = (Float) vals[voffset+j]; break;
            }
            voffset += size;
        }
    }

//    /**
//     * Obsolete setValues method
//     */
//    public void setValues(int[] intVals, float[] floatVals)
//    {
//        if (intVals != null)
//        {
//            for (int i = 0; i < intVals.length; i++)
//                intValue[i] = intVals[i];
//        }
//        if (floatVals != null)
//        {
//            for (int i = 0; i < floatVals.length; i++)
//                floatValue[i] = floatVals[i];
//        }
//    }

    /* passes values to OpenGL uniforms */
    private void glSetUniformValue(GLRenderContext glc, int i)
    {
        switch (uType[i])
        {
        case U1I:
            glc.gl.glUniform1i(uLocation[i], intValue[uOffset[i]]);
            break;
        case U2I:
            glc.gl.glUniform2i(uLocation[i], intValue[uOffset[i]], intValue[uOffset[i] + 1]);
            break;
        case U3I:
            glc.gl.glUniform3i(uLocation[i], intValue[uOffset[i]], intValue[uOffset[i] + 1], intValue[uOffset[i] + 2]);
            break;
        case U4I:
            glc.gl.glUniform4i(uLocation[i], intValue[uOffset[i]], intValue[uOffset[i] + 1], intValue[uOffset[i] + 2], intValue[uOffset[i] + 3]);
            break;

        case U1F:
            glc.gl.glUniform1f(uLocation[i], floatValue[uOffset[i]]);
            break;
        case U2F:
            glc.gl.glUniform2f(uLocation[i], floatValue[uOffset[i]], floatValue[uOffset[i] + 1]);
            break;
        case U3F:
            glc.gl.glUniform3f(uLocation[i], floatValue[uOffset[i]], floatValue[uOffset[i] + 1], floatValue[uOffset[i] + 2]);
            break;
        case U4F:
            glc.gl.glUniform4f(uLocation[i], floatValue[uOffset[i]], floatValue[uOffset[i] + 1], floatValue[uOffset[i] + 2], floatValue[uOffset[i] + 3]);
            break;

        default:
        }
    }

   static Pattern declPattern = Pattern.compile("(\\w+)\\s*(.*)"); // matches (type) (vars)
    static Pattern varPattern = Pattern.compile("(\\w+)([\\[\\]\\d]*)");



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







    /**
     * OpenGL initialization: the shader program is initialized, activated, and current uniform values are passed on to the OpenGL shader.
     */
    public void glInit(GLRenderContext glc)
    {
        if (prog == null)
        {
            progId = 0;
            return;
        }
        prog.glInit(glc);
        progId = prog.getShaderId();
        // hmi.util.Console.println("GLShader.glInit progId= " + progId);
        uLocation = prog.getUniformLocations(glc, uName);
        glc.gl.glUseProgram(progId);
        // int vertexIndex = gl.glGetAttribLocation( progId, "mcPosition");
        // int normalIndex = gl.glGetAttribLocation( progId, "mcNormal");
        // System.out.println("GLShader "+ shaderProgName + "INIT mcPositionIndex="+ vertexIndex + " mcNormalIndex="+ normalIndex);
        // System.out.println("GLShader nrOfUniforms=" + nrOfUniforms);
        for (int i = 0; i < nrOfUniforms; i++)
        {
            glSetUniformValue(glc, i);
        }
    }

    /**
     * OpenGL rendering: the shader is activated, and current uniform values are passed on to the OpenGL shader.
     */
    public final void glRender(GLRenderContext glc)
    {
        glc.gl.glUseProgram(progId);
        for (int i = 0; i < nrOfUniforms; i++)
        {
            glSetUniformValue(glc, i);
        }
    }

}
