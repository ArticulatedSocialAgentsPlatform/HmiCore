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

import static org.junit.Assert.*;

import java.util.*;

import org.junit.Test;
import org.junit.Before;

import hmi.testutil.animation.HanimBody;

/**
 * VJoint unit tests
 * @author Herwin
 */
public class VJointTest
{   
   
    VJoint vj0;
    VJoint vj00;
    VJoint vj01;
    VJoint vj010;
    VJoint vj011;
    VJoint vj012;
    VJoint vj0110;
 
    @Before
    public void init1()
    {
       vj0 = new VJoint("vj0", "root");
       vj00 = new VJoint("vj00", "lhip" );
       vj01 = new VJoint("vj01", "rhip");
       vj010 = new VJoint("vj010", "rknee0");
       vj011 = new VJoint("vj011", "rknee1");
       vj012 = new VJoint("vj012", "rknee2");
       vj0110 = new VJoint("vj0110", "rfoot");    
       
       vj0.addChild(vj00);        
       vj0.addChild(vj01);        
                                 
       vj01.addChild(vj010);      
       vj01.addChild(vj011);      
       vj01.addChild(vj012);      
                                 
       vj011.addChild(vj0110);    

    }
 
   
    @Test
    public void testGetPath()
    {
        VJoint human = HanimBody.getLOA1HanimBody();
        List<VJoint> path = human.getPath(human.getPart(Hanim.r_wrist));
        
        assertEquals(5,path.size());
        assertEquals(human.getPart(Hanim.r_wrist),path.get(0));
        assertEquals(human.getPart(Hanim.r_elbow),path.get(1));
        assertEquals(human.getPart(Hanim.r_shoulder),path.get(2));
        assertEquals(human.getPart(Hanim.vl5),path.get(3));
        assertEquals(human.getPart(Hanim.HumanoidRoot),path.get(4));
    }
    

  
     @Test
     public void testParent()
     { 
        assertTrue(vj0.getParent() == null);
        
        assertTrue(vj00.getParent() == vj0);
        assertTrue(vj01.getParent() == vj0);
        
        assertTrue(vj010.getParent() == vj01);
        assertTrue(vj011.getParent() == vj01);
        assertTrue(vj012.getParent() == vj01);
        
        assertTrue(vj0110.getParent() == vj011);
    }
    
 
    @Test
    public void testGetPart()
    {
       VJoint j011 = vj0.getPartById("vj011");
       assertTrue(j011 != null);
       assertTrue(j011 == vj011);
       assertTrue(vj0.getPartBySid("vj011") == null);
       assertTrue(vj0.getPartBySid("rknee1") == vj011);
       assertTrue(vj0.getPart("vj011") == vj011);
       assertTrue(vj0.getPart("rknee1") == vj011);
       
        
    }
  

    @Test
    public void testGetParts()
    {
      
       List<String> emptyList = new ArrayList<String>();
       List<VJoint> emptyVJList = vj0.getPartsBySids(emptyList);
       assertTrue(emptyVJList.size() == 0);
       
      
       List<String> sidList = Arrays.asList(new String[] {"lhip", "rknee1", "rknee2", "rfoot"});
       List<VJoint> vjList = vj0.getPartsBySids(sidList);
       assertTrue(vjList.size() == 4);

       assertTrue(vjList.get(0) == vj00);
       assertTrue(vjList.get(1) == vj011);
       assertTrue(vjList.get(2) == vj012);
       assertTrue(vjList.get(3) == vj0110);

       List<String> partList = Arrays.asList(new String[] {"vj00", "rknee1", "vj012", "rfoot"});
       List<VJoint> vjList1 = vj0.getParts(partList);
       assertTrue(vjList1.size() == 4);

       assertTrue(vjList1.get(0) == vj00);
       assertTrue(vjList1.get(1) == vj011);
       assertTrue(vjList1.get(2) == vj012);
       assertTrue(vjList1.get(3) == vj0110);

 
    
       List<VJoint> vjList2 = vj0.getPartsByIds(partList);
       assertTrue(vjList2.size() == 4);
       assertTrue(vjList2.get(0) == vj00);
       assertTrue(vjList2.get(1) == null);
       assertTrue(vjList2.get(2) == vj012);
       assertTrue(vjList2.get(3) == null);

       List<VJoint> vjList3 = vj0.getPartsBySids(partList);
       assertTrue(vjList3.size() == 4);
       assertTrue(vjList3.get(0) == null);
       assertTrue(vjList3.get(1) == vj011);
       assertTrue(vjList3.get(2) == null);
       assertTrue(vjList3.get(3) == vj0110); 
        
    }

    
}
