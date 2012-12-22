package hmi.animation;

import hmi.math.Quat4f;
import hmi.math.Vec3f;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.ImmutableList;

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
public class Torso
{
    private final ImmutableList<VJoint> thoracicJoints;
    private final ImmutableList<VJoint> torsoJoints;

    private List<VJoint> gatherJoints(String[] spineGroup, VJoint skeleton)
    {
        List<VJoint> joints = new ArrayList<>();
        for (String sid : spineGroup)
        {
            if (skeleton.getPartBySid(sid) != null)
            {
                joints.add(skeleton.getPart(sid));
            }
        }
        Collections.reverse(joints);
        return joints;
    }

    public Torso(VJoint skeleton)
    {
        List<VJoint> joints = gatherJoints(Hanim.LUMBAR_JOINTS, skeleton);
        thoracicJoints = ImmutableList.copyOf(gatherJoints(Hanim.THORACIC_JOINTS, skeleton));
        joints.addAll(thoracicJoints);
        torsoJoints = ImmutableList.copyOf(joints);
    }

    private double getUniform(int n)
    {
        return 1.0 / (double) n;
    }

    private double getLinearIncrease(int i, int n)
    {
        return (double) i * (2.0 / ((double) n * ((double) n + 1.0)));
    }

    private double getLinearDecrease(int i, int n)
    {
        return ((double) n + 1.0 - (double) i) * (2.0 / ((double) n * ((double) n + 1.0)));
    }

    public void setTorsoRotation(float[] q)
    {
        float []rpy = Vec3f.getVec3f();
        Quat4f.getRollPitchYaw(q, rpy);
        setTorsoRollPitchYawDegrees((float)Math.toDegrees(rpy[0]),(float)Math.toDegrees(rpy[1]),(float)Math.toDegrees(rpy[2]));
    }
    
    public void setTorsoRollPitchYawDegrees(float roll, float pitch, float yaw)
    {

        float currentYaw;
        int j = 1;
        int i = 1;
        int n = torsoJoints.size();
        int m = thoracicJoints.size();
        for (VJoint vj : torsoJoints)
        {
            if (thoracicJoints.contains(vj))
            {
                currentYaw = yaw * (float) getLinearIncrease(j, m);
                j++;
            }
            else
            {
                currentYaw = 0;
            }
            vj.setRollPitchYawDegrees(roll * (float) getUniform(n), pitch * (float) getLinearDecrease(i, n), currentYaw);
            i++;
        }
    }

    public List<String> getJoints()
    {
        return VJointUtils.transformToSidList(torsoJoints);
    }
}
