package hmi.animation;

public class InitMuscle implements Runnable
{

    Muscle muscle = null;

    public InitMuscle(Muscle muscle)
    {
        this.muscle = muscle;
    }

    @Override
    public void run()
    {
        if (muscle != null)
            muscle.init();
    }

}
