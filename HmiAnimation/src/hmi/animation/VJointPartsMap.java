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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

/**
 * Helper class to more efficiently obtain parts from a VJoint tree.
 * @author hvanwelbergen
 */
public class VJointPartsMap
{
    private Map<String,VJoint> jointMap;
    
    public VJointPartsMap(VJoint vjRoot)
    {
        Map<String,VJoint> m = new HashMap<String,VJoint>();
        m.put(vjRoot.getSid(),vjRoot);
        for(VJoint vj: vjRoot.getParts())
        {
            if(vj.getSid() != null)
            {
                m.put(vj.getSid(),vj);                
            }            
        }
        jointMap = ImmutableMap.copyOf(m);
    }
    
    public VJoint get(String sid)
    {
        return jointMap.get(sid);
    }
    
    public Collection<VJoint>getJoints()
    {
        return jointMap.values();
    }
}
