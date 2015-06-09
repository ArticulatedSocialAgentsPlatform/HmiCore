/*******************************************************************************
 * The MIT License (MIT)
 * Copyright (c) 2015 University of Twente
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *******************************************************************************/
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
