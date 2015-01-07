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

import static hmi.testutil.math.Quat4fTestUtil.assertQuat4fRotationEquivalent;
import static hmi.testutil.math.Vec3fTestUtil.assertVec3fEquals;
import static org.junit.Assert.assertEquals;
import hmi.animation.AnalyticalIKSolver.LimbPosition;
import hmi.math.Quat4f;
import hmi.math.Vec3f;
import hmi.testutil.LabelledParameterized;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameters;

/**
 * Unit tests for AnalyticalIKSolver
 * @author Herwin
 * 
 */
@RunWith(LabelledParameterized.class)
public class AnalyticalIKSolverTest
{
    private static final float ROTATION_PRECISION = 0.05f;
    private static final float POSITION_PRECISION = 0.05f;
    private static final float[] SHOULDER_TO_ELBOW = Vec3f.getVec3f(0, -0.5f, 0);
    private static final float[] ELBOW_TO_WRIST = Vec3f.getVec3f(0, -0.3f, 0);
    private AnalyticalIKSolver solver;
    private float qSho[] = Quat4f.getQuat4f();
    private float qElb[] = Quat4f.getQuat4f();

    @Parameters
    public static Collection<Object[]> configs() throws Exception
    {
        Collection<Object[]> objs = new ArrayList<Object[]>();
        Object obj[] = new Object[2];
        obj[0]="ARM";
        obj[1]=LimbPosition.ARM;
        objs.add(obj);
        
        obj = new Object[2];
        obj[0]="LEG";
        obj[1]=LimbPosition.LEG;
        objs.add(obj);
        return objs;
    }
    
    public AnalyticalIKSolverTest(String label, LimbPosition limbPos)
    {
        solver =  new AnalyticalIKSolver(ELBOW_TO_WRIST, SHOULDER_TO_ELBOW, limbPos);
    }
    
    @Test
    public void testAtGoal()
    {
        solver.solve(Vec3f.getVec3f(0, -0.7999f, 0), qSho, qElb);
        assertQuat4fRotationEquivalent(Quat4f.getIdentity(), qSho, ROTATION_PRECISION);
        assertQuat4fRotationEquivalent(Quat4f.getIdentity(), qElb, ROTATION_PRECISION);
    }

    @Test
    public void testStretchedNoRotation()
    {
        solver.solve(Vec3f.getVec3f(0, -2f, 0), qSho, qElb);
        assertQuat4fRotationEquivalent(Quat4f.getIdentity(), qElb, ROTATION_PRECISION);
        assertQuat4fRotationEquivalent(Quat4f.getIdentity(), qSho, ROTATION_PRECISION);
    }

    @Test
    public void testStretchedRotation()
    {
        float[] POS = Vec3f.getVec3f(0.1f, -2f, 0.1f);
        float[] POS_NORM = Vec3f.getVec3f(POS);
        Vec3f.normalize(POS_NORM);
        solver.solve(POS, qSho, qElb);
        assertQuat4fRotationEquivalent(Quat4f.getIdentity(), qElb, ROTATION_PRECISION);

        float[] shVec = Vec3f.getVec3f(SHOULDER_TO_ELBOW);
        Quat4f.transformVec3f(qSho, shVec);
        float[] elbVec = Vec3f.getVec3f(ELBOW_TO_WRIST);
        Quat4f.mul(qElb, qSho, qElb);
        Quat4f.transformVec3f(qElb, elbVec);
        Vec3f.add(shVec, elbVec);
        Vec3f.normalize(shVec);
        assertVec3fEquals(POS_NORM, shVec, POSITION_PRECISION);
    }

    @Test
    public void testAtPos()
    {
        solver.solve(Vec3f.getVec3f(0.1f, -0.5f, 0.1f), qSho, qElb);
        float[] shVec = Vec3f.getVec3f(SHOULDER_TO_ELBOW);
        Quat4f.transformVec3f(qSho, shVec);
        float[] elbVec = Vec3f.getVec3f(ELBOW_TO_WRIST);
        Quat4f.mul(qElb, qSho, qElb);
        Quat4f.transformVec3f(qElb, elbVec);
        Vec3f.add(shVec, elbVec);
        assertVec3fEquals(Vec3f.getVec3f(0.1f, -0.5f, 0.1f), shVec, POSITION_PRECISION);
    }

    @Test
    public void testAtPosWithSwivel()
    {
        solver.solve(Vec3f.getVec3f(0.1f, -0.5f, 0.1f), qSho, qElb);
        solver.setSwivel(0.5f);
        float[] shVec = Vec3f.getVec3f(SHOULDER_TO_ELBOW);
        Quat4f.transformVec3f(qSho, shVec);
        float[] elbVec = Vec3f.getVec3f(ELBOW_TO_WRIST);
        Quat4f.mul(qElb, qSho, qElb);
        Quat4f.transformVec3f(qElb, elbVec);
        Vec3f.add(shVec, elbVec);
        assertVec3fEquals(Vec3f.getVec3f(0.1f, -0.5f, 0.1f), shVec, POSITION_PRECISION);
    }

    @Test
    public void getSwivelFromShoulderAndGoal()
    {
        float goal[] = Vec3f.getVec3f(0.1f, -0.3f, 0.1f);
        solver.setSwivel(0.5d);
        solver.solve(goal, qSho, qElb);
        assertEquals(0.5d, solver.getSwivelFromShoulderElbowAndGoal(qSho, qElb, goal), POSITION_PRECISION);
    }

    @Test
    public void getSwivelFromShoulderAndGoalStretched()
    {
        float goal[] = Vec3f.getVec3f(0.1f, -2f, 0.1f);
        solver.setSwivel(0.5d);
        solver.solve(goal, qSho, qElb);
        assertEquals(0.5d, solver.getSwivelFromShoulderElbowAndGoal(qSho, qElb, goal), POSITION_PRECISION);
    }

    @Test
    public void getSwivel()
    {
        float goal[] = Vec3f.getVec3f(0.1f, -0.3f, 0.1f);
        solver.setSwivel(0.5d);
        solver.solve(goal, qSho, qElb);

        float[] elbVec = Vec3f.getVec3f(SHOULDER_TO_ELBOW);
        Quat4f.transformVec3f(qSho, elbVec);
        assertEquals(0.5d, solver.getSwivel(elbVec, goal), POSITION_PRECISION);
    }

    @Test(expected = RuntimeException.class)
    public void getSwivelStretched()
    {
        float goal[] = Vec3f.getVec3f(0.1f, -2f, 0.1f);
        solver.setSwivel(0.5d);
        solver.solve(goal, qSho, qElb);

        float[] elbVec = Vec3f.getVec3f(SHOULDER_TO_ELBOW);
        Quat4f.transformVec3f(qSho, elbVec);
        assertEquals(0.5d, solver.getSwivel(elbVec, goal), POSITION_PRECISION);
    }

    @Test
    public void getSwivel2()
    {
        float goal[] = Vec3f.getVec3f(-0.2f, 0.223606798f, 0.5f);
        float e[] = Vec3f.getVec3f(0f, 0f, 0.5f);
        double sw1 = solver.getSwivel(e, goal);

        goal = Vec3f.getVec3f(0.2f, 0.223606798f, 0.5f);
        e = Vec3f.getVec3f(0f, 0f, 0.5f);
        assertEquals(sw1, -solver.getSwivel(e, goal), ROTATION_PRECISION);
    }
}
