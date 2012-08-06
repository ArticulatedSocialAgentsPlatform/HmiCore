/*******************************************************************************
 * Copyright (C) 2009 Human Media Interaction, University of Twente, the Netherlands
 * 
 * This file is part of the Elckerlyc BML realizer.
 * 
 * Elckerlyc is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Elckerlyc is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Elckerlyc.  If not, see http://www.gnu.org/licenses/.
 ******************************************************************************/
package hmi.physics;

import hmi.math.Vec3f;

/**
 * Auto-fits an axis aligned bounding box around some points
 * @author welberge
 *
 */
public class AABoxFitter
{
    public float[] center = new float[3];
    public float[] half_extends = new float[3]; 
    public void fit(float []vertices)
    {
        float[] mins = new float[3];
        float[] maxs = new float[3];
        int n = vertices.length/3;
        Vec3f.set(mins,Float.MAX_VALUE,Float.MAX_VALUE,Float.MAX_VALUE);
        Vec3f.set(maxs,-Float.MAX_VALUE,-Float.MAX_VALUE,-Float.MAX_VALUE);
        for (int i=0;i<n;i++)
        {
            for(int j=0;j<3;j++)
            {
                if(vertices[i*3+j]<mins[j])mins[j]=vertices[i*3+j];
                if(vertices[i*3+j]>maxs[j])maxs[j]=vertices[i*3+j];
            }
        }
        Vec3f.set(half_extends, maxs);
        Vec3f.sub(half_extends, mins);        
        Vec3f.scale(0.5f,half_extends);
        Vec3f.set(center,mins);
        Vec3f.add(center, half_extends);        
    }
}
