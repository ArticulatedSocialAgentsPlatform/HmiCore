package hmi.neurophysics;

import hmi.math.Quat4f;
import hmi.math.Vec3f;

/**
 * Biomechanical spine rotation model, based on
 * 
 * B Boulic, B Ulicny and Daniel Thalmann, Versatile Walk Engine (2004), in: Journal of Game Development, 1:1(29-- 52)
 * 
 * secondary ref:
 * Augustus A. White and Manohar M. Panjabi, Clinical Biomechanics of the Spine, Lippincott Williams & Wilkins, 1990
 * 
 * 
 * @author Herwin
 */
public final class Torso
{
    private Torso(){}
    
    public static double getUniform(int n)
    {
        return 1.0 / (double) n;
    }

    public static double getLinearIncrease(int i, int n)
    {
        return (double) i * (2.0 / ((double) n * ((double) n + 1.0)));
    }

    public static double getLinearDecrease(int i, int n)
    {
        return ((double) n + 1.0 - (double) i) * (2.0 / ((double) n * ((double) n + 1.0)));
    }
    
    public static void setTorsoRollPitchYawDegrees(float qRes[], float roll, float pitch, float yaw, int nrOfLumbarJoint, int nrOfThoracicJoints)
    {
        setTorsoRollPitchYaw(qRes, (float)Math.toRadians(roll), (float)Math.toRadians(pitch), (float)Math.toRadians(yaw), nrOfLumbarJoint, nrOfThoracicJoints);
    }
    
    public static void setTorsoRotation(float[] qRes, float[] qRot, int nrOfLumbarJoint, int nrOfThoracicJoints)
    {
        float []rpy = Vec3f.getVec3f();
        Quat4f.getRollPitchYaw(qRot, rpy);
        setTorsoRollPitchYaw(qRes, rpy[0], rpy[1], rpy[2], nrOfLumbarJoint, nrOfThoracicJoints);
    }
    
    /**
     * Distribute the rotation specified in roll, pitch, yaw (in radians) over the torso joints
     * @param qRes output: the rotation will be set in this
     * @param roll
     * @param pitch
     * @param yaw
     * @param nrOfLumbarJoint
     * @param nrOfThoracicJoints
     */
    public static void setTorsoRollPitchYaw(float qRes[], float roll, float pitch, float yaw, int nrOfLumbarJoint, int nrOfThoracicJoints)
    {
        float currentYaw;
        int n = nrOfLumbarJoint+nrOfThoracicJoints;
        int m = nrOfThoracicJoints;        
        
        for(int i=1;i<=n;i++)
        {
            if(i<=nrOfLumbarJoint)
            {
                currentYaw = 0;
            }
            else
            {
                currentYaw = yaw * (float) getLinearIncrease(i, m);
            }
            Quat4f.setFromRollPitchYaw(qRes, (i-1)*4,roll * (float) getUniform(n), pitch * (float) getLinearDecrease(i, n), currentYaw);            
        }
    }
}
