package hmi.neurophysics;

/**
 * Saccade information.
 * From http://www.liv.ac.uk/~pcknox/teaching/Eymovs/params.htm<br>
 * see also http://en.wikipedia.org/wiki/Saccade
 * @author welberge
 *
 */
public final class Saccade
{
    private Saccade(){}
    /**
     * http://www.liv.ac.uk/~pcknox/teaching/Eymovs/params.htm<br>
     * For normal subjects the relationship between saccade amplitude and duration is fairly linear. 
     * The equation of the line through normal subject data is usually 2.2A+21 (where A is the saccade amplitude).
     */
    public static double getSaccadeDuration(double angle)
    {
        double angleDeg = Math.toDegrees(angle);
        return 0.0022*angleDeg;
    }
}
