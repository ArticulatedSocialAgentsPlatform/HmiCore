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
package hmi.animation.motiongraph;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import hmi.animation.ConfigList;
import hmi.animation.SkeletonInterpolator;
import hmi.math.Quat4f;

import org.junit.Test;

/**
 * Unit tests for the RotationVelocityChecker
 * @author herwinvw
 *
 */
public class RotationVelocityCheckerTest
{
    @Test
    public void testSameSki()
    {
        ConfigList clIn = new ConfigList(4);
        clIn.addConfig(0, Quat4f.getIdentity());
        clIn.addConfig(1, Quat4f.getQuat4fFromAxisAngle(1, 0, 0, 0.1f));
        clIn.addConfig(2, Quat4f.getQuat4fFromAxisAngle(1, 0, 0, 0.2f));
        ConfigList clOut = new ConfigList(4);
        clOut.addConfig(0, Quat4f.getIdentity());
        clOut.addConfig(1, Quat4f.getQuat4fFromAxisAngle(1, 0, 0, 0.1f));
        clOut.addConfig(2, Quat4f.getQuat4fFromAxisAngle(1, 0, 0, 0.2f));
        
        SkeletonInterpolator skiIn = new SkeletonInterpolator(new String[]{"skullbase"},clIn,"R");
        SkeletonInterpolator skiOut = new SkeletonInterpolator(new String[]{"skullbase"},clOut,"R");
        RotationVelocityChecker checker = new RotationVelocityChecker(0.1f,0.1f);
        assertTrue(checker.allowTransition(skiIn, skiOut, 1, 1));
    }
    
    @Test
    public void testSameSkiLastFrame()
    {
        ConfigList clIn = new ConfigList(4);
        clIn.addConfig(0, Quat4f.getIdentity());
        clIn.addConfig(1, Quat4f.getQuat4fFromAxisAngle(1, 0, 0, 0.1f));
        clIn.addConfig(2, Quat4f.getQuat4fFromAxisAngle(1, 0, 0, 0.2f));
        ConfigList clOut = new ConfigList(4);
        clOut.addConfig(0, Quat4f.getIdentity());
        clOut.addConfig(1, Quat4f.getQuat4fFromAxisAngle(1, 0, 0, 0.1f));
        clOut.addConfig(2, Quat4f.getQuat4fFromAxisAngle(1, 0, 0, 0.2f));
        
        SkeletonInterpolator skiIn = new SkeletonInterpolator(new String[]{"skullbase"},clIn,"R");
        SkeletonInterpolator skiOut = new SkeletonInterpolator(new String[]{"skullbase"},clOut,"R");
        RotationVelocityChecker checker = new RotationVelocityChecker(0.1f,0.1f);
        assertTrue(checker.allowTransition(skiIn, skiOut, 2, 2));
    }
    
    @Test
    public void testSameSkiDifferentVelocity()
    {
        ConfigList clIn = new ConfigList(4);
        clIn.addConfig(0, Quat4f.getQuat4fFromAxisAngle(1, 0, 0, 1f));
        clIn.addConfig(1, Quat4f.getIdentity());
        clIn.addConfig(2, Quat4f.getQuat4fFromAxisAngle(1, 0, 0, -1f));
        ConfigList clOut = new ConfigList(4);
        
        clOut.addConfig(0, Quat4f.getQuat4fFromAxisAngle(1, 0, 0, 1f));
        clOut.addConfig(1, Quat4f.getIdentity());
        clOut.addConfig(2, Quat4f.getQuat4fFromAxisAngle(1, 0, 0, 1f));
        
        SkeletonInterpolator skiIn = new SkeletonInterpolator(new String[]{"skullbase"},clIn,"R");
        SkeletonInterpolator skiOut = new SkeletonInterpolator(new String[]{"skullbase"},clOut,"R");
        RotationVelocityChecker checker = new RotationVelocityChecker(0.1f,0.1f);
        assertFalse(checker.allowTransition(skiIn, skiOut, 1, 1));
    }
    
    @Test
    public void testSameSkiDifferentVelocityTwoJoints()
    {
        ConfigList clIn = new ConfigList(8);
        float q0[]=new float[8];
        Quat4f.setQuat4fArrayFromAxisAngle4fArray(q0, new float[]{1,0,0,0.1f,  1,0,0,-1f});
        float q1[]=new float[8];
        Quat4f.setQuat4fArrayFromAxisAngle4fArray(q1, new float[]{1,0,0,0.1f,  1,0,0,0.0f});
        float q2[]=new float[8];
        Quat4f.setQuat4fArrayFromAxisAngle4fArray(q2, new float[]{1,0,0,0.1f,  1,0,0,1f});
        clIn.addConfig(0, q0);
        clIn.addConfig(1, q1);
        clIn.addConfig(2, q2);
        ConfigList clOut = new ConfigList(8);
        clOut.addConfig(0, q2);
        clOut.addConfig(1, q1);
        clOut.addConfig(2, q0);
        
        SkeletonInterpolator skiIn = new SkeletonInterpolator(new String[]{"skullbase","vc4"},clIn,"R");
        SkeletonInterpolator skiOut = new SkeletonInterpolator(new String[]{"skullbase","vc4"},clOut,"R");
        RotationVelocityChecker checker = new RotationVelocityChecker(0.1f,0.1f);
        assertFalse(checker.allowTransition(skiIn, skiOut, 1, 1));
    }
    
    @Test
    public void testSameSkiTwoJoints()
    {
        ConfigList clIn = new ConfigList(8);
        clIn.addConfig(0, new float[]{1,0,0,0,1,0,0,0});
        float q1[]=new float[8];
        Quat4f.setQuat4fArrayFromAxisAngle4fArray(q1, new float[]{1,0,0,0.1f,  1,0,0,0.3f});
        float q2[]=new float[8];
        Quat4f.setQuat4fArrayFromAxisAngle4fArray(q2, new float[]{1,0,0,0.2f,  1,0,0,0.4f});
        clIn.addConfig(1, q1);
        clIn.addConfig(2, q2);
        ConfigList clOut = new ConfigList(8);
        clOut.addConfig(0, new float[]{1,0,0,0,1,0,0,0});
        clOut.addConfig(1, q1);
        clOut.addConfig(2, q2);
        
        SkeletonInterpolator skiIn = new SkeletonInterpolator(new String[]{"skullbase","vc4"},clIn,"R");
        SkeletonInterpolator skiOut = new SkeletonInterpolator(new String[]{"skullbase","vc4"},clOut,"R");
        RotationVelocityChecker checker = new RotationVelocityChecker(0.1f,0.1f);
        assertTrue(checker.allowTransition(skiIn, skiOut, 1, 1));
    }
    
    @Test
    public void testNoTransition()
    {
        ConfigList clIn = new ConfigList(4);
        clIn.addConfig(0, Quat4f.getIdentity());
        clIn.addConfig(1, Quat4f.getQuat4fFromAxisAngle(1, 0, 0, 0.1f));
        clIn.addConfig(2, Quat4f.getQuat4fFromAxisAngle(1, 0, 0, 0.2f));
        ConfigList clOut = new ConfigList(4);
        clOut.addConfig(0, Quat4f.getIdentity());
        clOut.addConfig(1, Quat4f.getQuat4fFromAxisAngle(1, 0, 0, 1f));
        clOut.addConfig(2, Quat4f.getQuat4fFromAxisAngle(1, 0, 0, 1.5f));
        
        SkeletonInterpolator skiIn = new SkeletonInterpolator(new String[]{"skullbase"},clIn,"R");
        SkeletonInterpolator skiOut = new SkeletonInterpolator(new String[]{"skullbase"},clOut,"R");
        RotationVelocityChecker checker = new RotationVelocityChecker(0.1f,0.1f);
        assertFalse(checker.allowTransition(skiIn, skiOut, 1, 1));
    }
    
    @Test
    public void testNoTransitionTwoJoints()
    {
        ConfigList clIn = new ConfigList(8);
        clIn.addConfig(0, new float[]{1,0,0,0,1,0,0,0});
        float q1[]=new float[8];
        Quat4f.setQuat4fArrayFromAxisAngle4fArray(q1, new float[]{1,0,0,0.1f,  1,0,0,0.3f});
        float q2[]=new float[8];
        Quat4f.setQuat4fArrayFromAxisAngle4fArray(q1, new float[]{1,0,0,0.2f,  1,0,0,0.4f});
        clIn.addConfig(1, q1);
        clIn.addConfig(2, q2);
        ConfigList clOut = new ConfigList(8);
        clOut.addConfig(0, new float[]{1,0,0,0,1,0,0,0});
        float q3[]=new float[8];
        Quat4f.setQuat4fArrayFromAxisAngle4fArray(q3, new float[]{1,0,0,0.1f,  1,0,0,1f});
        clOut.addConfig(1, q3);
        clOut.addConfig(2, q2);
        
        SkeletonInterpolator skiIn = new SkeletonInterpolator(new String[]{"skullbase","vc4"},clIn,"R");
        SkeletonInterpolator skiOut = new SkeletonInterpolator(new String[]{"skullbase","vc4"},clOut,"R");
        RotationVelocityChecker checker = new RotationVelocityChecker(0.1f,0.1f);
        assertFalse(checker.allowTransition(skiIn, skiOut, 1, 1));
    }
}
