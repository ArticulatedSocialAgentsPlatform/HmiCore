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
package hmi.facegraphics;

import hmi.graphics.opengl.scenegraph.GLScene;

import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Matchers.any;

/**
 * unit tests for the HmiFaceController
 * @author Herwin
 *
 */
public class HmiFaceControllerTest
{
    private GLScene mockScene = mock(GLScene.class);
    private GLHead mockHead = mock(GLHead.class);
    
    @Test
    public void test()
    {
        HMIFaceController fc = new HMIFaceController(mockScene, mockHead);
        String targetNames[]={"x","y","z"};
        float weights[]={0.1f,0.2f,0.3f};
        fc.addMorphTargets(targetNames, weights);
        fc.copy();
        verify(mockScene).setMorphTargets(any(String[].class),any(float[].class));
    }
}
