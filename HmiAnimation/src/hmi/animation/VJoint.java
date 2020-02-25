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
package hmi.animation;

import hmi.math.Mat3f;
import hmi.math.Mat4f;
import hmi.math.Quat4f;
import hmi.math.Vec3f;
import hmi.util.Diff;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * VJoint represent virtual joints...
 */
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EI_EXPOSE_REP", justification = "Representations are exposed for efficiency reasons")
public class VJoint implements VObject
{

    private static Logger logger = LoggerFactory.getLogger(VJoint.class.getName());

    private String id; // Supposed to be a "globally" unique identifier within
                       // the scenegraph
    private String sid; // Scoped id: unique amongs siblings in the scene graph,
                        // not globally unique
    private String name; // Friendly name, without constraints.

    private VJoint parent;

    private ArrayList<VJoint> children;
    public static final int DEFAULTCAPACITY = 4;

    private float[] translation = Vec3f.getVec3f();
    private float[] rotation = Quat4f.getIdentity();
    private Mat3f.ScalingType scalingType = Mat3f.ScalingType.IDENTITY; // classification
                                                                        // of
                                                                        // scaling
                                                                        // type
    // private float[] scale = null; // Vec3f, allocated when scaling is set,
    // with some scale factor != 1.0
    private float[] scaleVec = null; // A Vec3f scaling vector for ALIGNED or
                                     // UNIFORM scaling, or for REFLECTION or
                                     // SCALED_REFLECTION
    private float[] scaleMatrix = null; // A Mat3f scaling/skewing matrix.
                                        // non-null for SKEW scaling. Could
                                        // incorporate reflection

    final private float[] localMatrix = Mat4f.getIdentity();
    final private float[] globalMatrix = Mat4f.getIdentity();
    private boolean validLocalMatrix = false; // "dirty bit" for localMatrix.
    private boolean hasSharedBuffers = false; // denotes whether the translation
                                              // and/or rotation
    // array should be considered a shared array, because getTranslationBuffer
    // and/or getRotationBuffer has been called


    // temp vars
    float qw[] = new float[4];
    float qp[] = new float[4];
    float q2[] = new float[4];

    /**
     * Creates a new VJoint, with null name and default capacity.
     */
    public VJoint()
    {
        this(null, DEFAULTCAPACITY);
    }

    /**
     * Creates a new VJoint, with specified id and default capacity.
     */
    public VJoint(String id)
    {
        this(id, DEFAULTCAPACITY);
    }
    
    public VJoint(String id, String sid)
    {
        this(id, sid, DEFAULTCAPACITY);
    }

    /**
     * Creates a new VJoint, with specified id and specified capacity. The latter specifies the initial capacity of the children List.
     */
    public VJoint(String id, int capacity)
    {
        this(id,null,capacity);
    }

    
    public VJoint(String id, String sid, int capacity)
    {
        children = new ArrayList<>(capacity);
        setRotation(Quat4f.getIdentity());
        setSid(sid);
        setId(id);
    }

    /* show (some of the) differences between two VJoints */
    public String showLocalDiff(String msg, Object vjointObj)
    {
        VJoint vj = (VJoint) vjointObj;
        if (vj == null) {
            return "VJoint " + id + ", diff: null VJoint";
        }

        String diff = Diff.showDiff("VJoint, id", id, vj.id);
        if (!"".equals(diff)) {
            return diff;
        }
        diff = Diff.showDiff("VJoint, sid", sid, vj.sid);
        if (!"".equals(diff)) {
            return diff;
        }
        diff = Diff.showDiff("VJoint, name", name, vj.name);
        if (!"".equals(diff)) {
            return diff;
        }

        float[] matrix1 = getLocalMatrix();
        float[] matrix2 = vj.getLocalMatrix();
        if (!Mat4f.epsilonEquals(matrix1, matrix2, 0.001f))
        {
            diff = Diff.showDiff("GNode " + id + ", diff Transform matrices", matrix1, matrix2);
            if (!"".equals(diff)) {
                return diff;
            }
        }

        return "";
    }

    /**
     * Sets the id for this VJoint.
     */
    @Override
    public final void setId(String id)
    {
        this.id = (id == null) ? null : id.intern();
    }

    /**
     * Sets the sid for this VJoint.
     */
    @Override
    public final void setSid(String sid)
    {
        this.sid = (sid == null) ? null : sid.intern();
    }

    /**
     * Sets the name for this VJoint.
     */
    @Override
    public void setName(String name)
    {
        this.name = (name == null) ? null : name.intern();
    }

    /**
     * Returns an interned String that specifies the id.
     */
    @Override
    public String getId()
    {
        return id;
    }

    /**
     * Returns an interned String that specifies the sid.
     */
    @Override
    public String getSid()
    {
        return sid;
    }

    /**
     * Returns an interned String that specifies the name.
     */
    @Override
    public String getName()
    {
        return name;
    }

    public String getIds()
    {
        StringBuilder buf = new StringBuilder();
        if (id != null)
        {
            buf.append("id=");
            buf.append(id);
        }
        if (sid != null)
        {
            buf.append("   sid=");
            buf.append(sid);
        }
        if (name != null)
        {
            buf.append("   name=");
            buf.append(name);
        }
        if (id == null && sid == null && name == null)
        {
            buf.append("---");
        }
        return buf.toString();
    }

    /**
     * returns true iff VJoint vj has the same non-null Id or the same non-null sid. The name is not considered.
     */
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "ES_COMPARING_STRINGS_WITH_EQ", justification = "String is interned")
    public boolean equivId(VJoint vj)
    {
        return ((id != null && this.id == vj.id) || (sid != null && this.sid == vj.sid));
    }

    /**
     * returns true iff VJoint vj has the same non-null Id or the same non-null sid or the same non-null name.
     */
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "ES_COMPARING_STRINGS_WITH_EQ", justification = "String is interned")
    public boolean equivName(VJoint vj)
    {
        return ((id != null && this.id == vj.id) || (sid != null && this.sid == vj.sid) || (name != null && this.name == vj.name));
    }

    /**
     * Return the scaling type: IDENTITY (i.e. no scaling), ALIGNED (i.e. vector scaling), or SKEW (matrix scaling).
     */
    public Mat3f.ScalingType getScalingType()
    {
        return scalingType;
    }

    /**
     * Returns the parent of this VJoint; possibly null
     */
    public VJoint getParent()
    {
        return parent;
    }

    /*
     * Sets the parent of this VJoint, which could be null.
     */
    private void setParent(VJoint parent)
    {
        this.parent = parent;
    }

    /**
     * Adds a a child part to this VJoint.
     */
    public void addChild(VJoint newChild)
    {
        if (newChild == null)
        {
            logger.warn("VJoint id=\"" + id + "\" : addPart(null)");
            return;
        }
        children.add(newChild);
        newChild.setParent(this);
    }

    /**
     * Removes child part, if present.
     */
    public void removeChild(VJoint vo)
    {
        children.remove(vo);
        vo.setParent(null);
    }

    /**
     * Returns the List of child VJoints
     */
    public List<VJoint> getChildren()
    {
        return children;
    }

    /**
     * Returns the result of a depth first search for some part identified by either id, sid, or name.
     */
    public VJoint getPart(String partIdent)
    {
        if (partIdent == null) {
            return null;
        }
        return searchPart(partIdent.intern());
    }

    /* performs the depth first search, for an interned, non-null String */
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "ES_COMPARING_PARAMETER_STRING_WITH_EQ", justification = "String is interned")
    private VJoint searchPart(String searchId)
    {
        if (searchId == this.id || searchId == this.sid || searchId == this.name) {
            return this;
        }
        for (VJoint vchild : children)
        {
            VJoint childPart = vchild.searchPart(searchId);
            if (childPart != null) {
                return childPart;
            }
        }
        return null;
    }

    /**
     * Returns the result of a depth first search for some part identified by either id.
     */
    public VJoint getPartById(String partId)
    {
        if (partId == null) {
            return null;
        }
        return searchPartById(partId.intern());
    }

    /* performs the depth first search, for an interned, non-null String */
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "ES_COMPARING_PARAMETER_STRING_WITH_EQ", justification = "String is interned")
    private VJoint searchPartById(String searchId)
    {
        if (searchId == this.id) {
            return this;
        }
        for (VJoint vchild : children)
        {
            VJoint childPart = vchild.searchPartById(searchId);
            if (childPart != null) {
                return childPart;
            }
        }
        return null;
    }

    /**
     * Returns the result of a breadth first search for some part identified by sid.
     */
    public VJoint getPartBySid(String partSid)
    {
        if (partSid == null) {
            return null;
        }
        return searchPartBySid(partSid.intern());

    }

    /* performs the breadth first search, for an interned, non-null String */
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "ES_COMPARING_PARAMETER_STRING_WITH_EQ", justification = "String is interned")
    private VJoint searchPartBySid(String searchSid)
    {
        if (this.sid == searchSid) {
            return this;
        }
        for (VJoint vchild : children)
        {
            if (vchild.sid == searchSid) {
                return vchild;
            }
        }
        for (VJoint vchild : children)
        {
            VJoint childPart = vchild.searchPartBySid(searchSid);
            if (childPart != null) {
                return childPart;
            }
        }
        return null;
    }

    /**
     * Returns a List of VJoints with an Id that occurs in the specified idList,
     * by searching recursively in the VJoint tree with this VJoint as root.
     * It is not guaranteed that sids from the list do have a matching VJoint;
     * such elements are represented by null elements in the VJoint list.
     */
    public List<VJoint> getPartsByIds(List<String> idList) 
    {
        ArrayList<VJoint> result = new ArrayList<>(idList.size());
        for (String s : idList) 
        {
           VJoint jnt = getPartById(s);
           result.add(jnt);
        }
        return result;
    }


    /**
     * Returns a List of VJoints with an sid that occurs in the specified sidList,
     * by searching recursively in the VJoint tree with this VJoint as root.
     * It is not guaranteed that sids from the list do have a matching VJoint;
     * such elements are represented by null elements in the VJoint list.
     */
    public List<VJoint> getPartsBySids(List<String> sidList) 
    {
        ArrayList<VJoint> result = new ArrayList<>(sidList.size());
        for (String s : sidList) 
        {
           VJoint jnt = getPartBySid(s);
           result.add(jnt);
        }
        return result;
    }
    
 
    /**
     * Returns a List of VJoints with an id or sid or name that occurs in the specified List,
     * by searching recursively in the VJoint tree with this VJoint as root.
     * It is not guaranteed that sids from the list do have a matching VJoint;
     * such elements are represented by null elements in the VJoint list.
     * It is allowed to use a null argument; in this case the call
     * is equivalent to getParts(), i.e. all reachable VJoints will be included.
     */
    public List<VJoint> getParts(List<String> partIdentList) 
    {
        if (partIdentList == null) {
            return getParts();
        } // Return all reachable parts
        ArrayList<VJoint> result = new ArrayList<>(partIdentList.size());
        for (String s : partIdentList) 
        {
           VJoint jnt = getPart(s);
           if (jnt == null) {
                logger.warn("VJoint.getParts, Could not find " + s);
            }
           result.add(jnt);
        }
        return result;
    }
    


    public List<VJoint> getParts()
    {
        return getParts(null, null);
    }

    public List<VJoint> getParts(VObject.Predicate select)
    {
        return getParts(select, null);
    }

    public List<VJoint> getParts(VObject.Predicate select, VObject.Predicate prune)
    {
        return getParts(select, prune, new ArrayList<VJoint>());

    }

    public List<VJoint> getParts(VObject.Predicate select, VObject.Predicate prune, ArrayList<VJoint> list)
    {
        if (select == null || select.valid(this)) {
            list.add(this);
        }
        if (prune == null || !prune.valid(this))
        {
            for (VJoint vchild : children)
            {
                vchild.getParts(select, prune, list);
            }
        }
        return list;
    }

    /*
     * Prepends the path from the specified root node to this VJoint, in the form of a List of VObjects. If root is null, the path from the root of
     * the scenegraph to this VJoint is created. the path parameter should not be null.
     */
    private List<VJoint> getVObjectPath(VJoint root, List<VJoint> path)
    {
        path.add(0, this);
        if (root != this && parent != null)
        {
            return parent.getVObjectPath(root, path);
        }
        return path;
    }

    /**
     * Clones the VJoint, creates a new VJoint with the id, sid, name, translation and rotation Use @see{cloneTree} to recursively clone the VJoint
     * and all its children
     */
    public VJoint copy(String newId)
    {
        VJoint copy = new VJoint();
        copy.setId(newId);
        copy.setSid(sid);
        copy.setName(name);
        copy.setTranslation(translation);
        copy.setRotation(rotation);
        // ...
        return copy;
    }

    /**
     * Copies a VJoint tree, copies sid, name, translation and rotation of the clone, and generates copies for all the children of this VJoint The ids
     * in the copy are prefixed with idPrefix
     * 
     * @return the copy
     */
    public VJoint copyTree(String idPrefix)
    {
        VJoint copy = copy(idPrefix + id);
        for (VJoint vchild : children)
        {
            VJoint childClone = vchild.copyTree(idPrefix);
            copy.addChild(childClone);
        }
        return copy;
    }

    /**
     * Make a master copy. A master copy is one that controls the rotation and translation 
     * of the original vjoint from which it was copied. The ids in the copy are those in the 
     * original, prefixed with idPrefix. Use @see{masterCopyTree} to recursively clone the 
     * VJoint and all its children.
     */
    public VJoint masterCopy(String idPrefix)
    {
        VJoint master = copy(idPrefix);
        setTranslationBuffer(master.getTranslationBuffer());
        setRotationBuffer(master.getRotationBuffer());
        return master;
    }
    /** set the given VJoint to be the master of this one (see also masterCopy) */
    public void setMaster(VJoint master)
    {
        setTranslationBuffer(master.getTranslationBuffer());
        setRotationBuffer(master.getRotationBuffer());
    }
    /**
     * master-copies a VJoint tree, copies the id, sid, name, translation and rotation of 
     * the clone, and generates master copies for all the children of this VJoint
     * 
     * @return the clone
     */
    public VJoint masterCopyTree(String idPrefix)
    {
        VJoint master = masterCopy(idPrefix + id);
        for (VJoint vchild : children)
        {
            VJoint childClone = vchild.masterCopyTree(idPrefix);
            master.addChild(childClone);
        }
        return master;
    }

    /**
     * make a slave copy. a slave copy is one that, just before executing calculatematrices, 
     * grabs translation and rotation from the original vjoint from which it was cloned. 
     * 
     * @see{slaveCopyTree to recursively copy the VJoint and all its children
     */
    public VJoint slaveCopy(String newId)
    {
        VJoint slave = new VJoint();
        slave.id = newId;
        slave.sid = sid;
        slave.name = name;
        slave.setTranslationBuffer(getTranslationBuffer());
        slave.setRotationBuffer(getRotationBuffer());
        // ...
        return slave;
    }

    /**
     * Slave-Clones a VJoint tree, copies the id, sid, name, translation and rotation of 
     * the clone, and generates slave clones for all the children of this VJoint
     * 
     * @return the clone
     */
    public VJoint slaveCopyTree(String idPrefix)
    {
        VJoint slave = slaveCopy(idPrefix + id);
        for (VJoint vchild : children)
        {
            VJoint childClone = vchild.slaveCopyTree(idPrefix);
            slave.addChild(childClone);
        }
        return slave;
    }

    /**
     * Returns the path from the specified root node to this VJoint in the form of 
     * a List of VObjects. If the root is null, or if the specified root
     * node is not an ancestor of this VJoint, the path starts at the root of the scenegraph.
     */
    public List<VJoint> getVObjectPath(VJoint root)
    {
        return getVObjectPath(root, new ArrayList<VJoint>(8));
    }

    /************************** 3D part *************************************/
    /* A VJoint has: */
    /* A local translation vector(tx, ty, tz) */
    /* A local rotation quaternion (qs, qx, qy, qz) */
    /* A rotation center vector (cx, cy, cz) */
    /*                                                                      */
    /* A local transform matrix localMatrix */
    /* A global transform matrix globalMatrix */
    /*                                                                      */
    /* The translation, rotation, scale, and rotation center determine */
    /* the current transform; the local transform matrix can be */
    /* calculated from these. */
    /* Updates to translation, rotation, or scale do not cause direct */
    /* recomputation of the local matrix; rather a flag is set that */
    /* causes recomputation whenever the local matrix is needed, for */
    /* instance, when recalculating the global transform matrix. */
    /* It is possible to get direct links to the rotation and translation */
    /* buffer arrays, via the getTranslationBuffer and getRotationBuffer */
    /* methods. It is allowed to write into these buffers; as a consequence */
    /* the local matrix will be recalculated every time is is needed, even */
    /* when no explicit setRotation or setTranslation was performed. */
    /* Thread synchronization is of concern for all access methods: */
    /* VJoint methods are not thread-safe. */
    /************************************************************************/

    /**
     * Returns the current translation vector in the form of a float array of length 3
     */
    @Override
    public void getTranslation(float[] t)
    {
        System.arraycopy(translation, 0, t, 0, 3);
    }

    /**
     * Returns the current translation vector in the form of a float array of length 3
     */
    @Override
    public void getTranslation(float[] vc, int vcIndex)
    {
        System.arraycopy(translation, 0, vc, vcIndex, 3);
    }

    /**
     * Returns the current translation buffer, in the form of a float array of length three. The buffer contains a translation vector, used to
     * calculate the transformation matrix for this VJoint.
     */
    // @Override
    public float[] getTranslationBuffer()
    {
        hasSharedBuffers = true;
        return translation;
    }

    /**
     * Sets the translation buffer. Should be taken from another VJoint.translation
     */
    public void setTranslationBuffer(float[] ta)
    {
        translation = ta;
        hasSharedBuffers = true;
        validLocalMatrix = false;
    }

    /**
     * Sets the current translation vector from a float array, which should have length 3.
     */
    @Override
    public void setTranslation(float[] ta)
    {
        System.arraycopy(ta, 0, translation, 0, 3);
        validLocalMatrix = false;
    }

    /**
     * Sets the current translation vector by copying three floats from float array ta, starting at the specified index.
     */
    @Override
    public void setTranslation(float[] ta, int taIndex)
    {
        System.arraycopy(ta, taIndex, translation, 0, 3);
        validLocalMatrix = false;
    }

    /**
     * Sets the current translation vector from three floats.
     */
    @Override
    public void setTranslation(float tx, float ty, float tz)
    {
        translation[0] = tx;
        translation[1] = ty;
        translation[2] = tz;
        validLocalMatrix = false;
    }

    public void clearTranslation()
    {
        translation[0] = 0f;
        translation[1] = 0f;
        translation[2] = 0f;
        validLocalMatrix = false;
    }

    /**
     * Adds the specified translation vector to the current translation
     */
    public void translate(float[] tvec)
    {
        Vec3f.add(translation, tvec);
        validLocalMatrix = false;
    }

    // /**
    // * Copies the translation from a specified source VJoint to
    // * the translation of this VJoint
    // */
    // public void copyTranslation(VJoint src) {
    // System.arraycopy(src.translation, 0, this.translation, 0, 3);
    // }

    public boolean hasTranslation()
    {
        return (translation[0] != 0.0f || translation[1] != 0.0f || translation[2] != 0.0f);
    }

    /**
     * Sets the current rotation quaternion from a float array, which should have length 4. The order is (s, x, y, z), where s is the real part, x, y,
     * z are the imaginary parts.
     */
    @Override
    public void setRotation(float[] ra)
    {
        System.arraycopy(ra, 0, rotation, 0, 4);
        validLocalMatrix = false;
    }

    /**
     * Sets the rotation to q, in the coordinate system of rootJoint
     */
    public void setPathRotation(float q[], VJoint rootJoint)
    {
        if (rootJoint == this)
        {
            setRotation(q);
        }
        else
        {
            Quat4f.set(qw, q);
            VJoint par = getParent();
            par.getPathRotation(rootJoint, qp);
            Quat4f.inverse(qp);
            Quat4f.mul(q2, qp, qw);
            setRotation(q2);
        }
    }

    /**
     * Sets the current rotation quaternion by copying four floats from float array ra, starting at the specified index.
     */
    @Override
    public void setRotation(float[] ra, int raIndex)
    {
        System.arraycopy(ra, raIndex, rotation, 0, 4);
        validLocalMatrix = false;
    }

    /**
     * Returns the current rotation quaternion in a float array of length four. The order is (s, x, y, z), where s is the scalar part, x, y, z are the
     * imaginary parts.
     */
    @Override
    public void getRotation(float[] r)
    {
        System.arraycopy(rotation, 0, r, 0, 4);
    }

    /**
     * Returns the current rotation quaternion in a float array of length four. The order is (s, x, y, z), where s is the scalar part, x, y, z are the
     * imaginary parts.
     */
    @Override
    public void getRotation(float r[], int index)
    {
        System.arraycopy(rotation, 0, r, index, 4);
    }

    public void getRotation(double r[], int index)
    {
        for (int i = 0; i < 4; i++)
        {
            r[index + i] = rotation[i];
        }
    }

    public void getTranslation(double[] t)
    {
        for (int i = 0; i < 3; i++)
        {
            t[i] = translation[i];
        }        
    }
    
    public void getTranslation(double[] t,int index)
    {
        for (int i = 0; i < 3; i++)
        {
            t[index+i] = translation[i];
        }        
    }

    /**
     * returns an array with references to the rotation buffers for
     * the specified parts. 
     * The returned array will have the same length as the partSids array.  
     * Parts are located with this VJoint considered a &quot;root&quot;.
     * If certain specified parts could not be located, the result will contain null elements.
     * The parts should preferably be specified by means of their sid, 
     * but id or name will also work.
     */
    public float[][] getRotationBuffers(String[] partIdents) {
        if (partIdents == null) {
            return null;
        }
        float[][] result = new float[partIdents.length][];
        for (int i=0; i<partIdents.length; i++) {
            VJoint part = getPart(partIdents[i]);
            if (part != null) {
                result[i] = part.getRotationBuffer();
            }
        }     
        return result;
    }
    
    /**
     * Returns the current rotation buffer, in the form of a float array of length four. The buffer contains a rotation quaternion, used to calculate
     * the rotation matrix for this VJoint. The order of the quaternion elements is (s, x, y, z), where s is the scalar part, and x, y, z are the
     * imaginary parts.
     */
    // @Override
    public float[] getRotationBuffer()
    {
        hasSharedBuffers = true;
        return rotation;
    }

    /**
     * Sets the rotation buffer. Should be taken from another VJoint.rotation
     */
    public void setRotationBuffer(float[] rot)
    {
        rotation = rot;
        hasSharedBuffers = true;
        validLocalMatrix = false;
    }

    /**
     * Sets the current rotation quaternion from four floats. qs is the real part, qx, qy, qz the imaginary parts.
     */
    @Override
    public void setRotation(float qs, float qx, float qy, float qz)
    {
        Quat4f.set(rotation, qs, qx, qy, qz);
        validLocalMatrix = false;
    }

    public void clearRotation()
    {
        Quat4f.set(rotation, 1f, 0f, 0f, 0f);
        validLocalMatrix = false;
    }

    // /**
    // * Copies the rotation quaternion from a specified source VJoint to
    // * the rotation of this VJoint
    // */
    // public void copyRotation(VJoint src) {
    // System.arraycopy(src.rotation, 0, this.rotation, 0, 4);
    // }

    /**
     * Sets the current rotation quaternion derived from a rotation <em>axis</em> (ax, ay, az) and a rotation <em>angle</em> angle, specified in
     * radians. The axis need not have length one. If all parameters are zero, the rotation is set to the identity quaternion (1,0,0,0).
     */
    @Override
    public void setAxisAngle(float ax, float ay, float az, float angle)
    {
        Quat4f.setFromAxisAngle4f(rotation, ax, ay, az, angle);
        validLocalMatrix = false;
    }

    /**
     * Sets the rotation from roll-pitch-yaw angles, specified in degrees(!)
     */
    public void setRollPitchYawDegrees(float roll, float pitch, float yaw)
    {
        Quat4f.setFromRollPitchYawDegrees(rotation, roll, pitch, yaw);
        validLocalMatrix = false;
    }

    /**
     * pre- multiplies the current rotation with the specified rotation:
     * rotation = rq * rotation
     */
    public void rotate(float[] rq)
    {
        Quat4f.mul(rotation, rq, rotation);
        validLocalMatrix = false;
    }

     /**
     * post- multiplies the current rotation with the specified rotation:
     * rotation =  rotation * rq
     */
    public void insertRotation(float[] rq)
    {
        Quat4f.mul(rotation, rotation, rq);
        validLocalMatrix = false;
    }

    /**
     * pre- multiplies the current rotation with the specified rotation, specified by axis (ax, ay, az) and an angle in radians.
     */
    public void rotateAxisAngle(float ax, float ay, float az, float angle)
    {
        rotate(Quat4f.getQuat4fFromAxisAngle(ax, ay, az, angle));
    }

    public boolean hasRotation()
    {
        return (rotation[0] != 1.0f);
    }

    /**
     * Rotates the scenegraph, starting at this VJoint, around the origin. (N.B> the nornmale rotate/setRotation methods use the joint center as the
     * center of rotation. This method rotates around the origin. So, it is NOT just the joint rotation that is affected, but also the translation
     * vector. The net effect is that the translation vector is rotated with the specified rotation, and that the existing rotation is premultiplied
     * with the specified rotation.
     */
    public void rotateJoint(float[] rq)
    {
        Quat4f.mul(rotation, rq, rotation);
        Quat4f.transformVec3f(rq, translation);
        validLocalMatrix = false;
    }

    /**
     * Rotates the scenegraph, starting at this VJoint, around the origin. (N.B> the nornmale rotate/setRotation methods use the joint center as the
     * center of rotation. This method rotates around the origin. So, it is NOT just the joint rotation that is affected, but also the translation
     * vector. The net effect is that the translation vector is rotated with the specified rotation, and that the existing rotation is premultiplied
     * with the specified rotation.
     */
    public void rotateScaleJoint(float[] rq, float scale)
    {
        Quat4f.transformVec3f(rq, translation);
        Vec3f.scale(scale, translation);
        validLocalMatrix = false;
    }

    /**
     * ma: 4x4 affine matrix of form T(tA) o U replace local transform L by ma o L o ma-inv replace translation t by U(t)
     */
    public void affineTransform(float[] ma)
    {
        getLocalMatrix();
        Mat4f.transformAffineMatrix(ma, localMatrix);
        decomposeLocalMatrix();
    }

    /**
     * Returns true when the local transform is a rigid transform, i.e. includes no scaling
     */
    public boolean isRigid()
    {
        return scalingType == Mat3f.ScalingType.IDENTITY;
    }

    /**
     * Sets the current scale vector from a float array sa, which should have length 3. If it is null, or if all three scale factors are 1.0, then the
     * local scale is set to null, and no scaling is applied.
     */
    public void setScale(float s)
    {
        setScale(s, s, s);
    }

    /**
     * Sets the current scale vector from a float array sa, which should have length 3. If it is null, or if all three scale factors are 1.0, then the
     * local scale is set to null, and no scaling is applied.
     */
    public void setScale(float sx, float sy, float sz)
    {
        if (sx == 1.0f && sy == 1.0f && sz == 1.0f)
        {
            scalingType = Mat3f.ScalingType.IDENTITY;
            scaleVec = null;
            scaleMatrix = null;
        }
        else
        {
            if (scaleVec == null)
            {
                scaleVec = new float[] { sx, sy, sz };
            }
            else
            {
                scaleVec[0] = sx;
                scaleVec[1] = sy;
                scaleVec[2] = sz;
            }
            scalingType = Mat3f.ScalingType.ALIGNED;
            scaleMatrix = null;
        }
        validLocalMatrix = false;
    }

    public void scale(float sx, float sy, float sz)
    {
        if (sx == 1.0f && sy == 1.0f && sz == 1.0f) {
            return;
        }
        switch (scalingType)
        {
        case IDENTITY:
            setScale(sx, sy, sz);
            break;
        case ALIGNED:
            scaleVec[0] *= sx;
            scaleVec[1] *= sy;
            scaleVec[2] *= sz;
            break;
        case SKEW:
            scaleMatrix[Mat3f.M00] *= sx;
            scaleMatrix[Mat3f.M11] *= sy;
            scaleMatrix[Mat3f.M22] *= sz;
            break;
        default:
            logger.error("VJoint.getLocalMatrix: unknown scaling type: " + scalingType);
        }
    }

    public void scale(float s)
    {
        scale(s, s, s);
    }

    public void clearScale()
    {
        scaleVec = null;
        scaleMatrix = null;
        scalingType = Mat3f.ScalingType.IDENTITY;
        validLocalMatrix = false;
    }

    /**
     * Sets the current scale vector from a float array sa, which should have length 3. If it is null, or if all three scale factors are 1.0, then the
     * local scale is set to null, and no scaling is applied.
     */
    @Override
    public void setScale(float[] sa)
    {
        setScale(sa[0], sa[1], sa[2]);
    }

    /**
     * Sets the current scale vector by copying three floats from float array sa, starting at the specified index.
     */
    @Override
    public void setScale(float[] sa, int saIndex)
    {
        setScale(sa[saIndex], sa[saIndex + 1], sa[saIndex + 2]);
    }

    /**
     * Returns the current scale vector in a float array of length three.
     */
    @Override
    public void getScale(float[] r)
    {
        getScale(r, 0);
    }

    /**
     * Returns the current scale vector.
     */
    @Override
    public void getScale(float r[], int index)
    {
        if (scaleVec != null)
        {
            System.arraycopy(scaleVec, 0, r, index, 3);
        }
        else
        {
            r[index] = r[index + 1] = r[index + 2] = 1.0f;
        }
    }

    /**
     * Sets the skewing matrix, in the form of a Mat3f matrix.
     */
    public void setSkewMatrix(float[] matrix)
    {
        this.scaleMatrix = matrix;
        scaleVec = null;
        scalingType = Mat3f.ScalingType.SKEW;
        validLocalMatrix = false;
    }

    /**
     * Returns the current skewing matrix, in the form of a Mat3f array.
     */
    public float[] getSkewMatrix()
    {
        return scaleMatrix;
    }

    public boolean hasScaling()
    {
        return (scalingType != Mat3f.ScalingType.IDENTITY);
    }

    /**
     * Returns (a reference to) the current local 4X4 transform matrix. When necessary, this will (re)calculate the local matrix from the current
     * rotation quaternion, translation, and scaling. M = T o R o S Direct modification of the local matrix via this reference should not occur.
     */
    public final float[] getLocalMatrix()
    {
        if (!validLocalMatrix || hasSharedBuffers)
        {
            Quat4f.normalize(rotation);
            switch (scalingType)
            {
            case IDENTITY:
                Mat4f.setFromTR(localMatrix, translation, rotation);
                break;
            case ALIGNED:
                Mat4f.setFromTRSVec3f(localMatrix, translation, rotation, scaleVec);
                break;
            case SKEW:
                Mat4f.setFromTRSMat3f(localMatrix, translation, rotation, scaleMatrix);
                break;
            default:
                logger.error("VJoint.getLocalMatrix: unknown scaling type: " + scalingType);
            }
            validLocalMatrix = true;
        }
        return localMatrix;
    }

    /**
     * Sets the local 4X4 transform matrix, and decomposes it into a rotation, translation, and scaling. Note: this is NOT the preferred way for
     * setting the transform: it is slow, due to the polar decomposition, needed to extract the rotation and scaling. The preferred way is to set
     * rotation, translation, and scaling directly, by means of methods like setRotation(), setTranslation(), and setScale().
     */
    public void setLocalTransform(float[] matrix)
    {
        Mat4f.set(localMatrix, matrix);
        decomposeLocalMatrix();
    }

    public void setLocalTransform(float[] matrix, int i)
    {
        Mat4f.set(localMatrix, 0, matrix, i);
        decomposeLocalMatrix();
    }
    
    /*
     * decomposes the localMatrix into quaternion, scaling, translation
     */
    private void decomposeLocalMatrix()
    {
        validLocalMatrix = true;
        if (!Mat4f.isAffine(localMatrix))
        {
            scalingType = Mat3f.ScalingType.SKEW;
            // a projection matrix: don't decompose this.
            return;
        }

        // if affine, decompose it into rotation, translation, scaling
        if (scaleMatrix == null) {
            scaleMatrix = new float[9];
        }
        Mat4f.decomposeToTRSMat3f(localMatrix, translation, rotation, scaleMatrix);
        float epsilon = 0.0001f; // determines smoothing for the scaling
        Mat3f.smooth(scaleMatrix, epsilon);
        scalingType = Mat3f.getScalingType(scaleMatrix);
        if (scalingType != Mat3f.ScalingType.SKEW)
        {
            if (scaleVec == null) {
                scaleVec = new float[3];
            }
            Mat3f.getDiagonal(scaleMatrix, scaleVec);
            scaleMatrix = null; // dispose scaleMatrix if not needed.
        }
    }

    /**
     * Sets the local affine transform to identity. That is, no rotation, no scaling, no translation remains
     */
    public void clearLocalAffineTransform()
    {
        clearScale();
        clearRotation();
        clearTranslation();
        getLocalMatrix();
    }

    /**
     * Sets the local linear transform to identity. That is, no rotation, no scaling remains, but the translation part is not affected.
     */
    public void clearLocalLinearTransform()
    {
        clearScale();
        clearRotation();
        getLocalMatrix();
    }

    /**
     * Sets the rotation quaternion, translation vector, scaling, as well as the local transform matrix, from the specified 4 X 4 matrix, in the form
     * of a Mat4f float array. (I.e. a 4 X 4 matrix, stored in a float array in row-major order).
     */
    public final void setLocalMatrix(float[] matrix4f)
    {
        Mat4f.set(localMatrix, matrix4f);
    }

    /**
     * Returns a reference to the global transform matrix.
     */
    public final float[] getGlobalMatrix()
    {
        return globalMatrix;
    }

    public String localMatrixToString()
    {
        return Mat4f.toString(getLocalMatrix());
    }

    public String globalMatrixToString()
    {
        return Mat4f.toString(globalMatrix);
    }

    /**
     * Equivalent to calculateMatrices(Matf4.ID)
     */
    public void calculateMatrices()
    {
        calculateMatrices(Mat4f.ID);
    }

    /**
     * Performs a recursive tree walk over the scene graph with this VJoint as root, calculating the global and local matrices for all visited
     * VObjects, automatically setting all local matrices The calculation per VJoint is: globalMatrix' = parent.globalMatrix * localMatrix. For this
     * (root) VJoint, the &quot;parent's&quot; global matrix is specified by means of the parentGlobalMatrix parameter.
     */
    public void calculateMatrices(float[] parentGlobalMatrix)
    {
        Mat4f.mul(globalMatrix, parentGlobalMatrix, getLocalMatrix());

        for (VJoint vchild : children)
        {
            vchild.calculateMatrices(globalMatrix);
        }
    }

    /**
     * Calculates the transform matrix for the scene graph path starting at the specified root VJoint, and ending in this VJoint. The resulting matrix
     * is stored in m. If rootObject == null, m will be set to the transform matrix for the complete scenegraph path up (but not including) to the
     * world root.
     */
    public void getPathTransformMatrix(VJoint rootObject, float[] m)
    {
        if (rootObject == this)
        {
            Mat4f.set(m, getLocalMatrix());
        }
        else if (parent != null)
        {
            parent.getPathTransformMatrix(rootObject, m);
            Mat4f.mul(m, m, getLocalMatrix());
        }
        else
        {
            if (rootObject != null)
            {
                throw new IllegalArgumentException("Root joint not found " + rootObject.sid);
            }
            else
            {
                Mat4f.set(m, getLocalMatrix());
            }
        }
    }

    /**
     * Calculates the "path" transform on point "pt", where all local transforms along a scene graph path are applied. The specified rootJoint acts as
     * the frame of reference, and local transfomation matrices from descendant from this reference joint up to and including this VJoint are applied
     * to the specified point pt, which should be a vector of length three. Note that the local transform of the root joint is <em>not</em> applied,
     * so, if the root joint is actually the parent of this VJoint, only the local transform is applied. When the root joint happens to be this VJoint
     * itself, pt will not be transformed at all. It is allowed to specify a null rootJoint; in this case all local tranforms, starting AFTER the
     * world root of the scene graph up to and including this VJoint, are applied.
     */
    public void pathTransform(VJoint rootJoint, float[] pt)
    {
        if (rootJoint == this) {
            return;
        } // identity transform on pt.
        Mat4f.transformPoint(getLocalMatrix(), pt);
        if (parent != null)
        {
            parent.pathTransform(rootJoint, pt); // recursive transform.
        }
        else
        {
            if (rootJoint != null)
            {
                throw new IllegalArgumentException("Root joint not found " + rootJoint.sid);
            }
        }
    }

    
    private boolean getPath(VJoint target, List<VJoint> path)
    {
        for (VJoint vj : children)
        {
            if (vj == target)
            {
                path.add(vj);
                return true;
            }
            else
            {
                if (vj.getPath(target, path))
                {
                    path.add(vj);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Gets the VJoint path from target to this joint, including target and this joint
     */
    public List<VJoint> getPath(VJoint target)
    {
        List<VJoint> path = new ArrayList<>();
        getPath(target, path);
        path.add(this);
        return path;
    }

    /**
     * Calculates the "path" rotation, for the scene graph path starting at the specified root VJoint, and ending in this VJoint. The resulting
     * quaternion is stored in quat. If rootJoint == null the complete scenegraph path starting at (but not including) the world root will be used.
     */
    public void getPathRotation(VJoint rootJoint, float[] quat)
    {
        if (rootJoint == this)
        {
            Quat4f.setIdentity(quat);
        }
        else if (parent != null)
        {
            parent.getPathRotation(rootJoint, quat);
            if (rotation[Quat4f.s] < 1.0) {
                Quat4f.mul(quat, rotation);
            } // avoid multiplication if local
                                            // rotation is Id transform.
        }
        else
        {
            if (rootJoint != null)
            {
                throw new IllegalArgumentException("Root joint not found " + rootJoint.sid);
            }
            else
            {
                Quat4f.setIdentity(quat);
            }
        }
    }
    
    /**
     * Calculates the "path" rotation, for the scene graph path starting at the specified root VJoint, and ending in this VJoint. The resulting
     * quaternion is stored in quat. If rootJoint == null the complete scenegraph path starting at AND INCLUDING the world root will be used.
     */
    public void getFullPathRotation(VJoint rootJoint, float[] quat)
    {
        if (rootJoint == this)
        {
            Quat4f.setIdentity(quat);
        }
        else if (parent != null)
        {
            parent.getFullPathRotation(rootJoint, quat);
            if (rotation[Quat4f.s] < 1.0) {
                Quat4f.mul(quat, rotation);
            } // avoid multiplication if local
                                            // rotation is Id transform.
        }
        else
        {
            if (rootJoint != null)
            {
                throw new IllegalArgumentException("Root joint not found " + rootJoint.sid);
            }
            else
            {
                Quat4f.set(quat,rotation);
            }
        }
    }

    /**
     * Calculates the "path" translation, for the scene graph path starting at the specified root VJoint, and ending in this VJoint. The resulting
     * vector is stored in vec, which should be a length three float array. If rootJoint == null the complete scenegraph path will be used.
     */
    public void getPathTranslation(VJoint rootJoint, float[] vec)
    {
        Vec3f.set(vec, 0f, 0f, 0f);
        pathTransform(rootJoint, vec);
    }

    /**
     * returns the Vec3f position of this VJoint.
     */
    public float[] getPosition()
    {
        return getPosition(new float[3]);
    }

    /**
     * returns the Vec3f position of this VJoint When the specified positionVec is non-null, this Vec3f is used and returned, otherwise a new Vec3f is
     * allocated.
     */
    public float[] getPosition(float[] positionVec)
    {
        if (positionVec == null) {
            positionVec = new float[3];
        }
        Vec3f.set(positionVec, 0f, 0f, 0f);
        pathTransform(null, positionVec);
        return positionVec;
    }

    /**
     * returns a Vec3f array with the position of this VJoint relative to the specified ancestor joint.
     */
    public float[] getRelativePositionFrom(VJoint ancestorJoint)
    {
        float[] relPos = getPosition();
        float[] ancestorPos = ancestorJoint.getPosition(null);
        Vec3f.sub(relPos, ancestorPos);
        return relPos;
    }

  

    /**
     * Unsupported VObject methods:
     * 
     * /** Returns the current velocity vector in the form of a float array of length 3
     */
    @Override
    public void getVelocity(float[] v)
    {
        throw new UnsupportedOperationException("getVelocity not suported for VJoints");
    }

    /**
     * Returns the current velocity vector in the form of a float array of length 3
     */
    @Override
    public void getVelocity(float[] vc, int vcIndex)
    {
        throw new UnsupportedOperationException("getVelocity not suported for VJoints");
    }

    /**
     * Returns the current velocity buffer, in the form of a float array of length three. The buffer contains a translation vector, used to calculate
     * the transformation matrix for this VJoint.
     */
    // public float[] getVelocityBuffer() ;

    /**
     * Sets the current velocity vector from a float array, which should have length 3.
     */
    @Override
    public void setVelocity(float[] v)
    {
        throw new UnsupportedOperationException("getVelocity not suported for VJoints");
    }

    /**
     * Sets the current velocity vector by copying three floats from float array va, starting at the specified index.
     */
    @Override
    public void setVelocity(float[] vc, int vcIndex)
    {
        throw new UnsupportedOperationException("setVelocity not suported for VJoints");
    }

    /**
     * Sets the current velocity vector from three floats.
     */
    @Override
    public void setVelocity(float vx, float vy, float vz)
    {
        throw new UnsupportedOperationException("setVelocity not suported for VJoints");
    }

    /**
     * Returns the current velocity vector in the form of a float array of length 3
     */
    @Override
    public void getAngularVelocity(float[] v)
    {
        throw new UnsupportedOperationException("getAngularVelocity not suported for VJoints");
    }

    /**
     * Returns the current velocity vector in the form of a float array of length 3
     */
    @Override
    public void getAngularVelocity(float[] vc, int vcIndex)
    {
        throw new UnsupportedOperationException("getAngularVelocity not suported for VJoints");
    }


    /**
     * Sets the current velocity vector from a float array, which should have length 3.
     */
    @Override
    public void setAngularVelocity(float[] v)
    {
        throw new UnsupportedOperationException("setAngularVelocity not suported for VJoints");
    }

    /**
     * Sets the current velocity vector by copying three floats from float array va, starting at the specified index.
     */
    @Override
    public void setAngularVelocity(float[] vc, int vcIndex)
    {
        throw new UnsupportedOperationException("setAngularVelocity not suported for VJoints");
    }

    /**
     * Sets the current angular velocity vector from three floats.
     */
    @Override
    public void setAngularVelocity(float wx, float wy, float wz)
    {
        throw new UnsupportedOperationException("setAngularVelocity not suported for VJoints");
    }

    private String idts(String s)
    {
        return (s == null) ? "null" : s;
    }

    private void newLine(StringBuilder buf, int tab)
    {
        buf.append('\n');
        for (int i = 0; i < tab; i++) {
            buf.append(' ');
        }
    }


    public StringBuilder appendTo(StringBuilder buf, int tab)
    {
       buf.append( "VJoint[  id=\"");
       buf.append( idts(id));
       buf.append("\"  sid=\"");
       buf.append(idts(sid));
       buf.append("\"  name=\"");
       buf.append(idts(name));
       buf.append("\"]\n");
       for (int i=0; i<tab; i++) {
            buf.append(' ');
        }
       buf.append("translation=");
       buf.append(Vec3f.toString(translation));
       buf.append('\n');
       for (int i=0; i<tab; i++) {
            buf.append(' ');
        }
       buf.append("rotation=");
       buf.append(Quat4f.toString(rotation));
       if (scaleVec != null) {
          buf.append('\n');
          for (int i=0; i<tab; i++) {
               buf.append(' ');
           }
          buf.append("scalevec=");
          buf.append(Vec3f.toString(scaleVec));
          
       }
       
                // + (inverseBindMatrix== null ? "" :
                // "\n  inverseBindMatrix="+Mat4f.toString(inverseBindMatrix))
       return buf;
      
    }

    @Override
    public String toString()
    {
        return appendTo(new StringBuilder(), 0).toString();
    }

    public String showSkeleton()
    {
        return showSkeleton(0, 10000);
    }

    public String showSkeleton(int detail, int level)
    {
        StringBuilder buf = appendSkeleton(new StringBuilder(), 0, detail, level);
        return buf.toString();
    }

    public static final int TAB = 6;

    /**
     * Appends a textual representation of the skeleton/scenegraph to buf, indentation tab,
     * level of detail specified (0, 1, or 2), and max level of the tree shown
     */
    public StringBuilder appendSkeleton(StringBuilder buf, int tab, int detail, int level )
    {
        
        newLine(buf, tab);
        buf.append("VJoint[");
        buf.append("  id=\"").append(idts(id));
        buf.append("\"  sid=\"").append(idts(sid));
        buf.append("\"  name=\"").append(idts(name));
        buf.append('\"');
        if (detail >= 1)
        {
            newLine(buf, tab);
            buf.append("translation=").append(Vec3f.toString(translation));
            newLine(buf, tab);
            buf.append("rotation=").append(Quat4f.toString(rotation));
            newLine(buf, tab);
            buf.append((scaleVec == null ? "no scaling" : "scaleVec=" + Vec3f.toString(scaleVec)));
            // newLine(buf, tab);
            // buf.append((inverseBindMatrix== null ? "no inversebindmatrix" :
            // "inverseBindMatrix="+Mat4f.toString(inverseBindMatrix, tab)));
        }
        if (detail >= 2)
        {
            newLine(buf, tab);
            buf.append("localMatrix=").append(Mat4f.toString(localMatrix, tab));
            newLine(buf, tab);
            buf.append("globalMatrix=").append(Mat4f.toString(globalMatrix, tab));
            newLine(buf, tab);
            buf.append(" validLocalMatrix=");
            buf.append(validLocalMatrix);
            newLine(buf, tab);
            buf.append(" hasSharedBuffers=");
            buf.append(hasSharedBuffers);
        }

        if (level > 0) 
        {
           for (VJoint child : children)
           {
               child.appendSkeleton(buf, tab + TAB, detail, level-1 );
           }
        }
        newLine(buf, tab);
        buf.append("]");
        return buf;
    }

    public boolean getHasSharedBuffers()
    {
        return hasSharedBuffers;
    }

   

}
