package hmi.neurophysics;

/**
 * Hand constraints for the left and right H-Anim hand. 
 * @author Herwin
 *
 */
public final class Hand
{
    /**
     * Get the rotation of finger joint 3 given that of joint 2
     */
    public static final double getDIPRotation(double PIPRotation)
    {
        return (2d/3d)*PIPRotation;
    }
    
    /**
     * Get the rotation of finger joint 2 given that of joint 3
     */
    public static final double getPIPRotation(double DIPRotation)
    {
        return (3d/2d)*DIPRotation;
    }
    
    public static double getMinimumFingerFlexionPIP()
    {
        return Math.toRadians(0d);
    }
    
    public static double getMaximumFingerFlexionPIP()
    {
        return Math.toRadians(110d);
    }
    
    public static double getMaximumFingerFlexionDIP()
    {
        return Math.toRadians(90d);
    }
    
    public static double getMinimumFingerFlexionDIP()
    {
        return Math.toRadians(0d);
    }
    
    public static double getMinimumFingerFlexionMCP()
    {
        return Math.toRadians(0d);
    }
    
    public static double getMaximumFingerFlexionMCP()
    {
        return Math.toRadians(90d);
    }
    
    //spreading angle
    public static double getMaximumFingerAbduction()
    {
        return Math.toRadians(15d);
    }
    
    //bringing together angle
    public static double getMinimumFingerAbduction()
    {
        return Math.toRadians(-15d);
    }
    
    
    //guestimated values below
    public static double getMaximumTMCAbduction()
    {
        return Math.toRadians(60);
    }
    
    public static double getMinimumTMCAbduction()
    {
        return Math.toRadians(-40);
    }
    
    public static double getMinimumTMCFlexion()
    {
        return Math.toRadians(0);
    }
    
    public static double getMaximumTMCFlexion()
    {
        return Math.toRadians(170);
    }
}
