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

import hmi.animation.VJoint;
import hmi.graphics.scenegraph.VertexAttribute;
import hmi.math.Mat4f;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * 
 */
public class GLSkinnedMesh extends GLBasicMesh
{

    // nrOfVertices: inherited from GLBasicMesh

    private float[] vertexCoordBaseData; // unmodified vertex coords for the morph base mesh
    private float[][] vertexCoordMorphData; // unmodified vertex coords for all morph targes
    private float[] vertexCoordMorphed; // unmodified vertex coordinates or result of morph
    private float[] vertexCoordCurrent; // current, i.e. modified, vertex coordinates
    private int vertexCoordAttrIndex;

    private float[] normalOriginal; // unmodified vertex normals
    private float[] normalCurrent; // current, i.e. modified, vertex normals
    private int normalAttrIndex;

    // Let low(v) = Sigma 0<=i<v jointCount(i), high(v) = low(v+1)-1
    // then jointIndex[low(v)], .. ,jointIndex[high(v)] are the indices of the joints that influence vertex v.
    // The corresponding weights are jointWeight[low(v)], .. , jointWeight[high(v)].
    private int[] jointIndex; // joint indices
    private float[] jointWeight; // joint weights
    private int[] jointCount; // jointCount[v] = number of associated joints, for vertex v.

    private float[][] jointMatrices; // references (typically VJoint-) matrices that define joint transforms, not including inverse bind matrices.
    private float[][] inverseBindMatrices; // locally stored inverse bind matrices.
    
   

    private float[][] transformMatrices; // locally stored transform matrices, combining jointMatrices with inverseBindMatrices
    private String[] jointSIDs; // used to identify/resolve the VJoints from the skeleton, by means of VJoint sids.
    private String[] jointNames; // optional user friendly joint names
    private int[] parentIndex; // indices of parent joints, where -1 is used for a root joint.
    private String[] skeletonIds; // used to identify the skeleton root VJoint, by means of a VJoint id.

    private boolean useFaps = false;
    private int[] fapIndex; // FAP indices
    private float[] fapWeight; // FAP weights
    private int[] fapCount; // fapCount[v] = number of associated FAPS, for vertex v.
    float[][] directionVectors; // directionVectors[fapnr] is a Vec3f float array specifying the direction vector for FAP number fafnr.

    private float[][] fapDirectionVectors; // references Vec3f vectors that define the (semi-static) FAP directional vectors.
    private float[][] fapDisplacements; // references Vec3f vectors that define the current FAP displacement vectors.
    private float[] fapAmplitudes;

    private String[] morphTargets = null;
    private int nrOfMorphTargets = -1;

    private hmi.graphics.opengl.scenegraph.GLNodeMarker[] jointMarkers; // objects (typical spheres) that are used to mark the positions of the
                                                                        // joints.

    private static Logger logger = LoggerFactory.getLogger(GLSkinnedMesh.class.getName());

    /**
     * Creates a new deformable mesh
     */
    public GLSkinnedMesh()
    {
        super();
    }

    public void setParentIndex(int[] parentIndex)
    {
        this.parentIndex = parentIndex;
    }

    public int[] getParentIndex()
    {
       return parentIndex;
    }

    




    /**
     * Sets a reference to an array of Vec3f (i.e. float[3]) vectors, one vector per FAP, definining the direction vector for each FAP.
     */
    public void setFapDirectionVectors(float[][] fapDirectionVectors)
    {
        this.fapDirectionVectors = fapDirectionVectors;
        int nrOfFaps = fapDirectionVectors.length;
        fapAmplitudes = new float[nrOfFaps];
        fapDisplacements = new float[nrOfFaps][];
        for (int fi = 0; fi < nrOfFaps; fi++)
        {
            fapDisplacements[fi] = new float[3];
        }
    }

    /**
     * Enables or disables the usage of FAPS
     */
    public void setUseFaps(boolean useFaps)
    {
        this.useFaps = useFaps;
    }

    /**
     * Copies the current FAPS amplitudes from the specified amplitude float array. These are scalar numbers, one for every FAP, and are multiplied
     * with the static FAP displacement vector in order to get the current FAP translation.
     */
    public void setFapAmplitudes(float[] amplitudes)
    {
        System.arraycopy(amplitudes, 0, fapAmplitudes, 0, fapAmplitudes.length);
    }


    /**
     * Defines the morph target names
     */
    public void setMorphTargets(String[] morphTargets)
    {
        this.morphTargets = morphTargets;
        nrOfMorphTargets = morphTargets.length;
    }

    /**
     * Returns the array with morph target names.
     */
    public String[] getMorphTargets()
    {
        return morphTargets;
    }

    /**
     * returns the index of the specified morph target, provided it occurs within the array of morph targets for this mesh. Otherwise, it returns -1;
     */
    public int getMorphTargetIndexFor(String morphTarget)
    {
        if (morphTargets == null || nrOfMorphTargets == 0)
            return -1;
        for (int i = 0; i < nrOfMorphTargets; i++)
        {
            if (morphTargets[i].equals(morphTarget))
                return i;
        }
        return -1;
    }

    /**
     * Sets the array of arrays, containing the coord data for all morph targets
     */
    public void setVertexCoordMorphData(float[][] vertexCoordMorphData)
    {
        this.vertexCoordMorphData = vertexCoordMorphData;
        if (nrOfMorphTargets < 0)
            nrOfMorphTargets = vertexCoordMorphData.length;
        vertexCoordBaseData = new float[vertexCoordMorphed.length];
        for (int i = 0; i < vertexCoordBaseData.length; i++)
            vertexCoordBaseData[i] = vertexCoordMorphed[i];
    }

    public void setJointSIDs(String[] jointSIDs)
    {
        this.jointSIDs = Arrays.copyOf(jointSIDs, jointSIDs.length);
    }

    public void setJointNames(String[] jointNames)
    {
        this.jointNames = Arrays.copyOf(jointNames, jointNames.length);
    }

    public void setSkeletonIds(String[] skeletonIds)
    {
        this.skeletonIds = Arrays.copyOf(skeletonIds, skeletonIds.length);
    }

    public void setVJoints(VJoint[] vjoints)
    {
        this.jointMatrices = new float[vjoints.length][];
        transformMatrices = new float[vjoints.length][];
        for (int m = 0; m < vjoints.length; m++)
        {
            jointMatrices[m] = vjoints[m].getGlobalMatrix();
            transformMatrices[m] = Mat4f.getIdentity();
        }

    }

    /**
     * Adds a new vertex attribute, and returns its index number.
     */
    @Override
    public int addGLVertexAttribute(VertexAttribute va)
    {
        int attrIndex = super.addGLVertexAttribute(va);
        String attrName = va.getName();
        if (attrName.equals("mcPosition"))
        {
            vertexCoordAttrIndex = attrIndex;
            vertexCoordMorphed = getVertexData(vertexCoordAttrIndex, null); // will *copy* data from the attribute
            vertexCoordCurrent = getVertexData(vertexCoordAttrIndex, null); // will copy data from the attribute
        }
        else if (attrName.equals("mcNormal"))
        {
            normalAttrIndex = attrIndex;
            normalOriginal = getVertexData(normalAttrIndex, null); // will copy data from the attribute
            normalCurrent = getVertexData(normalAttrIndex, null); // will copy data from the attribute
        } // else: no special action needed. (in particular, tangents and binormals need NO special treatment)
        return attrIndex;
    }

    /**
     * Sets the data needed for skinning: indices for joints, corresponding weights, and, for every vertex, the number of joints to which it is
     * attached.
     */
    public void setJointVertexWeights(int[] jointCount, int[] jointIndex, float[] jointWeight)
    {
        this.jointCount = Arrays.copyOf(jointCount, jointCount.length);
        this.jointIndex = Arrays.copyOf(jointIndex, jointIndex.length);
        this.jointWeight = Arrays.copyOf(jointWeight, jointWeight.length);
    }

    /**
     * Sets the data needed for facial action parameters: indices for FAPS, corresponding weights, and, for every vertex, the number of FAPS to which
     * it is attached.
     */
    public void setFapVertexWeights(int[] fapCount, int[] fapIndex, float[] fapWeight)
    {
        this.fapCount = Arrays.copyOf(fapCount, fapCount.length);
        this.fapIndex = Arrays.copyOf(fapIndex, fapIndex.length);
        this.fapWeight = Arrays.copyOf(fapWeight, fapWeight.length);
    }

    /**
     * Creates copies of the specified inverse bind matrices
     */
    public void setInverseBindMatrices(float[][] invBindMatrices)
    {
        logger.debug("setInverseBindMatrices");
        this.inverseBindMatrices = new float[invBindMatrices.length][16];
        for (int i = 0; i < inverseBindMatrices.length; i++)
        {
            Mat4f.set(inverseBindMatrices[i], invBindMatrices[i]);
        }
    }

    /**
     * returns inverse bind matrices
     */
    public float[][] getInverseBindMatrices()
    {
        return inverseBindMatrices;
    }




    private static boolean notshown = true;

    /**
     * (re)calculates the transform matrices: matrices[i] = jointMatrices[i] * inverseBindMatrices[i]
     */
    private void calculateMatricesAndFaps()
    {
        // float[] nullTrans = new float[]{ 0.0f, 0.0f, 0.0f};
        if (inverseBindMatrices != null)
        {
            // hmi.util.Console.println("inverseBindMatrices OK");
            // float[] bindMatrix = Mat4f.getMat4f();
            // float[] prod = Mat4f.getMat4f();
            for (int i = 0; i < transformMatrices.length; i++)
            {
                if (transformMatrices[i] == null)
                    logger.error("null transformmatrix for index " + i);
                if (inverseBindMatrices[i] == null)
                    logger.error("null inversebindmatrix for index " + i);
                if (jointMatrices[i] == null)
                    logger.error("null jointmatrix for index " + i);
                Mat4f.mul(transformMatrices[i], jointMatrices[i], inverseBindMatrices[i]);

                if (notshown)
                {
                    if (i < 3 || (21 <= i && i <= 25))
                    {
                        // hmi.util.Console.println("\njoint[" + i + "] = " + jointSIDs[i] + "   (" + jointNames[i] + ")");
                        // hmi.util.Console.println("jointMatrix[" + i + "]=" + hmi.math.Mat4f.explainMat4f(jointMatrices[i], 4, 2, 0.01f));
                        // hmi.util.Console.println("inversBindMatrix[" + i + "]=" + hmi.math.Mat4f.explainMat4f(inverseBindMatrices[i], 4, 2,
                        // 0.01f));
                        // hmi.util.Console.println(" transformMatrix[" + i + "]=" + hmi.math.Mat4f.explainMat4f(transformMatrices[i], 4, 2,
                        // 0.0001f));
                        // hmi.util.Console.println(" ======================= " );
                    }
                }

            }
            notshown = false;
        }
        else
        {
            logger.error("NULL inverseBindMatrices");
            for (int i = 0; i < transformMatrices.length; i++)
            {

                logger.error("matrix[" + i + "]=\n" + hmi.math.Mat4f.toString(transformMatrices[i], 0, 6, 1));
            }
        }

        if (useFaps)
        {
            for (int fi = 0; fi < fapDisplacements.length; fi++)
            {
                fapDisplacements[fi][0] = fapDirectionVectors[fi][0] * fapAmplitudes[fi];
                fapDisplacements[fi][1] = fapDirectionVectors[fi][1] * fapAmplitudes[fi];
                fapDisplacements[fi][2] = fapDirectionVectors[fi][2] * fapAmplitudes[fi];
            }

        }

    }

    // public void resetSkeleton() {
    // }

    public void addJointMarkers()
    {
        addJointMarkers(0.005f, 16);
    }

    public void addJointMarkers(float radius)
    {
        addJointMarkers(radius, 16);
    }

    public void addJointMarkers(float radius, int grid)
    {
        jointMarkers = new hmi.graphics.opengl.scenegraph.GLNodeMarker[transformMatrices.length];
        // jointMarkers = new hmi.graphics.opengl.scenegraph.GLNodeMarker[9];
        for (int i = 0; i < jointMarkers.length; i++)
        {
            jointMarkers[i] = new hmi.graphics.opengl.scenegraph.GLNodeMarker(i, "jointsid", radius, grid);
            jointMarkers[i].linkToTransformMatrix(jointMatrices[i]);
        }
    }

    /**
     * initializes the OpenGL ARRAY and ELEMENT_ARRAY buffers. Calculates the buffer offsets for coordinates, normals. colors etc. inside vertexBuffer
     */
    public void glInit(GLRenderContext glc)
    {
        super.glInit(glc);
        if (jointMarkers != null)
        {
            for (int i = 0; i < jointMarkers.length; i++)
            {
                jointMarkers[i].glInit(glc);
            }
        }
    }

    /**
     * renders the mesh, using the vertexBuffer data.
     */
    public void glRender(GLRenderContext glc)
    {
        super.glRender(glc);
        if (jointMarkers != null)
        {
            for (int i = 0; i < jointMarkers.length; i++)
            {
                jointMarkers[i].glRender(glc);
            }
        }
    }

    /**
     * Morph the specified morph target, with specified weight, tohether with the base mesh. The latter receives the remaining weight
     */
    public void morph(String targetName, float weight)
    {
        if (nrOfMorphTargets <= 0)  return;
        morph(getMorphTargetIndexFor(targetName), weight);
    }

    /**
     * Morph the specified morph target, with specified weight, tohether with the base mesh. The latter receives the remaining weight
     */
    public void morph(int target, float weight)
    {
        if (nrOfMorphTargets <= 0)
            return;
        if (target < 0 || target >= nrOfMorphTargets)
            return;
        float baseWeight = 1.0f - weight;
        float[] targetData = vertexCoordMorphData[target];
        for (int i = 0; i < vertexCoordMorphed.length; i++)
        {
            vertexCoordMorphed[i] = baseWeight * vertexCoordBaseData[i] + weight * targetData[i];
        }
    }

    /**
     * Morph the specified morph targets, with specified weights, tohether with the base mesh. The latter receives the remaining weight
     */
    public void morph(String[] targetNames, float[] weights)
    {
        if (nrOfMorphTargets <= 0)
            return;
        int[] targets = new int[targetNames.length];
        for (int ti = 0; ti < targets.length; ti++)
        {
            targets[ti] = getMorphTargetIndexFor(targetNames[ti]);
        }
        morph(targets, weights);

    }

    /**
     * Morph the specified morph targets, with specified weights, tohether with the base mesh. The latter receives the remaining weight
     */
    public void morph(int[] targets, float[] weights)
    {
        if (nrOfMorphTargets <= 0 || targets == null || weights == null)
            return;
        int tlen = targets.length < weights.length ? targets.length : weights.length;
        if (tlen == 0)
            return;
        // first, set all to zero
        for (int i = 0; i < vertexCoordMorphed.length; i++)
        {
            vertexCoordMorphed[i] = 0;
        }
        // then, add all targets that are valid
        float totalWeight = 0.0f;
        for (int ti = 0; ti < tlen; ti++)
        {
            float weight = weights[ti];
            if ((targets[ti] == -1) || (weight <= 0.0f)) // don't morph: too small target, or no valid target
            {
            }
            else
            // do morph;
            {
                totalWeight += weight; // to later adjust base weight
                float[] targetData = vertexCoordMorphData[targets[ti]];
                for (int i = 0; i < vertexCoordMorphed.length; i++)
                {
                    vertexCoordMorphed[i] += weight * targetData[i];
                }
            }
        }
        float baseWeight = 1.0f - totalWeight; // NB could become negative!
        // always use remainder of base
        for (int i = 0; i < vertexCoordMorphed.length; i++)
        {
            vertexCoordMorphed[i] += baseWeight * vertexCoordBaseData[i];
        }
    }

    /**
     * Transforms the mesh attributes.
     */
    public void deform()
    {
        // hmi.util.Console.println("Deform...");
        calculateMatricesAndFaps();
        deformCN();
    }

    /**
     * transforms vertex coordinates and vertex normals
     */
    private void deformCN()
    {
        int jointIndexBase = 0; // index in jointIndex and jointWeight arrays
        int fapIndexBase = 0;
        for (int v = 0; v < nrOfVertices; v++)
        { // transform vertex coordinates for vertex v
            int vertexBase = 3 * v; // index in vertexDataOriginal and vertexData arrays.
            float fvx = vertexCoordMorphed[vertexBase];
            float fvy = vertexCoordMorphed[vertexBase + 1];
            float fvz = vertexCoordMorphed[vertexBase + 2];
            float nx = normalOriginal[vertexBase];
            float ny = normalOriginal[vertexBase + 1];
            float nz = normalOriginal[vertexBase + 2];
            // modified vertex coordinates:
            float mvx = 0.0f;
            float mvy = 0.0f;
            float mvz = 0.0f;
            // modified normal:
            float mnx = 0.0f;
            float mny = 0.0f;
            float mnz = 0.0f;

            // First apply FAPS displacements (if enabled)
            float vx = fvx;
            float vy = fvy;
            float vz = fvz;
            if (useFaps)
            {
                for (int p = fapIndexBase; p < fapIndexBase + fapCount[v]; p++)
                {
                    int fi = fapIndex[p];
                    float[] fapDisplacement = fapDisplacements[fi];
                    float fw = fapWeight[p];
                    vx += fw * fapDisplacement[0];
                    vy += fw * fapDisplacement[1];
                    vz += fw * fapDisplacement[2];
                }
                fapIndexBase += fapCount[v];
            }

            // Next, apply skeleton based transformations
            float accumulatedWeight = 0.0f;
            for (int p = jointIndexBase; p < jointIndexBase + jointCount[v]; p++)
            {
                int ji = jointIndex[p];

                float[] mat = transformMatrices[ji]; // add: if jointIndex ==-1, mat = Id
                float jw = jointWeight[p];

                mvx += jw * (mat[0] * vx + mat[1] * vy + mat[2] * vz + mat[3]);
                mvy += jw * (mat[4] * vx + mat[5] * vy + mat[6] * vz + mat[7]);
                mvz += jw * (mat[8] * vx + mat[9] * vy + mat[10] * vz + mat[11]);

                mnx += jw * (mat[0] * nx + mat[1] * ny + mat[2] * nz);
                mny += jw * (mat[4] * nx + mat[5] * ny + mat[6] * nz);
                mnz += jw * (mat[8] * nx + mat[9] * ny + mat[10] * nz);

                accumulatedWeight += jw;
                // if (accumulatedWeight >= blendWeightLimit) break;
            }
            jointIndexBase += jointCount[v];
            // if (accumulatedWeight < 0.9f) hmi.util.Console.println("accumulatedWeight for vertex " + v + " = " + accumulatedWeight);
            vertexCoordCurrent[vertexBase] = mvx;
            vertexCoordCurrent[vertexBase + 1] = mvy;
            vertexCoordCurrent[vertexBase + 2] = mvz;

            // (re)normalize the normal vector:
            double mnLenSq = mnx * mnx + mny * mny + mnz * mnz;
            float mnfactor = (mnLenSq == 0.0) ? 1.0f : (float) (1.0 / Math.sqrt(mnLenSq));
            normalCurrent[vertexBase] = mnx * mnfactor;
            normalCurrent[vertexBase + 1] = mny * mnfactor;
            normalCurrent[vertexBase + 2] = mnz * mnfactor;

        }
        setVertexData(vertexCoordAttrIndex, vertexCoordCurrent);
        setVertexData(normalAttrIndex, normalCurrent);
    }

    /**
     * @return the jointSIDs
     */
    public String[] getJointSIDs()
    {
        return jointSIDs;
    }

    /**
     * @return the jointIndex
     */
    public int[] getJointIndex()
    {
        return jointIndex;
    }

    /**
     * @return the jointWeight
     */
    public float[] getJointWeight()
    {
        return jointWeight;
    }

    /**
     * @return the jointCount
     */
    public int[] getJointCount()
    {
        return jointCount;
    }
    
    
    public StringBuilder appendTo(StringBuilder buf, int tab) {
      GLUtil.appendSpacesString(buf, tab, "GLSkinnedMesh \""); buf.append(getId()) ; buf.append('"');
      if (showDetail()) {
         GLUtil.appendSpacesString(buf, tab, "nrOfVertices: "); buf.append(getNrOfVertices());
         GLUtil.appendSpacesString(buf, tab, "nrOfIndices: "); buf.append(getNrOfIndices());
      }
      if (showAttributes()) {
         appendAttributesTo(buf, tab+GLUtil.TAB);
      }
      return buf;
   }
    
   /**
    * toString represents the mesh in String format.
    * The amount of info depends on the settings for showDetail and showAttributes.
    */
   public String toString() {
      StringBuilder buf = appendTo(new StringBuilder(), 0);
      return buf.toString();
   }
    
}
