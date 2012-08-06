package hmi.faceanimation.model;

import hmi.animation.VJoint;
import hmi.math.Vec3f;

public class LowerJaw
{
    private VJoint joint;

    float[] neutralPos = new float[3];
    float[] disp = new float[3];

    public LowerJaw(VJoint joint)
    {
        this.joint = joint;

        joint.getTranslation(neutralPos);
    }

    public void setOpen(float[] disp)
    {
        this.disp[1] = disp[1];
    }

    public void setThrust(float[] disp)
    {
        this.disp[2] = disp[2];
    }

    public void setShift(float[] disp)
    {
        this.disp[0] = disp[0];
    }

    /** Called when the face deformations are actually applied to the face... */
    public void copy()
    {
        displace();
    }

    private void displace()
    {
        // Set the jaw at an angle and cancel the y-displacement.
        setRotation(disp[1]);
        float tmp = disp[1];
        disp[1] = 0;

        float[] tdisp = new float[3];
        Vec3f.add(tdisp, neutralPos, disp);
        joint.setTranslation(tdisp);

        disp[1] = tmp;
    }

    private void setRotation(float ydisp)
    {
        joint.setRollPitchYawDegrees(0.0f, ydisp * -1200, 0.0f);
    }
}
