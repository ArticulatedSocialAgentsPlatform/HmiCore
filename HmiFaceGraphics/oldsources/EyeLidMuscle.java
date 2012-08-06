/**
 * \file EyeLidMuscle.java
 * \author N.A.Nijdam
 */
package hmi.animation;

import hmi.graphics.render.BoundingBox;
import hmi.graphics.render.GenericMesh;
import hmi.math.BSpline2f;

/**
 * \brief muscle EyeLidMuscle
 * 
 * The base of the eyelid is a bspline vertices below the bspline are displace with max velocity (1.0) vertices above the bspline are displaced with a
 * decreasing velocity depending on the distance from the bspline. This results in a more natural stretching of the vertices and textures over the
 * mesh.
 * 
 */
public class EyeLidMuscle extends Muscle
{

    private BSpline2f bSpline = new BSpline2f();
    private float displacement[];
    private float zOffset = 0.04f;
    private float contractionScalar = 1.0f;
    private BoundingBox boundingBox = new BoundingBox();
    private boolean negative = false;

    /**
     * \brief the default constructor \param avatar the avatar \param name the name \param eye the parent eye
     */
    public EyeLidMuscle(String name, GenericMesh genericMesh)
    {
        super(name, genericMesh);
        paramSize = 1;
    }

    /**
     * \brief calculated the vertex indices Calculate the indices to be used Also precalculate displacement values for the max contraction (eyelid
     * closed)
     */
    @Override
    public void calculateVertexIndices()
    {
        float p[] = null;
        if (negative)
        {
            p = new float[] { boundingBox.vMin[0], boundingBox.vMax[1], boundingBox.vMax[0], boundingBox.vMax[1] };
        }
        else
        {
            p = new float[] { boundingBox.vMin[0], boundingBox.vMin[1], boundingBox.vMax[0], boundingBox.vMin[1] };
        }
        bSpline.setPoints(p);
        if (bSpline.getControlPoints() == null)
        {
            float cP[] = new float[] { (boundingBox.getCenter()[0]) - p[0], (boundingBox.vMax[1]) - p[1], (boundingBox.getCenter()[0]) - p[2],
                    (boundingBox.vMax[1]) - p[3] };
            bSpline.setControlPoints(cP);
            bSpline.setSegments(32);
        }
        bSpline.updateAbsolute();
        bSpline.updateBSpline();
        bSpline.calculateSpline();

        // bSpline = new BSpline2f(p, 0, cP, 0, 32);

        // super.calculateVertexIndices();

        indices.clear();
        indices.addAll(boundingBox.extractVertexIndices(vertices));

        if (!this.indicesFiltered.isEmpty())
        {
            indices.removeAll(this.indicesFiltered);
        }

        calculateDisplacement();
    }

    /**
     * \brief initialize the eyelid muscle performs the calculate vertex indices.
     */
    @Override
    public void init()
    {
        if (muscleSetSettings == null)
        {
            System.out.println("Unable init jawmuscle without musclesettings ");
            return;
        }
        calculateVertexIndices();
    }

    /**
     * \brief update the vertices by contraction * displacement
     */
    @Override
    public void updateVertices(float[] dstArray, float muscleSettings[])
    {
        int index;
        int cnt = 0;

        for (int i : this.indices)
        {
            index = i * 3;
            dstArray[index + 1] -= this.displacement[cnt * 3 + 1] * muscleSettings[paramOffset];
            dstArray[index + 2] += muscleSettings[paramOffset] * displacement[cnt * 3 + 2]; // + this.eye.getEyeOffset()[2];
            cnt++;
        }
        // TODO: linkage with the eye offset parameter
        // this.eye.getEyeLids()[Avatar.UPPER].getEyeLash().setEyeLashYOffset(-contraction * 0.2f);
    }

    /**
     * \brief precaluclate the displacement of the eyelid with a contraction of 1
     */
    private void calculateDisplacement()
    {

        this.displacement = new float[this.indices.size() * 3]; // displacement vectors
        float intensity;
        int index;
        int cnt = 0;

        for (int i : this.indices)
        {
            index = i * 3;

            float bCoord[] = this.bSpline.getSplineCoord((vertices[index] - boundingBox.vMin[0]) / boundingBox.getWidth());
            if (negative)
            {
                if (vertices[index + 1] > bCoord[1])
                {
                    intensity = contractionScalar;
                }
                else
                {
                    intensity = contractionScalar - ((float) Math.sqrt((vertices[index + 1] - bCoord[1]) / (boundingBox.vMin[1] - bCoord[1])))
                            * contractionScalar;
                }
                // intensity -=intensity;
                this.displacement[cnt * 3 + 1] = (vertices[index + 1] - boundingBox.vMax[1]) * intensity;
            }
            else
            {
                if (vertices[index + 1] < bCoord[1])
                {
                    intensity = contractionScalar;
                }
                else
                {
                    intensity = contractionScalar - ((float) Math.sqrt((vertices[index + 1] - bCoord[1]) / (boundingBox.vMax[1] - bCoord[1])))
                            * contractionScalar;
                }
                this.displacement[cnt * 3 + 1] = (vertices[index + 1] - boundingBox.vMin[1]) * intensity;
            }

            // this.displacement[cnt * 3] = 0.0f;
            this.displacement[cnt * 3 + 2] = zOffset;
            cnt++;
        }
    }

    /**
     * \brief get the bspline The bspline forms the abstract eyelid edge
     */
    public BSpline2f getBSpline()
    {
        return bSpline;
    }

    /**
     * \brief get the Z offset The Z offset is used to bring the eyelid a bit to the front as it goes down (closing) otherwise it would clip with the
     * eye. More realistic would be a curved offset (wrapping over the eye)
     */
    public float getZOffset()
    {
        return zOffset;
    }

    /**
     * brief set the Z offset
     */
    public void setZOffset(float offset)
    {
        zOffset = offset;
    }

    /**
     * \brief get the contraction scalar A scalar to manipulate the contraction intensity
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
     * \brief get the array of displacement precalculated values for the maximum displacement of the eyelid.
     */
    public float[] getDisplacement()
    {
        return displacement;
    }

    public float getContraction()
    {
        if (muscleSetSettings != null)
        {
            return muscleSetSettings[paramOffset];
        }
        return 0;
    }

    public void setContraction(float value)
    {
        if (muscleSetSettings != null)
        {
            muscleSetSettings[paramOffset] = value;
        }
    }

    public BoundingBox getBoundingBox()
    {
        return boundingBox;
    }

    public void setBoundingBox(BoundingBox boundingBox)
    {
        this.boundingBox = boundingBox;
    }

    public void setBSpline(BSpline2f bSpline)
    {
        this.bSpline = bSpline;
    }

    public boolean isNegative()
    {
        return negative;
    }

    public void setNegative(boolean negative)
    {
        this.negative = negative;
    }

}
