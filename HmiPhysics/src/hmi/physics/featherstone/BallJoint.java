package hmi.physics.featherstone;

import hmi.math.Quat4f;
import hmi.math.SpatialTransform;
import hmi.math.SpatialVec;
import hmi.math.Vec3f;

public class BallJoint implements Joint
{
    float tempq[]=new float[4];
    
    /**
     * q is rotation quaternion, qdot = w
     */
    @Override
    public void jcalc(float Xj[], float S[], float vj[], float cj[],float q[], float qdot[])
    {
        Quat4f.conjugate(tempq,q);
        SpatialTransform.setFromQuat4fVec3f(Xj,q,Vec3f.getZero());
        SpatialVec.set(vj, qdot, Vec3f.getZero());
        SpatialVec.setZero(cj);        
    }

    @Override
    public int getQDimension()
    {
        return 4;
    }

    @Override
    public int getQDotDimension()
    {
        return 3;
    }

    @Override
    public int getSWidth()
    {
        return 3;
    }
}
