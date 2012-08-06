/**
 * \file VectorMuscle.java
 * \author N.A.Nijdam
 */

package hmi.animation;

import java.util.logging.*;

import hmi.graphics.render.GenericMesh;
import hmi.math.Vec3f;

/**
 * \brief the vector muscle K. Waters Vector muscle http://doi.acm.org/10.1145/37401.37405
 * 
 * V'= f(K*A*R*V) V = vertex {x,y,z} from mesh K = spring constant A = angular displacement R = radial displacement factor
 * 
 * A = cos (u/pi * pi/2) u = is the angle between V1 V2 and V1 V
 * 
 * D = distance(V1,V); if D < fallStart R = cos ((1 - D/fallStart) pi/2 else if D < fallEnd R = cos ((D - fallStart) / (fallEnd - fallStart) pi/2)
 */
public class VectorMuscle extends Muscle
{

    // parameters (get and set methods)
    private float head[] = new float[3]; // muscle head position (V2)
    private float tail[] = new float[3]; // muscle tail position (V1)
    private float zoneOfInfluence; // angle in degrees
    private float fallStart; // real radial distance
    private float fallFin; // real radial distance
    private float elasticity; // E >= 1 (differs from original as it also represents the spring constant)
    private float displacement[];
    // preCalculated variables
    private float headTailDirection[] = new float[3]; // normalized

    /**
     * \brief the default constructor \param avatar the avatar \param name the name
     */
    public VectorMuscle(String name, GenericMesh genericMesh)
    {
        super(name, genericMesh);
        elasticity = 1.0f;
        paramSize = 1; // important always set the amount of params to be reserved for the muscle
    }

    /**
     * \brief intialize the vector muscle
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
     * \brief update the vertices displacement
     */
    @Override
    public void updateVertices(float dstArray[], float muscleSettings[])
    {
        int vertIndex, vertIndex2;

        for (int i = indices.size(); i > 0;)
        {
            i--;
            vertIndex = indices.get(i) * 3;
            vertIndex2 = i * 3;
            dstArray[vertIndex] -= displacement[vertIndex2] * muscleSettings[paramOffset];
            dstArray[vertIndex + 1] -= displacement[vertIndex2 + 1] * muscleSettings[paramOffset];
            dstArray[vertIndex + 2] -= displacement[vertIndex2 + 2] * muscleSettings[paramOffset];
        }
    }

    /**
     * \brief Calculate the vertex indices using the private model variable
     */
    @Override
    public void calculateVertexIndices()
    {
        indices.clear();
        float vertex[] = new float[3];
        float headVertexVector[] = new float[3];
        float distance = 0;

        Vec3f.sub(headTailDirection, head, tail);
        Vec3f.normalize(headTailDirection);

        // loop for all vertices
        for (int i = 0; i < vertices.length;)
        {
            // get vertice position
            vertex[0] = vertices[i++];
            vertex[1] = vertices[i++];
            vertex[2] = vertices[i++];

            // get the head to vertex vector, and calculate the length

            Vec3f.sub(headVertexVector, 0, vertex, 0, tail, 0);
            distance = Vec3f.length(headVertexVector);

            // check if the vertex lies within the muscle fall (thus length < fallFin)
            if (distance < fallFin)
            {
                // now check if the vertex lies in the zone of effect.
                Vec3f.normalize(headVertexVector);
                float angle = (float) Math.toDegrees(Math.acos(Vec3f.dot(headTailDirection, headVertexVector)));

                if (angle < zoneOfInfluence)
                {
                    indices.add((i / 3) - 1);
                }
            }
        }
        if (!this.indicesFiltered.isEmpty())
        {
            indices.removeAll(this.indicesFiltered);
        }

        updateAllVertices();
    }

    /**
     * \brief set the displacement array
     */
    public void updateAllVertices()
    {
        displacement = new float[indices.size() * 3];

        int vertIndex, vertIndex2;
        float vertex[] = new float[3];
        float p[] = new float[3];
        float pLength = 0;
        // float directionVector2[] = new float[3];
        float normVector2[] = new float[3];
        float cosBeta, a, r;

        for (int i = indices.size(); i > 0;)
        {
            i--;
            vertIndex = indices.get(i) * 3;
            vertIndex2 = i * 3;

            vertex[0] = vertices[vertIndex];
            vertex[1] = vertices[vertIndex + 1];
            vertex[2] = vertices[vertIndex + 2];

            Vec3f.sub(p, 0, vertex, 0, tail, 0);
            pLength = Vec3f.length(p);
            normVector2[0] = p[0] / pLength;
            normVector2[1] = p[1] / pLength;
            normVector2[2] = p[2] / pLength;

            cosBeta = (float) Math.acos(Vec3f.dot(headTailDirection, normVector2));

            a = (float) Math.cos((cosBeta / Math.toRadians(zoneOfInfluence)) * (Math.PI * 0.5));

            if (pLength < this.fallStart)
            {
                r = (float) Math.cos(((1 - pLength / fallStart) * (Math.PI * 0.5)));
            }
            else
            {
                r = (float) Math.cos(((pLength - fallStart) / (fallFin - fallStart) * (Math.PI * 0.5)));
            }

            float mul = elasticity * a * r;

            displacement[vertIndex2] = mul * normVector2[0];
            displacement[vertIndex2 + 1] = mul * normVector2[1];
            displacement[vertIndex2 + 2] = mul * normVector2[2];
        }
    }

    /**
     * \brief get the head coordinates of the vector muscle
     */
    public float[] getHeadCoordinate()
    {
        return head;
    }

    /**
     * \brief set the head coordinate
     */
    public void setHeadCoordinate(float[] head)
    {
        this.head = head;
    }

    /**
     * \brief get the tail coordinate of the vector muscle
     */
    public float[] getTailCoordinate()
    {
        return tail;
    }

    /**
     * \brief set the tail coordinates
     */
    public void setTailCoordinate(float[] tail)
    {
        this.tail = tail;
    }

    /**
     * \brief get the zone of influence of teh vector muscle Zone of influence angle in degrees.
     */
    public float getZoneOfInfluence()
    {
        return zoneOfInfluence;
    }

    /**
     * \brief set the zone of influence
     */
    public void setZoneOfInfluence(float zoneOfInfluence)
    {
        if (zoneOfInfluence < 0)
        {
            zoneOfInfluence -= zoneOfInfluence;
        }
        if (zoneOfInfluence > 360)
        {
            // Console.println("Error: Zone of influence greater than 360 degrees");
            return;
        }
        this.zoneOfInfluence = zoneOfInfluence;
    }

    /**
     * \brief get the fall start value;
     */
    public float getFallStart()
    {
        return fallStart;
    }

    /**
     * \brief set the fall start value
     */
    public void setFallStart(float fallStart)
    {
        if (fallStart < 0.0f)
        {
            Logger.getLogger(VectorMuscle.class.getName()).log(Level.WARNING, "Fall start < 0 not allowed");
            return;
        }
        if (fallStart > fallFin)
        {
            Logger.getLogger(VectorMuscle.class.getName()).log(Level.WARNING, "Fall start > fall fin; reset fall start to fall fin");
            fallStart = fallFin;
        }
        this.fallStart = fallStart;
    }

    /**
     * \brief get the fall fin value
     */
    public float getFallFin()
    {
        return fallFin;
    }

    /**
     * \brief set the fall fin value
     */
    public void setFallFin(float fallFin)
    {
        float vertex[] = new float[3];
        Vec3f.sub(vertex, tail, head);
        float length = Vec3f.length(vertex);
        if (fallFin > length)
        {

            Logger.getLogger(VectorMuscle.class.getName()).log(Level.WARNING, "Fall fin greater than length muscle; reset fallfin to length muscle.");
            fallFin = length;
        }
        if (fallFin < fallStart)
        {
            Logger.getLogger(VectorMuscle.class.getName()).log(Level.WARNING, "Fall fin < fall start; reset fall fin to fall start");
            fallFin = fallStart;
        }
        this.fallFin = fallFin;
    }

    /**
     * \brief get the elasticity
     */
    public float getElasticity()
    {
        return elasticity;
    }

    /**
     * \brief set the elasticity
     */
    public void setElasticity(float elasticity)
    {
        if (elasticity < 0.0)
        {
            Logger.getLogger(VectorMuscle.class.getName()).log(Level.WARNING, "Elasticity must be greater than 0.0");
        }
        else
        {
            this.elasticity = elasticity;
        }
    }

    /**
     * \brief get the displacement array
     */
    public float[] getDisplacement()
    {
        return this.displacement;
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
