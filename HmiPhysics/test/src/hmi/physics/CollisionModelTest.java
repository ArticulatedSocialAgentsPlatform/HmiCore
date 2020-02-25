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
