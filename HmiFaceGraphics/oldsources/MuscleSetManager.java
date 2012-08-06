package hmi.animation;

import hmi.graphics.render.GenericMesh;

import java.util.HashSet;

public class MuscleSetManager
{
    private static HashSet<MuscleSet> muscleSets = new HashSet<MuscleSet>();

    /**
     * \brief put a muscles settings object in the list of loaded muscle settings
     */
    public static void put(MuscleSet muscles)
    {
        muscleSets.add(muscles);
    }

    public static HashSet<MuscleSet> getValues()
    {
        return muscleSets;
    }

    /**
     * The combination of GenericMesh and MuscleSet name must be unique, otherwise the first found result will be returned
     * 
     * @param genericMesh
     * @param muscleSetName
     * @return
     */
    public static MuscleSet get(GenericMesh genericMesh, String muscleSetName)
    {
        for (MuscleSet muscleSet : muscleSets)
            if (genericMesh == muscleSet.getGenericMesh() && muscleSet.getName().equals(muscleSetName))
                return muscleSet;
        return null;
    }
}
