package hmi.neurophysics;

import static org.junit.Assert.assertEquals;
import hmi.testutil.LabelledParameterized;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameters;

/**
 * Unittests for Torso
 * @author Herwin
 */
@RunWith(value = LabelledParameterized.class)
public class TorsoDistributionsTest
{
    private final int N;
    private final double rot;
    private static final double PRECISION = 0.001;
    @Parameters
    public static Collection<Object[]> configs() throws Exception
    {
        Collection<Object[]> objs = new ArrayList<Object[]>();
        for (int i : new int[] {1, 2, 3, 5, 20 })
        {
            for (double j : new double[] { Math.PI, Math.PI, 0, 1, 2, -1.5 })
            {
                Object obj[] = new Object[3];
                obj[0] = Integer.toString(i) + " joints, " + "rotation: " + j;
                obj[1] = i;
                obj[2] = j;
                objs.add(obj);
            }
        }
        return objs;
    }

    public TorsoDistributionsTest(String label, int N, double rot)
    {
        this.N = N;
        this.rot = rot;
    }

    @Test
    public void testUniform()
    {
        double resRot = 0;
        for (int i = 1; i <= N; i++)
        {
            resRot += Torso.getUniform(N) * rot;
        }
        assertEquals(resRot,rot, PRECISION);
    }
    
    @Test
    public void testLinearIncrease()
    {
        double resRot = 0;
        for (int i = 1; i <= N; i++)
        {
            resRot += Torso.getLinearIncrease(i, N) * rot;
        }
        assertEquals(resRot,rot, PRECISION);
    }
    
    @Test
    public void testLinearDecrease()
    {
        double resRot = 0;
        for (int i = 1; i <= N; i++)
        {
            resRot += Torso.getLinearDecrease(i, N) * rot;
        }
        assertEquals(resRot,rot, PRECISION);
    }
}
