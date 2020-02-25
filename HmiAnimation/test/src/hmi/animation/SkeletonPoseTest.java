/*******************************************************************************
 * Copyright (C) 2009-2020 Human Media Interaction, University of Twente, the Netherlands
 *
 * This file is part of the Articulated Social Agents Platform BML realizer (ASAPRealizer).
 *
 * ASAPRealizer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License (LGPL) as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ASAPRealizer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ASAPRealizer.  If not, see http://www.gnu.org/licenses/.
 ******************************************************************************/
package hmi.animation;

import static hmi.testutil.math.Quat4fTestUtil.assertQuat4fRotationEquivalent;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import hmi.math.Quat4f;
import hmi.testutil.animation.HanimBody;
import hmi.xml.XMLTokenizer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

/**
 * SkeletonPose unit tests
 * @author Herwin, zwiers
 */
public class SkeletonPoseTest
{
    private SkeletonTest skTest;
    private static final float PARAMETER_PRECISION = 0.0001f;

    @Before
    public void init1()
    {

        // FROM SKELETONTEST:
        // float[] t0 = new float[]{0.0f, 1.0f, 0.0f};
        // float[] t00 = new float[]{-0.1f, 1.2f, 0.3f};
        // float[] t01 = new float[]{0.1f, 1.2f, 0.3f};
        // float[] t010 = new float[]{0.10f, 0.5f, 0.010f};
        // float[] t011 = new float[]{0.15f, 0.5f, 0.011f};
        // float[] t012 = new float[]{0.2f, 0.5f, 0.012f};
        // float[] t0110 = new float[]{0.17f, 0.27f, 0.37f};
        //
        // /* not unit quaternions, but ok for the test */
        // float[] q0 = new float[]{0.5f, 0.5f, 0.5f, 0.5f};
        // float[] q00 = new float[]{0.7f, 0.1f, 0.2f, 0.3f};
        // float[] q01 = new float[]{0.6f, 0.2f, 0.2f, 0.3f};
        // float[] q010 = new float[]{0.5f, 0.3f, 0.2f, 0.3f};
        // float[] q011 = new float[]{0.4f, 0.4f, 0.2f, 0.3f};
        // float[] q012 = new float[]{0.3f, 0.5f, 0.2f, 0.3f};
        // float[] q0110 = new float[]{0.2f, 0.6f, 0.2f, 0.3f};
        //
        // float[] s0 = new float[]{1.0f, 2.0f, 3.0f};
        // float[] s00 = new float[]{0.4f, 0.4f, 0.4f};
        // float[] s01 = new float[]{100f, 100f, 100f};
        //

        // vj0 = new VJoint("vj0", "root");
        // vj00 = new VJoint("vj00", "lhip" );
        // vj01 = new VJoint("vj01", "rhip");
        // vj010 = new VJoint("vj010", "rknee0");
        // vj011 = new VJoint("vj011", "rknee1");
        // vj012 = new VJoint("vj012", "rknee2");
        // vj0110 = new VJoint("vj0110", "rfoot");
        //
        // /*
        // * vj0[root]
        // * / \
        // * vj00[lhip] vj01[rhip]
        // * / | \
        // * vj010[rknee0] vj011[rknee1] vj012[rknee2]
        // * |
        // * vj0110[foot]
        // */
        //
        // vj0.addChild(vj00);
        // vj0.addChild(vj01);
        //
        // vj01.addChild(vj010);
        // vj01.addChild(vj011);
        // vj01.addChild(vj012);
        //
        // vj011.addChild(vj0110);
        skTest = new SkeletonTest();
        skTest.init();

    }

    //
    // public void initRotations()
    // {
    //
    // float[] t00 = new float[]{22.0f, 33.0f, 44.0f};
    // float[] r00 = new float[]{0.1f, 0.2f, 0.3f, 0.4f}; // not a proper quaternion, but ok for the test
    // float[] r011 = new float[]{0.15f, 0.25f, 0.35f, 0.45f}; // not a proper quaternion, but ok for the test
    // float[] r012 = new float[]{0.16f, 0.26f, 0.36f, 0.46f}; // not a proper quaternion, but ok for the test
    // float[] r0110 = new float[]{0.17f, 0.27f, 0.37f, 0.47f}; // not a proper quaternion, but ok for the test
    //
    // vj00.setTranslation(t00);
    // vj00.setRotation(r00);
    // vj011.setRotation(r011);
    // vj012.setRotation(r012);
    // vj0110.setRotation(r0110);
    // }

    @Test
    public void emptyTest() throws IOException
    {
        String str = "<SkeletonPose rotationEncoding=\"axisangles\" parts=\"\" encoding=\"T1R\"/>";
        SkeletonPose pose = new SkeletonPose(new XMLTokenizer(str));
        assertTrue(pose.getConfigSize() == 0);
    }

    @Test
    public void constructorTest()
    {
        Skeleton skel = skTest.createSkeleton("skel");
        VJoint root = skel.getRoot();
        assertTrue(root != null); // skTest should have been initialized
        List<String> partList = Arrays.asList(new String[] { "vj00", "rknee1", "vj012", "rfoot" });
        SkeletonPose pose0 = new SkeletonPose(null, skel, partList, "T1R");
        // System.out.println("configsize = " + pose0.getConfigSize());
        assertTrue(pose0.getConfigSize() == 19); // 3 + 4*4

    }
    
    @Test
    public void constructorTest2()
    {
        Skeleton skel = skTest.createSkeleton("skel");
        SkeletonPose pose = new SkeletonPose("pose1",skel,"T1R");
        assertEquals(skel.getRoot().getParts().size()*4+3, pose.getConfigSize());
    }

    @Test
    public void xmlTest() throws IOException
    {
        Skeleton skel = skTest.createSkeleton("skelXml");
        List<String> partList = Arrays.asList(new String[] { "vj00", "rknee1", "vj012", "rfoot" }); // vj00, vj011 vj012 vj0110
        SkeletonPose pose0 = new SkeletonPose("pose0", skel, partList, "T1R");

        assertEquals(3 + 4 * partList.size(), pose0.getConfigSize()); // 3 + 4*4
        float[] config0 = new float[] { 1.0f, 2.0f, 3.0f, 4.0f, 5.0f, 6.0f, 7.0f, 8.0f, 9.0f, 10.0f, 11.0f, 12.0f, 13.0f, 14.0f, 15.0f,
                16.0f, 17.0f, 18.0f, 19.0f };
        pose0.setConfig(config0);

        String encoded0 = pose0.toXMLString();
        XMLTokenizer tokenizer = new XMLTokenizer(encoded0);
        SkeletonPose pose1 = new SkeletonPose(tokenizer);

        String encoded1 = pose1.toXMLString();
        assertEquals(encoded1, encoded0);

        pose0.fromSkeleton();
        float[] config2 = pose0.getConfig();

        final int expectedSize = 3 + 4 * partList.size();
        assertEquals(expectedSize, config2.length);
        float[] expected = { -0.1f, 1.2f, 0.3f, 
                0.7f, 0.1f, 0.2f, 0.3f, 
                0.4f, 0.4f, 0.2f, 0.3f, 
                0.3f, 0.5f, 0.2f, 0.3f, 
                0.2f, 0.6f, 0.2f, 0.3f };
        for (int i = 0; i < expectedSize; i++)
        {
            assertEquals(expected[i], config2[i], PARAMETER_PRECISION);
        }
    }
    
    @Test
    public void testReadXML() throws IOException
    {
        String str = "<SkeletonPose id=\"pose1\" rotationEncoding=\"quaternions\" parts=\"HumanoidRoot r_shoulder l_shoulder\" encoding=\"T1R\">"
                + "0 1 0 1 0 0 0 0 1 0 0 0 0 1 0\n"
                + "</SkeletonPose>";
        SkeletonPose ski = new SkeletonPose(new XMLTokenizer(str));
        assertEquals("pose1", ski.getId());
        assertEquals("quaternions", ski.getRotationEncoding());
        assertThat(new float[]{0,1,0,1,0,0,0,0,1,0,0,0,0,1,0}, equalTo(ski.getConfig()));
        assertThat(new String[]{"HumanoidRoot","r_shoulder","l_shoulder"}, equalTo(ski.getPartIds()));
        assertEquals("T1R", ski.getConfigType());
    }

    @Test
    public void binaryTest() throws IOException
    {

        String tmpdir = System.getProperty("java.io.tmpdir");
        DataOutputStream dataOut = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(tmpdir + "/skeletonposeTest.dat")));
        String[] partIds = new String[] { "vj00", "rknee1", "vj012", "rfoot" };
        List<String> partList = Arrays.asList(partIds);
        Skeleton skel = skTest.createSkeleton("skelBinary");
        SkeletonPose pose0 = new SkeletonPose("pose0", skel, partList, "T1R");
        pose0.fromSkeleton();
        pose0.writeBinary(dataOut);
        dataOut.close();
        DataInputStream dataIn = new DataInputStream(new BufferedInputStream(new FileInputStream(tmpdir + "/skeletonposeTest.dat")));
        SkeletonPose pose1 = new SkeletonPose("pose1", partIds, "T1R");
        pose1.readBinary(dataIn);
        dataIn.close();
        // System.out.println("pose1 binary decoded:\n" + pose1);
        float[] config2 = pose0.getConfig();
        final int expectedSize = 3 + 4 * partList.size();

        assertEquals(expectedSize, config2.length);

        float[] expected = { -0.1f, 1.2f, 0.3f, 
                              0.7f, 0.1f, 0.2f, 0.3f, 
                              0.4f, 0.4f, 0.2f, 0.3f, 
                              0.3f, 0.5f, 0.2f, 0.3f, 
                              0.2f, 0.6f, 0.2f, 0.3f };
        for (int i = 0; i < expectedSize; i++)
        {
            assertEquals(expected[i], config2[i], PARAMETER_PRECISION);
        }
    }
    
    
    @Test
    public void testFilter() throws IOException
    {
        String str = "<SkeletonPose parts=\"HumanoidRoot r_shoulder l_shoulder\" encoding=\"T1R\">"
                + "0 1 0 1 0 0 0 0 1 0 0 0 0 1 0\n"+
              "</SkeletonPose>";
        SkeletonPose pose = new SkeletonPose(new XMLTokenizer(str));        

        VJoint vHuman = new VJoint();
        vHuman.setSid("HumanoidRoot");
        VJoint lSh = new VJoint();
        lSh.setSid("l_shoulder");
        VJoint rSh = new VJoint();
        rSh.setSid("r_shoulder");
        rSh.setRotation(0, 0, 0, 1);
        vHuman.addChild(rSh);
        vHuman.addChild(lSh);
        pose.setTargets(vHuman.getParts().toArray(new VJoint[0]));

        Set<String> filter = new HashSet<String>();
        filter.add("l_shoulder");
        filter.add("HumanoidRoot");
        pose.filterJoints(filter);

        assertEquals(2, pose.getPartIds().length);
        assertEquals("HumanoidRoot", pose.getPartIds()[0]);
        assertEquals("l_shoulder", pose.getPartIds()[1]);

        assertEquals("T1R", pose.getConfigType());
        assertEquals(11, pose.getConfigSize());

        pose.setToTarget();
        float[] q = Quat4f.getQuat4f();
        vHuman.getRotation(q);
        assertQuat4fRotationEquivalent(1, 0, 0, 0, q, PARAMETER_PRECISION);

        lSh.getRotation(q);
        assertQuat4fRotationEquivalent(0, 0, 1, 0, q, PARAMETER_PRECISION);

        rSh.getRotation(q);
        assertQuat4fRotationEquivalent(0, 0, 0, 1, q, PARAMETER_PRECISION);
    }
    
    @Test
    public void testFilterNonExistingJoint() throws IOException
    {
        String str = "<SkeletonPose rotationEncoding=\"quaternions\" parts=\"HumanoidRoot invalid l_shoulder\" encoding=\"T1R\">"
            + "0 1 0 1 0 0 0 0 1 0 0 0 0 1 0\n"+
            "</SkeletonPose>";
        SkeletonPose ski = new SkeletonPose(new XMLTokenizer(str));
        
        
        Set<String> filter = new HashSet<String>();
        filter.add("invalid");
        filter.add("l_shoulder");
        
        ski.setTargets(HanimBody.getLOA1HanimBody().getParts().toArray(new VJoint[0]));
        ski.filterJoints(filter);  
        assertEquals(2, ski.getPartIds().length);
        assertEquals(8, ski.getConfigSize());
    }
    
    @Test
    public void testFilterRemoveRoot() throws IOException
    {
        String str = "<SkeletonPose rotationEncoding=\"quaternions\" parts=\"HumanoidRoot r_shoulder l_shoulder\" encoding=\"T1R\">"
                + "0 1 0 1 0 0 0 0 1 0 0 0 0 1 0\n"
                + "</SkeletonPose>";
        SkeletonPose ski = new SkeletonPose(new XMLTokenizer(str));        

        VJoint vHuman = new VJoint();
        vHuman.setRotation(0, 0, 0, 1);
        vHuman.setSid("HumanoidRoot");
        VJoint lSh = new VJoint();
        lSh.setSid("l_shoulder");
        VJoint rSh = new VJoint();
        rSh.setSid("r_shoulder");
        rSh.setRotation(0, 0, 0, 1);
        vHuman.addChild(rSh);
        vHuman.addChild(lSh);
        ski.setTargets(vHuman.getParts().toArray(new VJoint[0]));

        Set<String> filter = new HashSet<String>();
        filter.add("l_shoulder");
        filter.add("r_shoulder");
        ski.filterJoints(filter);

        assertEquals(2, ski.getPartIds().length);
        assertEquals("r_shoulder", ski.getPartIds()[0]);
        assertEquals("l_shoulder", ski.getPartIds()[1]);

        assertEquals("R", ski.getConfigType());
        assertEquals(8, ski.getConfigSize());

        ski.setToTarget();
        float[] q = Quat4f.getQuat4f();
        vHuman.getRotation(q);
        assertQuat4fRotationEquivalent(0, 0, 0, 1, q, 0.0001f);

        lSh.getRotation(q);
        assertQuat4fRotationEquivalent(0, 0, 1, 0, q, 0.0001f);

        rSh.getRotation(q);
        assertQuat4fRotationEquivalent(0, 1, 0, 0, q, 0.0001f);
    }
    
    @Test
    public void testMirror() throws IOException
    {
        String str = "<SkeletonPose rotationEncoding=\"quaternions\" parts=\"r_shoulder\" encoding=\"R\">"
                + "0 0 1 0 \n" + "</SkeletonPose>";
        SkeletonPose ski = new SkeletonPose(new XMLTokenizer(str));
        ski.readXML(str);
        VJoint vHuman = HanimBody.getLOA1HanimBody();
        ski.setTargets(vHuman.getParts().toArray(new VJoint[0]));
        ski.mirror(vHuman);
        ski.setToTarget();
        
        float q[] = Quat4f.getQuat4f();
        vHuman.getPart(Hanim.r_shoulder).getRotation(q);
        assertQuat4fRotationEquivalent(1, 0, 0, 0, q, 0.0001f);

        vHuman.getPart(Hanim.l_shoulder).getRotation(q);
        assertQuat4fRotationEquivalent(0, 0, -1, 0, q, 0.0001f);
    }
    
    @Test
    public void testMirrorAfterFilter() throws IOException
    {
        String str = "<SkeletonPose rotationEncoding=\"quaternions\" parts=\"r_shoulder l_shoulder\" encoding=\"R\">"
                + "0 0 1 0 0 0 1 0\n"
                + "</SkeletonPose>";
        SkeletonPose ski = new SkeletonPose(new XMLTokenizer(str));
        VJoint vHuman = HanimBody.getLOA1HanimBody();
        ski.setTargets(vHuman.getParts().toArray(new VJoint[0]));
        Set<String> joints = new HashSet<String>();
        joints.add("r_shoulder");
        ski.filterJoints(joints);
        assertEquals(1, ski.getPartIds().length);
        assertEquals("r_shoulder", ski.getPartIds()[0]);

        ski.mirror(vHuman);
        ski.setToTarget();

        float q[] = Quat4f.getQuat4f();
        vHuman.getPart(Hanim.r_shoulder).getRotation(q);
        assertQuat4fRotationEquivalent(1, 0, 0, 0, q, 0.0001f);

        vHuman.getPart(Hanim.l_shoulder).getRotation(q);
        assertQuat4fRotationEquivalent(0, 0, -1, 0, q, 0.0001f);
    }

    @Test
    public void testMirrorAfterFilterBeforeSetTarget() throws IOException
    {
        String str = "<SkeletonPose rotationEncoding=\"quaternions\" parts=\"r_shoulder l_shoulder\" encoding=\"R\">"
                + "0 0 1 0 0 0 1 0\n"                
                + "</SkeletonPose>";
        SkeletonPose ski = new SkeletonPose(new XMLTokenizer(str));        
        Set<String> joints = new HashSet<String>();
        joints.add("r_shoulder");
        ski.filterJoints(joints);
        assertEquals(1, ski.getPartIds().length);
        assertEquals("r_shoulder", ski.getPartIds()[0]);

        ski.mirror(null);

        VJoint vHuman = HanimBody.getLOA1HanimBody();
        ski.setTargets(vHuman.getParts().toArray(new VJoint[0]));
        ski.setToTarget();

        float q[] = Quat4f.getQuat4f();
        vHuman.getPart(Hanim.r_shoulder).getRotation(q);
        assertQuat4fRotationEquivalent(1, 0, 0, 0, q, 0.0001f);

        vHuman.getPart(Hanim.l_shoulder).getRotation(q);
        assertQuat4fRotationEquivalent(0, 0, -1, 0, q, 0.0001f);
    }

    @Test
    public void testMirrorBeforeSetTarget() throws IOException
    {
        String str = "<SkeletonPose rotationEncoding=\"quaternions\" parts=\"r_shoulder\" encoding=\"R\">"
                + "0 0 1 0 \n"+ "</SkeletonPose>";
        SkeletonPose ski = new SkeletonPose(new XMLTokenizer(str));
        ski.mirror(null);

        VJoint vHuman = HanimBody.getLOA1HanimBody();
        ski.setTargets(vHuman.getParts().toArray(new VJoint[0]));
        ski.setToTarget();

        float q[] = Quat4f.getQuat4f();
        vHuman.getPart(Hanim.r_shoulder).getRotation(q);
        assertQuat4fRotationEquivalent(1, 0, 0, 0, q, 0.0001f);

        vHuman.getPart(Hanim.l_shoulder).getRotation(q);
        assertQuat4fRotationEquivalent(0, 0, -1, 0, q, 0.0001f);
    }
}
