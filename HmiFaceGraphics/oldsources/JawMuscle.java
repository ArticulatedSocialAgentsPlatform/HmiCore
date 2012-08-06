/**
 * \file JawMuscle.java
 * \author N.A.Nijdam
 */

package hmi.animation;

import hmi.graphics.render.*;

/**
 * \brief the jaw muscle The jaw muscle is basically a rotation bounding box. The jaw Muscle also creates the mouth muscle
 */
public class JawMuscle extends Muscle
{

    /*
     * Params contraction
     * 
     * count 1
     */

    private float[] intensity = null;
    private float contractionScalar = 1.0f;
    private float rotationScalar = 1.0f;
    private float displacementExtension = 0.0f;
    private MouthMuscle mouthMuscle = null;
    private float offsetPlane[] = new float[2];

    private BoundingBox boundingBox = new BoundingBox();
    private VJoint lTeeth = new VJoint();
    private VJoint tongue = new VJoint();

    /**
     * \brief the default constructor \param avatar the avatar \param name the name
     */
    public JawMuscle(String name, GenericMesh genericMesh)
    {
        super(name, genericMesh);
        /*
         * this.lowerTeeth = avatar.getHead().getTeeth()[Avatar.LOWER]; this.tongue = this.avatar.getHead().getTongue();
         */
        mouthMuscle = new MouthMuscle("Mouth control", genericMesh);
        mouthMuscle.setJaw(this);
        paramSize = 1;

    }

    /**
     * \brief calculate the vertex indices for the jaw
     */
    @Override
    public void calculateVertexIndices()
    {
        indices.clear();
        indices.addAll(boundingBox.extractVertexIndices(vertices));

        if (!this.indicesFiltered.isEmpty())
        {
            indices.removeAll(this.indicesFiltered);
        }

        calculateDisplacement();
    }

    /**
     * \brief initialize the jaw muscle
     */
    @Override
    public void init()
    {
        if (mouthMuscle == null)
        {
            System.out.println("Unable init jawmuscle without mouth is set");
        }
        if (muscleSetSettings == null)
        {
            System.out.println("Unable init jawmuscle without musclesettings ");
            return;
        }
        calculateVertexIndices();
    }

    /**
     * \brief update the vertices
     */
    @Override
    public void updateVertices(float[] dstArray, float muscleSettings[])
    {

        int index;
        int cnt = 0;

        for (int i : this.indices)
        {
            index = i * 3;
            dstArray[index + 1] -= this.intensity[cnt] * muscleSettings[paramOffset];
            cnt++;
        }

        float scalar = displacementExtension * muscleSettings[paramOffset];

        this.lTeeth.setAxisAngle(1, 0, 0, scalar);
        this.tongue.setAxisAngle(1, 0, 0, scalar);
    }

    /**
     * \brief precaluclate the displacement of the eyelid with a contraction of 1
     */
    private void calculateDisplacement()
    {
        this.intensity = new float[this.indices.size()]; // displacement vectors
        float intens;
        int cnt = 0, index;
        float vertex[] = new float[3];
        float mouth = mouthMuscle.getRightX() - mouthMuscle.getLeftX();

        displacementExtension = (float) Math.toDegrees(Math.atan2(boundingBox.getHeight(), boundingBox.getDepth())) * rotationScalar;

        for (int i : this.indices)
        {
            index = i * 3;
            vertex[0] = vertices[index];
            vertex[1] = vertices[index + 1];
            vertex[2] = vertices[index + 2];

            intens = (float) Math.sqrt((vertex[2] - boundingBox.getMinVertex()[2]) / boundingBox.getDepth());
            if (vertex[2] < offsetPlane[0])
            {
                intens *= ((float) Math.sqrt((boundingBox.getMaxVertex()[1] - vertex[1]) / boundingBox.getHeight())) * intens;
            }
            else
            {
                intens *= 0.8f;
            }

            if (vertex[0] > mouthMuscle.getLeftX() && vertex[0] < mouthMuscle.getRightX() && vertex[1] > offsetPlane[1])
            {
                // System.out.println(""+ vertex[1]);
                intens *= (float) Math.sin(((vertex[0] - mouthMuscle.getLeftX()) / mouth) * Math.PI);
            }

            this.intensity[cnt] = intens * contractionScalar;
            cnt++;
        }
    }

    /**
     * \brief filter a vertex
     */
    @Override
    public void filterVertex(Integer vertexIndex)
    {
        super.filterVertex(vertexIndex);
        init();
    }

    /**
     * \brief restore a vertex
     */
    @Override
    public void restoreVertex(Integer vertexIndex)
    {
        super.restoreVertex(vertexIndex);
        init();
    }

    /**
     * \brief set the contraction scalar
     */
    public float getContractionScalar()
    {
        return contractionScalar;
    }

    /**
     * \brief set the contraction scalar
     */
    public void setContractionScalar(float contractionScalar)
    {
        this.contractionScalar = contractionScalar;
    }

    public void setMouth(MouthMuscle mouthMuscle)
    {
        this.mouthMuscle = mouthMuscle;
    }

    /**
     * \brief get the mouth muscle
     */
    public MouthMuscle getMouth()
    {
        return mouthMuscle;
    }

    /**
     * \brief get the intensity array
     */
    public float[] getIntensity()
    {
        return intensity;
    }

    /**
     * \brief get the displacement extension Extra scalar for rotation of the teeth and tongue (rotate slightly when openening the jaw)
     */
    public float getDisplacementExtension()
    {
        return displacementExtension;
    }

    public BoundingBox getBoundingBox()
    {
        return boundingBox;
    }

    public void setBoundingBox(BoundingBox boundingBox)
    {
        this.boundingBox = boundingBox;
    }

    public float getContraction()
    {
        if (muscleSetSettings != null)
            return muscleSetSettings[paramOffset];
        return 0;
    }

    public void setContraction(float value)
    {
        if (muscleSetSettings != null)
            muscleSetSettings[paramOffset] = value;
    }

    public float getRotationScalar()
    {
        return rotationScalar;
    }

    public void setRotationScalar(float rotationScalar)
    {
        this.rotationScalar = rotationScalar;
    }

    public float getOffsetZ()
    {
        return offsetPlane[0];
    }

    public void setOffsetZ(float offsetZ)
    {
        this.offsetPlane[0] = offsetZ;
    }

    public float getOffsetY()
    {
        return offsetPlane[1];
    }

    public void setOffsetY(float offsetY)
    {
        this.offsetPlane[1] = offsetY;
    }

    public float[] getOffsetPlane()
    {
        return offsetPlane;
    }

    public VJoint getLTeeth()
    {
        return lTeeth;
    }

    public void setLTeeth(VJoint teeth)
    {
        lTeeth = teeth;
    }

    public VJoint getTongue()
    {
        return tongue;
    }

    public void setTongue(VJoint tongue)
    {
        this.tongue = tongue;
    }
}
