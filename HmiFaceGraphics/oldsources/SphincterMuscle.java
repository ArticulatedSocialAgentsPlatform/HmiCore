/**
 * \file SphincterMuscle.java
 * \author N.A.Nijdam
 */

package hmi.animation;

import hmi.graphics.render.GenericMesh;
import hmi.math.Vec3f;

public class SphincterMuscle extends Muscle
{

    private float center[] = new float[3];
    private float depth;
    private float longitude; // ly
    private float latitude; // lx
    private float longitudeTension;
    private float latitudeTension;

    volatile private float displacement[];

    /*
     * The semi axes are the default x,y,z axes
     */

    /**
     * \brief the default constructor \param avatar the avatar \param name the name
     */
    public SphincterMuscle(String name, GenericMesh genericMesh)
    {
        super(name, genericMesh);
        paramSize = 1;
    }

    /**
     * \brief calculate the vertex indices
     */
    @Override
    public void calculateVertexIndices()
    {
        // super.calculateVertexIndices();
        this.indices.clear();
        float v[] = new float[3];

        // loop for all vertices
        for (int i = 0; i < vertices.length;)
        {
            // get vertice position
            v[0] = vertices[i++];
            v[1] = vertices[i++];
            v[2] = vertices[i++];

            // get the head to vertex vector, and calculate the length
            Vec3f.sub(v, center);
            float a = v[0] * v[0] / latitude * latitude;
            float b = v[1] * v[1] / longitude * longitude;

            if (a + b <= 1 && (v[2] < depth && v[2] > -depth))
            {
                this.indices.add((i / 3) - 1);
            }
        }
        updateAllVertices();
    }

    /**
     * \brief initialize the sphincter muscle
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
     * \brief update the vertex displacement
     */
    @Override
    public void updateVertices(float[] dstArray, float muscleSettings[])
    {
        int vertIndex, vertIndex2;

        for (int i = this.indices.size(); i > 0;)
        {
            i--;
            vertIndex = this.indices.get(i) * 3;
            vertIndex2 = i * 2;
            dstArray[vertIndex] -= displacement[vertIndex2] * muscleSettings[paramOffset];
            dstArray[vertIndex + 1] -= displacement[vertIndex2 + 1] * muscleSettings[paramOffset];
        }
    }

    /**
     * \brief calculate the displacement array
     */
    public void updateAllVertices()
    {
        displacement = new float[this.indices.size() * 2];

        int vertIndex, vertIndex2;
        float vertex[] = new float[3];

        for (int i = this.indices.size(); i > 0;)
        {
            i--;
            vertIndex = this.indices.get(i) * 3;
            vertIndex2 = i * 2;

            vertex[0] = vertices[vertIndex];
            vertex[1] = vertices[vertIndex + 1];
            vertex[2] = vertices[vertIndex + 2];

            Vec3f.sub(vertex, this.center);
            float pLength = Vec3f.length(vertex);
            float directionVector2[] = new float[] { vertex[0] / pLength, vertex[1] / pLength };

            float mul = (1.0f - (float) Math.sqrt(vertex[0] * vertex[0] / latitude / latitude + vertex[1] * vertex[1] / longitude / longitude));

            displacement[vertIndex2] = mul * directionVector2[0] * this.latitude;
            displacement[vertIndex2 + 1] = mul * directionVector2[1] * this.longitude;
        }
    }

    /**
     * \brief get the longitude value
     */
    public float getLongitude()
    {
        return longitude;
    }

    /**
     * \brief set the longitude value
     */
    public void setLongitude(float longitude)
    {
        this.longitude = longitude;
    }

    /**
     * \brief get the latitude value
     */
    public float getLatitude()
    {
        return latitude;
    }

    /**
     * \brief set the latitude value
     */
    public void setLatitude(float latitude)
    {
        this.latitude = latitude;
    }

    /**
     * \brief get the longitude tension value
     */
    public float getLongitudeTension()
    {
        return longitudeTension;
    }

    /**
     * \brief set the longitude tension value
     */
    public void setLongitudeTension(float longitudeTension)
    {
        this.longitudeTension = longitudeTension;
    }

    /**
     * \brief get the latitude tension value
     */
    public float getLatitudeTension()
    {
        return latitudeTension;
    }

    /**
     * \brief set the latitude tension value
     */
    public void setLatitudeTension(float latitudeTension)
    {
        this.latitudeTension = latitudeTension;
    }

    /**
     * \brief get the displacement array
     */
    public float[] getDisplacement()
    {
        return displacement;
    }

    /**
     * \brief set the displamcement array
     */
    public void setDisplacement(float[] displacement)
    {
        this.displacement = displacement;
    }

    /**
     * \bief get the center of the sphincter muscle
     */
    public float[] getCenter()
    {
        return center;
    }

    /**
     * \brief set the center of the sphincter muscle
     */
    public void setCenter(float[] center)
    {
        this.center = center;
    }

    /**
     * \brief get the depth
     */
    public float getDepth()
    {
        return depth;
    }

    /**
     * \brief set the depth Extra radius filter. (otherwise the back of the avatar-head also is included)
     */
    public void setDepth(float depth)
    {
        this.depth = depth;
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

}
