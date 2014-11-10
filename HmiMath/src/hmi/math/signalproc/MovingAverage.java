package hmi.math.signalproc;

/**
 * Moving average filter
 * @author herwinvw
 *
 */
public final class MovingAverage
{
    private MovingAverage(){}
    
    /**
     * Calculates a moving average with the given windowSize over the input. If the windowSize is even, the average is calculated
     * with an extra frame on the right side. Start and end values that fall outside the window are copied.
     */
    public static final double[] movingAverageFilter(double input[], int windowSize)
    {
        double[] output = new double[input.length];
        int leftWidth = (windowSize - 1) / 2;
        int rightWidth = windowSize - 1 - leftWidth;

        for (int i = 0; i < leftWidth; i++)
        {
            output[i] = input[i];
        }
        for (int i = leftWidth; i < input.length - rightWidth; i++)
        {
            double average = 0;
            for (int j = i - leftWidth; j <= i + rightWidth; j++)
            {
                average += input[j];
            }
            output[i] = average / windowSize;
        }
        for (int i = input.length - rightWidth; i < input.length; i++)
        {
            output[i] = input[i];
        }
        return output;
    }
}
