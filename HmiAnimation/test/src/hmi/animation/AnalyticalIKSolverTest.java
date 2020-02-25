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
