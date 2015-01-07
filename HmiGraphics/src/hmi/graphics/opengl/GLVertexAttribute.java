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

package hmi.graphics.opengl;

import hmi.graphics.scenegraph.VertexAttribute;
import hmi.graphics.util.BufferUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.media.opengl.*;

/**
 * The OpenGL counterpart of the VertexAttribute class for GMesh It stores attribute data in FloatBuffer format, ready for rendering operations.
 */
public class GLVertexAttribute
{
    private String attributeName; // Attribute name: a GLSL attribute Name like mcPosition, mcNormal, or texCoord1
    // Each GLSL attribute has some index (>=0), determined by the GLSL shader program, stored here in attributeIndex.
    private int attributeIndex = -1; // A -1 value denotes an unbound attribute.
    private int texUnit = -1; // The texture unit number, for TexCoordi attributes. >= 0, if defined

    // The vertex attribute data is kept in a FloatBuffer:
    private java.nio.FloatBuffer vertexDataBuffer; // The FloatBuffer for the vertex data.
    private int floatBufferSize; // size of the vertexData buffer, in number of floats
    private int byteBufferSize; // size of the vertexData buffer, in number of bytes.
    private int attribSize; // size of a single vertex attribute, in bytes.
    private boolean bufferModified; // "Dirty bit", set to true when the buffer contents have been modified.
    // For rendering, this data is copied into a region of a OpenGL VBO (a GL_ARRAY_BUFFER), starting at bufferOffset:
    private int bufferOffset; // the GL_ARRAY_BUFFER offset
    private int nrOfVertices;
    
    private static Logger logger = LoggerFactory.getLogger(GLVertexAttribute.class.getName());

    public String getAttributeName()
    {
        return attributeName;
    }

    public String toString()
    {
        StringBuilder buf = new StringBuilder();
        appendTo(buf, 0);
        return buf.toString();
    }

    public StringBuilder appendTo(StringBuilder buf, int tab)
    {
        GLUtil.appendSpacesString(buf, tab, "GLVertexAttribute \"");
        buf.append(attributeName);
        buf.append('"');
        buf.append(" nrOfVertices=");
        buf.append(nrOfVertices);
        float[] vdata = getVertexData(null);
        int dataSize = vdata.length;
        int lineSize = 30;
        for (int i = 0; i < dataSize; i++)
        {
            if (i % lineSize == 0)
            {
                GLUtil.appendNLSpaces(buf, tab);
                buf.append(String.format("%6d", i));
                buf.append(":  ");

            }
            buf.append(String.format("%6.3f", vdata[i]));
            buf.append("  ");
        }
        return buf;

    }

    /**
     * Create a new GLVertexAttribute
     */
    public GLVertexAttribute(String attrName, float[] vertexData, int attribSize, int nrOfVertices, int texUnit)
    {
        attributeName = attrName;
        this.attribSize = attribSize;
        setTexUnit(texUnit);
        this.nrOfVertices = nrOfVertices;
        floatBufferSize = nrOfVertices * attribSize;
        byteBufferSize = 4 * floatBufferSize;
        vertexDataBuffer = BufferUtil.directFloatBuffer(floatBufferSize);
        setVertexData(vertexData);
    }

    /**
     * Create a new GLVertexAttribute from a generic VertexAttribute
     */
    public GLVertexAttribute(VertexAttribute va)
    {
        this(va.getName(), va.getVertexData(), va.getAttributeValueSize(), va.getNrOfValues(), 1);

    }

    /**
     * Sets the texture unit to be used for this (texture) attribute
     */
    public void setTexUnit(int texUnit)
    {
        logger.debug("GLVertexAttribute " + attributeName + " setTexUnit " + texUnit);
        this.texUnit = texUnit;
    }

    public int getTexUnit()
    {
        return texUnit;
    }

    /**
     * Returns the GLAttribute name.
     */
    public String getName()
    {
        return attributeName;
    }

    /**
     * Returns the number of vertices
     */
    public int getNrOfVertices()
    {
        return nrOfVertices;
    }

    /**
     * Returns the buffer size, in number of bytes
     */
    public int getByteBufferSize()
    {
        return byteBufferSize;
    }

    /**
     * Sets the GL_ARRAY_BUFFER buffer offset
     */
    public void setArrayBufferOffset(int offset)
    {
        bufferOffset = offset;
    }

    /**
     * Sets vertex data, by copying from the specified array.
     */
    public void setVertexData(float[] vertexData)
    {
        logger.debug("GLVertexAttribute.setVertexData length = " + vertexData.length + " floatBufferSize = " + floatBufferSize);
        vertexDataBuffer.clear();
        vertexDataBuffer.put(vertexData, 0, floatBufferSize);
        bufferModified = true;
    }

    /**
     * Fills and returns the vertexData float array with the current contents of the vertex data buffer. The passed in vertexData array can be null,
     * in which case a new float array is allocated.
     */
    public float[] getVertexData(float[] vertexData)
    {
        if (vertexData == null)
            vertexData = new float[floatBufferSize];
        vertexDataBuffer.rewind();
        vertexDataBuffer.get(vertexData, 0, floatBufferSize);
        return vertexData;
    }

    /**
     * Sets the GLSL attribute index for the specified shader program, by querying for the attribute location. If the attribute name is not an active
     * variable for this shader, the index is set to -1, which effectively disables this attribute for rendering. Returns the assigned attribute
     * index.
     */
    public int setAttributeIndex(GLRenderContext glc, int prog)
    {
        attributeIndex = glc.gl.glGetAttribLocation(prog, attributeName);
        logger.debug("GLVertexAttribute.setAttributeIdex from shader prog for " + attributeName + " to " + attributeIndex);
        return attributeIndex;
    }

    public void glInit(GLRenderContext glc)
    {
    }

    /**
     * GL render for this attribute. Basically, copies vertex data to the GL_ARRAY_BUFFER buffer, if data has been modified, and sets the gl vertex
     * data pointer to the buffer data.
     */
    public void glRender(GLRenderContext glc)
    {
        if (attributeIndex < 0)
            return; // not an active attribute (could be because shader has optimized an attribute away, such as normals when not used.
        if (bufferModified)
        {
            vertexDataBuffer.rewind();
            glc.gl.glBufferSubData(GL.GL_ARRAY_BUFFER, bufferOffset, byteBufferSize, vertexDataBuffer);
            bufferModified = false;
        }
        glc.gl2.glVertexAttribPointer(attributeIndex, attribSize, GL.GL_FLOAT, false, 0, bufferOffset);
        glc.gl2.glEnableVertexAttribArray(attributeIndex);

    }

}
