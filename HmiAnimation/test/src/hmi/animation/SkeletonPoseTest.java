package hmi.animation;

import static org.junit.Assert.*;

import java.io.*;

import hmi.xml.XMLTokenizer;

import org.junit.Test;
import org.junit.Before;
import java.util.*;

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

}
