package hmi.neurophysics;

import hmi.math.Quat4f;
import hmi.math.Vec4f;

/**
 * Calculates the eye position given a gaze direction, using Listing's Law.
 * 
 * Theoretical background:<br> 
 * Tweed, D. (1997). Three-dimensional model of
 * the human eye-head saccadic system. The Journal of Neurophysiology,
 * 77 (2), pp. 654-666.
 * 
 * @author welberge
 */
public final class ListingsLaw
{
    private ListingsLaw(){}
    
    private static final float[] FORWARD = {0,0,0,1};   //default forward gaze direction in a H-anim skeleton
    
    /**
     * Calculates a natural, unsaturized eye rotation to gaze in direction dir
     * @param dir the direction to gaze to, normalized, in eye coordinates
     * @param result output: the eye rotation quaternion
     */
    public static void listingsEye(float[] dir, float[] result)
    {
        //qeh = (-dir FORWARD)^0.5
        Quat4f.set(result, 0, dir[0],dir[1],dir[2]);
        Vec4f.scale(-1, result);        
        Quat4f.mul(result, FORWARD);
        Quat4f.pow(result, 0.5f);        
    }
}
