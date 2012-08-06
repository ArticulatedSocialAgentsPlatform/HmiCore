package hmi.animation;

public class Expression
{
    protected String name;
    protected MuscleSet muscleSet;
    protected float muscleSettings[];

    public Expression(String name, MuscleSet muscleSet)
    {
        this.name = name;
        this.muscleSet = muscleSet;
        muscleSettings = new float[muscleSet.getMuscleSettings().length];
        System.arraycopy(muscleSet.getMuscleSettings(), 0, muscleSettings, 0, muscleSettings.length);
    }

    public void setDefault()
    {
        // first check if the muscleset didn't change, if it did then reset the muscle settings
        // TODO: make a method for copy existing values, otherwise the complete expression must be remade;
        if (muscleSettings.length != muscleSet.getMuscleSettingsCount())
        {
            muscleSettings = new float[muscleSet.getMuscleSettings().length];
            System.arraycopy(muscleSet.getMuscleSettings(), 0, muscleSettings, 0, muscleSettings.length);
        }
        muscleSet.setMuscleSettings(muscleSettings);
        muscleSet.setDefault();
    }

    public MuscleSet getMuscleSet()
    {
        return muscleSet;
    }

    public void setMuscleSet(MuscleSet muscleSet)
    {
        this.muscleSet = muscleSet;
    }

    public float[] getMuscleSettings()
    {
        return muscleSettings;
    }

    public void setMuscleSettings(float[] muscleSettings)
    {
        this.muscleSettings = muscleSettings;
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

}
