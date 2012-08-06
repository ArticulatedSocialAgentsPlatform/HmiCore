/**
 * \file MouthMuscle.java
 * \author N.A.Nijdam
 */

package hmi.animation;

import hmi.graphics.render.BoundingBox;
import hmi.graphics.render.GenericMesh;
import hmi.math.Vec3f;

/**
 * \brief the mouth muscle model Creates the under and upper lip muscles Controls the mouth corners
 */
public class MouthMuscle extends Muscle
{
    final private float leftCornerBase[] = new float[] { 0, 0 };
    final private float rightCornerBase[] = new float[] { 0, 0 };

    final private float leftDir[] = new float[2];
    final private float rightDir[] = new float[2];
    private float leftIntensity[];
    private float rightIntensity[];

    final private int mouthData[] = new int[4];

    private BoundingBox boundingBox = new BoundingBox();

    private LipMuscle lowerLip = null;
    private LipMuscle upperLip = null;

    private JawMuscle jaw = null;

    /**
     * \brief the default constructor \param avatar the avatar \param name the name
     */
    public MouthMuscle(String name, GenericMesh genericMesh)
    {
        super(name, genericMesh);
        upperLip = new LipMuscle("Upper lip", genericMesh);
        lowerLip = new LipMuscle("Lower lip", genericMesh);
        lowerLip.setNegative(true);
        upperLip.setMouth(this);
        lowerLip.setMouth(this);
        paramSize = 4;
    }

    /**
     * \brief calculate vertex indices using a given mesh.
     */
    @Override
    public void calculateVertexIndices()
    {
        indices.clear();
        indices.addAll(boundingBox.extractVertexIndices(vertices));

        if (!indicesFiltered.isEmpty())
        {
            indices.removeAll(indicesFiltered);
        }

        setMouthData();
        setDirectionXZVectors();
        setDisplacement();
    }

    @Override
    public void init()
    {
        if (muscleSetSettings == null)
        {
            System.out.println("Unable to initialize without default musclesSettings");
            return;
        }
        muscleSetSettings[paramOffset] = leftCornerBase[0];
        muscleSetSettings[paramOffset + 1] = leftCornerBase[1];
        muscleSetSettings[paramOffset + 2] = rightCornerBase[0];
        muscleSetSettings[paramOffset + 3] = rightCornerBase[1];

        calculateVertexIndices();
    }

    /**
     * \brief update the vertices displacement
     */
    @Override
    public void updateVertices(float dstArray[], float muscleSettings[])
    {
        final float vertex[] = new float[3];
        final float leftDisplacement[] = new float[2];
        final float rightDisplacement[] = new float[2];

        leftDisplacement[0] = muscleSetSettings[paramOffset] - leftCornerBase[0];
        leftDisplacement[1] = muscleSetSettings[paramOffset + 1] - leftCornerBase[1];

        rightDisplacement[0] = muscleSetSettings[paramOffset + 2] - rightCornerBase[0];
        rightDisplacement[1] = muscleSetSettings[paramOffset + 3] - rightCornerBase[1];

        int cnt = 0;
        int index = 0;

        upperLip.getBSpline().updateBSpline();
        lowerLip.getBSpline().updateBSpline();
        upperLip.getBSplineBase().updateBSpline();
        lowerLip.getBSplineBase().updateBSpline();
        for (int i : indices)
        {
            index = i * 3;
            vertex[0] = vertices[index];
            vertex[1] = vertices[index + 1];
            vertex[2] = vertices[index + 2];
            if (leftIntensity[cnt] != 0)
            {
                dstArray[index] += leftIntensity[cnt] * leftDir[0] * leftDisplacement[0];
                dstArray[index + 1] += leftIntensity[cnt] * leftDisplacement[1];
                dstArray[index + 2] += leftIntensity[cnt] * leftDir[1] * leftDisplacement[0];
            }

            if (rightIntensity[cnt] != 0)
            {
                dstArray[index] += rightIntensity[cnt] * rightDir[0] * rightDisplacement[0];
                dstArray[index + 1] += rightIntensity[cnt] * rightDisplacement[1];
                dstArray[index + 2] += rightIntensity[cnt] * rightDir[1] * rightDisplacement[0];
            }

            cnt++;
        }
    }

    /**
     * \brief get the left corner of the mouth
     */
    public float getLeftX()
    {
        return muscleSetSettings[paramOffset];
    }

    /**
     * \brief get the left corner of the mouth
     */
    public float getLeftY()
    {
        return muscleSetSettings[paramOffset + 1];
    }

    /**
     * \brief get the right corner of the mouth
     */
    public float getRightX()
    {
        return muscleSetSettings[paramOffset + 2];
    }

    /**
     * \brief get the right corner of the mouth
     */
    public float getRightY()
    {
        return muscleSetSettings[paramOffset + 3];
    }

    /**
     * \brief set the slope/direction for the left and right corner along the face. displacement direction in order to move vertices along the face.
     */
    private void setDirectionXZVectors()
    {
        float vertexClosestLeft[] = new float[3];
        float vertexClosestRight[] = new float[3];
        float vertexMostLeft[] = new float[3];
        float vertexMostRight[] = new float[3];

        getVertex(this.mouthData[0], vertexClosestLeft);
        getVertex(this.mouthData[1], vertexClosestRight);
        getVertex(this.mouthData[2], vertexMostLeft);
        getVertex(this.mouthData[3], vertexMostRight);

        this.leftDir[0] = vertexClosestLeft[0] - vertexMostLeft[0];
        this.leftDir[1] = vertexClosestLeft[2] - vertexMostLeft[2];
        float length = (float) Math.sqrt(leftDir[0] * leftDir[0] + leftDir[1] * leftDir[1]);

        leftDir[0] /= length;
        leftDir[1] /= length;

        this.rightDir[0] = vertexClosestRight[0] - vertexMostRight[0];
        this.rightDir[1] = vertexClosestRight[2] - vertexMostRight[2];
        length = (float) Math.sqrt(rightDir[0] * rightDir[0] + rightDir[1] * rightDir[1]);
        rightDir[0] /= -length;
        rightDir[1] /= -length;
    }

    /**
     * \brief set the mouth data determines the vertices closest to the mouth corners and the most left/right vertices.
     */
    private void setMouthData()
    {
        float distanceClosestLeft = Float.MAX_VALUE;
        float distanceClosestRight = Float.MAX_VALUE;
        float distanceMostLeft = Float.MAX_VALUE;
        float distanceMostRight = Float.MIN_VALUE;
        float distanceTmp;
        float vertex[] = new float[3];
        float vTmp[] = new float[2];

        for (int index : this.indices)
        {
            getVertex(index, vertex);

            vTmp[0] = vertex[0] - muscleSetSettings[paramOffset]; // x
            vTmp[1] = vertex[1] - muscleSetSettings[paramOffset + 1];
            distanceTmp = vTmp[0] * vTmp[0] + vTmp[1] * vTmp[1];
            if (distanceTmp < distanceClosestLeft)
            {
                this.mouthData[0] = index;
                distanceClosestLeft = distanceTmp;
            }

            vTmp[0] = vertex[0] - muscleSetSettings[paramOffset + 2];
            vTmp[1] = vertex[1] - muscleSetSettings[paramOffset + 3];
            distanceTmp = vTmp[0] * vTmp[0] + vTmp[1] * vTmp[1];
            if (distanceTmp < distanceClosestRight)
            {
                this.mouthData[1] = index;
                distanceClosestRight = distanceTmp;
            }

            if (distanceMostLeft > vertex[0])
            {
                this.mouthData[2] = index;
                distanceMostLeft = vertex[0];
            }

            if (distanceMostRight < vertex[0])
            {
                this.mouthData[3] = index;
                distanceMostRight = vertex[0];
            }
        }
    }

    /**
     * \brief set the displacement array
     */
    private void setDisplacement()
    {

        float left[] = new float[3];
        float vertex[] = new float[3];
        float vertexRadius[] = new float[3];
        float distance = 0;
        getVertex(this.mouthData[0], left);
        left[0] = muscleSetSettings[paramOffset];
        left[1] = muscleSetSettings[paramOffset + 1];
        float right[] = new float[3];

        getVertex(mouthData[1], right);
        right[0] = muscleSetSettings[paramOffset + 2];
        right[1] = muscleSetSettings[paramOffset + 3];

        float radiusLeft = 0, radiusRight = 0;

        Vec3f.sub(vertexRadius, 0, vertices, this.mouthData[2] * 3, left, 0);
        radiusLeft = Vec3f.length(vertexRadius);

        Vec3f.sub(vertexRadius, 0, vertices, this.mouthData[3] * 3, right, 0);
        radiusRight = Vec3f.length(vertexRadius);

        this.leftIntensity = new float[this.indices.size()];
        this.rightIntensity = new float[this.indices.size()];

        int cnt = 0;

        for (int index : indices)
        {
            Vec3f.sub(vertex, 0, vertices, index * 3, left, 0);
            distance = Vec3f.length(vertex);

            if (distance < radiusLeft)
            {
                distance /= radiusLeft;
                leftIntensity[cnt] = 1.0f - distance;
            }
            else
            {
                leftIntensity[cnt] = 0;
            }

            Vec3f.sub(vertex, 0, vertices, index * 3, right, 0);
            distance = Vec3f.length(vertex);
            if (distance < radiusRight)
            {
                distance /= radiusRight;
                rightIntensity[cnt] = 1.0f - distance;
            }
            else
            {
                rightIntensity[cnt] = 0;
            }
            cnt++;
        }
    }

    /**
     * \brief get the left corner original setting (base)
     */
    public float[] getLeftCornerBase()
    {
        return leftCornerBase;
    }

    /**
     * \brief get the right corner original setting (base)
     */
    public float[] getRightCornerBase()
    {
        return rightCornerBase;
    }

    /**
     * \brief get the displacement direction vector on the left side of the face
     */
    public float[] getLeftDir()
    {
        return leftDir;
    }

    /**
     * \brief get the displacement direction vector on the right side of the face
     */
    public float[] getRightDir()
    {
        return rightDir;
    }

    /**
     * \brief get the intensity array for the left side
     */
    public float[] getLeftIntensity()
    {
        return leftIntensity;
    }

    /**
     * \brief get the intensity array for the right side
     */
    public float[] getRightIntensity()
    {
        return rightIntensity;
    }

    /**
     * \brief get the mouth data array
     */
    public int[] getMouthData()
    {
        return mouthData;
    }

    public void setLowerLip(LipMuscle lowerLipMuscle)
    {
        this.lowerLip = lowerLipMuscle;
        if (this.lowerLip.getMouth() != this)
            this.lowerLip.setMouth(this);
    }

    public void setUpperLip(LipMuscle upperLipMuscle)
    {
        this.upperLip = upperLipMuscle;
        if (this.upperLip.getMouth() != this)
            this.upperLip.setMouth(this);
    }

    /**
     * \brief get the upper lip muscle
     */
    public LipMuscle getUpperLip()
    {
        return upperLip;
    }

    /**
     * \brief get the lower lip muscle
     */
    public LipMuscle getLowerLip()
    {
        return lowerLip;
    }

    public BoundingBox getBoundingBox()
    {
        return boundingBox;
    }

    private void getVertex(int index, float vertex[])
    {
        int i = index * 3;
        vertex[0] = vertices[i];
        vertex[1] = vertices[i + 1];
        vertex[2] = vertices[i + 2];
    }

    /*
     * Set the default muscle Settings array When asked for the contraction this array will be used for retrieval
     */
    @Override
    public void setMuscleSettings(float[] muscleSetSettings)
    {
        this.muscleSetSettings = muscleSetSettings;
        upperLip.setMuscleSettings(muscleSetSettings);
        lowerLip.setMuscleSettings(muscleSetSettings);
    }

    public JawMuscle getJaw()
    {
        return jaw;
    }

    public void setJaw(JawMuscle jaw)
    {
        this.jaw = jaw;
        if (this.jaw.getMouth() != this)
            this.jaw.setMouth(this);
    }
}
