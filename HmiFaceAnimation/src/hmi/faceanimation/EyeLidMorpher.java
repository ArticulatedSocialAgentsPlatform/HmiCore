package hmi.faceanimation;

import hmi.math.Quat4f;
import hmi.math.Vec3f;

import java.util.Arrays;

/**
 * Rotates the eyelids, given left and right eye joint rotations
 * @author hvanwelbergen
 */
public class EyeLidMorpher
{
    private final String[] morphTargets;
    
    public EyeLidMorpher(String [] morphTargets)
    {
        this.morphTargets = morphTargets;
    }

    public void setEyeLidMorph(float []qLeftEye, float[]qRightEye, FaceController faceController)
    {
        float rpyL[] = Vec3f.getVec3f();
        float rpyR[] = Vec3f.getVec3f();
        Quat4f.getRollPitchYaw(qRightEye, rpyL);
        Quat4f.getRollPitchYaw(qLeftEye, rpyR);
        float morphValue = rpyL[1]+ rpyR[1];
        float values[]=new float [morphTargets.length];
        Arrays.fill(values, morphValue);
        faceController.addMorphTargets(morphTargets,values);
    }
}
