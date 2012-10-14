package hmi.animation;

import static org.junit.Assert.*;

import hmi.math.Vec3f;
import hmi.testutil.math.Vec3fTestUtil;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;
import static org.hamcrest.MatcherAssert.assertThat;
import org.hamcrest.collection.*;
/**
 * 
 * @author hvanwelbergen
 *
 */
public class VJointUtilsTest
{
    private static final float PRECISION = 0.001f;
    @Test
    public void testIntersect()
    {
        List<VJoint> vj1 = Lists.newArrayList(new VJoint("h1-j1","j1" ), new VJoint("h1-j2","j2"),  new VJoint("h1-j3","j3"), new VJoint("h1-j4","j4"));
        List<VJoint> vj2 = Lists.newArrayList(new VJoint("h1-j2","j2" ), new VJoint("h1-j4","j4"),  new VJoint("h1-j5","j5"), new VJoint("h1-j9","j9"));
        Collection<VJoint> vj = VJointUtils.intersection(vj1, vj2);
        assertEquals(2,vj.size());
        assertThat(VJointUtils.transformToSidSet(vj),IsIterableContainingInAnyOrder.containsInAnyOrder("j2","j4"));
    }
    
    @Test
    public void createNullRotationCopyTree()
    {
        VJoint joint1 = new VJoint("joint1");
        VJoint joint2 = new VJoint("joint2");
        VJoint joint3 = new VJoint("joint3");
        VJoint joint4 = new VJoint("joint4");
        joint1.addChild(joint2);
        joint1.addChild(joint3);
        joint3.addChild(joint4);
        joint1.setTranslation(1,2,3);
        joint2.setTranslation(2,3,4);
        joint3.setTranslation(3,4,5);
        joint4.setTranslation(6,7,8);
        joint1.setAxisAngle(1,1,0,0.5f);
        joint2.setAxisAngle(1,0,0.5f,1.5f);
        joint3.setAxisAngle(1,0,0,1f);
        joint4.setAxisAngle(0.5f,1,0.5f,3f);
        
        VJoint vj = VJointUtils.createNullRotationCopyTree(joint1,"prefix");
        
        Iterator<VJoint> vjExpIterator = joint1.getParts().iterator();
        Iterator<VJoint> vjActIterator = vj.getParts().iterator();
        
        float []tExp = Vec3f.getVec3f();
        float []tAct = Vec3f.getVec3f();
        while(vjExpIterator.hasNext())
        {
            VJoint vjExp = vjExpIterator.next();
            VJoint vjAct = vjActIterator.next();
            vjExp.getPathTranslation(null, tExp);
            vjAct.getPathTranslation(null, tAct);
            Vec3fTestUtil.assertVec3fEquals(tExp,tAct,PRECISION);
        }
    }
}