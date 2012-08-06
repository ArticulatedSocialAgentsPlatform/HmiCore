package hmi.physics;

import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;
import hmi.math.Quat4f;
import hmi.physics.assembler.CollisionModel;
import hmi.testutil.argumentmatcher.Quat4fMatcher;
import hmi.testutil.argumentmatcher.Vec3fMatcher;
import hmi.xml.XMLFormatting;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

/**
 * Unit tests for the CollisionModel description
 * @author Herwin
 *
 */
public class CollisionModelTest
{
    private CollisionModel cModel;
    RigidBody mockBody = mock(RigidBody.class);
    RigidBody mockBodyOut = mock(RigidBody.class);

    final float halfExtends[] = { 1f, 2f, 3f };
    final float q[] = Quat4f.getIdentity();
    final float tr[] = { 0f, 0f, 0f };
    
    @Before
    public void setup()
    {
        cModel = new CollisionModel(mockBody);
        
        String str = "<CollisionModel xmlns:col=\"http://www.collada.org/2005/11/COLLADASchema\">"
                + "<col:shape>"
                + "<col:box>"
                + "<col:half_extents>1 2 3</col:half_extents>"
                + "</col:box>"
                + "</col:shape>" + "</CollisionModel>";
        cModel.readXML(str);
    }

    @Test
    public void testReadXML()
    {
            verify(mockBody, times(1)).addBox(
                argThat(Quat4fMatcher.equalsQuat4f(q)),
                argThat(Vec3fMatcher.equalsVec3f(tr)),
                argThat(Vec3fMatcher.equalsVec3f(halfExtends)));
    }
    
    @Test
    public void testWriteXML()
    {
        final CollisionShape shape = new CollisionBox(halfExtends);
        when(mockBody.getCollisionShapes()).thenReturn(ImmutableList.of(shape));
        StringBuilder buf = new StringBuilder();        
        cModel.appendXML(buf, new XMLFormatting(), "col", "http://www.collada.org/2005/11/COLLADASchema");
        
        CollisionModel cModelOut = new CollisionModel(mockBodyOut);
        cModelOut.readXML(buf.toString());
        verify(mockBodyOut, times(1)).addBox(
                argThat(Quat4fMatcher.equalsQuat4f(q)),
                argThat(Vec3fMatcher.equalsVec3f(tr)),
                argThat(Vec3fMatcher.equalsVec3f(halfExtends)));

    }
}
