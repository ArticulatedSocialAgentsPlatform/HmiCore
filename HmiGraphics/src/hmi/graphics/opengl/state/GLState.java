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

package hmi.graphics.opengl.state;


import java.util.HashMap;
import javax.media.opengl.*; 

/**
 * @author Job Zwiers
 */
public final class GLState
{

    //
    // /**
    // * Creates a new GLStateComponent object for a boolean valued OpenGL variable.
    // */
    // public static GLCapability createGLStateComponent(String glName, boolean capability) {
    // Integer glId = glStateVarId.get(glName);
    // if (glId == null) {
    // throw new RuntimeException("GLState.createGLStateComponent, unknown OpenGL capability: " + glName);
    // }
    // return new GLCapability(glId, capability);
    // }
    //
    //
    //
    /***/
    private GLState()
    {
    }

    public static synchronized int createSCId()
    {
        lastSCId++;
        return lastSCId;
    }

    public static synchronized int createSCId(int stateGroup)
    {
        lastSCId++;
        return lastSCId;
    }

    /* adds a GL name plus GL Id plus SC Id */
    protected static void addStateVariable(String glName, int glId, int scId)
    {
        GLNAMETOGLID.put(glName, glId);
        GLIDTOGLNAME.put(glId, glName);
        GLIDTOSCID.put(glId, scId);
        SCIDTOGLID.put(scId, glId);
    }

    /* adds a GL name plus GL Id (no SC Id) */
    protected static void addGLName(String glName, int glId)
    {
        GLNAMETOGLID.put(glName, glId);
        GLIDTOGLNAME.put(glId, glName);
    }

    /**
     * Returns scId for a given GL state variable, or -1 if no scId has been assigned to this state variable.
     */
    public static int getSCId(int glId)
    {
        Integer scId = GLIDTOSCID.get(glId);
        if (scId == null)
        {
            return -1;
        }
        else
        {
            return scId;
        }
    }

    /**
     * Returns the String valued OpenGL name for an OpenGL token.
     */
    public static String getGLName(int glId)
    {
        String glName = GLIDTOGLNAME.get(glId);
        if (glName == null)
        {
            return "<unknown>";
        }
        else
        {
            return glName;
        }
    }

    /**
     * Returns the int valued OpenGL token for an OpenGL name.
     */
    public static int getGLId(String glName)
    {
        Integer glId = GLNAMETOGLID.get(glName);
        if (glId == null)
        {
            return -1;
        }
        else
        {
            return (int) glId;
        }
    }

    /* Mapping from GL name String to GL Id */
    private static final HashMap<String, Integer> GLNAMETOGLID = new HashMap<String, Integer>();
    /* Reverse mapping, from GL Id to GL name String */
    private static final HashMap<Integer, String> GLIDTOGLNAME = new HashMap<Integer, String>();
    /* Mapping from GL Id to SC Id */
    private static final HashMap<Integer, Integer> GLIDTOSCID = new HashMap<Integer, Integer>();
    /* Reverse mapping, from SC Id to GL Id */
    private static final HashMap<Integer, Integer> SCIDTOGLID = new HashMap<Integer, Integer>();

    // state groups are groups of scid numbers, reserved for particular groups like "Light", "Material" etc.
    // private static List<Integer> stateGroupOffset = new ArrayList<Integer>();

    // protected static final int LIGHT_GROUP_LAST = LIGHT_GROUP+9;

    // protected static final int MATERIAL_GROUP_LAST = 120;

    private static int lastSCId = 500;

    public static final int LIGHT_GROUP = 1;
    public static final int LIGHT_GROUP_SIZE = 23;
    public static final int MATERIAL_GROUP = LIGHT_GROUP + LIGHT_GROUP_SIZE;
    public static final int MATERIAL_GROUP_SIZE = 9;
    public static final int TEXTURE_GROUP = MATERIAL_GROUP + MATERIAL_GROUP_SIZE;
    public static final int TEXTURE_GROUP_SIZE = 16;
    public static final int POLYGON_GROUP = TEXTURE_GROUP + TEXTURE_GROUP_SIZE;
    public static final int POLYGON_GROUP_SIZE = 6;

    // format: glName glId
    static
    {
        addGLName("GL_FALSE", GL2.GL_FALSE);
        addGLName("GL_TRUE", GL2.GL_TRUE);
        addGLName("GL_SINGLE_COLOR", GL2.GL_SINGLE_COLOR);
        addGLName("GL_SEPARATE_SPECULAR_COLOR", GL2.GL_SEPARATE_SPECULAR_COLOR);
        addGLName("GL_FRONT", GL2.GL_FRONT);
        addGLName("GL_BACK", GL2.GL_BACK);
        addGLName("GL_FRONT_AND_BACK", GL2.GL_FRONT_AND_BACK);
        addGLName("GL_CCW", GL2.GL_CCW);
        addGLName("GL_CW", GL2.GL_CW);
        addGLName("GL_POINT", GL2.GL_POINT);
        addGLName("GL_LINE", GL2.GL_LINE);
        addGLName("GL_FILL", GL2.GL_FILL);
        addGLName("GL_TEXTURE0", GL2.GL_TEXTURE0);
        addGLName("GL_TEXTURE1", GL2.GL_TEXTURE1);
        addGLName("GL_TEXTURE2", GL2.GL_TEXTURE2);
        addGLName("GL_TEXTURE3", GL2.GL_TEXTURE3);
        addGLName("GL_TEXTURE4", GL2.GL_TEXTURE4);
        addGLName("GL_TEXTURE5", GL2.GL_TEXTURE5);
        addGLName("GL_TEXTURE6", GL2.GL_TEXTURE6);
        addGLName("GL_TEXTURE7", GL2.GL_TEXTURE7);
        addGLName("GL_NEAREST", GL2.GL_NEAREST);
        addGLName("GL_LINEAR", GL2.GL_LINEAR);
        addGLName("GL_NEAREST_MIPMAP_NEAREST", GL2.GL_NEAREST_MIPMAP_NEAREST);
        addGLName("GL_NEAREST_MIPMAP_LINEAR", GL2.GL_NEAREST_MIPMAP_LINEAR);
        addGLName("GL_LINEAR_MIPMAP_NEAREST", GL2.GL_LINEAR_MIPMAP_NEAREST);
        addGLName("GL_LINEAR_MIPMAP_LINEAR", GL2.GL_LINEAR_MIPMAP_LINEAR);
        addGLName("GL_REPEAT", GL2.GL_REPEAT);
        addGLName("GL_MIRROREDREPEAT", GL2.GL_MIRRORED_REPEAT);
        addGLName("GL_CLAMP", GL2.GL_CLAMP);
        addGLName("GL_CLAMP_TO_BORDER", GL2.GL_CLAMP_TO_BORDER);
        addGLName("GL_CLAMP_TO_EDGE", GL2.GL_CLAMP_TO_EDGE);
        addGLName("GL_MODULATE", GL2.GL_MODULATE);
        addGLName("GL_BLEND", GL2.GL_BLEND);
        addGLName("GL_REPLACE", GL2.GL_REPLACE);
        addGLName("GL_DECAL", GL2.GL_DECAL);
        addGLName("GL_ADD", GL2.GL_ADD);
        addGLName("GL_COMBINE", GL2.GL_COMBINE);

        // format: glName GL_Token (glId) state component Id (sc_Id)
        addStateVariable("GL_LIGHTING", GL2.GL_LIGHTING, LIGHT_GROUP);
        addStateVariable("GL_LIGHT_MODEL_AMBIENT", GL2.GL_LIGHT_MODEL_AMBIENT, LIGHT_GROUP + 1);
        addStateVariable("GL_LIGHT_MODEL_LOCAL_VIEWER", GL2.GL_LIGHT_MODEL_LOCAL_VIEWER, LIGHT_GROUP + 2);
        addStateVariable("GL_LIGHT_MODEL_TWO_SIDE", GL2.GL_LIGHT_MODEL_TWO_SIDE, LIGHT_GROUP + 3);
        addStateVariable("GL_LIGHT_MODEL_COLOR_CONTROL", GL2.GL_LIGHT_MODEL_COLOR_CONTROL, LIGHT_GROUP + 4);
        addStateVariable("GL_AMBIENT", GL2.GL_AMBIENT, LIGHT_GROUP + 5);
        addStateVariable("GL_DIFFUSE", GL2.GL_DIFFUSE, LIGHT_GROUP + 6);
        addStateVariable("GL_SPECULAR", GL2.GL_SPECULAR, LIGHT_GROUP + 7);
        addStateVariable("GL_POSITION", GL2.GL_POSITION, LIGHT_GROUP + 8);
        addStateVariable("GL_SPOT_DIRECTION", GL2.GL_SPOT_DIRECTION, LIGHT_GROUP + 9);
        addStateVariable("GL_SPOT_EXPONENT", GL2.GL_SPOT_EXPONENT, LIGHT_GROUP + 10);
        addStateVariable("GL_SPOT_CUTOFF", GL2.GL_SPOT_CUTOFF, LIGHT_GROUP + 11);
        addStateVariable("GL_CONSTANT_ATTENUATION", GL2.GL_CONSTANT_ATTENUATION, LIGHT_GROUP + 12);
        addStateVariable("GL_LINEAR_ATTENUATION", GL2.GL_LINEAR_ATTENUATION, LIGHT_GROUP + 13);
        addStateVariable("GL_QUADRATIC_ATTENUATION", GL2.GL_QUADRATIC_ATTENUATION, LIGHT_GROUP + 14);
        addStateVariable("GL_LIGHT0", GL2.GL_LIGHT0, LIGHT_GROUP + 15);
        addStateVariable("GL_LIGHT1", GL2.GL_LIGHT1, LIGHT_GROUP + 16);
        addStateVariable("GL_LIGHT2", GL2.GL_LIGHT2, LIGHT_GROUP + 17);
        addStateVariable("GL_LIGHT3", GL2.GL_LIGHT3, LIGHT_GROUP + 18);
        addStateVariable("GL_LIGHT4", GL2.GL_LIGHT4, LIGHT_GROUP + 19);
        addStateVariable("GL_LIGHT5", GL2.GL_LIGHT5, LIGHT_GROUP + 20);
        addStateVariable("GL_LIGHT6", GL2.GL_LIGHT6, LIGHT_GROUP + 21);
        addStateVariable("GL_LIGHT7", GL2.GL_LIGHT7, LIGHT_GROUP + 22);

        addStateVariable("GL_COLOR_MATERIAL", GL2.GL_COLOR_MATERIAL, MATERIAL_GROUP);
        addStateVariable("GL_COLOR_MATERIAL_PARAMETER", GL2.GL_COLOR_MATERIAL_PARAMETER, MATERIAL_GROUP + 1);
        addStateVariable("GL_COLOR_MATERIAL_FACE", GL2.GL_COLOR_MATERIAL_FACE, MATERIAL_GROUP + 2);
        addStateVariable("GL_AMBIENT", GL2.GL_AMBIENT, MATERIAL_GROUP + 3);
        addStateVariable("GL_DIFFUSE", GL2.GL_DIFFUSE, MATERIAL_GROUP + 4);
        addStateVariable("GL_AMBIENT_AND_DIFFUSE", GL2.GL_AMBIENT_AND_DIFFUSE, MATERIAL_GROUP + 5);
        addStateVariable("GL_SPECULAR", GL2.GL_SPECULAR, MATERIAL_GROUP + 6);
        addStateVariable("GL_EMISSION", GL2.GL_EMISSION, MATERIAL_GROUP + 7);
        addStateVariable("GL_SHININESS", GL2.GL_SHININESS, MATERIAL_GROUP + 8);

        addStateVariable("GL_ACTIVE_TEXTURE", GL2.GL_ACTIVE_TEXTURE, TEXTURE_GROUP);
        addStateVariable("GL_TEXTURE_MIN_FILTER", GL2.GL_TEXTURE_MIN_FILTER, TEXTURE_GROUP + 1);
        addStateVariable("GL_TEXTURE_MAG_FILTER", GL2.GL_TEXTURE_MAG_FILTER, TEXTURE_GROUP + 2);
        addStateVariable("GL_TEXTURE_MIN_LOD", GL2.GL_TEXTURE_MIN_LOD, TEXTURE_GROUP + 3);
        addStateVariable("GL_TEXTURE_MAX_LOD", GL2.GL_TEXTURE_MAX_LOD, TEXTURE_GROUP + 4);
        addStateVariable("GL_TEXTURE_BASE_LEVEL", GL2.GL_TEXTURE_BASE_LEVEL, TEXTURE_GROUP + 5);
        addStateVariable("GL_TEXTURE_MAX_LEVEL", GL2.GL_TEXTURE_MAX_LEVEL, TEXTURE_GROUP + 6);
        addStateVariable("GL_TEXTURE_WRAP_S", GL2.GL_TEXTURE_WRAP_S, TEXTURE_GROUP + 7);
        addStateVariable("GL_TEXTURE_WRAP_T", GL2.GL_TEXTURE_WRAP_T, TEXTURE_GROUP + 8);
        addStateVariable("GL_TEXTURE_WRAP_R", GL2.GL_TEXTURE_WRAP_R, TEXTURE_GROUP + 9);
        addStateVariable("GL_TEXTURE_BORDER_COLOR", GL2.GL_TEXTURE_BORDER_COLOR, TEXTURE_GROUP + 10);
        addStateVariable("GL_GENERATE_MIPMAP", GL2.GL_GENERATE_MIPMAP, TEXTURE_GROUP + 11);
        addStateVariable("GL_TEXTURE_ENV_MODE", GL2.GL_TEXTURE_ENV_MODE, TEXTURE_GROUP + 12);

        addStateVariable("GL_TEXTURE_1D", GL2.GL_TEXTURE_1D, TEXTURE_GROUP + 13);
        addStateVariable("GL_TEXTURE_2D", GL2.GL_TEXTURE_2D, TEXTURE_GROUP + 14);
        addStateVariable("GL_TEXTURE_3D", GL2.GL_TEXTURE_3D, TEXTURE_GROUP + 15);

        addStateVariable("GL_POLYGON_MODE", GL2.GL_POLYGON_MODE, POLYGON_GROUP);
        addStateVariable("GL_POLYGON_SMOOTH", GL2.GL_POLYGON_SMOOTH, POLYGON_GROUP + 1);
        addStateVariable("GL_POLYGON_OFFSET_FACTOR", GL2.GL_POLYGON_OFFSET_FACTOR, POLYGON_GROUP + 2);
        addStateVariable("GL_POLYGON_OFFSET_POINT", GL2.GL_POLYGON_OFFSET_POINT, POLYGON_GROUP + 3);
        addStateVariable("GL_POLYGON_OFFSET_LINE", GL2.GL_POLYGON_OFFSET_LINE, POLYGON_GROUP + 4);
        addStateVariable("GL_POLYGON_OFFSET_FILL", GL2.GL_POLYGON_OFFSET_FILL, POLYGON_GROUP + 5);

        // addStateVariable("GL_DEPTH_TEST", GLC.GL_DEPTH_TEST );
        // addStateVariable("GL_CULL_FACE", GLC.GL_CULL_FACE );
        // addStateVariable("GL_LIGHTING", GLC.GL_LIGHTING );
        //
        // addStateVariable("GL_ALPHA_TEST", GLC.GL_ALPHA_TEST );
        // addStateVariable("GL_AUTO_NORMAL", GLC.GL_AUTO_NORMAL );
        // addStateVariable("GL_BLEND", GLC.GL_BLEND );

    }

}
