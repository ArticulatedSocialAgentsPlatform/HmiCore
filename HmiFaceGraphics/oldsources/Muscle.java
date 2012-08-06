/**
 * \file Muscle.java
 * \author N.A.Nijdam
 */

package hmi.animation;

import hmi.graphics.render.GenericMesh;

import java.util.ArrayList;

/**
 * \brief the base class for a muscle
 */
public abstract class Muscle
{
    // indices that are in the muscle area of influence and are used.
    protected final ArrayList<Integer> indices = new ArrayList<Integer>();
    // indices that are in the muscle area of influence and are NOT used.
    protected final ArrayList<Integer> indicesFiltered = new ArrayList<Integer>();
    protected boolean update = true;
    protected int paramOffset = 0;
    // important always set the amount of params to be reserved for the muscle
    // mostly atleast one param is always needed (for example the contraction)
    protected int paramSize = 1;
    protected String paramNames[] = new String[0];
    protected float muscleSetSettings[];

    protected String name = "";
    protected GenericMesh genericMesh;
    protected float vertices[]; // pointer to the a vertices array

    /**
     * \brief the default constructor \param avatar the avatar \param name the name
     */
    public Muscle(String name, GenericMesh genericMesh)
    {
        this.name = name;
        this.genericMesh = genericMesh;
        this.vertices = genericMesh.getVertices().getOriginalData();
    }

    /**
     * \brief the copy constructor \param muscle the muscle to duplicate
     */
    public Muscle(Muscle muscle)
    {
        name = new String(muscle.getName());
        paramOffset = muscle.getParamOffset();
        paramSize = muscle.getParamSize();
        indicesFiltered.addAll(muscle.getIndicesFiltered());
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    @Override
    public String toString()
    {
        return name;
    }

    /**
     * \brief the initialize method for the muscle
     */
    abstract public void init();

    /**
     * \brief calculate the vertices to be used by the muscle, based on the given mesh \param mesh the mesh to be used
     */
    abstract public void calculateVertexIndices();

    /**
     * \brief update the displacement of the vertices \param dstArray the first array (used as the vertices array)
     */
    abstract public void updateVertices(float dstArray[], float muscleSettings[]);

    public ArrayList<Integer> getIndices()
    {
        return indices;
    }

    /**
     * \brief get the filtered vertices
     */
    public ArrayList<Integer> getIndicesFiltered()
    {
        return indicesFiltered;
    }

    /**
     * \brief check if the muscle is updated.
     */
    public boolean isUpdated()
    {
        return update;
    }

    /**
     * \brief set that the muscle is updated
     */
    public void setUpdate(boolean update)
    {
        this.update = update;
    }

    /**
     * \brief set the contraction by array Note: Override for multiple contraction values Also make sure that paramSize is correctly set
     */
    public void setMuscleSettings(float muscleSettings[], float[] paramValues)
    {
        muscleSettings[paramOffset] = paramValues[0];
        update = true;
    }

    /**
     * \brief filter a vertex
     */
    public void filterVertex(Integer vertexIndex)
    {
        synchronized (indices)
        {
            indices.remove(vertexIndex);
        }
        synchronized (indicesFiltered)
        {
            indicesFiltered.add(vertexIndex);
        }
    }

    /**
     * \brief restore a vertex
     */
    public void restoreVertex(Integer vertexIndex)
    {
        synchronized (indices)
        {
            indices.add(vertexIndex);
        }
        synchronized (indicesFiltered)
        {
            indicesFiltered.remove(vertexIndex);
        }
    }

    public void setVertices(float[] vertices)
    {
        this.vertices = vertices;
    }

    public void setParamOffset(int offset)
    {
        paramOffset = offset;
    }

    public int getParamOffset()
    {
        return paramOffset;
    }

    public int getParamSize()
    {
        return paramSize;
    }

    public String[] getParamNames()
    {
        return paramNames;
    }

    public GenericMesh getGenericMesh()
    {
        return genericMesh;
    }

    /*
     * Set the default muscle Settings array When asked for the contraction this array will be used for retrieval
     */
    public void setMuscleSettings(float[] muscleSetSettings)
    {
        this.muscleSetSettings = muscleSetSettings;
    }

    public float[] getMuscleSettings()
    {
        return muscleSetSettings;
    }
}