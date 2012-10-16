package hmi.animation;

import hmi.math.Vec3f;

/**
 * Does an additive blend of the rotations of two joints and all their children and adds up the root translations:<br>
 * qOut = q1 * q2
 * 
 * @author welberge
 */
public class AdditiveT1RBlend
{
    private final AdditiveRotationBlend rotBlend;
    private VJoint v1, v2;
    private VJoint vOut;

    /**
     * Constructor Assumes that v1.getParts(), v2.getParts() and vOut.getParts()
     * yield part lists of equal size and joint ids
     * 
     * @param v1
     *            input joints 1
     * @param v2
     *            input joints 2
     * @param vOut
     *            output joint
     */
    public AdditiveT1RBlend(final VJoint v1, final VJoint v2, VJoint vOut)
    {
        this.v1 = v1;
        this.v2 = v2;
        this.vOut = vOut;
        rotBlend = new AdditiveRotationBlend(v1, v2, vOut);
    }

    public void blend()
    {
        float t1[]=Vec3f.getVec3f();
        float t2[]=Vec3f.getVec3f();
        v1.getTranslation(t1);
        v2.getTranslation(t2);
        Vec3f.add(t1,t2);
        vOut.setTranslation(t1);
        rotBlend.blend();
    }
}
