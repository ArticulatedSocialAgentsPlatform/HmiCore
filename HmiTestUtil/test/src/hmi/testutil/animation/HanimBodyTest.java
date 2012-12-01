package hmi.testutil.animation;

import java.util.HashSet;
import java.util.Set;

import hmi.animation.VJoint;
import static org.junit.Assert.assertFalse;
import org.junit.Test;

/**
 * Unit tests for HanimBody
 * @author Herwin
 *
 */
public class HanimBodyTest
{
    private Set<VJoint>touched = new HashSet<VJoint>();
    
    private void assertNoCycles(VJoint vj)
    {
        assertFalse("Loop detected with joint "+vj.getSid(),touched.contains(vj));
        touched.add(vj);
        for(VJoint vjChild: vj.getChildren())
        {
            assertNoCycles(vjChild);
        }
    }
    
    @Test
    public void testNoCycles()
    {
        assertNoCycles(HanimBody.getLOA2HanimBody());
        assertNoCycles(HanimBody.getLOA1HanimBody());
        assertNoCycles(HanimBody.getLOA1HanimBodyWithEyes());
    }
}
