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
}
