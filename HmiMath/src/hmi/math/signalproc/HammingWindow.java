package hmi.math.signalproc;

/**
 * Applies a Hammingwindow on data
 * @author herwinvw
 *
 */
public final class HammingWindow
{
    private HammingWindow()
    {
    }

    public static final double hamming(int i, int n)
    {
        return 0.54-0.46*Math.cos((i*2*Math.PI)/(n-1));
    }

    public static final double[] apply(double input[])
    {
        double[] output = new double[input.length];
        for (int i = 0; i < output.length; i++)
        {
            output[i] = hamming(i, output.length) * input[i];
        }
        return output;
    }
}
