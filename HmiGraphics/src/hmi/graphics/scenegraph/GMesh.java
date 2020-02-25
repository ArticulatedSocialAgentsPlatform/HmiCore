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
package hmi.graphics.scenegraph;

import hmi.graphics.geometry.Triangulator;
import hmi.math.Mat3f;
import hmi.math.Vec3f;
import hmi.util.BinUtil;
import hmi.util.BinaryExternalizable;
import hmi.util.Diff;
import hmi.xml.XMLFormatting;
import hmi.xml.XMLStructureAdapter;
import hmi.xml.XMLTokenizer;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A GMesh object defines methods for defining and retrieving mesh data arrays and index arrays, as well as methods for converting between various
 * representation schemes. A GMesh is not dependend on any particular render technology, nor on any particular graphics file format. Rather, it is
 * intended to be an intermediate internal format, for easy and efficient exchange of graphical mesh data. The following schemes can be used to
 * specify mesh data: 1) specify just float data arrays d0, d1, ... dn, without index data. 2) specify float data arrays d0, d1, ..., dn and a single
 * index array i, 3) specify float data arrays d0, d1, ..., dn and similar index arrays i0, i1, ..., in. By the size of a data array for some named
 * attribute, we mean the length of that array divided by the attribute size. For instance, a float array of length 300 defines a data array of size
 * 100 if the attribute size would be 3, like for instance for 3D coordinates or normals. For texture coordinates with attribute size 2, on the other
 * hand, the size would be 300/2=150. (For index arrays, there is no distinction between array length and size of the data.) For case 1) and case 2)
 * above, we require that the sizes of all the data arrays are the same. For case 3), we require that the size of all the index arrays is the same,
 * and of course, the values of the indices within ik should be smaller than the size of dk. The mesh topology can be defined in several ways: the
 * default is to assume a triangulated mesh, where every three consecutive indices (or consecutive attributes for case 1) form a triangle. An
 * alternative is work with polygons, by specifying the vcounts data. The latter is an array specifying, for each polygon, the number of consecutive
 * indices. polygons with "holes" are not supported. Addition for morph targets: In addition to a "base" mesh we can have a number vof named morph
 * targets, each with its own set of attributes. It is allowed and expected that morph targets introduce new vertex positions and normals, but not new
 * tex coords etc.
 * 
 * @author Job Zwiers
 */
public class GMesh extends XMLStructureAdapter implements BinaryExternalizable, Diff.Differentiable
{

    /** MeshType enumerates the legal mesh geometry, like Triangles, Tristrips, Polygons, etc. */
    public enum MeshType
    {
        Undefined, Triangles, Trifans, Tristrips, Polygons, Polylist
    };

    private static final int ATTRIBUTELIST_SIZE = 32;

    private String id; // optional name or Id of this GMesh
    private MeshType meshType = MeshType.Undefined; // type of mesh, like Triangles etc.
    private ArrayList<VertexAttribute> attributeList = new ArrayList<VertexAttribute>(ATTRIBUTELIST_SIZE); // the List of (base-) mesh attributes
    private String[] morphTargets = null; // optional list of morph target names
    private ArrayList<ArrayList<VertexAttribute>> morphAttributeLists = null; // optional Lists of attributes for, for each of the named morph targets

    private int[] vcounts; // defined for polygon/polylist data only: number of vertices per polygon.
    private int[] indexData; // common index data, if non-null. Usually defined only after unifying indices.
    private int nrOfVertices = -1; // Number of *distinct* vertices for this mesh. only defined ( i.e. >= 0) after unifying indices.
    private boolean unifiedIndexData = true; // true when a common index for all vertex attributes is used, false when vertex atributes have
                                             // individual indices.
    private static Logger logger = LoggerFactory.getLogger(GMesh.class.getName());

    /**
     * Default constructor
     */
    public GMesh()
    {
    }

    /**
     * Creates a new GMesh and reads the data from the XMLTokenizer.
     */
    public GMesh(XMLTokenizer tokenizer) throws IOException
    {
        this();
        readXML(tokenizer);
    }

    /**
     * Creates a clone of the specified base GMesh. Data is not copied, rather this GMesh and the specified base Gmesh will share data arrays.
     */
    public GMesh(GMesh base)
    {
        this();
        this.id = base.id;
        this.meshType = base.meshType;
        this.attributeList = base.attributeList;
        this.morphTargets = base.morphTargets;
        this.morphAttributeLists = base.morphAttributeLists;
        this.vcounts = base.vcounts;
        this.indexData = base.indexData;
        this.nrOfVertices = base.nrOfVertices;
        this.unifiedIndexData = base.unifiedIndexData;
    }

    /**
     * show differences
     */
    public String showDiff(Object gmObj)
    {
        GMesh gm = (GMesh) gmObj;
        if (gm == null) return "GMesh " + id + ", diff: null GMesh";
        String diff = Diff.showDiff("GMesh, id", id, gm.id);
        if (diff != "") return diff;
        diff = Diff.showDiff("GMesh " + id + ", diff meshType", meshType.toString(), gm.meshType.toString());
        if (diff != "") return diff;
        diff = Diff.showDiff("GMesh " + id + ", diff attributes", attributeList, gm.attributeList);
        if (diff != "") return diff;
        diff = Diff.showDiff("GMesh " + id + ", diff morphTargets", morphTargets, gm.morphTargets);
        if (diff != "") return diff;
        diff = Diff.showDiff2("GMesh " + id + ", diff morphAttributeLists", morphAttributeLists, gm.morphAttributeLists);
        if (diff != "") return diff;
        diff = Diff.showDiff("GMesh " + id + ", diff vcounts", vcounts, gm.vcounts);
        if (diff != "") return diff;
        diff = Diff.showDiff("GMesh " + id + ", diff indexData", indexData, gm.indexData);
        if (diff != "") return diff;
        diff = Diff.showDiff("GMesh " + id + ", diff nrOfVertices", nrOfVertices, gm.nrOfVertices);
        if (diff != "") return diff;
        diff = Diff.showDiff("GMesh " + id + ", diff unifiedIndexData", unifiedIndexData, gm.unifiedIndexData);
        if (diff != "") return diff;
        return "";
    }

    /**
     * Defines the GMesh id
     */
    public void setId(String id)
    {
        this.id = id;
    }

    /**
     * returns the GMesh id, possibly null
     */
    public String getId()
    {
        return id;
    }

    /**
     * Sets the GMesh.MeshType of this GMesh, like Undefined, Triangles, etc.
     */
    public void setMeshType(MeshType t)
    {
        meshType = t;
    }

    /** Returns the GMesh.MeshType of this GMesh. */
    public MeshType getMeshType()
    {
        return meshType;
    }

    /**
     * Defines the names of the morph targets for this GMesh and allocates (empty) vertex attribute lists for each of these.
     */
    public void setMorphTargets(String[] morphTargets)
    {
        // hmi.util.Console.println("GMesh " + id + " setMorphTargets ");
        // for (int i=0; i<morphTargets.length; i++) hmi.util.Console.print(morphTargets[i] + " ");
        // hmi.util.Console.println();
        this.morphTargets = morphTargets;
        morphAttributeLists = new ArrayList<ArrayList<VertexAttribute>>(morphTargets.length);
        for (int i = 0; i < morphTargets.length; i++)
            morphAttributeLists.add(new ArrayList<VertexAttribute>(2));
    }

    /**
     * Returns the String array with morph target names, or null when no morph targets have been defined.
     */
    public String[] getMorphTargets()
    {
        return morphTargets;
    }

    /**
     * returns the morph data arrays for a specified attribute name. So getMorphData("mcPosition")[2] is the float array with vertex data for the
     * "mcPosition" attribute of the morph target with index 2.
     */
    public float[][] getMorphData(String semantic)
    {
        float[][] morphData = new float[morphTargets.length][];
        for (int i = 0; i < morphData.length; i++)
        {
            ArrayList<VertexAttribute> tgAttrList = morphAttributeLists.get(i);
            for (VertexAttribute attr : tgAttrList)
            {
                if (attr.getName().equals(semantic))
                {
                    // hmi.util.Console.println("getMorphData(" + semantic + ") for target " + i);
                    morphData[i] = attr.getVertexData();
                    break;
                }
            }
        }
        return morphData;
    }

    /**
     * returns the number of morph targets, or 0 when no morph targets have been defined
     */
    public int morphListSize()
    {
        if (morphAttributeLists == null) return -1;
        return morphAttributeLists.size();
    }

    /*
     * Requests a mesh attribute or a morph target attribute; if necessary, the attribute is created and added to the attributeList, else it is simply
     * retrieved from that list. morphtarget denotes the morph target when >= 0, otherwise (i.e. <0) it denotes the base mesh attribute list
     */
    private VertexAttribute requestVertexAttribute(int morphTarget, String attributeName)
    {
        ArrayList<VertexAttribute> atList = (morphTarget < 0) ? attributeList : morphAttributeLists.get(morphTarget);

        for (VertexAttribute attr : atList)
            if (attributeName.equals(attr.getName())) return attr;
        VertexAttribute attr = new VertexAttribute(attributeName);
        addVertexAttribute(morphTarget, attr);
        return attr;
    }

    /* Insert new attribute attr within the attribute list for the specified morphTarget or base mesh, */
    /* at the place determined by the ordinal for the attribute name. " Unknown" names end up at the end of the list */
    private void addVertexAttribute(int morphTarget, VertexAttribute attr)
    {
        List<VertexAttribute> atList = (morphTarget < 0) ? attributeList : morphAttributeLists.get(morphTarget);
        // fixed order: mcPosition, mcNormal, color, secondaryColor, texCoord1, texCoord2 ....
        for (int i = 0; i < atList.size(); i++)
        {
            if (attrOrd(attr.getName()) < attrOrd(atList.get(i).getName()))
            {
                atList.add(i, attr);
                return;
            }
        }
        atList.add(atList.size(), attr);
    }

    /* Defines the order and orinal number for all legal attribute names */
    private static String[] attrOrder = new String[] { "mcPosition", "mcNormal", "color", "secondaryColor", "texCoord0", "texCoord1",
            "texCoord2", "texCoord3", "texCoord4", "texCoord5", "texCoord6" };

    /* Return the ordinal for some legal attribute name */
    /* "Unknown" attribute names get an ordinal one higher than the ordinal of the last known name */
    private int attrOrd(String attrName)
    {
        for (int i = 0; i < attrOrder.length; i++)
        {
            if (attrName.equals(attrOrder[i])) return i;
        }
        return attrOrder.length;
    }

    /**
     * Retrieves a named attribute; this could be null if the attribute is not defined for this GMesh. This method can be used for fixed-function
     * OpenGL style attributes as well as for user-defined attributes.
     */
    public VertexAttribute getVertexAttribute(String attributeName)
    {
        return getVertexAttribute(-1, attributeName);
    }

    /**
     * Retrieves a named attribute; this could be null if the attribute is not defined for this GMesh. This method can be used for fixed-function
     * OpenGL style attributes as well as for user-defined attributes.
     */
    public VertexAttribute getVertexAttribute(int morphTarget, String attributeName)
    {
        if (morphTarget >= 0 && morphAttributeLists == null) return null;
        List<VertexAttribute> atList = (morphTarget < 0) ? attributeList : morphAttributeLists.get(morphTarget);
        if (atList == null) return null;
        for (VertexAttribute attr : atList)
            if (attributeName.equals(attr.getName())) return attr;
        return null;
    }

    /**
     * Returns the list with VertexAttributes for this GMesh
     */
    public ArrayList<VertexAttribute> getVertexAttributeList()
    {
        return attributeList;
    }

    /**
     * Returns the list with VertexAttributes for this GMesh
     */
    public ArrayList<VertexAttribute> getVertexAttributeList(int morphTarget)
    {
        if (morphTarget < 0)
        {
            return attributeList;
        }
        else
        {
            if (morphAttributeLists == null) return null;
            else return morphAttributeLists.get(morphTarget);
        }
    }

    /**
     * Returns the list with VertexAttribute names for this GMesh
     */
    public List<String> getVertexAttributeNameList(int morphTarget)
    {
        ArrayList<VertexAttribute> atlist = (morphTarget < 0) ? attributeList : morphAttributeLists.get(morphTarget);
        List<String> result = new ArrayList<String>(8);
        for (VertexAttribute va : atlist)
        {
            result.add(va.getName());
        }

        return result;
    }

    public boolean checkMorphTargetConsistency(String attrName)
    {
        int nrOfMorphs = morphAttributeLists.size();
        boolean result = true;
        for (int i = 0; i < nrOfMorphs; i++)
            result = result && checkMorphTargetConsistency(i, attrName);
        return result;
    }

    public boolean checkMorphTargetConsistency(int morphTarget, String attrName)
    {
        float[] basemeshData = null;
        for (VertexAttribute attr : attributeList)
        {
            if (attr.getName().equals(attrName))
            {
                // hmi.util.Console.println("getMorphData(" + semantic + ") for target " + i);
                basemeshData = attr.getVertexData();
                break;
            }
        }
        float[] morphData = null;
        ArrayList<VertexAttribute> tgAttrList = morphAttributeLists.get(morphTarget);
        for (VertexAttribute attr : tgAttrList)
        {
            if (attr.getName().equals(attrName))
            {
                // hmi.util.Console.println("getMorphData(" + semantic + ") for target " + i);
                morphData = attr.getVertexData();
                break;
            }
        }

        // hmi.util.Console.println("checkMorphTargetConsistency " + morphTarget + ": " + attrName);
        if (basemeshData == null)
        {
            //hmi.util.Console.println("GMesh " + getId() + ": Null basemeshData");
            return false;
        }
        if (morphData == null)
        {
            // hmi.util.Console.println("Null morphData");
            return false;
        }
        // hmi.util.Console.println("baseMeshData length = " + basemeshData.length);
        // hmi.util.Console.println("morphData length   = " + morphData.length);
        boolean result = morphData.length == basemeshData.length;
        return result;
    }

    /**
     * Defines the (non-indexed) vertex data for a named attribute. This includes the floating point data as well attribute size, but no index data
     * The attribute size specifies the number of floats per attribute.
     */
    public void setVertexData(String attributeName, int dataElementSize, float[] vertexData)
    {
        setVertexData(-1, attributeName, dataElementSize, vertexData);
    }

    /**
     * Defines the (non-indexed) vertex data for a named attribute. This includes the floating point data as well attribute size, but no index data
     * The attribute size specifies the number of floats per attribute.
     */
    public void setVertexData(int morphTarget, String attributeName, int dataElementSize, float[] vertexData)
    {
        VertexAttribute attr = requestVertexAttribute(morphTarget, attributeName);
        attr.setAttributeValueSize(dataElementSize);
        attr.setVertexData(vertexData);
    }

    /**
     * Defines the indexed vertex data for a named attribute. (Assumes that global index data is not being used.) This includes the floating point
     * data as well as index data and attribute size. The attribute size specifies the number of floats per attribute.
     */
    public void setIndexedVertexData(String attributeName, int dataElementSize, float[] vertexData, int[] indexData)
    {
        setIndexedVertexData(-1, attributeName, dataElementSize, vertexData, indexData);
    }

    /**
     * Defines the indexed vertex data for a named attribute. (Assumes that global index data is not being used.) This includes the floating point
     * data as well as index data and attribute size. The attribute size specifies the number of floats per attribute. The morphtarget parameter can
     * be -1, in which case there is no morph target. Similarly, the vertex attribute set can be -1, to denote absence of any set assignment.
     */
    public void setIndexedVertexData(int morphTarget, String attributeName, int dataElementSize, float[] vertexData, int[] vertexIndexData)
    {
        if (indexData != null) throw new IllegalStateException(
                "GMesh.setIndexedVertexdata not legal when shared indexData has been set or indices have been unified");
        // if (morphTarget >=0) hmi.util.Console.println("setIndexedVertexData for " + morphTarget + ": " + attributeName);
        VertexAttribute attr = requestVertexAttribute(morphTarget, attributeName);
        attr.setAttributeValueSize(dataElementSize);
        attr.setVertexData(vertexData);
        attr.setIndexData(vertexIndexData);
        unifiedIndexData = false;
    }

    /**
     * Returns the vertex data float array for a named attribute. Returns null if either the attribute was not defined, or no vertexData for that
     * attribute was defined.
     */
    public float[] getVertexData(String attributeName)
    {
        return getVertexData(-1, attributeName);
    }

    /**
     * Returns the vertex data float array for a named attribute. Returns null if either the attribute was not defined, or no vertexData for that
     * attribute was defined.
     */
    public float[] getVertexData(int morphTarget, String attributeName)
    {
        ArrayList<VertexAttribute> atList = (morphTarget < 0) ? attributeList : morphAttributeLists.get(morphTarget);
        for (VertexAttribute attr : atList)
        {
            if (attributeName.equals(attr.getName())) return attr.getVertexData();
        }
        return null;
    }

    /**
     * Returns the index int array for a named attribute. Returns null if either the attribute was not defined at all, or when no indices for the
     * attribute are defined.
     */
    public int[] getAttributeIndexData(String attributeName)
    {
        return getAttributeIndexData(-1, attributeName);
    }

    /**
     * Returns the index int array for a named attribute. Returns null if either the attribute was not defined at all, or when no indices for the
     * attribute are defined.
     */
    public int[] getAttributeIndexData(int morphTarget, String attributeName)
    {
        ArrayList<VertexAttribute> atList = (morphTarget < 0) ? attributeList : morphAttributeLists.get(morphTarget);
        if (hasUnifiedIndexData())
        {
            throw new IllegalStateException("GMesh.getAttributeIndexData not legal when indices are unified");
        }
        for (VertexAttribute attr : atList)
        {
            if (attributeName.equals(attr.getName())) return attr.getIndexData();
        }
        return null;
    }

    /**
     * Returns the common index data array, which could be null. It is assumed that unified i ndices are being used.
     */
    public int[] getIndexData()
    {
        if (!hasUnifiedIndexData())
        {
            throw new IllegalStateException("GMesh.getIndexData not legal when indices are not unified");
        }
        return indexData;
    }

    /**
     * Defines the common index data array. (Assumes that attribute index data is not being used.)
     */
    public void setIndexData(int[] indexData)
    {
        if (!hasUnifiedIndexData())
        {
            throw new IllegalStateException("GMesh.setIndexData not legal when indices are not unified");
        }
        this.indexData = indexData;
    }

    /**
     * Returns the number of vertex attributes, or -1 if attributeList == null
     */
    public int getNrOfAttributes()
    {
        return attributeList == null ? -1 : attributeList.size();
    }

    /**
     * Returns the length of the number of common indices, i.e. the length of the indexData array. If the latter is null, -1 is returned.
     */
    public int getNrOfIndices()
    {
        return indexData == null ? -1 : indexData.length;
    }

    /**
     * Returns the number of distinct vertices, or -1 when indices are not unified;
     */
    public int getNrOfVertices()
    {
        // if ( ! hasUnifiedIndexData()) {
        // throw new IllegalStateException("GMesh.getNrOfVertices() not legal when indices are not unified");
        // }
        return nrOfVertices;
    }

    /**
     * Defines the polgon/polylist vertex counts data: Each array element specifies the number of vertices for one polygon.
     */
    public void setVCountData(int[] counts)
    {
        vcounts = counts;
    }

    /**
     * Returns the polgon/polylist vertex counts data: Each array element specifies the number of vertices for one polygon. Returns null if no VCount
     * data has been set, or if the mesh has been triangulated.
     */
    public int[] getVCountData()
    {
        return vcounts;
    }

    /**
     * Transforms the mesh attributes with specific names: mcPosition and mcNormal. Assumption: transformMatrix is a 4x4 matrix, in row major order.
     * The translation part of it will be applied to the mcPosition (i.e. the vertex coordinates), but not to the mcNormal (i.e. the normal).
     * Assumption two: the transform matrix is a rotation
     * and/or translation. Uniform Scaling is ok, but will result in normal vectors that are no longer unit length.
     */
    public void affineTransform(float[] mat4x4)
    {
        // hmi.util.Console.println(">>>GMesh " + getId() + " affineTransform");
        VertexAttribute coordAttribute = getVertexAttribute("mcPosition");
        if (coordAttribute != null)
        {
            coordAttribute.affineTransform(mat4x4);

            if (morphAttributeLists != null)
            {
                for (int morphTarget = 0; morphTarget < morphAttributeLists.size(); morphTarget++)
                {
                    // hmi.util.Console.println("GMesh.affineTransform: morphTarget " + morphTarget);
                    VertexAttribute coordAttr = getVertexAttribute(morphTarget, "mcPosition");
                    if (coordAttr != null) coordAttr.affineTransform(mat4x4);
                }
            }
        }
        VertexAttribute normalAttribute = getVertexAttribute("mcNormal");
        if (normalAttribute != null)
        {
            float[] normalTransformMatrix = Mat3f.getMat3f();
            Mat3f.invertTransposeMat4f(normalTransformMatrix, mat4x4);
            normalAttribute.linearTransform(normalTransformMatrix);
            if (morphAttributeLists != null)
            {
                for (int morphTarget = 0; morphTarget < morphAttributeLists.size(); morphTarget++)
                {
                    VertexAttribute normalAttr = getVertexAttribute(morphTarget, "mcNormal");
                    if (normalAttr != null) normalAttr.affineTransform(mat4x4);
                }
            }
        }
    }

    /**
     * Transforms the mesh attributes with specific names: VertexCoord, Normal Assumption: transformMatrix is a 4x4 matrix, in row major order. The
     * translation part of it will be applied to * the vertex coordinates, but not to the normals. Assumption two: the transform matrix is a rotation
     * and/or translation Scaling is ok, but will result in normal vectors that are no longer unit length.
     */
    public void linearTransform(float[] mat3x3)
    {
        // TODO morpheed meshes

        VertexAttribute coordAttribute = getVertexAttribute("mcPosition");
        if (coordAttribute != null)
        {
            coordAttribute.linearTransform(mat3x3);
        }

        VertexAttribute normalAttribute = getVertexAttribute("mcNormal");
        if (normalAttribute != null)
        {
            float[] normalTransformMatrix = Mat3f.getMat3f();
            Mat3f.invertTranspose(normalTransformMatrix, mat3x3);
            normalAttribute.linearTransform(normalTransformMatrix);
        }
    }

    //
    // /**
    // * Rotates the GMesh of the skinned mesh
    // */
    // public void rotate(float x, float y, float z, float angle) {
    // float[] rot = Quat4f.getQuat4f();
    // Quat4f.setFromAxisAngle4f(rot, x, y, z, angle);
    // float[] mat3x3 = Mat3f.getMat3f();
    // Mat3f.setFromQuatScale(mat3x3, rot, 1.0f);
    // linearTransform(mat3x3);
    // }
    //
    /**
     * scales the mesh attributes with specific names: VertexCoord, Normal
     * 
     */
    // public void scale(float[] sa) {
    // if (sa[0] < 0f || sa[1] < 0f || sa[2] < 0f) {
    // hmi.util.Console.println("Warning: GMesh scale operation with reflection included not yet supported");
    // }
    // //hmi.util.Console.println("GMesh scale: " + hmi.math.Vec3f.toString(sa));
    // VertexAttribute coordAttribute = getVertexAttribute("mcPosition");
    // if (coordAttribute != null) {
    // float[] coords = coordAttribute.getVertexData();
    // //int coordSize = coordAttribute.getVertexDataSize();
    // int nrOfValues = coordAttribute.getNrOfValues();
    // int attributeValueSize = coordAttribute.getAttributeValueSize(); // assume 3 or 4
    //
    // for (int i=0; i<nrOfValues; i++) {
    // int offset = i*attributeValueSize;
    // coords[offset] *= sa[0];
    // coords[offset+1] *= sa[1];
    // coords[offset+2] *= sa[2];
    // }
    // }
    // VertexAttribute normalAttribute = getVertexAttribute("mcNormal");
    // // Only scale normals when present, and when scaling is non-uniform or scale factors < 0
    // if (normalAttribute != null && ( sa[0] != sa[1] || sa[1] != sa[2] || sa[0] < 0.0f)) {
    // float[] sainv = Vec3f.getVec3f(sa);
    // Vec3f.invert(sainv);
    // // hmi.util.Console.println("GMesh normal scale: " + hmi.math.Vec3f.toString(sainv));
    // float[] normals = normalAttribute.getVertexData();
    // //int normalSize = normalAttribute.getVertexDataSize();
    // int nrOfValues = normalAttribute.getNrOfValues();
    // int attributeValueSize = normalAttribute.getAttributeValueSize();
    // if (attributeValueSize != 3) {
    // throw new RuntimeException("GMesh.scale: normals with length " + attributeValueSize + " (should be 3)");
    // }
    // for (int i=0; i<nrOfValues; i++) {
    // int offset = 3*i;
    // // hmi.util.Console.println("normal " + i + Vec3f.toString(normals, offset));
    // normals[offset] *= sainv[0];
    // normals[offset+1] *= sainv[1];
    // normals[offset+2] *= sainv[2];
    // Vec3f.normalize(normals, offset);
    // // hmi.util.Console.println("scaled normal " + i + Vec3f.toString(normals, offset));
    // }
    // }
    // }

    public boolean checkIndexIntegrity()
    {
        if (indexData == null)
        {
            logger.error("checkIndexIntegrity: null indexData");
            return false;
        }
        for (int i = 0; i < indexData.length; i++)
        {
            if (indexData[i] < 0 || indexData[i] >= nrOfVertices)
            {
                logger.error("checkIndexIntegrity found out of range  index: " + indexData[i] + " (nrOfVertices== " + nrOfVertices + ")");
                return false;

            }

        }
        return true;
    }

    private static final int VERTEXCOORD_SIZE = 3;

    public boolean checkTriangleIntegrity(float minSize)
    {
        VertexAttribute coordsAttribute = getVertexAttribute("mcPosition");

        float[] coords = coordsAttribute.getVertexData();
        // int nrOfCoords = coordsAttribute.getNrOfValues();

        float[] u = new float[VERTEXCOORD_SIZE];
        float[] v = new float[VERTEXCOORD_SIZE];

        float[] c = new float[VERTEXCOORD_SIZE];

        for (int tribase = 0; tribase < indexData.length; tribase += VERTEXCOORD_SIZE)
        {
            int p = tribase;
            int q = tribase + 1;
            int r = tribase + 2;

            Vec3f.sub(u, 0, coords, indexData[q], coords, indexData[p]); // u = q-p
            Vec3f.sub(v, 0, coords, indexData[r], coords, indexData[p]); // v = r-p
            Vec3f.cross(c, u, v);
            float len = Vec3f.length(c);

            // hmi.util.Console.println(
            // " vertex p= " + p + " = (" + Vec3f.toString(coords, indexData[p]) + ")" +
            // " vertex q= " + q + " = (" + Vec3f.toString(coords, indexData[q]) + ")" +
            // " vertex r= " + r + " = (" + Vec3f.toString(coords, indexData[r]) + ")" + "\n" +
            // " vertex u= (" + Vec3f.toString(u) + ")" +
            // " vertex v= (" + Vec3f.toString(v) + ")" + "\n" +
            // " vertex c= (" + Vec3f.toString(c) + ")" +
            // " len c = " + len
            // );
            if (len <= minSize) logger.error("tri size: " + len);
            // if (len < 0.0001f) return false;

        }
        return true;
    }

    // public void addColors() {
    // int nrOfCoords = getVertexAttribute("msPosition").getNrOfValues();
    // float[] colors = new float[4*nrOfCoords];
    // for (int i=0; i<nrOfCoords; i++) {
    // colors[4*i] = 1.0f;
    // colors[4*i+1] = 0.0f;
    // colors[4*i+2] = 0.0f;
    // colors[4*i+3] = 1.0f;
    //
    // }
    // setVertexData("color", 4, colors);
    // }

    public void cleanupTriangles(float minSize)
    {
        VertexAttribute coordsAttribute = getVertexAttribute("mcPosition");

        float[] coords = coordsAttribute.getVertexData();
        // int nrOfCoords = coordsAttribute.getNrOfValues();

        float[] u = new float[VERTEXCOORD_SIZE];
        float[] v = new float[VERTEXCOORD_SIZE];

        float[] c = new float[VERTEXCOORD_SIZE];

        int[] newIndices = new int[indexData.length];
        int copyIndex = 0;

        for (int tribase = 0; tribase < indexData.length; tribase += VERTEXCOORD_SIZE)
        {
            int p = tribase;
            int q = tribase + 1;
            int r = tribase + 2;

            Vec3f.sub(u, 0, coords, indexData[q], coords, indexData[p]); // u = q-p
            Vec3f.sub(v, 0, coords, indexData[r], coords, indexData[p]); // v = r-p
            Vec3f.cross(c, u, v);
            float len = Vec3f.length(c);

            if (len > minSize)
            {
                newIndices[copyIndex] = indexData[p];
                newIndices[copyIndex + 1] = indexData[q];
                newIndices[copyIndex + 2] = indexData[r];
                copyIndex += VERTEXCOORD_SIZE;
            }
            else
            {
                logger.error("removing tri with size: " + len + " at indices " + p + ", " + q + ", " + r);
            }
        }
        if (copyIndex != indexData.length)
        {

            hmi.util.Console.println("cleanup removed " + ((indexData.length - copyIndex) / VERTEXCOORD_SIZE) + " triangles");
            // hmi.util.Console.println("old indexData: ");
            // for (int i=0; i<indexData.length; i++) hmi.util.Console.print(indexData[i] + ", ");
            // hmi.util.Console.println();
            // hmi.util.Console.println("new indexData: ");
            // for (int i=0; i<newIndices.length; i++) hmi.util.Console.print(newIndices[i] + ", ");
            // hmi.util.Console.println();
            indexData = new int[copyIndex];
            System.arraycopy(newIndices, 0, indexData, 0, copyIndex);

        } // else : cleanup OK
    }

    // /**
    // * checks whether at least vertexCoordIndexData has been defined
    // * checks whether defined index arrays all have the same length
    // * checks whether all corresponding data arrays have been defined
    // * checks and if necessary (re)allocates indexData array, of the correct size.
    // */
    // public boolean checkIndexIntegrity() {
    // if (attributeList.size() == 0) return true; // trivial case
    // int[] firstIndexData = attributeList.get(0).getIndexData();
    // if (firstIndexData == null) {
    // logger.severe("GMesh.checkIndexIntegerity: no indices for " + attributeList.get(0).getName());
    // // Console.println("GMesh.checkIndexIntegerity: no indices for " + attributeList.get(0).name);
    // return false;
    // }
    // int indexDataLength = firstIndexData.length;
    // for (VertexAttribute attr : attributeList) {
    // if (attr.getIndexData() == null) {
    // // Console.println("GMesh.checkIndexIntegerity: no indices for " + attr.name);
    // return false;
    // }
    // //Console.println("Index length for " + attr.name + " = " + attr.indexData.length);
    // if (attr.getIndexData().length != indexDataLength) {
    // logger.severe("GMesh.checkIndexIntegerity: number of indices for " + attr.getName()+ "(" + attr.getIndexData().length + ")"
    // + " is inconsistent with number of first attribute: " + attributeList.get(0).getName() + "(" +indexDataLength + ")");
    // return false;
    // }
    // }
    // return true;
    // }

    /**
     * Ensures that all polgons are transformed into triangles. So, the type of the mesh is modified from PolyList to Triangles. Polygons, that could
     * include holes, are not allowed. Polgons need not be convex, however. It is assumed that common indices are used. Else, if individual attribute
     * indices have been used, the GMesh will first apply a unifyIndices operation.
     */
    public void triangulate()
    {

        if (!hasUnifiedIndexData())
        {
            unifyIndices();
        }
        float[] coordData = null;
        // int coordSize = 0;
        for (VertexAttribute attr : attributeList)
        {
            if (attr.getName().equals("mcPosition"))
            {
                coordData = attr.getVertexData();
                // coordSize = attr.getVertexDataSize();
                break;
            }
        }
        // hmi.util.Console.println("GMesh.triangulate coordSize =" + coordSize);
        if (coordData == null)
        {
            logger.warn("GMesh.triangulate: no Vertex coordinates defined");
            return;
        }
        if (indexData == null)
        {
            logger.warn("GMesh.triangulate: no (shared) indices defined");
            return;
        }
        if (vcounts == null)
        {
            logger.warn("GMesh.triangulate: no vcount (i.e. polygon count) data defined");
            return;
        }

        Triangulator triangulator = new Triangulator();
        indexData = triangulator.triangulate(id, coordData, VERTEXCOORD_SIZE, indexData, vcounts);

        vcounts = null;
        meshType = MeshType.Triangles;

    }

    /**
     * returns true when a single, i.e. shared, index is used for all vertexattributes.
     */
    public boolean hasUnifiedIndexData()
    {
        return unifiedIndexData;
    }

    /**
     * Removes all attribute-specific indices, and replaces them by a single, common, indexData array. This implies that the vertexData arrays for
     * individual attributes has to be modified as well, and will usually grow in size, by replicating parts of the existing attribute data.
     */
    public void unifyIndices()
    {
        /*
         * vertexData[att] == attributeList.get(att).getVertexData(), for att in [0..nrOfAttributes-1] indices[att] ==
         * attributeList.get(att).getIndexData(). indexLength == indices[0].length == ... == indices[nrOfAttributes-1].length (all index lengths
         * should be equal) By definition, Vertex i has, for attribute att, attribute value: vertexData[att][ indices[att][i] ] We want to change the
         * vertexData arrays into (usually longer) vertexData' arrays, such that we can use a *common* index: indexData New situation: Vertex i has
         * the data for attribute att in vertexData'[att][ indexData[i] ] So we require vertexData'[att][indexData[i]] =
         * vertexData[att][indices[att][i]] We build up a map function such that vertexData'[att][j] == vertexData[att][map[att][j]], for j in
         * [0..m-1], where m = number of distinct vertices (two vertices are distinct if at least one of their attribute has distinct values)
         * Mathematically, map[att] == { (indexData[i], indices[att][i]) | i in [0..n-1] }, so [0..m-1] = range(indexData) We build up
         * range(indexData) as a growing segment of the form [0 .. nrOfTuples-1], where nrOfTuples is the number of *distinct* tuples found thus far.
         * (tuple "equality" here is based on equal indices, not on the actual vertexData) For Vertex i, we check if it differs from the vertices for
         * common indices [0..i-1]. If it actualy equals vertex k, we set indexData[i] to indexData[k]. Otherwise, that is, if it is new, we set
         * indexData[i] = nrOfTuples, and we add map[attr][nrOfTuples] = indices[attr][i] The map[attr] arrays are passed on to the remapData method
         * of the VertexAttribute instances, which will actually replace the vertexData arrays by vertexData' arrays.
         */
        if (hasUnifiedIndexData())
        {
            // throw new IllegalStateException("GMesh.unifyIndices cannot be called twice");
            // logger.warn("GMesh.unifyIndices cannot be called twice");
            return;
        }

        // throw new IllegalStateException("GMesh.unifyIndices cannot be called twice");
        calculateTuples(); // so that we can override calculateTuples in GSkinnedMesh
    }

    private static final double TUPLEINDEX_GROW_GUESTIMATE = 1.5;

    /**
     * calculates tuples for all new indices, together with the data remapping tables. The remapping for vertexData is returned (for usage within the
     * GSkinnedMesh class)
     */
    protected int[] calculateTuples()
    {
        int nrOfAttributes = attributeList.size();
        // hmi.util.Console.println("GMesh.calculateTuples, nrOfAttributes= " + nrOfAttributes );
        int[][] map = new int[nrOfAttributes][]; // arrays with index remapping, one array for every atribute

        int indexLength = attributeList.get(0).getIndexData().length;
        // indexLength should be the same length for all attributes; check whether this is ok:
        for (VertexAttribute va : attributeList)
        {
            if (va.getIndexData().length != indexLength)
            {
                hmi.util.Console.println("GMesh.unifyIndices:  attribute indices should have all equal size");
                throw new RuntimeException("GMesh.unifyIndices:  attribute indices should have all equal size");
            }
            else
            {
                // hmi.util.Console.println("GMesh.calculateTuples, attribute " + va.getName());
            }
        }
        indexData = new int[indexLength];

        // try to guess the number of different tuples.
        int maxVertexDataSize = 0;
        for (VertexAttribute attr : attributeList)
        {
            if (attr.getVertexData().length > maxVertexDataSize) maxVertexDataSize = attr.getVertexData().length;
        }
        int tupleIndexSize = (int) (TUPLEINDEX_GROW_GUESTIMATE * maxVertexDataSize); // guestimate

        int nrOfTuples = 0;
        int[][] indices = new int[nrOfAttributes][]; // arrays with existing attribute-specific indices, one array for each attribute

        int vertexCoordIndex = 0; // needed for remapping vertexweights

        for (int att = 0; att < nrOfAttributes; att++)
        {
            VertexAttribute attr = attributeList.get(att);
            indices[att] = attr.getIndexData();
            map[att] = new int[tupleIndexSize];
            if (attr.getName().equals("mcPosition")) vertexCoordIndex = att;
        }
        for (int i = 0; i < indexLength; i++)
        { // create tuple for common index i
            if (nrOfTuples >= tupleIndexSize)
            {
                tupleIndexSize = 2 * tupleIndexSize;
                for (int attr = 0; attr < nrOfAttributes; attr++)
                {
                    int[] oldTupleIndices = map[attr];

                    map[attr] = new int[tupleIndexSize];
                    System.arraycopy(oldTupleIndices, 0, map, 0, nrOfTuples);
                }
            }
            for (int attr = 0; attr < nrOfAttributes; attr++)
            {
                map[attr][nrOfTuples] = indices[attr][i];
            }
            boolean isNew = true;
            int tuple = 0;
            for (tuple = 0; tuple < nrOfTuples; tuple++)
            { // compare tuple at index "tuple" with new tuple at top
                boolean tupleDiffers = false;
                for (int attr = 0; attr < nrOfAttributes; attr++)
                {
                    tupleDiffers = (map[attr][tuple] != map[attr][nrOfTuples]);
                    if (tupleDiffers) break;
                }
                isNew = tupleDiffers;
                if (!isNew) break; // already prsesent, at index "tuple"
            }
            indexData[i] = tuple; // if new, tuple = nrOfTuples, else it is the number of the tuple found above.

            if (isNew)
            { // really new tuple: increase tuple count
                nrOfTuples++;
            }
            unifiedIndexData = true;
        }
        nrOfVertices = nrOfTuples; // i.e. the number of *distinct* vertices, not the number of indexed Mesh vertices
        // hmi.util.Console.println("GMesh.calculateTuples, remapData nrOfTuples= " + nrOfTuples);
        for (int att = 0; att < nrOfAttributes; att++)
        {
            VertexAttribute attr = attributeList.get(att);
            // hmi.util.Console.println("GMesh.calculateTuples, remapData attr= " + attr.getName());
            attr.remapData(nrOfVertices, map[att]);
            attr.setIndexData(null);
        }
        // hmi.util.Console.println(morphAttributeLists==null, "calculateTuples " + id + " null morphAttributeLists",
        // "calcTuples morphAttributeLists for " + id);
        if (morphAttributeLists != null)
        {
            // hmi.util.Console.println("calcTuples for morphAttributeLists");
            // int mtg = 0;
            for (ArrayList<VertexAttribute> atList : morphAttributeLists)
            {
                // hmi.util.Console.println("calcTuples for " + mtg++);
                for (int att = 0; att < atList.size(); att++)
                {
                    VertexAttribute attr = atList.get(att);
                    attr.remapData(nrOfVertices, map[att]);
                    attr.setIndexData(null);
                }
            }
        }
        return map[vertexCoordIndex];
    }

    private static boolean showGMeshData = false;

    public static void showGMeshData(boolean show)
    {
        showGMeshData = show;
    }

    public void analyze()
    {
        //hmi.util.Console.println("analyse GMesh: " + getId());
        // ###
        if (attributeList.size() == 0)
        {
            //hmi.util.Console.println(getId() + ": No attributes");
            return;
        }
        VertexAttribute firstAttribute = attributeList.get(0);
        int nrOfIndices = firstAttribute.getNrOfIndices();
        // hmi.util.Console.println("VertexAttribute " + firstAttribute.getName() + " has " + nrOfIndices + " indices");
        for (VertexAttribute va : attributeList)
        {
            int ni = va.getNrOfIndices();
            if (ni != nrOfIndices)
            {
                logger.error("VertexAttribute " + va.getName() + " has different number of indices: " + ni);
            }
            else
            {
                // hmi.util.Console.println("VertexAttribute " + va.getName() + " has " + ni + " indices");
            }

        }

        // boolean indexConsistent = checkIndexIntegrity();
        // hmi.util.Console.println("Consistent indices: " + indexConsistent);
    }

    /**
     * appends the XML attributes to buf.
     */
    @Override
    public StringBuilder appendAttributeString(StringBuilder buf, XMLFormatting fmt)
    {
        int itab = fmt.getIndentedTab();
        appendAttribute(buf, "id", id);
        appendAttribute(buf, "meshType", meshType.toString());
        if (nrOfVertices >= 0) appendAttribute(buf, "nrOfVertices", nrOfVertices);

        // if (indexData != null) {
        // appendAttribute(buf, "indexDataSize", indexData.length);
        // }
        return buf;
    }

    /**
     * decodes the XML attributes.
     */
    @Override
    public void decodeAttributes(HashMap<String, String> attrMap, XMLTokenizer tokenizer)
    {
        String mtype = getOptionalAttribute("meshType", attrMap);
        if (mtype != null) meshType = MeshType.valueOf(mtype);
        id = getOptionalAttribute("id", attrMap, "").intern();
        nrOfVertices = getOptionalIntAttribute("nrOfVertices", attrMap, -1);
        // getOptionalIntAttribute("indexDataSize", attrMap, -1);
        super.decodeAttributes(attrMap, tokenizer);
    }

    private static final int INDICESPERLINE = 30;
    private static final int STRINGSPERLINE = 15;

    /** Appends content part of XML encoding */
    @Override
    public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt)
    {

        if (vcounts != null)
        {
            appendIntArrayElement(buf, "vcounts", vcounts, ' ', fmt, INDICESPERLINE);
        }
        if (indexData != null)
        {
            appendIntArrayElement(buf, "indices", indexData, ' ', fmt, INDICESPERLINE);
        }
        for (VertexAttribute attr : attributeList)
        {
            buf.append('\n');
            attr.appendXML(buf, fmt);
        }
        if (morphTargets != null)
        {
            appendSTag(buf, "morphs", fmt);
            fmt.indent();
            appendStringArrayElement(buf, "morphtargets", morphTargets, ' ', fmt, STRINGSPERLINE);
            for (int mt = 0; mt < morphTargets.length; mt++)
            {
                // buf.append('\n'); appendSpaces(buf, fmt); appendOpenSTag(buf, "morphtarget"); appendAttribute(buf, "name", morphTargets[mt]);
                // appendCloseSTag(buf);
                appendSTag(buf, "morphtarget", fmt);
                ArrayList<VertexAttribute> mtAttributes = morphAttributeLists.get(mt);
                if (mtAttributes != null)
                {
                    fmt.indent();
                    for (VertexAttribute mtAttr : mtAttributes)
                    {
                        buf.append('\n');
                        mtAttr.appendXML(buf, fmt);
                    }
                    fmt.unIndent();
                }

                // buf.append('\n');appendSpaces(buf, fmt);
                appendETag(buf, "morphtarget", fmt);

            }
            appendETag(buf, "morphs", fmt.unIndent());
        }
        return buf;
    }

    /**
     * Decodes content part of XML encoding
     */
    @Override
    public void decodeContent(XMLTokenizer tokenizer) throws IOException
    {
        while (tokenizer.atSTag())
        {
            String tag = tokenizer.getTagName();
            if (tag.equals("vcounts"))
            {
                vcounts = decodeIntArrayElement("vcounts", tokenizer);
            }
            else if (tag.equals("indices"))
            {
                indexData = decodeIntArrayElement("indices", tokenizer);
            }
            else if (tag.equals(VertexAttribute.xmlTag()))
            {
                addVertexAttribute(-1, new VertexAttribute(tokenizer));
            }
            else if (tag.equals("morphs"))
            {
                tokenizer.takeSTag("morphs");
                tag = tokenizer.getTagName();
                // if (

                String[] morphTargets = decodeStringArrayElement("morphtargets", tokenizer);
                setMorphTargets(morphTargets);
                for (int mt = 0; mt < morphTargets.length; mt++)
                {
                    tokenizer.takeSTag("morphtarget");
                    tag = tokenizer.getTagName();
                    if (tag.equals(VertexAttribute.xmlTag()))
                    {
                        addVertexAttribute(mt, new VertexAttribute(tokenizer));
                    }
                    else
                    {
                        logger.warn(tokenizer.getErrorMessage("GMesh: VertexAttribute expected, skip: " + tokenizer.getTagName()));
                        tokenizer.skipTag();
                    }
                    tokenizer.takeETag("morphtarget");
                    tag = tokenizer.getTagName();
                }
                tokenizer.takeETag("morphs");
            }
            else
            {
                logger.warn(tokenizer.getErrorMessage("GMesh: skip: " + tokenizer.getTagName()));
                tokenizer.skipTag();
            }
        }
    }

    /*
     * The XML Stag for XML encoding
     */
    private static final String XMLTAG = "gmesh";

    /**
     * The XML Stag for XML encoding
     */
    public static String xmlTag()
    {
        return XMLTAG;
    }

    /**
     * returns the XML Stag for XML encoding
     */
    @Override
    public String getXMLTag()
    {
        return XMLTAG;
    }

    /**
     * Writes a binary encoding to dataOut
     */
    public void writeBinary(DataOutput dataOut) throws IOException
    {
        dataOut.writeUTF(id); // modified UTF-8
        dataOut.writeUTF(meshType.toString()); // modified UTF-8
        dataOut.writeInt(nrOfVertices);
        BinUtil.writeIntArray(dataOut, vcounts);
        BinUtil.writeIntArray(dataOut, indexData);
        BinUtil.writeBinaryList(dataOut, attributeList);
        BinUtil.writeStringArray(dataOut, morphTargets);
        if (morphAttributeLists == null)
        {
            dataOut.writeInt(-1);
        }
        else
        {
            dataOut.writeInt(morphAttributeLists.size());
            for (ArrayList<VertexAttribute> vaList : morphAttributeLists)
            {
                BinUtil.writeBinaryList(dataOut, vaList);
            }
        }
    }

    /**
     * Reads a binary encoding from dataIn
     */
    public void readBinary(DataInput dataIn) throws IOException
    {
        id = dataIn.readUTF().intern();
        meshType = MeshType.valueOf(dataIn.readUTF());
        nrOfVertices = dataIn.readInt();
        vcounts = BinUtil.readIntArray(dataIn);
        indexData = BinUtil.readIntArray(dataIn);
        attributeList = BinUtil.readBinaryList(dataIn, VertexAttribute.class);
        morphTargets = BinUtil.readStringArray(dataIn);
        int listSize = dataIn.readInt();
        if (listSize < 0)
        {
            morphAttributeLists = null;
        }
        else
        {
            morphAttributeLists = new ArrayList<ArrayList<VertexAttribute>>(listSize);
            for (int i = 0; i < listSize; i++)
            {
                morphAttributeLists.add(BinUtil.readBinaryList(dataIn, VertexAttribute.class));
            }
        }
    }

}
