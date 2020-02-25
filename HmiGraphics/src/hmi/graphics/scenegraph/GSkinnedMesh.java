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

import hmi.animation.VJoint;
import hmi.math.Mat4f;
import hmi.math.Vec3f;
import hmi.math.Vec4f;
import hmi.util.BinUtil;
import hmi.util.BinaryExternalizable;
import hmi.util.Diff;
import hmi.xml.XMLFormatting;
import hmi.xml.XMLTokenizer;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Job Zwiers
 */
@Slf4j
public class GSkinnedMesh extends GMesh implements BinaryExternalizable
{

    private String[] skeletonIds; // used to identify the skeleton GNode root(s). Usually only one is expected, but Collada allows more than one...
    private String[] jointNames;  // user friendly names for the join sids
    private String[] jointSIDs;   // used to resolve the GNodes from the skeleton
    private int[] parentIndex;    // parentIndex[i] = index of the parent joint of joint i, or -1 if joint i is a root joint.

    private VertexWeights vertexWeights;   // vertex weights
    private float[][] inverseBindMatrices; // inverse bind matrices, one for every joint, each matrix containing 16 floats, in row major order.
 
    private GNode[] skeletonRoots; // array with GNodes that are used as skeleton root for this skinned mesh. (typically only one)
    private GNode[] jointNodes;    // links to the skeleton joint nodes, obtaining by resolving the jointSIDs.

    // private float[][] transformMatrices;
    private float[][] jointMatrices;

    private float[][] jointPositions; // Vec3f arrays, that can be used to calculate the current joint positions.
    // private String HAnim_Shoulder_sid, HAnim_Elbow_sid, HAnim_Wrist_sid; // jointSIDs for the HAnim shoulder, elbow, and wrist joints.
    private static Logger logger = LoggerFactory.getLogger(GSkinnedMesh.class.getName());

    /**
     * Default constructor
     */
    public GSkinnedMesh()
    {
        super();
    }

    /**
     * Creates a new GSkinnedMesh, with the data for the underlying GMesh fields obtained from the specified base GMesh parameter. Data arrays from
     * the specified base are shared, not copied.
     */
    public GSkinnedMesh(GMesh base)
    {
        super(base);
        setId("skinned-" + getId());
    }

    /**
     * Creates a new GSkinnedMesh and reads the data from the XMLTokenizer.
     */
    public GSkinnedMesh(XMLTokenizer tokenizer) throws IOException
    {
        this();
        readXML(tokenizer);
    }

    /**
     * show differences
     */
    public String showDiff(Object gmObj)
    {
        GSkinnedMesh gm = (GSkinnedMesh) gmObj;
        if (gm == null)
            return "GSkinnedMesh " + getId() + ", diff: null GSkinnedMesh";
        String diff = Diff.showDiff("GSkinnedMesh, skeletonIds", skeletonIds, gm.skeletonIds);
        if (diff != "")
            return diff;
        diff = Diff.showDiff("GSkinnedMesh " + getId() + ", diff jointNames", jointNames, gm.jointNames);
        if (diff != "")
            return diff;
        diff = Diff.showDiff("GSkinnedMesh " + getId() + ", diff jointSIDs", jointSIDs, gm.jointSIDs);
        if (diff != "")
            return diff;
        diff = Diff.showDiff("GSkinnedMesh " + getId() + ", diff parentIndex", parentIndex, gm.parentIndex);
        if (diff != "")
            return diff;
        diff = Diff.showDiff("GSkinnedMesh " + getId() + ", diff vertexWeights", vertexWeights, gm.vertexWeights);
        if (diff != "")
            return diff;
        diff = Diff.showDiff2("GSkinnedMesh " + getId() + ", diff inverseBindMatrices", inverseBindMatrices, gm.inverseBindMatrices);
        if (diff != "")
            return diff;
        String[] skelRoots1 = getGNodeIds(skeletonRoots);
        String[] skelRoots2 = getGNodeIds(gm.skeletonRoots);
        diff = Diff.showDiff("GSkinnedMesh " + getId() + ", diff skeletonRoots", skelRoots1, skelRoots1);
        if (diff != "")
            return diff;
        String[] jointNodes1 = getGNodeIds(jointNodes);
        String[] jointNodes2 = getGNodeIds(gm.jointNodes);
        diff = Diff.showDiff("GSkinnedMesh " + getId() + ", diff jointNodes", jointNodes1, jointNodes2);
        if (diff != "")
            return diff;
        // omitted from test:  jointMatrices, jointPositions
        diff = super.showDiff(gmObj);
        if (diff != "")
            return diff;
        return "";
    }

    // shallow copy of GNode id's from an array of GNodes, needed for showDiff test
    private String[] getGNodeIds(GNode[] gnodes)
    {
        if (gnodes == null)
            return null;
        String[] result = new String[gnodes.length];
        for (int i = 0; i < result.length; i++)
            result[i] = gnodes[i].getId();
        return result;
    }

    /**
     * Sets the array with skeleton id's
     */
    public void setSkeletonIds(String[] skeletonIds)
    {
        this.skeletonIds = skeletonIds;
    }

    /**
     * Returns the String array with skeleton ids (probably of length 1)
     */
    public String[] getSkeletonIds()
    {
        return skeletonIds;
    }

    /**
     * Sets the array with joint names (sid's)
     */
    public void setJointSIDs(String[] jointSIDs)
    {
        this.jointSIDs = jointSIDs;
    }

    /**
     * Returns the String array with joint names
     */
    public String[] getJointSIDs()
    {
        return jointSIDs;
    }

    // /**
    // * Sets the array with the Collada joint names. (So, NOT the sid's, but just the "user friendly" strings)
    // */
    // public void setJointNames(String[] names) {
    // this.jointNames = names;
    // }

    /**
     * Returns the String array with joint names
     */
    public String[] getJointNames()
    {
        return jointNames;
    }

    /**
     * renames the sids of joints. here we assume that proper sid's, not names or id's, can be used.
     */
    public void renameJointSIDs(Map<String, String> renaming)
    {
        for (int i = 0; i < jointSIDs.length; i++)
        {
            String newName = renaming.get(jointSIDs[i]);
            if (newName != null)
            {
                jointSIDs[i] = newName;
            }
        }
    }

// not actually used
//    public GNode getJointGNode(String sid)
//    {
//        for (int i = 0; i < jointSIDs.length; i++)
//        {
//            if (jointSIDs[i].equals(sid))
//                return jointNodes[i];
//        }
//        return null;
//    }

    public VJoint getVJoint(String sid)
    {
        for (int i = 0; i < jointSIDs.length; i++)
        {
            if (jointSIDs[i].equals(sid))
                return jointNodes[i].getVJoint();
        }
        return null;
    }

    /**
     * Prints the joint names to the Console
     */
    public void printJointNames()
    {
        if (jointSIDs == null)
        {
            for (int j = 0; j < jointSIDs.length; j++)
            {
                hmi.util.Console.println("Joint[" + j + "]=" + jointSIDs[j]);
            }
        }
        else
        {
            for (int j = 0; j < jointSIDs.length; j++)
            {
                hmi.util.Console.println("Joint[" + j + "]=" + jointSIDs[j] + "   (" + jointSIDs[j] + ")");
            }
        }
    }

    /**
     * returns the array with parent indices for joints
     */
    public int[] getParentIndex()
    {
        return parentIndex;
    }

    /**
     * Sets the float array with packed inverse bind matrices. Matrices are stored in row major order, occupying 16 floats per matrix.
     */
    public void setInvBindMatrices(float[][] matrices)
    {
        this.inverseBindMatrices = matrices;
    }

    /**
     * returns the float array with packed inverse bind matrices. 16 floats per matrix, in row major order
     */
    public float[][] getInvBindMatrices()
    {
        return inverseBindMatrices;
    }

 

    /**
     * Prints the inverse bind matrices to the Console
     */
    public void printInvBindMatrices()
    {
        for (int m = 0; m < inverseBindMatrices.length; m++)
        {
            hmi.util.Console.println("InvBindMatrix[" + m + "]=" + Mat4f.toString(inverseBindMatrices[m], JINFOTAB, JINFOFIELDWIDTH, JINFOPRECISION));
        }
    }

    /**
     * Returns an array with VJoints for all joints within the skinned mesh
     */
    public VJoint[] getVJoints()
    {
        VJoint[] vjoints = new VJoint[jointNodes.length];
        for (int i = 0; i < jointNodes.length; i++) 
        {
              vjoints[i] = jointNodes[i].getVJoint();
        }
        return vjoints;
    }

    /**
     * Returns an array with the skeleton roots(s) of this skinned mesh
     */
    public GNode[] getSkeletonRoots()
    {
        return skeletonRoots;
    }

    /**
     * Prints the name and inverse bind matrix for all joints.
     */
    public void printJointInfo()
    {
        if (jointSIDs.length != inverseBindMatrices.length)
        {
            hmi.util.Console.println("GSkinnedMesh.printJointInfo warning: jointSIDs.length=" + jointSIDs.length + "  inverseBindMatrices.length="
                    + inverseBindMatrices.length);
        }
        for (int j = 0; j < jointSIDs.length; j++)
        {
            hmi.util.Console.println("Joint[" + j + "]=" + jointSIDs[j]);
            if (j < inverseBindMatrices.length)
            {
                hmi.util.Console.println("InvBindMatrix[" + j + "]="
                        + Mat4f.toString(inverseBindMatrices[j], JINFOTAB, JINFOFIELDWIDTH, JINFOPRECISION));
            }
        }
    }

    private static final int JINFOTAB = 3;
    private static final int JINFOFIELDWIDTH = 6;
    private static final int JINFOPRECISION = 1;

    /**
     * Sets the vertex weights for skinning
     */
    public void setVertexWeights(VertexWeights vertexWeights)
    {
        this.vertexWeights = vertexWeights;
    }

    /**
     * Returns the VertexWeights
     */
    public VertexWeights getVertexWeights()
    {
        return vertexWeights;
    }

    /**
     * Removes all attribute-specific indices, and replaces them by a single, common, indexData array. Overrides the unifyIndices from GMesh (adds
     * code for remapping vertexweight data).
     */
    @Override
    public void unifyIndices()
    {
        if (hasUnifiedIndexData())
        {
            logger.warn("GSkinnedMesh.unifyIndices should not be called twice");
            // return;
            throw new IllegalStateException("GSkinnedMesh.unifyIndices cannot be called twice");
        }
        int[] map = calculateTuples();
        if (vertexWeights != null)
        {
            vertexWeights.remapData(getNrOfVertices(), map);
        }

    }

    /**
     * Resolves the skeleton root node(s) within the GNode scenegraph adds these root(s) to skelRootList.
     */
    public void resolveSkeletonIds(List<GNode> jointRoots)
    {
        if (skeletonIds == null)
        {
            logger.warn("GSkinnedMesh.resolveSkeletonIds: no skeleton Ids defined");
            return;
        }
        skeletonRoots = new GNode[skeletonIds.length];

        for (int i = 0; i < skeletonIds.length; i++)
        {
            String skeletonId = skeletonIds[i];
            // hmi.util.Console.println("Searching skeleton Id:" + skeletonId);
            GNode skeletonRoot = null;
            for (GNode root : jointRoots)
            {
                skeletonRoot = root.getPartById(skeletonId); // by Id only or allow by name/by sid?
                if (skeletonRoot != null)
                    break;
            }
            // hmi.util.Console.println(skeletonRoot == null, "Null skeletonRoot", "skeletonRoot found: "+skeletonRoot);
            if (skeletonRoot == null)
            {
                logger.warn("GSkinnedMesh.resolveSkeletonIds: Cannot find skeleton " + skeletonId);
            }
            else
            {
                skeletonRoots[i] = skeletonRoot;
            }
        }
    }

    /**
     * Resolves the joints, used in this GSkinnedMesh, by searching for skeletons and joint names within the specified VJoint scene graphs. The
     * skeleton root node(s) are added to the skeletonRoots list, provided they are not already included.
     */
    public void resolveJoints(List<GNode> jointRoots)
    {
        if (jointSIDs == null)
            return;
        // hmi.util.Console.println("GSkinnedMesh.resolveJoints");
        if (jointNodes == null)
        {
            // hmi.util.Console.println("GSkinnedMesh.resolveJoints: " + jointSIDs.length );
            jointNodes = new GNode[jointSIDs.length];
            jointNames = new String[jointSIDs.length];
            jointMatrices = new float[jointSIDs.length][];
            parentIndex = new int[jointSIDs.length];
            jointPositions = new float[jointSIDs.length][];
            for (int i = 0; i < jointSIDs.length; i++)
                jointPositions[i] = Vec3f.getVec3f();
        }
        resolveSkeletonIds(jointRoots);
        for (int i = 0; i < skeletonRoots.length; i++)
        {
            GNode skeletonRoot = skeletonRoots[i];
            for (int n = 0; n < jointSIDs.length; n++)
            {
                GNode jointNode = skeletonRoot.getPartBySid(jointSIDs[n]);
                if (jointNode != null)
                {
                    if (jointNodes[n] != null)
                    {
                        logger.warn("Warning - GSkinnedMesh: resolveJoints: double definition for joint " + jointSIDs[n]);
                    }
                    jointNodes[n] = jointNode;
                    jointNames[n] = jointNode.getName();
                    jointMatrices[n] = jointNode.getVJoint().getGlobalMatrix();
                    String parentSid = jointNode.getParent().getSid();
                    parentIndex[n] = -1;
                    for (int p = 0; p < jointSIDs.length; p++)
                    {
                        if (jointSIDs[p].equals(parentSid))
                        {
                            parentIndex[n] = p;
                            break;
                        }
                    }
                }
            }
        }
    }

  

    private static boolean showCalc = true;

    /*
     * assume that global matrices have been calculated, so the jointMatrices have been set deprecated/not useful: calculateBindPositions has replaced
     * this method
     */

    /*
     * Herwin: disabled, never used locally private void calculateJointPositions() {
     * //hmi.util.Console.println("GSkinnedMesh.calculateJointPositions"); float[] zero = Vec3f.getZero();
     * 
     * float[] bm = Mat4f.getMat4f(); float[] idm = Mat4f.getIdentity(); float[] jparentm = Mat4f.getMat4f(); float[] invparentm = Mat4f.getMat4f();
     * float[] localm = Mat4f.getMat4f(); for (int i=0; i<jointSIDs.length; i++) {
     * 
     * Mat4f.invertAffine(bm, inverseBindMatrices[i]); // get the (affine) bind matrix into bm float[] jm = jointNodes[i].getGlobalMatrix(); int pi
     * =parentIndex[i]; // if (pi >= 0) { // invparentm = inverseBindMatrices[pi]; // Mat4f.mul(localm, invparentm, bm); //
     * jointNodes[i].setLocalTransform(localm); // Mat4f.set(jointMatrices[i], bm); // }
     * 
     * // GNode parenti = jointNodes[i].getParent(); // if (parenti == null) { // hmi.util.Console.println("null parent for " + i); // } else { //
     * jparentm = parenti.getGlobalMatrix(); // Mat4f.invertAffine(invparentm, jparentm); // Mat4f.mul(localm, invparentm, jm); //
     * jointNodes[i].setLocalTransform(idm); // //hmi.util.Console.println( "parent for " + i + " OK"); // }
     * 
     * 
     * // if (showCalc && ! Mat4f.isRigid(jointMatrices[i], 0.001f) ) { // hmi.util.Console.println("jointMatrices[" + i + "] " + jointSIDs[i] +
     * " not rigid"); // } // // if (showCalc && ! Mat4f.isRigid(bm, 0.001f) ) { // hmi.util.Console.println("BindMatrix[" + i + "] not rigid"); // }
     * // // if (showCalc && (i <=1)) { hmi.util.Console.println("jointMatrices[" + i + "]=" +Mat4f.toString(jointMatrices[i], 3 , 4, 1));
     * 
     * hmi.util.Console.println("BindMatrix[" + i + "]=" +Mat4f.toString(bm, 3 , 4, 1)); hmi.util.Console.println(".....................");
     * 
     * }
     * 
     * 
     * // Mat4f.set(jointMatrices[i], idm); if (showCalc && i < 25 && ! Mat4f.epsilonEquals(jm, bm, 0.07f)) {
     * hmi.util.Console.println("GSkinnedMesh.calculateJointPositions: unequal jointMatrx and bindMatrix for index " + i + "  " + jointSIDs[i]);
     * hmi.util.Console.println("jm[" + i + "]=" +Mat4f.toString(jm, 3 , 5, 2)); hmi.util.Console.println("bm[" + i + "]=" +Mat4f.toString(bm, 3 , 5,
     * 2)); hmi.util.Console.println("====");
     * 
     * 
     * }
     * 
     * 
     * Mat4f.transformPoint(jointMatrices[i], jointPositions[i], zero);
     * 
     * //if ( i < 10 && showCalc)hmi.util.Console.println("joint[ " + i + "] " + jointNames[i] + "  position=" + Vec3f.toString(jointPositions[i], 4,
     * 3)); } showCalc = false; }
     */

    /**
     * assume that global matrices have been calculated, so the jointMatrices have been set
     */
    public void showPositions(int limit)
    {
        float[] zero = Vec3f.getZero();
        float[] bm = Mat4f.getMat4f();
        float[] jp = Vec3f.getVec3f();
        float[] bp = Vec3f.getVec3f();
        int max = (limit < jointSIDs.length) ? limit : jointSIDs.length;
        for (int i = 0; i < max; i++)
        {

            Mat4f.invertAffine(bm, inverseBindMatrices[i]);
            Mat4f.getTranslation(bp, bm);

            Mat4f.transformPoint(jointMatrices[i], jp, zero);
            /*
            hmi.util.Console.println("joint[ " + i + "] " + jointNames[i] + "\nbind position="
                    + Vec3f.toString(bp, SHOWPOSFIELDWIDTH, SHOWPOSPRECISION) + "\nskel position="
                    + Vec3f.toString(jp, SHOWPOSFIELDWIDTH, SHOWPOSPRECISION));
                    */
        }
    }

    private static final int SHOWPOSFIELDWIDTH = 4;
    private static final int SHOWPOSPRECISION = 3;


    /*
     * calculate joint positions for bind pose, then simplify by eliminating rotations/scaling from joint matrices and inverse bind matrices
     */
    public void simplifyBindPose()
    {
        if (inverseBindMatrices == null)
            throw new IllegalArgumentException("GSkinnedMesh.adjustBindMatrices: no inverse bind matrices defined");

        float[] bm = Mat4f.getMat4f();
        float[] negCenter = Vec3f.getVec3f(); // - center of joint i
        float[] transVec = Vec3f.getVec3f(); // translation vector from parent of joint i to joint i

        // First calculate all joint positions for the bind pose, as specified by the inverse bind matrices:
        for (int i = 0; i < jointSIDs.length; i++)
        {
            Mat4f.invertAffine(bm, inverseBindMatrices[i]); // get the (affine) bind matrix into bm
            Mat4f.getTranslation(jointPositions[i], bm); // get the translation (= C_i) from the bind matrix into jointPositions[i]; 
        }
        // Next, we can simplify the inverse bind matrices and the joint transforms by eliminating the rotation/scaling component.

        for (int i = 0; i < jointSIDs.length; i++)
        {
            // First, calculate the relative joint positions when NO rotation/scaling would be applied:
            // This is the relative translation vector for joint i, from the relative bind positions of i and its parent
            if (parentIndex[i] < 0)
            {
                // no parent, take parent position to be (0,0,0) 
                // This effectively eliminates translations above the HumanoidRoot, like the large translation in
                //<matrix>0 1 0 0  -1 0 0 0.7999  0 0 1 94.701  0 0 0 1</matrix> <!-- original matrix for  Bip01-node -->
                Vec3f.set(transVec, jointPositions[i]);   
            }
            else
            {
                Vec3f.sub(transVec, jointPositions[i], jointPositions[parentIndex[i]]); // transvec = t_i' = C_i - C_{parent(i)                                                                        // jointPositions[parentIndex[i]]
            } 
            jointNodes[i].clearLocalLinearTransform();
            jointNodes[i].setTranslation(transVec); // t_i' = C_i - C_{parent(i)  
  
            Mat4f.clearRotationScale(inverseBindMatrices[i]);
            Vec3f.negate(negCenter, jointPositions[i]);
            Mat4f.setTranslation(inverseBindMatrices[i], negCenter); // B_i^{-1} = T_{-Ci}
        }

//        GNode skeletonParent = jointNodes[0].getParent();
//        if (skeletonParent != null) {
//           hmi.util.Console.println("GSkinnedMesh.adjustBindPose: clear linear transform for skeletonParent");
//            skeletonParent.clearLocalLinearTransform();
//        }
    }




//    /*
//     * clears all local rotations/scalings by setting the rotation to Id
//     */
//    public void clearLocalLinearTransforms()
//    {
//        for (int i = 0; i < jointSIDs.length; i++)
//        {
//            jointNodes[i].clearLocalLinearTransform();
//        }
//    }
//





    /**
     * A special case of an affineTransform, without a translation,
     * applied to the underlying GMesh as well as the inverse bind matrices.
     */
    public void linearTransform(float[] mat3x3)
    {
        float[] mat4x4 = Mat4f.from3x3(mat3x3);
        affineTransform(mat4x4);

    }

    /**
     * Applies an affine transform, assumed here to be a combination of rotation and translation, 
     * to the underlying GMesh and the inverse bind matrices of this GSkinnedMesh.
     */
    public void affineTransform(float[] ma)
    {
        /*
         * basically, the inverse bind matrices Binv must be replaced by Binv' = mat4x4 o Binv o mat4x4-inv For the special case that Binv is a
         * translation T(b), then Binv' = T(L(b)), where L = 3x3 upperleft submatrix from mat4x4
         */
        super.affineTransform(ma); // transforms the mesh geometry (GMesh method)

        if (jointSIDs != null)
        { // TODO: this takes care of the bind_shape matrix, in InstanceGeometryTranslator
            for (int i = 0; i < jointSIDs.length; i++)
            {
                Mat4f.transformAffineMatrix(ma, inverseBindMatrices[i]);
            }
        }
    }




    /* Allocates inverse bind matrices, and initializes them to identity matrices. */
    private void initInverseBindMatrices() 
    {
        inverseBindMatrices = new float[jointMatrices.length][];
        for (int i = 0; i < inverseBindMatrices.length; i++) inverseBindMatrices[i] =  Mat4f.getIdentity();
    }

   
    /**
     * Assuming that the global matrices contain the V_i matrices to be (left) multiplied with the existing
     * inverse bind matrices, this method actually performs the multiplication. 
     * Only the rotation/scaling part of the global matrices is used
     */
    public void setBindPose() 
    {
        if (inverseBindMatrices == null) initInverseBindMatrices();
        
        float[] bindTranslation = Vec3f.getVec3f();
        float[] rotations = Mat4f.getMat4f();
        float[] zeroVec = Vec3f.getZero();
        
        for (int i = 0; i < inverseBindMatrices.length; i++) 
        {
            VJoint joint = jointNodes[i].getVJoint();
            Mat4f.set(rotations, joint.getGlobalMatrix());
            Mat4f.setTranslation(rotations, zeroVec);
            Mat4f.mul(inverseBindMatrices[i], rotations, inverseBindMatrices[i]);       
        }  
    }

   



    private static final int NUMJOINTSSHOWN = 20;
    private static final int FIELDWIDTH = 4;
    private static final int PRECISION = 2;
    private static final float EPSILON1 = 0.01f;
    private static final float EPSILON2 = 0.0001f;

    /**
     * assumption: colorCoding is an array of float[3] colors, same length as indices array.
     */
    public void addVertexWeightColors(boolean useWeights, String[] names, float[][] colorCoding3)
    {
        int[] indices = getJointIndices(names);
        float[][] colorCoding = new float[jointSIDs.length][Vec4f.VEC4F_SIZE];
        for (int i = 0; i < jointSIDs.length; i++)
        {
            colorCoding[i] = new float[] { 0f, 0f, 0f, 1f };
        }

        // hmi.util.Console.println("GSkinnedMesh.addVertexWeightColors indices.length = " + indices.length + " colorCoding3.length " +
        // colorCoding3.length);
        for (int k = 0; k < indices.length; k++)
        {
            // hmi.util.Console.println("GSkinnedMesh.addVertexWeightColors k= " + k + " indices[k] = " + indices[k]);
            if (indices[k] < 0)
            {
                log.warn("GSkinnedMesh.addVertexWeightColors joint name  " + names[k] + " not present in skeleton");
            }
            else
            {
                colorCoding[indices[k]][0] = colorCoding3[k][0];
                colorCoding[indices[k]][1] = colorCoding3[k][1];
                colorCoding[indices[k]][2] = colorCoding3[k][2];
                colorCoding[indices[k]][3] = 1.0f;
            }
        }
        float[] colors = vertexWeights.getVertexWeightColors(useWeights, colorCoding);
        setVertexData(-1, "color", Vec4f.VEC4F_SIZE, colors);
    }

    public int[] getJointIndices(String[] names)
    {
        int[] indices = new int[names.length];
        for (int i = 0; i < names.length; i++)
        {
            String jn = names[i];
            // hmi.util.Console.println("searching for joint " + jn);
            indices[i] = -1;
            for (int j = 0; j < jointSIDs.length; j++)
            {
                // hmi.util.Console.println("comparing with  " + jointSIDs[j]);
                if (jointSIDs[j].equals(jn))
                {
                    indices[i] = j;
                    break;
                }
            }
        }
        return indices;
    }

    private static final int COLORCODINGSIZE = 10;
    private static final int COLORSIZE = 4;

    public float[][] getColorCoding()
    {
        float[][] colorCoding = new float[COLORCODINGSIZE][COLORSIZE];

        return colorCoding;
    }

    /**
     * appends the XML attributes to buf.
     */
    @Override
    public StringBuilder appendAttributeString(StringBuilder buf, XMLFormatting fmt)
    {
        super.appendAttributeString(buf, fmt);
        return buf;
    }

    /**
     * decodes the XML attributes.
     */
    @Override
    public void decodeAttributes(HashMap<String, String> attrMap, XMLTokenizer tokenizer)
    {
        super.decodeAttributes(attrMap, tokenizer);
    }

    private static final int INDICESPERLINE = 30;
    private static final int STRINGSPERLINE = 10;
    private static final int FLOATSPERLINE = 16;

    /** Appends content part of XML encoding */
    @Override
    public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt)
    {
        appendStringArrayElement(buf, "skeletonids", skeletonIds, ' ', fmt, STRINGSPERLINE);
        appendStringArrayElement(buf, "jointnames", jointNames, ' ', fmt, STRINGSPERLINE);
        appendStringArrayElement(buf, "jointsids", jointSIDs, ' ', fmt, STRINGSPERLINE);
        appendIntArrayElement(buf, "parentindices", parentIndex, ' ', fmt, INDICESPERLINE);
        if (vertexWeights != null)
        {
            buf.append('\n');
            vertexWeights.appendXML(buf, fmt);
        }
        if (inverseBindMatrices != null)
        {
            buf.append('\n');
            appendSpaces(buf, fmt);
            appendOpenSTag(buf, "inversebindmatrices");
            appendAttribute(buf, "count", inverseBindMatrices.length);
            appendCloseSTag(buf);
            fmt.indent();
            for (int i = 0; i < inverseBindMatrices.length; i++)
            {
                appendFloatArrayElement(buf, "matrix", inverseBindMatrices[i], ' ', fmt, FLOATSPERLINE);
            }
            buf.append('\n');
            appendSpaces(buf, fmt.unIndent());
            appendETag(buf, "inversebindmatrices");
        }
        super.appendContent(buf, fmt);
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
            if (tag.equals("skeletonids"))
            {
                skeletonIds = decodeStringArrayElement("skeletonids", tokenizer);
            }
            else if (tag.equals("jointnames"))
            {
                jointNames = decodeStringArrayElement("jointnames", tokenizer);
            }
            else if (tag.equals("jointsids"))
            {
                jointSIDs = decodeStringArrayElement("jointsids", tokenizer);
            }
            else if (tag.equals("parentindices"))
            {
                parentIndex = decodeIntArrayElement("parentindices", tokenizer);
            }
            else if (tag.equals("vertexweights"))
            {
                vertexWeights = new VertexWeights(tokenizer);
            }
            else if (tag.equals("inversebindmatrices"))
            {
                int count = getRequiredIntAttribute("count", tokenizer.getAttributes(), tokenizer);
                inverseBindMatrices = new float[count][Mat4f.MAT4F_SIZE];
                tokenizer.takeSTag("inversebindmatrices");
                for (int i = 0; i < count; i++)
                {
                    tokenizer.takeSTag("matrix");
                    decodeFloatArray(tokenizer.takeOptionalCharData(), inverseBindMatrices[i]);
                    tokenizer.takeETag("matrix");
                }
                tokenizer.takeETag("inversebindmatrices");
            }
            else
            {
                super.decodeContent(tokenizer);
            }
        }
    }

    /*
     * The XML Stag for XML encoding
     */
    private static final String XMLTAG = "gskinnedmesh";

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
        super.writeBinary(dataOut);
        BinUtil.writeStringArray(dataOut, skeletonIds);
        BinUtil.writeStringArray(dataOut, jointNames);
        BinUtil.writeStringArray(dataOut, jointSIDs);
        BinUtil.writeIntArray(dataOut, parentIndex);

        if (vertexWeights == null)
        {
            dataOut.writeInt(-1);
        }
        else
        {
            dataOut.writeInt(1);
            vertexWeights.writeBinary(dataOut);
        }

        if (inverseBindMatrices == null)
        {
            dataOut.writeInt(-1);
        }
        else
        {
            dataOut.writeInt(inverseBindMatrices.length);
            for (int i = 0; i < inverseBindMatrices.length; i++)
            {
                float[] mat = inverseBindMatrices[i];
                for (int j = 0; j < Mat4f.MAT4F_SIZE; j++)
                {
                    dataOut.writeFloat(mat[j]);
                }
            }

        }

    }

    /**
     * Reads a binary encoding from dataIn
     */
    public void readBinary(DataInput dataIn) throws IOException
    {
        super.readBinary(dataIn);
        skeletonIds = BinUtil.readStringArray(dataIn);
        jointNames = BinUtil.readStringArray(dataIn);
        jointSIDs = BinUtil.readStringArray(dataIn);
        parentIndex = BinUtil.readIntArray(dataIn);

        int hasVertexWeights = dataIn.readInt();
        if (hasVertexWeights < 0)
        {
            vertexWeights = null;
        }
        else
        {
            vertexWeights = new VertexWeights();
            vertexWeights.readBinary(dataIn);
        }
        int count = dataIn.readInt();
        if (count < 0)
        {
            inverseBindMatrices = null;
        }
        else
        {
            inverseBindMatrices = new float[count][Mat4f.MAT4F_SIZE];
            for (int i = 0; i < count; i++)
            {
                float[] mat = new float[Mat4f.MAT4F_SIZE];
                inverseBindMatrices[i] = mat;
                for (int j = 0; j < Mat4f.MAT4F_SIZE; j++)
                {
                    mat[j] = dataIn.readFloat();
                }
            }
        }

    }

}
