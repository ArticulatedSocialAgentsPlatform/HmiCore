package hmi.animation;

import static org.junit.Assert.*;

import java.util.Collection;
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
    @Test
    public void testIntersect()
    {
        List<VJoint> vj1 = Lists.newArrayList(new VJoint("h1-j1","j1" ), new VJoint("h1-j2","j2"),  new VJoint("h1-j3","j3"), new VJoint("h1-j4","j4"));
        List<VJoint> vj2 = Lists.newArrayList(new VJoint("h1-j2","j2" ), new VJoint("h1-j4","j4"),  new VJoint("h1-j5","j5"), new VJoint("h1-j9","j9"));
        Collection<VJoint> vj = VJointUtils.intersection(vj1, vj2);
        assertEquals(2,vj.size());
        assertThat(VJointUtils.transformToSidSet(vj),IsIterableContainingInAnyOrder.containsInAnyOrder("j2","j4"));
    }
}