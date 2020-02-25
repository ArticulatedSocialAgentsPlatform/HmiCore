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
package hmi.physics.inversedynamics;

import hmi.physics.Mass;

import java.util.ArrayList;

public class IDSegment
{
    ///Center of Mass, in local coordinates from the start joint
    public float[] com = new float[3];
    
    ///translation, in local coordinates from the joint of the preceding segment to the start joint
    public float[] translation = new float[3];
    
    ///Inertia tensor, at com
    public float[] I = new float[9];
    
    ///Mass (in kg)
    public float mass;
    
    ///name, links to VObject
    public String name = "";
    
    private ArrayList<IDSegment>children = new ArrayList<IDSegment>();

    /**
     * @return the children
     */
    public ArrayList<IDSegment> getChildren()
    {
        return children;
    }
    
    public void addChild(IDSegment child)
    {
        children.add(child);
    }
    
    /**
     * Creates a Mass object, used for 
     * @return null, unimplemented
     */
    public Mass createMass()
    {
        return null;
    }
    
    @Override
    public String toString()
    {
        return name;
    }    
}
