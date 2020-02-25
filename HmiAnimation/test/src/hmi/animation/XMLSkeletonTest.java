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
/*
 * XMLSkeleton JUnit test
 */

package hmi.animation;

import static org.junit.Assert.assertEquals;
import hmi.math.Vec3f;

import org.junit.Test;

import com.google.common.collect.ImmutableList;

/**
 * JUnit test for hmi.animation.XMLSkeleton
 */
public class XMLSkeletonTest
{

    public XMLSkeletonTest()
    {
    }

    @Test
    public void basics()
    {
        VJoint vj = new VJoint("idRoot");
        vj.setSid("root");        
        VJoint vj1 = new VJoint("joint1");
        vj1.setSid("joint1");
        vj1.setTranslation(Vec3f.getVec3f(1,2,3));
        VJoint vj2 = new VJoint("joint2");
        vj2.setSid("joint2");
        vj2.setTranslation(Vec3f.getVec3f(2,3,4));
        VJoint vj3 = new VJoint("joint3");
        vj3.setSid("joint3");
        vj3.setTranslation(Vec3f.getVec3f(3,4,5));
        vj.addChild(vj1);
        vj.addChild(vj2);
        vj1.addChild(vj3);
        
        XMLSkeleton skelIn = new XMLSkeleton("id1");
        skelIn.setRoots(ImmutableList.of(vj));
        skelIn.setJointSids(ImmutableList.of("root","joint1","joint2","joint3"));
        
        StringBuilder buf = new StringBuilder();
        skelIn.appendXML(buf);
        
        XMLSkeleton skelOut = new XMLSkeleton("id2");
        skelOut.readXML(buf.toString());
        
        VJoint vjRootOut = skelOut.getRoots().get(0);
        assertEquals("root", vjRootOut.getSid());
        assertEquals("joint1", vjRootOut.getChildren().get(0).getSid());
        assertEquals("joint2", vjRootOut.getChildren().get(1).getSid());
        assertEquals("joint3", vjRootOut.getChildren().get(0).getChildren().get(0).getSid());       
        
    }

}
