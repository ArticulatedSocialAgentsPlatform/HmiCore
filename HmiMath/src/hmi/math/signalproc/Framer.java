package hmi.math.signalproc;

/**
 * Splits an array into frames
 * @author herwinvw
 * 
 */
public final class Framer
{
    private Framer()
    {
    }

    public final static double[][] frame(double input[], int frameSize, int frameStep)
    {
        int l = input.length / frameStep;
        if( (l-1)*frameStep+frameSize>input.length)
        {
            l--;
        }
        double[][] output = new double[l][];
        int k =0;
        for (int i = 0; i < l*frameStep; i += frameStep)
        {
            output[k] = new double[frameSize];
            for (int j = 0; j < frameSize; j++)
            {
                output[k][j] = input[i + j];
            }
            k++;
        }
        return output;
    }
}
