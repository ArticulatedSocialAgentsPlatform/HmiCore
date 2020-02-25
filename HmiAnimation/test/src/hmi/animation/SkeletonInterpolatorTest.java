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
/*
 * SkeletonInterpolator JUnit test
 */

package hmi.animation;

import static org.junit.Assert.*;
import static hmi.testutil.math.Quat4fTestUtil.assertQuat4fRotationEquivalent;
import static hmi.testutil.math.Vec3fTestUtil.assertVec3fEquals;

import org.junit.*;

import hmi.testutil.animation.HanimBody;
import hmi.xml.*;
import hmi.math.*;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

/**
 * JUnit test for hmi.animation.SkeletonInterpolator
 */
public class SkeletonInterpolatorTest
{
    private static final float PRECISION = 0.0001f;

    public float[] c0, c1, c2, c3, c4, c5, c6, c7;
    public double t0, t1, t2, t3, t4, t5, t6, t7;
    ConfigList clist1;

    public SkeletonInterpolatorTest()
    {
    }

    @Before
    public void setUp()
    { // common initialization, executed for every test.
    }

    @After
    public void tearDown()
    {
    }

    public void init0()
    { // prepare some configs
        c0 = new float[] { 0f, 1f, 2f, 3f, 4f, 5f, 6f, 1f, 0f, 0f, 0f };
        c1 = new float[] { 1f, 2f, 3f, 4f, 5f, 6f, 7f, 0f, 1f, 0f, 0f };
        c2 = new float[] { 2f, 3f, 4f, 5f, 6f, 7f, 8f, 0f, 0f, 1f, 0f };
        c3 = new float[] { 3f, 4f, 5f, 6f, 7f, 8f, 9f, 0f, 0f, 0f, 1f };
        c4 = new float[] { 4f, 5f, 6f, 7f, 8f, 9f, 10f, 1f, 0f, 0f, 0f };
        c5 = new float[] { 5f, 6f, 7f, 8f, 9f, 10f, 11f, 0f, 1f, 0f, 0f };
        c6 = new float[] { 6f, 7f, 8f, 9f, 10f, 11f, 12f, 0f, 0f, 1f, 0f };
        c7 = new float[] { 7f, 8f, 9f, 10f, 11f, 12f, 13f, 0f, 0f, 0f, 01f };
    }

    public void init1()
    { // prepare ConfigList clist1
        init0();
        // t0 = 1.1; t1 = 1.1; t2 = 2.2; t3 = 3.3; t4 = 4.4; t5 = 5.5; t6=6.6;
        // t7 =7.7;

        t0 = 0.000000;
        t1 = 0.008333;
        t2 = 0.016667;
        t3 = 0.025000;
        t4 = 0.033333;
        t5 = 0.041667;
        t6 = 0.050000;
        t7 = 0.058333;
        // 0.066667
        // 0.075000
        // 0.083333
        // 0.091667
        // 0.100000
        // 0.108333
        // 0.116667
        // 0.125000
        // 0.133333
        // 0.141667

        clist1 = new ConfigList(c0.length);
        clist1.addConfig(t0, c0);
        clist1.addConfig(t1, c1);
        clist1.addConfig(t2, c2);
        clist1.addConfig(t3, c3);
        clist1.addConfig(t4, c4);
        clist1.addConfig(t5, c5);
        clist1.addConfig(t6, c6);
        clist1.addConfig(t7, c7);
    }

    // @Test
    // public void testBasics() {
    // SkeletonInterpolator ski = new SkeletonInterpolator();
    // assertTrue(ski.getConfigType() == 0);
    // assertTrue(ski.getConfigList() == null);
    // assertTrue(ski.getPartIds().length == 0);
    // assertTrue(ski.getStartTime() == 0.0);
    // assertTrue(ski.getEndTime() == 0.0);
    // assertTrue(ski.size() == 0);
    // assertTrue(ski.getConfigSize() == 0);
    // }

    @Test
    public void testConstructor()
    {
        init1(); // prepare clist1
        String[] partIds1 = new String[] { "Root", "Joint1" };
        String type1 = "T1R";

        SkeletonInterpolator ski1 = new SkeletonInterpolator(partIds1, clist1, type1);
        assertTrue(ski1.getConfigType() == type1);
        assertTrue(ski1.getConfigList() != null);
        assertTrue(ski1.getPartIds() == partIds1);
        // System.out.println("start time: " + ski1.getStartTime());
        assertTrue(ski1.getStartTime() == t0);
        assertTrue(ski1.getEndTime() == t7);
        assertTrue(ski1.size() == 8);
        assertTrue(ski1.getConfigSize() == 11);

    }

    @Test
    public void testInterpolate()
    {
        init1(); // prepare clist1
        String[] partIds1 = new String[] { "Root", "Joint1" };
        String type1 = "T1R";
        SkeletonInterpolator ski = new SkeletonInterpolator(partIds1, clist1, type1);

        float[] conf = ski.getInterpolatedConfig(t1, null);

        assertTrue(Vecf.epsilonEquals(conf, c1, 0.001f));
        assertTrue(Vecf.equals(conf, c1));

        conf = ski.getInterpolatedConfig(t2, null);
        // System.out.println("conf for time " + t2 + "  " +
        // Config.toString(conf));
        assertTrue(Vecf.epsilonEquals(conf, c2, 0.001f));
        assertTrue(Vecf.equals(conf, c2));

        conf = ski.getInterpolatedConfig((t1 + t2) / 2.0, null);
        // System.out.println("conf for time " + ((t1+t2)/2.0) + "  " +
        // Config.toString(conf));

        assertTrue(Math.abs(conf[0] - (c1[0] + c2[0]) / 2.0) < 0.001);
        assertTrue(Math.abs(conf[1] - (c1[1] + c2[1]) / 2.0) < 0.001);
        assertTrue(Math.abs(conf[2] - (c1[2] + c2[2]) / 2.0) < 0.001);

        float[] q1 = new float[4];
        Quat4f.set(q1, 0, c1, 7);
        float[] q2 = new float[4];
        Quat4f.set(q2, 0, c2, 7);
        float[] qi = new float[4];
        Quat4f.interpolate(qi, q1, q2, 0.5f);

        float[] qconf = new float[4];
        Quat4f.set(qconf, 0, conf, 7);
        // System.out.println("q1: " + Quat4f.toString(q1) + " q2:" +
        // Quat4f.toString(q2) + " qi: " + Quat4f.toString(qi) + " qconf: " +
        // Quat4f.toString(qconf));

        assertTrue(Quat4f.equals(qi, qconf));

        conf = ski.getInterpolatedConfig(t0, null);
        // System.out.println("conf for time " + t0 + "  " +
        // Config.toString(conf));
        // System.out.println("c1" + "  " + Config.toString(c1));
        assertTrue(Vecf.epsilonEquals(conf, c0, 0.001f));
        conf = ski.getInterpolatedConfig(t1, conf);
        assertTrue(Vecf.epsilonEquals(conf, c1, 0.001f));

        conf = ski.getInterpolatedConfig(0.0, null);
        // System.out.println("conf for time " + 0.0 + "  " +
        // Config.toString(conf));
        assertTrue(Vecf.epsilonEquals(conf, c0, 0.001f));

        conf = ski.getInterpolatedConfig(t7, null);
        // System.out.println("conf for time " + t7 + "  " +
        // Config.toString(conf));
        assertTrue(Vecf.epsilonEquals(conf, c7, 0.001f));

        conf = ski.getInterpolatedConfig(t7 + 3.0, null);
        // System.out.println("conf for time " + (t7+3.0) + "  " +
        // Config.toString(conf));
        assertTrue(Vecf.epsilonEquals(conf, c7, 0.001f));

        // test caching:
        ski.getInterpolatedConfig(t3 + 0.2, null);
        float[] conf1 = ski.getInterpolatedConfig(t4 + 0.2, null);
        ski.getInterpolatedConfig(t6, null);
        float[] conf2 = ski.getInterpolatedConfig(t4 + 0.2, null);
        assertTrue(Vecf.equals(conf1, conf2));
        ski.getInterpolatedConfig(t0, null);
        float[] conf3 = ski.getInterpolatedConfig(t4 + 0.2, null);
        assertTrue(Vecf.equals(conf1, conf3));
        ski.getInterpolatedConfig(t5 + 0.2, null);
        float[] conf4 = ski.getInterpolatedConfig(t4 + 0.2, null);
        assertTrue(Vecf.equals(conf1, conf4));
        ski.getInterpolatedConfig(t4 + 0.1, null);
        float[] conf5 = ski.getInterpolatedConfig(t4 + 0.2, null);
        assertTrue(Vecf.equals(conf1, conf5));
        ski.getInterpolatedConfig(t5, null);
        float[] conf6 = ski.getInterpolatedConfig(t4 + 0.2, null);
        assertTrue(Vecf.equals(conf1, conf6));

    }

    // @Test
    public void testXML() throws IOException
    {
        init1(); // prepare clist1
        String[] partIds1 = new String[] { "Root", "Joint1" };
        String type1 = "T1R";
        SkeletonInterpolator ski = new SkeletonInterpolator(partIds1, clist1, type1);
        String encoded = ski.toXMLString();
        // System.out.println("encoded: " + encoded);

        XMLTokenizer tokenizer = new XMLTokenizer(encoded);
        SkeletonInterpolator skelDecoded = new SkeletonInterpolator(tokenizer);
        // System.out.println("decoded size : " + skelDecoded.size());
        // System.out.println("decoded configSize : " +
        // skelDecoded.getConfigSize());

        String decoded = skelDecoded.toXMLString();
        // System.out.println("decoded: " + decoded);
        assertTrue(encoded.equals(decoded));
        assertTrue(skelDecoded.size() == 8);
        assertTrue(skelDecoded.getConfigSize() == 11);
        assertTrue(skelDecoded.getPartIds().length == 2);
        assertTrue(skelDecoded.getPartIds()[0].equals("Root"));
        assertTrue(skelDecoded.getPartIds()[1].equals("Joint1"));

        float[] dc0 = skelDecoded.getConfig(0);
        assertTrue(Vecf.equals(c0, dc0));
        float[] dc3 = skelDecoded.getConfig(3);
        assertTrue(Vecf.equals(c3, dc3));
        float[] dc7 = skelDecoded.getConfig(7);
        assertTrue(Vecf.equals(c7, dc7));

        double dt0 = skelDecoded.getTime(0);
        assertTrue(dt0 == t0);
        double dt3 = skelDecoded.getTime(3);
        assertTrue(dt3 == t3);
        double dt7 = skelDecoded.getTime(7);
        assertTrue(dt7 == t7);
    }

    @Test
    public void testEmpty()
    {
        SkeletonInterpolator ski = new SkeletonInterpolator();
        assertTrue(ski.getPartIds().length == 0);
        assertTrue(ski.getConfigList().size() == 0);
    }

    @Test
    public void testEmptyInterpolate()
    {
        SkeletonInterpolator ski = new SkeletonInterpolator();
        assertTrue(ski.getPartIds().length == 0);
        assertTrue(ski.getConfigList().size() == 0);
        ski.interpolateTargetParts(0);
    }

    @Test
    public void testSetTargetOnSkeletonInterpolatorContainingAnInvalidJoint()
    {
        String str = "<SkeletonInterpolator rotationEncoding=\"quaternions\" parts=\"HumanoidRoot invalid l_shoulder\" encoding=\"T1R\">"
                + "0 0 1 0 1 0 0 0 0 1 0 0 0 0 1 0\n" + "1 0 1 0 1 0 0 0 0 1 0 0 0 0 1 0" + "</SkeletonInterpolator>";
        SkeletonInterpolator ski = new SkeletonInterpolator();
        ski.readXML(str);

        VJoint vHuman = HanimBody.getLOA1HanimBody();
        ski.setTarget(HanimBody.getLOA1HanimBody());
        ski.interpolateMillis(0);
        float q[] = Quat4f.getQuat4f();
        vHuman.getPart(Hanim.HumanoidRoot).getRotation(q);
        assertQuat4fRotationEquivalent(1, 0, 0, 0, q, PRECISION);
    }

    @Test
    public void testOne()
    {
        String str = "<SkeletonInterpolator rotationEncoding=\"quaternions\" parts=\"HumanoidRoot\" encoding=\"R\">" + "0 1 0 0 0"
                + "</SkeletonInterpolator>";
        SkeletonInterpolator ski = new SkeletonInterpolator();
        ski.readXML(str);
        VJoint v = new VJoint();
        v.setSid("HumanoidRoot");
        ski.setTarget(v);
        ski.time(0);
        float[] qv = Quat4f.getQuat4f();
        v.getRotation(qv);
        float[] qski = Quat4f.getQuat4f();
        Quat4f.set(qski, 1, 0, 0, 0);
        assertTrue(Quat4f.epsilonEquals(qv, qski, PRECISION));
    }

    @Test
    public void testFilter()
    {
        String str = "<SkeletonInterpolator rotationEncoding=\"quaternions\" parts=\"HumanoidRoot r_shoulder l_shoulder\" encoding=\"T1R\">"
                + "0 0 1 0 1 0 0 0 0 1 0 0 0 0 1 0\n" + "1 0 1 0 1 0 0 0 0 1 0 0 0 0 1 0" + "</SkeletonInterpolator>";
        SkeletonInterpolator ski = new SkeletonInterpolator();
        ski.readXML(str);

        VJoint vHuman = new VJoint();
        vHuman.setSid("HumanoidRoot");
        VJoint lSh = new VJoint();
        lSh.setSid("l_shoulder");
        VJoint rSh = new VJoint();
        rSh.setSid("r_shoulder");
        rSh.setRotation(0, 0, 0, 1);
        vHuman.addChild(rSh);
        vHuman.addChild(lSh);
        ski.setTarget(vHuman);

        Set<String> filter = new HashSet<String>();
        filter.add("l_shoulder");
        filter.add("HumanoidRoot");
        ski.filterJoints(filter);

        assertEquals(2, ski.getPartIds().length);
        assertEquals("HumanoidRoot", ski.getPartIds()[0]);
        assertEquals("l_shoulder", ski.getPartIds()[1]);

        assertEquals("T1R", ski.getConfigType());
        assertEquals(11, ski.getConfigList().getConfigSize());

        ski.interpolateMillis(0);
        float[] q = Quat4f.getQuat4f();
        vHuman.getRotation(q);
        assertQuat4fRotationEquivalent(1, 0, 0, 0, q, PRECISION);

        lSh.getRotation(q);
        assertQuat4fRotationEquivalent(0, 0, 1, 0, q, PRECISION);

        rSh.getRotation(q);
        assertQuat4fRotationEquivalent(0, 0, 0, 1, q, PRECISION);
    }

    @Test
    public void testFilterNonExistingJoint()
    {
        String str = "<SkeletonInterpolator rotationEncoding=\"quaternions\" parts=\"HumanoidRoot invalid l_shoulder\" encoding=\"T1R\">"
                + "0 0 1 0 1 0 0 0 0 1 0 0 0 0 1 0\n" + "1 0 1 0 1 0 0 0 0 1 0 0 0 0 1 0" + "</SkeletonInterpolator>";
        SkeletonInterpolator ski = new SkeletonInterpolator();
        ski.readXML(str);

        Set<String> filter = new HashSet<String>();
        filter.add("invalid");
        filter.add("l_shoulder");

        ski.setTarget(HanimBody.getLOA1HanimBody());
        ski.filterJoints(filter);
        assertEquals(2, ski.getPartIds().length);
        assertEquals(2, ski.getTargetParts().length);
    }

    @Test
    public void testFilterRemoveRoot()
    {
        String str = "<SkeletonInterpolator rotationEncoding=\"quaternions\" parts=\"HumanoidRoot r_shoulder l_shoulder\" encoding=\"T1R\">"
                + "0 0 1 0 1 0 0 0 0 1 0 0 0 0 1 0\n" + "1 0 1 0 1 0 0 0 0 1 0 0 0 0 1 0" + "</SkeletonInterpolator>";
        SkeletonInterpolator ski = new SkeletonInterpolator();
        ski.readXML(str);

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
        ski.setTarget(vHuman);

        Set<String> filter = new HashSet<String>();
        filter.add("l_shoulder");
        filter.add("r_shoulder");
        ski.filterJoints(filter);

        assertEquals(2, ski.getPartIds().length);
        assertEquals("r_shoulder", ski.getPartIds()[0]);
        assertEquals("l_shoulder", ski.getPartIds()[1]);

        assertEquals("R", ski.getConfigType());
        assertEquals(8, ski.getConfigList().getConfigSize());

        ski.interpolateMillis(0);
        float[] q = Quat4f.getQuat4f();
        vHuman.getRotation(q);
        assertQuat4fRotationEquivalent(0, 0, 0, 1, q, PRECISION);

        lSh.getRotation(q);
        assertQuat4fRotationEquivalent(0, 0, 1, 0, q, PRECISION);

        rSh.getRotation(q);
        assertQuat4fRotationEquivalent(0, 1, 0, 0, q, PRECISION);
    }

    @Test
    public void testMirrorWithT1R()
    {
        String str = "<SkeletonInterpolator rotationEncoding=\"quaternions\" parts=\"HumanoidRoot\" encoding=\"T1R\">"
                + "0 1 2 3 0 0 1 0 \n" + "1 1 2 3 0 0 1 0 " + "</SkeletonInterpolator>";
        SkeletonInterpolator ski = new SkeletonInterpolator();
        ski.readXML(str);
        VJoint vHuman = HanimBody.getLOA1HanimBody();
        ski.setTarget(vHuman);
        ski.mirror();
        ski.interpolateTargetParts(0);

        float q[] = Quat4f.getQuat4f();
        vHuman.getPart(Hanim.HumanoidRoot).getRotation(q);
        assertQuat4fRotationEquivalent(0, 0, -1, 0, q, PRECISION);

        float tr[] = Vec3f.getVec3f();
        vHuman.getPart("HumanoidRoot").getTranslation(tr);
        assertVec3fEquals(-1, 2, 3, tr, PRECISION);
    }

    @Test
    public void testMirrorR()
    {
        String str = "<SkeletonInterpolator rotationEncoding=\"quaternions\" parts=\"r_shoulder\" encoding=\"R\">" + "0 0 0 1 0 \n"
                + "1 0 0 1 0 " + "</SkeletonInterpolator>";
        SkeletonInterpolator ski = new SkeletonInterpolator();
        ski.readXML(str);
        VJoint vHuman = HanimBody.getLOA1HanimBody();
        ski.setTarget(vHuman);
        ski.mirror();
        ski.interpolateTargetParts(0);

        float q[] = Quat4f.getQuat4f();
        vHuman.getPart(Hanim.r_shoulder).getRotation(q);
        assertQuat4fRotationEquivalent(1, 0, 0, 0, q, PRECISION);

        vHuman.getPart(Hanim.l_shoulder).getRotation(q);
        assertQuat4fRotationEquivalent(0, 0, -1, 0, q, PRECISION);
    }

    @Test
    public void testMirrorAfterFilter()
    {
        String str = "<SkeletonInterpolator rotationEncoding=\"quaternions\" parts=\"r_shoulder l_shoulder\" encoding=\"R\">"
                + "0 0 0 1 0 0 0 1 0\n" + "1 0 0 1 0 0 0 1 0" + "</SkeletonInterpolator>";
        SkeletonInterpolator ski = new SkeletonInterpolator();
        ski.readXML(str);
        VJoint vHuman = HanimBody.getLOA1HanimBody();
        ski.setTarget(vHuman);
        Set<String> joints = new HashSet<String>();
        joints.add("r_shoulder");
        ski.filterJoints(joints);
        assertEquals(1, ski.getPartIds().length);
        assertEquals("r_shoulder", ski.getPartIds()[0]);

        ski.mirror();
        ski.interpolateTargetParts(0);

        float q[] = Quat4f.getQuat4f();
        vHuman.getPart(Hanim.r_shoulder).getRotation(q);
        assertQuat4fRotationEquivalent(1, 0, 0, 0, q, PRECISION);

        vHuman.getPart(Hanim.l_shoulder).getRotation(q);
        assertQuat4fRotationEquivalent(0, 0, -1, 0, q, PRECISION);
    }

    @Test
    public void testMirrorAfterFilterBeforeSetTarget()
    {
        String str = "<SkeletonInterpolator rotationEncoding=\"quaternions\" parts=\"r_shoulder l_shoulder\" encoding=\"R\">"
                + "0 0 0 1 0 0 0 1 0\n" + "1 0 0 1 0 0 0 1 0" + "</SkeletonInterpolator>";
        SkeletonInterpolator ski = new SkeletonInterpolator();
        ski.readXML(str);
        Set<String> joints = new HashSet<String>();
        joints.add("r_shoulder");
        ski.filterJoints(joints);
        assertEquals(1, ski.getPartIds().length);
        assertEquals("r_shoulder", ski.getPartIds()[0]);

        ski.mirror();

        VJoint vHuman = HanimBody.getLOA1HanimBody();
        ski.setTarget(vHuman);
        ski.interpolateTargetParts(0);

        float q[] = Quat4f.getQuat4f();
        vHuman.getPart(Hanim.r_shoulder).getRotation(q);
        assertQuat4fRotationEquivalent(1, 0, 0, 0, q, PRECISION);

        vHuman.getPart(Hanim.l_shoulder).getRotation(q);
        assertQuat4fRotationEquivalent(0, 0, -1, 0, q, PRECISION);
    }

    @Test
    public void testMirrorBeforeSetTarget()
    {
        String str = "<SkeletonInterpolator rotationEncoding=\"quaternions\" parts=\"r_shoulder\" encoding=\"R\">" + "0 0 0 1 0 \n"
                + "1 0 0 1 0 " + "</SkeletonInterpolator>";
        SkeletonInterpolator ski = new SkeletonInterpolator();
        ski.readXML(str);
        ski.mirror();

        VJoint vHuman = HanimBody.getLOA1HanimBody();
        ski.setTarget(vHuman);
        ski.interpolateTargetParts(0);

        float q[] = Quat4f.getQuat4f();
        vHuman.getPart(Hanim.r_shoulder).getRotation(q);
        assertQuat4fRotationEquivalent(1, 0, 0, 0, q, PRECISION);

        vHuman.getPart(Hanim.l_shoulder).getRotation(q);
        assertQuat4fRotationEquivalent(0, 0, -1, 0, q, PRECISION);
    }

    @Test
    public void testSubSkeletonInterpolator()
    {
        init1(); // prepare clist1
        String[] partIds1 = new String[] { "Root", "Joint1" };
        String type1 = "T1R";
        SkeletonInterpolator ski = new SkeletonInterpolator(partIds1, clist1, type1);

        SkeletonInterpolator subSki = ski.subSkeletonInterpolator(2, 5);
        assertArrayEquals(ski.getPartIds(), subSki.getPartIds());
        assertEquals(3, subSki.size());
    }

    @Test
    public void testAppend()
    {
        ConfigList cl1 = new ConfigList(2);
        cl1.addConfig(0, Quat4f.getIdentity());
        float q1[] = Quat4f.getQuat4fFromAxisAngle(1, 0, 0, (float) Math.PI);
        cl1.addConfig(1, q1);
        SkeletonInterpolator ski1 = new SkeletonInterpolator(new String[] { Hanim.skullbase }, cl1, "R");
        ConfigList cl2 = new ConfigList(2);
        float q2[] = Quat4f.getQuat4fFromAxisAngle(0, 1, 0, (float) Math.PI);
        cl2.addConfig(0, q2);
        cl2.addConfig(1, Quat4f.getIdentity());
        SkeletonInterpolator ski2 = new SkeletonInterpolator(new String[] { Hanim.skullbase }, cl2, "R");
        ski1.appendInterpolator(2, ski2);

        VJoint vHuman = HanimBody.getLOA1HanimBody();
        ski1.setTarget(vHuman);
        ski1.time(0);
        float q[] = Quat4f.getQuat4f();
        vHuman.getPart(Hanim.skullbase).getRotation(q);
        assertQuat4fRotationEquivalent(Quat4f.getIdentity(), q, PRECISION);

        ski1.time(1);
        vHuman.getPart(Hanim.skullbase).getRotation(q);
        assertQuat4fRotationEquivalent(q1, q, PRECISION);

        ski1.time(2);
        vHuman.getPart(Hanim.skullbase).getRotation(q);
        assertQuat4fRotationEquivalent(q2, q, PRECISION);

        ski1.time(3);
        vHuman.getPart(Hanim.skullbase).getRotation(q);
        assertQuat4fRotationEquivalent(Quat4f.getIdentity(), q, PRECISION);
    }

    @Test
    public void testLiveAppend()
    {
        ConfigList cl1 = new ConfigList(2);
        cl1.addConfig(0, Quat4f.getIdentity());
        float q1[] = Quat4f.getQuat4fFromAxisAngle(1, 0, 0, (float) Math.PI);
        cl1.addConfig(1, q1);
        SkeletonInterpolator ski1 = new SkeletonInterpolator(new String[] { Hanim.skullbase }, cl1, "R");

        VJoint vHuman = HanimBody.getLOA1HanimBody();
        ski1.setTarget(vHuman);
        ski1.time(0);
        float q[] = Quat4f.getQuat4f();
        vHuman.getPart(Hanim.skullbase).getRotation(q);
        assertQuat4fRotationEquivalent(Quat4f.getIdentity(), q, PRECISION);

        ski1.time(1);
        vHuman.getPart(Hanim.skullbase).getRotation(q);
        assertQuat4fRotationEquivalent(q1, q, PRECISION);

        ConfigList cl2 = new ConfigList(2);
        float q2[] = Quat4f.getQuat4fFromAxisAngle(0, 1, 0, (float) Math.PI);
        cl2.addConfig(0, q2);
        cl2.addConfig(1, Quat4f.getIdentity());
        SkeletonInterpolator ski2 = new SkeletonInterpolator(new String[] { Hanim.skullbase }, cl2, "R");
        ski1.appendInterpolator(2, ski2);

        ski1.time(2);
        vHuman.getPart(Hanim.skullbase).getRotation(q);
        assertQuat4fRotationEquivalent(q2, q, PRECISION);

        ski1.time(3);
        vHuman.getPart(Hanim.skullbase).getRotation(q);
        assertQuat4fRotationEquivalent(Quat4f.getIdentity(), q, PRECISION);
    }

    @Test
    public void testBlend()
    {
        String str = "<SkeletonInterpolator rotationEncoding=\"quaternions\" parts=\"HumanoidRoot\" encoding=\"T1R\">"
                + "0 1 2 3 0 0 1 0 \n" + "2 3 1 2 0 0 0 1 " + "</SkeletonInterpolator>";
        SkeletonInterpolator ski1 = new SkeletonInterpolator();
        ski1.readXML(str);

        str = "<SkeletonInterpolator rotationEncoding=\"quaternions\" parts=\"HumanoidRoot\" encoding=\"T1R\">" + "0 0 0 0 0 1 0 0 \n"
                + "2 2 3 4 0 0 0 1 " + "</SkeletonInterpolator>";
        SkeletonInterpolator ski2 = new SkeletonInterpolator();
        ski2.readXML(str);

        SkeletonInterpolator blend = ski1.blend(0, 0, ski2, 2);
        float conf[] = new float[7];
        blend.getInterpolatedConfig(0, conf);
        assertVec3fEquals(Vec3f.getVec3f(1, 2, 3), conf, PRECISION);
        assertQuat4fRotationEquivalent(Quat4f.getQuat4f(0, 0, 1, 0), 0, conf, 3, PRECISION);

        blend.getInterpolatedConfig(2, conf);
        assertVec3fEquals(Vec3f.getVec3f(2, 3, 4), conf, 0.2f);
        assertQuat4fRotationEquivalent(Quat4f.getQuat4f(0, 0, 0, 1), 0, conf, 3, 0.05f);

        blend.getInterpolatedConfig(0.9, conf);
        float conf1[] = new float[7];
        ski1.getInterpolatedConfig(0.9, conf1);
        float conf2[] = new float[7];
        ski2.getInterpolatedConfig(0.9, conf2);
        float vExpected[] = Vec3f.getVec3f();
        float qExpected[] = Quat4f.getQuat4f();
        Vec3f.interpolate(vExpected, conf1, conf2, 0.9f/2f);
        Quat4f.interpolate(qExpected, 0, conf1, 3, conf2, 3, 0.9f/2f);
        assertVec3fEquals(vExpected, conf, 0.2f);
        assertQuat4fRotationEquivalent(qExpected, 0, conf, 3, 0.05f);        
    }
}
