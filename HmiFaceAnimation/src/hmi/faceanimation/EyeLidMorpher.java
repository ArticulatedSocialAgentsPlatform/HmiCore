package hmi.faceanimation;

import hmi.math.Quat4f;
import hmi.math.Vec3f;

/**
 * Rotates the eyelids, given left and right eye joint rotations<br>
 * Based on:<br>
 * C. Evinger, K. A. Manning and P. A. Sibony, Eyelid movements. Mechanisms and normal data (1991), in: Investigative ophthalmology and visual science, 32:2(387--400):<br>
 * Eye lid rotation angle is linearly related to the 'up/down gaze angle' (and angles seem also roughly the same).
 * @author hvanwelbergen
 */
public class EyeLidMorpher
{
    private final String[] morphTargets;

    public EyeLidMorpher(String[] morphTargets)
    {
        this.morphTargets = morphTargets;
    }

    public void setEyeLidMorph(float[] qLeftEye, float[] qRightEye, FaceController faceController)
    {
        float rpyL[] = Vec3f.getVec3f();
        float rpyR[] = Vec3f.getVec3f();
        Quat4f.getRollPitchYaw(qRightEye, rpyL);
        Quat4f.getRollPitchYaw(qLeftEye, rpyR);
        float morphValue = rpyL[1] + rpyR[1];
        float values[] = new float[morphTargets.length];
        for (int i = 0; i < morphTargets.length; i++)
        {
            float current = faceController.getCurrentWeight(morphTargets[i]);            
            if (Math.abs(current) > Math.abs(morphValue))
            {
                values[i] = current;
            }
            else
            {
                values[i] = morphValue;
            }
        }
        faceController.setMorphTargets(morphTargets, values);        
    }
}
