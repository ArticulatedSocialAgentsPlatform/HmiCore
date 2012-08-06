package hmi.animation;

import static hmi.testutil.math.Quat4fTestUtil.assertQuat4fRotationEquivalent;
import static hmi.testutil.math.Vec3fTestUtil.assertVec3fEquals;
import hmi.animation.AnalyticalIKSolver.LimbPosition;
import hmi.math.Quat4f;
import hmi.math.Vec3f;

import org.junit.Test;

/**
 * Unit tests for AnalyticalIKSolver
 * @author Herwin
 *
 */
public class AnalyticalIKSolverTest
{
    private static final float ROTATION_PRECISION = 0.05f;
    private static final float POSITION_PRECISION = 0.05f;
    private static final float[] SHOULDER_TO_ELBOW = Vec3f.getVec3f(0,-0.5f,0);
    private static final float[] ELBOW_TO_WRIST = Vec3f.getVec3f(0,-0.3f,0);
    private AnalyticalIKSolver solver = new AnalyticalIKSolver(ELBOW_TO_WRIST,SHOULDER_TO_ELBOW
            , LimbPosition.ARM);
    private float qSho[] = Quat4f.getQuat4f();
    private float qElb[] = Quat4f.getQuat4f();
    
    @Test
    public void testAtGoal()
    {
        solver.solve(Vec3f.getVec3f(0,-0.7999f,0), qSho, qElb);
        assertQuat4fRotationEquivalent(Quat4f.getIdentity(),qSho,ROTATION_PRECISION);
        assertQuat4fRotationEquivalent(Quat4f.getIdentity(),qElb,ROTATION_PRECISION);
    }
    
    @Test
    public void testAtPos()
    {
        solver.solve(Vec3f.getVec3f(0.1f,-0.5f,0.1f), qSho, qElb);
        float []shVec = Vec3f.getVec3f(SHOULDER_TO_ELBOW);
        Quat4f.transformVec3f(qSho,shVec);
        float []elbVec = Vec3f.getVec3f(ELBOW_TO_WRIST);
        Quat4f.mul(qElb, qSho,qElb);
        Quat4f.transformVec3f(qElb,elbVec);
        Vec3f.add(shVec, elbVec);
        assertVec3fEquals(Vec3f.getVec3f(0.1f,-0.5f,0.1f),shVec,POSITION_PRECISION);
    }
}
