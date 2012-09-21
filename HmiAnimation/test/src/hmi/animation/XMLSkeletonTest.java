/*
 * XMLSkeleton JUnit test
 */

package hmi.animation;

import static org.junit.Assert.assertEquals;
import hmi.math.Vec3f;

import org.junit.Test;

import com.google.common.collect.ImmutableList;

/**
 * JUnit test for hmi.animation.XMLSkeleton
 */
public class XMLSkeletonTest
{

    public XMLSkeletonTest()
    {
    }

    @Test
    public void basics()
    {
        VJoint vj = new VJoint("idRoot");
        vj.setSid("root");        
        VJoint vj1 = new VJoint("joint1");
        vj1.setSid("joint1");
        vj1.setTranslation(Vec3f.getVec3f(1,2,3));
        VJoint vj2 = new VJoint("joint2");
        vj2.setSid("joint2");
        vj2.setTranslation(Vec3f.getVec3f(2,3,4));
        VJoint vj3 = new VJoint("joint3");
        vj3.setSid("joint3");
        vj3.setTranslation(Vec3f.getVec3f(3,4,5));
        vj.addChild(vj1);
        vj.addChild(vj2);
        vj1.addChild(vj3);
        
        XMLSkeleton skelIn = new XMLSkeleton("id1");
        skelIn.setRoots(ImmutableList.of(vj));
        skelIn.setJointSids(ImmutableList.of("root","joint1","joint2","joint3"));
        
        StringBuilder buf = new StringBuilder();
        skelIn.appendXML(buf);
        
        XMLSkeleton skelOut = new XMLSkeleton("id2");
        skelOut.readXML(buf.toString());
        
        VJoint vjRootOut = skelOut.getRoots().get(0);
        assertEquals("root", vjRootOut.getSid());
        assertEquals("joint1", vjRootOut.getChildren().get(0).getSid());
        assertEquals("joint2", vjRootOut.getChildren().get(1).getSid());
        assertEquals("joint3", vjRootOut.getChildren().get(0).getChildren().get(0).getSid());       
        
    }

}
