/*******************************************************************************
 * The MIT License (MIT)
 * Copyright (c) 2015 University of Twente
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *******************************************************************************/
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
