/**
 * \file Muscles.java
 * \author N.A.Nijdam
 */

package hmi.animation;

import hmi.graphics.render.GenericMesh;
import hmi.graphics.render.VertexAttribute;

import java.util.ArrayList;

/*
 * possible muscles for use
 * Vector and Sphincter muscle can be added at will
 * 
 * structure of the mouth control
 * JawMuscle
 *      |-- MouthMuscle
 *          |-- (upper)LipMuscle
 *              |-- (lower)LipMuscle
 * 
 * Creating a JawMuscle object will create its depending muscles also.
 * Each muscle must be added separately to the muscle list.
 * example
 * JawMuscle jawMuscle = new JawMuscle(vertices);
 * //set muscle properties (such as the bounding boxes)
 *
 * 
 * muscles.add(jawMuscle);
 * muscles.add(jawMuscle.getMouth());
 * muscles.add(jawMuscle.getMouth().getUpperLip());
 * muscles.add(jawMuscle.getMouth().getLowerLip());
 *
 */

/**
 * \brief the management class for the muscles
 */
public class MuscleSet implements Runnable
{

    private String name;
    private ArrayList<Muscle> muscleList = new ArrayList<Muscle>();
    private Muscle muscleArray[] = new Muscle[0];
    private float muscleSettings[] = new float[0];
    private GenericMesh genericMesh;
    private VertexAttribute vertexBuffer;

    /**
     * \brief the default constructor
     */
    public MuscleSet()
    {
        name = "Muscle settings";
    }

    public MuscleSet(GenericMesh genericMesh)
    {
        name = "Muscle settings";
        this.genericMesh = genericMesh;
        this.vertexBuffer = genericMesh.getVertices();
    }

    /**
     * \brief copy constructor \param muscles the muscles object to be duplicated
     */
    public MuscleSet(MuscleSet muscles)
    {
        name = muscles.getName();
        vertexBuffer = muscles.getVertexBuffer();
        muscleList.addAll(muscles.getMuscleList());
        muscleArray = new Muscle[muscleList.size()];
        System.arraycopy(muscles.getMuscleArray(), 0, muscleArray, 0, muscleArray.length);
        muscleSettings = new float[muscles.getMuscleSettingsCount()];
        System.arraycopy(muscles.getMuscleSettings(), 0, muscleSettings, 0, muscleSettings.length);
    }

    /**
     * \brief get the muscles
     */
    public ArrayList<Muscle> getMuscleList()
    {
        return muscleList;
    }

    /**
     * \brief set the muscles
     */
    public void setMuscles(ArrayList<Muscle> muscleList)
    {
        this.muscleList = muscleList;
    }

    /**
     * \brief get the muscle by name
     */
    public Muscle getMuscle(String name)
    {
        for (Muscle muscle : muscleList)
        {
            if (muscle.getName().equals(name))
            {
                return muscle;
            }
        }
        return null;
    }

    /**
     * \brief get the muscle settings count
     */
    public int getMuscleSettingsCount()
    {
        return muscleSettings.length;
    }

    /**
     * \brief get te labels for eacht muscle settings
     */
    public String[] getMuscleSettingsLabels()
    {
        String names[] = new String[muscleSettings.length];
        String paramNames[] = null;
        int idx = 0;
        for (int i = 0; i < muscleArray.length; i++)
        {
            paramNames = muscleArray[i].getParamNames();
            for (String name : paramNames)
                names[idx++] = name;
        }
        return names;
    }

    public void setGenericMesh(GenericMesh genericMesh)
    {
        this.genericMesh = genericMesh;
        if (genericMesh != null) vertexBuffer = genericMesh.getVertices();
    }

    public GenericMesh getGenericMesh()
    {
        return genericMesh;
    }

    public Muscle[] getMuscleArray()
    {
        return muscleArray;
    }

    public float[] getMuscleSettings()
    {
        return muscleSettings;
    }

    /**
     * \brief adjust the muscles to an array of muscle settings
     */
    public void adjustMusclesToSettings(float[] muscleSettings)
    {
        this.muscleSettings = muscleSettings;
    }

    public VertexAttribute getVertexBuffer()
    {
        return vertexBuffer;
    }

    public void add(Muscle muscle)
    {
        muscleList.add(muscle);
    }

    /*
     * Convert the musclesList to a single float array
     */
    public void init()
    {
        int paramSize = 0;
        muscleArray = new Muscle[muscleList.size()];
        int i = 0;
        for (Muscle muscle : muscleList)
        {
            muscle.setParamOffset(paramSize);
            paramSize += muscle.getParamSize();
            muscleArray[i++] = muscle;
        }
        muscleSettings = new float[paramSize];
    }

    public void initAllMuscles()
    {
        for (Muscle muscle : muscleArray)
        {
            muscle.setVertices(genericMesh.getVertices().getOriginalData());
            muscle.init();
        }
    }

    @Override
    public void run()
    {
        System.arraycopy(vertexBuffer.getOriginalData(), 0, vertexBuffer.getCurrentData(), 0, vertexBuffer.getCurrentData().length);
        for (int i = muscleArray.length; i > 0;)
        {
            i--;
            // if (muscleArray[i].isUpdated())
            muscleArray[i].updateVertices(vertexBuffer.getCurrentData(), muscleSettings);
        }
        vertexBuffer.setModified(true);
    }

    /*
     * all Muscle objects are set to the muscleSetting array from current Muscles object.
     */
    public void setDefault()
    {
        for (Muscle muscle : muscleArray)
            muscle.setMuscleSettings(muscleSettings);
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    @Override
    public String toString()
    {
        return name;
    }

    public MuscleSet getCopy()
    {
        MuscleSet result = new MuscleSet(this);
        result.setName(result.getName() + " copy");
        return result;
    }

    public void setMuscleSettings(float[] muscleSettings)
    {
        this.muscleSettings = muscleSettings;
    }
}
