package hmi.animation;

import static hmi.testutil.math.Vec3fTestUtil.assertVec3fEquals;
import hmi.math.Vec3f;

import org.junit.Test;

/**
 * Unit tests for AdditiveT1RBlend 
 * @author hvanwelbergen
 *
 */
public class AdditiveT1RBlendTest
{
    private final float PRECISION = 0.0001f;
    
    private VJoint setupTestJointStructure(String id)
    {
        VJoint vj1 = new VJoint(id+"v1", "v1");
        VJoint vj2 = new VJoint(id+"v2", "v2");
        VJoint vj3 = new VJoint(id+"v3", "v3");
        VJoint vj4 = new VJoint(id+"v4", "v4");
        vj1.addChild(vj2);
        vj1.addChild(vj4);
        vj2.addChild(vj3);
        return vj1;
    }
    
    @Test
    public void testTranslation()
    {
        VJoint v1 = setupTestJointStructure("v1");
        VJoint v2 = setupTestJointStructure("v2");
        VJoint vOut = v1.copyTree("vOut");
        AdditiveT1RBlend blend = new AdditiveT1RBlend(v1,v2,vOut);
        v2.setTranslation(Vec3f.getVec3f(1,2,3));
        v1.setTranslation(Vec3f.getVec3f(2,3,4));
        blend.blend();
        float vOutTrans[]=Vec3f.getVec3f();
        vOut.getTranslation(vOutTrans);
        assertVec3fEquals(Vec3f.getVec3f(3,5,7),vOutTrans,PRECISION);
    }
}
