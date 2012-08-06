package hmi.animation;

import hmi.graphics.render.GenericMesh;

public class GenericMeshAnimator implements Runnable
{

    private MuscleSet muscleSet;
    private VJoint rootVObject;
    private GenericMesh genericMesh;

    public VJoint testVObject;

    public GenericMeshAnimator(GenericMesh genericMesh, VJoint rootVObject)
    {
        this.genericMesh = genericMesh;
        this.rootVObject = rootVObject;
        this.muscleSet = new MuscleSet(genericMesh); // dummy muscleSet
    }

    public void setMuscleSet(MuscleSet muscleSet)
    {
        this.muscleSet = muscleSet;
    }

    public MuscleSet getMuscleSet()
    {
        return muscleSet;
    }

    public GenericMesh getGenericMesh()
    {
        return genericMesh;
    }

    public VJoint getRootVObject()
    {
        return rootVObject;
    }

    @Override
    public void run()
    {
        muscleSet.run();
        synchronized (rootVObject)
        {
            rootVObject.calculateMatrices();
        }
        genericMesh.deform();
    }
}
