package hmi.math.signalproc;

public final class RootMeanSquared
{
    public static final double rootMeanSquared(double[] input)
    {
        double rms = 0;
        for (double d : input)
        {
            rms += d * d;
        }
        rms/=input.length;
        return Math.sqrt(rms);
    }
}
