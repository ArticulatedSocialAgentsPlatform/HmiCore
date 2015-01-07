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
