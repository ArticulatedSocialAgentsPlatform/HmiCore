package hmi.neurophysics;

import hmi.math.Quat4f;

import org.junit.Test;
import static org.junit.Assert.assertTrue;

public class EyeSaturationTest
{
    @Test
    public void testEyeSaturation()
    {
        float qCurDes[] = Quat4f.getQuat4f();
        float qDes[] = Quat4f.getQuat4f();
        float q[]= Quat4f.getQuat4f();
        Quat4f.set(qCurDes,0.918567f,   -0.3737188f, -0.12871999f, 0.0f);
        Quat4f.set(qDes,0.91856706f, -0.3737188f, -0.12871997f, 0.0f);
        EyeSaturation.sat(qCurDes, qDes, q);
        assertTrue(Math.abs(Quat4f.length(q)-1)<0.0001);
    }
    
    
    public void testIsSaturized()
    {
        assertTrue(EyeSaturation.isSaturized(Quat4f.getIdentity()));
        
        float q[] = new float[4];
        Quat4f.setFromAxisAngle4f(q, 0,1,0,3);
        assertTrue(!EyeSaturation.isSaturized(q));
    }
}
