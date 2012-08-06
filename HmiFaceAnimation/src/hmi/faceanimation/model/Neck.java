package hmi.faceanimation.model;

import hmi.animation.VJoint;
import hmi.math.Quat4f;

public class Neck
{
    private VJoint joint;
    private float au; // MPEG-4s angular unit.
    float pitch, yaw, roll;

    public Neck(VJoint joint, Head head)
    {
        this.joint = joint;
        au = head.getFAPU("AU");
    }

    public void setPitchValue(float value)
    {
        pitch = value * au + (float) Math.PI;
    }

    public void setYawValue(float value)
    {
        yaw = value * au + (float) Math.PI;
    }

    public void setRollValue(float value)
    {
        roll = value * au + (float) Math.PI;
    }

    /** Called when the face deformations are actually applied to the face... */
    public void copy()
    {
        float[] q = new float[4];
        Quat4f.setFromRollPitchYaw(q, roll, pitch, yaw);
        if (joint != null)
            joint.setRotation(q);
    }
}
