package hmi.math.signalproc;

/**
 * Calculates the root mean square of an array
 * @author herwinvw
 *
 */
public final class RootMeanSquare
{
    public static final double rootMeanSquare(double input[])
    {
        double rms = 0;
        for (double d : input)
        {
            rms += d * d;
        }
        return rms/input.length;
    }
}
