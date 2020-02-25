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

import static hmi.testutil.math.Vec3fTestUtil.assertVec3fEquals;
import hmi.math.Quat4f;
import hmi.math.Vec3f;
import hmi.testutil.math.Quat4fTestUtil;

import org.junit.Test;

import com.google.common.collect.ImmutableList;

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
    
    private VJoint vjOut = setupTestJointStructure("idout");
    private VJoint vj1 = setupTestJointStructure("id1");
    private VJoint vj2 = setupTestJointStructure("id2");
    private VJoint vj3 = setupTestJointStructure("id3");
    
    @Test
    public void testTranslationTwo()
    {
        AdditiveT1RBlend blend = new AdditiveT1RBlend(vj1,vj2,vjOut);
        vj2.setTranslation(Vec3f.getVec3f(1,2,3));
        vj1.setTranslation(Vec3f.getVec3f(2,3,4));
        blend.blend();
        float vOutTrans[]=Vec3f.getVec3f();
        vjOut.getTranslation(vOutTrans);
        assertVec3fEquals(Vec3f.getVec3f(3,5,7),vOutTrans,PRECISION);
    }
    
    @Test
    public void testTwoRotation()
    {
        AdditiveT1RBlend blend = new AdditiveT1RBlend(vj1, vj2, vjOut);
        vj1.setAxisAngle(1, 0, 0, (float) Math.PI);
        vj1.getPart("v2").setAxisAngle(0, 1, 0, (float) Math.PI * 0.5f);
        vj2.getPart("v2").setAxisAngle(0, 1, 0, (float) Math.PI * 0.5f);        
        vj2.getPart("v3").setAxisAngle(0, 0, 1, (float) Math.PI);
        blend.blend();
        float q1[] = Quat4f.getQuat4f();
        float q2[] = Quat4f.getQuat4f();
        float q3[] = Quat4f.getQuat4f();
        vjOut.getRotation(q1);
        vjOut.getPart("v2").getRotation(q2);
        vjOut.getPart("v3").getRotation(q3);
        Quat4fTestUtil.assertQuat4fRotationEquivalent(Quat4f.getQuat4fFromAxisAngle(1, 0, 0, (float) Math.PI), q1, PRECISION);
        Quat4fTestUtil.assertQuat4fRotationEquivalent(Quat4f.getQuat4fFromAxisAngle(0, 1, 0, (float) Math.PI), q2, PRECISION);
        Quat4fTestUtil.assertQuat4fRotationEquivalent(Quat4f.getQuat4fFromAxisAngle(0, 0, 1, (float) Math.PI), q3, PRECISION);
    }
    
    @Test
    public void testThreeRotation()
    {
        AdditiveT1RBlend blend = new AdditiveT1RBlend(vj1, ImmutableList.of(vj2, vj3), vjOut);
        vj1.setAxisAngle(1, 0, 0, (float) Math.PI);
        vj1.getPart("v2").setAxisAngle(0, 1, 0, (float) Math.PI * 1.0f/3.0f);
        vj2.getPart("v2").setAxisAngle(0, 1, 0, (float) Math.PI * 1.0f/3.0f);
        vj3.getPart("v2").setAxisAngle(0, 1, 0, (float) Math.PI * 1.0f/3.0f);
        vj2.getPart("v3").setAxisAngle(0, 0, 1, (float) Math.PI);
        vj3.getPart("v4").setAxisAngle(0, 1, 1, (float) Math.PI);
        blend.blend();
        float q1[] = Quat4f.getQuat4f();
        float q2[] = Quat4f.getQuat4f();
        float q3[] = Quat4f.getQuat4f();
        float q4[] = Quat4f.getQuat4f();
        vjOut.getRotation(q1);
        vjOut.getPart("v2").getRotation(q2);
        vjOut.getPart("v3").getRotation(q3);
        vjOut.getPart("v4").getRotation(q4);
        Quat4fTestUtil.assertQuat4fRotationEquivalent(Quat4f.getQuat4fFromAxisAngle(1, 0, 0, (float) Math.PI), q1, PRECISION);
        Quat4fTestUtil.assertQuat4fRotationEquivalent(Quat4f.getQuat4fFromAxisAngle(0, 1, 0, (float) Math.PI), q2, PRECISION);
        Quat4fTestUtil.assertQuat4fRotationEquivalent(Quat4f.getQuat4fFromAxisAngle(0, 0, 1, (float) Math.PI), q3, PRECISION);
        Quat4fTestUtil.assertQuat4fRotationEquivalent(Quat4f.getQuat4fFromAxisAngle(0, 1, 1, (float) Math.PI), q4, PRECISION);
    }
    
    @Test
    public void testTwoAddOne()
    {
        AdditiveT1RBlend blend = new AdditiveT1RBlend(vj1, vj2, vjOut);
        blend.addVJoint(vj3);
        vj1.setAxisAngle(1, 0, 0, (float) Math.PI);
        vj1.getPart("v2").setAxisAngle(0, 1, 0, (float) Math.PI * 1.0f/3.0f);
        vj2.getPart("v2").setAxisAngle(0, 1, 0, (float) Math.PI * 1.0f/3.0f);
        vj3.getPart("v2").setAxisAngle(0, 1, 0, (float) Math.PI * 1.0f/3.0f);
        vj2.getPart("v3").setAxisAngle(0, 0, 1, (float) Math.PI);
        vj3.getPart("v4").setAxisAngle(0, 1, 1, (float) Math.PI);
        blend.blend();
        float q1[] = Quat4f.getQuat4f();
        float q2[] = Quat4f.getQuat4f();
        float q3[] = Quat4f.getQuat4f();
        float q4[] = Quat4f.getQuat4f();
        vjOut.getRotation(q1);
        vjOut.getPart("v2").getRotation(q2);
        vjOut.getPart("v3").getRotation(q3);
        vjOut.getPart("v4").getRotation(q4);
        Quat4fTestUtil.assertQuat4fRotationEquivalent(Quat4f.getQuat4fFromAxisAngle(1, 0, 0, (float) Math.PI), q1, PRECISION);
        Quat4fTestUtil.assertQuat4fRotationEquivalent(Quat4f.getQuat4fFromAxisAngle(0, 1, 0, (float) Math.PI), q2, PRECISION);
        Quat4fTestUtil.assertQuat4fRotationEquivalent(Quat4f.getQuat4fFromAxisAngle(0, 0, 1, (float) Math.PI), q3, PRECISION);
        Quat4fTestUtil.assertQuat4fRotationEquivalent(Quat4f.getQuat4fFromAxisAngle(0, 1, 1, (float) Math.PI), q4, PRECISION);
    }
}
